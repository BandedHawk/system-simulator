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

        private final static int FILL_SIZE = 2000;
        private final static int CYCLES = 50000;
        private final List<Event> primary;
        private final List<Event> working;
        private final List<Event> completed;
        private double fillmark;
        private double lowmark;
        private double highmark;
        private boolean overrun;

        /**
         * Default constructor - unused
         */
        private Simulator()
        {
            this.primary = new ArrayList<>();
            this.working = new ArrayList<>();
            this.completed = new ArrayList<>();
            this.lowmark = 0;
            this.highmark = 0;
            this.fillmark = 0;
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
            this.primary = new ArrayList<>();
            this.working = new ArrayList<>();
            this.completed = new ArrayList<>();
            this.fillmark = 0;
            this.lowmark = 0;
            this.highmark = 0;
            this.overrun = false;
            // Generate all the events
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
            this.fill();
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
                final boolean balancer
                        = event.getComponent() instanceof Balancer;
                final Event priority = event.prioritize(this.working,
                        locked);
                priority.simulate();
                locked = false;
                transitions++;
                // Event has completed so move it to the completed queue
                if (priority.getComponent() == null)
                {
                    this.completed.add(priority);
                }
                // Load balancer with no execution delay so
                // continue moving through event system state
                else if (balancer)
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
                    // Refill working buffer if current event is higher than the
                    // fill point
                    if (!this.primary.isEmpty()
                            && (priority.getStarted() > this.fillmark))
                    {
                        System.out.println("        Refill working buffer");
                        System.out.println("          Current time:"
                                + priority.getStarted());
                        this.fill();
                    }
                    else
                    {
                        // Re-sort working buffer after we have modified the list
                        this.working.sort(Comparator
                                .comparingDouble(Event::getStarted)
                                .thenComparingDouble(Event::getArrived));
                    }
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
            if (!this.primary.isEmpty())
            {
                this.primary.sort(Comparator
                        .comparingDouble(Event::getStarted)
                        .thenComparingDouble(Event::getArrived));
                for (int index = 0; index < Simulator.FILL_SIZE; index++)
                {
                    final Event event = this.primary.remove(0);
                    assert event.getArrived() > this.lowmark :
                            "Below working time frame";
                    this.working.add(event);
                    if (this.primary.isEmpty())
                    {
                        break;
                    }
                }
                this.working.sort(Comparator.comparingDouble(Event::getStarted)
                            .thenComparingDouble(Event::getArrived));
                this.lowmark = this.working.getFirst().getArrived();
                this.highmark = this.working.getLast().getArrived();
                this.fillmark = (this.highmark + this.lowmark)/2;
                System.out.println("          Working event buffer: "
                        + this.working.size());
                System.out.println("          Primary event buffer: "
                        + this.primary.size());
                System.out.println("          High Watermark: " + this.highmark);
                System.out.println("          Low Watermark: " + this.lowmark);
                System.out.println("          Fill Watermark: " + this.fillmark);
           }
        }
    }
}
