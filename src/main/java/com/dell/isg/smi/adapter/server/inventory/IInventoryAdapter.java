/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.adapter.server.inventory;

import com.dell.isg.smi.adapter.server.model.WsmanCredentials;

/**
 * @author rahman.muhammad
 *
 */
public interface IInventoryAdapter {

	// Collect spec

	public Object collectHardwareInventory(WsmanCredentials credentials) throws Exception;

	public Object collectSummary(WsmanCredentials credentials) throws Exception;

	public Object collectSelLogs(WsmanCredentials credentials) throws Exception;

	public Object collectLcLogs(WsmanCredentials credentials) throws Exception;

	public Object collectNics(WsmanCredentials credentials) throws Exception;

	public Object collectSoftware(WsmanCredentials credentials) throws Exception;

	public Object collectIdracString(WsmanCredentials credentials) throws Exception;

	public Object collectIdracCardEnum(WsmanCredentials credentials) throws Exception;

	public Object collectIdracDetails(WsmanCredentials credentials) throws Exception;

	public Object collectBios(WsmanCredentials credentials) throws Exception;

	public Object collectBoot(WsmanCredentials credentials) throws Exception;

    public Object collect(WsmanCredentials wsmanCredentials, String dcimType) throws Exception;
}
