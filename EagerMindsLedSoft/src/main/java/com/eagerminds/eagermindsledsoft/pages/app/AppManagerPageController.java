/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerminds.eagermindsledsoft.pages.app;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import novj.platform.vxkit.common.result.OnResultListenerN;
import novj.platform.vxkit.handy.net.transfer.OnFileTransferListener;
import novj.publ.api.NovaOpt;
import novj.publ.net.exception.ErrorDetail;
import novj.publ.net.svolley.Request.IRequestBase;
import com.eagerminds.eagermindsledsoft.Contacts;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author user
 */
public class AppManagerPageController {
    @FXML
    Button btnUpdateSystem;
    @FXML
    Button btnUpdateSoftware;
    @FXML
    Button btnInstallAPK;
    @FXML
    Button btnUninstallAPK;
    @FXML
    TextField txtUninstallPackName;
    @FXML
    TextArea taMsg;

    public void updateLanguage() {
        btnUpdateSystem.setText(Contacts.getResourceValue("terminal_update_system"));
        btnUpdateSoftware.setText(Contacts.getResourceValue("terminal_update_software"));
        btnInstallAPK.setText(Contacts.getResourceValue("update_apkfile"));
    }

    @FXML
    void OnUpdateSystem(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(Contacts.getResourceValue("terminal_update_system"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter
                ("nuzip(*.nuzip)", "*.nuzip"));
        File systemFile = fileChooser.showOpenDialog(btnUpdateSystem.getScene().getWindow());
        if (null == systemFile) {
            return;
        }
        NovaOpt.GetInstance().startUpdateSystem(Contacts.deviceOpt.getSn(), systemFile, new OnFileTransferListener() {
            @Override
            public void onTransferred(long length) {
                showInfo("onTransferred: " + length + "byte");
            }

            @Override
            public void onStartTransfer() {
                showInfo("onStartTransfer");
            }

            @Override
            public void onSuccess(Integer response) {
                showInfo("onSuccess");
            }

            @Override
            public void onError(ErrorDetail error) {
                showInfo("onError：" + error.description);
            }
        });
    }

    @FXML
    void OnUpdateSoftware(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(Contacts.getResourceValue("terminal_update_software"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter
                ("nuzip(*.nuzip)", "*.nuzip"));
        File systemFile = fileChooser.showOpenDialog(btnUpdateSoftware.getScene().getWindow());
        if (null == systemFile) {
            return;
        }
        NovaOpt.GetInstance().startUpdateSoftware(Contacts.deviceOpt.getSn(), systemFile, new OnFileTransferListener() {
            @Override
            public void onTransferred(long length) {
                showInfo("onTransferred: " + length + "byte");
            }

            @Override
            public void onStartTransfer() {
                showInfo("onStartTransfer");
            }

            @Override
            public void onSuccess(Integer response) {
                showInfo("onSuccess");
            }

            @Override
            public void onError(ErrorDetail error) {
                showInfo("onError：" + error.description);
            }
        });
    }

    @FXML
    void OnInstallAPK(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(Contacts.getResourceValue("select_apk_file"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter
                ("APK(*.apk)", "*.apk"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter
                ("All Extensions", "*.*"));
        File apkFile = fileChooser.showOpenDialog(btnInstallAPK.getScene().getWindow());
        if (null == apkFile) {
            return;
        }
        String packageName = apkFile.getName();//update.apk
        NovaOpt.GetInstance().startUploadApk(Contacts.deviceOpt.getSn(), apkFile, packageName, true, new OnFileTransferListener() {
            @Override
            public void onTransferred(long length) {
                showInfo("onTransferred: " + length + "byte");
            }

            @Override
            public void onStartTransfer() {
                showInfo("APK: onStartTransfer");
            }

            @Override
            public void onSuccess(Integer response) {
                showInfo("APK: onSuccess");
            }

            @Override
            public void onError(ErrorDetail error) {
                showInfo("APK onError: " + error.description + "," + error.errorCode);
            }
        });
    }

    @FXML
    void OnUninstallAPK(ActionEvent actionEvent) {
        String uninstallPackName = txtUninstallPackName.getText();
        if (uninstallPackName == null || uninstallPackName.isEmpty()) {
            showInfo("Uninstalling！");
            return;
        }
        NovaOpt.GetInstance().uninstallPackage(Contacts.deviceOpt.getSn(), uninstallPackName, new OnResultListenerN<Integer, ErrorDetail>() {

            @Override
            public void onSuccess(IRequestBase requestBase, Integer response) {
                showInfo("onSuccess！");
            }

            @Override
            public void onError(IRequestBase requestBase, ErrorDetail error) {
                showInfo("onError！" + error.description + "(" + error.errorCode + ")");
            }
        });
    }

    void showInfo(String msgInfo) {
        Platform.runLater(() -> {
            Date day = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            taMsg.appendText("[" + df.format(day) + "]  " + msgInfo + "\r\n");
        });
    }

    void showDialog(Alert.AlertType alertType, String msgInfo) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setHeaderText(msgInfo);
            alert.setResult(ButtonType.OK);
            alert.showAndWait();
        });
    }
}
