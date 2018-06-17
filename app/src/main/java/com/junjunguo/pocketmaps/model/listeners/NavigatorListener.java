package com.junjunguo.pocketmaps.model.listeners;

/**
 * Created by Google Deveoper Student Club
 * Varshit Ratna(leader)
 * Devaraj Akhil(Core team)
 */

public interface NavigatorListener {
    /**
     * the change on navigator: navigation is used or not
     *
     * @param on
     */
    void onStatusChanged(boolean on);
    
    void onNaviStart(boolean on);
}
