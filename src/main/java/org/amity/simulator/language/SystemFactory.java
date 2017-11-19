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
 * Created on November 18, 2017
 */
package org.amity.simulator.language;

import java.util.Map;
import org.amity.simulator.elements.IComponent;
import org.amity.simulator.elements.Processor;
import org.amity.simulator.elements.Source;
import org.amity.simulator.generators.IGenerator;

/**
 * Declares methods for producing system simulation component instances
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public class SystemFactory
{

    /**
     * 
     * @param pairs
     * @param function
     * @return 
     */
    public final static IComponent getSource(final Map<String, String> pairs,
            IGenerator function)
    {
        String label = null;
        String nextReference = null;
        for (final String parameter : pairs.keySet())
        {
            switch (parameter)
            {
                case Vocabulary.NAME:
                    label = pairs.get(label);
                    break;
                case Vocabulary.NEXT:
                    nextReference = pairs.get(label);
                default:
                    break;
            }
        }
        final IComponent source = new Source(label, function, nextReference);
        return source;
    }

    /**
     * 
     * @param pairs
     * @param function
     * @return 
     */
    public final static IComponent getProcessor(final Map<String, String> pairs,
            IGenerator function)
    {
        String label = null;
        String nextReference = null;
        for (final String parameter : pairs.keySet())
        {
            switch (parameter)
            {
                case Vocabulary.NAME:
                    label = pairs.get(label);
                    break;
                case Vocabulary.NEXT:
                    nextReference = pairs.get(label);
                default:
                    break;
            }
        }
        final IComponent processor
                = new Processor(label, function, nextReference);
        return processor;
    }
}
