package dubhacks.android.sasr.burrow;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonArray;
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
    private static boolean toggle = false;
    private Context context;

    private SharedPreferences preferences;
    private RegisterClientInterface clientInterface;
    private TelephonyManager telephonyManager;

    private static RegisterClient instance;

    public static RegisterClient getInstance(Context context) {
        if (instance == null) {
            instance = new RegisterClient(context.getApplicationContext());
        }
        return instance;
    }

    public RegisterClient(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(context.getString(R.string.pref_location), Context.MODE_PRIVATE);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .build();
        clientInterface = restAdapter.create(RegisterClientInterface.class);
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

    }

    /**
     * Register all the calls we are making
     */
    interface RegisterClientInterface {
        @POST("/user/register")
        void registerUser(@Body Map<String, Map<String, String>> body, Callback<JsonObject> cb);

//        @POST("/home/register")
//        void registerHome(@Body Map<String, Map<String, String>> body, Callback<JsonObject> cb);

//        @POST("/home/connect")
//        void connectHome(@Body Map<String, String> body, Callback<JsonObject> cb);
        @POST("/user/update")
        void updateUser(@Body Map<String, String> map, Callback<JsonObject> cb);

        @POST("/users")
        void getHomeUsers(@Body Map<String, String> map, Callback<JsonObject> cb);
    }

    public void updateUserInfo(Callback<JsonObject> cb) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("deviceId", getDeviceId());
        map.put("isConnectedToHome", wifiMatch(toggle));
        toggle = !toggle;
        Log.d(TAG, "Making toggle request");
        clientInterface.updateUser(map, cb);
    }

    private String wifiMatch(boolean match) {
//        String ssid = preferences.getString("ssid", "FUCK");
        if (match) {
            return "connected";
        } else {
            return "disconnected";
        }
    }

    public void getUsers(String connectedHome, Callback<JsonObject> cb) {
        Log.d(TAG, "Getting homes for " + connectedHome);
        Map<String, String> map = new HashMap<String, String>();
        map.put("homeId", connectedHome);
        clientInterface.getHomeUsers(map, cb);
    }

    /**
     * Registers a user with the DB and adds their personal and phone info
     */
    public void registerUser(String firstName, String lastName,
                             String username, String password, String homeName, boolean isAdmin, Callback<JsonObject> cb) {
        Map<String, Map<String, String>> body = new HashMap<String, Map<String, String>>();
        setUpUserInfo(body, firstName, lastName, username, password, isAdmin);
        setUpHomeInfo(body, homeName);
        clientInterface.registerUser(body, cb);
    }


    /**
     * Adds all the appropriate user information
     * @param body Body being sent to server
     * @param firstName FN
     * @param lastName LN
     * @param username UN
     * @param password Pass
     * @param isAdmin
     */
    private void setUpUserInfo(Map<String, Map<String, String>> body, String firstName,
                               String lastName, String username, String password, boolean isAdmin) {
        HashMap<String, String> userInfo = new HashMap<String, String>();
        userInfo.put("firstName", firstName);
        userInfo.put("lastName", lastName);
        userInfo.put("userName", username);
        userInfo.put("password", password);
        userInfo.put("isAdmin", isAdmin ? "true" : "false");

        preferences.edit().putString("firstName", firstName)
                .putString("lastName", lastName)
                .putString("userName", username).apply();


        String phoneNumber = telephonyManager.getLine1Number();
        phoneNumber = (phoneNumber == null || phoneNumber.isEmpty()) ? "-1" : phoneNumber;
        String deviceId = getDeviceId();
        deviceId = (deviceId == null || deviceId.isEmpty()) ? "" + Math.random() : deviceId;

        Log.d(TAG, deviceId);
        userInfo.put("phoneNumber", phoneNumber);
        userInfo.put("deviceId", deviceId);
        body.put("userInfo", userInfo);
    }


    /**
     * Sets up the house information
     */
    public void setUpHomeInfo(Map<String,Map<String,String>> body, String homeName) {
        Map<String, String> houseInfo = new HashMap<String, String>();
        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String macAddress = wifiInfo.getMacAddress();
        String ssid = wifiInfo.getSSID();
        houseInfo.put("macAddress", macAddress);
        houseInfo.put("ssid", ssid);
        houseInfo.put("homeName", homeName);
        body.put("houseInfo", houseInfo);
    }

    /**
     * @return Gets the unique device id
     */
    private String getDeviceId() {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

}