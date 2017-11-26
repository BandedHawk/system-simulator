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
import org.amity.simulator.elements.IComponent;
import org.amity.simulator.generators.IGenerator;

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
    private final Map<String, Token> names;
    private final Map<String, Token> values;
    public final Map<String, IComponent> sources;
    public final Map<String, IComponent> components;
    public final List<IGenerator> generators;
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
        this.names = new HashMap<>();
        this.values = new HashMap<>();
        this.sources = new HashMap<>();
        this.components = new HashMap<>();
        this.generators = new ArrayList<>();
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
        this.names = new HashMap<>();
        this.values = new HashMap<>();
        this.sources = new HashMap<>();
        this.components = new HashMap<>();
        this.generators = new ArrayList<>();
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

    public void clear()
    {
        this.names.clear();
        this.values.clear();
    }

    public void clearErrors()
    {
        this.ok = true;
        this.errors.clear();
    }

    public void reset()
    {
        this.label = null;
        this.labelToken = null;
        this.names.clear();
        this.values.clear();
        this.sources.clear();
        this.generators.clear();
        this.components.clear();
        this.ok = true;
        this.errors.clear();
    }

    public boolean containsName(final String name)
    {
        return this.names.containsKey(name);
    }

    public String value(final String name)
    {
        return this.values.get(name).getValue();
    }

    public Set<String> values()
    {
        return this.values.keySet();
    }

    public String location(final String name)
    {
        final String line = Integer.toString(this.values.get(name).getLine());
        StringBuilder error = new StringBuilder(line);
        error.append(", ").append(this.values.get(name).getPosition());
        return error.toString();
    }

    public void put(final Token name, final Token value)
    {
        this.names.put(name.getValue(), name);
        this.values.put(name.getValue(), value);
    }
}
