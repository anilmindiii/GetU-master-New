package getu.app.com.getu.user_side_package.acrivity;

import android.Manifest;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import getu.app.com.getu.R;
import getu.app.com.getu.app_session.Session;
import getu.app.com.getu.chat.fragment.UserChatFragment;
import getu.app.com.getu.common_activity.LoginActivity;
import getu.app.com.getu.common_activity.NetworkErrorHomeActivity;
import getu.app.com.getu.hepler.PermissionAll;
import getu.app.com.getu.user_side_package.fragment.UserHomeFragment;
import getu.app.com.getu.user_side_package.fragment.UserOnlineFragment;
import getu.app.com.getu.user_side_package.fragment.UserProfileFragment;
import getu.app.com.getu.user_side_package.fragment.UserSettingFragment;
import getu.app.com.getu.util.Constant;

import static getu.app.com.getu.util.Constant.MY_PERMISSIONS_REQUEST_LOCATION;

public class UserMainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView iv_for_home, iv_for_chatting, iv_for_online, iv_for_notification, iv_for_satting;
    private boolean doubleBackToExitPressedOnce = false;
    private Session session;
    private int clickID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);
        initView();

        PermissionAll permissionAll = new PermissionAll();
        permissionAll.checkLocationPermission(UserMainActivity.this);

        session = new Session(this);
        iv_for_home.setImageResource(R.drawable.active_home_icon);
        addFragment(UserHomeFragment.newInstance(""), false, R.id.framlayout);

        iv_for_home.setOnClickListener(this);
        iv_for_chatting.setOnClickListener(this);
        iv_for_online.setOnClickListener(this);
        iv_for_notification.setOnClickListener(this);
        iv_for_satting.setOnClickListener(this);
    }

    private void initView() {
        iv_for_home = findViewById(R.id.iv_for_home);
        iv_for_chatting = findViewById(R.id.iv_for_chatting);
        iv_for_online = findViewById(R.id.iv_for_online);
        iv_for_notification = findViewById(R.id.iv_for_notification);
        iv_for_satting = findViewById(R.id.iv_for_satting);
    }

    public void replaceFragment(Fragment fragment, boolean addToBackStack, int containerId) {
        String backStackName = fragment.getClass().getName();
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();

        int i = fm.getBackStackEntryCount();

        while (i > 0) {
            fm.popBackStackImmediate();
            i--;
        }
        boolean fragmentPopped = getFragmentManager().popBackStackImmediate(backStackName, 0);
        if (!fragmentPopped) {
            android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(containerId, fragment, backStackName).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            if (addToBackStack)
                transaction.addToBackStack(backStackName);
            transaction.commit();
        }
    }

    public void addFragment(Fragment fragment, boolean addToBackStack, int containerId) {
        String backStackName = fragment.getClass().getName();
        boolean fragmentPopped = getFragmentManager().popBackStackImmediate(backStackName, 0);
        if (!fragmentPopped) {
            android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
            transaction.add(containerId, fragment, backStackName); //.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            if (addToBackStack)
                transaction.addToBackStack(backStackName);
            transaction.commit();
        }
    }

    @Override
    public void onBackPressed() {

        if (session.getIsLogedIn()) {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
            } else {
                if (!doubleBackToExitPressedOnce) {
                    this.doubleBackToExitPressedOnce = true;
                    Toast.makeText(UserMainActivity.this, R.string.for_exit, Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            doubleBackToExitPressedOnce = false;
                        }
                    }, Constant.BackPressed_Exit);
                } else {
                    super.onBackPressed();
                }
            }
        }else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
            }else {
                finish();
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_for_home:
                if (clickID != 1) {
                    clickID = 1;
                    iv_for_home.setImageResource(R.drawable.active_home_icon);
                    iv_for_chatting.setImageResource(R.drawable.inactive_chat_ico);
                    iv_for_notification.setImageResource(R.drawable.inactive_profile_ico);
                    iv_for_online.setImageResource(R.drawable.inactive_heart_ico);
                    iv_for_satting.setImageResource(R.drawable.inactive_setting_screen);
                    replaceFragment(UserHomeFragment.newInstance(""), false, R.id.framlayout);
                }
                break;
            case R.id.iv_for_chatting:
                if (session.getIsLogedIn()){
                    if (clickID != 2) {
                    clickID = 2;
                    iv_for_home.setImageResource(R.drawable.inactive_home_icon);
                    iv_for_chatting.setImageResource(R.drawable.active_chat_ico);
                    iv_for_notification.setImageResource(R.drawable.inactive_profile_ico);
                    iv_for_online.setImageResource(R.drawable.inactive_heart_ico);
                    iv_for_satting.setImageResource(R.drawable.inactive_setting_screen);
                    replaceFragment(UserChatFragment.newInstance(""), false, R.id.framlayout);
                }
                }else {
                    guestUserDialog();
                }
                break;
            case R.id.iv_for_online:
                if (session.getIsLogedIn()) {
                    if (clickID != 3) {
                        clickID = 3;
                        iv_for_home.setImageResource(R.drawable.inactive_home_icon);
                        iv_for_chatting.setImageResource(R.drawable.inactive_chat_ico);
                        iv_for_notification.setImageResource(R.drawable.inactive_profile_ico);
                        iv_for_online.setImageResource(R.drawable.active_heart_ico);
                        iv_for_satting.setImageResource(R.drawable.inactive_setting_screen);
                        replaceFragment(UserOnlineFragment.newInstance(""), false, R.id.framlayout);
                    }
                }else {
                    guestUserDialog();
                }
                break;
            case R.id.iv_for_notification:
                if (session.getIsLogedIn()) {
                if (clickID != 4) {
                    clickID = 4;
                    iv_for_home.setImageResource(R.drawable.inactive_home_icon);
                    iv_for_chatting.setImageResource(R.drawable.inactive_chat_ico);
                    iv_for_notification.setImageResource(R.drawable.active_profile_ico);
                    iv_for_online.setImageResource(R.drawable.inactive_heart_ico);
                    iv_for_satting.setImageResource(R.drawable.inactive_setting_screen);
                    replaceFragment(UserProfileFragment.newInstance(""), false, R.id.framlayout);
                }}else {
                    guestUserDialog();
                }
                break;
            case R.id.iv_for_satting:
                if (session.getIsLogedIn()) {
                    if (clickID != 5) {
                        clickID = 5;
                        iv_for_home.setImageResource(R.drawable.inactive_home_icon);
                        iv_for_chatting.setImageResource(R.drawable.inactive_chat_ico);
                        iv_for_notification.setImageResource(R.drawable.inactive_profile_ico);
                        iv_for_online.setImageResource(R.drawable.inactive_heart_ico);
                        iv_for_satting.setImageResource(R.drawable.active_setting_screen);
                        replaceFragment(UserSettingFragment.newInstance(""), false, R.id.framlayout);
                    }}else {
                    guestUserDialog();
                }
                break;
        }
    }

    private void guestUserDialog() {
        final Dialog dialog = new Dialog(this);
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
                Intent intent = new Intent(UserMainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    BroadcastReceiver netSwitchReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isConnectionAvailable = intent.getExtras().getBoolean("is_connected");
            if (!isConnectionAvailable) {
                if (NetworkErrorHomeActivity.isOptedToOffline()) {

                } else {
                    Intent intent1 = new Intent(UserMainActivity.this,NetworkErrorHomeActivity.class);
                    startActivity(intent1);
                }
            } else {
                NetworkErrorHomeActivity.setOptedToOffline(false);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        try {
            registerReceiver(netSwitchReceiver, new IntentFilter(Constant.NETWORK_SWITCH_FILTER));
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {

            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        //success permission granted & call Location method
                    }
                } else {
                    // permission denied, boo! Disable the
                    Toast.makeText(UserMainActivity.this, "deny Location Permission", Toast.LENGTH_SHORT).show();
                }
            }
            break;

        }
    }
}
