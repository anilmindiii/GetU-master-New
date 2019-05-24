package getu.app.com.getu.freelancer_side.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Matrix;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import getu.app.com.getu.R;
import getu.app.com.getu.app_session.Session;
import getu.app.com.getu.chat.model.ChatHistory;
import getu.app.com.getu.common_activity.NetworkErrorHomeActivity;
import getu.app.com.getu.user_side_package.acrivity.UserMainActivity;
import getu.app.com.getu.util.Constant;
import getu.app.com.getu.util.Utils;
import getu.app.com.getu.vollyemultipart.VolleyMultipartRequest;
import getu.app.com.getu.vollyemultipart.VolleySingleton;

public class HomeFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private ImageView iv_for_profileImg;
    private String mParam1;
    private DatabaseReference databaseReference;
    private HashMap<String, ChatHistory> listmap;
    private Session session;
    private TextView tv_for_chatCount;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1) {
        HomeFragment fragment = new HomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        toolbar(view);

        tv_for_chatCount = view.findViewById(R.id.tv_for_chatCount);
        session = new Session(getContext());
        listmap = new HashMap<String,ChatHistory>();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("history/"+session.getUserID());
        getMessageList();
        //addChatCount();

        TextView tv_for_name = view.findViewById(R.id.tv_for_name);
        TextView tv_for_category = view.findViewById(R.id.tv_for_category);
        TextView tv_for_address = view.findViewById(R.id.tv_for_address);
        iv_for_profileImg = view.findViewById(R.id.iv_for_profileImg);
        ImageView iv_for_profileImgOffline = view.findViewById(R.id.iv_for_profileImgOffline);

        tv_for_name.setText(session.getFullName());
        tv_for_address.setText(session.getAddressName());
        tv_for_category.setText(session.getCategoryName());
        tv_for_chatCount.setText(session.getChatCount());

        String pic = session.getProfilePic();
        if (!pic.equals("")) {
            Picasso.with(iv_for_profileImg.getContext()).load(pic).placeholder(R.drawable.user).into(iv_for_profileImg);
            Picasso.with(iv_for_profileImgOffline.getContext()).load(pic).placeholder(R.drawable.user).into(iv_for_profileImgOffline);
        }

        if (session.getUserOnlineStatus().equals("1")){
            iv_for_profileImg.setVisibility(View.VISIBLE);
            iv_for_profileImgOffline.setVisibility(View.GONE);
        }else {
            iv_for_profileImg.setVisibility(View.GONE);
            iv_for_profileImgOffline.setVisibility(View.VISIBLE);
        }

        return view;
    }

    private void toolbar(View view){
        iv_for_profileImg = view.findViewById(R.id.iv_for_profileImg);
        TextView tv_for_tittle = view.findViewById(R.id.tv_for_tittle);

        tv_for_tittle.setText(R.string.app_name);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void getMessageList() {
        databaseReference.orderByKey().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatHistory messageOutput = dataSnapshot.getValue(ChatHistory.class);
                if (!messageOutput.deleteby.equals(session.getUserID())) {
                    listmap.put(dataSnapshot.getKey(),messageOutput);
                    session.setChatCount(String.valueOf(listmap.size()));
                    tv_for_chatCount.setText(session.getChatCount());
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                ChatHistory messageOutput = dataSnapshot.getValue(ChatHistory.class);
                if (!messageOutput.deleteby.equals(session.getUserID())) {
                    listmap.put(dataSnapshot.getKey(),messageOutput);
                    session.setChatCount(String.valueOf(listmap.size()));
                    tv_for_chatCount.setText(session.getChatCount());
                }else {
                    listmap.remove(dataSnapshot.getKey());
                    session.setChatCount(String.valueOf(listmap.size()));
                    tv_for_chatCount.setText(session.getChatCount());
                }
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void addChatCount() {

        if (Utils.isNetworkAvailable(getContext())) {

            final Dialog pDialog = new Dialog(getContext());
            Constant.myDialog(getContext(),pDialog);
            pDialog.show();

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST,
                    Constant.URL_After_LOGIN + "updateChatToken", new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String data = new String(response.data);
                    Log.e("Response", data);

                    try {
                        JSONObject jsonObject = new JSONObject(data);

                        String message = jsonObject.getString("message");
                        String status = jsonObject.getString("status");

                        if (status.equals("SUCCESS")) {


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

                    params.put("chatCount", session.getChatCount());

                    return params;
                }

                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();

                    headers.put("authToken", session.getAuthToken());

                    return headers;
                }
            };

            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(getContext()).addToRequestQueue(multipartRequest);
        } else {
            //Toast.makeText(getContext(), R.string.check_net_connection, Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent(getActivity(),NetworkErrorHomeActivity.class);
            startActivity(intent1);
        }
    }
}
