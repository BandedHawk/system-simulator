/*
 * Distributor.java
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
 * Created on November 26, 2017
 */
package org.amity.simulator.distributors;

import java.util.List;
import org.amity.simulator.elements.Event;
import org.amity.simulator.elements.Sequencer;
import org.amity.simulator.elements.Component;
import org.amity.simulator.elements.Function;

/**
 * Interface for the distributor that sends events to other active components
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public interface Distributor extends Function
{
    int UNKNOWN = -1;

    /**
     * Assigns event to downstream component
     *
     * @param event entity passing into component in chronological order
     * @return entity with modified data due to component interaction
     */
    Event assign(Event event);

    /**
     * Clears the distributor function;
     */
    void reset();

    /**
     * 
     * @return labels for downstream components
     */
    List<String> getReferences();

    /**
     * Adds component as downstream distribution sink
     * 
     * @param component downstream module for balancer
     */
    void addNext(Component component);

    /**
     * Obtain a list of components participating in the balancing
     * 
     * @return list of downstream connections
     */
    Component[] connections();

    /**
     * 
     * @return next available processing of downstream component
     */
    double available();

    /**
     * Find priority definitions for treatment of sources
     * 
     * @param sequencer algorithm and data to prioritize processing
     * @param explore discovery mode if <code>true</code>
     */
    void prioritize(Sequencer sequencer, boolean explore);
}
