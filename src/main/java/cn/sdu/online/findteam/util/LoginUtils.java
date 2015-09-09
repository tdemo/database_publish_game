package cn.sdu.online.findteam.util;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.wukong.auth.ALoginParam;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import cn.sdu.online.findteam.activity.LoginActivity;
import cn.sdu.online.findteam.share.MyApplication;

/**
 * Created by wn on 2015/8/13.
 */
public class LoginUtils {
    private static final String DEMO_LOGIN_URL = "http://202.194.14.195:8080/findTeamChat/r/app/";

/*    public static ALoginParam registerRequest(String username, String password) {
        return request("reg", username, password);
    }*/

    public static ALoginParam loginRequest(String username, String password) {
        return request("login", username, password);
    }

    private static ALoginParam request(String type, String username, String password) {
        HttpURLConnection conn = null;
        try {
            username = URLEncoder.encode(username, "utf-8");
            password = URLEncoder.encode(password, "utf-8");
            URL url = new URL(DEMO_LOGIN_URL + type + "?devType=android&username=" + username + "&password=" + password);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("GET");

            int statusCode = conn.getResponseCode();
            if(statusCode == 200){
                InputStream in = conn.getInputStream();
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buffer = new byte[2048];
                int count;
                while((count = in.read(buffer, 0, 2048)) != -1)
                    out.write(buffer, 0, count);
                String result = new String(out.toByteArray(),"utf-8");
                if(!TextUtils.isEmpty(result)){
                    try {
                        JSONObject object = new JSONObject(result);
                        boolean ret = object.getBoolean("success");
                        JSONObject data = object.getJSONObject("imSignModel");
                        MyApplication.getInstance().getSharedPreferences("loginmessage", Context.MODE_PRIVATE).
                                edit().putLong("loginID", data.getLong("openId")).apply();
                        if(ret) {
                            return buildLoginParam(data.getLong("openId"), data.getString("nonce"),
                                    data.getLong("timestamp"), data.getString("signature"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(conn != null){
                conn.disconnect();
            }
        }
        return null;
    }

    public static ALoginParam buildLoginParam(long openId, String nonce, long timestamp, String signature) throws UnsupportedEncodingException {
        ALoginParam param = new ALoginParam();
        param.domain = "findTeams";
        param.openId = openId;
        param.nonce = nonce;
        param.timestamp = timestamp;
        param.signature = signature;
        return param;
    }
}
