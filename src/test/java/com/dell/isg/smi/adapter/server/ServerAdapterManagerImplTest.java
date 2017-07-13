/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.adapter.server;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dell.isg.smi.adapter.server.inventory.IInventoryAdapter;
import com.dell.isg.smi.adapter.server.model.WsmanCredentials;
import com.dell.isg.smi.adapter.server.powerthermal.IPowerThermalAdapter;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * @author prashanth.gowda
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/test-server-adapter-context.xml" })
public class ServerAdapterManagerImplTest {

    @Autowired
    IPowerThermalAdapter powerThermalAdapterImpl;
    
    @Autowired
    IInventoryAdapter inventoryAdapterImpl;
    

    @Autowired
    WsmanCredentials wsmanCredentials;


    @Ignore
    @Test
    public void collectHardwareInventoryTest() throws Exception {
        Object result = powerThermalAdapterImpl.collectPowerMonitoring(wsmanCredentials);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(result);
        System.out.println("Result :" + json);
    }


}
