/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.adapter.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.dell.isg.smi.commons.elm.exception.RuntimeCoreException;
import com.dell.isg.smi.wsman.WSCommandRNDConstant;
import com.dell.isg.smi.wsman.command.BootConfigSettingCmd;
import com.dell.isg.smi.wsman.command.BootSourceSettingCmd;
import com.dell.isg.smi.wsman.command.EnumerateBIOSAttributeCmd;
import com.dell.isg.smi.wsman.command.EnumerateBIOSIntegerCmd;
import com.dell.isg.smi.wsman.command.EnumerateBIOSStringCmd;
import com.dell.isg.smi.wsman.command.entity.BootConfigSetting;
import com.dell.isg.smi.wsman.command.entity.BootOrderDetails;
import com.dell.isg.smi.wsman.command.entity.BootSourceSettingDetails;
import com.dell.isg.smi.wsman.command.entity.BootSourcesType;
import com.dell.isg.smi.wsman.command.entity.CimString;
import com.dell.isg.smi.wsman.command.entity.DCIMBIOSConfig;
import com.dell.isg.smi.wsman.command.entity.DCIMBIOSEnumerationType;
import com.dell.isg.smi.wsman.command.entity.DCIMBIOSIntegerType;
import com.dell.isg.smi.wsman.command.entity.DCIMBIOSStringType;
import com.dell.isg.smi.adapter.server.delegate.ServerAdapterConfigurationDelegate;
import com.dell.isg.smi.adapter.server.model.WsmanCredentials;

/**
 * @author pradeep.nagandla
 *
 */
@Component
public class ServerDeviceHardwareAdapterImpl implements IServerDeviceHardwareAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ServerAdapterConfigurationDelegate.class.getName());


    @Override
    public DCIMBIOSConfig collectBiosConfig(WsmanCredentials wsmanCredentials) throws Exception {
        EnumerateBIOSAttributeCmd enumerateBIOSAttributeCmd = new EnumerateBIOSAttributeCmd(wsmanCredentials.getAddress(), wsmanCredentials.getUserName(), wsmanCredentials.getPassword(), null);
        List<DCIMBIOSEnumerationType> biosEnumerationList = enumerateBIOSAttributeCmd.execute();

        EnumerateBIOSStringCmd enumerateBIOSStringCmd = new EnumerateBIOSStringCmd(wsmanCredentials.getAddress(), wsmanCredentials.getUserName(), wsmanCredentials.getPassword());
        List<DCIMBIOSStringType> biosStringList = enumerateBIOSStringCmd.execute();

        EnumerateBIOSIntegerCmd enumerateBIOSIntegerCmd = new EnumerateBIOSIntegerCmd(wsmanCredentials.getAddress(), wsmanCredentials.getUserName(), wsmanCredentials.getPassword());
        List<DCIMBIOSIntegerType> biosIntegerList = enumerateBIOSIntegerCmd.execute();

        DCIMBIOSConfig dcimBIOSConfig = new DCIMBIOSConfig();
        dcimBIOSConfig.setDcimBIOSEnumerationTypeList(biosEnumerationList);
        dcimBIOSConfig.setDCIMBIOSStringType(biosStringList);
        dcimBIOSConfig.setDcimBIOSIntegerType(biosIntegerList);

        return dcimBIOSConfig;
    }


    @Override
    public BootOrderDetails getBootOrderDetails(WsmanCredentials wsmanCredentials) throws Exception {
        // Get the boot config settings
        BootConfigSettingCmd bootConfigSettingCmd = new BootConfigSettingCmd(wsmanCredentials.getAddress(), wsmanCredentials.getUserName(), wsmanCredentials.getPassword());
        List<BootConfigSetting> bootConfigSettings = bootConfigSettingCmd.execute();
        if (CollectionUtils.isEmpty(bootConfigSettings)) {
            RuntimeCoreException rce = new RuntimeCoreException("Empty results from BootConfigSettingCmd");
            throw rce;
        }

        // Once you get the boot config settings, get the boot sources.
        BootSourceSettingCmd bootSourceSettingCmd = new BootSourceSettingCmd(wsmanCredentials.getAddress(), wsmanCredentials.getUserName(), wsmanCredentials.getPassword());
        List<BootSourceSettingDetails> bootSourceSettings = bootSourceSettingCmd.execute();
        if (CollectionUtils.isEmpty(bootSourceSettings)) {
            RuntimeCoreException rce = new RuntimeCoreException("Empty results from BootSourceSettingCmd");
            throw rce;
        }
        // Get the Bios output after enumerating
        EnumerateBIOSAttributeCmd enumerateBIOSAttributeCmd = new EnumerateBIOSAttributeCmd(wsmanCredentials.getAddress(), wsmanCredentials.getUserName(), wsmanCredentials.getPassword(), "BootSeqRetry");
        List<DCIMBIOSEnumerationType> bootSeqRetry = enumerateBIOSAttributeCmd.execute();
        if (CollectionUtils.isEmpty(bootSeqRetry)) {
            RuntimeCoreException rce = new RuntimeCoreException("Empty results from EnumerateBIOSAttributeCmd");
            throw rce;
        }

        // Convert it into the object and the XML string you need it to be.
        return getBootOrderDetails(bootConfigSettings, bootSourceSettings, bootSeqRetry);
    }


    /**
     *
     * @param bootConfigSettings
     * @param bootSourceSettings
     * @return
     */
    private BootOrderDetails getBootOrderDetails(List<BootConfigSetting> bootConfigSettings, List<BootSourceSettingDetails> bootSourceSettings, List<DCIMBIOSEnumerationType> bootSeqRetry) {

        logger.info("Entering getBootOrderDetails () ");
        BootOrderDetails bootOrderDetails = new BootOrderDetails();

        // first time it will be set to true - when the UI edits it , it will be set from the UI
        bootOrderDetails.setIncludeBootOrderInProfile(true);

        String currentBootSource = getCurrentBootSourceConfig(bootConfigSettings);

        // Set the bootMode
        bootOrderDetails.setCurrentBootMode(currentBootSource);

        // separate the bootSources by the boot Mode - BCV/IPL/UEFI/vFLASH will be the key of the map.
        Map<String, List<BootSourcesType>> bootSourcesMap = getBootSourcesByBootMode(bootSourceSettings);
        Set<String> bootSourcesKey = bootSourcesMap.keySet();
        Iterator<String> bootSourcesIterator = bootSourcesKey.iterator();

        // Set the BootSourceTypes for hard disks and also other lists after iterating through the Boot source map.
        while (bootSourcesIterator.hasNext()) {

            String sourceName = bootSourcesIterator.next();
            List<BootSourcesType> bootSourcesList = bootSourcesMap.get(sourceName);

            // If the bootSource Name is BCV , it is a harddisk - so add to the hard disk list
            if (StringUtils.equalsIgnoreCase(sourceName, WSCommandRNDConstant.BCV)) {

                createBootOrderHardDrives(bootOrderDetails);
                bootOrderDetails.getHardDrives().getHardDrive().addAll(bootSourcesList);

            } else { // else add it to the normal BootSource Settings.

                // Add it to the bootSources List by the boot mode.
                createBootSourcesByBootModes(bootOrderDetails);

                // Create the BootMode Object for
                BootOrderDetails.BootSourcesByBootModes.BootSourcesByBootMode bootSourcesByBootMode = createBootSourcesByBootMode();

                // Set the values of the BootMode and the BootSources
                if (StringUtils.equalsIgnoreCase(WSCommandRNDConstant.IPL, sourceName)) {
                    sourceName = WSCommandRNDConstant.BIOS;
                }
                bootSourcesByBootMode.setBootMode(sourceName);
                bootSourcesByBootMode.getBootSources().getBootSource().addAll(bootSourcesList);
                bootOrderDetails.getBootSourcesByBootModes().getBootSourcesByBootMode().add(bootSourcesByBootMode);
            }
        }

        // update the current BootSeqRetry token.
        setBootSeqRetry(bootOrderDetails, bootSeqRetry);

        logger.info("Returning from getBootOrderDetails () ");
        return bootOrderDetails;

    }


    /**
     * Check the boot config to see what is the current boot config set to.
     *
     * @param bootConfigSettings
     * @return
     */
    private String getCurrentBootSourceConfig(List<BootConfigSetting> bootConfigSettings) {
        for (BootConfigSetting bootConfig : bootConfigSettings) {
            if (StringUtils.equalsIgnoreCase(bootConfig.getIsCurrent(), WSCommandRNDConstant.IS_CURRENT)) {

                logger.info("Retuning from getCurrentBootSourceConfig (): " + bootConfig.getInstanceID());

                if (StringUtils.equalsIgnoreCase(bootConfig.getInstanceID(), WSCommandRNDConstant.IPL)) {
                    return WSCommandRNDConstant.BIOS;
                }

                return bootConfig.getInstanceID();
            }
        }

        return null;
    }


    private Map<String, List<BootSourcesType>> getBootSourcesByBootMode(List<BootSourceSettingDetails> bootSourceSettings) {

        logger.info("Entering getBootSourcesByBootMode () ");

        HashMap<String, List<BootSourcesType>> bootSourcesMap = new HashMap<String, List<BootSourcesType>>();

        for (BootSourceSettingDetails bootSourceSetting : bootSourceSettings) {
            String bootSourceName = getSourceType(bootSourceSetting.getInstanceId());

            List<BootSourcesType> bootSourcesList = bootSourcesMap.get(bootSourceName);
            if (null == bootSourcesList) {
                bootSourcesList = new ArrayList<BootSourcesType>();
                bootSourcesMap.put(bootSourceName, bootSourcesList);
            }
            bootSourcesList.add(createBootSourcesType(bootSourceSetting));

        }
        logger.info("Retuning the map from getBootSourcesByBootMode()");
        return bootSourcesMap;

    }


    private void createBootOrderHardDrives(BootOrderDetails bootOrderDetails) {

        if (null == bootOrderDetails.getHardDrives()) {
            BootOrderDetails.HardDrives hardDrives = new BootOrderDetails.HardDrives();
            bootOrderDetails.setHardDrives(hardDrives);
        }
    }


    private void createBootSourcesByBootModes(BootOrderDetails bootOrderDetails) {
        if (null == bootOrderDetails.getBootSourcesByBootModes()) {
            BootOrderDetails.BootSourcesByBootModes bootSources = new BootOrderDetails.BootSourcesByBootModes();
            bootOrderDetails.setBootSourcesByBootModes(bootSources);
        }
    }


    private BootOrderDetails.BootSourcesByBootModes.BootSourcesByBootMode createBootSourcesByBootMode() {
        BootOrderDetails.BootSourcesByBootModes.BootSourcesByBootMode bootSourcesByBootMode = new BootOrderDetails.BootSourcesByBootModes.BootSourcesByBootMode();
        BootOrderDetails.BootSourcesByBootModes.BootSourcesByBootMode.BootSources bootSources = new BootOrderDetails.BootSourcesByBootModes.BootSourcesByBootMode.BootSources();

        bootSourcesByBootMode.setBootSources(bootSources);

        return bootSourcesByBootMode;
    }


    private BootSourcesType createBootSourcesType(BootSourceSettingDetails bootSourceSetting) {

        BootSourcesType bootSource = new BootSourcesType();

        bootSource.setCurrentSequence(bootSourceSetting.getCurrentSequence() + "");
        bootSource.setEnabled(bootSourceSetting.getCurrentEnabledState() == WSCommandRNDConstant.CURRENT_ENABLED_STATUS ? true : false);
        bootSource.setInstanceId(bootSourceSetting.getInstanceId());
        bootSource.setType(getSourceType(bootSourceSetting.getInstanceId()));
        bootSource.setName(bootSourceSetting.getBootString());

        return bootSource;

    }


    /**
     * Instance Id looks like BCV:RAID.Integrated.1-1:a6abefeed86ceb13f4b8f65fbcae7e97. Parse this string and return BCV/IPL/UEFI/vFLASH etc
     *
     * @param instanceID
     * @return BCV/IPL/UEFI/vFLASH etc
     */
    private String getSourceType(String instanceID) {
        String[] splitStrings = StringUtils.split(instanceID, ":");

        return splitStrings[0];
    }


    private void setBootSeqRetry(BootOrderDetails bootOrderDetails, List<DCIMBIOSEnumerationType> bootSeqRetry) {

        logger.info("Entering into setBootSeqRetry() ");

        if (!CollectionUtils.isEmpty(bootSeqRetry)) {

            BootOrderDetails.BootSequenceRetry value = new BootOrderDetails.BootSequenceRetry();

            DCIMBIOSEnumerationType bootSeqRetryType = bootSeqRetry.get(0);
            List<CimString> currentValues = bootSeqRetryType.getCurrentValue();
            value.setCurrentValue(currentValues.get(0).getValue());

            // Set the possible values here
            List<CimString> possibleValues = bootSeqRetryType.getPossibleValues();
            for (CimString cimString : possibleValues) {
                value.getPossibleValues().add(cimString.getValue());
            }

            bootOrderDetails.setBootSequenceRetry(value);
        }

        logger.info("Returning from setBootSeqRetry() ");
    }

}
