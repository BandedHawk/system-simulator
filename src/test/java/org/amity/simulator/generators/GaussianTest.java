/*
 * GaussianTest.java
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

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests that the resulting values follow a Gaussian distribution
 * characteristic.
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public class GaussianTest
{
    
    public GaussianTest()
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
     * Test of generate method, of class Gaussian.
     */
    @Test
    public void testGenerate()
    {
        System.out.println("generate");
        final int eventTotal = 1000000;
        final double maximum = 3;
        final double minimum = 1;
        IGenerator instance = new Gaussian(minimum, maximum);
        final DescriptiveStatistics statistics = new DescriptiveStatistics();
        for (int count = 0; count < eventTotal; count++)
        {
            final double value = instance.generate();
            statistics.addValue(value);
        }
        final double mean = statistics.getMean();
        final double sd = statistics.getStandardDeviation();
        final double max = statistics.getMax();
        final double min = statistics.getMin();
        final double error = 0.001;
        System.out.println("  Mean:" + mean);
        System.out.println("  Standard Deviation:" + sd);
        System.out.println("  Maximum: " + max);
        System.out.println("  Minimum: " + min);
        assertTrue(Math.abs(mean - (maximum + minimum)/2) < error);
        assertTrue(min >= minimum);
        assertTrue(max <= maximum);
    }
}
