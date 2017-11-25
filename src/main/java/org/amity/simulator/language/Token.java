/*
 * Token.java
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
 * Created on November 13, 2017
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
 * Storage and processing for data collected during parsing against syntax
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public class Token
{

    private final String value;
    private final Syntax syntax;
    private final int line;
    private final int position;
    private Token next;
    private ScratchPad[] scratch;

    /**
     * Hidden default constructor to avoid implicit creation
     */
    private Token()
    {
        this.value = null;
        this.syntax = null;
        this.position = 0;
        this.line = 0;
        this.next = null;
        this.scratch = null;
    }

    /**
     * Creates a fully initialized and immutable token
     *
     * @param value source text
     * @param syntax type of token
     * @param line location in the source file
     * @param position location of token in the source line
     */
    public Token(final String value, final Syntax syntax,
            final int line, final int position)
    {
        this.value = value;
        this.syntax = syntax;
        this.position = position;
        this.line = line;
        this.next = null;
        this.scratch = null;
    }

    /**
     *
     * @return
     */
    public String getValue()
    {
        return this.value;
    }

    /**
     *
     * @return part of language tag of the token
     */
    public Syntax getSyntax()
    {
        return this.syntax;
    }

    /**
     *
     * @return file line location for the token
     */
    public int getLine()
    {
        return this.line;
    }

    /**
     *
     * @return location on the line for the token
     */
    public int getPosition()
    {
        return this.position;
    }

    /**
     * Put token at the end of the linked list
     *
     * @param token item to be added to the list
     */
    public void add(final Token token)
    {
        Token current = this;
        while (current.next != null)
        {
            current = current.next;
        }
        current.next = token;
    }

    /**
     *
     * @return nextToken token in linked list
     */
    public Token getNext()
    {
        return next;
    }

    /**
     *
     * @return linked list length
     */
    public int size()
    {
        return this.next == null ? 1 : this.next.size() + 1;
    }

    /**
     * Compiles the simulation model from the token chain
     * 
     * @return simulation model information
     */
    public Model compile()
    {
        if (this.scratch == null)
        {
            this.scratch = (ScratchPad[]) new ScratchPad[3];
            this.scratch[0] = new ScratchPad(0);
            this.scratch[1] = new ScratchPad(1);
            this.scratch[2] = new ScratchPad(2);
        }
        final ScratchPad local = this.scratch[0];
        this.compile(local);
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
    private void compile(final ScratchPad local)
    {
        final Token token = this;
        final Token nextToken = this.next;
        int current = local.depth;
        switch (this.syntax)
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
                this.compileObject(this.scratch[local.depth]);
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
            nextToken.scratch = this.scratch;
            nextToken.compile(this.scratch[current]);
        }
    }

    /**
     * Manufactures the actual system simulation components for the model
     * 
     * @param local nest level scratchpad for collecting compilation
     * information
     */
    private void compileObject(final ScratchPad local)
    {
        final Token token = this;
        if (local.containsName(Vocabulary.TYPE_NAME))
        {
            final String type = local.value(Vocabulary.TYPE_NAME);
            if (Vocabulary.containsDefinition(type, local.depth - 1))
            {
                final Map<String, Definition> parameters
                        = Vocabulary.get(type, local.depth - 1);
                final Map<String, String> pairs = new HashMap<>();
                for (final String name : local.values())
                {
                    if (parameters.containsKey(name))
                    {
                        final String pairValue = local.value(name);
                        final Matcher matcher = parameters.get(name)
                                .getPattern().matcher(pairValue);
                        if (matcher.find())
                        {
                            pairs.put(name, pairValue);
                        }
                        // Parameter value does not match requirements
                        else
                        {
                            final StringBuilder error =
                                    new StringBuilder(name);
                            error.append(" does not have a valid value at ");
                            error.append(local.location(name));
                            local.addError(error.toString());
                        }
                    }
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
                // Only compile if there have been no errors
                if (local.ok())
                {
                    // Get generators for the component being compiled
                    List<IGenerator> functions = local.depth == 1
                            ? this.scratch[local.depth + 1].functions : null;
                    switch (type)
                    {
                        case Vocabulary.SOURCE:
                            final IComponent source
                                    = Source.instance(pairs, functions);
                            local.components.put(source.getLabel(), source);
                            local.sources.put(source.getLabel(), source);
                            break;
                        case Vocabulary.PROCESSOR:
                            final IComponent processor
                                    = Processor.instance(pairs, functions);
                            local.components.put(processor.getLabel(),
                                    processor);
                            break;
                        case Vocabulary.UNIFORM:
                            final IGenerator uniform
                                    = Uniform.instance(pairs);
                            local.functions.add(uniform);
                            break;
                        case Vocabulary.GAUSSIAN:
                            final IGenerator gaussian
                                    = Gaussian.instance(pairs);
                            local.functions.add(gaussian);
                            break;
                        case Vocabulary.CONSTANT:
                            final IGenerator constant
                                    = Constant.instance(pairs);
                            local.functions.add(constant);
                            break;
                        case Vocabulary.SKEWED:
                            final IGenerator skewed
                                    = Skewed.instance(pairs);
                            local.functions.add(skewed);
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
                error.append(type).append(" at ")
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
            this.scratch[local.depth + 1].functions.clear();
        }
    }
}
