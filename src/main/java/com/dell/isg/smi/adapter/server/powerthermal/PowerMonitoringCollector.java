/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.adapter.server.powerthermal;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;

import com.dell.isg.smi.wsman.command.EnumerateAggregationMetricValueCmd;
import com.dell.isg.smi.wsman.command.EnumerateBIOSAttributeCmd;
import com.dell.isg.smi.wsman.command.EnumerateBaseMetricValueCmd;
import com.dell.isg.smi.wsman.command.EnumeratePSNumericSensorCmd;
import com.dell.isg.smi.wsman.command.entity.AggregationMetricValue;
import com.dell.isg.smi.wsman.command.entity.BaseMetricValue;
import com.dell.isg.smi.wsman.command.entity.CimString;
import com.dell.isg.smi.wsman.command.entity.DCIMBIOSEnumerationType;
import com.dell.isg.smi.wsman.command.entity.PSNumericView;
import com.dell.isg.smi.wsman.IdracWSManClient;
import com.dell.isg.smi.adapter.server.model.HeadRoom;
import com.dell.isg.smi.adapter.server.model.PowerMonitoring;
import com.dell.isg.smi.adapter.server.model.PowerMonitoringConstants;
import com.dell.isg.smi.adapter.server.model.PowerStatistics;
import com.dell.isg.smi.adapter.server.util.PowerMonitoringUtility;

/**
 * @author rahman.muhammad
 *
 */
public class PowerMonitoringCollector {

    private static final Logger logger = LoggerFactory.getLogger(PowerMonitoringCollector.class.getName());


    public PowerMonitoring collectPowerMonitoring(IdracWSManClient idracWsManClient) throws Exception {
        logger.trace("collecting  power monitoring for server {}", idracWsManClient.getIpAddress());
        PowerMonitoring powerMonitoring = new PowerMonitoring();
        HeadRoom headRoom = new HeadRoom();
        PowerStatistics powerStatistics = new PowerStatistics();
        try {
            collectPowerStatistics(idracWsManClient, powerStatistics, headRoom);
            collectPowerStatisticsAggregated(idracWsManClient, powerStatistics, headRoom);
            powerMonitoring.setPowerStatistics(powerStatistics);
            powerMonitoring.setHeadRoom(headRoom);
            collectSystemProfile(powerMonitoring, idracWsManClient);
            collectPsNumericSensorAttributes(powerMonitoring, idracWsManClient);
        } catch (Exception exp) { // Suppressing the exception unless needed to be made mandatory for hardware inventory
            logger.error(exp.getMessage(), exp);
        }
        logger.trace("collected power monitoring for server {}", idracWsManClient.getIpAddress());
        return powerMonitoring;
    }


    /*
     * collect PowerStatistics data to populate the power monitoring
     */

    private void collectPowerStatistics(IdracWSManClient idracWsManClient, PowerStatistics powerStatistics, HeadRoom headRoom) throws Exception {
        logger.trace("Entering collectPowerStatistics");
        List<BaseMetricValue> baseMetricValueList = idracWsManClient.execute(new EnumerateBaseMetricValueCmd());
        BaseMetricValue baseMetricValue = CollectionUtils.find(baseMetricValueList, predicateBaseMetricValue(PowerMonitoringConstants.ENERGY_CONSUMPTION));
        if (baseMetricValue != null) {
            powerStatistics.setEnergyConsumption(baseMetricValue.getMetricValue() + PowerMonitoringConstants.KILOWATT);
            powerStatistics.setEnergyConsumptionStartDateTime(PowerMonitoringUtility.getFormattedStartDate(baseMetricValue.getTimeStamp(), baseMetricValue.getDuration()));
            powerStatistics.setEnergyConsumptionEndDateTime(PowerMonitoringUtility.getFormattedString(baseMetricValue.getTimeStamp()));
        }

        baseMetricValue = CollectionUtils.find(baseMetricValueList, predicateBaseMetricValue(PowerMonitoringConstants.SYSTEM_INSTANTANEOUS_HEADROOM));
        if (baseMetricValue != null) {
            headRoom.setSystemInstantaneousHeadroom(baseMetricValue.getMetricValue() + PowerMonitoringConstants.WATT);
        }
        logger.trace("Exiting the method collectPowerStatistics");
    }


    private Predicate<BaseMetricValue> predicateBaseMetricValue(String instanceId) {
        return new Predicate<BaseMetricValue>() {
            @Override
            public boolean evaluate(BaseMetricValue baseMetricValue) {
                if (baseMetricValue == null) {
                    return false;
                }
                return StringUtils.equals(baseMetricValue.getInstanceId(), instanceId.trim());
            }
        };
    }


    /*
     * Aggregated statistical Data
     */

    private void collectPowerStatisticsAggregated(IdracWSManClient idracWsManClient, PowerStatistics powerStatistics, HeadRoom headRoom) throws Exception {
        logger.trace("Entering collectPowerStatisticsAggregated method");
        List<AggregationMetricValue> aggregateMetricValueList = idracWsManClient.execute(new EnumerateAggregationMetricValueCmd());

        AggregationMetricValue aggregationMetricValue = CollectionUtils.find(aggregateMetricValueList, predicateAggregationMetricValue(PowerMonitoringConstants.SYSTEM_PEAK_POWER));
        if (aggregationMetricValue != null) {
            powerStatistics.setSystemPeakPower(aggregationMetricValue.getMetricValue() + PowerMonitoringConstants.WATT);
            powerStatistics.setSystemPeakPowerStartDateTime(PowerMonitoringUtility.getFormattedStartDate(aggregationMetricValue.getAggregationTimestamp(), aggregationMetricValue.getAggegationDuration()));
            powerStatistics.setSystemPeakPowerEndDateTime(PowerMonitoringUtility.getFormattedString(aggregationMetricValue.getTimestamp()));
        }

        aggregationMetricValue = CollectionUtils.find(aggregateMetricValueList, predicateAggregationMetricValue(PowerMonitoringConstants.SYSTEM_PEAK_AMPS));
        if (aggregationMetricValue != null) {
            powerStatistics.setSystemPeakAmps(aggregationMetricValue.getMetricValue() + PowerMonitoringConstants.WATT);
            powerStatistics.setPeakAmpsStartDateTime(PowerMonitoringUtility.getFormattedStartDate(aggregationMetricValue.getAggregationTimestamp(), aggregationMetricValue.getAggegationDuration()));
            powerStatistics.setPeakAmpsEndDateTime(PowerMonitoringUtility.getFormattedString(aggregationMetricValue.getTimestamp()));
        }

        aggregationMetricValue = CollectionUtils.find(aggregateMetricValueList, predicateAggregationMetricValue(PowerMonitoringConstants.SYSTEM_PEAK_HEAD_ROOM));
        if (aggregationMetricValue != null) {
            headRoom.setSystemPeakHeadroom(aggregationMetricValue.getMetricValue() + PowerMonitoringConstants.WATT);
        }

        aggregationMetricValue = CollectionUtils.find(aggregateMetricValueList, predicateAggregationMetricValue(PowerMonitoringConstants.LAST_HOUR_AVERAGE));
        if (aggregationMetricValue != null) {
            powerStatistics.setLastHourPeakAverage(aggregationMetricValue.getMetricValue() + PowerMonitoringConstants.WATT);
            powerStatistics.setLastHourPeakTime(PowerMonitoringUtility.getFormattedString(aggregationMetricValue.getTimestamp()));
        }

        aggregationMetricValue = CollectionUtils.find(aggregateMetricValueList, predicateAggregationMetricValue(PowerMonitoringConstants.LAST_DAY_AVERAGE));
        if (aggregationMetricValue != null) {
            powerStatistics.setLastDayPeakAverage(aggregationMetricValue.getMetricValue() + PowerMonitoringConstants.WATT);
            powerStatistics.setLastDayPeakTime(PowerMonitoringUtility.getFormattedString(aggregationMetricValue.getTimestamp()));
        }

        aggregationMetricValue = CollectionUtils.find(aggregateMetricValueList, predicateAggregationMetricValue(PowerMonitoringConstants.LAST_WEEK_AVERAGE));
        if (aggregationMetricValue != null) {
            powerStatistics.setLastWeekPeakAverage(aggregationMetricValue.getMetricValue() + PowerMonitoringConstants.WATT);
            powerStatistics.setLastWeekPeakTime(PowerMonitoringUtility.getFormattedString(aggregationMetricValue.getTimestamp()));
        }

        aggregationMetricValue = CollectionUtils.find(aggregateMetricValueList, predicateAggregationMetricValue(PowerMonitoringConstants.LAST_HOUR_MAX));
        if (aggregationMetricValue != null) {
            powerStatistics.setLastHourPeakMax(aggregationMetricValue.getMetricValue() + PowerMonitoringConstants.WATT);
            powerStatistics.setLastHourPeakMaxTime(PowerMonitoringUtility.getFormattedString(aggregationMetricValue.getTimestamp()));
        }

        aggregationMetricValue = CollectionUtils.find(aggregateMetricValueList, predicateAggregationMetricValue(PowerMonitoringConstants.LAST_DAY_MAX));
        if (aggregationMetricValue != null) {
            powerStatistics.setLastDayPeakMax(aggregationMetricValue.getMetricValue() + PowerMonitoringConstants.WATT);
            powerStatistics.setLastDayPeakMaxTime(PowerMonitoringUtility.getFormattedString(aggregationMetricValue.getTimestamp()));
        }

        aggregationMetricValue = CollectionUtils.find(aggregateMetricValueList, predicateAggregationMetricValue(PowerMonitoringConstants.LAST_WEEK_MAX));
        if (aggregationMetricValue != null) {
            powerStatistics.setLastWeekPeakMax(aggregationMetricValue.getMetricValue() + PowerMonitoringConstants.WATT);
            powerStatistics.setLastWeekPeakMaxTime(PowerMonitoringUtility.getFormattedString(aggregationMetricValue.getTimestamp()));
        }

        aggregationMetricValue = CollectionUtils.find(aggregateMetricValueList, predicateAggregationMetricValue(PowerMonitoringConstants.LAST_HOUR_MIN));
        if (aggregationMetricValue != null) {
            powerStatistics.setLastHourPeakMin(aggregationMetricValue.getMetricValue() + PowerMonitoringConstants.WATT);
            powerStatistics.setLastHourPeakMinTime(PowerMonitoringUtility.getFormattedString(aggregationMetricValue.getTimestamp()));
        }

        aggregationMetricValue = CollectionUtils.find(aggregateMetricValueList, predicateAggregationMetricValue(PowerMonitoringConstants.LAST_DAY_MIN));
        if (aggregationMetricValue != null) {
            powerStatistics.setLastDayPeakMin(aggregationMetricValue.getMetricValue() + PowerMonitoringConstants.WATT);
            powerStatistics.setLastDayPeakMinTime(PowerMonitoringUtility.getFormattedString(aggregationMetricValue.getTimestamp()));
        }

        aggregationMetricValue = CollectionUtils.find(aggregateMetricValueList, predicateAggregationMetricValue(PowerMonitoringConstants.LAST_WEEK_MIN));
        if (aggregationMetricValue != null) {
            powerStatistics.setLastWeekPeakMin(aggregationMetricValue.getMetricValue() + PowerMonitoringConstants.WATT);
            powerStatistics.setLastWeekPeakMinTime(PowerMonitoringUtility.getFormattedString(aggregationMetricValue.getTimestamp()));
        }

        logger.trace("Exiting the method collectPowerStatisticsAggregated");

    }


    private Predicate<AggregationMetricValue> predicateAggregationMetricValue(String instanceId) {
        return new Predicate<AggregationMetricValue>() {
            @Override
            public boolean evaluate(AggregationMetricValue aggregationMetricValue) {
                if (aggregationMetricValue == null) {
                    return false;
                }
                return StringUtils.equals(aggregationMetricValue.getInstanceId(), instanceId.trim());
            }
        };
    }


    /*
     * Enumerate BIOS for Power Monitoring
     */
    private void collectSystemProfile(PowerMonitoring powerMonitoring, IdracWSManClient idracWsManClient) throws Exception {
        logger.trace("Entering the method collectSystemProfile");
        List<DCIMBIOSEnumerationType> biosEnumlist = idracWsManClient.execute(new EnumerateBIOSAttributeCmd());
        DCIMBIOSEnumerationType dcimBiosEnumerationType = CollectionUtils.find(biosEnumlist, predicateDcimBiosEnumeration(PowerMonitoringConstants.SYSTEM_PROFILE));
        if (dcimBiosEnumerationType != null) {
            String currentValue = dcimBiosEnumerationType.getCurrentValue().get(0).getValue();
            int index = ListUtils.indexOf(dcimBiosEnumerationType.getPossibleValues(), predicateListValue(currentValue));
            powerMonitoring.setProfile(dcimBiosEnumerationType.getPossibleValuesDescription().get(index).getValue());
        }
        logger.trace("Exiting the method collectSystemProfile");
    }


    private Predicate<CimString> predicateListValue(String value) {
        return new Predicate<CimString>() {
            @Override
            public boolean evaluate(CimString cimString) {
                if (cimString == null || cimString.getValue() == null) {
                    return false;
                }
                return StringUtils.equals(cimString.getValue(), value);
            }
        };
    }


    private Predicate<DCIMBIOSEnumerationType> predicateDcimBiosEnumeration(String attributeName) {
        return new Predicate<DCIMBIOSEnumerationType>() {
            @Override
            public boolean evaluate(DCIMBIOSEnumerationType dcimBiosEnumerationType) {
                if (dcimBiosEnumerationType == null || dcimBiosEnumerationType.getAttributeName().getValue() == null) {
                    return false;
                }
                return StringUtils.contains(dcimBiosEnumerationType.getAttributeName().getValue(), attributeName.trim());
            }
        };
    }


    private void collectPsNumericSensorAttributes(PowerMonitoring powerMonitoring, IdracWSManClient idracWsManClient) throws Exception {
        logger.trace("Entering the method collectPsNumericSensorAttributes");
        List<PSNumericView> psNumericViewList = idracWsManClient.execute(new EnumeratePSNumericSensorCmd());
        PSNumericView psNumericView = CollectionUtils.find(psNumericViewList, predicatePsNumericView(PowerMonitoringConstants.SYSTEM_BOARD_PWR_CONSUMPTION));
        if (psNumericView != null) {
            powerMonitoring.setProbeName(psNumericView.getElementName());
            powerMonitoring.setCurrentReading(psNumericView.getCurrentReading() + PowerMonitoringConstants.WATT);
            powerMonitoring.setFailureThreshold(psNumericView.getUpperThresholdCritical() + PowerMonitoringConstants.WATT);
            powerMonitoring.setWarningThreshold(psNumericView.getUpperThresholdNonCritical() + PowerMonitoringConstants.WATT);
        }
        logger.trace("Exiting the method collectPsNumericSensorAttributes");
    }


    private Predicate<PSNumericView> predicatePsNumericView(String deviceId) {
        return new Predicate<PSNumericView>() {
            @Override
            public boolean evaluate(PSNumericView psNumericView) {
                if (psNumericView == null) {
                    return false;
                }
                return StringUtils.contains(psNumericView.getDeviceId(), deviceId.trim());
            }
        };
    }

}
