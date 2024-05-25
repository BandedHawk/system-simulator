/*
 * (C) Copyright 2024 Jon Barnett.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Created on May 24, 2024
 */
package org.amity.simulator.data;

/**
 * Implements data store for queue information
 *
 * @author <a href="mailto:jonb@ieee.org">Jon Barnett</a>
 */
public class QueueStatistics
{
    private int depth;
    private double time;
    private double span;

    public QueueStatistics()
    {
        this.depth = 0;
        this.time = 0.0;
        this.span = 0.0;
    }

    public QueueStatistics(final int depth, final double time, final double span)
    {
        this.depth = depth;
        this.time = time;
        this.span = span;
    }

    public void setDepth(final int depth)
    {
        this.depth = depth;
    }

    public void setTime(final double time)
    {
        this.time = time;
    }

    public void setSpan(final double span)
    {
        this.span = span;
    }

    public int getDepth()
    {
        return this.depth;
    }

    public double getTime()
    {
        return this.time;
    }

    public double getSpan()
    {
        return this.span;
    }
}
