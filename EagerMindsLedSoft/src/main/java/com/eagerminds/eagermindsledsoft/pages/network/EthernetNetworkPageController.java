/*         
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerminds.eagermindsledsoft.pages.network;

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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import novj.platform.vxkit.common.bean.EthernetBean;
import novj.platform.vxkit.common.bean.EthernetInfo;
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
public class EthernetNetworkPageController {
    @FXML
    TextArea ta_show;
    @FXML
    TextField tf_ethernet_ip, tf_ethernet_mask, tf_ethernet_gateway, tf_ethernet_dns;
    @FXML
    ComboBox cb_dhcp;
    @FXML
    Button btn_get_ethernet, btn_set_ethernet;


    public void init() {
        ObservableList<String> openOptions = FXCollections.observableArrayList(
                Contacts.getResourceValue("yes"), Contacts.getResourceValue("no"));
        cb_dhcp.setItems(openOptions);
        cb_dhcp.getSelectionModel().select(0);
        cb_dhcp.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {

            }
        });
    }

    @FXML
    protected void onActionGetEthernet(ActionEvent actionEvent) {
        NovaOpt.GetInstance().getEthernetInfo(Contacts.deviceOpt.getSn(),
                new OnResultListenerN<EthernetBean, ErrorDetail>() {
            @Override
            public void onSuccess(IRequestBase requestBase, EthernetBean response) {
                if (null != response) {
                    showInfo(JSONUtil.toJsonStringByFastJson(response));
                } else {
                    showInfo("Error");
                }
            }

            @Override
            public void onError(IRequestBase requestBase, ErrorDetail error) {
                if (StringUtil.isEmpty(error.description)) {
                    showInfo("Error: onError");
                } else {
                    showInfo("Error:" + error.description);
                }
            }
        });
    }

    @FXML
    protected void onActionSetEthernet(ActionEvent actionEvent) {
        List<EthernetInfo> infoList = new ArrayList<>();
        EthernetInfo info = new EthernetInfo();
        if (0 == cb_dhcp.getSelectionModel().getSelectedIndex()) {
            info.dhcp = true;
        } else {
            info.dhcp = false;
            if (StringUtil.isEmpty(tf_ethernet_ip.getText().trim()) ||
                    StringUtil.isEmpty(tf_ethernet_mask.getText().trim()) ||
                    StringUtil.isEmpty(tf_ethernet_gateway.getText().trim()) ||
                    StringUtil.isEmpty(tf_ethernet_dns.getText().trim())) {
                showInfo("onActionSetEthernet");
                return;
            }
            info.ip = tf_ethernet_ip.getText().trim();
            info.mask = tf_ethernet_mask.getText().trim();
            info.gateWay = tf_ethernet_gateway.getText().trim();
            info.dns = new String[]{tf_ethernet_dns.getText().trim()};
        }

        infoList.add(info);
        EthernetBean ethernetBean = new EthernetBean();
        ethernetBean.ethernets = infoList;
        NovaOpt.GetInstance().setEthernetInfo(Contacts.deviceOpt.getSn(), ethernetBean, new OnResultListenerN<Integer, ErrorDetail>() {
            @Override
            public void onSuccess(IRequestBase requestBase, Integer response) {
                showInfo("onSuccess");
            }

            @Override
            public void onError(IRequestBase requestBase, ErrorDetail error) {
                if (StringUtil.isEmpty(error.description)){
                    showInfo("Error: onError");
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
