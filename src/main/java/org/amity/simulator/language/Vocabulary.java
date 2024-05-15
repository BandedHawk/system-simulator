/*
 * java
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Declares definition language elements
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public class Vocabulary
{
    private final static Map<String, Map<String, Definition>>[] DEFINITIONS;
    private final static List<String>[] DECLARATIONS;
    public final static String TYPE_NAME = "type";
    public final static Pattern TYPE_PATTERN;
    public final static String MONITOR = "monitor";
    public final static String MAXIMUM = "maximum";
    public final static String MINIMUM = "minimum";
    public final static String PERIOD = "period";
    public final static String SKEW = "skew";
    public final static String BIAS = "bias";
    public final static String ROUNDROBIN = "round-robin";
    public final static String SMART = "smart";
    public final static String RANDOM = "random";
    public final static String SKEWED = "skewed";
    public final static String UNIFORM = "uniform";
    public final static String CONSTANT = "constant";
    public final static String GAUSSIAN = "gaussian";
    public final static String FUNCTION = "function";
    public final static String SOURCE = "source";
    public final static String DEFAULT = "default";
    public final static String PROCESSOR = "processor";
    public final static String THROTTLE = "throttle";
    public final static String BALANCER = "balancer";
    public final static String NEXT = "next";
    public final static String NAME = "name";
    public final static String START = "start";
    public final static String END = "end";
    public final static String PRIORITY = "priority";
    public final static String COMPONENT = "component";

    static
    {
        TYPE_PATTERN = Pattern.compile("^\\s*[a-zA-Z][\\s\\w]*[\\-]*[\\s\\w]*$");
        final Map<String, Map<String, Definition>> blocks = new HashMap<>();
        final Map<String, Definition> source = new HashMap<>();
        final Map<String, Definition> processor = new HashMap<>();
        final Map<String, Definition> throttle = new HashMap<>();
        final Map<String, Definition> balancer = new HashMap<>();
        final Pattern words = Pattern.compile("^\\s*[a-zA-Z][\\s|\\w]*$");
        final Pattern positiveDecimal = Pattern.compile("^\\+?\\d*\\.?\\d+$");
        final Pattern decimal = Pattern.compile("^[\\+\\-]?\\d*\\.?\\d+$");
        final Pattern binaryResponse = Pattern.compile("^[Yy]([Ee][Ss])*|[Nn][Oo]*$");
        final Definition mandatoryWords = new Definition(words, true, false);
        final Definition optionalWords = new Definition(words, false, false);
        final Definition monitor = new Definition(binaryResponse, false, false);
        final Definition mandatoryDecimal =
                new Definition(positiveDecimal, true, false);
        final Definition optionalDecimal =
                new Definition(positiveDecimal, false, false);
        final Definition biasDecimal =
                new Definition(decimal, true, false);
        final Definition multiWords = new Definition(words, true, true);
        final Definition optionalMulti = new Definition(words, false, true);
        balancer.put(NAME, mandatoryWords);
        balancer.put(MONITOR, monitor);
        source.put(NAME, mandatoryWords);
        source.put(START, optionalDecimal);
        source.put(END, optionalDecimal);
        source.put(MONITOR, monitor);
        processor.put(NAME, mandatoryWords);
        processor.put(MONITOR, monitor);
        processor.put(PRIORITY, optionalMulti);
        throttle.put(NAME, mandatoryWords);
        throttle.put(MONITOR, monitor);
        throttle.put(PRIORITY, optionalMulti);
        blocks.put(BALANCER, balancer);
        blocks.put(SOURCE, source);
        blocks.put(PROCESSOR, processor);
        blocks.put(THROTTLE, throttle);
        final Map<String, Map<String, Definition>> functions = new HashMap<>();
        final Map<String, Definition> bounds = new HashMap<>();
        bounds.put(MAXIMUM, mandatoryDecimal);
        bounds.put(MINIMUM, mandatoryDecimal);
        final Map<String, Definition> complex = new HashMap<>();
        complex.put(MAXIMUM, mandatoryDecimal);
        complex.put(MINIMUM, mandatoryDecimal);
        complex.put(BIAS, biasDecimal);
        complex.put(SKEW, mandatoryDecimal);
        final Map<String, Definition> offset = new HashMap<>();
        offset.put(PERIOD, mandatoryDecimal);
        final Map<String, Definition> divert = new HashMap<>();
        divert.put(NEXT, multiWords);
        // Define vocabulary definitions for each function
        functions.put(UNIFORM, bounds);
        functions.put(CONSTANT, offset);
        functions.put(GAUSSIAN, bounds);
        functions.put(SKEWED, complex);
        // Add these only to generators and not to balancers
        for (final Map<String, Definition> generator : functions.values())
        {
            generator.put(NEXT, optionalWords);
            generator.put(SOURCE, optionalWords);
        }
        // These are for balancers
        functions.put(ROUNDROBIN, divert);
        functions.put(SMART, divert);
        functions.put(RANDOM, divert);
        final List<String> components = new ArrayList<>();
        components.add(COMPONENT);
        final List<String> subcomponents = new ArrayList<>();
        subcomponents.add(FUNCTION);
        DEFINITIONS = (Map<String, Map<String, Definition>>[]) new Map[2];
        DEFINITIONS[0] = blocks;
        DEFINITIONS[1] = functions;
        DECLARATIONS = (List<String>[]) new List[2];
        DECLARATIONS[0] = components;
        DECLARATIONS[1] = subcomponents;
    }

    /**
     * 
     * @param key
     * @param depth
     * @return 
     */
    public static boolean containsDefinition(final String key,
            final int depth)
    {
        return DEFINITIONS[depth].containsKey(key);
    }

    /**
     * 
     * @param key
     * @param depth
     * @return 
     */
    public static Map<String, Definition> get(final String key,
            final int depth)
    {
        return DEFINITIONS[depth].get(key);
    }

    /**
     * 
     * @param key
     * @param depth
     * @return 
     */
    public static boolean containsDeclaration(final String key,
            final int depth)
    {
        return DECLARATIONS[depth].contains(key);
    }
}
