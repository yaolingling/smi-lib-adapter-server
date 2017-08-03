/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
/**
 * 
 */
package com.dell.isg.smi.adapter.server.osdeployment;

import com.dell.isg.smi.adapter.server.model.WsmanCredentials;
import com.dell.isg.smi.commons.model.credential.PasswordCredential;
import com.dell.isg.smi.commons.model.server.JobStatus;

/**
 * @author Michael_Regert
 *
 */
public interface OSDeploymentAdapter {
    public JobStatus connectToNetworkISO(WsmanCredentials cred, String shareIpAddress, String sharePath, String fileName);
    public JobStatus rebootServer(String serverAddress, String userName, String password);
}