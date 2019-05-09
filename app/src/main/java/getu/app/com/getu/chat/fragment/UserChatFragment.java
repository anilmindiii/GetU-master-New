package getu.app.com.getu.chat.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SimpleTimeZone;

import getu.app.com.getu.R;
import getu.app.com.getu.app_session.Session;
import getu.app.com.getu.chat.adapter.ChatHistoryAdapter;
import getu.app.com.getu.chat.model.ChatHistory;
import getu.app.com.getu.chat.model.Chatting;
import getu.app.com.getu.chat.model.FirebaseData;
import getu.app.com.getu.freelancer_side.adapter.NotificationAdapter;
import getu.app.com.getu.freelancer_side.model.NotificationListModel;
import getu.app.com.getu.util.Constant;
import getu.app.com.getu.util.Utils;
import getu.app.com.getu.vollyemultipart.VolleyMultipartRequest;
import getu.app.com.getu.vollyemultipart.VolleySingleton;

public class UserChatFragment extends Fragment {
    private String mParam1;
    private Session session;
    private ArrayList<ChatHistory> chatHistories;
    private ArrayList<ChatHistory> newList;
    private ArrayList<FirebaseData> userDataList;
    private HashMap<String, ChatHistory> listmap;
    private HashMap<String, FirebaseData> userlistmap;
    private ChatHistoryAdapter chatHistoryAdapter;
    private DatabaseReference databaseReference;
    private RelativeLayout layout_for_noData;
    private Toolbar searchtollbar;
    private Menu search_menu;
    private MenuItem item_search;
    private int i;

    public UserChatFragment() {
        // Required empty public constructor
    }

    public static UserChatFragment newInstance(String param1) {
        UserChatFragment fragment = new UserChatFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString("param1");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_user_chat, container, false);

        session = new Session(getActivity());
        databaseReference = FirebaseDatabase.getInstance().getReference().child("history/" + session.getUserID());

        RecyclerView recycler_view = view.findViewById(R.id.recycler_view);
        layout_for_noData = view.findViewById(R.id.layout_for_noData);

        newList = new ArrayList<ChatHistory>();
        userDataList = new ArrayList<FirebaseData>();
        chatHistories = new ArrayList<ChatHistory>();
        listmap = new HashMap<String,ChatHistory>();
        userlistmap = new HashMap<String,FirebaseData>();

        chatHistoryAdapter = new ChatHistoryAdapter(newList, getActivity());
        recycler_view.setAdapter(chatHistoryAdapter);
        chatHistoryAdapter.notifyDataSetChanged();

        setHasOptionsMenu(true);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        searchtollbar = view.findViewById(R.id.searchtoolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        setSearchtollbar();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getMessageList();
        addChatCount();
    }

    private void getMessageList() {
        chatHistories.clear();
        newList.clear();
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatHistory messageOutput = dataSnapshot.getValue(ChatHistory.class);

                if (!messageOutput.deleteby.equals(session.getUserID())) {
                    listmap.put(dataSnapshot.getKey(),messageOutput);
                    layout_for_noData.setVisibility(View.GONE);
                    Collection<ChatHistory> demoValues = listmap.values();
                    newList.clear();
                    chatHistories.clear();

                    newList.addAll(demoValues);

                    for (int l =0 ;l<newList.size();l++){
                        String id = newList.get(l).uid;
                        FirebaseDatabase.getInstance().getReference().child("users").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot != null && !dataSnapshot.equals("")) {
                                    FirebaseData messageOutput = dataSnapshot.getValue(FirebaseData.class);
                                    String name = messageOutput.name;
                                    String image = messageOutput.profilePic;

                                    for (int k = 0; k < newList.size(); k++) {
                                        if (newList.get(k).uid.equals(messageOutput.uid)) {
                                            newList.get(k).name = name;
                                            newList.get(k).profilePic = image;
                                        }
                                    }
                                    chatHistories.addAll(newList);
                                    shorting();
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                ChatHistory messageOutput = dataSnapshot.getValue(ChatHistory.class);

                if (!messageOutput.deleteby.equals(session.getUserID())) {
                    listmap.put(dataSnapshot.getKey(), messageOutput);
                    Collection<ChatHistory> demoValues = listmap.values();
                    newList.clear();
                    chatHistories.clear();
                    newList.addAll(demoValues);
                    for (int l =0 ;l<newList.size();l++) {
                        String id = newList.get(l).uid;
                        FirebaseDatabase.getInstance().getReference().child("users").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                FirebaseData messageOutput = dataSnapshot.getValue(FirebaseData.class);
                                String name = messageOutput.name;
                                String image = messageOutput.profilePic;

                                for (int k = 0; k < newList.size(); k++) {
                                    if (newList.get(k).uid.equals(messageOutput.uid)) {
                                        newList.get(k).name = name;
                                        newList.get(k).profilePic = image;
                                    }
                                }
                                chatHistories.addAll(newList);
                                session.setChatCount(newList.size() + "");
                                shorting();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }else {
                    listmap.remove(dataSnapshot.getKey());
                    Collection<ChatHistory> demoValues = listmap.values();
                    newList.clear();
                    chatHistories.clear();
                    newList.addAll(demoValues);

                        for (int l =0 ;l<newList.size();l++) {
                            String id = newList.get(l).uid;
                            FirebaseDatabase.getInstance().getReference().child("users").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    FirebaseData messageOutput = dataSnapshot.getValue(FirebaseData.class);
                                    String name = messageOutput.name;
                                    String image = messageOutput.profilePic;

                                    for (int k = 0; k < newList.size(); k++) {
                                        if (newList.get(k).uid.equals(messageOutput.uid)) {
                                            newList.get(k).name = name;
                                            newList.get(k).profilePic = image;
                                        }
                                    }
                                    chatHistories.addAll(newList);
                                    shorting();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }

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



    public void shorting() {

        Collections.sort(newList, new Comparator<ChatHistory>() {
            @Override
            public int compare(ChatHistory a1, ChatHistory a2) {
                if (a1.timeStamp == null || a2.timeStamp == null) {
                    return -1;
                } else {
                    Long long1 = Long.parseLong(String.valueOf(a1.timeStamp));
                    Long long2 = Long.parseLong(String.valueOf(a2.timeStamp));
                    return long2.compareTo(long1);
                }
            }
        });
        chatHistoryAdapter.notifyDataSetChanged();
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_home, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_search:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    circleReveal(R.id.searchtoolbar,1,true,true);
                else
                    searchtollbar.setVisibility(View.VISIBLE);

                item_search.expandActionView();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setSearchtollbar()
    {
        if (searchtollbar != null) {
            searchtollbar.inflateMenu(R.menu.menu_search);
            search_menu=searchtollbar.getMenu();

            searchtollbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        circleReveal(R.id.searchtoolbar,1,true,false);
                    else
                        searchtollbar.setVisibility(View.GONE);
                }
            });

            item_search = search_menu.findItem(R.id.action_filter_search);

            MenuItemCompat.setOnActionExpandListener(item_search, new MenuItemCompat.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    // Do something when collapsed
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        circleReveal(R.id.searchtoolbar,1,true,false);
                    }
                    else
                        searchtollbar.setVisibility(View.GONE);
                    return true;
                }

                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    // Do something when expanded
                    return true;
                }
            });

            initSearchView();

        } else
            Log.d("toolbar", "setSearchtollbar: NULL");
    }

    public void initSearchView()
    {
        final SearchView searchView =
                (SearchView) search_menu.findItem(R.id.action_filter_search).getActionView();

        // Enable/Disable Submit button in the keyboard

        searchView.setSubmitButtonEnabled(false);

        // Change search close button image

        ImageView closeButton = (ImageView) searchView.findViewById(R.id.search_close_btn);
        closeButton.setImageResource(R.drawable.ic_close);

        // set hint and the text colors

        EditText txtSearch = ( searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text));
        txtSearch.setHint("Search..");
        txtSearch.setHintTextColor(getResources().getColor(R.color.lightGrey));
        txtSearch.setTextColor(getResources().getColor(R.color.darkGrey));

        // set the cursor

        AutoCompleteTextView searchTextView =  searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchTextView, R.drawable.search_cursor); //This sets the cursor resource ID to 0 or @null which will make it visible on white background

        } catch (Exception e) {
            e.printStackTrace();
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                callSearch(query);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                callSearch(newText);
                return true;
            }

            void callSearch(String query) {
                //Do searching
                Log.i("query", "" + query);

                newList.clear();
                if (!query.trim().equals("")) {
                    for (int i = 0; i < chatHistories.size(); i++) {
                        if (chatHistories.get(i).name.toLowerCase().contains(query)||chatHistories.get(i).name.toUpperCase().contains(query)) {
                            newList.add(chatHistories.get(i));
                        }
                    }
                } else {
                    newList.addAll(chatHistories);
                }
                chatHistoryAdapter.notifyDataSetChanged();
            }

        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void circleReveal(int viewID, int posFromRight, boolean containsOverflow, final boolean isShow)
    {
        final View myView = getActivity().findViewById(viewID);

        int width=myView.getWidth();

        if(posFromRight>0)
            width-=(posFromRight*getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material))-(getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material)/ 2);
        if(containsOverflow)
            width-=getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material);

        int cx=width;
        int cy=myView.getHeight()/2;

        Animator anim;
        if(isShow)
            anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0,(float)width);
        else
            anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, (float)width, 0);

        anim.setDuration((long)220);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if(!isShow)
                {
                    super.onAnimationEnd(animation);
                    myView.setVisibility(View.INVISIBLE);
                }
            }
        });

        // make the view visible and start the animation
        if(isShow)
            myView.setVisibility(View.VISIBLE);

        // start the animation
        anim.start();
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
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<String, String>();

                    headers.put("authToken", session.getAuthToken());

                    return headers;
                }
            };

            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(getContext()).addToRequestQueue(multipartRequest);
        } else {
            Toast.makeText(getContext(), R.string.check_net_connection, Toast.LENGTH_SHORT).show();
        }
    }

    private void getUserList() {
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                FirebaseData messageOutput = dataSnapshot.getValue(FirebaseData.class);
                userlistmap.put(dataSnapshot.getKey(),messageOutput);
                Collection<FirebaseData> demoValues = userlistmap.values();
                userDataList.addAll(demoValues);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                FirebaseData messageOutput = dataSnapshot.getValue(FirebaseData.class);
                userlistmap.put(dataSnapshot.getKey(),messageOutput);
                Collection<FirebaseData> demoValues = userlistmap.values();
                userDataList.addAll(demoValues);
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
}
