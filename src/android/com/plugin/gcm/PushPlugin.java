// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.plugin.gcm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONObject;

// Referenced classes of package com.plugin.gcm:
//            NotificationService

public class PushPlugin extends CordovaPlugin
{

    public static final String EXIT = "exit";
    public static final String GCM_SENDER_ID = "gcm_senderid";
    public static final String ON_MESSAGE_BACKGROUND = "onMessageInBackground";
    public static final String ON_MESSAGE_FOREGROUND = "onMessageInForeground";
    public static final String REGISTER = "register";
    public static final String SENDER_ID = "senderID";
    public static final String TAG = "PushPlugin";
    public static final String UNREGISTER = "unregister";

    public PushPlugin()
    {
    }

    private Context getApplicationContext()
    {
        return cordova.getActivity().getApplicationContext();
    }

    private boolean handleOnMessageBackground(JSONArray jsonarray, CallbackContext callbackcontext)
    {
        Log.v("PushPlugin", (new StringBuilder()).append("handleOnMessageBackground() -> data: ").append(jsonarray).toString());
        NotificationService.getInstance(getApplicationContext()).addNotificationBackgroundCallBack(webView, callbackcontext);
        return true;
    }

    private boolean handleOnMessageForeground(JSONArray jsonarray, CallbackContext callbackcontext)
    {
        Log.v("PushPlugin", (new StringBuilder()).append("handleOnMessageForeground() -> data: ").append(jsonarray).toString());
        NotificationService.getInstance(getApplicationContext()).addNotificationForegroundCallBack(webView, callbackcontext);
        return true;
    }

    private boolean handleRegister(JSONArray jsonarray, CallbackContext callbackcontext)
    {
        try
        {
            jsonarray = (String)jsonarray.getJSONObject(0).get("senderID");
        }
        // Misplaced declaration of an exception variable
        catch (JSONArray jsonarray)
        {
            Log.e("PushPlugin", (new StringBuilder()).append("execute: Got JSON Exception ").append(jsonarray.getMessage()).toString());
            callbackcontext.error(jsonarray.getMessage());
            return false;
        }
        if (jsonarray == null)
        {
            break MISSING_BLOCK_LABEL_39;
        }
        if (jsonarray.trim().length() > 0)
        {
            NotificationService.getInstance(getApplicationContext()).setSenderID(jsonarray);
        }
        NotificationService.getInstance(getApplicationContext()).registerWebView(webView);
        NotificationService.getInstance(getApplicationContext()).addRegisterCallBack(webView, callbackcontext);
        return true;
    }

    private boolean handleUnRegister(JSONArray jsonarray, CallbackContext callbackcontext)
    {
        Log.v("PushPlugin", (new StringBuilder()).append("handleUnRegister() -> data: ").append(jsonarray).toString());
        NotificationService.getInstance(getApplicationContext()).unRegister();
        callbackcontext.success();
        return true;
    }

    private void readSenderIdFromCordovaConfig()
    {
        Object obj = cordova.getActivity().getIntent().getExtras();
        if (((Bundle) (obj)).containsKey("gcm_senderid"))
        {
            obj = ((Bundle) (obj)).getString("gcm_senderid");
            NotificationService.getInstance(getApplicationContext()).setSenderID(((String) (obj)));
        }
    }

    public boolean execute(String s, JSONArray jsonarray, CallbackContext callbackcontext)
    {
        Log.v("PushPlugin", (new StringBuilder()).append("handleRegister -> data: ").append(jsonarray).toString());
        if ("register".equals(s))
        {
            return handleRegister(jsonarray, callbackcontext);
        }
        if ("onMessageInForeground".equals(s))
        {
            return handleOnMessageForeground(jsonarray, callbackcontext);
        }
        if ("onMessageInBackground".equals(s))
        {
            return handleOnMessageBackground(jsonarray, callbackcontext);
        }
        if ("unregister".equals(s))
        {
            return handleUnRegister(jsonarray, callbackcontext);
        } else
        {
            Log.e("PushPlugin", (new StringBuilder()).append("Invalid action : ").append(s).toString());
            callbackcontext.error((new StringBuilder()).append("Invalid action : ").append(s).toString());
            return false;
        }
    }

    public void initialize(CordovaInterface cordovainterface, CordovaWebView cordovawebview)
    {
        super.initialize(cordovainterface, cordovawebview);
        readSenderIdFromCordovaConfig();
    }

    public void onDestroy()
    {
        Log.v("PushPlugin", (new StringBuilder()).append("onDestroy() -> webView: ").append(webView).toString());
        NotificationService.getInstance(getApplicationContext()).removeWebView(webView);
        super.onDestroy();
    }

    public void onPause(boolean flag)
    {
        super.onPause(flag);
        Log.v("PushPlugin", (new StringBuilder()).append("onPause() -> webView: ").append(webView).toString());
        NotificationService.getInstance(getApplicationContext()).setForeground(false);
    }

    public void onResume(boolean flag)
    {
        super.onResume(flag);
        Log.v("PushPlugin", (new StringBuilder()).append("onResume() -> webView: ").append(webView).toString());
        NotificationService.getInstance(getApplicationContext()).setForeground(true);
    }
}
