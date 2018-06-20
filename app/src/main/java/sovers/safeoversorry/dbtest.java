package sovers.safeoversorry;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;

import sovers.safeoversorry.models.Trip_summary;
import sovers.safeoversorry.models.User;
import sovers.safeoversorry.utility.UserAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import static java.lang.System.out;


public class dbtest extends AppCompatActivity {





    private static final String TAG = "UserListActivity";

    //firebase
    private FirebaseAuth.AuthStateListener mAuthListener;

    //widgets
    private RecyclerView mRecyclerView;

    //vars
    private ArrayList<User> mUsers;
    private UserAdapter mUserAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbtest);

    }

    public void dbpull (View v) {

        getUserList();
    }

    private void getUserList() throws NullPointerException {
        Log.d(TAG, "getTokenList: getting a list of all Tokens");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference.child(getString(R.string.dbnode_users)).orderByChild("name").equalTo("lisa");

       // Query query = reference.child("questions").orderByChild("from").equalTo("this");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Trip_summary user = snapshot.getValue(Trip_summary.class);
                    Log.d("ChangeDataDB", "onDataChange: found a user: " + user.getName());
                    //            mUsers.add(user)
                    TextView mydbtxt = (TextView)findViewById(R.id.dbtxt);
                    mydbtxt.setText(user.toString());
                }

                mUserAdapter.notifyDataSetChanged();

            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }




        });


    }

}







/* WICHTIG: RICHTIGE VERSION

    public void dbpull (View v) {

        getUserList();
    }

   private void getUserList() throws NullPointerException {
        Log.d(TAG, "getTokenList: getting a list of all Tokens");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference.child(getString(R.string.dbnode_users));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

           for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
               Trip_summary user = snapshot.getValue(Trip_summary.class);
                  Log.d("ChangeDataDB", "onDataChange: found a user: " + user.getName());
        //            mUsers.add(user)
               TextView mydbtxt = (TextView)findViewById(R.id.dbtxt);
               mydbtxt.setText(user.toString());
               }

                mUserAdapter.notifyDataSetChanged();

            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }




        });


    }

}
*/