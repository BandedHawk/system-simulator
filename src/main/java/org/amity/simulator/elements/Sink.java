/*
 * Sink.java
 *
 * (C) Copyright 2024 Jon Barnett.
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
 * Created on May 20, 2024
 */
package org.amity.simulator.elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.amity.simulator.data.QueueStatistics;
import org.amity.simulator.language.NameValue;
import org.amity.simulator.language.Vocabulary;

/**
 * Implements an event sink, accepting events.
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public class Sink implements Component
{

    private final String label;
    private final List<Event> local;
    private final Map<String, List<Function>> generators;
    private final boolean monitor;
    private double time;

    /**
     * Hidden default constructor to avoid implicit creation
     */
    private Sink()
    {
        this.label = "dummy";
        this.local = new ArrayList<>();
        this.monitor = false;
        this.time = 0;
        this.generators = new HashMap<>();
    }

    /**
     * Constructs event sink component
     *
     * @param label distinguishing name of source component
     * @param monitor flag for generating component output information
     */
    public Sink(final String label, final boolean monitor)
    {
        this.label = label;
        this.monitor = monitor;
        this.local = new ArrayList<>();
        this.time = 0;
        this.generators = new HashMap<>();
    }

    @Override
    public Event simulate(final Event event)
    {
        if (event != null)
        {
            this.local.add(event);
            this.time = event.getCompleted();
            // No further components to pass through
            event.setComponent(null);
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
        this.time = 0 ;
    }

    @Override
    public String getLabel()
    {
        return label;
    }

    @Override
    public List<QueueStatistics> getQueueStatistics()
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
        string.append("[No defined characteristic]");
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
     * @return manufactured event source component
     */
    public final static Component instance(final List<NameValue> pairs)
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
        final Component sink = new Sink(label, monitor);
        return sink;
    }
}
