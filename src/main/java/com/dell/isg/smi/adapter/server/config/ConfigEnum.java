/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.adapter.server.config;

/**
 * @author rahman.muhammad
 *
 */
public class ConfigEnum {

	public enum SHARE_TYPES {
		NFS("0"), FTP("1"), CIFS("2"), TFTP("3"), VFLASH("4"), HTTP("5");

		String value;

		SHARE_TYPES(String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}
	}

	public enum SHARE_NAME {
		NFS("nfs"), FTP("ftp"), CIFS("cifs"), TFTP("tftp"), VFLASH("vflash"), HTTP("http");

		String value;

		SHARE_NAME(String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}
	}

	public enum EXPORT_MODE {
		NORMAL("0"), CLONE("1"), REPLACE("2");

		String value;

		EXPORT_MODE(String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}
	}

	public enum ERASE_COMPONENT {
		BIOS_RESET_DEFULT("BIOS"), EMBEDDED_DIAGNOSTICS_ERASE("DIAG"), OS_DRIVERPACK_ERASE("DRVPACK"), IDRAC_DEFAULT(
				"IDRAC"), LC_DATA_ERASE("LCDATA");

		String value;

		ERASE_COMPONENT(String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}
	}

}
