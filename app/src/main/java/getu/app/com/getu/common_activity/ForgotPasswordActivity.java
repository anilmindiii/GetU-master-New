package getu.app.com.getu.common_activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import getu.app.com.getu.R;
import getu.app.com.getu.freelancer_side.activity.FreelancerActivity;
import getu.app.com.getu.model.UserDetails;
import getu.app.com.getu.user_side_package.acrivity.UserMainActivity;
import getu.app.com.getu.util.Constant;
import getu.app.com.getu.util.Utils;
import getu.app.com.getu.vollyemultipart.VolleyMultipartRequest;
import getu.app.com.getu.vollyemultipart.VolleySingleton;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView iv_for_back;
    private EditText et_for_email;
    private Button btn_for_resetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initView();
        iv_for_back.setOnClickListener(this);
        btn_for_resetPassword.setOnClickListener(this);
    }

    private void initView() {
        iv_for_back = findViewById(R.id.iv_for_back);
        et_for_email = findViewById(R.id.et_for_email);
        btn_for_resetPassword = findViewById(R.id.btn_for_resetPassword);
    }

    private void validation() {
        String email = et_for_email.getText().toString().trim();
        if (email.equalsIgnoreCase("")) {
            Toast.makeText(this, R.string.email, Toast.LENGTH_LONG).show();
            et_for_email.requestFocus();
        } else if (Utils.Validationemail(email, this)) {
            //write here your code
            forgotPasswordAPI(email);
        }
    }

    public void forgotPasswordAPI(final String email) {

        if (Utils.isNetworkAvailable(this)) {

            final Dialog pDialog = new Dialog(this);
            Constant.myDialog(this,pDialog);
            pDialog.show();

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, Constant.URL_BEFORE_LOGIN + "forgotPassword", new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String data = new String(response.data);
                    Log.e("Response", data);

                    try {
                        JSONObject jsonObject = new JSONObject(data);

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equals("SUCCESS")) {
                            Intent intent = new Intent(ForgotPasswordActivity.this,LoginActivity.class);
                            startActivity(intent);
                            Toast.makeText(ForgotPasswordActivity.this, message, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ForgotPasswordActivity.this, message, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ForgotPasswordActivity.this, networkResponse + "", Toast.LENGTH_SHORT).show();
                    pDialog.dismiss();
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();

                    params.put("email", email);
                    return params;
                }
            };

            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(ForgotPasswordActivity.this).addToRequestQueue(multipartRequest);
        } else {
            Toast.makeText(ForgotPasswordActivity.this, R.string.check_net_connection, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.anim_left_to_right, R.anim.anim_right_to_left);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_for_back:
                onBackPressed();
                break;
            case R.id.btn_for_resetPassword:
                validation();
                break;
        }
    }
}
