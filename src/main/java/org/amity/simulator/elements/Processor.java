/*
 * Processor.java
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
package org.amity.simulator.elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.amity.simulator.generators.IGenerator;
import org.amity.simulator.language.NameValue;
import org.amity.simulator.language.Vocabulary;

/**
 * Implements an active processing component in a system model
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public class Processor implements IComponent
{

    private final String label;
    private final Map<String, IGenerator> generators;
    private final Map<String, List<IFunction>> references;
    private final List<Event> local;
    private final boolean monitor;
    private double available;
    private final List<Integer> depths;
    private int depth;

    /**
     * Hidden default constructor to avoid implicit creation
     */
    private Processor()
    {
        this.label = "dummy";
        this.generators = new HashMap<>();
        this.references = new HashMap<>();
        this.local = new ArrayList<>();
        this.monitor = false;
        this.available = 0;
        this.depths = new ArrayList<>();
        this.depth = 0;
    }

    /**
     * Constructs operational component
     *
     * @param label distinguishing name of processing component
     * @param generators models for the component based on processing time
     * distribution characteristic
     * @param monitor flag for generating component output information
     */
    public Processor(final String label, final List<IGenerator> generators,
            final boolean monitor)
    {
        this.label = label;
        this.generators = new HashMap<>();
        this.local = new ArrayList<>();
        this.references = new HashMap<>();
        this.monitor = monitor;
        this.available = 0;
        this.depths = new ArrayList<>();
        this.depth = 0;
        // Put the generators into the source lookup
        if (generators != null && !generators.isEmpty())
        {
            for (final IGenerator generator : generators)
            {
                this.generators.put(generator.getSource(), generator);
            }
        }
        // Determine downstream references to resolve
        for (final IGenerator generator : this.generators.values())
        {
            final String reference = generator.getReference();
            if (reference != null)
            {
                final List<IFunction> list
                        = this.references.containsKey(reference)
                        ? this.references.get(reference)
                        : new ArrayList<>();
                list.add(generator);
                references.putIfAbsent(reference, list);
            }
        }
    }

    @Override
    public Event simulate(final Event event)
    {
        if (event != null)
        {
            // Generate processing times for this event at this component
            final IGenerator generator
                    = generators.containsKey(event.getSource())
                    ? generators.get(event.getSource())
                    : generators.containsKey(Vocabulary.DEFAULT)
                    ? generators.get(Vocabulary.DEFAULT)
                    : null;
            final double value = generator.generate();
            // Arrival at this component is time event completed
            // processing at last component
            final double arrived = event.getCompleted();
            // Availability of this component depends on when it
            // finished processing last event in queue -
            // it is either when the event arrived on queue or when
            // the component finished processing the last event
            this.available = this.available > arrived ? this.available
                    : arrived;
            // Update the event for current component interaction
            final double completed = this.available + value;
            final double executed = event.getExecuted() + value;
            event.setValues(arrived, this.available, completed);
            event.setExecuted(executed);
            // Set next component availability
            this.available = completed;
            // Copy current event to local stats
            final Event current = new Event(event);
            current.setComponent(null);
            final boolean result = this.local.add(current);
            // Modify global event to next component to pass through
            event.setComponent(generator.getNext());
            // Check how many events are waiting to execute to find queue depth
            final int last = this.local.size() - 1;
            final double joined = current.getArrived();
            if (last > 0)
            {
                this.depth = 0;
                for (int i = last - 1; i > -1; i--)
                {
                    this.depth
                            += this.local.get(i).getCompleted() > joined
                            ? 1 : 0;
                }
            }
            depths.add(this.depth);
        }
        return event;
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
        this.available = 0;
        // Reset downstream components
        for (final IGenerator generator : this.generators.values())
        {
            if (generator.getNext() != null)
            {
                generator.getNext().reset();
            }
        }
    }

    @Override
    public String getLabel()
    {
        return this.label;
    }

    @Override
    public List<Integer> getDepths()
    {
        return this.depths;
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
        if (this.generators.isEmpty())
        {
            string.append("No defined characteristic");
        }
        else
        {
            for (final String source : this.generators.keySet())
            {
                string.append("[").append(source).append(" := ");
                string.append(this.generators.get(source).characteristics());
                string.append("]");
            }
        }
        return string.toString();
    }

    @Override
    public Map<String, List<IFunction>> getReferences()
    {
        return this.references;
    }

    /**
     * Create Processor component given raw name-value pairs and plug-in
     * functions
     * 
     * @param pairs list of name-values to convert into variables
     * @param generators time functions for processing times
     * @return manufactured processor component
     */
    public final static IComponent instance(final List<NameValue> pairs,
            final List<IGenerator> generators)
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
        final IComponent processor
                = new Processor(label, generators, monitor);
        return processor;
    }

    @Override
    public double getAvailable()
    {
        return this.available;
    }
}
