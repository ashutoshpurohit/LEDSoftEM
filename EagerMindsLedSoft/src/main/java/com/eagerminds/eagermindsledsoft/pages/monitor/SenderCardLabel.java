/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerminds.eagermindsledsoft.pages.monitor;

import javafx.scene.control.Label;
import novj.platform.vxkit.common.bean.monitor.SenderMonitorInfo;

/**
 *
 * @author user
 */
public class SenderCardLabel extends Label {
    private SenderMonitorInfo senderMonitorInfo;
    private boolean isSelected=false;

   
    public SenderMonitorInfo getSenderMonitorInfo() {
        return senderMonitorInfo;
    }

    public void setSenderMonitorInfo(SenderMonitorInfo senderMonitorInfo) {
        this.senderMonitorInfo = senderMonitorInfo;
    }

   
    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}

