// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.plugin.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gcm.GCMBaseIntentService;
import java.util.Iterator;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;

// Referenced classes of package com.plugin.gcm:
//            PushHandlerActivity, NotificationService

public class GCMIntentService extends GCMBaseIntentService
{

    public static final String ALERT = "alert";
    public static final String DATA = "data";
    public static final String MESSAGE = "message";
    public static final int NOTIFICATION_ID = 237;
    private static String TAG = "PushPlugin-GCMIntentService";

    public GCMIntentService()
    {
        super(new String[] {
            "GCMIntentService"
        });
    }

    public static void cancelNotification(Context context)
    {
        ((NotificationManager)context.getSystemService("notification")).cancel(getAppName(context), 237);
    }

    private static String getAppName(Context context)
    {
        return (String)context.getPackageManager().getApplicationLabel(context.getApplicationInfo());
    }

    public void createNotification(Context context, Bundle bundle)
    {
        Object obj;
        Object obj1;
        NotificationManager notificationmanager = (NotificationManager)getSystemService("notification");
        String s = getAppName(this);
        obj = new Intent(this, com/plugin/gcm/PushHandlerActivity);
        ((Intent) (obj)).addFlags(0x20000000);
        ((Intent) (obj)).putExtra("pushBundle", bundle);
        obj = PendingIntent.getActivity(this, 0, ((Intent) (obj)), 0x8000000);
        int j = -1;
        int i = j;
        JSONObject jsonobject;
        if (bundle.getString("defaults") != null)
        {
            try
            {
                i = Integer.parseInt(bundle.getString("defaults"));
            }
            // Misplaced declaration of an exception variable
            catch (Object obj1)
            {
                Log.d(TAG, "NumberFormatException");
                i = j;
            }
        }
        obj1 = (new android.support.v4.app.NotificationCompat.Builder(context)).setDefaults(i).setSmallIcon(context.getApplicationInfo().icon).setWhen(System.currentTimeMillis()).setContentTitle(bundle.getString("title")).setTicker(bundle.getString("title")).setContentIntent(((PendingIntent) (obj))).setAutoCancel(true);
        obj = null;
        if (bundle.getString("data") == null) goto _L2; else goto _L1
_L1:
        jsonobject = new JSONObject(bundle.getString("data"));
        context = ((Context) (obj));
        try
        {
            if (jsonobject.has("alert"))
            {
                context = jsonobject.getString("alert");
            }
        }
        // Misplaced declaration of an exception variable
        catch (Context context)
        {
            Log.e(TAG, "extrasToJSON: JSON exception");
            context = ((Context) (obj));
        }
_L3:
        if (context != null)
        {
            ((android.support.v4.app.NotificationCompat.Builder) (obj1)).setContentTitle(s);
            ((android.support.v4.app.NotificationCompat.Builder) (obj1)).setContentText(context);
        } else
        {
            ((android.support.v4.app.NotificationCompat.Builder) (obj1)).setContentText("<missing message content>");
        }
        context = bundle.getString("msgcnt");
        if (context != null)
        {
            ((android.support.v4.app.NotificationCompat.Builder) (obj1)).setNumber(Integer.parseInt(context));
        }
        i = 237;
        j = Integer.parseInt(bundle.getString("notId"));
        i = j;
_L4:
        notificationmanager.notify(s, i, ((android.support.v4.app.NotificationCompat.Builder) (obj1)).build());
        return;
_L2:
        context = ((Context) (obj));
        if (bundle.getString("message") != null)
        {
            context = bundle.getString("message");
        }
          goto _L3
        context;
        Log.e(TAG, (new StringBuilder()).append("Number format exception - Error parsing Notification ID: ").append(context.getMessage()).toString());
          goto _L4
        context;
        Log.e(TAG, (new StringBuilder()).append("Number format exception - Error parsing Notification ID").append(context.getMessage()).toString());
          goto _L4
    }

    public void dumpExtras(Bundle bundle)
    {
        Iterator iterator = bundle.keySet().iterator();
        Log.d(TAG, "Dumping Extras Start");
        String s;
        for (; iterator.hasNext(); Log.d(TAG, (new StringBuilder()).append("[").append(s).append("=").append(bundle.get(s)).append("]").toString()))
        {
            s = (String)iterator.next();
        }

        Log.d(TAG, "Dumping Extras End");
    }

    public void onError(Context context, String s)
    {
        Log.e(TAG, (new StringBuilder()).append("onError - errorId: ").append(s).toString());
    }

    protected void onMessage(Context context, Intent intent)
    {
        boolean flag = NotificationService.getInstance(context).isForeground();
        intent = intent.getExtras();
        if (intent != null)
        {
            dumpExtras(intent);
            if (!flag)
            {
                if (intent.getString("message") != null && intent.getString("message").length() != 0 || intent.getString("data") != null && intent.getString("data").length() != 0)
                {
                    Log.d(TAG, "Creating Notification now");
                    createNotification(context, intent);
                } else
                {
                    Log.d(TAG, "Will not create Notification with empty Message/Alert");
                }
            }
            NotificationService.getInstance(context).onMessage(intent);
        }
    }

    public void onRegistered(Context context, String s)
    {
        Log.d(TAG, (new StringBuilder()).append("onRegistered: ").append(s).toString());
        NotificationService.getInstance(context).onRegistered(s);
    }

    public void onUnregistered(Context context, String s)
    {
        Log.d(TAG, (new StringBuilder()).append("onUnregistered - regId: ").append(s).toString());
    }

}
