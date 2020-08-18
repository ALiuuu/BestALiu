package com.aliu.aliubase;

import android.app.Application;

/**
 *
 */
public class ALiuBaseApplication extends Application {


  private static ALiuBaseApplication sInstance;

  public static ALiuBaseApplication getIns() {
    return sInstance;
  }

  @Override public void onCreate() {
    super.onCreate();
    sInstance = this;
  }
}
