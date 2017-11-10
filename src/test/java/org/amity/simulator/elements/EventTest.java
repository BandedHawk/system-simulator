/*
 * EventTest.java
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
 * Created on November 7, 2017
 */
package org.amity.simulator.elements;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the operation of the carrier class for simulated system events.
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public class EventTest
{
    
    public EventTest()
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
     * Test of setValues method, of class Event.
     */
    @Test
    public void testSetValues()
    {
        System.out.println("setValues");
        final Event instance = new Event("generator", "test");
        final double arrival = 10.0;
        final double start = 15.0;
        final double complete = 20.0;
        System.out.println("  Test setting values");
        instance.setValues(arrival, start, complete);
        System.out.println("    Check arrival");
        double result = instance.getArrived();
        assertEquals(arrival, result, 0.0);
        System.out.println("    Check start");
        result = instance.getStarted();
        assertEquals(start, result, 0.0);
        System.out.println("    Check complete");
        result = instance.getCompleted();
        assertEquals(complete, result, 0.0);
    }

    /**
     * Test of getSource method, of class Event.
     */
    @Test
    public void testGetSource()
    {
        System.out.println("getSource");
        System.out.println("  Test source initialize");
        final String source = "source";
        final String label = "test";
        final Event instance = new Event(source, label);
        assertEquals(source, instance.getSource());
        System.out.println("  Test source set");
        final String update = "other";
        instance.setSource(update);
        assertEquals(update, instance.getSource());
        System.out.println("  Test source copy");
        instance.setSource(source);
        final Event other = new Event(instance);
        assertEquals(source, other.getSource());
    }

    /**
     * Test of getLabel method, of class Event.
     */
    @Test
    public void testGetLabel()
    {
        System.out.println("getLabel");
        System.out.println("  Test label initialize");
        final String source = "source";
        final String label = "test";
        final Event instance = new Event(source, label);
        assertEquals(label, instance.getLabel());
        System.out.println("  Test label set");
        final String update = "other";
        instance.setLabel(update);
        assertEquals(update, instance.getLabel());
        System.out.println("  Test label copy");
        instance.setLabel(label);
        final Event other = new Event(instance);
        assertEquals(label, other.getLabel());
    }

    /**
     * Test of getArrival method, of class Event.
     */
    @Test
    public void testGetArrival()
    {
        System.out.println("getArrival");
        System.out.println("  Test arrival initialize");
        final Event instance = new Event("source", "test");
        double expResult = 0.0;
        double result = instance.getArrived();
        assertEquals(expResult, result, 0.0);
        System.out.println("  Test setting arrival");
        expResult = 15.0;
        instance.setArrived(expResult);
        result = instance.getArrived();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of getStart method, of class Event.
     */
    @Test
    public void testGetStart()
    {
        System.out.println("getStart");
        System.out.println("  Test start initialize");
        final Event instance = new Event("source", "test");
        double expResult = 0.0;
        double result = instance.getStarted();
        assertEquals(expResult, result, 0.0);
        System.out.println("  Test setting start");
        expResult = 15.0;
        instance.setStarted(expResult);
        result = instance.getStarted();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of getComplete method, of class Event.
     */
    @Test
    public void testGetComplete()
    {
        System.out.println("getComplete");
        System.out.println("  Test complete initialize");
        final Event instance = new Event("source", "test");
        double expResult = 0.0;
        double result = instance.getCompleted();
        assertEquals(expResult, result, 0.0);
        System.out.println("  Test complete start");
        expResult = 15.0;
        instance.setCompleted(expResult);
        result = instance.getCompleted();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of getElapsed method, of class Event.
     */
    @Test
    public void testGetElapsed()
    {
        System.out.println("getElapsed");
        System.out.println("  Test elapsed initialize");
        final Event instance = new Event("source", "test");
        double expResult = 0.0;
        double result = instance.getElapsed();
        assertEquals(expResult, result, 0.0);
        System.out.println("  Test setting elapsed");
        expResult = 15.0;
        instance.setElapsed(expResult);
        result = instance.getElapsed();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of getExecuted method, of class Event.
     */
    @Test
    public void testGetExecuted()
    {
        System.out.println("getExecuted");
        System.out.println("  Test executed initialize");
        final Event instance = new Event("source", "test");
        double expResult = 0.0;
        double result = instance.getExecuted();
        assertEquals(expResult, result, 0.0);
        System.out.println("  Test setting executed");
        expResult = 15.0;
        instance.setExecuted(expResult);
        result = instance.getExecuted();
        assertEquals(expResult, result, 0.0);
    }
}
