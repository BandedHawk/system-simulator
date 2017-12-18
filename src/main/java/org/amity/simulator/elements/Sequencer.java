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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.amity.simulator.distributors.Distributor;

/**
 * Algorithm to re-prioritize event processing sequence where events are waiting
 * in a queue - this is designed to be a global instance for all events
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public class Sequencer
{

    double available;
    final Set<Component> paths;
    final Set<Component> exclusions;
    final Set<Component> participants;
    final Set<Distributor> intelligentFunctions;
    String[] sources;
    Set<String> priorities;

    /**
     * Default constructor
     */
    public Sequencer()
    {
        this.paths = new HashSet<>();
        this.exclusions = new HashSet<>();
        this.participants = new HashSet<>();
        this.intelligentFunctions = new HashSet<>();
        this.available = Distributor.UNKNOWN;
        this.sources = new String[0];
    }

    /**
     * Trace next active component for the selected that called this, and locate
     * priority selected that would replace this order of execution.
     *
     * @param selected first selected on the schedule
     * @param buffer working buffer for selected schedules
     * @return selected to be processed after re-prioritization
     */
    Event prioritize(final Event selected, final List<Event> buffer)
    {
        assert selected != null : "Only called within event itself";
        assert this.sources != null : "Should always be defined";
        boolean found = false;
        Event prioritized = null;
        // priority of self
        int priority = Integer.MAX_VALUE;
        // Only do this if we have working buffer
        if (buffer != null && !buffer.isEmpty())
        {
            // Make sure system is ready for new run
            final int size = buffer.size();
            this.paths.clear();
            this.exclusions.clear();
            this.intelligentFunctions.clear();
            // Lock in availability path
            this.available = selected.getComponent().getAvailable();
            // find path and priority sources
            selected.getComponent().prioritize(this, false);
            // Only if we have a priority list
            if (this.sources.length != 0)
            {
                // Stores position where prioritized selected is in list
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
                    // Check whether current is prioritized by active component
                    // and not same source we have
                    if (!selected.getSource().equals(current.getSource())
                            && this.priorities.contains(current.getSource())
                            && this.inPath(current))
                    {
                        for (int i = 0; i < this.sources.length; i++)
                        {
                            // Otherwise define priority of selection
                            if (priority == Integer.MAX_VALUE
                                    && this.sources[i].equals(selected.getSource()))
                            {
                                priority = i;
                            }
                            // Don't care as priority is equivalent or higher
                            // than that of current selected
                            if (i >= priority)
                            {
                                break;
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
                    // selected is highest priority
                    if (priority == 0
                            || (found && position[0] != Distributor.UNKNOWN))
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
                            // Remove prioritized selected
                            prioritized = buffer.remove(index);
                            // Add current one back to head of list to execute
                            // first next time
                            buffer.add(0, selected);
                            break;
                        }
                    }
                }
            }
        }
        return found ? prioritized : selected;
    }

    /**
     * Determines if current event being tested traverses the path to the
     * next active component or otherwise adds the information for additional
     * checks
     *
     * @param current event checked for pathway match
     * @return 
     */
    private boolean inPath(final Event current)
    {
        // test if component is known to lead to active target point
        boolean connects = this.paths.contains(current.getComponent());
        // Not in current known path to active delay component
        if (!connects)
        {
            final boolean excluded =
                    this.exclusions.contains(current.getComponent());
            // Not in known set of excluded components
            if (!excluded)
            {
                // if not in direct registered path, check for deeper connection
                this.participants.clear();
                // Current number of known elements leading to active target
                // point
                final int knownPath = this.paths.size();
                // Lock in path
                final double tick = current.getComponent().getAvailable();
                current.getComponent().prioritize(this, true);
                // If we've changed path elements, then there is a connection
                connects = knownPath != this.paths.size();
            }
        }
        return connects;
    }

    /**
     * Clears predicted paths on availability for intelligent distributors
     */
    void clear()
    {
        if (this.intelligentFunctions.size() > 0)
        {
            for (final Distributor distributor : this.intelligentFunctions)
            {
                distributor.reset();
            }
        }
    }
}
