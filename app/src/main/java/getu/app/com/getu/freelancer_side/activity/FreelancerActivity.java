package getu.app.com.getu.freelancer_side.activity;

import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import getu.app.com.getu.R;
import getu.app.com.getu.chat.fragment.ChatFragment;
import getu.app.com.getu.common_activity.NetworkErrorHomeActivity;
import getu.app.com.getu.freelancer_side.fragments.HomeFragment;
import getu.app.com.getu.freelancer_side.fragments.NotificationFragment;
import getu.app.com.getu.freelancer_side.fragments.ProfileFragment;
import getu.app.com.getu.freelancer_side.fragments.SettingFragment;
import getu.app.com.getu.util.Constant;


public class FreelancerActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView iv_for_home, iv_for_chatting,iv_for_notification,iv_for_profile,iv_for_satting;
    private  boolean doubleBackToExitPressedOnce = false;
    private int clickID = 1 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freelancer);
        initView();

        iv_for_home.setImageResource(R.drawable.active_home_icon);
        addFragment(HomeFragment.newInstance(""), false, R.id.framlayout);

        iv_for_home.setOnClickListener(this);
        iv_for_chatting.setOnClickListener(this);
        iv_for_notification.setOnClickListener(this);
        iv_for_profile.setOnClickListener(this);
        iv_for_satting.setOnClickListener(this);
    }

    private void initView() {
        iv_for_home = findViewById(R.id.iv_for_home);
        iv_for_chatting = findViewById(R.id.iv_for_chatting);
        iv_for_notification = findViewById(R.id.iv_for_notification);
        iv_for_profile = findViewById(R.id.iv_for_profile);
        iv_for_satting = findViewById(R.id.iv_for_satting);
    }

    public void replaceFragment(Fragment fragment, boolean addToBackStack, int containerId) {
        String backStackName = fragment.getClass().getName();
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();

        int i = fm.getBackStackEntryCount();

        while(i>0){
            fm.popBackStackImmediate();
            i--;
        }
        boolean fragmentPopped = getFragmentManager().popBackStackImmediate(backStackName, 0);
        if (!fragmentPopped)
        {
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
            transaction.setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left,R.anim.enter_from_left,R.anim.exit_to_right);
            transaction.add(containerId, fragment, backStackName); //.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            if (addToBackStack)
                transaction.addToBackStack(backStackName);
            transaction.commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            if (!doubleBackToExitPressedOnce) {
                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(FreelancerActivity.this, R.string.for_exit, Toast.LENGTH_SHORT).show();

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
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_for_home:
                if (clickID != 1) {
                    clickID = 1;
                    iv_for_home.setImageResource(R.drawable.active_home_icon);
                    iv_for_chatting.setImageResource(R.drawable.inactive_chat_ico);
                    iv_for_notification.setImageResource(R.drawable.inactive_notifications_ico);
                    iv_for_profile.setImageResource(R.drawable.inactive_profile_ico);
                    iv_for_satting.setImageResource(R.drawable.inactive_setting_screen);
                    replaceFragment(HomeFragment.newInstance(""), false, R.id.framlayout);
                }
                break;
            case R.id.iv_for_chatting:
                if (clickID != 2) {
                    clickID = 2;
                    iv_for_home.setImageResource(R.drawable.inactive_home_icon);
                    iv_for_chatting.setImageResource(R.drawable.active_chat_ico);
                    iv_for_notification.setImageResource(R.drawable.inactive_notifications_ico);
                    iv_for_profile.setImageResource(R.drawable.inactive_profile_ico);
                    iv_for_satting.setImageResource(R.drawable.inactive_setting_screen);
                    replaceFragment(ChatFragment.newInstance(""), false, R.id.framlayout);
                }
                break;
            case R.id.iv_for_notification:
                if (clickID != 3) {
                    clickID = 3;
                    iv_for_home.setImageResource(R.drawable.inactive_home_icon);
                    iv_for_chatting.setImageResource(R.drawable.inactive_chat_ico);
                    iv_for_notification.setImageResource(R.drawable.active_notifications_ico);
                    iv_for_profile.setImageResource(R.drawable.inactive_profile_ico);
                    iv_for_satting.setImageResource(R.drawable.inactive_setting_screen);
                    replaceFragment(NotificationFragment.newInstance(""), false, R.id.framlayout);
                }
                break;
            case R.id.iv_for_profile:
                if (clickID != 4) {
                    clickID = 4;
                    iv_for_home.setImageResource(R.drawable.inactive_home_icon);
                    iv_for_chatting.setImageResource(R.drawable.inactive_chat_ico);
                    iv_for_notification.setImageResource(R.drawable.inactive_notifications_ico);
                    iv_for_profile.setImageResource(R.drawable.active_profile_ico);
                    iv_for_satting.setImageResource(R.drawable.inactive_setting_screen);
                    replaceFragment(ProfileFragment.newInstance(""), false, R.id.framlayout);
                }
                break;
            case R.id.iv_for_satting:
                if (clickID != 5) {
                    clickID = 5;
                    iv_for_home.setImageResource(R.drawable.inactive_home_icon);
                    iv_for_chatting.setImageResource(R.drawable.inactive_chat_ico);
                    iv_for_notification.setImageResource(R.drawable.inactive_notifications_ico);
                    iv_for_profile.setImageResource(R.drawable.inactive_profile_ico);
                    iv_for_satting.setImageResource(R.drawable.active_setting_screen);
                    replaceFragment(SettingFragment.newInstance(""), false, R.id.framlayout);
                }
                break;
        }
    }

    BroadcastReceiver netSwitchReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isConnectionAvailable = intent.getExtras().getBoolean("is_connected");
            if (!isConnectionAvailable) {
                if (NetworkErrorHomeActivity.isOptedToOffline()) {

                } else {
                    Intent intent1 = new Intent(FreelancerActivity.this,NetworkErrorHomeActivity.class);
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
            //getCategoryFullList();
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

}
