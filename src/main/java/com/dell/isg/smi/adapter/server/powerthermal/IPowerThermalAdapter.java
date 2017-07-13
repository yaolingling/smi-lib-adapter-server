/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.adapter.server.powerthermal;

import com.dell.isg.smi.adapter.server.model.WsmanCredentials;
import com.dell.isg.smi.commons.model.server.JobStatus;

/**
 * @author rahman.muhammad
 *
 */
public interface IPowerThermalAdapter {

    public Object collectPowerMonitoring(WsmanCredentials credentials) throws Exception;

    public JobStatus setPowerCapping(WsmanCredentials credentials, String capValue) throws Exception;

    public JobStatus enablePowerCapping(WsmanCredentials credentials, String status) throws Exception;

    public JobStatus createConfigJob(WsmanCredentials credentials) throws Exception;
}
