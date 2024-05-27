/*
 * Balancer.java
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
package org.amity.simulator.elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.amity.simulator.data.QueueStatistics;
import org.amity.simulator.language.NameValue;
import org.amity.simulator.language.Vocabulary;
import org.amity.simulator.distributors.Distributor;
import org.amity.simulator.distributors.Smart;

/**
 * Component that takes event and delivers it to one of a selection of
 * downstream components for additional handling
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public class Balancer implements Component
{
    private final String label;
    private final Distributor distributor;
    private final List<Event> local;
    private final boolean monitor;
    private final boolean intelligent;

    /**
     * Hidden default constructor to avoid implicit creation
     */
    private Balancer()
    {
        this.label = "dummy";
        this.distributor = null;
        this.monitor = false;
        this.intelligent = false;
        this.local = new ArrayList<>();
    }

    /**
     * Constructs balancer component
     * 
     * @param label name of component
     * @param distributor algorithm implementation for balancing
     * @param monitor monitor if <code>true</code>
     */
    public Balancer(final String label, final Distributor distributor,
            final boolean monitor)
    {
        this.label = label;
        this.distributor = distributor;
        this.intelligent = distributor instanceof Smart;
        this.monitor = monitor;
        this.local = new ArrayList<>();
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
        if (this.distributor != null)
        {
            this.distributor.reset();
            final Component[] components = this.distributor.connections();
            if (components.length > 0)
            {
                for (final Component component : components)
                {
                    component.reset();
                }
            }
        }
    }

    @Override
    public Event simulate(final Event event)
    {
        // Arrival at this component is time event completed
        // processing at last component
        if (event != null)
        {
            final double arrived = event.getCompleted();
            // Non-delay component so no associated wait time
            event.setValues(arrived, arrived, arrived);
            // Copy current event to local records
            final Event current = new Event(event);
            current.setComponent(null);
            this.local.add(current);
        }
        return event == null ? event : distributor.assign(event);
    }

    @Override
    public String getLabel()
    {
        return this.label;
    }

    @Override
    public Map<String, List<Function>> getReferences()
    {
        final Map<String, List<Function>> map = new HashMap<>();
        if (this.distributor != null)
        {
            for (final String reference : this.distributor.getReferences())
            {
                final List<Function> list = new ArrayList<>();
                list.add(this.distributor);
                map.put(reference, list);
            }
        }
        return map;
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
        if (this.distributor == null)
        {
            string.append("[No defined characteristic]");
        }
        else
        {
            string.append("[");
            string.append(this.distributor.characteristics());
            string.append("]");
        }
        return string.toString();
    }

    @Override
    public void prioritize(final Sequencer sequencer, final boolean explore)
    {
        boolean matched = false;
        // Finding which set the event belongs
        if (explore)
        {
            // If matched to path, add collected information
            if (sequencer.paths.contains(this))
            {
                sequencer.paths.addAll(sequencer.participants);
                matched = true;
            }
            // If matched to exclusions, add collected information
            else if (sequencer.exclusions.contains(this))
            {
                sequencer.exclusions.addAll(sequencer.participants);
                matched = true;
            }
            // Undecided so store until we are sure
            else
            {
                sequencer.participants.add(this);
            }
        }
        // We are obtaining prioritization information
        else
        {
            sequencer.paths.add(this);
        }
        // Intelligent function so need to reset prediction after event
        // complete
        if (this.intelligent)
        {
            sequencer.intelligentFunctions.add(distributor);
        }
        // Go downstream if we are prioritizing or if exploring and we
        // did not find any decision information on this current path
        if (!matched)
        {
            this.distributor.prioritize(sequencer, explore);
        }
    }

    @Override
    public double getAvailable()
    {
        return this.distributor.available();
    }

    /**
     * Create Balancer object given raw name-value pairs and algorithm
     * 
     * @param pairs list of name-values to convert into variables
     * @param distributors list of distribution/balancing algorithms
     * @return manufactured balancer component
     */
    public final static Component instance(final List<NameValue> pairs,
            final List<Distributor> distributors)
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
        final Component balancer
                = new Balancer(label, distributors.get(0), monitor);
        return balancer;
    }
}
