/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.adapter.server.delegate;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.dell.isg.smi.adapter.server.ServerAdapterConstants;
import com.dell.isg.smi.adapter.server.model.HardwareInventory;
import com.dell.isg.smi.adapter.server.model.NetworkShare;
import com.dell.isg.smi.adapter.server.model.PowerMonitoring;
import com.dell.isg.smi.adapter.server.model.PowerMonitoringConstants;
import com.dell.isg.smi.adapter.server.model.Storage;
import com.dell.isg.smi.adapter.server.model.WsmanCredentials;
import com.dell.isg.smi.adapter.server.powerthermal.IPowerThermalAdapter;
import com.dell.isg.smi.adapter.server.powerthermal.PowerThermalAdapterImpl;
import com.dell.isg.smi.wsman.IdracWSManClient;
import com.dell.isg.smi.wsman.WSManBaseCommand.WSManClassEnum;
import com.dell.isg.smi.wsman.WSManClientFactory;
import com.dell.isg.smi.wsman.command.ApplyXmlConfigCmd;
import com.dell.isg.smi.wsman.command.DcimEnumerationCmd;
import com.dell.isg.smi.wsman.command.DcimIntegerCmd;
import com.dell.isg.smi.wsman.command.DcimStringCmd;
import com.dell.isg.smi.wsman.command.EnumerateCPUViewCmd;
import com.dell.isg.smi.wsman.command.EnumerateControllerBatteryViewCmd;
import com.dell.isg.smi.wsman.command.EnumerateControllerView;
import com.dell.isg.smi.wsman.command.EnumerateEnclosureViewcmd;
import com.dell.isg.smi.wsman.command.EnumerateFanViewCmd;
import com.dell.isg.smi.wsman.command.EnumerateMemoryViewCmd;
import com.dell.isg.smi.wsman.command.EnumerateNICStatisticsCmd;
import com.dell.isg.smi.wsman.command.EnumerateNICView;
import com.dell.isg.smi.wsman.command.EnumerateNicCapabilitiesCmd;
import com.dell.isg.smi.wsman.command.EnumerateNumericSensorViewCmd;
import com.dell.isg.smi.wsman.command.EnumeratePcieSsdViewCmd;
import com.dell.isg.smi.wsman.command.EnumeratePhysicalDiskView;
import com.dell.isg.smi.wsman.command.EnumeratePowerSupplyViewCmd;
import com.dell.isg.smi.wsman.command.EnumerateSensorViewCmd;
import com.dell.isg.smi.wsman.command.EnumerateSystemViewCmd;
import com.dell.isg.smi.wsman.command.EnumerateVFlashViewCmd;
import com.dell.isg.smi.wsman.command.EnumerateVirtualDiskView;
import com.dell.isg.smi.wsman.command.ExportTechSupportReportCmd;
import com.dell.isg.smi.wsman.command.ExportXmlConfigCmd;
import com.dell.isg.smi.wsman.command.GetConfigResultsCmd;
import com.dell.isg.smi.wsman.command.GetDeviceLicensesCmd;
import com.dell.isg.smi.wsman.command.NicAttributeConstants;
import com.dell.isg.smi.wsman.command.PeviewImportConfigCmd;
import com.dell.isg.smi.wsman.command.RaidAttributeConstants;
import com.dell.isg.smi.wsman.command.entity.ControllerBatteryView;
import com.dell.isg.smi.wsman.command.entity.ControllerView;
import com.dell.isg.smi.wsman.command.entity.DCIMNICViewType;
import com.dell.isg.smi.wsman.command.entity.DCIMSystemViewType;
import com.dell.isg.smi.wsman.command.entity.DcimEnumCmdView;
import com.dell.isg.smi.wsman.command.entity.DcimIntegerCmdView;
import com.dell.isg.smi.wsman.command.entity.DcimStringCmdView;
import com.dell.isg.smi.wsman.command.entity.FanView;
import com.dell.isg.smi.wsman.command.entity.IDRACCardStringView;
import com.dell.isg.smi.wsman.command.entity.NICStatistics;
import com.dell.isg.smi.wsman.command.entity.NicCapabilities;
import com.dell.isg.smi.wsman.command.entity.NumericSensorView;
import com.dell.isg.smi.wsman.command.entity.PcieSsdView;
import com.dell.isg.smi.wsman.command.entity.PhysicalDiskView;
import com.dell.isg.smi.wsman.command.entity.SensorTypeEnum;
import com.dell.isg.smi.wsman.command.entity.SensorView;
import com.dell.isg.smi.wsman.command.idraccmd.GetIDracEnumAttributes;
import com.dell.isg.smi.wsman.command.idraccmd.GetIdracEnumByInstanceId;
import com.dell.isg.smi.wsman.command.idraccmd.UpdateIdracAttributeCmd;
import com.dell.isg.smi.wsman.entity.DeviceLicense;
import com.dell.isg.smi.wsman.entity.KeyValuePair;
import com.dell.isg.smi.wsman.model.XmlConfig;

/**
 * @author prashanth.gowda
 *
 */
@Component("saOnboardingDelegate")
public class ServerAdapterOnboardingDelegate {

	private static final Logger logger = LoggerFactory.getLogger(ServerAdapterOnboardingDelegate.class.getName());
	private static final String IDRAC_TARGET = "iDRAC.Embedded.1";
	private static final String TROUBLESHOOT_ATTRIBUTE = "IntegratedDatacenter.1#TroubleshootingMode";
	private static final String TROUBLESHOOT_ENABLE = "Enabled";
	private static final String TROUBLESHOOT_DISABLE = "Disabled";
	private static final String SYSTEM_FQDD = "System.Embedded.1";
	private static final int MAX_RETRY = 2;

	public HardwareInventory collectHardwareInventory(WsmanCredentials credentials) throws Exception {
		logger.info("collecting hardware inventory for {} ", credentials.getAddress());
		return this.buildHardwareInventory(credentials);

	}

	public String getServerTroubleShootMode(WsmanCredentials credentials) throws Exception {

		logger.info("Setting the server in troubleshoot mode ", credentials.getAddress());
		GetIdracEnumByInstanceId idracCmd = new GetIdracEnumByInstanceId(credentials.getAddress(),
				credentials.getUserName(), credentials.getPassword());
		idracCmd.setInstanceId(IDRAC_TARGET + "#" + TROUBLESHOOT_ATTRIBUTE);
		@SuppressWarnings("unchecked")
		List<IDRACCardStringView> iDracCardViewList = idracCmd.execute();
		return iDracCardViewList.get(0).getCurrentValue();
	}

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

	public String collectIDRACInfo(WsmanCredentials credentials) throws Exception {

		logger.info("collecting System Identity Info for {} ", credentials.getAddress());
		GetIDracEnumAttributes idracCmd = new GetIDracEnumAttributes(credentials.getAddress(),
				credentials.getUserName(), credentials.getPassword());
		return idracCmd.execute();

	}

	public DCIMSystemViewType collectSystemInfo(WsmanCredentials credentials) throws Exception {
		/*
		 * These retries are introduced for some issues happening on switch and
		 * iDRAC side, will remove when fixed and diagnosed.
		 */
		logger.info("collecting System Identity Info for {} ", credentials.getAddress());
		IdracWSManClient idracWSManClient = WSManClientFactory.getIdracWSManClient(credentials.getAddress(),
				credentials.getUserName(), credentials.getPassword());
		DCIMSystemViewType response = null;
		for (int i = 0; i < MAX_RETRY; i++) {
			try {
				logger.info("==================== " + i + "====================");
				response = idracWSManClient.execute(new EnumerateSystemViewCmd());
				if (response != null)
					return response;
			} catch (Exception exp) {
				logger.info(exp.toString(), exp);
				if (i == MAX_RETRY)
					throw exp;
			}
		}
		return response;

	}

	private HardwareInventory buildHardwareInventory(WsmanCredentials credentials) throws Exception {
		logger.info("Building hardware inventory for {} ", credentials.getAddress());
		HardwareInventory hardwareInventory = new HardwareInventory();
		IdracWSManClient idracWsManClient = WSManClientFactory.getIdracWSManClient(credentials.getAddress(),
				credentials.getUserName(), credentials.getPassword());
		hardwareInventory.setSystem(idracWsManClient.execute(new EnumerateSystemViewCmd()));
		hardwareInventory.setMemoryView(idracWsManClient.execute(new EnumerateMemoryViewCmd()));
		hardwareInventory.setNicView(buildNicView(idracWsManClient));
		hardwareInventory.setPowerSupplyView(idracWsManClient.execute(new EnumeratePowerSupplyViewCmd()));
		hardwareInventory.setFanView(buildFans(idracWsManClient));
		hardwareInventory.setCpuView(idracWsManClient.execute(new EnumerateCPUViewCmd()));
		hardwareInventory.setRemovableMedia(idracWsManClient.execute(new EnumerateVFlashViewCmd()));
		hardwareInventory.setStorage(buildStorage(idracWsManClient));
		hardwareInventory.setTemperatureView(buildTemperatureView(idracWsManClient));
		List<SensorView> sensors = idracWsManClient.execute(new EnumerateSensorViewCmd());
		for (SensorView sensorView : sensors) {
			if (sensorView.isBattery()) {
				hardwareInventory.getBatteryView().add(sensorView);
			} else if (sensorView.isVoltage()) {
				hardwareInventory.getVoltageView().add(sensorView);
			}
		}
		try {
			PowerMonitoring powerMonitoring = buildPowerMonitoring(idracWsManClient);
			powerMonitoring.setPowerCap(
					hardwareInventory.getSystem().getPowerCap().getValue() + " " + PowerMonitoringConstants.WATT);
			hardwareInventory.setPowerMonitoring(powerMonitoring);
		} catch (Exception e) {
			logger.error("Failed to build  for PowerMonitoring {} ", credentials.getAddress());
		}
		logger.info("Building hardware inventory finished for {} ", credentials.getAddress());

		return hardwareInventory;
	}

	/*
	 * Configure target server after auto discovery Apply required
	 * configurations on target server for complete onboarding
	 */

	public String onBoardServer(WsmanCredentials wsmanCredentials, NetworkShare networkShare) throws Exception {

		String jobId = "";

		/*
		 * logger.info("Server onboarding started for server  {}",
		 * wsmanCredentials.getAddress());
		 * 
		 * ApplyXmlConfigCmd cmd = new
		 * ApplyXmlConfigCmd(wsmanCredentials.getAddress(),
		 * wsmanCredentials.getUserName(), wsmanCredentials.getPassword(),
		 * networkShare.getShareType().getValue(), networkShare.getShareName(),
		 * networkShare.getShareAddress(), networkShare.getFileName(),
		 * networkShare.getShareUserName(), networkShare.getSharePassword());
		 * 
		 * String jobId = cmd.execute(); logger.info(
		 * "Server onboarding initiated on target address {}  with jobId {}",
		 * wsmanCredentials.getAddress(), jobId);
		 */
		return jobId;

	}

	@SuppressWarnings("unchecked")
	public boolean isSupportedLicenseInstalled(WsmanCredentials wsmanCredentials) throws Exception {

		GetDeviceLicensesCmd license = new GetDeviceLicensesCmd(wsmanCredentials.getAddress(),
				wsmanCredentials.getUserName(), wsmanCredentials.getPassword(), false);
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

	public String exportTechSupportReport(WsmanCredentials wsmanCredentials, NetworkShare networkShare)
			throws Exception {
		logger.info("Exporting Technical Support Report started for server  {}", wsmanCredentials.getAddress());
		ExportTechSupportReportCmd cmd = new ExportTechSupportReportCmd(wsmanCredentials.getAddress(),
				wsmanCredentials.getUserName(), wsmanCredentials.getPassword(), networkShare.getShareType().getValue(),
				networkShare.getShareName(), networkShare.getShareAddress(), networkShare.getShareUserName(),
				networkShare.getSharePassword());
		String jobId = cmd.execute();
		if (jobId == null) {
			logger.error("Unable to create job to export TSR into local appliance share for server {} ",
					wsmanCredentials.getAddress());
		}
		logger.info("Exporting Technical Support Report triggered for target address {}  with jobId {}",
				wsmanCredentials.getAddress(), jobId);
		return jobId;
	}

	public XmlConfig exportServerConfig(WsmanCredentials wsmanCredentials, NetworkShare networkShare, String components,
			String mode) throws Exception {
		ExportXmlConfigCmd xmlConfig = new ExportXmlConfigCmd(wsmanCredentials.getAddress(),
				wsmanCredentials.getUserName(), wsmanCredentials.getPassword(), networkShare.getShareType().getValue(),
				networkShare.getShareName(), networkShare.getShareAddress(), networkShare.getFileName(),
				networkShare.getShareUserName(), networkShare.getSharePassword(), components, mode);
		XmlConfig config = xmlConfig.execute();
		return config;
	}

	public XmlConfig applyServerConfig(WsmanCredentials wsmanCredentials, NetworkShare networkShare, int shutdownType)
			throws Exception {
		ApplyXmlConfigCmd xmlConfig = new ApplyXmlConfigCmd(wsmanCredentials.getAddress(),
				wsmanCredentials.getUserName(), wsmanCredentials.getPassword(), networkShare.getShareType().getValue(),
				networkShare.getShareName(), networkShare.getShareAddress(), networkShare.getFileName(),
				networkShare.getShareUserName(), networkShare.getSharePassword(), shutdownType);
		XmlConfig config = xmlConfig.execute();
		return config;
	}

	public XmlConfig previewImportServerConfig(WsmanCredentials wsmanCredentials, NetworkShare networkShare)
			throws Exception {
		PeviewImportConfigCmd xmlConfig = new PeviewImportConfigCmd(wsmanCredentials.getAddress(),
				wsmanCredentials.getUserName(), wsmanCredentials.getPassword(), networkShare.getShareType().getValue(),
				networkShare.getShareName(), networkShare.getShareAddress(), networkShare.getFileName(),
				networkShare.getShareUserName(), networkShare.getSharePassword());
		XmlConfig config = xmlConfig.execute();
		return config;
	}

	public Object previewConfigResults(WsmanCredentials wsmanCredentials, String jobId) throws Exception {
		GetConfigResultsCmd result = new GetConfigResultsCmd(wsmanCredentials.getAddress(),
				wsmanCredentials.getUserName(), wsmanCredentials.getPassword(), jobId);
		return result;
	}

	public List<DCIMNICViewType> collectNics(WsmanCredentials credentials) throws Exception {
		logger.info("Collecting NICs info for server onboarding ");
		IdracWSManClient idracWsManClient = WSManClientFactory.getIdracWSManClient(credentials.getAddress(),
				credentials.getUserName(), credentials.getPassword());
		return idracWsManClient.execute(new EnumerateNICView());

	}

	private Storage buildStorage(IdracWSManClient idracWsManClient) throws Exception {
		Storage storage = new Storage();
		storage.setEnclosures(idracWsManClient.execute(new EnumerateEnclosureViewcmd()));
		storage.setControllers(buildControllerProfile(idracWsManClient));
		storage.setVirtualDisks(idracWsManClient.execute(new EnumerateVirtualDiskView()));
		storage.setPhysicalDisks(buildPhysicalDiskViews(idracWsManClient));
		return storage;
	}

	private List<ControllerView> buildControllerProfile(IdracWSManClient idracWsManClient) throws Exception {
		List<ControllerView> controllers = idracWsManClient.execute(new EnumerateControllerView());
		DcimEnumerationCmd raidEnumCmd = new DcimEnumerationCmd(idracWsManClient.getIpAddress(),
				idracWsManClient.getUser(), idracWsManClient.getPassword(), WSManClassEnum.DCIM_RAIDEnumeration.name());
		List<DcimEnumCmdView> raidEnumViews = raidEnumCmd.execute();
		DcimIntegerCmd raidIntegerCmd = new DcimIntegerCmd(idracWsManClient.getIpAddress(), idracWsManClient.getUser(),
				idracWsManClient.getPassword(), WSManClassEnum.DCIM_RAIDInteger.name());
		List<DcimIntegerCmdView> raidIntegerViews = raidIntegerCmd.execute();
		List<ControllerBatteryView> controllerBatteryViews = idracWsManClient
				.execute(new EnumerateControllerBatteryViewCmd());
		for (ControllerView controller : controllers) {
			DcimEnumCmdView raidEnumView = CollectionUtils.find(raidEnumViews, predicateDcimEnumerationView(
					controller.getfQDD() + ":" + RaidAttributeConstants.MAX_CAPABLE_SPEED));
			if (raidEnumView != null) {
				controller.setPossibleSpeed(raidEnumView.getPossibleValues());
			}
			raidEnumView = CollectionUtils.find(raidEnumViews, predicateDcimEnumerationView(
					controller.getfQDD() + ":" + RaidAttributeConstants.BATTERY_LEARN_MODE));
			if (raidEnumView != null) {
				controller.setBatteryStatus(raidEnumView.getCurrentValue());
			}
			raidEnumView = CollectionUtils.find(raidEnumViews,
					predicateDcimEnumerationView(controller.getfQDD() + ":" + RaidAttributeConstants.PATROL_READ_MODE));
			if (raidEnumView != null) {
				controller.setPatrolReadMode(raidEnumView.getCurrentValue());
			}
			raidEnumView = CollectionUtils.find(raidEnumViews, predicateDcimEnumerationView(
					controller.getfQDD() + ":" + RaidAttributeConstants.CHECK_CONSISTENCY_MODE));
			if (raidEnumView != null) {
				controller.setCcMode(raidEnumView.getCurrentValue());
			}
			raidEnumView = CollectionUtils.find(raidEnumViews,
					predicateDcimEnumerationView(controller.getfQDD() + ":" + RaidAttributeConstants.COPY_BACK_MODE));
			if (raidEnumView != null) {
				controller.setCopyBackMode(raidEnumView.getCurrentValue());
			}
			DcimIntegerCmdView raidIntegerView = CollectionUtils.find(raidIntegerViews, predicateDcimIntegerView(
					controller.getfQDD() + ":" + RaidAttributeConstants.CHECK_CONSISTENCY_RATE));
			if (raidIntegerView != null) {
				controller.setCcRate(raidIntegerView.getCurrentValue());
			}
			raidIntegerView = CollectionUtils.find(raidIntegerViews,
					predicateDcimIntegerView(controller.getfQDD() + ":" + RaidAttributeConstants.BGI_RATE));
			if (raidIntegerView != null) {
				controller.setBgiRate(raidIntegerView.getCurrentValue());
			}
			raidIntegerView = CollectionUtils.find(raidIntegerViews,
					predicateDcimIntegerView(controller.getfQDD() + ":" + RaidAttributeConstants.REBUILD_RATE));
			if (raidIntegerView != null) {
				controller.setRebuildRate(raidIntegerView.getCurrentValue());
			}

			ControllerBatteryView controllerBatteryView = CollectionUtils.find(controllerBatteryViews,
					predicateControllerBatteryView(controller.getInstanceID()));
			if (controllerBatteryView != null) {
				controller.setBatteryState(controllerBatteryView.getRaidState());
				controller.setBatteryStatus(controllerBatteryView.getPrimaryStatus());
			}
		}
		return controllers;
	}

	private List<PhysicalDiskView> buildPhysicalDiskViews(IdracWSManClient idracWsManClient) throws Exception {
		List<PhysicalDiskView> physicalDiskViews = idracWsManClient.execute(new EnumeratePhysicalDiskView());
		List<PcieSsdView> pcieSsdViews = idracWsManClient.execute(new EnumeratePcieSsdViewCmd());
		for (PhysicalDiskView physicalDiskView : physicalDiskViews) {
			String fqdd = physicalDiskView.getfQDD();
			PcieSsdView pcieSsdView = CollectionUtils.find(pcieSsdViews, predicatePcieSsdView(fqdd));
			if (pcieSsdView != null) {
				physicalDiskView.setDeviceLifeStatus(pcieSsdView.getDeviceLifeStatus());
			}
		}
		return physicalDiskViews;
	}

	private List<DCIMNICViewType> buildNicView(IdracWSManClient idracWsManClient) throws Exception {
		List<DCIMNICViewType> dcimNicViewList = idracWsManClient.execute(new EnumerateNICView());
		DcimEnumerationCmd nicEnumCmd = new DcimEnumerationCmd(idracWsManClient.getIpAddress(),
				idracWsManClient.getUser(), idracWsManClient.getPassword(), WSManClassEnum.DCIM_NICEnumeration.name());
		List<DcimEnumCmdView> nicEnumViews = nicEnumCmd.execute();
		DcimStringCmd nicStringCmd = new DcimStringCmd(idracWsManClient.getIpAddress(), idracWsManClient.getUser(),
				idracWsManClient.getPassword(), WSManClassEnum.DCIM_NICString.name());
		List<DcimStringCmdView> nicStringViews = nicStringCmd.execute();
		List<NICStatistics> nicStatisticsViews = idracWsManClient.execute(new EnumerateNICStatisticsCmd());
		List<NicCapabilities> nicCapabilitiesViews = idracWsManClient.execute(new EnumerateNicCapabilitiesCmd());
		for (DCIMNICViewType nicView : dcimNicViewList) {
			String fqdd = nicView.getFqdd().getValue();
			DcimEnumCmdView nicEnumView = CollectionUtils.find(nicEnumViews,
					predicateDcimEnumerationView(fqdd + ":" + NicAttributeConstants.LINK_STATUS));
			if (nicEnumView != null) {
				nicView.setLinkStatus(nicEnumView.getCurrentValue());
			}
			nicEnumView = CollectionUtils.find(nicEnumViews,
					predicateDcimEnumerationView(fqdd + ":" + NicAttributeConstants.BOOT_PROTOCOL));
			if (nicEnumView != null) {
				nicView.setLegacyBootProtocol(nicEnumView.getCurrentValue());
			}
			nicEnumView = CollectionUtils.find(nicEnumViews,
					predicateDcimEnumerationView(fqdd + ":" + NicAttributeConstants.ISCSI_BOOT_MODE));
			if (nicEnumView != null) {
				nicView.setIcsciBootMode(nicEnumView.getCurrentValue());
			}
			DcimStringCmdView nicStringView = CollectionUtils.find(nicStringViews,
					predicateDcimStringView(fqdd + ":" + NicAttributeConstants.MAC_ADDRESS));
			if (nicStringView != null) {
				nicView.setMacAddress(nicStringView.getCurrentValue());
			}
			nicStringView = CollectionUtils.find(nicStringViews,
					predicateDcimStringView(fqdd + ":" + NicAttributeConstants.ISCSI_MAC_ADDRESS));
			if (nicStringView != null) {
				nicView.setIscsiMacAddress(nicStringView.getCurrentValue());
			}
			nicStringView = CollectionUtils.find(nicStringViews,
					predicateDcimStringView(fqdd + ":" + NicAttributeConstants.VIRTUAL_MAC_ADDRESS));
			if (nicStringView != null) {
				nicView.setVirtualMacAddress(nicStringView.getCurrentValue());
			}
			nicStringView = CollectionUtils.find(nicStringViews,
					predicateDcimStringView(fqdd + ":" + NicAttributeConstants.VIRTUAL_ISCSI_MAC_ADDRESS));
			if (nicStringView != null) {
				nicView.setVirtualIscsiMacAddress(nicStringView.getCurrentValue());
			}
			nicStringView = CollectionUtils.find(nicStringViews,
					predicateDcimStringView(fqdd + ":" + NicAttributeConstants.ISCSI_OFFLOAD_SUPPORT));
			if (nicStringView != null) {
				nicView.setIscsiOffloadSupport(nicStringView.getCurrentValue());
			}
			nicStringView = CollectionUtils.find(nicStringViews,
					predicateDcimStringView(fqdd + ":" + NicAttributeConstants.TOE_SUPPORT));
			if (nicStringView != null) {
				nicView.setToeSupport(nicStringView.getCurrentValue());
			}
			nicStringView = CollectionUtils.find(nicStringViews,
					predicateDcimStringView(fqdd + ":" + NicAttributeConstants.ISCSI_INITIATOR_IP_ADDRESS));
			if (nicStringView != null) {
				nicView.setIscsiInitiatorIpAddress(nicStringView.getCurrentValue());
			}
			nicStringView = CollectionUtils.find(nicStringViews,
					predicateDcimStringView(fqdd + ":" + NicAttributeConstants.ISCSI_INITIATOR_IP_SUBNET));
			if (nicStringView != null) {
				nicView.setIscsiInitiatorSubnet(nicStringView.getCurrentValue());
			}
			nicStringView = CollectionUtils.find(nicStringViews,
					predicateDcimStringView(fqdd + ":" + NicAttributeConstants.ISCSI_INITIATOR_IP_GATEWAY));
			if (nicStringView != null) {
				nicView.setIscsiInitiatorGateway(nicStringView.getCurrentValue());
			}
			nicStringView = CollectionUtils.find(nicStringViews,
					predicateDcimStringView(fqdd + ":" + NicAttributeConstants.ISCSI_INITIATOR_IP_PRIMARY_DNS));
			if (nicStringView != null) {
				nicView.setIscsiInitiatorPrimaryDns(nicStringView.getCurrentValue());
			}
			nicStringView = CollectionUtils.find(nicStringViews,
					predicateDcimStringView(fqdd + ":" + NicAttributeConstants.ISCSI_INITIATOR_IP_SECONDARY_DNS));
			if (nicStringView != null) {
				nicView.setIscsiInitiatorSecondryDns(nicStringView.getCurrentValue());
			}
			nicStringView = CollectionUtils.find(nicStringViews,
					predicateDcimStringView(fqdd + ":" + NicAttributeConstants.ISCSI_INITIATOR_NAME));
			if (nicStringView != null) {
				nicView.setIscsiInitiatorName(nicStringView.getCurrentValue());
			}

			NICStatistics nicStatistics = CollectionUtils.find(nicStatisticsViews, predicateNicStatistics(fqdd));
			if (nicStatistics != null) {
				nicView.setOsDriverState(nicStatistics.getOsDriverState());
			}

			NicCapabilities nicCapabilities = CollectionUtils.find(nicCapabilitiesViews,
					predicateNicCapabilities(fqdd));
			if (nicCapabilities != null) {
				nicView.setIcsciBootMode(nicCapabilities.getIscsiBootSupport());
			}

		}

		return dcimNicViewList;
	}

	private Predicate<NICStatistics> predicateNicStatistics(String fqdd) {
		return new Predicate<NICStatistics>() {
			@Override
			public boolean evaluate(NICStatistics nicStatistics) {
				if (nicStatistics == null) {
					return false;
				}
				return StringUtils.contains(nicStatistics.getFqdd(), fqdd.trim());
			}
		};
	}

	private Predicate<NicCapabilities> predicateNicCapabilities(String fqdd) {
		return new Predicate<NicCapabilities>() {
			@Override
			public boolean evaluate(NicCapabilities nicCapabilities) {
				if (nicCapabilities == null) {
					return false;
				}
				return StringUtils.contains(nicCapabilities.getFqdd(), fqdd.trim());
			}
		};
	}

	private PowerMonitoring buildPowerMonitoring(IdracWSManClient idracWsManClient) throws Exception {

		IPowerThermalAdapter adapter = new PowerThermalAdapterImpl();

		PowerMonitoring powerMonitoring = adapter.collectPowerMonitoring(idracWsManClient);
		DcimStringCmd systemStringCmd = new DcimStringCmd(idracWsManClient.getIpAddress(), idracWsManClient.getUser(),
				idracWsManClient.getPassword(), WSManClassEnum.DCIM_SystemString.name());
		List<DcimStringCmdView> systemStringViews = systemStringCmd.execute();
		DcimStringCmdView systemStringView = CollectionUtils.find(systemStringViews,
				predicateDcimStringView(SYSTEM_FQDD, PowerMonitoringConstants.ACTIVE_POWER_POLICY));
		if (systemStringView != null) {
			powerMonitoring.setActivePolicyName(systemStringView.getCurrentValue());
		}
		return powerMonitoring;
	}

	private List<NumericSensorView> buildTemperatureView(IdracWSManClient idracWsManClient) throws Exception {
		List<NumericSensorView> temperatureView = (List<NumericSensorView>) CollectionUtils.select(
				idracWsManClient.execute(new EnumerateNumericSensorViewCmd()),
				predicateNumericSensorView(SensorTypeEnum.IDENTIFIER_PROPERTY, SensorTypeEnum.TEMPERATURE.getValue()));
		return temperatureView;
	}

	private List<FanView> buildFans(IdracWSManClient idracWsManClient) throws Exception {
		List<FanView> fanViewList = idracWsManClient.execute(new EnumerateFanViewCmd());
		List<NumericSensorView> numericSensorViews = idracWsManClient.execute(new EnumerateNumericSensorViewCmd());
		for (FanView fanView : fanViewList) {
			String fqdd = fanView.getFQDD();
			NumericSensorView numericSensorView = CollectionUtils.find(numericSensorViews,
					predicateNumericSensorView(fqdd));
			if (numericSensorView != null) {
				fanView.setLowerThresholdCritical(numericSensorView.getLowerThresholdCritical());
			}
			numericSensorView = CollectionUtils.find(numericSensorViews, predicateNumericSensorView(fqdd));
			if (numericSensorView != null) {
				fanView.setLowerThresholdNonCritical(numericSensorView.getLowerThresholdNonCritical());
			}
			numericSensorView = CollectionUtils.find(numericSensorViews, predicateNumericSensorView(fqdd));
			if (numericSensorView != null) {
				fanView.setUpperThresholdCritical(numericSensorView.getUpperThresholdCritical());
			}
			numericSensorView = CollectionUtils.find(numericSensorViews, predicateNumericSensorView(fqdd));
			if (numericSensorView != null) {
				fanView.setUpperThresholdNonCritical(numericSensorView.getUpperThresholdNonCritical());
			}
		}
		return fanViewList;
	}

	private Predicate<NumericSensorView> predicateNumericSensorView(String propertyName, String propertyValue) {
		return new Predicate<NumericSensorView>() {
			@Override
			public boolean evaluate(NumericSensorView numericSensorView) {
				if (numericSensorView == null) {
					return false;
				}
				try {
					return propertyValue.equals(PropertyUtils.getProperty(numericSensorView, propertyName));
				} catch (Exception e) {
					return false;
				}
			}
		};
	}

	private Predicate<NumericSensorView> predicateNumericSensorView(String fqdd) {
		return new Predicate<NumericSensorView>() {
			@Override
			public boolean evaluate(NumericSensorView numericSensorView) {
				if (numericSensorView == null) {
					return false;
				}
				return StringUtils.contains(numericSensorView.getDeviceId(), fqdd.trim());
			}
		};
	}

	private Predicate<DcimEnumCmdView> predicateDcimEnumerationView(String instanceId) {
		return new Predicate<DcimEnumCmdView>() {
			@Override
			public boolean evaluate(DcimEnumCmdView dcimEnumView) {
				if (dcimEnumView == null) {
					return false;
				}
				return StringUtils.contains(dcimEnumView.getInstanceID(), instanceId.trim());
			}
		};
	}

	private Predicate<DcimIntegerCmdView> predicateDcimIntegerView(String instanceId) {
		return new Predicate<DcimIntegerCmdView>() {
			@Override
			public boolean evaluate(DcimIntegerCmdView dcimIntegerView) {
				if (dcimIntegerView == null) {
					return false;
				}
				return StringUtils.contains(dcimIntegerView.getInstanceID(), instanceId.trim());
			}
		};
	}

	private Predicate<DcimStringCmdView> predicateDcimStringView(String instanceId) {
		return new Predicate<DcimStringCmdView>() {
			@Override
			public boolean evaluate(DcimStringCmdView dcimStringView) {
				if (dcimStringView == null) {
					return false;
				}
				return StringUtils.contains(dcimStringView.getInstanceID(), instanceId.trim());
			}
		};
	}

	private Predicate<DcimStringCmdView> predicateDcimStringView(String instanceId, String attributeName) {
		return new Predicate<DcimStringCmdView>() {
			@Override
			public boolean evaluate(DcimStringCmdView dcimStringView) {
				if (dcimStringView == null) {
					return false;
				}
				return StringUtils.contains(dcimStringView.getInstanceID(), instanceId.trim())
						&& StringUtils.equals(dcimStringView.getAttributeName(), attributeName.trim());
			}
		};
	}

	private Predicate<ControllerBatteryView> predicateControllerBatteryView(String instanceId) {
		return new Predicate<ControllerBatteryView>() {
			@Override
			public boolean evaluate(ControllerBatteryView controllerBatteryView) {
				if (controllerBatteryView == null) {
					return false;
				}
				return StringUtils.contains(controllerBatteryView.getInstanceId(), instanceId.trim());
			}
		};
	}

	private Predicate<PcieSsdView> predicatePcieSsdView(String fqdd) {
		return new Predicate<PcieSsdView>() {
			@Override
			public boolean evaluate(PcieSsdView pcieSsdView) {
				if (pcieSsdView == null) {
					return false;
				}
				return StringUtils.contains(pcieSsdView.getInstanceId(), fqdd.trim());
			}
		};
	}

}
