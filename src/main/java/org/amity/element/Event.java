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
 * Created on November 6, 2017
 */
package org.amity.element;

/**
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public class Event
{
    private String label;
    private double arrival;
    private double start;
    private double complete;
    private double elapsed;

    private Event()
    {
        this.label = null;
        this.arrival = 0;
        this.start = 0;
        this.complete = 0;
        this.elapsed = 0;
    }

    public Event(String label)
    {
        this.label = label;
        this.arrival = 0;
        this.start = 0;
        this.complete = 0;
        this.elapsed = 0;
    }

    public Event(Event copy)
    {
        this.label = copy.label;
        this.arrival = copy.arrival;
        this.start = copy.start;
        this.complete = copy.complete;
        this.elapsed = copy.elapsed;
    }

    public void setValues(final double arrival, final double start,
            final double complete)
    {
        this.arrival = arrival;
        this.start = start;
        this.complete = complete;
    }

    public void setLabel(final String label)
    {
        this.label = label;
    }

    public String getLabel()
    {
        return this.label;
    }

    public void setArrival(final double arrival)
    {
        this.arrival = arrival;
    }

    public double getArrival()
    {
        return this.arrival;
    }

    public void setStart(final double start)
    {
        this.start = start;
    }

    public double getStart()
    {
        return this.start;
    }

    public void setComplete(final double complete)
    {
        this.complete = complete;
    }

    public double getComplete()
    {
        return this.complete;
    }

    public void setElapsed(final double elapsed)
    {
        this.elapsed = elapsed;
    }

    public double getElapsed()
    {
        return this.elapsed;
    }
}
