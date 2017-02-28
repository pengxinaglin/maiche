package com.haoche51.sales.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.haoche51.sales.R;
import com.haoche51.sales.constants.TaskConstants;

import java.io.File;
import java.util.Map;

import me.iwf.photopicker.utils.PhotoPickerIntent;

/**
 * Created by yangming on 2015/12/21.
 */
public class PopupWindowUtil {
  public static String photo_path;

  /**
   * Array(-1)
   * -1 代表不限
   * 1; //黑色
   * 2; //白色
   * 3; //银灰色
   * 4; //深灰色
   * 5; //红色
   * 6; //蓝色
   * 7; //黄色
   * 8; //橙色
   * 9; //金色/棕色
   * 10; //绿色
   * 11; //其他
   *
   * @param context
   * @param view
   * @param checkBoxCheckedListener
   * @return
   */
  public static PopupWindow vehicleColorPopUpWindow(Context context, View view, Map<Integer, String> colorMap, final OnCheckBoxCheckedListener checkBoxCheckedListener) {

    final Map<Integer, String> mColorMap = colorMap;

    LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    final View viewPopUpWindow = layoutInflater.inflate(R.layout.layout_vehicle_sub_structure_pop_window, null);

    //checkbox onClick
    CheckBox checkBoxUnlimited = (CheckBox) viewPopUpWindow.findViewById(R.id.cb_vehicle_color_unlimited);
    final CheckBox checkBoxBlack = (CheckBox) viewPopUpWindow.findViewById(R.id.cb_vehicle_color_black);
    final CheckBox checkBoxWhite = (CheckBox) viewPopUpWindow.findViewById(R.id.cb_vehicle_color_white);
    final CheckBox checkBoxSilver = (CheckBox) viewPopUpWindow.findViewById(R.id.cb_vehicle_color_silver);
    final CheckBox checkBoxGray = (CheckBox) viewPopUpWindow.findViewById(R.id.cb_vehicle_color_gray);
    final CheckBox checkBoxRed = (CheckBox) viewPopUpWindow.findViewById(R.id.cb_vehicle_color_red);
    final CheckBox checkBoxBlue = (CheckBox) viewPopUpWindow.findViewById(R.id.cb_vehicle_color_blue);
    final CheckBox checkBoxGreen = (CheckBox) viewPopUpWindow.findViewById(R.id.cb_vehicle_color_green);
    final CheckBox checkBoxYellow = (CheckBox) viewPopUpWindow.findViewById(R.id.cb_vehicle_color_yellow);
    final CheckBox checkBoxGolden = (CheckBox) viewPopUpWindow.findViewById(R.id.cb_vehicle_color_golden);
    final CheckBox checkBoxOrige = (CheckBox) viewPopUpWindow.findViewById(R.id.cb_vehicle_color_orange);
    TextView textView = (TextView) viewPopUpWindow.findViewById(R.id.tv_vehicle_color_ok);

    final PopupWindow popupWindow = new PopupWindow(viewPopUpWindow, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


    textView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        popupWindow.dismiss();
        checkBoxCheckedListener.checkBoxChecked(mColorMap);
      }
    });

    checkBoxUnlimited.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        //选中--其他选项取消选择，不可选；
        if (isChecked) {
          checkBoxBlack.setChecked(false);
          checkBoxWhite.setChecked(false);
          checkBoxSilver.setChecked(false);
          checkBoxGray.setChecked(false);
          checkBoxRed.setChecked(false);
          checkBoxBlue.setChecked(false);
          checkBoxGreen.setChecked(false);
          checkBoxYellow.setChecked(false);
          checkBoxGolden.setChecked(false);
          checkBoxOrige.setChecked(false);

          checkBoxBlack.setClickable(false);
          checkBoxWhite.setClickable(false);
          checkBoxSilver.setClickable(false);
          checkBoxGray.setClickable(false);
          checkBoxRed.setClickable(false);
          checkBoxBlue.setClickable(false);
          checkBoxGreen.setClickable(false);
          checkBoxYellow.setClickable(false);
          checkBoxGolden.setClickable(false);
          checkBoxOrige.setClickable(false);

        } else {

          checkBoxBlack.setClickable(true);
          checkBoxWhite.setClickable(true);
          checkBoxSilver.setClickable(true);
          checkBoxGray.setClickable(true);
          checkBoxRed.setClickable(true);
          checkBoxBlue.setClickable(true);
          checkBoxGreen.setClickable(true);
          checkBoxYellow.setClickable(true);
          checkBoxGolden.setClickable(true);
          checkBoxOrige.setClickable(true);

        }
        setColorMapValue(-1, DisplayUtils.getVehicleColor(-1), isChecked, mColorMap);
      }
    });

    for (Integer key : colorMap.keySet()) {
      switch (key) {
        case -1:
          checkBoxUnlimited.setChecked(true);
          break;
        case 1:
          checkBoxBlack.setChecked(true);
          break;
        case 2:
          checkBoxWhite.setChecked(true);
          break;
        case 3:
          checkBoxSilver.setChecked(true);
          break;
        case 4:
          checkBoxGray.setChecked(true);
          break;
        case 5:
          checkBoxRed.setChecked(true);
          break;
        case 6:
          checkBoxBlue.setChecked(true);
          break;
        case 7:
          checkBoxYellow.setChecked(true);
          break;
        case 8:
          checkBoxOrige.setChecked(true);
          break;
        case 9:
          checkBoxGolden.setChecked(true);
          break;
        case 10:
          checkBoxGreen.setChecked(true);
          break;
      }
    }

    checkBoxBlack.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        setColorMapValue(1, DisplayUtils.getVehicleColor(1), isChecked, mColorMap);
      }
    });
    checkBoxWhite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        setColorMapValue(2, DisplayUtils.getVehicleColor(2), isChecked, mColorMap);
      }
    });
    checkBoxSilver.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        setColorMapValue(3, DisplayUtils.getVehicleColor(3), isChecked, mColorMap);
      }
    });
    checkBoxGray.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        setColorMapValue(4, DisplayUtils.getVehicleColor(4), isChecked, mColorMap);
      }
    });
    checkBoxRed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        setColorMapValue(5, DisplayUtils.getVehicleColor(5), isChecked, mColorMap);
      }
    });
    checkBoxBlue.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        setColorMapValue(6, DisplayUtils.getVehicleColor(6), isChecked, mColorMap);
      }
    });
    checkBoxGreen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        setColorMapValue(10, DisplayUtils.getVehicleColor(10), isChecked, mColorMap);
      }
    });
    checkBoxYellow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        setColorMapValue(7, DisplayUtils.getVehicleColor(7), isChecked, mColorMap);
      }
    });
    checkBoxGolden.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        setColorMapValue(9, DisplayUtils.getVehicleColor(9), isChecked, mColorMap);
      }
    });
    checkBoxOrige.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        setColorMapValue(8, DisplayUtils.getVehicleColor(8), isChecked, mColorMap);
      }
    });


    popupWindow.setFocusable(true);
    popupWindow.setOutsideTouchable(false);
//    popupWindow.setBackgroundDrawable(new ColorDrawable(0));//不加这一句，点击outside区域，popwindow不消失，但是back按键不起作用了
    popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, 0, (DisplayUtils.getScreenHeightPixels() - popupWindow.getHeight()) / 2);

    return popupWindow;
  }

  /**
   * 车身结构的popupwindow
   * Array(-1)
   * -1 代表不限
   * 1; //两厢
   * 2; //三厢
   * 3; //SUV
   * 4; //MPV
   * 5; //旅行车
   * 6; //跑车
   * 7; //皮卡
   * 8; //面包车
   *
   * @param context
   * @param view
   * @return
   */
  public static PopupWindow vehicleBodyPopUpWindow(Context context, View view, Map<Integer, String> bodyMap, final OnCheckBoxCheckedListener checkBoxCheckedListener) {
    final Map<Integer, String> mBodyMap = bodyMap;
    LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View carbodyPopUpWindow = layoutInflater.inflate(R.layout.vehicele_subscribe_carbody_select_pop_window_layout, null);
    TextView textView = (TextView) carbodyPopUpWindow.findViewById(R.id.tv_carbody_popu_sure);
    final CheckBox checkBoxLimit = (CheckBox) carbodyPopUpWindow.findViewById(R.id.cb_carbody_unlimited);
    final CheckBox checkBoxTwoFront = (CheckBox) carbodyPopUpWindow.findViewById(R.id.cb_carbody_two_fronts);
    final CheckBox checkBoxThreeFront = (CheckBox) carbodyPopUpWindow.findViewById(R.id.cb_carbody_three_fronts);
    final CheckBox checkBoxSuv = (CheckBox) carbodyPopUpWindow.findViewById(R.id.cb_carbody_suv);
    final CheckBox checkBoxMpv = (CheckBox) carbodyPopUpWindow.findViewById(R.id.cb_carbody_mpv);
    final CheckBox checkBoxWagon = (CheckBox) carbodyPopUpWindow.findViewById(R.id.cb_carbody_wagon);
    final CheckBox checkBoxSports = (CheckBox) carbodyPopUpWindow.findViewById(R.id.cb_carbody_sports);
    final CheckBox checkBoxPickup = (CheckBox) carbodyPopUpWindow.findViewById(R.id.cb_carbody_pickup);
    final CheckBox checkBoxMinibus = (CheckBox) carbodyPopUpWindow.findViewById(R.id.cb_carbody_minibus);

    final PopupWindow popupWindow = new PopupWindow(carbodyPopUpWindow, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    textView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        popupWindow.dismiss();
        checkBoxCheckedListener.checkBoxChecked(mBodyMap);
      }
    });
    checkBoxLimit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        //选中--其他选项取消选择，不可选；
        if (isChecked) {
          checkBoxTwoFront.setChecked(false);
          checkBoxThreeFront.setChecked(false);
          checkBoxSuv.setChecked(false);
          checkBoxMpv.setChecked(false);
          checkBoxWagon.setChecked(false);
          checkBoxSports.setChecked(false);
          checkBoxPickup.setChecked(false);
          checkBoxMinibus.setChecked(false);

          checkBoxTwoFront.setClickable(false);
          checkBoxThreeFront.setClickable(false);
          checkBoxSuv.setClickable(false);
          checkBoxMpv.setClickable(false);
          checkBoxWagon.setClickable(false);
          checkBoxSports.setClickable(false);
          checkBoxPickup.setClickable(false);
          checkBoxMinibus.setClickable(false);

        } else {

          checkBoxTwoFront.setClickable(true);
          checkBoxThreeFront.setClickable(true);
          checkBoxSuv.setClickable(true);
          checkBoxMpv.setClickable(true);
          checkBoxWagon.setClickable(true);
          checkBoxSports.setClickable(true);
          checkBoxPickup.setClickable(true);
          checkBoxMinibus.setClickable(true);

        }
        setBodyMapValue(-1, DisplayUtils.getVehicleStructure(-1), isChecked, mBodyMap);
      }
    });
    for (Integer key : bodyMap.keySet()) {
      switch (key) {
        case -1:
          checkBoxLimit.setChecked(true);
          break;
        case 1:
          checkBoxTwoFront.setChecked(true);
          break;
        case 2:
          checkBoxThreeFront.setChecked(true);
          break;
        case 3:
          checkBoxSuv.setChecked(true);
          break;
        case 4:
          checkBoxMpv.setChecked(true);
          break;
        case 5:
          checkBoxWagon.setChecked(true);
          break;
        case 6:
          checkBoxSports.setChecked(true);
          break;
        case 7:
          checkBoxPickup.setChecked(true);
          break;
        case 8:
          checkBoxMinibus.setChecked(true);
          break;
      }
    }

    checkBoxTwoFront.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        setBodyMapValue(1, DisplayUtils.getVehicleStructure(1), isChecked, mBodyMap);
      }
    });
    checkBoxThreeFront.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        setBodyMapValue(2, DisplayUtils.getVehicleStructure(2), isChecked, mBodyMap);
      }
    });
    checkBoxSuv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        setBodyMapValue(3, DisplayUtils.getVehicleStructure(3), isChecked, mBodyMap);
      }
    });
    checkBoxMpv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        setBodyMapValue(4, DisplayUtils.getVehicleStructure(4), isChecked, mBodyMap);
      }
    });
    checkBoxWagon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        setBodyMapValue(5, DisplayUtils.getVehicleStructure(5), isChecked, mBodyMap);
      }
    });
    checkBoxSports.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        setBodyMapValue(6, DisplayUtils.getVehicleStructure(6), isChecked, mBodyMap);
      }
    });
    checkBoxPickup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        setBodyMapValue(7, DisplayUtils.getVehicleStructure(7), isChecked, mBodyMap);
      }
    });
    checkBoxMinibus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        setBodyMapValue(8, DisplayUtils.getVehicleStructure(8), isChecked, mBodyMap);
      }
    });

    popupWindow.setFocusable(true);
    popupWindow.setOutsideTouchable(false);
//    popupWindow.setBackgroundDrawable(new BitmapDrawable());
    popupWindow.showAsDropDown(view, 0, 0);
    return popupWindow;
  }


  /**
   * color checkbox checked add map value
   *
   * @param enumValue
   * @param enumStr
   * @param isChecked
   * @param colorMap
   * @return
   */
  private static Map<Integer, String> setColorMapValue(int enumValue, String enumStr, boolean isChecked, Map<Integer, String> colorMap) {
    if (isChecked) {//选中--add
      if (!colorMap.containsKey(enumValue)) {
        colorMap.put(enumValue, enumStr);
      }
    } else {//取消选中--remove
      colorMap.remove(enumValue);
    }
    return colorMap;
  }

  private static Map<Integer, String> setBodyMapValue(int enumValue, String enumStr, boolean isChecked, Map<Integer, String> bodyMap) {
    if (isChecked) {//选中--add
      if (!bodyMap.containsKey(enumValue)) {
        bodyMap.put(enumValue, enumStr);
      }
    } else {//取消选中--remove
      bodyMap.remove(enumValue);
    }
    return bodyMap;
  }

  public interface OnCheckBoxCheckedListener {
    void checkBoxChecked(Map<Integer, String> checkedMap);
  }

  public static PopupWindow UploadPicturePopup(final Activity activity, View v) {
    LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View view = layoutInflater.inflate(R.layout.layout_transaction_option, null);
    LinearLayout album_to_choose = (LinearLayout) view.findViewById(R.id.album_to_choose);
//    LinearLayout take_a_picture = (LinearLayout) view.findViewById(R.id.take_a_picture);
    LinearLayout update_cancel = (LinearLayout) view.findViewById(R.id.update_cancel);
    final PopupWindow popupView = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    popupView.setOnDismissListener(new PopupWindow.OnDismissListener() {
      @Override
      public void onDismiss() {
        backgroundAlpha(activity, 1.0f);
      }
    });
    update_cancel.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        popupView.dismiss();

      }
    });
    album_to_choose.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        PhotoPickerIntent intent = new PhotoPickerIntent(activity);
        intent.setPhotoCount(50);//设置每次可以选着50张图片
        intent.setShowCamera(false);
        activity.startActivityForResult(intent, TaskConstants.PREPAY_SELECT_PHOTO);
        popupView.dismiss();
      }

    });
//    take_a_picture.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View v) {
//        intentCamera(activity);
//        popupView.dismiss();
//      }
//    });
    popupView.setFocusable(true);
    popupView.setOutsideTouchable(true);
    backgroundAlpha(activity, 0.5f);
    popupView.showAtLocation(view, Gravity.BOTTOM, 0, 0);
    return popupView;
  }

  /**
   * 设置背景透明度
   *
   * @param activity
   * @param alpha
   */
  private static void backgroundAlpha(Activity activity, float alpha) {
    WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
    lp.alpha = alpha;
    activity.getWindow().setAttributes(lp);
  }

  /**
   * 打开相机
   *
   * @param activity
   */
  public static void intentCamera(Activity activity) {
    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
    File file = new File(Environment.getExternalStorageDirectory() + File.separator + "HCHaochebang" + File.separator + "Images");
    if (!file.exists()) {
      file.mkdirs();
    }
    StringBuffer sb = new StringBuffer();
    photo_path = sb.append(Environment.getExternalStorageDirectory()).append("/HCHaochebang/Images/").append("cameraImg").append(System.currentTimeMillis()).append(".jpg").toString();
    Uri mUri = Uri.fromFile(new File(photo_path));
    cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mUri);
    activity.startActivityForResult(cameraIntent, TaskConstants.PREPAY_TAKE_PHOTO);
  }


}