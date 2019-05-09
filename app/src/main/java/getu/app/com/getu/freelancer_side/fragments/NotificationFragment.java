package getu.app.com.getu.freelancer_side.fragments;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import getu.app.com.getu.R;
import getu.app.com.getu.app_session.Session;
import getu.app.com.getu.common_activity.WelcomeActivity;
import getu.app.com.getu.freelancer_side.adapter.NotificationAdapter;
import getu.app.com.getu.freelancer_side.model.NotificationListModel;
import getu.app.com.getu.hepler.CusDialogProg;
import getu.app.com.getu.model.UserDetails;
import getu.app.com.getu.user_side_package.adapter.MapUserAdapter;
import getu.app.com.getu.user_side_package.fragment.UserMapFragment;
import getu.app.com.getu.user_side_package.model.UserList;
import getu.app.com.getu.util.Constant;
import getu.app.com.getu.util.Utils;
import getu.app.com.getu.vollyemultipart.VolleyMultipartRequest;
import getu.app.com.getu.vollyemultipart.VolleySingleton;

public class NotificationFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private String mParam1;
    private Session session;
    private ArrayList<NotificationListModel> notificationListModels;
    private NotificationAdapter notificationAdapter;
    private RecyclerView recycler_view;
    private TextView tv_for_noData;

    public NotificationFragment() {
        // Required empty public constructor
    }

    public static NotificationFragment newInstance(String param1) {
        NotificationFragment fragment = new NotificationFragment();
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
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        toolbar(view);

        session = new Session(getContext());
        recycler_view = view.findViewById(R.id.recycler_view);
        tv_for_noData = view.findViewById(R.id.tv_for_noData);

        notificationListModels = new ArrayList<NotificationListModel>();
        notificationAdapter = new NotificationAdapter(notificationListModels, getActivity());
        recycler_view.setAdapter(notificationAdapter);

        notificationList();

        return view;
    }

    private void toolbar(View view) {
        TextView tv_for_tittle = view.findViewById(R.id.tv_for_tittle);

        tv_for_tittle.setText(R.string.notification);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void notificationList() {

        if (Utils.isNetworkAvailable(getContext())) {

            /*final Dialog pDialog = new Dialog(getActivity());
            CusDialogProg.myDialog(getActivity(),pDialog);
            pDialog.show();*/

            final Dialog pDialog = new Dialog(getActivity());
            Constant.myDialog(getActivity(),pDialog);
            pDialog.show();

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.GET,
                    Constant.URL_After_LOGIN + "getNotificationList", new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String data = new String(response.data);
                    Log.e("Response", data);

                    try {
                        JSONObject jsonObject = new JSONObject(data);

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        String userstatus = jsonObject.getString("userstatus");

                        if (status.equals("SUCCESS")) {
                           pDialog.dismiss();
                            if (userstatus.equals("1")) {
                                notificationListModels.clear();
                                JSONArray result = jsonObject.getJSONArray("data");
                                for (int i = 0; i < result.length(); i++) {

                                    JSONObject object = result.getJSONObject(i);
                                    NotificationListModel notificationListModel = new NotificationListModel();

                                    notificationListModel.message = object.getString("message");
                                    notificationListModel.notificationTime = object.getString("notificationTime");
                                    notificationListModel.profileImage = object.getString("profileImage");
                                    notificationListModel.fullName = object.getString("fullName");
                                    notificationListModel.userId = object.getString("userId");

                                    notificationListModels.add(notificationListModel);
                                }
                                tv_for_noData.setVisibility(View.GONE);
                                notificationAdapter.notifyDataSetChanged();

                            } else {
                                Utils.customAlertDialog(getActivity(), "Alert!", "You are temporary inactive by admin");
                            }

                        } else {
                           // Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            pDialog.dismiss();
                        }

                    } catch (Throwable t) {
                        Log.e("My App", "Could not parse malformed JSON: \"" + response + "\"");
                    }

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
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<String, String>();

                    headers.put("authToken", session.getAuthToken());

                    return headers;
                }
            };

            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(getContext()).addToRequestQueue(multipartRequest);
        } else {
            Toast.makeText(getActivity(), R.string.check_net_connection, Toast.LENGTH_SHORT).show();
        }
    }

}
