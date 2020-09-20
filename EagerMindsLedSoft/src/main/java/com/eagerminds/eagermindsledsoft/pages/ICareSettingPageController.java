/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerminds.eagermindsledsoft.pages;

import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import novj.platform.vxkit.common.bean.CareConfigInfo;
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
public class ICareSettingPageController {

    @FXML
    ComboBox cb_open_icare;
    @FXML
    TextField tf_icare_address, tf_icare_user;
    @FXML
    TextArea ta_show;

    public void init() {
        ObservableList<String> openOptions = FXCollections.observableArrayList(
                Contacts.getResourceValue("yes"), Contacts.getResourceValue("no"));
        cb_open_icare.setItems(openOptions);
        cb_open_icare.getSelectionModel().select(0);
        cb_open_icare.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {

            }
        });
    }

    /**
     * 获取iCare配置
     *
     * @param actionEvent
     */
    @FXML
    protected void onActionGetiCare(ActionEvent actionEvent) {
        NovaOpt.GetInstance().getiCare(Contacts.deviceOpt.getSn(), "us-en", new OnResultListenerN<CareConfigInfo, ErrorDetail>() {
            @Override
            public void onSuccess(IRequestBase requestBase, CareConfigInfo response) {
                if (null != response) {
                    showInfo(JSONUtil.toJsonStringByFastJson(response));
                } else {
                    showInfo("Error: Failed to obtain iCare configuration");
                }
            }

            @Override
            public void onError(IRequestBase requestBase, ErrorDetail error) {
                if (StringUtil.isEmpty(error.description)) {
                    showInfo("Error: Failed to obtain iCare configuration");
                } else {
                    showInfo("Error:" + error.description);
                }
            }
        });
    }

    @FXML
    protected void onActionSetiCare(ActionEvent actionEvent) {
        CareConfigInfo info = new CareConfigInfo();
        if (0 == cb_open_icare.getSelectionModel().getSelectedIndex()) {
            info.state = true;
        } else {
            info.state = false;
        }
        if (StringUtil.isEmpty(tf_icare_address.getText().trim()) ||
                StringUtil.isEmpty(tf_icare_user.getText().trim())) {
            return;
        }
        info.url = tf_icare_address.getText().trim();
        info.username = tf_icare_user.getText().trim();
        boolean iCareState = 0 == cb_open_icare.getSelectionModel().getSelectedIndex() ? true : false;
        NovaOpt.GetInstance().setiCare(Contacts.deviceOpt.getSn(), iCareState,
                tf_icare_address.getText().trim(), tf_icare_user.getText().trim(),
                new OnResultListenerN<Integer, ErrorDetail>() {
                    @Override
                    public void onSuccess(IRequestBase requestBase, Integer response) {
                        showInfo("iCare configured successfully");
                    }

                    @Override
                    public void onError(IRequestBase requestBase, ErrorDetail error) {
                        if (StringUtil.isEmpty(error.description)){
                            showInfo("Error:iCare configuration failed");
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
