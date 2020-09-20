/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerminds.eagermindsledsoft.pages.monitor;

import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import novj.platform.vxkit.common.bean.monitor.DeviceSearchMapping;
import novj.platform.vxkit.common.bean.monitor.MonitorCardMonitorInfo;
import novj.platform.vxkit.common.bean.monitor.ReceiveCardMonitorData;
import novj.platform.vxkit.common.bean.monitor.ScannerMonitorInfo;
import novj.platform.vxkit.common.bean.monitor.monitorcardInfo.MCSocketCableUpdateInfo;
import novj.platform.vxkit.common.bean.monitor.monitorcardInfo.ModuleUpdateInfo;
import com.eagerminds.eagermindsledsoft.Contacts;

import java.util.List;
/**
 * FXML Controller class
 *
 * @author user
 */
public class ReceiveCardMonitorInfoUIController {
    @FXML
    TreeView tvReceiveCardMonitorInfo;

    public void init(ReceiveCardMonitorData receiveCardMonitorData) {
        if (tvReceiveCardMonitorInfo.getRoot() != null) {
            tvReceiveCardMonitorInfo.getRoot().getChildren().clear();
        }
        TreeItem root = new TreeItem(Contacts.getResourceValue("DetailInfos"));
        tvReceiveCardMonitorInfo.setRoot(root);
        TreeItem receiveTreeItem = new TreeItem(Contacts.getResourceValue("ReceiveCardInfo"));
        root.getChildren().add(receiveTreeItem);
        ScannerMonitorInfo scannerMonitorInfo = receiveCardMonitorData.getReceiveCardMonitorInfo();
        if (scannerMonitorInfo == null) {
            receiveTreeItem.getChildren().add(new TreeItem(Contacts.getResourceValue("NoReceiveCardInfo")));
        } else {
            receiveTreeItem.getChildren().add(new TreeItem(Contacts.getResourceValue("WorkStatus") + "：" + Utils.getDeviceWorkStatus(scannerMonitorInfo.getDevWorkStatus())));
            receiveTreeItem.getChildren().add(new TreeItem(Contacts.getResourceValue("Temperature") + scannerMonitorInfo.getTemprature()));
            receiveTreeItem.getChildren().add(new TreeItem(Contacts.getResourceValue("Voltage") + scannerMonitorInfo.getVoltage()));
            TreeItem devices1 = new TreeItem(Contacts.getResourceValue("DevicePosition"));
            receiveTreeItem.getChildren().add(devices1);
            List<DeviceSearchMapping> deviceSearchMappingList1 = scannerMonitorInfo.getDevMappingList();
            if (deviceSearchMappingList1 == null || deviceSearchMappingList1.isEmpty() || deviceSearchMappingList1.size() <= 0) {
                devices1.getChildren().add(new TreeItem(Contacts.getResourceValue("NoDevicePosition")));
            } else {
                int index1 = 0;
                for (DeviceSearchMapping device : deviceSearchMappingList1) {
                    if (device == null) {
                        //devices1.getChildren().add(new TreeItem("tree" + index1 + " index"));
                    } else {
                        TreeItem temp1 = new TreeItem(Contacts.getResourceValue("DevicePosition") + device.getDevIndex()/*index1*/);
                        devices1.getChildren().add(temp1);
                        temp1.getChildren().add(new TreeItem(Contacts.getResourceValue("PositionIndex") + device.getDevIndex()));
                        temp1.getChildren().add(new TreeItem(Contacts.getResourceValue("DeviceType") + Utils.getDeviceName(device.getDevType())));
                    }
                    index1++;
                }
            }
        }
        TreeItem monitorTreeItem = new TreeItem(Contacts.getResourceValue("MonitorCardInfo"));
        root.getChildren().add(monitorTreeItem);
        MonitorCardMonitorInfo monitorCardMonitorInfo = receiveCardMonitorData.getMonitorCardMonitorInfo();
        if (monitorCardMonitorInfo == null) {
            monitorTreeItem.getChildren().add(new TreeItem(Contacts.getResourceValue("NoMonitorCardInfo")));
        } else {
            monitorTreeItem.getChildren().add(new TreeItem(Contacts.getResourceValue("WorkStatus") + "：" + Utils.getDeviceWorkStatus(monitorCardMonitorInfo.getDevWorkStatus())));
            TreeItem device2 = new TreeItem(Contacts.getResourceValue("DevicePosition"));
            monitorTreeItem.getChildren().add(device2);
            List<DeviceSearchMapping> deviceSearchMappingList2 = monitorCardMonitorInfo.getDevMappingList();
            if (deviceSearchMappingList2 == null || deviceSearchMappingList2.isEmpty() || deviceSearchMappingList2.size() <= 0) {
                device2.getChildren().add(new TreeItem(Contacts.getResourceValue("NoDevicePosition")));
            } else {
                int index2 = 0;
                for (DeviceSearchMapping device : deviceSearchMappingList2) {
                    if (device == null) {
                        //device2.getChildren().add(new TreeItem("tree" + index2 + " index"));
                    } else {
                        TreeItem tempTreeItem = new TreeItem(Contacts.getResourceValue("DevicePosition") + device.getDevIndex()/*index2*/);
                        device2.getChildren().add(tempTreeItem);
                        tempTreeItem.getChildren().add(new TreeItem(Contacts.getResourceValue("PositionIndex") + device.getDevIndex()));
                        tempTreeItem.getChildren().add(new TreeItem(Contacts.getResourceValue("DeviceType") + Utils.getDeviceName(device.getDevType())));
                    }
                    index2++;
                }
            }
            TreeItem tempTreeItem = new TreeItem(Contacts.getResourceValue("TemperatureInfo"));
            monitorTreeItem.getChildren().add(tempTreeItem);
            if (monitorCardMonitorInfo.temperatureUpdateInfo == null) {
                tempTreeItem.getChildren().add(new TreeItem(Contacts.getResourceValue("NoTemperatureInfo")));
            } else {
                tempTreeItem.getChildren().add(new TreeItem(Contacts.getResourceValue("IsUpdated") + (monitorCardMonitorInfo.temperatureUpdateInfo.isUpdate ? Contacts.getResourceValue("yes") : Contacts.getResourceValue("no"))));
                tempTreeItem.getChildren().add(new TreeItem(Contacts.getResourceValue("Temperature") + monitorCardMonitorInfo.temperatureUpdateInfo.temprature));
            }
            TreeItem humTreeItem = new TreeItem(Contacts.getResourceValue("HumidityInfo"));
            monitorTreeItem.getChildren().add(humTreeItem);
            if (monitorCardMonitorInfo.humidityUpdateInfo == null) {
                humTreeItem.getChildren().add(new TreeItem(Contacts.getResourceValue("NoHumidityInfo")));
            } else {
                humTreeItem.getChildren().add(new TreeItem(Contacts.getResourceValue("IsUpdated") + (monitorCardMonitorInfo.humidityUpdateInfo.isUpdate ? Contacts.getResourceValue("yes") : Contacts.getResourceValue("no"))));
                humTreeItem.getChildren().add(new TreeItem(Contacts.getResourceValue("Humidity") + monitorCardMonitorInfo.humidityUpdateInfo.humidity));
            }
            TreeItem smokeTreeItem = new TreeItem(Contacts.getResourceValue("SmokeInfo"));
            monitorTreeItem.getChildren().add(smokeTreeItem);
            if (monitorCardMonitorInfo.smokeUpdateInfo == null) {
                smokeTreeItem.getChildren().add(new TreeItem(Contacts.getResourceValue("NoSmokeInfo")));
            } else {
                smokeTreeItem.getChildren().add(new TreeItem(Contacts.getResourceValue("IsUpdated") + (monitorCardMonitorInfo.smokeUpdateInfo.isUpdate ? Contacts.getResourceValue("yes") : Contacts.getResourceValue("no"))));
                smokeTreeItem.getChildren().add(new TreeItem(Contacts.getResourceValue("HasSmoke") + (monitorCardMonitorInfo.smokeUpdateInfo.smoke ? Contacts.getResourceValue("yes") : Contacts.getResourceValue("no"))));
            }
            TreeItem doorTreeItem = new TreeItem(Contacts.getResourceValue("DoorInfo"));
            monitorTreeItem.getChildren().add(doorTreeItem);
            if (monitorCardMonitorInfo.cabinDoorUpdateInfo == null) {
                doorTreeItem.getChildren().add(new TreeItem(Contacts.getResourceValue("NoDoorInfo")));
            } else {
                doorTreeItem.getChildren().add(new TreeItem(Contacts.getResourceValue("IsUpdated") + (monitorCardMonitorInfo.cabinDoorUpdateInfo.isUpdate ? Contacts.getResourceValue("yes") : Contacts.getResourceValue("no"))));
                doorTreeItem.getChildren().add(new TreeItem(Contacts.getResourceValue("DoorStatus") + (monitorCardMonitorInfo.cabinDoorUpdateInfo.isDoorOpen ? Contacts.getResourceValue("power_open") : Contacts.getResourceValue("power_close"))));
            }
            TreeItem fanTreeItem = new TreeItem(Contacts.getResourceValue("FanInfo"));
            monitorTreeItem.getChildren().add(fanTreeItem);
            if (monitorCardMonitorInfo.fansUpdateInfo == null) {
                fanTreeItem.getChildren().add(new TreeItem(Contacts.getResourceValue("NoFanInfo")));
            } else {
                fanTreeItem.getChildren().add(new TreeItem(Contacts.getResourceValue("IsUpdated") + (monitorCardMonitorInfo.fansUpdateInfo.isUpdate ? Contacts.getResourceValue("yes") : Contacts.getResourceValue("no"))));
                if (monitorCardMonitorInfo.fansUpdateInfo.fansMonitorInfoCollection != null &&
                        monitorCardMonitorInfo.fansUpdateInfo.fansMonitorInfoCollection.size() > 0) {
                    int index = 0;
                    for (Integer key : monitorCardMonitorInfo.fansUpdateInfo.fansMonitorInfoCollection.keySet()) {
                        TreeItem tempFanTreeItem = new TreeItem(Contacts.getResourceValue("Fan") + key.intValue()/*index*/);
                        fanTreeItem.getChildren().add(tempFanTreeItem);
                        tempFanTreeItem.getChildren().add(new TreeItem(Contacts.getResourceValue("DeviceWayNo") + key.intValue()));
                        tempFanTreeItem.getChildren().add(new TreeItem(Contacts.getResourceValue("FanSpeep") + monitorCardMonitorInfo.fansUpdateInfo.fansMonitorInfoCollection.get(key).intValue()));
                        index++;
                    }
                } else {
                    fanTreeItem.getChildren().add(new TreeItem(Contacts.getResourceValue("NoFanDetailsInfo")));
                }
            }
            TreeItem powerTreeItem = new TreeItem(Contacts.getResourceValue("PowerInfo"));
            monitorTreeItem.getChildren().add(powerTreeItem);
            if (monitorCardMonitorInfo.powerUpdateInfo == null) {
                powerTreeItem.getChildren().add(new TreeItem(Contacts.getResourceValue("NoPowerInfo")));
            } else {
                powerTreeItem.getChildren().add(new TreeItem(Contacts.getResourceValue("IsUpdated") + (monitorCardMonitorInfo.powerUpdateInfo.isUpdate ? Contacts.getResourceValue("yes") : Contacts.getResourceValue("no"))));
                if (monitorCardMonitorInfo.powerUpdateInfo.powerMonitorInfoCollection != null &&
                        monitorCardMonitorInfo.powerUpdateInfo.powerMonitorInfoCollection.size() > 0) {
                    int index = 0;
                    for (Integer key : monitorCardMonitorInfo.powerUpdateInfo.powerMonitorInfoCollection.keySet()) {
                        TreeItem tempPowerTreeItem = new TreeItem(Contacts.getResourceValue("Power") + key.intValue()/*index*/);
                        powerTreeItem.getChildren().add(tempPowerTreeItem);
                        if (key.intValue() == 0) {
                            tempPowerTreeItem.getChildren().add(new TreeItem(Contacts.getResourceValue("MonitorCardVoltage") + monitorCardMonitorInfo.powerUpdateInfo.powerMonitorInfoCollection.get(key).floatValue()));
                        } else {
                            tempPowerTreeItem.getChildren().add(new TreeItem(Contacts.getResourceValue("DeviceWayNo") + key.intValue()));
                            tempPowerTreeItem.getChildren().add(new TreeItem(Contacts.getResourceValue("Voltage") + monitorCardMonitorInfo.powerUpdateInfo.powerMonitorInfoCollection.get(key).floatValue()));
                        }
                        index++;
                    }
                } else {
                    powerTreeItem.getChildren().add(new TreeItem(Contacts.getResourceValue("NoPowerDetailsInfo")));
                }
            }
            TreeItem cableTreeItem = new TreeItem(Contacts.getResourceValue("CableInfo"));
            monitorTreeItem.getChildren().add(cableTreeItem);
            if (monitorCardMonitorInfo.socketCableUpdateInfo == null) {
                cableTreeItem.getChildren().add(new TreeItem(Contacts.getResourceValue("NoCableInfo")));
            } else {
                cableTreeItem.getChildren().add(new TreeItem(Contacts.getResourceValue("IsUpdated") + (monitorCardMonitorInfo.socketCableUpdateInfo.isUpdate ? Contacts.getResourceValue("yes") : Contacts.getResourceValue("no"))));
                if (monitorCardMonitorInfo.socketCableUpdateInfo.socketCableMonitorInfoList != null &&
                        monitorCardMonitorInfo.socketCableUpdateInfo.socketCableMonitorInfoList.size() > 0) {
                    int index = 0;
                    for (MCSocketCableUpdateInfo.SocketCableMonitorInfo cableMonitorInfo : monitorCardMonitorInfo.socketCableUpdateInfo.socketCableMonitorInfoList) {
                        if (cableMonitorInfo == null) {
                           
                        } else {
                            TreeItem tempCableTreeItem = new TreeItem(Contacts.getResourceValue("CableInfo") + index);
                            cableTreeItem.getChildren().add(tempCableTreeItem);
                            TreeItem device3 = new TreeItem(Contacts.getResourceValue("DevicePosition"));
                            tempCableTreeItem.getChildren().add(device3);

                            List<DeviceSearchMapping> deviceSearchMappingList3 = cableMonitorInfo.getDevMappingList();
                            if (deviceSearchMappingList3 == null || deviceSearchMappingList3.size() <= 0 || deviceSearchMappingList3.isEmpty()) {
                                device3.getChildren().add(new TreeItem(Contacts.getResourceValue("NoDevicePosition")));
                            } else {
                                int index3 = 0;
                                for (DeviceSearchMapping deviceMap : deviceSearchMappingList3) {
                                    if (deviceMap == null) {
                                       
                                    } else {
                                        TreeItem tempAdd3 = new TreeItem(Contacts.getResourceValue("DevicePosition") + deviceMap.getDevIndex()/*index3*/);
                                        device3.getChildren().add(tempAdd3);
                                        tempAdd3.getChildren().add(new TreeItem(Contacts.getResourceValue("PositionIndex") + deviceMap.getDevIndex()));
                                        tempAdd3.getChildren().add(new TreeItem(Contacts.getResourceValue("DeviceType") + Utils.getDeviceName(deviceMap.getDevType())));
                                    }
                                    index3++;
                                }
                            }

                            TreeItem cableMapTreeItem = new TreeItem(Contacts.getResourceValue("CableDetailInfo"));
                            tempCableTreeItem.getChildren().add(cableMapTreeItem);
                            if (cableMonitorInfo.socketCableInfoMap != null && cableMonitorInfo.socketCableInfoMap.size() > 0) {
                                int index4 = 0;
                                for (Integer key : cableMonitorInfo.socketCableInfoMap.keySet()) {
                                    List<MCSocketCableUpdateInfo.SocketCableStatus> cableStatusList = cableMonitorInfo.socketCableInfoMap.get(key);
                                    if (cableStatusList == null || cableStatusList.size() <= 0) {
                                       
                                    } else {
                                        TreeItem tempCable4 = new TreeItem(Contacts.getResourceValue("CableDetailInfo") + index4);
                                        cableMapTreeItem.getChildren().add(tempCable4);
                                        int index5 = 0;
                                        for (MCSocketCableUpdateInfo.SocketCableStatus cableStatus : cableStatusList) {
                                            TreeItem tempAdd5 = new TreeItem(Contacts.getResourceValue("CableDetailInfo") + key.intValue() + "-" + index5);
                                            tempCable4.getChildren().add(tempAdd5);
                                            tempAdd5.getChildren().add(new TreeItem(Contacts.getResourceValue("CableStatus") + (cableStatus.isCableOK ? Contacts.getResourceValue("WorkStatusOK") : Contacts.getResourceValue("WorkStatusError"))));
                                            tempAdd5.getChildren().add(new TreeItem(Contacts.getResourceValue("CableType") + getCableType(cableStatus.cableType)));
                                            index5++;
                                        }
                                    }
                                    index4++;
                                }
                            }
                        }
                        index++;
                    }
                } else {
                    cableTreeItem.getChildren().add(new TreeItem(Contacts.getResourceValue("NoCableDetailInfo")));
                }
            }
            TreeItem moduleTreeItem = new TreeItem(Contacts.getResourceValue("ModuleInfo"));
            monitorTreeItem.getChildren().add(moduleTreeItem);
            if (monitorCardMonitorInfo.moduleUpdateInfo == null) {
               
            } else {
                moduleTreeItem.getChildren().add(new TreeItem(Contacts.getResourceValue("IsUpdated") + (monitorCardMonitorInfo.moduleUpdateInfo.isUpdate ? Contacts.getResourceValue("yes") : Contacts.getResourceValue("no"))));
                if (monitorCardMonitorInfo.moduleUpdateInfo.moduleMonitorInfoCollection != null &&
                        monitorCardMonitorInfo.moduleUpdateInfo.moduleMonitorInfoCollection.size() > 0) {
                    int index = 0;
                    for (Integer key : monitorCardMonitorInfo.moduleUpdateInfo.moduleMonitorInfoCollection.keySet()) {
                        ModuleUpdateInfo.ModuleStatus moduleStatus = monitorCardMonitorInfo.moduleUpdateInfo.moduleMonitorInfoCollection.get(key);
                        if (moduleStatus == null) {
                            
                        } else {
                            TreeItem tempModuleTreeItem = new TreeItem(Contacts.getResourceValue("ModuleInfo") + index);
                            moduleTreeItem.getChildren().add(tempModuleTreeItem);
                            tempModuleTreeItem.getChildren().add(new TreeItem(Contacts.getResourceValue("WorkStatus") + "：" + Utils.getDeviceWorkStatus(moduleStatus.deviceWorkStatus)));
                            tempModuleTreeItem.getChildren().add(new TreeItem(Contacts.getResourceValue("BusIndex") + moduleStatus.busIndex));
                            tempModuleTreeItem.getChildren().add(new TreeItem(Contacts.getResourceValue("FlashIndex") + moduleStatus.flashIndex));
                            tempModuleTreeItem.getChildren().add(new TreeItem(Contacts.getResourceValue("RowIndex") + moduleStatus.rowIndex));
                            tempModuleTreeItem.getChildren().add(new TreeItem(Contacts.getResourceValue("ColIndex") + moduleStatus.colIndex));
                            tempModuleTreeItem.getChildren().add(new TreeItem(Contacts.getResourceValue("Temperature") + moduleStatus.temperature));
                            tempModuleTreeItem.getChildren().add(new TreeItem(Contacts.getResourceValue("Voltage") + moduleStatus.voltage));
                            tempModuleTreeItem.getChildren().add(new TreeItem(Contacts.getResourceValue("WorkTime") + moduleStatus.workTime));
                        }
                        index++;
                    }
                } else {
                    moduleTreeItem.getChildren().add(new TreeItem(Contacts.getResourceValue("NoModuleDetailInfo")));
                }
            }
        }
    }

    private String getCableType(int cableType) {
        String type = "Unknown";
        switch (cableType) {
            case 0:
                type = "RED";
                break;
            case 1:
                type = "GREEN";
                break;
            case 2:
                type = "BLUE";
                break;
            case 3:
                type = "VRED";
                break;
            case 4:
                type = "ABCD";
                break;
            case 5:
                type = "LAT";
                break;
            case 6:
                type = "OE";
                break;
            case 7:
                type = "DCLK";
                break;
            case 8:
                type = "CTRL";
                break;
            default:
                type = "Unknown";
                break;
        }
        return type;
    }
}
