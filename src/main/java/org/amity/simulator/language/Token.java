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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.amity.simulator.elements.IComponent;

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
    private Assembler assembler[];

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
        this.assembler = null;
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
        this.assembler = null;
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

    public int size()
    {
        return this.next == null ? 1 : this.next.size() + 1;
    }

    public void assemble()
    {
        if (this.assembler == null)
        {
            this.assembler = (Assembler[]) new Assembler[3];
            this.assembler[0] = new Assembler(0);
            this.assembler[1] = new Assembler(1);
            this.assembler[2] = new Assembler(2);
        }
        this.assemble(0);
    }

    private void assemble(final int depth)
    {
        this.process(this.assembler[depth]);
    }

    private void process(final Assembler local)
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
                            if (!Vocabulary.DECLARATIONS[local.depth]
                                    .contains(local.label))
                            {
                                System.out.println(token.value
                                        + " is not recognised at "
                                        + token.line + ", "
                                        + token.position);
                            }
                            break;
                        case ASSIGN:
                            break;
                        default:
                            System.out.println(nextToken.value
                                    + " is unexpected at "
                                    + nextToken.line + ", "
                                    + nextToken.position);
                            break;
                    }
                }
                else
                {
                    System.out.println("Expected token following " + token.line
                            + ", " + token.position);
                }
                break;
            case VALUE:
                local.names.put(local.label, local.labelToken);
                local.values.put(local.label, token);
                break;
            case CLOSE:
                this.createObjects(this.assembler[local.depth]);
                current -= 1;
                break;
            case OPEN:
                current += 1;
                break;
            case ASSIGN:
            default:
                break;
        }
        if (nextToken != null)
        {
            nextToken.assembler = this.assembler;
            nextToken.assemble(current);
        }
    }

    private void createObjects(final Assembler local)
    {
        final Token token = Token.this;
        if (local.names.containsKey(Vocabulary.TYPE_NAME))
        {
            final String type = local.values
                    .get(Vocabulary.TYPE_NAME).getValue();
            System.out.println("Type: " + type);
            if (Vocabulary.DEFINITIONS[local.depth - 1].containsKey(type))
            {
                final Map<String, Pattern> parameters
                        = Vocabulary.DEFINITIONS[local.depth - 1]
                                .get(type);
                System.out.println(parameters.keySet());
                System.out.println(local.depth - 1);
                final Map<String, String> pairs = new HashMap<>();
                for (final String name : local.values.keySet())
                {
                    if (parameters.containsKey(name))
                    {
                        final String pairValue
                                = local.values.get(name).getValue();
                        final Matcher matcher
                                = parameters.get(name).matcher(pairValue);
                        if (matcher.find())
                        {
                            pairs.put(name, pairValue);
                        }
                        else
                        {
                            System.out.println(name
                                    + " does not have a valid value at "
                                    + local.values.get(name).line
                                    + ", "
                                    + local.values.get(name).position);
                        }
                    }
                    else if (!name.equals(Vocabulary.TYPE_NAME))
                    {
                        System.out.println(name
                                + " is not a valid parameter at "
                                + token.line + ", "
                                + token.position);
                    }
                }
                switch(type)
                {
                    case Vocabulary.SOURCE:
                        final IComponent source =
                                SystemFactory.getSource(pairs, null);
                        break;
                    case Vocabulary.PROCESSOR:
                        final IComponent processor =
                                SystemFactory.getProcessor(pairs, null);
                        break;
                    default:
                        break;
                }
            }
            for (final Token info : local.values.values())
            {
            }
        }
        else
        {
            System.out.println("Did not specify a type at "
                    + token.line + ", " + token.position);
        }
        local.names.clear();
        local.values.clear();
        System.out.println("Closed out: " + local.depth);
    }

    private class Assembler
    {

        private final int depth;
        private String label;
        private Token labelToken;
        private final Map<String, Token> names;
        private final Map<String, Token> values;

        private Assembler()
        {
            this.depth = 0;
            this.label = null;
            this.names = new HashMap<>();
            this.values = new HashMap<>();
            this.labelToken = null;
        }

        private Assembler(final int depth)
        {
            this.depth = depth;
            this.label = null;
            this.names = new HashMap<>();
            this.values = new HashMap<>();
            this.labelToken = null;
        }
    }
}
