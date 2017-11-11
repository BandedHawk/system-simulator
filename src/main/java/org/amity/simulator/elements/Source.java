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
    private final IComponent next;
    private final List<Event> local;
    private int counter;
    private double time;

    private Source()
    {
        this.label = "dummy";
        this.local = new ArrayList<>();
        this.next = null;
        this.function = null;
        this.counter = 0;
        this.time = 0;
    }

    public Source(final String label, final int eventTotal,
            final IGenerator function, final IComponent next)
    {
        this.label = label;
        this.next = next;
        this.function = function;
        this.local = new ArrayList<>();
        this.counter = 0;
        this.time = 0;
    }

    @Override
    public Event simulate(final Event injectedEvent)
    {
        if (injectedEvent == null)
        {
            // Cumulative injectedEvent timeline
            final double value = function.generate();
            final Event event = new Event(this.label,
                    Integer.toString(this.counter++));
            this.time += value;
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
}
