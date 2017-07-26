/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.adapter.server;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dell.isg.smi.adapter.server.model.NetworkShare;
import com.dell.isg.smi.adapter.server.model.WsmanCredentials;
import com.dell.isg.smi.commons.utilities.CustomRecursiveToStringStyle;
import com.dell.isg.smi.wsman.model.XmlConfig;


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

    @Ignore
    @Test
    public void cloneConfigTest() throws Exception {
    	XmlConfig config = serverAdapterImpl.previewImportServerConfig(wsmanCredentials, networkShare);
    	Object result = serverAdapterImpl.previewConfigResults(wsmanCredentials, config.getJobID());
    	System.out.println("Result : " + result);
    }

    @Ignore
    @Test
    public void exportHWInventoryTest() throws Exception {
    	XmlConfig result = serverAdapterImpl.exportHardwareInventory(wsmanCredentials, networkShare);
    	System.out.println("Result : "+ ReflectionToStringBuilder.toString(result, new CustomRecursiveToStringStyle(99)));
    }
    
    @Ignore
    @Test
    public void exportFactorySettingTest() throws Exception {
    	XmlConfig result = serverAdapterImpl.exportFactorySetting(wsmanCredentials, networkShare);
    	System.out.println("Result : "+ ReflectionToStringBuilder.toString(result, new CustomRecursiveToStringStyle(99)));
    }
}
