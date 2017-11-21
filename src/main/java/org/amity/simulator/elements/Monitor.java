/*
 * BuilderTest.java
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
 * Created on November 21, 2017
 */
package org.amity.simulator.elements;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.util.FastMath;

/**
 * Monitoring for simulated components in the system. Not thread safe.
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public class Monitor
{
    private final DescriptiveStatistics arrivals;
    private final DescriptiveStatistics waiting;
    private final DescriptiveStatistics processing;
    private final DescriptiveStatistics visiting;
    private final double start;
    private final double end;

    /**
     * Default constructor - hidden on purpose
     */
    private Monitor()
    {
        this.start = 0;
        this.end = 0;
        this.arrivals = new DescriptiveStatistics();
        this.waiting = new DescriptiveStatistics();
        this.processing = new DescriptiveStatistics();
        this.visiting = new DescriptiveStatistics();
    }

    /**
     * Constructor to initialize monitor
     * 
     * @param start lower bound of monitoring period
     * @param end upper bound of monitoring period
     */
    public Monitor(final double start, final double end)
    {
        final double absoluteStart = FastMath.abs(start);
        final double absoluteEnd = FastMath.abs(end);
        final double max = FastMath.max(absoluteStart, absoluteEnd);
        final double min = FastMath.min(absoluteStart, absoluteEnd);
        this.start = min;
        this.end = max;
        this.arrivals = new DescriptiveStatistics();
        this.waiting = new DescriptiveStatistics();
        this.processing = new DescriptiveStatistics();
        this.visiting = new DescriptiveStatistics();
    }

    /**
     * Calculate statistics for a component and send to standard output
     * 
     * @param component item to be interrogated
     */
    public void displayStatistics(final IComponent component)
    {
        double last = 0;
        double idle = 0;
        double arrival = 0;
        final boolean source = component instanceof Source;
        if (!source)
        {
            waiting.clear();
            processing.clear();
            visiting.clear();
        }
        arrivals.clear();
        // Collect data into statistical services
        for (final Event event : component.getLocalEvents())
        {
            final double arrived = event.getArrived();
            final double started = event.getStarted();
            final double completed = event.getCompleted();
            if (completed > this.end)
            {
                break;
            }
            if (arrived >= this.start
                    && completed <= this.end)
            {
                if (arrival > 0)
                {
                    arrivals.addValue(arrived - arrival);
                }
                if (!source)
                {
                    waiting.addValue(started - arrived);
                    processing.addValue(completed - started);
                    visiting.addValue(completed - arrived);
                    // time from completion of last event or start of time to
                    // time execution begins for this event
                    idle += started - FastMath.max(this .start, last);
                }
            }
            // last event complete time
            last = completed;
            // last arrival time
            arrival = arrived;
        }
        // total time
        final double timespan = last - this.start;
        System.out.println("Component: " + component.getLabel());
        if (source)
        {
            System.out.println("  Events generated: " + this.waiting.getN());
            System.out.println("  Generation rate");
        }
        else
        {
            System.out.println("  Events processed: " + this.waiting.getN());
            final double utilization = (timespan - idle)/ timespan;
            System.out.println("  Utilization: " + utilization);
            System.out.println("  Wait time");
            System.out.println("    Mean:" + this.waiting.getMean());
            System.out.println("    Standard Deviation:"
                    + this.waiting.getStandardDeviation());
            System.out.println("    Maximum: " + this.waiting.getMax());
            System.out.println("    Minimum: " + this.waiting.getMin());
            System.out.println("  Process time");
            System.out.println("    Mean:" + this.processing.getMean());
            System.out.println("    Standard Deviation:"
                    + this.processing.getStandardDeviation());
            System.out.println("    Maximum: " + this.processing.getMax());
            System.out.println("    Minimum: " + this.processing.getMin());
            System.out.println("  Visit time");
            System.out.println("    Mean:" + this.visiting.getMean());
            System.out.println("    Standard Deviation:"
                    + this.visiting.getStandardDeviation());
            System.out.println("    Maximum: " + this.visiting.getMax());
            System.out.println("    Minimum: " + this.visiting.getMin());
            System.out.println("  Arrival rate");
        }
        System.out.println("    Mean:" + this.arrivals.getMean());
        System.out.println("    Standard Deviation:"
                + this.arrivals.getStandardDeviation());
        System.out.println("    Maximum: " + this.arrivals.getMax());
        System.out.println("    Minimum: " + this.arrivals.getMin());
    }
}
