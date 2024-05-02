/*
 * Copyright 2017 <a href="mailto:jonb@ieee.org">Jon Barnett</a>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.amity.simulator.elements;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.amity.simulator.distributors.Distributor;
import org.amity.simulator.distributors.Smart;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public class SequencerTest
{
    
    public SequencerTest()
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

    @Test
    public void testPrioritize()
    {
        final List<Event> buffer = new ArrayList<>();
        final Sequencer sequencer = new Sequencer();
        final Component component = new DummyComponent("processor", 1.7);
        final Event current = new Event("test", "123", 1.0);
        final Event priority1 = new Event("source 1", "234", 1.5);
        final Event priority2 = new Event("source 2", "345", 1.5);
        current.setComponent(component);
        priority1.setComponent(component);
        priority2.setComponent(component);
        priority1.setValues(1.7, 1.8, 1.9);
        priority2.setValues(1.6, 1.7, 1.8);
        current.setValues(1.4, 1.5, 1.6);
        buffer.add(priority1);
        buffer.add(priority2);
        buffer.sort(Comparator.comparingDouble(Event::getCompleted));
        Event event = sequencer.prioritize(current, buffer);
        System.out.println("  Test when no applicable candidates");
        assertTrue(event == current);
        priority2.setValues(1.4, 1.5, 1.6);
        buffer.sort(Comparator.comparingDouble(Event::getCompleted));
        event = sequencer.prioritize(current, buffer);
        System.out.println("  Test when one applicable candidate of lower priority");
        assertTrue(event == priority2);
        assertTrue(buffer.get(0) == current);
        buffer.clear();
        buffer.add(priority1);
        buffer.add(priority2);
        priority1.setValues(1.45, 1.55, 1.65);
        buffer.sort(Comparator.comparingDouble(Event::getCompleted));
        event = sequencer.prioritize(current, buffer);
        System.out.println("  Test when two applicable candidates, different priorities");
        assertTrue(event == priority1);
        assertTrue(buffer.get(0) == current);
        buffer.clear();
        buffer.add(priority1);
        buffer.add(priority2);
        buffer.sort(Comparator.comparingDouble(Event::getCompleted));
        final Event source1 = new Event("source 1", "456", 1.1);
        source1.setComponent(component);
        source1.setValues(1.4, 1.5, 1.6);
        event = sequencer.prioritize(source1, buffer);
        System.out.println("  Test when current event is highest priority");
        assertTrue(event == source1);
        final Event source2 = new Event("source 2", "567", 1.1);
        source2.setComponent(component);
        source2.setValues(1.45, 1.55, 1.65);
        event = sequencer.prioritize(source2, buffer);
        System.out.println("  Test when current event is lower priority");
        assertTrue(event == priority1);
        assertTrue(buffer.get(0) == source2);
        final Component differentX = new DummyComponent("processor X", 1.7);
        final Component differentY = new DummyComponent("processor Y", 1.75);
        final Event priorityX = new Event("source 1", "678", 1.5);
        priorityX.setComponent(differentX);
        priorityX.setValues(1.2, 1.3, 1.4);
        final Event priorityY = new Event("source 1", "789", 1.3);
        priorityY.setComponent(differentY);
        priorityY.setValues(1.23, 1.33, 1.45);
        buffer.clear();
        buffer.add(priority1);
        buffer.add(priority2);
        buffer.add(priorityX);
        buffer.add(priorityY);
        buffer.sort(Comparator.comparingDouble(Event::getCompleted));
        assertTrue(buffer.get(0) == priorityX);
        System.out.println("  Test when event is high priority but unconnected component");
        event = sequencer.prioritize(current, buffer);
        assertFalse(sequencer.exclusions.isEmpty());
        assertTrue(sequencer.exclusions.contains(differentX));
        assertTrue(sequencer.exclusions.contains(differentY));
        assertTrue(event != priorityX);
        assertTrue(event != priorityY);
        System.out.println("  Test when event is priority but different route");
        final List<String> references = new ArrayList<>();
        references.add("processor");
        final Distributor distributor = new Smart(references);
        distributor.addNext(component);
        final Component balancer = new Balancer("balancer", distributor, false);
        priorityY.setComponent(balancer);
        buffer.clear();
        buffer.add(priority1);
        buffer.add(priority2);
        buffer.add(priorityX);
        buffer.add(priorityY);
        buffer.sort(Comparator.comparingDouble(Event::getCompleted));
        event = sequencer.prioritize(current, buffer);
        assertTrue(sequencer.paths.contains(balancer));
        assertTrue(event == priorityY);
        System.out.println("  Test when event is not priority");
        buffer.clear();
        buffer.add(priority1);
        buffer.add(priority2);
        buffer.add(priorityX);
        buffer.add(current);
        buffer.sort(Comparator.comparingDouble(Event::getCompleted));
        event = sequencer.prioritize(priority2, buffer);
        assertTrue(event != current);
        System.out.println("  Test only first event of a priority level is retained");
        buffer.clear();
        buffer.add(source2);
        buffer.add(priority2);
        buffer.sort(Comparator.comparingDouble(Event::getCompleted));
        event = sequencer.prioritize(current, buffer);
        assertTrue(event == priority2);
        System.out.println("  Test when already in exclusion");
        priorityY.setComponent(differentX);
        buffer.clear();
        buffer.add(priority1);
        buffer.add(priority2);
        buffer.add(priorityX);
        buffer.add(priorityY);
        buffer.sort(Comparator.comparingDouble(Event::getCompleted));
        event = sequencer.prioritize(current, buffer);
        assertTrue(sequencer.exclusions.size() == 1);
        assertTrue(sequencer.exclusions.contains(differentX));
        assertTrue(event != priorityY);
        System.out.println("  Test for current of higher priority");
        final Event priority3 = new Event("source 3", "345", 1.5);
        priority3.setComponent(component);
        priority3.setValues(1.4, 1.5, 1.6);
        buffer.clear();
        buffer.add(priority3);
        event = sequencer.prioritize(priority2, buffer);
        assertTrue(event == priority2);
    }
}
