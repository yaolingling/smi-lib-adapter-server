/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
/**
 * 
 */
package com.dell.isg.smi.adapter.server.firmware;

import java.util.List;

import com.dell.isg.smi.commons.model.server.firmware.SoftwareIdentity;
import com.dell.isg.smi.wsman.command.EnumerateFirmwareInventory;
import com.dell.isg.smi.adapter.server.model.WsmanCredentials;

/**
 * @author rahman.muhammad
 *
 */
public class ServerFirmwareAdapterImpl implements IServerFirmwareAdapter {

    public final static int PORT = 443;
    public final static boolean cCheck = false;


    @SuppressWarnings("unchecked")
    @Override
    public List<SoftwareIdentity> getSoftwareIdentity(WsmanCredentials credential) throws Exception {

        EnumerateFirmwareInventory cmd = new EnumerateFirmwareInventory(credential.getAddress(), PORT, credential.getUserName(), credential.getPassword(), cCheck);
        return (List<SoftwareIdentity>) cmd.execute();

    }

}
