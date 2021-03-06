package com.aliu.myutils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import com.aliu.aliubase.ALiuBaseApplication;

public class SizeUtil {

  private static float sPixelDensity = -1;
  private static int sScreenWidth;
  private static int sScreenHeight;

  public static int getScreenHeight() {
    if (sScreenHeight != 0) {
      return sScreenHeight;
    }
    initScreenSize();
    return sScreenHeight;
  }

  public static int getsScreenWidth() {
    if (sScreenWidth != 0) {
      return sScreenWidth;
    }
    initScreenSize();
    return sScreenWidth;
  }

  public static int getFitPxFromDp(float dp) {
    return (int) (dp * getPixelDensity() + 0.5f);
  }

  public static float pixel2dp(float dp) {
    return dp * getPixelDensity() + 0.5f;
  }

  public static float dpToPixel(float dp) {
    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
        ALiuBaseApplication.getIns().getResources().getDisplayMetrics());
  }

  public static int pixel2sp(Context context, float pxValue) {
    return (int) (pxValue / context.getResources().getDisplayMetrics().scaledDensity + 0.5f);
  }

  public static float getPixelDensity() {
    if (sPixelDensity != -1) {
      return sPixelDensity;
    }
    DisplayMetrics metrics = ALiuBaseApplication.getIns().getResources().getDisplayMetrics();
    sPixelDensity = metrics.density;
    return sPixelDensity;
  }

  private static void initScreenSize() {
    Context appContext = ALiuBaseApplication.getIns();
    DisplayMetrics dm = appContext.getResources().getDisplayMetrics();
    sScreenHeight = dm.heightPixels;
    //if (NotchUtil.isNotchDevice(appContext)) {
    //  sScreenHeight -= getStatusBarHeight(appContext);
    //}
    sScreenWidth = dm.widthPixels;
  }

  /**
   * 获取设备statusBar高度
   */
  public static int getStatusBarHeight(Context ctx) {
    int height = 0;
    int resourceId = ctx.getResources().getIdentifier("status_bar_height", "dimen", "android");
    if (resourceId > 0) {
      height = ctx.getResources().getDimensionPixelSize(resourceId);
    }
    return height;
  }
}
