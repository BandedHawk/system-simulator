/*
 * Random.java
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
 * Created on December 6, 2017
 */

package org.amity.simulator.distributors;

import java.util.ArrayList;
import java.util.List;
import org.amity.simulator.elements.Event;
import org.amity.simulator.elements.Sequencer;
import org.amity.simulator.language.NameValue;
import org.amity.simulator.language.Vocabulary;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;
import org.amity.simulator.elements.Component;

/**
 * Distribution function that is a uniform random selection - useful for
 * modeling unpredictable event flows
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public class Random implements Distributor
{
    private final List<String> references;
    private final int modulus;
    private final Component[] next;
    private final RandomGenerator generator;
    private int peek;

    /**
     * Default constructor - hidden
     */
    private Random()
    {
        this.references = null;
        this.modulus = 0;
        this.next = new Component[0];
        this.generator = null;
        this.peek = UNKNOWN;
    }

    /**
     * Constructor for creating random distribution algorithm
     * 
     * @param references list of downstream components
     */
    public Random(final List<String> references)
    {
        this.references = references;
        this.modulus = references != null ? references.size() : 0;
        this.next = new Component[this.modulus];
        this.generator = new JDKRandomGenerator();
        this.peek = UNKNOWN;
    }

    @Override
    public Event assign(Event event)
    {
        if (event != null && this.modulus > 0)
        {
            // If we were asked for availability - we already generated
            // next target so we must honor that
            final int selection = this.peek == UNKNOWN
                    ? generator.nextInt(this.modulus)
                    : this.peek;
            // Reset peek value
            this.peek = UNKNOWN;
            event.setComponent(this.next[selection]);
        }
        return event;
    }

    @Override
    public void reset()
    {
        this.peek = UNKNOWN;
    }

    @Override
    public List<String> getReferences()
    {
        return this.references;
    }

    @Override
    public void addNext(Component component)
    {
        if (component != null && this.modulus != 0)
        {
            // Add component in reference list order
            for (int index = 0; index < this.modulus; index++)
            {
                final String reference = references.get(index);
                if (reference.equals(component.getLabel()))
                {
                    this.next[index] = component;
                }
            }
        }
    }

    @Override
    public Component[] connections()
    {
        return this.next;
    }

    @Override
    public double available()
    {
        // Lookahead at what next random selection is going to be
        // to get availability of that selection
        if (this.peek == UNKNOWN)
        {
            this.peek  = generator.nextInt(this.modulus);
        }
        return this.next[this.peek].getAvailable();
    }

    @Override
    public String characteristics()
    {
        final StringBuilder string
                = new StringBuilder(this.getClass().getSimpleName());
        for (int index = 0; index < this.references.size(); index++)
        {
            final String reference = this.references.get(index);
            if (index == 0)
            {
                string.append(" - ");
            }
            string.append(reference);
            if (index != this.references.size() - 1)
            {
                string.append(", ");
            }
        }
        return string.toString();
    }

    /**
     * Create algorithm object given raw name-value pairs
     * 
     * @param pairs list of name-values to convert into variables
     * @return manufactured balancer algorithm object
     */
    public final static Distributor instance(final List<NameValue> pairs)
    {
        final List<String> references = new ArrayList<>();
        for (final NameValue parameter : pairs)
        {
            switch (parameter.name)
            {
                case Vocabulary.NEXT:
                    references.add(parameter.value);
                default:
                    break;
            }
        }
        final Distributor distributor = new Random(references);
        return distributor;
    }

    @Override
    public void prioritize(final Sequencer sequencer, final boolean explore)
    {
        if (this.peek == UNKNOWN)
        {
            this.peek  = generator.nextInt(this.modulus);
        }
        this.next[this.peek].prioritize(sequencer, explore);
    }
}
