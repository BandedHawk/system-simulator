/*
 * Source.java
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
package org.amity.simulator.elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.amity.simulator.language.NameValue;
import org.amity.simulator.language.Vocabulary;
import org.amity.simulator.generators.Generator;

/**
 * Implements an event source, generating events spread out in time as specified
 * by the generation generator.
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public class Source implements Component
{

    private final String label;
    private final Generator generator;
    private final Map<String, List<Function>> generators;
    private final List<Event> local;
    private final boolean monitor;
    private int counter;
    private Double end;
    private double time;

    /**
     * Hidden default constructor to avoid implicit creation
     */
    private Source()
    {
        this.label = "dummy";
        this.local = new ArrayList<>();
        this.generator = null;
        this.generators = new HashMap<>();
        this.monitor = false;
        this.counter = 0;
        this.end = null;
        this.time = 0;
    }

    /**
     * Constructs event source component
     *
     * @param label distinguishing name of source component
     * @param generator model for the source based on generation time
     * distribution characteristic
     * @param start beginning of generation period in ticks
     * @param end end of generation period in ticks
     * @param monitor flag for generating component output information
     */
    public Source(final String label, final Generator generator,
            final Double start, final Double end, final boolean monitor)
    {
        this.label = label;
        this.generator = generator;
        this.generators = new HashMap<>();
        if (this.generator != null)
        {
            final List<Function> list = new ArrayList<>();
            list.add(this.generator);
            this.generators.put(generator.getReference(), list);
        }
        this.local = new ArrayList<>();
        this.end = end;
        this.monitor = monitor;
        this.counter = 0;
        // Set the start time for generation
        this.time = start == null ? 0 : start ;
    }

    @Override
    public Event simulate(final Event injectedEvent)
    {
        boolean generated = false;
        if (injectedEvent == null)
        {
            // Cumulative injectedEvent timeline
            final double value = this.generator.generate();
            this.time += value;
            // Only generate an event if it is within the time limit
            if (this.end == null || this.end > this.time)
            {
                final Event event = new Event(this.label,
                        Integer.toString(this.counter++), this.time);
                event.setValues(this.time, this.time, this.time);
                this.local.add(event);
                generated = true;
            }
        }
        // Pass through an injected event
        else
        {
            this.local.add(injectedEvent);
            generated = true;
        }
        // Make a global copy to pass on
        final Event global = generated ?
                new Event(this.local.get(this.local.size() - 1)) : null;
        if (global != null)
        {
            global.setComponent(this.generator.getNext());
        }
        return global;
    }

    @Override
    public List<Event> getLocalEvents()
    {
        return this.local;
    }

    @Override
    public void reset()
    {
        this.local.clear();
        this.counter = 0;
        this.time = 0;
        if (this.generator.getNext() != null)
        {
            this.generator.getNext().reset();
        }
    }

    @Override
    public String getLabel()
    {
        return label;
    }

    @Override
    public List<Integer> getDepths()
    {
        return new ArrayList<>();
    }

    @Override
    public void generateStatistics(final Monitor monitor)
    {
        if (this.monitor && monitor != null)
        {
            monitor.displayStatistics(this);
        }
    }

    @Override
    public String description()
    {
        final StringBuilder string = new StringBuilder();
        if (this.generator == null)
        {
            string.append("[No defined characteristic]");
        }
        else
        {
            string.append("[");
            string.append(this.generator.characteristics());
            string.append("]");
        }
        return string.toString();
    }

    @Override
    public Map<String, List<Function>> getReferences()
    {
        return this.generators;
    }

    @Override
    public double getAvailable()
    {
        return this.time;
    }

    @Override
    public void prioritize(final Sequencer sequencer, final boolean explore)
    {
        if (explore)
        {
            sequencer.participants.add(this);
        }
        else
        {
            sequencer.sources = new String[0];
            sequencer.paths.add(this);
        }
    }

    /**
     * Create event source component given raw name-value pairs and plug-in
     * function
     *
     * @param pairs list of name-values to convert into variables
     * @param generators time functions for event creation times
     * @return manufactured event source component
     */
    public final static Component instance(final List<NameValue> pairs,
            List<Generator> generators)
    {
        String label = null;
        Double start = null;
        Double end = null;
        boolean monitor = false;
        for (final NameValue parameter : pairs)
        {
            switch (parameter.name)
            {
                case Vocabulary.NAME:
                    label = parameter.value;
                    break;
                case Vocabulary.START:
                    start = Double.parseDouble(parameter.value);
                    break;
                case Vocabulary.END:
                    end = Double.parseDouble(parameter.value);
                    break;
                case Vocabulary.MONITOR:
                    monitor = parameter.value.toLowerCase().contains("y");
                    break;
                default:
                    break;
            }
        }
        final Component source = new Source(label, generators.get(0), start,
                end, monitor);
        return source;
    }
}
