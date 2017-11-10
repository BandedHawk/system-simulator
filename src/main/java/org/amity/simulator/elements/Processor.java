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
import java.util.List;
import org.amity.simulator.generators.IGenerator;

/**
 * Implements an active processing component in a system model
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public class Processor implements IComponent
{

    private final String label;
    private final IGenerator function;
    private final IComponent next;
    private final List<Event> local;
    private double available;

    /**
     * Hidden default constructor to avoid implicit creation
     */
    private Processor()
    {
        this.label = "dummy";
        this.function = null;
        this.next = null;
        this.local = new ArrayList<>();
        this.available = 0;
    }

    /**
     * Constructs operational component
     *
     * @param label distinguishing name of processing component
     * @param function model for the component based on processing time
     * distribution characteristic
     * @param component next component to process events after current component
     */
    public Processor(final String label, final IGenerator function,
            final IComponent component)
    {
        this.label = label;
        this.function = function;
        this.next = component;
        this.local = new ArrayList<>();
        this.available = 0;
    }

    @Override
    public void simulate(final Event event)
    {
        if (event != null)
        {
            // Generate processing times for this event at this component
            final double value = function.generate();
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
            final double elapsed = event.getElapsed() + completed
                    - arrived;
            final double executed = event.getExecuted() + completed
                    - this.available;
            event.setValues(arrived, this.available, completed);
            event.setElapsed(elapsed);
            event.setExecuted(executed);
            // Set next component availability
            this.available = completed;
            // Copy current event to local stats
            final Event current = new Event(event);
            final boolean result = local.add(current);
        }
        if (next != null)
        {
            next.simulate(event);
        }
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
        if (this.next != null)
        {
            this.next.reset();
        }
    }
}