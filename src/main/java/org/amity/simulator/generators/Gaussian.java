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

import java.util.List;
import org.amity.simulator.elements.IComponent;
import org.amity.simulator.language.NameValue;
import org.amity.simulator.language.Vocabulary;
import org.apache.commons.math3.random.GaussianRandomGenerator;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.util.FastMath;

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
    private final String source;
    private final String reference;
    private IComponent next;
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
        this.source = Vocabulary.DEFAULT;
        this.reference = null;
        this.next = null;
    }

    /**
     * Construct Gaussian distribution generator
     *
     * @param minimum smallest time interval to be produced
     * @param maximum largest time interval to be produced
     * @param source name of associated event source
     * @param reference name of downstream component
     */
    public Gaussian(final double minimum, final double maximum,
            final String source, final String reference)
    {
        final double max = FastMath.abs(maximum);
        final double min = FastMath.abs(minimum);
        this.minimum = FastMath.min(max, min);
        this.maximum = FastMath.max(max, min);
        this.deviation = FastMath.abs(max - min) / 10;
        this.offset = FastMath.min(max, min) + deviation * 5;
        this.source = source;
        this.reference = reference;
        this.next = null;
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
     * Create Gaussian probability function given raw name-value pairs
     * 
     * @param pairs list of name-values to convert into variables
     * @return manufactured Gaussian probability generator
     */
    public final static IGenerator instance(final List<NameValue> pairs)
    {
        double maximum = 0;
        double minimum = 0;
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
                case Vocabulary.SOURCE:
                    source = parameter.value;
                    break;
                case Vocabulary.NEXT:
                    reference = parameter.value;
                default:
                    break;
            }
        }
        final IGenerator generator = new Gaussian(minimum, maximum, source,
                reference);
        return generator;
    }
}
