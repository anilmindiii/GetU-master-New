package getu.app.com.getu.common_activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import getu.app.com.getu.R;
import getu.app.com.getu.app_session.Session;
import getu.app.com.getu.chat.activity.ChatActivity;
import getu.app.com.getu.freelancer_side.activity.FreelancerActivity;
import getu.app.com.getu.util.Constant;
import getu.app.com.getu.util.Utils;
import getu.app.com.getu.vollyemultipart.VolleyMultipartRequest;
import getu.app.com.getu.vollyemultipart.VolleySingleton;

public class NotificationUserDetailActivity extends AppCompatActivity {

    private Session session;
    private ImageView iv_for_profileImg;
    private TextView et_for_fullname, et_for_email, et_for_address, et_for_catactNo, et_for_discription;
    private String back,userId,fullName,profilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_user_detail);

        toolbar();
        initView();

        session = new Session(this);

        Bundle extras = getIntent().getExtras();
        String userIdforNotificatoin = extras.getString("senderId");
        String venName = extras.getString("USER_NOTIFICATION");
        if (userIdforNotificatoin != null) {
            getData(userIdforNotificatoin);
            back = userIdforNotificatoin;
        } else if (venName != null) {
            userIdforNotificatoin = venName;
            getData(userIdforNotificatoin);
        }
    }

    private void toolbar() {
        ImageView iv_for_back = findViewById(R.id.iv_for_back);
        ImageView iv_for_chat = findViewById(R.id.iv_for_chat);
        TextView tv_for_tittle = findViewById(R.id.tv_for_tittle);

        tv_for_tittle.setText("Profile");
        iv_for_chat.setVisibility(View.VISIBLE);

        iv_for_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        iv_for_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NotificationUserDetailActivity.this, ChatActivity.class);
                intent.putExtra("USER_ID",userId);
                intent.putExtra("FULLNAME",fullName);
                intent.putExtra("PROFILE_PIC",profilePic);
                startActivity(intent);
            }
        });
    }

    private void initView() {
        et_for_fullname = findViewById(R.id.et_for_fullname);
        et_for_email = findViewById(R.id.et_for_email);
        et_for_address = findViewById(R.id.et_for_address);
        et_for_catactNo = findViewById(R.id.et_for_catactNo);
        et_for_discription = findViewById(R.id.et_for_discription);
        iv_for_profileImg = findViewById(R.id.iv_for_profileImg);
    }

    public void getData(String userIdforNotificatoin) {

        if (Utils.isNetworkAvailable(this)) {

            final Dialog pDialog = new Dialog(this);
            Constant.myDialog(this,pDialog);
            pDialog.show();

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.GET,
                    Constant.URL_After_LOGIN + "getProfile?" + "userId=" + userIdforNotificatoin, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String data = new String(response.data);
                    Log.e("Response", data);

                    try {
                        JSONObject jsonObject = new JSONObject(data);

                        String status = jsonObject.getString("status");
                        String userstatus = jsonObject.getString("userstatus");
                        String message = jsonObject.getString("message");

                        if (status.equals("SUCCESS")) {
                            pDialog.dismiss();
                            JSONObject userDetail = jsonObject.getJSONObject("data");
                            userId = userDetail.getString("id");
                            fullName = userDetail.getString("fullName");
                            profilePic = userDetail.getString("profileImage");
                            String email = userDetail.getString("email");
                            String address = userDetail.getString("address");
                            String countryCode = userDetail.getString("countryCode");
                            String contactNo = userDetail.getString("contactNo");
                            String discription = userDetail.getString("description");

                            String phone = countryCode + contactNo;

                            setdata(profilePic, fullName, email, address, phone, discription);

                        } else {
                            Toast.makeText(NotificationUserDetailActivity.this, message, Toast.LENGTH_SHORT).show();
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
                    Constant.errorHandle(error, NotificationUserDetailActivity.this);
                    Log.i("Error", networkResponse + "");
                   // Toast.makeText(NotificationUserDetailActivity.this, networkResponse + "", Toast.LENGTH_SHORT).show();
                    pDialog.dismiss();
                    error.printStackTrace();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<String, String>();

                    headers.put("authToken", session.getAuthToken());

                    return headers;
                }
            };

            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(NotificationUserDetailActivity.this).addToRequestQueue(multipartRequest);
        } else {
            Toast.makeText(NotificationUserDetailActivity.this, R.string.check_net_connection, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (back != null) {
            Intent intent = new Intent(NotificationUserDetailActivity.this, FreelancerActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_left_to_right, R.anim.anim_right_to_left);
        } else {
            finish();
        }
    }

    private void setdata(String profilePic, String fullName, String email, String address, String phone, String discription) {

        if (profilePic != null && !profilePic.equals("")) {
            Picasso.with(this).load(profilePic).placeholder(R.drawable.user).into(iv_for_profileImg);
        } else {
            Picasso.with(this).load(R.drawable.user).fit().into(iv_for_profileImg);
        }

        et_for_fullname.setText(fullName);
        if (!email.equals("")) {
            et_for_email.setText(email);
        } else {
            et_for_email.setText("NA");
        }
        if (!address.equals("")) {
            et_for_address.setText(address);
        } else {
            et_for_address.setText("NA");
        }
        if (!phone.equals("")) {
            et_for_catactNo.setText(phone);
        } else {
            et_for_catactNo.setText("NA");
        }
        if (!discription.equals("")) {
            et_for_discription.setText(discription);
        } else {
            et_for_discription.setText("NA");
        }

    }
}
