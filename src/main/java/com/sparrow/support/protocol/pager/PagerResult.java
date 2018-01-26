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
import com.sparrow.constant.magic.ESCAPED;
import com.sparrow.support.protocol.VO;
import com.sparrow.utility.StringUtility;

import java.util.List;

/**
 * @author harry
 */
public class PagerResult<T> extends Pager implements VO {

    public PagerResult(Pager pager, Long recordCount) {
        this.currentPageIndex = pager.currentPageIndex;
        this.recordCount = recordCount;
        this.pageSize = pager.pageSize;
        this.indexPageFormat = pager.indexPageFormat;
        this.pageFormat = pager.pageFormat;
        this.pageSizeOfPage = pager.pageSizeOfPage;
    }

    private Long recordCount;

    private List<T> list;

    public Long getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(Long recordCount) {
        this.recordCount = recordCount;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    /**
     * 根据总记录数和每页记录数获取最后页码
     *
     * @return
     */
    public Integer getLastPageIndex() {
        return (int) Math.ceil((double) this.recordCount / this.pageSize);
    }

    public boolean more() {
        return getCurrentPageIndex() < getLastPageIndex();
    }

    /**
     * 获取分页字符串
     *
     * @return
     */
    public String html() {
        if (this.recordCount <= this.pageSize) {
            return "<input type=\"hidden\" id=\"currentPageIndex\" name=\"currentPageIndex\" value=\"1\"/>\n";
        }
        String pageNumberStyle = " class=\"num\"";
        String pageFirstStyle = "class=\"first\"";
        String pageFirstDisableStyle = "class=\"disable first\"";
        String disablePageNumStyle = "class=\"disable\"";
        StringBuilder pageString = new StringBuilder();
        pageString.append("<div id=\"divPage\" class=\"page\">\n");
        boolean isConvertHTML = this.pageFormat.endsWith(".html");
        if (!isConvertHTML) {
            pageString
                .append("<input type=\"hidden\" id=\"currentPageIndex\" name=\"currentPageIndex\" value=\" ");
            pageString.append(this.currentPageIndex);
            pageString.append(" \"/>\n ");
        }
        Integer pageCount = (int) Math.ceil(this.recordCount / (double) this.pageSize);
        if (this.currentPageIndex != 1) {
            if (StringUtility.isNullOrEmpty(this.indexPageFormat)) {
                pageString.append("<a " + pageFirstStyle + " href=\""
                    + this.pageFormat.replace(PAGER.PAGE_INDEX, "1")
                    + "\">首页</a>\n");
            } else {
                pageString.append("<a " + pageFirstStyle + " href=\""
                    + this.indexPageFormat.replace(PAGER.PAGE_INDEX, "1")
                    + "\">首页</a>\n");
            }
            if (!StringUtility.isNullOrEmpty(this.indexPageFormat)
                && this.currentPageIndex - 1 == 1) {
                pageString.append("<a "
                    + pageNumberStyle
                    + " href=\""
                    + this.indexPageFormat.replace(PAGER.PAGE_INDEX,
                    String.valueOf(this.currentPageIndex - 1)));
                pageString.append("\">上一页");
                pageString.append("</a>\n");
            } else {
                pageString.append("<a "
                    + pageNumberStyle
                    + " href=\""
                    + this.pageFormat.replace(PAGER.PAGE_INDEX,
                    String.valueOf(this.currentPageIndex - 1)));
                pageString.append("\">上一页");
                pageString.append("</a>\n");
            }
        } else {
            pageString.append("<a " + pageFirstDisableStyle + ">首页</a>\n<a "
                + disablePageNumStyle + ">上一页</a>\n");
        }
        Integer beginPageIndex = this.currentPageIndex - (pageSizeOfPage - 1);
        if (this.currentPageIndex % pageSizeOfPage != 0) {
            beginPageIndex = this.currentPageIndex - this.currentPageIndex
                % pageSizeOfPage + 1;
        }

        //当前只显示5个页码
        Integer endPageIndex = beginPageIndex + 4;
        for (Integer i = beginPageIndex; i <= endPageIndex; i++) {
            if (i > pageCount) {
                break;
            }
            if (i.equals(this.currentPageIndex)) {
                pageString.append("<a style='color:red;font-weight:bold;'>");
                pageString.append(i);
                pageString.append("</a>\n");
            } else if (i == 1 && !StringUtility.isNullOrEmpty(this.indexPageFormat)) {
                pageString.append("<a " + pageNumberStyle + " href=\""
                    + this.indexPageFormat.replace(PAGER.PAGE_INDEX, "1")
                    + "\">" + i + "</a>\n");
            } else {
                pageString.append(" <a "
                    + pageNumberStyle
                    + " href=\""
                    + this.pageFormat.replace(PAGER.PAGE_INDEX,
                    String.valueOf(i)) + "\">");
                pageString.append(i);
                pageString.append("</a>\n");
            }
        }

        if (this.currentPageIndex < pageCount) {
            pageString.append("<a "
                + pageNumberStyle
                + " href=\""
                + this.pageFormat.replace(PAGER.PAGE_INDEX,
                String.valueOf(this.currentPageIndex + 1)));
            pageString.append("\">下一页");
            pageString.append("</a>\n");
            pageString.append("<a "
                + pageNumberStyle
                + " href=\""
                + this.pageFormat.replace(PAGER.PAGE_INDEX,
                String.valueOf(pageCount)) + "\">末页</a>\n");
        } else {
            pageString.append("<a " + disablePageNumStyle + ">下一页</a>\n<a "
                + disablePageNumStyle + "\">末页</a>\n");
        }
        if (!this.simple) {
            pageString.append("<input id=\"defPageIndex\" onmouseover=\"this.select();\" onkeyup=\"this.value=this.value.replace(/\\D/g,'');\"  onafterpaste=\"this.value=this.value.replace(/\\D/g,'');\""
                + "onblur=\"if(this.value.trim()==''){this.value=parseInt($('currentPageIndex').value)+1;}\" value=\""
                + (this.currentPageIndex + 1)
                + "\" type=\"text\" />\n");
            pageString.append("<a id=\"go\" onclick=\"page.toTargetPage(");
            pageString.append(pageCount);
            pageString.append(",'");
            pageString.append(this.pageFormat);
            pageString.append("',this);\" style='font-weight:bold'>GO</a>\n");
            pageString.append("<span style='color:#5f5f60'>\n");
            pageString.append("<a style='display:none;' id='spanCurrentPageIndex'>\n");
            pageString.append(this.currentPageIndex);
            pageString.append("</span>共<span>\n");
            pageString.append(pageCount);
            pageString.append("</span>页");
            pageString.append(ESCAPED.EM_SPACE);
            pageString.append("每页<span id='rowCountPerPage'>");
            pageString.append(this.pageSize);
            pageString.append("</span>条");
            pageString.append(ESCAPED.EM_SPACE);
            pageString.append("共<span id='sumOfRecord'>");
            pageString.append(this.recordCount);
            pageString.append("</span>条");
            pageString.append("</a>");
        }
        pageString.append("</div>");
        return pageString.toString();
    }
}
