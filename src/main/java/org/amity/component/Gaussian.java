/*
 * Gaussian.java
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
 * Created on November 6, 2017
 */
package org.amity.component;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.random.GaussianRandomGenerator;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;

/**
 * Implements generation of values that have a Gaussian distribution.
 * 
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public class Gaussian implements IFunction
{

    private final double offset;
    private final double deviation;
    private final RandomGenerator generator = new JDKRandomGenerator();
    private final GaussianRandomGenerator gaussian
            = new GaussianRandomGenerator(generator);

    private Gaussian()
    {
        this.offset = 0;
        this.deviation = 0;
    }

    public Gaussian(final double offset, final double deviation)
    {
        this.offset = offset != 0 ? ( offset < 0 ? -offset : offset) : 1.0;
        this.deviation = deviation < 0 ? -deviation : deviation;
    }

    @Override
    public List<Double> generate(final int eventTotal)
    {
        final List<Double> values = new ArrayList<>();
        if (eventTotal > 0)
        {
            for (int count = 0; count < eventTotal; count++)
            {
                final double value = gaussian.nextNormalizedDouble() * deviation
                        + offset;
                if (value < 0)
                {
                    values.add(-value);
                }
                else
                {
                    values.add(value);
                }
            }
        }
        return values;
    }
}
