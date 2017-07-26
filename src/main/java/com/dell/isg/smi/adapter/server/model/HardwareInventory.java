/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.adapter.server.model;

import java.util.ArrayList;
import java.util.List;

import com.dell.isg.smi.wsman.command.entity.CPUView;
import com.dell.isg.smi.wsman.command.entity.DCIMNICViewType;
import com.dell.isg.smi.wsman.command.entity.DCIMSystemViewType;
import com.dell.isg.smi.wsman.command.entity.FanView;
import com.dell.isg.smi.wsman.command.entity.MemoryView;
import com.dell.isg.smi.wsman.command.entity.NumericSensorView;
import com.dell.isg.smi.wsman.command.entity.PowerSupplyView;
import com.dell.isg.smi.wsman.command.entity.SensorView;
import com.dell.isg.smi.wsman.command.entity.VFlashView;

/**
 * @author rahman.muhammad
 *
 */
public class HardwareInventory {

    private DCIMSystemViewType system = null;
    private List<CPUView> cpuView = null;
    private List<MemoryView> memoryView = null;
    private List<DCIMNICViewType> nicView = null;
    private List<PowerSupplyView> powerSupplyView = null;
    private List<SensorView> batteryView = null;
    private List<SensorView> voltageView = null;
    private List<NumericSensorView> temperatureView = null;
    private List<FanView> fanView = null;
    private List<VFlashView> removableMedia = null;
    private PowerMonitoring powerMonitoring = null;
    private Storage storage = null;


    /**
     * @return the system
     */
    public DCIMSystemViewType getSystem() {
        return system;
    }


    /**
     * @param system the system to set
     */
    public void setSystem(DCIMSystemViewType system) {
        this.system = system;
    }


    /**
     * @return the cpuView
     */
    public List<CPUView> getCpuView() {
        if (cpuView == null) {
            cpuView = new ArrayList<CPUView>();
        }
        return cpuView;
    }


    /**
     * @param cpuView the cpuView to set
     */
    public void setCpuView(List<CPUView> cpuView) {
        this.cpuView = cpuView;
    }


    /**
     * @return the memoryView
     */
    public List<MemoryView> getMemoryView() {

        if (memoryView == null) {
            memoryView = new ArrayList<MemoryView>();
        }

        return memoryView;
    }


    /**
     * @param memoryView the memoryView to set
     */
    public void setMemoryView(List<MemoryView> memoryView) {
        this.memoryView = memoryView;
    }


    /**
     * @return the nicView
     */
    public List<DCIMNICViewType> getNicView() {

        if (nicView == null) {
            nicView = new ArrayList<DCIMNICViewType>();
        }

        return nicView;
    }


    /**
     * @param nicView the nicView to set
     */
    public void setNicView(List<DCIMNICViewType> nicView) {
        this.nicView = nicView;
    }


    /**
     * @return the powerSupplyView
     */
    public List<PowerSupplyView> getPowerSupplyView() {

        if (powerSupplyView == null) {
            powerSupplyView = new ArrayList<PowerSupplyView>();
        }

        return powerSupplyView;
    }


    /**
     * @param powerSupplyView the powerSupplyView to set
     */
    public void setPowerSupplyView(List<PowerSupplyView> powerSupplyView) {
        this.powerSupplyView = powerSupplyView;
    }


    /**
     * @return the batteryView
     */
    public List<SensorView> getBatteryView() {

        if (batteryView == null) {
            batteryView = new ArrayList<SensorView>();
        }

        return batteryView;
    }


    /**
     * @param batteryView the batteryView to set
     */
    public void setBatteryView(List<SensorView> batteryView) {
        this.batteryView = batteryView;
    }


    /**
     * @return the fanView
     */
    public List<FanView> getFanView() {

        if (fanView == null) {
            fanView = new ArrayList<FanView>();
        }

        return fanView;
    }


    /**
     * @param fanView the fanView to set
     */
    public void setFanView(List<FanView> fanView) {
        this.fanView = fanView;
    }


    /**
     * @return the storage
     */
    public Storage getStorage() {
        return storage;
    }


    /**
     * @param storage the storage to set
     */
    public void setStorage(Storage storage) {
        this.storage = storage;
    }


    /**
     * @return the removableMedia
     */
    public List<VFlashView> getRemovableMedia() {
        if (removableMedia == null) {
            removableMedia = new ArrayList<VFlashView>();
        }
        return removableMedia;
    }


    /**
     * @param removableMedia the removableMedia to set
     */
    public void setRemovableMedia(List<VFlashView> removableMedia) {
        this.removableMedia = removableMedia;
    }


    public List<SensorView> getVoltageView() {

        if (voltageView == null) {
            voltageView = new ArrayList<SensorView>();
        }
        return voltageView;
    }


    public void setVoltageView(List<SensorView> voltageView) {
        this.voltageView = voltageView;
    }


    public PowerMonitoring getPowerMonitoring() {
        return powerMonitoring;
    }


    public void setPowerMonitoring(PowerMonitoring powerMonitoring) {
        this.powerMonitoring = powerMonitoring;
    }


    public List<NumericSensorView> getTemperatureView() {
        return temperatureView;
    }


    public void setTemperatureView(List<NumericSensorView> temperatureView) {
        this.temperatureView = temperatureView;
    }

}
