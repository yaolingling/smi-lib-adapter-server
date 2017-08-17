/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.adapter.server.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dell.isg.smi.adapter.server.inventory.IInventoryAdapter;
import com.dell.isg.smi.adapter.server.model.NetworkShare;
import com.dell.isg.smi.adapter.server.model.WsmanCredentials;
import com.dell.isg.smi.commons.elm.exception.RuntimeCoreException;
import com.dell.isg.smi.wsman.command.ApplyXmlConfigCmd;
import com.dell.isg.smi.wsman.command.BackupImageCmd;
import com.dell.isg.smi.wsman.command.ChangeBootOrderCmd;
import com.dell.isg.smi.wsman.command.ExportFactorySettingConfigCmd;
import com.dell.isg.smi.wsman.command.ExportHardwareInventoryCmd;
import com.dell.isg.smi.wsman.command.ExportTechSupportReportCmd;
import com.dell.isg.smi.wsman.command.ExportXmlConfigCmd;
import com.dell.isg.smi.wsman.command.GetConfigResultsCmd;
import com.dell.isg.smi.wsman.command.GetDeviceLicensesCmd;
import com.dell.isg.smi.wsman.command.InvokeBlinkLedCmd;
import com.dell.isg.smi.wsman.command.LifeControllerWipeCmd;
import com.dell.isg.smi.wsman.command.PeviewImportConfigCmd;
import com.dell.isg.smi.wsman.command.PowerBootCmd;
import com.dell.isg.smi.wsman.command.PowerBootCmd.PowerRebootEnum;
import com.dell.isg.smi.wsman.command.ResetIdracCmd;
import com.dell.isg.smi.wsman.command.RestoreImageCmd;
import com.dell.isg.smi.wsman.command.SetEventsCmd;
import com.dell.isg.smi.wsman.command.SystemEraseCmd;
import com.dell.isg.smi.wsman.command.TestNetworkShareCmd;
import com.dell.isg.smi.wsman.command.UpdateBIOSAttributesCmd;
import com.dell.isg.smi.wsman.command.UpdateEventsCmd;
import com.dell.isg.smi.wsman.command.entity.IDRACCardStringView;
import com.dell.isg.smi.wsman.command.idraccmd.GetIdracEnumByInstanceId;
import com.dell.isg.smi.wsman.command.idraccmd.IdracJobStatusCheckCmd;
import com.dell.isg.smi.wsman.command.idraccmd.UpdateIdracAttributeCmd;
import com.dell.isg.smi.wsman.entity.DeviceLicense;
import com.dell.isg.smi.wsman.entity.KeyValuePair;
import com.dell.isg.smi.wsman.model.XmlConfig;
import com.dell.isg.smi.wsmanclient.IWSManClient;
import com.dell.isg.smi.wsmanclient.WSCommandRNDConstant;
import com.dell.isg.smi.wsmanclient.WSManClientFactory;
import com.dell.isg.smi.wsmanclient.model.InvokeCmdResponse;

/**
 * @author prashanth.gowda
 * @param <IDRACCardStringViewList>
 *
 */
@Component("configAdapter, configAdapterImpl")
public class ConfigAdapterImpl implements IConfigAdapter {

	@Autowired
	IInventoryAdapter inventoryAdapterImpl;

	private static final Logger logger = LoggerFactory.getLogger(ConfigAdapterImpl.class.getName());
	private static final String IDRAC_TARGET = "iDRAC.Embedded.1";
	private static final String TROUBLESHOOT_ATTRIBUTE = "IntegratedDatacenter.1#TroubleshootingMode";
	private static final String TROUBLESHOOT_ENABLE = "Enabled";
	private static final String TROUBLESHOOT_DISABLE = "Disabled";

	@Override
	public boolean configureTraps(WsmanCredentials wsmanCredentials, String targetDestination) throws Exception {
		String address = wsmanCredentials.getAddress();
		logger.info("Entering registerAppliance address {} targetDestination {}", address, targetDestination);
		String username = wsmanCredentials.getUserName();
		String password = wsmanCredentials.getPassword();
		boolean result = false;
		SetEventsCmd setEventsCmd = new SetEventsCmd(address, username, password);
		setEventsCmd.setTrapDestinationIP(targetDestination);
		setEventsCmd.setDomainDestination(true);
		try {
			List<KeyValuePair> keyValuePairList = (List<KeyValuePair>) setEventsCmd.execute();
			if (!CollectionUtils.isEmpty(keyValuePairList)) {
				result = CollectionUtils.exists(keyValuePairList, predicateForSuccessStatus("returnStatus"))
						&& CollectionUtils.exists(keyValuePairList, predicateForJobId("jobId"));
				return result;
			}
		} catch (Exception e) {
			logger.info("Unable to register the appliance for the server IP {} ", address, targetDestination);
		} finally {
			logger.info("Exiting registerAppliance address {} targetDestination {}", address, targetDestination);
		}
		return result;
	}

	@Override
	public boolean updateTrapFormat(WsmanCredentials wsmanCredentials, String snmpTrapFormat) throws Exception {
		String address = wsmanCredentials.getAddress();
		logger.info("Entering updateTrapFormat {} ", address, snmpTrapFormat);
		String username = wsmanCredentials.getUserName();
		String password = wsmanCredentials.getPassword();
		boolean result = false;
		IDRACCardStringView idracCardStringView = getIdracCardForUpdate(address, username, password);
		if (idracCardStringView == null) {
			return result;
		}
		idracCardStringView.setCurrentValue(snmpTrapFormat);
		UpdateEventsCmd updateEventsCmd = new UpdateEventsCmd(address, username, password);
		updateEventsCmd.setIdracCardStringView(idracCardStringView);
		try {
			List<KeyValuePair> keyValuePairList = (List<KeyValuePair>) updateEventsCmd.execute();
			if (!CollectionUtils.isEmpty(keyValuePairList)) {
				result = CollectionUtils.exists(keyValuePairList, predicateForSuccessStatus("returnStatus"));
				return result;
			}
		} catch (Exception e) {
			logger.info("Unable to update the trap format for the server IP {} ", address, snmpTrapFormat);
		} finally {
			logger.info("Exiting updateTrapFormat {} ", address, snmpTrapFormat);
		}
		return result;
	}

	private IDRACCardStringView getIdracCardForUpdate(String address, String username, String password) {
		String trapValueString = "#SNMP.1#TrapFormat";
		logger.info("Entering getIdracCardForUpdate {} ", address);
		IDRACCardStringView idracCardStringView = null;
		try {
			List<IDRACCardStringView> idracCardStringViewList = (List<IDRACCardStringView>) inventoryAdapterImpl
					.collectIdracCardEnum(new WsmanCredentials(address, username, password));
			idracCardStringView = CollectionUtils.find(idracCardStringViewList,
					predicateIdracCardStringView(trapValueString));
		} catch (Exception e) {
			logger.info("Unable to get IDRACCardStringView for attribute update  {} ", address);
		} finally {
			logger.info("Exiting getIdracCardForUpdate {} ", address);
		}
		return idracCardStringView;
	}

	// InstanceID = iDRAC.Embedded.1#SNMP.1#TrapFormat
	private Predicate<IDRACCardStringView> predicateIdracCardStringView(String compareValue) {
		return new Predicate<IDRACCardStringView>() {
			@Override
			public boolean evaluate(IDRACCardStringView idracCardStringView) {
				if (idracCardStringView == null) {
					return false;
				}
				return idracCardStringView.getInstanceID().contains(compareValue);
			}
		};
	}

	@Override
	public int manageServerPower(WsmanCredentials wsmanCredentials, String powerState) throws Exception {
		String address = wsmanCredentials.getAddress();
		String username = wsmanCredentials.getUserName();
		logger.info("Entering manageServerPower address {} username {}", address, username);
		PowerBootCmd powerBootCmd = new PowerBootCmd(address, username, wsmanCredentials.getPassword(),
				PowerRebootEnum.valueOf(powerState));
		int result = powerBootCmd.execute().intValue();
		logger.info("Exiting manageServerPower address {} username {}", address, username);
		return result;
	}

	@Override
	public int resetServer(WsmanCredentials wsmanCredentials) throws Exception {
		String address = wsmanCredentials.getAddress();
		String username = wsmanCredentials.getUserName();
		logger.info("Entering resetServer address {} username {}", address, username);
		ResetIdracCmd resetIdracCmd = new ResetIdracCmd(address, username, wsmanCredentials.getPassword(),
				WSCommandRNDConstant.RESET_IDRAC);
		int result = Integer.valueOf(resetIdracCmd.execute().toString());
		logger.info("Exiting resetServer address {} username {}", address, username);
		return result;
	}

	@Override
	public boolean ejectServer(WsmanCredentials wsmanCredentials) {
		boolean result = false;
		String ipAddr = wsmanCredentials.getAddress();
		logger.info("Entering ejectServer {} ", ipAddr);
		UpdateIdracAttributeCmd cmd = new UpdateIdracAttributeCmd(ipAddr, wsmanCredentials.getUserName(),
				wsmanCredentials.getPassword());
		cmd.addTargetValue("iDRAC.Embedded.1");
		cmd.addAttributeKeyValue("IntegratedDatacenter.1#Eject", "Yes");
		try {
			List<KeyValuePair> kvp = (List<KeyValuePair>) cmd.execute();
			if (!CollectionUtils.isEmpty(kvp)) {
				result = CollectionUtils.exists(kvp, predicateForSuccessStatus("returnStatus"))
						&& CollectionUtils.exists(kvp, predicateForJobId("jobId"));
			}
		} catch (Exception e) {
			logger.error("Unable to EJECT server with IP {} ", ipAddr);
			RuntimeCoreException rce = new RuntimeCoreException("Eject failed for server with IP " + ipAddr, e);
			throw rce;
		} finally {
			logger.info("Exiting ejectServer {} ", ipAddr);
		}
		return result;
	}

	@Override
	public boolean startBlinkLed(WsmanCredentials wsmanCredentials, int duration) throws Exception {
	    IWSManClient client = null;
	    InvokeCmdResponse response = null;
	    try{
	        client = WSManClientFactory.getClient(wsmanCredentials.getAddress(), wsmanCredentials.getUserName(), wsmanCredentials.getPassword());
	        response = client.execute(new InvokeBlinkLedCmd(true, duration));
	    } finally{
		    client.close();
		}
		return (response.getReturnValue() == 0);
	}

	@Override
	public boolean stopBlinkLed(WsmanCredentials wsmanCredentials) throws Exception {
        IWSManClient client = null;
        InvokeCmdResponse response = null;
        try{
            client = WSManClientFactory.getClient(wsmanCredentials.getAddress(),wsmanCredentials.getUserName(), wsmanCredentials.getPassword());
            response = client.execute(new InvokeBlinkLedCmd(false));
        } finally{
            client.close();
        }
		return (response.getReturnValue() == 0);
	}

	@Override
	public String getServerTroubleShootMode(WsmanCredentials credentials) throws Exception {
		logger.info("Setting the server in troubleshoot mode ", credentials.getAddress());
		GetIdracEnumByInstanceId idracCmd = new GetIdracEnumByInstanceId(credentials.getAddress(),
				credentials.getUserName(), credentials.getPassword());
		idracCmd.setInstanceId(IDRAC_TARGET + "#" + TROUBLESHOOT_ATTRIBUTE);
		List<IDRACCardStringView> iDracCardViewList = idracCmd.execute();
		return iDracCardViewList.get(0).getCurrentValue();
	}

	@Override
	public String setServerTroubleShootMode(WsmanCredentials credentials) throws Exception {
		logger.info("Setting the server in troubleshoot mode ", credentials.getAddress());
		UpdateIdracAttributeCmd idracCmd = new UpdateIdracAttributeCmd(credentials.getAddress(),
				credentials.getUserName(), credentials.getPassword());
		idracCmd.addTargetValue(IDRAC_TARGET);
		idracCmd.addAttributeKeyValue(TROUBLESHOOT_ATTRIBUTE, TROUBLESHOOT_ENABLE);
		@SuppressWarnings("unchecked")
		List<KeyValuePair> keyValuePairs = (List<KeyValuePair>) idracCmd.execute();
		return keyValuePairs.get(1).getValue();
	}

	@Override
	public String clearServerTroubleShootMode(WsmanCredentials credentials) throws Exception {
		logger.info("Clearing the server out of troubleshoot mode", credentials.getAddress());
		UpdateIdracAttributeCmd idracCmd = new UpdateIdracAttributeCmd(credentials.getAddress(),
				credentials.getUserName(), credentials.getPassword());
		idracCmd.addTargetValue(IDRAC_TARGET);
		idracCmd.addAttributeKeyValue(TROUBLESHOOT_ATTRIBUTE, TROUBLESHOOT_DISABLE);
		@SuppressWarnings("unchecked")
		List<KeyValuePair> keyValuePairs = (List<KeyValuePair>) idracCmd.execute();
		return keyValuePairs.get(1).getValue();
	}

	@Override
	public boolean isSupportedLicenseInstalled(WsmanCredentials wsmanCredentials) throws Exception {

		GetDeviceLicensesCmd license = new GetDeviceLicensesCmd(wsmanCredentials.getAddress(), wsmanCredentials.getUserName(), wsmanCredentials.getPassword(), false);
		List<DeviceLicense> deviceLicenseList = (List<DeviceLicense>) license.execute();
		if (CollectionUtils.isEmpty(deviceLicenseList)) {
			return false;
		}
		DeviceLicense deviceLicense = deviceLicenseList.get(0);
		if (deviceLicense.getLicenseDescription() != null && deviceLicense.getLicensePrimaryStatus() != null) {
			String licenseDescription = deviceLicense.getLicenseDescription().toLowerCase();
			Set<String> items = new HashSet<String>(Arrays.asList(licenseDescription.split("\\s")));
			if (deviceLicense.getLicensePrimaryStatus().equalsIgnoreCase(ServerAdapterConstants.OK)
					&& items.contains(ServerAdapterConstants.ENTERPRISE)
					&& items.contains(ServerAdapterConstants.LICENSE)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String exportTechSupportReport(WsmanCredentials wsmanCredentials, NetworkShare networkShare) throws Exception {
		logger.info("Exporting Technical Support Report started for server  {}", wsmanCredentials.getAddress());
		ExportTechSupportReportCmd cmd = new ExportTechSupportReportCmd(wsmanCredentials.getAddress(),
				wsmanCredentials.getUserName(), wsmanCredentials.getPassword(), networkShare.getShareType().getValue(),
				networkShare.getShareName(), networkShare.getShareAddress(), networkShare.getShareUserName(),
				networkShare.getSharePassword());
		String jobId = cmd.execute();
		if (jobId == null) {
			logger.error("Unable to create job to export TSR into local appliance share for server {} ", wsmanCredentials.getAddress());
		}
		logger.info("Exporting Technical Support Report triggered for target address {}  with jobId {}", wsmanCredentials.getAddress(), jobId);
		return jobId;
	}

	private Predicate<KeyValuePair> predicateForSuccessStatus(String compareValue) {
		return new Predicate<KeyValuePair>() {
			@Override
			public boolean evaluate(KeyValuePair keyValuePair) {
				if (keyValuePair == null) {
					return false;
				}
				String key = keyValuePair.getKey();
				String value = keyValuePair.getValue();

				logger.info("Entering createKeyValuePairPredicate {} ", keyValuePair.getKey(), keyValuePair.getValue());
				return key.equals(compareValue) && (value.equals(WSCommandRNDConstant.SUCCESSFULL_CONFIG_JOB_RETURN)
						|| value.equals(WSCommandRNDConstant.COMPLETED_WITH_NO_ERROR));
			}
		};
	}

	private Predicate<KeyValuePair> predicateForJobId(String compareValue) {
		return new Predicate<KeyValuePair>() {
			@Override
			public boolean evaluate(KeyValuePair keyValuePair) {
				if (keyValuePair == null) {
					return false;
				}
				logger.info("Entering createKeyValuePairPredicate {} ", keyValuePair.getKey(), keyValuePair.getValue());
				return keyValuePair.getKey().equals(compareValue) && !StringUtils.isEmpty(keyValuePair.getValue());
			}
		};
	}

	@Override
	public Object pollJobStatus(WsmanCredentials credentials, String jobId, int sleepTimeInMillis, int retryCount) throws Exception {
		IdracJobStatusCheckCmd cmd = new IdracJobStatusCheckCmd(credentials.getAddress(), credentials.getUserName(), credentials.getPassword(), jobId, sleepTimeInMillis, retryCount);
		return cmd.execute();
	}

	@Override
	public XmlConfig exportServerConfig(WsmanCredentials wsmanCredentials, NetworkShare networkShare, String components, String mode) throws Exception {
		ExportXmlConfigCmd xmlConfig = new ExportXmlConfigCmd(wsmanCredentials.getAddress(),
				wsmanCredentials.getUserName(), wsmanCredentials.getPassword(), networkShare.getShareType().getValue(),
				networkShare.getShareName(), networkShare.getShareAddress(), networkShare.getFileName(),
				networkShare.getShareUserName(), networkShare.getSharePassword(), components, mode);
		XmlConfig config = xmlConfig.execute();
		return config;
	}

	@Override
	public XmlConfig applyServerConfig(WsmanCredentials wsmanCredentials, NetworkShare networkShare, int shutdownType) throws Exception {
		ApplyXmlConfigCmd xmlConfig = new ApplyXmlConfigCmd(wsmanCredentials.getAddress(),
				wsmanCredentials.getUserName(), wsmanCredentials.getPassword(), networkShare.getShareType().getValue(),
				networkShare.getShareName(), networkShare.getShareAddress(), networkShare.getFileName(),
				networkShare.getShareUserName(), networkShare.getSharePassword(), shutdownType);
		XmlConfig config = xmlConfig.execute();
		return config;
	}

	@Override
	public XmlConfig previewImportServerConfig(WsmanCredentials wsmanCredentials, NetworkShare networkShare) throws Exception {
		PeviewImportConfigCmd xmlConfig = new PeviewImportConfigCmd(wsmanCredentials.getAddress(),
				wsmanCredentials.getUserName(), wsmanCredentials.getPassword(), networkShare.getShareType().getValue(),
				networkShare.getShareName(), networkShare.getShareAddress(), networkShare.getFileName(),
				networkShare.getShareUserName(), networkShare.getSharePassword());
		XmlConfig config = xmlConfig.execute();
		return config;
	}

	@Override
	public XmlConfig exportHardwareInventory(WsmanCredentials wsmanCredentials, NetworkShare networkShare) throws Exception {
		ExportHardwareInventoryCmd cmd = new ExportHardwareInventoryCmd(wsmanCredentials.getAddress(),
				wsmanCredentials.getUserName(), wsmanCredentials.getPassword(), networkShare.getShareType().getValue(),
				networkShare.getShareName(), networkShare.getShareAddress(), networkShare.getFileName(),
				networkShare.getShareUserName(), networkShare.getSharePassword());
		XmlConfig config = cmd.execute();
		return config;
	}

	@Override
	public XmlConfig exportFactorySetting(WsmanCredentials wsmanCredentials, NetworkShare networkShare) throws Exception {
		ExportFactorySettingConfigCmd cmd = new ExportFactorySettingConfigCmd(wsmanCredentials.getAddress(),
				wsmanCredentials.getUserName(), wsmanCredentials.getPassword(), networkShare.getShareType().getValue(),
				networkShare.getShareName(), networkShare.getShareAddress(), networkShare.getFileName(),
				networkShare.getShareUserName(), networkShare.getSharePassword());
		XmlConfig config = cmd.execute();
		return config;
	}

	@Override
	public String previewConfigResults(WsmanCredentials wsmanCredentials, String jobId) throws Exception {
		GetConfigResultsCmd cmd = new GetConfigResultsCmd(wsmanCredentials.getAddress(), wsmanCredentials.getUserName(), wsmanCredentials.getPassword(), jobId);
		String json = XML.toJSONObject((String) cmd.execute()).toString();
		return json;
	}

	@Override
	public XmlConfig wipeLifeController(WsmanCredentials wsmanCredentials) throws Exception {
		LifeControllerWipeCmd cmd = new LifeControllerWipeCmd(wsmanCredentials.getAddress(), wsmanCredentials.getUserName(), wsmanCredentials.getPassword());
		XmlConfig config = cmd.execute();
		return config;
	}

	@Override
	public XmlConfig performSystemErase(WsmanCredentials wsmanCredentials, String[] components) throws Exception {
		SystemEraseCmd cmd = new SystemEraseCmd(wsmanCredentials.getAddress(), wsmanCredentials.getUserName(), wsmanCredentials.getPassword(), components);
		XmlConfig config = cmd.execute();
		return config;
	}

	@Override
	public XmlConfig backupServerImage(WsmanCredentials wsmanCredentials, NetworkShare networkShare, String passphrase, String imageName, String workgroup, String scheduleStartTime, String untilTime) throws Exception {
		BackupImageCmd cmd = new BackupImageCmd(wsmanCredentials.getAddress(), wsmanCredentials.getUserName(),
				wsmanCredentials.getPassword(), networkShare.getShareType().getValue(), networkShare.getShareName(),
				networkShare.getShareAddress(), networkShare.getShareUserName(), networkShare.getSharePassword(),
				passphrase, imageName, workgroup, scheduleStartTime, untilTime);
		XmlConfig config = cmd.execute();
		return config;
	}

	@Override
	public XmlConfig restoreServerImage(WsmanCredentials wsmanCredentials, NetworkShare networkShare, String passphrase,
			String imageName, String workgroup, String scheduleStartTime, String untilTime, String preserveVDConfig) throws Exception {
		RestoreImageCmd cmd = new RestoreImageCmd(wsmanCredentials.getAddress(), wsmanCredentials.getUserName(),
				wsmanCredentials.getPassword(), networkShare.getShareType().getValue(), networkShare.getShareName(),
				networkShare.getShareAddress(), networkShare.getShareUserName(), networkShare.getSharePassword(),
				passphrase, imageName, workgroup, scheduleStartTime, untilTime, preserveVDConfig);
		XmlConfig config = cmd.execute();
		return config;
	}

	@Override
	public String verifyServerNetworkShareConnectivity(WsmanCredentials wsmanCredentials, NetworkShare networkShare) throws Exception {
		TestNetworkShareCmd cmd = new TestNetworkShareCmd(wsmanCredentials.getAddress(), wsmanCredentials.getUserName(),
				wsmanCredentials.getPassword(), networkShare.getShareType().getValue(), networkShare.getShareName(),
				networkShare.getShareAddress(), networkShare.getFileName(), networkShare.getShareUserName(),
				networkShare.getSharePassword());
		String config = cmd.execute();
		return config;
	}

	@Override
	public String updateBiosAttributes(WsmanCredentials wsmanCredentials, Map<String, String> attributes, boolean isCreateConfigJob) throws Exception {
		UpdateBIOSAttributesCmd cmd = new UpdateBIOSAttributesCmd(wsmanCredentials.getAddress(), wsmanCredentials.getUserName(), wsmanCredentials.getPassword(), attributes, isCreateConfigJob);
		String result = cmd.execute();
		return result;
	}

	@Override
	public String changeBootOrder(WsmanCredentials wsmanCredentials, String instanceType, List<String> instanceIdList) throws Exception {
		ChangeBootOrderCmd cmd = new ChangeBootOrderCmd(wsmanCredentials.getAddress(), wsmanCredentials.getUserName(), wsmanCredentials.getPassword(),instanceType,instanceIdList);
		String result = cmd.execute();
		return result;
	}

}