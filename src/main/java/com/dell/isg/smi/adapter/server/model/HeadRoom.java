/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.adapter.server.model;

/**
 * @author rahman.muhammad
 *
 */
public class HeadRoom {

    protected String systemInstantaneousHeadroom;
    protected String systemPeakHeadroom;


    public String getSystemInstantaneousHeadroom() {
        return systemInstantaneousHeadroom;
    }


    public void setSystemInstantaneousHeadroom(String systemInstantaneousHeadroom) {
        this.systemInstantaneousHeadroom = systemInstantaneousHeadroom;
    }


    public String getSystemPeakHeadroom() {
        return systemPeakHeadroom;
    }


    public void setSystemPeakHeadroom(String systemPeakHeadroom) {
        this.systemPeakHeadroom = systemPeakHeadroom;
    }

}
