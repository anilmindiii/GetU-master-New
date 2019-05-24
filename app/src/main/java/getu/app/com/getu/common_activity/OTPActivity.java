package getu.app.com.getu.common_activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import getu.app.com.getu.R;
import getu.app.com.getu.app_session.Session;
import getu.app.com.getu.chat.model.FirebaseData;
import getu.app.com.getu.freelancer_side.activity.FreelancerActivity;
import getu.app.com.getu.model.UserDetails;
import getu.app.com.getu.user_side_package.acrivity.UserMainActivity;
import getu.app.com.getu.util.Constant;
import getu.app.com.getu.util.Utils;
import getu.app.com.getu.vollyemultipart.AppHelper;
import getu.app.com.getu.vollyemultipart.VolleyMultipartRequest;
import getu.app.com.getu.vollyemultipart.VolleySingleton;

public class OTPActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_for_firstOTP, tv_for_secondOTP, tv_for_thirdOTP, tv_for_forthOTP, tv_for_one, tv_for_two,
            tv_for_three, tv_for_four, tv_for_five, tv_for_six, tv_for_seven, tv_for_eight, tv_for_nine, tv_for_zero;

    private ImageView iv_for_enter, iv_for_back, iv_for_backActivity;
    private String OTP, contactNumber, CountryCode;
    private LinearLayout layout_for_reSend;
    private Session session;
    private FirebaseAuth auth;
    private Bitmap bitmap;
    private UserDetails userDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        initView();

        Bundle bundle = getIntent().getExtras();
        OTP = bundle.getString("OTP");
        contactNumber = bundle.getString("CONTACT_NO");
        CountryCode = bundle.getString("CountryCode");

        Intent intent = getIntent();

        if(getIntent().getByteArrayExtra("bitmap")!= null){
            byte[] imageByteArrary   = getIntent().getByteArrayExtra("bitmap");
            bitmap = BitmapFactory.decodeByteArray(imageByteArrary, 0, imageByteArrary.length);
        }

        userDetails = (UserDetails) intent.getSerializableExtra("user");

        session = new Session(this);

        tv_for_one.setOnClickListener(this);
        tv_for_two.setOnClickListener(this);
        tv_for_three.setOnClickListener(this);
        tv_for_four.setOnClickListener(this);
        tv_for_five.setOnClickListener(this);
        tv_for_six.setOnClickListener(this);
        tv_for_seven.setOnClickListener(this);
        tv_for_eight.setOnClickListener(this);
        tv_for_nine.setOnClickListener(this);
        tv_for_zero.setOnClickListener(this);
        iv_for_enter.setOnClickListener(this);
        iv_for_back.setOnClickListener(this);
        iv_for_backActivity.setOnClickListener(this);
        layout_for_reSend.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_for_one:
                if (tv_for_firstOTP.getText().equals("")) {
                    tv_for_firstOTP.setText("1");
                } else if (tv_for_secondOTP.getText().equals("")) {
                    tv_for_secondOTP.setText("1");
                } else if (tv_for_thirdOTP.getText().equals("")) {
                    tv_for_thirdOTP.setText("1");
                } else if (tv_for_forthOTP.getText().equals("")) {
                    tv_for_forthOTP.setText("1");
                }
                break;
            case R.id.tv_for_two:
                if (tv_for_firstOTP.getText().equals("")) {
                    tv_for_firstOTP.setText("2");
                } else if (tv_for_secondOTP.getText().equals("")) {
                    tv_for_secondOTP.setText("2");
                } else if (tv_for_thirdOTP.getText().equals("")) {
                    tv_for_thirdOTP.setText("2");
                } else if (tv_for_forthOTP.getText().equals("")) {
                    tv_for_forthOTP.setText("2");
                }
                break;
            case R.id.tv_for_three:
                if (tv_for_firstOTP.getText().equals("")) {
                    tv_for_firstOTP.setText("3");
                } else if (tv_for_secondOTP.getText().equals("")) {
                    tv_for_secondOTP.setText("3");
                } else if (tv_for_thirdOTP.getText().equals("")) {
                    tv_for_thirdOTP.setText("3");
                } else if (tv_for_forthOTP.getText().equals("")) {
                    tv_for_forthOTP.setText("3");
                }
                break;
            case R.id.tv_for_four:
                if (tv_for_firstOTP.getText().equals("")) {
                    tv_for_firstOTP.setText("4");
                } else if (tv_for_secondOTP.getText().equals("")) {
                    tv_for_secondOTP.setText("4");
                } else if (tv_for_thirdOTP.getText().equals("")) {
                    tv_for_thirdOTP.setText("4");
                } else if (tv_for_forthOTP.getText().equals("")) {
                    tv_for_forthOTP.setText("4");
                }
                break;
            case R.id.tv_for_five:
                if (tv_for_firstOTP.getText().equals("")) {
                    tv_for_firstOTP.setText("5");
                } else if (tv_for_secondOTP.getText().equals("")) {
                    tv_for_secondOTP.setText("5");
                } else if (tv_for_thirdOTP.getText().equals("")) {
                    tv_for_thirdOTP.setText("5");
                } else if (tv_for_forthOTP.getText().equals("")) {
                    tv_for_forthOTP.setText("5");
                }
                break;
            case R.id.tv_for_six:
                if (tv_for_firstOTP.getText().equals("")) {
                    tv_for_firstOTP.setText("6");
                } else if (tv_for_secondOTP.getText().equals("")) {
                    tv_for_secondOTP.setText("6");
                } else if (tv_for_thirdOTP.getText().equals("")) {
                    tv_for_thirdOTP.setText("6");
                } else if (tv_for_forthOTP.getText().equals("")) {
                    tv_for_forthOTP.setText("6");
                }
                break;
            case R.id.tv_for_seven:
                if (tv_for_firstOTP.getText().equals("")) {
                    tv_for_firstOTP.setText("7");
                } else if (tv_for_secondOTP.getText().equals("")) {
                    tv_for_secondOTP.setText("7");
                } else if (tv_for_thirdOTP.getText().equals("")) {
                    tv_for_thirdOTP.setText("7");
                } else if (tv_for_forthOTP.getText().equals("")) {
                    tv_for_forthOTP.setText("7");
                }
                break;
            case R.id.tv_for_eight:
                if (tv_for_firstOTP.getText().equals("")) {
                    tv_for_firstOTP.setText("8");
                } else if (tv_for_secondOTP.getText().equals("")) {
                    tv_for_secondOTP.setText("8");
                } else if (tv_for_thirdOTP.getText().equals("")) {
                    tv_for_thirdOTP.setText("8");
                } else if (tv_for_forthOTP.getText().equals("")) {
                    tv_for_forthOTP.setText("8");
                }
                break;
            case R.id.tv_for_nine:
                if (tv_for_firstOTP.getText().equals("")) {
                    tv_for_firstOTP.setText("9");
                } else if (tv_for_secondOTP.getText().equals("")) {
                    tv_for_secondOTP.setText("9");
                } else if (tv_for_thirdOTP.getText().equals("")) {
                    tv_for_thirdOTP.setText("9");
                } else if (tv_for_forthOTP.getText().equals("")) {
                    tv_for_forthOTP.setText("9");
                }
                break;
            case R.id.tv_for_zero:
                if (tv_for_firstOTP.getText().equals("")) {
                    tv_for_firstOTP.setText("0");
                } else if (tv_for_secondOTP.getText().equals("")) {
                    tv_for_secondOTP.setText("0");
                } else if (tv_for_thirdOTP.getText().equals("")) {
                    tv_for_thirdOTP.setText("0");
                } else if (tv_for_forthOTP.getText().equals("")) {
                    tv_for_forthOTP.setText("0");
                }
                break;
            case R.id.iv_for_enter:
                validatioin();
                break;
            case R.id.iv_for_back:
                if (!tv_for_forthOTP.getText().equals("")) {
                    tv_for_forthOTP.setText("");
                } else if (!tv_for_thirdOTP.getText().equals("")) {
                    tv_for_thirdOTP.setText("");
                } else if (!tv_for_secondOTP.getText().equals("")) {
                    tv_for_secondOTP.setText("");
                } else if (!tv_for_firstOTP.getText().equals("")) {
                    tv_for_firstOTP.setText("");
                }
                break;
            case R.id.iv_for_backActivity:
                onBackPressed();
                break;
            case R.id.layout_for_reSend:
                reSendOTP(userDetails.fullName, userDetails.userName, userDetails.email, userDetails.countryCode,
                        userDetails.contactNo, userDetails.address, userDetails.password);
                break;
        }
    }

    private void initView() {
        tv_for_firstOTP = findViewById(R.id.tv_for_firstOTP);
        tv_for_secondOTP = findViewById(R.id.tv_for_secondOTP);
        tv_for_thirdOTP = findViewById(R.id.tv_for_thirdOTP);
        tv_for_forthOTP = findViewById(R.id.tv_for_forthOTP);
        tv_for_one = findViewById(R.id.tv_for_one);
        tv_for_two = findViewById(R.id.tv_for_two);
        tv_for_three = findViewById(R.id.tv_for_three);
        tv_for_four = findViewById(R.id.tv_for_four);
        tv_for_five = findViewById(R.id.tv_for_five);
        tv_for_six = findViewById(R.id.tv_for_six);
        tv_for_seven = findViewById(R.id.tv_for_seven);
        tv_for_eight = findViewById(R.id.tv_for_eight);
        tv_for_nine = findViewById(R.id.tv_for_nine);
        tv_for_zero = findViewById(R.id.tv_for_zero);
        layout_for_reSend = findViewById(R.id.layout_for_reSend);
        iv_for_enter = findViewById(R.id.iv_for_enter);
        iv_for_back = findViewById(R.id.iv_for_back);
        iv_for_backActivity = findViewById(R.id.iv_for_backActivity);
    }

    public void varificationOTP(final String myOTP) {

        if (Utils.isNetworkAvailable(this)) {

            final Dialog pDialog = new Dialog(this);
            Constant.myDialog(this,pDialog);
            pDialog.show();

            final VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, Constant.URL_BEFORE_LOGIN + "contactVerify", new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String data = new String(response.data);
                    Log.e("Response", data);

                    try {
                        JSONObject jsonObject = new JSONObject(data);

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equals("SUCCESS")) {
                            JSONObject userDetail = jsonObject.getJSONObject("userDetail");
                            pDialog.dismiss();

                            UserDetails userDetails = new UserDetails();

                            userDetails = new Gson().fromJson(userDetail.toString(),UserDetails.class);

                            if (userDetails.status.equals("1")) {
                                firebaseRagistration(userDetails);
                            } else {
                                Toast.makeText(OTPActivity.this, "You are temporary inactive by admin", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(OTPActivity.this, message, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(OTPActivity.this, networkResponse + "", Toast.LENGTH_SHORT).show();
                    pDialog.dismiss();
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();

                    params.put("contactNo", contactNumber);
                    params.put("countryCode", CountryCode);
                    params.put("OTP", myOTP);
                    params.put("isVerified", "1");

                    return params;
                }
            };

            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(OTPActivity.this).addToRequestQueue(multipartRequest);
        } else {
            Toast.makeText(OTPActivity.this, R.string.check_net_connection, Toast.LENGTH_SHORT).show();
        }
    }

    public void reSendOTP(final String fullName, final String userName, final String email,
                          final String code, final String contactNo, final String address,
                          final String password) {

        final String deviceToken = FirebaseInstanceId.getInstance().getToken();

        if (Utils.isNetworkAvailable(this)) {

            final Dialog pDialog = new Dialog(this);
            Constant.myDialog(this,pDialog);
            pDialog.show();

            final VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST,
                    Constant.URL_BEFORE_LOGIN + "auth/sendOtp", new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String data = new String(response.data);
                    Log.e("Response", data);

                    try {
                        JSONObject jsonObject = new JSONObject(data);

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equals("success")) {
                            OTP = jsonObject.getString("data");
                            Toast.makeText(OTPActivity.this, message, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(OTPActivity.this, message, Toast.LENGTH_SHORT).show();
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
                   Toast.makeText(OTPActivity.this, networkResponse + "", Toast.LENGTH_SHORT).show();
                    pDialog.dismiss();
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();

                    params.put("contactNo", contactNumber);
                    params.put("countryCode", CountryCode);
                    params.put("userName", userName);
                    params.put("email", email);
                    params.put("contactNo", contactNo);
                    params.put("countryCode", code);
                    params.put("password", password);
                    params.put("address", address);
                    params.put("userType", Constant.USER_TYPE);
                    params.put("fullName", fullName);

                    if (Constant.USER_TYPE.equals("1"))
                        params.put("category", Constant.CATEGORY_ID);
                    else
                        params.put("category", "");

                    params.put("latitude", String.valueOf(userDetails.latitude));
                    params.put("longitude", String.valueOf(userDetails.longitude));
                    params.put("deviceToken", deviceToken);
                    params.put("fireBaseToken", deviceToken);
                    params.put("fireBaseId", "");
                    params.put("deviceType", "2");
                    params.put("socialId", "");
                    params.put("socialType", "");
                    params.put("city", userDetails.city);

                    if (bitmap == null){
                        params.put("profilePic", "");
                    }

                    return params;
                }
            };
            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(OTPActivity.this).addToRequestQueue(multipartRequest);
        } else {
            Toast.makeText(OTPActivity.this, R.string.check_net_connection, Toast.LENGTH_SHORT).show();
        }
    }

    private void validatioin() {
        if (tv_for_firstOTP.getText().toString().trim().equalsIgnoreCase("")) {
            Toast.makeText(this, R.string.otp, Toast.LENGTH_LONG).show();
            tv_for_firstOTP.requestFocus();
        } else if (tv_for_secondOTP.getText().toString().trim().equalsIgnoreCase("")) {
            Toast.makeText(this, R.string.otp, Toast.LENGTH_LONG).show();
            tv_for_secondOTP.requestFocus();
        } else if (tv_for_thirdOTP.getText().toString().trim().equalsIgnoreCase("")) {
            Toast.makeText(this, R.string.otp, Toast.LENGTH_LONG).show();
            tv_for_thirdOTP.requestFocus();
        } else if (tv_for_forthOTP.getText().toString().trim().equalsIgnoreCase("")) {
            Toast.makeText(this, R.string.otp, Toast.LENGTH_LONG).show();
            tv_for_forthOTP.requestFocus();
        } else {

            String firstOTP = tv_for_firstOTP.getText().toString().trim();
            String secondOTP = tv_for_secondOTP.getText().toString().trim();
            String thirdOTP = tv_for_thirdOTP.getText().toString().trim();
            String forthOTP = tv_for_forthOTP.getText().toString().trim();

            String myOTP = firstOTP + secondOTP + thirdOTP + forthOTP;

            if (myOTP.equals(OTP)) {
               // varificationOTP(myOTP);
                doRegistration(userDetails.fullName, userDetails.userName, userDetails.email, userDetails.countryCode,
                        userDetails.contactNo, userDetails.address, userDetails.password);
            } else {
                Toast.makeText(this, "Wrong OTP", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.anim_left_to_right, R.anim.anim_right_to_left);
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
                .addOnCompleteListener(OTPActivity.this, new OnCompleteListener<AuthResult>() {
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
                                Intent intent = new Intent(OTPActivity.this, FreelancerActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                Intent intent = new Intent(OTPActivity.this, UserMainActivity.class);
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
        String email = id + "@getu.com";
        String password = "123456";

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
                .addOnCompleteListener(OTPActivity.this, new OnCompleteListener<AuthResult>() {
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
                                Intent intent = new Intent(OTPActivity.this, FreelancerActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                Intent intent = new Intent(OTPActivity.this, UserMainActivity.class);
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


    private void doRegistration(final String fullName, final String userName,
                                final String email, final String code,
                                final String contactNo, final String address,
                                final String password) {

        final String deviceToken = FirebaseInstanceId.getInstance().getToken();

        if (Utils.isNetworkAvailable(this)) {

            final Dialog pDialog = new Dialog(this);
            Constant.myDialog(this,pDialog);
            pDialog.show();

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, Constant.URL_BEFORE_LOGIN + "auth/signup", new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String data = new String(response.data);
                    Log.e("Response", data);

                    try {
                        JSONObject jsonObject = new JSONObject(data);

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("SUCCESS")) {
                            String userDetail = jsonObject.getString("userDetail");
                            //JSONObject userDetail = jsonObject.getJSONObject("userDetail");

                            UserDetails userDetails = new UserDetails();

                            userDetails = new Gson().fromJson(userDetail.toString(),UserDetails.class);

                            if (userDetails.status.equals("1")) {
                                firebaseRagistration(userDetails);
                            } else {
                                Toast.makeText(OTPActivity.this, "You are temporary inactive by admin", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(OTPActivity.this, message, Toast.LENGTH_SHORT).show();
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
                  Toast.makeText(OTPActivity.this, networkResponse + "", Toast.LENGTH_SHORT).show();
                    pDialog.dismiss();
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("userName", userName);
                    params.put("email", email);
                    params.put("contactNo", contactNo);
                    params.put("countryCode", code);
                    params.put("password", password);
                    params.put("address", address);
                    params.put("userType", Constant.USER_TYPE);
                    params.put("fullName", fullName);

                    if (Constant.USER_TYPE.equals("1"))
                        params.put("category", Constant.CATEGORY_ID);
                    else
                        params.put("category", "");

                    params.put("latitude", String.valueOf(userDetails.latitude));
                    params.put("longitude", String.valueOf(userDetails.longitude));
                    params.put("deviceToken", deviceToken);
                    params.put("fireBaseToken", deviceToken);
                    params.put("fireBaseId", "");
                    params.put("deviceType", "2");
                    params.put("socialId", "");
                    params.put("socialType", "");
                    params.put("city", userDetails.city);
                    if (bitmap == null){
                        params.put("profileImage", "");
                    }
                    return params;
                }

                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<String, DataPart>();
                    if (bitmap != null) {
                        params.put("profileImage", new VolleyMultipartRequest.DataPart("profileImage.jpg", AppHelper.getFileDataFromDrawable(bitmap), "image/jpeg"));
                    }
                    return params;
                }
            };

            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(OTPActivity.this).addToRequestQueue(multipartRequest);
        } else {
            Toast.makeText(OTPActivity.this, R.string.check_net_connection, Toast.LENGTH_SHORT).show();
        }
    }

}
