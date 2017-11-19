/*
 * StateMachine.java
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
 * Created on November 13, 2017
 */
package org.amity.simulator.language;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Defines valid state traversals for the parsing of the system definition file
 * for modeling.
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public class StateMachine
{

    private final static Map<Syntax, List<Syntax>> MACHINE;

    static
    {
        MACHINE = new HashMap<>();
        for (Syntax grammar : Syntax.values())
        {
            final List<Syntax> options = new ArrayList<>();
            switch (grammar)
            {
                case START:
                    options.add(Syntax.LABEL);
                    options.add(Syntax.COMMENT);
                    MACHINE.put(grammar, options);
                    break;
                case OPEN:
                    options.add(Syntax.CLOSE);
                    options.add(Syntax.LABEL);
                    options.add(Syntax.COMMENT);
                    MACHINE.put(grammar, options);
                    break;
                case CLOSE:
                    options.add(Syntax.LABEL);
                    options.add(Syntax.COMMENT);
                    MACHINE.put(grammar, options);
                    break;
                case LABEL:
                    options.add(Syntax.ASSIGN);
                    options.add(Syntax.OPEN);
                    options.add(Syntax.COMMENT);
                    MACHINE.put(grammar, options);
                    break;
                case ASSIGN:
                    options.add(Syntax.VALUE);
                    options.add(Syntax.COMMENT);
                    MACHINE.put(grammar, options);
                    break;
                case VALUE:
                    options.add(Syntax.LABEL);
                    options.add(Syntax.CLOSE);
                    options.add(Syntax.COMMENT);
                    MACHINE.put(grammar, options);
                    break;
                default:
            }
        }
    }

    public static List<Syntax> getOptions(Syntax state)
    {
        return MACHINE.get(state);
    }
}
