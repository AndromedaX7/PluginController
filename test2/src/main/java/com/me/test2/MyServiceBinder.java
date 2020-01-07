package com.me.test2;

import android.os.RemoteException;
import android.util.Log;

public class MyServiceBinder extends IMyService2.Stub {
    @Override
    public void connect(String msg) throws RemoteException {
        Log.e("receive message",msg);
    }
}
