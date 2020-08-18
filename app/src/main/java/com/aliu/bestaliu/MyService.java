package com.aliu.bestaliu;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

public class MyService extends Service {

  private static final int CODE = 1;

  public MyService() {
  }

  @Override
  public IBinder onBind(Intent intent) {
    IBinder binder = mMessenger.getBinder();
    Log.i("fuck", "服务端 连接成功" + binder);
    return binder;
  }

  //创建一个送信人，封装handler
  private Messenger mMessenger = new Messenger(new Handler() {
    @Override
    public void handleMessage(Message msg) {
      Message toClient = Message.obtain();
      switch (msg.what) {
        case CODE:
          //接收来自客户端的消息，并作处理
          int arg = msg.arg1;
          Toast.makeText(getApplicationContext(), arg + "", Toast.LENGTH_SHORT).show();
          toClient.arg1 = 1111111111;
          try {
            //回复客户端消息
            msg.replyTo.send(toClient);
          } catch (RemoteException e) {
            e.printStackTrace();
          }
      }
      super.handleMessage(msg);
    }
  });
}
