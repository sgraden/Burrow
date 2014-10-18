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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class RegisterActivity extends Activity
            implements View.OnClickListener,
                        Callback<JsonObject> {

    public String TAG = RegisterActivity.class.getCanonicalName();
    SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        preferences = SharedPrefs.getInstance(this);
        boolean registered = preferences.getBoolean(getString(R.string.user_registered), false);
        String connectedHome = preferences.getString("connectedHome", BurrowMainActivity.NO_HOME);
        if (registered && !connectedHome.equals("noHome")) {
            BurrowMainActivity.launch(RegisterActivity.this);;
        }
        Button registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(this);
        final CheckBox checkBox = (CheckBox)findViewById(R.id.checkBox);
        final EditText hint = (EditText)findViewById(R.id.connect_home_text);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    hint.setText(R.string.connect_home_name);
                } else {
                    hint.setText("Connect to a Home Name");
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.register, menu);
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



    /**
     * Listerner for the button. Eventually we want to push this elsewhere since we
     * don't need this logic in the the activity start.
     * @param v
     */
    @Override
    public void onClick(View v) {
        String firstName = getIdString(R.id.first_name);
        String lastName = getIdString(R.id.last_name);
        String userName = getIdString(R.id.user_name);
        String password = getIdString(R.id.password);
        String homeName = getIdString(R.id.connect_home_text);
        boolean isAdmin = ((CheckBox)findViewById(R.id.checkBox)).isChecked();
        if (firstName.isEmpty() || lastName.isEmpty() || userName.isEmpty() || password.isEmpty() || homeName.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Please fill out all fields", Toast.LENGTH_LONG).show();
        } else {
            RegisterClient.getInstance(this).registerUser(firstName,
                    lastName, userName, password, homeName, isAdmin, this);
        }
    }

    private String getIdString(int id) {
        return ((EditText)findViewById(id)).getText().toString();
    }

    @Override
    public void success(JsonObject jsonObject, Response response) {
        Log.d(TAG, "" + jsonObject);
        boolean success = jsonObject.get("success").getAsBoolean();
        String homeid = jsonObject.get("homeid").getAsString();
        String ssid = jsonObject.get("ssid").getAsString();


        preferences.edit().putBoolean(getString(R.string.user_registered), success).apply();
        preferences.edit().putString("homeConnected", homeid).apply();
        preferences.edit().putString("ssid", ssid).apply();
        BurrowMainActivity.launch(RegisterActivity.this);
    }

    @Override
    public void failure(RetrofitError error) {
        Log.d(TAG, error.toString());
    }

    public static void launch(Context c) {
        Intent i = new Intent(c, RegisterActivity.class);
        c.startActivity(i);
    }
}
