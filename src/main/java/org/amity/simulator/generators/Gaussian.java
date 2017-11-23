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
package org.amity.simulator.generators;

import org.apache.commons.math3.random.GaussianRandomGenerator;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;

/**
 * Implements generation of values that have a Gaussian random distribution.
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public class Gaussian implements IGenerator
{

    private final double offset;
    private final double deviation;
    private final double minimum;
    private final double maximum;
    private final RandomGenerator generator = new JDKRandomGenerator();
    private final GaussianRandomGenerator gaussian
            = new GaussianRandomGenerator(generator);

    /**
     * Hidden default constructor to avoid implicit creation
     */
    private Gaussian()
    {
        this.deviation = 0;
        this.offset = 0;
        this.minimum = 0;
        this.maximum = 0;
    }

    /**
     * Construct Gaussian distribution generator
     *
     * @param minimum smallest time interval to be produced
     * @param maximum largest time interval to be produced
     */
    public Gaussian(final double minimum, final double maximum)
    {
        final double max = Math.abs(maximum);
        final double min = Math.abs(minimum);
        this.minimum = Math.min(max, min);
        this.maximum = Math.max(max, min);
        this.deviation = Math.abs(max - min) / 10;
        this.offset = Math.min(max, min) + deviation * 5;
    }

    @Override
    public double generate()
    {
        final double value = gaussian.nextNormalizedDouble() * deviation
                + offset;
        final double modifiedValue = value > this.maximum ? this.maximum
                : value < this.minimum ? this.minimum : value;
        return modifiedValue;
    }

    @Override
    public String characteristics()
    {
        final StringBuilder string =
                new StringBuilder(this.getClass().getSimpleName());
        string.append(" - ").append(this.minimum).append(":");
        string.append(this.maximum);
        return string.toString();
    }
}
