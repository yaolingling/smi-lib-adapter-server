/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.adapter.server.model;

/**
 * @author prashanth.gowda
 *
 */
public class HypervisorInformation {

    private String name;
    private String versionName;


    /**
     * @return the name
     */
    public String getName() {
        return name;
    }


    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * @return the versionName
     */
    public String getVersionName() {
        return versionName;
    }


    /**
     * @param versionName the versionName to set
     */
    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

}
