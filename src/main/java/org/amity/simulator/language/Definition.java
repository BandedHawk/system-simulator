/*
 * Definition.java
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
 * Created on November 20, 2017
 */
package org.amity.simulator.language;

import java.util.regex.Pattern;

/**
 * Specification for name-value pair requirements
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public class Definition
{
    private final Pattern pattern;
    private final boolean mandatory;

    /**
     * Default constructor - intentionally hidden
     */
    private Definition()
    {
        this.pattern = null;
        mandatory = false;
    }

    /**
     * Create immutable specification for name-value pair
     * 
     * @param pattern regular expression to test value token
     * @param mandatory required name-value pair if <code>true</code> 
     */
    public Definition(final Pattern pattern, final boolean mandatory)
    {
        this.pattern = pattern;
        this.mandatory = mandatory;
    }

    /**
     * 
     * @return regular expression to test value token
     */
    public Pattern getPattern()
    {
        return pattern;
    }

    /**
     * 
     * @return required name-value pair if <code>true</code> 
     */
    public boolean getMandatory()
    {
        return mandatory;
    }
}
