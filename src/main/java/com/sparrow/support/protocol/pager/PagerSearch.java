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

package com.sparrow.support.protocol.pager;

import com.sparrow.constant.PAGER;
import com.sparrow.constant.magic.DIGIT;

/**
 * @author harry
 */
public class PagerSearch extends Pager {
    public PagerSearch() {
    }

    public PagerSearch(Integer currentPageIndex, Integer pageSize, String pageFormat, String indexPageFormat,
        boolean simple) {
        this.pageSize = pageSize;
        this.pageFormat = pageFormat;
        if (currentPageIndex == null) {
            this.currentPageIndex = 1;
        } else {
            this.currentPageIndex = currentPageIndex;
        }
        this.indexPageFormat = indexPageFormat;
        this.simple = simple;
    }

    public PagerSearch(String pageFormat) {
        this(1, DIGIT.ALL, PAGER.ACTION_PAGE_FORMAT, pageFormat, false);
    }

    public PagerSearch(Integer currentPageIndex) {
        this(currentPageIndex, DIGIT.ALL, PAGER.ACTION_PAGE_FORMAT, PAGER.ACTION_PAGE_FORMAT, false);
    }

    public PagerSearch(Integer currentPageIndex, Integer pageSize) {
        this(currentPageIndex, pageSize, PAGER.ACTION_PAGE_FORMAT, PAGER.ACTION_PAGE_FORMAT, false);
    }

    public PagerSearch(Integer currentPageIndex, Integer pageSize, boolean simple) {
        this(currentPageIndex, pageSize, PAGER.ACTION_PAGE_FORMAT, PAGER.ACTION_PAGE_FORMAT, simple);
    }

    public PagerSearch(Integer currentPageIndex, Integer pageSize, String pageFormat) {
        this(currentPageIndex, pageSize, pageFormat, PAGER.ACTION_PAGE_FORMAT, false);
    }

    public String getLimitClause() {
        Integer pageIndex = this.getCurrentPageIndex() == 0 ? 0 : this.getCurrentPageIndex() - 1;
        return " limit " + (pageIndex * this.getPageSize()) + "," + this.getPageSize();
    }
}
