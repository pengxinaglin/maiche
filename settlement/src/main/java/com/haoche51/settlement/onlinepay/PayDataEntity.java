package com.haoche51.settlement.onlinepay;

import com.haoche51.settlement.cashiers.PaymentEntity;

import java.io.Serializable;
import java.util.List;

/**
 * {
 "errno": 0,
 "errmsg": "success",
 "data": {
    "unconfirm_list":
        [{
            "trade_number": "20160418210243",  // 商家订单号
            "name_goods": "快速刷卡" "预收定金" "过户收尾款"   // 商品名称
        }],
    "oid_trader": "201512301000661505",  // 商户号
    "yt_auth": {
        "MD5_KEY": "#knds446",
        "RSA_PRIVATE": "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAK61Egy8TowXAutjcQi6rwpC8l76bLqFG\/LXo4ouaLU
            aHowwX1vYtx38ttjmgsWQYddbE9OpHMZStYhcpTlg83s2oegggTIteqIR\/uYYll0XfzAfpM6iO8M+JjXkD74RPzQwOIV\/CNtt88wNu7ZhAuZHU4
            eJKoAixjuiPiuzi3RJAgMBAAECgYBBjeHS84mJmUzYJD0SqHHrqMknCFJp\/m5JRc6wl89kPQO\/WJs\/bGbxkpTNeFkTZqS\/2QXsguUwKXLCUvk
            QOafCMewFCp7mpQ2CKNZcHteophf\/x6Dxfi7O+nkSTplHEwN3qCeknSmwIs0OMfcZ9t0HVV\/I91qh4fV9bFvslhwarQJBAOFH5r7GkGDgXx\/4U
            zGGjyXO9w2ywXyCpwUcgcK+oldajVw+wm7yD1UumVgZOXvF1jIsgPt2bvGiyjhBtgP6APcCQQDGh7+qSh1WwZmQ5wfxkWsMd6TqFlnRaCXGy2vIKs
            \/qS6TynCz4DxSBfFNwdyvZ\/tadDs7DYhVe6KGVh2V0riS\/AkBzb9KPQ1RML+zOhwKqSBcl+o2h2U1ILfLDrb3YOrMZK+9vq0EadLLipUQyXoqUH
            +YIQ8G8GQ30h8QzW4iXNQ41AkEAo8jMO73HNJKlkWC4MffJO9Dc8e4cjWEBgV7C\/bEsNFvRqgFkLAWs4iJiZ+848zCFd68GrzQU82ayH71C2x7r2
            wJAbY5l5SP6tRQDy1+c+FEW+KmWd18\/z\/uvM+hN8feihMnmlLJt6XgN\/nVeLnDhgUvDi41LqryHecSaNTzVIw+XWQ==",
        "RSA_YT_PUBLIC": "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCSS\/DiwdCf\/aZsxxcacDnooGph3d2JOj5GXWi+q3gznZauZjk
            NP8SKl3J2liP0O6rU\/Y\/29+IUe+GTMhMOFJuZm1htAtKiu5ekW0GlBMWxf4FPkYlQkPE0FtaoMP3gYfh+OwI+fIRrpW3ySn3mScnc6Z700nU\/VYrRkfcSCbSnRwIDAQAB"
        }
    }
 }
 */
public class PayDataEntity implements Serializable {

    // 已完成的账单
    private List<PayListEntity> list;
    // 未完成的账单
    private List<PaymentEntity> unconfirm_list;
    private String oid_trader;
    private PayAuthEntity yt_auth;

    public void setList(List<PayListEntity> list) {
        this.list = list;
    }

    public List<PayListEntity> getList() {
        return list;
    }

    public void setUnconfirm_list(List<PaymentEntity> unconfirm_list) {
        this.unconfirm_list = unconfirm_list;
    }

    public List<PaymentEntity> getUnconfirm_list() {
        return unconfirm_list;
    }

    public void setOid_trader(String oid_trader) {
        this.oid_trader = oid_trader;
    }

    public String getOid_trader() {
        return oid_trader;
    }

    public void setYt_auth(PayAuthEntity yt_auth) {
        this.yt_auth = yt_auth;
    }

    public PayAuthEntity getYt_auth() {
        return yt_auth;
    }
}
