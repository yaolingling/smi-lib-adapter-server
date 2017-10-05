/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.adapter.server.delegate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.dell.isg.smi.adapter.server.model.HypervisorInformation;
import com.dell.isg.smi.adapter.server.model.WsmanCredentials;
import com.dell.isg.smi.commons.elm.exception.RuntimeCoreException;
import com.dell.isg.smi.wsman.IdracWSManClient;
import com.dell.isg.smi.wsman.WSCommandRNDConstant;
import com.dell.isg.smi.wsman.WSManClientFactory;
import com.dell.isg.smi.wsman.command.BlinkLED;
import com.dell.isg.smi.wsman.command.EnumerateIDRACCardEnumCmd;
import com.dell.isg.smi.wsman.command.EnumerateIDRACCardStrCmd;
import com.dell.isg.smi.wsman.command.EnumerateLCLogEntryCmd;
import com.dell.isg.smi.wsman.command.EnumerateSelLogEntryCmd;
import com.dell.isg.smi.wsman.command.EnumerateSoftwareIdentityCmd;
import com.dell.isg.smi.wsman.command.EnumerateSoftwareIdentityForHostCmd;
import com.dell.isg.smi.wsman.command.PowerBootCmd;
import com.dell.isg.smi.wsman.command.PowerBootCmd.PowerRebootEnum;
import com.dell.isg.smi.wsman.command.ResetIdracCmd;
import com.dell.isg.smi.wsman.command.SetEventsCmd;
import com.dell.isg.smi.wsman.command.UnblinkLED;
import com.dell.isg.smi.wsman.command.UpdateEventsCmd;
import com.dell.isg.smi.wsman.command.entity.DCIMSoftwareIdentityType;
import com.dell.isg.smi.wsman.command.entity.IDRACCardStringView;
import com.dell.isg.smi.wsman.command.entity.LcLogEntry;
import com.dell.isg.smi.wsman.command.entity.SelLogEntry;
import com.dell.isg.smi.wsman.command.idraccmd.UpdateIdracAttributeCmd;
import com.dell.isg.smi.wsman.entity.KeyValuePair;

/**
 * The Class ServerAdapterConfigurationDelegate.
 *
 * @author prashanth.gowda
 */
@Component("saConfigDelegate")
public class ServerAdapterConfigurationDelegate {

    private static final Logger logger = LoggerFactory.getLogger(ServerAdapterConfigurationDelegate.class.getName());


    public HypervisorInformation collectHypervisorInformation(WsmanCredentials wsmanCredentials) {
        String address = wsmanCredentials.getAddress();
        logger.info("Entering collectHypervisorInformation {} ", address);
        String username = wsmanCredentials.getUserName();
        String password = wsmanCredentials.getPassword();
        HypervisorInformation hypervisorInformation = null;
        try {
            EnumerateSoftwareIdentityForHostCmd enumerateSoftwareIdentityForHostCmd = new EnumerateSoftwareIdentityForHostCmd(address, username, password);
            Map<String, String> resultMap = enumerateSoftwareIdentityForHostCmd.execute();
            if (resultMap != null) {
                hypervisorInformation = new HypervisorInformation();
                hypervisorInformation.setName(resultMap.get("Name"));
                hypervisorInformation.setVersionName(resultMap.get("VersionString"));
            }
        } catch (Exception e) {
            logger.info("Unable to collect hypervisor information for the server IP {} ", wsmanCredentials.getAddress());
            return hypervisorInformation;
        }
        logger.info("Exiting collectHypervisorInformation {} ", wsmanCredentials.getAddress());
        return hypervisorInformation;
    }


    @SuppressWarnings("unchecked")
    public boolean configureTraps(WsmanCredentials wsmanCredentials, String targetDestination) {
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
                result = CollectionUtils.exists(keyValuePairList, predicateForSuccessStatus("returnStatus")) && CollectionUtils.exists(keyValuePairList, predicateForJobId("jobId"));
                return result;
            }
        } catch (Exception e) {
            logger.info("Unable to register the appliance for the server IP {} ", address, targetDestination);
        } finally {
            logger.info("Exiting registerAppliance address {} targetDestination {}", address, targetDestination);
        }
        return result;
    }


    @SuppressWarnings("unchecked")
    public boolean updateTrapFormat(WsmanCredentials wsmanCredentials, String snmpTrapFormat) {
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
                return key.equals(compareValue) && (value.equals(WSCommandRNDConstant.SUCCESSFULL_CONFIG_JOB_RETURN) || value.equals(WSCommandRNDConstant.COMPLETED_WITH_NO_ERROR));
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


    private IDRACCardStringView getIdracCardForUpdate(String address, String username, String password) {
        String trapValueString = "#SNMP.1#TrapFormat";
        logger.info("Entering getIdracCradForUpdate {} ", address);
        EnumerateIDRACCardEnumCmd enumerateIdracCardEnumCmd = new EnumerateIDRACCardEnumCmd(address, username, password);
        IDRACCardStringView idracCardStringView = null;
        try {
            List<IDRACCardStringView> idracCardStringViewList = enumerateIdracCardEnumCmd.execute();
            idracCardStringView = CollectionUtils.find(idracCardStringViewList, predicateIdracCardStringView(trapValueString));
        } catch (Exception e) {
            logger.info("Unable to get IDRACCardStringView for attribute update  {} ", address);
        } finally {
            logger.info("Exiting getIdracCradForUpdate {} ", address);
        }
        return idracCardStringView;
    }

    
    public List<IDRACCardStringView> getIdracCardStringView(WsmanCredentials creds) throws Exception {
    	
        EnumerateIDRACCardStrCmd enumerateIdracCardStrCmd = new EnumerateIDRACCardStrCmd(creds.getAddress(), creds.getUserName(), creds.getPassword());
        List<IDRACCardStringView> idracCardStringViewList = Collections.emptyList();
        idracCardStringViewList = enumerateIdracCardStrCmd.execute();
        return idracCardStringViewList;
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


    public int manageServerPower(WsmanCredentials wsmanCredentials, String powerState) throws Exception {
        String address = wsmanCredentials.getAddress();
        String username = wsmanCredentials.getUserName();
        logger.info("Entering manageServerPower address {} username {}", address, username);
        PowerBootCmd powerBootCmd = new PowerBootCmd(address, username, wsmanCredentials.getPassword(), PowerRebootEnum.valueOf(powerState));
        int result = powerBootCmd.execute().intValue();
        logger.info("Exiting manageServerPower address {} username {}", address, username);
        return result;
    }


    public int resetServer(WsmanCredentials wsmanCredentials) throws Exception {
        String address = wsmanCredentials.getAddress();
        String username = wsmanCredentials.getUserName();
        logger.info("Entering resetServer address {} username {}", address, username);
        ResetIdracCmd resetIdracCmd = new ResetIdracCmd(address, username, wsmanCredentials.getPassword(), WSCommandRNDConstant.RESET_IDRAC);
        int result = Integer.valueOf(resetIdracCmd.execute().toString());
        logger.info("Exiting resetServer address {} username {}", address, username);
        return result;
    }


    @SuppressWarnings("unchecked")
    public boolean ejectServer(WsmanCredentials wsmanCredentials) {
        boolean result = false;
        String ipAddr = wsmanCredentials.getAddress();
        logger.info("Entering ejectServer {} ", ipAddr);
        UpdateIdracAttributeCmd cmd = new UpdateIdracAttributeCmd(ipAddr, wsmanCredentials.getUserName(), wsmanCredentials.getPassword());
        cmd.addTargetValue("iDRAC.Embedded.1");
        cmd.addAttributeKeyValue("IntegratedDatacenter.1#Eject", "Yes");
        try {
            List<KeyValuePair> kvp = (List<KeyValuePair>) cmd.execute();
            if (!CollectionUtils.isEmpty(kvp)) {
                result = CollectionUtils.exists(kvp, predicateForSuccessStatus("returnStatus")) && CollectionUtils.exists(kvp, predicateForJobId("jobId"));
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


    public List<SelLogEntry> getServerSelLogEntries(WsmanCredentials wsmanCredentials) throws Exception {
        EnumerateSelLogEntryCmd enumerateSelLogEntryCmd = new EnumerateSelLogEntryCmd(wsmanCredentials.getAddress(), wsmanCredentials.getUserName(), wsmanCredentials.getPassword());
        List<SelLogEntry> selLogEntryList = enumerateSelLogEntryCmd.execute();
        return selLogEntryList;
    }


    public boolean startBlinkLed(WsmanCredentials wsmanCredentials, int duration) throws Exception {
        BlinkLED blinkLed = new BlinkLED(wsmanCredentials.getAddress(), wsmanCredentials.getUserName(), wsmanCredentials.getPassword(), duration);
        boolean result = (boolean) blinkLed.execute();
        return result;
    }


    public boolean stopBlinkLed(WsmanCredentials wsmanCredentials) throws Exception {
        UnblinkLED unBlinkLed = new UnblinkLED(wsmanCredentials.getAddress(), wsmanCredentials.getUserName(), wsmanCredentials.getPassword());
        boolean result = (boolean) unBlinkLed.execute();
        return result;
    }


    public List<LcLogEntry> getServerLcLogEntries(WsmanCredentials wsmanCredentials) throws Exception {

        EnumerateLCLogEntryCmd enumerateLCLogEntryCmd = new EnumerateLCLogEntryCmd(wsmanCredentials.getAddress(), wsmanCredentials.getUserName(), wsmanCredentials.getPassword());
        List<LcLogEntry> lcLogEntryList = enumerateLCLogEntryCmd.execute();
        return lcLogEntryList;
    }


    public List<DCIMSoftwareIdentityType> enumerateDcimSoftwareIdentity(WsmanCredentials wsmanCredentials) throws Exception {
        logger.info("Entering enumerateDcimSoftwareIdentity {} ", wsmanCredentials.getAddress());
        IdracWSManClient idracWsManClient = WSManClientFactory.getIdracWSManClient(wsmanCredentials.getAddress(), wsmanCredentials.getUserName(), wsmanCredentials.getPassword());
        return idracWsManClient.execute(new EnumerateSoftwareIdentityCmd());
    }
}
