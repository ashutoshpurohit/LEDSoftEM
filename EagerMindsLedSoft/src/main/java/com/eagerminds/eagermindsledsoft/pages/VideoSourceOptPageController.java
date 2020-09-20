/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerminds.eagermindsledsoft.pages;

import com.eagerminds.eagermindsledsoft.DeviceInfo;
import com.eagerminds.eagermindsledsoft.PolicyInfo;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import novj.platform.vxkit.common.bean.VideoControlInfo;
import novj.platform.vxkit.common.bean.VideoTimingPolicy;
import novj.platform.vxkit.common.bean.edid.EdidInfo;
import novj.platform.vxkit.common.result.OnResultListenerN;
import novj.publ.api.NovaOpt;
import novj.publ.net.exception.ErrorDetail;
import novj.publ.net.svolley.Request.IRequestBase;
import novj.publ.util.cron.CronBean;
import novj.publ.util.cron.CronItemBean;
import novj.publ.util.cron.CronUtil;
import com.eagerminds.eagermindsledsoft.Contacts;
import com.eagerminds.eagermindsledsoft.ViewModel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * FXML Controller class
 *
 * @author user
 */
public class VideoSourceOptPageController {

    private DeviceInfo deviceInfo = null;
    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }
    public void setDeviceInfo(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    @FXML
    Label labelDevice, labelRepeatHeader;
    @FXML
    ToggleGroup group, tgTimingType;
    @FXML
    GridPane paneManual,paneManualType_hdmi,paneManualType_inner, paneHDMI;
    @FXML
    GridPane paneWeek,paneTiming,paneTiming_Add;
    @FXML
    RadioButton rbManual, rbTiming, rbHDMI, rbPolicyHDMI, rbPolicyInner;
    @FXML
    ComboBox<String> cbManualType,cbRepeatType;
    @FXML
    Button btnRead, btnSet, btnDel, btnAdd, btnAddPolicy;
    @FXML
    CheckBox ckbManualHdmiScale, ckbHdmiScale, cb1,cb2,cb3,cb4,cb5,cb6,cb7,ckbTimingScale;
    @FXML
    TextField tfHdmiX, tfHdmiY, tfManualHdmiX, tfManualHdmiY, tf_hour, tf_minute, tf_second;
    @FXML
    TextField tfManualInnerX, tfManualInnerY, tfTimingX, tfTimingY, tfManualHdmiWidth, tfManualHdmiHeight;
    @FXML
    TextField tfTimingWidth, tfTimingHeigth, tfHdmiWidth, tfHdmiHeight;
    @FXML
    TextArea taInfo;
    @FXML
    ListView lvPolicy;

    /** HDMI priority*/
    final int MODE_HDMI = 0;
    /**timing*/
    final int MODE_TIMING = 2;
    /**Manual*/
    final int MODE_MANUAL = 1;
    /**internal*/
    final int SOURCE_INSIDE = 0;
    /**HDMI*/
    final int SOURCE_HDMI = 1;

    CheckBox[] cbWeeks = new CheckBox[7];
    public void initData() {

        //region radiobutton
        group.selectedToggleProperty().addListener((changed, oldVal, newVal) -> {
            if(null == newVal) {
                return;
            }
            groupSelectedChanged(newVal);
        });
        group.selectToggle(rbManual);
        //endregion
        //region combobox Manual
        List<String> strs = new ArrayList<>();
        strs.add(Contacts.getResourceValue("Inner"));
        strs.add("HDMI");
        ObservableList<String> listManualType = FXCollections.observableArrayList(strs);
        cbManualType.setItems(listManualType);
        cbManualType.getSelectionModel().selectedItemProperty().addListener((ChangeListener) (observable, oldValue, newValue) -> cbManualTypeChanged(newValue));
        cbManualType.getSelectionModel().select(0);
        //endregion
        //region combobox Timing
        List<String> strs1 = new ArrayList<>();
        strs1.add(Contacts.getResourceValue("EveryDay"));
        strs1.add(Contacts.getResourceValue("Custom"));
        ObservableList<String> listManualType1 = FXCollections.observableArrayList(strs1);
        cbRepeatType.setItems(listManualType1);
        cbRepeatType.getSelectionModel().selectedItemProperty().addListener((ChangeListener) (observable, oldValue, newValue) -> cbRepeatTypeChanged(newValue));
        cbRepeatType.getSelectionModel().select(0);
        //endregion
        //region ListView
        lvPolicy.setItems(ViewModel.policyInfos);
        lvPolicy.setCellFactory(e -> new ListCell<PolicyInfo>() {
            @Override
            protected void updateItem(PolicyInfo item, boolean empty) {
                super.updateItem(item, empty);
                if (null != item && !empty) {
                    HBox hb = new HBox();
                    Label tfTime = new Label();
                    tfTime.setFont(Font.font(12));
                    tfTime.setPrefSize(80, 30);
                    tfTime.setTextAlignment(TextAlignment.CENTER);
                    tfTime.setText(item.getTime());

                    Label tfType = new Label();
                    tfType.setFont(Font.font(12));
                    tfType.setPrefSize(80, 30);
                    tfType.setTextAlignment(TextAlignment.CENTER);
                    tfType.setText(item.getType());

                    Label tfRepeat = new Label();
                    tfRepeat.setFont(Font.font(12));
                    tfRepeat.setPrefSize(350, 30);
                    tfRepeat.setTextAlignment(TextAlignment.CENTER);
                    tfRepeat.setText(item.getRepeat());

                    Label tfEnable = new Label();
                    tfEnable.setFont(Font.font(12));
                    tfEnable.setPrefSize(70, 30);
                    tfEnable.setTextAlignment(TextAlignment.CENTER);
                    tfEnable.setText(item.getIsEnable());

                    hb.getChildren().add(tfTime);
                    hb.getChildren().add(tfType);
                    hb.getChildren().add(tfRepeat);
                    hb.getChildren().add(tfEnable);
                    hb.setAlignment(Pos.CENTER_LEFT);
                    setGraphic(hb);
                } else {
                    setGraphic(null);
                }
            }
        });
        //endregion
        //region weeks checkbox
        cbWeeks[0] = cb7;
        cbWeeks[1] = cb1;
        cbWeeks[2] = cb2;
        cbWeeks[3] = cb3;
        cbWeeks[4] = cb4;
        cbWeeks[5] = cb5;
        cbWeeks[6] = cb6;
        //endregion
        labelDevice.setText("SN: " + deviceInfo.getSn());
        btn_Read_pressed(null);
        initTextData();
        //setTVersionWorkMode();

    }
    private void initTextData() {
        TextField[] tf = new TextField[14];
        tf[0] = tfHdmiX;
        tf[1] = tfHdmiY;
        tf[2] = tfManualHdmiX;
        tf[3] = tfManualHdmiY;
        tf[4] = tfManualInnerX;
        tf[5] = tfManualInnerY;
        tf[6] = tfTimingX;
        tf[7] = tfTimingY;
        tf[8] = tfManualHdmiWidth;
        tf[9] = tfManualHdmiHeight;
        tf[10] = tfTimingWidth;
        tf[11] = tfTimingHeigth;
        tf[12] = tfHdmiWidth;
        tf[13] = tfHdmiHeight;
        for (TextField item :
                tf) {
            item.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    item.setText(oldValue);
                }
            });
        }

        tf_hour.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                tf_hour.setText(oldValue);
                return;
            }
            //0-23 hours
            if(newValue.isEmpty()){
                return;
            }

            if(Integer.parseInt(newValue) < 0 ||
                    Integer.parseInt(newValue) > 23){
                tf_hour.setText(oldValue);
                return;
            }
        });

        TextField[] tfTime = new TextField[2];
        tfTime[0] = tf_minute;
        tfTime[1] = tf_second;
        for (TextField item :
                tfTime) {
            item.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    item.setText(oldValue);
                    return;
                }
                //0-59
                if(newValue.isEmpty()){
                    return;
                }

                if(Integer.parseInt(newValue) < 0 ||
                        Integer.parseInt(newValue) > 59){
                    item.setText(oldValue);
                    return;
                }
            });
        }
    }
    private void setTVersionWorkMode(){
        //The T1\T3 board does not support external video sources. You can call the setting interface, but it is invalid; here is the restriction through the interface.
        if(deviceInfo.getProductName().equals("T1") ||
                deviceInfo.getProductName().equals("T3 ")){
            cbManualType.setDisable(true);
            rbHDMI.setDisable(true);
            rbPolicyHDMI.setDisable(true);
            ckbTimingScale.setDisable(true);
            tfTimingX.setDisable(true);
            tfTimingY.setDisable(true);
            tfTimingWidth.setDisable(true);
            tfTimingHeigth.setDisable(true);
        }
    }
    @FXML
    protected void btn_Read_pressed(ActionEvent event){
        ViewModel.policyInfos.removeAll(ViewModel.policyInfos);
        NovaOpt.GetInstance().getVideoControlInfo(deviceInfo.getSn(), new OnResultListenerN<VideoControlInfo, ErrorDetail>() {
            @Override
            public void onSuccess(IRequestBase iRequestBase, VideoControlInfo videoControlInfo) {
                updateUI(videoControlInfo);
                showInfo("read ok");
            }

            @Override
            public void onError(IRequestBase iRequestBase, ErrorDetail errorDetail) {
                showInfo(errorDetail.description);
            }
        });
    }
    @FXML
    protected void btn_Set_pressed(ActionEvent event){

        VideoControlInfo videoControlInfo = null;
        if (rbManual == group.getSelectedToggle()) {
            videoControlInfo = getVideoControlInfoByManual();
        }else if(rbTiming == group.getSelectedToggle()){
            videoControlInfo = getVideoControlInfoByTiming();
        }else if(rbHDMI == group.getSelectedToggle()){
            videoControlInfo = getVideoControlInfoByHDMI();
        }

        NovaOpt.GetInstance().setVideoControlInfo(deviceInfo.getSn(), videoControlInfo, new OnResultListenerN() {
            @Override
            public void onSuccess(IRequestBase iRequestBase, Object o) {
                showInfo("set ok");
            }

            @Override
            public void onError(IRequestBase iRequestBase, Object o) {
                showInfo("set error");
            }
        });
    }
    @FXML
    protected void btn_Del_pressed(ActionEvent event){
        if(-1 != lvPolicy.getSelectionModel().getSelectedIndex()){
            ViewModel.policyInfos.remove(lvPolicy.getSelectionModel().getSelectedIndex());
        }
    }
    @FXML
    protected void btn_Add_pressed(ActionEvent event){
        paneTiming.setVisible(false);
        paneTiming_Add.setVisible(true);
    }
    @FXML
    protected void btn_AddPolicy_pressed(ActionEvent event){
        PolicyInfo policyInfo = new PolicyInfo();
        //region get info
        if(rbPolicyInner.isSelected()){
            policyInfo.setType(Contacts.getResourceValue("Inner"));
            policyInfo.videoTimingPolicy.setSource(0);
        }else{
            policyInfo.setType("HDMI");
            policyInfo.videoTimingPolicy.setSource(1);
        }

        int hour = Integer.parseInt(tf_hour.getText());
        int minute = Integer.parseInt(tf_minute.getText());
        int second = Integer.parseInt(tf_second.getText());
        policyInfo.setTime(String.format("%02d:%02d:%02d", hour, minute, second));
        //region dayOfWeek
        int[] dayOfWeeks;
        if(0 == cbRepeatType.getSelectionModel().getSelectedIndex()){
            policyInfo.setRepeat(Contacts.getResourceValue("EveryDay"));
            dayOfWeeks = new int[7];
            for (int i=1; i<=7; i++){
                dayOfWeeks[i-1] = i;
            }
        }else{
            int selectedCount = 0;
            for (CheckBox cb :
                    cbWeeks) {
                if (cb.isSelected()){
                    selectedCount++;
                }
            }
            dayOfWeeks = new int[selectedCount];
            int nIndex = 0;
            String sRepeat = "";
            for(int i=0; i<7; i++){
                if (cbWeeks[i].isSelected()){
                    dayOfWeeks[nIndex++] = i+1;
                    sRepeat += transWeek2describe(i+1) + "/";
                }
            }
            policyInfo.setRepeat(sRepeat);
        }
        //endregion
        CronItemBean dayOfWeek = new CronItemBean(dayOfWeeks);
        policyInfo.videoTimingPolicy.setCron(CronUtil.createCron(second, minute, hour, null, null, dayOfWeek));
        policyInfo.setIsEnable("√");
        //endregion
        if(isExistedObj(policyInfo)){
            showInfo("Existed!");
        } else{
            ViewModel.policyInfos.add(policyInfo);
            paneTiming.setVisible(true);
            paneTiming_Add.setVisible(false);
        }
    }
    private boolean isExistedObj(PolicyInfo policyInfo){
        if (ViewModel.policyInfos.size() > 0){
            String sTmp="";
            String sDes=policyInfo.getTime()+policyInfo.getRepeat()+policyInfo.getIsEnable();
            for (PolicyInfo item :
                    ViewModel.policyInfos) {
                sTmp = item.getTime()+item.getRepeat()+item.getIsEnable();
                if(sTmp.equals(sDes)){
                    return true;
                }
            }
        }

        return false;
    }
    //region UI OPT
    private void groupSelectedChanged(Toggle newVal){
        Platform.runLater(() -> {
            paneManual.setVisible(false);
            paneHDMI.setVisible(false);
            paneTiming.setVisible(false);
            paneTiming_Add.setVisible(false);
            RadioButton temp_rb=(RadioButton)newVal;
            if(temp_rb == rbManual){
                paneManual.setVisible(true);
            }else if(temp_rb == rbTiming){
                paneTiming.setVisible(true);
            }else if(temp_rb == rbHDMI){
                paneHDMI.setVisible(true);
            }
        });
    }
    private void cbManualTypeChanged(Object newValue){
        Platform.runLater(() -> {
            if(newValue.equals(cbManualType.getItems().get(0))){
                paneManualType_hdmi.setVisible(false);
                paneManualType_inner.setVisible(true);
            }else{
                paneManualType_hdmi.setVisible(true);
                paneManualType_inner.setVisible(false);
            }
        });
    }
    private void cbRepeatTypeChanged(Object newValue){
        Platform.runLater(() -> {
            if(newValue.equals(cbRepeatType.getItems().get(0))){
                paneWeek.setVisible(false);
            }else{
                paneWeek.setVisible(true);
            }
        });
    }
    VideoControlInfo curVideoControlInfo = null;
    private void updateUI(VideoControlInfo videoControlInfo){
        Platform.runLater(() -> {
            curVideoControlInfo = videoControlInfo;
            //region mode//HDMI priority 0//manual 1//timing 2
            switch(videoControlInfo.videoMode){
                case MODE_HDMI:
                    rbHDMI.setSelected(true);
                    break;
                case MODE_MANUAL:
                    rbManual.setSelected(true);
                    break;
                case MODE_TIMING:
                    rbTiming.setSelected(true);
                    paneTiming.setVisible(true);
                    paneTiming_Add.setVisible(false);
                    break;
                default:
                    break;
            }
            //endregion
            //region video source SOURCE_INSIDE internal 0, SOURCE_HDMI HDMI 1, this field is only useful in manual mode
            cbManualType.getSelectionModel().select(videoControlInfo.videoSource);
            //endregion
            //whether the region is scaled isScale
            ckbTimingScale.setSelected(videoControlInfo.isScale);
            ckbManualHdmiScale.setSelected(videoControlInfo.isScale);
            ckbHdmiScale.setSelected(videoControlInfo.isScale);
            //endregion
            //region X/Y
            tfTimingX.setText(String.valueOf(videoControlInfo.offsetX));
            tfHdmiX.setText(String.valueOf(videoControlInfo.offsetX));
            tfManualHdmiX.setText(String.valueOf(videoControlInfo.offsetX));
            tfManualInnerX.setText(String.valueOf(videoControlInfo.offsetX));
            tfTimingY.setText(String.valueOf(videoControlInfo.offsetY));
            tfHdmiY.setText(String.valueOf(videoControlInfo.offsetY));
            tfManualHdmiY.setText(String.valueOf(videoControlInfo.offsetY));
            tfManualInnerY.setText(String.valueOf(videoControlInfo.offsetY));
            //endregion
            //region PolicyInfos
            for (VideoTimingPolicy item :
                    videoControlInfo.conditions) {
                PolicyInfo policyInfo = new PolicyInfo();
                if(0 == item.getSource()){
                    policyInfo.setType(Contacts.getResourceValue("Inner"));
                }else{
                    policyInfo.setType("HDMI");
                }

                if (item.isEnable()) {
                    policyInfo.setIsEnable("√");
                }else{
                    policyInfo.setIsEnable("×");
                }

                if(item.getCron().isEmpty()){
                    policyInfo.setTime("00:00:00");
                    policyInfo.setRepeat(Contacts.getResourceValue("EveryDay"));
                }else{
                    CronBean cronBean = CronUtil.parseCron(item.getCron());
                    int hour = cronBean.getHour().getValues()[0] > 0 ? cronBean.getHour().getValues()[0] : 0;
                    int minute = cronBean.getMinute().getValues()[0] > 0 ? cronBean.getMinute().getValues()[0] : 0;
                    int second = cronBean.getSecond().getValues()[0] > 0 ? cronBean.getSecond().getValues()[0] : 0;
                    policyInfo.setTime(String.format("%02d:%02d:%02d", hour, minute, second));

                    String sRepeat = "";
                    if (cronBean.getDayOfWeek().getValues().length == 0 ||
                            cronBean.getDayOfWeek().getValues().length == 7){
                        sRepeat = Contacts.getResourceValue("EveryDay");
                    }else{
                        for (int week :
                                cronBean.getDayOfWeek().getValues()) {
                            sRepeat += transWeek2describe(week) + "/";
                        }
                    }

                    policyInfo.setRepeat(sRepeat);
                }

                policyInfo.videoTimingPolicy = item;
                ViewModel.policyInfos.add(policyInfo);
            }
            //endregion

            NovaOpt.GetInstance().getEdidInfo(deviceInfo.getSn(), new OnResultListenerN<EdidInfo, ErrorDetail>() {
                @Override
                public void onSuccess(IRequestBase iRequestBase, EdidInfo edidInfo) {
                    updateUI(edidInfo);
                }

                @Override
                public void onError(IRequestBase iRequestBase, ErrorDetail errorDetail) {

                }
            });
        });
    }
    private void updateUI(EdidInfo edidInfo){
        Platform.runLater(() -> {
            tfHdmiWidth.setText(String.valueOf(edidInfo.getWidth()));
            tfHdmiHeight.setText(String.valueOf(edidInfo.getHeight()));
            tfManualHdmiWidth.setText(String.valueOf(edidInfo.getWidth()));
            tfManualHdmiHeight.setText(String.valueOf(edidInfo.getHeight()));
            tfTimingWidth.setText(String.valueOf(edidInfo.getWidth()));
            tfTimingHeigth.setText(String.valueOf(edidInfo.getHeight()));
        });
    }
    //endregion

    //region getVideoControlInfo
    private VideoControlInfo getVideoControlInfoByManual(){
        int videoMode;
        int videoSource;
        boolean isScale = false;
        int offsetX = 0;
        int offsetY = 0;
        List<VideoTimingPolicy> conditions = null;
        videoMode = MODE_MANUAL;
        if(0 == cbManualType.getSelectionModel().getSelectedIndex()){
            videoSource = SOURCE_INSIDE;
            offsetX = Integer.parseInt(tfManualInnerX.getText());
            offsetY = Integer.parseInt(tfManualInnerY.getText());
        }else{
            videoSource = SOURCE_HDMI;
            isScale = ckbManualHdmiScale.isSelected();
            offsetX = Integer.parseInt(tfManualHdmiX.getText());
            offsetY = Integer.parseInt(tfManualHdmiY.getText());
            //Set resolution
            EdidInfo edidInfo = new EdidInfo();
            edidInfo.setWidth(Integer.parseInt(tfManualHdmiWidth.getText()));
            edidInfo.setHeight(Integer.parseInt(tfManualHdmiHeight.getText()));
            edidInfo.setFieldRate(60);
            NovaOpt.GetInstance().setEdidInfo(deviceInfo.getSn(), edidInfo, new OnResultListenerN() {
                @Override
                public void onSuccess(IRequestBase iRequestBase, Object o) {
                    showInfo("setEdidInfo ok");
                }

                @Override
                public void onError(IRequestBase iRequestBase, Object o) {
                    showInfo("setEdidInfo error");
                }
            });
        }

        SyncShowParams(offsetX, offsetY, Integer.parseInt(tfManualHdmiWidth.getText()), Integer.parseInt(tfManualHdmiHeight.getText()), isScale);
        return new VideoControlInfo(videoMode, videoSource, isScale, offsetX, offsetY, conditions);
    }
    private VideoControlInfo getVideoControlInfoByTiming(){
        int videoMode;
        int videoSource = SOURCE_INSIDE;
        boolean isScale;
        int offsetX;
        int offsetY;
        List<VideoTimingPolicy> conditions = new ArrayList<>();
        videoMode = MODE_TIMING;
        isScale = ckbTimingScale.isSelected();
        offsetX = Integer.parseInt(tfTimingX.getText());
        offsetY = Integer.parseInt(tfTimingY.getText());
        for (PolicyInfo item :
                ViewModel.policyInfos) {
            conditions.add(item.videoTimingPolicy);
        }
        //Set resolution
        EdidInfo edidInfo = new EdidInfo();
        edidInfo.setWidth(Integer.parseInt(tfTimingWidth.getText()));
        edidInfo.setHeight(Integer.parseInt(tfTimingHeigth.getText()));
        edidInfo.setFieldRate(60);
        NovaOpt.GetInstance().setEdidInfo(deviceInfo.getSn(), edidInfo, new OnResultListenerN() {
            @Override
            public void onSuccess(IRequestBase iRequestBase, Object o) {
                showInfo("setEdidInfo ok");
            }

            @Override
            public void onError(IRequestBase iRequestBase, Object o) {
                showInfo("setEdidInfo error");
            }
        });

        SyncShowParams(offsetX, offsetY, edidInfo.getWidth(), edidInfo.getHeight(), isScale);
        return new VideoControlInfo(videoMode, videoSource, isScale, offsetX, offsetY, conditions);
    }
    private VideoControlInfo getVideoControlInfoByHDMI(){
        int videoMode;
        int videoSource = SOURCE_INSIDE;
        boolean isScale;
        int offsetX;
        int offsetY;
        List<VideoTimingPolicy> conditions = null;
        videoMode = MODE_HDMI;
        isScale = ckbHdmiScale.isSelected();
        offsetX = Integer.parseInt(tfHdmiX.getText());
        offsetY = Integer.parseInt(tfHdmiY.getText());
        //Set resolution
        EdidInfo edidInfo = new EdidInfo();
        edidInfo.setWidth(Integer.parseInt(tfHdmiWidth.getText()));
        edidInfo.setHeight(Integer.parseInt(tfHdmiHeight.getText()));
        edidInfo.setFieldRate(60);
        NovaOpt.GetInstance().setEdidInfo(deviceInfo.getSn(), edidInfo, new OnResultListenerN() {
            @Override
            public void onSuccess(IRequestBase iRequestBase, Object o) {
                showInfo("setEdidInfo ok");
            }

            @Override
            public void onError(IRequestBase iRequestBase, Object o) {
                showInfo("setEdidInfo error");
            }
        });

        SyncShowParams(offsetX, offsetY, edidInfo.getWidth(), edidInfo.getHeight(), isScale);
        return new VideoControlInfo(videoMode, videoSource, isScale, offsetX, offsetY, conditions);
    }
    //endregion
    /**
     * Weekly description conversion, 1-7, 1 represents Sunday, 2 Monday, and so on
     * */
    public String transWeek2describe(int week){
        String sRepeat="";
        switch (week){
            case 1: sRepeat += Contacts.getResourceValue("repeat_sunday");break;
            case 2: sRepeat += Contacts.getResourceValue("repeat_monday");break;
            case 3: sRepeat += Contacts.getResourceValue("repeat_tuesday");break;
            case 4: sRepeat += Contacts.getResourceValue("repeat_wednesday");break;
            case 5: sRepeat += Contacts.getResourceValue("repeat_thursday");break;
            case 6: sRepeat += Contacts.getResourceValue("repeat_friday");break;
            case 7: sRepeat += Contacts.getResourceValue("repeat_saturday");break;
            default: break;
        }
        return sRepeat;
    }

    public void showInfo(String sInfo) {
        Platform.runLater(() -> {
            Date day = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            taInfo.appendText("[" + df.format(day) + "]  " + sInfo + "\r\n");
        });
    }

    public void SyncShowParams(int offsetX, int offsetY, int width, int height, boolean isScale){
        //region Whether to scale isScale
        ckbTimingScale.setSelected(isScale);
        ckbManualHdmiScale.setSelected(isScale);
        ckbHdmiScale.setSelected(isScale);
        //endregion
        //region X/Y
        tfTimingX.setText(String.valueOf(offsetX));
        tfHdmiX.setText(String.valueOf(offsetX));
        tfManualHdmiX.setText(String.valueOf(offsetX));
        tfTimingY.setText(String.valueOf(offsetY));
        tfHdmiY.setText(String.valueOf(offsetY));
        tfManualHdmiY.setText(String.valueOf(offsetY));
        tfManualInnerX.setText(String.valueOf(offsetX));
        tfManualInnerY.setText(String.valueOf(offsetY));
        //endregion
        //region width/height
        tfHdmiWidth.setText(String.valueOf(width));
        tfHdmiHeight.setText(String.valueOf(height));
        tfManualHdmiWidth.setText(String.valueOf(width));
        tfManualHdmiHeight.setText(String.valueOf(height));
        tfTimingWidth.setText(String.valueOf(width));
        tfTimingHeigth.setText(String.valueOf(height));
        //endregion
    }
}
