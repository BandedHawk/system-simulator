/*
 * Smartest.java
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
package org.amity.simulator.distributors;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.amity.simulator.elements.DummyComponent;
import org.amity.simulator.elements.Event;
import org.amity.simulator.elements.IComponent;
import org.amity.simulator.elements.Source;
import org.amity.simulator.generators.Constant;
import org.amity.simulator.generators.IGenerator;
import org.amity.simulator.language.NameValue;
import org.amity.simulator.language.Vocabulary;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Makes sure that event gets routed to first available component.
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public class SmartTest
{
    
    public SmartTest()
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
     * Test of assign method, of class Smart.
     */
    @Test
    public void testAssign()
    {
        System.out.println("assign");
        final String sourceLabel = "source";
        final String label1 = "component 1";
        final String label2 = "component 2";
        final String label3 = "component 3";
        final List<String> references = new ArrayList<>();
        references.add(label1);
        references.add(label2);
        references.add(label3);
        final IComponent component1 = new DummyComponent(label1, 2.0);
        final IComponent component2 = new DummyComponent(label2, 1.0);
        final IComponent component3 = new DummyComponent(label3, 0.5);
        IDistributor distributor = new Smart(references);
        distributor.addNext(component3);
        distributor.addNext(component2);
        distributor.addNext(component1);
        Event event = new Event(sourceLabel, sourceLabel, 1.0);
        event = distributor.assign(event);
        assertEquals(event.getComponent(), component3);
        final IComponent component4 = new DummyComponent(label3, 1.5);
        references.clear();
        references.add(label1);
        references.add(label2);
        references.add(label3);
        distributor = new Smart(references);
        distributor.addNext(component4);
        distributor.addNext(component2);
        distributor.addNext(component1);
        event = distributor.assign(event);
        assertEquals(event.getComponent(), component2);
    }

    /**
     * Test of reset method, of class Smart.
     */
    @Test
    public void testReset()
    {
        System.out.println("reset");
        final String sourceLabel = "source";
        final String label1 = "component 1";
        final String label2 = "component 2";
        final List<String> references = new ArrayList<>();
        references.add(label1);
        references.add(label2);
        final IDistributor distributor = new Smart(references);
        final IComponent component1 = new DummyComponent(label1, 2.0);
        final IComponent component2 = new DummyComponent(label2, 1.0);
        distributor.addNext(component2);
        distributor.addNext(component1);
        Event event = new Event(sourceLabel, sourceLabel, 1.0);
        event = distributor.assign(event);
        assertEquals(event.getComponent(), component2);
        distributor.reset();
        event = distributor.assign(event);
        assertEquals(event.getComponent(), component2);
    }

    /**
     * Test of getReferences method, of class Smart.
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
        final Smart instance = new Smart(references);
        assertEquals(references.size(), instance.getReferences().size());
        assertTrue(instance.getReferences().contains(reference1));
        assertTrue(instance.getReferences().contains(reference2));
    }

    /**
     * Test of addNext method, of class Smart.
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
        final IComponent component1 = new DummyComponent(label1, 2.0);
        final IComponent component2 = new DummyComponent(label2, 1.0);
        final IComponent component3 = new DummyComponent(label3, 0.5);
        IDistributor distributor = new Smart(references);
        distributor.addNext(component3);
        distributor.addNext(component2);
        distributor.addNext(component1);
        assertEquals(component1, distributor.connections()[0]);
        assertEquals(component2, distributor.connections()[1]);
        assertEquals(component3, distributor.connections()[2]);
    }

    /**
     * Test of characteristics method, of class Smart.
     */
    @Test
    public void testCharacteristics()
    {
        System.out.println("characteristics");
        final List<String> references = new ArrayList<>(); 
        final String reference = "database";
        references.add(reference);
        final IDistributor instance = new Smart(references);
        assertTrue(instance.characteristics() != null);
        assertTrue(instance.characteristics().contains("Smart"));
    }

    /**
     * Test of instance method, of class Smart.
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
        IDistributor instance = Smart.instance(pairs);
        assertEquals(pairs.size(), instance.getReferences().size());
        assertTrue(instance.getReferences().contains(reference1));
        assertTrue(instance.getReferences().contains(reference2));
        pairs.add(pair3);
        instance = Smart.instance(pairs);
        assertFalse(pairs.size() == instance.getReferences().size());
        assertFalse(instance.getReferences().contains(reference3));
    }

    /**
     * Test of connections method, of class Smart.
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
        final IDistributor distributor = new Smart(references);
        final IComponent component1 = new DummyComponent(label1, 1.0);
        final IComponent component2 = new DummyComponent(label2, 2.0);
        distributor.addNext(component2);
        distributor.addNext(component1);
        final IComponent[] connections = distributor.connections();
        assertEquals(component1.getLabel(), connections[0].getLabel());
        assertEquals(component2.getLabel(), connections[1].getLabel());
        assertEquals(component1, connections[0]);
        assertEquals(component2, connections[1]);
    }
    
}
