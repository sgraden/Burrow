package dubhacks.android.sasr.burrow;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by saryana on 10/18/14.
 */
public class SharedPrefs {
    private static SharedPreferences prefs;
    private static SharedPrefs init;

    public static SharedPreferences getInstance(Context context) {
        if (init == null) {
            init = new SharedPrefs(context);
        }
        return prefs;
    }

    private SharedPrefs(Context context) {
        prefs = context.getSharedPreferences(context.getString(R.string.pref_location), Context.MODE_PRIVATE);
    }

}
