/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 *
 * @author jbarnett
 */
public class SkewedTest
{
    
    public SkewedTest()
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
     * Test of generate method, of class Skewed.
     */
    @Test
    public void testGenerate()
    {
        System.out.println("generate");
        final int eventTotal = 1000000;
        final double maximum = 3;
        final double minimum = 1;
        double bias = -3;
        final double skew = 0.8;
        IGenerator instance = new Skewed(minimum, maximum, skew, bias);
        final DescriptiveStatistics statistics = new DescriptiveStatistics();
        for (int count = 0; count < eventTotal; count++)
        {
            final double value = instance.generate();
            statistics.addValue(value);
        };
        System.out.println("  skew left");
        double mean = statistics.getMean();
        double sd = statistics.getStandardDeviation();
        double max = statistics.getMax();
        double min = statistics.getMin();
        System.out.println("    Mean:" + mean);
        System.out.println("    Standard Deviation:" + sd);
        System.out.println("    Maximum: " + max);
        System.out.println("    Minimum: " + min);
        assertTrue(min >= minimum);
        assertTrue(max <= maximum);
        assertTrue(mean < (max + min)/2);
        statistics.clear();
        System.out.println("  skew right");
        bias = 3;
        instance = new Skewed(minimum, maximum, skew, bias);
        for (int count = 0; count < eventTotal; count++)
        {
            final double value = instance.generate();
            statistics.addValue(value);
        };
        mean = statistics.getMean();
        sd = statistics.getStandardDeviation();
        max = statistics.getMax();
        min = statistics.getMin();
        System.out.println("    Mean:" + mean);
        System.out.println("    Standard Deviation:" + sd);
        System.out.println("    Maximum: " + max);
        System.out.println("    Minimum: " + min);
        assertTrue(min >= minimum);
        assertTrue(max <= maximum);
        assertTrue(mean > (max + min)/2);
    }
    
}
