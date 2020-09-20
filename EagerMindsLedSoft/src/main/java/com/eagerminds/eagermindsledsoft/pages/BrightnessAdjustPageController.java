/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerminds.eagermindsledsoft.pages;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import novj.platform.vxkit.common.bean.AdjustModeInfo;
import novj.platform.vxkit.common.bean.AutoAdjustBrightnessArgument;
import novj.platform.vxkit.common.bean.BrightnessAdjustBean;
import novj.platform.vxkit.common.bean.BrightnessAdjustPolicy;
import novj.platform.vxkit.common.bean.BrightnessAdjustPolicyEntity;
import novj.platform.vxkit.common.bean.RatioBean;
import novj.platform.vxkit.common.bean.SourceBean;
import novj.platform.vxkit.common.bean.TimingAdjustBrightnessArgument;
import novj.platform.vxkit.common.result.OnResultListenerN;
import novj.publ.api.NovaOpt;
import novj.publ.net.exception.ErrorDetail;
import novj.publ.net.svolley.Request.IRequestBase;
import novj.publ.util.JSONUtil;
import com.eagerminds.eagermindsledsoft.Contacts;

/**
 * FXML Controller class
 *
 * @author user
 */
public class BrightnessAdjustPageController {
    @FXML
    HBox hbox_brightness_manual;
    @FXML
    TextField tf_brightness_value;
    @FXML
    HBox hbox_brightness_repeat;
    /**
     * Effective time
     */
    @FXML
    HBox hbox_brightness_operation;
    @FXML
    TextField tf_operation_hour;
    @FXML
    TextField tf_operation_minute;
    @FXML
    TextField tf_operation_second;


    @FXML
    ComboBox cb_adjust_type;
    @FXML
    ComboBox cb_adjust_repeat_type;
    @FXML
    HBox hbox_brightness_selectday;
    @FXML
    CheckBox cb_monday;
    @FXML
    CheckBox cb_tuesday;
    @FXML
    CheckBox cb_wednesday;
    @FXML
    CheckBox cb_thursday;
    @FXML
    CheckBox cb_friday;
    @FXML
    CheckBox cb_saturday;
    @FXML
    CheckBox cb_sunday;
    @FXML
    Button btn_confirm;
    @FXML
    Button btn_add_policy;
    @FXML
    TextArea ta_info;
    DecimalFormat df = new DecimalFormat("#00");
    private List<BrightnessAdjustPolicy> policyList = new ArrayList<>();

    public void initData() {
        btn_add_policy.setVisible(false);
        hbox_brightness_operation.setVisible(false);
        hbox_brightness_operation.setManaged(false);
        hbox_brightness_selectday.setManaged(false);
        hbox_brightness_selectday.setVisible(false);
        hbox_brightness_repeat.setManaged(false);
        hbox_brightness_repeat.setVisible(false);
        tf_brightness_value.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.matches("\\d*") && newValue.length() < 4) {
                if (newValue.length() > 0 && Integer.parseInt(newValue) > 100) {
                    tf_brightness_value.setText(oldValue);
                }
            } else {
                tf_brightness_value.setText(oldValue);
            }
        });

        tf_operation_hour.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.matches("\\d*") && newValue.length() < 3) {
                if (newValue.length() > 0 && Integer.parseInt(newValue) >= 24) {
                    tf_operation_hour.setText(oldValue);
                }
            } else {
                tf_operation_hour.setText(oldValue);
            }
        });
        tf_operation_minute.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.matches("\\d*") && newValue.length() < 3) {
                if (newValue.length() > 0 && Integer.parseInt(newValue) >= 60) {
                    tf_operation_minute.setText(oldValue);
                }
            } else {
                tf_operation_minute.setText(oldValue);
            }
        });
        tf_operation_second.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.matches("\\d*") && newValue.length() < 3) {
                if (newValue.length() > 0 && Integer.parseInt(newValue) >= 60) {
                    tf_operation_second.setText(oldValue);
                }
            } else {
                tf_operation_second.setText(oldValue);
            }
        });
        ObservableList<String> adjustOptions = FXCollections.observableArrayList(Contacts
                .getResourceValue("set_manual"), Contacts.getResourceValue
                ("set_timing"), Contacts.getResourceValue("set_auto"));
        cb_adjust_type.setItems(adjustOptions);
        cb_adjust_type.getSelectionModel().select(0);
        cb_adjust_type.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                int index = cb_adjust_type.getSelectionModel().getSelectedIndex();
                hbox_brightness_operation.setVisible(0 == index ? false : true);
                hbox_brightness_operation.setManaged(0 == index ? false : true);
                hbox_brightness_repeat.setVisible(0 == index ? false : true);
                hbox_brightness_repeat.setManaged(0 == index ? false : true);
                hbox_brightness_manual.setVisible(2 == index ? false : true);
                hbox_brightness_manual.setManaged(2 == index ? false : true);
                hbox_brightness_selectday.setVisible(0 == index ? false :
                        (1 == cb_adjust_repeat_type.getSelectionModel().getSelectedIndex() ? true :
                                false));
                hbox_brightness_selectday.setManaged(0 == index ? false :
                        (1 == cb_adjust_repeat_type.getSelectionModel().getSelectedIndex() ? true :
                                false));
                btn_add_policy.setVisible(0 == index ? false : true);
            }
        });
        ObservableList<String> repeatOptions = FXCollections.observableArrayList(Contacts
                .getResourceValue("repeat_everyday"), Contacts.getResourceValue
                ("repeat_everyday_by_week"));
        cb_adjust_repeat_type.setItems(repeatOptions);
        cb_adjust_repeat_type.getSelectionModel().select(0);
        cb_adjust_repeat_type.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                int index = cb_adjust_repeat_type.getSelectionModel().getSelectedIndex();
                hbox_brightness_selectday.setManaged(0 == index ? false : true);
                hbox_brightness_selectday.setVisible(0 == index ? false : true);
            }
        });
    }


    @FXML
    protected void getBrightness(ActionEvent actionEvent){
        NovaOpt.GetInstance().getScreenBrightness(Contacts.deviceOpt.getSn(), new OnResultListenerN<RatioBean<Float>, ErrorDetail>() {
            @Override
            public void onSuccess(IRequestBase requestBase, RatioBean<Float> response) {
                showInfo("response="+response.getRatio());
            }

            @Override
            public void onError(IRequestBase requestBase, ErrorDetail error) {
                showInfo("Error:"+error.description);
            }
        });
    }

    @FXML
    protected void getBrightnessPolicy(ActionEvent actionEvent){
        NovaOpt.GetInstance().getBrightnessPolicy(Contacts.deviceOpt.getSn(), new OnResultListenerN<BrightnessAdjustBean, ErrorDetail>() {
            @Override
            public void onSuccess(IRequestBase requestBase, BrightnessAdjustBean response) {
                showInfo(JSONUtil.toJsonStringByFastJson(response));
            }

            @Override
            public void onError(IRequestBase requestBase, ErrorDetail error) {
                showInfo("Error:"+error.description);
            }
        });
    }

    @FXML
    protected void onAddPolicy(ActionEvent ae) {
        //Set brightness regularly
        BrightnessAdjustPolicy policy = new BrightnessAdjustPolicy();
        policy.startMilliseconds = Integer.parseInt(tf_operation_hour.getText().isEmpty()
                ? "0" : tf_operation_hour.getText()) * 60 * 60 * 1000 + Integer.parseInt
                (tf_operation_minute.getText().isEmpty() ? "0" : tf_operation_minute.getText())
                * 60 * 1000 + Integer.parseInt(tf_operation_second.getText().isEmpty()
                ? "0" : tf_operation_second.getText()) * 1000;
        policy.enable = true;
        if (0 == cb_adjust_repeat_type.getSelectionModel().getSelectedIndex()) {
            policy.repeat = 127;
        } else {
            int repeatSum = 0;
            repeatSum += cb_sunday.isSelected() ? 64 : 0;
            repeatSum += cb_monday.isSelected() ? 32 : 0;
            repeatSum += cb_tuesday.isSelected() ? 16 : 0;
            repeatSum += cb_wednesday.isSelected() ? 8 : 0;
            repeatSum += cb_thursday.isSelected() ? 4 : 0;
            repeatSum += cb_friday.isSelected() ? 2 : 0;
            repeatSum += cb_saturday.isSelected() ? 1 : 0;
            if (0 == repeatSum) {
                showInfo(Contacts.getResourceValue("repeat_must_select_one"));
                return;
            }
            policy.repeat = repeatSum;
        }
        if (1 == cb_adjust_type.getSelectionModel().getSelectedIndex()) {
            TimingAdjustBrightnessArgument timingArgument = new
                    TimingAdjustBrightnessArgument(Integer.parseInt
                    (tf_brightness_value.getText()));
            policy.argument = timingArgument;
        } else {//Automatically set brightness
            List<AutoAdjustBrightnessArgument.EnvironmentAndScreenBrightInfo>
                    envAndScrList = new ArrayList<>();
            envAndScrList.add(new AutoAdjustBrightnessArgument
                    .EnvironmentAndScreenBrightInfo(500, 5));
            envAndScrList.add(new AutoAdjustBrightnessArgument
                    .EnvironmentAndScreenBrightInfo(20000, 20));
            envAndScrList.add(new AutoAdjustBrightnessArgument
                    .EnvironmentAndScreenBrightInfo(30000, 40));
            envAndScrList.add(new AutoAdjustBrightnessArgument
                    .EnvironmentAndScreenBrightInfo(40000, 60));
            envAndScrList.add(new AutoAdjustBrightnessArgument
                    .EnvironmentAndScreenBrightInfo(50000, 80));
            envAndScrList.add(new AutoAdjustBrightnessArgument
                    .EnvironmentAndScreenBrightInfo(60000, 100));
            AutoAdjustBrightnessArgument.OpticalFailureInfo failureInfo = new
                    AutoAdjustBrightnessArgument.OpticalFailureInfo();
            failureInfo.setEnable(true);
            failureInfo.setScreenBrightness(10);
            AutoAdjustBrightnessArgument autoArgument = new
                    AutoAdjustBrightnessArgument(65535, 0, 100, 0, 2,
                    envAndScrList, failureInfo);
            policy.argument = autoArgument;
        }
        for (BrightnessAdjustPolicy policy1 : policyList) {
            if (policy.startMilliseconds == policy1.startMilliseconds && (policy.repeat &
                    policy1.repeat) > 0) {
                showInfo(Contacts.getResourceValue("pls_modify_policy"));
                return;
            }
        }
        policyList.add(policy);
        showInfo(Contacts.getResourceValue("total_data") + policyList.size() + Contacts
                .getResourceValue("total_datas") + "：" +
                JSONUtil.toJsonString(policyList));
    }

    @FXML
    protected void onAdjustBrightness(ActionEvent ae) {
        //Set brightness manually
        if (0 == cb_adjust_type.getSelectionModel().getSelectedIndex()) {
            //AdjustModeInfo:type-BRIGHTNESS,SourceBean(type-1,nova Own platform/0,
            // Third-party platform platform-1, terminal/2, PC/3, tablet/4, web), mode-MANUALLY/AUTO
            NovaOpt.GetInstance().setBrightnessAdjustMode(Contacts.deviceOpt.getSn(), new
                            AdjustModeInfo
                            ("SCREEN_BRIGHTNESS", new SourceBean(0, 1), "MANUALLY")
                    , new OnResultListenerN<Integer, ErrorDetail>() {
                        @Override
                        public void onSuccess(IRequestBase requestBase, Integer response) {
                            showInfo(Contacts.getResourceValue("set_brightness_manual_successed"));
                            NovaOpt.GetInstance().setScreenBrightness(Contacts.deviceOpt.getSn(),
                                    (float) Integer.parseInt(tf_brightness_value.getText()), new
                                            OnResultListenerN<Integer, ErrorDetail>() {

                                                @Override
                                                public void onSuccess(IRequestBase requestBase,
                                                                      Integer response) {
//                                                    showInfo(Contacts.getResourceValue
//                                                            ("set_brightness_value_manual_successed") + "：" +
//                                                            response);
                                                }

                                                @Override
                                                public void onError(IRequestBase requestBase,
                                                                    ErrorDetail error) {
                                                    showInfo(Contacts.getResourceValue
                                                            ("set_brightness_value_manual_failed") + "：" + error
                                                            .toString());
                                                }
                                            });
                        }

                        @Override
                        public void onError(IRequestBase requestBase, ErrorDetail error) {
                            showInfo(Contacts.getResourceValue("set_brightness_manual_failed")
                                    + "：" + error.toString());
                        }
                    });
        } else {
            if (policyList.size() == 0) {
                showInfo(Contacts.getResourceValue("policy_list_is_null"));
                return;
            }
            NovaOpt.GetInstance().setBrightnessAdjustMode(Contacts.deviceOpt.getSn(), new
                            AdjustModeInfo
                            ("SCREEN_BRIGHTNESS", new SourceBean(0, 1), "AUTO")
                    , new OnResultListenerN<Integer, ErrorDetail>() {
                        @Override
                        public void onSuccess(IRequestBase requestBase, Integer response) {
                            showInfo(Contacts.getResourceValue("set_brightness_auto_successed"));

                            BrightnessAdjustPolicyEntity entity = new
                                    BrightnessAdjustPolicyEntity(true, policyList);
                            NovaOpt.GetInstance().setBrightnessPolicy(Contacts.deviceOpt.getSn(),
                                    entity, new OnResultListenerN<Integer, ErrorDetail>() {
                                        @Override
                                        public void onSuccess(IRequestBase requestBase,
                                                              Integer response) {
                                            showInfo(Contacts.getResourceValue
                                                    ("set_brightness_value_auto_successed"));
                                        }

                                        @Override
                                        public void onError(IRequestBase requestBase,
                                                            ErrorDetail error) {
                                            showInfo(Contacts.getResourceValue
                                                    ("set_brightness_value_auto_failed") + "：" +
                                                    error.toString());
                                        }
                                    });
                        }

                        @Override
                        public void onError(IRequestBase requestBase, ErrorDetail error) {
                            showInfo(Contacts.getResourceValue("set_brightness_auto_failed") + "："
                                    + error.toString());
                        }
                    });
        }
    }

    public void showInfo(String info) {
        Platform.runLater(() -> {
            Date day = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            ta_info.appendText("[" + df.format(day) + "]  " + info + "\r\n");
        });
    }
}
