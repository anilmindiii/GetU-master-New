package getu.app.com.getu.user_side_package.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import getu.app.com.getu.R;
import getu.app.com.getu.app_session.Session;
import getu.app.com.getu.chat.activity.ChatActivity;
import getu.app.com.getu.chat.fragment.UserChatFragment;
import getu.app.com.getu.common_activity.LoginActivity;
import getu.app.com.getu.common_activity.WelcomeActivity;
import getu.app.com.getu.freelancer_side.activity.FreelancerActivity;
import getu.app.com.getu.model.UserDetails;
import getu.app.com.getu.user_side_package.acrivity.UserMainActivity;
import getu.app.com.getu.user_side_package.adapter.UserAdapter;
import getu.app.com.getu.user_side_package.model.UserList;
import getu.app.com.getu.util.Constant;
import getu.app.com.getu.util.Utils;
import getu.app.com.getu.vollyemultipart.VolleyMultipartRequest;
import getu.app.com.getu.vollyemultipart.VolleySingleton;

public class ChatProfileFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private Session session;
    private UserList userList;
    private ImageView iv_for_back,iv_for_chat,iv_for_profileImg;
    private TextView tv_for_tittle,tv_for_address,tv_for_name,tv_for_distance,tv_for_email,tv_for_contactNo,tv_for_category,tv_for_discription,tv_for_moreDetails,tv_for_aboutName;
    private LinearLayout layout_for_email,layout_for_contactNo;

    public ChatProfileFragment() {
        // Required empty public constructor
    }

    public static ChatProfileFragment newInstance(UserList param1, String param2) {
        ChatProfileFragment fragment = new ChatProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            userList = (UserList) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_chat_profile, container, false);
        toolbar(view);
        initView(view);

        view.setFocusableInTouchMode(true);
        view.setClickable(true);
        view.requestFocus();

        session = new Session(getContext());
        if (!session.getIsLogedIn()) {
            layout_for_email.setVisibility(View.GONE);
            layout_for_contactNo.setVisibility(View.GONE);
            tv_for_moreDetails.setVisibility(View.VISIBLE);
        }else {
            layout_for_email.setVisibility(View.VISIBLE);
            layout_for_contactNo.setVisibility(View.VISIBLE);
            tv_for_moreDetails.setVisibility(View.GONE);
            sendNotificationAPI();
        }

        tv_for_moreDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guestUserDialog();
            }
        });

        iv_for_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
            }
        });

        if (userList.profileimage != null && !userList.profileimage.equals("")) {
            Picasso.with(getContext()).load(userList.profileimage).placeholder(R.drawable.user).into(iv_for_profileImg);
        }
        tv_for_address.setText(userList.address);
        tv_for_name.setText(userList.fullName);

        if (userList.fullName != null && !userList.fullName.equals("")) {
            String firstName = userList.fullName.replaceAll(" .*", "");
            tv_for_aboutName.setText(firstName);
            tv_for_tittle.setText(firstName);
        }

        if (userList.fullName != null && !userList.email.equals("")){
            tv_for_email.setText(userList.email);
        }else {
            tv_for_email.setText("NA");
        }
       String phone = userList.countryCode+userList.contactNo;
        if (!phone.equals("")){
            tv_for_contactNo.setText(phone);
        }else {
            tv_for_contactNo.setText("NA");
        }

        tv_for_category.setText(userList.category);

        try{
            Double str = Double.valueOf(userList.distance);
            String value = new DecimalFormat("#.##").format(str);
            tv_for_distance.setText(value);
        }catch (Exception e){

        }


        if (userList.fullName != null && !userList.description.equals(""))
        {
            tv_for_discription.setText(userList.description);
        }else {
            tv_for_discription.setText("NA");
        }
        return view;
    }

    private void guestUserDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_guestuser);
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
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void initView(View view){
        iv_for_profileImg = view.findViewById(R.id.iv_for_profileImg);
        tv_for_address = view.findViewById(R.id.tv_for_address);
        tv_for_name = view.findViewById(R.id.tv_for_name);
        tv_for_distance = view.findViewById(R.id.tv_for_distance);
        tv_for_email = view.findViewById(R.id.tv_for_email);
        tv_for_contactNo = view.findViewById(R.id.tv_for_contactNo);
        tv_for_category = view.findViewById(R.id.tv_for_category);
        tv_for_discription = view.findViewById(R.id.tv_for_discription);
        layout_for_email = view.findViewById(R.id.layout_for_email);
        layout_for_contactNo = view.findViewById(R.id.layout_for_contactNo);
        tv_for_moreDetails = view.findViewById(R.id.tv_for_moreDetails);
        tv_for_aboutName = view.findViewById(R.id.tv_for_aboutName);

    }

    public void sendNotificationAPI() {

        if (Utils.isNetworkAvailable(getContext())) {

            final Dialog pDialog = new Dialog(getActivity());
            Constant.myDialog(getActivity(),pDialog);
            pDialog.show();

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, Constant.URL_After_LOGIN + "viewProfileNotification", new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String data = new String(response.data);
                    Log.e("Response", data);

                    try {
                        JSONObject jsonObject = new JSONObject(data);

                        String userstatus = jsonObject.getString("userstatus");
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equals("SUCCESS")) {
                            pDialog.dismiss();

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
                   // Toast.makeText(getContext(), networkResponse + "", Toast.LENGTH_SHORT).show();
                    pDialog.dismiss();
                    error.printStackTrace();
                }
            }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();

                    headers.put("authToken", session.getAuthToken());
                    return headers;
                }

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();

                    params.put("receiverId", userList.userId);

                    return params;
                }

            };

            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(getContext()).addToRequestQueue(multipartRequest);
        } else {
            Toast.makeText(getContext(), R.string.check_net_connection, Toast.LENGTH_SHORT).show();
        }
    }

    private void toolbar(View view){
        iv_for_back = view.findViewById(R.id.iv_for_back);
        iv_for_chat = view.findViewById(R.id.iv_for_chat);
        tv_for_tittle = view.findViewById(R.id.tv_for_tittle);

        iv_for_chat.setVisibility(View.VISIBLE);
        iv_for_back.setVisibility(View.VISIBLE);

        iv_for_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (session.getIsLogedIn()){
                   Intent intent = new Intent(getActivity(), ChatActivity.class);
                   String uid = userList.userId.toString();
                   String name = userList.fullName.toString();
                   String img = userList.profileimage.toString();

                   intent.putExtra("USER_ID",uid);
                   intent.putExtra("FULLNAME",name);
                   intent.putExtra("PROFILE_PIC",img);
                   startActivity(intent);

                   /* Intent yourIntent = new Intent(getActivity(), ChatActivity.class);
                    Bundle b = new Bundle();
                    b.putSerializable("user", userList);
                    yourIntent.putExtras(b); //pass bundle to your intent
                    startActivity(yourIntent);*/

                }else {
                    guestUserDialog();
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

}
