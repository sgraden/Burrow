package dubhacks.android.sasr.burrow;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class RegisterActivity extends Activity implements View.OnClickListener {

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
}
