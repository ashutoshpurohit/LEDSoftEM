/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerminds.eagermindsledsoft.pages.monitor;

import novj.platform.vxkit.common.bean.monitor.DeviceSearchMapping;
import novj.platform.vxkit.common.bean.monitor.HardwareDistribution;
import novj.platform.vxkit.common.bean.monitor.SenderMonitorInfo;
import com.eagerminds.eagermindsledsoft.Contacts;

import java.util.List;

/**
 *
 * @author user
 */
public class Utils {

    public static final int UNDEFINED = 0;
  
    public static final int LED_SCREEN = 1;
 
    public static final int SEND_CARD = 2;
 
    public static final int SEND_CARD_PORT = 3;
  
    public static final int DECONCENT_CARD = 4;
 
    public static final int SCANNER = 5;
   
    public static final int MONITORING_CARD = 6;
   
    public static final int MONITORING_CARD_FAN = 7;
   
    public static final int MONITORING_CARD_VOLTAGE = 8;
   
    public static final int MONITORING_CARD_FLAT_CABLE = 9;
    
    public static final int FUNCTION_CARD = 10;
   
    public static final int COMPUTER = 11;
   
    public static final int SMART_MODULE = 12;
   
    public static final int SMART_MODULE_ROW = 13;
    
    public static final int SMART_MODULE_COL = 14;

    public static final String STRING_EMPTY = "";

    public static final int DEVICE_WORK_STATUS_OK = 0;
   
    public static final int DEVICE_WORK_STATUS_ERROR = 1;
   
    public static final int DEVICE_WORK_STATUS_UNKNOWN = 2;

    public static String getSenderCardShortMsg(SenderMonitorInfo senderMonitorInfo) {
        if (senderMonitorInfo == null ||
                senderMonitorInfo.getDevMappingList() == null ||
                senderMonitorInfo.getDevMappingList().size() <= 0 ||
                senderMonitorInfo.getDevMappingList().isEmpty()) {
            return STRING_EMPTY;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Contacts.getResourceValue("SendCard")).append(":").append(getSenderCardIndex(senderMonitorInfo));
        stringBuilder.append("\r\n");
        stringBuilder.append(Contacts.getResourceValue("WorkStatus")).append(":").append(getDeviceWorkStatus(senderMonitorInfo.getDevWorkStatus()));
        return stringBuilder.toString();
    }

    public static String getReceiveCardShortMsg(HardwareDistribution.ScanBoardRegionInfo receiveCardInfo) {
        if (receiveCardInfo == null) {
            return STRING_EMPTY;
        }
        StringBuilder stringBuilder = new StringBuilder();

        if (receiveCardInfo.senderIndex == -1) {
            stringBuilder.append(Contacts.getResourceValue("ReceiveCard")).append(":").append(Contacts.getResourceValue("ReceiveCardEmpty"));
            stringBuilder.append("\r\n");
            stringBuilder.append(Contacts.getResourceValue("SendCard"));
            stringBuilder.append("\r\n");
            stringBuilder.append(Contacts.getResourceValue("NetPort"));
        } else {
            stringBuilder.append(Contacts.getResourceValue("ReceiveCard")).append(":").append(receiveCardInfo.connectIndex);
            stringBuilder.append("\r\n");
            stringBuilder.append(Contacts.getResourceValue("SendCard")).append(":").append(receiveCardInfo.senderIndex);
            stringBuilder.append("\r\n");
            stringBuilder.append(Contacts.getResourceValue("NetPort")).append(":").append(receiveCardInfo.portIndex);
        }
        stringBuilder.append("\r\n");
        stringBuilder.append("X").append(":").append(receiveCardInfo.X);
        stringBuilder.append("\r\n");
        stringBuilder.append("Y").append(":").append(receiveCardInfo.Y);
        stringBuilder.append("\r\n");
        stringBuilder.append("Width").append(":").append(receiveCardInfo.width);
        stringBuilder.append("\r\n");
        stringBuilder.append("Height").append(":").append(receiveCardInfo.height);
        return stringBuilder.toString();
    }

  
    public static String getDeviceName(int deviceType) {
        String deviceName = STRING_EMPTY;
        switch (deviceType) {
            case UNDEFINED:
                deviceName = Contacts.getResourceValue("Device_UNDEFINED");
                break;
            case LED_SCREEN:
                deviceName = Contacts.getResourceValue("Device_LED_SCREEN");
                break;
            case SEND_CARD:
                deviceName = Contacts.getResourceValue("Device_SEND_CARD");
                break;
            case SEND_CARD_PORT:
                deviceName = Contacts.getResourceValue("Device_SEND_CARD_PORT");
                break;
            case DECONCENT_CARD:
                deviceName = Contacts.getResourceValue("Device_DECONCENT_CARD");
                break;
            case SCANNER:
                deviceName = Contacts.getResourceValue("Device_SCANNER");
                break;
            case MONITORING_CARD:
                deviceName = Contacts.getResourceValue("Device_MONITORING_CARD");
                break;
            case MONITORING_CARD_FAN:
                deviceName = Contacts.getResourceValue("Device_MONITORING_CARD_FAN");
                break;
            case MONITORING_CARD_VOLTAGE:
                deviceName = Contacts.getResourceValue("Device_MONITORING_CARD_VOLTAGE");
                break;
            case MONITORING_CARD_FLAT_CABLE:
                deviceName = Contacts.getResourceValue("Device_MONITORING_CARD_FLAT_CABLE");
                break;
            case FUNCTION_CARD:
                deviceName = Contacts.getResourceValue("Device_FUNCTION_CARD");
                break;
            case COMPUTER:
                deviceName = Contacts.getResourceValue("Device_COMPUTER");
                break;
            case SMART_MODULE:
                deviceName = Contacts.getResourceValue("Device_SMART_MODULE");
                break;
            case SMART_MODULE_ROW:
                deviceName = Contacts.getResourceValue("Device_SMART_MODULE_ROW");
                break;
            case SMART_MODULE_COL:
                deviceName = Contacts.getResourceValue("Device_SMART_MODULE_COL");
                break;
            default:
                deviceName = Contacts.getResourceValue("Device_UNDEFINED");
                break;
        }
        return deviceName;
    }

   
    public static int getSenderCardIndex(SenderMonitorInfo senderMonitorInfo) {
        int sendCardIndex = -1;
        if (senderMonitorInfo == null ||
                senderMonitorInfo.getDevMappingList() == null ||
                senderMonitorInfo.getDevMappingList().size() <= 0 ||
                senderMonitorInfo.getDevMappingList().isEmpty()) {
            return sendCardIndex;
        }
        List<DeviceSearchMapping> deviceSearchMappingList = senderMonitorInfo.getDevMappingList();
        for (DeviceSearchMapping deviceSearchMapping : deviceSearchMappingList) {
            if (deviceSearchMapping.getDevType() == SEND_CARD) {
                sendCardIndex = deviceSearchMapping.getDevIndex();
                break;
            }
        }
        return sendCardIndex;
    }

  
    public static String getDeviceWorkStatus(int deviceWorkStatus) {
        String workStatus = "OK";
        switch (deviceWorkStatus) {
            case 0:
                workStatus = Contacts.getResourceValue("WorkStatusOK");
                break;
            case 1:
                workStatus = Contacts.getResourceValue("WorkStatusError");
                break;
            default:
                workStatus = Contacts.getResourceValue("WorkStatusUnknown");
                break;
        }
        return workStatus;
    }
}

