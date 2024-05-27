/*
 * Component.java
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
 * Created on November 6, 2017
 */
package org.amity.simulator.elements;

import java.util.List;
import java.util.Map;
import org.amity.simulator.data.QueueStatistics;

/**
 * Interface of a system component in the simulation
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public interface Component
{
    /**
     * Access event information for events that passed through component
     * 
     * @return chronological list of events passing through, with global
     * information
     */
    List<Event> getLocalEvents();

    /**
     * Clear persistent variables in the component model
     */
    void reset();

    /**
     * Process a simulation of events passing through the component model,
     * marking the arrival, start of processing and completion of processing for
     * each event, as well as tracking other details such as total lifespan of
     * the event.
     * 
     * @param event entity passing into component in chronological order
     * @return entity with modified data due to component interaction
     */
    Event simulate(final Event event);

    /**
     * 
     * @return name of component
     */
    String getLabel();

    /**
     * 
     * @return list of named references and associated generators
     */
    Map<String, List<Function>> getReferences();

    /**
     * 
     * @return queue information
     */
    List<QueueStatistics> getQueueStatistics();

    /**
     * Produce display statistics on component
     * 
     * @param monitor plug-in to generate monitoring information
     */
    void generateStatistics(Monitor monitor);

    /**
     * Produces characteristics of function
     * 
     * @return description of function
     */
    String description();

    /**
     * 
     * @return next time component can process something
     */
    double getAvailable();

    /**
     * Find priority parameters for an active component
     * 
     * @param sequencer information to locate a priority event
     * @param explore discovery mode if <code>true</code>
     */
    void prioritize(Sequencer sequencer, boolean explore);
}
