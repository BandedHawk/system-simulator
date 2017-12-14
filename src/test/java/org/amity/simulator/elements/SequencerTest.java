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
        Component component = new DummyComponent("processor", 1.7);
        final Event current = new Event("test", "123", 1.0, sequencer);
        final Event priority1 = new Event("source 1", "234", 1.5, sequencer);
        final Event priority2 = new Event("source 2", "345", 1.5, sequencer);
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
        final Event source1 = new Event("source 1", "456", 1.1, sequencer);
        source1.setComponent(component);
        source1.setValues(1.4, 1.5, 1.6);
        event = sequencer.prioritize(source1, buffer);
        System.out.println("  Test when cuirrent event is highest priority");
        assertTrue(event == source1);
        final Event source2 = new Event("source 2", "567", 1.1, sequencer);
        source2.setComponent(component);
        source2.setValues(1.4, 1.5, 1.6);
        event = sequencer.prioritize(source2, buffer);
        System.out.println("  Test when cuirrent event is lower priority");
        assertTrue(event == priority1);
        assertTrue(buffer.get(0) == source2);
    }
}
