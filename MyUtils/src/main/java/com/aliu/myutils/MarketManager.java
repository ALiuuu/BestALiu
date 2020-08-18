package com.aliu.myutils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.text.format.Formatter;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import com.aliu.aliubase.ALiuBaseApplication;
import java.io.File;

/**
 * @author ruomiz
 * @desc
 * @since 2019/6/12
 */
public class MarketManager {

  /**
   * 邮件反馈
   */
  public static void doFeedBackByEmail(Context context, String filePath, String title, String hint) {
    if (context == null) {
      return;
    }
    String emailTitle = TextUtils.isEmpty(title) ? "feedback" : title;
    String emailContent = "";
    String versionName = "0.0.0.0";
    long verCode = 0;
    try {
      PackageManager pm = context.getPackageManager();
      PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
      versionName = pi.versionName;
      verCode = pi.versionCode;
    } catch (Exception exception) {

    }
    //String strGPURender = MyQHWCodecQuery.getGpuType();

    emailContent += TextUtils.isEmpty(hint) ? "" : hint + "\n";
    emailContent +=
        "------------------------------------------------------------------------" + "\n";
    emailContent += "\n\n\n\n\n\n\n";
    emailContent +=
        "------------------------------------------------------------------------" + "\n";
    emailContent += "App VerName:      " + versionName + "\n";
    emailContent += "App VerCode:      " + verCode + "\n";
    emailContent += "Android Version:  " + getSDKVersion() + "\n";
    emailContent += "Device Model:     " + getModule() + "\n";

    emailContent += "Device Width:     " + SizeUtil.getsScreenWidth() + "\n";
    emailContent += "Device Height:    " + SizeUtil.getScreenHeight() + "\n";
    //emailContent += "Device GPU:       " + strGPURender + "\n";
    emailContent += "Device Capacity:  " + getSdTotalSize(context) + "\n";
    emailContent += "Device Avaliable: " + getSdAvailableSize(context) + "\n";
    //emailContent += "Device isDome: " + DeviceUserProxy.isDomeFlavor() + "\n";

    Intent data = new Intent();
    data.setAction(filePath == null ? Intent.ACTION_SENDTO : Intent.ACTION_SEND);
    if (filePath == null) {
      Uri uri = Uri.parse("mailto:" + getFeedbackEmail(context));
      data.setData(uri);
    }
    data.putExtra(Intent.EXTRA_SUBJECT, emailTitle);
    data.putExtra(Intent.EXTRA_TEXT, emailContent);
    if (FileUtils.isFileExisted(filePath)) {
      data.setType("application/octet-stream");
      data.putExtra(Intent.EXTRA_STREAM, getFileProvider(filePath));
      data.setType("message/rfc822");
      String[] email = { getFeedbackEmail(context) };
      data.putExtra(Intent.EXTRA_EMAIL, email);
    }
    try {
      context.startActivity(
          filePath == null ? data : Intent.createChooser(data, "Choose Email Client"));
    } catch (ActivityNotFoundException e) {
      e.printStackTrace();
    }
  }

  private static Uri getFileProvider(String filePath) {
    Uri fileUri;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      fileUri = FileProvider.getUriForFile(ALiuBaseApplication.getIns(),
          ALiuBaseApplication.getIns().getPackageName() + ".provider", new File(filePath));
    } else {
      fileUri = Uri.fromFile(new File(filePath));
    }
    return fileUri;
  }

  // 获取系统版本,如: 2.3.3
  public static String getSDKVersion() {
    return Build.VERSION.RELEASE;
  }

  // 获取设备型号,如: Samsung Galaxy S2
  public static String getModule() {
    return Build.MODEL;
  }

  // 获取系统版本代号,如: 10
  public static String getSDK() {
    return String.valueOf(Build.VERSION.SDK_INT);
  }

  private static String getSdTotalSize(Context context) {
    if (context == null) {
      return "0";
    }
    StatFs sf = new StatFs(Environment.getExternalStorageDirectory().toString());
    long blockSize = sf.getBlockSize();
    long totalBlocks = sf.getBlockCount();
    return Formatter.formatFileSize(context, blockSize * totalBlocks);
  }

  public static String getSdAvailableSize(Context context) {
    if (context == null) {
      return "0";
    }
    StatFs sf = new StatFs(Environment.getExternalStorageDirectory().toString());
    long blockSize = sf.getBlockSize();
    long availableBlocks = sf.getAvailableBlocks();
    return Formatter.formatFileSize(context, blockSize * availableBlocks);
  }

  @NonNull private static String getFeedbackEmail(Context context) {
    return "enjoyment.internetional@gmail.com";
  }
}
