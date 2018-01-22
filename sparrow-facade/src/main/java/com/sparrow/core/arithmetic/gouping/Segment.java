/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sparrow.core.arithmetic.gouping;

/**
 * @author by harry
 */
public class Segment {
    private Point start;
    private Point end;

    public Segment(Comparable start, Comparable end) {
        if (end.compareTo(start) < 0) {
            throw new IllegalArgumentException("this.end<this.start");
        }
        this.start = new Point(start);
        this.end = new Point(end);
    }

    public Segment(Point start, Point end) {
        if (end.getPoint().compareTo(start.getPoint()) < 0) {
            throw new IllegalArgumentException("this.end<this.start");
        }
        this.start = start;
        this.end = end;
    }

    public Point getStart() {
        return start;
    }

    public void setStart(Point start) {
        this.start = start;
    }

    public Point getEnd() {
        return end;
    }

    public void setEnd(Point end) {
        this.end = end;
    }

    public Segment intersection(Segment segment) {
        if (segment.getEnd().getPoint().compareTo(segment.getStart().getPoint()) < 0) {
            throw new IllegalArgumentException("segment.end < segment.start");
        }
        //取大的起始节点
        Point start=this.start.getPoint().compareTo(segment.start.getPoint())>0?this.start:segment.start;
        //取小的截止节点
        Point end=this.end.getPoint().compareTo(segment.end.getPoint())<0?this.end:segment.end;
        if(start.getPoint().compareTo(end.getPoint())<=0){
            return new Segment(start,end);
        }
        return null;
    }

    @Override
    public String toString() {
        return "Segment{" +
                "start=" + start.getPoint() +
                ", end=" + end.getPoint() +
                '}';
    }
}
