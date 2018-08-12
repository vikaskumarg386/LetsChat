package com.kumar.vikas.jecchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import id.zelory.compressor.Compressor;

public class CroppedImage_Activity extends AppCompatActivity {

    ImageView imageView;
    Button button;
    private StorageReference mImageRef;
    private FirebaseUser firebaseUser;
    private DatabaseReference groupRef;
    private String year;
    private String id;
    private StorageReference thumbStorage;
    private StorageReference ref;
    private String image,thumbImage;
    private String college,fieldOfWork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cropped_image_);

        final String uri=getIntent().getStringExtra("uri");
        id=getIntent().getStringExtra("id");
        year=getIntent().getStringExtra("year");
        imageView=findViewById(R.id.croppedImage);
        button=findViewById(R.id.saveCroppedImage);
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        mImageRef= FirebaseStorage.getInstance().getReference();

        FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                college=dataSnapshot.child("college").getValue().toString();
                fieldOfWork=dataSnapshot.child("fieldOfWork").getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        if (id.contains("groupId")){
            groupRef=FirebaseDatabase.getInstance().getReference().child("group").child(id);
            ref = mImageRef.child("image_group").child(id+""+year+college+ ".jpg");
            thumbStorage = mImageRef.child("image_group").child("thumb").child(id+""+year+college+ ".jpg");
            image="image";
            thumbImage="thumbImage";
        }
        else if (id.equals("cse")||id.equals("me")||id.equals("ec")||id.equals("it")||id.equals("ce")||id.equals("ee")||id.equals("ip")||id.contains("branchId")){

            FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    year=dataSnapshot.child("graduationYear").getValue().toString();
                    college=dataSnapshot.child("college").getValue().toString();
                    fieldOfWork=dataSnapshot.child("fieldOfWork").getValue().toString();
                    //Toast.makeText(CroppedImage_Activity.this,year+" "+id,Toast.LENGTH_SHORT).show();
                    groupRef= FirebaseDatabase.getInstance().getReference().child("college").child(college).child("fieldOfWork").child(fieldOfWork).child("year").child(year).child("branch").child(id);
                    ref = mImageRef.child("image_group").child(id+""+year+college + ".jpg");
                    thumbStorage = mImageRef.child("image_group").child("thumb").child(id+""+year+college + ".jpg");
                    image="image";
                    thumbImage="thumbImage";
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
         else if (id.equals("imageCover")){
            groupRef=FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            thumbStorage = mImageRef.child("image_profiles_cover").child("thumb_cover").child(firebaseUser.getUid() + "jpg");
            ref = mImageRef.child("image_profiles_cover").child(firebaseUser.getUid() + "jpg");
            image="imageCover";
            thumbImage="thumbImageCover";


         }
        else if (id.equals("image")){
            groupRef=FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            thumbStorage =mImageRef.child("image_profiles").child("thumb").child(firebaseUser.getUid() + "jpg");
            ref = mImageRef.child("image_profiles").child(firebaseUser.getUid() + "jpg");
            image="image";
            thumbImage="thumbImage";
        }
        Picasso.with(CroppedImage_Activity.this).load(uri).networkPolicy(NetworkPolicy.OFFLINE).into(imageView, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                Picasso.with(CroppedImage_Activity.this).load(uri).into(imageView);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog progressDialog = new ProgressDialog(CroppedImage_Activity.this);
                progressDialog.setTitle("Uploading Image...");
                progressDialog.setMessage("Please wait until image upload");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();




                final File filePath = new File(Uri.parse(uri).getPath());
                Bitmap thumbBitmap;


                try {
                    thumbBitmap = new Compressor(CroppedImage_Activity.this)
                            .setMaxHeight(250)
                            .setMaxWidth(250)
                            .setQuality(60)
                            .compressToBitmap(filePath);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    thumbBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] thumbData = baos.toByteArray();


                    UploadTask uploadTask = thumbStorage.putBytes(thumbData);
                    uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            final String thumbUrl = task.getResult().getDownloadUrl().toString();
                            groupRef.child(thumbImage).setValue(thumbUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (id.equals("image")){
                                        Intent intent=new Intent(CroppedImage_Activity.this,ChangeProfileImage_Activity.class);
                                        intent.putExtra("image",thumbUrl);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else if(id.equals("imageCover")){
                                        Intent intent=new Intent(CroppedImage_Activity.this,ChangeImageCover_Activity.class);
                                        intent.putExtra("image",thumbUrl);
                                        startActivity(intent);
                                        finish();

                                    }
                                    else {
                                    Intent intent=new Intent(CroppedImage_Activity.this,ChangeGroupImage_Activity.class);
                                    intent.putExtra("image",thumbUrl);
                                    intent.putExtra("Id",id);
                                    intent.putExtra("year",year);
                                    startActivity(intent);
                                    finish();}
                                }
                            });
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }




                ref.putFile(Uri.parse(uri)).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            final String imageUrl = task.getResult().getDownloadUrl().toString();

                            groupRef.child(image).setValue(imageUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {



                                    } else {
                                        Toast.makeText(CroppedImage_Activity.this, "faild to upload in database", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(CroppedImage_Activity.this, "faild to upload in storage", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
            }
        });





    }
}
