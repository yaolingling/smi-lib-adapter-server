/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */

package com.dell.isg.smi.adapter.server.powerthermal;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.dell.isg.smi.adapter.server.model.WsmanCredentials;
import com.dell.isg.smi.commons.model.server.JobStatus;
import com.dell.isg.smi.wsman.command.CreateConfigJobCmd;
import com.dell.isg.smi.wsman.command.SetPowerCappingCmd;
import com.dell.isg.smi.wsman.command.entity.ConfigJobDetail;
import com.dell.isg.smi.wsmanclient.IWSManClient;
import com.dell.isg.smi.wsmanclient.WSManClientFactory;
import com.dell.isg.smi.wsmanclient.WSManConstants.WSManClassEnum;
import com.dell.isg.smi.wsmanclient.impl.DefaultEnumerate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Criteria;
import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.JsonPath;

/**
 * @author rahman.muhammad
 *
 */

@Component("powerThermalAdapter, powerThermalAdapterImpl")
public class PowerThermalAdapterImpl implements IPowerThermalAdapter {

	private static final Logger logger = LoggerFactory.getLogger(PowerThermalAdapterImpl.class.getName());

	@Override
	public Object collectPowerMonitoring(WsmanCredentials credentials) throws Exception {
		logger.info("Collecting PowerMonitoring data from PowerAdapter for server {}", credentials.getAddress());
		Map<String, Object> results = new LinkedHashMap<>();
		IWSManClient wsmanClient = WSManClientFactory.getClient(credentials.getAddress(), credentials.getUserName(),
				credentials.getPassword());
		addPowerCap(wsmanClient, results);
		collectPowerStatistics(wsmanClient, results);
		collectPowerStatisticsAggregated(wsmanClient, results);
		collectSystemProfile(wsmanClient, results);
		collectPsNumericSensorAttributes(wsmanClient, results);

		return results;
	}

	private void addPowerCap(IWSManClient wsmanClient, Map<String, Object> results) throws Exception {
		Object system = wsmanClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_SystemView));
		String json = new ObjectMapper().writeValueAsString(system);
		String powerCap = JsonPath.parse(json).read("$.PowerCap");
		results.put("PowerCap", powerCap);
	}

	/*
	 * collect PowerStatistics data to populate the power monitoring
	 */

	private void collectPowerStatistics(IWSManClient wsmanClient, Map<String, Object> results) throws Exception {
		logger.trace("Entering collectPowerStatistics");
		String querbyBy = "InstanceID";
		Object baseMatricValues = wsmanClient
				.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_BaseMetricValue));
		String[] attributes = { PowerMonitoringConstants.ENERGY_CONSUMPTION,
				PowerMonitoringConstants.SYSTEM_INSTANTANEOUS_HEADROOM };
		addAttributes(querbyBy,baseMatricValues, attributes, results);
		logger.trace("Exiting the method collectPowerStatistics");
	}

	private void addAttributes(String queryBy, Object jsonObj, String[] attributes, Map<String, Object> results) throws Exception {
		String json = new ObjectMapper().writeValueAsString(jsonObj);
		for (String attribute : attributes) {
			Filter filter = Filter.filter(Criteria.where(queryBy).contains(attribute));
			List<Map<String, Object>> objList = JsonPath.parse(json).read("$[?]", filter);
			if (!CollectionUtils.isEmpty(objList)) {
				// String value = JsonPath.parse(new
				// ObjectMapper().writeValueAsString(objList.get(0))).read("$.MetricValue");
				results.put(attribute, objList.get(0));
			}
		}
	}

	private void collectPowerStatisticsAggregated(IWSManClient wsmanClient, Map<String, Object> results)
			throws Exception {
		logger.trace("Entering collectPowerStatisticsAggregated method");
		String querbyBy = "InstanceID";
		Object aggregateMetricValueList = wsmanClient
				.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_AggregationMetricValue));
		String[] attributes = { PowerMonitoringConstants.SYSTEM_PEAK_POWER, PowerMonitoringConstants.SYSTEM_PEAK_AMPS,
				PowerMonitoringConstants.SYSTEM_PEAK_HEAD_ROOM, PowerMonitoringConstants.LAST_HOUR_AVERAGE,
				PowerMonitoringConstants.LAST_DAY_AVERAGE, PowerMonitoringConstants.LAST_WEEK_AVERAGE,
				PowerMonitoringConstants.LAST_HOUR_MAX, PowerMonitoringConstants.LAST_DAY_MAX,
				PowerMonitoringConstants.LAST_WEEK_MAX, PowerMonitoringConstants.LAST_HOUR_MIN,
				PowerMonitoringConstants.LAST_DAY_MIN, PowerMonitoringConstants.LAST_WEEK_MIN };
		
		addAttributes(querbyBy,aggregateMetricValueList, attributes, results);

		logger.trace("Exiting the method collectPowerStatisticsAggregated");

	}

	/*
	 * Enumerate BIOS for Power Monitoring
	 */
	private void collectSystemProfile(IWSManClient wsmanClient, Map<String, Object> results) throws Exception {
		logger.trace("Entering the method collectSystemProfile");
		String querbyBy = "InstanceID";
		Object biosEnumlist = wsmanClient.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_BIOSEnumeration));
		String[] attributes = { PowerMonitoringConstants.SYSTEM_PROFILE };
		addAttributes(querbyBy,biosEnumlist, attributes, results);
		logger.trace("Exiting the method collectSystemProfile");
	}

	private void collectPsNumericSensorAttributes(IWSManClient wsmanClient, Map<String, Object> results)
			throws Exception {
		logger.trace("Entering the method collectPsNumericSensorAttributes");
		String querbyBy = "DeviceID";
		Object psNumericViewList = wsmanClient
				.execute(new DefaultEnumerate<Object>(WSManClassEnum.DCIM_PSNumericsensor));
		String[] attributes = { PowerMonitoringConstants.SYSTEM_BOARD_PWR_CONSUMPTION };
		addAttributes(querbyBy,psNumericViewList, attributes, results);

		logger.trace("Exiting the method collectPsNumericSensorAttributes");
	}

	@Override
	public JobStatus setPowerCapping(WsmanCredentials credentials, String capValue) throws Exception {

		logger.info("Configuring Power capping for server node {}", credentials.getAddress());
		JobStatus job = new JobStatus();
		job.setServerAddress(credentials.getAddress());
		SetPowerCappingCmd cmd = new SetPowerCappingCmd(credentials.getAddress(), credentials.getUserName(),
				credentials.getPassword());
		cmd.addAttributeKeyValue("ServerPwr.1#PowerCapValue", capValue);

		Object result = cmd.execute();
		return job;
	}

	@Override
	public JobStatus enablePowerCapping(WsmanCredentials credentials, String status) throws Exception {

		SetPowerCappingCmd cmd = new SetPowerCappingCmd(credentials.getAddress(), credentials.getUserName(),
				credentials.getPassword());
		cmd.addAttributeKeyValue("ServerPwr.1#PowerCapSetting", status);
		Object result = cmd.execute();

		return null;

	}

	public JobStatus createConfigJob(WsmanCredentials credentials) throws Exception {

		CreateConfigJobCmd configCmd = new CreateConfigJobCmd(credentials.getAddress(), credentials.getUserName(),
				credentials.getPassword(), false);
		ConfigJobDetail job = configCmd.execute();
		return this.getJobStatus(job);

	}

	private JobStatus getJobStatus(ConfigJobDetail job) {

		JobStatus jobStatus = new JobStatus();

		if (job != null) {
			jobStatus.setJobId(job.getJobList().get(0));
			jobStatus.setStatus(job.getReturnCode().toString());

		}

		return jobStatus;

	}
}
