/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.adapter.server;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dell.isg.smi.adapter.server.model.NetworkShare;
import com.dell.isg.smi.adapter.server.model.WsmanCredentials;


/**
 * @author prashanth.gowda
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/test-server-adapter-context.xml" })
public class ServerAdapterManagerImplTest {

    @Autowired
    IServerAdapter serverAdapterImpl;
    

    @Autowired
    WsmanCredentials wsmanCredentials;
    
    @Autowired
    NetworkShare networkShare;

    @Test
    public void cloneConfigTest() throws Exception {
    	//XmlConfig config = serverAdapterImpl.previewImportServerConfig(wsmanCredentials, networkShare);
    	Object result = serverAdapterImpl.previewConfigResults(wsmanCredentials, "JID_004752690265");
    	System.out.println("Result : " + result);
    }


}
