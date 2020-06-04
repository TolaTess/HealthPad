package com.tolaotesanya.healthpad.activities.chat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.tolaotesanya.healthpad.R;
import com.tolaotesanya.healthpad.dependencies.DependencyInjection;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";

    private static final int GALLERY_PICK = 1;

    private Context mContext = ChatActivity.this;
    private ChatActivity mMainView = ChatActivity.this;
    private ChatActivityPresenter chatPresenter;
    private ImageView mUploadImage;

    private String mChatReceiverUser;
    private String mCurrentUserId;

    private RecyclerView mMessageRecyclerView;
    private SwipeRefreshLayout mRefreshLayout;
    private LinearLayoutManager mLinearLayout;
    private EditText mChatMessageView;
    private TextView mLastSeen;
    private CircleImageView mProfileImage;
    private TextView mNameView;
    private ImageView mChatSendBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        setupToolbar();
        attachUI();

        Bundle bundle = getIntent().getExtras();
        DependencyInjection.shared.inject(this, bundle);

    }

    public void configureWith(ChatActivityPresenter chatActivityPresenter) {
        this.chatPresenter = chatActivityPresenter;

        chatPresenter.checkLastSeenOnline(mNameView, mLastSeen, mProfileImage);
        setupUI();

    }

    private void attachUI() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.chat_custom_bar, null);
        this.getSupportActionBar().setCustomView(action_bar_view);

        mUploadImage = findViewById(R.id.upload_image_chat);
        mNameView = findViewById(R.id.custom_bar_name);
        mChatSendBtn = findViewById(R.id.chat_msg_send);
        mChatMessageView = findViewById(R.id.chat_message_input);
        mLastSeen = findViewById(R.id.last_seen);
        mProfileImage = findViewById(R.id.custom_bar_image);
        mMessageRecyclerView = findViewById(R.id.messages_list);
        mLinearLayout = new LinearLayoutManager(this);
        mMessageRecyclerView.setLayoutManager(mLinearLayout);
        mRefreshLayout = findViewById(R.id.swipe_message_layout);
    }

    private void setupUI() {
        chatPresenter.createChatDatabase();
        chatPresenter.setupUI(mChatSendBtn, mChatMessageView, mMessageRecyclerView, mLinearLayout, mRefreshLayout);
        mUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //option 1
                // this will direct user to the Gallery chooser (the library to choose images)
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "SELECT IMAGE"), GALLERY_PICK);
            }
        });
    }


    private void setupToolbar() {
        Toolbar mToolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(mToolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowCustomEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {

            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                Uri resultUri = result.getUri();
                File thumb_file = new File(resultUri.getPath());
                String userUid = mCurrentUserId;
                Bitmap thumb_bitmap = null;
                try {
                    thumb_bitmap = new Compressor(this)
                            .setMaxWidth(200).setMaxHeight(200)
                            .setQuality(75).compressToBitmap(thumb_file);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] thumb_byte = baos.toByteArray();


                final StorageReference filepath = chatPresenter.getPresenter().getmStorageRef()
                .child("messages_image").child(userUid + ".jpg");

                uploadImage(resultUri, filepath, thumb_byte);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void uploadImage(Uri resultUri, final StorageReference filepath, final byte[] thumb_byte) {
        filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                //Toast.makeText(SettingsActivity.this, "Working", Toast.LENGTH_LONG).show();
                filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        final String download_uri = uri.toString();

                        String current_user_ref = "Messages/" + mCurrentUserId + "/" + mChatReceiverUser;
                        String chat_user_ref = "Messages/" + mChatReceiverUser + "/" + mCurrentUserId;

                        DatabaseReference user_message_push = chatPresenter.getPresenter().getmRootRef()
                        .child("Messages").child(mCurrentUserId).child(mChatReceiverUser).push();

                        String push_id = user_message_push.getKey();

                        //use Map instead of HashMap to update rather than delete existing data
                        Map messageMap = new HashMap();
                        messageMap.put("message", download_uri);
                        messageMap.put("seen", false);
                        messageMap.put("type", "image");
                        messageMap.put("time", ServerValue.TIMESTAMP);
                        messageMap.put("from", mCurrentUserId);

                        Map messageUserMap = new HashMap();
                        messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
                        messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);
                        //use updateChildren instead of setValue
                        chatPresenter.getPresenter().getmRootRef().updateChildren(messageUserMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                } else {
                                    Toast.makeText(ChatActivity.this, "Error occured while uploading image", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });
            }
        });
    }

}
