/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerminds.eagermindsledsoft;

import com.eagerminds.eagermindsledsoft.DeviceInfo;
import com.eagerminds.eagermindsledsoft.PolicyInfo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;          

/**
 *
 * @author user
 */
public class ViewModel {
    public static ObservableList<PolicyInfo> policyInfos = FXCollections.observableArrayList();
    public static ObservableList<DeviceInfo> devicesData = FXCollections.observableArrayList();

    public static DeviceInfo getDeviceInfoBySN(String sn) {
        if(!sn.isEmpty()){
            for (DeviceInfo info:devicesData) {
                if(info.getSn().equals(sn)){
                    return info;
                }
            }
        }

        return null;
    }

}

