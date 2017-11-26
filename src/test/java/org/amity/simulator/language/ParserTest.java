/*
 * ParserTest.java
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
 * Created on November 17, 2017
 */
package org.amity.simulator.language;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import org.amity.simulator.elements.Event;
import org.amity.simulator.elements.IComponent;
import org.amity.simulator.elements.Model;
import org.amity.simulator.elements.Processor;
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
public class ParserTest
{
    
    public ParserTest()
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
     * Test of parse method, of class Parser.
     */
    @Test
    public void testParse()
    {
        System.out.println("parse");
        final File file = new File("src/test/data/test.example.txt");
        final Parser instance = new Parser();
        final Token token = instance.parse(file);
        final Compiler compiler = new Compiler();
        System.out.println("  syntax rule check");
        if (token.size() > 3)
        {
            int depth = 0;
            Token current = token;
            while (current != null)
            {
                final Syntax next = current.getNext() == null ? null
                        : current.getNext().getSyntax();
                switch (current.getSyntax())
                {
                    case LABEL:
                        assertTrue(current.getNext() != null);
                        assertTrue(next.equals(Syntax.OPEN)
                                || next.equals(Syntax.ASSIGN));
                        break;
                    case OPEN:
                        depth += 1;
                        assertTrue(next.equals(Syntax.LABEL));
                        break;
                    case CLOSE:
                        depth -= 1;
                        assertTrue((next == null) || next.equals(Syntax.LABEL)
                                || next.equals(Syntax.CLOSE));
                        break;
                    case ASSIGN:
                        assertTrue(next.equals(Syntax.VALUE));
                        break;
                    default:
                        break;
                }
                current = current.getNext();
                token.compile();
            }
            System.out.println("  check balanced parentheses");
            assertTrue(depth == 0);
        }
        else
        {
            assertTrue(false);
        }
        System.out.println("  check real system");
        final File test = new File("src/test/data/test.example.txt");
        final double period = 2;
        final double sourcePeriod = 1;
        final int eventTotal = 4;
        final Token testToken = instance.parse(test);
        final Model model = testToken.compile();
        final LinkedList<Event> events = new LinkedList<>();
        System.out.println("    check components");
        assertTrue(model.sources.size() == 1);
        assertTrue(model.components.size() == 2);
        for (final IComponent source : model.sources)
        {
            for (int count = 0; count < eventTotal; count++)
            {
                final Event event = source.simulate(null);
                events.add(event);
            }
        }
        System.out.println("    check generated events");
        // sort events by arrival time
        events.sort(Comparator.comparingDouble(Event::getArrived));
        assertTrue(events.size() == eventTotal);
        System.out.println("    simulate system");
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
        final List<Event> local = new ArrayList<>();
        for (final IComponent component : model.components.values())
        {
            if (component instanceof Processor)
            {
                local.addAll(component.getLocalEvents());
            }
        }
        System.out.println("    check event states are as expected");
        assertTrue(local.size() == eventTotal);
        assertTrue(events.isEmpty());
        double sourceTick = 0;
        double tick = sourcePeriod;
        System.out.println("    check event statistics are correct");
        for (final Event event : local)
        {
            System.out.println("      " + event.getSource() + " "
                    + event.getLabel() + " -> arrival: " + event.getArrived()
                    + ", start: " + event.getStarted() + ", complete: "
                    + event.getCompleted());
            sourceTick += sourcePeriod;
            assertTrue(event.getArrived() == sourceTick);
            assertTrue(event.getStarted() == tick);
            final double completeTick = tick + period;
            assertTrue(event.getCompleted() == completeTick);
            final double elapsed = completeTick - sourceTick;
            assertTrue(event.getCompleted() - event.getCreated() == elapsed);
            final double executed = completeTick - tick;
            assertTrue(event.getExecuted() == executed);
            tick = completeTick;
        }
    }
}
