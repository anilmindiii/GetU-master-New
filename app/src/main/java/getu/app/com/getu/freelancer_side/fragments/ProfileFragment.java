package getu.app.com.getu.freelancer_side.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import getu.app.com.getu.R;
import getu.app.com.getu.adapter.CustomSpAdapter;
import getu.app.com.getu.app_session.Session;
import getu.app.com.getu.chat.model.FirebaseData;
import getu.app.com.getu.common_activity.WelcomeActivity;
import getu.app.com.getu.hepler.ImagePicker;
import getu.app.com.getu.hepler.PermissionAll;
import getu.app.com.getu.model.Category;
import getu.app.com.getu.model.UserDetails;
import getu.app.com.getu.util.Constant;
import getu.app.com.getu.util.Utils;
import getu.app.com.getu.vollyemultipart.AppHelper;
import getu.app.com.getu.vollyemultipart.VolleyMultipartRequest;
import getu.app.com.getu.vollyemultipart.VolleySingleton;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static getu.app.com.getu.util.Constant.MY_PERMISSIONS_REQUEST_CAMERA;

public class ProfileFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private String mParam1,city,deviceToken;
    private Session session;
    private Dialog dialog;
    private String catID;
    private Bitmap bitmap = null;
    private ImageView iv_for_profileImg,iv_for_edit,iv_for_update;
    private RelativeLayout layout_for_userImg;
    private EditText et_for_fullname,et_for_email,et_for_discription,et_for_userName;
    private TextView et_for_address;
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 2;
    private FusedLocationProviderClient mFusedLocationClient;
    private Double latitude, longitude;
    private final String TAG = this.getClass().getSimpleName();
    private Spinner sp_for_selectItem;
    private CustomSpAdapter customSpAdapter;
    private ArrayList<Category> arrayList;
    private FirebaseAuth auth;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String param1) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater,container,savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        toolbar(view);
        initView(view);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        setClickEvent();
        session = new Session(getContext());

        arrayList = new ArrayList<>();
        customSpAdapter = new CustomSpAdapter(getContext(), arrayList);
        getCategoryFullList();
        sp_for_selectItem.setAdapter(customSpAdapter);

        sp_for_selectItem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View v, int position, long id) {

                Category category = arrayList.get(position);
                catID = category.cID;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });

        deviceToken = FirebaseInstanceId.getInstance().getToken();
        setText();

        layout_for_userImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userImageClick();
            }
        });

        et_for_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addressClick();
                checkLocationPermission();
                et_for_address.setEnabled(false);
            }
        });

        return view;
    }

    private void setText(){
        String profilePic = session.getProfilePic();
        String fullName = session.getFullName();
        String email = session.getEmail();
        String address = session.getAddressName();
        String cName = session.getCName();
        String username = session.getuserName();
        String description = session.getDescription();

        if (profilePic != null && !profilePic.equals("")) {
            Picasso.with(getContext()).load(profilePic).placeholder(R.drawable.user).into(iv_for_profileImg);
        }else{
            Picasso.with(getContext()).load(R.drawable.user).fit().into(iv_for_profileImg);
        }

        et_for_fullname.setText(fullName);
        if (!email.equals("")){
            et_for_email.setText(email);
        }else {
            et_for_email.setText("NA");
        }
        if (!address.equalsIgnoreCase("")){
            et_for_address.setText(address);
        }else {
            et_for_address.setText("NA");
        }
        if (!description.equalsIgnoreCase("")){
            et_for_discription.setText(StringEscapeUtils.unescapeJava(description));

        }else {
            et_for_discription.setText("NA");
        }
        if (!username.equalsIgnoreCase("")){
            et_for_userName.setText(username);
        }else {
            et_for_userName.setText("NA");
        }
    }

    private void addressClick() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .build(getActivity());
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    } // method for address button click

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Display UI and wait for user interaction
            } else {
                ActivityCompat.requestPermissions(
                        getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        3);
            }
        } else {
            // permission has been granted, continue as usual
           mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                latitude = Double.valueOf(String.valueOf(location.getLatitude()));
                                longitude = Double.valueOf(String.valueOf(location.getLongitude()));

                            }
                        }
                    });
        }
    } // parmission for location and code after parmission

    private void userImageClick() {
        dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.take_picture);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        LinearLayout layout_for_camera = dialog.findViewById(R.id.layout_for_camera);
        LinearLayout layout_for_gallery = dialog.findViewById(R.id.layout_for_gallery);

        PermissionAll permissionAll = new PermissionAll();
        permissionAll.chackCameraPermission(getActivity());

        layout_for_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.pickImageFromCamera(ProfileFragment.this);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            Uri imageUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);
                iv_for_profileImg.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (requestCode == 234) {
            //Bitmap bitmap = ImagePicker.getImageFromResult(this, requestCode, resultCode, data);
            Uri imageUri = ImagePicker.getImageURIFromResult(getActivity(), requestCode, resultCode, data);
            if (imageUri != null) {
                bitmap = ImagePicker.getImageFromResult(getActivity(), requestCode, resultCode, data);
                iv_for_profileImg.setImageBitmap(bitmap);}

        }

        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            et_for_address.setEnabled(true);
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getContext(), data);

                latitude = place.getLatLng().latitude;
                longitude = place.getLatLng().longitude;

                Log.i(TAG, "Place: " + place.getName());

                String placename = String.valueOf(place.getName());
                et_for_address.setText(placename);


            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getContext(), data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    } // onActivityResult

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {

            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED) {
                        //success permission granted & call Location method
                        //getDeviceLocation();
                    }
                } else {
                    // permission denied, boo! Disable the
                    Toast.makeText(getContext(), "Deny Location Permission", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
            break;

        }
    }

    private void initView(View view){
        et_for_fullname = view.findViewById(R.id.et_for_fullname);
        et_for_email = view.findViewById(R.id.et_for_email);
        et_for_address = view.findViewById(R.id.et_for_address);
        et_for_discription = view.findViewById(R.id.et_for_discription);
        iv_for_profileImg = view.findViewById(R.id.iv_for_profileImg);
        et_for_userName = view.findViewById(R.id.et_for_userName);
        layout_for_userImg = view.findViewById(R.id.layout_for_userImg);
        sp_for_selectItem = view.findViewById(R.id.sp_for_selectItem);
    }

    private void toolbar(View view) {
        TextView tv_for_tittle = view.findViewById(R.id.tv_for_tittle);
        iv_for_edit = view.findViewById(R.id.iv_for_edit);
        iv_for_update = view.findViewById(R.id.iv_for_update);

        tv_for_tittle.setText(R.string.Profile);
        iv_for_edit.setVisibility(View.VISIBLE);

        iv_for_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_for_userName.setEnabled(true);
                et_for_fullname.setEnabled(true);
                et_for_email.setEnabled(true);
                et_for_address.setEnabled(true);
                et_for_discription.setEnabled(true);
                layout_for_userImg.setEnabled(true);
                sp_for_selectItem.setEnabled(true);
                iv_for_update.setVisibility(View.VISIBLE);
                iv_for_edit.setVisibility(View.GONE);
            }
        });

        iv_for_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setClickEvent();
                iv_for_update.setVisibility(View.GONE);
                iv_for_edit.setVisibility(View.VISIBLE);

                final String userName = et_for_userName.getText().toString().trim();
                final String email = et_for_email.getText().toString().trim();
                final String address = et_for_address.getText().toString();
                final String discriptions = et_for_discription.getText().toString();
                final String fullName = et_for_fullname.getText().toString();

                if (fullName.equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), R.string.enter_name, Toast.LENGTH_SHORT).show();
                    et_for_fullname.requestFocus();
                } else if (userName.equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), R.string.enterUsername, Toast.LENGTH_SHORT).show();
                    et_for_userName.requestFocus();
                } else if (email.equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), R.string.enter_email, Toast.LENGTH_SHORT).show();
                    et_for_email.requestFocus();
                } else if (!Utils.Validationemail(et_for_email.getText().toString(), getContext())) {}
                else if (address.equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), R.string.enter_address, Toast.LENGTH_SHORT).show();
                    et_for_address.requestFocus();
                } else {

                    String oldUserName = session.getUserName();
                    if (!oldUserName.equalsIgnoreCase(userName)) {
                        customAlertDialog(getActivity(), "Alert", "your session will expire, when you change your username", userName, email, address, discriptions, fullName,oldUserName);

                    } else {
                        if (bitmap != null |!userName.equals(session.getuserName()) | !email.equals(session.getEmail()) | !address.equals(session.getAddressName()) | !discriptions.equals(session.getDescription()) | !fullName.equals(session.getFullName())) {

                        }
                        updateProfile(userName, email, address, discriptions, fullName, oldUserName);
                    }
                }
            }
        });
    }

    private void setClickEvent(){
        et_for_userName.setEnabled(false);
        et_for_fullname.setEnabled(false);
        et_for_email.setEnabled(false);
        et_for_address.setEnabled(false);
        et_for_discription.setEnabled(false);
        layout_for_userImg.setEnabled(false);
        sp_for_selectItem.setEnabled(false);
    }

    public void customAlertDialog(final Activity context, String title, String message, final String userName, final String email, final String address, final String discriptions, final String fullName, final String oldUserName) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setCancelable(false);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                if (fullName.equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), "Please enter fullname", Toast.LENGTH_SHORT).show();
                    et_for_fullname.requestFocus();
                } else if (userName.equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), "Please enter username", Toast.LENGTH_SHORT).show();
                    et_for_userName.requestFocus();
                } else if (email.equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), "Please enter email", Toast.LENGTH_SHORT).show();
                    et_for_email.requestFocus();
                } else if (!Utils.Validationemail(et_for_email.getText().toString(), getContext())) {}
                else if (address.equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), "Please enter address", Toast.LENGTH_SHORT).show();
                    et_for_address.requestFocus();
                } else {
                    if (bitmap != null |!userName.equals(session.getuserName()) | !email.equals(session.getEmail()) | !address.equals(session.getAddressName()) | !discriptions.equals(session.getDescription()) | !fullName.equals(session.getFullName())) {
                    }
                    updateProfile(userName, email, address, discriptions, fullName, oldUserName);
                }
            }
        });
        dialog.setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Action for "Cancel".
            }
        });

        final AlertDialog alert = dialog.create();
        alert.show();
    }

    private void updateProfile(final String userName, final String email, final String address, final String discriptions, final String fullName, final String oldUserName) {

        if (Utils.isNetworkAvailable(getContext())) {

            final Dialog pDialog = new Dialog(getActivity());
            Constant.myDialog(getActivity(),pDialog);
            pDialog.show();

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST,
                    Constant.URL_After_LOGIN + "user/updateProfile", new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String data = new String(response.data);
                    Log.e("Response", data);

                    try {
                        JSONObject jsonObject = new JSONObject(data);

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("SUCCESS")) {
                            if (oldUserName.equalsIgnoreCase(userName)) {

                                JSONObject userDetail = jsonObject.getJSONObject("userDetail");

                                UserDetails userDetails = new Gson().fromJson(userDetail.toString(),UserDetails.class);

                                firebaseLogin(userDetails);

                            }else {
                                Intent intent = new Intent(getActivity(),WelcomeActivity.class);
                                session.logout();
                                startActivity(intent);
                            }
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();


                        } else {
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
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
                    Constant.errorHandle(error, getActivity());
                    Log.i("Error", networkResponse + "");
                   Toast.makeText(getContext(), networkResponse + "", Toast.LENGTH_SHORT).show();
                    pDialog.dismiss();
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("userName", userName);
                    params.put("fullName", fullName);
                    params.put("email", email);
                    params.put("address", address);
                    if (latitude != null){
                        params.put("latitude", String.valueOf(latitude));
                    }else {
                        params.put("latitude", session.getLatitude());
                    }
                    if (longitude != null){
                        params.put("longitude", String.valueOf(longitude));
                    }else {
                        params.put("longitude", session.getLongitude());
                    }
                    params.put("description", StringEscapeUtils.escapeJava(discriptions).replace(" +","") );
                    params.put("fireBaseId", "");
                    params.put("fireBaseToken", deviceToken);
                    params.put("chatCount", "");

                    if (city != null){
                        params.put("city", city);
                    }else {
                        params.put("city", session.getCity());
                    }
                    params.put("category", catID);

                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<String, String>();

                    String auth = session.getAuthToken();
                    headers.put("authToken", auth);

                    return headers;
                }

                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<String, DataPart>();
                    if (bitmap != null) {
                        params.put("profileImage", new VolleyMultipartRequest.DataPart("profilePic.jpg", AppHelper.getFileDataFromDrawable(bitmap), "image/jpeg"));
                    }
                    return params;
                }
            };

            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(getContext()).addToRequestQueue(multipartRequest);
        } else {
            Toast.makeText(getContext(), R.string.check_net_connection, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void getCategoryFullList() {

        if (Utils.isNetworkAvailable(getContext())) {

            final Dialog pDialog = new Dialog(getActivity());
            Constant.myDialog(getActivity(),pDialog);
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

                            String cId = session.getCID();
                            for(int j=0;j<arrayList.size();j++){
                                if(arrayList.get(j).cID.equals(cId)){
                                    sp_for_selectItem.setSelection(j);
                                }
                            }

                        } else {
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
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
                   Toast.makeText(getContext(), networkResponse + "", Toast.LENGTH_SHORT).show();
                    pDialog.dismiss();
                    error.printStackTrace();
                }
            });
            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(getContext()).addToRequestQueue(multipartRequest);
        } else {
            Toast.makeText(getContext(), R.string.check_net_connection, Toast.LENGTH_SHORT).show();
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
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        userDetails.authToken = session.getAuthToken();
                        if (!task.isSuccessful()) {

                            firebaseLogin(userDetails);

                        } else {

                            String fId = FirebaseAuth.getInstance().getUid();
                            firebaseData.firebaseId = fId;

                            writeToDBProfiles(firebaseData);

                            session.createSession(userDetails);
                            iv_for_edit.setVisibility(View.VISIBLE);
                            iv_for_update.setVisibility(View.GONE);
                            setClickEvent();
                            setText();
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
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        userDetails.authToken = session.getAuthToken();
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

                            session.createSession(userDetails);
                            iv_for_edit.setVisibility(View.VISIBLE);
                            iv_for_update.setVisibility(View.GONE);
                            setClickEvent();
                            setText();
                        }
                    }
                });
    }

}
