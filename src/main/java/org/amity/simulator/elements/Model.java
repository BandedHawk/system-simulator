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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Container for system components
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public class Model
{

    private final List<String> errors;
    private boolean compiled;
    final List<IComponent> sources;
    final Map<String, IComponent> components;

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
     * 
     * @param errors 
     */
    public void addErrors(final List<String> errors)
    {
        this.errors.addAll(errors);
        this.compiled = this.errors.isEmpty();
    }

    /**
     * Load all the elements of the model into the container
     * 
     * @param sources
     * @param components 
     */
    public void addComponents(final Collection<IComponent> sources,
            final Map<String, IComponent> components)
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
     * 
     * @param generate
     * @param start
     * @param end 
     * @return  
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
            System.out.println("Start simulation");
            final Simulator simulator = new Simulator(this.sources, generate);
            simulator.execute();
            System.out.println("Statistics for events that occurred between "
                    + start + " and " + end);
            final boolean multisource = this.sources.size() > 1;
            monitor.displayStatistics(simulator.completed, multisource);
            for (final IComponent component : this.components.values())
            {
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
        private final LinkedList<Event> primary;
        private final LinkedList<Event> working;
        private final List<Event> completed;
        private int capacity;
        private double highmark;

        private Simulator()
        {
            this.capacity = Simulator.FILL;
            this.primary = new LinkedList<>();
            this.working = new LinkedList<>();
            this.completed = new ArrayList<>();
            this.highmark = 0;
        }

        /**
         * 
         * @param sources
         * @param generate 
         */
        private Simulator(final List<IComponent> sources, final double generate)
        {
            this.capacity = Simulator.FILL;
            this.primary = new LinkedList<>();
            this.working = new LinkedList<>();
            this.completed = new ArrayList<>();
            this.highmark = 0;
            for (final IComponent source : sources)
            {
                do
                {
                    final Event event = source.simulate(null);
                    if (event.getStarted() > generate)
                    {
                        break;
                    }
                    this.primary.add(event);
                }
                while (true);
            }
            // Sort primary store for all events in order of completion
            this.primary.sort(Comparator.comparingDouble(Event::getCompleted));
            // Copy a fractional part of the primary store
            // We do this as the working sort will take less time
            this.fill();
            // Sort working in chronological order of last completion
            this.working.sort(Comparator.comparingDouble(Event::getCompleted));
        }

        /**
         * 
         */
        void execute()
        {
            int operations = 0;
            while (this.working.size() > 0)
            {
                final Event event = this.working.removeFirst();
                event.simulate();
                operations++;
                if (event.getComponent() == null)
                {
                    this.completed.add(event);
                }
                else
                {
                    this.working.add(event);
                    // Refill working if modified event completion is
                    // higher than the high watermark or we are getting
                    // to a low working and we still have events in the
                    // primary working
                    if (!this.primary.isEmpty()
                            && (this.working.size() < Simulator.MIN_BUFFER
                            || event.getCompleted() > this.highmark))
                    {
                        if (this.highmark < event.getCompleted()
                                && this.capacity < Simulator.MAX_BUFFER)
                        {
                            this.capacity += Simulator.FILL;
                        }
                        this.highmark = event.getCompleted();
                        this.fill();
                    }
                    // Re-sort working after we have modified the list
                    this.working.sort(Comparator
                            .comparingDouble(Event::getCompleted));
                }
            }
            System.out.println("  Simulation operations: " + operations);
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
                if (this.working.size() > Simulator.MAX_BUFFER)
                {
                    System.err.println("    System arrivals faster than system exits");
                    this.primary.addAll(this.working);
                    this.working.clear();
                    this.primary.sort(Comparator
                            .comparingDouble(Event::getCompleted));
                }
                // Copy events from main working into working working
                for (int index = 0; index < this.capacity; index++)
                {
                    if (this.primary.isEmpty())
                    {
                        break;
                    }
                    else
                    {
                        final Event event = primary.removeFirst();
                        this.working.add(event);
                        current = event.getCompleted();
                    }
                }
            }
            this.highmark = current;
        }
    }
}
