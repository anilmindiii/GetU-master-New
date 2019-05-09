package getu.app.com.getu.common_activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import getu.app.com.getu.R;
import getu.app.com.getu.app_session.Session;
import getu.app.com.getu.util.Constant;

public class NetworkErrorHomeActivity extends AppCompatActivity {
    private static boolean optedToOffline = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_error_home);
        Constant.HOME_RESUME = 1;
        Session session = new Session(NetworkErrorHomeActivity.this);
        TextView tv_for_tittle = findViewById(R.id.tv_for_tittle);
        tv_for_tittle.setText("Network Error");

        LinearLayout layout_for_tapbarUser = findViewById(R.id.layout_for_tapbarUser);
        LinearLayout layout_for_tapbarFree = findViewById(R.id.layout_for_tapbarFree);
        if (session.getUserType().equals("0")){
            layout_for_tapbarUser.setVisibility(View.VISIBLE);
            layout_for_tapbarFree.setVisibility(View.GONE);
        }else {
            layout_for_tapbarUser.setVisibility(View.GONE);
            layout_for_tapbarFree.setVisibility(View.VISIBLE);
        }
    }

    public static boolean isOptedToOffline() {
        return optedToOffline;
    }

    public static void setOptedToOffline(boolean optedToOffline){
        NetworkErrorHomeActivity.optedToOffline = optedToOffline;
    }

    BroadcastReceiver netSwitchReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isConnectionAvailable =  intent.getExtras().getBoolean("is_connected");
            if (isConnectionAvailable) {
                optedToOffline = false;

                finish();
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
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent intent = new Intent(NetworkErrorHomeActivity.this,NetworkErrorHomeActivity.class);
        startActivity(intent);
    }
}
