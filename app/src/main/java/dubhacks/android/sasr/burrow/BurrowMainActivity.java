package dubhacks.android.sasr.burrow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class BurrowMainActivity extends Activity {

    public String TAG = this.getClass().getCanonicalName();
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_burrow_main);

        preferences = SharedPrefs.getInstance(this);
        boolean registered = preferences.getBoolean(getString(R.string.user_registered), false);
        if (!registered) {
            Intent registerIntent = new Intent(this, RegisterActivity.class);
//            startActivity(registerIntent);
            startActivityForResult(registerIntent, -1);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != -1) return;
        if (resultCode == RESULT_OK) {
            String home = preferences.getString("home", "noHome");
            if (home.equals("noHome")) {
                Intent intent = new Intent(BurrowMainActivity.this, RegisterHome.class);
                startActivity(intent);
            }
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
}
