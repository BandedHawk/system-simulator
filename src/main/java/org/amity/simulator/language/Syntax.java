/*
 * Syntax.java
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

import java.util.regex.Pattern;

/**
 * Defines parts of the grammar for defining system elements
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public enum Syntax
{
    START(0),
    OPEN(1),
    CLOSE(2),
    LABEL(3),
    ASSIGN(4),
    VALUE(5),
    COMMENT(6),
    END(7);

    private final int id;
    private final Pattern pattern;

    Syntax(final int id)
    {
        this.id = id;
        switch (id)
        {
            case 1:
                this.pattern = Pattern.compile("^\\s*\\{");
                break;
            case 2:
                this.pattern = Pattern.compile("^\\s*\\}");
                break;
            case 3:
                this.pattern = Pattern.compile("^\\s*[a-zA-Z]+");
                break;
            case 4:
                this.pattern = Pattern.compile("^\\s*[:=]");
                break;
            case 5:
                this.pattern = 
                        Pattern.compile("^\\s*[a-zA-Z][\\s\\w]*$|^\\s*[\\+\\-]?\\d*\\.?\\d+\\s*$");
                break;
            case 6:
                this.pattern = Pattern.compile("^\\s*//.*$");
                break;
            default:
                this.pattern = Pattern.compile("");
        }
    }

    public int getId()
    {
        return this.id;
    }

    public Pattern getPattern()
    {
        return this.pattern;
    }
}
