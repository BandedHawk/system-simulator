/*
 * Event.java
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
 * Created on November 20, 2017
 */
package org.amity.simulator.elements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Container for system components
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public class Model
{

    private final List<String> errors;
    private boolean compiled;
    final List<Source> sources;
    final Map<String, Component> components;

    /**
     * Default constructor for the system model
     */
    public Model()
    {
        this.errors = new ArrayList<>();
        this.compiled = false;
        this.sources = new ArrayList<>();
        this.components = new HashMap<>();
    }

    /**
     * Copy batch of errors into model repository - usually in place of an
     * actual model
     *
     * @param errors list of strings representing collection of errors
     */
    public void addErrors(final List<String> errors)
    {
        this.errors.addAll(errors);
        this.compiled = this.errors.isEmpty();
    }

    /**
     * Load all the elements of the model into the container
     *
     * @param sources convenience list of event sources
     * @param components objects that represent the connected system
     */
    public void addComponents(final Collection<Source> sources,
            final Map<String, Component> components)
    {
        if (this.compiled)
        {
            this.sources.addAll(sources);
            this.components.putAll(components);
        }
    }

    /**
     *
     * @return model compiled successfully
     */
    public boolean isCompiled()
    {
        return this.compiled;
    }

    /**
     *
     * @return list of error messages while building model
     */
    public List<String> getErrors()
    {
        return this.errors;
    }

    /**
     * Main entry point for starting the simulation using the model
     *
     * @param generate end point in ticks for the simulation
     * @param start beginning of statistics sample in ticks
     * @param end final point in ticks for statistics sample
     * @return simulation and sampling successful if <code>true</code>
     */
    public boolean execute(final double generate, final double start,
            final double end)
    {
        final boolean run = this.compiled && generate > end
                && end > start && start >= 0;
        if (run)
        {
            System.out.println("Completed compilation");
            final Monitor monitor = new Monitor(start, end);
            final Simulator simulator = new Simulator(this.sources, generate);
            simulator.execute(generate);
            System.out.println("Statistics for events that occurred between "
                    + start + " and " + end);
            final boolean multisource = this.sources.size() > 1;
            monitor.displayStatistics(simulator.completed, multisource);
            final Set<Map.Entry<String, Component>> set
                    = new TreeSet<>(Map.Entry.comparingByKey());
            set.addAll(this.components.entrySet());
            for (final Map.Entry<String, Component> entry : set)
            {
                final Component component = entry.getValue();
                component.generateStatistics(monitor);
            }
        }
        return run;
    }

    /**
     * Container for running simulation
     */
    private class Simulator
    {

        private final static int FILL = 500;
        private final static int MIN_BUFFER = 20;
        private final static int MAX_BUFFER = 2000;
        private final static int CYCLES = 50000;
        private final List<Event> primary;
        private final List<Event> working;
        private final List<Event> completed;
        private int capacity;
        private double highmark;
        private boolean overrun;

        /**
         * Default constructor - unused
         */
        private Simulator()
        {
            this.capacity = FILL;
            this.primary = new ArrayList<>();
            this.working = new ArrayList<>();
            this.completed = new ArrayList<>();
            this.highmark = 0;
            this.overrun = false;
        }

        /**
         * Constructor for the simulation space
         *
         * @param sources event generation components
         * @param generate end-point in ticks for the simulation completion
         */
        private Simulator(final List<Source> sources, final double generate)
        {
            this.capacity = FILL;
            this.primary = new ArrayList<>();
            this.working = new ArrayList<>();
            this.completed = new ArrayList<>();
            this.highmark = 0;
            this.overrun = false;
            for (final Source source : sources)
            {
                do
                {
                    final Event event = source.simulate(null);
                    if (event == null || event.getStarted() > generate)
                    {
                        break;
                    }
                    // Only add event if it has a component to execute against
                    if (event.getComponent() != null)
                    {
                        this.primary.add(event);
                    }
                }
                while (true);
            }
            // Sort primary store for all events in order of estimated start time
            this.primary.sort(Comparator.comparingDouble(Event::getStarted)
                    .thenComparingDouble(Event::getArrived));
            // Copy a fractional part of the primary store
            // We do this as the working sort will take less time
            this.fill();
            // Sort working in chronological order of estimated start
            this.working.sort(Comparator.comparingDouble(Event::getStarted)
                    .thenComparingDouble(Event::getArrived));
        }

        /**
         * Step through simulation
         * 
         * @param generate end-point in ticks for the simulation completion
         */
        void execute(final double generate)
        {
            System.out.println("  Start simulation");
            int transitions = 0;
            boolean locked = false;
            while (!this.working.isEmpty())
            {
                final Event event = this.working.remove(0);
                // End of simulation time
                if (event.getCompleted() > generate)
                {
                    break;
                }
                // Check there is no delay with execution
                final boolean zeroDelay
                        = event.getComponent() instanceof Balancer;
                final Event priority = event.prioritize(this.working,
                        locked);
                priority.simulate();
                locked = false;
                transitions++;
                // 
                if (priority.getComponent() == null)
                {
                    this.completed.add(priority);
                }
                // Load balancer with no execution delay so
                // continue moving through event system state
                else if (zeroDelay)
                {
                    // Put this back on to execute first again on the next
                    // transition as availability may have been calculated
                    // for this event so we need to do it first
                    this.working.add(0, priority);
                    // Don't re-check priority next iteration as we're
                    // executing on this event
                    locked = true;
                }
                // New completion time for current event so re-sort execution
                // schedule
                else
                {
                    // Add to end as we will reshuffle
                    this.working.add(priority);
                    // Refill working buffer if modified event completion is
                    // higher than the high watermark or we are getting
                    // to a low working buffer and we still have events in the
                    // primary event buffer
                    if (!this.primary.isEmpty()
                            && (this.working.size() < MIN_BUFFER
                            || priority.getCompleted() > this.highmark))
                    {
                        if (this.highmark < priority.getCompleted()
                                && this.capacity < MAX_BUFFER)
                        {
                            this.capacity += FILL;
                        }
                        this.highmark = priority.getCompleted();
                        this.fill();
                    }
                    // Re-sort working buffer after we have modified the list
                    this.working.sort(Comparator
                            .comparingDouble(Event::getStarted)
                            .thenComparingDouble(Event::getArrived));
                }
                if (transitions % CYCLES == 0)
                {
                    System.out.println("    Transitions: " + transitions);
                    System.out.println("      Time: "
                            + priority.getCompleted());
                    System.out.println("      Events completed: "
                            + this.completed.size());
                    final int remaining = this.primary.size()
                            + this.working.size();
                    System.out.println("      Events remaining: "
                            + remaining);
                }
            }
            System.out.println("  Simulation transitions to complete: "
                    + transitions);
        }

        /**
         * Transfer events from the primary event storage to the working area
         */
        private void fill()
        {
            double current = this.highmark;
            if (!primary.isEmpty())
            {
                // Re-calculate timeline due to extreme out-of-order insertions
                if (this.working.size() > MAX_BUFFER)
                {
                    if (!overrun)
                    {
                        System.err.println("        System arrivals faster than system exits");
                        System.err.println("          Completed: "
                                + this.completed.size());
                        this.overrun = true;
                    }
                    this.primary.addAll(this.working);
                    this.working.clear();
                    this.primary.sort(Comparator
                            .comparingDouble(Event::getStarted)
                            .thenComparingDouble(Event::getArrived));
                }
                else
                {
                    this.overrun = false;
                }
                // Copy events from main buffer into working buffer
                for (int index = 0; index < this.capacity; index++)
                {
                    if (this.primary.isEmpty())
                    {
                        break;
                    }
                    else
                    {
                        final Event event = primary.remove(0);
                        this.working.add(event);
                        current = event.getCompleted();
                    }
                }
            }
            this.highmark = current;
        }
    }
}
