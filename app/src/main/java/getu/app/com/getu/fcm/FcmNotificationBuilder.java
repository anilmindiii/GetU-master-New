package getu.app.com.getu.fcm;

import android.app.Notification;
import android.telecom.Call;
import android.util.Log;

import com.facebook.share.model.ShareMessengerMediaTemplateContent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by abc on 12/01/2018.
 */

public class FcmNotificationBuilder {
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String TAG = "FcmNotificationBuilder";
    private static final String SERVER_API_KEY = "AAAAG0RQhPQ:APA91bF6hzD66RZKsdhgw1y60j9y2yLvBioa2hSjz9sdGOoEOLBnFQryVSTgaxrCqC4Mz1vCG0bZl4oJlKkwSrtIUDsQcaU019hvrg4IYTlid6D1JOHdswRZcMP3K_tMZ3rIsp1ovTWM";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";
    private static final String AUTHORIZATION = "Authorization";
    private static final String AUTH_KEY = "key=" + SERVER_API_KEY;
    private static final String FCM_URL = "https://fcm.googleapis.com/fcm/send";
    // json related keys
    private static final String KEY_TO = "to";
    private static final String KEY_PROFILE_PIC = "profilepic";
    private static final String KEY_NOTIFICATION = "notification";
    private static final String KEY_TITLE = "title";
    private static final String KEY_CLICKACTION = "click_action";
    private static final String KEY_TYPE = "type";
    private static final String KEY_TEXT = "body";
    private static final String KEY_DATA = "data";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_CHATNODE = "reference_id";
    private static final String KEY_UID = "uid";
    private static final String KEY_FCM_TOKEN = "fcm_token";

    private String mTitle;
    private String mClickAction;
    private String mMessage;
    private String mUsername;
    private String mChatNode;
    private String mUid;
    private String mFirebaseToken;
    private String mReceiverFirebaseToken;
    private String mProfilePic;

    public FcmNotificationBuilder() {

    }

    public static FcmNotificationBuilder initialize() {
        return new FcmNotificationBuilder();
    }

    public FcmNotificationBuilder title(String title) {
        mTitle = title;
        return this;
    }

    public FcmNotificationBuilder clickaction(String clickaction) {
        mClickAction = clickaction;
        return this;
    }

    public FcmNotificationBuilder message(String message) {
        mMessage = message;
        return this;
    }

    public FcmNotificationBuilder username(String username) {
        mUsername = username;
        return this;
    }

    public FcmNotificationBuilder chatNode(String chantNode) {
        mChatNode = chantNode;
        return this;
    }

    public FcmNotificationBuilder uid(String uid) {
        mUid = uid;
        return this;
    }

    public FcmNotificationBuilder firebaseToken(String firebaseToken) {
        mFirebaseToken = firebaseToken;
        return this;
    }

    public FcmNotificationBuilder receiverFirebaseToken(String receiverFirebaseToken) {
        mReceiverFirebaseToken = receiverFirebaseToken;
        return this;
    }

    public FcmNotificationBuilder profilePic(String profilePic) {
        mProfilePic = profilePic;
        return this;
    }

    public void send() {
        RequestBody requestBody = null;
        try {
            requestBody = RequestBody.create(MEDIA_TYPE_JSON, getValidJsonBody().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new Request.Builder()
                .addHeader(CONTENT_TYPE, APPLICATION_JSON)
                .addHeader(AUTHORIZATION, AUTH_KEY)
                .url(FCM_URL)
                .post(requestBody)
                .build();

        okhttp3.Call call = new OkHttpClient().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.e(TAG, "onGetAllUsersFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                Log.e(TAG, "onResponse: " + response.body().string());
            }
        });
    }

    private JSONObject getValidJsonBody() throws JSONException {
        JSONObject jsonObjectBody = new JSONObject();

        jsonObjectBody.put(KEY_TO, mReceiverFirebaseToken);
        JSONObject jsonObjectData = new JSONObject();
        jsonObjectData.put(KEY_TITLE, mTitle);
        jsonObjectData.put(KEY_CLICKACTION, mClickAction);
        jsonObjectData.put(KEY_TEXT, mMessage);
        jsonObjectData.put(KEY_USERNAME, mUsername);
        jsonObjectData.put(KEY_PROFILE_PIC, mProfilePic);
        jsonObjectData.put(KEY_UID, mUid);
        jsonObjectData.put(KEY_TYPE, "chat");
        jsonObjectData.put(KEY_FCM_TOKEN, mFirebaseToken);
        jsonObjectData.put("other_key", "true");
        jsonObjectBody.put(KEY_DATA, jsonObjectData);

        JSONObject object = new JSONObject();
        object.put(KEY_UID, mUid);
        object.put(KEY_TITLE, mTitle);
        object.put(KEY_TEXT, mMessage);
        object.put("type","chat");
        object.put("click_action","ChatActivity");
        object.put("sound", "default");
        object.put("icon", "icon");
        object.put("badge", "1");
        object.put(KEY_CHATNODE, mChatNode);
        object.put(KEY_PROFILE_PIC, mProfilePic);
        jsonObjectBody.put(KEY_NOTIFICATION, object);

        Log.e(TAG, "getValidJsonBody: "+jsonObjectBody );
        return jsonObjectBody;
    }
}


