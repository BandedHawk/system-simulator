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
import org.amity.simulator.elements.Source;
import org.amity.simulator.generators.IGenerator;

/**
 * Storage for data collected during parsing against syntax
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
    private Scratchpad[] scratchpad;

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
        this.scratchpad = null;
    }

    /**
     *
     * @param value
     * @param syntax
     * @param line
     * @param position
     */
    public Token(final String value, final Syntax syntax,
            final int line, final int position)
    {
        this.value = value;
        this.syntax = syntax;
        this.position = position;
        this.line = line;
        this.next = null;
        this.scratchpad = null;
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
     * @return
     */
    public Syntax getSyntax()
    {
        return this.syntax;
    }

    /**
     *
     * @return
     */
    public int getLine()
    {
        return this.line;
    }

    /**
     *
     * @return
     */
    public int getPosition()
    {
        return this.position;
    }

    /**
     * Put token at the end of the double linked list
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
     * @return nextToken token in double linked list
     */
    public Token getNext()
    {
        return next;
    }

    /**
     *
     * @return
     */
    public int size()
    {
        return this.next == null ? 1 : this.next.size() + 1;
    }

    /**
     *
     */
    public void compile()
    {
        if (this.scratchpad == null)
        {
            this.scratchpad = (Scratchpad[]) new Scratchpad[3];
            this.scratchpad[0] = new Scratchpad(0);
            this.scratchpad[1] = new Scratchpad(1);
            this.scratchpad[2] = new Scratchpad(2);
        }
        this.compile(this.scratchpad[0]);
        // Resolve downstream references
        final Scratchpad local = this.scratchpad[0];
        if (local.success)
        {
            final Map<String, IComponent> components =
                    scratchpad[1].components;
            final Map<String, List<IComponent>> references = new HashMap<>();
            // Determine references to resolve
            for (final IComponent component: components.values())
            {
                final String reference = component.getNextReference();
                if (reference != null)
                {
                    final List<IComponent> list =
                            references.containsKey(reference)
                            ? references.get(reference) :
                            new ArrayList<>();
                    list.add(component);
                    references.putIfAbsent(reference, list);
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
                        local.success = false;
                        final StringBuilder error =
                                new StringBuilder(reference);
                        error.append(" is a source and cannot be downstream");
                        local.errors.add(error.toString());
                    }
                    // Resolve references
                    else
                    {
                        for (final IComponent referee
                                : references.get(reference))
                        {
                            referee.setNext(referee);
                        }
                    }
                }
                else
                {
                    local.success = false;
                    final StringBuilder error =
                            new StringBuilder(reference);
                    error.append(" is not defined");
                    local.errors.add(error.toString());
                }
            }
        }
        if (!local.success)
        {
            for (final String error : local.errors)
            {
                System.out.println(error);
            }
        }
    }

    /**
     *
     * @param local
     */
    private void compile(final Scratchpad local)
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
                            if (!Vocabulary.DECLARATIONS[local.depth]
                                    .contains(local.label))
                            {
                                local.success = false;
                                final StringBuilder error =
                                        new StringBuilder(token.value);
                                error.append(" is not recognised at ");
                                error.append(token.line).append(", ")
                                        .append(token.position);
                                local.errors.add(error.toString());
                            }
                            break;
                        case ASSIGN:
                            break;
                        default:
                            local.success = false;
                            final StringBuilder error =
                                    new StringBuilder(nextToken.value);
                            error.append(" is unexpected at ");
                            error.append(nextToken.line).append(", ")
                                    .append(nextToken.position);
                            local.errors.add(error.toString());
                            break;
                    }
                }
                else
                {
                    local.success = false;
                    final StringBuilder error =
                            new StringBuilder("Expected token following ");
                    error.append(token.line).append(", ")
                            .append(token.position);
                    local.errors.add(error.toString());
                }
                break;
            case VALUE:
                local.names.put(local.label, local.labelToken);
                local.values.put(local.label, token);
                break;
            case CLOSE:
                this.compileObject(this.scratchpad[local.depth]);
                current -= 1;
                // Copy error and error status to upper scratchpad
                this.scratchpad[current].success =
                        this.scratchpad[current].success && local.success;
                this.scratchpad[current].errors.addAll(local.errors);
                // clear local errors
                local.errors.clear();
                local.success = true;
                break;
            case OPEN:
                current += 1;
                break;
            case ASSIGN:
            default:
                break;
        }
        // move to next token and the appropriate scratchpad by nesting level
        if (nextToken != null)
        {
            nextToken.scratchpad = this.scratchpad;
            nextToken.compile(this.scratchpad[current]);
        }
    }

    /**
     * 
     * @param local 
     */
    private void compileObject(final Scratchpad local)
    {
        final Token token = this;
        if (local.names.containsKey(Vocabulary.TYPE_NAME))
        {
            final String type = local.values
                    .get(Vocabulary.TYPE_NAME).getValue();
            if (Vocabulary.DEFINITIONS[local.depth - 1].containsKey(type))
            {
                final Map<String, Definition> parameters
                        = Vocabulary.DEFINITIONS[local.depth - 1]
                        .get(type);
                final Map<String, String> pairs = new HashMap<>();
                for (final String name : local.values.keySet())
                {
                    if (parameters.containsKey(name))
                    {
                        final String pairValue = local.values.get(name)
                                .getValue();
                        final Matcher matcher = parameters.get(name)
                                .getPattern().matcher(pairValue);
                        if (matcher.find())
                        {
                            pairs.put(name, pairValue);
                        }
                        else
                        // Parameter value does not match requirements
                        {
                            local.success = false;
                            final StringBuilder error =
                                    new StringBuilder(name);
                            error.append(" does not have a valid value at ");
                            error.append(local.values.get(name).line)
                                    .append(", ")
                                    .append(local.values.get(name).position);
                            local.errors.add(error.toString());
                        }
                    }
                    else if (!name.equals(Vocabulary.TYPE_NAME))
                    {
                        local.success = false;
                        final StringBuilder error =
                                new StringBuilder(name);
                        error.append(" is not a valid parameter at ");
                        error.append(token.line).append(", ")
                                .append(token.position);
                        local.errors.add(error.toString());
                    }
                }
                for (final String parameter : parameters.keySet())
                {
                    if (parameters.get(parameter).getMandatory())
                    {
                        final boolean specified
                                = local.values.containsKey(parameter);
                        if (!specified)
                        {
                            local.success = false;
                            final StringBuilder error =
                                    new StringBuilder("Mandatory parameter ");
                            error.append(parameter)
                                    .append(" was not defined at ");
                            error.append(token.line).append(", ")
                                    .append(token.position);
                            local.errors.add(error.toString());
                        }
                    }
                }
                // Only compile if there have been no errors
                if (local.success)
                {
                    // Get generators for the component being compiled
                    List<IGenerator> functions = local.depth == 1
                            ? this.scratchpad[local.depth + 1].functions : null;
                    switch (type)
                    {
                        case Vocabulary.SOURCE:
                            final IComponent source
                                    = SystemFactory.getSource(pairs,
                                            functions);
                            local.components.put(source.getLabel(), source);
                            local.sources.put(source.getLabel(), source);
                            break;
                        case Vocabulary.PROCESSOR:
                            final IComponent processor
                                    = SystemFactory.getProcessor(pairs,
                                            functions);
                            local.components.put(processor.getLabel(),
                                    processor);
                            break;
                        case Vocabulary.UNIFORM:
                            final IGenerator uniform
                                    = SystemFactory.getUniform(pairs);
                            local.functions.add(uniform);
                            break;
                        case Vocabulary.GAUSSIAN:
                            final IGenerator gaussian
                                    = SystemFactory.getGaussian(pairs);
                            local.functions.add(gaussian);
                            break;
                        case Vocabulary.CONSTANT:
                            final IGenerator constant
                                    = SystemFactory.getConstant(pairs);
                            local.functions.add(constant);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        else
        {
            local.success = false;
            final StringBuilder error =
                    new StringBuilder("Did not specify a type at ");
            error.append(token.line).append(", ")
                    .append(token.position);
            local.errors.add(error.toString());
        }
        local.names.clear();
        local.values.clear();
        // Clear generator scratchpad after injection into components
        if (local.depth == 1)
        {
            this.scratchpad[local.depth + 1].functions.clear();
        }
    }

    /**
     * Internal storage for tracking compilation state
     */
    private class Scratchpad
    {

        private final int depth;
        private String label;
        private Token labelToken;
        private boolean success;
        private final Map<String, Token> names;
        private final Map<String, Token> values;
        private final Map<String, IComponent> sources;
        private final Map<String, IComponent> components;
        private final List<IGenerator> functions;
        private final List<String> errors;

        /**
         * Default constructor - not used
         */
        private Scratchpad()
        {
            this.depth = 0;
            this.label = null;
            this.labelToken = null;
            this.success = true;
            this.names = new HashMap<>();
            this.values = new HashMap<>();
            this.sources = new HashMap<>();
            this.components = new HashMap<>();
            this.functions = new ArrayList<>();
            this.errors = new ArrayList<>();
        }

        /**
         * Constructor that notes the nesting for the processing
         *
         * @param depth level of nesting
         */
        private Scratchpad(final int depth)
        {
            this.depth = depth;
            this.label = null;
            this.labelToken = null;
            this.success = true;
            this.names = new HashMap<>();
            this.values = new HashMap<>();
            this.sources = new HashMap<>();
            this.components = new HashMap<>();
            this.functions = new ArrayList<>();
            this.errors = new ArrayList<>();
        }
    }
}
