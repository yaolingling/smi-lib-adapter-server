/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.adapter.server;

import java.util.List;

import com.dell.isg.smi.adapter.server.model.HardwareInventory;
import com.dell.isg.smi.adapter.server.model.HypervisorInformation;
import com.dell.isg.smi.adapter.server.model.NetworkShare;
import com.dell.isg.smi.adapter.server.model.WsmanCredentials;
import com.dell.isg.smi.wsman.command.entity.DCIMNICViewType;
import com.dell.isg.smi.wsman.command.entity.DCIMSoftwareIdentityType;
import com.dell.isg.smi.wsman.command.entity.DCIMSystemViewType;
import com.dell.isg.smi.wsman.command.entity.IDRACCardStringView;
import com.dell.isg.smi.wsman.command.entity.LcLogEntry;
import com.dell.isg.smi.wsman.command.entity.SelLogEntry;
import com.dell.isg.smi.wsman.model.XmlConfig;

/**
 * @author rahman.muhammad
 *
 */
public interface IServerAdapter {

	public HardwareInventory collectHardwareInventory(WsmanCredentials credentials) throws Exception;

	public DCIMSystemViewType collectSystemInfo(WsmanCredentials credentials) throws Exception;

	public HypervisorInformation collectHypervisorInformation(WsmanCredentials wsmanCredentials);

	public boolean configureTraps(WsmanCredentials wsmanCredentials, String targetDestination);

	public boolean updateTrapFormat(WsmanCredentials wsmanCredentials, String snmpTrapFormat);

	public int manageServerPower(WsmanCredentials wsmanCredentials, String powerState) throws Exception;

	public String onBoardServer(WsmanCredentials wsmanCredentials, NetworkShare networkShare) throws Exception;

	public Object pollJobStatus(WsmanCredentials wsmanCredentials, String jobId, int sleepTimeInMillis, int retryCount)
			throws Exception;

	public int resetServer(WsmanCredentials wsmanCredentials) throws Exception;

	public boolean ejectServer(WsmanCredentials wsmanCredentials) throws Exception;

	public boolean isSupportedLicenseInstalled(WsmanCredentials wsmanCredentials) throws Exception;

	public List<SelLogEntry> getServerSelLogEntries(WsmanCredentials wsmanCredentials) throws Exception;

	public List<LcLogEntry> getServerLcLogEntries(WsmanCredentials wsmanCredentials) throws Exception;

	public String exportTechSupportReport(WsmanCredentials wsmanCredentials, NetworkShare networkShare)
			throws Exception;

	public String setServerTroubleShootMode(WsmanCredentials wsmanCredentials) throws Exception;

	public String clearServerTroubleShootMode(WsmanCredentials wsmanCredentials) throws Exception;

	public boolean startBlinkLed(WsmanCredentials wsmanCredentials, int duration) throws Exception;

	public boolean stopBlinkLed(WsmanCredentials wsmanCredentials) throws Exception;

	public List<DCIMNICViewType> collectNics(WsmanCredentials credentials) throws Exception;

	public List<DCIMSoftwareIdentityType> enumerateDcimSoftwareIdentity(WsmanCredentials wsmanCredentials);

	public List<IDRACCardStringView> collectIdracString(WsmanCredentials credentials) throws Exception;

	public XmlConfig exportServerConfig(WsmanCredentials wsmanCredentials, NetworkShare networkShare, String components,
			String mode) throws Exception;

	public XmlConfig applyServerConfig(WsmanCredentials wsmanCredentials, NetworkShare networkShare, int shutdownType)
			throws Exception;

	public XmlConfig previewImportServerConfig(WsmanCredentials wsmanCredentials, NetworkShare networkShare)
			throws Exception;

	public Object previewConfigResults(WsmanCredentials wsmanCredentials, String jobId) throws Exception;

}
