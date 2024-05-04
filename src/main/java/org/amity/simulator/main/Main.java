/*
 * Main.java
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
 * Created on November 21, 2017
 */
package org.amity.simulator.main;

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.amity.simulator.elements.Model;
import org.amity.simulator.language.Lexer;
import org.amity.simulator.language.Token;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Command line entry point for running system simulation
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public class Main
{

    final static int BAD_EXIT = -1; 
    final static int GOOD_EXIT = 0; 
    private final static String START = "start";
    private final static String END = "end";
    private final static String GENERATE = "generate";
    private final static String FILE = "file";

    /**
     * Create command line options for use
     *
     * @return Apache CLI definitions
     */
    private static Options generateOptions()
    {
        final Option startOption = Option.builder("s")
                .required(true)
                .hasArg(true)
                .longOpt(START)
                .desc("Sampling start time")
                .build();
        final Option endOption = Option.builder("e")
                .required(true)
                .hasArg(true)
                .longOpt(END)
                .desc("Sampling end time")
                .build();
        final Option generateOption = Option.builder("g")
                .required(true)
                .hasArg(true)
                .longOpt(GENERATE)
                .desc("Event generation time")
                .build();
        final Option fileOption = Option.builder("f")
                .required(true)
                .hasArg(true)
                .longOpt(FILE)
                .desc("System definition file")
                .build();
        final Options options = new Options();
        options.addOption(generateOption);
        options.addOption(startOption);
        options.addOption(endOption);
        options.addOption(fileOption);
        return options;
    }

    /**
     * Process command line arguments
     *
     * @param options expected CLI definitions
     * @param arguments user input from the command line
     * @return parsed arguments
     */
    private static CommandLine parseCommandLine(final Options options,
            final String[] arguments)
    {
        final CommandLineParser parser = new DefaultParser();
        CommandLine commandLine = null;
        try
        {
            commandLine = parser.parse(options, arguments);
        }
        catch (ParseException exception)
        {
            System.err.println("Unable to parse command-line arguments:");
            System.err.println("  " + Arrays.toString(arguments));
            System.err.println("  " + exception.getMessage());
        }
        return commandLine;
    }

    /**
     * Produce explanation of command line options to output
     *
     * @param options expected CLI definitions
     */
    private static void printUsage(final Options options)
    {
        final HelpFormatter formatter = new HelpFormatter();
        final String syntax = "Main";
        System.out.println("\n=====");
        System.out.println("USAGE");
        System.out.println("=====");
        final PrintWriter writer = new PrintWriter(System.out);
        formatter.printUsage(writer, 80, syntax, options);
        writer.flush();
    }

    /**
     * Produce help information to output
     *
     * @param options expected CLI definitions
     */
    private static void printHelp(final Options options)
    {
        final HelpFormatter formatter = new HelpFormatter();
        final String syntax = "Main";
        final String usageHeader = "System throughput simulator";
        final String usageFooter = "See https://github.com/BandedHawk/system-simulator";
        System.out.println("\n====");
        System.out.println("HELP");
        System.out.println("====");
        formatter.printHelp(syntax, usageHeader, options, usageFooter);
    }

    /**
     * Run a simulation
     *
     * @param arguments command line arguments
     */
    static int simulate(String[] arguments)
    {
        final Pattern pattern = Pattern.compile("^\\+?\\d*\\.?\\d+$");
        final Options options = generateOptions();
        if (arguments.length < 8)
        {
            printUsage(options);
            printHelp(options);
            return Main.BAD_EXIT;
        }
        final CommandLine commandLine
                = parseCommandLine(options, arguments);
        boolean error = false;
        if (commandLine != null)
        {
            final String generateText
                    = commandLine.getOptionValue(GENERATE);
            final String startText
                    = commandLine.getOptionValue(START);
            final String endText
                    = commandLine.getOptionValue(END);
            final String filename
                    = commandLine.getOptionValue(FILE);
            Matcher matcher = pattern.matcher(generateText);
            if (!matcher.find())
            {
                System.err.println("Generation time must be a positive decimal value");
                error = true;
            }
            matcher = pattern.matcher(startText);
            if (!matcher.find())
            {
                System.err.println("Sample start time must be a positive decimal value");
                error = true;
            }
            matcher = pattern.matcher(endText);
            if (!matcher.find())
            {
                System.err.println("Sample end time must be a positive decimal value");
                error = true;
            }
            final File file = new File(filename);
            if (!(file.exists() && file.isFile()))
            {
                System.err.println("File does not exist");
                error = true;
            }
            if (!error)
            {
                double generate = Double.parseDouble(generateText);
                double start = Double.parseDouble(startText);
                double end = Double.parseDouble(endText);
                error = run(file, generate, start, end);
            }
        }
        return error ? BAD_EXIT : GOOD_EXIT;
    }

    public final static void main(String[] arguments)
    {
        if (simulate(arguments) != GOOD_EXIT)
        {
            System.exit(BAD_EXIT);
        }
    }

    /**
     * Perform the main task of creating the model and running the simulation
     * 
     * @param file specification for the model
     * @param generate time span of event generation
     * @param start low time limit for statistics sample
     * @param end high time limit for statistics sample
     * @return error result if any of the steps fail
     */
    private static boolean run(final File file, final double generate,
            final double start, final double end)
    {
        boolean error = false;
        if (start >= end)
        {
            System.err.println("Sample start time must be lower than the sample end time");
            error = true;
        }
        if (end >= generate)
        {
            System.err.println("Sample end time must be less than the generate time");
            error =true;
        }
        if (!error)
        {
            final Lexer lexer = new Lexer();
            final Token token = lexer.analyze(file);
            if (token != null)
            {
                System.out.println("Completed syntax parsing");
                final Model model = token.parse();
                if (model.isCompiled())
                {
                    error = !model.execute(generate, start, end);
                }
                else
                {
                    error = true;
                }
            }
            else
            {
                error = true;
            }
        }
        return error;
    }
}
