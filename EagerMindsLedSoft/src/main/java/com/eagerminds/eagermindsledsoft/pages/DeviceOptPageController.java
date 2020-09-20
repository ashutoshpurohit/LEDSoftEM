/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerminds.eagermindsledsoft.pages;

import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import com.eagerminds.eagermindsledsoft.DeviceInfo;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleButton;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import novj.platform.vxkit.common.bean.FirmwareInfo;
import novj.platform.vxkit.common.bean.TimeZoneParam;
import novj.platform.vxkit.common.bean.Volume;
import novj.platform.vxkit.common.bean.monitor.DiskSizeBean;
import novj.platform.vxkit.common.result.OnResultListenerN;
import novj.platform.vxkit.handy.api.ScreenShotManager;
import novj.platform.vxkit.handy.net.transfer.OnFileTransferListener;
import novj.publ.api.NovaOpt;
import novj.publ.net.exception.ErrorDetail;
import novj.publ.net.svolley.Request.IRequestBase;
import com.eagerminds.eagermindsledsoft.Contacts;
import com.eagerminds.eagermindsledsoft.pages.TimeZones.TimeZone;
import com.eagerminds.eagermindsledsoft.pages.TimeZones.TimeZones;
import com.eagerminds.eagermindsledsoft.pages.app.AppManagerPageController;
import com.eagerminds.eagermindsledsoft.pages.color.ColorTempController;
import com.eagerminds.eagermindsledsoft.pages.monitor.MonitorUIController;
import com.eagerminds.eagermindsledsoft.pages.monitor.SystemParamsMonitorController;
import com.eagerminds.eagermindsledsoft.pages.network.*;
import com.eagerminds.eagermindsledsoft.pages.play.*;
/**
 * FXML Controller class
 *
 * @author user
 */

/**
 *  Main panel
 */
public class DeviceOptPageController {

    private DeviceInfo deviceInfo = null;
    TimeZones timeZones = new TimeZones();
    List<TimeZone> listTimeZone = timeZones.LoadXML();

    @FXML
    private Label labelSN;
    @FXML
    private TextArea ta_show;
    @FXML
    private ComboBox cb_timezones;
    @FXML
    private TextField tf_time;
    @FXML
    Button btn_play_program;
    @FXML
    ToggleButton toggleBtn_Sync_Play;
    @FXML
    private Label device_sn;
    @FXML
    private Button btn_screenshot;
    @FXML
    private Button btn_video_source;
    @FXML
    private Button btn_update_apk;


    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    @FXML
    protected void btn_modify_pressed(ActionEvent event) {
        toCreatePage();
    }

    private void toCreatePage() {
        try {
            Stage primaryStage = new Stage();
            URL location = getClass().getResource("/sample/pages/play/ProgramSettingPage.fxml");
            FXMLLoader fxmlLoader = Contacts.getFxmlLoader(location);
            Parent root = fxmlLoader.load();
            ProgramSettingPageController programSettingPageController = fxmlLoader.getController();
            programSettingPageController.setDeviceInfo(this.getDeviceInfo());
            //Pass parameters
            primaryStage.initModality(Modality.APPLICATION_MODAL);
            primaryStage.setTitle(Contacts.getResourceValue("terminal_program_editor"));
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
            ProgramSettingPageController psgc = fxmlLoader.getController();
            psgc.initData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init() {
        device_sn.setText(Contacts.getResourceValue("terminal_serialno") + "：");
        labelSN.setText(deviceInfo.getSn());
        //T1\T3 The board does not support external video sources
        if (deviceInfo.getProductName().equals("T1") ||
                deviceInfo.getProductName().equals("T3") ||
                deviceInfo.getProductName().equals("JT100")) {
            btn_video_source.setDisable(true);
        }
        //Initialize combobox data
        cb_timezones.setPromptText("");
        if (Locale.CHINESE == Contacts.DEFAULT_LOCALE) {
            for (int i = 0; i < listTimeZone.size(); i++) {
                cb_timezones.getItems().add(listTimeZone.get(i).content);
            }
        } else if (Locale.ENGLISH == Contacts.DEFAULT_LOCALE) {
            for (int i = 0; i < listTimeZone.size(); i++) {
                cb_timezones.getItems().add(listTimeZone.get(i).id);
            }
        }
        getCurrentTimeZone();
//        cb_timezones.getSelectionModel().select(0);
        //Initialization time display
        Date day = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        tf_time.setText(df.format(day));

        //Initialize the sync play button state
        NovaOpt.GetInstance().getSyncPlay(deviceInfo.getSn(), new OnResultListenerN<Boolean,
                ErrorDetail>() {

            @Override
            public void onSuccess(IRequestBase iRequestBase, Boolean aBoolean) {
                Platform.runLater(() -> {
                    if (aBoolean) {
                        toggleBtn_Sync_Play.setText(Contacts.getResourceValue(
                                "terminal_sync_playing_close"));
                    } else {
                        toggleBtn_Sync_Play.setText(Contacts.getResourceValue(
                                "terminal_sync_playing_open"));
                    }
                    toggleBtn_Sync_Play.setSelected(aBoolean);
                });
            }

            @Override
            public void onError(IRequestBase iRequestBase, ErrorDetail errorDetail) {
                showInfo(Contacts.getResourceValue("terminal_init_sync_failure"));
            }
        });
    }

    /**
     * Restart the device now
     *
     * @param event
     */
    @FXML
    protected void btn_reboot_pressed(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.WARNING, Contacts.getResourceValue("reboot_soon")
                , new ButtonType(Contacts.getResourceValue("confirm"), ButtonBar
                .ButtonData.YES), new ButtonType(Contacts.getResourceValue("reboot_cancel"),
                ButtonBar.ButtonData.NO));
        alert.setTitle(Contacts.getResourceValue("alert_title_warning"));
        alert.setHeaderText(null);
        Optional<ButtonType> optionalButtonType = alert.showAndWait();
        if (optionalButtonType.get().getButtonData().equals(ButtonBar.ButtonData.YES)) {
            NovaOpt.GetInstance().reboot(deviceInfo.getSn(), new
                    OnResultListenerN<Integer, ErrorDetail>() {

                        @Override
                        public void onSuccess(IRequestBase requestBase, Integer response) {
                            showInfo("success " + Contacts.getResourceValue("terminal_reboot_now"));
                        }

                        @Override
                        public void onError(IRequestBase requestBase, ErrorDetail error) {
                            Platform.runLater(() -> {
                                Alert errorAlert = new Alert(Alert.AlertType.ERROR, error
                                        .description);
                                errorAlert.setTitle(Contacts.getResourceValue("alert_title_error"));
                                errorAlert.setHeaderText(null);
                                errorAlert.showAndWait();
                            });
                        }
                    });
        }
    }

    //Restart regularly
    @FXML
    protected void btn_time_reboot(ActionEvent ae) {
        toSetTimeRebootCronPage();
    }

    private void toSetTimeRebootCronPage() {
        try {
            Stage primaryStage = new Stage();
            URL location = getClass().getResource("/sample/pages/TimeRebootPage.fxml");
            FXMLLoader fxmlLoader = Contacts.getFxmlLoader(location);
            Parent root = fxmlLoader.load();

            primaryStage.initModality(Modality.APPLICATION_MODAL);
            primaryStage.setTitle(Contacts.getResourceValue("terminal_reboot_timing"));
            primaryStage.setScene(new Scene(root));
            primaryStage.show();

            TimeRebootPageController timeRebootPageController = fxmlLoader.getController();
            timeRebootPageController.initData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Obtain software major version information and FPGA version information
     *
     * @param event
     */
    @FXML
    protected void btn_get_version_pressed(ActionEvent event) {
        NovaOpt.GetInstance().getVersionMessage(deviceInfo.getSn(), new
                OnResultListenerN<FirmwareInfo, ErrorDetail>() {
                    @Override
                    public void onSuccess(IRequestBase requestBase, FirmwareInfo response) {
                        String sInfo = Contacts.getResourceValue("terminal_version") + "    ";
                        sInfo += "FPGA: " + response.fpga + ", ";
                        sInfo += Contacts.getResourceValue("termianl_system_version") + ": " + response.model + ", ";
                        sInfo += Contacts.getResourceValue("terminal_main_version") +
                                ": " + response.mainVersion;
                        showInfo(sInfo);
                    }

                    @Override
                    public void onError(IRequestBase requestBase, ErrorDetail error) {
                        showInfo(error.description);
                    }
                });

    }

    /**
     * Get free memory
     *
     * @param event
     */
    @FXML
    protected void btn_get_memory_pressed(ActionEvent event) {
        NovaOpt.GetInstance().getAvailableMemoryData(deviceInfo.getSn(), new
                OnResultListenerN<Float, ErrorDetail>() {
                    @Override
                    public void onSuccess(IRequestBase requestBase, Float aFloat) {
                        String sInfo = "";
                        sInfo += Contacts.getResourceValue("available_memory") + "：" + aFloat
                                .toString() + "%";
                        showInfo(sInfo);
                    }

                    @Override
                    public void onError(IRequestBase requestBase, ErrorDetail errorDetail) {
                        showInfo(errorDetail.description);
                    }
                });
    }

    /**
     * Get current screen brightness
     *
     * @param event
     */
    @FXML
    protected void btn_get_env_bright_pressed(ActionEvent event) {
        NovaOpt.GetInstance().getEnvironmentBrightness(deviceInfo.getSn(), new
                OnResultListenerN<Integer, ErrorDetail>() {
                    @Override
                    public void onSuccess(IRequestBase requestBase, Integer integer) {
                        showInfo(Contacts.getResourceValue("current_screen_brightness") + "：" +
                                integer.toString() + "Lux");
                    }

                    @Override
                    public void onError(IRequestBase requestBase, ErrorDetail errorDetail) {
                        showInfo(Contacts.getResourceValue("current_screen_brightness")
                                + "：" + errorDetail.description);
                    }
                });
    }

    /**
     * Set time zone
     *
     * @param event
     */
    @FXML
    protected void btn_time_sync_pressed(ActionEvent event) {
        try {
            TimeZoneParam timeZoneParam = new TimeZoneParam();
            timeZoneParam.setTimeZone(getTimeZoneIdByContent(cb_timezones.getValue().toString()));
            NovaOpt.GetInstance().calibrateTime(deviceInfo.getSn(), timeZoneParam, new
                    OnResultListenerN<TimeZoneParam, ErrorDetail>() {
                        @Override
                        public void onSuccess(IRequestBase requestBase, TimeZoneParam timeZoneParam) {
                            String sInfo = "";
                            sInfo += Contacts.getResourceValue("terminal_sync_time")
                                    + "：" + Contacts.getResourceValue("terminal_timezone") + "-" +
                                    timeZoneParam.getTimeZone();
                            Date date = new Date(timeZoneParam.getUtcTimeMillis());
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            sInfo += " " + Contacts.getResourceValue("terminal_datetime") + "-" +
                                    df.format(date);
                            showInfo(sInfo);
                        }

                        @Override
                        public void onError(IRequestBase requestBase, ErrorDetail errorDetail) {
                            showInfo(Contacts.getResourceValue("terminal_sync_time") + "：" + errorDetail
                                    .description);
                        }
                    });
        } catch (Exception e) {
            showInfo(e.getMessage());
        }
    }

    /**
     * Get current time and time zone
     *
     * @param event
     */
    @FXML
    protected void btn_time_get_pressed(ActionEvent event) {
        getCurrentTimeZone();
    }

    private void getCurrentTimeZone() {
        try {
            NovaOpt.GetInstance().getCurrentTimeAndZone(deviceInfo.getSn(), new
                    OnResultListenerN<TimeZoneParam, ErrorDetail>() {

                        @Override
                        public void onSuccess(IRequestBase iRequestBase,
                                              TimeZoneParam timeZoneParam) {
                            for (int i = 0; i < listTimeZone.size(); i++) {
                                if (timeZoneParam.getTimeZone().endsWith(listTimeZone.get(i).getId())) {
                                    final int x = i;
                                    Platform.runLater(() -> cb_timezones.getSelectionModel().select(x));
                                }
                            }
                            String sInfo = Contacts.getResourceValue("terminal_timezone") + "："
                                    + timeZoneParam.getTimeZone() +
                                    "; " + Contacts.getResourceValue("terminal_present_time") +
                                    "：" + timeZoneParam.getCurrentTime();

                            showInfo(sInfo);
                        }

                        @Override
                        public void onError(IRequestBase iRequestBase, ErrorDetail errorDetail) {
                            showInfo(Contacts.getResourceValue("terminal_get_time_failure"));
                        }
                    });
        } catch (Exception e) {
            showInfo(e.getMessage());
        }
    }

    /**
     * Sync play switch
     *
     * @param event
     */
    @FXML
    protected void toggleBtn_Sync_Play_pressed(ActionEvent event) {
        if (toggleBtn_Sync_Play.isSelected()) {
            //Set_ON
            NovaOpt.GetInstance().setSyncPlay(deviceInfo.getSn(), true, new OnResultListenerN() {
                @Override
                public void onSuccess(IRequestBase iRequestBase, Object o) {
                    showInfo(Contacts.getResourceValue("terminal_sync_playing_open"));
                    Platform.runLater(() -> {
                        toggleBtn_Sync_Play.setText(Contacts.getResourceValue(
                                "terminal_sync_playing_close"));
                    });
                }

                @Override
                public void onError(IRequestBase iRequestBase, Object o) {
                    Platform.runLater(() -> {
                        toggleBtn_Sync_Play.setSelected(false);
                    });
                    showInfo(Contacts.getResourceValue("terminal_sync_playing_open_failure"));
                }
            });
        } else {
            //Set_OFF
            NovaOpt.GetInstance().setSyncPlay(deviceInfo.getSn(), false, new OnResultListenerN() {
                @Override
                public void onSuccess(IRequestBase iRequestBase, Object o) {
                    showInfo(Contacts.getResourceValue("terminal_sync_playing_close"));
                    Platform.runLater(() -> {
                        toggleBtn_Sync_Play.setText(Contacts.getResourceValue(
                                "terminal_sync_playing_open"));
                    });
                }

                @Override
                public void onError(IRequestBase iRequestBase, Object o) {
                    Platform.runLater(() -> {
                        toggleBtn_Sync_Play.setSelected(true);
                    });
                    showInfo(Contacts.getResourceValue("terminal_sync_playing_close_failure"));
                }
            });
        }
    }

    /**
     * Get a screenshot of the currently playing content
     *
     * @param event
     */
    @FXML
    protected void btn_screenshot_pressed(ActionEvent event) {
        //file name
        Date day = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String sFileName = deviceInfo.getSn() + "_" + df.format(day);

        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle(Contacts.getResourceValue("snapshot_save_path"));
        File directory = dc.showDialog(btn_screenshot.getScene().getWindow());
        if (null == directory) {
            return;
        }
        String filePath = directory.getAbsolutePath();
        NovaOpt.GetInstance().downLoadScreenshot(deviceInfo.getSn(), deviceInfo.searchResult
                        .width, deviceInfo.searchResult.height,
                ScreenShotManager.PictureType.PICTURE_PNG,
                filePath, sFileName, new OnFileTransferListener() {
                    @Override
                    public void onTransferred(long l) {
                    }

                    @Override
                    public void onStartTransfer() {
                    }

                    @Override
                    public void onSuccess(Integer integer) {
                        showInfo(Contacts.getResourceValue("snapshot_download_successed"));
                    }

                    @Override
                    public void onError(ErrorDetail errorDetail) {
                        showInfo(Contacts.getResourceValue("snapshot_download_failed")
                                + "：" + errorDetail.description);
                    }
                });
    }


    /**
     * Adjust brightness
     *
     * @param ae
     */
    @FXML
    protected void onBrightnessAdjust(ActionEvent ae) {
        try {
            Stage primaryStage = new Stage();
            URL location = getClass().getResource("/sample/pages/BrightnessAdjustPage.fxml");
            FXMLLoader fxmlLoader = Contacts.getFxmlLoader(location);
            Parent root = fxmlLoader.load();

            primaryStage.initModality(Modality.APPLICATION_MODAL);
            primaryStage.setTitle(Contacts.getResourceValue("terminal_brightness_control"));
            primaryStage.setScene(new Scene(root));
            primaryStage.show();

            BrightnessAdjustPageController brightnessAdjustPageController = fxmlLoader
                    .getController();
            brightnessAdjustPageController.initData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Play control
     *
     * @param ae
     */
    @FXML
    protected void onPlayControl(ActionEvent ae) {
        try {
            Stage primaryStage = new Stage();
            URL location = getClass().getResource("/sample/pages/play/PlayControlPage.fxml");
            FXMLLoader fxmlLoader = Contacts.getFxmlLoader(location);
            Parent root = fxmlLoader.load();

            primaryStage.initModality(Modality.APPLICATION_MODAL);
            primaryStage.setTitle(Contacts.getResourceValue("terminal_player_controller"));
            primaryStage.setScene(new Scene(root));
            primaryStage.show();

            PlayControlPageController playControlPageController = fxmlLoader.getController();
            playControlPageController.initData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void btn_video_source_pressed(ActionEvent event) {
        try {
            Stage primaryStage = new Stage();
            URL location = getClass().getResource("/sample/pages/VideoSourceOptPage.fxml");
            FXMLLoader fxmlLoader = Contacts.getFxmlLoader(location);
            Parent root = fxmlLoader.load();

            primaryStage.initModality(Modality.APPLICATION_MODAL);
            primaryStage.setTitle(Contacts.getResourceValue("terminal_video_source"));
            primaryStage.setScene(new Scene(root));
            primaryStage.setResizable(false);
            primaryStage.show();

            VideoSourceOptPageController videoSourceOptPageController = fxmlLoader.getController();
            videoSourceOptPageController.setDeviceInfo(deviceInfo);
            videoSourceOptPageController.initData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Timed play
     *
     * @param actionEvent
     */
    @FXML
    protected void btn_timing_play(ActionEvent actionEvent) {
        try {
            Stage primaryStage = new Stage();
            URL location = getClass().getResource("/sample/pages/play/TimingProgramPage.fxml");
            FXMLLoader fxmlLoader = Contacts.getFxmlLoader(location);
            Parent root = fxmlLoader.load();

            primaryStage.initModality(Modality.APPLICATION_MODAL);
            primaryStage.setTitle(Contacts.getResourceValue("timing_play"));
            primaryStage.setScene(new Scene(root));
            primaryStage.setResizable(false);
            primaryStage.show();

            TimingProgramPageController timingProgramPageController = fxmlLoader.getController();
            timingProgramPageController.init(getDeviceInfo());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Upgrade or install apk
     *
     * @param actionEvent
     */
    @FXML
    protected void btn_update_apk(ActionEvent actionEvent) {
        try {
            Stage primaryStage = new Stage();
            URL location = getClass().getResource("/sample/pages/app/AppManagerPage.fxml");
            FXMLLoader fxmlLoader = Contacts.getFxmlLoader(location);
            Parent root = fxmlLoader.load();

            primaryStage.initModality(Modality.APPLICATION_MODAL);
            primaryStage.setTitle(Contacts.getResourceValue("terminal_update"));
            primaryStage.setScene(new Scene(root));
            primaryStage.show();

            AppManagerPageController controller = fxmlLoader.getController();
            controller.updateLanguage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*
        List<String> choices = new ArrayList<>();
        choices.add(Contacts.getResourceValue("terminal_update_system"));
        choices.add(Contacts.getResourceValue("terminal_update_software"));
        choices.add(Contacts.getResourceValue("update_apkfile"));

        ChoiceDialog<String> choiceDialog = new ChoiceDialog<>(Contacts.getResourceValue(
                "terminal_update_system"), choices);
        choiceDialog.setHeight(150);
        choiceDialog.setWidth(150);
        choiceDialog.setTitle(Contacts.getResourceValue("select_action_type"));
        choiceDialog.setContentText(null);
        choiceDialog.setHeaderText(null);
        Optional<String> result = choiceDialog.showAndWait();
        if (result.isPresent()) {
            if (result.get().equals(Contacts.getResourceValue("terminal_update_system"))) {
                selectUpdateSystem();
            } else if (result.get().equals(Contacts.getResourceValue("terminal_update_software"))) {
                selectUpdateSoftware();
            } else if (result.get().equals(Contacts.getResourceValue("update_apkfile"))) {
                selectUpdateApk();
            }
        }
        */
    }

    /**
     * Select system upgrade
     */
    private void selectUpdateSystem() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(Contacts.getResourceValue("terminal_update_system"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter
                ("nuzip(*.nuzip)", "*.nuzip"));
        File systemFile = fileChooser.showOpenDialog(btn_update_apk.getScene().getWindow());
        if (null == systemFile) {
            return;
        }
        NovaOpt.GetInstance().startUpdateSystem(deviceInfo.getSn(), systemFile, new OnFileTransferListener() {
            @Override
            public void onTransferred(long length) {
                showInfo("Issued: " + length + "byte");
            }

            @Override
            public void onStartTransfer() {
                showInfo("Start upgrading the system");
            }

            @Override
            public void onSuccess(Integer response) {
                showInfo("System upgrade successfully");
            }

            @Override
            public void onError(ErrorDetail error) {
                showInfo("System upgrade failed：" + error.description);
            }
        });
    }

    /**
     * Upgrade terminal software
     */
    private void selectUpdateSoftware() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(Contacts.getResourceValue("terminal_update_software"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter
                ("nuzip(*.nuzip)", "*.nuzip"));
        File systemFile = fileChooser.showOpenDialog(btn_update_apk.getScene().getWindow());
        if (null == systemFile) {
            return;
        }
        NovaOpt.GetInstance().startUpdateSoftware(deviceInfo.getSn(), systemFile, new OnFileTransferListener() {
            @Override
            public void onTransferred(long length) {
                showInfo("Issued: " + length + "byte");
            }

            @Override
            public void onStartTransfer() {
                showInfo("Start to upgrade the software");
            }

            @Override
            public void onSuccess(Integer response) {
                showInfo("Software upgrade successfully");
            }

            @Override
            public void onError(ErrorDetail error) {
                showInfo("Software upgrade failed：" + error.description);
            }
        });
    }

    /**
     * Select the APK file to be upgraded
     */
    private void selectUpdateApk() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(Contacts.getResourceValue("select_apk_file"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter
                ("APK(*.apk)", "*.apk"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter
                ("All files", "*.*"));
        File apkFile = fileChooser.showOpenDialog(btn_update_apk.getScene().getWindow());
        if (null == apkFile) {
            return;
        }
        String packageName = apkFile.getName();//update.apk
        NovaOpt.GetInstance().startUploadApk(deviceInfo.getSn(), apkFile, packageName, true, new OnFileTransferListener() {
            @Override
            public void onTransferred(long length) {
                showInfo("Issued: " + length + "byte");
            }

            @Override
            public void onStartTransfer() {
                showInfo("Start upgrading APK");
            }

            @Override
            public void onSuccess(Integer response) {
                showInfo("APK upgrade successfully");
            }

            @Override
            public void onError(ErrorDetail error) {
                showInfo("APK upgrade failed: " + error.description + "," + error.errorCode);
            }
        });
    }

    /**
     *   Clear program files
     *
     * @param ae
     */
    @FXML
    protected void btn_clear_programs(ActionEvent ae) {
        List<String> choices = new ArrayList<>();
        choices.add(Contacts.getResourceValue("clear_all_programs"));
        choices.add(Contacts.getResourceValue("clear_all_other_programs"));

        ChoiceDialog<String> choiceDialog = new ChoiceDialog<>(Contacts.getResourceValue(
                "clear_all_programs"), choices);
        choiceDialog.setHeight(150);
        choiceDialog.setWidth(150);
        choiceDialog.setTitle(Contacts.getResourceValue("select_action_type"));
        choiceDialog.setContentText(null);
        choiceDialog.setHeaderText(null);
        Optional<String> result = choiceDialog.showAndWait();
        if (result.isPresent()) {
            if (Contacts.getResourceValue("clear_all_programs").equals(result.get())) {
                NovaOpt.GetInstance().clearAllPrograms(deviceInfo.getSn(), new OnResultListenerN<Integer, ErrorDetail>() {
                    @Override
                    public void onSuccess(IRequestBase requestBase, Integer response) {
                        showInfo("Successfully delete all program files");
                    }

                    @Override
                    public void onError(IRequestBase requestBase, ErrorDetail error) {
                        showInfo(error.description);
                    }
                });
            } else {
                NovaOpt.GetInstance().clearOtherPrograms(deviceInfo.getSn(), new OnResultListenerN<Integer, ErrorDetail>() {
                    @Override
                    public void onSuccess(IRequestBase requestBase, Integer response) {
                        showInfo("Successfully deleted other program files");
                    }

                    @Override
                    public void onError(IRequestBase requestBase, ErrorDetail error) {
                        showInfo(error.description);
                    }
                });
            }
        }
    }

    /**
     * 
Get storage space size
     *
     * @param actionEvent
     */
    @FXML
    protected void btnStorageData(ActionEvent actionEvent) {
        NovaOpt.GetInstance().getAvailableStorageData(deviceInfo.getSn(), new OnResultListenerN<DiskSizeBean, ErrorDetail>() {
            @Override
            public void onSuccess(IRequestBase requestBase, DiskSizeBean response) {
                String storageData =
                        Contacts.getResourceValue("get_storage_data") + ": Total-" + response.getDiskTotalSize() + "G，Free-" + response.getDiskAvailableSize() + "G";
                String external = "EXTERNAL";
                String internal = "LOCAL";
                int externalStorageIndex = 1;
                if (response.getStorageInfos().size() > 1) {
                    for (DiskSizeBean.StorageInfo storageInfo : response.getStorageInfos()) {
                        if (external.equals(storageInfo.getType())) {
                            storageData = storageData + "\n外部存储" + externalStorageIndex++ + ": Total-" +
                                    getSizeFormat(storageInfo.getDiskTotalSize()) + "，Free-" +
                                    getSizeFormat(storageInfo.getDiskAvailableSize());
                        }
                    }
                }
                showInfo(storageData);
            }

            @Override
            public void onError(IRequestBase requestBase, ErrorDetail error) {
                showInfo(error.description);
            }
        });
    }

    private DecimalFormat df = new DecimalFormat("#0.00");

    private String getSizeFormat(long size) {
        String sizeFormat;
        if (size < 1024) {
            sizeFormat = size + " Byte";
        } else if (size < (1024 * 1024)) {
            sizeFormat = (int) (size / (float) 1024) + " KB";
        } else if (size < (1024 * 1024 * 1024)) {
            sizeFormat = df.format(size / (float) (1024 * 1024)) + " MB";
        } else {
            sizeFormat = df.format(size / (float) (1024 * 1024 * 1024)) + " GB";
        }
        return sizeFormat;
    }

    /**
     * Set volume and read back volume
     *
     * @param actionEvent
     */
    @FXML
    protected void onActionVolume(ActionEvent actionEvent) {
        List<String> choices = new ArrayList<>();
        choices.add(Contacts.getResourceValue("action_getvolume"));
        choices.add(Contacts.getResourceValue("action_setvolume"));
        choices.add(Contacts.getResourceValue("action_volumepolicy"));

        ChoiceDialog<String> choiceDialog = new ChoiceDialog<>(Contacts.getResourceValue(
                "action_getvolume"), choices);
        choiceDialog.setHeight(150);
        choiceDialog.setWidth(150);
        choiceDialog.setTitle(Contacts.getResourceValue("select_action_type"));
        choiceDialog.setContentText(null);
        choiceDialog.setHeaderText(null);
        Optional<String> result = choiceDialog.showAndWait();
        if (result.isPresent()) {
            if (Contacts.getResourceValue("action_getvolume").equals(result.get())) {
                NovaOpt.GetInstance().getVolume(deviceInfo.getSn(), new OnResultListenerN<Volume, ErrorDetail>() {
                    @Override
                    public void onSuccess(IRequestBase requestBase, Volume response) {
                        showInfo("Current volume：" + response.getRatio() + "%");
                    }

                    @Override
                    public void onError(IRequestBase requestBase, ErrorDetail error) {
                        showInfo("Volume readback failed");
                    }
                });
            } else if(Contacts.getResourceValue("action_setvolume").equals(result.get())){
                TextInputDialog textInputDialog = new TextInputDialog();
                textInputDialog.setTitle("Please enter volume percentage");
                textInputDialog.setWidth(150);
                textInputDialog.setHeight(150);
                Optional<String> resultRatio = textInputDialog.showAndWait();
                if (resultRatio.isPresent()) {
                    try {
                        int volume = Integer.parseInt(resultRatio.get());
                        if (volume < 0 || volume > 100) {
                            showInfo("Please enter an integer from 0 to 100");
                            return;
                        }
                        NovaOpt.GetInstance().setVolume(deviceInfo.getSn(), volume,
                                new OnResultListenerN<Integer, ErrorDetail>() {
                                    @Override
                                    public void onSuccess(IRequestBase requestBase, Integer response) {
                                        showInfo("Set volume successfully");
                                    }

                                    @Override
                                    public void onError(IRequestBase requestBase, ErrorDetail error) {
                                        showInfo("Failed to set volume");
                                    }
                                });
                    } catch (NumberFormatException e) {
                        showInfo("Integer required");
                    }
                }
            }else if(Contacts.getResourceValue("action_volumepolicy").equals(result.get())){
                try {
                Stage primaryStage = new Stage();
                URL location = getClass().getResource("/sample/pages/VolumePolicyPage.fxml");
                FXMLLoader fxmlLoader = Contacts.getFxmlLoader(location);
                Parent root = fxmlLoader.load();

                primaryStage.initModality(Modality.APPLICATION_MODAL);
                primaryStage.setTitle(Contacts.getResourceValue("action_volumepolicypage"));
                primaryStage.setScene(new Scene(root));
                primaryStage.show();

                VolumePolicyPageController controller = fxmlLoader.getController();
                controller.init();
            }catch (Exception e) {
           e.printStackTrace(); }
        }}
    }

    /**
     * Jump to the relevant configuration page of the multi-function card
     *
     * @param actionEvent
     */
    @FXML
    protected void onActionPower(ActionEvent actionEvent) {
        try {
            Stage primaryStage = new Stage();
            URL location = getClass().getResource("/sample/pages/FunctionCardPowerPage.fxml");
            FXMLLoader fxmlLoader = Contacts.getFxmlLoader(location);
            Parent root = fxmlLoader.load();

            primaryStage.initModality(Modality.APPLICATION_MODAL);
            primaryStage.setTitle(Contacts.getResourceValue("action_power"));
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Jump to iCare related configuration page
     *
     * @param actionEvent
     */
    @FXML
    protected void onActioniCare(ActionEvent actionEvent) {
        try {
            Stage primaryStage = new Stage();
            URL location = getClass().getResource("/sample/pages/ICareSettingPage.fxml");
            FXMLLoader fxmlLoader = Contacts.getFxmlLoader(location);
            Parent root = fxmlLoader.load();

            primaryStage.initModality(Modality.APPLICATION_MODAL);
            primaryStage.setTitle(Contacts.getResourceValue("set_icare_setting"));
            primaryStage.setScene(new Scene(root));
            primaryStage.show();

            ICareSettingPageController careSettingPageController = fxmlLoader.getController();
            careSettingPageController.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Jump to vnnox binding page

     *
     * @param actionEvent
     */
    @FXML
    protected void onActionVnnox(ActionEvent actionEvent) {
        try {
            Stage primaryStage = new Stage();
            URL location = getClass().getResource("/sample/pages/BindPlayerPage.fxml");
            FXMLLoader fxmlLoader = Contacts.getFxmlLoader(location);
            Parent root = fxmlLoader.load();

            primaryStage.initModality(Modality.APPLICATION_MODAL);
            primaryStage.setTitle(Contacts.getResourceValue("vnnox_setting"));
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
            BindPlayerPageController bindPlayerPageController = fxmlLoader.getController();
            bindPlayerPageController.init();
//            ICareSettingPageController careSettingPageController = fxmlLoader.getController();
//            careSettingPageController.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Jump to the network configuration page
     *
     * @param actionEvent
     */
    @FXML
    protected void onActionNetwork(ActionEvent actionEvent) {
        List<String> choices = new ArrayList<>();
        choices.add(Contacts.getResourceValue("ethernet_setting"));
        choices.add(Contacts.getResourceValue("mobile_setting"));
        choices.add(Contacts.getResourceValue("wifi_setting"));
        choices.add(Contacts.getResourceValue("wifi_ap_setting"));

        ChoiceDialog<String> choiceDialog = new ChoiceDialog<>(Contacts.getResourceValue(
                "ethernet_setting"), choices);
        choiceDialog.setHeight(150);
        choiceDialog.setWidth(150);
        choiceDialog.setTitle(Contacts.getResourceValue("select_action_type"));
        choiceDialog.setContentText(null);
        choiceDialog.setHeaderText(null);
        Optional<String> result = choiceDialog.showAndWait();
        if (result.isPresent()) {
            if (Contacts.getResourceValue("ethernet_setting").equals(result.get())) {
                navToEthernet();
            } else if (Contacts.getResourceValue("mobile_setting").equals(result.get())) {
                navToMobile();
            } else if (Contacts.getResourceValue("wifi_setting").equals(result.get())) {
                navToWifi();
            } else {
                navToAp();
            }
        }
    }

    private void navToEthernet() {
        try {
            Stage primaryStage = new Stage();
            URL location = getClass().getResource("/sample/pages/network/EthernetNetworkPage.fxml");
            FXMLLoader fxmlLoader = Contacts.getFxmlLoader(location);
            Parent root = fxmlLoader.load();

            primaryStage.initModality(Modality.APPLICATION_MODAL);
            primaryStage.setTitle(Contacts.getResourceValue("ethernet_setting"));
            primaryStage.setScene(new Scene(root));
            primaryStage.show();

            EthernetNetworkPageController controller = fxmlLoader.getController();
            controller.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void navToMobile() {
        try {
            Stage primaryStage = new Stage();
            URL location = getClass().getResource("/sample/pages/network/MobileNetworkPage_SB.fxml");
            FXMLLoader fxmlLoader = Contacts.getFxmlLoader(location);
            Parent root = fxmlLoader.load();

            primaryStage.initModality(Modality.APPLICATION_MODAL);
            primaryStage.setTitle(Contacts.getResourceValue("mobile_setting"));
            primaryStage.setScene(new Scene(root));
            primaryStage.show();

            MobileNetworkPage_SBController controller = fxmlLoader.getController();
            controller.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void navToWifi() {
        try {
            Stage primaryStage = new Stage();
            URL location = getClass().getResource("/sample/pages/network/WifiNetworkPage.fxml");
            FXMLLoader fxmlLoader = Contacts.getFxmlLoader(location);
            Parent root = fxmlLoader.load();

            primaryStage.initModality(Modality.APPLICATION_MODAL);
            primaryStage.setTitle(Contacts.getResourceValue("wifi_setting"));
            primaryStage.setScene(new Scene(root));
            primaryStage.show();

            WifiNetworkPageController controller = fxmlLoader.getController();
            controller.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void navToAp() {
        try {
            Stage primaryStage = new Stage();
            URL location = getClass().getResource("/sample/pages/network/APNetworkPage.fxml");
            FXMLLoader fxmlLoader = Contacts.getFxmlLoader(location);
            Parent root = fxmlLoader.load();

            primaryStage.initModality(Modality.APPLICATION_MODAL);
            primaryStage.setTitle(Contacts.getResourceValue("wifi_ap_setting"));
            primaryStage.setScene(new Scene(root));
            primaryStage.show();

            APNetworkPageController controller = fxmlLoader.getController();
            controller.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Switch screen
     *
     * @param actionEvent
     */
    @FXML
    protected void onActionSwitchScreen(ActionEvent actionEvent) {
        navToScreenPower();
    }

    /**
     * Jump to the switch screen control page
     */
    private void navToScreenPower() {
        try {
            Stage primaryStage = new Stage();
            URL location = getClass().getResource("/sample/pages/ScreenPowerPage.fxml");
            FXMLLoader fxmlLoader = Contacts.getFxmlLoader(location);
            Parent root = fxmlLoader.load();

            primaryStage.initModality(Modality.APPLICATION_MODAL);
            primaryStage.setTitle(Contacts.getResourceValue("power_switch"));
            primaryStage.setScene(new Scene(root));
            primaryStage.show();

            ScreenPowerPageController controller = fxmlLoader.getController();
            controller.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 
Get id based on time zone name
     *
     * @param content
     * @return
     */
    private String getTimeZoneIdByContent(String content) {
        for (int i = 0; i < listTimeZone.size(); i++) {
            if (listTimeZone.get(i).content.equals(content) || listTimeZone.get(i).id.equals(content)) {
                return listTimeZone.get(i).id;
            }
        }
        return "";
    }

    @FXML
    protected void onActionInsertPlay(ActionEvent actionEvent) {
        try {
            Stage primaryStage = new Stage();
            URL location = getClass().getResource("/sample/pages/play/InsertPlayingCtrlPage.fxml");
            FXMLLoader fxmlLoader = Contacts.getFxmlLoader(location);
            Parent root = fxmlLoader.load();

            primaryStage.initModality(Modality.APPLICATION_MODAL);
            primaryStage.setTitle(Contacts.getResourceValue("insert_play"));
            primaryStage.setScene(new Scene(root));
            primaryStage.show();

            InsertPlayingCtrlPageController controller = fxmlLoader.getController();
            controller.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onBtnMonitorClick(ActionEvent actionEvent) {
        try {
            Stage primaryStage = new Stage();
            URL location = getClass().getResource("/sample/pages/monitor/MonitorUI.fxml");
            FXMLLoader fxmlLoader = Contacts.getFxmlLoader(location);
            Parent root = fxmlLoader.load();

            primaryStage.initModality(Modality.APPLICATION_MODAL);
            primaryStage.setTitle(Contacts.getResourceValue("lbMonitor"));
            primaryStage.setScene(new Scene(root));
            primaryStage.show();

            MonitorUIController controller = fxmlLoader.getController();
            controller.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void OnSystemParams(ActionEvent actionEvent) {
        try {
            Stage primaryStage = new Stage();
            URL location = getClass().getResource("/sample/pages/monitor/SystemParamsMonitor.fxml");
            FXMLLoader fxmlLoader = Contacts.getFxmlLoader(location);
            Parent root = fxmlLoader.load();

            primaryStage.initModality(Modality.APPLICATION_MODAL);
            primaryStage.setTitle(Contacts.getResourceValue("btnSystemParams"));
            primaryStage.setScene(new Scene(root));
            primaryStage.show();

//            SystemParamsMonitorController controller = fxmlLoader.getController();
//            controller.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void OnBtnColorTempClick(ActionEvent actionEvent) {
        try {
            Stage primaryStage = new Stage();
            URL location = getClass().getResource("/sample/pages/color/ColorTemp.fxml");
            FXMLLoader fxmlLoader = Contacts.getFxmlLoader(location);
            Parent root = fxmlLoader.load();

            primaryStage.initModality(Modality.APPLICATION_MODAL);
            primaryStage.setTitle(Contacts.getResourceValue("btnColorTemp"));
            primaryStage.setScene(new Scene(root));
            primaryStage.show();

            ColorTempController controller = fxmlLoader.getController();
            controller.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void btn_program_media(ActionEvent event) {
        try {
            Stage primaryStage = new Stage();
            URL location = getClass().getResource("/sample/pages/play/ProgramMedia.fxml");
            FXMLLoader fxmlLoader = Contacts.getFxmlLoader(location);
            Parent root = fxmlLoader.load();
            ProgramMediaController programMediaController = fxmlLoader.getController();
            //programSettingPageController.setDeviceInfo(this.getDeviceInfo());
            //Pass parameters
            primaryStage.initModality(Modality.APPLICATION_MODAL);
            primaryStage.setTitle(Contacts.getResourceValue("terminal_program_media"));
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
            primaryStage.setResizable(true);
            //ProgramSettingPageController psgc = fxmlLoader.getController();
            //psgc.initData();
            programMediaController.getPlayProgramMediaInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showInfo(String msgInfo) {
        Platform.runLater(() -> {
            Date day = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            ta_show.appendText("[" + df.format(day) + "]  " + msgInfo + "\r\n");
        });
    }
}
