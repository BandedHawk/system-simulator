/*
 * IComponent.java
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

/**
 * Interface of a system component in the simulation
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public interface IComponent
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
     * @return entity with modified timing data due to component interaction
     */
    Event simulate(final Event event);
}
