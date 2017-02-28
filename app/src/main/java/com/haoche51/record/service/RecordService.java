package com.haoche51.record.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Process;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.haoche51.record.dao.PhoneRecordDAO;
import com.haoche51.record.entity.PhoneRecordEntity;
import com.haoche51.record.util.AlarmUtil;
import com.haoche51.record.util.RecordUtil;
import com.haoche51.record.util.SupportUtil;
import com.haoche51.record.util.UploadCallLogTask;
import com.haoche51.record.util.UploadRecordFileTask;
import com.haoche51.sales.GlobalData;
import com.haoche51.sales.R;
import com.haoche51.sales.activity.MainPageActivity;
import com.haoche51.sales.net.HCHttpCallback;
import com.haoche51.sales.net.HCHttpResponse;
import com.haoche51.sales.util.HCLogUtil;

import java.io.File;
import java.io.IOException;

/**
 * 录音service,后台常驻,监听通话状态. 通话时长,对方号码
 * <p/>
 * 监听网络变化,如果当前为wifi,并且有录音文件启动上传service上传录音文件
 */
public class RecordService extends Service implements MediaRecorder.OnErrorListener, HCHttpCallback {
    /**
     * 录音文件存储目录
     */
    // private static String mRecordDir;
    private final static String TAG = "RecordService";
    private TelephonyManager mTelManager;
    private PhoneListener mPhoneListener;
    private NetChangeBroadCast mNetChangeReceiver;
    /**
     * 网络变化action
     */
    private final static String ACTION_NET_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";

    /**
     * 记录本机拨打的号码
     */
    private String mDialNumber;

    /**
     * 记录来电号码
     */
    private String mIncomingNumber;

    private MediaRecorder mediaRecorder;

    /**
     * 录音文件
     */
    private File mRecordFile;

    /**
     * 记录文件创建时间
     */
    private long fileCreateTime;

    /**
     * 标识是否正在拨打电话录音
     */
    private boolean isDialRecording = false;

    /**
     * 标识是否正在接电话录音
     */
    private boolean isInComingRecording = false;

    /**
     * 记录录音时间,时长过短作相应处理
     */
    private long call_duration;

    /**
     * 上传通讯记录
     */
    private UploadCallLogTask callLogDao;

    /**
     * 上传录音文件
     */
    private UploadRecordFileTask mUploadTask;


    private Context mContext;

    /**
     * 发送心跳间隔时间(毫秒)
     */
    private long HEART_BEAT_RATE = 1000 * 60 * 5;
    private int HEART_WHAT = 0X1010;


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        callLogDao = new UploadCallLogTask();
        mUploadTask = new UploadRecordFileTask(GlobalData.mContext);
        listenPhoneState();
        registNetChange();
        setServiceForeground();
        HCLogUtil.d(TAG, "RecordService start Checkiing ");
        AlarmUtil.startCheckAlarm(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        HCLogUtil.d(TAG, "onStartCommand isDial=====" + isDialRecording + ", isInComing======" + isInComingRecording);

        if (RecordUtil.REBOOT_SERVICE_ACTION.equals(intent.getAction())) {
            // 检测到需要重启service,把当前是否处于录制状态带回给CheckRelaunchService
            boolean isRecording = isDialRecording || isInComingRecording;
            if (!isRecording) {
                Intent mIntent = new Intent(this, CheckReLaunchService.class);
                HCLogUtil.d("CheckReLaunchService", "RecordService isRecording " + isRecording);
                mIntent.putExtra("isRecording", false);
                startService(mIntent);
            }
        }

        if (!isDialRecording && !isInComingRecording) {
            if (intent != null) {
                mDialNumber = intent.getStringExtra("dialNum");
                HCLogUtil.d(TAG, "onstartCommand mDialNumber " + mDialNumber);
            }
        }

        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        // 取消注册网络变化监听
        if (mNetChangeReceiver != null) {
            unregisterReceiver(mNetChangeReceiver);
        }
        AlarmUtil.cancelCheckAlarm(this);
        HCLogUtil.d(TAG, "RecordService onDestroy-----" + Process.myPid());
        RecordUtil.sendStartServiceBroadCast(mContext);
        resetToOriginalStatus();
        super.onDestroy();
        Process.killProcess(Process.myPid());
    }

    /**
     * 监听网络变化
     */
    private void registNetChange() {
        mNetChangeReceiver = new NetChangeBroadCast();
        IntentFilter mInfilter = new IntentFilter(ACTION_NET_CHANGE);
        registerReceiver(mNetChangeReceiver, mInfilter);
    }


    /**
     * 监听网络变化的广播接收者
     */
    private class NetChangeBroadCast extends BroadcastReceiver {
        @SuppressWarnings("deprecation")
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_NET_CHANGE.equals(intent.getAction())) {
                //TODO 当前网络为WiFi/3G/4G并且已经连接
                if (SupportUtil.isCurrentNetWifiOr3gOr4g(mContext)) {
                    HCLogUtil.d(TAG, "receive wifi now " + new java.util.Date(System.currentTimeMillis()).toLocaleString());
                    if (!mUploadTask.isUploadingFile()) {
                        mUploadTask.getUploadToken(null);
                    }
                }

                //当前网络是否可用
                boolean isNetAvailable = SupportUtil.isNetAvailable(mContext);
                if (!isNetAvailable) {
                    mUploadTask.setIsUploadingFile(false);
                }
                HCLogUtil.d(TAG, "receive net changed netAvailable = " + isNetAvailable);
            }
        }
    }

    /**
     * 注册监听器 监听电话状态
     */
    private void listenPhoneState() {
        if (mPhoneListener == null) {
            mPhoneListener = new PhoneListener();
        }
        mTelManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mTelManager.listen(mPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    private final class PhoneListener extends PhoneStateListener {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            try {
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING: // 来电
                        // 过滤正在录音但是来电的情况
                        if (!isDialRecording && !isInComingRecording) {
                            mIncomingNumber = incomingNumber;
                            HCLogUtil.d(TAG, "CALL_STATE_RINGING  incomingNumber=" + incomingNumber);
                        } else {
                            HCLogUtil.d(TAG, "录音中,通话中来电  ---------" + incomingNumber);
                        }
                        break;

                    /***
                     * Device call state:Off-hook. At least one call exists that is
                     * dialing, active, or on hold, and no calls are ringing or
                     * waiting
                     */
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        if (!GlobalData.userDataHelper.isLogin())
                            return;

                        // 过滤正在录音但是来电的情况
                        if (isDialRecording || isInComingRecording)
                            return;//TODO 正在录音，但是有第二个来电，并且接通了第二个来电

                        int delay = !TextUtils.isEmpty(mIncomingNumber) ? 0 : 1000;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                doPhoneOffHook();
                            }
                        }, delay);

                        break;

                    case TelephonyManager.CALL_STATE_IDLE: // 挂掉电话

                        if (GlobalData.userDataHelper.isLogin()) {
                            int lastId = RecordUtil.getLastUploadRecordID();
                            callLogDao.startUploadCalls(mContext, lastId);
                        }

                        if (!GlobalData.userDataHelper.isLogin() || call_duration == 0) {
                            // 排除来电了,但是直接挂了 没有接通的情况
                            if (!TextUtils.isEmpty(mIncomingNumber)) {
                                HCLogUtil.d(TAG, "来电拒接 " + mIncomingNumber);
                                resetToOriginalStatus();
                            }
                            return;
                        }

                        HCLogUtil.d(TAG, "CALL_STATE_IDLE 挂电话 " + "incomeNum = " + mIncomingNumber + ", mDialNumber=" + mDialNumber +
                                " ,recordFile = " + mRecordFile + ", isExist=" + mRecordFile.exists());

                        String number = TextUtils.isEmpty(mIncomingNumber) ? mDialNumber : mIncomingNumber;
                        // 电话挂断 有来电号码或者拨打号码
                        if (mRecordFile != null && mRecordFile.exists() && !TextUtils.isEmpty(number)) {
                            if (isDialRecording || isInComingRecording) {
                                long create_time = fileCreateTime;
                                call_duration = System.currentTimeMillis() - call_duration;

                                // 过滤通话时长少于5秒
                                if (call_duration <= 5000) {
                                    HCLogUtil.d(TAG, "时长少于5秒 call_duration = " + call_duration);
                                    releaseRecorder(mediaRecorder);
                                    mRecordFile.delete();
                                    mRecordFile = null;
                                    return;
                                }

                                // 0 呼入 1 呼出
                                int call_type = TextUtils.isEmpty(mIncomingNumber) ? 1 : 0;
                                String saler_phone = PreferenceManager.getDefaultSharedPreferences(mContext).getString("userMobile", "");
                                int employee_number = 0;
                                String customer_phone = number;

                                if (GlobalData.userDataHelper != null) {
                                    if (GlobalData.userDataHelper.getUser() != null) {
                                        employee_number = GlobalData.userDataHelper.getUser().getId();
                                    }
                                }

                                PhoneRecordEntity entity = new PhoneRecordEntity();
                                saler_phone = saler_phone == null ? "" : saler_phone;
                                customer_phone = customer_phone == null ? "" : customer_phone;
                                entity.setCall_duration((int) call_duration);
                                entity.setCall_type(call_type);
                                entity.setCreate_time(create_time);
                                entity.setSaler_phone(saler_phone);
                                entity.setEmployee_number(employee_number);
                                entity.setCustomer_phone(customer_phone);
                                long result = PhoneRecordDAO.getInstance().insert(entity);
                                HCLogUtil.d(TAG, "insert result = " + result);
                                releaseRecorder(mediaRecorder);
                                RecordUtil.setRecordingFile(mContext, "");
                                //TODO 当前网络为WiFi/3G/4G并且已经连接
                                if (SupportUtil.isCurrentNetWifiOr3gOr4g(mContext) && !mUploadTask.isUploadingFile()) {
                                    mUploadTask.getUploadToken(mRecordFile);
                                }
                            }
                        }

                        // 什么情况会进入这,recordFile被删除了
                        if (!mRecordFile.exists()) {
                            HCLogUtil.d(TAG, "录音文件不存在 ...  重启服务 ");
                            releaseRecorder(mediaRecorder);
                            resetToOriginalStatus();
                            RecordUtil.reLaunchService(mContext);
                        }
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                releaseRecorder(mediaRecorder);
                mRecordFile.delete();
                mRecordFile = null;
                e.printStackTrace();
                HCLogUtil.d(TAG, "onCallStateCahnged state exception ----" + e.getMessage() + ",cause " + e.getCause());
            }
        }

        private void doPhoneOffHook() {

            HCLogUtil.d(TAG, "CALL_STATE_OFFHOOOOOK inCome=" + mIncomingNumber + ",Dia=" + mDialNumber + ", isDiaRecoring=" +
                    isDialRecording + ",isIncomRecording=" + isInComingRecording);

            // 有拨打号码并且没有来电号码
            if (TextUtils.isEmpty(mIncomingNumber) && !TextUtils.isEmpty(mDialNumber)) {
                // 拨打电话状态
                isDialRecording = true;
                initRecordFile();
                doRealRecord();
                HCLogUtil.d(TAG, "拨打电话 dialNum= " + mDialNumber + "---------mIncomingNumber=" + mIncomingNumber);
            }

            // 有来电号码并且没有拨打号码
            if (!TextUtils.isEmpty(mIncomingNumber) && TextUtils.isEmpty(mDialNumber)) {
                // 接电话并接通
                isInComingRecording = true;
                initRecordFile();
                doRealRecord();
                HCLogUtil.d(TAG, "接听电话 incomingNumber= " + mIncomingNumber + "++++++++mDialNumber=" + mDialNumber);
            }

            // TODO 有接打电话并且有新的来电号码
//            if ((isInComingRecording || isDialRecording) && !TextUtils.isEmpty(mIncomingNumber)){
//                // 接电话并接通
//                isInComingRecording = true;
//                initRecordFile();
//                doRealRecord();
//                HCLogUtil.d(TAG, "接听电话 incomingNumber= " + mIncomingNumber + "++++++++mDialNumber=" + mDialNumber);
//            }
        }

        /**
         * 开始录音
         */
        private void doRealRecord() {
            new Thread() {
                public void run() {
                    try {
                        initMediaRecorder();
                    } catch (Exception e) {
                        e.printStackTrace();
                        HCLogUtil.d(TAG, "VOICE_CALL ERROR----" + e.getMessage() + ", cause " + e.getCause());
                        handleMediaRecorderError(mediaRecorder, true);
                        SharedPreferences ds = PreferenceManager.getDefaultSharedPreferences(mContext);
                        ds.edit().putInt("audiosource", MediaRecorder.AudioSource.MIC).commit();
                    }
                }

            }.start();
        }

        /**
         * 初始化并设置声音源
         *
         * @throws IllegalStateException
         * @throws IOException
         */
        private void initMediaRecorder() throws IllegalStateException, IOException {
            mediaRecorder = new MediaRecorder();

            // H60-L03 华为荣耀6手机型号 // 荣耀6P brand：Huawei--model:PE-UL00
            HCLogUtil.i(TAG, "brand" + Build.BRAND + "--model:" + android.os.Build.MODEL);
            if ("huawei".equals(Build.BRAND.toLowerCase()) && isDialRecording) {
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); // 获得声音数据源
            } else {
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL); // 获得声音数据源
            }

            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP); // 按3gp格式输出
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB); //
            mediaRecorder.setOutputFile(mRecordFile.getAbsolutePath()); // 输出文件
            mediaRecorder.setOnErrorListener(RecordService.this);
            mediaRecorder.prepare();
            mediaRecorder.start();
            call_duration = System.currentTimeMillis();
            RecordUtil.setRecordingFile(mContext, mRecordFile.getName());
            HCLogUtil.d(TAG, "MEDIARECORD STARTED---------------------");
        }

        /**
         * 初始化录音文件
         */
        private void initRecordFile() {
            if (isDialRecording || isInComingRecording) {
                fileCreateTime = System.currentTimeMillis() / 1000;
                String fileName = fileCreateTime + ".3gp";
                mRecordFile = new File(mUploadTask.getRecordDir(), fileName);
                HCLogUtil.d(TAG, "initRecordFile  = " + mRecordFile);
                if (mRecordFile.exists()) {
                    mRecordFile.delete();
                } else {
                    try {
                        boolean created = mRecordFile.createNewFile();
                        HCLogUtil.d(TAG, "mRecordFile= " + mRecordFile + ",created = " + created + ", exists = " + mRecordFile.exists());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        /**
         * 释放mediaRecorder
         *
         * @param mediaRecorder
         */
        private void releaseRecorder(MediaRecorder mediaRecorder) {
            if (mediaRecorder != null) {
                HCLogUtil.d(TAG, "录音结束 releaseRecorder isDial " + isDialRecording + ",isComing=" + isInComingRecording);
                if (isDialRecording || isInComingRecording) {
                    try {
                        resetToOriginalStatus();
                        mediaRecorder.stop();
                    } catch (Exception e) {
                        RecordUtil.reLaunchService(mContext);
                        HCLogUtil.d(TAG, "可能是用户未授权  stop  exception " + e.getMessage());
                    } finally {
                        mediaRecorder.release();
                        mediaRecorder = null;
                    }
                }

                if (mediaRecorder != null) {
                    mediaRecorder.release();
                    mediaRecorder = null;
                }
            }
        }
    }

    private void resetToOriginalStatus() {
        isDialRecording = false;
        isInComingRecording = false;
        mIncomingNumber = "";
        mDialNumber = "";
        call_duration = 0;
    }

    @Override
    public void onError(MediaRecorder mr, int what, int extra) {
        HCLogUtil.d(TAG, "录制过程异常 onError " + "what = " + what + ", extra= " + extra);
        handleMediaRecorderError(mr, true);
    }

    /**
     * 处理异常情况.
     *
     * @param mr
     * @param shouldDelRecordFile 是否删除录音文件
     */
    private synchronized void handleMediaRecorderError(MediaRecorder mr, boolean shouldDelRecordFile) {
        if (mr != null) {
            HCLogUtil.d(TAG, "handleMediaError-------" + mr);
            mr.release();
            mr = null;
            resetToOriginalStatus();
            stopService(new Intent(mContext, RecordService.class));
            if (shouldDelRecordFile && mRecordFile != null && mRecordFile.exists()) {
                mRecordFile.delete();
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void setServiceForeground() {
        // TODO
        Notification notification = new Notification(R.drawable.ic_launcher, getText(R.string.app_name), System.currentTimeMillis());
        Intent notificationIntent = new Intent(this, MainPageActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification.setLatestEventInfo(this, getText(R.string.app_name), getText(R.string.notification_message), pendingIntent);
        startForeground(101, notification);
    }


    @Override
    public void onHttpStart(String action, int requestId) {

    }

    @Override
    public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
        if (response.getData() != null) {
            com.haoche51.sales.util.HCLogUtil.i(TAG, "heart beat response from server" + response.getData());
        }
    }

    @Override
    public void onHttpProgress(String action, int requestId, long bytesWritten, long totalSize) {

    }

    @Override
    public void onHttpRetry(String action, int requestId, int retryNo) {

    }

    @Override
    public void onHttpFinish(String action, int requestId) {

    }


}