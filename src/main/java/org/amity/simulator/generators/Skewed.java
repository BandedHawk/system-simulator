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
import org.amity.simulator.elements.IComponent;
import org.amity.simulator.language.NameValue;
import org.amity.simulator.language.Vocabulary;
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
    private final String source;
    private final String reference;
    private IComponent next;
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
        this.source = Vocabulary.DEFAULT;
        this.reference = null;
        this.next = null;
    }

    /**
     * Construct skewed Gaussian distribution generator
     *
     * @param minimum smallest time interval to be produced
     * @param maximum largest time interval to be produced
     * @param skew the degree to which the values cluster around the mode
     * @param bias the tendency of the mode to approach the min, max
     * @param source name of associated event source
     * @param reference name of downstream component
     * or midpoint value; positive values bias toward max, negative values
     * toward min
     */
    public Skewed(final double minimum, final double maximum,
            final double skew, final double bias, final String source,
            final String reference)
    {
        final double max = FastMath.abs(maximum);
        final double min = FastMath.abs(minimum);
        this.minimum = FastMath.min(max, min);
        this.maximum = FastMath.max(max, min);
        this.middle = (max + min) / 2;
        this.range = FastMath.abs(max - min);
        this.skew = FastMath.abs(skew);
        this.bias = bias;
        this.factor = FastMath.exp(bias);
        this.source = source;
        this.reference = reference;
        this.next = null;
    }

    @Override
    public double generate()
    {
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

    @Override
    public String getSource()
    {
        return source;
    }

    @Override
    public String getReference()
    {
        return reference;
    }

    @Override
    public IComponent getNext()
    {
        return next;
    }

    @Override
    public void setNext(IComponent next)
    {
        this.next = next;
    }

    /**
     * 
     * @param pairs
     * @return 
     */
    public final static IGenerator instance(final List<NameValue> pairs)
    {
        double maximum = 0;
        double minimum = 0;
        double skew = 0;
        double bias = 0;
        String reference = null;
        String source = Vocabulary.DEFAULT;
        for (final NameValue parameter : pairs)
        {
            switch (parameter.name)
            {
                case Vocabulary.MAXIMUM:
                    maximum = Double.parseDouble(parameter.value);
                    break;
                case Vocabulary.MINIMUM:
                    minimum = Double.parseDouble(parameter.value);
                    break;
                case Vocabulary.SKEW:
                    skew = Double.parseDouble(parameter.value);
                    break;
                case Vocabulary.BIAS:
                    bias = Double.parseDouble(parameter.value);
                    break;
                case Vocabulary.SOURCE:
                    source = parameter.value;
                    break;
                case Vocabulary.NEXT:
                    reference = parameter.value;
                default:
                    break;
            }
        }
        final IGenerator generator = new Skewed(minimum, maximum, skew, bias,
                source, reference);
        return generator;
    }
}
