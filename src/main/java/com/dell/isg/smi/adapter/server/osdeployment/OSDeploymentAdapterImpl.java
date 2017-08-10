/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
/**
 *
 */
package com.dell.isg.smi.adapter.server.osdeployment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.dell.isg.smi.adapter.server.model.WsmanCredentials;
import com.dell.isg.smi.commons.elm.exception.RuntimeCoreException;
import com.dell.isg.smi.commons.model.server.JobStatus;
import com.dell.isg.smi.commons.model.server.JobStatusEnum;
import com.dell.isg.smi.wsman.command.ConnectNetworkISOImageCmd;
import com.dell.isg.smi.wsman.command.PowerBootCmd;
import com.dell.isg.smi.wsman.command.PowerBootCmd.PowerRebootEnum;
import com.dell.isg.smi.wsman.entity.CommandResponse;

/**
 * @author Michael_Regert
 *
 */
@Component
public class OSDeploymentAdapterImpl implements OSDeploymentAdapter {

	private static final Logger logger = LoggerFactory.getLogger(OSDeploymentAdapterImpl.class.getName());


	/**
	 * Mounts an ISO image on a server
	 *
	 * @param ipAddress the ip address of the OOB management card
	 * @param username the userName for the OOB management card
	 * @param password the password for the OOB management card
	 * @param isoIpAddress the ip address of the share where the ISO resides
	 * @param isoSharePath the share name and/or path for the iso file
	 * @param isoFileName the ISO file name
	 * @return the job status
	 *
	 */

	@Override

	public JobStatus connectToNetworkISO(WsmanCredentials cred, String shareAddress, String sharePath, String fileName){

		try {
			
			ConnectNetworkISOImageCmd connectCmd = new ConnectNetworkISOImageCmd(cred.getAddress(), cred.getUserName(), cred.getPassword(), shareAddress, sharePath, fileName);
			CommandResponse resp = (CommandResponse) connectCmd.execute();
			JobStatus status = new JobStatus();
			if (resp!=null){
				status.setJobId(resp.getJobID());
				status.setServerAddress(cred.getAddress());
				status.setStatus(String.valueOf(resp.isbSuccess()));
			}

			return status;

		} catch (RuntimeCoreException e) {
			throw e;
		} catch (Exception e) {
			RuntimeCoreException ex = new RuntimeCoreException(e);
			ex.setErrorID(267069);
			ex.addAttribute("ConnectToNetworkISO");
			ex.addAttribute(shareAddress);
			ex.addAttribute(e.getMessage());
			throw ex;
		}
	}


	@Override
	public JobStatus rebootServer(String serverAddress, String userName, String password) {
		JobStatus status = new JobStatus();
		try {
			PowerBootCmd powerBootCmd = new PowerBootCmd(serverAddress, userName, password, PowerRebootEnum.valueOf("REBOOT"));
			int result = powerBootCmd.execute().intValue();
			logger.info("Reboot Executed");
		} catch (Exception e) {
			RuntimeCoreException ex = new RuntimeCoreException(e);
			ex.setErrorID(267069);
			ex.addAttribute("RebootServer");
			ex.addAttribute(serverAddress);
			ex.addAttribute(e.getMessage());
			throw ex;
		}

		status.setJobId("");
		status.setServerAddress(serverAddress);
		status.setStatus(JobStatusEnum.LCJobStatus.INPROGRESS.toString());
		status.setMessage("Server has started Rebooting");
		status.setDescription("Server Reboot has been invoked and started rebooting");
		return status;
	}
}
