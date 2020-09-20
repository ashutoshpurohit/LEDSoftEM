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
import novj.platform.vxkit.common.bean.MobileData;
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
public class MobileNetworkPageController {
    @FXML
    Button btn_mobile_exist,btn_mobile_get,btn_mobile_set;
    @FXML
    TextField tf_mobile_name;
    @FXML
    TextArea ta_show;
    @FXML
    ComboBox cb_mobile_switch,cb_mobile_roam,cb_mobile_enable;


    public void init(){
        ObservableList<String> openOptions = FXCollections.observableArrayList(
                Contacts.getResourceValue("yes"), Contacts.getResourceValue("no"));

        cb_mobile_switch.setItems(openOptions);
        cb_mobile_switch.getSelectionModel().select(0);

        cb_mobile_roam.setItems(openOptions);
        cb_mobile_roam.getSelectionModel().select(0);

        cb_mobile_enable.setItems(openOptions);
        cb_mobile_enable.getSelectionModel().select(0);
    }

    @FXML
    protected void onActionMobileExist(ActionEvent actionEvent){
        NovaOpt.GetInstance().isMobileModuleExisted(Contacts.deviceOpt.getSn(), new OnResultListenerN<Boolean, ErrorDetail>() {
            @Override
            public void onSuccess(IRequestBase requestBase, Boolean response) {
                showInfo(response?" onActionMobileExist ":"onSuccess");
            }

            @Override
            public void onError(IRequestBase requestBase, ErrorDetail error) {
                if (StringUtil.isEmpty(error.description)){
                    showInfo("Error:");
                }else {
                    showInfo("Error:"+error.description);
                }
            }
        });
    }

    @FXML
    protected void onActionMobileGet(ActionEvent actionEvent){
        NovaOpt.GetInstance().getMobileNetwork(Contacts.deviceOpt.getSn(), new OnResultListenerN<MobileData, ErrorDetail>() {
            @Override
            public void onSuccess(IRequestBase requestBase, MobileData response) {
                if (null != response){
                    showInfo(JSONUtil.toJsonStringByFastJson(response));
                }else {
                    showInfo("Error:");
                }
            }

            @Override
            public void onError(IRequestBase requestBase, ErrorDetail error) {
                if (StringUtil.isEmpty(error.description)){
                    showInfo("Error:");
                }else {
                    showInfo("Error:"+error.description);
                }
            }
        });
    }

    @FXML
    protected void onActionMobileSet(ActionEvent actionEvent){
        MobileData.BasicConfig basicConfig = new MobileData.BasicConfig();
        basicConfig.setMobileData(0 == cb_mobile_switch.getSelectionModel().getSelectedIndex()?
                true:false);
        basicConfig.setDataRoaming(0 == cb_mobile_roam.getSelectionModel().getSelectedIndex()?
                true:false);
        basicConfig.setEnable4G(0 == cb_mobile_enable.getSelectionModel().getSelectedIndex()?
                true:false);

        MobileData.Advanced advanced = new MobileData.Advanced();
        advanced.setNetworkType("AUTO");
        if (StringUtil.isEmpty(tf_mobile_name.getText().trim())){
            MobileData.APN apn = new MobileData.APN();
            apn.setProviderName(tf_mobile_name.getText().trim());
            advanced.setAPN(apn);
        }

        MobileData mobileData = new MobileData();
        mobileData.setBasicConfigs(basicConfig);
        mobileData.setAdvanced(advanced);

        NovaOpt.GetInstance().setMobileNetwork(Contacts.deviceOpt.getSn(), mobileData, new OnResultListenerN<Integer, ErrorDetail>() {
            @Override
            public void onSuccess(IRequestBase requestBase, Integer response) {
                showInfo("onSuccess");
            }

            @Override
            public void onError(IRequestBase requestBase, ErrorDetail error) {
                if (StringUtil.isEmpty(error.description)){
                    showInfo("Error: onError");
                }else {
                    showInfo("Error:"+error.description);
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
