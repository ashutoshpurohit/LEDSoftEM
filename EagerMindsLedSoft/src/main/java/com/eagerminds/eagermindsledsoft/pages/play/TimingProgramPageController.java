/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerminds.eagermindsledsoft.pages.play;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import com.eagerminds.eagermindsledsoft.DeviceInfo;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import novj.platform.vxkit.handy.api.ProgramSendManager;
import novj.publ.api.NovaOpt;
import novj.publ.api.beans.TimingParamBean;
import novj.publ.net.exception.ErrorDetail;
import novj.publ.util.StringUtil;
import com.eagerminds.eagermindsledsoft.Contacts;
import com.eagerminds.eagermindsledsoft.pages.AddTimingParamListener;

/**
 * FXML Controller class
 *
 * @author user
 */
public class TimingProgramPageController implements AddTimingParamListener {
    @FXML
    private ListView listview;
    @FXML
    private TextField tf_playtimes;
    private DeviceInfo deviceInfo;
    private int selectIndex = -1;
    @FXML
    private TextArea ta_info;
    private ObservableList<TimingParamBean> timingParamBeans =
            FXCollections.observableArrayList();


    public void init(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
        NovaOpt.GetInstance().initTimingProgram();
        initListView();
    }


    @FXML
    protected void btn_delete_program(ActionEvent actionEvent) {
        if (selectIndex >= 0) {
            NovaOpt.GetInstance().getTimingPlayParams().remove(selectIndex);
            timingParamBeans.remove(selectIndex);
            for (int x = 0; x < NovaOpt.GetInstance().getTimingPlayParams().size(); x++) {
                timingParamBeans.get(x).setId(x + 1);
                NovaOpt.GetInstance().getTimingPlayParams().get(x).setId(x + 1);
            }
        }
    }



    @FXML
    protected void btn_publish_program(ActionEvent actionEvent) {
        String program = tf_playtimes.getText();
        if (!StringUtil.isEmpty(program.trim())) {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("选择节目文件存放路径");
            File directory = directoryChooser.showDialog(tf_playtimes.getScene().getWindow());
            if (null == directory){
                return;
            }
            String programPath = directory.getAbsolutePath();
            NovaOpt.GetInstance().makeTimingProgram(program, programPath);
            try {
                InetAddress ia = InetAddress.getLocalHost();
                NovaOpt.GetInstance().startTransferTimingProgram(
                        deviceInfo.getSn(),
                        programPath,
                        program,
                        getLocalMac(ia),
                        true,
                        new ProgramSendManager.OnProgramTransferListener() {
                            @Override
                            public void onStarted() {
                                showInfo(Contacts.getResourceValue("start_transfer"));
                            }

                            @Override
                            public void onTransfer(long transferredSize, long totalSize) {
                                showInfo(Contacts.getResourceValue("publishing"));
                            }

                            @Override
                            public void onError(ErrorDetail errorDetail) {
                                showInfo(Contacts.getResourceValue("publish_failed")+":"+errorDetail.description);
                            }

                            @Override
                            public void onCompleted() {
                                showInfo(Contacts.getResourceValue("complete_published"));
                            }

                            @Override
                            public void onAborted() {
                                showInfo(Contacts.getResourceValue("publish_abort"));
                            }
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showInfo(String message) {
        Platform.runLater(() -> {
            Date day = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            ta_info.appendText("[" + df.format(day) + "]  " + message + "\r\n");
        });
    }


    private String[] weeks = {Contacts.getResourceValue("repeat_sunday"),
            Contacts.getResourceValue("repeat_monday"),
            Contacts.getResourceValue("repeat_tuesday"),
            Contacts.getResourceValue("repeat_wednesday"),
            Contacts.getResourceValue("repeat_thursday"),
            Contacts.getResourceValue("repeat_friday"),
            Contacts.getResourceValue("repeat_saturday")};

    private void initListView() {
        listview.setItems(timingParamBeans);
        listview.setCellFactory(e -> new ListCell<TimingParamBean>() {
            @Override
            protected void updateItem(TimingParamBean item, boolean empty) {
                super.updateItem(item, empty);
                if (null != item && !empty) {
                    HBox hb = new HBox();
                    Label labOrder = new Label();
                    labOrder.setFont(Font.font(14));
                    labOrder.setPrefSize(50, 30);
                    labOrder.setText(item.getId() + "");

                    Label labName = new Label();
                    labName.setFont(Font.font(14));
                    labName.setPrefSize(150, 30);
                    labName.setText((item.getProgramType().equals(TimingParamBean.FREE_PROGRAM) ?
                            Contacts.getResourceValue("timing_free_style") : "") +
                            item.getProgramName().substring(item.getProgramName().lastIndexOf(File.separator) + 1));

                    Label labValidDate = new Label();
                    labValidDate.setFont(Font.font(14));
                    labValidDate.setPrefSize(200, 30);
                    if (item.getStartDate().equals("1970-01-01") && item.getEndDate().equals(
                            "4012-01-01")) {
                        labValidDate.setText(Contacts.getResourceValue("valid_forever"));
                    } else {
                        labValidDate.setText(item.getStartDate() + "~" + item.getEndDate());
                    }

                    Label labRepeat = new Label();
                    labRepeat.setFont(Font.font(14));
                    labRepeat.setPrefSize(200, 30);
                    if (item.getWeekDays().size() == 7) {
                        labRepeat.setText(Contacts.getResourceValue("repeat_everyday"));
                    } else {
                        StringBuilder stringBuilder = new StringBuilder();
                        Iterator<Integer> iterator = item.getWeekDays().iterator();
                        while (iterator.hasNext()) {
                            stringBuilder.append(weeks[iterator.next() - 1] + "/");

                        }
                        labRepeat.setText(stringBuilder.toString().substring(0,
                                stringBuilder.toString().length() - 1));
                    }

                    Label labPlayTime = new Label();
                    labPlayTime.setFont(Font.font(14));
                    labPlayTime.setPrefSize(200, 30);
                    labPlayTime.setText(item.getStartTime() + "-" + item.getEndTime());


                    Platform.runLater(() -> {
                        hb.getChildren().add(labOrder);
                        hb.getChildren().add(labName);
                        hb.getChildren().add(labValidDate);
                        hb.getChildren().add(labRepeat);
                        hb.getChildren().add(labPlayTime);
                        hb.setAlignment(Pos.CENTER_LEFT);
                        setGraphic(hb);
                    });
                } else {
                    Platform.runLater(() -> setGraphic(null));
                }
            }
        });
        listview.getFocusModel().focusedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue,
                                Number newValue) {
                if (newValue.intValue() >= 0) {

                    selectIndex = newValue.intValue();
                } else {
                    selectIndex = -1;
                }
            }
        });
    }


    @FXML
    protected void btn_add_program(ActionEvent actionEvent) {
        try {
            Stage primaryStage = new Stage();
            URL location = getClass().getResource("/sample/pages/play/TimingProgramEditorPage.fxml");
            FXMLLoader fxmlLoader = Contacts.getFxmlLoader(location);
            Parent root = fxmlLoader.load();

            primaryStage.initModality(Modality.APPLICATION_MODAL);
            primaryStage.setTitle(Contacts.getResourceValue("add_play_plan"));
            primaryStage.setScene(new Scene(root));
            primaryStage.setResizable(false);
            primaryStage.show();

            TimingProgramEditorPageController timingProgramEditorPageController =
                    fxmlLoader.getController();
            timingProgramEditorPageController.init(this::addTimingParam);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取MAC地址
     *
     * @param ia
     * @return
     * @throws SocketException
     */
    private static String getLocalMac(InetAddress ia) throws SocketException {
        //获取网卡，获取地址
        String osName = System.getProperty("os.name");
        if (osName.startsWith("Windows")) {
            byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < mac.length; i++) {
                if (i != 0) {
                    sb.append("-");
                }
                //字节转换为整数
                int temp = mac[i] & 0xff;
                String str = Integer.toHexString(temp);
                if (str.length() == 1) {
                    sb.append("0" + str);
                } else {
                    sb.append(str);
                }
            }
            return sb.toString().toUpperCase();
        } else if (osName.startsWith("Linux")) {
            String command = "/bin/sh -c ifconfig -a";
            try {
                StringBuilder sBuilder = new StringBuilder();
                Process process = Runtime.getRuntime().exec(command);
                BufferedReader br = new BufferedReader(new InputStreamReader(process
                        .getInputStream()));
                String line;
                while (null != (line = br.readLine())) {
                    if (line.indexOf("ether") > 0) {
                        String address = line.split("ether")[1].trim()
                                .split("txqueuelen")[0].trim();
                        String[] macSplit = address.split(":");
                        for (int i = 0; i < macSplit.length; i++) {
                            if (0 != i) {
                                sBuilder.append("-");
                            }
                            if (macSplit[i].length() == 1) {
                                sBuilder.append("0" + macSplit[i]);
                            } else {
                                sBuilder.append(macSplit[i]);
                            }
                        }
                        break;
                    }
                }
                br.close();
                return sBuilder.toString().toUpperCase();
            } catch (Exception e) {

            }
        }
        return "";
    }

    @Override
    public void addTimingParam(TimingParamBean timingParamBean) {
        timingParamBeans.add(timingParamBean);
    }
}
