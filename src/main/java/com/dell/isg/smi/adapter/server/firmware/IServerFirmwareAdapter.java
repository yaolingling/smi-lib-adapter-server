/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
/**
 * 
 */
package com.dell.isg.smi.adapter.server.firmware;

import java.util.List;

import com.dell.isg.smi.commons.model.server.firmware.SoftwareIdentity;
import com.dell.isg.smi.adapter.server.model.WsmanCredentials;

/**
 * @author rahman.muhammad
 *
 */
public interface IServerFirmwareAdapter {

    public List<SoftwareIdentity> getSoftwareIdentity(WsmanCredentials credential) throws Exception;

}
