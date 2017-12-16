/*
 * BalancerTest.java
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
 * Created on November 29, 2017
 */
package org.amity.simulator.elements;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import org.amity.simulator.distributors.RoundRobin;
import org.amity.simulator.generators.Constant;
import org.amity.simulator.language.NameValue;
import org.amity.simulator.language.Vocabulary;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.amity.simulator.distributors.Distributor;
import org.amity.simulator.distributors.Smart;
import org.amity.simulator.generators.Generator;

/**
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public class BalancerTest
{
    
    public BalancerTest()
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
     * Test of getLocalEvents method, of class Balancer.
     */
    @Test
    public void testGetLocalEvents()
    {
        System.out.println("getLocalEvents");
        System.out.println("  populate components");        
        final double sourcePeriod = 1;
        final double period = 1;
        final String sourceLabel = "source";
        final String label = "balancer";
        final String label1 = "delay 1";
        final String label2 = "delay 2";
        final List<String> references = new ArrayList<>();
        references.add(label1);
        references.add(label2);
        final int eventTotal = 4;
        final Generator sourceGenerator = new Constant(sourcePeriod,
                sourceLabel, label);
        final Distributor distributor = new RoundRobin(references);
        final Component instance = new Balancer(label, distributor, false);
        final LinkedList<Event> events = new LinkedList<>();
        final Component source
                = new Source(sourceLabel, sourceGenerator, false);
        sourceGenerator.setNext(instance);
        final Generator generator = new Constant(period, sourceLabel, null);
        final List<Generator> generators = new ArrayList<>();
        generators.add(generator);
        final List<String> priority = new ArrayList<>();
        final Component processor1 = new Processor(label1, generators,
                priority, false);
        final Component processor2 = new Processor(label2, generators,
                priority, false);
        distributor.addNext(processor1);
        distributor.addNext(processor2);
        for (int count = 0; count < eventTotal; count++)
        {
            final Event event = source.simulate(null);
            events.add(event);
        }
        // sort events by arrival time
        events.sort(Comparator.comparingDouble(Event::getCompleted));
        assertTrue(events.size() == eventTotal);
        while (events.size() > 0)
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
    }

    /**
     * Test of reset method, of class Balancer.
     */
    @Test
    public void testReset()
    {
        System.out.println("reset");
        System.out.println("  populate components");        
        final double sourcePeriod = 1;
        final double period = 1;
        final String sourceLabel = "source";
        final String label = "balancer";
        final String label1 = "delay 1";
        final String label2 = "delay 2";
        final List<String> references = new ArrayList<>();
        references.add(label1);
        references.add(label2);
        final int eventTotal = 4;
        final Generator sourceGenerator = new Constant(sourcePeriod,
                sourceLabel, label);
        final Distributor distributor = new RoundRobin(references);
        final Component instance = new Balancer(label, distributor, false);
        final LinkedList<Event> events = new LinkedList<>();
        final Component source
                = new Source(sourceLabel, sourceGenerator, false);
        sourceGenerator.setNext(instance);
        final Generator generator = new Constant(period, sourceLabel, null);
        final List<Generator> generators = new ArrayList<>();
        generators.add(generator);
        final List<String> priority = new ArrayList<>();
        final Component processor1 = new Processor(label1, generators,
                priority, false);
        final Component processor2 = new Processor(label2, generators,
                priority, false);
        distributor.addNext(processor1);
        distributor.addNext(processor2);
        for (int count = 0; count < eventTotal; count++)
        {
            final Event event = source.simulate(null);
            events.add(event);
        }
        // sort events by arrival time
        events.sort(Comparator.comparingDouble(Event::getCompleted));
        assertTrue(events.size() == eventTotal);
        while (events.size() > 0)
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
        while (events.size() > 0)
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
        assertEquals(eventTotal / 2, processor1.getLocalEvents().size());
        assertEquals(eventTotal / 2, processor2.getLocalEvents().size());
        double sourceTick = 0;
        System.out.println("  check event statistics are correct");
        for (final Event event : local)
        {
            System.out.println("    " + event.getSource() + " "
                    + event.getLabel() + " -> arrival: " + event.getArrived()
                    + ", start: " + event.getStarted() + ", complete: "
                    + event.getCompleted());
            sourceTick += sourcePeriod;
            assertTrue(event.getArrived() == sourceTick);
            assertTrue(event.getStarted() == sourceTick);
            assertTrue(event.getCompleted() == sourceTick);
            final double elapsed = 0;
            assertTrue(event.getCompleted() - event.getCreated() == elapsed);
            final double executed = 0;
            assertTrue(event.getExecuted() == executed);
        }
        assertEquals(eventTotal / 2, processor1.getLocalEvents().size());
        assertEquals(eventTotal / 2, processor2.getLocalEvents().size());
        System.out.println("  check when no distributor");
        Balancer broken = new Balancer(label, null, false);
        broken.reset();
        assertTrue(true); // nothing broke when we reset
        System.out.println("  check when broken distributor");
        references.clear();
        final Distributor round = new RoundRobin(references);
        broken = new Balancer(label, round, false);
        broken.reset();
        assertTrue(true); // nothing broke when we reset
    }

    /**
     * Test of simulate method, of class Balancer.
     */
    @Test
    public void testSimulate()
    {
        System.out.println("simulate");
        System.out.println("  populate components");
        final double sourcePeriod = 1;
        final double period = 1;
        final String sourceLabel = "source";
        final String label = "balancer";
        final String label1 = "delay 1";
        final String label2 = "delay 2";
        final List<String> references = new ArrayList<>();
        references.add(label1);
        references.add(label2);
        final int eventTotal = 4;
        final Generator sourceGenerator = new Constant(sourcePeriod,
                sourceLabel, label);
        final Distributor distributor = new RoundRobin(references);
        final Component instance = new Balancer(label, distributor, false);
        final LinkedList<Event> events = new LinkedList<>();
        final Component source
                = new Source(sourceLabel, sourceGenerator, false);
        sourceGenerator.setNext(instance);
        final Generator generator = new Constant(period, sourceLabel, null);
        final List<Generator> generators = new ArrayList<>();
        generators.add(generator);
        final List<String> priority = new ArrayList<>();
        final Component processor1 = new Processor(label1, generators,
                priority, false);
        final Component processor2 = new Processor(label2, generators,
                priority, false);
        distributor.addNext(processor1);
        distributor.addNext(processor2);
        for (int count = 0; count < eventTotal; count++)
        {
            final Event event = source.simulate(null);
            events.add(event);
        }
        // sort events by arrival time
        events.sort(Comparator.comparingDouble(Event::getCompleted));
        assertTrue(events.size() == eventTotal);
        while (events.size() > 0)
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
        double sourceTick = 0;
        System.out.println("  check event statistics are correct");
        for (final Event event : local)
        {
            System.out.println("    " + event.getSource() + " "
                    + event.getLabel() + " -> arrival: " + event.getArrived()
                    + ", start: " + event.getStarted() + ", complete: "
                    + event.getCompleted());
            sourceTick += sourcePeriod;
            assertTrue(event.getArrived() == sourceTick);
            assertTrue(event.getStarted() == sourceTick);
            assertTrue(event.getCompleted() == sourceTick);
            final double elapsed = 0;
            assertTrue(event.getCompleted() - event.getCreated() == elapsed);
            final double executed = 0;
            assertTrue(event.getExecuted() == executed);
        }
        assertEquals(eventTotal / 2, processor1.getLocalEvents().size());
        assertEquals(eventTotal / 2, processor2.getLocalEvents().size());
        System.out.println("  check null event");
        final Event test = instance.simulate(null);
        assertTrue(test == null);
    }

    /**
     * Test of getLabel method, of class Balancer.
     */
    @Test
    public void testGetLabel()
    {
        System.out.println("getLabel");
        final String label = "balancer";
        final String label1 = "processor 1";
        final String label2 = "processor 2";
        final List<String> references = new ArrayList<>();
        references.add(label1);
        references.add(label2);
        final Distributor distributor = new RoundRobin(references);
        final Component instance = new Balancer(label, distributor, false);
        assertEquals(label, instance.getLabel());
    }

    /**
     * Test of getReferences method, of class Balancer.
     */
    @Test
    public void testGetReferences()
    {
        System.out.println("getReferences");
        final String label = "balancer";
        final String label1 = "processor 1";
        final String label2 = "processor 2";
        final List<String> references = new ArrayList<>();
        references.add(label1);
        references.add(label2);
        System.out.println("  check normal operation");
        final Distributor distributor = new RoundRobin(references);
        final Component instance = new Balancer(label, distributor, false);
        assertEquals(references.size(), instance.getReferences().size());
        assertTrue(instance.getReferences().containsKey(label1));
        assertTrue(instance.getReferences().containsKey(label2));
        assertTrue(instance.getReferences().get(label1).size() == 1);
        assertTrue(instance.getReferences().get(label2).size() == 1);
        assertEquals(distributor, instance.getReferences().get(label1).get(0));
        assertEquals(distributor, instance.getReferences().get(label2).get(0));
        System.out.println("  check when no distributor");
        final Balancer broken = new Balancer(label, null, false);
        assertTrue(broken.getReferences().isEmpty());
    }

    /**
     * Test of getDepths method, of class Balancer.
     */
    @Test
    public void testGetDepths()
    {
        System.out.println("getDepths");
        final double sourcePeriod = 1;
        final double period = 1;
        final String sourceLabel = "source";
        final String label = "balancer";
        final String label1 = "delay 1";
        final String label2 = "delay 2";
        final List<String> references = new ArrayList<>();
        references.add(label1);
        references.add(label2);
        final int eventTotal = 4;
        final Generator sourceGenerator = new Constant(sourcePeriod,
                sourceLabel, label);
        final Distributor distributor = new RoundRobin(references);
        final Component instance = new Balancer(label, distributor, false);
        final LinkedList<Event> events = new LinkedList<>();
        final Component source
                = new Source(sourceLabel, sourceGenerator, false);
        sourceGenerator.setNext(instance);
        final Generator generator = new Constant(period, sourceLabel, null);
        final List<Generator> generators = new ArrayList<>();
        generators.add(generator);
        final List<String> priority = new ArrayList<>();
        final Component processor1 = new Processor(label1, generators,
                priority, false);
        final Component processor2 = new Processor(label2, generators,
                priority, false);
        distributor.addNext(processor1);
        distributor.addNext(processor2);
        for (int count = 0; count < eventTotal; count++)
        {
            final Event event = source.simulate(null);
            events.add(event);
        }
        // sort events by arrival time
        events.sort(Comparator.comparingDouble(Event::getCompleted));
        assertTrue(events.size() == eventTotal);
        while (events.size() > 0)
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
        double sourceTick = 0;
        System.out.println("  check event statistics are correct");
        for (final Event event : local)
        {
            System.out.println("    " + event.getSource() + " "
                    + event.getLabel() + " -> arrival: " + event.getArrived()
                    + ", start: " + event.getStarted() + ", complete: "
                    + event.getCompleted());
            sourceTick += sourcePeriod;
            assertTrue(event.getArrived() == sourceTick);
            assertTrue(event.getStarted() == sourceTick);
            assertTrue(event.getCompleted() == sourceTick);
            final double elapsed = 0;
            assertTrue(event.getCompleted() - event.getCreated() == elapsed);
            final double executed = 0;
            assertTrue(event.getExecuted() == executed);
        }
        assertEquals(eventTotal / 2, processor1.getLocalEvents().size());
        assertEquals(eventTotal / 2, processor2.getLocalEvents().size());
        // No way to adequately test this except under load
        assertTrue(instance.getDepths() != null);
        assertEquals(eventTotal, instance.getDepths().size());
    }

    /**
     * Test of description method, of class Balancer.
     */
    @Test
    public void testDescription()
    {
        System.out.println("description");
        final String label = "balancer";
        final String label1 = "processor 1";
        final String label2 = "processor 2";
        final List<String> references = new ArrayList<>();
        references.add(label1);
        references.add(label2);
        final Distributor distributor = new RoundRobin(references);
        final Component instance = new Balancer(label, distributor, false);
        assertTrue(instance.description() != null);
        assertTrue(instance.description().endsWith("]"));
        assertTrue(instance.description().startsWith("["));
    }

    /**
     * Test of getAvailable method, of class Balancer.
     */
    @Test
    public void testGetAvailable()
    {
        System.out.println("getAvailable");
        final double sourcePeriod = 1;
        final double period = 1;
        final String sourceLabel = "source";
        final String label = "balancer";
        final String label1 = "delay 1";
        final String label2 = "delay 2";
        final List<String> references = new ArrayList<>();
        references.add(label2);
        final int eventTotal = 4;
        final Generator sourceGenerator = new Constant(sourcePeriod,
                sourceLabel, label);
        final Distributor distributor = new RoundRobin(references);
        final Component instance = new Balancer(label, distributor, false);
        final LinkedList<Event> events = new LinkedList<>();
        final Component source
                = new Source(sourceLabel, sourceGenerator, false);
        final Generator generator1 = new Constant(period, sourceLabel, null);
        final List<Generator> generators1 = new ArrayList<>();
        final Generator generator2 = new Constant(period, sourceLabel, null);
        final List<Generator> generators2 = new ArrayList<>();
        generators1.add(generator1);
        generators2.add(generator2);
        final List<String> priority = new ArrayList<>();
        final Component processor1 = new Processor(label1, generators1,
                priority, false);
        final Component processor2 = new Processor(label2, generators2,
                priority, false);
        distributor.addNext(processor2);
        generator1.setNext(instance);
        sourceGenerator.setNext(processor1);
        for (int count = 0; count < eventTotal; count++)
        {
            final Event event = source.simulate(null);
            events.add(event);
        }
        // sort events by arrival time
        events.sort(Comparator.comparingDouble(Event::getCompleted));
        assertTrue(events.size() == eventTotal);
        final int[] balancer = {0, 0, 0, 3, 3, 3, 4, 4, 4, 5, 5, 6};
        final int[] state1 = {2, 3, 3, 3, 4, 4, 4, 5, 5, 5, 5, 5};
        final int[] state2 = {0, 0, 0, 3, 3, 3, 4, 4, 4, 5, 5, 6};
        int index = 0;
        while (events.size() > 0)
        {
            final Event event = events.removeFirst();
            event.simulate();
            if (event.getComponent() != null)
            {
                events.add(event);
                events.sort(Comparator.comparingDouble(Event::getCompleted));
            }
            assertTrue(instance.getAvailable() == balancer[index]);
            assertTrue(processor1.getAvailable() == state1[index]);
            assertTrue(processor2.getAvailable() == state2[index]);
            index++;
        }
        assertTrue(processor1.getLocalEvents().size() == eventTotal);
        assertTrue(instance.getLocalEvents().size() == eventTotal);
        assertTrue(processor2.getLocalEvents().size() == eventTotal);
    }

    @Test
    public void testPrioritize()
    {
        System.out.println("prioritize");
        final String label1 = "delay 1";
        final Component component = new DummyComponent(label1, 2.0);
        final List<String> references = new ArrayList<>();
        references.add(label1);
        final Sequencer sequencer = new Sequencer();
        final Distributor distributor = new Smart(references);
        distributor.addNext(component);
        final Component instance = new Balancer("balancer", distributor, false);
        System.out.println("  Try cleared system for no match in component");
        final double available = instance.getAvailable();
        instance.prioritize(sequencer, true);
        assertTrue(sequencer.exclusions.isEmpty());
        assertTrue(sequencer.paths.isEmpty());
        assertTrue(sequencer.participants.contains(instance));
        System.out.println("  Try system with match in paths");
        sequencer.paths.add(instance);
        sequencer.participants.clear();
        instance.prioritize(sequencer, true);
        assertFalse(sequencer.paths.isEmpty());
        assertTrue(sequencer.exclusions.isEmpty());
        assertFalse(sequencer.participants.contains(instance));
        System.out.println("  Try system with match in exclusions");
        sequencer.exclusions.add(instance);
        sequencer.participants.clear();
        sequencer.paths.clear();
        instance.prioritize(sequencer, true);
        assertTrue(sequencer.paths.isEmpty());
        assertFalse(sequencer.exclusions.isEmpty());
        assertFalse(sequencer.participants.contains(instance));
    }

    /**
     * Test of instance method, of class Balancer.
     */
    @Test
    public void testInstance()
    {
        System.out.println("instance");
        final String label1 = "processor 1";
        final String label2 = "processor 2";
        final List<String> references = new ArrayList<>();
        references.add(label1);
        references.add(label2);
        final Distributor distributor = new RoundRobin(references);
        final List<Distributor> distributors = new ArrayList<>();
        distributors.add(distributor);
        final List<NameValue> pairs = new ArrayList<>();
        final String name  = "Balancer";
        NameValue pair = new NameValue(Vocabulary.NAME, name);
        pairs.add(pair);
        pair = new NameValue(Vocabulary.MONITOR, "Y");
        pairs.add(pair);
        pair = new NameValue(name, name);
        pairs.add(pair);
        Component instance = Balancer.instance(pairs, distributors);
        assertEquals(name, instance.getLabel());
        assertEquals(references.size(), instance.getReferences().size());
        assertTrue(instance.description() != null);
        assertTrue(instance.description().endsWith("]"));
        assertTrue(instance.description().startsWith("["));
    }
    
}
