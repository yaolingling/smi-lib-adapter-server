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

import com.dell.isg.smi.commons.elm.exception.RuntimeCoreException;
import com.dell.isg.smi.commons.model.credential.PasswordCredential;
import com.dell.isg.smi.commons.model.server.JobStatus;
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
    public JobStatus connectToNetworkISO(String oobIp, PasswordCredential oobCredentials, String isoIpAddress, String isoSharePath, String isoFileName) {
        try {
            ConnectNetworkISOImageCmd connectCmd = new ConnectNetworkISOImageCmd(oobIp, oobCredentials.getUsername(), oobCredentials.getPassword(), isoIpAddress, isoSharePath, isoFileName);
            CommandResponse resp = (CommandResponse) connectCmd.execute();

            JobStatus status = new JobStatus();
            // TODO Convert from CommandResponse to JobStatus here
            return status;

        } catch (RuntimeCoreException e) {
            throw e;
        } catch (Exception e) {
            RuntimeCoreException ex = new RuntimeCoreException(e);
            ex.setErrorID(267069);
            ex.addAttribute("ConnectToNetworkISO");
            ex.addAttribute(isoIpAddress);
            ex.addAttribute(e.getMessage());
            throw ex;
        }
    }


    public JobStatus rebootServer(String ipAddr, String userName, String password) {
        JobStatus status = new JobStatus();
        try {
            PowerBootCmd powerBootCmd = new PowerBootCmd(ipAddr, userName, password, PowerRebootEnum.valueOf("REBOOT"));
            int result = powerBootCmd.execute().intValue();
            logger.info("Reboot Executed");
        } catch (Exception e) {

        }
        // TODO Convert from result to JobStatus here

        return status;
    }
}
