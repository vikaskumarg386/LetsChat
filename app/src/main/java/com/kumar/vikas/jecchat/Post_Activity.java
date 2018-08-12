package com.kumar.vikas.jecchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;


public class Post_Activity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText timelineUserMessage;

    private DatabaseReference mrootRef;
    private Button addimage;
    private Button addVideo;
    private Button addFile;
    private ImageView imageView;
    private StorageReference mImageRef;
    private  int GALLARY_PIC=1;
    private String pushKey;
    private String imageUrl="null";
    private String videoUrl="null";
    private String fileUrl="null";
    private String refKey;
    private ProgressDialog progressDialog;
    private VideoView videoView;
    private String fileName;
    private String userId;
    private String college;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_);


        toolbar=(Toolbar)findViewById(R.id.postToolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        getSupportActionBar().setTitle("post message");
        timelineUserMessage=(EditText) findViewById(R.id.timelineUserMessage);

        addimage=(Button)findViewById(R.id.addImage);
        addVideo=(Button)findViewById(R.id.addVideo);
        addFile=(Button)findViewById(R.id.addFile);
        imageView=(ImageView)findViewById(R.id.postImage);
        videoView=(VideoView)findViewById(R.id.postVideo);
        mrootRef= FirebaseDatabase.getInstance().getReference();
        mImageRef= FirebaseStorage.getInstance().getReference();
        userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        mrootRef.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("online").setValue("true");
        videoView.setVisibility(View.INVISIBLE);
        mrootRef.child("users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                college=dataSnapshot.child("college").getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference pushRef=mrootRef.child("timeLinePost").push();
        pushKey=pushRef.getKey();

        String message=getIntent().getStringExtra("message");
        String image=getIntent().getStringExtra("image");
        final String video=getIntent().getStringExtra("video");
        refKey=getIntent().getStringExtra("ref");

        timelineUserMessage.setText(message);
        videoUrl=video;
        imageUrl=image;
        if (!video.equals("null")){
            image="null";
            imageUrl="null";
            videoView.setVideoURI(Uri.parse(video));
            videoView.seekTo(100);
        }
        Picasso.with(Post_Activity.this).load(image).into(imageView);
        if(!video.equals("null")){
            videoView.setVideoURI(Uri.parse(video));
            videoView.seekTo(100);
            //videoView.start();
        }



        addimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* Intent galleryIntent=new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent,"Select Image"),GALLARY_PIC);*/

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(Post_Activity.this);


            }
        });

        addVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.setVisibility(View.VISIBLE);
                Intent galleryIntent=new Intent();
                galleryIntent.setType("video/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent,"Select Video"),GALLARY_PIC);
            }
        });

        addFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                //intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 3);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

       /* progressDialog=new ProgressDialog(Post_Activity.this);
        progressDialog.setTitle("Image is uploading");
        progressDialog.setMessage("please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();*/
           final String userId=FirebaseAuth.getInstance().getCurrentUser().getUid();




            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {

                    progressDialog = new ProgressDialog(Post_Activity.this);
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
                        StorageReference thumbStorage = mImageRef.child("imagePost").child("thumb").child(pushKey).child(ServerValue.TIMESTAMP + "jpg");

                        UploadTask uploadTask = thumbStorage.putBytes(thumbData);
                        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {

                                    imageUrl = task.getResult().getDownloadUrl().toString();

                                    Picasso.with(Post_Activity.this).load(imageUrl).into(imageView);
                                    progressDialog.dismiss();

                                } else {
                                    Toast.makeText(Post_Activity.this, "faild to upload in storage", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    StorageReference ref = mImageRef.child("imagePost").child(pushKey).child(ServerValue.TIMESTAMP + "jpg");

                    ref.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        }
                    });
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();}
            }


            if(requestCode==GALLARY_PIC&&resultCode==RESULT_OK){

                progressDialog = new ProgressDialog(Post_Activity.this);
                progressDialog.setTitle("Uploading Video...");
                progressDialog.setMessage("Please wait until video upload");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                Uri videoUri=data.getData();

                StorageReference thumbStorage = mImageRef.child("videoPost").child(pushKey).child(ServerValue.TIMESTAMP+" mp4");

                UploadTask uploadTask=thumbStorage.putFile(videoUri);
                        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()) {

                            videoUrl = task.getResult().getDownloadUrl().toString();

                            videoView.setVideoURI(Uri.parse(videoUrl));
                            videoView.seekTo(100);
                            //videoView.start();
                            progressDialog.dismiss();

                        } else {
                            Toast.makeText(Post_Activity.this, "faild to upload in storage", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }

                    }
                });

            }

        if(requestCode==3&&resultCode==RESULT_OK){

            progressDialog = new ProgressDialog(Post_Activity.this);
            progressDialog.setTitle("Uploading file...");
            progressDialog.setMessage("Please wait until file upload");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            Uri fileUri=data.getData();
            final Cursor returnCursor =
                    getContentResolver().query(fileUri, null, null, null, null);

            final int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            fileName=returnCursor.getString(nameIndex);

            StorageReference thumbStorage = mImageRef.child("filePost").child(pushKey).child(ServerValue.TIMESTAMP+" ");

            UploadTask uploadTask=thumbStorage.putFile(fileUri);
            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if (task.isSuccessful()) {

                        fileUrl = task.getResult().getDownloadUrl().toString();
                        final int dr;
                        if(fileName.contains("pdf")){
                            dr=R.drawable.pdf;
                        }
                        else if(fileName.contains("doc")){
                            dr=R.drawable.doc;
                        }
                        else if(fileName.contains("ppt"))
                        {dr=R.drawable.ppt;}
                        else dr=R.drawable.folder;
                        final Picasso picasso;
                        picasso=Picasso.with(getApplicationContext());
                        picasso.setIndicatorsEnabled(false);
                        picasso.load(dr).resize(70,70).centerCrop().networkPolicy(NetworkPolicy.OFFLINE).into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                picasso.load(dr).resize(70,70).centerCrop().into(imageView);
                            }
                        });


                        progressDialog.dismiss();

                    } else {
                        Toast.makeText(Post_Activity.this, "faild to upload in storage", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }

                }
            });

        }



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.post_menu_bar,menu);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        String time=DateFormat.getDateInstance().format(new Date()).substring(0,DateFormat.getDateInstance().format(new Date()).lastIndexOf(" "))+" at "+DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date());

        switch (item.getItemId()) {


            case android.R.id.home:
            {onBackPressed();
                    return true;
            }
            case R.id.post: {

                item.setEnabled(false);

                final String userMessage = timelineUserMessage.getText().toString();
                if ((userMessage.equals(null)||userMessage.equals(""))&&imageUrl.equals("null")&&videoUrl.equals("null")) {
                    Toast.makeText(Post_Activity.this, "message is empty", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    if(!refKey.equals("null")){
                        if(imageUrl.equals("null")&&videoUrl.equals("null")&&fileUrl.equals("null"))
                        {
                            progressDialog = new ProgressDialog(Post_Activity.this);
                            progressDialog.setMessage("Pleas wait posting");
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.show();
                         Map map=new HashMap();
                         map.put("timeLinePost/"+"timeLineUserPost/"+userId+"/"+refKey+"/"+"message",userMessage);
                            map.put("timeLinePost/"+"timeLineAllPost/"+college+"/"+refKey+"/"+"message",userMessage);

                         mrootRef.updateChildren(map, new DatabaseReference.CompletionListener() {
                             @Override
                             public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                 if (databaseError==null){

                                     final Map map1=new HashMap();

                                     mrootRef.child("college").child(college).child("users").addValueEventListener(new ValueEventListener() {
                                         @Override
                                         public void onDataChange(DataSnapshot dataSnapshot) {
                                             for (DataSnapshot childSnapshot:dataSnapshot.getChildren()){

                                                 map1.put("timeLinePost/"+"timeLineUserAllPost/"+childSnapshot.getKey()+"/"+refKey+"/"+"message",userMessage);
                                                 mrootRef.updateChildren(map1);


                                             }
                                         }

                                         @Override
                                         public void onCancelled(DatabaseError databaseError) {

                                         }
                                     });
                                     Toast.makeText(Post_Activity.this,"Post updated",Toast.LENGTH_SHORT).show();
                                     Intent intent = new Intent(Post_Activity.this, MainPage.class);
                                     intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                     startActivity(intent);
                                     progressDialog.dismiss();
                                 }
                             }
                         });
                        }
                        else  if((!imageUrl.equals("null"))&&videoUrl.equals("null")&&fileUrl.equals("null")){
                            progressDialog = new ProgressDialog(Post_Activity.this);
                            progressDialog.setTitle("Uploading image...");
                            progressDialog.setMessage("Please wait until file upload");
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.show();
                            Map map=new HashMap();
                            map.put("timeLinePost/"+"timeLineUserPost/"+userId+"/"+refKey+"/"+"message",userMessage);
                            map.put("timeLinePost/"+"timeLineUserPost/"+userId+"/"+refKey+"/"+"image",imageUrl);
                            map.put("timeLinePost/"+"timeLineUserPost/"+userId+"/"+refKey+"/"+"type","image");

                            map.put("timeLinePost/"+"timeLineAllPost/"+college+"/"+refKey+"/"+"message",userMessage);
                            map.put("timeLinePost/"+"timeLineAllPost/"+college+"/"+refKey+"/"+"image",imageUrl);
                            map.put("timeLinePost/"+"timeLineAllPost/"+college+"/"+refKey+"/"+"type","image");
                            mrootRef.updateChildren(map, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if (databaseError==null){

                                        final Map map1=new HashMap();

                                        mrootRef.child("college").child(college).child("users").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                for (DataSnapshot childSnapshot:dataSnapshot.getChildren()){

                                                    map1.put("timeLinePost/"+"timeLineUserAllPost/"+childSnapshot.getKey()+"/"+refKey+"/"+"message",userMessage);
                                                    map1.put("timeLinePost/"+"timeLineUserAllPost/"+childSnapshot.getKey()+"/"+refKey+"/"+"image",imageUrl);
                                                    map1.put("timeLinePost/"+"timeLineUserAllPost/"+childSnapshot.getKey()+"/"+refKey+"/"+"type","image");

                                                    mrootRef.updateChildren(map1);


                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                                        Toast.makeText(Post_Activity.this,"Post updated",Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Post_Activity.this, MainPage.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                        }

                        else if(imageUrl.equals("null")&&(!videoUrl.equals("null"))&&fileUrl.equals("null")) {
                            progressDialog = new ProgressDialog(Post_Activity.this);
                            progressDialog.setTitle("Uploading video...");
                            progressDialog.setMessage("Please wait until file upload");
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.show();
                            Map map = new HashMap();
                            map.put("timeLinePost/"+"timeLineUserPost/"+userId+"/"+refKey+"/"+"message",userMessage);
                            map.put("timeLinePost/"+"timeLineUserPost/"+userId+"/"+refKey+"/"+"video",videoUrl);
                            map.put("timeLinePost/"+"timeLineUserPost/"+userId+"/"+refKey+"/"+"type","video");

                            map.put("timeLinePost/"+"timeLineAllPost/"+college+"/"+refKey +"/"+"message",userMessage);
                            map.put("timeLinePost/"+"timeLineAllPost/"+college+"/"+refKey +"/"+"video",videoUrl);
                            map.put("timeLinePost/"+"timeLineAllPost/"+college+"/"+refKey +"/"+"type","video");
                            mrootRef.updateChildren(map, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if (databaseError == null) {

                                        final Map map1=new HashMap();

                                        mrootRef.child("college").child(college).child("users").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                for (DataSnapshot childSnapshot:dataSnapshot.getChildren()){

                                                    map1.put("timeLinePost/"+"timeLineUserAllPost/"+childSnapshot.getKey()+"/"+refKey+"/"+"message",userMessage);
                                                    map1.put("timeLinePost/"+"timeLineUserAllPost/"+childSnapshot.getKey()+"/"+refKey+"/"+"video",videoUrl);
                                                    map1.put("timeLinePost/"+"timeLineUserAllPost/"+childSnapshot.getKey()+"/"+refKey+"/"+"type","video");

                                                    mrootRef.updateChildren(map1);


                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                        Toast.makeText(Post_Activity.this, "Post updated", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(Post_Activity.this, MainPage.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                        }
                        else  {
                            progressDialog = new ProgressDialog(Post_Activity.this);
                            progressDialog.setTitle("Uploading file...");
                            progressDialog.setMessage("Please wait until file upload");
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.show();
                            final Map map = new HashMap();
                            map.put("timeLinePost/"+"timeLineUserPost/"+userId+"/"+refKey+"/"+"message",userMessage);
                            map.put("timeLinePost/"+"timeLineUserPost/"+userId+"/"+refKey+"/"+"file",fileUrl);
                            map.put("timeLinePost/"+"timeLineUserPost/"+userId+"/"+refKey+"/"+"type","file");

                            map.put("timeLinePost/"+"timeLineAllPost/"+college+"/"+refKey+"/"+"message",userMessage);
                            map.put("timeLinePost/"+"timeLineAllPost/"+college+"/"+refKey+"/"+"file",fileUrl);
                            map.put("timeLinePost/"+"timeLineAllPost/"+college+"/"+refKey+"/"+"type","file");
                            mrootRef.updateChildren(map, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if (databaseError == null) {

                                        final Map map1=new HashMap();

                                        mrootRef.child("college").child(college).child("users").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                for (DataSnapshot childSnapshot:dataSnapshot.getChildren()){

                                                    map1.put("timeLinePost/"+"timeLineUserAllPost/"+childSnapshot.getKey()+"/"+refKey+"/"+"message",userMessage);
                                                    map1.put("timeLinePost/"+"timeLineUserAllPost/"+childSnapshot.getKey()+"/"+refKey+"/"+"file",fileUrl);
                                                    map1.put("timeLinePost/"+"timeLineUserAllPost/"+childSnapshot.getKey()+"/"+refKey+"/"+"type","file");

                                                        mrootRef.updateChildren(map1);


                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                                        Toast.makeText(Post_Activity.this, "Post updated", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(Post_Activity.this, MainPage.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                        }



                    }


                    else
                    {  if(imageUrl.equals("null")&&videoUrl.equals("null")&&fileUrl.equals("null"))
                      {
                          progressDialog = new ProgressDialog(Post_Activity.this);
                          progressDialog.setMessage("Pleas wait posting");
                          progressDialog.setCanceledOnTouchOutside(false);
                          progressDialog.show();
                          final DatabaseReference pushRef = mrootRef.child("college").child(college).child("users").child(userId).child("timeLinePost").push();
                          pushKey = pushRef.getKey();
                          final Map<String, String> map = new HashMap<>();
                          map.put("message", userMessage);
                          map.put("image","null");
                          map.put("video","null");
                          map.put("file","null");
                          map.put("time", time);
                          map.put("from", FirebaseAuth.getInstance().getCurrentUser().getUid());
                          map.put("type", "text");
                          map.put("likes", "0");
                          map.put("comments", "0");
                          map.put("push_key", pushKey);
                          map.put("college",college);
                          Map mapPost =new HashMap();
                          mapPost.put("timeLinePost/"+"timeLineUserPost/"+userId+"/"+pushKey,map);
                          mapPost.put("timeLinePost/"+"timeLineUserAllPost/"+userId+"/"+pushKey,map);
                          mapPost.put("timeLinePost/"+"timeLineAllPost/"+college+"/"+pushKey,map);

                          mrootRef.updateChildren(mapPost, new DatabaseReference.CompletionListener() {
                              @Override
                              public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                  if (databaseError==null){


                                      final Map map1=new HashMap();

                                      mrootRef.child("college").child(college).child("users").addValueEventListener(new ValueEventListener() {
                                          @Override
                                          public void onDataChange(DataSnapshot dataSnapshot) {
                                              for (DataSnapshot childSnapshot:dataSnapshot.getChildren()){
                                                  map1.put("timeLinePost/"+"timeLineUserAllPost/"+childSnapshot.getKey()+"/"+pushKey,map);

                                                  mrootRef.updateChildren(map1);


                                              }
                                          }

                                          @Override
                                          public void onCancelled(DatabaseError databaseError) {

                                          }
                                      });

                                      Toast.makeText(Post_Activity.this, "Post", Toast.LENGTH_SHORT).show();

                                      Intent intent = new Intent(Post_Activity.this, MainPage.class);
                                      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                      startActivity(intent);
                                      progressDialog.dismiss();
                                  }
                              }
                          });


                   }

                   else if((!imageUrl.equals("null"))&&videoUrl.equals("null")&&fileUrl.equals("null")) {
                    progressDialog = new ProgressDialog(Post_Activity.this);
                    progressDialog.setTitle("Uploading Image...");
                    progressDialog.setMessage("Please wait until image upload");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    DatabaseReference pushRef = mrootRef.child("timeLinePost").child("timelinePostUser").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push();
                    pushKey = pushRef.getKey();
                    final Map<String, String> map = new HashMap<>();
                    map.put("message", userMessage);
                    map.put("image",imageUrl);
                    map.put("video","null");
                    map.put("file","null");
                    map.put("time",time);
                    map.put("from", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    map.put("type", "image");
                    map.put("likes", "0");
                    map.put("comments", "0");
                    map.put("push_key", pushKey);
                    map.put("college",college);

                    Map mapPost =new HashMap();
                    mapPost.put("timeLinePost/"+"timeLineUserPost/"+userId+"/"+pushKey,map);
                    mapPost.put("timeLinePost/"+"timeLineUserAllPost/"+userId+"/"+pushKey,map);
                    mapPost.put("timeLinePost/"+"timeLineAllPost/"+college+"/"+pushKey,map);
                        mrootRef.updateChildren(mapPost, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError==null){


                                    final Map map1=new HashMap();

                                    mrootRef.child("college").child(college).child("users").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot childSnapshot:dataSnapshot.getChildren()){
                                                map1.put("timeLinePost/"+"timeLineUserAllPost/"+childSnapshot.getKey()+"/"+pushKey,map);

                                                mrootRef.updateChildren(map1);


                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                    Toast.makeText(Post_Activity.this, "Post", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(Post_Activity.this, MainPage.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    progressDialog.dismiss();
                                }
                            }
                        });


                    }

                    else if(imageUrl.equals("null")&&(!videoUrl.equals("null"))&&fileUrl.equals("null"))
                    {
                    progressDialog = new ProgressDialog(Post_Activity.this);
                    progressDialog.setTitle("Uploading Video...");
                    progressDialog.setMessage("Please wait until video upload");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    DatabaseReference pushRef = mrootRef.child("timeLinePost").child("timelinePostUser").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push();
                    pushKey = pushRef.getKey();
                    final Map<String, String> map = new HashMap<>();
                    map.put("message", userMessage);
                    map.put("image","https://firebasestorage.googleapis.com/v0/b/jecchat-bcd82.appspot.com/o/images.jpeg?alt=media&token=5cdc5b9b-6d05-4221-947e-80a7c5af8b89");
                    map.put("video",videoUrl);
                    map.put("file","null");
                    map.put("time", time);
                    map.put("from", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    map.put("type", "video");
                    map.put("likes", "0");
                    map.put("comments", "0");
                    map.put("push_key", pushKey);
                    map.put("college",college);
                        Map mapPost =new HashMap();
                        mapPost.put("timeLinePost/"+"timeLineUserPost/"+userId+"/"+pushKey,map);
                        mapPost.put("timeLinePost/"+"timeLineUserAllPost/"+userId+"/"+pushKey,map);
                        mapPost.put("timeLinePost/"+"timeLineAllPost/"+college+"/"+pushKey,map);
                        mrootRef.updateChildren(mapPost, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError==null){


                                    final Map map1=new HashMap();

                                    mrootRef.child("college").child(college).child("users").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot childSnapshot:dataSnapshot.getChildren()){
                                                map1.put("timeLinePost/"+"timeLineUserAllPost/"+childSnapshot.getKey()+"/"+pushKey,map);

                                                mrootRef.updateChildren(map1);


                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                    Toast.makeText(Post_Activity.this, "Post", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(Post_Activity.this, MainPage.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    progressDialog.dismiss();
                                }
                            }
                        });
                    }
                    else {

                        progressDialog = new ProgressDialog(Post_Activity.this);
                        progressDialog.setTitle("Uploading file...");
                        progressDialog.setMessage("Please wait until file upload");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();

                        DatabaseReference pushRef = mrootRef.child("timeLinePost").child("timelinePostUser").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push();
                        pushKey = pushRef.getKey();
                        final Map<String, String> map = new HashMap<>();
                        map.put("message", userMessage);
                        map.put("image", "null");
                        map.put("video", "null");
                        map.put("file", fileUrl);
                        map.put("fileName", fileName);
                        map.put("time", time);
                        map.put("from", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        map.put("type", "file");
                        map.put("likes", "0");
                        map.put("comments", "0");
                        map.put("push_key", pushKey);
                        map.put("college",college);
                        Map mapPost =new HashMap();
                        mapPost.put("timeLinePost/"+"timeLineUserPost/"+userId+"/"+pushKey,map);
                        mapPost.put("timeLinePost/"+"timeLineUserAllPost/"+userId+"/"+pushKey,map);
                        mapPost.put("timeLinePost/"+"timeLineAllPost/"+college+"/"+pushKey,map);
                        mrootRef.updateChildren(mapPost, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError==null){


                                    final Map map1=new HashMap();

                                    mrootRef.child("college").child(college).child("users").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot childSnapshot:dataSnapshot.getChildren()){
                                                map1.put("timeLinePost/"+"timeLineUserAllPost/"+childSnapshot.getKey()+"/"+pushKey,map);

                                                mrootRef.updateChildren(map1);


                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                    Toast.makeText(Post_Activity.this, "Post", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(Post_Activity.this, MainPage.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    progressDialog.dismiss();
                                }
                            }
                        });

                    }
                }  }
            }
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mrootRef.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("online").setValue("true");
    }

    @Override
    protected void onPause() {
        super.onPause();
        mrootRef.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("online").setValue(ServerValue.TIMESTAMP);
    }
}
