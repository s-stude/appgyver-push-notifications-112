// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.plugin.gcm;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import com.appgyver.cordova.AGCordovaApplicationInterface;

// Referenced classes of package com.plugin.gcm:
//            GCMIntentService

public class PushHandlerActivity extends Activity
{

    public static final String PUSH_BUNDLE = "pushBundle";
    private static String TAG = "PushPlugin-PushHandlerActivity";

    public PushHandlerActivity()
    {
    }

    private void forceMainActivityReload()
    {
        PackageManager packagemanager = getPackageManager();
        String s = getApplicationContext().getPackageName();
        Log.d(TAG, (new StringBuilder()).append("forceMainActivityReload() - packageName: ").append(s).toString());
        startActivity(packagemanager.getLaunchIntentForPackage(s).setFlags(0x20000000));
    }

    private boolean isApplicationRunning()
    {
        return ((AGCordovaApplicationInterface)getApplicationContext()).getCurrentActivity() != null;
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        Log.d(TAG, (new StringBuilder()).append("onCreate - isApplicationRunning: ").append(isApplicationRunning()).toString());
        GCMIntentService.cancelNotification(this);
        if (!isApplicationRunning())
        {
            forceMainActivityReload();
        }
        finish();
    }

    protected void onResume()
    {
        super.onResume();
        ((NotificationManager)getSystemService("notification")).cancelAll();
    }

}
