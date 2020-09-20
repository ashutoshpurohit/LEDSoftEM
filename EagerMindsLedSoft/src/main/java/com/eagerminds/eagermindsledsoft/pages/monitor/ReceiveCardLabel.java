/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerminds.eagermindsledsoft.pages.monitor;

import javafx.scene.control.Label;
import novj.platform.vxkit.common.bean.monitor.HardwareDistribution;
import novj.platform.vxkit.common.bean.monitor.ReceiveCardMonitorData;

/**
 *
 * @author user
 */
public class ReceiveCardLabel extends Label {
    private HardwareDistribution.ScanBoardRegionInfo receiveCardRegionInfo;
    private ReceiveCardMonitorData receiveCardMonitorData;
    private boolean isSelected=false;

    
    public HardwareDistribution.ScanBoardRegionInfo getReceiveCardRegionInfo() {
        return receiveCardRegionInfo;
    }

    public void setReceiveCardRegionInfo(HardwareDistribution.ScanBoardRegionInfo receiveCardRegionInfo) {
        this.receiveCardRegionInfo = receiveCardRegionInfo;
    }

   
    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

  
    public ReceiveCardMonitorData getReceiveCardMonitorData() {
        return receiveCardMonitorData;
    }

    public void setReceiveCardMonitorData(ReceiveCardMonitorData receiveCardMonitorData) {
        this.receiveCardMonitorData = receiveCardMonitorData;
    }
}
