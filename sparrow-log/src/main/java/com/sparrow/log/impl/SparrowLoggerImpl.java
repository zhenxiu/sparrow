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

package com.sparrow.log.impl;

import com.sparrow.constant.CACHE_KEY;
import com.sparrow.constant.CONFIG;
import com.sparrow.constant.CONSTANT;
import com.sparrow.constant.DATE_TIME;
import com.sparrow.constant.magic.SYMBOL;
import com.sparrow.core.Cache;
import com.sparrow.enums.LOG_LEVEL;
import com.sparrow.utility.DateTimeUtility;
import com.sparrow.utility.StringUtility;
import org.slf4j.Logger;
import org.slf4j.Marker;

import java.io.*;
import java.nio.channels.FileLock;

/**
 * @author harry
 */
public class SparrowLoggerImpl implements Logger {
    private String className;

    public SparrowLoggerImpl() {
    }

    public void setClazz(String clazz) {
        this.className = clazz;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean isTraceEnabled() {
        return false;
    }

    @Override
    public void trace(String msg) {
        this.writeLog(msg, LOG_LEVEL.TRACE);
    }

    @Override
    public void trace(String format, Object arg) {
        this.writeLog(StringUtility.format(format, arg), LOG_LEVEL.TRACE);
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        this.writeLog(StringUtility.format(format, arg1, arg2), LOG_LEVEL.TRACE);
    }

    @Override
    public void trace(String format, Object... arguments) {
        this.writeLog(StringUtility.format(format, arguments), LOG_LEVEL.TRACE);
    }

    @Override
    public void trace(String msg, Throwable t) {
        this.writeLog(StringUtility.printStackTrace(msg,t), LOG_LEVEL.TRACE);
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return false;
    }

    @Override
    public void trace(Marker marker, String msg) {
        throw new UnsupportedOperationException("sparrow logger unsupport operation");

    }

    @Override
    public void trace(Marker marker, String format, Object arg) {
        throw new UnsupportedOperationException("sparrow logger unsupport operation");
    }

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        throw new UnsupportedOperationException("sparrow logger unsupport operation");
    }

    @Override
    public void trace(Marker marker, String format, Object... argArray) {
        throw new UnsupportedOperationException("sparrow logger unsupport operation");
    }

    @Override
    public void trace(Marker marker, String msg, Throwable t) {
        throw new UnsupportedOperationException("sparrow logger unsupport operation");
    }

    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    @Override
    public void debug(String log) {
        this.writeLog(log, LOG_LEVEL.DEBUG);
    }

    @Override
    public void debug(String format, Object arg) {
        this.writeLog(StringUtility.format(format, arg), LOG_LEVEL.DEBUG);
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        this.writeLog(StringUtility.format(format, arg1, arg2), LOG_LEVEL.DEBUG);
    }

    @Override
    public void debug(String format, Object... arguments) {
        this.writeLog(StringUtility.format(format, arguments), LOG_LEVEL.DEBUG);
    }

    @Override
    public void debug(String msg, Throwable t) {
        this.writeLog(StringUtility.printStackTrace(msg,t), LOG_LEVEL.DEBUG);
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return false;
    }

    @Override
    public void debug(Marker marker, String msg) {
        throw new UnsupportedOperationException("sparrow logger unsupport operation");

    }

    @Override
    public void debug(Marker marker, String format, Object arg) {
        throw new UnsupportedOperationException("sparrow logger unsupport operation");

    }

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        throw new UnsupportedOperationException("sparrow logger unsupport operation");

    }

    @Override
    public void debug(Marker marker, String format, Object... arguments) {
        throw new UnsupportedOperationException("sparrow logger unsupport operation");

    }

    @Override
    public void debug(Marker marker, String msg, Throwable t) {
        throw new UnsupportedOperationException("sparrow logger unsupport operation");
    }

    @Override
    public boolean isInfoEnabled() {
        return false;
    }

    @Override
    public void warn(String log) {
        this.writeLog(log, LOG_LEVEL.WARN);
    }

    @Override
    public void warn(String format, Object arg) {
        this.writeLog(StringUtility.format(format, arg), LOG_LEVEL.WARN);
    }

    @Override
    public void warn(String format, Object... arguments) {
        this.writeLog(StringUtility.format(format, arguments), LOG_LEVEL.WARN);
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        this.writeLog(StringUtility.format(format, arg1, arg2), LOG_LEVEL.WARN);
    }

    @Override
    public void warn(String msg, Throwable t) {
        this.writeLog(StringUtility.printStackTrace(msg,t), LOG_LEVEL.WARN);
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return false;
    }

    @Override
    public void warn(Marker marker, String msg) {
        throw new UnsupportedOperationException("sparrow logger unsupport operation");
    }

    @Override
    public void warn(Marker marker, String format, Object arg) {
        throw new UnsupportedOperationException("sparrow logger unsupport operation");
    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        throw new UnsupportedOperationException("sparrow logger unsupport operation");
    }

    @Override
    public void warn(Marker marker, String format, Object... arguments) {
        throw new UnsupportedOperationException("sparrow logger unsupport operation");
    }

    @Override
    public void warn(Marker marker, String msg, Throwable t) {
        throw new UnsupportedOperationException("sparrow logger unsupport operation");
    }

    @Override
    public boolean isErrorEnabled() {
        return false;
    }

    @Override
    public void info(String log) {
        this.writeLog(log, LOG_LEVEL.INFO);
    }

    @Override
    public void info(String format, Object arg) {
        this.writeLog(StringUtility.format(format, arg), LOG_LEVEL.INFO);
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        this.writeLog(StringUtility.format(format, arg1, arg2), LOG_LEVEL.INFO);
    }

    @Override
    public void info(String format, Object... arguments) {
        this.writeLog(StringUtility.format(format, arguments), LOG_LEVEL.INFO);
    }

    @Override
    public void info(String msg, Throwable t) {
        this.writeLog(StringUtility.printStackTrace(msg,t), LOG_LEVEL.INFO);
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return false;
    }

    @Override
    public void info(Marker marker, String msg) {
        throw new UnsupportedOperationException("sparrow logger unsupport operation");
    }

    @Override
    public void info(Marker marker, String format, Object arg) {
        throw new UnsupportedOperationException("sparrow logger unsupport operation");
    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        throw new UnsupportedOperationException("sparrow logger unsupport operation");
    }

    @Override
    public void info(Marker marker, String format, Object... arguments) {
        throw new UnsupportedOperationException("sparrow logger unsupport operation");
    }

    @Override
    public void info(Marker marker, String msg, Throwable t) {
        throw new UnsupportedOperationException("sparrow logger unsupport operation");

    }

    @Override
    public boolean isWarnEnabled() {
        return false;
    }

    @Override
    public void error(String log) {
        this.writeLog(log, LOG_LEVEL.ERROR);
    }

    @Override
    public void error(String format, Object arg) {
        this.writeLog(StringUtility.format(format, arg), LOG_LEVEL.ERROR);
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        this.writeLog(StringUtility.format(format, arg1, arg2), LOG_LEVEL.ERROR);
    }

    @Override
    public void error(String format, Object... arguments) {
        this.writeLog(StringUtility.format(format, arguments), LOG_LEVEL.ERROR);
    }

    public void error(Throwable exception) {
        this.error(null, exception);
    }

    @Override
    public void error(String prefixLog, Throwable exception) {
        this.writeLog(StringUtility.printStackTrace(prefixLog, exception), LOG_LEVEL.ERROR);
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return false;
    }

    @Override
    public void error(Marker marker, String msg) {
        throw new UnsupportedOperationException("sparrow logger unsupport operation");

    }

    @Override
    public void error(Marker marker, String format, Object arg) {
        throw new UnsupportedOperationException("sparrow logger unsupport operation");

    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        throw new UnsupportedOperationException("sparrow logger unsupport operation");

    }

    @Override
    public void error(Marker marker, String format, Object... arguments) {
        throw new UnsupportedOperationException("sparrow logger unsupport operation");

    }

    @Override
    public void error(Marker marker, String msg, Throwable t) {
        throw new UnsupportedOperationException("sparrow logger unsupport operation");
    }

    /**
     * 写日志文件。日志文件被入到WebRoot/errorlog.txt
     *
     * @param str log
     */
    private void writeLog(String str, LOG_LEVEL logLevel) {
        FileOutputStream fileOutputStream = null;
        try {
            int minLevel = Integer.valueOf(Cache.getInstance().get(CACHE_KEY.LOG, CONFIG.LOG_LEVEL).toString());
            String logPrintConsole = Cache.getInstance().get(CACHE_KEY.LOG, CONFIG.LOG_PRINT_CONSOLE).toString();
            if (logLevel.ordinal() < minLevel) {
                return;
            }
            String path = System.getProperty("user.dir") + "/logs";
            File directory = new File(path);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            fileOutputStream = new FileOutputStream(path
                + String.format("/log%1$s.log",
                DateTimeUtility.getFormatCurrentTime(DATE_TIME.FORMAT_YYYYMMDD)),
                true);

            String log = logLevel.toString() + "|"
                + DateTimeUtility.getFormatCurrentTime() + "|"
                + this.className + CONSTANT.ENTER_TEXT +
                "--------------------------------------------------------------" + CONSTANT.ENTER_TEXT +
                str + CONSTANT.ENTER_TEXT;

            //阻塞文件锁
            FileLock fl = fileOutputStream.getChannel().lock();
            if (fl == null) {
                return;
            }
            try {
                fileOutputStream.write(log.getBytes(CONSTANT.CHARSET_UTF_8));
                if (Boolean.TRUE.toString().equalsIgnoreCase(logPrintConsole)) {
                    System.out.println(log);
                }
            } finally {
                fl.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (Exception ignore) {
                }
            }
        }
    }
}
