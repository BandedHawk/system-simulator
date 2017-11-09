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
import org.amity.simulator.elements.Source;
import java.util.List;
import org.amity.simulator.elements.Event;
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
        instance.simulate(null);
        final List<Event> local = instance.getLocalEvents();
        assertTrue(local.size() == eventTotal);
        double tick = 0;
        for (Event event: local)
        {
            tick += period;
            assertTrue(event.getArrival() == tick);
            assertTrue(event.getStart() == tick);
            assertTrue(event.getComplete() == tick);
        }
    }
}
