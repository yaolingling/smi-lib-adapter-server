/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.adapter.server.inventory;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.dell.isg.smi.adapter.server.model.WsmanCredentials;
import com.dell.isg.smi.wsmanclient.IWSManClient;
import com.dell.isg.smi.wsmanclient.WSManClientFactory;
import com.dell.isg.smi.wsmanclient.impl.DefaultEnumerate;
import com.dell.isg.smi.wsmanclient.WSManConstants.WSManClassEnum;

@Component("inventoryAdapter, inventoryAdapterImpl")
public class InventoryAdapterImpl implements IInventoryAdapter {

	private static final Logger logger = LoggerFactory.getLogger(InventoryAdapterImpl.class.getName());

    @Override
    public Object collectHardwareInventory(WsmanCredentials credentials) throws Exception {
        logger.info("collecting hardware inventory for {} ", credentials.getAddress());
        Map<String, Object> results = new LinkedHashMap<>();

        IWSManClient IWSManClient = WSManClientFactory.getClient(credentials.getAddress(), credentials.getUserName(), credentials.getPassword());
        IWSManClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_SystemView));

        results.put(WSManClassEnum.DCIM_SystemView.name(), IWSManClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_SystemView)));
        results.put(WSManClassEnum.DCIM_Memoryview.name(), IWSManClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_Memoryview)));
        results.put(WSManClassEnum.DCIM_Powersupplyview.name(), IWSManClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_Powersupplyview)));
        results.put(WSManClassEnum.DCIM_CPUView.name(), IWSManClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_CPUView)));
        results.put(WSManClassEnum.DCIM_VFlashView.name(), IWSManClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_VFlashView)));
        results.put(WSManClassEnum.DCIM_FanView.name(), IWSManClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_FanView)));
        results.put(WSManClassEnum.DCIM_EnclosureView.name(), IWSManClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_EnclosureView)));
        results.put(WSManClassEnum.DCIM_VirtualDiskView.name(), IWSManClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_VirtualDiskView)));
        results.put(WSManClassEnum.DCIM_Sensor.name(), IWSManClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_Sensor)));
        results.put(WSManClassEnum.DCIM_NumericSensor.name(), IWSManClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_NumericSensor)));
        results.put(WSManClassEnum.DCIM_SystemString.name(), IWSManClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_SystemString)));
        results.put(WSManClassEnum.DCIM_ControllerView.name(), IWSManClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_ControllerView)));
        results.put(WSManClassEnum.DCIM_ControllerBatteryView.name(), IWSManClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_ControllerBatteryView)));
        results.put(WSManClassEnum.DCIM_RAIDEnumeration.name(), IWSManClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_RAIDEnumeration)));
        results.put(WSManClassEnum.DCIM_RAIDInteger.name(), IWSManClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_RAIDInteger))); 
        return results;

    }

	@Override
    public Object collectSummary(WsmanCredentials credentials) throws Exception {
        logger.info("collecting System Identity Info for {} ", credentials.getAddress());
        IWSManClient IWSManClient = WSManClientFactory.getClient(credentials.getAddress(), credentials.getUserName(), credentials.getPassword());
        Object response = IWSManClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_SystemView));
        return response;
    }


	@Override
    public Object collectNics(WsmanCredentials credentials) throws Exception {
        logger.info("Collecting NIC info.");
        Map<String, Object> results = new LinkedHashMap<>();
        IWSManClient IWSManClient = WSManClientFactory.getClient(credentials.getAddress(), credentials.getUserName(), credentials.getPassword());
        results.put(WSManClassEnum.DCIM_NICView.name(), IWSManClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_NICView)));
        results.put(WSManClassEnum.DCIM_NICEnumeration.name(), IWSManClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_NICEnumeration)));
        results.put(WSManClassEnum.DCIM_NICString.name(), IWSManClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_NICString)));
        return results;
    }
    
    @Override
    public Object collectSelLogs(WsmanCredentials wsmanCredentials) throws Exception {
    	logger.info("collecting Sel log for {} ", wsmanCredentials.getAddress());
        IWSManClient IWSManClient = WSManClientFactory.getClient(wsmanCredentials.getAddress(), wsmanCredentials.getUserName(), wsmanCredentials.getPassword());
        Object response = IWSManClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_Sellogentry));
        return response;
    }

    @Override
    public Object collectLcLogs(WsmanCredentials wsmanCredentials) throws Exception {
        IWSManClient IWSManClient = WSManClientFactory.getClient(wsmanCredentials.getAddress(), wsmanCredentials.getUserName(), wsmanCredentials.getPassword());
        Object response = IWSManClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_LCLogEntry));
        return response;
    }

    @Override
    public Object collectSoftware(WsmanCredentials wsmanCredentials) throws Exception {
    	logger.info("Collecting Software Inventory {} ", wsmanCredentials.getAddress());
    	Map<String, Object> results = new LinkedHashMap<>();
        IWSManClient IWSManClient = WSManClientFactory.getClient(wsmanCredentials.getAddress(), wsmanCredentials.getUserName(), wsmanCredentials.getPassword());
        results.put(WSManClassEnum.DCIM_SoftwareIdentity.name(), IWSManClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_SoftwareIdentity)));
        results.put(WSManClassEnum.CIM_InstalledSoftwareIdentity.name(), IWSManClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.CIM_InstalledSoftwareIdentity)));
        return results;
    }
   
    @Override
    public Object collectBios(WsmanCredentials wsmanCredentials) throws Exception {
    	logger.info("Collecting Bios {} ", wsmanCredentials.getAddress());
    	Map<String, Object> results = new LinkedHashMap<>();
        IWSManClient IWSManClient = WSManClientFactory.getClient(wsmanCredentials.getAddress(), wsmanCredentials.getUserName(), wsmanCredentials.getPassword());
        results.put(WSManClassEnum.DCIM_BIOSEnumeration.name(), IWSManClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_BIOSEnumeration)));
        results.put(WSManClassEnum.DCIM_BIOSString.name(), IWSManClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_BIOSString)));
        results.put(WSManClassEnum.DCIM_BIOSInteger.name(), IWSManClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_BIOSInteger)));
        return results;
    }

    @Override
    public Object collectBoot(WsmanCredentials wsmanCredentials) throws Exception {
    	logger.info("Collecting Boot {} ", wsmanCredentials.getAddress());
    	Map<String, Object> results = new LinkedHashMap<>();
        IWSManClient IWSManClient = WSManClientFactory.getClient(wsmanCredentials.getAddress(), wsmanCredentials.getUserName(), wsmanCredentials.getPassword());
        results.put(WSManClassEnum.DCIM_BootConfigSetting.name(), IWSManClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_BootConfigSetting)));
        results.put(WSManClassEnum.DCIM_BootSourceSetting.name(), IWSManClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_BootSourceSetting)));
        results.put(WSManClassEnum.DCIM_BIOSEnumeration.name(), IWSManClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_BIOSEnumeration)));
        return results;
    }
    
	@Override
    public Object collectIdracDetails(WsmanCredentials creds) throws Exception {
    	logger.info("Collecting IDRAC details.");
        Map<String, Object> results = new LinkedHashMap<>();
        IWSManClient IWSManClient = WSManClientFactory.getClient(creds.getAddress(), creds.getUserName(), creds.getPassword());
        results.put(WSManClassEnum.DCIM_IDRACCardView.name(), IWSManClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_IDRACCardView)));
        results.put(WSManClassEnum.DCIM_iDRACCardEnumeration.name(), IWSManClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_iDRACCardEnumeration)));
        results.put(WSManClassEnum.DCIM_iDRACCardString.name(), IWSManClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_iDRACCardString)));
        return results;
    }
    
    
    @Override
    public Object collectIdracCardEnum(WsmanCredentials wsmanCredentials) throws Exception{
        logger.debug("Collecting IdracCardEnum {} ", wsmanCredentials.getAddress());
        IWSManClient IWSManClient = WSManClientFactory.getClient(wsmanCredentials.getAddress(), wsmanCredentials.getUserName(), wsmanCredentials.getPassword());
        Object response = IWSManClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_IDRACCardView));
        return response;
    }

    @Override
    public Object collectIdracString(WsmanCredentials wsmanCredentials)  throws Exception{
    	 logger.debug("Collecting IdracCardString {} ", wsmanCredentials.getAddress());
         IWSManClient IWSManClient = WSManClientFactory.getClient(wsmanCredentials.getAddress(), wsmanCredentials.getUserName(), wsmanCredentials.getPassword());
         Object response = IWSManClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_iDRACCardString));
         return response;

    }
    
}
