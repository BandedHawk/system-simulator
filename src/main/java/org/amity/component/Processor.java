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
package org.amity.component;

import java.util.ArrayList;
import java.util.List;
import org.amity.element.Event;

/**
 * Implements an active processing component in a system model.
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public class Processor implements IComponent
{

    private final String label;
    private final IFunction function;
    private final IComponent next;
    private final List<Event> local;

    /**
     * Hidden default constructor to avoid implicit creation
     */
    private Processor()
    {
        this.label = "dummy";
        this.local = new ArrayList<>();
        this.function = null;
        this.next = null;
    }

    /**
     * Constructs operational component
     * 
     * @param label distinguishing name of processing component
     * @param function model for the component based on processing time
     * distribution characteristic
     * @param component next component to process events after current component
     */
    public Processor(final String label, final IFunction function,
            final IComponent component)
    {
        this.label = label;
        this.function = function;
        this.next = component;
        this.local = new ArrayList<>();
    }

    @Override
    public void simulate(final List<Event> events)
    {
        // Component is immediately available
        double available = 0;
        if (events != null)
        {
            final int eventTotal = events.size();
            if (eventTotal > 0)
            {
                // Generate processing times for each event queued at this
                // component
                final List<Double> values = function.generate(events.size());
                for (int count = 0; count < eventTotal; count++)
                {
                    // Assume events in chronological order
                    final Event event = events.get(count);
                    // Arrival at this component is time event completed
                    // processing at last component
                    final double arrival = event.getComplete();
                    // Availability of this component depends on when it
                    // finished processing last event in queue -
                    // it is either when the event arrived on queue or when
                    // the component finished processing the last event
                    available = available > arrival ? available : arrival;
                    // Update the event for current component interaction
                    final double complete = available + values.get(count);
                    final double elapsed = event.getElapsed() + complete
                            - arrival;
                    final double executed = event.getExecuted() + complete
                            - available;
                    event.setValues(arrival, available, complete);
                    event.setElapsed(elapsed);
                    event.setExecuted(executed);
                    // Set next component availability
                    available = complete;
                    // Copy current event to local stats
                    final Event current = new Event(event);
                    final boolean result = local.add(current);
                }
            }
            if (next != null)
            {
                next.simulate(events);
            }
        }
    }

    @Override
    public List<Event> getLocalEvents()
    {
        return this.local;
    }
}
