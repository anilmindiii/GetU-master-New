package getu.app.com.getu.common_activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.firebase.client.Firebase;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import getu.app.com.getu.R;
import getu.app.com.getu.adapter.CustomSpAdapter;
import getu.app.com.getu.freelancer_side.fragments.ProfileFragment;
import getu.app.com.getu.hepler.HelperGetU;
import getu.app.com.getu.hepler.ImagePicker;
import getu.app.com.getu.hepler.PermissionAll;
import getu.app.com.getu.model.Category;
import getu.app.com.getu.model.CodeInfo;
import getu.app.com.getu.model.UserDetails;
import getu.app.com.getu.util.Constant;
import getu.app.com.getu.util.Utils;
import getu.app.com.getu.vollyemultipart.AppHelper;
import getu.app.com.getu.vollyemultipart.VolleyMultipartRequest;
import getu.app.com.getu.vollyemultipart.VolleySingleton;
import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

import static getu.app.com.getu.util.Constant.MY_PERMISSIONS_REQUEST_CAMERA;

public class RagistrationActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout layout_for_code, layout_for_login, layout_for_checkbox;
    private RelativeLayout layout_for_spinner;
    private TextView tv_for_code;
    private Button btn_for_signUp;
    private RelativeLayout layout_for_userImg;
    private ImageView iv_profile_image, checkbox_forCondition;
    private EditText et_for_address, et_for_username, et_for_email, et_for_contactNo, et_for_password, et_for_fullname;
    private Spinner sp_for_selectItem;
    private View view_for_spinner;
    private List<CodeInfo> countries;
    private Intent intent;
    private Bitmap bitmap;
    private boolean checkBox = false;
    private String countryCode;
    private Boolean isvalidate = false;
    private CustomSpAdapter customSpAdapter;
    private ArrayList<Category> arrayList;
    private String sCatId;
    private String deviceToken;
    private String city;
    private Double latitude, longitude;
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 2;
    private final String TAG = this.getClass().getSimpleName();
    private FusedLocationProviderClient mFusedLocationClient;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ragistration);
        initView();

        sCatId = Constant.CATEGORY_ID;
        deviceToken = FirebaseInstanceId.getInstance().getToken();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        checkLocationPermission();
        countries = Utils.loadCountries(this);
        et_for_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addressClick();
                et_for_address.setEnabled(false);
            }
        });

        Firebase.setAndroidContext(this);

        if (Constant.USER_TYPE.equals("0")) {
            view_for_spinner.setVisibility(View.GONE);
            layout_for_spinner.setVisibility(View.GONE);
        }

        arrayList = new ArrayList<>();
        customSpAdapter = new CustomSpAdapter(RagistrationActivity.this, arrayList);
        getCategoryFullList();
        sp_for_selectItem.setAdapter(customSpAdapter);

        sp_for_selectItem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View v, int position, long id) {
                Category category = arrayList.get(position);
                Constant.CATEGORY_ID = category.cID;
                sCatId = Constant.CATEGORY_ID;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });

        layout_for_code.setOnClickListener(this);
        layout_for_login.setOnClickListener(this);
        btn_for_signUp.setOnClickListener(this);
        layout_for_userImg.setOnClickListener(this);
        layout_for_checkbox.setOnClickListener(this);
    }

    private void latlong(Double latitude, Double longitude) throws IOException {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName();

        et_for_address.setText(address);

    } // latlog to address find

    private void addressClick() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .build(RagistrationActivity.this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    } // method for address button click

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            Uri imageUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                iv_profile_image.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (requestCode == 234) {
            //Bitmap bitmap = ImagePicker.getImageFromResult(this, requestCode, resultCode, data);
            Uri imageUri = ImagePicker.getImageURIFromResult(this, requestCode, resultCode, data);
            if (imageUri != null) {
                bitmap = ImagePicker.getImageFromResult(this, requestCode, resultCode, data);
                iv_profile_image.setImageBitmap(bitmap);}

        }

        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            et_for_address.setEnabled(true);
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);

                latitude = place.getLatLng().latitude;
                longitude = place.getLatLng().longitude;

                Log.i(TAG, "Place: " + place.getName());

                String placename = String.valueOf(place.getName());
                et_for_address.setText(placename);

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    } // onActivityResult

    private void SelectCountry() {
        List<String> list = new ArrayList<>();
        for (CodeInfo country : countries) {
            list.add(country.country_name + " (+" + country.phone_code + ")");
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select your country");
        CharSequence[] mEntries = list.toArray(new CharSequence[list.size()]);
        builder.setItems(mEntries, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                tv_for_code.setText(String.format("+%s", countries.get(position).phone_code));
                countryCode = String.format("%s", countries.get(position).code);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    } // dilog for country code

    private void checkPhoneNumber() {
        String contactNo = et_for_contactNo.getText().toString();
        try {
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.createInstance(this);
            if (countryCode != null) {
                String code = countryCode.toUpperCase();
                Phonenumber.PhoneNumber swissNumberProto = phoneUtil.parse(contactNo, code);
                isvalidate = phoneUtil.isValidNumber(swissNumberProto);
            }
            //isvalidate = true;
        } catch (NumberParseException e) {
            System.err.println("NumberParseException was thrown: " + e.toString());

        }
    }

    private void initView() {
        layout_for_code = findViewById(R.id.layout_for_code);
        layout_for_login = findViewById(R.id.layout_for_login);
        tv_for_code = findViewById(R.id.tv_for_code);
        btn_for_signUp = findViewById(R.id.btn_for_signUp);
        layout_for_userImg = findViewById(R.id.layout_for_userImg);
        iv_profile_image = findViewById(R.id.iv_profile_image);
        layout_for_checkbox = findViewById(R.id.layout_for_checkbox);
        checkbox_forCondition = findViewById(R.id.checkbox_forCondition);
        et_for_address = findViewById(R.id.et_for_address);
        sp_for_selectItem = findViewById(R.id.sp_for_selectItem);
        view_for_spinner = findViewById(R.id.view_for_spinner);
        layout_for_spinner = findViewById(R.id.layout_for_spinner);
        et_for_username = findViewById(R.id.et_for_username);
        et_for_email = findViewById(R.id.et_for_email);
        et_for_contactNo = findViewById(R.id.et_for_contactNo);
        et_for_password = findViewById(R.id.et_for_password);
        et_for_fullname = findViewById(R.id.et_for_fullname);
    } // initview for id

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.anim_left_to_right, R.anim.anim_right_to_left);
        Constant.CATEGORY_Name = "";
    }  // backpressed manage

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

    public void validationLogin() {

        if (!et_for_contactNo.getText().toString().trim().equalsIgnoreCase("")) {
            checkPhoneNumber();
        }
        if (et_for_fullname.getText().toString().trim().equalsIgnoreCase("")) {
            Toast.makeText(this, R.string.fullname, Toast.LENGTH_LONG).show();
            et_for_fullname.requestFocus();
        } else if (et_for_username.getText().toString().trim().equalsIgnoreCase("")) {
            Toast.makeText(this, R.string.enterUsername, Toast.LENGTH_LONG).show();
            et_for_username.requestFocus();
        } else if (et_for_username.getText().toString().trim().contains(" ")) {
            Toast.makeText(this, R.string.Space, Toast.LENGTH_LONG).show();
            et_for_username.requestFocus();
        } else if (et_for_email.getText().toString().trim().equalsIgnoreCase("")) {
            Toast.makeText(this, R.string.email, Toast.LENGTH_LONG).show();
            et_for_email.requestFocus();
        } else if (!Utils.Validationemail(et_for_email.getText().toString(), this)) {
        } else if (tv_for_code.getText().toString().trim().equalsIgnoreCase("")) {
        } else if (Constant.USER_TYPE.equals("1") && sp_for_selectItem.getSelectedItem().toString().trim().equalsIgnoreCase("")) {
            Toast.makeText(this, R.string.category, Toast.LENGTH_LONG).show();
            sp_for_selectItem.requestFocus();
        } else if (et_for_contactNo.getText().toString().trim().equalsIgnoreCase("")) {
            Toast.makeText(this, R.string.enter_contact, Toast.LENGTH_LONG).show();
            et_for_contactNo.requestFocus();
        } else if (et_for_contactNo.getText().toString().trim().length() < 4) {
            Toast.makeText(this, R.string.contact_required, Toast.LENGTH_LONG).show();
            et_for_contactNo.requestFocus();
        } else if (!isvalidate) {
            Toast.makeText(this, R.string.contact_wrong, Toast.LENGTH_SHORT).show();
        } else if (et_for_address.getText().toString().trim().equalsIgnoreCase("")) {
            Toast.makeText(this, R.string.enter_address, Toast.LENGTH_LONG).show();
            et_for_address.requestFocus();
        } else if (et_for_password.getText().toString().trim().equalsIgnoreCase("")) {
            Toast.makeText(this, R.string.enter_password, Toast.LENGTH_LONG).show();
            et_for_password.requestFocus();
        } else if (et_for_password.getText().toString().trim().length() < 6) {
            Toast.makeText(this, R.string.password_required, Toast.LENGTH_LONG).show();
            et_for_password.requestFocus();
        } else if (!checkBox) {
            Toast.makeText(this, R.string.tandc, Toast.LENGTH_LONG).show();
        } else {
            String fullName = et_for_fullname.getText().toString();
            String userName = et_for_username.getText().toString();
            String email = et_for_email.getText().toString();
            String code = tv_for_code.getText().toString();
            String contactNo = et_for_contactNo.getText().toString();
            String address = et_for_address.getText().toString();
            String password = et_for_password.getText().toString();


            //doRegistration(fullName, userName, email, code, contactNo, address, password);
            reSendOTP(fullName, userName, email, code, contactNo, address, password);
        }
    }

    public void reSendOTP(final String fullName, final String userName, final String email,
                          final String code, final String contactNo, final String address,
                          final String password) {

        if (Utils.isNetworkAvailable(this)) {

            final Dialog pDialog = new Dialog(this);
            Constant.myDialog(this,pDialog);
            pDialog.show();

            final VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, Constant.URL_BEFORE_LOGIN + "auth/sendOtp", new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String data = new String(response.data);
                    Log.e("Response", data);

                    try {
                        JSONObject jsonObject = new JSONObject(data);

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equals("success")) {
                           String OTP = String.valueOf(jsonObject.getInt("data"));

                            UserDetails user = new UserDetails();
                            user.fullName = fullName;
                            user.userName = userName;
                            user.email = email;
                            user.countryCode = code;
                            user.contactNo = contactNo;
                            user.address = address;
                            user.password = password;
                            user.latitude = String.valueOf(latitude);
                            user.longitude = String.valueOf(longitude);
                            user.city = city;


                            Intent intent = new Intent(RagistrationActivity.this, OTPActivity.class);
                            intent.putExtra("OTP", OTP);
                            intent.putExtra("CONTACT_NO", contactNo);
                            intent.putExtra("CountryCode", code);
                            intent.putExtra("user", user);
                            if(bitmap == null){
                                intent.putExtra("bitmap", "");

                            }else {
                                intent.putExtra("bitmap", HelperGetU.getFileDataFromBitmap(RagistrationActivity.this,bitmap));
                            }
                            startActivity(intent);

                            Toast.makeText(RagistrationActivity.this, message, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RagistrationActivity.this, message, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(RagistrationActivity.this, networkResponse + "", Toast.LENGTH_SHORT).show();
                    pDialog.dismiss();
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();

                    params.put("contactNo", contactNo);
                    params.put("countryCode", code);
                    params.put("userName", userName);
                    params.put("email", email);
                    params.put("contactNo", contactNo);
                    params.put("countryCode", code);
                    params.put("password", password);
                    params.put("address", address);
                    params.put("userType", Constant.USER_TYPE);
                    params.put("fullName", fullName);

                    if (Constant.USER_TYPE.equals("1"))
                        params.put("category", sCatId);
                    else
                        params.put("category", "");

                    params.put("latitude", String.valueOf(latitude));
                    params.put("longitude", String.valueOf(longitude));
                    params.put("deviceToken", deviceToken);
                    params.put("fireBaseToken", deviceToken);
                    params.put("fireBaseId", "");
                    params.put("deviceType", "2");
                    params.put("socialId", "");
                    params.put("socialType", "");
                    params.put("city", city);

                    if (bitmap == null){
                        params.put("profileImage", "");
                    }

                    return params;
                }
            };
            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(RagistrationActivity.this).addToRequestQueue(multipartRequest);
        } else {
            Toast.makeText(RagistrationActivity.this, R.string.check_net_connection, Toast.LENGTH_SHORT).show();
        }
    }

    private void doRegistration(final String fullName, final String userName, final String email, final String code, final String contactNo, final String address, final String password) {

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
                            Intent intent = new Intent(RagistrationActivity.this, OTPActivity.class);
                            intent.putExtra("OTP", userDetail);
                            intent.putExtra("CONTACT_NO", contactNo);
                            intent.putExtra("CountryCode", code);
                            startActivity(intent);

                        } else {
                            Toast.makeText(RagistrationActivity.this, message, Toast.LENGTH_SHORT).show();
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
                   Toast.makeText(RagistrationActivity.this, networkResponse + "", Toast.LENGTH_SHORT).show();
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
                        params.put("cId", sCatId);
                    else
                        params.put("cId", "");

                    params.put("latitude", String.valueOf(latitude));
                    params.put("longitude", String.valueOf(longitude));
                    params.put("deviceToken", deviceToken);
                    params.put("fireBaseToken", deviceToken);
                    params.put("fireBaseId", "");
                    params.put("deviceType", "2");
                    params.put("socialId", "");
                    params.put("socialType", "");
                    params.put("city", city);

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
            VolleySingleton.getInstance(RagistrationActivity.this).addToRequestQueue(multipartRequest);
        } else {
            Toast.makeText(RagistrationActivity.this, R.string.check_net_connection, Toast.LENGTH_SHORT).show();
        }
    }

    private void userImageClick() {
        dialog = new Dialog(RagistrationActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.take_picture);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        LinearLayout layout_for_camera = dialog.findViewById(R.id.layout_for_camera);
        LinearLayout layout_for_gallery = dialog.findViewById(R.id.layout_for_gallery);

        PermissionAll permissionAll = new PermissionAll();
        permissionAll.chackCameraPermission(RagistrationActivity.this);

        layout_for_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.pickImageFromCamera(RagistrationActivity.this);
                dialog.dismiss();
            }
        });
        layout_for_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 1);
                dialog.dismiss();
            }
        });
        dialog.show();
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
                        if (status.equalsIgnoreCase("success")) {
                            arrayList.clear();
                            JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                            JSONArray result = jsonObject1.getJSONArray("categoryList");
                            int i;
                            for (i = 0; i < result.length(); i++) {

                                JSONObject object = result.getJSONObject(i);
                                Category category = new Category();

                                category.cID = object.getString("id");
                                category.cName = object.getString("categoryName");

                                arrayList.add(category);
                            }
                            customSpAdapter.notifyDataSetChanged();
                            sp_for_selectItem.setSelection(Constant.CATEGORY_POS-1);

                        } else {
                            Toast.makeText(RagistrationActivity.this, message, Toast.LENGTH_SHORT).show();
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
                   Toast.makeText(RagistrationActivity.this, networkResponse + "", Toast.LENGTH_SHORT).show();
                    pDialog.dismiss();
                    error.printStackTrace();
                }
            });
            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(RagistrationActivity.this).addToRequestQueue(multipartRequest);
        } else {
            Toast.makeText(RagistrationActivity.this, R.string.check_net_connection, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_for_code:
                SelectCountry();
                InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                break;
            case R.id.layout_for_login:
                onBackPressed();
                break;
            case R.id.btn_for_signUp:
                validationLogin();
                break;
            case R.id.layout_for_userImg:
                userImageClick();
                break;
            case R.id.layout_for_checkbox:
                if (!checkBox) {
                    checkbox_forCondition.setBackgroundResource(R.drawable.check);
                    checkBox = true;
                } else {
                    checkbox_forCondition.setBackgroundResource(R.drawable.uncheck);
                    checkBox = false;
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {

            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED) {
                        //success permission granted & call Location method
                        //getDeviceLocation();
                    }
                } else {
                    // permission denied, boo! Disable the
                    Toast.makeText(RagistrationActivity.this, "Deny Location Permission", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
            break;

        }
    }
}
