// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.plugin.gcm;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import com.appgyver.cordova.AGCordovaApplicationInterface;
import com.google.android.gcm.GCMRegistrar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NotificationService
{
    static class WebViewReference
    {

        private CallbackContext mNotificationBackgroundCallBack;
        private CallbackContext mNotificationForegroundCallBack;
        private NotificationService mNotificationService;
        private List mNotifications;
        private boolean mNotifiedOfRegistered;
        private CallbackContext mRegisterCallBack;
        private CordovaWebView mWebView;

        private void sendNotification(CallbackContext callbackcontext, JSONObject jsonobject)
        {
            if (callbackcontext != null)
            {
                PluginResult pluginresult = new PluginResult(org.apache.cordova.PluginResult.Status.OK, jsonobject);
                pluginresult.setKeepCallback(true);
                callbackcontext.sendPluginResult(pluginresult);
                mNotifications.add(jsonobject);
                return;
            } else
            {
                Log.v(NotificationService.TAG, (new StringBuilder()).append("No Notification callback - webview: ").append(getWebView()).toString());
                return;
            }
        }

        public void destroy()
        {
            mWebView = null;
            mRegisterCallBack = null;
            mNotificationForegroundCallBack = null;
            mNotificationBackgroundCallBack = null;
            mNotificationService = null;
            mNotifications.clear();
        }

        public CallbackContext getNotificationBackgroundCallBack()
        {
            return mNotificationBackgroundCallBack;
        }

        public CallbackContext getNotificationForegroundCallBack()
        {
            return mNotificationForegroundCallBack;
        }

        public CallbackContext getRegisterCallBack()
        {
            return mRegisterCallBack;
        }

        public CordovaWebView getWebView()
        {
            return mWebView;
        }

        public boolean hasNotification(JSONObject jsonobject)
        {
            return mNotifications.contains(jsonobject);
        }

        public boolean hasNotifiedOfRegistered()
        {
            return mNotifiedOfRegistered;
        }

        public void notifyRegistered()
        {
            if (hasNotifiedOfRegistered())
            {
                Log.v(NotificationService.TAG, (new StringBuilder()).append("notifyRegistered() - Webview already notified of registration. skipping callback. webview: ").append(getWebView()).toString());
                return;
            }
            if (getRegisterCallBack() != null)
            {
                setNotifiedOfRegistered(true);
                getRegisterCallBack().success(mNotificationService.mRegistrationID);
                return;
            } else
            {
                Log.v(NotificationService.TAG, (new StringBuilder()).append("No Register callback - webview: ").append(getWebView()).toString());
                return;
            }
        }

        public void sendNotification(JSONObject jsonobject)
        {
            boolean flag;
            if (hasNotification(jsonobject))
            {
                return;
            }
            flag = true;
            boolean flag1 = jsonobject.getBoolean("foreground");
            flag = flag1;
_L2:
            if (flag)
            {
                Log.v(NotificationService.TAG, (new StringBuilder()).append("sendNotification() - foreground callback - webview: ").append(getWebView()).toString());
                sendNotification(getNotificationForegroundCallBack(), jsonobject);
                return;
            } else
            {
                Log.v(NotificationService.TAG, (new StringBuilder()).append("sendNotification() - background callback - webview: ").append(getWebView()).toString());
                sendNotification(getNotificationBackgroundCallBack(), jsonobject);
                return;
            }
            JSONException jsonexception;
            jsonexception;
            if (true) goto _L2; else goto _L1
_L1:
        }

        public void setNotificationBackgroundCallBack(CallbackContext callbackcontext)
        {
            Log.v(NotificationService.TAG, (new StringBuilder()).append("setNotificationBackgroundCallBack() - webview: ").append(getWebView()).toString());
            mNotificationBackgroundCallBack = callbackcontext;
        }

        public void setNotificationForegroundCallBack(CallbackContext callbackcontext)
        {
            Log.v(NotificationService.TAG, (new StringBuilder()).append("setNotificationForegroundCallBack() - webview: ").append(getWebView()).toString());
            mNotificationForegroundCallBack = callbackcontext;
        }

        public void setNotifiedOfRegistered(boolean flag)
        {
            mNotifiedOfRegistered = flag;
        }

        public void setRegisterCallBack(CallbackContext callbackcontext)
        {
            mRegisterCallBack = callbackcontext;
        }

        public String toString()
        {
            String s = "empty";
            if (getWebView() != null)
            {
                s = getWebView().toString();
            }
            return (new StringBuilder()).append("WebViewReference -> ").append(s).toString();
        }

        public WebViewReference(NotificationService notificationservice, CordovaWebView cordovawebview)
        {
            mNotifiedOfRegistered = false;
            mNotifications = new ArrayList();
            mNotificationService = notificationservice;
            mWebView = cordovawebview;
        }
    }


    public static final String COLDSTART = "coldstart";
    public static final String COLLAPSE_KEY = "collapse_key";
    public static final String FOREGROUND = "foreground";
    public static final String FROM = "from";
    public static final String JSON_ARRAY_START_PREFIX = "[";
    public static final String JSON_START_PREFIX = "{";
    public static final String KEY_UUID = "uuid";
    public static final String MESSAGE = "message";
    public static final String MSGCNT = "msgcnt";
    public static final String PAYLOAD = "payload";
    public static final String SOUNDNAME = "soundname";
    private static String TAG = "PushPlugin-NotificationService";
    public static final String TIMESTAMP = "timestamp";
    public static final String USER_ACTION = "userAction";
    private static NotificationService sInstance;
    private final Context mContext;
    private boolean mForeground;
    private List mNotifications;
    private String mRegistrationID;
    private String mSenderID;
    private List mWebViewReferences;

    public NotificationService(Context context)
    {
        mWebViewReferences = new ArrayList();
        mRegistrationID = null;
        mNotifications = new ArrayList();
        mForeground = false;
        mContext = context;
    }

    private void addNotification(JSONObject jsonobject)
    {
        mNotifications.add(jsonobject);
    }

    private void cleanUp()
    {
        Log.v(TAG, "Cleaning up");
        mWebViewReferences.clear();
        mNotifications.clear();
    }

    private JSONObject createNotificationJSON(Bundle bundle)
    {
        JSONObject jsonobject;
        JSONObject jsonobject1;
        try
        {
            jsonobject = new JSONObject();
            jsonobject1 = new JSONObject();
            Iterator iterator = bundle.keySet().iterator();
            do
            {
                if (!iterator.hasNext())
                {
                    break;
                }
                String s = (String)iterator.next();
                if (!parseSystemData(s, jsonobject, bundle))
                {
                    parseLegacyProperty(s, jsonobject, bundle);
                    parseJsonProperty(s, jsonobject, bundle, jsonobject1);
                }
            } while (true);
        }
        // Misplaced declaration of an exception variable
        catch (Bundle bundle)
        {
            Log.e(TAG, "extrasToJSON: JSON exception");
            return null;
        }
        jsonobject.put("payload", jsonobject1);
        jsonobject.put("foreground", isForeground());
        boolean flag;
        if (!isApplicationRunning())
        {
            flag = true;
        } else
        {
            flag = false;
        }
        jsonobject.put("coldstart", flag);
        jsonobject.put("timestamp", getTimeStamp());
        jsonobject.put("uuid", generateUUID());
        return jsonobject;
    }

    private WebViewReference createWebViewReference(CordovaWebView cordovawebview)
    {
        cordovawebview = new WebViewReference(this, cordovawebview);
        mWebViewReferences.add(cordovawebview);
        return cordovawebview;
    }

    private WebViewReference findWebViewReference(CordovaWebView cordovawebview)
    {
        Object obj = null;
        Iterator iterator = mWebViewReferences.iterator();
        WebViewReference webviewreference;
        do
        {
            webviewreference = obj;
            if (!iterator.hasNext())
            {
                break;
            }
            webviewreference = (WebViewReference)iterator.next();
        } while (webviewreference.getWebView() != cordovawebview);
        return webviewreference;
    }

    private void flushNotificationToWebView(WebViewReference webviewreference)
    {
        Log.v(TAG, (new StringBuilder()).append("flushNotificationToWebView() - Notifications.size(): ").append(mNotifications.size()).append(" -> webViewReference: ").append(webviewreference).toString());
        for (Iterator iterator = mNotifications.iterator(); iterator.hasNext(); webviewreference.sendNotification((JSONObject)iterator.next())) { }
    }

    private String generateUUID()
    {
        return UUID.randomUUID().toString();
    }

    public static NotificationService getInstance(Context context)
    {
        if (sInstance == null)
        {
            sInstance = new NotificationService(context);
        }
        return sInstance;
    }

    private String getTimeStamp()
    {
        TimeZone timezone = TimeZone.getTimeZone("UTC");
        SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        simpledateformat.setTimeZone(timezone);
        return simpledateformat.format(new Date());
    }

    private WebViewReference getWebViewReference(CordovaWebView cordovawebview)
    {
        WebViewReference webviewreference1 = findWebViewReference(cordovawebview);
        WebViewReference webviewreference = webviewreference1;
        if (webviewreference1 == null)
        {
            webviewreference = createWebViewReference(cordovawebview);
        }
        return webviewreference;
    }

    private boolean isRegistered()
    {
        return mRegistrationID != null;
    }

    private void notifyAllWebViews()
    {
        for (Iterator iterator = mWebViewReferences.iterator(); iterator.hasNext(); flushNotificationToWebView((WebViewReference)iterator.next())) { }
    }

    private void notifyRegisteredToAllWebViews()
    {
        for (Iterator iterator = mWebViewReferences.iterator(); iterator.hasNext(); ((WebViewReference)iterator.next()).notifyRegistered()) { }
    }

    private void parseJsonProperty(String s, JSONObject jsonobject, Bundle bundle, JSONObject jsonobject1)
        throws JSONException
    {
        if (!(bundle.get(s) instanceof String))
        {
            break MISSING_BLOCK_LABEL_41;
        }
        bundle = bundle.getString(s);
        if (!bundle.startsWith("{"))
        {
            break MISSING_BLOCK_LABEL_52;
        }
        jsonobject1.put(s, new JSONObject(bundle));
_L1:
        return;
        jsonobject;
        jsonobject1.put(s, bundle);
        return;
        if (bundle.startsWith("["))
        {
            try
            {
                jsonobject1.put(s, new JSONArray(bundle));
                return;
            }
            // Misplaced declaration of an exception variable
            catch (JSONObject jsonobject)
            {
                jsonobject1.put(s, bundle);
            }
            return;
        }
        if (!jsonobject.has(s))
        {
            jsonobject1.put(s, bundle);
            return;
        }
          goto _L1
    }

    private void parseLegacyProperty(String s, JSONObject jsonobject, Bundle bundle)
        throws JSONException
    {
        if (s.equals("message") || s.equals("msgcnt") || s.equals("soundname"))
        {
            jsonobject.put(s, bundle.get(s));
        }
    }

    private boolean parseSystemData(String s, JSONObject jsonobject, Bundle bundle)
        throws JSONException
    {
        boolean flag = false;
        if (s.equals("from") || s.equals("collapse_key"))
        {
            jsonobject.put(s, bundle.get(s));
            flag = true;
        } else
        if (s.equals("coldstart"))
        {
            jsonobject.put(s, bundle.getBoolean("coldstart"));
            return true;
        }
        return flag;
    }

    private void registerDevice()
    {
        if (mSenderID == null)
        {
            throw new IllegalArgumentException("sender ID is required in order to register this device for push notifications. You must specify the senderId in the ApplicationManifest or when calling the JavaScript API.");
        } else
        {
            GCMRegistrar.register(mContext, new String[] {
                mSenderID
            });
            return;
        }
    }

    public void addNotificationBackgroundCallBack(CordovaWebView cordovawebview, CallbackContext callbackcontext)
    {
        cordovawebview = getWebViewReference(cordovawebview);
        cordovawebview.setNotificationBackgroundCallBack(callbackcontext);
        flushNotificationToWebView(cordovawebview);
    }

    public void addNotificationForegroundCallBack(CordovaWebView cordovawebview, CallbackContext callbackcontext)
    {
        cordovawebview = getWebViewReference(cordovawebview);
        cordovawebview.setNotificationForegroundCallBack(callbackcontext);
        flushNotificationToWebView(cordovawebview);
    }

    public void addRegisterCallBack(CordovaWebView cordovawebview, CallbackContext callbackcontext)
    {
        cordovawebview = getWebViewReference(cordovawebview);
        cordovawebview.setRegisterCallBack(callbackcontext);
        if (isRegistered())
        {
            cordovawebview.notifyRegistered();
            return;
        } else
        {
            registerDevice();
            return;
        }
    }

    public boolean isApplicationRunning()
    {
        return ((AGCordovaApplicationInterface)mContext.getApplicationContext()).isRunning();
    }

    public boolean isForeground()
    {
        return mForeground;
    }

    public void onDestroy()
    {
        GCMRegistrar.onDestroy(mContext);
        cleanUp();
        sInstance = null;
    }

    public void onMessage(Bundle bundle)
    {
        bundle = createNotificationJSON(bundle);
        Log.v(TAG, (new StringBuilder()).append("onMessage() -> isForeground: ").append(isForeground()).append(" isApplicationRunning ").append(isApplicationRunning()).append(" notification: ").append(bundle).toString());
        addNotification(bundle);
        notifyAllWebViews();
    }

    public void onRegistered(String s)
    {
        mRegistrationID = s;
        notifyRegisteredToAllWebViews();
    }

    public void registerWebView(CordovaWebView cordovawebview)
    {
        getWebViewReference(cordovawebview);
    }

    public void removeWebView(CordovaWebView cordovawebview)
    {
        WebViewReference webviewreference = findWebViewReference(cordovawebview);
        if (webviewreference != null)
        {
            mWebViewReferences.remove(webviewreference);
            webviewreference.destroy();
            Log.v(TAG, (new StringBuilder()).append("removeWebView : ").append(cordovawebview).append(" - after remove -> mWebViewReferences: ").append(mWebViewReferences).toString());
        }
    }

    public void setForeground(boolean flag)
    {
        if (mForeground != flag)
        {
            Log.v(TAG, (new StringBuilder()).append("setForeground() -> oldValue: ").append(mForeground).append(" newValue: ").append(flag).toString());
            ((NotificationManager)mContext.getSystemService("notification")).cancelAll();
        }
        mForeground = flag;
    }

    public void setSenderID(String s)
    {
        if (s != null && s.trim().length() > 0)
        {
            mSenderID = s;
        }
    }

    public void unRegister()
    {
        Log.v(TAG, "unRegister");
        GCMRegistrar.unregister(mContext);
        mRegistrationID = null;
        cleanUp();
    }



}
