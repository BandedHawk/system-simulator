/*
 * DummyComponent.java
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
 * Created on November 30, 2017
 */
package org.amity.simulator.elements;

import java.util.List;
import java.util.Map;

/**
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public class DummyComponent implements IComponent
{
    private final double available;
    private final String label;

    private DummyComponent()
    {
        this.available = 0;
        this.label = null;
    }

    public DummyComponent(final String label, final double available)
    {
        this.label = label;
        this.available = available;
    }

    @Override
    public List<Event> getLocalEvents()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void reset()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Event simulate(Event event)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getLabel()
    {
        return this.label;
    }

    @Override
    public Map<String, List<IFunction>> getReferences()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Integer> getDepths()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void generateStatistics(Monitor monitor)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String description()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getAvailable()
    {
        return this.available;
    }
    
}