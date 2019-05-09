package getu.app.com.getu.chat.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ServerValue;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import getu.app.com.getu.R;
import getu.app.com.getu.app_session.Session;
import getu.app.com.getu.chat.adapter.ChatAdapter;
import getu.app.com.getu.chat.model.BlockUsers;
import getu.app.com.getu.chat.model.Chatting;
import getu.app.com.getu.fcm.FcmNotificationBuilder;
import getu.app.com.getu.fcm.MyFirebaseMessagingService;
import getu.app.com.getu.freelancer_side.activity.FreelancerActivity;
import getu.app.com.getu.hepler.ImageRotator;
import getu.app.com.getu.user_side_package.acrivity.UserMainActivity;
import getu.app.com.getu.util.Constant;

import static getu.app.com.getu.hepler.ImagePicker.decodeBitmap;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TEMP_IMAGE_NAME = "tempImage.jpg";
    private ImageView iv_for_send, iv_for_plus, iv_for_image, iv_for_delete, iv_for_block;
    private EditText et_for_sendTxt;
    private TextView tv_for_noChat;
    private RecyclerView recycler_view;
    private Session session;
    private ArrayList<Chatting> chattings;
    private ChatAdapter chatAdapter;
    private String fullname, uID, chatNode, profileImage, OtherFirebaseToken, blockBy = "", noticiationStaus;
    private DatabaseReference chatRef;
    private Uri imageUri, photoURI;
    private DatabaseReference databaseReference;
    private FirebaseStorage storage;
    private ArrayList<String> keys;
    private int notification = 0;
    private String s = "1";

    private boolean isCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        MyFirebaseMessagingService.CHAT_HISTORY = "1";
        storage = FirebaseStorage.getInstance();
        keys = new ArrayList<>();
        session = new Session(this);

        uID = getIntent().getStringExtra("USER_ID");
        fullname = getIntent().getStringExtra("FULLNAME");
        profileImage = getIntent().getStringExtra("PROFILE_PIC");

        notification = 0;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getString("uid") != null) {
                notification = 1;
                uID = bundle.getString("uid");
                fullname = bundle.getString("title");
                profileImage = bundle.getString("profilepic");
            }
        }

        if (uID != null) {
            if (Integer.parseInt(uID) < Integer.parseInt(session.getUserID())) {
                chatNode = session.getUserID() + "_" + uID;
            } else {
                chatNode = uID + "_" + session.getUserID();
            }
        }

        chatRef = FirebaseDatabase.getInstance().getReference().child("chatting/" + chatNode);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("BlockUsers/" + chatNode);

        toolbar();
        initView();

        iv_for_send.setOnClickListener(this);
        iv_for_plus.setOnClickListener(this);
        iv_for_delete.setOnClickListener(this);
        iv_for_block.setOnClickListener(this);

        chattings = new ArrayList<Chatting>();
        chatAdapter = new ChatAdapter(chattings, this);
        recycler_view.setAdapter(chatAdapter);

        getBlockList();
        getMessageList();

        if (imageUri == null) {
            iv_for_image.setVisibility(View.GONE);
            et_for_sendTxt.setVisibility(View.VISIBLE);
        } else {
            iv_for_image.setVisibility(View.VISIBLE);
            et_for_sendTxt.setVisibility(View.GONE);
        }

        if (keys.size() < 0) {
            iv_for_delete.setClickable(true);
        } else {
            iv_for_delete.setClickable(false);
        }

        FirebaseDatabase.getInstance().getReference().child("users").child(uID).child("firebaseToken").addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    OtherFirebaseToken = dataSnapshot.getValue().toString();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("users").child(uID).child("notificationStatus").addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (
                        dataSnapshot.getValue() != null) {
                    noticiationStaus = dataSnapshot.getValue().toString();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getMessageList() {
        chattings.clear();
        chatRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Chatting messageOutput = dataSnapshot.getValue(Chatting.class);
                Log.e("Chatting", "User: " + messageOutput.uid + " message: " + messageOutput.message);

                if (messageOutput.deleteby.equals("") || messageOutput.deleteby.equals(uID)) {
                    chattings.add(messageOutput);
                    tv_for_noChat.setVisibility(View.GONE);
                    keys.add(dataSnapshot.getKey());
                    iv_for_delete.setClickable(true);
                }
                recycler_view.scrollToPosition(chattings.size() - 1);
                chatAdapter.notifyDataSetChanged();
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                recycler_view.scrollToPosition(chattings.size() - 1);
                // keys.add(dataSnapshot.getKey());
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

    private void initView() {
        iv_for_send = findViewById(R.id.iv_for_send);
        et_for_sendTxt = findViewById(R.id.et_for_sendTxt);
        recycler_view = findViewById(R.id.recycler_view);
        iv_for_plus = findViewById(R.id.iv_for_plus);
        iv_for_image = findViewById(R.id.iv_for_image);
        tv_for_noChat = findViewById(R.id.tv_for_noChat);
    }

    private void toolbar() {
        ImageView iv_for_back = findViewById(R.id.iv_for_back);
        iv_for_delete = findViewById(R.id.iv_for_delete);
        iv_for_block = findViewById(R.id.iv_for_block);
        TextView tv_for_tittle = findViewById(R.id.tv_for_tittle);

        iv_for_back.setVisibility(View.VISIBLE);
        iv_for_delete.setVisibility(View.VISIBLE);
        iv_for_block.setVisibility(View.VISIBLE);
        String firstName = fullname.replaceAll(" .*", "");
        tv_for_tittle.setText(firstName);

        iv_for_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (notification != 1) {
            finish();
        } else {
            if (session.getUserType().equals("1")) {
                Intent intent = new Intent(ChatActivity.this, FreelancerActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(ChatActivity.this, UserMainActivity.class);
                startActivity(intent);
            }
        }
        MyFirebaseMessagingService.CHAT_HISTORY = "";
    }

    private void writeToDBProfiles(Chatting chatModel, Chatting chatModel2, String myId) {

        chatRef.push().setValue(chatModel2);
        //chatRef.child(ServerValue.TIMESTAMP.toString()).setValue(chatModel);
        FirebaseDatabase.getInstance().getReference().child("history").child(myId).child(uID).setValue(chatModel);
        FirebaseDatabase.getInstance().getReference().child("history").child(uID).child(myId).setValue(chatModel2);

        if (noticiationStaus != null && !noticiationStaus.equals("")) {
            if (noticiationStaus.equals("1")) {
                String fToken = FirebaseInstanceId.getInstance().getToken();
                String message;
                if (chatModel.message.contains("firebasestorage.googleapis.com/v0/b/getu")) {
                    message = "Image";
                } else {
                    message = chatModel.message;
                }
                sendPushNotificationToReceiver(session.getFullName(), message, session.getUserID(), fToken, OtherFirebaseToken, session.getProfilePic());
            }
        }
    }

    private void userImageClick() {
        final Dialog dialog = new Dialog(ChatActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.take_picture);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        LinearLayout layout_for_camera = dialog.findViewById(R.id.layout_for_camera);
        LinearLayout layout_for_gallery = dialog.findViewById(R.id.layout_for_gallery);
        EnableRuntimePermission();

        layout_for_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //dispatchTakePictureIntent(dialog);
                picImage(dialog);
            }
        });
        layout_for_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 1);
                isCamera = false;
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void dispatchTakePictureIntent(Dialog dialog) {
        Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;

            photoFile = new File(this.getExternalCacheDir(), "cameraImage");
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        this.getApplicationContext().getPackageName() + ".fileprovider",
                        getTemporalFile(this));
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                takePictureIntent.putExtra("return-data", true);

                startActivityForResult(takePictureIntent, 7);
                dialog.dismiss();
            }
        }
    }



    /*Dharmraj acharya */
    private void picImage(Dialog dialog){
        if (!appManifestContainsPermission(this, Manifest.permission.CAMERA) || hasCameraAccess(this)) {
            Intent takePhotoIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            takePhotoIntent.putExtra("return-data", true);
            Uri uri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName()
                    + ".fileprovider", getTemporalFile(this));
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            // takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTemporalFile(context)));
            startActivityForResult(takePhotoIntent, 7);
            isCamera = true;
            dialog.dismiss();
        }
    }

    private static boolean hasCameraAccess(Context context) {
        return ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private static boolean appManifestContainsPermission(Context context, String permission) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] requestedPermissions = null;
            if (packageInfo != null) {
                requestedPermissions = packageInfo.requestedPermissions;
            }
            if (requestedPermissions == null) {
                return false;
            }

            if (requestedPermissions.length > 0) {
                List<String> requestedPermissionsList = Arrays.asList(requestedPermissions);
                return requestedPermissionsList.contains(permission);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static File getTemporalFile(Context context) {
        return new File(context.getExternalCacheDir(), TEMP_IMAGE_NAME);
    }


    private static final String TAG = ChatActivity.class.getSimpleName();
    public static final int PICK_IMAGE_REQUEST_CODE = 234; // the number doesn't matter
    private static final int DEFAULT_MIN_WIDTH_QUALITY = 400;        // min pixels
    private static final int DEFAULT_MIN_HEIGHT_QUALITY = 400;
    private static int minWidthQuality = DEFAULT_MIN_WIDTH_QUALITY;
    private static int minHeightQuality = DEFAULT_MIN_HEIGHT_QUALITY;


    /**
     * Resize to avoid using too much memory loading big images (e.g.: 2560*1920)
     **/
    public static Bitmap getImageResized(Context context, Uri selectedImage) {
        Bitmap bm;
        int[] sampleSizes = new int[]{5, 3, 2, 1};
        int i = 0;
        do {
            bm = decodeBitmap(context, selectedImage, sampleSizes[i]);
            i++;
        } while (bm != null
                && (bm.getWidth() < minWidthQuality || bm.getHeight() < minHeightQuality)
                && i < sampleSizes.length);
        Log.i(TAG, "Final bitmap width = " + (bm != null ? bm.getWidth() : "No final bitmap"));
        return bm;
    }

//Uri code

    public void EnableRuntimePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            Toast.makeText(this, "CAMERA permission allows us to Access CAMERA app", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA}, Constant.RequestPermissionCode);
        }
    } // camera parmission

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            imageUri = data.getData();
            iv_for_image.setImageURI(imageUri);
        } else {
            if (requestCode == 7 && resultCode == RESULT_OK) {

                Bitmap bm = null;
                File imageFile = getTemporalFile(this);
                photoURI = Uri.fromFile(imageFile);

                bm = getImageResized(this, photoURI);
                int rotation = ImageRotator.getRotation(this, photoURI, isCamera);
                bm = ImageRotator.rotate(bm, rotation);


                //File file = new File(ChatActivity.this.getExternalCacheDir(), s+".jpg");
                File file = new File(ChatActivity.this.getExternalCacheDir(), UUID.randomUUID()+".jpg");
                imageUri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName()
                        + ".fileprovider", file);
                String j = s+1;
                s=j;

                if(file!=null){
                    try {
                        OutputStream outStream = null;
                        outStream = new FileOutputStream(file);
                        bm.compress(Bitmap.CompressFormat.PNG, 80, outStream);
                        outStream.flush();
                        outStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (imageUri != null) {
                    iv_for_image.setVisibility(View.VISIBLE);
                    et_for_sendTxt.setVisibility(View.GONE);
                }
                iv_for_image.setImageURI(imageUri);
            }
        }
    } // onActivityResult

    private void uploadImage() {

        if (imageUri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference storageReference = storage.getReference();

            StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
            ref.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();

                            Uri fireBaseUri = taskSnapshot.getDownloadUrl();
                            //Toast.makeText(ChatActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();

                            Chatting chatModel = new Chatting();
                            chatModel.message = fireBaseUri.toString();

                            chatModel.timeStamp = ServerValue.TIMESTAMP;
                            chatModel.uid = uID;
                            chatModel.firebaseId = FirebaseAuth.getInstance().getUid();
                            chatModel.deviceToken = FirebaseInstanceId.getInstance().getToken();
                            chatModel.name = fullname;
                            chatModel.profilePic = profileImage;
                            chatModel.deleteby = "";

                            Chatting chatModel2 = new Chatting();
                            chatModel2.message = fireBaseUri.toString();

                            chatModel2.timeStamp = ServerValue.TIMESTAMP;
                            chatModel2.uid = session.getUserID();
                            chatModel2.firebaseId = FirebaseAuth.getInstance().getUid();
                            chatModel2.deviceToken = FirebaseInstanceId.getInstance().getToken();
                            chatModel2.name = session.getuserName();
                            chatModel2.profilePic = session.getProfilePic();
                            chatModel2.deleteby = "";

                            writeToDBProfiles(chatModel, chatModel2, session.getUserID());

                            iv_for_image.setVisibility(View.GONE);
                            et_for_sendTxt.setVisibility(View.VISIBLE);

                            imageUri = null;
                            photoURI = null;
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Log.e("TAG", "onFailure: " + e.getMessage());
                            Toast.makeText(ChatActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }

    private void sendPushNotificationToReceiver(String name, String message, String userID, String token, String otherFirebaseToken, String profileImage) {
        FcmNotificationBuilder.initialize().title(name)
                .message(message).clickaction("ChatActivity")
                .firebaseToken(token)
                .receiverFirebaseToken(otherFirebaseToken)
                .uid(userID).profilePic(profileImage).chatNode(chatNode)
                .send();
    }

    private void blockUserDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_chatblock);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Button btn_for_ok = dialog.findViewById(R.id.btn_for_ok);
        ImageView iv_for_cansel = dialog.findViewById(R.id.iv_for_cansel);
        final TextView tv_for_text = dialog.findViewById(R.id.tv_for_text);

        final BlockUsers blockUsers = new BlockUsers();
        if (blockBy.equals("")) {
            blockUsers.blockedBy = session.getUserID();
            tv_for_text.setText("Do you want to block this user");
        } else if (blockBy.equals("Both")) {
            blockUsers.blockedBy = uID;
            tv_for_text.setText("Do you want to unblock this user");
        } else if (blockBy.equals(session.getUserID())) {
            blockUsers.blockedBy = "";
            tv_for_text.setText("Do you want to unblock this user");
        } else if (blockBy.equals(uID)) {
            blockUsers.blockedBy = "Both";
            tv_for_text.setText("Do you want to block this user");
        }

        iv_for_cansel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn_for_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.setValue(blockUsers);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void chatDeleteDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_chatblock);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Button btn_for_ok = dialog.findViewById(R.id.btn_for_ok);
        ImageView iv_for_cansel = dialog.findViewById(R.id.iv_for_cansel);
        TextView tv_for_text = dialog.findViewById(R.id.tv_for_text);
        tv_for_text.setText("Do you want to delete chat");

        iv_for_cansel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn_for_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                deteleMsg();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    void deteleMsg() {

        if (keys != null && keys.size() != 0) {
            for (int i = 0; i < keys.size(); i++) {
                if (!chattings.get(i).deleteby.equals(session.getUserID())) {
                    if (chattings.get(i).deleteby.equals("")) {
                        chatRef.child(keys.get(i)).child("deleteby").setValue(session.getUserID());
                    } else {
                        chatRef.child(keys.get(i)).child("deleteby").setValue("Both");
                    }
                    iv_for_delete.setClickable(false);
                }
            }
            keys.clear();
        }
        FirebaseDatabase.getInstance().getReference().child("history").child(session.getUserID()).child(uID).child("deleteby").setValue(session.getUserID());

        chattings.clear();
        chatAdapter.notifyDataSetChanged();

    }

    private void getBlockList() {
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                blockBy = dataSnapshot.getValue(String.class);

                if (blockBy.equals("")) {
                    iv_for_block.setImageResource(R.drawable.block_ico);
                } else if (blockBy.equals(session.getUserID())) {
                    iv_for_block.setImageResource(R.drawable.new_block_ico);
                } else if (blockBy.equals("Both")) {
                    iv_for_block.setImageResource(R.drawable.new_block_ico);
                } else if (blockBy.equals(uID)) {
                    iv_for_block.setImageResource(R.drawable.block_ico);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                blockBy = dataSnapshot.getValue(String.class);

                if (blockBy.equals("")) {
                    iv_for_block.setImageResource(R.drawable.block_ico);
                } else if (blockBy.equals(session.getUserID())) {
                    iv_for_block.setImageResource(R.drawable.new_block_ico);
                } else if (blockBy.equals("Both")) {
                    iv_for_block.setImageResource(R.drawable.new_block_ico);
                } else if (blockBy.equals(uID)) {
                    iv_for_block.setImageResource(R.drawable.block_ico);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.iv_for_send:
                if (imageUri != null) {
                    iv_for_image.setVisibility(View.VISIBLE);
                    et_for_sendTxt.setVisibility(View.GONE);

                    if (blockBy.equals("")) {
                        uploadImage();
                        imageUri = null;
                        photoURI = null;
                    } else if (blockBy.equals(session.getUserID())) {
                        Toast.makeText(this, "You blocked " + fullname + ". " + "Can't send any message", Toast.LENGTH_SHORT).show();
                    } else if (!blockBy.equals("")) {
                        Toast.makeText(this, "You are blocked by " + fullname + ". " + "Can't send any message.", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    if (iv_for_image == null) {
                        et_for_sendTxt.setVisibility(View.VISIBLE);
                    }
                    String txt = et_for_sendTxt.getText().toString().trim();
                    if (!txt.equals("")) {

                        Chatting chatModel = new Chatting();
                        chatModel.message = txt;
                        chatModel.timeStamp = ServerValue.TIMESTAMP;
                        chatModel.uid = uID;
                        chatModel.firebaseId = FirebaseAuth.getInstance().getUid();
                        chatModel.deviceToken = FirebaseInstanceId.getInstance().getToken();
                        chatModel.name = fullname;
                        chatModel.profilePic = profileImage;
                        chatModel.deleteby = "";

                        Chatting chatModel2 = new Chatting();
                        chatModel2.message = txt;
                        chatModel2.timeStamp = ServerValue.TIMESTAMP;
                        chatModel2.uid = session.getUserID();
                        chatModel2.firebaseId = FirebaseAuth.getInstance().getUid();
                        chatModel2.deviceToken = FirebaseInstanceId.getInstance().getToken();
                        chatModel2.name = session.getFullName();
                        chatModel2.profilePic = session.getProfilePic();
                        chatModel2.deleteby = "";

                        if (blockBy.equals("")) {
                            writeToDBProfiles(chatModel, chatModel2, session.getUserID());
                            et_for_sendTxt.setText("");
                        } else if (blockBy.equals(session.getUserID())) {
                            Toast.makeText(this, "You blocked " + fullname + ". " + "Can't send any message", Toast.LENGTH_SHORT).show();
                        } else if (!blockBy.equals("")) {
                            Toast.makeText(this, "You are blocked by " + session.getFullName() + ". " + "Can't send any message.", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(this, "Please enter text", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.iv_for_plus:
                if (blockBy.equals("")) {
                    imageUri = null;
                    photoURI = null;
                    userImageClick();
                } else if (blockBy.equals(session.getUserID())) {
                    Toast.makeText(this, "You blocked " + fullname + ". " + "Can't send any image", Toast.LENGTH_SHORT).show();
                } else if (!blockBy.equals("")) {
                    Toast.makeText(this, "You are blocked by " + session.getFullName() + ". " + "Can't send any image.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.iv_for_delete:
                chatDeleteDialog();
                break;
            case R.id.iv_for_block:
                blockUserDialog();
                break;
        }
    }

}
