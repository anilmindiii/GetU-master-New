package getu.app.com.getu.user_side_package.fragment;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import getu.app.com.getu.R;
import getu.app.com.getu.adapter.CustomSpAdapter;
import getu.app.com.getu.app_session.Session;
import getu.app.com.getu.common_activity.LoginActivity;
import getu.app.com.getu.model.Category;
import getu.app.com.getu.pagination.EndlessRecyclerViewScrollListener;
import getu.app.com.getu.user_side_package.acrivity.UserMainActivity;
import getu.app.com.getu.user_side_package.adapter.UserAdapter;
import getu.app.com.getu.user_side_package.model.UserList;
import getu.app.com.getu.util.Constant;
import getu.app.com.getu.util.Utils;
import getu.app.com.getu.vollyemultipart.VolleyMultipartRequest;
import getu.app.com.getu.vollyemultipart.VolleySingleton;

public class UserOnlineFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private String mParam1;
    private TextView tv_for_noData;
    private Dialog pDialog;
    private RecyclerView recycler_view;
    private Spinner sp_for_selectItem;
    private ArrayList<UserList> userLists;
    private UserAdapter userAdapter;
    private CustomSpAdapter customSpAdapter;
    private ArrayList<Category> arrayList;
    private String sCatId, GuestUser;
    private FusedLocationProviderClient mFusedLocationClient;
    private Double latitude, longitude;
    private Session session;
    private EndlessRecyclerViewScrollListener scrollListener;
    private final String TAG = this.getClass().getSimpleName();
    private int page = 0, limit = 20, apiSingleCall;
    private Bundle bundle;

    public UserOnlineFragment() {
        // Required empty public constructor
    }

    public static UserOnlineFragment newInstance(String param1) {
        UserOnlineFragment fragment = new UserOnlineFragment();
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
        View view = inflater.inflate(R.layout.fragment_user_online, container, false);
        toolbar(view);
        session = new Session(getContext());
        apiSingleCall = 0;
        pDialog = new Dialog(getActivity());
        Constant.myDialog(getActivity(),pDialog);

        if(getActivity().getIntent().getExtras() != null){
            bundle = getActivity().getIntent().getExtras();
            GuestUser = bundle.getString("GuestUser", "GuestUser");
        }else {
            GuestUser = "";
        }



        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        checkLocationPermission();

        recycler_view = view.findViewById(R.id.recycler_view);
        sp_for_selectItem = view.findViewById(R.id.sp_for_selectItem);
        tv_for_noData = view.findViewById(R.id.tv_for_noData);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recycler_view.setLayoutManager(linearLayoutManager);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextDataFromApi(page, totalItemsCount);
            }
        };

        tv_for_noData.setVisibility(View.GONE);

        userLists = new ArrayList<>();
        userAdapter = new UserAdapter(userLists, getActivity());
        recycler_view.setAdapter(userAdapter);

        arrayList = new ArrayList<>();
        customSpAdapter = new CustomSpAdapter(getActivity(), arrayList);
        sp_for_selectItem.setAdapter(customSpAdapter);
        getCategoryFullList();

        sp_for_selectItem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View v, int position, long id) {
                Category category = arrayList.get(position);
                sCatId = category.cID;
                Constant.CATEGORY_ID = sCatId;
                page = 0;
                getAllData(sCatId);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        return view;
    }

    public void loadNextDataFromApi(int page, int totalItemsCount) {
        Log.e(TAG, "loadNextDataFromApi: " + page);
        Log.e(TAG, "loadNextDataFromApi: " + limit);
        getAllData(sCatId);
    } // pagination

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
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                latitude = Double.valueOf(String.valueOf(location.getLatitude()));
                                longitude = Double.valueOf(String.valueOf(location.getLongitude()));
                            }
                            if(latitude == null && longitude == null){
                                locationDialog();
                            }
                        }
                    });
        }
    } // parmission for location and code after parmission

    private void toolbar(View view) {
        TextView tv_for_tittle = view.findViewById(R.id.tv_for_tittle);
        ImageView iv_for_map = view.findViewById(R.id.iv_for_map);

        tv_for_tittle.setText(R.string.online);
        iv_for_map.setVisibility(View.VISIBLE);

        iv_for_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((UserMainActivity) getActivity()).addFragment(UserMapFragment.newInstance("online",sCatId), true, R.id.framlayout);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void getAllData(final String sCatId) {

        if (Utils.isNetworkAvailable(getActivity())) {

            pDialog.show();

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, Constant.URL_BEFORE_LOGIN + "getAllData", new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String data = new String(response.data);
                    Log.e("Response", data);

                    try {
                        JSONObject jsonObject = new JSONObject(data);

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        String userstatus = jsonObject.getString("userstatus");
                        pDialog.dismiss();
                        if (userstatus.equals("1") || GuestUser.equals("GuestUser")) {
                            if (status.equals("SUCCESS")) {
                                String totalCount = jsonObject.getString("totalCount");
                                userLists.clear();
                                pDialog.dismiss();
                                tv_for_noData.setVisibility(View.GONE);
                                recycler_view.setVisibility(View.VISIBLE);
                                JSONArray result = jsonObject.getJSONArray("data");
                                if (result != null) {
                                    for (int i = 0; i < result.length(); i++) {
                                        JSONObject object = result.getJSONObject(i);
                                        UserList userList = new UserList();
                                        userList.userName = object.getString("userName");
                                        userList.fireBaseId = object.getString("fireBaseId");
                                        userList.fireBaseToken = object.getString("fireBaseToken");
                                        userList.email = object.getString("email");
                                        userList.contactNo = object.getString("contactNo");
                                        userList.countryCode = object.getString("countryCode");
                                        userList.description = object.getString("description");
                                        userList.distance = object.getString("distance");
                                        userList.profileimage = object.getString("profileImage");
                                        userList.userId = object.getString("userId");
                                        userList.fullName = object.getString("fullName");
                                        userList.onlineStatus = object.getString("onlineStatus");
                                        userList.latitude = object.getString("latitude");
                                        userList.longitude = object.getString("longitude");
                                        userList.address = object.getString("address");
                                        userList.category = object.getString("cName");
                                        if (userList.onlineStatus.equals("1")) {
                                            userLists.add(userList);
                                        }
                                    }
                                    userAdapter.notifyDataSetChanged();
                                    page = page + 1;

                                }
                            } else {
//                            Toast.makeText(getContext(), message + "", Toast.LENGTH_SHORT).show();
                                tv_for_noData.setVisibility(View.VISIBLE);
                                recycler_view.setVisibility(View.GONE);
                            }
                        } else {
                            Utils.customAlertDialog(getActivity(), "Alert!", "You are temporary inactive by admin");
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
                    tv_for_noData.setVisibility(View.VISIBLE);
                    recycler_view.setVisibility(View.GONE);
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();

                    params.put("latitude", String.valueOf(latitude));
                    params.put("longitude", String.valueOf(longitude));
                    if (session.getIsLogedIn()) {
                        params.put("userId", session.getUserID());
                    } else {
                        params.put("userId", "");
                    }
                    params.put("cId", sCatId);
                    params.put("page", page + "");
                    params.put("limit", limit + "");
                    return params;
                }
            };
            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(multipartRequest);
        } else {
            Toast.makeText(getContext(), R.string.check_net_connection, Toast.LENGTH_SHORT).show();
        }
    }

    private void getCategoryFullList() {

        if (Utils.isNetworkAvailable(getContext())) {
            pDialog.show();

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.GET, Constant.URL_BEFORE_LOGIN + "getAllCategory", new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String data = new String(response.data);
                    Log.e("Response", data);

                    try {
                        JSONObject jsonObject = new JSONObject(data);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        if (status.equals("SUCCESS")) {
                            arrayList.clear();
                            arrayList.add(new Category());

                            JSONArray result = jsonObject.getJSONArray("category");
                            int i;
                            for (i = 0; i < result.length(); i++) {
                                JSONObject object = result.getJSONObject(i);
                                Category category = new Category();
                                category.cID = object.getString("id");
                                category.cName = object.getString("cName");
                                arrayList.add(category);
                            }
                            customSpAdapter.notifyDataSetChanged();

                        } else {
                            pDialog.dismiss();
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Throwable t) {
                        Log.e("My App", "Could not parse malformed JSON: \"" + response + "\"");
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    NetworkResponse networkResponse = error.networkResponse;
                    pDialog.dismiss();
                    Log.i("Error", networkResponse + "");
                    Toast.makeText(getContext(), networkResponse + "", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
            });
            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(getContext()).addToRequestQueue(multipartRequest);
        } else {
            Toast.makeText(getContext(), R.string.check_net_connection, Toast.LENGTH_SHORT).show();
        }
    }

    private void locationDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_location);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Button btn_for_ok = dialog.findViewById(R.id.btn_for_ok);
        ImageView iv_for_cansel = dialog.findViewById(R.id.iv_for_cansel);

        iv_for_cansel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn_for_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
