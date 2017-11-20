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
    public final static Map<String, Map<String, Definition>>[] DEFINITIONS;
    public final static List<String>[] DECLARATIONS;
    public final static String TYPE_NAME = "type";
    public final static Pattern TYPE_PATTERN;
    public final static String MAXIMUM = "maximum";
    public final static String MINIMUM = "minimum";
    public final static String OFFSET = "offset";
    public final static String UNIFORM = "uniform";
    public final static String CONSTANT = "constant";
    public final static String GAUSSIAN = "gaussian";
    public final static String FUNCTION = "function";
    public final static String SOURCE = "source";
    public final static String PROCESSOR = "processor";
    public final static String NEXT = "next";
    public final static String NAME = "name";
    public final static String COMPONENT = "component";

    static
    {
        TYPE_PATTERN = Pattern.compile("^\\s*[a-zA-Z][\\s|\\w]*$");
        final Map<String, Map<String, Definition>> blocks = new HashMap<>();
        final Map<String, Definition> component = new HashMap<>();
        final Pattern words = Pattern.compile("^\\s*[a-zA-Z][\\s|\\w]*$");
        final Pattern number = Pattern.compile("^\\d+$");
        final Definition name = new Definition(words, true);
        final Definition next = new Definition(words, false);
        final Definition mandatoryNumber = new Definition(number, true);
        component.put(Vocabulary.NAME, name);
        component.put(Vocabulary.NEXT, next);
        blocks.put(Vocabulary.SOURCE, component);
        blocks.put(Vocabulary.PROCESSOR, component);
        final Map<String, Map<String, Definition>> functions = new HashMap<>();
        final Map<String, Definition> bounds = new HashMap<>();
        bounds.put(Vocabulary.MAXIMUM, mandatoryNumber);
        bounds.put(Vocabulary.MINIMUM, mandatoryNumber);
        final Map<String, Definition> offset = new HashMap<>();
        offset.put(Vocabulary.OFFSET, mandatoryNumber);
        functions.put(Vocabulary.UNIFORM, bounds);
        functions.put(Vocabulary.CONSTANT, offset);
        functions.put(Vocabulary.GAUSSIAN, bounds);
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
}
