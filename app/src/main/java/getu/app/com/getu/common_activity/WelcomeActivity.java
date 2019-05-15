package getu.app.com.getu.common_activity;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import getu.app.com.getu.R;
import getu.app.com.getu.adapter.CustomSpAdapter;
import getu.app.com.getu.hepler.PermissionAll;
import getu.app.com.getu.model.Category;
import getu.app.com.getu.user_side_package.acrivity.UserMainActivity;
import getu.app.com.getu.util.Constant;
import getu.app.com.getu.util.Utils;
import getu.app.com.getu.vollyemultipart.VolleyMultipartRequest;
import getu.app.com.getu.vollyemultipart.VolleySingleton;

import static getu.app.com.getu.util.Constant.MY_PERMISSIONS_REQUEST_LOCATION;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener{

    private Spinner sp_for_selectItem;
    private LinearLayout layout_for_signin;
    private RadioButton rb_for_freelancer;
    private boolean radioBtn = false;
    private Button btn_for_start;
    private ArrayList<Category> arrayList;
    private CustomSpAdapter customSpAdapter;
    private String catID = "";
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        initView();

        Constant.CATEGORY_ID = "";

        PermissionAll permissionAll = new PermissionAll();
        permissionAll.checkLocationPermission(WelcomeActivity.this);

        btn_for_start.setOnClickListener(this);
        rb_for_freelancer.setOnClickListener(this);
        layout_for_signin.setOnClickListener(this);
        arrayList = new ArrayList<>();
        customSpAdapter = new CustomSpAdapter(WelcomeActivity.this, arrayList);

        sp_for_selectItem.setAdapter(customSpAdapter);

        // Listener called when spinner item selected
        sp_for_selectItem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View v, int position, long id) {
                Category category = arrayList.get(position);
                Constant.CATEGORY_Name = category.cName;
                Constant.CATEGORY_ID = category.cID;
                catID = category.cID;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
    }

    public void getCategoryFullList() {

        if (Utils.isNetworkAvailable(this)) {

            final Dialog pDialog = new Dialog(this);
            Constant.myDialog(this,pDialog);
            pDialog.show();

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.GET, Constant.URL_BEFORE_LOGIN + "user/getCategoryList", new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String data = new String(response.data);
                    Log.e("Response", data);

                    try {
                        JSONObject jsonObject = new JSONObject(data);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        if (status.equals("success")) {
                            arrayList.clear();

                            arrayList.add(new Category());
                            JSONObject objectJSONObject = jsonObject.getJSONObject("data");

                            JSONArray result = objectJSONObject.getJSONArray("categoryList");
                            int i;
                            for (i = 0; i < result.length(); i++) {
                                JSONObject object = result.getJSONObject(i);
                                Category category = new Category();
                                category.cID = object.getString("id");
                                category.cName = object.getString("categoryName");
                                arrayList.add(category);
                            }
                            customSpAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(WelcomeActivity.this, message, Toast.LENGTH_SHORT).show();
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
                   // Toast.makeText(WelcomeActivity.this, networkResponse + "", Toast.LENGTH_SHORT).show();
                    pDialog.dismiss();
                    error.printStackTrace();
                }
            });
            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(WelcomeActivity.this).addToRequestQueue(multipartRequest);
        } else {
           // Toast.makeText(WelcomeActivity.this, R.string.check_net_connection, Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent(WelcomeActivity.this,NetworkErrorActivity.class);
            startActivity(intent1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        //success permission granted & call Location method
                        //getDeviceLocation();
                    }
                } else {
                    // permission denied, boo! Disable the
                    Toast.makeText(WelcomeActivity.this, "Deny Location Permission", Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
    }

    @Override
    public void onBackPressed() {

        if (!doubleBackToExitPressedOnce) {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(WelcomeActivity.this, R.string.for_exit, Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, Constant.BackPressed_Exit);
        } else {
            super.onBackPressed();
            finish();
        }
    }

    private void initView() {
        layout_for_signin = findViewById(R.id.layout_for_signin);
        sp_for_selectItem = findViewById(R.id.sp_for_selectItem);
        rb_for_freelancer = findViewById(R.id.rb_for_freelancer);
        btn_for_start = findViewById(R.id.btn_for_start);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_for_start:
                Constant.CATEGORY_POS = sp_for_selectItem.getSelectedItemPosition();
                if (rb_for_freelancer.isChecked()) {
                    if (!catID.equals("") && !catID.equals("null")) {
                        Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, R.string.category, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (!catID.equals("") && !catID.equals("null")) {
                        Intent intent = new Intent(WelcomeActivity.this, UserMainActivity.class);
                        intent.putExtra("GuestUser","GuestUser");
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, R.string.category, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.rb_for_freelancer:
                String uerType = "0";
                if (!radioBtn) {
                    rb_for_freelancer.setChecked(true);
                    radioBtn = true;
                    Constant.USER_TYPE = "1";
                    uerType = "1";
                } else {
                    rb_for_freelancer.setChecked(false);
                    radioBtn = false;
                    Constant.USER_TYPE = "0";
                    uerType = "0";
                }
                break;
            case R.id.layout_for_signin:
                Constant.CATEGORY_POS = sp_for_selectItem.getSelectedItemPosition();
                if (rb_for_freelancer.isChecked()) {
                    if (!catID.equals("") && !catID.equals("null")) {
                        Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, R.string.category, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                break;
        }
    }

    BroadcastReceiver netSwitchReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isConnectionAvailable = intent.getExtras().getBoolean("is_connected");
            if (!isConnectionAvailable) {
                if (NetworkErrorActivity.isOptedToOffline()) {

                } else {
                    Intent intent1 = new Intent(WelcomeActivity.this,NetworkErrorActivity.class);
                    startActivity(intent1);
                }
            } else {
                NetworkErrorActivity.setOptedToOffline(false);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        try {
            registerReceiver(netSwitchReceiver, new IntentFilter(Constant.NETWORK_SWITCH_FILTER));
            getCategoryFullList();
        }
        catch (Exception e){

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(netSwitchReceiver);
        }
        catch (Exception e){

        }
    }
}
