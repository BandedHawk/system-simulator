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
package org.amity.component;

import java.util.List;
import org.amity.element.Event;

/**
 * Definition of a system component for simulation
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public interface IComponent
{
    List<Event> getLocalEvents();

    /**
     * Process a simulation of events passing through the component model,
     * marking the arrival, start of processing and completion of processing for
     * the event, as well as tracking other details such as total lifespan of
     * the event.
     * 
     * @param events list of events traveling through the system model
     */
    void simulate(final List<Event> events);
}
