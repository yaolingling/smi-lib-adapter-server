/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.adapter.server.powerthermal;

import com.dell.isg.smi.commons.model.server.JobStatus;
import com.dell.isg.smi.wsman.IdracWSManClient;
import com.dell.isg.smi.adapter.server.model.PowerMonitoring;
import com.dell.isg.smi.adapter.server.model.WsmanCredentials;

/**
 * @author rahman.muhammad
 *
 */
public interface IPowerThermalAdapter {

    public PowerMonitoring collectPowerMonitoring(IdracWSManClient idracWsManClient) throws Exception;


    public PowerMonitoring collectPowerMonitoring(String address, String userName, String password) throws Exception;


    public JobStatus setPowerCapping(WsmanCredentials credentials, String capValue) throws Exception;


    public JobStatus enablePowerCapping(WsmanCredentials credentials, String status) throws Exception;


    public JobStatus createConfigJob(WsmanCredentials credentials) throws Exception;
}
