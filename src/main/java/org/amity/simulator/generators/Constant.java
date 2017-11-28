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

package org.amity.simulator.generators;

import java.util.List;
import org.amity.simulator.elements.IComponent;
import org.amity.simulator.language.NameValue;
import org.amity.simulator.language.Vocabulary;

/**
 *  Implements generation of a constant name.
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public class Constant implements IGenerator
{

    private final double period;
    private final String source;
    private final String reference;
    private IComponent next;

    /**
     * Hidden default constructor to avoid implicit creation
     */
    private Constant()
    {
        this.period = 0;
        this.source = Vocabulary.DEFAULT;
        this.reference = null;
        this.next = null;
    }

    /**
     * Construct constant timing generator
     * 
     * @param offset constant time interval
     * @param source name of associated event source
     * @param reference name of downstream component
     */
    public Constant(final double offset, final String source,
            final String reference)
    {
        this.period = offset;
        this.source = source;
        this.reference = reference;
        this.next = null;
    }

    @Override
    public double generate()
    {
        return this.period;
    }    

    @Override
    public String characteristics()
    {
        final StringBuilder string =
                new StringBuilder(this.getClass().getSimpleName());
        string.append(" - ").append(this.period);
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
        double offset = 0;
        String reference = null;
        String source = Vocabulary.DEFAULT;
        for (final NameValue parameter : pairs)
        {
            switch (parameter.name)
            {
                case Vocabulary.OFFSET:
                    offset = Double.parseDouble(parameter.value);
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
        final IGenerator generator = new Constant(offset, source, reference);
        return generator;
    }
}
