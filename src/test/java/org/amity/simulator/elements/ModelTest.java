/*
 * ModelTest.java
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
package org.amity.simulator.elements;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.amity.simulator.language.Lexer;
import org.amity.simulator.language.Token;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Checks creation of simulation model
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public class ModelTest
{
    
    public ModelTest()
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
     * Test of addErrors method, of class Model.
     */
    @Test
    public void testAddErrors()
    {
        System.out.println("addErrors");
        final List<String> errors = new ArrayList();
        final Model instance = new Model();
        instance.addErrors(errors);
        assertTrue(instance.isCompiled());
        assertTrue(instance.getErrors().isEmpty());
        errors.add("Some random messaage");
        instance.addErrors(errors);
        assertFalse(instance.isCompiled());
        assertTrue(instance.getErrors().size() == 1);
    }


    /**
     * Test of isCompiled method, of class Model.
     */
    @Test
    public void testIsCompiled()
    {
        System.out.println("isCompiled");
        System.out.println("  Working specification");
        final File file = new File("src/test/data/test.example.txt");
        final Lexer lexer = new Lexer();
        final Token token = lexer.analyze(file);
        assertTrue(token != null);
        Model model = token.parse();
        assertTrue(model.getErrors().isEmpty());
        assertTrue(model.isCompiled());
        System.out.println("  Pre-object build broken specification");
        final File badFile = new File("src/test/data/broken.example.txt");
        final Token badToken = lexer.analyze(badFile);
        assertTrue(badToken != null);
        model = badToken.parse();
        assertTrue(model.getErrors().size() == 14);
        assertFalse(model.isCompiled());
        System.out.println("  Bad reference specification");
        final File brokenFile = new File("src/test/data/broken.reference.txt");
        final Token brokenToken = lexer.analyze(brokenFile);
        assertTrue(brokenToken != null);
        model = brokenToken.parse();
        assertTrue(model.getErrors().size() == 2);
        assertFalse(model.isCompiled());
    }

    /**
     * Test of execute method, of class Model.
     */
    @Test
    public void testExecute()
    {
        System.out.println("execute");
        System.out.println("  Test with working definition");
        double generate = 100.0;
        double start = 10.0;
        double end = 90.0;
        File file = new File("src/test/data/balancer.example.txt");
        final Lexer lexer = new Lexer();
        Token token = lexer.analyze(file);
        Model model = token.parse();
        assertTrue(model.getErrors().isEmpty());
        assertTrue(model.isCompiled());
        final boolean completed = model.execute(generate, start, end);
        assertTrue(completed);
        System.out.println("  Test with incorrect parameters");
        assertFalse(model.execute(generate, end, start));
        assertFalse(model.execute(end, start, generate));
        assertFalse(model.execute(generate, -1, end));
        System.out.println("  Test with long execution");
        assertTrue(model.execute(1000, 50, 900));
        System.out.println("  Test with empty model");
        model = new Model();
        assertFalse(model.isCompiled());
        assertFalse(model.execute(generate, start, end));
        System.out.println("  Test with complex definition");
        file = new File("src/test/data/multi-source.model.txt");
        token = lexer.analyze(file);
        model = token.parse();
        assertTrue(model.getErrors().isEmpty());
        assertTrue(model.isCompiled());
        assertTrue(model.execute(generate, start, end));
    }
}
