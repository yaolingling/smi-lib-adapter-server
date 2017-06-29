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
import com.dell.isg.smi.wsman.IdracWSManClient;
import com.dell.isg.smi.wsman.WSManClientBaseCommand.WSManClassEnum;
import com.dell.isg.smi.wsman.WSManClientFactory;
import com.dell.isg.smi.wsman.command.WsmanEnumerate;

/**
 * @author rahman.muhammad
 *
 */
@Component("inventoryAdapter, inventoryAdapterImpl")
public class InventoryAdapterImpl implements IInventoryAdapter {

	private static final Logger logger = LoggerFactory.getLogger(InventoryAdapterImpl.class.getName());

	@Override
    public Object collectHardwareInventory(WsmanCredentials credentials) throws Exception {
        logger.info("collecting hardware inventory for {} ", credentials.getAddress());
        Map<String, Object> results = new LinkedHashMap<>();
        IdracWSManClient idracWsManClient = WSManClientFactory.getIdracWSManClient(credentials.getAddress(), credentials.getUserName(), credentials.getPassword());
        results.put(WSManClassEnum.DCIM_SystemView.name(), idracWsManClient.execute(new WsmanEnumerate(WSManClassEnum.DCIM_SystemView.name())));
        results.put(WSManClassEnum.DCIM_Memoryview.name(), idracWsManClient.execute(new WsmanEnumerate(WSManClassEnum.DCIM_Memoryview.name())));
        results.put(WSManClassEnum.DCIM_Powersupplyview.name(), idracWsManClient.execute(new WsmanEnumerate(WSManClassEnum.DCIM_Powersupplyview.name())));
        results.put(WSManClassEnum.DCIM_CPUView.name(), idracWsManClient.execute(new WsmanEnumerate(WSManClassEnum.DCIM_CPUView.name())));
        results.put(WSManClassEnum.DCIM_VFlashView.name(), idracWsManClient.execute(new WsmanEnumerate(WSManClassEnum.DCIM_VFlashView.name())));
        results.put(WSManClassEnum.DCIM_FanView.name(), idracWsManClient.execute(new WsmanEnumerate(WSManClassEnum.DCIM_FanView.name())));
        results.put(WSManClassEnum.DCIM_EnclosureView.name(), idracWsManClient.execute(new WsmanEnumerate(WSManClassEnum.DCIM_EnclosureView.name())));
        results.put(WSManClassEnum.DCIM_VirtualDiskView.name(), idracWsManClient.execute(new WsmanEnumerate(WSManClassEnum.DCIM_VirtualDiskView.name())));
        results.put(WSManClassEnum.DCIM_Sensor.name(), idracWsManClient.execute(new WsmanEnumerate(WSManClassEnum.DCIM_Sensor.name())));
        results.put(WSManClassEnum.DCIM_NumericSensor.name(), idracWsManClient.execute(new WsmanEnumerate(WSManClassEnum.DCIM_NumericSensor.name())));
        results.put(WSManClassEnum.DCIM_SystemString.name(), idracWsManClient.execute(new WsmanEnumerate(WSManClassEnum.DCIM_SystemString.name())));
        results.put(WSManClassEnum.DCIM_ControllerView.name(), idracWsManClient.execute(new WsmanEnumerate(WSManClassEnum.DCIM_ControllerView.name())));
        results.put(WSManClassEnum.DCIM_ControllerBatteryView.name(), idracWsManClient.execute(new WsmanEnumerate(WSManClassEnum.DCIM_ControllerBatteryView.name())));
        results.put(WSManClassEnum.DCIM_RAIDEnumeration.name(), idracWsManClient.execute(new WsmanEnumerate(WSManClassEnum.DCIM_RAIDEnumeration.name())));
        results.put(WSManClassEnum.DCIM_RAIDInteger.name(), idracWsManClient.execute(new WsmanEnumerate(WSManClassEnum.DCIM_RAIDInteger.name()))); 
        return results;

    }

	@Override
    public Object collectSummary(WsmanCredentials credentials) throws Exception {
        logger.info("collecting System Identity Info for {} ", credentials.getAddress());
        IdracWSManClient idracWsManClient = WSManClientFactory.getIdracWSManClient(credentials.getAddress(), credentials.getUserName(), credentials.getPassword());
        Object response = idracWsManClient.execute(new WsmanEnumerate(WSManClassEnum.DCIM_SystemView.name()));
        return response;
    }


	@Override
    public Object collectNics(WsmanCredentials credentials) throws Exception {
        logger.info("Collecting NIC info.");
        Map<String, Object> results = new LinkedHashMap<>();
        IdracWSManClient idracWsManClient = WSManClientFactory.getIdracWSManClient(credentials.getAddress(), credentials.getUserName(), credentials.getPassword());
        results.put(WSManClassEnum.DCIM_NICView.name(), idracWsManClient.execute(new WsmanEnumerate(WSManClassEnum.DCIM_NICView.name())));
        results.put(WSManClassEnum.DCIM_NICEnumeration.name(), idracWsManClient.execute(new WsmanEnumerate(WSManClassEnum.DCIM_NICEnumeration.name())));
        results.put(WSManClassEnum.DCIM_NICString.name(), idracWsManClient.execute(new WsmanEnumerate(WSManClassEnum.DCIM_NICString.name())));
        return results;
    }
    
    @Override
    public Object collectSelLogs(WsmanCredentials wsmanCredentials) throws Exception {
    	logger.info("collecting Sel log for {} ", wsmanCredentials.getAddress());
        IdracWSManClient idracWsManClient = WSManClientFactory.getIdracWSManClient(wsmanCredentials.getAddress(), wsmanCredentials.getUserName(), wsmanCredentials.getPassword());
        Object response = idracWsManClient.execute(new WsmanEnumerate(WSManClassEnum.DCIM_Sellogentry.name()));
        return response;
    }

    @Override
    public Object collectLcLogs(WsmanCredentials wsmanCredentials) throws Exception {
    	IdracWSManClient idracWsManClient = WSManClientFactory.getIdracWSManClient(wsmanCredentials.getAddress(), wsmanCredentials.getUserName(), wsmanCredentials.getPassword());
        Object response = idracWsManClient.execute(new WsmanEnumerate(WSManClassEnum.DCIM_LCLogEntry.name()));
        return response;
    }

    @Override
    public Object collectSoftware(WsmanCredentials wsmanCredentials) throws Exception {
    	logger.info("Collecting Software Inventory {} ", wsmanCredentials.getAddress());
    	Map<String, Object> results = new LinkedHashMap<>();
        IdracWSManClient idracWsManClient = WSManClientFactory.getIdracWSManClient(wsmanCredentials.getAddress(), wsmanCredentials.getUserName(), wsmanCredentials.getPassword());
        results.put(WSManClassEnum.DCIM_SoftwareIdentity.name(), idracWsManClient.execute(new WsmanEnumerate(WSManClassEnum.DCIM_SoftwareIdentity.name())));
        results.put(WSManClassEnum.CIM_InstalledSoftwareIdentity.name(), idracWsManClient.execute(new WsmanEnumerate(WSManClassEnum.CIM_InstalledSoftwareIdentity.name())));
        return results;
    }
   
    @Override
    public Object collectBios(WsmanCredentials wsmanCredentials) throws Exception {
    	logger.info("Collecting Bios {} ", wsmanCredentials.getAddress());
    	Map<String, Object> results = new LinkedHashMap<>();
        IdracWSManClient idracWsManClient = WSManClientFactory.getIdracWSManClient(wsmanCredentials.getAddress(), wsmanCredentials.getUserName(), wsmanCredentials.getPassword());
        results.put(WSManClassEnum.DCIM_BIOSEnumeration.name(), idracWsManClient.execute(new WsmanEnumerate(WSManClassEnum.DCIM_BIOSEnumeration.name())));
        results.put(WSManClassEnum.DCIM_BIOSString.name(), idracWsManClient.execute(new WsmanEnumerate(WSManClassEnum.DCIM_BIOSString.name())));
        results.put(WSManClassEnum.DCIM_BIOSInteger.name(), idracWsManClient.execute(new WsmanEnumerate(WSManClassEnum.DCIM_BIOSInteger.name())));
        return results;
    }

    @Override
    public Object collectBoot(WsmanCredentials wsmanCredentials) throws Exception {
    	logger.info("Collecting Boot {} ", wsmanCredentials.getAddress());
    	Map<String, Object> results = new LinkedHashMap<>();
        IdracWSManClient idracWsManClient = WSManClientFactory.getIdracWSManClient(wsmanCredentials.getAddress(), wsmanCredentials.getUserName(), wsmanCredentials.getPassword());
        results.put(WSManClassEnum.DCIM_BootConfigSetting.name(), idracWsManClient.execute(new WsmanEnumerate(WSManClassEnum.DCIM_BootConfigSetting.name())));
        results.put(WSManClassEnum.DCIM_BootSourceSetting.name(), idracWsManClient.execute(new WsmanEnumerate(WSManClassEnum.DCIM_BootSourceSetting.name())));
        results.put(WSManClassEnum.DCIM_BIOSEnumeration.name(), idracWsManClient.execute(new WsmanEnumerate(WSManClassEnum.DCIM_BIOSEnumeration.name())));
        return results;
    }
    
	@Override
    public Object collectIdracDetails(WsmanCredentials creds) throws Exception {
    	logger.info("Collecting IDRAC details.");
        Map<String, Object> results = new LinkedHashMap<>();
        IdracWSManClient idracWsManClient = WSManClientFactory.getIdracWSManClient(creds.getAddress(), creds.getUserName(), creds.getPassword());
        results.put(WSManClassEnum.DCIM_IDRACCardView.name(), idracWsManClient.execute(new WsmanEnumerate(WSManClassEnum.DCIM_IDRACCardView.name())));
        results.put(WSManClassEnum.DCIM_iDRACCardEnumeration.name(), idracWsManClient.execute(new WsmanEnumerate(WSManClassEnum.DCIM_iDRACCardEnumeration.name())));
        results.put(WSManClassEnum.DCIM_iDRACCardString.name(), idracWsManClient.execute(new WsmanEnumerate(WSManClassEnum.DCIM_iDRACCardString.name())));
        return results;
    }
    
    
    @Override
    public Object collectIdracCardEnum(WsmanCredentials wsmanCredentials) throws Exception{
        logger.debug("Collecting IdracCardEnum {} ", wsmanCredentials.getAddress());
        IdracWSManClient idracWsManClient = WSManClientFactory.getIdracWSManClient(wsmanCredentials.getAddress(), wsmanCredentials.getUserName(), wsmanCredentials.getPassword());
        Object response = idracWsManClient.execute(new WsmanEnumerate(WSManClassEnum.DCIM_IDRACCardView.name()));
        return response;
    }

    @Override
    public Object collectIdracString(WsmanCredentials wsmanCredentials)  throws Exception{
    	 logger.debug("Collecting IdracCardString {} ", wsmanCredentials.getAddress());
         IdracWSManClient idracWsManClient = WSManClientFactory.getIdracWSManClient(wsmanCredentials.getAddress(), wsmanCredentials.getUserName(), wsmanCredentials.getPassword());
         Object response = idracWsManClient.execute(new WsmanEnumerate(WSManClassEnum.DCIM_iDRACCardString.name()));
         return response;

    }
    
}
