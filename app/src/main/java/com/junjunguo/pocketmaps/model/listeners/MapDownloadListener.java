package com.junjunguo.pocketmaps.model.listeners;
/**
 * Created by Google Deveoper Student Club
 * Varshit Ratna(leader)
 * Devaraj Akhil(Core team)
 */

public interface MapDownloadListener {
    /**
     * a download is started
     */
    void downloadStart();

    /**
     * a download activity is finished
     */
    void downloadFinished(String mapName);
    
    void onStartUnpacking();

    void progressUpdate(Integer value);
}
