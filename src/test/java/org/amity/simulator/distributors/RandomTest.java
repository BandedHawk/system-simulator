/*
 * RandomTest.java
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
 * Created on December 6, 2017
 */

package org.amity.simulator.distributors;

import java.util.ArrayList;
import java.util.List;
import org.amity.simulator.elements.DummyComponent;
import org.amity.simulator.elements.Event;
import org.amity.simulator.language.NameValue;
import org.amity.simulator.language.Vocabulary;
import org.apache.commons.math3.util.FastMath;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.amity.simulator.elements.Component;

/**
 * Tests that implementation meets uniform random operation.
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public class RandomTest
{
    
    public RandomTest()
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
     * Test of assign method, of class Random.
     */
    @Test
    public void testAssign()
    {
        System.out.println("assign");
        final String sourceLabel = "source";
        final String label1 = "component 1";
        final String label2 = "component 2";
        final String label3 = "component 3";
        final String label4 = "component 4";
        final String label5 = "component 5";
        final List<String> references = new ArrayList<>();
        references.add(label1);
        references.add(label2);
        references.add(label3);
        references.add(label4);
        references.add(label5);
        final Component component1 = new DummyComponent(label1, 2.0);
        final Component component2 = new DummyComponent(label2, 1.0);
        final Component component3 = new DummyComponent(label3, 0.5);
        final Component component4 = new DummyComponent(label4, 2.5);
        final Component component5 = new DummyComponent(label5, 1.5);
        Distributor distributor = new Random(references);
        distributor.addNext(component3);
        distributor.addNext(component2);
        distributor.addNext(component1);
        distributor.addNext(component4);
        distributor.addNext(component5);
        final double[] count = new double[references.size()];
        System.out.println("  check for standard operation");
        Event event = new Event(sourceLabel, sourceLabel, 1.0);
        final int total = 2000000;
        for (int index = 0; index < total; index++)
        {
            event = distributor.assign(event);
            if (event.getComponent() == component1)
            {
                count[0] += 1;
            }
            else if (event.getComponent() == component2)
            {
                count[1] += 1;
            }
            else if (event.getComponent() == component3)
            {
                count[2] += 1;
            }
            else if (event.getComponent() == component4)
            {
                count[3] += 1;
            }
            else
            {
                count[4] += 1;
            }
        }
        for (int index = 0; index < 5; index++)
        {
            System.out.println("  Change from even distribution: "
                    + FastMath.abs(count[index]/total - 0.2));
            assertTrue(FastMath.abs(count[index]/total - 0.2) < 0.002);
        }
        System.out.println("  check for null event handling");
        Event outcome = distributor.assign(null);
        assertTrue(outcome == null);
        System.out.println("  check when no references");
        references.clear();
        final Distributor broken = new Random(references);
        for (int index  = 0; index < 40; index++)
        {
            final Component next = event.getComponent();
            outcome = broken.assign(event);
            assertEquals(next, outcome.getComponent());
        }
    }

    /**
     * Test of reset method, of class Random.
     */
    @Test
    public void testReset()
    {
        System.out.println("reset");
        final String sourceLabel = "source";
        final String label1 = "component 1";
        final String label2 = "component 2";
        final String label3 = "component 3";
        final List<String> references = new ArrayList<>();
        references.add(label1);
        references.add(label2);
        references.add(label3);
        final Distributor distributor = new Random(references);
        final Component component1 = new DummyComponent(label1, 2.0);
        final Component component2 = new DummyComponent(label2, 1.0);
        final Component component3 = new DummyComponent(label3, 1.5);
        distributor.addNext(component3);
        distributor.addNext(component2);
        distributor.addNext(component1);
        Event event = new Event(sourceLabel, sourceLabel, 1.0);
        boolean mismatch = false;
        for (int count = 0; count < 20; count++)
        {
            final double available = distributor.available();
            distributor.reset();
            event = distributor.assign(event);
            mismatch = event.getComponent().getAvailable() != available
                    || mismatch;
        }
        assertTrue(mismatch);
    }

    /**
     * Test of getReferences method, of class Random.
     */
    @Test
    public void testGetReferences()
    {
        System.out.println("getReferences");
        final String reference1 = "server 1";
        final String reference2 = "server 2";
        final List<String> references = new ArrayList<>();
        references.add(reference1);
        references.add(reference2);
        final Distributor instance = new Random(references);
        assertEquals(references.size(), instance.getReferences().size());
        assertTrue(instance.getReferences().contains(reference1));
        assertTrue(instance.getReferences().contains(reference2));
    }

    /**
     * Test of addNext method, of class Random.
     */
    @Test
    public void testAddNext()
    {
        System.out.println("addNext");
        final String label1 = "component 1";
        final String label2 = "component 2";
        final String label3 = "component 3";
        final List<String> references = new ArrayList<>();
        references.add(label1);
        references.add(label2);
        references.add(label3);
        final Component component1 = new DummyComponent(label1, 2.0);
        final Component component2 = new DummyComponent(label2, 1.0);
        final Component component3 = new DummyComponent(label3, 0.5);
        System.out.println("  check for normal operation");
        final Distributor distributor = new Random(references);
        distributor.addNext(component3);
        distributor.addNext(component2);
        distributor.addNext(component1);
        assertEquals(component1, distributor.connections()[0]);
        assertEquals(component2, distributor.connections()[1]);
        assertEquals(component3, distributor.connections()[2]);
        System.out.println("  check when component is null");
        distributor.addNext(null);
        assertEquals(component1, distributor.connections()[0]);
        assertEquals(component2, distributor.connections()[1]);
        assertEquals(component3, distributor.connections()[2]);
        System.out.println("  check when no references");
        references.clear();
        final Distributor broken = new Random(references);
        broken.addNext(component1);
        assertEquals(0, broken.connections().length);
    }

    /**
     * Test of connections method, of class Random.
     */
    @Test
    public void testConnections()
    {
        System.out.println("connections");
        final String label1 = "component 1";
        final String label2 = "component 2";
        final List<String> references = new ArrayList<>();
        references.add(label1);
        references.add(label2);
        final Distributor distributor = new Random(references);
        final Component component1 = new DummyComponent(label1, 1.0);
        final Component component2 = new DummyComponent(label2, 2.0);
        distributor.addNext(component2);
        distributor.addNext(component1);
        System.out.println("  check normal operation");
        final Component[] connections = distributor.connections();
        assertEquals(component1.getLabel(), connections[0].getLabel());
        assertEquals(component2.getLabel(), connections[1].getLabel());
        assertEquals(component1, connections[0]);
        assertEquals(component2, connections[1]);
        System.out.println("  check when no downstream components");
        final Distributor random = new Random(null);
        assertEquals(0, random.connections().length);
    }

    /**
     * Test of available method, of class Random.
     */
    @Test
    public void testAvailable()
    {
        System.out.println("available");
        final String sourceLabel = "source";
        final String label1 = "component 1";
        final String label2 = "component 2";
        final String label3 = "component 3";
        final List<String> references = new ArrayList<>();
        references.add(label1);
        references.add(label2);
        references.add(label3);
        final Distributor distributor = new Random(references);
        final Component component1 = new DummyComponent(label1, 2.0);
        final Component component2 = new DummyComponent(label2, 1.0);
        final Component component3 = new DummyComponent(label3, 1.5);
        distributor.addNext(component3);
        distributor.addNext(component2);
        distributor.addNext(component1);
        Event event = new Event(sourceLabel, sourceLabel, 1.0);
        System.out.println("  check available changes with assignment");
        int changed = 0;
        double last = 0;
        for (int count = 0; count < 40; count++)
        {
            final double available = distributor.available();
            event = distributor.assign(event);
            assertEquals(available, event.getComponent().getAvailable(), 0.0);
            if (last > 0)
            {
                changed += last == available ? 0 : 1; 
            }
            last = available;
        }
        assertTrue(changed > 0);
        System.out.println("  check available remains the same when not assigned");
        changed = 0;
        last = 0;
        for (int count = 0; count < 20; count++)
        {
            final double available = distributor.available();
            assertEquals(available, distributor.available(), 0.0);
            if (last > 0)
            {
                changed += last == available ? 0 : 1; 
            }
            last = available;
        }
        assertTrue(changed == 0);
    }

    /**
     * Test of characteristics method, of class Random.
     */
    @Test
    public void testCharacteristics()
    {
        System.out.println("characteristics");
        final List<String> references = new ArrayList<>(); 
        final String reference = "database";
        references.add(reference);
        final Distributor instance = new Random(references);
        assertTrue(instance.characteristics() != null);
        assertTrue(instance.characteristics().contains("Random"));
    }

    /**
     * Test of instance method, of class Random.
     */
    @Test
    public void testInstance()
    {
        System.out.println("instance");
        final List<NameValue> pairs = new ArrayList<>();
        final String reference1 = "server 1";
        final String reference2 = "server 2";
        final String reference3 = "garbage";
        final NameValue pair1 = new NameValue(Vocabulary.NEXT, reference1);
        final NameValue pair2 = new NameValue(Vocabulary.NEXT, reference2);
        final NameValue pair3 = new NameValue(Vocabulary.NAME, reference3);
        pairs.add(pair1);
        pairs.add(pair2);
        final NameValue pair = new NameValue(Vocabulary.FUNCTION,
                Vocabulary.FUNCTION);
        pairs.add(pair);
        Distributor instance = Random.instance(pairs);
        assertEquals(pairs.size() - 1, instance.getReferences().size());
        assertTrue(instance.getReferences().contains(reference1));
        assertTrue(instance.getReferences().contains(reference2));
        pairs.add(pair3);
        instance = Random.instance(pairs);
        assertFalse(pairs.size() == instance.getReferences().size());
        assertFalse(instance.getReferences().contains(reference3));
    }
}
