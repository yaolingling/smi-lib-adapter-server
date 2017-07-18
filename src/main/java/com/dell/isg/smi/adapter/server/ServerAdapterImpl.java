/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.adapter.server;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dell.isg.smi.adapter.server.delegate.ServerAdapterConfigurationDelegate;
import com.dell.isg.smi.adapter.server.delegate.ServerAdapterOnboardingDelegate;
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
import com.dell.isg.smi.wsman.command.idraccmd.IdracJobStatusCheckCmd;
import com.dell.isg.smi.wsman.model.XmlConfig;

/**
 * @author rahman.muhammad
 *
 */
@Component("serverAdapter, serverAdapterImpl")
public class ServerAdapterImpl implements IServerAdapter {

    @Autowired
    ServerAdapterConfigurationDelegate saConfigDelegate;

    @Autowired
    ServerAdapterOnboardingDelegate saOnboardingDelegate;


    @Override
    public HardwareInventory collectHardwareInventory(WsmanCredentials credentials) throws Exception {
        return saOnboardingDelegate.collectHardwareInventory(credentials);

    }


    @Override
    public DCIMSystemViewType collectSystemInfo(WsmanCredentials credentials) throws Exception {
        return saOnboardingDelegate.collectSystemInfo(credentials);
    }


    @Override
    public HypervisorInformation collectHypervisorInformation(WsmanCredentials wsmanCredentials) {
        return saConfigDelegate.collectHypervisorInformation(wsmanCredentials);
    }


    @Override
    public boolean configureTraps(WsmanCredentials wsmanCredentials, String targetDestination) {
        return saConfigDelegate.configureTraps(wsmanCredentials, targetDestination);
    }


    @Override
    public boolean updateTrapFormat(WsmanCredentials wsmanCredentials, String snmpTrapFormat) {
        return saConfigDelegate.updateTrapFormat(wsmanCredentials, snmpTrapFormat);
    }


    @Override
    public int manageServerPower(WsmanCredentials wsmanCredentials, String powerState) throws Exception {
        return saConfigDelegate.manageServerPower(wsmanCredentials, powerState);
    }


    @Override
    public String onBoardServer(WsmanCredentials wsmanCredentials, NetworkShare networkShare) throws Exception {
        return saOnboardingDelegate.onBoardServer(wsmanCredentials, networkShare);
    }


    @Override
    public int resetServer(WsmanCredentials wsmanCredentials) throws Exception {
        return saConfigDelegate.resetServer(wsmanCredentials);
    }


    @Override
    public boolean ejectServer(WsmanCredentials wsmanCredentials) throws Exception {
        return saConfigDelegate.ejectServer(wsmanCredentials);

    }


    @Override
    public boolean isSupportedLicenseInstalled(WsmanCredentials wsmanCredentials) throws Exception {
        return saOnboardingDelegate.isSupportedLicenseInstalled(wsmanCredentials);
    }


    @Override
    public List<SelLogEntry> getServerSelLogEntries(WsmanCredentials wsmanCredentials) throws Exception {
        return saConfigDelegate.getServerSelLogEntries(wsmanCredentials);
    }


    @Override
    public List<LcLogEntry> getServerLcLogEntries(WsmanCredentials wsmanCredentials) throws Exception {
        return saConfigDelegate.getServerLcLogEntries(wsmanCredentials);
    }


    @Override
    public String exportTechSupportReport(WsmanCredentials wsmanCredentials, NetworkShare networkShare) throws Exception {
        return saOnboardingDelegate.exportTechSupportReport(wsmanCredentials, networkShare);
    }


    @Override
    public String setServerTroubleShootMode(WsmanCredentials wsmanCredentials) throws Exception {
        return saOnboardingDelegate.setServerTroubleShootMode(wsmanCredentials);
    }


    @Override
    public String clearServerTroubleShootMode(WsmanCredentials wsmanCredentials) throws Exception {
        return saOnboardingDelegate.clearServerTroubleShootMode(wsmanCredentials);
    }


    @Override
    public boolean startBlinkLed(WsmanCredentials wsmanCredentials, int duration) throws Exception {
        return saConfigDelegate.startBlinkLed(wsmanCredentials, duration);
    }


    @Override
    public boolean stopBlinkLed(WsmanCredentials wsmanCredentials) throws Exception {
        return saConfigDelegate.stopBlinkLed(wsmanCredentials);
    }


    @Override
    public List<DCIMNICViewType> collectNics(WsmanCredentials credentials) throws Exception {
        return saOnboardingDelegate.collectNics(credentials);
    }


    @Override
    public Object pollJobStatus(WsmanCredentials wsmanCredentials, String jobId, int sleepTimeInMillis, int retryCount) throws Exception {
        IdracJobStatusCheckCmd cmd = new IdracJobStatusCheckCmd(wsmanCredentials.getAddress(), wsmanCredentials.getUserName(), wsmanCredentials.getPassword(), jobId, sleepTimeInMillis, retryCount);
        return cmd.execute();

    }


    @Override
    public List<DCIMSoftwareIdentityType> enumerateDcimSoftwareIdentity(WsmanCredentials wsmanCredentials) {
        return saConfigDelegate.enumerateDcimSoftwareIdentity(wsmanCredentials);
    }

    @Override
    public List<IDRACCardStringView> collectIdracString(WsmanCredentials credentials) throws Exception {
        return saConfigDelegate.getIdracCardStringView(credentials);
    }


	@Override
	public XmlConfig exportServerConfig(WsmanCredentials wsmanCredentials, NetworkShare networkShare, String components,
			String mode) throws Exception {
		return saOnboardingDelegate.exportServerConfig(wsmanCredentials, networkShare, components, mode);
	}


	@Override
	public XmlConfig applyServerConfig(WsmanCredentials wsmanCredentials, NetworkShare networkShare,int shutdownType)
			throws Exception {
		return saOnboardingDelegate.applyServerConfig(wsmanCredentials, networkShare, shutdownType);
	}


}
