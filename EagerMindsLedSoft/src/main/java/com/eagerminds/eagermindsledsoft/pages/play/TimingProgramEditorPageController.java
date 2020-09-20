/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerminds.eagermindsledsoft.pages.play;

import java.io.File;
import java.time.LocalDate;
import java.util.HashSet;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import novj.publ.api.ErrorCode;
import novj.publ.api.NovaOpt;
import novj.publ.api.beans.TimingParamBean;
import novj.publ.util.StringUtil;
import com.eagerminds.eagermindsledsoft.Contacts;
import com.eagerminds.eagermindsledsoft.pages.AddTimingParamListener;

/**
 * FXML Controller class
 *
 * @author user
 */
public class TimingProgramEditorPageController {
    @FXML
    Button btn_choose_program;//选择节目按钮
    @FXML
    Label lab_program_name;//显示节目名称
    @FXML
    private ComboBox cb_select_program_type;//选择节目播放方式  定期播放或者空闲时间播放
    @FXML
    private ComboBox cb_select_program_date;//选择日期   永久有效或者自定义起止日期
    @FXML
    private HBox hbox_selectdate;//显示或者隐藏选择起止日期按钮
    @FXML
    private DatePicker dp_select_start_date, dp_select_end_date;//点击选择开始和结束日期
    @FXML
    private ComboBox cb_select_repeat_style;//重复方式    每天或者自定义周几
    @FXML
    private HBox hbox_repeat_selectday;//显示或隐藏按星期选择重复方式
    @FXML
    private CheckBox cb_monday, cb_tuesday, cb_wednesday, cb_thursday, cb_friday, cb_saturday,
            cb_sunday;
    @FXML
    private TextField tf_start_hour, tf_start_minute, tf_start_second;//开始时间
    @FXML
    private TextField tf_end_hour, tf_end_minute, tf_end_second;//结束时间
    @FXML
    private Button btn_confirm, btn_cancel;//确定 取消按钮
    @FXML
    private Label lab_tag;//显示提示信息
    @FXML
    private VBox vb_date_area;
    private AddTimingParamListener listener;


    public void init(AddTimingParamListener listener) {
        this.listener = listener;
        hbox_selectdate.setVisible(false);
        hbox_selectdate.setManaged(false);
        hbox_repeat_selectday.setManaged(false);
        hbox_repeat_selectday.setVisible(false);

        ObservableList<String> playTypeOption = FXCollections.observableArrayList(Contacts
                .getResourceValue("timing_play_style"), Contacts.getResourceValue
                ("free_play_style"));
        cb_select_program_type.setItems(playTypeOption);
        cb_select_program_type.getSelectionModel().select(0);
        cb_select_program_type.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue,
                                Number newValue) {
                if (newValue.intValue() == 0) {
                    vb_date_area.setVisible(true);
                    vb_date_area.setManaged(true);
                } else {
                    vb_date_area.setVisible(false);
                    vb_date_area.setManaged(false);
                }
            }
        });


        ObservableList<String> repeatDateOptions = FXCollections.observableArrayList(Contacts
                .getResourceValue("valid_forever"), Contacts.getResourceValue
                ("custom_style"));
        cb_select_program_date.setItems(repeatDateOptions);
        cb_select_program_date.getSelectionModel().select(0);
        cb_select_program_date.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue,
                                Number newValue) {
                if (newValue.intValue() == 0) {
                    hbox_selectdate.setVisible(false);
                    hbox_selectdate.setManaged(false);
                } else if (newValue.intValue() == 1) {
                    hbox_selectdate.setVisible(true);
                    hbox_selectdate.setManaged(true);
                }
            }
        });


        ObservableList<String> selectDayOptions = FXCollections.observableArrayList(Contacts
                .getResourceValue("repeat_everyday"), Contacts.getResourceValue
                ("custom_style"));
        cb_select_repeat_style.setItems(selectDayOptions);
        cb_select_repeat_style.getSelectionModel().select(0);
        cb_select_repeat_style.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue,
                                Number newValue) {
                if (0 == newValue.intValue()) {
                    hbox_repeat_selectday.setManaged(false);
                    hbox_repeat_selectday.setVisible(false);
                } else if (1 == newValue.intValue()) {
                    hbox_repeat_selectday.setManaged(true);
                    hbox_repeat_selectday.setVisible(true);
                }
            }
        });
        dp_select_start_date.setValue(LocalDate.now());
        dp_select_end_date.setValue(LocalDate.now());
    }

    private String programName;

    /**
     * 选择节目文件夹
     *
     * @param actionEvent
     */
    @FXML
    protected void btn_choose_program(ActionEvent actionEvent) {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle(Contacts.getResourceValue("program_save_path"));
        File direciory = dc.showDialog(btn_choose_program.getScene().getWindow());
        if (null == direciory) {
            return;
        }
        String programPath = direciory.getAbsolutePath();
        if (!StringUtil.isEmpty(programPath)) {
            if (programPath.endsWith(File.separator)) {
                programPath = programPath.substring(0, programPath.length() - 1);
            }
            lab_program_name.setText(programPath.substring(programPath.lastIndexOf(File.separator) + 1));
            this.programName = programPath;
        }

    }

    @FXML
    protected void btn_confirm(ActionEvent actionEvent) {
        lab_tag.setText("");
        TimingParamBean bean = NovaOpt.GetInstance().createDefaultTimingParam();
        if (StringUtil.isEmpty(programName.trim())) {
            lab_tag.setText(Contacts.getResourceValue("not_select_program"));
            return;
        }
        bean.setProgramName(programName);
        //保存节目类型  定期播放或者空闲时间播放
        if (cb_select_program_type.getSelectionModel().getSelectedIndex() == 0) {
            bean.setProgramType(TimingParamBean.TIMING_PROGRAM);
        } else {
            bean.setProgramType(TimingParamBean.FREE_PROGRAM);
        }
        //保存节目开始和结束日期
        if (1 == cb_select_program_date.getSelectionModel().getSelectedIndex()) {
            bean.setStartDate(dp_select_start_date.getEditor().getText());
            bean.setEndDate(dp_select_end_date.getEditor().getText());
        }
        //保存每周几播放
        if (1 == cb_select_repeat_style.getSelectionModel().getSelectedIndex()) {
            HashSet days = new HashSet();
            if (cb_sunday.isSelected()) {
                days.add(1);
            }
            if (cb_monday.isSelected()) {
                days.add(2);
            }
            if (cb_tuesday.isSelected()) {
                days.add(3);
            }
            if (cb_wednesday.isSelected()) {
                days.add(4);
            }
            if (cb_thursday.isSelected()) {
                days.add(5);
            }
            if (cb_friday.isSelected()) {
                days.add(6);
            }
            if (cb_saturday.isSelected()) {
                days.add(7);
            }
            if (days.size() == 0 && bean.getProgramType() == TimingParamBean.TIMING_PROGRAM) {
                lab_tag.setText(Contacts.getResourceValue("not_select_time"));
                return;
            }
            bean.setWeekDays(days);
        }
        bean.setStartTime(tf_start_hour.getText() + ":" + tf_start_minute.getText() + ":" + tf_start_second.getText());
        bean.setEndTime(tf_end_hour.getText() + ":" + tf_end_minute.getText() + ":" + tf_end_second.getText());
        bean.setId(NovaOpt.GetInstance().getTimingPlayParams().size() + 1);
        int tag = NovaOpt.GetInstance().addTimingPlayParam(bean);
        if (tag != ErrorCode.Err_None) {
            lab_tag.setText(Contacts.getResourceValue("error_code") + "：" + tag);
        } else {
            listener.addTimingParam(bean);
        }
    }
}
