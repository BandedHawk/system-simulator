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

import java.util.List;
import org.amity.simulator.elements.IComponent;
import org.amity.simulator.language.NameValue;
import org.amity.simulator.language.Vocabulary;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.util.FastMath;

/**
 * Implements generation of values that have a uniform random distribution.
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public class Uniform implements IGenerator
{

    private final double offset;
    private final double width;
    private final String source;
    private final String reference;
    private IComponent next;
    private final RandomGenerator generator;

    /**
     * Hidden default constructor to avoid implicit creation
     */
    private Uniform()
    {
        offset = 0;
        width = 1;
        this.source = Vocabulary.DEFAULT;
        this.reference = null;
        this.next = null;
        this.generator = null;
    }

    /**
     * Construct uniform random distribution generator
     *
     * @param minimum smallest time interval to be produced
     * @param maximum largest time interval to be produced
     * @param source name of associated event source
     * @param reference name of downstream component
     */
    public Uniform(final double minimum, final double maximum,
            final String source, final String reference)
    {
        final double max = FastMath.abs(maximum);
        final double min = FastMath.abs(minimum);
        this.offset = FastMath.min(max, min);
        this.width = FastMath.abs(max - min);
        this.source = source;
        this.reference = reference;
        this.next = null;
        this.generator = new JDKRandomGenerator();
    }

    @Override
    public double generate()
    {
        final double value = generator.nextDouble() * width + offset;
        return value;
    }

    @Override
    public String characteristics()
    {
        final StringBuilder string =
                new StringBuilder(this.getClass().getSimpleName());
        string.append(" - ").append(this.offset).append(":");
        string.append(this.offset + this.width);
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
        final IGenerator generator = new Uniform(minimum, maximum, source,
                reference);
        return generator;
    }
}
