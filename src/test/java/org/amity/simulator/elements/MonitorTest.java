/*
 * MonitorTest.java
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
 * Created on November 21, 2017
 */
package org.amity.simulator.elements;

import java.io.File;
import java.util.Comparator;
import java.util.LinkedList;
import org.amity.simulator.language.Parser;
import org.amity.simulator.language.Token;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test monitoring for the system
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public class MonitorTest
{
    
    public MonitorTest()
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
     * Test of displayStatistics method, of class Monitor.
     */
    @Test
    public void testDisplayStatistics()
    {
        System.out.println("displayStatistics");
        final File file = new File("src/test/data/monitoring.example.txt");
        final Parser parser = new Parser();
        final Token token = parser.parse(file);
        final Model model = token.compile();
        final double sampleStart = 40;
        final double sampleEnd = 10000;
        final double end = 10100;
        final Monitor instance = new Monitor(sampleStart, sampleEnd);
        final LinkedList<Event> events = new LinkedList<>();
        for (final IComponent source : model.sources)
        {
            do
            {
                final Event event = source.simulate(null);
                if (event.getStarted() > end)
                {
                    break;
                }
                events.add(event);
            }
            while(true);
        }
        while (events.size() > 0)
        {
            final Event event = events.removeFirst();
            event.simulate();
            if (event.getComponent() != null)
            {
                events.add(event);
                events.sort(Comparator.comparingDouble(Event::getArrived));
            }
        }
        for (final IComponent component : model.components.values())
        {
            component.generateStatistics(instance);
        }
        assertTrue(true);
    }
    
}
