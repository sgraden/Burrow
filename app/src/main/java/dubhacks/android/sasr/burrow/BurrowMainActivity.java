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
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class BurrowMainActivity extends Activity implements Callback<JsonObject>, View.OnClickListener {

    public String TAG = BurrowMainActivity.class.getCanonicalName();
    private SharedPreferences mPreferences;
    public static String NO_HOME = "noHome";
    private List<User> mUsers;
    private ListView mListView;

    public static void launch(Context context) {
        Intent i = new Intent(context, BurrowMainActivity.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = SharedPrefs.getInstance(this);
        setContentView(R.layout.activity_burrow_main);
        mListView = (ListView)findViewById(R.id.list_content_view);
        TextView homeName = (TextView)findViewById(R.id.random_text);
        String home = mPreferences.getString("homeName", NO_HOME);
        home = home.equals(NO_HOME) ? "" : home;
        homeName.setText(home);
        Button refreshButton = (Button) findViewById(R.id.refresh_button);
        refreshButton.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        boolean registered = mPreferences.getBoolean(getString(R.string.user_registered), false);
        String connectedHome = mPreferences.getString("homeConnected", NO_HOME);
        Log.d(TAG, "R " + registered + " C " + connectedHome);
        if (!registered || connectedHome.equals(NO_HOME)) {
            RegisterActivity.launch(this);
        } else {
            fetchUsers();
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
                                        mPreferences.edit().putString("ssid", "something").apply();
                                    } else {
                                        setSsidToCurrent();
                                    }
                                    fetchUsers();

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

    private void fetchUsers() {
        String connectedHome = mPreferences.getString("homeConnected", NO_HOME);
        RegisterClient.getInstance(BurrowMainActivity.this).getUsers(connectedHome, BurrowMainActivity.this);
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
        String ssid = wifiInfo.getSSID();
        mPreferences.edit().putString("ssid", ssid).apply();
    }

    @Override
    public void success(JsonObject jsonObject, Response response) {
        Log.d(TAG, jsonObject.toString());
        JsonArray array = jsonObject.getAsJsonArray("userInfo");
        parse(array);
    }

    private void parse(JsonArray array) {
        mUsers = new ArrayList<User>();
        for (int i = 0; i < array.size(); i++) {
            JsonObject element = array.get(i).getAsJsonObject();
            mUsers.add(new User(
                    element.get("userName").toString(),
                    element.get("firstName").toString(),
                    element.get("lastName").toString()
            ));
        }
        ListUserAdapter listUserAdapter = new ListUserAdapter(this, mUsers);
        mListView.setAdapter(listUserAdapter);
    }

    @Override
    public void failure(RetrofitError error) {
        Log.d(TAG, error.toString());
    }

    @Override
    public void onClick(View v) {
        fetchUsers();
    }
}
