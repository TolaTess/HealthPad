package com.tolaotesanya.healthpad.activities.accountsettings;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.tolaotesanya.healthpad.R;
import com.tolaotesanya.healthpad.activities.auth.DoctorRegisterActivity;
import com.tolaotesanya.healthpad.helper.DialogFragmentHelper;
import com.tolaotesanya.healthpad.modellayer.database.FirebaseDatabaseLayer;
import com.tolaotesanya.healthpad.modellayer.database.FirebasePresenter;
import com.tolaotesanya.healthpad.modellayer.enums.ClassName;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "SettingsActivity";

    private static final int GALLERY_PICK = 1;
    private static final int MAX_LENGTH = 30;

    private ProgressDialog mProgressBar;

    //UI element
    private Button mChangeImage;
    private Button rewardClaim;
    private Button mSaveChanges;
    private Button mDoctorCheck;
    private TextInputLayout mStatus;
    private TextInputLayout mName;
    private ProgressDialog mRegProgress;

    private Context mContext = SettingsActivity.this;

    //Firebase
    private FirebasePresenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        presenter = new FirebaseDatabaseLayer(mContext);
        setupToolbar();
        attachUI();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(" ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

     private void attachUI() {
        rewardClaim = findViewById(R.id.payment);
        mChangeImage = findViewById(R.id.setting_change_image);
        mSaveChanges = findViewById(R.id.save_settings);
        mDoctorCheck = findViewById(R.id.doctor_check_btn);
        mStatus = findViewById(R.id.settings_status_input);
        mName = findViewById(R.id.setting_display_name);


        //Persist from Account Activity
        String status_value = getIntent().getStringExtra("status_value");
        String display_name_value = getIntent().getStringExtra("display_name_value");

        mStatus.getEditText().setHint(status_value);
        mName.getEditText().setHint(display_name_value);

        mSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");
                mRegProgress = new ProgressDialog(SettingsActivity.this);
                mRegProgress.setTitle("Saving changes");
                mRegProgress.setMessage("Please wait while we save the changes");
                mRegProgress.setCanceledOnTouchOutside(false);
                mRegProgress.show();

                String status_v = mStatus.getEditText().getText().toString();
                String name_v = mName.getEditText().getText().toString();

                if (!TextUtils.isEmpty(status_v)) {
                    presenter.getmUserDatabase().child(presenter.getMcurrent_user_id())
                            .child("status").setValue(status_v).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d(TAG, "onComplete: Status update");
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Status update: is Sucessfull");
                                mRegProgress.dismiss();
                            } else {
                                Log.d(TAG, "Status update: is not sucessfull");
                                mRegProgress.hide();
                                Toast.makeText(SettingsActivity.this, "Error saving changes", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                if (!TextUtils.isEmpty(name_v)) {
                    presenter.getmUserDatabase().child(presenter.getMcurrent_user_id())
                            .child("name").setValue(name_v).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d(TAG, "onComplete: Display Name update");
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Display Name update: is Sucessfull");
                                mRegProgress.dismiss();
                            } else {
                                Log.d(TAG, "Display Name update: is not sucessfull");
                                mRegProgress.hide();
                                Toast.makeText(SettingsActivity.this,
                                        "Error saving changes", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });

        mChangeImage.setOnClickListener(new View.OnClickListener() {
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

        rewardClaim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setTitle("Rewards Coming Soon");
                builder.setMessage("We are planning some cool rewards just for you. Please look forward to it!");
                builder.create().show();
            }
        });

         userTypeCheck();
     }

    private void userTypeCheck() {
        final DatabaseReference updateUserType = presenter.getmUserDatabase()
                .child(presenter.getMcurrent_user_id()).child("user_type");

        updateUserType.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("user_type")) {
                    final String user_type = dataSnapshot.getValue().toString();
                    Log.d(TAG, "onDataChange: ");
                    if (user_type.equals("doctor")) {
                        mDoctorCheck.setText("Disable Doctor Account");
                        mDoctorCheck.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                updateUserType.setValue("user");
                                Toast.makeText(SettingsActivity.this,
                                        "Doctor Account now Disabled", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else if (user_type.equals("user")) {
                        DatabaseReference doctorCheckDB = FirebaseDatabase.getInstance().getReference()
                                .child("Doctors");
                        doctorCheckDB.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot doctors : dataSnapshot.getChildren()) {
                                    final String doctor_id = doctors.getKey();
                                    Log.d(TAG, "onDataChange: doctor keys" + doctor_id);
                                    if (presenter.getMcurrent_user_id().equals(doctor_id)) {
                                        mDoctorCheck.setText("Enable Doctor Account");
                                        mDoctorCheck.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Log.d(TAG, "onDataChange: doctor key" + doctor_id);
                                                updateUserType.setValue("doctor");
                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                } else {
                    mDoctorCheck.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent doctorIntent = new Intent(SettingsActivity.this, DoctorRegisterActivity.class);
                            startActivity(doctorIntent);
                            presenter.getIntentPresenter().presentIntent(ClassName.DoctorRegister, null, null);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                mProgressBar = new ProgressDialog(this);
                mProgressBar.setTitle("Uploading Image");
                mProgressBar.setMessage("Please wait while we upload and process the image");
                mProgressBar.setCanceledOnTouchOutside(false);
                mProgressBar.show();

                Uri resultUri = result.getUri();
                File thumb_file = new File(resultUri.getPath());
                String userUid = presenter.getMcurrent_user_id();
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
                byte [] thumb_byte = baos.toByteArray();


                final StorageReference filepath = presenter.getmStorageRef().child("profile_images").child(userUid + ".jpg");
                StorageReference thumb_filepath = presenter.getmStorageRef().child("profile_images").child("thumbs").child(userUid + ".jpg");

                uploadImage(resultUri, filepath, thumb_filepath, thumb_byte);

            } else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
            }
        }
    }

    private void uploadImage(Uri resultUri, final StorageReference filepath, final StorageReference thumbpath, final byte[] thumb_byte) {
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
                                        String thumb_download_uri = uri.toString();
                                        //use Map instead of HashMap to update rather than delete existing data
                                        final Map update_hashMap = new HashMap();
                                        update_hashMap.put("image", download_uri);
                                        update_hashMap.put("thumb_image", thumb_download_uri);
                                        //use updateChildren instead of setValue
                                        presenter.getmUserDatabase().child(presenter.getMcurrent_user_id())
                                                .updateChildren(update_hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    presenter.getmUserDatabase().child(presenter.getMcurrent_user_id()).child("user_type").addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            if (dataSnapshot.hasChild("user_type")) {
                                                                String userType = dataSnapshot.getValue().toString();
                                                                if (userType.equals("doctor")) {
                                                                    presenter.getmRootRef().child("Doctors").child(presenter.getMcurrent_user_id())
                                                                            .updateChildren(update_hashMap).addOnCompleteListener(new OnCompleteListener() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task task) {
                                                                            if (task.isSuccessful()) {
                                                                                mProgressBar.dismiss();
                                                                            } else {
                                                                                Toast.makeText(SettingsActivity.this, "Error occured while uploading image", Toast.LENGTH_LONG).show();
                                                                                mProgressBar.dismiss();
                                                                            }
                                                                        }
                                                                    });
                                                                } else {
                                                                    mRegProgress.dismiss();
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });
                                                    mProgressBar.dismiss();
                                                } else {
                                                    Toast.makeText(SettingsActivity.this, "Error occured while uploading image", Toast.LENGTH_LONG).show();
                                                    mProgressBar.dismiss();
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
        });
    }

}
