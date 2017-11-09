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
package org.amity.simulator.elements;

/**
 * Element that interacts with system components
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
    private double executed;

    /**
     * Hidden default constructor to avoid implicit creation
     */
    private Event()
    {
        this.label = null;
        this.arrival = 0;
        this.start = 0;
        this.complete = 0;
        this.elapsed = 0;
        this.executed = 0;
    }

    /**
     * Construct labeled event
     *
     * @param label distinguishing name of event
     */
    public Event(String label)
    {
        this.label = label;
        this.arrival = 0;
        this.start = 0;
        this.complete = 0;
        this.elapsed = 0;
        this.executed = 0;
    }

    /**
     * Construct value copy of event
     *
     * @param copy event to be replicated
     */
    public Event(Event copy)
    {
        this.label = copy.label;
        this.arrival = copy.arrival;
        this.start = copy.start;
        this.complete = copy.complete;
        this.elapsed = copy.elapsed;
        this.executed = copy.executed;
    }

    /**
     * Convenience method for updating event information
     *
     * @param arrival time arrived at component for processing
     * @param start time began processing by component
     * @param complete time finished at current component
     */
    public void setValues(final double arrival, final double start,
            final double complete)
    {
        this.arrival = arrival;
        this.start = start;
        this.complete = complete;
    }

    /**
     *
     * @param label distinguishing name of event
     */
    public void setLabel(final String label)
    {
        this.label = label;
    }

    /**
     *
     * @return distinguishing name of event
     */
    public String getLabel()
    {
        return this.label;
    }

    /**
     *
     * @param arrival time arrived at component for processing
     */
    public void setArrival(final double arrival)
    {
        this.arrival = arrival;
    }

    /**
     *
     * @return time arrived at component for processing
     */
    public double getArrival()
    {
        return this.arrival;
    }

    /**
     *
     * @param start time began processing by component
     */
    public void setStart(final double start)
    {
        this.start = start;
    }

    /**
     *
     * @return time began processing by component
     */
    public double getStart()
    {
        return this.start;
    }

    /**
     *
     * @param complete time finished at current component
     */
    public void setComplete(final double complete)
    {
        this.complete = complete;
    }

    /**
     *
     * @return time finished at current component
     */
    public double getComplete()
    {
        return this.complete;
    }

    /**
     *
     * @param elapsed time alive
     */
    public void setElapsed(final double elapsed)
    {
        this.elapsed = elapsed;
    }

    /**
     *
     * @return time alive
     */
    public double getElapsed()
    {
        return this.elapsed;
    }

    /**
     *
     * @param executed time actively processed
     */
    public void setExecuted(final double executed)
    {
        this.executed = executed;
    }

    /**
     *
     * @return time actively processed
     */
    public double getExecuted()
    {
        return this.executed;
    }
}
