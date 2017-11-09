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
package org.amity.component;

import java.util.List;
import org.amity.element.Event;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

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
    public void testExecute()
    {
        System.out.println("execute");
        final double sourcePeriod = 1;
        final double period = 2;
        assertTrue(period > sourcePeriod);
        final String sourceLabel = "source";
        final String label = "delay";
        final int eventTotal = 4;
        final IFunction sourceFunction = new Constant(sourcePeriod);
        final IFunction function = new Constant(period);
        final IComponent instance = new Processor(label, function, null);
        final IComponent source =
                new Source(sourceLabel, eventTotal, sourceFunction, instance);
        source.simulate(null);
        System.out.println("  check events are preserved");
        final List<Event> local = instance.getLocalEvents();
        assertTrue(local.size() == eventTotal);
        double sourceTick = 0;
        double tick = sourcePeriod;
        System.out.println("  check event statistics are correct");
        for (final Event event: local)
        {
            System.out.println("    " + event.getLabel() + " -> arrival: "
                    + event.getArrival() + ", start: " + event.getStart()
                    + ", complete: " + event.getComplete());
            sourceTick += sourcePeriod;
            assertTrue(event.getArrival() == sourceTick);
            assertTrue(event.getStart() == tick);
            final double completeTick = tick + period;
            assertTrue(event.getComplete() == completeTick);
            tick = completeTick;
        }
    }
}
