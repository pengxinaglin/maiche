package com.haoche51.sales.net;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haoche51.sales.GlobalData;
import com.haoche51.sales.LoginToken;
import com.haoche51.sales.User;
import com.haoche51.sales.activity.SubsidyInfoEntity;
import com.haoche51.sales.entity.CommonBooleanEntity;
import com.haoche51.sales.entity.ExtInfo;
import com.haoche51.sales.dao.VehicleBrandEntity;
import com.haoche51.sales.hccustomer.OverViewEntity;
import com.haoche51.sales.hccustomer.VehicleMatchEntity;
import com.haoche51.sales.hcrecommend.BuyerCluesEntity;
import com.haoche51.sales.hcmessage.MessageEntity;
import com.haoche51.sales.hcrecommend.CheckVehicleRecomEntity;
import com.haoche51.sales.hcrecommend.CityInfo;
import com.haoche51.sales.hcrecommend.PurchaseCluesEntity;
import com.haoche51.sales.hccustomer.RevisitEntity;
import com.haoche51.sales.hcshare.WxShareEntity;
import com.haoche51.sales.hctransaction.BuyerEntity;
import com.haoche51.sales.hctransaction.BuyerRevisitEntity;
import com.haoche51.sales.hctransaction.Coupon;
import com.haoche51.sales.hctransaction.CouponEntity;
import com.haoche51.sales.hctransaction.IntendedVehicleEntity;
import com.haoche51.sales.hctransaction.PhoneRecordEntity;
import com.haoche51.sales.hctransaction.SalesInfoEntity;
import com.haoche51.sales.hctransaction.TransactionHighSeasEntity;
import com.haoche51.sales.hctransaction.TransactionRecordEntity;
import com.haoche51.sales.hctransaction.TransactionTaskEntity;
import com.haoche51.sales.hctransaction.TransactionTaskShortEntity;
import com.haoche51.sales.hctransaction.VehicleRevisitEntity;
import com.haoche51.sales.hctransaction.VehicleSeriesEntity;
import com.haoche51.sales.hctransfer.TransferEntity;
import com.haoche51.sales.hctransfer.TransferSimpleEntity;
import com.haoche51.sales.hcvehiclerecommend.VehicleRecommentEntity;
import com.haoche51.sales.service.UpdateVersionEntity;
import com.haoche51.sales.workreport.WorkReportEntity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 解析json 在界面中只操作javabean
 *
 * @author lightman_mac
 */
public class HCJsonParse {

	public static Gson mGson = new Gson();

	/**
	 * 解析登陆
	 */
	public static User parseLogin(String result) {
		Type type = new TypeToken<User>() {
		}.getType();
		User user;
		try {
			user = mGson.fromJson(result, type);
		} catch (Exception e) {
			user = null;
		}
		return user;
	}

	/**
	 * 解析过户列表(新)
	 *
	 * @param respStr
	 * @return
	 */
	public static List<TransferSimpleEntity> parseTransferListData(String respStr) {
		List<TransferSimpleEntity> transferEntities;
		try {
			transferEntities = mGson.fromJson(respStr, new TypeToken<List<TransferSimpleEntity>>() {
			}.getType());
		} catch (Exception e) {
			transferEntities = new ArrayList<TransferSimpleEntity>();
		}
		return transferEntities;
	}


	/**
	 * 解析过户详情数据
	 *
	 * @param respStr resp
	 * @return TransferEntity
	 */
	public static TransferEntity parseTransferDetail(String respStr) {
		TransferEntity transferEntity;
		try {
			transferEntity = mGson.fromJson(respStr, new TypeToken<TransferEntity>() {
			}.getType());
		} catch (Exception e) {
			e.printStackTrace();
			transferEntity = null;
		}
		return transferEntity;
	}


	/**
	 * 解析extInfo
	 *
	 * @param respStr resp
	 * @return TransferEntity
	 */
	public static ExtInfo parseExtInfo(String respStr) {
		ExtInfo extInfo;
		try {
			extInfo = mGson.fromJson(respStr, new TypeToken<ExtInfo>() {
			}.getType());
		} catch (Exception e) {
			extInfo = null;
		}
		return extInfo;
	}


	/**
	 * 解析过户列表
	 *
	 * @param respStr
	 * @return
	 */
	public static List<TransferEntity> parseTransferData(String respStr) {
		List<TransferEntity> transferEntities;
		try {
			transferEntities = mGson.fromJson(respStr, new TypeToken<List<TransferEntity>>() {
			}.getType());
		} catch (Exception e) {
			transferEntities = new ArrayList<TransferEntity>();
		}
		return transferEntities;
	}

	/**
	 * 修改过户时间地址返回
	 *
	 * @param respStr
	 * @return
	 */
	public static CommonBooleanEntity parseTransferModifyData(String respStr) {
		CommonBooleanEntity entity;
		try {
			entity = mGson.fromJson(respStr, CommonBooleanEntity.class);
		} catch (Exception e) {
			return null;
		}
		return entity;
	}

	/**
	 * 提交过户信息返回
	 */
	public static CommonBooleanEntity parseTransferCommitData(String respStr) {
		return mGson.fromJson(respStr, CommonBooleanEntity.class);
	}


	/**
	 * 解析车主回访记录
	 */
	public static List<BuyerRevisitEntity> parseBuyerCallbackResult(String result) {
		Type type = new TypeToken<List<BuyerRevisitEntity>>() {
		}.getType();
		List<BuyerRevisitEntity> list;
		try {
			list = mGson.fromJson(result, type);
		} catch (Exception e) {
			list = null;
		}
		return list;
	}

	/**
	 * 解析车主回访记录
	 */
	public static List<VehicleRevisitEntity> parseVehicleRevisitResult(String result) {
		Type type = new TypeToken<List<VehicleRevisitEntity>>() {
		}.getType();
		List<VehicleRevisitEntity> list;
		try {
			list = mGson.fromJson(result, type);
		} catch (Exception e) {
			list = null;
		}
		return list;
	}

	/**
	 * 解析线索列表
	 *
	 * @param result
	 * @return
	 */
	public static List<BuyerCluesEntity> parseBuyerCluesResult(String result) {
		Type type = new TypeToken<List<BuyerCluesEntity>>() {
		}.getType();
		List<BuyerCluesEntity> buyerCluesList;
		try {
			buyerCluesList = mGson.fromJson(result, type);
		} catch (Exception e) {
			buyerCluesList = null;
		}
		return buyerCluesList;
	}

	/**
	 * 解析线索列表
	 *
	 * @param result
	 * @return
	 */
	public static List<CheckVehicleRecomEntity> parseCheckCluesResult(String result) {
		Type type = new TypeToken<List<CheckVehicleRecomEntity>>() {
		}.getType();
		List<CheckVehicleRecomEntity> cluesList;
		try {
			cluesList = mGson.fromJson(result, type);
		} catch (Exception e) {
			cluesList = null;
		}
		if (cluesList == null) cluesList = new ArrayList<>();
		return cluesList;
	}

	/**
	 * get view task result
	 *
	 * @param result
	 * @return
	 */
	public static List<TransactionTaskShortEntity> parseGetTransTaskShortEntitys(String result) {
		Type type = new TypeToken<ArrayList<TransactionTaskShortEntity>>() {
		}.getType();
		List<TransactionTaskShortEntity> taskList;
		try {
			taskList = mGson.fromJson(result, type);

		} catch (Exception e) {
			taskList = null;
		}
		return taskList;
	}

	/**
	 * get view task result
	 *
	 * @param result
	 * @return
	 */
	public static List<RevisitEntity> parseRevisitList(String result) {
		Type type = new TypeToken<ArrayList<RevisitEntity>>() {
		}.getType();
		List<RevisitEntity> taskList;
		try {
			taskList = mGson.fromJson(result, type);

		} catch (Exception e) {
			taskList = null;
		}
		return taskList;
	}


	/**
	 * get view task failed result
	 *
	 * @param result
	 * @return
	 */
	public static List<BuyerEntity> parseGetTransFailBuyerEntitys(String result) {
		Type type = new TypeToken<ArrayList<BuyerEntity>>() {
		}.getType();
		List<BuyerEntity> taskList;
		try {
			taskList = mGson.fromJson(result, type);

		} catch (Exception e) {
			taskList = null;
		}
		return taskList;
	}

	/**
	 * get view task failed result
	 *
	 * @param result
	 * @return
	 */
	public static List<TransactionHighSeasEntity> parseGetTransHighSeasEntitys(String result) {
		Type type = new TypeToken<ArrayList<TransactionHighSeasEntity>>() {
		}.getType();
		List<TransactionHighSeasEntity> taskList;
		try {
			taskList = mGson.fromJson(result, type);

		} catch (Exception e) {
			taskList = null;
		}
		return taskList;
	}

	/**
	 * 解析买家看车记录
	 */
	public static List<TransactionRecordEntity> parseBuyerTransHistoryInfos(String result) {
		Type type = new TypeToken<ArrayList<TransactionRecordEntity>>() {
		}.getType();
		List<TransactionRecordEntity> taskList;
		try {
			taskList = mGson.fromJson(result, type);

		} catch (Exception e) {
			taskList = null;
		}
		return taskList;

	}

	/**
	 * 解析车辆订购选择礼品
	 */
	public static List<Coupon> parseCouponResult(String result) {
		Type type = new TypeToken<ArrayList<Coupon>>() {
		}.getType();
		List<Coupon> list;
		try {
			list = mGson.fromJson(result, type);
		} catch (Exception e) {
			list = null;
		}
		return list;
	}

	/**
	 * 解析优惠券列表
	 */
	public static List<CouponEntity> parseUserCoupons(String result) {
		Type type = new TypeToken<ArrayList<CouponEntity>>() {
		}.getType();
		List<CouponEntity> list;
		try {
			list = mGson.fromJson(result, type);
		} catch (Exception e) {
			list = null;
		}
		return list;
	}

	/**
	 * 解析兑换的一个优惠券
	 */
	public static CouponEntity parseUserCoupon(String result) {
		Type type = new TypeToken<ArrayList<CouponEntity>>() {
		}.getType();
		CouponEntity coupon;
		List<CouponEntity> coupons;
		try {
			coupons = mGson.fromJson(result, type);
			coupon = coupons.get(0);
		} catch (Exception e) {
			e.printStackTrace();
			coupon = null;
		}
		return coupon;
	}

	/**
	 * 解析通知推荐车源
	 */
	public static List<VehicleRecommentEntity> parseSubVehicleList(String result) {
		Type type = new TypeToken<ArrayList<VehicleRecommentEntity>>() {
		}.getType();
		List<VehicleRecommentEntity> list;
		try {
			list = mGson.fromJson(result, type);
		} catch (Exception e) {
			list = null;
		}
		return list;
	}


	/**
	 * 解析意向车源
	 */
	public static List<IntendedVehicleEntity> parseDemandList(String result) {
		Type type = new TypeToken<ArrayList<IntendedVehicleEntity>>() {
		}.getType();
		List<IntendedVehicleEntity> list;
		try {
			list = mGson.fromJson(result, type);
		} catch (Exception e) {
			list = null;
		}
		return list;
	}

	/**
	 * 解析匹配车源
	 */
	public static List<VehicleMatchEntity> parseVehicleMatchList(String result) {
		Type type = new TypeToken<ArrayList<VehicleMatchEntity>>() {
		}.getType();
		List<VehicleMatchEntity> list;
		try {
			list = mGson.fromJson(result, type);
		} catch (Exception e) {
			list = null;
		}
		return list;
	}

	/**
	 * 解析消息list
	 */
	public static List<MessageEntity> parseMessageLists(String result) {
		Type type = new TypeToken<ArrayList<MessageEntity>>() {
		}.getType();
		List<MessageEntity> list;
		try {
			list = mGson.fromJson(result, type);
		} catch (Exception e) {
			list = null;
		}
		return list;
	}

	/**
	 * 解析录音list
	 */
	public static List<PhoneRecordEntity> parseRecordLists(String result) {
		Type type = new TypeToken<ArrayList<PhoneRecordEntity>>() {
		}.getType();
		List<PhoneRecordEntity> list;
		try {
			list = mGson.fromJson(result, type);
		} catch (Exception e) {
			list = null;
		}
		return list;
	}

	/**
	 * 解析看车任务
	 */
	public static TransactionTaskEntity parseTransTaskEntity(String result) {
		TransactionTaskEntity entity;
		try {
			entity = mGson.fromJson(result, TransactionTaskEntity.class);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return entity;
	}


	/**
	 * 解析品牌列表
	 *
	 * @param result
	 * @return
	 */
	public static ArrayList<VehicleBrandEntity> parseVehicleBrandList(String result) {
		Type type = new TypeToken<ArrayList<VehicleBrandEntity>>() {
		}.getType();
		ArrayList<VehicleBrandEntity> brandList;
		try {
			brandList = mGson.fromJson(result, type);
		} catch (Exception e) {
			brandList = null;
		}
		return brandList;
	}

	/**
	 * 解析工作汇报
	 */
	public static WorkReportEntity parseWorkReportDetail(String result) {
		Type type = new TypeToken<WorkReportEntity>() {
		}.getType();

		WorkReportEntity entity;
		try {
			entity = mGson.fromJson(result, type);
		} catch (Exception e) {
			return null;
		}
		return entity;
	}

	/**
	 * 解析门店登录Token
	 */
	public static LoginToken parseStoreLoginToken(String result) {
		Type type = new TypeToken<LoginToken>() {
		}.getType();

		LoginToken loginToken;
		try {
			loginToken = mGson.fromJson(result, type);
			GlobalData.userDataHelper.saveStoreToken(loginToken);
		} catch (Exception e) {
			return null;
		}
		return loginToken;
	}

	/**
	 * 解析车系列表
	 *
	 * @param result
	 * @return
	 */
	public static ArrayList<VehicleSeriesEntity> parseVehicleSeriesList(String result) {
		Type type = new TypeToken<ArrayList<VehicleSeriesEntity>>() {
		}.getType();
		ArrayList<VehicleSeriesEntity> list;
		try {
			list = mGson.fromJson(result, type);
		} catch (Exception e) {
			list = null;
		}
		return list;
	}

	/**
	 * 解析买家线索
	 */
	public static List<BuyerCluesEntity> parseGetBuyerCluesEntitys(String json) {
		Type type = new TypeToken<ArrayList<BuyerCluesEntity>>() {
		}.getType();
		ArrayList<BuyerCluesEntity> list;
		try {
			list = mGson.fromJson(json, type);
		} catch (Exception e) {
			list = null;
		}
		return list;
	}

	/**
	 * 解析回购任务线索
	 */
	public static List<PurchaseCluesEntity> parseGetPurchaseCluesEntitys(String json) {
		Type type = new TypeToken<ArrayList<PurchaseCluesEntity>>() {
		}.getType();
		ArrayList<PurchaseCluesEntity> list;
		try {
			list = mGson.fromJson(json, type);
		} catch (Exception e) {
			list = null;
		}
		return list;
	}

	/**
	 * 解析验车任务线索
	 */
	public static List<CheckVehicleRecomEntity> parseGetCheckVehicleRecomEntitys(String json) {
		Type type = new TypeToken<ArrayList<CheckVehicleRecomEntity>>() {
		}.getType();
		ArrayList<CheckVehicleRecomEntity> list;
		try {
			list = mGson.fromJson(json, type);
		} catch (Exception e) {
			list = null;
		}
		return list;
	}

	/**
	 * 解析当月奖金
	 */
	public static CheckVehicleRecomEntity parseVehicleRecomBonus(String json) {
		Type type = new TypeToken<CheckVehicleRecomEntity>() {
		}.getType();

		CheckVehicleRecomEntity entity;
		try {
			entity = mGson.fromJson(json, type);
		} catch (Exception e) {
			return null;
		}
		return entity;
	}

	/**
	 * 解析薪资
	 */
	public static SubsidyInfoEntity parseSubsidyInfoEntity(String result) {
		SubsidyInfoEntity entity;
		try {
			entity = mGson.fromJson(result, SubsidyInfoEntity.class);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return entity;
	}

	/**
	 * 解析买家线索
	 */
	public static List<CityInfo> parseOnLineCityInfos(String json) {
		Type type = new TypeToken<ArrayList<CityInfo>>() {
		}.getType();
		ArrayList<CityInfo> list;
		try {
			list = mGson.fromJson(json, type);
		} catch (Exception e) {
			list = null;
		}
		return list;
	}

	/**
	 * 解析版本更新
	 */
	public static UpdateVersionEntity parseUpdateVersion(String json) {
		Type type = new TypeToken<UpdateVersionEntity>() {
		}.getType();
		UpdateVersionEntity updateVersionEntity;
		try {
			updateVersionEntity = mGson.fromJson(json, type);
		} catch (Exception e) {
			e.printStackTrace();
			updateVersionEntity = null;
		}
		return updateVersionEntity;
	}

	/**
	 * 解析地销列表
	 */
	public static List<SalesInfoEntity> parseGeSalesInfoEntitys(String json) {
		Type type = new TypeToken<ArrayList<SalesInfoEntity>>() {
		}.getType();
		ArrayList<SalesInfoEntity> list;
		try {
			list = mGson.fromJson(json, type);
		} catch (Exception e) {
			list = null;
		}
		return list;
	}

	/**
	 * 解析微信分享
	 */
	public static WxShareEntity parseWxShareEntity(String json) {
		WxShareEntity entity = null;
		HCHttpResponse response = new HCHttpResponse(new String(json));
		if (response != null) {
			try {
				entity = mGson.fromJson(response.getData(), WxShareEntity.class);
			} catch (Exception e) {
				e.printStackTrace();
				entity = null;
			}
		}
		return entity;
	}

	/**
	 * 解析客户概述
	 */
	public static OverViewEntity parseOverViewEntity(String json) {
		OverViewEntity entity = null;
		try {
			entity = mGson.fromJson(json, OverViewEntity.class);
		} catch (Exception e) {
			entity = null;
		}
		return entity;
	}
}
