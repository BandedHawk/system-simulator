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
import org.amity.simulator.distributors.Random;
import org.amity.simulator.distributors.RoundRobin;
import org.amity.simulator.distributors.Smart;
import org.amity.simulator.elements.Balancer;
import org.amity.simulator.elements.Model;
import org.amity.simulator.elements.Processor;
import org.amity.simulator.elements.Source;
import org.amity.simulator.generators.Constant;
import org.amity.simulator.generators.Gaussian;
import org.amity.simulator.generators.Skewed;
import org.amity.simulator.generators.Uniform;
import org.amity.simulator.distributors.Distributor;
import org.amity.simulator.elements.Component;
import org.amity.simulator.elements.Function;
import org.amity.simulator.elements.Throttle;
import org.amity.simulator.generators.Generator;

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
     * Convert the chain of tokens into a system simulation model
     * 
     * @param token start of token chain
     * @return system model to run simulation
     */
    Model compile(final Token token)
    {
        final ScratchPad local = this.scratch[0];
        this.compile(token, local);
        // Resolve downstream references
        if (local.ok())
        {
            final Map<String, Component> components =
                    this.scratch[1].components;
            // Create list of references and associated functions that need
            // target components
            final Map<String, List<Function>> references = new HashMap<>();
            // Determine references to resolve by component
            for (final Component component: components.values())
            {
                // Get list of functions and the references in the component
                final Map<String, List<Function>> functions
                        = component.getReferences();
                // Get references in the component
                for (final Map.Entry<String, List<Function>> entry
                        : functions.entrySet())
                {
                    final String reference = entry.getKey();
                    final List<Function> functionList = entry.getValue();
                    // Add list of functions per reference into global list
                    if (reference != null)
                    {
                        final List<Function> list =
                                references.containsKey(reference)
                                ? references.get(reference) :
                                new ArrayList<>();
                        list.addAll(functionList);
                        references.putIfAbsent(reference, list);
                    }
                }
            }
            // Look for reference component
            for (final Map.Entry<String, List<Function>> entry
                    : references.entrySet())
            {
                final String reference = entry.getKey();
                if (components.containsKey(reference))
                {
                    final Component component =
                            components.get(reference);
                    // Make sure we don't connect to a source component
                    if (component instanceof Source)
                    {
                        final StringBuilder error = new StringBuilder("'");
                        error.append(reference);
                        error.append("' is a source and cannot be downstream");
                        local.addError(error.toString());
                    }
                    // Resolve references
                    else
                    {
                        for (final Function referee : entry.getValue())
                        {
                            if (referee instanceof Generator)
                            {
                                final Generator generator
                                        = (Generator)referee;
                                generator.setNext(component);
                            }
                            else if (referee instanceof Distributor)
                            {
                                final Distributor distributor
                                        = (Distributor)referee;
                                distributor.addNext(component);
                            }
                            else
                            {
                                final StringBuilder error =
                                        new StringBuilder("Referee");
                                error.append(" is an unknown function");
                                local.addError(error.toString());
                            }
                        }
                    }
                }
                else
                {
                    final StringBuilder error = new StringBuilder("'");
                    error.append(reference);
                    error.append("' is not defined");
                    local.addError(error.toString());
                }
            }
        }
        final Model model = new Model();
        model.addErrors(local.errors());
        if (!local.ok())
        {
            for (final String error : local.errors())
            {
                System.err.println(error);
            }
        }
        else
        {
            model.addComponents(this.scratch[1].sources.values(),
                    this.scratch[1].components);
        }
        return model;
    }

    /**
     * Compilation at the nested level in the language syntax
     *
     * @param token current point of parsing
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
                                final StringBuilder error
                                        = new StringBuilder("'");
                                error.append(token.value);
                                error.append("' is not recognised at ");
                                error.append(token.line).append(", ")
                                        .append(token.position);
                                local.addError(error.toString());
                            }
                            break;
                        case ASSIGN:
                            break;
                        default:
                            final StringBuilder error = new StringBuilder("'");
                            error.append(nextToken.value);
                            error.append("' is unexpected at ");
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
                this.assemble(token, this.scratch[local.depth]);
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
     * @param token current point of parsing
     * @param local nest level scratch-pad for collecting compilation
     * information
     */
    private void assemble(final Token token, final ScratchPad local)
    {
        if (local.containsName(Vocabulary.TYPE_NAME))
        {
            final List<Pair> types = local.value(Vocabulary.TYPE_NAME);
            // type should only be ever declared once
            if (types.size() == 1)
            {
                final Token value = types.get(0).value;
                // final Token name = types.get(0).name; //Unused
                // Get type declared
                final String type = value.getValue();
                final List<NameValue> pairs = new ArrayList<>();
                if (Vocabulary.containsDefinition(type, local.depth - 1))
                {
                    // Obtain parsing information for valid values forms
                    final Map<String, Definition> parameters
                            = Vocabulary.get(type, local.depth - 1);
                    // Look through collected label-values pairs
                    for (final String label : local.values())
                    {
                        // Compare label of list to valid names
                        if (parameters.containsKey(label))
                        {
                            final Definition definition = parameters.get(label);
                            final List<Pair> list = local.value(label);
                            // Unexpected condition
                            if (list.isEmpty())
                            {
                                final StringBuilder error =
                                        new StringBuilder("'");
                                error.append(label);
                                error.append("' does not have any values near ");
                                error.append(this.location(token));
                                local.addError(error.toString());
                            }
                            // Process if valid number of values
                            else if (list.size() == 1
                                    || definition.getMultivalue())
                            {
                                for(final Pair tokens : list)
                                {
                                    final String pairValue
                                            = tokens.value.getValue();
                                    final Matcher matcher = definition
                                            .getPattern().matcher(pairValue);
                                    if (matcher.find())
                                    {
                                        final NameValue parameter
                                                = new NameValue(label, pairValue);
                                        pairs.add(parameter);
                                    }
                                    // Parameter values does not match requirements
                                    else
                                    {
                                        final StringBuilder error =
                                                new StringBuilder("'");
                                        error.append(label);
                                        error.append("' does not have a valid value at ");
                                        error.append(this.location(tokens.value));
                                        local.addError(error.toString());
                                    }
                                }
                            }
                            // Only expected label to appear once
                            else
                            {
                                final StringBuilder error =
                                        new StringBuilder("'");
                                error.append(label);
                                error.append("' declared more than once near  ");
                                error.append(this.location(token));
                                local.addError(error.toString());
                            }
                        }
                        // Unexpected label in value list
                        else if (!label.equals(Vocabulary.TYPE_NAME))
                        {
                            final StringBuilder error =
                                    new StringBuilder("'");
                            error.append(label);
                            error.append("' is not a valid parameter near ");
                            error.append(this.location(token));
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
                                        new StringBuilder("Mandatory parameter '");
                                error.append(parameter)
                                        .append("' was not defined near ");
                                error.append(this.location(token));
                                local.addError(error.toString());
                            }
                        }
                    }
                }
                // Only build if there have been no errors
                if (local.ok())
                {
                    this.build(type, token, pairs, local);
                }
            }
            else
            {
                final StringBuilder error =
                        new StringBuilder("Only a single type can be");
                error.append(" declared near ").append(this.location(token));
                local.addError(error.toString());
            }
        }
        else
        {
            final StringBuilder error =
                    new StringBuilder("Did not specify a type near ");
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
            this.scratch[local.depth + 1].distributors.clear();
        }
    }

    /**
     * Produces the actual objects for the system model and final rule checks
     * 
     * @param type category of token
     * @param token current point of parsing
     * @param local scratch-pad for current nested level
     */
    private void build(final String type, final Token token,
            final List<NameValue> pairs, final ScratchPad local)
    {
        // Get functions for the component being compiled
        List<Generator> generators = local.depth == 1
                ? this.scratch[local.depth + 1].generators
                : null;
        List<Distributor> distributors = local.depth == 1
                ? this.scratch[local.depth + 1].distributors
                : new ArrayList<>();
        switch (type)
        {
            case Vocabulary.SOURCE:
                if (generators.size() == 1
                        && distributors.isEmpty())
                {
                    final Component source
                            = Source.instance(pairs, generators);
                    if (local.components.containsKey(source.getLabel()))
                    {
                        final StringBuilder error =
                                new StringBuilder("Component with label '");
                        error.append(source.getLabel());
                        error.append("' already exists before ");
                        error.append(this.location(token));
                        local.addError(error.toString());                                
                    }
                    else
                    {
                        local.components.put(source.getLabel(), source);
                        local.sources.put(source.getLabel(), (Source)source);
                    }
                }
                else if (generators.size() < 1)
                {
                    final StringBuilder error =
                            new StringBuilder("No declared source generation function near ");
                    error.append(this.location(token));
                    local.addError(error.toString());                                
                }
                else if (!distributors.isEmpty())
                {
                    final StringBuilder error =
                            new StringBuilder("Balancer function cannot be used in a source near ");
                    error.append(this.location(token));
                    local.addError(error.toString());                                
                }
                else
                {
                    final StringBuilder error =
                            new StringBuilder("Source generation function already exists near ");
                    error.append(this.location(token));
                    local.addError(error.toString());                                
                }
                break;
            case Vocabulary.PROCESSOR:
                if (distributors.isEmpty())
                {
                    final Component processor
                            = Processor.instance(pairs, generators);
                    if (local.components.containsKey(processor.getLabel()))
                    {
                        final StringBuilder error =
                                new StringBuilder("Component with label '");
                        error.append(processor.getLabel());
                        error.append("' already exists before ");
                        error.append(this.location(token));
                        local.addError(error.toString());                                
                    }
                    else
                    {
                        local.components.put(processor.getLabel(),
                                processor);
                    }
                }
                else
                {
                    final StringBuilder error =
                            new StringBuilder("Balancer function cannot be used in a processor near ");
                    error.append(this.location(token));
                    local.addError(error.toString());                                
                }
                break;
            case Vocabulary.THROTTLE:
                if (distributors.isEmpty())
                {
                    final Component throttle
                            = Throttle.instance(pairs, generators);
                    if (local.components.containsKey(throttle.getLabel()))
                    {
                        final StringBuilder error =
                                new StringBuilder("Component with label '");
                        error.append(throttle.getLabel());
                        error.append("' already exists before ");
                        error.append(this.location(token));
                        local.addError(error.toString());                                
                    }
                    else
                    {
                        local.components.put(throttle.getLabel(), throttle);
                    }
                }
                else
                {
                    final StringBuilder error =
                            new StringBuilder("Balancer function cannot be used in a processor near ");
                    error.append(this.location(token));
                    local.addError(error.toString());                                
                }
                break;
            case Vocabulary.UNIFORM:
                final Generator uniform
                        = Uniform.instance(pairs);
                local.generators.add(uniform);
                break;
            case Vocabulary.GAUSSIAN:
                final Generator gaussian
                        = Gaussian.instance(pairs);
                local.generators.add(gaussian);
                break;
            case Vocabulary.CONSTANT:
                final Generator constant
                        = Constant.instance(pairs);
                local.generators.add(constant);
                break;
            case Vocabulary.SKEWED:
                final Generator skewed
                        = Skewed.instance(pairs);
                local.generators.add(skewed);
                break;
            case Vocabulary.BALANCER:
                if (distributors.size() == 1
                        && generators.isEmpty())
                {
                    final Component balancer
                            = Balancer.instance(pairs, distributors);
                    if (local.components.containsKey(balancer.getLabel()))
                    {
                        final StringBuilder error =
                                new StringBuilder("Component with label '");
                        error.append(balancer.getLabel());
                        error.append("' already exists before ");
                        error.append(this.location(token));
                        local.addError(error.toString());                                
                    }
                    else
                    {
                        local.components.put(balancer.getLabel(),
                                balancer);
                    }
                }
                else if (distributors.size() < 1)
                {
                    final StringBuilder error =
                            new StringBuilder("No declared balancer function near ");
                    error.append(this.location(token));
                    local.addError(error.toString());                                
                }
                else if (!generators.isEmpty())
                {
                    final StringBuilder error =
                            new StringBuilder("Generation function cannot be used in a balancer near ");
                    error.append(this.location(token));
                    local.addError(error.toString());                                
                }
                else
                {
                    final StringBuilder error =
                            new StringBuilder("Balancer function already exists near ");
                    error.append(this.location(token));
                    local.addError(error.toString());                                
                }
                break;
            case Vocabulary.ROUNDROBIN:
                if (distributors.isEmpty())
                {
                    final Distributor roundRobin
                            = RoundRobin.instance(pairs);
                    local.distributors.add(roundRobin);
                }
                else
                {
                    final StringBuilder error =
                            new StringBuilder("Balancer function already exists near ");
                    error.append(this.location(token));
                    local.addError(error.toString());                                
                }
                break;
            case Vocabulary.SMART:
                if (distributors.isEmpty())
                {
                    final Distributor smart
                            = Smart.instance(pairs);
                    local.distributors.add(smart);
                }
                else
                {
                    final StringBuilder error =
                            new StringBuilder("Balancer function already exists near ");
                    error.append(this.location(token));
                    local.addError(error.toString());                                
                }
                break;
            case Vocabulary.RANDOM:
                if (distributors.isEmpty())
                {
                    final Distributor random
                            = Random.instance(pairs);
                    local.distributors.add(random);
                }
                else
                {
                    final StringBuilder error =
                            new StringBuilder("Balancer function already exists near ");
                    error.append(this.location(token));
                    local.addError(error.toString());                                
                }
                break;
            default:
                break;
        }
    }

    /**
     * Convenience method to generate line and position information for errors
     * 
     * @param token parsed object where problem occurred
     * @return file location information for token
     */
    private String location(final Token token)
    {
        final StringBuilder string = new StringBuilder();
        if (token != null)
        {
            string.append(Integer.toString(token.getLine()));
            string.append(", ").append(Integer.toString(token.getPosition()));
        }
        else
        {
            string.append("unknown location ");
        }
        return string.toString();
    }
}
