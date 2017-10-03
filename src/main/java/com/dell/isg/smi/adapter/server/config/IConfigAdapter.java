/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.adapter.server.config;

import java.util.List;
import java.util.Map;

import com.dell.isg.smi.adapter.server.model.NetworkShare;
import com.dell.isg.smi.adapter.server.model.WsmanCredentials;
import com.dell.isg.smi.wsman.model.XmlConfig;

/**
 * The Interface IConfigAdapter.
 */
public interface IConfigAdapter {

	/**
	 * Configure traps.
	 *
	 * @param credentials the credentials
	 * @param targetDestination the target destination
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	public boolean configureTraps(WsmanCredentials credentials, String targetDestination) throws Exception;

	/**
	 * Update trap format.
	 *
	 * @param credentials the credentials
	 * @param snmpTrapFormat  the snmp trap format
	 * @return true, if successful
	 * @throws Exception  the exception
	 */
	public boolean updateTrapFormat(WsmanCredentials credentials, String snmpTrapFormat) throws Exception;

	/**
	 * Manage server power.
	 *
	 * @param credentials the credentials
	 * @param powerState the power state
	 * @return the int
	 * @throws Exception the exception
	 */
	public int manageServerPower(WsmanCredentials credentials, String powerState) throws Exception;

	/**
	 * Poll job status.
	 *
	 * @param credentials the credentials
	 * @param jobId the job id
	 * @param sleepTimeInMillis the sleep time in millis
	 * @param retryCount the retry count
	 * @return the object
	 * @throws Exception the exception
	 */
	public Object pollJobStatus(WsmanCredentials credentials, String jobId, int sleepTimeInMillis, int retryCount)
			throws Exception;

	/**
	 * Reset server.
	 *
	 * @param credentials the credentials
	 * @return the int
	 * @throws Exception the exception
	 */
	public int resetServer(WsmanCredentials credentials) throws Exception;

	/**
	 * Eject server.
	 *
	 * @param credentials the credentials
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	public boolean ejectServer(WsmanCredentials credentials) throws Exception;

	/**
	 * Checks if is supported license installed.
	 *
	 * @param credentials the credentials
	 * @return true, if is supported license installed
	 * @throws Exception the exception
	 */
	public boolean isSupportedLicenseInstalled(WsmanCredentials credentials) throws Exception;

	/**
	 * Export tech support report.
	 *
	 * @param credentials the credentials
	 * @param networkShare the network share
	 * @return the string
	 * @throws Exception the exception
	 */
	public String exportTechSupportReport(WsmanCredentials credentials, NetworkShare networkShare) throws Exception;

	/**
	 * Gets the server trouble shoot mode.
	 *
	 * @param credentials the credentials
	 * @return the server trouble shoot mode
	 * @throws Exception the exception
	 */
	public String getServerTroubleShootMode(WsmanCredentials credentials) throws Exception;

	/**
	 * Sets the server trouble shoot mode.
	 *
	 * @param credentials the credentials
	 * @return the string
	 * @throws Exception the exception
	 */
	public String setServerTroubleShootMode(WsmanCredentials credentials) throws Exception;

	/**
	 * Clear server trouble shoot mode.
	 *
	 * @param credentials the credentials
	 * @return the string
	 * @throws Exception the exception
	 */
	public String clearServerTroubleShootMode(WsmanCredentials credentials) throws Exception;

	/**
	 * Start blink led.
	 *
	 * @param credentials the credentials
	 * @param duration the duration
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	public boolean startBlinkLed(WsmanCredentials credentials, int duration) throws Exception;

	/**
	 * Stop blink led.
	 *
	 * @param credentials the credentials
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	public boolean stopBlinkLed(WsmanCredentials credentials) throws Exception;

	/**
	 * Export configuration setting.
	 *
	 * @param wsmanCredentials the wsman credentials
	 * @param networkShare the network share
	 * @param components the components
	 * @param mode the mode
	 * @return the xml config
	 * @throws Exception the exception
	 */
	public XmlConfig exportServerConfig(WsmanCredentials wsmanCredentials, NetworkShare networkShare, String components,
			String mode) throws Exception;

	/**
	 * Import configuration setting.
	 * 
	 * configuration import Result.
	 *
	 * @param wsmanCredentials the wsman credentials
	 * @param networkShare the network share
	 * @param shutdownType the shutdown type
	 * @return the xml config
	 * @throws Exception the exception
	 */
	public XmlConfig applyServerConfig(WsmanCredentials wsmanCredentials, NetworkShare networkShare, int shutdownType)
			throws Exception;

	/**
	 * Preview configuration import Result.
	 *
	 * @param wsmanCredentials the wsman credentials
	 * @param networkShare the network share
	 * @return the xml config
	 * @throws Exception the exception
	 */
	public XmlConfig previewImportServerConfig(WsmanCredentials wsmanCredentials, NetworkShare networkShare)
			throws Exception;

	/**
	 * Get configuration import Result.
	 *
	 * @param wsmanCredentials the wsman credentials
	 * @param jobId the job id
	 * @return the object
	 * @throws Exception the exception
	 */
	public Object previewConfigResults(WsmanCredentials wsmanCredentials, String jobId) throws Exception;

	/**
	 * Export Hardware Inventory.
	 *
	 * @param wsmanCredentials the wsman credentials
	 * @param networkShare the network share
	 * @return the xml config
	 * @throws Exception the exception
	 */
	public XmlConfig exportHardwareInventory(WsmanCredentials wsmanCredentials, NetworkShare networkShare)
			throws Exception;

	/**
	 * Export Factory Setting.
	 *
	 * @param wsmanCredentials the wsman credentials
	 * @param networkShare the network share
	 * @return the xml config
	 * @throws Exception the exception
	 */
	public XmlConfig exportFactorySetting(WsmanCredentials wsmanCredentials, NetworkShare networkShare)
			throws Exception;
	
	/**
	 * Wipe Life controller.
	 *
	 * @param wsmanCredentials the wsman credentials
	 * @return the xml config
	 * @throws Exception the exception
	 */
	public XmlConfig wipeLifeController(WsmanCredentials wsmanCredentials) throws Exception;
	
	/**
	 * System Erase.
	 *
	 * @param wsmanCredentials the wsman credentials
	 * @param components the components
	 * @return the xml config
	 * @throws Exception the exception
	 */
	public XmlConfig performSystemErase(WsmanCredentials wsmanCredentials, String[] components) throws Exception;
	
	/**
	 * Backup Server Image.
	 *
	 * @param wsmanCredentials the wsman credentials
	 * @param networkShare the network share
	 * @param passphrase the passphrase
	 * @param imageName the image name
	 * @param workgroup the workgroup
	 * @param scheduleStartTime the schedule start time
	 * @param untilTime the until time
	 * @return the xml config
	 * @throws Exception the exception
	 */
	public XmlConfig backupServerImage(WsmanCredentials wsmanCredentials, NetworkShare networkShare,  String passphrase, String imageName,
			String workgroup, String scheduleStartTime, String untilTime) throws Exception;
	
	/**
	 * Restore Server Image.
	 *
	 * @param wsmanCredentials the wsman credentials
	 * @param networkShare the network share
	 * @param passphrase the passphrase
	 * @param imageName the image name
	 * @param workgroup the workgroup
	 * @param scheduleStartTime the schedule start time
	 * @param untilTime the until time
	 * @param preserveVDConfig the preserve VD config
	 * @return the xml config
	 * @throws Exception the exception
	 */
	public XmlConfig restoreServerImage(WsmanCredentials wsmanCredentials, NetworkShare networkShare,  String passphrase, String imageName,
			String workgroup, String scheduleStartTime, String untilTime, String preserveVDConfig) throws Exception;
	
	/**
	 * Verify the share connectivity from server.
	 *
	 * @param wsmanCredentials the wsman credentials
	 * @param networkShare the network share
	 * @return the string
	 * @throws Exception the exception
	 */
	public String verifyServerNetworkShareConnectivity(WsmanCredentials wsmanCredentials, NetworkShare networkShare)
			throws Exception;
	

	/**
	 * Update Bios attribute.
	 *
	 * @param wsmanCredentials the wsman credentials
	 * @param attributes the attributes
	 * @param isCreateConfigJob the is create config job
	 * @return the string
	 * @throws Exception the exception
	 */
	public String updateBiosAttributes(WsmanCredentials wsmanCredentials, Map<String, String> attributes, boolean isCreateConfigJob)
			throws Exception;
	

	/**
	 * Change boot order.
	 *
	 * @param wsmanCredentials the wsman credentials
	 * @param instanceType the instance type
	 * @param instanceIdList the instance id list
	 * @return the string
	 * @throws Exception the exception
	 */
	public String changeBootOrder(WsmanCredentials wsmanCredentials, String instanceType, List<String> instanceIdList)
			throws Exception;
	
	/**
	 * Change BootSourceState.
	 *
	 * @param wsmanCredentials the wsman credentials
	 * @param instanceIdList the instance id list
	 * @param isEnabled the is enabled
	 * @param instanceType the instance type
	 * @return the string
	 * @throws Exception the exception
	 */
	public String changeBootSourceState(WsmanCredentials wsmanCredentials, List<String> instanceIdList, boolean isEnabled, String instanceType)
	        throws Exception;
	
	/**
	 * Create Target Config Job.
	 *
	 * @param wsmanCredentials the wsman credentials
	 * @param target the target
	 * @param rebootJobType the reboot job type
	 * @param scheduledStartTime the scheduled start time
	 * @param untilTime the until time
	 * @return the map
	 * @throws Exception the exception
	 */
	public Map<String, String> createTargetConfigJob(WsmanCredentials wsmanCredentials, String target, int rebootJobType, String scheduledStartTime, String untilTime)
	        throws Exception;
}
