/*
 * SourceTest.java
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
import org.amity.simulator.generators.Constant;
import java.util.List;
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
 * Tests generation of incoming events is properly populated.
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public class SourceTest
{

    public SourceTest()
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
     * Test of simulate method, of class Source.
     */
    @Test
    public void testSimulate()
    {
        System.out.println("simulate");
        final double period = 5;
        final String label = "test";
        final int eventTotal = 3;
        final IGenerator generator = new Constant(period, "source", "next");
        final IComponent instance = new Source(label, generator, false);
        for (int count = 0; count < eventTotal; count++)
        {
            final Event event = instance.simulate(null);
        }
        System.out.println("  check events are preserved");
        final List<Event> local = instance.getLocalEvents();
        assertTrue(local.size() == eventTotal);
        double tick = 0;
        System.out.println("  check event statistics are correct");
        for (final Event event : local)
        {
            tick += period;
            System.out.println("    " + event.getSource() + " "
                    + event.getLabel() + " -> arrival: " + event.getArrived()
                    + ", start: " + event.getStarted() + ", complete: "
                    + event.getCompleted());
            assertTrue(event.getArrived() == tick);
            assertTrue(event.getStarted() == tick);
            assertTrue(event.getCompleted() == tick);
        }
        System.out.println("  check passage of injected event");
        final String source = "source";
        tick = 1000;
        Event event = new Event(source, label, tick);
        event.setValues(tick, tick, tick);
        event = instance.simulate(event);
        assertEquals(source, event.getSource());
        assertEquals(label, event.getLabel());
        assertEquals(tick, event.getArrived(), 0.0);
        assertEquals(tick, event.getStarted(), 0.0);
        assertEquals(tick, event.getCompleted(), 0.0);
    }

    /**
     * Test of reset method, of class Processor.
     */
    @Test
    public void testReset()
    {
        System.out.println("reset");
        final double period = 5;
        final String label = "test";
        final int eventTotal = 3;
        final IGenerator generator = new Constant(period, "source", "next");
        final IComponent instance = new Source(label, generator, false);
        for (int count = 0; count < eventTotal; count++)
        {
            instance.simulate(null);
        }
        final List<Event> local = instance.getLocalEvents();
        assertTrue(local.size() == eventTotal);
        System.out.println("  check reset clears events");        
        instance.reset();
        assertTrue(local.isEmpty());
        System.out.println("  repopulate and check events are preserved");
        for (int count = 0; count < eventTotal; count++)
        {
            final Event event = instance.simulate(null);
            if (event == null)
            {
                break;
            }
        }
        assertTrue(local.size() == eventTotal);
        double tick = 0;
        System.out.println("  check event statistics are correct");
        for (Event event : local)
        {
            tick += period;
            System.out.println("    " + event.getSource() + " "
                    + event.getLabel() + " -> arrival: " + event.getArrived()
                    + ", start: " + event.getStarted() + ", complete: "
                    + event.getCompleted());
            assertTrue(event.getArrived() == tick);
            assertTrue(event.getStarted() == tick);
            assertTrue(event.getCompleted() == tick);
        }
    }

    /**
     * Test of getLocalEvents method, of class Source.
     */
    @Test
    public void testGetLocalEvents()
    {
        System.out.println("getLocalEvents");
        final double period = 5;
        final String label = "test";
        final int eventTotal = 3;
        final IGenerator generator = new Constant(period, "source", "next");
        final IComponent instance = new Source(label, generator, false);
        assertEquals(0, instance.getLocalEvents().size());
        for (int count = 0; count < eventTotal; count++)
        {
            instance.simulate(null);
        }
        final List<Event> local = instance.getLocalEvents();
        assertEquals(eventTotal, local.size());
    }

    /**
     * Test of getLabel method, of class Source.
     */
    @Test
    public void testGetLabel()
    {
        System.out.println("getLabel");
        final double period = 5;
        final String label = "test";
        final IGenerator generator = new Constant(period, "source", "next");
        final IComponent instance = new Source(label, generator, false);
        assertEquals(label, instance.getLabel());
    }

    /**
     * Test of getDepths method, of class Source.
     */
    @Test
    public void testGetDepths()
    {
        System.out.println("getDepths");
        final double period = 5;
        final String label = "test";
        final IGenerator generator = new Constant(period, "source", "next");
        final IComponent instance = new Source(label, generator, false);
        // This is invariant as a source never has any wait times
        assertTrue(instance.getDepths() != null);
        assertEquals(0, instance.getDepths().size());
    }

    /**
     * Test of description method, of class Source.
     */
    @Test
    public void testDescription()
    {
        System.out.println("description");
        final double period = 5;
        final String label = "test";
        final IGenerator generator = new Constant(period, "source", "next");
        final IComponent instance = new Source(label, generator, false);
        assertTrue(instance.description() != null);
        assertTrue(instance.description().endsWith("]"));
        assertTrue(instance.description().startsWith("["));
    }

    /**
     * Test of getReferences method, of class Source.
     */
    @Test
    public void testGetReferences()
    {
        System.out.println("getReferences");
        // this is invariant as source only ever has one generator
        final double period = 5;
        final String label = "test";
        final String reference = "next";
        System.out.println("  Check normal source definition");
        final IGenerator generator = new Constant(period, "source", reference);
        final IComponent instance = new Source(label, generator, false);
        assertTrue(instance.getReferences() != null);
        assertEquals(1, instance.getReferences().size());
        assertEquals(generator, instance.getReferences().get(reference).get(0));
        System.out.println("  Check incorrect source definition");
        final IComponent source = new Source(label, null, false);
        assertEquals(0, source.getReferences().size());
    }

    /**
     * Test of instance method, of class Source.
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
        final String name = "source";
        NameValue pair = new NameValue(Vocabulary.NAME, name);
        pairs.add(pair);
        pair = new NameValue(Vocabulary.MONITOR, "Y");
        pairs.add(pair);
        pair = new NameValue(source, source);
        pairs.add(pair);
        IComponent instance = Source.instance(pairs, generators);
        assertEquals(name, instance.getLabel());
        assertEquals(1, instance.getReferences().size());
        assertEquals(1, instance.getReferences().get(reference).size());
    }

    /**
     * Test of getAvailable method, of class Source.
     */
    @Test
    public void testGetAvailable()
    {
        System.out.println("getAvailable");
        final double period = 5;
        final String label = "test";
        final int eventTotal = 3;
        final IGenerator generator = new Constant(period, "source", "next");
        final IComponent instance = new Source(label, generator, false);
        for (int count = 0; count < eventTotal; count++)
        {
            final Event event = instance.simulate(null);
            assertEquals(event.getCompleted(), instance.getAvailable(), 0.0);
        }
    }
}
