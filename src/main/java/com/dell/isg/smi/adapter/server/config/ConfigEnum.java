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
        NFS("0"), CIFS("2");

        String value;


        SHARE_TYPES(String value) {
            this.value = value;
        }


        public String getValue() {
            return this.value;
        }
    }

    public enum SHARE_NAME {
        NFS("nfs"), CIFS("cifs");

        String value;


        SHARE_NAME(String value) {
            this.value = value;
        }


        public String getValue() {
            return this.value;
        }
    }

}
