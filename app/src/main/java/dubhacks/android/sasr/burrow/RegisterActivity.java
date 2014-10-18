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
import android.widget.Toast;

import com.google.gson.JsonObject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class RegisterActivity extends Activity
            implements View.OnClickListener,
                        Callback<JsonObject> {

    public String TAG = RegisterActivity.class.getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        Button registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(this);
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
        String firstName = ((EditText)findViewById(R.id.first_name)).getText().toString();
        String lastName = ((EditText)findViewById(R.id.last_name)).getText().toString();
        String userName = ((EditText)findViewById(R.id.user_name)).getText().toString();
        String password = ((EditText)findViewById(R.id.password)).getText().toString();

        RegisterClient registerClient = new RegisterClient(this);
        registerClient.registerUser(firstName, lastName, userName, password);
    }


    @Override
    public void success(JsonObject jsonObject, Response response) {
        boolean success = jsonObject.get("success").getAsBoolean();
        Log.d(TAG, "" + success);
        SharedPreferences preferences = getSharedPreferences(getString(R.string.pref_location), Context.MODE_PRIVATE);
        preferences.edit().putBoolean(getString(R.string.user_registered), success).apply();
        Intent intent = new Intent();
        intent.putExtra("result", "test");
        setResult(RESULT_OK);
        Toast.makeText(RegisterActivity.this, "Welcome to Burrow, register with a home now!", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void failure(RetrofitError error) {
        Log.d(TAG, error.toString());
    }
}
