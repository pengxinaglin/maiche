package com.haoche51.settlement.cashiers;

import java.io.Serializable;

/**
 * Created by yangming on 2016/1/13.
 */
public class PospayEntity implements Serializable {

	/**
	 * trade_number : 20160113001806
	 * oid_trader : 123456
	 * name_goods : 买家定金
	 * yt_auth : {"PARTNER_PREAUTH":"201407032000003743","MD5_KEY_PREAUTH":"201407032000003743_MD5","PARTNER":"201307232000003510","MD5_KEY":"2121","RSA_PRIVATE":"MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAMlGNh/WsyZSYnQcHd9t5qUkhcOhuQmozrAY9DM4+7fhpbJenmYee4chREW4RB3m95+vsz9DqCq61/dIOoLK940/XmhKkuVjfPqHJpoyHJsHcMYy2bXCd2fI++rERdXtYm0Yj2lFbq1aEAckciutyVZcAIHQoZsFwF8l6oS6DmZRAgMBAAECgYAApq1+JN+nfBS9c2nVUzGvzxJvs5I5qcYhY7NGhySpT52NmijBA9A6e60Q3Ku7vQeICLV3uuxMVxZjwmQOEEIEvXqauyYUYTPgqGGcwYXQFVI7raHa0fNMfVWLMHgtTScoKVXRoU3re6HaXB2z5nUR//NE2OLdGCv0ApaJWEJMwQJBAPWoD/Cm/2LpZdfh7oXkCH+JQ9LoSWGpBDEKkTTzIqU9USNHOKjth9vWagsR55aAn2ImG+EPS+wa9xFTVDk/+WUCQQDRv8B/lYZD43KPi8AJuQxUzibDhpzqUrAcu5Xr3KMvcM4Us7QVzXqP7sFc7FJjZSTWgn3mQqJg1X0pqpdkQSB9AkBFs2jKbGe8BeM6rMVDwh7TKPxQhE4F4rHoxEnND0t+PPafnt6pt7O7oYu3Fl5yao5Oh+eTJQbyt/fwN4eHMuqtAkBx/ob+UCNyjhDbFxa9sgaTqJ7EsUpix6HTW9f1IirGQ8ac1bXQC6bKxvXsLLvyLSxCMRV/qUNa4Wxu0roI0KR5AkAZqsY48Uf/XsacJqRgIvwODstC03fgbml890R0LIdhnwAvE4sGnC9LKySRKmEMo8PuDhI0dTzaV0AbvXnsfDfp","RSA_YT_PUBLIC":"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCSS/DiwdCf/aZsxxcacDnooGph3d2JOj5GXWi+q3gznZauZjkNP8SKl3J2liP0O6rU/Y/29+IUe+GTMhMOFJuZm1htAtKiu5ekW0GlBMWxf4FPkYlQkPE0FtaoMP3gYfh+OwI+fIRrpW3ySn3mScnc6Z700nU/VYrRkfcSCbSnRwIDAQAB"}
	 */

	private String trade_number;
	private String oid_trader;
	private String name_goods;
	/**
	 * PARTNER_PREAUTH : 201407032000003743
	 * MD5_KEY_PREAUTH : 201407032000003743_MD5
	 * PARTNER : 201307232000003510
	 * MD5_KEY : 2121
	 * RSA_PRIVATE : MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAMlGNh/WsyZSYnQcHd9t5qUkhcOhuQmozrAY9DM4+7fhpbJenmYee4chREW4RB3m95+vsz9DqCq61/dIOoLK940/XmhKkuVjfPqHJpoyHJsHcMYy2bXCd2fI++rERdXtYm0Yj2lFbq1aEAckciutyVZcAIHQoZsFwF8l6oS6DmZRAgMBAAECgYAApq1+JN+nfBS9c2nVUzGvzxJvs5I5qcYhY7NGhySpT52NmijBA9A6e60Q3Ku7vQeICLV3uuxMVxZjwmQOEEIEvXqauyYUYTPgqGGcwYXQFVI7raHa0fNMfVWLMHgtTScoKVXRoU3re6HaXB2z5nUR//NE2OLdGCv0ApaJWEJMwQJBAPWoD/Cm/2LpZdfh7oXkCH+JQ9LoSWGpBDEKkTTzIqU9USNHOKjth9vWagsR55aAn2ImG+EPS+wa9xFTVDk/+WUCQQDRv8B/lYZD43KPi8AJuQxUzibDhpzqUrAcu5Xr3KMvcM4Us7QVzXqP7sFc7FJjZSTWgn3mQqJg1X0pqpdkQSB9AkBFs2jKbGe8BeM6rMVDwh7TKPxQhE4F4rHoxEnND0t+PPafnt6pt7O7oYu3Fl5yao5Oh+eTJQbyt/fwN4eHMuqtAkBx/ob+UCNyjhDbFxa9sgaTqJ7EsUpix6HTW9f1IirGQ8ac1bXQC6bKxvXsLLvyLSxCMRV/qUNa4Wxu0roI0KR5AkAZqsY48Uf/XsacJqRgIvwODstC03fgbml890R0LIdhnwAvE4sGnC9LKySRKmEMo8PuDhI0dTzaV0AbvXnsfDfp
	 * RSA_YT_PUBLIC : MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCSS/DiwdCf/aZsxxcacDnooGph3d2JOj5GXWi+q3gznZauZjkNP8SKl3J2liP0O6rU/Y/29+IUe+GTMhMOFJuZm1htAtKiu5ekW0GlBMWxf4FPkYlQkPE0FtaoMP3gYfh+OwI+fIRrpW3ySn3mScnc6Z700nU/VYrRkfcSCbSnRwIDAQAB
	 */

	private YtAuthEntity yt_auth;

	public void setTrade_number(String trade_number) {
		this.trade_number = trade_number;
	}

	public void setOid_trader(String oid_trader) {
		this.oid_trader = oid_trader;
	}

	public void setName_goods(String name_goods) {
		this.name_goods = name_goods;
	}

	public void setYt_auth(YtAuthEntity yt_auth) {
		this.yt_auth = yt_auth;
	}

	public String getTrade_number() {
		return trade_number;
	}

	public String getOid_trader() {
		return oid_trader;
	}

	public String getName_goods() {
		return name_goods;
	}

	public YtAuthEntity getYt_auth() {
		return yt_auth;
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
