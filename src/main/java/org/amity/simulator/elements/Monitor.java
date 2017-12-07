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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private final DescriptiveStatistics elapsed;
    private final DescriptiveStatistics executed;
    private final DescriptiveStatistics queue;
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
        this.elapsed = new DescriptiveStatistics();
        this.executed = new DescriptiveStatistics();
        this.queue = new DescriptiveStatistics();
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
        this.elapsed = new DescriptiveStatistics();
        this.executed = new DescriptiveStatistics();
        this.queue = new DescriptiveStatistics();
    }

    /**
     * Calculate statistics for a component and send to standard output
     *
     * @param component item to be interrogated
     */
    void displayStatistics(final IComponent component)
    {
        double last = 0;
        double idle = 0;
        double arrival = 0;
        double startPeriod = 0;
        boolean counted = false;
        final boolean source = component instanceof Source;
        final boolean processor = component instanceof Processor;
        if (!source)
        {
            this.waiting.clear();
            this.processing.clear();
            this.visiting.clear();
        }
        this.arrivals.clear();
        // Collect data into statistical services
        assert component != null : "unexpected null component";
        final List<Event> events = component.getLocalEvents();
        events.sort(Comparator.comparingDouble(Event::getArrived));
        for (final Event event : component.getLocalEvents())
        {
            final double arrived = event.getArrived();
            final double started = event.getStarted();
            final double completed = event.getCompleted();
            if (completed > this.end)
            {
                break;
            }
            if (counted || arrived >= this.start)
            {
                if (arrival > 0)
                {
                    this.arrivals.addValue(arrived - arrival);
                }
                if (!source)
                {
                    if (!counted)
                    {
                        startPeriod = arrived;
                        counted = true;
                    }
                    this.waiting.addValue(started - arrived);
                    this.processing.addValue(completed - started);
                    this.visiting.addValue(completed - arrived);
                    // time from completion of last event or start of time to
                    // time execution begins for this event
                    idle += started - FastMath.max(this.start, last);
                }
            }
            // last event complete time
            last = completed;
            // last arrival time
            arrival = arrived;
        }
        // total time
        final double timespan = last - this.start;
        // throughput based on arrival of first event to completion of
        // last event
        final double throughput = this.arrivals.getN() / (last - startPeriod);
        System.out.println("Component: " + component.getLabel());
        System.out.println("  function: " + component.description());
        if (source)
        {
            System.out.println("  Events generated: " + this.arrivals.getN());
            System.out.println("  Generation rate: " + throughput
                    + " events per tick");
            System.out.println("               Or: " + 1/throughput
                    + " ticks between events");
            System.out.println("  Generation characteristics");
        }
        else
        {
            queue.clear();
            final List<Integer> depths = component.getDepths();
            for (final int depth : depths)
            {
                queue.addValue(depth);
            }
            System.out.println("  Events processed: " + this.waiting.getN());
            final double utilization = (timespan - idle) / timespan;
            if (processor)
            {
                System.out.println("  Utilization: " + utilization * 100
                        + " %");
            }
            System.out.println("  Throughput: " + throughput
                    + " events per tick");
            System.out.println("          Or: " + 1/throughput
                    + " ticks between events");
            if (processor)
            {
                System.out.println("  Queued events");
                System.out.println("    Mean: " + this.queue.getMean());
                System.out.println("    Standard deviation: "
                        + this.queue.getStandardDeviation());
                System.out.println("    Median: "
                        + this.queue.getPercentile(50));
                System.out.println("    Maximum: " + this.queue.getMax());
                System.out.println("    Minimum: " + this.queue.getMin());
                assert this.queue.getMin() >=0
                        : "Obtained minimum below 0";
                System.out.println("  Wait time");
                System.out.println("    Mean: " + this.waiting.getMean()
                        + " ticks");
                System.out.println("    Standard deviation: "
                        + this.waiting.getStandardDeviation());
                System.out.println("    Median: "
                        + this.waiting.getPercentile(50) + " ticks");
                System.out.println("    Maximum: " + this.waiting.getMax()
                        + " ticks");
                System.out.println("    Minimum: " + this.waiting.getMin()
                        + " ticks");
                assert this.waiting.getMin() >=0
                        : "Obtained minimum below 0";
                System.out.println("  Process time");
                System.out.println("    Mean: " + this.processing.getMean()
                        + " ticks");
                System.out.println("    Standard Deviation: "
                        + this.processing.getStandardDeviation());
                System.out.println("    Median: "
                        + this.processing.getPercentile(50) + " ticks");
                System.out.println("    Maximum: " + this.processing.getMax()
                        + " ticks");
                System.out.println("    Minimum: " + this.processing.getMin()
                        + " ticks");
                assert this.processing.getMin() >=0
                        : "Obtained minimum below 0";
                System.out.println("  Visit time");
                System.out.println("    Mean: " + this.visiting.getMean()
                        + " ticks");
                System.out.println("    Standard Deviation: "
                        + this.visiting.getStandardDeviation());
                System.out.println("    Median: "
                        + this.visiting.getPercentile(50) + " ticks");
                System.out.println("    Maximum: " + this.visiting.getMax()
                        + " ticks");
                System.out.println("    Minimum: " + this.visiting.getMin()
                        + " ticks");
                assert this.visiting.getMin() >=0
                        : "Obtained minimum below 0";
            }
            System.out.println("  Arrival characteristics");
        }
        System.out.println("    Mean: " + this.arrivals.getMean() + " ticks");
        System.out.println("    Standard Deviation: "
                + this.arrivals.getStandardDeviation());
        System.out.println("    Median: " + this.arrivals.getPercentile(50)
                + " ticks");
        System.out.println("    Maximum: " + this.arrivals.getMax() + " ticks");
        System.out.println("    Minimum: " + this.arrivals.getMin() + " ticks");
        assert this.arrivals.getMin() >=0 : "Obtained minimum below 0";
    }

    /**
     * Calculate statistics for events that completed processing in the system
     * 
     * @param events list of completed events
     * @param multisource
     */
    public void displayStatistics(final List<Event> events,
            final boolean multisource)
    {
        waiting.clear();
        processing.clear();
        visiting.clear();
        arrivals.clear();
        elapsed.clear();
        executed.clear();
        boolean counted = false;
        // Collect data for statistical services
        final Map<String, List<Event>> sources = new HashMap<>();
        final List<Event> general = new ArrayList<>();
        events.sort(Comparator.comparingDouble(Event::getCreated));
        for (final Event event : events)
        {
            // Can't guarantee events will finish in same order as creation
            if (event.getCompleted() <= this.end)
            {
                // Short-circuit comparison
                if (counted || event.getCreated() >= this.start)
                {
                    if (!counted)
                    {
                        counted = true;
                    }
                    general.add(event);
                    if (multisource)
                    {
                        final List<Event> list
                                = sources.containsKey(event.getSource())
                                ? sources.get(event.getSource())
                                : new ArrayList<>();
                        sources.putIfAbsent(event.getSource(), list);
                        list.add(event);
                    }
                }
            }
        }
        System.out.println("General event information");
        generateStatistics(general, true);
        if (multisource)
        {
            for (final String source : sources.keySet())
            {
                System.out.println("    '" + source + "' event information");
                generateStatistics(sources.get(source), false);
            }
        }
    }

    /**
     * 
     * @param events
     * @param general 
     */
    private void generateStatistics(final List<Event> events,
            final boolean general)
    {
        this.elapsed.clear();
        this.executed.clear();
        for (final Event event : events)
        {
            elapsed.addValue(event.getLifetime());
            executed.addValue(event.getExecuted());
        }
        final double started = events.isEmpty() ? 0
                : events.get(0).getCreated();
        final double completed = events.isEmpty() ? 0
                : events.get(events.size() - 1).getCreated();
        final double throughput = this.elapsed.getN() / (completed - started);
        final double active = this.executed.getMean() / this.elapsed.getMean();
        List<String> messages = new ArrayList<>();
        messages.add("  Events completed processing: "
                + this.elapsed.getN());
        messages.add("  Throughput: " + throughput  + " events per tick");
        messages.add("          Or: " + 1/throughput
                    + " ticks between events");
        messages.add("  Ratio of processing in lifetime: " + active * 100
                + " %");
        messages.add("  Event lifetime");
        messages.add("    Mean: " + this.elapsed.getMean() + " ticks");
        messages.add("    Standard Deviation: "
                + this.elapsed.getStandardDeviation());
        messages.add("    Median: " + this.elapsed.getPercentile(50)
                + " ticks");
        messages.add("    Maximum time in system: "
                + this.elapsed.getMax() + " ticks");
        messages.add("    Minimum time in system: "
                + this.elapsed.getMin() + " ticks");
        assert this.elapsed.getMin() >=0 : "Obtained minimum below 0";
        messages.add("  Event in execution");
        messages.add("    Mean: " + this.executed.getMean() + " ticks");
        messages.add("    Standard Deviation: "
                + this.executed.getStandardDeviation());
        messages.add("    Median: " + this.executed.getPercentile(50)
                + " ticks");
        messages.add("    Maximum time processing: "
                + this.executed.getMax() + " ticks");
        messages.add("    Minimum time processing: "
                + this.executed.getMin() + " ticks");
        assert this.executed.getMin() >=0 : "Obtained minimum below 0";
        for(final String message : messages)
        {
            final StringBuilder string = general ? new StringBuilder()
                    : new StringBuilder("    ");
            string.append(message);
            System.out.println(string.toString());
        }
    }
}
