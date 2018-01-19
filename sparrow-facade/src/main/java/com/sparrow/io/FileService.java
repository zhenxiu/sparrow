package com.sparrow.io;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by harry on 2017/3/16.
 */
public interface FileService {

    boolean exist(String fullFilePath) throws IOException;

    boolean mkdirs(String fullFilePath) throws IOException;

    OutputStream createOutputStream(String fullFilePath) throws IOException;

    List<String> getFileNameList(String directory) throws IOException;

    List<String> getFileNameList(String directory, String extension) throws IOException;

    boolean isFile(String fullFilePath) throws IOException;

    void move(String fileName, String destDir) throws IOException;

    boolean delete(String fileName) throws IOException;

    void close() throws IOException;

    InputStream getInputStream(String fullFileName) throws IOException;

    Long getFileLength(String filePath) throws IOException;

    void copy(String srcName, String descPath) throws IOException;
}
