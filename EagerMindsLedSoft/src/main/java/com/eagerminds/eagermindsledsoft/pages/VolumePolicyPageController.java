/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerminds.eagermindsledsoft.pages;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import novj.platform.vxkit.common.bean.SourceBean;
import novj.platform.vxkit.common.bean.task.VolumeTaskBean;
import novj.platform.vxkit.common.result.OnResultListenerN;
import novj.publ.api.NovaOpt;
import novj.publ.net.exception.ErrorDetail;
import novj.publ.net.svolley.Request.IRequestBase;
import novj.publ.util.JSONUtil;
import com.eagerminds.eagermindsledsoft.Contacts;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * FXML Controller class
 *
 * @author user
 */
public class VolumePolicyPageController {

    @FXML
    ComboBox<String> cb_adjust_repeat_type;
    @FXML
    TextField volume_value;
    @FXML
    TextArea ta_show;
    @FXML
    HBox hbox_volume_selectday, hbox_volume_repeat, hbox_volume_policy, hbox_policy_time;
    @FXML
    CheckBox cb_monday, cb_tuesday, cb_wednesday, cb_thursday, cb_friday, cb_saturday, cb_sunday;
    @FXML
    TextField tf_policy_hour, tf_policy_minute, tf_policy_second;

    /**
     * Volume timing strategy
     */
    private VolumeTaskBean volumeTaskBean = new VolumeTaskBean();

    public void init() {
        volumeTaskBean.setType("VOLUME");
        volumeTaskBean.setEnable(true);//Conditional execution strategy
        SourceBean sourceBean = new SourceBean(0, 2);
        volumeTaskBean.setSource(sourceBean);
        volumeTaskBean.setConditions(new ArrayList<VolumeTaskBean.VolumeCondition>());
        volume_value.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.matches("\\d*") && newValue.length() < 4) {
                if (newValue.length() > 0 && Integer.parseInt(newValue) > 100) {
                    volume_value.setText(oldValue);
                }
            } else {
                volume_value.setText(oldValue);
            }
        });

        tf_policy_hour.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.matches("\\d*") && newValue.length() < 3) {
                    if (newValue.length() > 0 && Integer.parseInt(newValue) >= 24) {
                        tf_policy_hour.setText(oldValue);
                    }
                } else {
                    tf_policy_hour.setText(oldValue);
                }
            }
        });

        tf_policy_minute.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.matches("\\d*") && newValue.length() < 3) {
                    if (newValue.length() > 0 && Integer.parseInt(newValue) >= 60) {
                        tf_policy_minute.setText(oldValue);
                    }
                } else {
                    tf_policy_minute.setText(oldValue);
                }
            }
        });

        tf_policy_second.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.matches("\\d*") && newValue.length() < 3) {
                    if (newValue.length() > 0 && Integer.parseInt(newValue) >= 60) {
                        tf_policy_second.setText(oldValue);
                    }
                } else {
                    tf_policy_second.setText(oldValue);
                }
            }
        });

        ObservableList<String> repeatOptions = FXCollections.observableArrayList(Contacts
                .getResourceValue("repeat_everyday"), Contacts.getResourceValue
                ("repeat_everyday_by_week"));

        cb_adjust_repeat_type.setItems(repeatOptions);
        cb_adjust_repeat_type.getSelectionModel().select(0);
        hbox_volume_selectday.setVisible(false);
        hbox_volume_selectday.setManaged(false);

        cb_adjust_repeat_type.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue observable, String oldValue, String newValue) {
                    hbox_volume_repeat.setVisible(true);
                    hbox_volume_repeat.setManaged(true);
                    hbox_volume_policy.setManaged(true);
                    hbox_volume_policy.setVisible(true);
                    hbox_policy_time.setVisible(true);
                    hbox_policy_time.setManaged(true);

                    if ((Contacts.getResourceValue("repeat_everyday").equals(newValue))) {
                            hbox_volume_selectday.setVisible(false);
                            hbox_volume_selectday.setManaged(false);
                        } else {
                            hbox_volume_selectday.setVisible(true);
                            hbox_volume_selectday.setManaged(true);
                    }
                }
        });
    }

    /**
     * Get timing volume strategy
     *
     * @param actionEvent
     */
    @FXML
    protected void onActionGetVolumePolicy(ActionEvent actionEvent) {
        NovaOpt.GetInstance().getTimingVolumePolicy(Contacts.deviceOpt.getSn(), new OnResultListenerN<VolumeTaskBean, ErrorDetail>() {
            @Override
            public void onSuccess(IRequestBase requestBase, VolumeTaskBean response) {
                showInfo(JSONUtil.toJsonStringByFastJson(response));
            }

            @Override
            public void onError(IRequestBase requestBase, ErrorDetail error) {
                showInfo("Error:" + error.description);
            }
        });
    }

    /**
     * Add time switch screen strategy
     *
     * @param actionEvent
     */
    @FXML
    protected void onActionAddVolumePolicy(ActionEvent actionEvent) {
        StringBuilder cron = new StringBuilder();
        if (0 == cb_adjust_repeat_type.getSelectionModel().getSelectedIndex()) {
            //weekly
            cron.append(tf_policy_second.getText() + " " + tf_policy_minute.getText() + " " +
                    tf_policy_hour.getText() + " * * ?");
        } else {
            //Select a few days
            cron.append(tf_policy_second.getText() + " " + tf_policy_minute.getText() + " " +
                    tf_policy_hour.getText() + " ? * ");
            cron.append(cb_sunday.isSelected() ? "1," : "");
            cron.append(cb_monday.isSelected() ? "2," : "");
            cron.append(cb_tuesday.isSelected() ? "3," : "");
            cron.append(cb_wednesday.isSelected() ? "4," : "");
            cron.append(cb_thursday.isSelected() ? "5," : "");
            cron.append(cb_friday.isSelected() ? "6," : "");
            cron.append(cb_saturday.isSelected() ? "7" : "");
        }
        VolumeTaskBean.VolumeCondition screenCondition = new VolumeTaskBean.VolumeCondition();
        screenCondition.setEnable(true);
        List<String> crons = new ArrayList<>();
        crons.add(cron.toString());
        screenCondition.setCron(crons);
        screenCondition.setValue(Float.parseFloat(volume_value.getText()));
        if(volumeTaskBean.getConditions() != null){
            volumeTaskBean.getConditions().add(screenCondition);
        }
        showInfo(JSONUtil.toJsonStringByFastJson(volumeTaskBean));
        addPolicy = true;
    }

    private boolean addPolicy = false;

    /**
     * Set time switch screen strategy
     *
     * @param actionEvent
     */
    @FXML
    protected void onActionSetVolumePolicy(ActionEvent actionEvent) {
        if (!addPolicy){
            showInfo("Please add volume strategy first");
            return;
        }
        List<VolumeTaskBean.VolumeCondition> screenConditions = volumeTaskBean.getConditions();
        NovaOpt.GetInstance().setTimingVolumePolicy(Contacts.deviceOpt.getSn(), volumeTaskBean, new OnResultListenerN<Integer, ErrorDetail>() {
            @Override
            public void onSuccess(IRequestBase requestBase, Integer response) {
                showInfo("Set successfully");
            }

            @Override
            public void onError(IRequestBase requestBase, ErrorDetail error) {
                showInfo("Error:" + error.description);
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
