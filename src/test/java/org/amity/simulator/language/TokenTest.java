/*
 * TokenTest.java
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
 * Created on December 3, 2017
 */
package org.amity.simulator.language;

import java.io.File;
import org.amity.simulator.elements.Model;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests generation of tokens and basic identification
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public class TokenTest
{
    
    public TokenTest()
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
     * Test of getValue method, of class Token.
     */
    @Test
    public void testGetValue()
    {
        System.out.println("getValue");
        final File file = new File("src/test/data/test.example.txt");
        final Lexer lexer = new Lexer();
        final Token token = lexer.analyze(file);
        assertEquals(Syntax.LABEL, token.getSyntax());
        assertEquals(1, token.getLine());
        assertEquals(1, token.getPosition());
        assertEquals(Vocabulary.COMPONENT, token.getValue());
    }

    /**
     * Test of getSyntax method, of class Token.
     */
    @Test
    public void testGetSyntax()
    {
        System.out.println("getSyntax");
        final File file = new File("src/test/data/test.example.txt");
        final Lexer lexer = new Lexer();
        final Token token = lexer.analyze(file);
        final Token next = token.getNext();
        assertEquals(Syntax.OPEN, next.getSyntax());
        assertEquals(2, next.getLine());
        assertEquals(1, next.getPosition());
        assertEquals("{", next.getValue());
    }

    /**
     * Test of getLine method, of class Token.
     */
    @Test
    public void testGetLine()
    {
        System.out.println("getLine");
        final File file = new File("src/test/data/test.example.txt");
        final Lexer lexer = new Lexer();
        final Token token = lexer.analyze(file);
        final Token next = token.getNext();
        assertEquals(Syntax.OPEN, next.getSyntax());
        assertEquals(2, next.getLine());
        assertEquals(1, next.getPosition());
        assertEquals("{", next.getValue());
    }

    /**
     * Test of getPosition method, of class Token.
     */
    @Test
    public void testGetPosition()
    {
        System.out.println("getPosition");
        final File file = new File("src/test/data/test.example.txt");
        final Lexer lexer = new Lexer();
        final Token token = lexer.analyze(file);
        assertEquals(Syntax.LABEL, token.getSyntax());
        assertEquals(1, token.getLine());
        assertEquals(1, token.getPosition());
        assertEquals(Vocabulary.COMPONENT, token.getValue());
    }

    /**
     * Test of add method, of class Token.
     */
    @Test
    public void testAdd()
    {
        System.out.println("add");
        final File file = new File("src/test/data/test.example.txt");
        final Lexer lexer = new Lexer();
        final Token token = lexer.analyze(file);
        assertTrue(token.size() == 153);
    }

    /**
     * Test of getNext method, of class Token.
     */
    @Test
    public void testGetNext()
    {
        System.out.println("getNext");
        final File file = new File("src/test/data/test.example.txt");
        final Lexer lexer = new Lexer();
        final Token token = lexer.analyze(file);
        assertEquals(Syntax.LABEL, token.getSyntax());
        assertTrue(token.getNext() != null);
        final Token next = token.getNext();
        assertEquals("{", next.getValue());
        assertEquals(Syntax.OPEN, next.getSyntax());
        assertEquals(2, next.getLine());
        assertEquals(1, next.getPosition());
    }

    /**
     * Test of size method, of class Token.
     */
    @Test
    public void testSize()
    {
        System.out.println("size");
        final File file = new File("src/test/data/test.example.txt");
        final Lexer lexer = new Lexer();
        final Token token = lexer.analyze(file);
        assertTrue(token.size() == 153);
    }

    /**
     * Test of parse method, of class Token.
     */
    @Test
    public void testParse()
    {
        System.out.println("parse");
        System.out.println("  Test simple case");
        final File file = new File("src/test/data/test.example.txt");
        final Lexer lexer = new Lexer();
        final Token token = lexer.analyze(file);
        Model model = token.parse();
        assertTrue(model.isCompiled());
        System.out.println("  Test complex case");
        final File complexFile = new File("src/test/data/balancer.example.txt");
        final Token complexToken = lexer.analyze(complexFile);
        model = complexToken.parse();
        assertTrue(model.isCompiled());
    }
}
