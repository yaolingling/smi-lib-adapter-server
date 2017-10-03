/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
/**
 * 
 */
package com.dell.isg.smi.adapter.server.osdeployment;

import com.dell.isg.smi.adapter.server.model.WsmanCredentials;
import com.dell.isg.smi.commons.model.server.JobStatus;

/**
 * @author Michael_Regert
 *
 */
public interface OSDeploymentAdapter {
    
    /**
     * Connect to network ISO.
     *
     * @param cred the cred
     * @param shareIpAddress the share ip address
     * @param sharePath the share path
     * @param fileName the file name
     * @return the job status
     */
    public JobStatus connectToNetworkISO(WsmanCredentials cred, String shareIpAddress, String sharePath, String fileName);
    
    /**
     * Reboot server.
     *
     * @param serverAddress the server address
     * @param userName the user name
     * @param password the password
     * @return the job status
     */
    public JobStatus rebootServer(String serverAddress, String userName, String password);
    
    /**
     * Detach ISO.
     *
     * @param wsmanCredentials the wsman credentials
     * @return the job status
     */
    public JobStatus detachISO (WsmanCredentials wsmanCredentials);
}