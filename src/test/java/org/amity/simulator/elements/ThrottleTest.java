/*
 * throttleTest.java
 *
 * (C) Copyright 2024 Jon Barnett.
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
 * Created on May 11, 2024
 */
package org.amity.simulator.elements;

import java.util.ArrayList;
import java.util.Comparator;
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
import org.amity.simulator.language.NameValue;
import org.amity.simulator.language.Vocabulary;
import org.amity.simulator.generators.Generator;

/**
 * Tests throttle statistics of availability modeling a system component.
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public class ThrottleTest
{
    final static double DELTA = 0.0000000001;

    public ThrottleTest()
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
     * Test of simulate method, of class Throttle.
     */
    @Test
    public void testSimulate()
    {
        System.out.println("simulate");
        final double sourcePeriod = 1;
        double period = 0.5;
        assertTrue(period < sourcePeriod);
        final String sourceLabel = "source";
        final String label = "delay";
        final int eventTotal = 4;
        final Generator sourceGenerator = new Constant(sourcePeriod,
                sourceLabel, label);
        Generator generator = new Constant(period, sourceLabel, null);
        final List<Generator> generators = new ArrayList<>();
        generators.add(generator);
        final List<String> priority = new ArrayList<>();
        Component instance = new Throttle(label, generators,
                priority, false);
        final LinkedList<Event> events = new LinkedList<>();
        final Component source
                = new Source(sourceLabel, sourceGenerator, null, null, false);
        sourceGenerator.setNext(instance);
        for (int count = 0; count < eventTotal; count++)
        {
            final Event event = source.simulate(null);
            events.add(event);
        }
        // sort events by arrival time
        events.sort(Comparator.comparingDouble(Event::getCompleted));
        assertTrue(events.size() == eventTotal);
        while (!events.isEmpty())
        {
            final Event event = events.removeFirst();
            event.simulate();
            if (event.getComponent() != null)
            {
                events.add(event);
                events.sort(Comparator.comparingDouble(Event::getCompleted));
            }
        }
        System.out.println("  check events are preserved");
        final List<Event> local = instance.getLocalEvents();
        assertTrue(local.size() == eventTotal);
        assertTrue(events.isEmpty());
        double sourceTick = sourcePeriod;
        double tick = sourcePeriod;
        System.out.println("  check event statistics are correct with no overlap");
        for (final Event event : local)
        {
            System.out.println("    " + event.getSource() + " "
                    + event.getLabel() + " -> arrival: " + event.getArrived()
                    + ", start: " + event.getStarted() + ", complete: "
                    + event.getCompleted());
            assertTrue(Math.abs(event.getArrived() - sourceTick) < DELTA);
            assertTrue(Math.abs(event.getStarted() - sourceTick) < DELTA);
            assertTrue(Math.abs(event.getCompleted() - sourceTick) < DELTA);
            assertTrue(Math.abs(event.getExecuted()) < DELTA);
            sourceTick += tick;
        }
        System.out.println("  check event statistics are correct with overlap");
        period = 1.3;
        assertTrue(Math.abs(period - sourcePeriod) > DELTA);
        generator = new Constant(period, sourceLabel, null);
        generators.clear();
        generators.add(generator);
        instance = new Throttle(label, generators, priority, false);
        source.reset();
        sourceGenerator.setNext(instance);
        events.clear();
        for (int count = 0; count < eventTotal; count++)
        {
            final Event event = source.simulate(null);
            events.add(event);
        }
        // sort events by arrival time
        events.sort(Comparator.comparingDouble(Event::getCompleted));
        assertTrue(events.size() == eventTotal);
        while (!events.isEmpty())
        {
            final Event event = events.removeFirst();
            event.simulate();
            if (event.getComponent() != null)
            {
                events.add(event);
                events.sort(Comparator.comparingDouble(Event::getCompleted));
            }
        }
        System.out.println("  check events are preserved");
        local.clear();
        local.addAll(instance.getLocalEvents());
        assertTrue(local.size() == eventTotal);
        assertTrue(events.isEmpty());
        sourceTick = sourcePeriod;
        double available = tick;
        boolean first = true;
        // Only aligned at the start of the run, with subsequent delays
        // requiring updates ofr event records
        for (final Event event : local)
        {
            System.out.println("    " + event.getSource() + " "
                    + event.getLabel() + " -> arrival: " + event.getArrived()
                    + ", start: " + event.getStarted() + ", complete: "
                    + event.getCompleted());
            assertTrue(Math.abs(event.getArrived() - sourceTick) < DELTA);
            if (first)
            {
                assertTrue(Math.abs(event.getStarted() - available) < DELTA);
                assertTrue(Math.abs(event.getCompleted() - available) < DELTA);
                assertTrue(Math.abs(event.getExecuted()) < DELTA);
                first = false;
            }
            else
            {
                System.out.println(Math.abs(event.getStarted() - available));
                assertTrue(Math.abs(event.getStarted() - available) > 0.0);
                assertTrue(Math.abs(event.getCompleted() - available) > 0.0);
                assertTrue(Math.abs(event.getExecuted()) < DELTA);
            }
            sourceTick += tick;
            available += period;
        }
        System.out.println("  check null event");
        final Event test = instance.simulate(null);
        assertTrue(test == null);
    }

    /**
     * Test of reset method, of class Throttle.
     */
    @Test
    public void testReset()
    {
        System.out.println("reset");
        System.out.println("  populate components");        
        final double sourcePeriod = 2;
        final double period = 1;
        assertTrue(period < sourcePeriod);
        final String sourceLabel = "source";
        final String label = "delay";
        final int eventTotal = 4;
        final Generator sourceGenerator = new Constant(sourcePeriod,
                sourceLabel, label);
        final Generator generator = new Constant(period, sourceLabel, null);
        final List<Generator> generators = new ArrayList<>();
        generators.add(generator);
        final List<String> priority = new ArrayList<>();
        final Component instance = new Throttle(label, generators,
                priority, false);
        final LinkedList<Event> events = new LinkedList<>();
        final Component source
                = new Source(sourceLabel, sourceGenerator, null, null, false);
        sourceGenerator.setNext(instance);
        for (int count = 0; count < eventTotal; count++)
        {
            final Event event = source.simulate(null);
            events.add(event);
        }
        // sort events by arrival time
        events.sort(Comparator.comparingDouble(Event::getCompleted));
        assertTrue(events.size() == eventTotal);
        while (!events.isEmpty())
        {
            final Event event = events.removeFirst();
            event.simulate();
            if (event.getComponent() != null)
            {
                events.add(event);
                events.sort(Comparator.comparingDouble(Event::getCompleted));
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
        events.sort(Comparator.comparingDouble(Event::getCompleted));
        assertTrue(events.size() == eventTotal);
        while (!events.isEmpty())
        {
            final Event event = events.removeFirst();
            event.simulate();
            if (event.getComponent() != null)
            {
                events.add(event);
                events.sort(Comparator.comparingDouble(Event::getCompleted));
            }
        }
        assertTrue(local.size() == eventTotal);
        assertTrue(events.isEmpty());
        double sourceTick = sourcePeriod;
        System.out.println("  check event statistics are correct");
        for (final Event event : local)
        {
            System.out.println("    " + event.getSource() + " "
                    + event.getLabel() + " -> arrival: " + event.getArrived()
                    + ", start: " + event.getStarted() + ", complete: "
                    + event.getCompleted());
            assertTrue(Math.abs(event.getArrived() - sourceTick) < DELTA);
            assertTrue(Math.abs(event.getStarted() - sourceTick) < DELTA);
            assertTrue(Math.abs(event.getCompleted() -  sourceTick) < DELTA);
            sourceTick += sourcePeriod;
        }
    }

    /**
     * Test of getLocalEvents method, of class Throttle.
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
        final Generator sourceGenerator = new Constant(sourcePeriod,
                sourceLabel, label);
        final Generator generator = new Constant(period, sourceLabel, null);
        final List<Generator> generators = new ArrayList<>();
        generators.add(generator);
        final List<String> priority = new ArrayList<>();
        final Component instance = new Throttle(label, generators,
                priority, false);
        final LinkedList<Event> events = new LinkedList<>();
        final Component source
                = new Source(sourceLabel, sourceGenerator, null, null, false);
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
     * Test of getLabel method, of class Throttle.
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
        final Generator generator = new Constant(period, sourceLabel, null);
        final List<Generator> generators = new ArrayList<>();
        generators.add(generator);
        final List<String> priority = new ArrayList<>();
        final Component instance = new Throttle(label, generators,
                priority, false);
        assertEquals(label, instance.getLabel());
    }

    /**
     * Test of getQueueStatistics method, of class Throttle.
     */
    @Test
    public void testGetQueueStatistics()
    {
        System.out.println("getQueueStatistics");
        final double sourcePeriod = 1;
        final double period = 2;
        assertTrue(period > sourcePeriod);
        final String sourceLabel = "source";
        final String label = "delay";
        final String label1 = "throttle";
        final int eventTotal = 4;
        final Generator sourceGenerator = new Constant(sourcePeriod,
                sourceLabel, label);
        final Generator generator = new Constant(period, sourceLabel, null);
        final List<Generator> generators = new ArrayList<>();
        generators.add(generator);
        final Generator generator1 = new Constant(period, sourceLabel, null);
        final List<Generator> generators1 = new ArrayList<>();
        generators1.add(generator1);
        final List<String> priority = new ArrayList<>();
        final Component instance = new Throttle(label, generators,
                priority, false);
        final Component throttle = new Throttle(label1, generators1,
                priority, false);
        final LinkedList<Event> events = new LinkedList<>();
        final Component source
                = new Source(sourceLabel, sourceGenerator, null, null, false);
        generator.setNext(throttle);
        sourceGenerator.setNext(instance);
        for (int count = 0; count < eventTotal; count++)
        {
            final Event event = source.simulate(null);
            events.add(event);
        }
        System.out.println("  Pre-simulate checks");
        assertTrue(events.size() == eventTotal);
        // sort events by arrival time
        System.out.println("  Check basic statistics");
        events.sort(Comparator.comparingDouble(Event::getCompleted));
        while (!events.isEmpty())
        {
            final Event event = events.removeFirst();
            event.simulate();
            if (event.getComponent() != null)
            {
                events.add(event);
                events.sort(Comparator.comparingDouble(Event::getCompleted));
            }
        }
        assertTrue(instance.getLocalEvents().size() == eventTotal);
        assertTrue(throttle.getLocalEvents().size() == eventTotal);
        System.out.println("  Check depth");
        // No way to adequately test this except under load
        assertTrue(instance.getQueueStatistics() != null);
        assertEquals(1, instance.getQueueStatistics().size());
    }

    /**
     * Test of description method, of class Throttle.
     */
    @Test
    public void testDescription()
    {
        System.out.println("description");
        System.out.println("  Test basic configuration");
        final double sourcePeriod = 1;
        final double period = 2;
        assertTrue(period > sourcePeriod);
        final String sourceLabel = "source";
        final String label = "delay";
        final Generator sourceGenerator = new Constant(sourcePeriod,
                sourceLabel, label);
        final Generator generator = new Constant(period, sourceLabel, null);
        final List<Generator> generators = new ArrayList<>();
        generators.add(generator);
        final List<String> priority = new ArrayList<>();
        final Component instance = new Throttle(label, generators,
                priority, false);
        sourceGenerator.setNext(instance);
        assertTrue(instance.description() != null);
        assertTrue(instance.description().endsWith("]"));
        assertTrue(instance.description().startsWith("["));
        System.out.println("  Test empty generators list");
        generators.clear();
        Component broken = new Throttle(label, generators,
                priority, false);
        assertTrue(broken.description() != null);
        assertTrue(broken.description().endsWith("]"));
        assertTrue(broken.description().startsWith("["));
        assertTrue(broken.description()
                .contains("No defined characteristic"));
        System.out.println("  Test null generators list");
        generators.clear();
        broken = new Throttle(label, null, priority, false);
        assertTrue(broken.description() != null);
        assertTrue(broken.description().endsWith("]"));
        assertTrue(broken.description().startsWith("["));
        assertTrue(broken.description()
                .contains("No defined characteristic"));
    }

    /**
     * Test of getReferences method, of class Throttle.
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
        final Generator generator1 = new Constant(period, sourceLabel,
                reference);
        Generator generator2 = new Constant(period, Vocabulary.DEFAULT,
                reference);
        System.out.println("  Add 2 generators with same endpoint");
        List<Generator> generators = new ArrayList<>();
        generators.add(generator1);
        generators.add(generator2);
        final List<String> priority = new ArrayList<>();
        Component instance = new Throttle(label, generators,
                priority, false);
        Map<String, List<Function>> map = instance.getReferences();
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
        instance = new Throttle(label, generators, priority, false);
        map = instance.getReferences();
        assertEquals(2, map.size());
        assertEquals(1, map.get(reference).size());
        assertEquals(generator1, map.get(reference).get(0));
        assertEquals(1, map.get(reference2).size());
        assertEquals(generator1, map.get(reference).get(0));
        assertEquals(generator2, map.get(reference2).get(0));
    }

    /**
     * Test of prioritize method, of class Throttle.
     */
    @Test
    public void testPrioritize()
    {
        System.out.println("prioritize");
        final String reference = "delay 1";
        final String label = "source";
        final List<Generator> generators = new ArrayList<>();
        final List<String> priorities = new ArrayList<>();
        priorities.add(label);
        final Sequencer sequencer = new Sequencer();
        final Generator generator = new Constant(1, "fixed", reference);
        generators.add(generator);
        final Component instance = new Throttle("balancer", generators,
                priorities, false);
        final Component other = new Throttle("other-balancer", generators,
                priorities, false);
        System.out.println("  Try prioritize");
        instance.prioritize(sequencer, false);
        assertTrue(sequencer.exclusions.isEmpty());
        assertFalse(sequencer.paths.isEmpty());
        assertTrue(sequencer.paths.contains(instance));
        assertTrue(sequencer.participants.isEmpty());
        assertFalse(sequencer.priorities.isEmpty());
        assertTrue(sequencer.priorities.contains(label));
        assertTrue(sequencer.sources.length == 1);
        System.out.println("  Try explore with different end component");
        sequencer.participants.clear();
        sequencer.paths.clear();
        sequencer.exclusions.clear();
        sequencer.paths.add(other);
        instance.prioritize(sequencer, true);
        assertFalse(sequencer.paths.isEmpty());
        assertTrue(sequencer.paths.size() == 1);
        assertTrue(sequencer.paths.contains(other));
        assertFalse(sequencer.paths.contains(instance));
        assertFalse(sequencer.exclusions.isEmpty());
        assertTrue(sequencer.exclusions.contains(instance));
        System.out.println("  Try explore with path set");
        sequencer.participants.clear();
        sequencer.paths.clear();
        sequencer.exclusions.clear();
        sequencer.paths.add(instance);
        instance.prioritize(sequencer, true);
        assertTrue(sequencer.participants.isEmpty());
        System.out.println("  Try explore with exclusion set");
        sequencer.participants.clear();
        sequencer.paths.clear();
        sequencer.exclusions.clear();
        sequencer.paths.add(other);
        sequencer.exclusions.add(instance);
        instance.prioritize(sequencer, true);
        assertFalse(sequencer.paths.isEmpty());
        assertTrue(sequencer.paths.size() == 1);
        assertTrue(sequencer.paths.contains(other));
        assertFalse(sequencer.paths.contains(instance));
        assertFalse(sequencer.exclusions.isEmpty());
        assertTrue(sequencer.exclusions.size() == 1);
        assertTrue(sequencer.exclusions.contains(instance));
    }

    /**
     * Test of instance method, of class Throttle.
     */
    @Test
    public void testInstance()
    {
        System.out.println("instance");
        final double period = 5;
        final String source = Vocabulary.DEFAULT;
        final String reference = "database";
        final Generator generator = new Constant(period, source, reference);
        final List<Generator> generators = new ArrayList<>();
        generators.add(generator);
        final List<NameValue> pairs = new ArrayList<>();
        final String name  = "CPU";
        NameValue pair = new NameValue(Vocabulary.NAME, name);
        pairs.add(pair);
        pair = new NameValue(Vocabulary.MONITOR, "Y");
        pairs.add(pair);
        pair = new NameValue(name, name);
        pairs.add(pair);
        Component instance = Throttle.instance(pairs, generators);
        assertEquals(name, instance.getLabel());
        assertEquals(1, instance.getReferences().size());
        assertEquals(1, instance.getReferences().get(reference).size());
    }

    /**
     * Test of getAvailable method, of class Throttle.
     */
    @Test
    public void testGetAvailable()
    {
        System.out.println("getAvailable");
        final double sourcePeriod = 1;
        final double period = 2;
        assertTrue(period > sourcePeriod);
        final String sourceLabel = "source";
        final String label = "delay";
        final int eventTotal = 4;
        final Generator sourceGenerator = new Constant(sourcePeriod,
                sourceLabel, label);
        final Generator generator = new Constant(period, sourceLabel, null);
        final List<Generator> generators = new ArrayList<>();
        generators.add(generator);
        final List<String> priority = new ArrayList<>();
        final Component instance = new Throttle(label, generators,
                priority, false);
        final LinkedList<Event> events = new LinkedList<>();
        final Component source
                = new Source(sourceLabel, sourceGenerator, null, null, false);
        sourceGenerator.setNext(instance);
        for (int count = 0; count < eventTotal; count++)
        {
            final Event event = source.simulate(null);
            events.add(event);
        }
        // sort events by arrival time
        events.sort(Comparator.comparingDouble(Event::getCompleted));
        assertTrue(events.size() == eventTotal);
        int[] state = {2, 4, 4, 6, 6, 8};
        int index = 0;
        while (!events.isEmpty())
        {
            final Event event = events.removeFirst();
            event.simulate();
            if (event.getComponent() != null)
            {
                events.add(event);
                events.sort(Comparator.comparingDouble(Event::getCompleted));
            }
            assertTrue(instance.getAvailable() == state[index++]);
        }
    }
}
