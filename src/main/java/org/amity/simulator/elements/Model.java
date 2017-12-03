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
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.amity.simulator.main.Main;

/**
 * Container for system components
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public class Model
{

    private final static int FILL = 500;
    private final static int MIN_BUFFER = 20;
    private final static int MAX_BUFFER = 2000;
    public final List<String> errors;
    public boolean compiled;
    public final List<IComponent> sources;
    public final Map<String, IComponent> components;

    public Model()
    {
        this.errors = new ArrayList<>();
        this.compiled = false;
        this.sources = new ArrayList<>();
        this.components = new HashMap<>();
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
        if (this.compiled)
        {
            System.out.println("Completed compilation");
            final Monitor monitor = new Monitor(start, end);
            final LinkedList<Event> events = new LinkedList<>();
            final List<Event> completed = new ArrayList<>();
            System.out.println("Start simulation");
            int operations = 0;
            for (final IComponent source : this.sources)
            {
                do
                {
                    final Event event = source.simulate(null);
                    if (event.getStarted() > generate)
                    {
                        break;
                    }
                    events.add(event);
                }
                while (true);
            }
            // Sort primary store for all events in order of completion
            events.sort(Comparator
                    .comparingDouble(Event::getCompleted));
            // Copy a fractional part of the primary store
            // We do this as the buffer sort will take less time
            final LinkedList<Event> buffer = new LinkedList<>();
            double highmark = this.fillBuffer(events, buffer, 0);
            // Sort buffer in chronological order of last completion
            buffer.sort(Comparator
                    .comparingDouble(Event::getCompleted));
            while (buffer.size() > 0)
            {
                final Event event = buffer.removeFirst();
                event.simulate();
                operations++;
                if (event.getComponent() == null)
                {
                    completed.add(event);
                }
                else
                {
                    buffer.add(event);
                    // Refill buffer if modified event completion is
                    // higher than the high watermark or we are getting
                    // to a low buffer and we still have events in the
                    // primary buffer
                    if (!events.isEmpty()
                            && (buffer.size() < Model.MIN_BUFFER
                            || event.getCompleted() > highmark))
                    {
                        highmark = this.fillBuffer(events, buffer,
                                event.getCompleted());
                    }
                    // Re-sort buffer after we have modified the list
                    buffer.sort(Comparator
                            .comparingDouble(Event::getCompleted));
                }
            }
            System.out.println("Statistics for events that occurred between "
                    + start + " and " + end);
            monitor.displayStatistics(completed);
            for (final IComponent component : this.components.values())
            {
                component.generateStatistics(monitor);
            }
            System.out.println("Operations: " + operations);
        }
        return this.compiled;
    }

    /**
     * Transfer events from the primary event storage to the working area
     * 
     * @param primary main store for generated events
     * @param buffer working storage for processing events
     * @param highmark highest time boundary for events in the buffer
     * @return updated highmark for the working buffer
     */
    private double fillBuffer(final LinkedList<Event> primary,
            final LinkedList<Event> buffer, final double highmark)
    {
        double current = highmark;
        if (!primary.isEmpty())
        {
            // Re-calculate timeline due to extreme out-of-order insertions
            if (buffer.size() > Model.MAX_BUFFER)
            {
                System.out.println("System arrivals faster than system exits");
                primary.addAll(buffer);
                buffer.clear();
                primary.sort(Comparator.comparingDouble(Event::getCompleted));
            }
            // Copy events from main buffer into working buffer
            for (int index = 0; index < Model.FILL; index++)
            {
                if (primary.isEmpty())
                {
                    break;
                }
                else
                {
                    final Event event = primary.removeFirst();
                    buffer.add(event);
                    current = event.getCompleted();
                }
            }
        }
        return current;
    }
}
