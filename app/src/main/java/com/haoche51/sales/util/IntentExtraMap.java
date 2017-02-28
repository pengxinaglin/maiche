package com.haoche51.sales.util;

import java.util.HashMap;
import java.util.Map;

public class IntentExtraMap {
	/**
	 * Intent Extra For Add Id
	 * @param id
	 * @return
	 */
	public static Map<String, Object> putId(int id){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		return map;
	}

	/**
	 * Intent Extra For Add Id
	 *
	 * @param id
	 * @return
	 */
	public static Map<String, Object> putTransactionId(int id) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("trans_id", id);
		return map;
	}
	
	/**
	 * Intent Extra For Add Id and taskId
	 * @param id
	 * @return
	 */
	public static Map<String, Object> putIdAndTaskId(int id,int taskId){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("taskId", taskId);
		return map;
	}
	
	/**
	 * Intent Extra For Add TaskId
	 * @param id
	 * @return
	 */
	public static Map<String, Object> putTaskId(int taskId){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("taskId", taskId);
		return map;
	}
	
	public static Map<String, Object> putSingle(boolean single){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("single", single);
		return map;
	}
	
	public static Map<String, Object> putNoticeDetail(String url){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("url", url);
		return map;
	}
}
