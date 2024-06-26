/*
 * Lexer.java
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
 * Created on November 13, 2017
 */
package org.amity.simulator.language;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;

/**
 * Parses file against the syntax rules for the specification
 * language and generates tokens for further processing.
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public class Lexer
{

    /**
     * Converts the text in a file to syntactically correct tokens for
     * further processing
     * 
     * @param file text to be converted into tokens
     * @return start of the linked list of tokens if the text was
     * syntactically correct otherwise <code>null</code>
     */
    public Token analyze(final File file)
    {
        Token token = null;
        try (Scanner scanner = new Scanner(file, "UTF-8"))
        {
            final ParseTracker tracker = new ParseTracker();
            parse(scanner, tracker);
            // Convert from list to linked list
            if (tracker.passed && !tracker.tokens.isEmpty())
            {
                token = tracker.tokens.get(0);
                if (tracker.tokens.size() > 1)
                {
                    for (int index = 1; index < tracker.tokens.size(); index++)
                    {
                        final Token current = tracker.tokens.get(index);
                        token.add(current);
                    }
                }
            }
            for (final String error : tracker.errors)
            {
                System.err.println(error);
            }
        }
        catch (Exception exception)
        {
            System.err.println(exception.toString());
            for (StackTraceElement element : exception.getStackTrace())
            {
                System.err.println(element.toString());
            }
        }
        return token;
    }

    /**
     * Splits file stream into lines and delegates to a line parser to
     * do more granular parsing
     * 
     * @param scanner stream representation of the file
     * @param tracker local state container for the parser for line to line
     * continuity
     */
    private void parse(final Scanner scanner, final ParseTracker tracker)
    {
        while (scanner.hasNextLine())
        {
            final String line = scanner.nextLine();
            tracker.line = line;
            tracker.lineNumber++;
            parseLine(tracker);
        }
        if (tracker.depth > 0)
        {
            final String error = "Unclosed declarations";
            tracker.errors.add(error);
            tracker.passed = false;
        }
    }

    /**
     * Processes a line of text from the file and generates valid tokens
     * if the text meets the syntax rules.
     * 
     * @param tracker local state container for the parser for line to line
     * continuity
     */
    private void parseLine(final ParseTracker tracker)
    {
        int position = 0;
        boolean found = true;
        while (found)
        {
            final String section = tracker.line.substring(position);
            found = !"".equals(section.trim());
            if (found)
            {
                for (final Syntax syntax
                        : StateMachine.getOptions(tracker.state))
                {
                    final Matcher matcher
                            = syntax.getPattern().matcher(section);
                    found = matcher.find();
                    if (found)
                    {
                        final String match
                                = section.substring(matcher.start(),
                                        matcher.end()).trim();
                        position += matcher.end();
                        final int start = position - match.length() + 1;
                        // ignore comments
                        if (!syntax.equals(Syntax.COMMENT))
                        {
                            final Token token
                                    = new Token(match, syntax,
                                            tracker.lineNumber, start);
                            tracker.tokens.add(token);
                        }
                        if (syntax.equals(Syntax.CLOSE))
                        {
                            if (tracker.depth > 0)
                            {
                                tracker.state = Syntax.OPEN;
                            }
                            else
                            {
                                tracker.state = Syntax.START;
                            }
                            tracker.depth--;
                        }
                        else if (!syntax.equals(Syntax.COMMENT))
                        {
                            tracker.state = syntax;
                            if (syntax.equals(Syntax.OPEN))
                            {
                                tracker.depth++;
                            }
                        }
                        break;
                    }
                }
                if (!found)
                {
                    final StringBuilder error =
                            new StringBuilder("Unexpected entry found near ");
                    error.append(tracker.lineNumber).append(", ")
                            .append(position + 1);
                    tracker.errors.add(error.toString());
                    // Reset state and hope we get a re-sync eventually
                    tracker.state = Syntax.OPEN;
                    tracker.passed = false;
                }
            }
        }
    }

    /**
     * State container to supporting parsing of the file
     */
    private class ParseTracker
    {

        private String line;
        private Syntax state;
        private int lineNumber;
        private final List<Token> tokens;
        private final List<String> errors;
        private boolean passed;
        private int depth;

        /**
         * Constructor to configure initial parsing state
         */
        private ParseTracker()
        {
            this.state = Syntax.START;
            this.lineNumber = 0;
            this.tokens = new ArrayList<>();
            this.errors = new ArrayList<>();
            this.passed = true;
            this.depth = 0;
        }
    }
}
