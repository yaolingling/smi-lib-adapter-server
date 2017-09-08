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
import com.dell.isg.smi.wsman.command.WSManClassEnum;

@Component("inventoryAdapter, inventoryAdapterImpl")
public class InventoryAdapterImpl implements IInventoryAdapter {

	private static final Logger logger = LoggerFactory.getLogger(InventoryAdapterImpl.class.getName());

    @Override
    public Object collectHardwareInventory(WsmanCredentials credentials) throws Exception {
        logger.info("collecting hardware inventory for {} ", credentials.getAddress());
        Map<String, Object> results = new LinkedHashMap<>();

        IWSManClient wsmanClient = null;
        try{
            wsmanClient = WSManClientFactory.getClient(credentials.getAddress(), credentials.getUserName(), credentials.getPassword());
            results.put(WSManClassEnum.DCIM_SystemView.name(), wsmanClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_SystemView)));
            results.put(WSManClassEnum.DCIM_Memoryview.name(), wsmanClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_Memoryview)));
            results.put(WSManClassEnum.DCIM_Powersupplyview.name(), wsmanClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_Powersupplyview)));
            results.put(WSManClassEnum.DCIM_CPUView.name(), wsmanClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_CPUView)));
            results.put(WSManClassEnum.DCIM_VFlashView.name(), wsmanClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_VFlashView)));
            results.put(WSManClassEnum.DCIM_FanView.name(), wsmanClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_FanView)));
            results.put(WSManClassEnum.DCIM_EnclosureView.name(), wsmanClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_EnclosureView)));
            results.put(WSManClassEnum.DCIM_VirtualDiskView.name(), wsmanClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_VirtualDiskView)));
            results.put(WSManClassEnum.DCIM_Sensor.name(), wsmanClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_Sensor)));
            results.put(WSManClassEnum.DCIM_NumericSensor.name(), wsmanClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_NumericSensor)));
            results.put(WSManClassEnum.DCIM_SystemString.name(), wsmanClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_SystemString)));
            results.put(WSManClassEnum.DCIM_ControllerView.name(), wsmanClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_ControllerView)));
            results.put(WSManClassEnum.DCIM_ControllerBatteryView.name(), wsmanClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_ControllerBatteryView)));
            results.put(WSManClassEnum.DCIM_RAIDEnumeration.name(), wsmanClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_RAIDEnumeration)));
            results.put(WSManClassEnum.DCIM_RAIDInteger.name(), wsmanClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_RAIDInteger))); 
        } finally{
            wsmanClient.close();
        }
        return results;
    }

	@Override
    public Object collectSummary(WsmanCredentials credentials) throws Exception {
        logger.info("collecting System Identity Info for {} ", credentials.getAddress());
        Object response = null;
        IWSManClient wsmanClient = null;
        try {
            wsmanClient = WSManClientFactory.getClient(credentials.getAddress(), credentials.getUserName(), credentials.getPassword());
            response = wsmanClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_SystemView));
        } finally {
            wsmanClient.close();
        }
        return response;
    }


	@Override
    public Object collectNics(WsmanCredentials credentials) throws Exception {
        logger.info("Collecting NIC info.");
        Map<String, Object> results = new LinkedHashMap<>();
        IWSManClient wsmanClient = null;
        try{
            wsmanClient = WSManClientFactory.getClient(credentials.getAddress(), credentials.getUserName(), credentials.getPassword());
            results.put(WSManClassEnum.DCIM_NICView.name(), wsmanClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_NICView)));
            results.put(WSManClassEnum.DCIM_NICEnumeration.name(), wsmanClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_NICEnumeration)));
            results.put(WSManClassEnum.DCIM_NICString.name(), wsmanClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_NICString)));
        } finally {
           wsmanClient.close(); 
        }
        return results;
    }
    
    @Override
    public Object collectSelLogs(WsmanCredentials wsmanCredentials) throws Exception {
    	logger.info("collecting Sel log for {} ", wsmanCredentials.getAddress());
    	Object response = null;
        IWSManClient wsmanClient = null;
        try{
            wsmanClient = WSManClientFactory.getClient(wsmanCredentials.getAddress(), wsmanCredentials.getUserName(), wsmanCredentials.getPassword());
            response = wsmanClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_Sellogentry));
        } finally {
            wsmanClient.close(); 
        }
        return response;
    }

    @Override
    public Object collectLcLogs(WsmanCredentials wsmanCredentials) throws Exception {
        Object response = null;
        IWSManClient wsmanClient = null;
        try{
            wsmanClient = WSManClientFactory.getClient(wsmanCredentials.getAddress(), wsmanCredentials.getUserName(), wsmanCredentials.getPassword());
            response = wsmanClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_LCLogEntry));
        } finally {
            wsmanClient.close(); 
        }
        return response;
    }

    @Override
    public Object collectSoftware(WsmanCredentials wsmanCredentials) throws Exception {
    	logger.info("Collecting Software Inventory {} ", wsmanCredentials.getAddress());
    	Map<String, Object> results = new LinkedHashMap<>();
        IWSManClient wsmanClient = null;
        try{
            wsmanClient = WSManClientFactory.getClient(wsmanCredentials.getAddress(), wsmanCredentials.getUserName(), wsmanCredentials.getPassword());
            results.put(WSManClassEnum.DCIM_SoftwareIdentity.name(), wsmanClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_SoftwareIdentity)));
            results.put(WSManClassEnum.CIM_InstalledSoftwareIdentity.name(), wsmanClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.CIM_InstalledSoftwareIdentity)));
        } finally {
            wsmanClient.close(); 
        }
        return results;
    }
   
    @Override
    public Object collectBios(WsmanCredentials wsmanCredentials) throws Exception {
    	logger.info("Collecting Bios {} ", wsmanCredentials.getAddress());
    	Map<String, Object> results = new LinkedHashMap<>();
        IWSManClient wsmanClient = null;
        try{
            wsmanClient = WSManClientFactory.getClient(wsmanCredentials.getAddress(), wsmanCredentials.getUserName(), wsmanCredentials.getPassword());
            results.put(WSManClassEnum.DCIM_BIOSEnumeration.name(), wsmanClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_BIOSEnumeration)));
            results.put(WSManClassEnum.DCIM_BIOSString.name(), wsmanClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_BIOSString)));
            results.put(WSManClassEnum.DCIM_BIOSInteger.name(), wsmanClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_BIOSInteger)));
        } finally {
            wsmanClient.close(); 
        }
        return results;
    }

    @Override
    public Object collectBoot(WsmanCredentials wsmanCredentials) throws Exception {
    	logger.info("Collecting Boot {} ", wsmanCredentials.getAddress());
    	Map<String, Object> results = new LinkedHashMap<>();
        IWSManClient wsmanClient = null;
        try{
            wsmanClient = WSManClientFactory.getClient(wsmanCredentials.getAddress(), wsmanCredentials.getUserName(), wsmanCredentials.getPassword());
            results.put(WSManClassEnum.DCIM_BootConfigSetting.name(), wsmanClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_BootConfigSetting)));
            results.put(WSManClassEnum.DCIM_BootSourceSetting.name(), wsmanClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_BootSourceSetting)));
            results.put(WSManClassEnum.DCIM_BIOSEnumeration.name(), wsmanClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_BIOSEnumeration)));
        } finally {
            wsmanClient.close(); 
        }
        return results;
    }
    
	@Override
    public Object collectIdracDetails(WsmanCredentials creds) throws Exception {
    	logger.info("Collecting IDRAC details.");
        Map<String, Object> results = new LinkedHashMap<>();
        IWSManClient wsmanClient = null;
        try{
            wsmanClient = WSManClientFactory.getClient(creds.getAddress(), creds.getUserName(), creds.getPassword());
            results.put(WSManClassEnum.DCIM_IDRACCardView.name(), wsmanClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_IDRACCardView)));
            results.put(WSManClassEnum.DCIM_iDRACCardEnumeration.name(), wsmanClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_iDRACCardEnumeration)));
            results.put(WSManClassEnum.DCIM_iDRACCardString.name(), wsmanClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_iDRACCardString)));
        } finally {
            wsmanClient.close(); 
        }
        return results;
    }
    
    
    @Override
    public Object collectIdracCardEnum(WsmanCredentials wsmanCredentials) throws Exception{
        logger.debug("Collecting IdracCardEnum {} ", wsmanCredentials.getAddress());
        Object response = null;
        IWSManClient wsmanClient = null;
        try{
            wsmanClient = WSManClientFactory.getClient(wsmanCredentials.getAddress(), wsmanCredentials.getUserName(), wsmanCredentials.getPassword());
            response = wsmanClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_IDRACCardView));
        } finally {
            wsmanClient.close(); 
        }
        return response;
    }

    @Override
    public Object collectIdracString(WsmanCredentials wsmanCredentials)  throws Exception{
    	 logger.debug("Collecting IdracCardString {} ", wsmanCredentials.getAddress());
         Object response = null;
         IWSManClient wsmanClient = null;
         try{
             wsmanClient = WSManClientFactory.getClient(wsmanCredentials.getAddress(), wsmanCredentials.getUserName(), wsmanCredentials.getPassword());
             response = wsmanClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_iDRACCardString));
         } finally {
             wsmanClient.close(); 
         }
         return response;
    }
    
    @Override
    public Object collect(WsmanCredentials wsmanCredentials, String dcimType)  throws Exception{
        logger.debug("Collecting: {} - {} ", dcimType, wsmanCredentials.getAddress());
        Object response = null;
        IWSManClient wsmanClient = null;
        try {
            wsmanClient = WSManClientFactory.getClient(wsmanCredentials.getAddress(), wsmanCredentials.getUserName(), wsmanCredentials.getPassword());
            response = wsmanClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.valueOf(dcimType)));
        } catch(Exception e){
            //String error = "{ \"Illegal type.  Valid types are\": ";
            return WSManClassEnum.values();
        }
        finally{
            wsmanClient.close();
        }
        return response;
   }
   
}
