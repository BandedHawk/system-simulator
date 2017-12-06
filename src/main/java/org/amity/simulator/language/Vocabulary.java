/*
 * Vocabulary.java
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
    public final static String BALANCER = "balancer";
    public final static String NEXT = "next";
    public final static String NAME = "name";
    public final static String COMPONENT = "component";

    static
    {
        TYPE_PATTERN = Pattern.compile("^\\s*[a-zA-Z][\\s\\w]*[\\-]*[\\s\\w]*$");
        final Map<String, Map<String, Definition>> blocks = new HashMap<>();
        final Map<String, Definition> component = new HashMap<>();
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
        final Definition biasDecimal =
                new Definition(decimal, true, false);
        final Definition multiWords = new Definition(words, true, true);
        balancer.put(Vocabulary.NAME, mandatoryWords);
        balancer.put(Vocabulary.MONITOR, monitor);
        component.put(Vocabulary.NAME, mandatoryWords);
        component.put(Vocabulary.MONITOR, monitor);
        blocks.put(Vocabulary.BALANCER, balancer);
        blocks.put(Vocabulary.SOURCE, component);
        blocks.put(Vocabulary.PROCESSOR, component);
        final Map<String, Map<String, Definition>> functions = new HashMap<>();
        final Map<String, Definition> bounds = new HashMap<>();
        bounds.put(Vocabulary.MAXIMUM, mandatoryDecimal);
        bounds.put(Vocabulary.MINIMUM, mandatoryDecimal);
        final Map<String, Definition> complex = new HashMap<>();
        complex.put(Vocabulary.MAXIMUM, mandatoryDecimal);
        complex.put(Vocabulary.MINIMUM, mandatoryDecimal);
        complex.put(Vocabulary.BIAS, biasDecimal);
        complex.put(Vocabulary.SKEW, mandatoryDecimal);
        final Map<String, Definition> offset = new HashMap<>();
        offset.put(Vocabulary.PERIOD, mandatoryDecimal);
        final Map<String, Definition> divert = new HashMap<>();
        divert.put(Vocabulary.NEXT, multiWords);
        // Define vocabulary definitions for each function
        functions.put(Vocabulary.UNIFORM, bounds);
        functions.put(Vocabulary.CONSTANT, offset);
        functions.put(Vocabulary.GAUSSIAN, bounds);
        functions.put(Vocabulary.SKEWED, complex);
        // Add these only to generators and not to balancers
        for (final Map<String, Definition> generator : functions.values())
        {
            generator.put(Vocabulary.NEXT, optionalWords);
            generator.put(Vocabulary.SOURCE, optionalWords);
        }
        // These are for balancers
        functions.put(Vocabulary.ROUNDROBIN, divert);
        functions.put(Vocabulary.SMART, divert);
        functions.put(Vocabulary.RANDOM, divert);
        final List<String> components = new ArrayList<>();
        components.add(Vocabulary.COMPONENT);
        final List<String> subcomponents = new ArrayList<>();
        subcomponents.add(Vocabulary.FUNCTION);
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
        return Vocabulary.DEFINITIONS[depth].containsKey(key);
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
        return Vocabulary.DEFINITIONS[depth].get(key);
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
        return Vocabulary.DECLARATIONS[depth].contains(key);
    }
}
