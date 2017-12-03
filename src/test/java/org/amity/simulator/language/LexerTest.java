/*
 * LexerTest.java
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
public class LexerTest
{
    
    public LexerTest()
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
     * Test of analyze method, of class Lexer.
     */
    @Test
    public void testAnalyze()
    {
        System.out.println("analyze");
        final File file = new File("src/test/data/test.example.txt");
        final Lexer instance = new Lexer();
        final Token token = instance.analyze(file);
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
            }
            token.parse();
            System.out.println("  check balanced parentheses");
            assertTrue(depth == 0);
        }
        else
        {
            assertTrue(false);
        }
    }
}
