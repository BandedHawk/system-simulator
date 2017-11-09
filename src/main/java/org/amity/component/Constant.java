/*
 * Constant.java
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
 * Created on November 7, 2017
 */

package org.amity.component;

import java.util.ArrayList;
import java.util.List;

/**
 *  Implements generation of a constant value.
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public class Constant implements IFunction
{

    private final double period;

    /**
     * Hidden default constructor to avoid implicit creation
     */
    private Constant()
    {
        this.period = 0;
    }

    /**
     * Construct constant timing generator
     * 
     * @param offset constant time interval
     */
    public Constant(final double offset)
    {
        this.period = offset;
    }

    @Override
    public List<Double> generate(final int eventTotal)
    {
        final List<Double> values = new ArrayList<>();
        if (eventTotal > 0)
        {
            final double value = this.period;
            for (int count = 0; count < eventTotal; count++)
            {
                values.add(value);
            }
        }
        return values;
    }    
}
