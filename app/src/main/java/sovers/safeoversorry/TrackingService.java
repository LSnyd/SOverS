package sovers.safeoversorry;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.os.IBinder;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.Manifest;
import android.location.Location;
import android.app.Notification;
import android.content.pm.PackageManager;
import android.app.PendingIntent;
import android.app.Service;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import android.support.v4.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import sovers.safeoversorry.models.Message;

public class TrackingService extends Service  {


    private static final String TAG = TrackingService.class.getSimpleName();

    private String mMessage;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("location", "service start");
        buildNotification();
        requestLocationUpdates();
    }

    //Create the persistent notification//
    private void buildNotification() {
        String stop = "stop";
        registerReceiver(stopReceiver, new IntentFilter(stop));
        PendingIntent broadcastIntent = PendingIntent.getBroadcast(
                this, 0, new Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT);

        // Create the persistent notification//
        Notification.Builder builder = new Notification.Builder(this)
                .setContentTitle(getString(R.string.app_name))
               // .setContentText(getString(R.string.tracking_enabled_notif))

                //Make this notification ongoing so it can’t be dismissed by the user//
                .setOngoing(true)
                .setContentIntent(broadcastIntent)
                .setSmallIcon(R.drawable.tracking_enabled);
        startForeground(1, builder.build());
    }

    protected BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //Stop the Service//
            stopSelf();

            //Unregister the BroadcastReceiver when the notification is tapped//
            unregisterReceiver(stopReceiver);






        }
    };


    //Initiate the request to track the device's location//
    private void requestLocationUpdates() {
        LocationRequest request = new LocationRequest();

        Log.i("location", "got a location start");

        //Specify how often your app should request the device’s location//
        request.setInterval(100000);

        //Get the most accurate location data available//
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);

        //R.string.firebase_database_url

        //final String path = getString(R.string.firebase_path);
        final String path = getString(R.string.firebase_database_url);
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        //If the app currently has access to the location permission...//
        if (permission == PackageManager.PERMISSION_GRANTED) {

            //...then request location updates//
            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {

                    //Recieve String from shared preferences
                    //SharedPreferences preferences1 = PreferenceManager.getDefaultSharedPreferences(this.getContext());
                    //String mUserId = preferences1.getString("follower", "");



                    //mLoginPreferences=getSharedPreferences(getResources().getString(R.string.pref_name), Context.MODE_PRIVATE);

                  /*  Location location = locationResult.getLastLocation();


                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    if(!mMessage.equals("")){

                        //create the new message
                        Message message = new Message();
                        message.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        message.setPosition(location);
                        message.setTimestamp(getTimestamp());


                        Log.i(TAG, "message_trip: getUid " + FirebaseAuth.getInstance().getCurrentUser().getUid());
                        Log.i(TAG, "message_trip: dbnode_messages " + getString(R.string.dbnode_messages));
                       // Log.i(TAG, "message_trip: mUserId: " + mUserId);
                        Log.i(TAG, "message_trip: getkey " + reference.push().getKey());
                        Log.i(TAG, "message_trip: message " + message);

                        //insert the new message
                        reference
                                .child(getString(R.string.dbnode_messages))
                                .child(mUserId)
                                .child(reference.push().getKey())
                                .setValue(message);



                        //Toast.makeText(getActivity(), "message sent", Toast.LENGTH_SHORT).show();
                    }else{
                        //Toast.makeText(getActivity(), "enter a message", Toast.LENGTH_SHORT).show();
                    }

   /*                  //Get a reference to the database, so your app can perform read and write operations//
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path);
                    Location location = locationResult.getLastLocation();
                    if (location != null) {


                        Log.i("location", "got a location");

                        FirebaseDatabase.getInstance()
                                .getReference()
                                .push()
                                .setValue(new ChatMessage(location.toString(),
                                        FirebaseAuth.getInstance()
                                                .getCurrentUser()
                                                .getDisplayName())
                              );


                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String name = user.getUid();
                        name = name + "/" + MainActivity.trip1.trip_name + "Location";

                        //Store the Informations in Firestore
                        Map<String, Object> dataToSave = new HashMap<String, Object>();
                        dataToSave.put("Latitude", location.getLatitude() );
                        dataToSave.put("Longitude", location.getLongitude() );
                        FirebaseFirestore.getInstance().document(name).set(dataToSave).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("Firestore", "Data was saved!");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("Firestore", "Data was not saved!", e);
                            }
                        });


                    }*/
                }
            }, null);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Tracking Stopped.", Toast.LENGTH_LONG).show();
    }

    private String getTimestamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));
        return sdf.format(new Date());
    }
}
