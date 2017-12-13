/*
 * Generator.java
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
 * Created on November 7, 2017
 */
package org.amity.simulator.generators;

import org.amity.simulator.elements.Component;
import org.amity.simulator.elements.Function;

/**
 * Interface for the distribution model that generates values in the
 * system simulation
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public interface Generator extends Function
{

    /**
     * Returns a list of timing values, that depends on the generation model
     * being used - currently supports uniform random distribution, Gaussian
     * distribution or constant value.
     *
     * @return value based on the generation model
     */
    double generate();

    /**
     * 
     * @return name of associated event source
     */
    String getSource();

    /**
     * 
     * @return name of downstream component
     */
    String getReference();

    /**
     * 
     * @param next downstream component for this generator
     */
    void setNext(Component next);

    /**
     * 
     * @return downstream component for this generator
     */
    Component getNext();
}
