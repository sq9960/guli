package com.atguigu.guli.service.oss.service;

import java.io.InputStream;

/**
 * The interface File service.
 */
public interface FileService {
    /**
     * Upload string.
     *
     * @param inputStream      the input stream
     * @param module           the module
     * @param OriginalFilename the original filename
     * @return the string
     */
    String upload(InputStream inputStream, String module, String OriginalFilename);

    /**
     * Remove file.
     *
     * @param url the url
     */
    void removeFile(String url);
}
