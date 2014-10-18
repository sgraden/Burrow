package dubhacks.android.sasr.burrow;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.*;
import retrofit.http.QueryMap;

/**
 * This is going to make the request to register the user.
 */
public class RegisterClient {

    private String TAG = getClass().getCanonicalName();
    private static String ENDPOINT = "http://108.179.144.202:8008";

    private Context context;
    private Map<String, Map<String, String>> upHomeInfo;

    public RegisterClient(Context context) {
        this.context = context;
    }

    interface RegisterClientInterface {
        @POST("/data")
        void registerUser(@Body Map<String, Map<String, String>> body, Callback<JsonObject> cb);
    }

    public void registerUser(String firstName, String lastName, String username, String password) {
        Map<String, Map<String, String>> body = new HashMap<String, Map<String, String>>();
        setUpUserInfo(body, firstName, lastName, username, password);
        setUpHomeInfo(body);


        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .build();
        RegisterClientInterface clientInterface = restAdapter.create(RegisterClientInterface.class);

        clientInterface.registerUser(body, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                boolean success = jsonObject.get("success").getAsBoolean();
                Log.d(TAG, "" + success);
                SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.pref_location), Context.MODE_PRIVATE);
                preferences.edit().putBoolean(context.getString(R.string.user_registered), success).apply();


            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, error.toString());
            }
        });
    }

    /**
     * Adds all the appropriate user information
     * @param body Body being sent to server
     * @param firstName FN
     * @param lastName LN
     * @param username UN
     * @param password Pass
     */
    private void setUpUserInfo(Map<String, Map<String, String>> body, String firstName,
                               String lastName, String username, String password) {
        HashMap<String, String> userInfo = new HashMap<String, String>();
        userInfo.put("firstName", firstName);
        userInfo.put("lastName", lastName);
        userInfo.put("userName", username);
        userInfo.put("password", password);

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String phoneNumber = telephonyManager.getLine1Number();
        String deviceId = telephonyManager.getDeviceId();
        userInfo.put("phoneNumber", phoneNumber);
        userInfo.put("deviceId", deviceId);

        body.put("userInfo", userInfo);
    }


    public void setUpHomeInfo(Map<String,Map<String,String>> body) {
        Map<String, String> houseInfo = new HashMap<String, String>();
        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String macAddress = wifiInfo.getMacAddress();
        String ssid = wifiInfo.getSSID();
        houseInfo.put("macAddress", macAddress);
        houseInfo.put("ssid", ssid);
        body.put("houseInfo", houseInfo);
    }

}