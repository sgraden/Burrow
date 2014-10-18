package dubhacks.android.sasr.burrow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by saryana on 10/18/14.
 */
public class ListUserAdapter extends ArrayAdapter<User> {
    private Context mContext;
    private List<User> mUsers;

    public ListUserAdapter(Context context, List<User> users) {
        super(context, R.layout.list_items, users);
        mContext = context;
        mUsers = users;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
//        layoutInflater.inflate()
//        View rowView =
        convertView = layoutInflater.inflate(R.layout.list_items, parent, false);
        TextView secondRow = (TextView) convertView.findViewById(R.id.first_line);
        TextView firstRow = (TextView)  convertView.findViewById(R.id.second_line);
        User user = mUsers.get(position);
        firstRow.setText(user.userName.replace("\"", ""));
        secondRow.setText("FirstName: " + user.firstName.replace("\"", "") + "\n Last Name: " + user.lastName.replace("\"", ""));
        return convertView;
    }
}
