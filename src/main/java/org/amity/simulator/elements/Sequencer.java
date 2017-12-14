/*
 * Sequencer.java
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
 * Created on December 12, 2017
 */
package org.amity.simulator.elements;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.amity.simulator.distributors.Distributor;

/**
 * Algorithm to re-prioritize event processing sequence where events are waiting
 * in a queue
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public class Sequencer
{

    double available;
    final Set<Component> paths;
    String[] sources;
    Set<String> priorities;

    /**
     * Default constructor
     */
    public Sequencer()
    {
        this.paths = new HashSet<>();
        this.available = Distributor.UNKNOWN;
        this.sources = new String[0];
    }

    /**
     * Trace next active component for the event that called this, and locate
     * priority event that would replace this order of execution.
     *
     * @param event first event on the schedule
     * @param buffer working buffer for event schedules
     * @return event to be processed after re-prioritization
     */
    Event prioritize(final Event event, final List<Event> buffer)
    {
        assert event != null : "Only called within event itself";
        assert this.sources != null : "Should always be defined";
        boolean found = false;
        Event selected = null;
        // priority of self
        int priority = Integer.MAX_VALUE;
        // Only do this if we have working buffer
        if (buffer != null && !buffer.isEmpty())
        {
            // Make sure system is ready for new run
            final int size = buffer.size();
            this.paths.clear();
            // Lock in availability path
            this.available = event.getComponent().getAvailable();
            // find path and priority sources
            event.getComponent().prioritize(this);
            // Only if we have a priority list
            if (this.sources.length != 0)
            {
                // Stores position where prioritized event is in list
                final int[] position = new int[this.sources.length];
                // initialize position storage
                for (int i = 0; i < this.sources.length; i++)
                {
                    position[i] = Distributor.UNKNOWN;
                }
                // Move through buffer looking for event that can be
                // prioritized - expected to be order ascending by completed
                for (int index = 0; index < size; index++)
                {
                    final Event current = buffer.get(index);
                    // Finish if we have not found anything in availability
                    // time-frame
                    if (current.getCompleted() > this.available)
                    {
                        break;
                    }
                    // Check whether event is prioritized by active component
                    // and not same source we have
                    if (!event.getSource().equals(current.getSource())
                            && this.priorities.contains(current.getSource())
                            && this.paths.contains(current.getComponent()))
                    {
                        for (int i = 0; i < this.sources.length; i++)
                        {
                            // Don't care as priority is equivalent or lower
                            // than that of current event
                            if (i == priority)
                            {
                                break;
                            }
                            // Otherwise define priority of current selection
                            if (priority == Integer.MAX_VALUE &&
                                    this.sources[i].equals(event.getSource()))
                            {
                                priority = i;
                                // Nothing can override this
                                if (priority == 0)
                                {
                                    break;
                                }
                            }
                            // Lock in possible re-sequence
                            if (this.sources[i].equals(current.getSource()))
                            {
                                // Only store if first at that priority
                                if (position[i] == Distributor.UNKNOWN)
                                {
                                    position[i] = index;
                                    found = true;
                                    break;
                                }
                            }
                        }
                    }
                    // If we have filled priority 1 slot we can stop or
                    // current event is highest priority
                    if (priority == 0 ||
                            (found && position[0] != Distributor.UNKNOWN))
                    {
                        break;
                    }
                }
                if (found)
                {
                    // Move through priority selections from highest to lowest
                    for (int i = 0; i < this.sources.length; i++)
                    {
                        final int index = position[i];
                        if (index != Distributor.UNKNOWN)
                        {
                            // Remove prioritized event
                            selected = buffer.remove(index);
                            // Add current one back to head of list to execute
                            // first next time
                            buffer.add(0, event);
                            break;
                        }
                    }
                }
            }
        }
        return found ? selected : event;
    }
}
