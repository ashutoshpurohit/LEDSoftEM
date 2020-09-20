/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerminds.eagermindsledsoft.pages;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import novj.platform.vxkit.common.bean.task.BaseTaskItemBean;
import novj.platform.vxkit.common.result.OnResultListenerN;
import novj.publ.api.NovaOpt;
import novj.publ.net.exception.ErrorDetail;
import novj.publ.net.svolley.Request.IRequestBase;
import com.eagerminds.eagermindsledsoft.Contacts;
/**
 * FXML Controller class
 *
 * @author user
 */
public class TimeRebootPageController {
    @FXML
    TextField tf_reboot_hour;
    @FXML
    TextField tf_reboot_minute;
    @FXML
    TextField tf_reboot_second;
    @FXML
    ComboBox cb_reboot_repeat_type;
    @FXML
    Button btn_confirm;
    @FXML
    CheckBox cb_monday, cb_tuesday, cb_wednesday, cb_thursday, cb_friday, cb_saturday, cb_sunday;
    @FXML
    HBox hbox_brightness_selectday;

    private int mRebootHour = 0, mRebootMinute = 0, mRebootSecond = 0;

    public void initData() {
        hbox_brightness_selectday.setVisible(false);
        hbox_brightness_selectday.setManaged(false);
        tf_reboot_hour.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (newValue.matches("\\d*") && newValue.length() < 3) {
                    if (newValue.length() > 0) {
                        if (Integer.parseInt(newValue) < 24) {
                            mRebootHour = Integer.parseInt(newValue);
                        } else {
                            tf_reboot_hour.setText(oldValue);
                        }
                    }
                } else {
                    tf_reboot_hour.setText(oldValue);
                }
            }
        });
        tf_reboot_minute.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (newValue.matches("\\d*") && newValue.length() < 3) {
                    if (newValue.length() > 0) {
                        if (Integer.parseInt(newValue) < 60) {
                            mRebootMinute = Integer.parseInt(newValue);
                        } else {
                            tf_reboot_minute.setText(oldValue);
                        }
                    }
                } else {
                    tf_reboot_minute.setText(oldValue);
                }
            }
        });
        tf_reboot_second.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (newValue.matches("\\d*") && newValue.length() < 3) {
                    if (newValue.length() > 0) {
                        if (Integer.parseInt(newValue) < 60) {
                            mRebootSecond = Integer.parseInt(newValue);
                        } else {
                            tf_reboot_second.setText(oldValue);
                        }
                    }
                } else {
                    tf_reboot_second.setText(oldValue);
                }
            }
        });

        ObservableList<String> repeatOptions = FXCollections.observableArrayList(Contacts
                .getResourceValue("repeat_everyday"), Contacts.getResourceValue
                ("repeat_everyday_by_week"));
        cb_reboot_repeat_type.setItems(repeatOptions);
        cb_reboot_repeat_type.getSelectionModel().select(0);
        cb_reboot_repeat_type.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (0 == cb_reboot_repeat_type.getSelectionModel().getSelectedIndex()) {
                    hbox_brightness_selectday.setManaged(false);
                    hbox_brightness_selectday.setVisible(false);
                } else {
                    hbox_brightness_selectday.setManaged(true);
                    hbox_brightness_selectday.setVisible(true);
                }
            }
        });
    }

    @FXML
    public void onConfirmRebootTime(MouseEvent me) {
        List<BaseTaskItemBean.ConditionsBean> cronList = new ArrayList<>();
        StringBuilder cronition = new StringBuilder();
        if (0 == cb_reboot_repeat_type.getSelectionModel().getSelectedIndex()) {
            cronition.append(mRebootSecond + " " + mRebootMinute + " " + mRebootHour + " * * ?");
        } else {
            cronition.append(mRebootSecond + " " + mRebootMinute + " " + mRebootHour + " ? * ");
            cronition.append(cb_sunday.isSelected() ? "1," : "");
            cronition.append(cb_monday.isSelected() ? "2," : "");
            cronition.append(cb_tuesday.isSelected() ? "3," : "");
            cronition.append(cb_wednesday.isSelected() ? "4," : "");
            cronition.append(cb_thursday.isSelected() ? "5," : "");
            cronition.append(cb_friday.isSelected() ? "6," : "");
            cronition.append(cb_saturday.isSelected() ? "7," : "");
        }
        BaseTaskItemBean.ConditionsBean conditionsBean = new BaseTaskItemBean.ConditionsBean();
        List<String> crons = new ArrayList<>();
        String cron;
        if (cronition.toString().endsWith(",")) {
            cron = cronition.toString().substring(0, cronition.toString().length() - 1);
        } else {
            cron = cronition.toString();
        }
        crons.add(cron);
        conditionsBean.setCron(crons);
        conditionsBean.setEnable(true);
        cronList.add(conditionsBean);
        NovaOpt.GetInstance().reboot(Contacts.deviceOpt.getSn(), "", cronList, false,
                new OnResultListenerN<Integer, ErrorDetail>() {
                    @Override
                    public void onSuccess(IRequestBase requestBase, Integer response) {
                        Platform.runLater(() -> {
                            System.out.println("reboot success: " + response + ",cron: " + cron);
                            Alert infoAlert = new Alert(Alert.AlertType.INFORMATION,Contacts.getResourceValue
                                    ("success_publish_reboot_task"),new ButtonType(Contacts.getResourceValue("confirm")));
                            infoAlert.setHeaderText(null);
                            infoAlert.setTitle(Contacts.getResourceValue
                                    ("alert_title_information"));
                            infoAlert.showAndWait();
                            ((Stage) btn_confirm.getScene().getWindow()).close();
                        });
                    }

                    @Override
                    public void onError(IRequestBase requestBase, ErrorDetail error) {
                        System.out.println("reboot error: " + error.toString());
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION, Contacts
                                    .getResourceValue("fail_publish_reboot_task"));
                            alert.setTitle(Contacts.getResourceValue("alert_title_information"));
                            alert.setHeaderText(null);
                            alert.showAndWait();
                        });
                    }
                });
    }

}
