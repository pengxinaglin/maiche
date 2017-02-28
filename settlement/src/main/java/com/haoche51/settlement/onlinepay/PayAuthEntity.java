package com.haoche51.settlement.onlinepay;

import java.io.Serializable;

public class PayAuthEntity implements Serializable {

    private String MD5_KEY;
    private String RSA_PRIVATE;
    private String RSA_YT_PUBLIC;

    public void setMD5_KEY(String MD5_KEY) {
        this.MD5_KEY = MD5_KEY;
    }

    public String getMD5_KEY() {
        return MD5_KEY;
    }

    public String getRSA_PRIVATE() {
        return RSA_PRIVATE;
    }

    public void setRSA_PRIVATE(String RSA_PRIVATE) {
        this.RSA_PRIVATE = RSA_PRIVATE;
    }

    public String getRSA_YT_PUBLIC() {
        return RSA_YT_PUBLIC;
    }

    public void setRSA_YT_PUBLIC(String RSA_YT_PUBLIC) {
        this.RSA_YT_PUBLIC = RSA_YT_PUBLIC;
    }
}
