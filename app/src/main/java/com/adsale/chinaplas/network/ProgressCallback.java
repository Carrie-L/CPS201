package com.adsale.chinaplas.network;

public interface ProgressCallback {
    /**
     * @param progress 已经下载或上传字节数
     * @param total    总字节数
     * @param done     是否完成
     */
    <T> void onProgress(long progress, long total, boolean done, T entity);
}