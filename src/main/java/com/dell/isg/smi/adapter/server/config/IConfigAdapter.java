package com.dell.isg.smi.adapter.server.config;

import com.dell.isg.smi.adapter.server.model.NetworkShare;
import com.dell.isg.smi.adapter.server.model.WsmanCredentials;

public interface IConfigAdapter {
	
	public boolean configureTraps(WsmanCredentials credentials, String targetDestination) throws Exception;

	public boolean updateTrapFormat(WsmanCredentials credentials, String snmpTrapFormat) throws Exception;

	public int manageServerPower(WsmanCredentials credentials, String powerState) throws Exception;

	public Object pollJobStatus(WsmanCredentials credentials, String jobId, int sleepTimeInMillis, int retryCount)
			throws Exception;

	public int resetServer(WsmanCredentials credentials) throws Exception;

	public boolean ejectServer(WsmanCredentials credentials) throws Exception;

	public boolean isSupportedLicenseInstalled(WsmanCredentials credentials) throws Exception;

	public String exportTechSupportReport(WsmanCredentials credentials, NetworkShare networkShare) throws Exception;
	
	public String getServerTroubleShootMode(WsmanCredentials credentials) throws Exception;

	public String setServerTroubleShootMode(WsmanCredentials credentials) throws Exception;

	public String clearServerTroubleShootMode(WsmanCredentials credentials) throws Exception;

	public boolean startBlinkLed(WsmanCredentials credentials, int duration) throws Exception;

	public boolean stopBlinkLed(WsmanCredentials credentials) throws Exception;

}
