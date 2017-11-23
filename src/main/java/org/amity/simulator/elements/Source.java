/*
 * Source.java
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
 * Created on November 7, 2017
 */
package org.amity.simulator.elements;

import java.util.ArrayList;
import java.util.List;
import org.amity.simulator.generators.IGenerator;

/**
 * Implements an event source, generating events spread out in time as specified
 * by the generation function.
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public class Source implements IComponent
{

    private final String label;
    private final IGenerator function;
    private IComponent next;
    private final String nextReference;
    private final List<Event> local;
    private final boolean monitor;
    private int counter;
    private double time;

    private Source()
    {
        this.label = "dummy";
        this.local = new ArrayList<>();
        this.nextReference = null;
        this.function = null;
        this.monitor = false;
        this.counter = 0;
        this.time = 0;
        this.next = null;
    }

    public Source(final String label, final IGenerator function,
            final String component, final boolean monitor)
    {
        this.label = label;
        this.nextReference = component;
        this.function = function;
        this.local = new ArrayList<>();
        this.monitor = monitor;
        this.counter = 0;
        this.time = 0;
        this.next = null;
    }

    @Override
    public Event simulate(final Event injectedEvent)
    {
        if (injectedEvent == null)
        {
            // Cumulative injectedEvent timeline
            final double value = function.generate();
            this.time += value;
            final Event event = new Event(this.label,
                    Integer.toString(this.counter++), this.time);
            event.setValues(this.time, this.time, this.time);
            this.local.add(event);
        }
        else
        {
            this.local.add(injectedEvent);
        }
        // Make a global copy to pass on
        final Event global =
                new Event(this.local.get(this.local.size() - 1));
        global.setComponent(this.next);
        return global;
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
        this.counter = 0;
        this.time = 0;
        if (this.next != null)
        {
            this.next.reset();
        }
    }

    @Override
    public IComponent getNext()
    {
        return this.next;
    }

    @Override
    public void setNext(final IComponent next)
    {
        this.next = next;
    }

    @Override
    public String getNextReference()
    {
        return this.nextReference;
    }

    @Override
    public String getLabel()
    {
        return label;
    }

    @Override
    public List<Integer> getDepths()
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
        return this.function.characteristics();
    }
}
