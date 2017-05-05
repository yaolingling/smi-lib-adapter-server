/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
/**
 * 
 */
package com.dell.isg.smi.adapter.server.osdeployment;

import com.dell.isg.smi.commons.model.credential.PasswordCredential;
import com.dell.isg.smi.commons.model.server.JobStatus;

/**
 * @author Michael_Regert
 *
 */
public interface OSDeploymentAdapter {
    public JobStatus connectToNetworkISO(String oobIp, PasswordCredential oobCredentials, String isoIpAddress, String isoSharePath, String isoFileName);


    public JobStatus rebootServer(String ipAddr, String userName, String password);
}