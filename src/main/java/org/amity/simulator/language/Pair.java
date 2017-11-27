/*
 * Pair.java
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
 * Created on November 26, 2017
 */
package org.amity.simulator.language;

/**
 * Name-value pair as token representation
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public class Pair
{
    final public Token name;
    final public Token value;

    /**
     * 
     */
    private Pair()
    {
        this.name = null;
        this.value = null;
    }

    /**
     * Constructor for name-value pair tokens
     * 
     * @param name token containing name of pair
     * @param value token containing value of pair
     */
    public Pair(final Token name, final Token value)
    {
        this.name = name;
        this.value = value;
    }
}
