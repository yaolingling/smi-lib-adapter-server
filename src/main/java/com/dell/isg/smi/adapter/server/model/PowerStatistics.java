/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.adapter.server.model;

/**
 * @author rahman.muhammad
 *
 */
public class PowerStatistics {

    /*
     * Date data types are intentionally String here, so that Infrastructure layer do the transformation to ISO format
     */
    protected String energyConsumption;
    protected String energyConsumptionStartDateTime;
    protected String energyConsumptionEndDateTime;
    protected String systemPeakPower;
    protected String systemPeakPowerStartDateTime;
    protected String systemPeakPowerEndDateTime;
    protected String systemPeakAmps;
    protected String peakAmpsStartDateTime;
    protected String peakAmpsEndDateTime;
    protected String lastHourPeakTime;
    protected String lastDayPeakTime;
    protected String lastWeekPeakTime;
    protected String lastHourPeakAverage;
    protected String lastDayPeakAverage;
    protected String lastWeekPeakAverage;
    protected String lastHourPeakMax;
    protected String lastDayPeakMax;
    protected String lastWeekPeakMax;
    protected String lastHourPeakMaxTime;
    protected String lastDayPeakMaxTime;
    protected String lastWeekPeakMaxTime;
    protected String lastHourPeakMin;
    protected String lastDayPeakMin;
    protected String lastWeekPeakMin;
    protected String lastHourPeakMinTime;
    protected String lastDayPeakMinTime;
    protected String lastWeekPeakMinTime;


    public String getEnergyConsumption() {
        return energyConsumption;
    }


    public void setEnergyConsumption(String energyConsumption) {
        this.energyConsumption = energyConsumption;
    }


    public String getEnergyConsumptionStartDateTime() {
        return energyConsumptionStartDateTime;
    }


    public void setEnergyConsumptionStartDateTime(String energyConsumptionStartDateTime) {
        this.energyConsumptionStartDateTime = energyConsumptionStartDateTime;
    }


    public String getEnergyConsumptionEndDateTime() {
        return energyConsumptionEndDateTime;
    }


    public void setEnergyConsumptionEndDateTime(String energyConsumptionEndDateTime) {
        this.energyConsumptionEndDateTime = energyConsumptionEndDateTime;
    }


    public String getSystemPeakPower() {
        return systemPeakPower;
    }


    public void setSystemPeakPower(String systemPeakPower) {
        this.systemPeakPower = systemPeakPower;
    }


    public String getSystemPeakPowerStartDateTime() {
        return systemPeakPowerStartDateTime;
    }


    public void setSystemPeakPowerStartDateTime(String systemPeakPowerStartDateTime) {
        this.systemPeakPowerStartDateTime = systemPeakPowerStartDateTime;
    }


    public String getSystemPeakPowerEndDateTime() {
        return systemPeakPowerEndDateTime;
    }


    public void setSystemPeakPowerEndDateTime(String systemPeakPowerEndDateTime) {
        this.systemPeakPowerEndDateTime = systemPeakPowerEndDateTime;
    }


    public String getSystemPeakAmps() {
        return systemPeakAmps;
    }


    public void setSystemPeakAmps(String systemPeakAmps) {
        this.systemPeakAmps = systemPeakAmps;
    }


    public String getPeakAmpsStartDateTime() {
        return peakAmpsStartDateTime;
    }


    public void setPeakAmpsStartDateTime(String peakAmpsStartDateTime) {
        this.peakAmpsStartDateTime = peakAmpsStartDateTime;
    }


    public String getPeakAmpsEndDateTime() {
        return peakAmpsEndDateTime;
    }


    public void setPeakAmpsEndDateTime(String peakAmpsEndDateTime) {
        this.peakAmpsEndDateTime = peakAmpsEndDateTime;
    }


    public String getLastHourPeakTime() {
        return lastHourPeakTime;
    }


    public void setLastHourPeakTime(String lastHourPeakTime) {
        this.lastHourPeakTime = lastHourPeakTime;
    }


    public String getLastDayPeakTime() {
        return lastDayPeakTime;
    }


    public void setLastDayPeakTime(String lastDayPeakTime) {
        this.lastDayPeakTime = lastDayPeakTime;
    }


    public String getLastWeekPeakTime() {
        return lastWeekPeakTime;
    }


    public void setLastWeekPeakTime(String lastWeekPeakTime) {
        this.lastWeekPeakTime = lastWeekPeakTime;
    }


    public String getLastHourPeakAverage() {
        return lastHourPeakAverage;
    }


    public void setLastHourPeakAverage(String lastHourPeakAverage) {
        this.lastHourPeakAverage = lastHourPeakAverage;
    }


    public String getLastDayPeakAverage() {
        return lastDayPeakAverage;
    }


    public void setLastDayPeakAverage(String lastDayPeakAverage) {
        this.lastDayPeakAverage = lastDayPeakAverage;
    }


    public String getLastWeekPeakAverage() {
        return lastWeekPeakAverage;
    }


    public void setLastWeekPeakAverage(String lastWeekPeakAverage) {
        this.lastWeekPeakAverage = lastWeekPeakAverage;
    }


    public String getLastHourPeakMax() {
        return lastHourPeakMax;
    }


    public void setLastHourPeakMax(String lastHourPeakMax) {
        this.lastHourPeakMax = lastHourPeakMax;
    }


    public String getLastDayPeakMax() {
        return lastDayPeakMax;
    }


    public void setLastDayPeakMax(String lastDayPeakMax) {
        this.lastDayPeakMax = lastDayPeakMax;
    }


    public String getLastWeekPeakMax() {
        return lastWeekPeakMax;
    }


    public void setLastWeekPeakMax(String lastWeekPeakMax) {
        this.lastWeekPeakMax = lastWeekPeakMax;
    }


    public String getLastHourPeakMaxTime() {
        return lastHourPeakMaxTime;
    }


    public void setLastHourPeakMaxTime(String lastHourPeakMaxTime) {
        this.lastHourPeakMaxTime = lastHourPeakMaxTime;
    }


    public String getLastDayPeakMaxTime() {
        return lastDayPeakMaxTime;
    }


    public void setLastDayPeakMaxTime(String lastDayPeakMaxTime) {
        this.lastDayPeakMaxTime = lastDayPeakMaxTime;
    }


    public String getLastWeekPeakMaxTime() {
        return lastWeekPeakMaxTime;
    }


    public void setLastWeekPeakMaxTime(String lastWeekPeakMaxTime) {
        this.lastWeekPeakMaxTime = lastWeekPeakMaxTime;
    }


    public String getLastHourPeakMin() {
        return lastHourPeakMin;
    }


    public void setLastHourPeakMin(String lastHourPeakMin) {
        this.lastHourPeakMin = lastHourPeakMin;
    }


    public String getLastDayPeakMin() {
        return lastDayPeakMin;
    }


    public void setLastDayPeakMin(String lastDayPeakMin) {
        this.lastDayPeakMin = lastDayPeakMin;
    }


    public String getLastWeekPeakMin() {
        return lastWeekPeakMin;
    }


    public void setLastWeekPeakMin(String lastWeekPeakMin) {
        this.lastWeekPeakMin = lastWeekPeakMin;
    }


    public String getLastHourPeakMinTime() {
        return lastHourPeakMinTime;
    }


    public void setLastHourPeakMinTime(String lastHourPeakMinTime) {
        this.lastHourPeakMinTime = lastHourPeakMinTime;
    }


    public String getLastDayPeakMinTime() {
        return lastDayPeakMinTime;
    }


    public void setLastDayPeakMinTime(String lastDayPeakMinTime) {
        this.lastDayPeakMinTime = lastDayPeakMinTime;
    }


    public String getLastWeekPeakMinTime() {
        return lastWeekPeakMinTime;
    }


    public void setLastWeekPeakMinTime(String lastWeekPeakMinTime) {
        this.lastWeekPeakMinTime = lastWeekPeakMinTime;
    }

}