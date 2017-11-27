/*
 * Compiler.java
 *
 * (C) Copyright 2017 Jon Barnett.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Created on November 25, 2017
 */
package org.amity.simulator.language;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import org.amity.simulator.elements.IComponent;
import org.amity.simulator.elements.Model;
import org.amity.simulator.elements.Processor;
import org.amity.simulator.elements.Source;
import org.amity.simulator.generators.Constant;
import org.amity.simulator.generators.Gaussian;
import org.amity.simulator.generators.IGenerator;
import org.amity.simulator.generators.Skewed;
import org.amity.simulator.generators.Uniform;

/**
 * Converts Tokens into an executable model
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
class Compiler
{
    private final ScratchPad[] scratch;

    /**
     * Default constructor
     */
    Compiler()
    {
        this.scratch = (ScratchPad[]) new ScratchPad[3];
        this.scratch[0] = new ScratchPad(0);
        this.scratch[1] = new ScratchPad(1);
        this.scratch[2] = new ScratchPad(2);
    }

    /**
     * 
     * @param token
     * @return 
     */
    Model compile(final Token token)
    {
        final ScratchPad local = this.scratch[0];
        this.compile(token, local);
        // Resolve downstream references
        if (local.ok())
        {
            final Map<String, IComponent> components =
                    this.scratch[1].components;
            // Create list of references and associated generators that need
            // target components
            final Map<String, List<IGenerator>> references = new HashMap<>();
            // Determine references to resolve by component
            for (final IComponent component: components.values())
            {
                // Get list of generators and the references in the component
                final Map<String, List<IGenerator>> generators
                        = component.getReferences();
                // Get references in the component
                for (final String reference : generators.keySet())
                {
                    // Add list of generators per reference into global list
                    if (reference != null)
                    {
                        final List<IGenerator> list =
                                references.containsKey(reference)
                                ? references.get(reference) :
                                new ArrayList<>();
                        list.addAll(generators.get(reference));
                        references.putIfAbsent(reference, list);
                    }
                }
            }
            // Look for reference component
            for (final String reference : references.keySet())
            {
                if (components.containsKey(reference))
                {
                    final IComponent component =
                            components.get(reference);
                    // Make sure we don't connect to a source component
                    if (component instanceof Source)
                    {
                        final StringBuilder error =
                                new StringBuilder(reference);
                        error.append(" is a source and cannot be downstream");
                        local.addError(error.toString());
                    }
                    // Resolve references
                    else
                    {
                        for (final IGenerator referee
                                : references.get(reference))
                        {
                            referee.setNext(component);
                        }
                    }
                }
                else
                {
                    final StringBuilder error =
                            new StringBuilder(reference);
                    error.append(" is not defined");
                    local.addError(error.toString());
                }
            }
        }
        final Model model = new Model();
        model.errors.addAll(local.errors());
        if (!local.ok())
        {
            for (final String error : local.errors())
            {
                System.err.println(error);
            }
        }
        else
        {
            model.compiled = true;
            model.sources.addAll(this.scratch[1].sources.values());
            model.components.putAll(this.scratch[1].components);
        }
        return model;
    }

    /**
     * Compilation at the nested level in the language syntax
     *
     * @param local nest level scratchpad for collecting compilation
     * information
     */
    private void compile(final Token token, final ScratchPad local)
    {
        final Token nextToken = token.next;
        int current = local.depth;
        switch (token.syntax)
        {
            case LABEL:
                local.labelToken = token;
                local.label = token.value;
                if (nextToken != null)
                {
                    switch (nextToken.syntax)
                    {
                        case OPEN:
                            // Word unknown in vocabulary for declaration
                            if (!Vocabulary.containsDeclaration(local.label,
                                    local.depth))
                            {
                                final StringBuilder error =
                                        new StringBuilder(token.value);
                                error.append(" is not recognised at ");
                                error.append(token.line).append(", ")
                                        .append(token.position);
                                local.addError(error.toString());
                            }
                            break;
                        case ASSIGN:
                            break;
                        default:
                            final StringBuilder error =
                                    new StringBuilder(nextToken.value);
                            error.append(" is unexpected at ");
                            error.append(nextToken.line).append(", ")
                                    .append(nextToken.position);
                            local.addError(error.toString());
                            break;
                    }
                }
                else
                {
                    final StringBuilder error =
                            new StringBuilder("Expected token following ");
                    error.append(token.line).append(", ")
                            .append(token.position);
                    local.addError(error.toString());
                }
                break;
            case VALUE:
                local.put(local.labelToken, token);
                break;
            case CLOSE:
                this.compileObject(token, this.scratch[local.depth]);
                current -= 1;
                // Copy errors to upper scratch
                this.scratch[current].addErrors(local.errors());
                // clear local errors
                local.clearErrors();
                break;
            case OPEN:
                current += 1;
                break;
            case ASSIGN:
            default:
                break;
        }
        // move to next token and the appropriate scratch by nesting level
        if (nextToken != null)
        {
            this.compile(nextToken, this.scratch[current]);
        }
    }

    /**
     * Manufactures the actual system simulation components for the model
     * 
     * @param local nest level scratch-pad for collecting compilation
     * information
     */
    private void compileObject(final Token token, final ScratchPad local)
    {
        if (local.containsName(Vocabulary.TYPE_NAME))
        {
            final List<Pair> types = local.value(Vocabulary.TYPE_NAME);
            // type should only be ever declared once
            if (types.size() == 1)
            {
                // Get type declared
                final String type = types.get(0).value.getValue();
                final List<NameValue> pairs = new ArrayList<>();
                if (Vocabulary.containsDefinition(type, local.depth - 1))
                {
                    // Obtain parsing information for valid values forms
                    final Map<String, Definition> parameters
                            = Vocabulary.get(type, local.depth - 1);
                    // Look through collected name-values pairs
                    for (final String name : local.values())
                    {
                        // Compare name of pair to valid names
                        if (parameters.containsKey(name))
                        {
                            final Definition definition = parameters.get(name);
                            final List<Pair> pair = local.value(name);
                            // process for multiple values for same name
                            if (pair.size() > 1 && definition.getMultivalue())
                            {
                            }
                            // 
                            else if (pair.size() == 1 && !definition.getMultivalue())
                            {
                                final String pairValue
                                        = pair.get(0).value.getValue();
                                final Matcher matcher = definition
                                        .getPattern().matcher(pairValue);
                                if (matcher.find())
                                {
                                    final NameValue parameter
                                            = new NameValue(name, pairValue);
                                    pairs.add(parameter);
                                }
                                // Parameter values does not match requirements
                                else
                                {
                                    final StringBuilder error =
                                            new StringBuilder(name);
                                    error.append(" does not have a valid value at ");
                                    error.append(local.location(name));
                                    local.addError(error.toString());
                                }
                            }
                            else
                            {
                                
                            }
                        }
                        // Unexpected name in value pair
                        else if (!name.equals(Vocabulary.TYPE_NAME))
                        {
                            final StringBuilder error =
                                    new StringBuilder(name);
                            error.append(" is not a valid parameter at ");
                            error.append(token.line).append(", ")
                                    .append(token.position);
                            local.addError(error.toString());
                        }
                    }
                    for (final String parameter : parameters.keySet())
                    {
                        if (parameters.get(parameter).getMandatory())
                        {
                            final boolean specified
                                    = local.containsName(parameter);
                            if (!specified)
                            {
                                final StringBuilder error =
                                        new StringBuilder("Mandatory parameter ");
                                error.append(parameter)
                                        .append(" was not defined at ");
                                error.append(token.line).append(", ")
                                        .append(token.position);
                                local.addError(error.toString());
                            }
                        }
                    }
                }
                // Only compile if there have been no errors
                if (local.ok())
                {
                    // Get generators for the component being compiled
                    List<IGenerator> generators = local.depth == 1
                            ? this.scratch[local.depth + 1].generators
                            : null;
                    switch (type)
                    {
                        case Vocabulary.SOURCE:
                            final IComponent source
                                    = Source.instance(pairs, generators);
                            local.components.put(source.getLabel(), source);
                            local.sources.put(source.getLabel(), source);
                            break;
                        case Vocabulary.PROCESSOR:
                            final IComponent processor
                                    = Processor.instance(pairs, generators);
                            local.components.put(processor.getLabel(),
                                    processor);
                            break;
                        case Vocabulary.UNIFORM:
                            final IGenerator uniform
                                    = Uniform.instance(pairs);
                            local.generators.add(uniform);
                            break;
                        case Vocabulary.GAUSSIAN:
                            final IGenerator gaussian
                                    = Gaussian.instance(pairs);
                            local.generators.add(gaussian);
                            break;
                        case Vocabulary.CONSTANT:
                            final IGenerator constant
                                    = Constant.instance(pairs);
                            local.generators.add(constant);
                            break;
                        case Vocabulary.SKEWED:
                            final IGenerator skewed
                                    = Skewed.instance(pairs);
                            local.generators.add(skewed);
                            break;
                        default:
                            break;
                    }
                }
            }
            else
            {
                final StringBuilder error =
                        new StringBuilder("Unrecognized type ");
                error.append(token.value).append(" at ")
                        .append(local.location(Vocabulary.TYPE_NAME));
                local.addError(error.toString());
            }
        }
        else
        {
            final StringBuilder error =
                    new StringBuilder("Did not specify a type at ");
            error.append(token.line).append(", ")
                    .append(token.position);
            local.addError(error.toString());
        }
        // clear stored pairs
        local.clear();
        // Clear generator scratch after injection into components
        if (local.depth == 1)
        {
            this.scratch[local.depth + 1].generators.clear();
        }
    }
}
