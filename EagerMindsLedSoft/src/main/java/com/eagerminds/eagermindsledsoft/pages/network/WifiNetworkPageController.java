/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerminds.eagermindsledsoft.pages.network;

import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import novj.platform.vxkit.common.bean.monitor.StateBean;
import novj.platform.vxkit.common.bean.wifi.WifiListBean;
import novj.platform.vxkit.common.result.OnResultListenerN;
import novj.publ.api.NovaOpt;
import novj.publ.net.exception.ErrorDetail;
import novj.publ.net.svolley.Request.IRequestBase;
import novj.publ.util.JSONUtil;
import novj.publ.util.StringUtil;
import com.eagerminds.eagermindsledsoft.Contacts;

/**
 * FXML Controller class
 *
 * @author user
 */
public class WifiNetworkPageController {
    @FXML
    ComboBox cb_wifi_set_status;
    @FXML
    Button btn_wifi_get_open, btn_wifi_get_list, btn_wifi_set_open, btn_wifi_connect;
    @FXML
    TextField tf_wifi_password, tf_wifi_ssid;
    @FXML
    TextArea ta_show;


    public void init() {
        ObservableList<String> openOptions = FXCollections.observableArrayList(
                Contacts.getResourceValue("yes"), Contacts.getResourceValue("no"));
        cb_wifi_set_status.setItems(openOptions);
        cb_wifi_set_status.getSelectionModel().select(0);
    }


    @FXML
    protected void onActionWifiGet(ActionEvent actionEvent) {
        NovaOpt.GetInstance().getWifiEnabled(Contacts.deviceOpt.getSn(), new OnResultListenerN<StateBean<Integer>, ErrorDetail>() {
            @Override
            public void onSuccess(IRequestBase requestBase, StateBean<Integer> response) {
                showInfo(1 == response.getState() ? "WIFI" : "onActionWifiGet");
            }

            @Override
            public void onError(IRequestBase requestBase, ErrorDetail error) {
                if (StringUtil.isEmpty(error.description)) {
                    showInfo("Error:onActionWifiGet");
                } else {
                    showInfo("Error:" + error.description);
                }
            }
        });
    }

    @FXML
    protected void onActionWifiGetList(ActionEvent actionEvent) {
        NovaOpt.GetInstance().getWifiList(Contacts.deviceOpt.getSn(), new OnResultListenerN<WifiListBean, ErrorDetail>() {
            @Override
            public void onSuccess(IRequestBase requestBase, WifiListBean response) {
                if (null != response) {
                    showInfo(JSONUtil.toJsonStringByFastJson(response));
                } else {
                    showInfo("onActionWifiGetList Success");
                }
            }

            @Override
            public void onError(IRequestBase requestBase, ErrorDetail error) {
                if (StringUtil.isEmpty(error.description)) {
                    showInfo("Error: onActionWifiGetList");
                } else {
                    showInfo("Error:" + error.description);
                }
            }
        });
    }

    @FXML
    protected void onActionWifiSet(ActionEvent actionEvent) {
        boolean open = 0 == cb_wifi_set_status.getSelectionModel().getSelectedIndex() ? true : false;
        NovaOpt.GetInstance().setWifiEnabled(Contacts.deviceOpt.getSn(), open, new OnResultListenerN<Integer, ErrorDetail>() {
            @Override
            public void onSuccess(IRequestBase requestBase, Integer response) {
                showInfo("onActionWifiSet onSuccess");
            }

            @Override
            public void onError(IRequestBase requestBase, ErrorDetail error) {
                if (StringUtil.isEmpty(error.description)) {
                    showInfo("Error: onActionWifiSet");
                } else {
                    showInfo("Error:" + error.description);
                }
            }
        });
    }

    @FXML
    protected void onActionWifiConnect(ActionEvent actionEvent) {
        if (StringUtil.isEmpty(tf_wifi_ssid.getText().trim()) ||
                StringUtil.isEmpty(tf_wifi_password.getText().trim())) {
            showInfo("onActionWifiConnect");
            return;
        }
        NovaOpt.GetInstance().connectWifiNetwork(Contacts.deviceOpt.getSn(),
                tf_wifi_ssid.getText().trim(), tf_wifi_password.getText(),
                new OnResultListenerN<Integer, ErrorDetail>() {
                    @Override
                    public void onSuccess(IRequestBase requestBase, Integer response) {
                        showInfo("onActionWifiConnect onSuccess");
                    }

                    @Override
                    public void onError(IRequestBase requestBase, ErrorDetail error) {
                        if (StringUtil.isEmpty(error.description)) {
                            showInfo("Error: onActionWifiConnect");
                        } else {
                            showInfo("Error:" + error.description);
                        }
                    }
                });
    }


    public void showInfo(String sInfo) {
        Platform.runLater(() -> {
            Date day = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            ta_show.appendText("[" + df.format(day) + "]  " + sInfo + "\r\n");
        });
    }
}
