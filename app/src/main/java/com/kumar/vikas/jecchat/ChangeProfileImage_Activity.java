package com.kumar.vikas.jecchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class ChangeProfileImage_Activity extends AppCompatActivity {

    private ImageView profileImage;
    private Button changeImage;
    private ProgressDialog progressDialog;
    private StorageReference mImageRef;
    private FirebaseUser firebaseUser;
    private DatabaseReference user;
    private DatabaseReference mRootRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile_image_);

        mImageRef= FirebaseStorage.getInstance().getReference();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        final String userId;

        userId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        user= FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        mRootRef=FirebaseDatabase.getInstance().getReference();
        profileImage=(ImageView)findViewById(R.id.profileImage);
        final String imgUrl=getIntent().getStringExtra("image");
        Picasso.with(ChangeProfileImage_Activity.this).load(imgUrl).networkPolicy(NetworkPolicy.OFFLINE).into(profileImage, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                Picasso.with(ChangeProfileImage_Activity.this).load(imgUrl).into(profileImage);
            }
        });


        changeImage=(Button)findViewById(R.id.changeImage);
        changeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CropImage.activity().setAspectRatio(1,1)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(ChangeProfileImage_Activity.this);


            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                Intent intent=new Intent(ChangeProfileImage_Activity.this,CroppedImage_Activity.class);
                intent.putExtra("uri",result.getUri().toString());
                intent.putExtra("id","image");
                startActivity(intent);
                finish();

               /* progressDialog = new ProgressDialog(ChangeProfileImage_Activity.this);
                progressDialog.setTitle("Uploading Image...");
                progressDialog.setMessage("Please wait until image upload");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                Uri resultUri = result.getUri();


                File filePath = new File(resultUri.getPath());
                Bitmap thumbBitmap;


                try {
                    thumbBitmap = new Compressor(this)
                            .setMaxHeight(250)
                            .setMaxWidth(250)
                            .setQuality(60)
                            .compressToBitmap(filePath);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    thumbBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] thumbData = baos.toByteArray();
                    StorageReference thumbStorage = mImageRef.child("image_profiles").child("thumb").child(firebaseUser.getUid() + "jpg");

                    UploadTask uploadTask = thumbStorage.putBytes(thumbData);
                    uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            final String thumbUrl = task.getResult().getDownloadUrl().toString();
                            user.child("thumbImage").setValue(thumbUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Picasso.with(ChangeProfileImage_Activity.this).load(thumbUrl).networkPolicy(NetworkPolicy.OFFLINE).into(profileImage, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            progressDialog.dismiss();
                                        }

                                        @Override
                                        public void onError() {
                                            Picasso.with(ChangeProfileImage_Activity.this).load(thumbUrl).into(profileImage);
                                            progressDialog.dismiss();
                                        }
                                    });
                                }
                            });
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

                StorageReference ref = mImageRef.child("image_profiles").child(firebaseUser.getUid() + "jpg");


                ref.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            final String imageUrl = task.getResult().getDownloadUrl().toString();

                            user.child("image").setValue(imageUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {



                                    } else {
                                        Toast.makeText(ChangeProfileImage_Activity.this, "faild to upload in database", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(ChangeProfileImage_Activity.this, "faild to upload in storage", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });*/
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();}
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        mRootRef.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("online").setValue("true");

    }

    @Override
    protected void onPause() {
        super.onPause();
        mRootRef.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("online").setValue(ServerValue.TIMESTAMP);

    }
}
