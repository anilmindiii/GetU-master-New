package getu.app.com.getu.freelancer_side.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import getu.app.com.getu.R;
import getu.app.com.getu.app_session.Session;
import getu.app.com.getu.common_activity.LoginActivity;
import getu.app.com.getu.common_activity.OTPActivity;
import getu.app.com.getu.common_activity.WelcomeActivity;
import getu.app.com.getu.freelancer_side.activity.AboutUsFreelancerActivity;
import getu.app.com.getu.freelancer_side.activity.TandCFreelancerActivity;
import getu.app.com.getu.model.CodeInfo;
import getu.app.com.getu.user_side_package.acrivity.AboutUsActivity;
import getu.app.com.getu.user_side_package.acrivity.TandCActivity;
import getu.app.com.getu.util.Constant;
import getu.app.com.getu.util.Utils;
import getu.app.com.getu.vollyemultipart.VolleyMultipartRequest;
import getu.app.com.getu.vollyemultipart.VolleySingleton;
import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

public class SettingFragment extends Fragment implements View.OnClickListener {
    private static final String ARG_PARAM1 = "param1";
    private String mParam1;
    private Session session;
    private RelativeLayout layout_for_logout, layout_for_changeNumber, layout_for_changePassword,layout_for_sharing,layout_for_AboutUs,layout_for_TandC,layout_for_feedback;
    private ToggleButton tbutton, toggle_button;
    private Boolean isvalidate = false;
    private String countryCode;
    private String otpNew;
    private List<CodeInfo> countries;
    private Dialog pDialog;

    public SettingFragment() {
        // Required empty public constructor
    }

    public static SettingFragment newInstance(String param1) {
        SettingFragment fragment = new SettingFragment();
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
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        pDialog = new Dialog(getActivity());
        Constant.myDialog(getActivity(),pDialog);

        toolbar(view);
        initView(view);
        session = new Session(getContext());

        countries = Utils.loadCountries(getContext());

        if (session.getUserOnlineStatus().equals("1")) {
            toggle_button.setChecked(false);
        } else {
            toggle_button.setChecked(true);
        }

        layout_for_logout.setOnClickListener(this);
        toggle_button.setOnClickListener(this);
        tbutton.setOnClickListener(this);
        layout_for_changeNumber.setOnClickListener(this);
        layout_for_changePassword.setOnClickListener(this);
        layout_for_sharing.setOnClickListener(this);
        layout_for_AboutUs.setOnClickListener(this);
        layout_for_TandC.setOnClickListener(this);
        layout_for_feedback.setOnClickListener(this);

        if (session.getNotificationStatus().equals("1")) {
            tbutton.setChecked(false);
        } else {
            tbutton.setChecked(true);
        }

        return view;
    }

    private void initView(View view) {
        toggle_button = view.findViewById(R.id.toggle_button);
        layout_for_logout = view.findViewById(R.id.layout_for_logout);
        tbutton = view.findViewById(R.id.tbutton);
        layout_for_changeNumber = view.findViewById(R.id.layout_for_changeNumber);
        layout_for_changePassword = view.findViewById(R.id.layout_for_changePassword);
        layout_for_sharing = view.findViewById(R.id.layout_for_sharing);
        layout_for_AboutUs = view.findViewById(R.id.layout_for_AboutUs);
        layout_for_TandC = view.findViewById(R.id.layout_for_TandC);
        layout_for_feedback = view.findViewById(R.id.layout_for_feedback);
    }

    private void toolbar(View view) {
        TextView tv_for_tittle = view.findViewById(R.id.tv_for_tittle);
        tv_for_tittle.setText(R.string.settings);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public void changeWorkingStatus() {

        if (Utils.isNetworkAvailable(getContext())) {

            pDialog.show();

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.GET,
                    Constant.URL_BEFORE_LOGIN + "user/updateOnlineStatus", new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String data = new String(response.data);
                    Log.e("Response", data);

                    try {
                        JSONObject jsonObject = new JSONObject(data);

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                       JSONObject obj =  jsonObject.getJSONObject("data");

                        String online_status = obj.getString("online_status");
                        String userstatus = obj.getString("userStatus");


                        if (status.equalsIgnoreCase("success")) {

                            if (userstatus.equals("1")) {
                                if (online_status.equals("1")) {
                                    session.setUserOnlineStatus("0");
                                } else {
                                    session.setUserOnlineStatus("1");
                                }

                            } else {
                                //Utils.customAlertDialog(getActivity(), "Alert!", "Your account has been inactivated by admin, please contact to activate");
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

                    headers.put("Auth-Token", session.getAuthToken());

                    return headers;
                }
            };

            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(getContext()).addToRequestQueue(multipartRequest);
        } else {
            Toast.makeText(getActivity(), R.string.check_net_connection, Toast.LENGTH_SHORT).show();
        }
    }

    public void changeNotificationStatus() {

        if (Utils.isNetworkAvailable(getContext())) {

            pDialog.show();

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.GET,
                    Constant.URL_After_LOGIN + "notificationStatus", new Response.Listener<NetworkResponse>() {
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

                            if (userstatus.equals("1")) {

                                String notificationStatus = jsonObject.getString("notificationStatus");

                                if (notificationStatus.equals("1")) {
                                    session.setNotificationStatus("1");
                                    FirebaseDatabase.getInstance().getReference().child("users").child(session.getUserID()).child("notificationStatus").setValue("1");
                                } else {
                                    session.setNotificationStatus("0");
                                    FirebaseDatabase.getInstance().getReference().child("users").child(session.getUserID()).child("notificationStatus").setValue("0");
                                }
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

                            } else {
                                Utils.customAlertDialog(getActivity(), "Alert!", "Your account has been inactivated by admin, please contact to activate");
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

    private void changePasswordDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_change_password);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Button btn_for_save = dialog.findViewById(R.id.btn_for_save);
        ImageView iv_for_cansel = dialog.findViewById(R.id.iv_for_cansel);
        final EditText et_for_oldPassword = dialog.findViewById(R.id.et_for_oldPassword);
        final EditText et_for_newPassword = dialog.findViewById(R.id.et_for_newPassword);

        iv_for_cansel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn_for_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String newPassword = et_for_newPassword.getText().toString();
                final String oldPassword = et_for_oldPassword.getText().toString();

                if (oldPassword.equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), R.string.enter_old_password, Toast.LENGTH_LONG).show();
                    et_for_oldPassword.requestFocus();
                } else if (oldPassword.length() < 6) {
                    Toast.makeText(getContext(), R.string.password_required, Toast.LENGTH_LONG).show();
                    et_for_oldPassword.requestFocus();
                } else if (newPassword.equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), R.string.enter_new_password, Toast.LENGTH_LONG).show();
                    et_for_newPassword.requestFocus();
                } else if (newPassword.length() < 6) {
                    Toast.makeText(getContext(), R.string.password_required, Toast.LENGTH_LONG).show();
                    et_for_newPassword.requestFocus();
                } else {

                    AlertDialog.Builder dialogNew = new AlertDialog.Builder(getContext());
                    dialogNew.setCancelable(false);
                    dialogNew.setTitle("Alert!");
                    dialogNew.setMessage(R.string.session);
                    dialogNew.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            changePassword(newPassword, oldPassword);
                        }
                    });
                    dialogNew.setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    final AlertDialog alert = dialogNew.create();
                    alert.show();
                }
            }
        });

        dialog.show();
    }

    public void changePassword(final String newPassword, final String oldPassword) {

        if (Utils.isNetworkAvailable(getContext())) {

            pDialog.show();

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST,
                    Constant.URL_After_LOGIN + "changePassword", new Response.Listener<NetworkResponse>() {
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

                            if (userstatus.equals("1")) {

                                Intent intent = new Intent(getContext(), WelcomeActivity.class);
                                getContext().startActivity(intent);
                                if (session != null) {
                                    session.logout();
                                    Constant.USER_TYPE = "0";
                                }
                                getActivity().finish();

                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

                            } else {
                                Utils.customAlertDialog(getActivity(), "Alert!", "Your account has been inactivated by admin, please contact to activate");
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

                    params.put("newPassword", newPassword);
                    params.put("oldPassword", oldPassword);

                    return params;
                }

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

    private void SelectCountry(final TextView tv_for_code) {
        List<String> list = new ArrayList<>();
        for (CodeInfo country : countries) {
            list.add(country.country_name + " (+" + country.phone_code + ")");
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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

    private void changeContactNumberDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_change_contact);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Button btn_for_save = dialog.findViewById(R.id.btn_for_save);
        ImageView iv_for_cansel = dialog.findViewById(R.id.iv_for_cansel);
        LinearLayout layout_for_code = dialog.findViewById(R.id.layout_for_code);
        final EditText et_for_contactNo = dialog.findViewById(R.id.et_for_contactNo);
        final TextView tv_for_code = dialog.findViewById(R.id.tv_for_code);

        layout_for_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectCountry(tv_for_code);
            }
        });

        iv_for_cansel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn_for_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String code = tv_for_code.getText().toString();
                final String contactNo = et_for_contactNo.getText().toString();

                if (!et_for_contactNo.getText().toString().trim().equalsIgnoreCase("")) {
                    checkPhoneNumber(et_for_contactNo);
                }

                if (contactNo.equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), R.string.contactNo, Toast.LENGTH_LONG).show();
                    et_for_contactNo.requestFocus();
                } else if (contactNo.length() < 4) {
                    Toast.makeText(getContext(), R.string.contact_required, Toast.LENGTH_LONG).show();
                    et_for_contactNo.requestFocus();
                } else if (!isvalidate) {
                    Toast.makeText(getContext(), R.string.contact_wrong, Toast.LENGTH_SHORT).show();
                } else {
                    final AlertDialog.Builder dialogNew = new AlertDialog.Builder(getContext());
                    dialogNew.setCancelable(false);
                    dialogNew.setTitle("Alert!");
                    dialogNew.setMessage(R.string.session_contact);
                    dialogNew.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            changeContactNumber(contactNo, code);
                        }
                    });
                    dialogNew.setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    final AlertDialog alert = dialogNew.create();
                    alert.show();
                }
            }
        });

        dialog.show();
    }

    public void changeContactNumber(final String contactNo, final String code) {

        if (Utils.isNetworkAvailable(getContext())) {

            pDialog.show();

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST,
                    Constant.URL_After_LOGIN + "changeNumber", new Response.Listener<NetworkResponse>() {
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

                            if (userstatus.equals("1")) {

                                String otp = jsonObject.getString("otp");
                                otpVerificationDialog(code, contactNo, otp);

                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

                            } else {
                                Utils.customAlertDialog(getActivity(), "Alert!", "Your account has been inactivated by admin, please contact to activate");
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

                    params.put("countryCode", code);
                    params.put("contactNo", contactNo);

                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<String, String>();

                    String auth = session.getAuthToken();
                    headers.put("authToken", auth);

                    return headers;
                }
            };

            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(getContext()).addToRequestQueue(multipartRequest);
        } else {
            Toast.makeText(getActivity(), R.string.check_net_connection, Toast.LENGTH_SHORT).show();
        }
    }

    public void reSendOTP(final String code, final String contactNo) {

        if (Utils.isNetworkAvailable(getContext())) {

            pDialog.show();

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST,
                    Constant.URL_After_LOGIN + "changeNumber", new Response.Listener<NetworkResponse>() {
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

                            if (userstatus.equals("1")) {
                                otpNew = jsonObject.getString("otp");

                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

                            } else {
                                Utils.customAlertDialog(getActivity(), "Alert!", "You are temporary inactive by admin");
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

                    params.put("countryCode", code);
                    params.put("contactNo", contactNo);

                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<String, String>();

                    String auth = session.getAuthToken();
                    headers.put("authToken", auth);

                    return headers;
                }
            };

            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(getContext()).addToRequestQueue(multipartRequest);
        } else {
            Toast.makeText(getActivity(), R.string.check_net_connection, Toast.LENGTH_SHORT).show();
        }

    }

    private void otpVerificationDialog(final String code, final String contactNo, final String otp) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_otp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Button btn_for_save = dialog.findViewById(R.id.btn_for_save);
        ImageView iv_for_cansel = dialog.findViewById(R.id.iv_for_cansel);

        final EditText et_for_firstOTP = dialog.findViewById(R.id.et_for_firstOTP);
        final EditText et_for_secondOTP = dialog.findViewById(R.id.et_for_secondOTP);
        final EditText et_for_thirdOTP = dialog.findViewById(R.id.et_for_thirdOTP);
        final EditText et_for_forthOTP = dialog.findViewById(R.id.et_for_forthOTP);

        TextView tv_for_resnedOTP = dialog.findViewById(R.id.tv_for_resnedOTP);

        tv_for_resnedOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reSendOTP(code, contactNo);
            }
        });

        et_for_firstOTP.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et_for_firstOTP.getText().toString().length() == 1)     //size as per your requirement
                {
                    et_for_secondOTP.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        et_for_secondOTP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et_for_secondOTP.getText().toString().length() == 1)     //size as per your requirement
                {
                    et_for_thirdOTP.requestFocus();
                } else if (et_for_secondOTP.getText().toString().length() == 0)     //size as per your requirement
                {
                    et_for_firstOTP.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        et_for_thirdOTP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et_for_thirdOTP.getText().toString().length() == 1)     //size as per your requirement
                {
                    et_for_forthOTP.requestFocus();
                } else if (et_for_thirdOTP.getText().toString().length() == 0)     //size as per your requirement
                {
                    et_for_secondOTP.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        et_for_forthOTP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et_for_forthOTP.getText().toString().length() == 1)     //size as per your requirement
                {
                    et_for_forthOTP.requestFocus();
                } else if (et_for_forthOTP.getText().toString().length() == 0)     //size as per your requirement
                {
                    et_for_thirdOTP.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        iv_for_cansel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn_for_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (et_for_firstOTP.getText().toString().trim().equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), R.string.otp, Toast.LENGTH_LONG).show();
                    et_for_firstOTP.requestFocus();
                } else if (et_for_secondOTP.getText().toString().trim().equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), R.string.otp, Toast.LENGTH_LONG).show();
                    et_for_secondOTP.requestFocus();
                } else if (et_for_thirdOTP.getText().toString().trim().equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), R.string.otp, Toast.LENGTH_LONG).show();
                    et_for_thirdOTP.requestFocus();
                } else if (et_for_forthOTP.getText().toString().trim().equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), R.string.otp, Toast.LENGTH_LONG).show();
                    et_for_forthOTP.requestFocus();
                } else {

                    String firstOTP = et_for_firstOTP.getText().toString().trim();
                    String secondOTP = et_for_secondOTP.getText().toString().trim();
                    String thirdOTP = et_for_thirdOTP.getText().toString().trim();
                    String forthOTP = et_for_forthOTP.getText().toString().trim();

                    otpNew = firstOTP + secondOTP + thirdOTP + forthOTP;

                    if (otp.equals(otpNew)) {
                        otpVerifiation(code, contactNo, otpNew, dialog);
                    }else {
                        Toast.makeText(getContext(), R.string.worng_otp, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        dialog.show();
    }

    public void otpVerifiation(final String code, final String contactNo, final String otpNew, final Dialog dialog) {

        if (Utils.isNetworkAvailable(getContext())) {

            pDialog.show();

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST,
                    Constant.URL_After_LOGIN + "verifyOTP", new Response.Listener<NetworkResponse>() {
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

                            if (userstatus.equals("1")) {
                                dialog.dismiss();
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(getContext(), WelcomeActivity.class);
                                startActivity(intent);

                            } else {
                                Utils.customAlertDialog(getActivity(), "Alert!", "You are temporary inactive by admin");
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

                    params.put("contactNo", contactNo);
                    params.put("countryCode", code);
                    params.put("OTP", otpNew);

                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<String, String>();

                    String auth = session.getAuthToken();
                    headers.put("authToken", auth);

                    return headers;
                }
            };

            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(getContext()).addToRequestQueue(multipartRequest);
        } else {
            Toast.makeText(getActivity(), R.string.check_net_connection, Toast.LENGTH_SHORT).show();
        }
    }

    private void checkPhoneNumber(EditText et_for_contactNo) {
        String contactNo = et_for_contactNo.getText().toString();
        try {
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.createInstance(getContext());
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

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void addFeedbackDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_feedback);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Button btn_for_save = dialog.findViewById(R.id.btn_for_save);
        ImageView iv_for_cansel = dialog.findViewById(R.id.iv_for_cansel);
        final EditText et_for_feedback = dialog.findViewById(R.id.et_for_feedback);

        iv_for_cansel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn_for_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String feedback = et_for_feedback.getText().toString();

                if (feedback.equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), "Please enter feedback", Toast.LENGTH_LONG).show();
                    et_for_feedback.requestFocus();
                } else {
                    setfeedback(feedback, dialog);
                }
            }
        });

        dialog.show();
    }

    public void setfeedback(final String feedback, final Dialog dialog) {
        if (Utils.isNetworkAvailable(getContext())) {

            pDialog.show();

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST,
                    Constant.URL_After_LOGIN + "addFeedback", new Response.Listener<NetworkResponse>() {
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

                            if (userstatus.equals("1")) {
                                dialog.dismiss();
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

                            } else {
                                Utils.customAlertDialog(getActivity(), "Alert!", "Your account has been inactivated by admin, please contact to activate");
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

                    params.put("feedback", feedback);

                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<String, String>();

                    String auth = session.getAuthToken();
                    headers.put("authToken", auth);

                    return headers;
                }
            };

            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(getContext()).addToRequestQueue(multipartRequest);
        } else {
            Toast.makeText(getActivity(), R.string.check_net_connection, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_for_logout:
                Constant.logout(getActivity());
                break;
            case R.id.toggle_button:
                changeWorkingStatus();
                break;
            case R.id.tbutton:
                changeNotificationStatus();
                break;
            case R.id.layout_for_changePassword:
                if (session.getSocialID().equals("")&&session.getSocialType().equals("")) {
                    changePasswordDialog();
                } else {
                    Toast.makeText(getActivity(), R.string.password_change, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.layout_for_changeNumber:
                changeContactNumberDialog();
                break;
            case R.id.layout_for_feedback:
                addFeedbackDialog();
                break;
            case R.id.layout_for_sharing:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "http://www.mindiii.com/");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;
            case R.id.layout_for_AboutUs:
                Intent intent = new Intent(getContext(), AboutUsFreelancerActivity.class);
                startActivity(intent);
                break;
            case R.id.layout_for_TandC:
                intent = new Intent(getContext(), TandCFreelancerActivity.class);
                startActivity(intent);
                break;
        }

    }
}
