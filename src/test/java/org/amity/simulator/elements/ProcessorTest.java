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
        final Generator sourceGenerator = new Constant(sourcePeriod,
                sourceLabel, label);
        final Generator generator = new Constant(period, sourceLabel, null);
        final List<Generator> generators = new ArrayList<>();
        generators.add(generator);
        final List<String> priority = new ArrayList<>();
        final Component instance = new Processor(label, generators,
                priority, false);
        final LinkedList<Event> events = new LinkedList<>();
        final Component source
                = new Source(sourceLabel, sourceGenerator, false);
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
        System.out.println("  check null event");
        final Event test = instance.simulate(null);
        assertTrue(test == null);
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
        final Generator sourceGenerator = new Constant(sourcePeriod,
                sourceLabel, label);
        final Generator generator = new Constant(period, sourceLabel, null);
        final List<Generator> generators = new ArrayList<>();
        generators.add(generator);
        final List<String> priority = new ArrayList<>();
        final Component instance = new Processor(label, generators,
                priority, false);
        final LinkedList<Event> events = new LinkedList<>();
        final Component source
                = new Source(sourceLabel, sourceGenerator, false);
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
        final Generator sourceGenerator = new Constant(sourcePeriod,
                sourceLabel, label);
        final Generator generator = new Constant(period, sourceLabel, null);
        final List<Generator> generators = new ArrayList<>();
        generators.add(generator);
        final List<String> priority = new ArrayList<>();
        final Component instance = new Processor(label, generators,
                priority, false);
        final LinkedList<Event> events = new LinkedList<>();
        final Component source
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
        final Generator generator = new Constant(period, sourceLabel, null);
        final List<Generator> generators = new ArrayList<>();
        generators.add(generator);
        final List<String> priority = new ArrayList<>();
        final Component instance = new Processor(label, generators,
                priority, false);
        assertEquals(label, instance.getLabel());
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
        final String label1 = "processor";
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
        final Component instance = new Processor(label, generators,
                priority, false);
        final Component processor = new Processor(label1, generators1,
                priority, false);
        final LinkedList<Event> events = new LinkedList<>();
        final Component source
                = new Source(sourceLabel, sourceGenerator, false);
        generator.setNext(processor);
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
        assertTrue(processor.getLocalEvents().size() == eventTotal);
        System.out.println("  Check depth");
        // No way to adequately test this except under load
        assertTrue(instance.getDepths() != null);
        assertEquals(eventTotal, instance.getDepths().size());
    }

    /**
     * Test of description method, of class Processor.
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
        final Component instance = new Processor(label, generators,
                priority, false);
        sourceGenerator.setNext(instance);
        assertTrue(instance.description() != null);
        assertTrue(instance.description().endsWith("]"));
        assertTrue(instance.description().startsWith("["));
        System.out.println("  Test empty generators list");
        generators.clear();
        Component broken = new Processor(label, generators,
                priority, false);
        assertTrue(broken.description() != null);
        assertTrue(broken.description().endsWith("]"));
        assertTrue(broken.description().startsWith("["));
        assertTrue(broken.description()
                .contains("No defined characteristic"));
        System.out.println("  Test null generators list");
        generators.clear();
        broken = new Processor(label, null, priority, false);
        assertTrue(broken.description() != null);
        assertTrue(broken.description().endsWith("]"));
        assertTrue(broken.description().startsWith("["));
        assertTrue(broken.description()
                .contains("No defined characteristic"));
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
        final Generator generator1 = new Constant(period, sourceLabel,
                reference);
        Generator generator2 = new Constant(period, Vocabulary.DEFAULT,
                reference);
        System.out.println("  Add 2 generators with same endpoint");
        List<Generator> generators = new ArrayList<>();
        generators.add(generator1);
        generators.add(generator2);
        final List<String> priority = new ArrayList<>();
        Component instance = new Processor(label, generators,
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
        instance = new Processor(label, generators, priority, false);
        map = instance.getReferences();
        assertEquals(2, map.size());
        assertEquals(1, map.get(reference).size());
        assertEquals(generator1, map.get(reference).get(0));
        assertEquals(1, map.get(reference2).size());
        assertEquals(generator1, map.get(reference).get(0));
        assertEquals(generator2, map.get(reference2).get(0));
    }
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
        final Component instance = new Processor("balancer", generators,
                priorities, false);
        final Component other = new Processor("other-balancer", generators,
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
     * Test of instance method, of class Processor.
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
        Component instance = Processor.instance(pairs, generators);
        assertEquals(name, instance.getLabel());
        assertEquals(1, instance.getReferences().size());
        assertEquals(1, instance.getReferences().get(reference).size());
    }

    /**
     * Test of getAvailable method, of class Processor.
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
        final Component instance = new Processor(label, generators,
                priority, false);
        final LinkedList<Event> events = new LinkedList<>();
        final Component source
                = new Source(sourceLabel, sourceGenerator, false);
        sourceGenerator.setNext(instance);
        for (int count = 0; count < eventTotal; count++)
        {
            final Event event = source.simulate(null);
            events.add(event);
        }
        // sort events by arrival time
        events.sort(Comparator.comparingDouble(Event::getCompleted));
        assertTrue(events.size() == eventTotal);
        int[] state = {3, 5, 7, 9};
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
