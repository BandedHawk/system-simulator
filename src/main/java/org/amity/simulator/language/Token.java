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

import org.amity.simulator.elements.Model;

/**
 * Storage and processing for data collected during parsing against syntax
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public class Token
{

    final String value;
    final Syntax syntax;
    final int line;
    final int position;
    Token next;

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
     * @param compiler object responsible for building simulation model
     * @return simulation model information
     */
    public Model compile()
    {
        final Compiler compiler = new Compiler();
        return compiler.compile(this);
    }
}
