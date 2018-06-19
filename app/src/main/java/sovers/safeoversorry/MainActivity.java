package sovers.safeoversorry;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import android.widget.RadioButton;

import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;


import android.Manifest;

import sovers.safeoversorry.models.Message;
import sovers.safeoversorry.utility.MessageDialog;


public class MainActivity extends AppCompatActivity implements Serializable {

    private static final int PERMISSIONS_REQUEST = 100;
    private RadioButton Live, radio1, radio2, radio5;
    public static trip trip1 = new trip();
    private static final String TAG = "LoginActivity";
    private String mUserId;
    private String mMessage;
    int PLACE_PICKER_REQUEST = 1;
    java.lang.String destination1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Check whether GPS tracking is enabled//
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            finish();
        }
        //Check whether this app has access to the location permission//
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        //If the location permission has been granted, then start the TrackerService//
        if (permission == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "GPS possible!", Toast.LENGTH_SHORT).show();        }

        else {//If the app doesn’t currently have access to the user’s location, then request access//
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);        }



        //Recieve the follower String from shared preferences
        //SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences preferences = getSharedPreferences("MY_SHARED_PREF", MODE_PRIVATE);
        String follower = preferences.getString("follower", "");

        TextView followertxt = (TextView)findViewById(R.id.followerstxt);
        //print the follower string
        followertxt.setText(follower);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults) {

        //If the permission has been granted...//
        if (requestCode == PERMISSIONS_REQUEST && grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            //...then start the GPS tracking service//
            //  startTrackerService();
            Toast.makeText(this, "GPS possible!", Toast.LENGTH_SHORT).show();
        } else {

            //If the user denies the permission request, then display a toast with some more information//
            Toast.makeText(this, "Please enable location services to allow GPS tracking", Toast.LENGTH_SHORT).show();
        }
    }

    // Creaate a trip object and safe all informations
    // --> can be replaced by saving all the informations in the shared preferences!!!!
    public void create_new_trip (View v)
    { Button new_trip = (Button) v;

        //The button is changing its text
        ((Button) v).setText("clicked");

        //create a new trip
        //trip trip1 = new trip();

        //Find the right one in the Layout
        TextView txtinput=(TextView)findViewById(R.id.TripName);
        Live = findViewById(R.id.radioButtonLive);
        radio1 = findViewById(R.id.radioButton1);
        radio2 = findViewById(R.id.radioButton2);
        radio5 = findViewById(R.id.radioButton5);


        //Check the right radio Button
        if(Live.isChecked()){
            trip1.gps_frequency = 5000;
        }
        else if(radio1.isChecked()){
            trip1.gps_frequency = 60000;
        }

        else if(radio2.isChecked()){
            trip1.gps_frequency = 120000;
        }
        else if(radio5.isChecked()){
            trip1.gps_frequency = 300000;
        }

        //read the name input
        trip1.trip_name = txtinput.getText().toString();

        //add the destination input
        trip1.destination = destination1;


        java.lang.String trip_name = trip1.trip_name;
        java.lang.String destination = trip1.destination;
        int gps_frequency = trip1.gps_frequency;
        char[] follower = trip1.follower;
        char[] status = trip1.status;



        //convert object to string
        Gson gson = new Gson();
        String trip1_string = gson.toJson(trip1);

        //save to a shared preference
        SharedPreferences preferences = getSharedPreferences("MY_SHARED_PREF", MODE_PRIVATE);
        //SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("trip1", trip1_string);
        editor.putInt("trip1_frequence", trip1.gps_frequency);
        editor.putString("trip1_destination", trip1.destination);
        editor.putString("trip1_name", trip1.trip_name);
        editor.apply();

        mMessage = trip1_string;

        //start tracking
        startService(new Intent(this, TrackingService.class));

        //Notify the user that tracking has been enabled//
        Toast.makeText(this, "GPS tracking enabled", Toast.LENGTH_SHORT).show();
        /////////////////////////////////////////////////////////////////////////////////////////

        //Send the trip Informations to Firebase
        Log.i(TAG, "onClick: sending a new message");


        //Recieve String from shared preferences
        SharedPreferences preferences1 = getSharedPreferences("MY_SHARED_PREF", MODE_PRIVATE);
        //SharedPreferences preferences1 = PreferenceManager.getDefaultSharedPreferences(this);
        String mUserId = preferences1.getString("follower", "");



        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        if(!mMessage.equals("")){

            //create the new message
            Message message = new Message();
            message.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
            message.setMessage("NewTrip  " + mMessage);
            message.setTimestamp(getTimestamp());

            Log.i(TAG, "message_trip: getUid " + FirebaseAuth.getInstance().getCurrentUser().getUid());
            Log.i(TAG, "message_trip: dbnode_messages " + getString(R.string.dbnode_messages));
            Log.i(TAG, "message_trip: mUserId: " + mUserId);
            Log.i(TAG, "message_trip: getkey " + reference.push().getKey());
            Log.i(TAG, "message_trip: message " + message);

            //insert the new message
            reference
                    .child(getString(R.string.dbnode_messages))
                    .child(mUserId)
                    .child(trip1.trip_name)
                    .child(reference.push().getKey())
                    .setValue(message);



            //Toast.makeText(getActivity(), "message sent", Toast.LENGTH_SHORT).show();
        }else{
            //Toast.makeText(getActivity(), "enter a message", Toast.LENGTH_SHORT).show();
        }

   /*     //send the location via Firebase
        FirebaseDatabase.getInstance()
                .getReference()
                .push()
                .setValue(new ChatMessage(trip1_string,
                        FirebaseAuth.getInstance()
                                .getCurrentUser()
                                .getDisplayName())
                );

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String name = user.getUid();
        name = name + "/" + trip_name + "Destination";

        //Store the Informations in Firestore
        Map<String, Object> dataToSave = new HashMap<String, Object>();
        //  dataToSave.put("Name", trip_name );
        dataToSave.put("Destination", destination  );
        dataToSave.put("Followers", destination  );
        FirebaseFirestore.getInstance().document(name).set(dataToSave).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("Firestore", "Data was saved!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("Firestore", "Data was not saved!", e);
            }
        });
*/
        finish();
    }



    //all the following stuff is from the places API description
    public void new_destination (View v)
    {
        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("%s", place.getName()+",\n"+place.getAddress());
                destination1 = toastMsg;

                //Place place1 = PlacePicker.getLatLngBounds(data, this);

                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();

                TextView destinationtxt = (TextView)findViewById(R.id.destinationtxt);
                destinationtxt.setText(destination1);
            }
        }
    }

    public void add_followers (View v)
    {
        Intent intent = new Intent(MainActivity.this, UserListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        //check for extras from FCM
        if (getIntent().getExtras() != null) {
            Log.d(TAG, "initFCM: found intent extras: " + getIntent().getExtras().toString());
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "initFCM: Key: " + key + " Value: " + value);
            }
            String data = getIntent().getStringExtra("data");
            Log.d(TAG, "initFCM: data: " + data);
        }
        startActivity(intent);


    }

    private String getTimestamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));
        return sdf.format(new Date());
    }

    @Override
    public void onRestart() {
        super.onRestart();

    }
}
