/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.adapter.server;

/**
 * @author pradeep.nagandla
 *
 */

import com.dell.isg.smi.wsman.command.entity.DCIMBIOSConfig;
import com.dell.isg.smi.wsman.command.entity.BootOrderDetails;
import com.dell.isg.smi.adapter.server.model.WsmanCredentials;

public interface IServerDeviceHardwareAdapter {

    DCIMBIOSConfig collectBiosConfig(WsmanCredentials wsmanCredentials) throws Exception;


    BootOrderDetails getBootOrderDetails(WsmanCredentials credentials) throws Exception;

}
