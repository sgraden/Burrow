package dubhacks.android.sasr.burrow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.gson.JsonObject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class BurrowMainActivity extends Activity implements Callback<JsonObject> {

    public String TAG = BurrowMainActivity.class.getCanonicalName();
    private SharedPreferences preferences;
    public static String NO_HOME = "noHome";


    public static void launch(Context context) {
        Intent i = new Intent(context, BurrowMainActivity.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = SharedPrefs.getInstance(this);
//        boolean registered = preferences.getBoolean(getString(R.string.user_registered), false);
//        String connectedHome = preferences.getString("connectedHome", NO_HOME);
//        if (!registered) {
//            finish();
//        } else if (connectedHome.equals(NO_HOME)) {
//            Intent registerIntent = new Intent(this, RegisterActivity.class);
//            startActivity(registerIntent);
//            RegisterHome.launch(this);
//        }
        setContentView(R.layout.activity_burrow_main);
//        RegisterClient registerClient = RegisterClient.getInstance(this);
//        registerClient.getUsers(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        boolean registered = preferences.getBoolean(getString(R.string.user_registered), false);
        String connectedHome = preferences.getString("homeConnected", NO_HOME);
        Log.d(TAG, "R " + registered + " C " + connectedHome);
        if (!registered || connectedHome.equals(NO_HOME)) {
            RegisterActivity.launch(this);
        } else {
            RegisterClient.getInstance(this).getUsers(connectedHome, this);
        }
        Button toggle = (Button)findViewById(R.id.toggle_button);
        Log.d(TAG, toggle == null ? "Failed" : "good");
        if (toggle != null) {

            toggle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "toggleing");
                    RegisterClient.getInstance(BurrowMainActivity.this).updateUserInfo(
                            new Callback<JsonObject>() {
                                @Override
                                public void success(JsonObject jsonObject, Response response) {
                                    Log.d(TAG, jsonObject.toString());
                                    String status = jsonObject.get("success").toString();
                                    if (status.equals("connected")) {
                                        preferences.edit().putString("ssid", "something").apply();
                                    } else {
                                        setSsidToCurrent();
                                    }
                                    String connectedHome = preferences.getString("homeConnected", NO_HOME);
                                    RegisterClient.getInstance(BurrowMainActivity.this).getUsers(connectedHome, BurrowMainActivity.this);

                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    Log.d(TAG, error.toString());
                                }
                            });
                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.burrow_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setSsidToCurrent() {
        WifiManager wifiManager = (WifiManager)this.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String macAddress = wifiInfo.getMacAddress();
        String ssid = wifiInfo.getSSID();
        preferences.edit().putString("ssid", ssid).apply();
    }

    @Override
    public void success(JsonObject jsonObject, Response response) {
        Log.d(TAG, jsonObject.toString());
    }

    @Override
    public void failure(RetrofitError error) {
        Log.d(TAG, error.toString());
    }
}
