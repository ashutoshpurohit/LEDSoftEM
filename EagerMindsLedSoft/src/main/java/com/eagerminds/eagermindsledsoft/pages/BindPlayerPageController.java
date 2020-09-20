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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import novj.platform.vxkit.common.bean.player.PlayerBindingInfo;
import novj.platform.vxkit.common.bean.player.PlayerInfo;
import novj.platform.vxkit.common.bean.player.PlayerListBean;
import novj.platform.vxkit.common.bean.player.PlayerNodeBean;
import novj.platform.vxkit.common.bean.player.PlayerlistData;
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
public class BindPlayerPageController {
    @FXML
    TextArea ta_show;
    @FXML
    TextField tf_vnnox_server_address, tf_player_username, tf_player_password,
            tf_player_token, tf_server_player_identifier, tf_player_isused, tf_player_name,
            tf_player_identifier;

    @FXML
    TextField tf_node_host, tf_node_username, tf_node_password, tf_player_type;


    public void init() {
        tf_player_identifier.setText(Contacts.deviceOpt.getSn());
    }

    @FXML
    protected void onActionGetNode(ActionEvent actionEvent) {
        PlayerNodeBean nodeBean = NovaOpt.GetInstance().getCloudPlayerNodes();
        if (null != nodeBean) {
            showInfo(JSONUtil.toJsonStringByFastJson(nodeBean));
        }else {
            showInfo("Error: Failed to obtain Vnnox node");
        }
    }

    @FXML
    protected void onActionGetPlayerList(ActionEvent actionEvent) {
        String nodeHost = tf_node_host.getText();
        String nodeUsername = tf_node_username.getText();
        String nodePassword = tf_node_password.getText();
        String playerType = tf_player_type.getText();
        if (StringUtil.isEmpty(nodeHost) || StringUtil.isEmpty(nodeUsername) ||
                StringUtil.isEmpty(nodePassword) || StringUtil.isEmpty(playerType)) {
            showInfo("Error: Parameter error");
            return;
        }
        try {
            PlayerListBean playerListBean = NovaOpt.GetInstance().getCloudPlayerList(nodeHost,
                    nodeUsername,nodePassword,playerType);
            if (null != playerListBean){
                showInfo(JSONUtil.toJsonStringByFastJson(playerListBean));
                return;
            }
        } catch (Exception e) {
            showInfo(e.getLocalizedMessage());
        }
        showInfo("Error: Failed to get player list");
    }


    @FXML
    protected void onActionGetBind(ActionEvent actionEvent) {
        String sn = Contacts.deviceOpt.getSn();
        NovaOpt.GetInstance().getBindPlayer(sn, new OnResultListenerN<PlayerBindingInfo, ErrorDetail>() {
            @Override
            public void onSuccess(IRequestBase requestBase, PlayerBindingInfo response) {
                showInfo(JSONUtil.toJsonStringByFastJson(response));
            }

            @Override
            public void onError(IRequestBase requestBase, ErrorDetail error) {
                showInfo("Error:" + error.description);
            }
        });
    }

    @FXML
    protected void onActionSetBind(ActionEvent actionEvent) {
        if (StringUtil.isEmpty(tf_vnnox_server_address.getText().trim()) ||
                StringUtil.isEmpty(tf_player_username.getText().trim()) ||
                StringUtil.isEmpty(tf_player_password.getText().trim()) ||
                StringUtil.isEmpty(tf_player_token.getText().trim()) ||
                StringUtil.isEmpty(tf_server_player_identifier.getText().trim()) ||
                StringUtil.isEmpty(tf_player_isused.getText().trim()) ||
                StringUtil.isEmpty(tf_player_name.getText().trim()) ||
                StringUtil.isEmpty(tf_player_identifier.getText().trim())) {
            showInfo("Error: Parameter error");
            return;
        }

        PlayerInfo playerInfo = new PlayerInfo();
        if (tf_player_isused.getText().trim().equals("0")) {
            playerInfo.setUsed(true);
        } else if (tf_player_isused.getText().trim().equals("1")) {
            playerInfo.setUsed(false);
        } else {
            showInfo("Error: Is the player binding information wrong?");
            return;
        }
        playerInfo.setIdentifier(tf_server_player_identifier.getText().trim());
        playerInfo.setName(tf_player_name.getText().trim());
        playerInfo.setPlayerIdentifier(tf_player_identifier.getText().trim());

        List<PlayerInfo> playerInfos = new ArrayList<>();
        playerInfos.add(playerInfo);

        PlayerlistData playerlistData = new PlayerlistData();
        playerlistData.setToken(tf_player_token.getText().trim());
        playerlistData.setPlayerList(playerInfos);

        PlayerBindingInfo bindingInfo = new PlayerBindingInfo();
        bindingInfo.setBaseUrl(tf_vnnox_server_address.getText().trim());
        bindingInfo.setUsername(tf_player_username.getText().trim());
        bindingInfo.setPassword(tf_player_password.getText().trim());
        bindingInfo.setData(playerlistData);

        NovaOpt.GetInstance().setBindPlayer(Contacts.deviceOpt.getSn(), bindingInfo, new OnResultListenerN<Integer, ErrorDetail>() {
            @Override
            public void onSuccess(IRequestBase requestBase, Integer response) {
                showInfo("Bind successfully");
            }

            @Override
            public void onError(IRequestBase requestBase, ErrorDetail error) {
                if (StringUtil.isEmpty(error.description)) {
                    showInfo("Error: Binding failed");
                } else {
                    showInfo("Error:" + error.description);
                }
            }
        });
    }

    private void showInfo(String sInfo) {
        Platform.runLater(() -> {
            Date day = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            ta_show.appendText("[" + df.format(day) + "]  " + sInfo + "\r\n");
        });
    }

}
