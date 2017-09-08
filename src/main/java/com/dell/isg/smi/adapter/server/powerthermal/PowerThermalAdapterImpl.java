/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */

package com.dell.isg.smi.adapter.server.powerthermal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dell.isg.smi.commons.model.server.JobStatus;
import com.dell.isg.smi.wsman.IdracWSManClient;
import com.dell.isg.smi.wsman.WSManClientFactory;
import com.dell.isg.smi.wsman.command.CreateConfigJobCmd;
import com.dell.isg.smi.wsman.command.EnumerateSystemViewCmd;
import com.dell.isg.smi.wsman.command.SetPowerCappingCmd;
import com.dell.isg.smi.wsman.command.entity.ConfigJobDetail;
import com.dell.isg.smi.wsman.command.entity.DCIMSystemViewType;
import com.dell.isg.smi.adapter.server.model.PowerMonitoring;
import com.dell.isg.smi.adapter.server.model.PowerMonitoringConstants;
import com.dell.isg.smi.adapter.server.model.WsmanCredentials;

/**
 * @author rahman.muhammad
 *
 */
public class PowerThermalAdapterImpl implements IPowerThermalAdapter {

    private static final Logger logger = LoggerFactory.getLogger(PowerThermalAdapterImpl.class.getName());


    @Override
    public PowerMonitoring collectPowerMonitoring(IdracWSManClient idracWsManClient) throws Exception {

        PowerMonitoringCollector collector = new PowerMonitoringCollector();
        return collector.collectPowerMonitoring(idracWsManClient);

    }


    @Override
    public PowerMonitoring collectPowerMonitoring(String address, String userName, String password) throws Exception {

        logger.info("Collecting PowerMonitoring data from PowerAdapter for server {}", address);

        DCIMSystemViewType system = null;
        IdracWSManClient idracWsManClient = WSManClientFactory.getIdracWSManClient(address, userName, password);
        system = idracWsManClient.execute(new EnumerateSystemViewCmd());
        PowerMonitoring powerMonitoring = this.collectPowerMonitoring(idracWsManClient);
        powerMonitoring.setPowerCap(system.getPowerCap().getValue() + " " + PowerMonitoringConstants.WATT);

        return powerMonitoring;
    }


    @Override
    public JobStatus setPowerCapping(WsmanCredentials credentials, String capValue) throws Exception {

        logger.info("Configuring Power capping for server node {}", credentials.getAddress());
        JobStatus job = new JobStatus();
        job.setServerAddress(credentials.getAddress());
        SetPowerCappingCmd cmd = new SetPowerCappingCmd(credentials.getAddress(), credentials.getUserName(), credentials.getPassword());
        cmd.addAttributeKeyValue("ServerPwr.1#PowerCapValue", capValue);

        Object result = cmd.execute();
        return job;
    }


    @Override
    public JobStatus enablePowerCapping(WsmanCredentials credentials, String status) throws Exception {

        SetPowerCappingCmd cmd = new SetPowerCappingCmd(credentials.getAddress(), credentials.getUserName(), credentials.getPassword());
        cmd.addAttributeKeyValue("ServerPwr.1#PowerCapSetting", status);
        Object result = cmd.execute();

        return null;

    }


    public JobStatus createConfigJob(WsmanCredentials credentials) throws Exception {
    	String target = "System.Embedded.1";

        CreateConfigJobCmd configCmd = new CreateConfigJobCmd(credentials.getAddress(), credentials.getUserName(), credentials.getPassword(), false, target);
        ConfigJobDetail job = configCmd.execute();
        return this.getJobStatus(job);

    }


    private JobStatus getJobStatus(ConfigJobDetail job) {

        JobStatus jobStatus = new JobStatus();

        if (job != null) {
            jobStatus.setJobId(job.getJobList().get(0));
            jobStatus.setStatus(job.getReturnCode().toString());

        }

        return jobStatus;

    }

}