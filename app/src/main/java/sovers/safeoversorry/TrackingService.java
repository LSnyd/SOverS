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

    private android.location.Location oldlocation;
    private android.location.Location currentlocation;

    private static final String TAG = TrackingService.class.getSimpleName();

    private String mMessage;

    private String mUserId;
    private String trip1_name;
    private String trip1_Lat;
    private String trip1_Lng;
    private int trip1_frequence;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("location", "service start");

        oldlocation = null;

        //get all important trip informations
        SharedPreferences preferences = getSharedPreferences("MY_SHARED_PREF", MODE_PRIVATE);
        mUserId = preferences.getString("follower", "");
        trip1_name = preferences.getString("trip1_name", "");
        trip1_Lat = preferences.getString("trip1_Lat", "");
        trip1_Lng = preferences.getString("trip1_Lng", "");
        trip1_frequence = preferences.getInt("trip1_frequence", 1000);


        buildNotification();
        requestLocationUpdates();
    }









    //Create the persistent notification//
    private void buildNotification() {
        String stop = "stop";
        registerReceiver(stopReceiver, new IntentFilter(stop));
        PendingIntent broadcastIntent = PendingIntent.getBroadcast(
                this, 0, new Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT);

        //The notification, that can be clicked, to stop the tracking
        // Create the persistent notification//
        Notification.Builder builder = new Notification.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("We'll keep an eye on you")

                //Make this notification ongoing so it can’t be dismissed by the user//
                .setOngoing(true)
                .setContentIntent(broadcastIntent)
                .setSmallIcon(R.drawable.tracking_enabled);
        startForeground(1, builder.build());
    }


    //If the notification is klicked
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

        //request.setInterval(trip1_frequence);
        request.setInterval(1000);

        //Get the most accurate location data available//
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);

        //R.string.firebase_database_url

        //final String path = getString(R.string.firebase_path);
        final String path = getString(R.string.firebase_database_url);
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        SharedPreferences preferences = getSharedPreferences("MY_SHARED_PREF", MODE_PRIVATE);
        String mUserId = preferences.getString("follower", "");

        //Specify how often your app should request the device’s location//

        //If the app currently has access to the location permission...//
        if (permission == PackageManager.PERMISSION_GRANTED) {

            //...then request location updates//
            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {

                    Location location = locationResult.getLastLocation();

                    if(!location.equals("")){


                    //mLogi

                    travelInfo(location);

                    }else{
                        //Toast.makeText(getActivity(), "enter a message", Toast.LENGTH_SHORT).show();
                    }

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

    public void travelInfo (android.location.Location location){

        float distance_old = 666;
        float distance = 666;
        float speed = 666;
        String status = "first status";

        //set the current location
        if (oldlocation == null)
        {oldlocation = location;}
        else
        {currentlocation = location;}

        //calculate the distance to the destination

        Location Destination = new Location("newlocation");
        Destination.setLatitude(Double.valueOf(trip1_Lat));
        Destination.setLongitude(Double.valueOf(trip1_Lng));

       // distance = currentlocation.distanceTo(Destination); //in m

        //calculate the speed
        distance_old = currentlocation.distanceTo(oldlocation);
       speed = (distance_old/1000/((trip1_frequence/1000)/3600));

        //the current becomes the old Location"Distance: " + distance + " Speed: " + speed + "km/h"
        oldlocation = currentlocation;


        //Check if the publisher is fast enough
        if (speed >= 3)
        {
            status = "ok";            }
        else
        {
            status = "not ok";        }


        sendMessage(distance, speed, status);
    }

    public void sendMessage (float distance, float speed, String status){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

            //create the new message
            Message message = new Message();
            message.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
            message.setMessage("Distance: " + distance + " Speed: " + speed + "km/h");
            message.setStatus(status);
            message.setSpeed(speed);
            message.setDistance(distance);
            message.setTimestamp(getTimestamp());

            Log.i("location", "message_trip: getUid " + FirebaseAuth.getInstance().getCurrentUser().getUid());
            Log.i("location", "message_trip: dbnode_messages " + getString(R.string.dbnode_messages));
            Log.i("location", "message_trip: Info: " + "Distance: " + distance + " Speed: " + speed + "km/h");
            Log.i("location", "message_trip: getkey " + reference.push().getKey());
            Log.i("location", "message_trip: message " + message);

            //insert the new message
            reference
                    .child(getString(R.string.dbnode_messages))
                    .child(mUserId)
                    .child(trip1_name)
                    .child(reference.push().getKey())
                    .setValue(message);



    }
}
