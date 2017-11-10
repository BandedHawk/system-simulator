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
    public void testExecute()
    {
        System.out.println("execute");
        final double period = 5;
        final String label = "test";
        final int eventTotal = 3;
        final IGenerator function = new Constant(period);
        final IComponent instance = new Source(label, eventTotal, function, null);
        for (int count = 0; count < eventTotal; count++)
        {
            instance.simulate(null);
        }
        System.out.println("  check events are preserved");
        final List<Event> local = instance.getLocalEvents();
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
     * Test of reset method, of class Processor.
     */
    @Test
    public void testReset()
    {
        System.out.println("reset");
        final double period = 5;
        final String label = "test";
        final int eventTotal = 3;
        final IGenerator function = new Constant(period);
        final IComponent instance = new Source(label, eventTotal, function, null);
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
            instance.simulate(null);
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
}
