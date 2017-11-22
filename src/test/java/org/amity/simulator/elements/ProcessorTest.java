/*
 * ProcessorTest.java
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
 * Created on November 8, 2017
 */
package org.amity.simulator.elements;

import java.util.Comparator;
import java.util.LinkedList;
import org.amity.simulator.generators.Constant;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.amity.simulator.generators.IGenerator;

/**
 * Tests processing statistics of delay modeling a system component.
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public class ProcessorTest
{

    public ProcessorTest()
    {
    }

    @BeforeClass
    public static void setUpClass()
    {
    }

    @AfterClass
    public static void tearDownClass()
    {
    }

    @Before
    public void setUp()
    {
    }

    @After
    public void tearDown()
    {
    }

    /**
     * Test of simulate method, of class Processor.
     */
    @Test
    public void testSimulate()
    {
        System.out.println("simulate");
        final double sourcePeriod = 1;
        final double period = 2;
        assertTrue(period > sourcePeriod);
        final String sourceLabel = "source";
        final String label = "delay";
        final int eventTotal = 4;
        final IGenerator sourceFunction = new Constant(sourcePeriod);
        final IGenerator function = new Constant(period);
        final IComponent instance = new Processor(label, function, null);
        final LinkedList<Event> events = new LinkedList<>();
        final IComponent source
                = new Source(sourceLabel, sourceFunction, label);
        source.setNext(instance);
        for (int count = 0; count < eventTotal; count++)
        {
            final Event event = source.simulate(null);
            events.add(event);
        }
        // sort events by arrival time
        events.sort(Comparator.comparingDouble(Event::getArrived));
        assertTrue(events.size() == eventTotal);
        while (events.size() > 0)
        {
            final Event event = events.removeFirst();
            event.simulate();
            if (event.getComponent() != null)
            {
                events.add(event);
                events.sort(Comparator.comparingDouble(Event::getArrived));
            }
        }
        System.out.println("  check events are preserved");
        final List<Event> local = instance.getLocalEvents();
        assertTrue(local.size() == eventTotal);
        assertTrue(events.isEmpty());
        double sourceTick = 0;
        double tick = sourcePeriod;
        System.out.println("  check event statistics are correct");
        for (final Event event : local)
        {
            System.out.println("    " + event.getSource() + " "
                    + event.getLabel() + " -> arrival: " + event.getArrived()
                    + ", start: " + event.getStarted() + ", complete: "
                    + event.getCompleted());
            sourceTick += sourcePeriod;
            assertTrue(event.getArrived() == sourceTick);
            assertTrue(event.getStarted() == tick);
            final double completeTick = tick + period;
            assertTrue(event.getCompleted() == completeTick);
            final double elapsed = completeTick - sourceTick;
            assertTrue(event.getCompleted() - event.getCreated() == elapsed);
            final double executed = completeTick - tick;
            assertTrue(event.getExecuted() == executed);
            tick = completeTick;
        }
    }

    /**
     * Test of reset method, of class Processor.
     */
    @Test
    public void testReset()
    {
        System.out.println("reset");
        System.out.println("  populate components");        
        final double sourcePeriod = 1;
        final double period = 2;
        assertTrue(period > sourcePeriod);
        final String sourceLabel = "source";
        final String label = "delay";
        final int eventTotal = 4;
        final IGenerator sourceFunction = new Constant(sourcePeriod);
        final IGenerator function = new Constant(period);
        final IComponent instance = new Processor(label, function, null);
        final LinkedList<Event> events = new LinkedList<>();
        final IComponent source
                = new Source(sourceLabel, sourceFunction, label);
        source.setNext(instance);
        for (int count = 0; count < eventTotal; count++)
        {
            final Event event = source.simulate(null);
            events.add(event);
        }
        // sort events by arrival time
        events.sort(Comparator.comparingDouble(Event::getArrived));
        assertTrue(events.size() == eventTotal);
        while (events.size() > 0)
        {
            final Event event = events.removeFirst();
            event.simulate();
            if (event.getComponent() != null)
            {
                events.add(event);
                events.sort(Comparator.comparingDouble(Event::getArrived));
            }
        }
        final List<Event> local = instance.getLocalEvents();
        assertTrue(local.size() == eventTotal);
        System.out.println("  check reset clears events");        
        source.reset();
        assertTrue(local.isEmpty());
        System.out.println("  repopulate and check events are preserved");
        for (int count = 0; count < eventTotal; count++)
        {
            final Event event = source.simulate(null);
            events.add(event);
        }
        // sort events by arrival time
        events.sort(Comparator.comparingDouble(Event::getArrived));
        assertTrue(events.size() == eventTotal);
        while (events.size() > 0)
        {
            final Event event = events.removeFirst();
            event.simulate();
            if (event.getComponent() != null)
            {
                events.add(event);
                events.sort(Comparator.comparingDouble(Event::getArrived));
            }
        }
        assertTrue(local.size() == eventTotal);
        assertTrue(events.isEmpty());
        double sourceTick = 0;
        double tick = sourcePeriod;
        System.out.println("  check event statistics are correct");
        for (final Event event : local)
        {
            System.out.println("    " + event.getSource() + " "
                    + event.getLabel() + " -> arrival: " + event.getArrived()
                    + ", start: " + event.getStarted() + ", complete: "
                    + event.getCompleted());
            sourceTick += sourcePeriod;
            assertTrue(event.getArrived() == sourceTick);
            assertTrue(event.getStarted() == tick);
            final double completeTick = tick + period;
            assertTrue(event.getCompleted() == completeTick);
            final double elapsed = completeTick - sourceTick;
            assertTrue(event.getCompleted() - event.getCreated() == elapsed);
            final double executed = completeTick - tick;
            assertTrue(event.getExecuted() == executed);
            tick = completeTick;
        }
    }
}
