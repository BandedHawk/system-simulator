/*
 * RoundRobin.java
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
 * Created on November 26, 2017
 */
package org.amity.simulator.distributors;

import java.util.ArrayList;
import java.util.List;
import org.amity.simulator.elements.Event;
import org.amity.simulator.elements.Sequencer;
import org.amity.simulator.language.NameValue;
import org.amity.simulator.language.Vocabulary;
import org.amity.simulator.elements.Component;

/**
 * Distribution function that uses a round-robin algorithm to split between
 * downstream processing units
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public class RoundRobin implements Distributor
{

    private final List<String> references;
    private final Component[] next;
    private final int modulus;
    private int current;

    /**
     * Default constructor - hidden
     */
    private RoundRobin()
    {
        this.references = null;
        this.modulus = 0;
        this.current = 0;
        this.next = new Component[0];
    }

    /**
     * Constructor for creating balancing algorithm
     * 
     * @param references list of downstream components
     */
    public RoundRobin(final List<String> references)
    {
        this.references = references;
        this.modulus = references != null ? references.size() : 0;
        this.current = 0;
        this.next = new Component[this.modulus];
    }

    @Override
    public Event assign(final Event event)
    {
        if (event != null && this.modulus > 0)
        {
            event.setComponent(this.next[this.current]);
            this.current = (this.current + 1) % this.modulus;
        }
        return event;
    }

    @Override
    public void addNext(final Component component)
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

    @Override
    public void reset()
    {
        this.current = 0;
    }

    @Override
    public List<String> getReferences()
    {
        return this.references;
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
        final Distributor distributor = new RoundRobin(references);
        return distributor;
    }

    @Override
    public Component[] connections()
    {
        return this.next;
    }

    @Override
    public double available()
    {
        return this.next[this.current].getAvailable();
    }

    @Override
    public void prioritize(final Sequencer sequencer)
    {
        this.next[this.current].prioritize(sequencer);
    }
}
