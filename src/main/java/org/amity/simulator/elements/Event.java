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
    private String source;
    private String label;
    private final double created;
    private double arrived;
    private double started;
    private double completed;
    private double executed;
    private IComponent component;

    /**
     * Hidden default constructor to avoid implicit creation
     */
    private Event()
    {
        this.source = null;
        this.label = null;
        this.created = 0;
        this.arrived = 0;
        this.started = 0;
        this.completed = 0;
        this.executed = 0;
        this.component = null;
    }

    /**
     * Construct labeled event
     *
     * @param source name of source
     * @param label distinguishing name of event
     * @param created time event was created
     */
    public Event(final String source, final String label, final double created)
    {
        this.source = source;
        this.label = label;
        this.created = created;
        this.arrived = 0;
        this.started = 0;
        this.completed = 0;
        this.executed = 0;
        this.component = null;
    }

    /**
     * Construct value copy of event
     *
     * @param copy event to be replicated
     */
    public Event(Event copy)
    {
        this.source = copy.source;
        this.label = copy.label;
        this.created = copy.created;
        this.arrived = copy.arrived;
        this.started = copy.started;
        this.completed = copy.completed;
        this.executed = copy.executed;
        this.component = copy.component;
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
        this.arrived = arrival;
        this.started = start;
        this.completed = complete;
    }

    /**
     *
     * @param source distinguishing name of source
     */
    public void setSource(final String source)
    {
        this.source = source;
    }

    /**
     *
     * @return distinguishing name of source
     */
    public String getSource()
    {
        return this.source;
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
     * @return time event was created
     */
    public double getCreated()
    {
        return this.created;
    }

    /**
     *
     * @param arrived time arrived at component for processing
     */
    public void setArrived(final double arrived)
    {
        this.arrived = arrived;
    }

    /**
     *
     * @return time arrived at component for processing
     */
    public double getArrived()
    {
        return this.arrived;
    }

    /**
     *
     * @param started time began processing by component
     */
    public void setStarted(final double started)
    {
        this.started = started;
    }

    /**
     *
     * @return time began processing by component
     */
    public double getStarted()
    {
        return this.started;
    }

    /**
     *
     * @param completed time finished at current component
     */
    public void setCompleted(final double completed)
    {
        this.completed = completed;
    }

    /**
     *
     * @return time finished at current component
     */
    public double getCompleted()
    {
        return this.completed;
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

    /**
     * 
     * @param component current system element event is at
     */
    public void setComponent(final IComponent component)
    {
        this.component = component;
    }

    /**
     * 
     * @return current system element event is at
     */
    public IComponent getComponent()
    {
        return this.component;
    }

    /**
     * Simulate event passing through component
     */
    public void simulate()
    {
        if (component != null)
        {
            this.component.simulate(this);
        }
    }
}
