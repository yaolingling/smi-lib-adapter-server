/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.adapter.server.model;

import com.dell.isg.smi.adapter.server.config.ConfigEnum.SHARE_TYPES;

/**
 * @author rahman.muhammad
 *
 */
public class NetworkShare {
    private SHARE_TYPES shareType;
    private String shareName;
    private String shareAddress;
    private String fileName;
    private String shareUserName;
    private String sharePassword;


    public SHARE_TYPES getShareType() {
        return shareType;
    }


    public void setShareType(SHARE_TYPES shareType) {
        this.shareType = shareType;
    }


    public String getShareName() {
        return shareName;
    }


    public void setShareName(String shareName) {
        this.shareName = shareName;
    }


    public String getShareAddress() {
        return shareAddress;
    }


    public void setShareAddress(String shareAddress) {
        this.shareAddress = shareAddress;
    }


    public String getFileName() {
        return fileName;
    }


    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    public String getShareUserName() {
        return shareUserName;
    }


    public void setShareUserName(String shareUserName) {
        this.shareUserName = shareUserName;
    }


    public String getSharePassword() {
        return sharePassword;
    }


    public void setSharePassword(String sharePassword) {
        this.sharePassword = sharePassword;
    }
}
