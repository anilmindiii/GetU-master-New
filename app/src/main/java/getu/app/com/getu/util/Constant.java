package getu.app.com.getu.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import getu.app.com.getu.R;
import getu.app.com.getu.app_session.Session;
import getu.app.com.getu.common_activity.LoginActivity;
import getu.app.com.getu.common_activity.WelcomeActivity;
import getu.app.com.getu.vollyemultipart.VolleyMultipartRequest;
import getu.app.com.getu.vollyemultipart.VolleySingleton;

/**
 * Created by abc on 25/11/2017.
 */

public class Constant {
    public static final String USER_NAME = "userName";
    public static final String EMAIL = "email";
    public static final String CONTACT_NO = "contactNo";
    public static final String COUNTRY_CODE = "countryCode";
    public static final String PASSWORD = "password";
    public static final String ADDRESS = "address";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String DEVICETOKEN = "deviceToken";
    public static final String DEVICETYPE = "deviceType";
    public static final String SOCIAL_ID = "socialId";
    public static final String SOCIAL_TYPE = "socialType";
    public static final String ID = "id";
    public static final String FULL_NAME = "fullName";
    public static final String CID = "cId";
    public static final String AUTH_TOKEN = "authToken";
    public static final String STATUS = "status";
    public static final String PROFILE_PIC = "profilePic";
    public static final String C_Name = "cName";
    public static final String ONLINE_STATUS = "onlineStatus";
    public static final String NOTIFICATION_STATUS = "notificationStatus";
    public static final String FIREBASE_ID = "fireBaseId";
    public static final String FIREBASE_TOKEN = "fireBaseToken";
    public static final String CHAT_COUNT = "chatCount";
    public static final String DESCRIPTION = "description";
    public static final String CITY = "city";

    public static int CATEGORY_POS = 0;
    public static String USER_TYPE = "0";
    public static String CATEGORY_ID = "";
    public static String CATEGORY_Name = "";
    public static int HOME_RESUME = 0;

    //public static final String URL_BEFORE_LOGIN = "http://gnmtechnology.com/index.php/service/service/";
    //public static final String URL_BEFORE_LOGIN = "http://dev.mindiii.com/getu/api_v1/";
    public static final String URL_BEFORE_LOGIN = "http://52.201.121.172/api_v1/";

    //public static final String URL_After_LOGIN = "http://gnmtechnology.com/index.php/service/user/";
    //public static final String URL_After_LOGIN = "http://dev.mindiii.com/getu/index.php/service/user/";
   // public static final String URL_After_LOGIN =  "http://dev.mindiii.com/getu/api_v1/";
    public static final String URL_After_LOGIN =  "http://52.201.121.172/api_v1/";

    public static final int BackPressed_Exit = 2000;
    public static final int ACCESS_FINE_LOCATION = 99;
    public static final int REQUEST_LOCATION = 2;
    public static final int SPLESH_TIME = 3000;
    public static final int RequestPermissionCode = 1;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 5;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 8;
    public static final String NETWORK_SWITCH_FILTER = "com.devglan.broadcastreceiver.NETWORK_SWITCH_FILTER";


    public static void errorHandle(VolleyError error, Activity activity) {
        NetworkResponse networkResponse = error.networkResponse;
        String errorMessage = "Unknown error";
        if (networkResponse == null) {
            if (error.getClass().equals(TimeoutError.class)) {
                errorMessage = "Request timeout";
            } else if (error.getClass().equals(NoConnectionError.class)) {
                errorMessage = "Failed to connect server";
            }
        } else {
            String result = new String(networkResponse.data);
            try {
                JSONObject response = new JSONObject(result);

                String status = response.getString("responseCode");
                String message = response.getString("message");
                //int isActive = response.getInt("isActive");

                /*if(isActive == 1){
                    if (activity != null) {
                        showAlertDialog(activity, "You are inactive by Admin", "Inactive", "Ok");
                        return;
                    }
                }*/


                if (status.equals("300")) {
                    if (activity != null) {
                        showAlertDialog(activity, "Please Login Again", "Session Expired", "LogOut");
                    }
                }

                Log.e("Error Status", "" + status);
                Log.e("Error Message", message);

                if (networkResponse.statusCode == 404) {
                    errorMessage = "Resource not found";
                } else if (networkResponse.statusCode == 401) {
                    errorMessage = message + " Please login again";
                } else if (networkResponse.statusCode == 400) {
                    errorMessage = message + " Check your inputs";
                } else if (networkResponse.statusCode == 500) {
                    errorMessage = message + " Something is getting wrong";
                }
            } catch (JSONException e) {
                e.printStackTrace();
                if (activity != null) {

                }
            }
        }
    }

    public static void showAlertDialog(final Activity con, String msg, String title, String ok) {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(con);
        builder1.setTitle(title);
        builder1.setMessage(msg);
        builder1.setCancelable(false);
        builder1.setPositiveButton(ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        logout(con);
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public static void logout(final Activity con) {
       /* final Session session = new Session(con);
        signOut(con, session);*/

        if (Utils.isNetworkAvailable(con)) {

            final Dialog pDialog = new Dialog(con);
            Constant.myDialog(con,pDialog);
            pDialog.show();

            final Session session = new Session(con);

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.GET,
                    Constant.URL_After_LOGIN + "user/logout", new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String data = new String(response.data);
                    Log.e("Response", data);

                    try {
                        JSONObject jsonObject = new JSONObject(data);


                        String message = jsonObject.getString("message");
                        signOut(con, session);
                        Toast.makeText(con, message, Toast.LENGTH_SHORT).show();

                       /* if (message.equals("Invalid Auth Token")){
                            signOut(con, session);
                        }
                        String status = jsonObject.getString("status");

                        if (status.equals("SUCCESS")) {

                            signOut(con, session);
                            Toast.makeText(con, message, Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(con, message, Toast.LENGTH_SHORT).show();
                        }*/

                    } catch (Throwable t) {
                        Log.e("My App", "Could not parse malformed JSON: \"" + response + "\"");
                    }
                    pDialog.dismiss();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    final Session session = new Session(con);
                    signOut(con, session);
                    pDialog.dismiss();
                    error.printStackTrace();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<String, String>();

                    String auth = session.getAuthToken();
                    headers.put("authToken", auth);

                    return headers;
                }
            };

            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(con).addToRequestQueue(multipartRequest);
        } else {
            Toast.makeText(con, R.string.check_net_connection, Toast.LENGTH_SHORT).show();
        }
    }

    public static void signOut(final Activity con, final Session session) {

        FirebaseDatabase.getInstance().getReference().child("users").child(session.getUserID()).child("firebaseToken").setValue("");
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signOut();
        NotificationManager notificationManager = (NotificationManager) con.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        Intent intent = new Intent(con, WelcomeActivity.class);
        session.logout();
        Constant.USER_TYPE = "0";
        con.finish();
        con.startActivity(intent);
    }

    public static File getTemporalFile(Context context) {
        return new File(context.getExternalCacheDir(), "getu.jpeg");
    }

    public static void myDialog(Context context, Dialog pDialog) {
        pDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pDialog.setCancelable(false);
        pDialog.setContentView(R.layout.progress_bar_layout);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        //pDialog.show();
    }

}
