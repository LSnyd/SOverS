package sovers.safeoversorry;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.Serializable;

@SuppressWarnings("serial")
public class trip implements Serializable {
    public  java.lang.String trip_name;
    public java.lang.String destination;
    public int gps_frequency;
    public char[] follower;
    public char[] status;

    public trip(){

    }



}
