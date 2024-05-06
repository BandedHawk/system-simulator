/*
 * Smart.java
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
 * Created on November 27, 2017
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
 * Distribution function for sending an event to the next available processing
 * component
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public class Smart implements Distributor
{
    private final List<String> references;
    private final Component[] next;
    private int peek;

    /**
     * Default constructor - not used
     */
    private Smart()
    {
        this.references = new ArrayList<>();
        this.next = new Component[0];
        this.peek = UNKNOWN;
    }

    /**
     * Constructor for creating balancing algorithm
     * 
     * @param references list of downstream components
     */
    public Smart(final List<String> references)
    {
        this.references = references;
        final int size = references != null ? references.size() : 0;
        this.next = new Component[size];
        this.peek = UNKNOWN;
    }

    @Override
    public Event assign(final Event event)
    {
        // Find downstream component with lowest wait time
        double minimum = Double.MAX_VALUE;
        Component component = null;
        // Search if an availability check hasn't been performed
        if (this.peek == UNKNOWN)
        {
            for (final Component item : this.next)
            {
                if (item.getAvailable() < minimum)
                {
                    minimum = item.getAvailable();
                    component = item;
                }
            }
        }
        // Otherwise use availability results and then reset
        else
        {
            component = this.next[this.peek];
            this.peek = UNKNOWN;
        }
        // Direct event to next available component
        event.setComponent(component);
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
    public void addNext(final Component component)
    {
        if (component != null)
        {
            // Add component in reference list order
            for (int index = 0; index < this.next.length; index++)
            {
                final String reference = this.references.get(index);
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
    public Component[] connections()
    {
        return this.next;
    }

    @Override
    public double available()
    {
        double minimum = Double.MAX_VALUE;
        // Search if we haven't already done availability
        if (this.peek == UNKNOWN)
        {
            for (int index = 0; index < this.next.length; index++)
            {
                final Component item = this.next[index];
                if (item.getAvailable() < minimum)
                {
                    minimum = item.getAvailable();
                    this.peek = index;
                }
            }
        }
        // Otherwise use existing result
        else
        {
            minimum = this.next[this.peek].getAvailable();
        }
        return minimum;
    }

    @Override
    public void prioritize(final Sequencer sequencer, final boolean explore)
    {
        this.next[this.peek].prioritize(sequencer, explore);
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
        final Distributor distributor = new Smart(references);
        return distributor;
    }
}
