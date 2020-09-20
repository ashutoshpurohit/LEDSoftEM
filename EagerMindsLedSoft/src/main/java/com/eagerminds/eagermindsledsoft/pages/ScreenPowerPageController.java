/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerminds.eagermindsledsoft.pages;

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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import novj.platform.vxkit.common.bean.AdjustModeInfo;
import novj.platform.vxkit.common.bean.SourceBean;
import novj.platform.vxkit.common.bean.monitor.StateBean;
import novj.platform.vxkit.common.bean.task.ScreenTaskBean;
import novj.platform.vxkit.common.easypluto.contract.ScreenContract;
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
public class ScreenPowerPageController {
    @FXML
    ComboBox cb_power_switch, cb_power_switch_mode, cb_adjust_repeat_type;
    @FXML
    TextArea ta_show;
    @FXML
    HBox hbox_screenpower_selectday, hbox_screenpower_repeat, hbox_screenpower_policy, hbox_policy_time;
    @FXML
    CheckBox cb_monday, cb_tuesday, cb_wednesday, cb_thursday, cb_friday, cb_saturday, cb_sunday;
    @FXML
    TextField tf_policy_hour, tf_policy_minute, tf_policy_second;

    private ScreenTaskBean screenTaskBean = new ScreenTaskBean();

    public void init() {
        screenTaskBean.setType("SCREENPOWER");
        screenTaskBean.setEnable(true);
        SourceBean sourceBean = new SourceBean(0, 2);
        screenTaskBean.setSource(sourceBean);
//        List<ScreenTaskBean.ScreenCondition> screenConditions = new ArrayList<>();
//        ScreenTaskBean.ScreenCondition openCondition = new ScreenTaskBean.ScreenCondition();
//        openCondition.setAction(ScreenContract.ScreenOnOffState.STATE_OPEN);
//        openCondition.setEnable(true);
//        openCondition.setCron(new ArrayList<>());
//        ScreenTaskBean.ScreenCondition closeCondition = new ScreenTaskBean.ScreenCondition();
//        closeCondition.setEnable(true);
//        closeCondition.setAction(ScreenContract.ScreenOnOffState.STATE_CLOSE);
//        closeCondition.setCron(new ArrayList<>());
//        screenConditions.add(openCondition);
//        screenConditions.add(closeCondition);
        screenTaskBean.setConditions(new ArrayList<ScreenTaskBean.ScreenCondition>());

        tf_policy_hour.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!(newValue.matches("\\d*") &&
                        newValue.length() < 3 &&
                        newValue.length() > 0 &&
                        (Integer.parseInt(newValue) < 24))) {
                    tf_policy_minute.setText(oldValue);
                }
            }
        });
        tf_policy_minute.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!(newValue.matches("\\d*") &&
                        newValue.length() < 3 &&
                        newValue.length() > 0 &&
                        (Integer.parseInt(newValue) < 60))) {
                    tf_policy_minute.setText(oldValue);
                }
            }
        });
        tf_policy_second.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!(newValue.matches("\\d*") &&
                        newValue.length() < 3 &&
                        newValue.length() > 0 &&
                        (Integer.parseInt(newValue) < 60))) {
                    tf_policy_second.setText(oldValue);
                }
            }
        });

        ObservableList<String> openOptions = FXCollections.observableArrayList(
                Contacts.getResourceValue("power_open"), Contacts.getResourceValue("power_close"));
        cb_power_switch.setItems(openOptions);
        cb_power_switch.getSelectionModel().select(0);

        ObservableList<String> modeOptions = FXCollections.observableArrayList(
                Contacts.getResourceValue("set_manual"),
                Contacts.getResourceValue("set_timing"));
        cb_power_switch_mode.setItems(modeOptions);

        cb_power_switch_mode.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue observable, String oldValue, String newValue) {
                if (Contacts.getResourceValue("set_manual").equals(newValue)) {
                    hbox_screenpower_repeat.setVisible(false);
                    hbox_screenpower_repeat.setManaged(false);

                    hbox_screenpower_selectday.setVisible(false);
                    hbox_screenpower_selectday.setManaged(false);

                    hbox_screenpower_policy.setVisible(false);
                    hbox_screenpower_policy.setManaged(false);

                    hbox_policy_time.setVisible(false);
                    hbox_policy_time.setManaged(false);
                } else {
                    hbox_screenpower_repeat.setVisible(true);
                    hbox_screenpower_repeat.setManaged(true);

                    hbox_screenpower_policy.setManaged(true);
                    hbox_screenpower_policy.setVisible(true);

                    hbox_policy_time.setVisible(true);
                    hbox_policy_time.setManaged(true);

                    if (0 == cb_adjust_repeat_type.getSelectionModel().getSelectedIndex()) {
                        hbox_screenpower_selectday.setVisible(false);
                        hbox_screenpower_selectday.setManaged(false);
                    } else {
                        hbox_screenpower_selectday.setVisible(true);
                        hbox_screenpower_selectday.setManaged(true);
                    }
                }
            }
        });
        cb_power_switch_mode.getSelectionModel().select(0);

        ObservableList<String> repeatOptions = FXCollections.observableArrayList(Contacts
                .getResourceValue("repeat_everyday"), Contacts.getResourceValue
                ("repeat_everyday_by_week"));
        cb_adjust_repeat_type.setItems(repeatOptions);
        cb_adjust_repeat_type.getSelectionModel().select(0);
        cb_adjust_repeat_type.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue observable, String oldValue, String newValue) {
                if (Contacts.getResourceValue("repeat_everyday").equals(newValue)) {
                    hbox_screenpower_selectday.setVisible(false);
                    hbox_screenpower_selectday.setManaged(false);
                } else {
                    hbox_screenpower_selectday.setVisible(true);
                    hbox_screenpower_selectday.setManaged(true);
                }
            }
        });
    }

    /**
     * Set the power state (screen on or off)
     *
     * @param actionEvent
     */
    @FXML
    protected void onActionSetPowerState(ActionEvent actionEvent) {
        int selectIndex = cb_power_switch.getSelectionModel().getSelectedIndex();
        NovaOpt.GetInstance().setScreenPowerState(Contacts.deviceOpt.getSn(), 0 == selectIndex ?
                true : false, new OnResultListenerN<Integer, ErrorDetail>() {
            @Override
            public void onSuccess(IRequestBase requestBase, Integer response) {
                showInfo("Set successfully");
            }

            @Override
            public void onError(IRequestBase requestBase, ErrorDetail error) {
                showInfo(error.description);
            }
        });
    }

    /**
     * Get power status (screen on or off)
     *
     * @param actionEvent
     */
    @FXML
    protected void onActionGetPowerState(ActionEvent actionEvent) {
        NovaOpt.GetInstance().getScreenPowerState(Contacts.deviceOpt.getSn(), new OnResultListenerN<StateBean<String>, ErrorDetail>() {
            @Override
            public void onSuccess(IRequestBase requestBase, StateBean<String> response) {
                showInfo(response.getState());
            }

            @Override
            public void onError(IRequestBase requestBase, ErrorDetail error) {
                showInfo(error.description);
            }
        });
    }

    /**
     * Get power mode (manual or timing)
     *
     * @param actionEvent
     */
    @FXML
    protected void onActionGetPowerMode(ActionEvent actionEvent) {
        NovaOpt.GetInstance().getScreenPowerMode(Contacts.deviceOpt.getSn(), new OnResultListenerN<AdjustModeInfo, ErrorDetail>() {
            @Override
            public void onSuccess(IRequestBase requestBase, AdjustModeInfo response) {
                showInfo(JSONUtil.toJsonStringByFastJson(response));
            }

            @Override
            public void onError(IRequestBase requestBase, ErrorDetail error) {
                showInfo(error.description);
            }
        });
    }

    /**
     * Set the power mode (manual or timing)
     *
     * @param actionEvent
     */
    @FXML
    protected void onActionSetPowerMode(ActionEvent actionEvent) {
        String powerMode = 0 == cb_power_switch_mode.getSelectionModel().getSelectedIndex() ?
                ScreenContract.AdjustMode.MODE_MANUALLY : ScreenContract.AdjustMode.MODE_AUTO;
        NovaOpt.GetInstance().setScreenPowerMode(Contacts.deviceOpt.getSn(), powerMode, new OnResultListenerN<Integer, ErrorDetail>() {
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


    /**
     * Get the timing switch screen strategy
     *
     * @param actionEvent
     */
    @FXML
    protected void onActionGetPowerPolicy(ActionEvent actionEvent) {
        NovaOpt.GetInstance().getScreenPowerPolicy(Contacts.deviceOpt.getSn(), new OnResultListenerN<ScreenTaskBean, ErrorDetail>() {
            @Override
            public void onSuccess(IRequestBase requestBase, ScreenTaskBean response) {
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
    protected void onActionAddPolicy(ActionEvent actionEvent) {
        int stateIndex = cb_power_switch.getSelectionModel().getSelectedIndex();
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
        ScreenTaskBean.ScreenCondition screenCondition = new ScreenTaskBean.ScreenCondition();
        screenCondition.setAction(0 == stateIndex?ScreenContract.ScreenOnOffState.STATE_OPEN:
                ScreenContract.ScreenOnOffState.STATE_CLOSE);
        screenCondition.setEnable(true);
        List<String> crons = new ArrayList<>();
        crons.add(cron.toString());
        screenCondition.setCron(crons);
        screenTaskBean.getConditions().add(screenCondition);
//        screenTaskBean.getConditions().get(stateIndex).getCron().add(cron.toString());
        showInfo(JSONUtil.toJsonStringByFastJson(screenTaskBean));
        addPolicy = true;

    }

    private boolean addPolicy = false;

    /**
     * Set time switch screen strategy
     *
     * @param actionEvent
     */
    @FXML
    protected void onActionSetPowerPolicy(ActionEvent actionEvent) {
        if (!addPolicy){
            showInfo("Please add a switch screen strategy first");
            return;
        }
        List<ScreenTaskBean.ScreenCondition> screenConditions = screenTaskBean.getConditions();
        for (int x = 1; x >= 0; x--) {
            if (screenConditions.get(x).getCron().size() == 0){
                screenConditions.remove(x);
            }
        }
        NovaOpt.GetInstance().setScreenPowerPolicy(Contacts.deviceOpt.getSn(), screenTaskBean, new OnResultListenerN<Integer, ErrorDetail>() {
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
