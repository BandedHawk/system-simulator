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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import org.amity.simulator.generators.Constant;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.amity.simulator.generators.IGenerator;
import org.amity.simulator.language.NameValue;
import org.amity.simulator.language.Vocabulary;

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
        final IGenerator sourceGenerator = new Constant(sourcePeriod,
                sourceLabel, label);
        final IGenerator generator = new Constant(period, sourceLabel, null);
        final List<IGenerator> generators = new ArrayList<>();
        generators.add(generator);
        final IComponent instance = new Processor(label, generators, false);
        final LinkedList<Event> events = new LinkedList<>();
        final IComponent source
                = new Source(sourceLabel, sourceGenerator, false);
        sourceGenerator.setNext(instance);
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
        final IGenerator sourceGenerator = new Constant(sourcePeriod,
                sourceLabel, label);
        final IGenerator generator = new Constant(period, sourceLabel, null);
        final List<IGenerator> generators = new ArrayList<>();
        generators.add(generator);
        final IComponent instance = new Processor(label, generators, false);
        final LinkedList<Event> events = new LinkedList<>();
        final IComponent source
                = new Source(sourceLabel, sourceGenerator, false);
        sourceGenerator.setNext(instance);
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

    /**
     * Test of getLocalEvents method, of class Processor.
     */
    @Test
    public void testGetLocalEvents()
    {
        System.out.println("getLocalEvents");
        final double sourcePeriod = 1;
        final double period = 2;
        assertTrue(period > sourcePeriod);
        final String sourceLabel = "source";
        final String label = "delay";
        final int eventTotal = 4;
        final IGenerator sourceGenerator = new Constant(sourcePeriod,
                sourceLabel, label);
        final IGenerator generator = new Constant(period, sourceLabel, null);
        final List<IGenerator> generators = new ArrayList<>();
        generators.add(generator);
        final IComponent instance = new Processor(label, generators, false);
        final LinkedList<Event> events = new LinkedList<>();
        final IComponent source
                = new Source(sourceLabel, sourceGenerator, false);
        sourceGenerator.setNext(instance);
        assertEquals(0, instance.getLocalEvents().size());
        for (int count = 0; count < eventTotal; count++)
        {
            final Event event = source.simulate(null);
            events.add(event);
        }
        assertEquals(eventTotal, events.size());
    }

    /**
     * Test of getLabel method, of class Processor.
     */
    @Test
    public void testGetLabel()
    {
        System.out.println("getLabel");
        final double sourcePeriod = 1;
        final double period = 2;
        assertTrue(period > sourcePeriod);
        final String sourceLabel = "source";
        final String label = "delay";
        final int eventTotal = 4;
        final IGenerator sourceGenerator = new Constant(sourcePeriod,
                sourceLabel, label);
        final IGenerator generator = new Constant(period, sourceLabel, null);
        final List<IGenerator> generators = new ArrayList<>();
        generators.add(generator);
        final IComponent instance = new Processor(label, generators, false);
        final LinkedList<Event> events = new LinkedList<>();
        final IComponent source
                = new Source(sourceLabel, sourceGenerator, false);
        sourceGenerator.setNext(instance);
        assertEquals(label, instance.getLabel());
        // No way to adequately test this except under load
        assertTrue(instance.getDepths() != null);
        assertEquals(0, instance.getDepths().size());
    }

    /**
     * Test of getDepths method, of class Processor.
     */
    @Test
    public void testGetDepths()
    {
        System.out.println("getDepths");
        final double sourcePeriod = 1;
        final double period = 2;
        assertTrue(period > sourcePeriod);
        final String sourceLabel = "source";
        final String label = "delay";
        final int eventTotal = 4;
        final IGenerator sourceGenerator = new Constant(sourcePeriod,
                sourceLabel, label);
        final IGenerator generator = new Constant(period, sourceLabel, null);
        final List<IGenerator> generators = new ArrayList<>();
        generators.add(generator);
        final IComponent instance = new Processor(label, generators, false);
        final LinkedList<Event> events = new LinkedList<>();
        final IComponent source
                = new Source(sourceLabel, sourceGenerator, false);
    }

    /**
     * Test of description method, of class Processor.
     */
    @Test
    public void testDescription()
    {
        System.out.println("description");
        final double sourcePeriod = 1;
        final double period = 2;
        assertTrue(period > sourcePeriod);
        final String sourceLabel = "source";
        final String label = "delay";
        final int eventTotal = 4;
        final IGenerator sourceGenerator = new Constant(sourcePeriod,
                sourceLabel, label);
        final IGenerator generator = new Constant(period, sourceLabel, null);
        final List<IGenerator> generators = new ArrayList<>();
        generators.add(generator);
        final IComponent instance = new Processor(label, generators, false);
        final LinkedList<Event> events = new LinkedList<>();
        final IComponent source
                = new Source(sourceLabel, sourceGenerator, false);
        sourceGenerator.setNext(instance);
        assertTrue(instance.description() != null);
        assertTrue(instance.description().endsWith("]"));
        assertTrue(instance.description().startsWith("["));
    }

    /**
     * Test of getReferences method, of class Processor.
     */
    @Test
    public void testGetReferences()
    {
        System.out.println("getReferences");
        final double sourcePeriod = 1;
        final double period = 2;
        assertTrue(period > sourcePeriod);
        final String sourceLabel = "source 1";
        final String label = "delay";
        final String reference = "end";
        final String reference2 = "bad end";
        final IGenerator sourceGenerator = new Constant(sourcePeriod,
                sourceLabel, label);
        final IGenerator generator1 = new Constant(period, sourceLabel,
                reference);
        IGenerator generator2 = new Constant(period, Vocabulary.DEFAULT,
                reference);
        System.out.println("  Add 2 generators with same endpoint");
        List<IGenerator> generators = new ArrayList<>();
        generators.add(generator1);
        generators.add(generator2);
        IComponent instance = new Processor(label, generators, false);
        Map<String, List<IGenerator>> map = instance.getReferences();
        assertEquals(1, map.size());
        assertEquals(2, map.get(reference).size());
        assertEquals(generator2, map.get(reference).get(0));
        assertEquals(generator1, map.get(reference).get(1));
        System.out.println("  Add 2 generators with different endpoints");
        generator2 = new Constant(period, Vocabulary.DEFAULT,
                reference2);
        generators = new ArrayList<>();
        generators.add(generator1);
        generators.add(generator2);
        instance = new Processor(label, generators, false);
        map = instance.getReferences();
        assertEquals(2, map.size());
        assertEquals(1, map.get(reference).size());
        assertEquals(generator1, map.get(reference).get(0));
        assertEquals(1, map.get(reference2).size());
        assertEquals(generator1, map.get(reference).get(0));
        assertEquals(generator2, map.get(reference2).get(0));
    }

    /**
     * Test of instance method, of class Processor.
     */
    @Test
    public void testInstance()
    {
        System.out.println("instance");
        final double period = 5;
        final String source = Vocabulary.DEFAULT;
        final String reference = "database";
        final IGenerator generator = new Constant(period, source, reference);
        final List<IGenerator> generators = new ArrayList<>();
        generators.add(generator);
        final List<NameValue> pairs = new ArrayList<>();
        final String name  = "CPU";
        NameValue pair = new NameValue(Vocabulary.NAME, name);
        pairs.add(pair);
        pair = new NameValue(Vocabulary.MONITOR, "Y");
        pairs.add(pair);
        IComponent instance = Processor.instance(pairs, generators);
        assertEquals(name, instance.getLabel());
        assertEquals(1, instance.getReferences().size());
        assertEquals(1, instance.getReferences().get(reference).size());
    }
}
