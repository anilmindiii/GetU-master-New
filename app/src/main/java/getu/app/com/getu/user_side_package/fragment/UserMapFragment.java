package getu.app.com.getu.user_side_package.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import getu.app.com.getu.R;
import getu.app.com.getu.app_session.Session;
import getu.app.com.getu.hepler.PermissionAll;
import getu.app.com.getu.pagination.EndlessRecyclerViewScrollListener;
import getu.app.com.getu.user_side_package.acrivity.UserMainActivity;
import getu.app.com.getu.user_side_package.adapter.MapUserAdapter;
import getu.app.com.getu.user_side_package.listener.CustomItemClickListener;
import getu.app.com.getu.user_side_package.model.UserList;
import getu.app.com.getu.util.Constant;
import getu.app.com.getu.util.Utils;
import getu.app.com.getu.vollyemultipart.VolleyMultipartRequest;
import getu.app.com.getu.vollyemultipart.VolleySingleton;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class UserMapFragment extends Fragment implements OnMapReadyCallback, CustomItemClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1, mParam2, onlineStatus;
    private TextView tv_for_search;
    private RecyclerView recycler_view;
    private ArrayList<UserList> mapUserLists;
    private MapUserAdapter mapUserAdapter;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 2;
    private final String TAG = this.getClass().getSimpleName();
    private FusedLocationProviderClient mFusedLocationClient;
    private Geocoder geocoder;
    private List<Address> addresses;
    private Session session;
    private Double latitude, longitude;
    private int page = 0, limit = 20;
    private EndlessRecyclerViewScrollListener scrollListener;
    private GoogleMap map;
    private MapView mapview;

    public UserMapFragment() {
        // Required empty public constructor
    }

    public static UserMapFragment newInstance(String param1, String param2) {
        UserMapFragment fragment = new UserMapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_user_map, container, false);
        toolbar(view);
        initView(view);

        session = new Session(getContext());
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        getLocation();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),1, GridLayoutManager.HORIZONTAL, false);
        recycler_view.setLayoutManager(gridLayoutManager);

        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {

                loadNextDataFromApi(page, totalItemsCount);
            }
        };

        mapview.onCreate(savedInstanceState);
        mapview.onResume();

        mapview.getMapAsync(UserMapFragment.this);

        checkLocationPermission();
        PermissionAll permissionAll = new PermissionAll();
        permissionAll.checkLocationPermission(getActivity());

        mapUserLists = new ArrayList<UserList>();
        mapUserAdapter = new MapUserAdapter(mapUserLists, getActivity());
        recycler_view.setAdapter(mapUserAdapter);
        mapUserAdapter.setClickListener(UserMapFragment.this);

        tv_for_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchClick();
                tv_for_search.setEnabled(false);
            }
        });
        return view;
    }

    public void loadNextDataFromApi(int page, int totalItemsCount) {
        getAllData();
    } // pagination

    private void initView(View view) {
        mapview = view.findViewById(R.id.mapview);
        recycler_view = view.findViewById(R.id.recycler_view);
        tv_for_search = view.findViewById(R.id.tv_for_search);
    }

    private void searchClick() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .build(getActivity());
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);

        } catch (GooglePlayServicesRepairableException e) {
           e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
           e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            tv_for_search.setEnabled(true);
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getContext(), data);
                latitude = place.getLatLng().latitude;
                longitude = place.getLatLng().longitude;

                String placename = String.valueOf(place.getName());
                tv_for_search.setText(placename);

                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 10);
                map.animateCamera(cameraUpdate);

                getAllData();

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getContext(), data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private void getAllData() {

        if (Utils.isNetworkAvailable(getActivity())) {

            final Dialog pDialog = new Dialog(getActivity());
            Constant.myDialog(getActivity(),pDialog);
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

                        pDialog.dismiss();
                        if (status.equals("SUCCESS")) {
                            recycler_view.setVisibility(View.VISIBLE);
                            mapUserLists.clear();
                            pDialog.dismiss();
                            mapUserLists.clear();
                            JSONArray result = jsonObject.getJSONArray("data");
                            for (int i = 0; i < result.length(); i++) {

                                JSONObject object = result.getJSONObject(i);
                                UserList mapUserList = new UserList();

                                mapUserList.profileimage = object.getString("profileImage");
                                mapUserList.userId = object.getString("userId");
                                mapUserList.userName = object.getString("userName");
                                mapUserList.email = object.getString("email");
                                mapUserList.contactNo = object.getString("contactNo");
                                mapUserList.countryCode = object.getString("countryCode");
                                mapUserList.description = object.getString("description");
                                mapUserList.fullName = object.getString("fullName");
                                mapUserList.onlineStatus = object.getString("onlineStatus");
                                mapUserList.latitude = object.getString("latitude");
                                mapUserList.longitude = object.getString("longitude");
                                mapUserList.address = object.getString("address");
                                mapUserList.category = object.getString("cName");
                                mapUserList.distance = object.getString("distance");

                                onlineStatus = mapUserList.onlineStatus;
                                if (!mParam1.equals("online")) {
                                    mapUserLists.add(mapUserList);
                                } else if (onlineStatus.equals("1")) {
                                    mapUserLists.add(mapUserList);
                                }
                            }
                            page = page+1;
                            mapUserAdapter.notifyDataSetChanged();
                            recycler_view.smoothScrollToPosition(0);

                            multipleMarkers(mapUserLists, onlineStatus);

                        } else {
                            Toast.makeText(getContext(), message + "", Toast.LENGTH_SHORT).show();
                            recycler_view.setVisibility(View.GONE);
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
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    String lat = String.valueOf(latitude);
                    String lng = String.valueOf(longitude);
                    params.put("latitude", lat);
                    params.put("longitude", lng);
                    if (!mParam1.equals("online")) {
                        if (session.getIsLogedIn()) {
                            params.put("userId", session.getUserID());
                        } else {
                            params.put("userId", "");
                        }
                    } else {
                        if (!mParam2.equals("")) {
                            params.put("userId", mParam2);
                        } else {
                            params.put("userId", "");
                        }
                    }
                    params.put("cId", Constant.CATEGORY_ID);
                    params.put("page", "0");
                    params.put("limit", "20");
                    return params;
                }
            };
            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(multipartRequest);
        } else {
            Toast.makeText(getContext(), R.string.check_net_connection, Toast.LENGTH_SHORT).show();
        }
    }

    private void multipleMarkers(final ArrayList<UserList> mapUserList, String onlineStatus) {
        if (mapUserList == null) {
            return;
        } else {
            map.clear();
            for (int i = 0; i < mapUserList.size(); i++) {
                createMarker(mapUserList.get(i).latitude, mapUserList.get(i).longitude, mapUserList.get(i).fullName, mapUserList.get(i).onlineStatus, i);
            }
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 10);
            map.animateCamera(cameraUpdate);
        }
    }

    private void createMarker(String latitude, String longitude, final String fullName, final String onlineStatus, int i) {
        if (map == null) {
            return;
        }
        final LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(fullName);
        // markerOptions.snippet("" + i);

        if (onlineStatus.equals("1")) {
            markerOptions.icon((BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.map_green_ico))));
        } else {
            markerOptions.icon((BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.map_red_ico))));
        }
        Marker marker = map.addMarker(markerOptions);
        marker.setTag("" + i);
        mapUserLists.get(i).marker = marker;

    }

    private void toolbar(View view) {
        TextView tv_for_tittle = view.findViewById(R.id.tv_for_tittle);
        ImageView iv_for_back = view.findViewById(R.id.iv_for_back);
        tv_for_tittle.setText(R.string.map);
        iv_for_back.setVisibility(View.VISIBLE);
        iv_for_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
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

    @Override
    public void onResume() {
        mapview.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapview.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapview.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        intiMapView();

        if (latitude != null && longitude != null) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 12);
            map.animateCamera(cameraUpdate);
        }

        getAllData();

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String stPos = String.valueOf(marker.getTag());
                try {
                    int pos = Integer.parseInt(stPos);

                    UserList mapUserList = mapUserLists.get(pos);
                    ((UserMainActivity) getContext()).addFragment(ChatProfileFragment.newInstance(mapUserList, ""), true, R.id.framlayout);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return false;
            }
        });
    }

    private void intiMapView() {
        map.getUiSettings().setMyLocationButtonEnabled(true);
//        map.setMyLocationEnabled(true);
        MapsInitializer.initialize(this.getActivity());
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now

            if (ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Display UI and wait for user interaction
            } else {
                ActivityCompat.requestPermissions(
                        this.getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        Constant.ACCESS_FINE_LOCATION);
            }
        } else {
            // permission has been granted, continue as usual
            mapview.getMapAsync(UserMapFragment.this);

        }
    }

    private void getLocation() {
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

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == Constant.REQUEST_LOCATION) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // We can now safely use the API we requested access to
                mapview.getMapAsync(UserMapFragment.this);
            } else {
                // Permission was denied or request was cancelled
                Toast.makeText(getContext(), R.string.Permissions_denight, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Bitmap getMarkerBitmapFromView(@DrawableRes int resId) {
        View customMarkerView = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_marker, null);
        ImageView markerImageView = customMarkerView.findViewById(R.id.profile_image);
        markerImageView.setImageResource(resId);
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();


        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getWidth(), customMarkerView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null) drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }

    @Override
    public void callback(int position, UserList mapUserList) {
        Double lat = Double.parseDouble(mapUserList.latitude);
        Double lng = Double.parseDouble(mapUserList.longitude);
        LatLng latLng = new LatLng(lat, lng);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19));
        //   map.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);

        makeTheMarkerBounce(mapUserList);
    }

    private void makeTheMarkerBounce(UserList mapUserList) {
        final Marker marker = mapUserList.marker;
        final Handler handler = new Handler();

        final long startTime = SystemClock.uptimeMillis();
        final long duration = 2000;

        Projection proj = map.getProjection();
        final LatLng markerLatLng = marker.getPosition();
        Point startPoint = proj.toScreenLocation(markerLatLng);
        startPoint.offset(0, -100);
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);

        final Interpolator interpolator = new BounceInterpolator();


        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - startTime;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                double lng = t * markerLatLng.longitude + (1 - t) * startLatLng.longitude;
                double lat = t * markerLatLng.latitude + (1 - t) * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

}

