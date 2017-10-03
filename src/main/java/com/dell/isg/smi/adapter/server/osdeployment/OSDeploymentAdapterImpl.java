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
import com.dell.isg.smi.wsman.command.DetachISOImageCmd;
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


	/* (non-Javadoc)
	 * @see com.dell.isg.smi.adapter.server.osdeployment.OSDeploymentAdapter#connectToNetworkISO(com.dell.isg.smi.adapter.server.model.WsmanCredentials, java.lang.String, java.lang.String, java.lang.String)
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


	/* (non-Javadoc)
	 * @see com.dell.isg.smi.adapter.server.osdeployment.OSDeploymentAdapter#rebootServer(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public JobStatus rebootServer(String serverAddress, String userName, String password) {
		JobStatus status = new JobStatus();
		try {
			PowerBootCmd powerBootCmd = new PowerBootCmd(serverAddress, userName, password, PowerRebootEnum.valueOf("REBOOT"));
			int result = powerBootCmd.execute().intValue();
			logger.info("Reboot Executed, %s", result);
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

	/* (non-Javadoc)
	 * @see com.dell.isg.smi.adapter.server.osdeployment.OSDeploymentAdapter#detachISO(com.dell.isg.smi.adapter.server.model.WsmanCredentials)
	 */
	@Override
    public JobStatus detachISO(WsmanCredentials wsmanCredentials) {
	    JobStatus status = new JobStatus();
	    status.setJobId("");
	    status.setServerAddress(wsmanCredentials.getAddress());
        try {
            DetachISOImageCmd detachCmd = new DetachISOImageCmd(wsmanCredentials.getAddress(), wsmanCredentials.getUserName(), wsmanCredentials.getPassword());
            Object obj = detachCmd.execute();
            if ( Boolean.TRUE.toString().equals(obj.toString())) {
                status.setStatus(JobStatusEnum.LCJobStatus.COMPLETED.toString());
                status.setMessage("DetachIso initiated");
                status.setDescription("Server Detach Iso initiated");
            } else {
                status.setStatus(JobStatusEnum.LCJobStatus.FAILED.toString());
                if(obj instanceof String){
                    status.setMessage((String) obj);
                }            
            }
        } catch (RuntimeCoreException e) {
            throw e;
        } catch (Exception e) {
            RuntimeCoreException ex = new RuntimeCoreException(e);
            ex.setErrorID(267069);
            ex.addAttribute("DetachISOImage");
            ex.addAttribute(wsmanCredentials.getAddress());
            ex.addAttribute(e.getMessage());
            throw ex;
        }
        return status;
    }
}
