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
package org.amity.simulator.main;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public class MainTest
{
    public MainTest()
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
     * Test with no args of main method.
     */
    @Test
    public void testNoArgs()
    {
        System.out.println("  Execute with no parameters");
        String[] arguments = new String[0];
        final int statusCode = Main.simulate(arguments);
        assertEquals(Main.BAD_EXIT, statusCode);
    }

    /**
     * Test with just under number of args expected of main method.
     */
    @Test
    public void testUnderArgs()
    {
        System.out.println("  Execute with 1 too few parameters");
        String[] arguments
            = new String[]{"-s", "a", "-e", "b", "-g", "d", "-f"};
        final int statusCode = Main.simulate(arguments);
        assertEquals(Main.BAD_EXIT, statusCode);
    }

    /**
     * Test with illegal generate value of main method.
     */
    @Test
    public void testIllegalGenerate()
    {
        System.out.println("  Execute with illegal generate value");
        final String[] arguments
                = new String[]{"-g", "-100", "-s", "1", "-e", "5", "-f",
                    "src/test/data/test.example.txt"};
        final int statusCode = Main.simulate(arguments);
        assertEquals(Main.BAD_EXIT, statusCode);
    }

    /**
     * Test with illegal start value of main method.
     */
    @Test
    public void testIllegalStart()
    {
        System.out.println("  Execute with illegal start value");
        final String[] arguments
            = new String[]{"-g", "100", "-s", "-1", "-e", "5", "-f",
                "src/test/data/test.example.txt"};
        final int statusCode = Main.simulate(arguments);
        assertEquals(Main.BAD_EXIT, statusCode);
    }

    /**
     * Test with illegal end value of main method.
     */
    @Test
    public void testIllegalEnd()
    {
        System.out.println("  Execute with illegal end value");
        final String[] arguments
            = new String[]{"-g", "100", "-s", "1", "-e", "-5", "-f",
                "src/test/data/test.example.txt"};
        final int statusCode = Main.simulate(arguments);
        assertEquals(Main.BAD_EXIT, statusCode);
    }

    /**
     * Test with non-existent file of main method.
     */
    @Test
    public void testNonexistentFile()
    {
        System.out.println("  Execute with wrong file reference");
        final String[] arguments
            = new String[]{"-g", "100", "-s", "1", "-e", "5", "-f",
                "test.example.txt"};
        final int statusCode = Main.simulate(arguments);
        assertEquals(Main.BAD_EXIT, statusCode);
    }

    /**
     * Test with start and end not valid range in main method.
     */
    @Test
    public void testIllegalRange()
    {
        System.out.println("  Execute with start larger than end value");
        final String[] arguments
            = new String[]{"-g", "100", "-s", "10", "-e", "5", "-f",
                "src/test/data/test.example.txt"};
        final int statusCode = Main.simulate(arguments);
        assertEquals(Main.BAD_EXIT, statusCode);
    }

    /**
     * Test with generate and end not valid range in main method.
     */
    @Test
    public void testIllegalGenerateRange() throws Exception
    {
        System.out.println("  Execute with end larger than generate value");
        final String[] arguments
                = new String[]{"-g", "15", "-s", "5", "-e", "20", "-f",
                    "src/test/data/test.example.txt"};
        final int statusCode = Main.simulate(arguments);
        assertEquals(Main.BAD_EXIT, statusCode);
    }

    /**
     * Test with bad file for lexer.
     */
    @Test
    public void testIllegalLexer()
    {
        System.out.println("  Execute with lexer failure");
        final String[] arguments
            = new String[]{"-g", "20", "-s", "5", "-e", "15", "-f",
                "src/test/data/bad.lexer.example.txt"};
        final int statusCode = Main.simulate(arguments);
        assertEquals(Main.BAD_EXIT, statusCode);
    }

    /**
     * Test with bad file for compiler.
     */
    @Test
    public void testIllegalCompiler()
    {
        System.out.println("  Execute with compiler failure");
        final String[] arguments
            = new String[]{"-g", "20", "-s", "5", "-e", "15", "-f",
                "src/test/data/broken.example.txt"};
        final int statusCode = Main.simulate(arguments);
        assertEquals(Main.BAD_EXIT, statusCode);
    }

    /**
     * Test with acceptable arguments.
     */
    @Test
    public void testAcceptableArguments()
    {
        System.out.println("  Execute with runnable model");
        final String[] arguments
            = new String[]{"-g", "20", "-s", "5", "-e", "15", "-f",
                "src/test/data/test.example.txt"};
        final int statusCode = Main.simulate(arguments);
        assertEquals(Main.GOOD_EXIT, statusCode);
    }
}
