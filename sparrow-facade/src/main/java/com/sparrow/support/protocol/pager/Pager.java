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

/**
 * @author harry
 */
public class Pager {
    public Pager() {
    }

    /**
     * 页码数
     */
    protected Integer pageSizeOfPage = 5;
    /**
     * 页尺寸
     */
    protected Integer pageSize;
    /**
     * 分页格式化字符
     */
    protected String pageFormat;
    /**
     * 当前页
     */
    protected Integer currentPageIndex;
    /**
     * 首页字符
     */
    protected String indexPageFormat;
    /**
     * 简化
     */
    protected boolean simple;

    public Integer getPageSizeOfPage() {
        return pageSizeOfPage;
    }

    public void setPageSizeOfPage(Integer pageSizeOfPage) {
        this.pageSizeOfPage = pageSizeOfPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getPageFormat() {
        return pageFormat;
    }

    public void setPageFormat(String pageFormat) {
        this.pageFormat = pageFormat;
    }

    public Integer getCurrentPageIndex() {
        if (currentPageIndex == null) {
            currentPageIndex = 1;
        }
        return currentPageIndex;
    }

    public void setCurrentPageIndex(Integer currentPageIndex) {
        this.currentPageIndex = currentPageIndex;
    }

    public String getIndexPageFormat() {
        return indexPageFormat;
    }

    public void setIndexPageFormat(String indexPageFormat) {
        this.indexPageFormat = indexPageFormat;
    }

    public boolean isSimple() {
        return simple;
    }

    public void setSimple(boolean simple) {
        this.simple = simple;
    }
}
