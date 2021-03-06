package com.tolaotesanya.healthpad.activities.posts;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.tolaotesanya.healthpad.R;
import com.tolaotesanya.healthpad.coordinator.IntentPresenter;
import com.tolaotesanya.healthpad.dependencies.DependencyRegistry;
import com.tolaotesanya.healthpad.modellayer.database.FirebasePresenter;
import com.tolaotesanya.healthpad.modellayer.enums.ClassName;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import id.zelory.compressor.Compressor;

public class PostsActivity extends AppCompatActivity {
    private static final String TAG = "PostsActivity";
    private static final int GALLERY_PICK = 1;

    private RelativeLayout mImageLayout;
    private ImageView mPostImage, mFeedImage;
    private ImageView mPostDirect;
    private EditText mTitle;
    private EditText mBody;
    private ProgressDialog mProgressbar;

    //Injection
    private FirebasePresenter presenter;
    private IntentPresenter intentPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);

        setupToolbar();
        attachUI();

        DependencyRegistry.shared.inject(this);
    }

    public void configureWith(FirebasePresenter presenter, IntentPresenter intentPresenter) {
        this.presenter = presenter;
        this.intentPresenter = intentPresenter;

        setupPostDirect();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.post_act_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add a posts");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void attachUI() {
        mBody = findViewById(R.id.post_body);
        mPostImage = findViewById(R.id.post_upload_image_btn);
        mPostDirect = findViewById(R.id.post_direct_btn);
        mTitle = findViewById(R.id.post_title_text);
        mFeedImage =findViewById(R.id.feed_image);
        mImageLayout = findViewById(R.id.relativeLayout_image);
    }

    private void setupPostDirect() {

        mPostDirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = mTitle.getText().toString();
                String body = mBody.getText().toString();
                mProgressbar = new ProgressDialog(PostsActivity.this);
                mProgressbar.setTitle("Uploading Post");
                mProgressbar.setMessage("Please wait...");
                mProgressbar.setCanceledOnTouchOutside(false);
                mProgressbar.show();
                Map setupMap = presenter.setupPostMap(title, body, "default", "default");
                presenter.getmRootRef().updateChildren(setupMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            mProgressbar.dismiss();
                            intentPresenter.presentIntent(PostsActivity.this, ClassName.Main, null, null);
                            finish();
                        } else
                        {
                            mProgressbar.hide();
                            Toast.makeText(PostsActivity.this, "An Error Occurred while Posting", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        mPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "SELECT IMAGE"), GALLERY_PICK);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {

            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .start(this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mProgressbar = new ProgressDialog(this);
                mProgressbar.setTitle("Uploading Image");
                mProgressbar.setMessage("Please wait while we upload and process the image");
                mProgressbar.setCanceledOnTouchOutside(false);
                mProgressbar.show();

                Uri resultUri = result.getUri();
                File thumb_file = new File(resultUri.getPath());
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

                DatabaseReference user_post_push = presenter.getmRootRef().child("Posts")
                        .child(presenter.getMcurrent_user_id()).push();

                final String push_id = user_post_push.getKey();

                final StorageReference filepath = presenter.getmStorageRef().child("post_images")
                        .child(push_id + ".jpg");
                StorageReference thumb_filepath = presenter.getmStorageRef().child("post_images")
                        .child("thumbs").child(push_id + ".jpg");

                uploadImage(resultUri, filepath, thumb_filepath, thumb_byte);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void uploadImage(Uri resultUri, final StorageReference filepath, final StorageReference thumbpath,
                             final byte[] thumb_byte) {
        filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        final String download_uri = uri.toString();
                        UploadTask uploadTask = thumbpath.putBytes(thumb_byte);
                        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                thumbpath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        final String thumb_download_uri = uri.toString();

                                        if (thumb_download_uri != null) {
                                            mProgressbar.dismiss();
                                            Picasso.get().load(thumb_download_uri).placeholder(R.drawable.health_pad_logo).into(mFeedImage);
                                            mImageLayout.setVisibility(View.VISIBLE);
                                            String title = mTitle.getText().toString();
                                            String body = mBody.getText().toString();
                                            savePostInDB(download_uri, thumb_download_uri, body, title);
                                        }
                                    }
                                    });
                                }
                            });
                        }
                    });
                }
            });
        }

    private void savePostInDB(final String download_uri, final String thumb_download_uri, final String body, final String
                              title) {
        mPostDirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map setupMap = presenter.setupPostMap(title, body, download_uri, thumb_download_uri);
                presenter.getmRootRef().updateChildren(setupMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            mProgressbar.dismiss();
                            intentPresenter.presentIntent(PostsActivity.this, ClassName.Main, null, null);
                            finish();
                        }else
                        {
                            Toast.makeText(PostsActivity.this, "An Error Occurred while Posting", Toast.LENGTH_LONG).show();
                            mProgressbar.dismiss();
                        }
                    }
                });
            }
        });
    }

}
