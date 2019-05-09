package getu.app.com.getu.user_side_package.model;

import com.google.android.gms.maps.model.Marker;

import java.io.Serializable;

/**
 * Created by abc on 01/11/2017.
 */

public class MapUserList implements Serializable {
    public String profileimage;
    public String fullName;
    public String address;
    public String category;
    public String userId;
    public String latitude;
    public String longitude;
    public String userName;
    public String fireBaseId;
    public String fireBaseToken;
    public String email;
    public String contactNo;
    public String countryCode;
    public String description;
    public String onlineStatus;
    public String distance;
    public Marker marker;
}
