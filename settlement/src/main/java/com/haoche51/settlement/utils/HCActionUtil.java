package com.haoche51.settlement.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.haoche51.settlement.entity.Serializable.SerializableMap;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

public class HCActionUtil {


    public static void startActivity(Context context, Class<?> activity, Map<String, Object> map) {
        Intent intent = new Intent(context, activity);
        SerializableMap tmpmap = new SerializableMap();
        tmpmap.setMap(map);
        Bundle bundle = new Bundle();
        bundle.putSerializable("params", tmpmap);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 打开activity
     */
    public static void launchActivity(Context context, Class<?> activity, Map<String, Object> extraMap) {
        if (context != null) {
            Intent intent = new Intent(context, activity);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (extraMap != null) {
                Iterator<String> iterator = extraMap.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    Object object = extraMap.get(key);
                    if (object instanceof String) {
                        intent.putExtra(key, (String) object);
                    } else if (object instanceof Boolean) {
                        intent.putExtra(key, (Boolean) object);
                    } else if (object instanceof Float) {
                        intent.putExtra(key, Float.parseFloat(object.toString()));
                    } else if (object instanceof Integer) {
                        intent.putExtra(key, Integer.parseInt(object.toString()));
                    } else if (object instanceof Long) {
                        intent.putExtra(key, Long.parseLong(object.toString()));
                    } else if (object instanceof Serializable) {
                        intent.putExtra(key, (Serializable) object);
                    } else {
                        intent.putExtra(key, object.toString());
                    }
                }
            }
            context.startActivity(intent);
        }
    }


    /**
     * 打开activity
     */
    public static void launchActivityForResult(Activity context, Class<?> activity, Map<String, Object> extraMap, int requestCode) {
        if (context != null) {
            Intent intent = new Intent(context, activity);
            if (extraMap != null) {
                Iterator<String> iterator = extraMap.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    Object object = extraMap.get(key);
                    if (object instanceof String) {
                        intent.putExtra(key, (String) object);
                    } else if (object instanceof Boolean) {
                        intent.putExtra(key, (Boolean) object);
                    } else if (object instanceof Float) {
                        intent.putExtra(key, Float.parseFloat(object.toString()));
                    } else if (object instanceof Integer) {
                        intent.putExtra(key, Integer.parseInt(object.toString()));
                    } else if (object instanceof Long) {
                        intent.putExtra(key, Long.parseLong(object.toString()));
                    } else if (object instanceof Serializable) {
                        intent.putExtra(key, (Serializable) object);
                    } else {
                        intent.putExtra(key, object.toString());
                    }
                }
            }
            context.startActivityForResult(intent, requestCode);
        }
    }


}
