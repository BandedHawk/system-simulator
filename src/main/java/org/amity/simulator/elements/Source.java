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
import org.amity.simulator.generators.IGenerator;
import org.amity.simulator.language.NameValue;
import org.amity.simulator.language.Vocabulary;

/**
 * Implements an event source, generating events spread out in time as specified
 by the generation generator.
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public class Source implements IComponent
{

    private final String label;
    private final IGenerator generator;
    private final Map<String, List<IGenerator>> generators;
    private final List<Event> local;
    private final boolean monitor;
    private int counter;
    private double time;

    private Source()
    {
        this.label = "dummy";
        this.local = new ArrayList<>();
        this.generator = null;
        this.generators = new HashMap<>();
        this.monitor = false;
        this.counter = 0;
        this.time = 0;
    }

    /**
     * 
     * @param label distinguishing name of source component
     * @param generator model for the source based on generation time
     * distribution characteristic
     * @param monitor flag for generating component output information
     */
    public Source(final String label, final IGenerator generator,
            final boolean monitor)
    {
        this.label = label;
        this.generator = generator;
        this.generators = new HashMap<>();
        final List<IGenerator> list = new ArrayList<>();
        list.add(generator);
        if (this.generator != null && generator != null)
        {
            this.generators.put(generator.getReference(), list);
        }
        this.local = new ArrayList<>();
        this.monitor = monitor;
        this.counter = 0;
        this.time = 0;
    }

    @Override
    public Event simulate(final Event injectedEvent)
    {
        if (injectedEvent == null)
        {
            // Cumulative injectedEvent timeline
            final double value = this.generator.generate();
            this.time += value;
            final Event event = new Event(this.label,
                    Integer.toString(this.counter++), this.time);
            event.setValues(this.time, this.time, this.time);
            this.local.add(event);
        }
        else
        {
            this.local.add(injectedEvent);
        }
        // Make a global copy to pass on
        final Event global =
                new Event(this.local.get(this.local.size() - 1));
        global.setComponent(this.generator.getNext());
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
        final StringBuilder string = new StringBuilder("[");
        string.append(this.generator.characteristics()).append("]");
        return string.toString();
    }

    @Override
    public Map<String, List<IGenerator>> getReferences()
    {
        return this.generators;
    }

    /**
     * 
     * @param pairs
     * @param generators
     * @return 
     */
    public final static IComponent instance(final List<NameValue> pairs,
            List<IGenerator> generators)
    {
        String label = null;
        boolean monitor = false;
        for (final NameValue parameter : pairs)
        {
            switch (parameter.name)
            {
                case Vocabulary.NAME:
                    label = parameter.value;
                    break;
                case Vocabulary.MONITOR:
                    monitor = parameter.value.toLowerCase().contains("y");
                    break;
                default:
                    break;
            }
        }
        final IComponent source = new Source(label, generators.get(0),
                monitor);
        return source;
    }
}
