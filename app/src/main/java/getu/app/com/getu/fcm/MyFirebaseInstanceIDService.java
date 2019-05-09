package getu.app.com.getu.fcm;

import android.annotation.SuppressLint;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by abc on 15/11/2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseInstanceIdService";

    @SuppressLint("LongLogTag")
    @Override
    public void onTokenRefresh() {

        String refreshDiviceToken = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "onTokenRefresh: "+refreshDiviceToken );

    }
}
