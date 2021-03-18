package com.yourdigitalmenu.myweatherapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    public final static String TAG = LoginActivity.class.getSimpleName();
    public static final String USER_KEY = "userKey";

    // TODO: Not secure
    private static final String FB_CLIENT_ID = "457195402137733";
    private static final String FB_CLIENT_SECRET = "619b0cfb4cb55348765889dc6ec4c181";
    private static final String ACCESS_TOKEN = "457195402137733|WsAITo56JnpWBm-1PNtE5p1JO0Q";

    private final static String FB_LOGIN_SUCCESS_URL =
            "https://www.facebook.com/connect/login_success.html";
    private final static String FB_LOGIN_URL =
            "https://www.facebook.com/v10.0/dialog/oauth?client_id=" + FB_CLIENT_ID
                    + "&redirect_uri=" + FB_LOGIN_SUCCESS_URL
                    + "&state={}";
    private final static String FB_LOGIN_TOKEN_URL =
            "https://graph.facebook.com/v10.0/oauth/access_token?client_id=" + FB_CLIENT_ID
                    + "&redirect_uri=" + FB_LOGIN_SUCCESS_URL
                    + "&client_secret=" + FB_CLIENT_SECRET
                    + "&code=%s";
    private final static String FB_LOGIN_DEBUG_URL =
            "https://graph.facebook.com/debug_token?input_token=%s&access_token=" + ACCESS_TOKEN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        WebView webView = findViewById(R.id.webview);
        webView.loadUrl(FB_LOGIN_URL);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, final String url) {
                if (url.startsWith(FB_LOGIN_SUCCESS_URL)) {
                    getFBAccessToken(url);
                }
            }
        });
    }

    private void getFBAccessToken(final String url) {
        NetworkHelper.getJson(
                getFBLoginURLForUserByCode(getCodeFromQueryString(url)),
                LoginActivity.this,
                this::getFBUserId,
                this::showError);
    }

    private void getFBUserId(String json) {
        NetworkHelper.getJson(getFbLoginDebugUrl(json),
                LoginActivity.this,
                response -> {
                    User user = User.userHelper(response);
                    Intent intent = new Intent(LoginActivity.this, WeatherInfo.class);
                    intent.putExtra(USER_KEY, user.getUserId());
                    startActivity(intent);
                    finish();
                }, this::showError);
    }

    private void showError(VolleyError error) {
        Log.e(TAG, error.toString());
        Toast.makeText(LoginActivity.this, error.toString(), Toast.LENGTH_LONG).show();
    }

    private static String getCodeFromQueryString(String response) {
        Uri uri = Uri.parse(response);
        return uri.getQueryParameter("code");
    }

    private static String getFbLoginDebugUrl(String json) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        return String.format(FB_LOGIN_DEBUG_URL, jsonObject.optString("access_token"));
    }

    private static String getFBLoginURLForUserByCode(String code) {
        return String.format(FB_LOGIN_TOKEN_URL, code);
    }

}