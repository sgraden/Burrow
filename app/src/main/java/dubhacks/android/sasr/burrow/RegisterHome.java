package dubhacks.android.sasr.burrow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.JsonObject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class RegisterHome extends Activity implements View.OnClickListener, Callback<JsonObject> {

    private String TAG = RegisterHome.class.getCanonicalName();
    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = SharedPrefs.getInstance(this);
        String connectedHome = preferences.getString("connectedHome", BurrowMainActivity.NO_HOME);
        if (!connectedHome.equals(BurrowMainActivity.NO_HOME)) {
            Intent homeIntent = new Intent(RegisterHome.this, BurrowMainActivity.class);
            startActivity(homeIntent);
        }
        setContentView(R.layout.activity_register_home);

        Button connectToHomeButt = (Button) findViewById(R.id.connect_to_home_by_name);
        Button registerNewHome = (Button) findViewById(R.id.home_register_button);
        if (connectToHomeButt != null)
            connectToHomeButt.setOnClickListener(this);
        if (registerNewHome != null)
            registerNewHome.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.register_home, menu);
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

    public static void launch(Context context) {
        Intent intent = new Intent(context, RegisterHome.class);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
//        int id = v.getId();
//        Log.d(TAG, "CLICKED " + id);
//        RegisterClient registerClient = new RegisterClient(RegisterHome.this);
//        if (id == R.id.home_register_button) {
//            String newHome = ((EditText)findViewById(R.id.home_name)).getText().toString();
//            if (!newHome.isEmpty()) {
//                preferences.edit().putString("connectedHome", newHome).apply();
//                registerClient.registerHome(newHome, this);
//            } else {
//                Log.e(TAG, "Empty new home string");
//            }
//        } else if (id == R.id.connect_to_home_by_name) {
//            String existingHome = ((EditText)findViewById(R.id.connect_home_text)).getText().toString();
//            if (!existingHome.isEmpty()) {
//                preferences.edit().putString("connectedHome", existingHome).apply();
//                registerClient.connectToHome(existingHome, this);
//            } else {
//                Log.e(TAG, "Empty home connecting string");
//            }
//
//        }
    }

    @Override
    public void success(JsonObject jsonObject, Response response) {
        Log.d(TAG, "Success");
        Intent homeIntent = new Intent(RegisterHome.this, BurrowMainActivity.class);
        startActivity(homeIntent);
    }

    @Override
    public void failure(RetrofitError error) {
        Log.d(TAG, "fail");
    }
}
