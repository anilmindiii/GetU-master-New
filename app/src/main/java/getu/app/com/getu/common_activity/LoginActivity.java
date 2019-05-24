package getu.app.com.getu.common_activity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.firebase.client.Firebase;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import getu.app.com.getu.R;
import getu.app.com.getu.app_session.Session;
import getu.app.com.getu.chat.model.FirebaseData;
import getu.app.com.getu.freelancer_side.activity.FreelancerActivity;
import getu.app.com.getu.model.UserDetails;
import getu.app.com.getu.user_side_package.acrivity.TandCActivity;
import getu.app.com.getu.user_side_package.acrivity.UserMainActivity;
import getu.app.com.getu.util.Constant;
import getu.app.com.getu.util.Utils;
import getu.app.com.getu.vollyemultipart.AppHelper;
import getu.app.com.getu.vollyemultipart.VolleyMultipartRequest;
import getu.app.com.getu.vollyemultipart.VolleySingleton;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout layout_for_createAccount;
    private TextView tv_for_forgotPassword, tv_for_TandC;
    private EditText et_for_username, et_for_password;
    private ImageView iv_for_facebook, iv_for_google;
    private Button btn_for_login;
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager callbackManager;
    private String deviceToken, addressCity, city, socialType;
    private Session session;
    private Double latitude, longitude;
    private FusedLocationProviderClient mFusedLocationClient;
    private FirebaseAuth auth;
    //    private final String TAG = this.getClass().getSimpleName();
   // util.e("date",data)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        initView();

        deviceToken = FirebaseInstanceId.getInstance().getToken();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        checkLocationPermission();

        FacebookSdk.sdkInitialize(this);
        callbackManager = CallbackManager.Factory.create();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        getString(com.google.android.gms.R.string.common_signin_button_text_long);

        session = new Session(this);

        iv_for_google.setOnClickListener(this);
        layout_for_createAccount.setOnClickListener(this);
        tv_for_forgotPassword.setOnClickListener(this);
        iv_for_facebook.setOnClickListener(this);
        btn_for_login.setOnClickListener(this);
        tv_for_TandC.setOnClickListener(this);
    }

    private void initView() {
        layout_for_createAccount = findViewById(R.id.layout_for_createAccount);
        tv_for_forgotPassword = findViewById(R.id.tv_for_forgotPassword);
        et_for_username = findViewById(R.id.et_for_username);
        iv_for_facebook = findViewById(R.id.iv_for_facebook);
        iv_for_google = findViewById(R.id.iv_for_google);
        btn_for_login = findViewById(R.id.btn_for_login);
        et_for_password = findViewById(R.id.et_for_password);
        tv_for_TandC = findViewById(R.id.tv_for_TandC);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String email = account.getEmail();
            String id = account.getId();
            String url = String.valueOf(account.getPhotoUrl());
            String name = account.getDisplayName();
            String userName = name.replaceAll(" .*", "");
            socialLogin(name, email, id, url, userName);
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    private void facebookCallbackManager() {
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        getUserDetails(loginResult);
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(LoginActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.d("Exception", exception.getStackTrace().toString());
                        if (exception instanceof FacebookAuthorizationException) {
                            if (AccessToken.getCurrentAccessToken() != null) {
                                LoginManager.getInstance().logOut();
                            }
                        }
                    }
                });
    } //facebook

    public void getUserDetails(LoginResult loginResult) {

        final String socialId = loginResult.getAccessToken().getUserId();

        GraphRequest data_request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject json_object, GraphResponse response) {
//                        Log.d(TAG, String.valueOf(json_object));
                        try {
                            JSONObject jsonObject = new JSONObject(String.valueOf(json_object));

                            String name = jsonObject.getString("name");
                            String userName = name.replaceAll(" .*", "");
                            String email;
                            if (jsonObject.has("email")) {
                                email = jsonObject.getString("email");
                            } else {
                                email = socialId + "@facebook.com";
                            }

                            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                            Boolean forlogin = pref.getBoolean("ForLogin", Boolean.parseBoolean(null));

                            String url = "https://graph.facebook.com/" + socialId + "/picture?type=large";

                            socialLogin(name, email, socialId, url, userName);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle permission_param = new Bundle();
        permission_param.putString("fields", "id,name,email, picture.width(120).height(120)");
        data_request.setParameters(permission_param);
        data_request.executeAsync();
    } //facebook

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.anim_left_to_right, R.anim.anim_right_to_left);
    }

    public void validationLogin() {
        final String username = et_for_username.getText().toString().trim();
        final String password = et_for_password.getText().toString();

        if (username.equalsIgnoreCase("")) {
            Toast.makeText(this, R.string.for_usrname, Toast.LENGTH_LONG).show();
            et_for_username.requestFocus();
        } else if (username.contains(" ")) {
            Toast.makeText(this, R.string.Space, Toast.LENGTH_LONG).show();
            et_for_username.requestFocus();
        } else if (password.equalsIgnoreCase("")) {
            Toast.makeText(this, R.string.for_password, Toast.LENGTH_LONG).show();
            et_for_password.requestFocus();
        } else if (password.length() < 6) {
            Toast.makeText(this, R.string.password_required, Toast.LENGTH_LONG).show();
            et_for_password.requestFocus();
        } else {
            login(username, password);
        }
    }

    public void login(final String username, final String password) {

        if (Utils.isNetworkAvailable(this)) {

            final Dialog pDialog = new Dialog(this);
            Constant.myDialog(this,pDialog);
            pDialog.show();

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, Constant.URL_BEFORE_LOGIN + "auth/login", new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String data = new String(response.data);
                    Log.e("Response", data);

                    try {
                        JSONObject jsonObject = new JSONObject(data);

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        pDialog.dismiss();
                        if (status.equals("success")) {
                            JSONObject userDetail = jsonObject.getJSONObject("data");

                            UserDetails userDetails = new Gson().fromJson(userDetail.toString(),UserDetails.class);

                            if (userDetails.status.equals("1")) {

                                firebaseLogin(userDetails);

                            } else {
                                Toast.makeText(LoginActivity.this, "Your account has been inactivated by admin, please contact to activate", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (Throwable t) {
                        Log.e("My App", "Could not parse malformed JSON: \"" + response + "\"");
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    NetworkResponse networkResponse = error.networkResponse;
                    Log.i("Error", networkResponse + "");
                    Toast.makeText(LoginActivity.this, networkResponse + "", Toast.LENGTH_SHORT).show();
                    pDialog.dismiss();
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();

                    params.put("userName", username);
                    params.put("password", password);
                    params.put("deviceToken", deviceToken);
                    params.put("deviceType", "2");
                    params.put("userType", Constant.USER_TYPE);
                    params.put("fireBaseToken", deviceToken);
                    params.put("fireBaseId", "");

                    return params;
                }
            };

            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(LoginActivity.this).addToRequestQueue(multipartRequest);
        } else {
            Toast.makeText(LoginActivity.this, R.string.check_net_connection, Toast.LENGTH_SHORT).show();
        }
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Display UI and wait for user interaction
            } else {
                ActivityCompat.requestPermissions(
                        this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        3);
            }
        } else {
            // permission has been granted, continue as usual
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                latitude = Double.valueOf(String.valueOf(location.getLatitude()));
                                longitude = Double.valueOf(String.valueOf(location.getLongitude()));

                                try {
                                    latlong(latitude, longitude);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        }
    } // parmission for location and code after parmission

    private void latlong(Double latitude, Double longitude) throws IOException {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());

            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();

            addressCity = address;
        }catch (Exception e){

        }



    } // latlog to address find

    public void socialLogin(final String name, final String email, final String id, final String uri, final String userName) {

        if (Utils.isNetworkAvailable(this)) {

            final Dialog pDialog = new Dialog(this);
            Constant.myDialog(this,pDialog);
            pDialog.show();

            final VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, Constant.URL_BEFORE_LOGIN + "auth/signup", new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String data = new String(response.data);
                    Log.e("Response", data);

                    try {
                        JSONObject jsonObject = new JSONObject(data);

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equals("success")) {

                            Constant.CATEGORY_POS = 0;

                            JSONObject userDetail = jsonObject.getJSONObject("userDetail");
                            UserDetails userDetails = new UserDetails();

                            userDetails = new Gson().fromJson(userDetail.toString(),UserDetails.class);

                            if (userDetails.status.equals("1")) {

                                firebaseRagistration(userDetails);

                            } else {
                                Toast.makeText(LoginActivity.this, "Your account has been inactivated by admin, please contact to activate", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (Throwable t) {
                        Log.e("My App", "Could not parse malformed JSON: \"" + response + "\"");
                    }
                    pDialog.dismiss();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    NetworkResponse networkResponse = error.networkResponse;
                    Log.i("Error", networkResponse + "");
                    Toast.makeText(LoginActivity.this, networkResponse + "", Toast.LENGTH_SHORT).show();
                    pDialog.dismiss();
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();

                    params.put("userName", userName);
                    params.put("email", email);
                    params.put("contactNo", "");
                    params.put("countryCode", "");
                    params.put("password", "");
                    params.put("address", addressCity);

                    params.put("userType", Constant.USER_TYPE);
                    params.put("fullName", name);

                    if (Constant.USER_TYPE.equals("1"))
                        params.put("category", Constant.CATEGORY_ID);
                    else
                        params.put("category", "");

                    params.put("latitude", String.valueOf(latitude));
                    params.put("longitude", String.valueOf(longitude));
                    params.put("deviceToken", deviceToken);
                    params.put("deviceType", "2");
                    params.put("socialId", id);
                    params.put("socialType", socialType);
                    params.put("city", city);
                    params.put("fireBaseToken", deviceToken);
                    params.put("fireBaseId", "");

                    if (uri != null) {
                        params.put("profileImage", uri.toString());
                    }else {
                        params.put("profileImage", "");
                    }
                    return params;
                }
            };
            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(LoginActivity.this).addToRequestQueue(multipartRequest);
        } else {
            Toast.makeText(LoginActivity.this, R.string.check_net_connection, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_for_google:
                socialType = "google";
                signIn();
                break;
            case R.id.layout_for_createAccount:
                Intent intent = new Intent(LoginActivity.this, RagistrationActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_left_to_right, R.anim.anim_right_to_left);
                break;
            case R.id.tv_for_forgotPassword:
                intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_left_to_right, R.anim.anim_right_to_left);
                break;
            case R.id.iv_for_facebook:
                socialType = "facebook";
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile"));
                facebookCallbackManager();
                break;
            case R.id.btn_for_login:
                validationLogin();
                break;
            case R.id.tv_for_TandC:
                intent = new Intent(LoginActivity.this, TandCActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_left_to_right, R.anim.anim_right_to_left);
                break;
        }
    }

    private void firebaseRagistration(final UserDetails userDetails) {
        auth = FirebaseAuth.getInstance();

        String id = userDetails.id;
        final String email = id + "@getu.com";
        final String password = "123456";

        final FirebaseData firebaseData = new FirebaseData();
        firebaseData.name = userDetails.fullName;
        firebaseData.email = userDetails.email;
        firebaseData.firebaseToken = FirebaseInstanceId.getInstance().getToken();
        firebaseData.profilePic = userDetails.userAvatar;
        firebaseData.userType = userDetails.userType;
        firebaseData.uid = userDetails.id;
        firebaseData.notificationStatus = userDetails.notificationStatus;

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {

                            firebaseLogin(userDetails);

                        } else {

                            String fId = FirebaseAuth.getInstance().getUid();
                            firebaseData.firebaseId = fId;

                            writeToDBProfiles(firebaseData);

                            session.createSession(userDetails);

                            if (userDetails.userType.equals("1")) {
                                Intent intent = new Intent(LoginActivity.this, FreelancerActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                Intent intent = new Intent(LoginActivity.this, UserMainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                Constant.CATEGORY_ID = "";
                                Constant.CATEGORY_POS = 0;
                                finish();
                            }
                        }
                    }
                });
    }

    private void writeToDBProfiles(FirebaseData firebaseData) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = database.child("users/" + firebaseData.uid);
        myRef.setValue(firebaseData);
    }

    private void firebaseLogin(final UserDetails userDetails) {

        auth = FirebaseAuth.getInstance();

        String id = userDetails.id;
        final String email = id + "@getu.com";
        final String password = "123456";

        //added
        final FirebaseData firebaseData = new FirebaseData();
        firebaseData.name = userDetails.fullName;
        firebaseData.email = userDetails.email;
        firebaseData.firebaseToken = FirebaseInstanceId.getInstance().getToken();
        firebaseData.profilePic = userDetails.userAvatar;
        firebaseData.userType = userDetails.userType;
        firebaseData.uid = userDetails.id;
        firebaseData.notificationStatus = userDetails.notificationStatus;
        //

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {
                            // there was an error
                            firebaseRagistration(userDetails);

                        } else {
                            session.createSession(userDetails);

                            //added
                            String fId = FirebaseAuth.getInstance().getUid();
                            firebaseData.firebaseId = fId;
                            writeToDBProfiles(firebaseData);
                            //
                            if (userDetails.userType.equals("1")) {
                                Intent intent = new Intent(LoginActivity.this, FreelancerActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                Intent intent = new Intent(LoginActivity.this, UserMainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                Constant.CATEGORY_ID = "";
                                Constant.CATEGORY_POS = 0;
                                finish();
                            }
                        }
                    }
                });
    }
}
