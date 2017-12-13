/*
 * ScratchPad.java
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
 * Created on November 20, 2017
 */
package org.amity.simulator.language;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.amity.simulator.distributors.Distributor;
import org.amity.simulator.elements.Component;
import org.amity.simulator.generators.Generator;

/**
 * Scratch area for compilation tracking
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
class ScratchPad
{

    public final int depth;
    public String label;
    public Token labelToken;
    private boolean ok;
    private final Map<String, List<Pair>> pairs;
    public final Map<String, Component> sources;
    public final Map<String, Component> components;
    public final List<Generator> generators;
    public final List<Distributor> distributors;
    private final List<String> errors;

    /**
     * Default constructor - not used
     */
    private ScratchPad()
    {
        this.depth = 0;
        this.label = null;
        this.labelToken = null;
        this.ok = true;
        this.pairs = new HashMap<>();
        this.sources = new HashMap<>();
        this.components = new HashMap<>();
        this.generators = new ArrayList<>();
        this.distributors = new ArrayList<>();
        this.errors = new ArrayList<>();
    }

    /**
     * Constructor that notes the nesting for the processing
     *
     * @param depth level of nesting
     */
    public ScratchPad(final int depth)
    {
        this.depth = depth;
        this.label = null;
        this.labelToken = null;
        this.ok = true;
        this.pairs = new HashMap<>();
        this.sources = new HashMap<>();
        this.components = new HashMap<>();
        this.generators = new ArrayList<>();
        this.distributors = new ArrayList<>();
        this.errors = new ArrayList<>();
    }

    /**
     * 
     * @param error 
     */
    public void addError(final String error)
    {
        this.errors.add(error);
        this.ok = false;
    }

    /**
     * 
     * @param errors 
     */
    public void addErrors(final List<String> errors)
    {
        if (errors != null && !errors.isEmpty())
        {
            this.errors.addAll(errors);
            this.ok = false;
        }
    }

    /**
     * 
     * @return 
     */
    public List<String> errors()
    {
        return this.errors;
    }

    /**
     * 
     * @return 
     */
    public boolean ok()
    {
        return this.ok;
    }

    /**
     * 
     */
    public void clear()
    {
        this.pairs.clear();
    }

    /**
     * 
     */
    public void clearErrors()
    {
        this.ok = true;
        this.errors.clear();
    }

    /**
     * 
     */
    public void reset()
    {
        this.label = null;
        this.labelToken = null;
        this.pairs.clear();
        this.sources.clear();
        this.distributors.clear();
        this.generators.clear();
        this.components.clear();
        this.ok = true;
        this.errors.clear();
    }

    /**
     * 
     * @param name
     * @return 
     */
    public boolean containsName(final String name)
    {
        return this.pairs.containsKey(name);
    }

    /**
     * 
     * @param name
     * @return 
     */
    public List<Pair> value(final String name)
    {
        return this.pairs.get(name);
    }

    /**
     * 
     * @return 
     */
    public Set<String> values()
    {
        return pairs.keySet();
    }

    public void put(final Token name, final Token value)
    {
        final String label = name.getValue();
        final Pair pair = new Pair(name, value);
        final List<Pair> list
                = this.pairs.containsKey(label)
                ? this.pairs.get(label)
                : new ArrayList<>();
        list.add(pair);
        pairs.putIfAbsent(label, list);
    }
}
