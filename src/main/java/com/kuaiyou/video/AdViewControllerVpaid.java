package com.kuaiyou.video;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;
import com.kuaiyou.utils.AdViewUtils;
import com.kuaiyou.utils.ConstantValues;
import com.kuaiyou.video.vast.model.Tracking;
import com.kuaiyou.video.vast.vpaid.BridgeEventHandler;
import com.kuaiyou.video.vast.vpaid.CreativeParams;
import com.kuaiyou.video.vast.vpaid.EventConstants;
import com.kuaiyou.video.vast.vpaid.VpaidBridge;
import com.kuaiyou.video.vast.vpaid.VpaidBridgeImpl;
import com.qq.e.comm.util.StringUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static com.kuaiyou.utils.ConstantValues.WEBVIEW_BASEURL;

public class AdViewControllerVpaid implements BridgeEventHandler {

    private static final String LOG_TAG = AdViewControllerVpaid.class.getSimpleName();
    //private static final String BASE_URL = "http://www.adview.com";
    private static final String ENVIRONMENT_VARS = "{ " +
            "slot: document.getElementById('adview-slot'), " +
            "videoSlot: document.getElementById('adview-videoslot'), " +
            "videoSlotCanAutoPlay: true }";

    private static final String VPAID_CREATIVE_URL_STRING = "[VPAID_CREATIVE_URL]";
    private static final String VPAID_BRIDGE_JS_STRING = "[VPAID_BRIDGE_JS]";
    private static final String MIME_TYPE = "text/html";

    private static final String test_HTML_SOURCE_FILE = "assets/test/vast_ad.html";
    private static final String test_bridge_js = "file:///sdcard/ad_vpaid_android_bridge.js";
    private static final String test_ui_js = "file:///sdcard/ad_vpaid_UI.js";

    private static final String VPAID_ANDROID_BRIDGE_JS_STRING = "[VPAID_BRIDGE_JS]";
    private static final String VPAID_ANDROID_UI_JS_STRING = "[VPAID_UI_JS]";
    /*
    "        <script src=\"[VPAID_BRIDGE_JS]\" type=\"text/javascript\"></script>\n" +
            "        <script src=\"[VPAID_UI_JS]\" type=\"text/javascript\"></script>\n" +
            "        <script src=\"[VPAID_CREATIVE_URL]\" type=\"text/javascript\"></script>\n" +
     */
    /*
    "        <video style=\"position:absolute; width:100%; height:100%; z-index:1;\" id=\"adview-videoslot\"></video>\n" +

    object-fit:contain;
    object-fit:fill;
    "        <video id=\"adview-videoslot\" width=\"100%\" height=\"100%\" preload=\"none\" style=\"-webkit-transform: translate3d(0, 0, 0)\">" +
     "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1, maximum-scale=1\" />\n" +
    * */
    private static final String VPAID_HTML =
            "<html>\n" +
            "    <header>\n" +
            "    </header>\n" +
            "    <meta name='viewport' content='width=device-width,initial-scale=1,maximum-scale=1,user-scalable=no'>" +
            "    <body style=\"margin:0\">\n" +
            "         <script type=\"text/javascript\">\n" +
            "            [VPAID_BRIDGE_JS]\n" +
            "         </script>\n" +
            "        <script src=\"[VPAID_CREATIVE_URL]\" type=\"text/javascript\"></script>\n" +
            "        <video id=\"adview-videoslot\" style=\"position:absolute; width:100%; height:100%; z-index:1; -webkit-transform: translate3d(0, 0, 0);\" ></video>\n" +
            "        <div id=\"adview-slot\" style=\"width:100%; height:100%; z-index: 3; position:absolute; -webkit-user-select: none;\" ></div>\n" +
            "    </body>\n" +
            "</html>";

    /*
    private static final String VPAID_HTML =
            "<html>\n" +
                    "  <head>\n" +
                    "  </head>\n" +
                    "\n" +
                    "  <body>\n" +
                    "    <div id=\"mainContainer\" style = \"width:100%; height:100%; position:relative;\">\n" +
                    "      <div id=\"content\" style = \"width:100%; height:100%; position:absolute;\">\n" +
                    "        <video id=\"contentElement\" style = \"width:100%; height:100%; overflow: hidden;\">\n" +
                    "        </video>\n" +
                    "      </div>\n" +
                    "      <div id=\"adContainer\" style = \"width:100%; height:100%; position:absolute;\"></div>\n" +
                    "    </div>\n" +
                    "         <script type=\"text/javascript\">\n" +
                    "            [VPAID_BRIDGE_JS]\n" +
                    "         </script>\n" +
                    "        <script src=\"[VPAID_CREATIVE_URL]\" type=\"text/javascript\"></script>\n" +
                    "  </body>\n" +
            "</html>";
    */


    private final VpaidBridge mVpaidBridge; //it must be final
    private /*static*/ String mAdParams;
    //end wilder

    private String mEndCardFilePath;
    private WebView mWebView;
    private boolean mIsWaitingForSkippableState;
    //private boolean mIsWaitingForWebView;
    private boolean mIsStarted;
    private String mVastFileContent;
    private AdControllerInterface adListener;

    private Activity parentACT;
    private String js_bridge;
    //private String js_ui;

    public AdViewControllerVpaid(Activity parent, String adParams) {
        mAdParams = adParams;
        parentACT = parent;
        //mAdSpotDimensions = adSpotDimensions;
        //mVastFileContent = vastFileContent;
        mVpaidBridge = new VpaidBridgeImpl(this, createCreativeParams());
        try {
            js_bridge = AdViewUtils.loadAssetsFile("ad_vbridge.js"); /*/assets/ad_vbridge.js */
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setListener(AdControllerInterface listener) {
        this.adListener = listener;
    }
    public VpaidBridge getBridge(){
        return mVpaidBridge;
    }



    @SuppressLint("NewApi")
    public void injectVPaidJs(final WebView wv) {
        if (wv == null) {
            return;
        }
        mWebView = wv;
        try {
            String mvpaidJs = js_bridge;
            AdViewUtils.logInfo("+++++++++ injectMvpaidJs ok +++++++++++" + mvpaidJs.length());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                wv.evaluateJavascript(mvpaidJs, new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        AdViewUtils.logInfo("+++++++++ evaJS-bridge: " + value + "+++++");
                        //adListener.onInjectJSdone();
                    }
                });

            } else {
                wv.loadUrl("javascript:" + mvpaidJs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //region AdController methods
    public void loadVPAIDURL(WebView wv, String vpaidURL) {
        //if (!vpaidURL.startsWith("https"))
        {
             String vpURL = vpaidURL;
            mWebView = wv;
            if(vpURL.startsWith("//")){
                vpURL = "http:" + vpURL;
            }
            AdViewUtils.logInfo("++++ prepare(): vpaid mode ++++");
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    try {
                        //initWebView();
                        String html = VPAID_HTML;
                        String finalHtml = html.replace(VPAID_CREATIVE_URL_STRING, vpaidURL);
                        finalHtml = finalHtml.replace(VPAID_BRIDGE_JS_STRING,js_bridge);
                        AdViewUtils.logInfo("++++ html: " + finalHtml);
                        //(wilder 2019) for google IMA3.0, if not set baseURL , it will cause "Uncaught SecurityError"
                        wv.loadDataWithBaseURL(ConstantValues.WEBVIEW_BASEURL,
                                                finalHtml,
                                                MIME_TYPE,
                                                StandardCharsets.UTF_8.toString(),
                                        null);

                    } catch (Exception e) {
                        AdViewUtils.logInfo("+++ Can't read assets: " + e.getMessage() + "++++");
                    }
                } else {
                    wv.loadUrl("javascript:" + vpURL);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    //apply for activity
    public void initBridgeWrapper() {
        mVpaidBridge.prepare();
    }

    public void setVideoFilePath(String filePath) {

    }

    public void setEndCardFilePath(String endCardFilePath) {

        mEndCardFilePath = endCardFilePath;
    }

    public void playAd() {
        mIsStarted = true;
        mVpaidBridge.startAd();
    }

    public void pause() {
        if (mIsStarted) {
            mVpaidBridge.pauseAd();
        }
    }

    public void resume() {

        mVpaidBridge.resumeAd();
    }

    public void dismiss() {
        mVpaidBridge.pauseAd();
        mVpaidBridge.stopAd();
        if (mWebView != null) {
            mWebView.clearCache(true);
            mWebView.clearFormData();
            mWebView.clearView();
        }
    }


    public void destroy() {
        if (mWebView != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mWebView.getParent() != null) {
                        ((ViewGroup) mWebView.getParent()).removeAllViews();
                    }
                    mWebView.clearHistory();
                    mWebView.clearCache(true);
                    mWebView.loadUrl("about:blank");
                    mWebView.pauseTimers();
                    mWebView = null;
                }
            });
        }
    }

    public void setVolume(float vol) {
        mVpaidBridge.setAdVolume(vol);
    }

    public void skipVideo() {
        mIsStarted = false;
        mVpaidBridge.skipAd(); //wilder 2019
    }
    //endregion

    //region BridgeEventHandler methods
    public void runOnUiThread(Runnable runnable) {
        if (parentACT != null) {
            parentACT.runOnUiThread(runnable);
        }
    }

    @Override
    public void callJsMethod(final String url) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                injectJavaScript(url);
                /*
                if (mWebView != null) {
                    mWebView.loadUrl("javascript:" + url);
                }*/
            }
        });
    }

    @Override
    public void onPrepared() {
        //mOnPreparedListener.onPrepared();
        if(adListener != null) {
            adListener.vpaid_onPrepared();
        }
    }

    @Override
    public void onAdSkipped() {
        if (!mIsStarted) {
            return;
        }
        mIsWaitingForSkippableState = true;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //mBaseAdInternal.dismiss();
                mIsWaitingForSkippableState = false;
                postEvent(EventConstants.SKIP);
            }
        });
        //mVpaidBridge.getAdSkippableState();
    }

    @Override
    public void onAdStopped() {
        if (!mIsStarted) {
            return;
        }
        postEvent(EventConstants.CLOSE);
        skipVideo();
    }

    @Override
    public void onSelfClosed() {

        postEvent(EventConstants.SELF_CLOSE);


    }
    @Override
    public void onGetVolumeResult(float vol) {
        postEvent(EventConstants.VOLUME, String.valueOf(vol));
    }

    @Override
    public void onGetSkippableState(boolean skippable) {
        //if (!mIsStarted) {
        //    return;
        //}
        if(adListener != null) {
            adListener.vpaid_setSkippableState(skippable);
        }
    }

    @Override
    public void onGetDurationResult(int result) {
        if(adListener != null) {
            adListener.vpaid_setDurationTime(result);
        }
    }

    @Override
    public void onDurationChanged() {

    }

    @Override
    public void onAdLinearChange() {

    }

    @Override
    public void onAdVolumeChange() {
        mVpaidBridge.getAdVolume(); //wilder 2019
    }

    @Override
    public void onAdImpression() {
        //(wilder 2019)adview will processImression at video activity oncreate()
        AdViewUtils.logInfo("mAdParams.getImpressions() ");
        /*
        for (String url : mAdParams.getImpressions()) {
            EventTracker.post(url);
            AdViewUtils.logInfo("mAdParams.getImpressions() " + url);
        }
        */

    }

    public void openUrl(@Nullable String url) {

        if(adListener != null) {
            adListener.vpaid_openUrl(url);
        }
        /*
        for (String trackUrl : mAdParams.getVideoClicks()) {
            EventTracker.post(trackUrl);
        }
        if (TextUtils.isEmpty(url)) {
            url = mAdParams.getVideoRedirectUrl();
        }
        Logging.out(LOG_TAG, "Handle external url");
        if (Utils.isOnline()) {
            Context context = mBaseAdInternal.getContext();
            Intent intent = new Intent(context, AdBrowserActivity.class);
            intent.putExtra(AdBrowserActivity.KEY_URL, url);
            intent.putExtra(AdBrowserActivity.APPKEY_TAG, mBaseAdInternal.getAppKey());
            intent.putExtra(AdBrowserActivity.FORMAT_TAG, mBaseAdInternal.getAdFormat());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mBaseAdInternal.onAdClicked();
            context.startActivity(intent);
        } else {
            Logging.out(LOG_TAG, "No internet connection");
        }
        */
    }

    public void trackError(String message) {
        //ErrorLog.postError(VastError.VPAID);
        if(adListener != null) {
            postEvent(EventConstants.ERROR);
        }
    }

    @Override
    public void postEvent(String eventType, String value) {
        if(adListener != null) {
            //String v = String.valueOf(value);
            adListener.vpaid_fireEvent(eventType, value);
        }
        /*
        for (Tracking tracking : mAdParams.getEvents()) {
            TrackingEvent event = new TrackingEvent(tracking.getText());
            if (tracking.getEvent().equalsIgnoreCase(EventConstants.PROGRESS)) {
                if (tracking.getOffset() == null) {
                    continue;
                }
                int sendEventTime = mAdParams.getDuration() - value;
                if (Utils.parseDuration(tracking.getOffset()) == sendEventTime) {
                    EventTracker.post(event.url);
                }
            }
        }
        */
    }

    @Override
    public void postEvent(String eventType) {
        //EventTracker.postEventByType(mAdParams.getEvents(), eventType);
        if(adListener != null) {
            adListener.vpaid_fireEvent(eventType, null);
        }
    }
    //endregion

    /*
    //region other methods
    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    private void initWebView() {
        mWebView = new WebView(mBaseAdInternal.getContext());
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        if (Utils.isDebug()) {
            webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
            mWebView.clearCache(true);
        }
        mWebView.setWebChromeClient(new WebChromeClient());
        mIsWaitingForWebView = true;
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (mIsWaitingForWebView) {
                    mVpaidBridge.prepare();
                    Logging.out(LOG_TAG, "Init webView done");
                    mIsWaitingForWebView = false;
                }
            }
        });
        CookieManager.getInstance().setAcceptCookie(true);
        mWebView.addJavascriptInterface(mVpaidBridge, "android");
    }
    */

    public void closeSelf() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //mIsWaitingForWebView = false;
                mVpaidBridge.stopAd();
                //mBaseAdInternal.dismiss();
                if(adListener != null) {
                    adListener.vpaid_dismiss();
                }
            }
        });
    }

    private CreativeParams createCreativeParams() {
        //CreativeParams result = new CreativeParams(-1, -1, "normal", 720);
        //int[] widthAndHeight = AdViewUtils.getWidthAndHeight(parentACT, true,false);

        CreativeParams result = new CreativeParams( -1, //widthAndHeight[0]/*width*/,
                                                     -1,//widthAndHeight[1] /*height*/,
                                                    "normal", 720);
        result.setAdParameters("{'AdParameters':'" + mAdParams + "'}");
        result.setEnvironmentVars(ENVIRONMENT_VARS);
        return result;
    }
    //endregion



    @SuppressLint("NewApi")
    private void injectJavaScript(String js) {
        if (mWebView != null) {
            injectJavaScript(mWebView, js);
        }
    }

    @SuppressLint("NewApi")
    private void injectJavaScript(WebView webView, String js) {
        try {
            if (!TextUtils.isEmpty(js)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    AdViewUtils.logInfo("evaluating js: " + js);
                    webView.evaluateJavascript(js, new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            AdViewUtils.logInfo(
                                    "evaluate js complete: " + value);
                        }
                    });
                } else {
                    AdViewUtils.logInfo("loading url: " + js);
                    webView.loadUrl("javascript:" + js);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
