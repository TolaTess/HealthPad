package com.tolaotesanya.healthpad.activities.posts;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.tolaotesanya.healthpad.modellayer.database.FirebaseDatabaseLayer;
import com.tolaotesanya.healthpad.modellayer.database.FirebasePresenter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import id.zelory.compressor.Compressor;

public class PostsActivity extends AppCompatActivity {
    private static final String TAG = "PostsActivity";
    private static final int GALLERY_PICK = 1;

    private Button mUploadImage;
    private EditText mCaption;
    private TextView mName;
    private ImageView mImageView;
    private ProgressDialog mProgressbar;
    private String mCurrentUserId;
    private Context mContext = PostsActivity.this;

    private FirebasePresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);
        presenter = new FirebaseDatabaseLayer(mContext);
        mCurrentUserId = presenter.getMcurrent_user_id();
        attachUI();
    }

    private void attachUI() {
        mCaption = findViewById(R.id.post_caption);
        mName = findViewById(R.id.post_page_title);
        mUploadImage = findViewById(R.id.post_upload_image);
        mImageView = findViewById(R.id.posts_image_view);

        mUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //option 1
                // this will direct user to the Gallery chooser (the library to choose images)
                mProgressbar = new ProgressDialog(PostsActivity.this);
                mProgressbar.setTitle("Uploading Post");
                mProgressbar.setMessage("Please wait...");
                mProgressbar.setCanceledOnTouchOutside(false);
                mProgressbar.show();
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
                        .child(mCurrentUserId).push();

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

    //save image in database
    private void uploadImage(Uri resultUri, final StorageReference filepath, final StorageReference thumbpath,
                             final byte[] thumb_byte) {
        filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                //Toast.makeText(SettingsActivity.this, "Working", Toast.LENGTH_LONG).show();
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
                                        String caption = mCaption.getText().toString();
                                        if (!TextUtils.isEmpty(caption)) {
                                            Map setupMap = presenter.setupPostMap(caption, download_uri, thumb_download_uri);
                                            mCaption.setText("");
                                            presenter.getmRootRef().updateChildren(setupMap).addOnCompleteListener(new OnCompleteListener() {
                                                @Override
                                                public void onComplete(@NonNull Task task) {
                                                    if (task.isSuccessful()) {
                                                        Picasso.get().load(thumb_download_uri).placeholder(R.drawable.ic_launcher_foreground).into(mImageView);
                                                        mProgressbar.dismiss();
                                                     /*   HomeFragment fragment = new HomeFragment();
                                                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                                                        fragmentTransaction.replace(R.id.fragment_cont, fragment);
                                                        fragmentTransaction.commit();*/
                                                    }
                                                }
                                            });
                                        } else
                                        {
                                            Toast.makeText(PostsActivity.this, "Can post", Toast.LENGTH_LONG).show();
                                            mProgressbar.dismiss();
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
    }
