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
 * Created on November 21, 2017
 */
package org.amity.simulator.generators;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.random.GaussianRandomGenerator;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.util.FastMath;

/**
 * Implements generation of values that have a skewed Gaussian random
 * distribution.
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public class Skewed implements IGenerator
{
    private final double factor;
    private final double middle;
    private final double minimum;
    private final double maximum;
    private final double range;
    private final double skew;
    private final double bias;
    private final RandomGenerator generator = new JDKRandomGenerator();
    private final GaussianRandomGenerator gaussian
            = new GaussianRandomGenerator(generator);

    /**
     * Hidden default constructor to avoid implicit creation
     */
    private Skewed()
    {
        this.factor = 0;
        this.middle = 0;
        this.minimum = 0;
        this.maximum = 0;
        this.range = 0;
        this.skew = 0;
        this.bias = 0;
    }

    /**
     * Construct skewed Gaussian distribution generator
     *
     * @param minimum smallest time interval to be produced
     * @param maximum largest time interval to be produced
     * @param skew the degree to which the values cluster around the mode
     * @param bias the tendency of the mode to approach the min, max
     * or midpoint value; positive values bias toward max, negative values
     * toward min
     */
    public Skewed(final double minimum, final double maximum,
            final double skew, final double bias)
    {
        final double max = Math.abs(maximum);
        final double min = Math.abs(minimum);
        this.minimum = Math.min(max, min);
        this.maximum = Math.max(max, min);
        this.middle = (max + min) / 2;
        this.range = Math.abs(max - min);
        this.skew = Math.abs(skew);
        this.bias = bias;
        this.factor = FastMath.exp(bias);
    }

    @Override
    public double generate()
    {
        final List<Double> values = new ArrayList<>();
        final double value = factor
                + FastMath.exp(-gaussian.nextNormalizedDouble() / skew);
        final double modifiedValue = middle + range * (factor / value - 0.5);
        return modifiedValue;
    }

    @Override
    public String characteristics()
    {
        final StringBuilder string =
                new StringBuilder(this.getClass().getSimpleName());
        string.append(" - ").append(this.minimum).append(":");
        string.append(this.maximum).append(":").append(this.skew).append(":");
        string.append(this.bias);
        return string.toString();
    }
}
