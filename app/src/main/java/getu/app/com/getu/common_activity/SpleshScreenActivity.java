package getu.app.com.getu.common_activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import getu.app.com.getu.app_session.Session;
import getu.app.com.getu.freelancer_side.activity.FreelancerActivity;
import getu.app.com.getu.user_side_package.acrivity.UserMainActivity;
import io.fabric.sdk.android.Fabric;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import getu.app.com.getu.R;
import getu.app.com.getu.util.Constant;

public class SpleshScreenActivity extends AppCompatActivity {
    //private String TAG = "KEYHASHKEY";
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        /*requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

       // printHashKey(this);

        session = new Session(this);
        setContentView(R.layout.activity_splesh_screen);
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                if (!session.getIsLogedIn()) {
                    Intent mainIntent = new Intent(SpleshScreenActivity.this, WelcomeActivity.class);
                    SpleshScreenActivity.this.startActivity(mainIntent);
                    SpleshScreenActivity.this.finish();
                }else {
                    String userType = session.getUserType();
                    if (session.getUserType().equals("0")) {
                        Intent intent = new Intent(SpleshScreenActivity.this, UserMainActivity.class);
                        startActivity(intent);
                        SpleshScreenActivity.this.finish();
                    }else {
                        Intent intent = new Intent(SpleshScreenActivity.this, FreelancerActivity.class);
                        startActivity(intent);
                        SpleshScreenActivity.this.finish();
                    }
                }
                overridePendingTransition(R.anim.anim_left_to_right, R.anim.anim_right_to_left);
            }
        }, Constant.SPLESH_TIME);


        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo("getu.app.com.getu", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                Log.e("hash key", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
    }


   /* public void printHashKey(Context pContext) {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i(TAG, "printHashKey() Hash Key: " + hashKey);
   //             ucUMLYQ6lgbdRBu1blzaUzJ0mfA=

            }
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "printHashKey()", e);
        } catch (Exception e) {
            Log.e(TAG, "printHashKey()", e);
        }
    }*/
}
