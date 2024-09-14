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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.amity.simulator.data.QueueStatistics;
import org.amity.simulator.language.NameValue;
import org.amity.simulator.language.Vocabulary;
import org.amity.simulator.generators.Generator;

/**
 * Implements an active processing component in a system model
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public class Processor implements Component
{

    private final String label;
    private final Map<String, Generator> generators;
    private final Map<String, List<Function>> references;
    private final String[] sources;
    private final Set<String> priorities;
    private final List<Event> local;
    private final List<Event> queue;
    private final List<QueueStatistics> statistics;
    private final boolean monitor;
    private double available;

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
        this.sources = new String[0];
        this.priorities = new HashSet<>();
        this.queue = new ArrayList<>();
        this.statistics = new ArrayList<>();
    }

    /**
     * Constructs operational component
     *
     * @param label distinguishing name of processing component
     * @param generators models for the component based on processing time
     * distribution characteristic
     * @param priorities list of source priorities for processing
     * @param monitor flag for generating component output information
     */
    public Processor(final String label, final List<Generator> generators,
            final List<String> priorities, final boolean monitor)
    {
        this.label = label;
        this.generators = new HashMap<>();
        this.local = new ArrayList<>();
        this.references = new HashMap<>();
        this.monitor = monitor;
        this.available = 0;
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
        // Initialize the queue handling
        this.queue = new ArrayList<>();
        this.statistics = new ArrayList<>();
        final QueueStatistics value = new QueueStatistics(0, 0.0, 0.0);
        this.statistics.add(value);
    }

    @Override
    public Event simulate(final Event event)
    {
        if (event != null)
        {
            // Availability of this component depends on when it
            // finished processing last event in queue -
            // it is either when the event arrived for processing or when
            // the component finished processing the last event
            final double arrived = event.getArrived();
            final double possible = event.getStarted();
            // Check if processing must be deferred
            boolean defer = false;
            // Generate processing times for this event at this component
            final Generator generator
                    = generators.containsKey(event.getSource())
                    ? generators.get(event.getSource())
                    : generators.containsKey(Vocabulary.DEFAULT)
                    ? generators.get(Vocabulary.DEFAULT)
                    : null;
            assert generator != null : "Should never be declared with no functions";
            // Event processing has not been calculated
            if (!this.queue.contains(event))
            {
                // Processing time
                final double value = generator.generate();
                // The processor is not available to process immediately
                if (this.available > possible)
                {
                    if (this.monitor && !this.statistics.isEmpty())
                    {
                        // Update previous queue span statistics entry
                        QueueStatistics stats = this.statistics.getLast();
                        assert stats != null :
                                "Unexpected null queue statistics";
                        final double span = arrived - stats.getTime();
                        stats.setSpan(span);
                        // Add new queue span statistics
                        stats = new QueueStatistics(this.queue.size() + 1,
                                arrived, 0.0);
                        statistics.add(stats);
                    }
                    // Add event to the queue
                    this.queue.add(event);
                    // Can't process event at this time
                    defer = true;
                }
                // Update the processing start of event for current component
                // interaction
                final double start = Math.max(this.available, possible);
                event.setStarted(start);
                // Completion time
                final double completed = start + value;
                event.setCompleted(completed);
                // Cumulative event processing time
                final double executed = event.getExecuted() + value;
                event.setExecuted(executed);
                // Set next component availability which is after this event
                this.available = completed;
            }
            // Process event
            if (!defer)
            {
                final Event current = new Event(event);
                current.setComponent(null);
                this.local.add(current);
                // Modify global event to next component to pass through
                event.setComponent(generator.getNext());
                // Time when event completed being processed here
                final double completed = event.getCompleted();
                // Set event start time for next component
                event.setArrived(completed);
                // Possible time for when event can be processed
                event.setStarted(completed);
                // If the queue is not empty, this event must have been in queue
                if (!this.queue.isEmpty())
                {
                    // Remove event from queue as it has been processed
                    this.queue.remove(event);
                    if (this.monitor && !this.statistics.isEmpty())
                    {
                        // Update previous queue span statistics entry
                        QueueStatistics stats = this.statistics.getLast();
                        assert stats != null :
                                "Unexpected null queue statistics";
                        final double start = event.getStarted();
                        final double span = start - stats.getTime();
                        stats.setSpan(span);
                        // Add new queue span statistics
                        stats = new QueueStatistics(this.queue.size(),
                                start, 0.0);
                        statistics.add(stats);
                    }
                }
            }
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
        this.queue.clear();
        this.statistics.clear();
        final QueueStatistics value = new QueueStatistics(0, 0.0, 0.0);
        this.statistics.add(value);
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
    public List<QueueStatistics> getQueueStatistics()
    {
        return this.statistics;
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
     * Create Processor component given raw name-value pairs and plug-in
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
        final Component processor
                = new Processor(label, generators, priorities, monitor);
        return processor;
    }
}
