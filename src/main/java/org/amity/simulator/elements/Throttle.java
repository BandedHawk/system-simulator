/*
 * Copyright 2024 jbarnett.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.amity.simulator.elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.amity.simulator.generators.Generator;
import org.amity.simulator.language.NameValue;
import org.amity.simulator.language.Vocabulary;

/**
 * Implements a throttling component in a system model that limits throughput
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public class Throttle implements Component
{
    private final String label;
    private final Map<String, Generator> generators;
    private final Map<String, List<Function>> references;
    private final String[] sources;
    private final Set<String> priorities;
    private final List<Event> local;
    private final boolean monitor;
    private double available;
    private final List<Integer> depths;
    private int depth;

    /**
     * Hidden default constructor to avoid implicit creation
     */
    private Throttle()
    {        
        this.label = "dummy";
        this.generators = new HashMap<>();
        this.references = new HashMap<>();
        this.local = new ArrayList<>();
        this.monitor = false;
        this.available = 0;
        this.depths = new ArrayList<>();
        this.depth = 0;
        this.sources = new String[0];
        this.priorities = new HashSet<>();
    }

    public Throttle(final String label, final List<Generator> generators,
            final List<String> priorities, final boolean monitor)
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
            for (final Generator generator : generators)
            {
                this.generators.put(generator.getSource(), generator);
            }
        }
        // Determine downstream references to resolve
        for (final Generator generator : this.generators.values())
        {
            final String reference = generator.getReference();
            if (reference != null)
            {
                final List<Function> list
                        = this.references.containsKey(reference)
                        ? this.references.get(reference)
                        : new ArrayList<>();
                list.add(generator);
                references.putIfAbsent(reference, list);
            }
        }
        this.sources = priorities == null ? new String[0]
                : new String[priorities.size()];
        if (this.sources.length > 0)
        {
            int index = 0;
            // Priority order array
            for (final String source : priorities)
            {
                this.sources[index++] = source;
            }
            // Convenience to avoid downstream conversion
            this.priorities = new HashSet<>(priorities);
        }
        else
        {
            this.priorities = new HashSet<>();
        }
    }

    @Override
    public Event simulate(final Event event)
    {
        if (event != null)
        {
            // Generate processing times for this event at this component
            final Generator generator
                    = generators.containsKey(event.getSource())
                    ? generators.get(event.getSource())
                    : generators.containsKey(Vocabulary.DEFAULT)
                    ? generators.get(Vocabulary.DEFAULT)
                    : null;
            // Cool down time that is the throttle
            final double value = generator.generate();
            // Arrival at this component is time event completed
            // processing at last component
            final double arrived = event.getCompleted();
            // Availability of this component depends on when it
            // finished processing last event in queue -
            // it is either when the event arrived on queue or when
            // the component finished processing the last event including the
            // cooldown period
            this.available = this.available > arrived ? this.available
                    : arrived;
            event.setValues(arrived, this.available, this.available);
            // Availability is set for after the cooldown time
            this.available += value;
            // Copy current event to local stats
            final Event current = new Event(event);
            current.setComponent(null);
            this.local.add(current);
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
        for (final Generator generator : this.generators.values())
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
            string.append("[No defined characteristic]");
        }
        else
        {
            for (final Map.Entry<String, Generator> entry
                    : this.generators.entrySet())
            {
                final String source = entry.getKey();
                final Generator generator = entry.getValue();
                string.append("[").append(source).append(" := ");
                if (generator != null)
                {
                    string.append(generator.characteristics());
                }
                else
                {
                    string.append("Undefined");
                }
                string.append("]");
            }
        }
        return string.toString();
    }

    @Override
    public Map<String, List<Function>> getReferences()
    {
        return this.references;
    }

    @Override
    public double getAvailable()
    {
        return this.available;
    }

    @Override
    public void prioritize(final Sequencer sequencer, final boolean explore)
    {
        if (explore)
        {
            // If matched to path, add collected information
            if (sequencer.paths.contains(this))
            {
                sequencer.paths.addAll(sequencer.participants);
            }
            // End-of-explore and not in path
            else
            {
                if (!sequencer.participants.isEmpty())
                {
                    sequencer.exclusions.addAll(sequencer.participants);
                }
                if (!sequencer.exclusions.contains(this))
                {
                    sequencer.exclusions.add(this);
                }
            }
        }
        else
        {
            sequencer.sources = this.sources;
            sequencer.priorities = this.priorities;
            sequencer.paths.add(this);
        }
    }

    /**
     * Create throttle component given raw name-value pairs and plug-in
     * functions
     * 
     * @param pairs list of name-values to convert into variables
     * @param generators time functions for processing times
     * @return manufactured processor component
     */
    public final static Component instance(final List<NameValue> pairs,
            final List<Generator> generators)
    {
        String label = null;
        boolean monitor = false;
        final List<String> priorities = new ArrayList<>();
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
                case Vocabulary.PRIORITY:
                    priorities.add(parameter.value);
                default:
                    break;
            }
        }
        final Component throttle
                = new Throttle(label, generators, priorities, monitor);
        return throttle;
    }
}
