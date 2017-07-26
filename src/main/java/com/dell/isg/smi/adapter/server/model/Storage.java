/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.adapter.server.model;

import java.util.ArrayList;
import java.util.List;
import com.dell.isg.smi.wsman.command.entity.ControllerView;
import com.dell.isg.smi.wsman.command.entity.EnclosureView;
import com.dell.isg.smi.wsman.command.entity.PhysicalDiskView;
import com.dell.isg.smi.wsman.command.entity.VirtualDiskView;

/**
 * @author rahman.muhammad
 *
 */
public class Storage {

    List<ControllerView> controllers = null;
    List<PhysicalDiskView> physicalDisks = null;
    List<VirtualDiskView> virtualDisks = null;
    List<EnclosureView> enclosures = null;


    public List<ControllerView> getControllers() {
        if (controllers == null)
            controllers = new ArrayList<ControllerView>();

        return controllers;
    }


    public void setControllers(List<ControllerView> controllers) {
        this.controllers = controllers;
    }


    public List<PhysicalDiskView> getPhysicalDisks() {
        if (physicalDisks == null)
            physicalDisks = new ArrayList<PhysicalDiskView>();

        return physicalDisks;
    }


    public void setPhysicalDisks(List<PhysicalDiskView> physicalDisks) {
        this.physicalDisks = physicalDisks;
    }


    public List<VirtualDiskView> getVirtualDisks() {
        if (virtualDisks == null)
            virtualDisks = new ArrayList<VirtualDiskView>();

        return virtualDisks;
    }


    public void setVirtualDisks(List<VirtualDiskView> virtualDisks) {
        this.virtualDisks = virtualDisks;
    }


    public List<EnclosureView> getEnclosures() {
        if (enclosures == null)
            enclosures = new ArrayList<EnclosureView>();
        return enclosures;
    }


    public void setEnclosures(List<EnclosureView> enclosures) {
        this.enclosures = enclosures;
    }

}
