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
package org.amity.component;

import java.util.ArrayList;
import java.util.List;
import org.amity.element.Event;

/**
 * Component represents an event source, generating events spaced out as
 * specified by the generation function.
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public class Source implements IComponent
{

    private final String label;
    private final IFunction function;
    private final IComponent next;
    private int eventTotal;
    private final List<Event> local;

    private Source()
    {
        this.label = "dummy";
        this.local = new ArrayList<>();
        this.eventTotal = 0;
        this.next = null;
        this.function = null;
    }

    public Source(final String label, final int eventTotal,
            final IFunction function, final IComponent next)
    {
        this.label = label;
        this.eventTotal = eventTotal;
        this.function = function;
        this.next = next;
        this.local = new ArrayList<>();
    }

    @Override
    public void simulate(final List<Event> events)
    {
        if (events == null && this.eventTotal > 0)
        {
            // Cumulative event timeline
            double counter = 0;
            this.local.clear();
            final List<Double> values = function.generate(eventTotal);
            for (int count = 0; count < this.eventTotal; count++)
            {
                final Event event = new Event(Integer.toString(count));
                final double value = values.get(count);
                counter = counter + value;
                event.setValues(counter, counter, counter);
                this.local.add(event);
            }
        }
        else if (events != null)
        {
            this.local.clear();
            this.local.addAll(events);
            this.eventTotal = events.size();
        }
        if (next != null && this.eventTotal > 0)
        {
            // Make a global copy to pass on
            final List<Event> global = new ArrayList<>();
            this.local.stream().map((event) -> new Event(event)).forEach((copy) ->
            {
                global.add(copy);
            });
            next.simulate(global);
        }
    }

    @Override
    public List<Event> getLocalEvents()
    {
        return this.local;
    }
}
