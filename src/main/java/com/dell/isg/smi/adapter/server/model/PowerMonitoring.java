/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.adapter.server.model;

/**
 * @author rahman.muhammad
 *
 */
public class PowerMonitoring {

    protected String powerCap;
    protected String warningThreshold;
    protected String profile;
    protected String failureThreshold;
    protected String probeName;
    protected String currentReading;
    protected String inputAmps;
    protected String activePolicyName;
    protected PowerStatistics powerStatistics;
    protected HeadRoom headRoom;


    public String getActivePolicyName() {
        return activePolicyName;
    }


    public void setActivePolicyName(String activePolicyName) {
        this.activePolicyName = activePolicyName;
    }


    public String getPowerCap() {
        return powerCap;
    }


    public void setPowerCap(String powerCap) {
        this.powerCap = powerCap;
    }


    public String getWarningThreshold() {
        return warningThreshold;
    }


    public void setWarningThreshold(String warningThreshold) {
        this.warningThreshold = warningThreshold;
    }


    public String getProfile() {
        return profile;
    }


    public void setProfile(String profile) {
        this.profile = profile;
    }


    public String getFailureThreshold() {
        return failureThreshold;
    }


    public void setFailureThreshold(String failureThreshold) {
        this.failureThreshold = failureThreshold;
    }


    public PowerStatistics getPowerStatistics() {
        return powerStatistics;
    }


    public void setPowerStatistics(PowerStatistics powerStatistics) {
        this.powerStatistics = powerStatistics;
    }


    public HeadRoom getHeadRoom() {
        return headRoom;
    }


    public void setHeadRoom(HeadRoom headRoom) {
        this.headRoom = headRoom;
    }


    public String getProbeName() {
        return probeName;
    }


    public void setProbeName(String probeName) {
        this.probeName = probeName;
    }


    public String getCurrentReading() {
        return currentReading;
    }


    public void setCurrentReading(String currentReading) {
        this.currentReading = currentReading;
    }


    public String getInputAmps() {
        return inputAmps;
    }


    public void setInputAmps(String inputAmps) {
        this.inputAmps = inputAmps;
    }
}
