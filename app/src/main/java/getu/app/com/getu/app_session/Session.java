package getu.app.com.getu.app_session;

import android.content.Context;
import android.content.SharedPreferences;

import getu.app.com.getu.model.UserDetails;
import getu.app.com.getu.util.Constant;

/**
 * Created by abc on 28/10/2017.
 */

public class Session {

    private SharedPreferences mypref ;
    private SharedPreferences.Editor editor;
    private static final String PREF_NAME = "GETU";
    private static final String IS_LOGEDIN = "isLogedin";

    public Session(Context context){
        Context _context = context;
        mypref = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = mypref.edit();
        editor.apply();
    }

    public void createSession(UserDetails userDetails) {
        editor.putString(Constant.USER_NAME, userDetails.userName);
        editor.putString(Constant.EMAIL, userDetails.email);
        editor.putString(Constant.CONTACT_NO, userDetails.contactNo);
        editor.putString(Constant.COUNTRY_CODE, userDetails.countryCode);
        editor.putString(Constant.PASSWORD, userDetails.password);
        editor.putString(Constant.LATITUDE, userDetails.latitude);
        editor.putString(Constant.ADDRESS, userDetails.address);
        editor.putString(Constant.LONGITUDE, userDetails.longitude);
        editor.putString(Constant.DEVICETOKEN, userDetails.deviceToken);
        editor.putString(Constant.DEVICETYPE, userDetails.deviceType);
        editor.putString(Constant.SOCIAL_ID, userDetails.socialId);
        editor.putString(Constant.SOCIAL_TYPE, userDetails.socialType);
        editor.putString(Constant.ID, userDetails.id);
        editor.putString("userType", userDetails.userType);
        editor.putString(Constant.FULL_NAME, userDetails.fullName);
        editor.putString(Constant.CID, userDetails.cId);
        editor.putString(Constant.AUTH_TOKEN, userDetails.authToken);
        editor.putString(Constant.STATUS, userDetails.status);
        editor.putString(Constant.PROFILE_PIC, userDetails.profileImage);
        editor.putString(Constant.C_Name, userDetails.cName);

        editor.putString(Constant.ONLINE_STATUS, userDetails.onlineStatus);
        editor.putString(Constant.NOTIFICATION_STATUS, userDetails.notificationStatus);
        editor.putString(Constant.FIREBASE_ID, userDetails.fireBaseId);
        editor.putString(Constant.FIREBASE_TOKEN, userDetails.fireBaseToken);
        editor.putString(Constant.CHAT_COUNT, userDetails.chatCount);
        editor.putString(Constant.DESCRIPTION, userDetails.description);
        editor.putString(Constant.CITY, userDetails.city);

        editor.putBoolean(IS_LOGEDIN,true);
        editor.commit();
    }

    public void setNotificationStatus(String notificationStatus) {
        editor.putString(Constant.NOTIFICATION_STATUS, notificationStatus);
        editor.commit();
    }
    public void setUserName(String username) {
        editor.putString(Constant.USER_NAME, username);
        editor.commit();
    }
    public void setChatCount(String chatCount) {
        editor.putString(Constant.CHAT_COUNT, chatCount);
        editor.commit();
    }
    public void setUserOnlineStatus(String onlineStatus) {
        editor.putString(Constant.ONLINE_STATUS, onlineStatus);
        editor.commit();
    }

    public String getCity(){
        return mypref.getString(Constant.CITY,"");
    }
    public String getSocialType(){
        return mypref.getString(Constant.SOCIAL_TYPE,"");
    }
    public String getChatCount(){
        return mypref.getString(Constant.CHAT_COUNT,"");
    }
    public String getLongitude(){
        return mypref.getString(Constant.LONGITUDE,"");
    }
    public String getLatitude(){
        return mypref.getString(Constant.LATITUDE,"");
    }
    public String getDescription(){
        return mypref.getString(Constant.DESCRIPTION,"");
    }
    public String getCID(){
        return mypref.getString(Constant.CID,"");
    }
    public String getUserName(){
        return mypref.getString(Constant.USER_NAME,"");
    }
    public String getSocialID(){
        return mypref.getString(Constant.SOCIAL_ID,"");
    }
    public String getUserOnlineStatus(){
        return mypref.getString(Constant.ONLINE_STATUS,"");
    }
    public String getNotificationStatus(){
        return mypref.getString(Constant.NOTIFICATION_STATUS,"");
    }
    public String getUserID(){
        return mypref.getString(Constant.ID,"");
    }
    public String getuserName(){
        return mypref.getString(Constant.USER_NAME,"");
    }
    public String getCName(){
        return mypref.getString(Constant.C_Name,"");
    }
    public String getEmail(){
        return mypref.getString(Constant.EMAIL,"");
    }
    public String getContactNo(){
        return mypref.getString(Constant.CONTACT_NO,"");
    }
    public String getCountryCode(){
        return mypref.getString(Constant.COUNTRY_CODE,"");
    }
    public String getCategoryName(){
        return mypref.getString(Constant.C_Name,"");
    }
    public String getFullName(){
        return mypref.getString(Constant.FULL_NAME,"");
    }
    public String getProfilePic(){
        return mypref.getString(Constant.PROFILE_PIC,"");
    }
    public String getAddressName(){
        return mypref.getString(Constant.ADDRESS,"");
    }
    public String getAuthToken(){
        return mypref.getString(Constant.AUTH_TOKEN,"");
    }
    public String getUserType(){
        return mypref.getString("userType","");
    }

    public boolean getIsLogedIn(){
        return mypref.getBoolean(IS_LOGEDIN, false);
    }

    public void logout(){
        editor.clear();
        editor.apply();
//        Toast.makeText(_context, "User logout sucessfully", Toast.LENGTH_SHORT).show();
    }
}
