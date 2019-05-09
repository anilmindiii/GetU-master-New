package getu.app.com.getu.user_side_package.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.Marker;

import java.io.Serializable;

/**
 * Created by abc on 01/11/2017.
 */

public class UserList implements Serializable {

    public transient String profileimage = "";
    public transient String fullName = "";
    public transient String address = "";
    public transient String category = "";
    public transient String userId = "";
    public transient String latitude = "";
    public transient String longitude = "";
    public transient String userName = "";
    public transient String fireBaseId = "";
    public transient String fireBaseToken = "";
    public transient String email = "";
    public transient String contactNo = "";
    public transient String countryCode = "";
    public transient String description = "";
    public transient String onlineStatus = "";
    public transient String distance = "";
    public transient Marker marker;
}
