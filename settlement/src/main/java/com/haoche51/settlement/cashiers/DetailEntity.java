package com.haoche51.settlement.cashiers;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yangming on 2016/4/9.
 */
public class DetailEntity implements Serializable {

    private String user_amount; // 账户余额，单位分
    private String oid_trader; // 商户号
    private YtAuthEntity yt_auth;
    private List<PaymentEntity> unconfirm_list;// 未确认的POS机订单

    public String getUser_amount() {
        return user_amount;
    }

    public void setUser_amount(String user_amount) {
        this.user_amount = user_amount;
    }

    public String getOid_trader() {
        return oid_trader;
    }

    public void setOid_trader(String oid_trader) {
        this.oid_trader = oid_trader;
    }

    public YtAuthEntity getYt_auth() {
        return yt_auth;
    }

    public void setYt_auth(YtAuthEntity yt_auth) {
        this.yt_auth = yt_auth;
    }

    public List<PaymentEntity> getUnconfirm_list() {
        return unconfirm_list;
    }

    public void setUnconfirm_list(List<PaymentEntity> unconfirm_list) {
        this.unconfirm_list = unconfirm_list;
    }

    public static class YtAuthEntity implements Serializable {
        private String PARTNER_PREAUTH;
        private String MD5_KEY_PREAUTH;
        private String PARTNER;
        private String MD5_KEY;
        private String RSA_PRIVATE;
        private String RSA_YT_PUBLIC;

        public void setPARTNER_PREAUTH(String PARTNER_PREAUTH) {
            this.PARTNER_PREAUTH = PARTNER_PREAUTH;
        }

        public void setMD5_KEY_PREAUTH(String MD5_KEY_PREAUTH) {
            this.MD5_KEY_PREAUTH = MD5_KEY_PREAUTH;
        }

        public void setPARTNER(String PARTNER) {
            this.PARTNER = PARTNER;
        }

        public void setMD5_KEY(String MD5_KEY) {
            this.MD5_KEY = MD5_KEY;
        }

        public void setRSA_PRIVATE(String RSA_PRIVATE) {
            this.RSA_PRIVATE = RSA_PRIVATE;
        }

        public void setRSA_YT_PUBLIC(String RSA_YT_PUBLIC) {
            this.RSA_YT_PUBLIC = RSA_YT_PUBLIC;
        }

        public String getPARTNER_PREAUTH() {
            return PARTNER_PREAUTH;
        }

        public String getMD5_KEY_PREAUTH() {
            return MD5_KEY_PREAUTH;
        }

        public String getPARTNER() {
            return PARTNER;
        }

        public String getMD5_KEY() {
            return MD5_KEY;
        }

        public String getRSA_PRIVATE() {
            return RSA_PRIVATE;
        }

        public String getRSA_YT_PUBLIC() {
            return RSA_YT_PUBLIC;
        }
    }
}
