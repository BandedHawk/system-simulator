/*
 * Uniform.java
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
package org.amity.simulator.generators;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;

/**
 * Implements generation of values that have a uniform random distribution.
 * 
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public class Uniform implements IGenerator
{
    private final double offset;
    private final double width;
    private final RandomGenerator generator = new JDKRandomGenerator();

    /**
     * Hidden default constructor to avoid implicit creation
     */
    private Uniform()
    {
        offset = 0;
        width = 1;
    }

    /**
     * Construct uniform random distribution generator
     * 
     * @param minimum smallest time interval to be produced
     * @param maximum largest time interval to be produced
     */
    public Uniform(final double minimum, final double maximum)
    {
        final double max = Math.abs(maximum);
        final double min = Math.abs(minimum);
        this.offset = Math.min(max, min);
        this.width = Math.abs(max - min);
    }

    @Override
    public List<Double> generate(final int eventTotal)
    {
        final List<Double> values = new ArrayList<>();
        if (eventTotal > 0)
        {
            for (int count = 0; count < eventTotal; count++)
            {
                final double value = generator.nextDouble() * width
                        + offset;
                values.add(value);
            }
        }
        return values;
    }
}
