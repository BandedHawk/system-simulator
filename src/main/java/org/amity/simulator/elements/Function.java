/*
 * Function.java
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
package org.amity.simulator.elements;

/**
 * Common interface for generators and distributors
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public interface Function
{

    /**
     * Returns information on the configuration and type of generator
     * 
     * @return information on generator
     */
    String characteristics();    
}
