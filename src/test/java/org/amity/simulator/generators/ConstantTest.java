/*
 * ConstantTest.java
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
package org.amity.simulator.generators;

import java.util.ArrayList;
import java.util.List;
import org.amity.simulator.elements.Processor;
import org.amity.simulator.language.NameValue;
import org.amity.simulator.language.Vocabulary;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.amity.simulator.elements.Component;

/**
 * Tests that the resulting values follow a constant characteristic.
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public class ConstantTest
{
    
    public ConstantTest()
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
     * Test of generate method, of class Constant.
     */
    @Test
    public void testGenerate()
    {
        System.out.println("generate");
        final int eventTotal = 1000000;
        final double period = 5;
        final String source = Vocabulary.DEFAULT;
        final String reference = "database";
        Generator instance = new Constant(period, source, reference);
        final DescriptiveStatistics statistics = new DescriptiveStatistics();
        for (int count = 0; count < eventTotal; count++)
        {
            final double value = instance.generate();
            statistics.addValue(value);
        };
        final double mean = statistics.getMean();
        final double sd = statistics.getStandardDeviation();
        final double maximum = statistics.getMax();
        final double error = 0.002;
        System.out.println("  Mean:" + mean);
        System.out.println("  Standard Deviation:" + sd);
        assertTrue(Math.abs(mean - period) < error);
        assertTrue(sd < error);
    }

    /**
     * Test of characteristics method, of class Constant.
     */
    @Test
    public void testCharacteristics()
    {
        System.out.println("characteristics");
        final double period = 5;
        final String source = Vocabulary.DEFAULT;
        final String reference = "database";
        final Generator instance = new Constant(period, source, reference);
        assertTrue(instance.characteristics() != null);
    }

    /**
     * Test of getSource method, of class Constant.
     */
    @Test
    public void testGetSource()
    {
        System.out.println("getSource");
        final double period = 5;
        final String source = Vocabulary.DEFAULT;
        final String reference = "database";
        final Generator instance = new Constant(period, source, reference);
        assertEquals(instance.getSource(), source);
    }

    /**
     * Test of getReference method, of class Constant.
     */
    @Test
    public void testGetReference()
    {
        System.out.println("getReference");
        final double period = 5;
        final String source = Vocabulary.DEFAULT;
        final String reference = "database";
        final Generator instance = new Constant(period, source, reference);
        assertEquals(instance.getReference(), reference);
    }

    /**
     * Test of getNext method, of class Constant.
     */
    @Test
    public void testGetNext()
    {
        System.out.println("getNext");
        final double period = 5;
        final String source = Vocabulary.DEFAULT;
        final String reference = "database";
        final Generator instance = new Constant(period, source, reference);
        final List<Generator> generators = new ArrayList<>();
        final List<String> priority = new ArrayList<>();
        generators.add(instance);
        final Component component = new Processor("test", generators,
                priority, false);
        instance.setNext(component);
        assertEquals(instance.getNext(), component);
    }

    /**
     * Test of instance method, of class Constant.
     */
    @Test
    public void testInstance()
    {
        System.out.println("instance");
        final List<NameValue> pairs = new ArrayList<>();
        NameValue pair = new NameValue(Vocabulary.PERIOD, "51.3");
        pairs.add(pair);
        Generator result = Constant.instance(pairs);
        assertTrue(result != null);
        assertEquals(result.getReference(), null);
        assertEquals(result.getSource(), Vocabulary.DEFAULT);
        assertEquals(result.characteristics(), "Constant - 51.3");
        pair = new NameValue(Vocabulary.SOURCE, Vocabulary.COMPONENT);
        pairs.add(pair);
        pair = new NameValue(Vocabulary.NEXT, Vocabulary.PROCESSOR);
        pairs.add(pair);
        pair = new NameValue(Vocabulary.FUNCTION, Vocabulary.FUNCTION);
        pairs.add(pair);
        result = Constant.instance(pairs);
        assertEquals(result.getSource(), Vocabulary.COMPONENT);
        assertEquals(result.getReference(), Vocabulary.PROCESSOR);
    }
    
}
