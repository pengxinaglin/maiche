package com.haoche51.sales.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.haoche51.sales.R;
import com.haoche51.sales.helper.ImageLoaderHelper;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuao on 16/8/8
 */
public class WeChatUtil {

	private final static String APP_ID = "wxa6b33538ae632621";

	private static int IMAGE_NAME = 0;
	private static boolean isShare = false;

	public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
		if (needRecycle) {
			bmp.recycle();
		}

		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private static String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}

	public static void share(Activity activity, String title, String description, String url, String picUrl, boolean isTimeline) {
		if (activity == null || activity.isFinishing()) return;
		IWXAPI api = WXAPIFactory.createWXAPI(activity.getApplicationContext(), APP_ID);
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = url;
		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = title;
		msg.description = description;

		//优先使用网络图
//		if (picUrl != null) {
//			Bitmap thumb = Bitmap.createBitmap(ImageLoaderHelper.loadImageSync(picUrl, 120, 120));//压缩Bitmap
//			msg.thumbData = bmpToByteArray(thumb, true);
//		} else {
			Bitmap thumb = BitmapFactory.decodeResource(activity.getApplicationContext().getResources(), R.drawable.ic_launcher);
			msg.thumbData = bmpToByteArray(thumb, true);
//		}

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("mcsq");
		req.message = msg;
		req.scene = isTimeline ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
		api.sendReq(req);
	}

	/***
	 * @param activity context
	 * @param urls     所选择八张图片的url
	 * @param qr       二维码的链接
	 * @param title    分享的文字
	 * @param quality  图片的压缩比例
	 * @param callback 分享时的回调
	 */
	public static void share(Activity activity, List<String> urls, String qr, String title,
	                         int quality, ShareCallback callback) {
		if (activity == null || activity.isFinishing()) return;

		if (TextUtils.isEmpty(qr)) {
			ToastUtil.showInfo("没有二维码");
			return;
		}
		if (urls == null || urls.size() != 8) {
			ToastUtil.showInfo("请选择8张图片");
			return;
		}
		if (!isShare) {
			new shareTask(activity, title, urls, quality, callback).execute(qr);
		}
	}

	private static class shareTask extends AsyncTask<String, Integer, Void> {

		private Context context;
		private String title;
		private List<String> urls;
		private int quality;
		private List<File> files;
		private ShareCallback callback;

		public shareTask(Context context, String title, List<String> urls, int quality,
		                 ShareCallback callback) {
			this.context = context;
			this.title = title;
			this.urls = urls;
			this.quality = quality;
			this.callback = callback;
			files = new ArrayList<>();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			isShare = true;
			callback.start();
		}

		@Override
		protected Void doInBackground(String... params) {
			urls.add(4, params[0]);
			for (int i = 0; i < urls.size(); i++) {
				try {
					File file = createStableImageFile(context);
					Bitmap bitmap = WeChatUtil.getNetWorkBitmap(urls.get(i));
					File qrFile = saveImageToSdCard(bitmap, file, quality);
					if (qrFile != null) {
						files.add(qrFile);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {
			callback.end();
			Intent intent = new Intent();
			ComponentName comp =
					new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI");
			intent.setComponent(comp);
			intent.setAction(Intent.ACTION_SEND_MULTIPLE);
			intent.setType("image/*");
			intent.putExtra("Kdescription", title);
			ArrayList<Uri> imageUris = new ArrayList<>();
			for (File f : files) {
				imageUris.add(Uri.fromFile(f));
			}
			intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
			try {
				context.startActivity(intent);
			} catch (ActivityNotFoundException e) {
				ToastUtil.showInfo("你还没有安装微信");
			}
			isShare = false;
			urls.remove(4);
		}
	}

	public interface ShareCallback {
		void start();

		void end();
	}

	private static File saveImageToSdCard(Bitmap bitmap, File file, int quality) {
		boolean success = false;
		FileOutputStream outStream;
		try {
			outStream = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outStream);
			outStream.flush();
			outStream.close();
			bitmap.recycle();
			success = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (success) {
			return file;
		} else {
			return null;
		}
	}

	private static File createStableImageFile(Context context) throws IOException {
		if (IMAGE_NAME == 9) IMAGE_NAME = 0;
		IMAGE_NAME++;
		String imageFileName = "haoche51_share_" + Integer.toString(IMAGE_NAME) + ".jpg";
		File storageDir = context.getExternalCacheDir();
		return new File(storageDir, imageFileName);
	}

	private static Bitmap getNetWorkBitmap(String urlString) {
		URL imgUrl;
		Bitmap bitmap = null;
		try {
			imgUrl = new URL(urlString);
			HttpURLConnection urlConn = (HttpURLConnection) imgUrl.openConnection();
			urlConn.setDoInput(true);
			urlConn.connect();
			InputStream is = urlConn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}
}
