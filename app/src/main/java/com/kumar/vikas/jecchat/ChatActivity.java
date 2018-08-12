package com.kumar.vikas.jecchat;


import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.text.DateFormat;
import android.util.Base64;
import android.widget.VideoView;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatActivity extends AppCompatActivity {

    private Toolbar chatToolbar1;
    private TextView displayName;
    private TextView lastSeen;
    private CircleImageView icon;
    private EditText chatText;
    private ImageButton sendButton;
    private ImageButton addButton;
    private DatabaseReference mrootRef;
    private DatabaseReference mRef;
    private String userId;
    private String name;
    private RecyclerView messageList;
    private String id="";

    private ImageButton documents,images;
    private ImageButton video,music;

    private int selectMessageCount=0;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;



    private LinearLayoutManager linearLayout;
    private String lMessage;



    private int GALLARY_PIC=1,PDF_PIC=2;
    private StorageReference mImageRef;
    private ProgressDialog progressDialog;
    private String pushKey;
    private RelativeLayout chatRelativeLayout;

    private String college,fieldOfWork,branch;
    private String year;
    private long queiId;
    private DownloadManager dm;
    private String iMageUri;
    private String userName;
    private String friendName;
    private String image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatToolbar1=(Toolbar)findViewById(R.id.layout1);
        setSupportActionBar(chatToolbar1);



        final ActionBar actionBar=getSupportActionBar();
        getSupportActionBar().setTitle(" ");
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mrootRef= FirebaseDatabase.getInstance().getReference();
        id=getIntent().getStringExtra("key_noti");
        userId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        mRef=mrootRef.child("messages").child(userId).child(id);
        mrootRef.child("users").child(userId).child("online").setValue("true");


       LayoutInflater inflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionbarView=inflater.inflate(R.layout.chat_layout_for_img_icon,null);

        actionBar.setCustomView(actionbarView);

        displayName=(TextView)findViewById(R.id.displayName);

        lastSeen=(TextView)findViewById(R.id.last_Seen);

        chatRelativeLayout=(RelativeLayout)findViewById(R.id.chatRelativeLayout);
        icon=(CircleImageView)findViewById(R.id.chatIcon);

        mrootRef.child("notifications").child(userId).child(id+" Message").removeValue();
        mrootRef.child("chat").child(userId).child(id).child("seen").setValue(true);


        if (id.contains("groupId")){
            mrootRef.child("group").child(id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    friendName=dataSnapshot.child("name").getValue().toString();
                    image=dataSnapshot.child("thumbImage").getValue().toString();
                    displayName.setText(friendName);
                    lastSeen.setText("");
                    Picasso.with(ChatActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.user).into(icon, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(getApplicationContext()).load(image).placeholder(R.drawable.user).into(icon);
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else if(!(id.equals("cse")||id.equals("me")||id.equals("ec")||id.equals("it")||id.equals("ce")||id.equals("ee")||id.equals("ip")||id.contains("groupId")||id.contains("branchId"))) {
            mrootRef.child("users").child(id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String online = dataSnapshot.child("online").getValue().toString();
                    friendName=dataSnapshot.child("name").getValue().toString();
                    image=dataSnapshot.child("thumbImage").getValue().toString();
                    displayName.setText(friendName);
                    Picasso.with(ChatActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.user).into(icon, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(getApplicationContext()).load(image).placeholder(R.drawable.user).into(icon);
                        }
                    });


                    if (online.equals("true"))
                        lastSeen.setText("online");
                    else {

                        // TimeAgo timeAgo=new TimeAgo();
                        long seenTime = Long.parseLong(online);
                        String lastSeenTime = TimeAgo.getTimeAgo(seenTime, getApplicationContext());
                        lastSeen.setText(lastSeenTime);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else {

            mrootRef.child("users").child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    year=dataSnapshot.child("graduationYear").getValue().toString();
                    userName=dataSnapshot.child("name").getValue().toString();
                    String college=dataSnapshot.child("college").getValue().toString();
                    String fieldOfWork=dataSnapshot.child("fieldOfWork").getValue().toString();

                    mrootRef.child("college").child(college).child("fieldOfWork").child(fieldOfWork).child("year").child(year).child("branch").child(id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild("name"))
                            friendName=dataSnapshot.child("name").getValue().toString();
                            if (dataSnapshot.hasChild("thumbImage")){
                            image=dataSnapshot.child("thumbImage").getValue().toString();}
                            displayName.setText(friendName.toUpperCase());
                            lastSeen.setText("");
                            Picasso.with(ChatActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.user).into(icon, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {
                                    Picasso.with(getApplicationContext()).load(image).placeholder(R.drawable.user).into(icon);
                                }
                            });
                            lastSeen.setText("");
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }

        mrootRef.child("users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                year=dataSnapshot.child("graduationYear").getValue().toString();
                userName=dataSnapshot.child("name").getValue().toString();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        Picasso.with(this).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.user).into(icon, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                Picasso.with(getApplicationContext()).load(image).placeholder(R.drawable.user).into(icon);
            }
        });






                //saving messages

        sendButton=(ImageButton)findViewById(R.id.chat_send_btn);
        chatText=(EditText)findViewById(R.id.chat_message_view);
        messageList=(RecyclerView)findViewById(R.id.chatRecyclerView);
        mImageRef= FirebaseStorage.getInstance().getReference();
        addButton=(ImageButton)findViewById(R.id.chat_add_btn);


        mrootRef.child("friends").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
              if(!(id.equals("cse")||id.equals("me")||id.equals("ec")||id.equals("it")||id.equals("ce")||id.equals("ee")||id.equals("ip")||id.contains("groupId")||id.contains("branchId"))) {

                  if (!dataSnapshot.hasChild(id)) {
                      chatText.setText("Cannot send message");
                      chatText.setEnabled(false);
                      sendButton.setEnabled(false);
                      addButton.setEnabled(false);
                  }
              }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mrootRef.child("deactivateAccount").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(userId)){
                    chatText.setText("Cannot send message");
                    chatText.setEnabled(false);
                    sendButton.setEnabled(false);
                    addButton.setEnabled(false);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





        linearLayout=new LinearLayoutManager(this);
        linearLayout.setStackFromEnd(true);
        messageList.setLayoutManager(linearLayout);
        messageList.setHasFixedSize(true);
        showChat();


        if(id.contains("groupId")){
            mrootRef.child("group").child(id).child("users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild(userId)){
                        chatText.setText("Cannot send message");
                        chatText.setEnabled(false);
                        sendButton.setEnabled(false);
                        addButton.setEnabled(false);
                        chatRelativeLayout.setEnabled(false);
                        icon.setEnabled(false);
                        icon.setVisibility(View.INVISIBLE);

                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        mrootRef.child("users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                 college=dataSnapshot.child("college").getValue().toString();
                 fieldOfWork=dataSnapshot.child("fieldOfWork").getValue().toString();
                 branch=dataSnapshot.child("branch").getValue().toString();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if(id.equals("cse")||id.equals("me")||id.equals("ec")||id.equals("it")||id.equals("ce")||id.equals("ee")||id.equals("ip")||id.contains("groupId")||id.contains("branchId")){
                    Intent GroupImageIntent=new Intent(getApplicationContext(),ChangeGroupImage_Activity.class);
                    GroupImageIntent.putExtra("Id",id);
                    GroupImageIntent.putExtra("image",image);
                    GroupImageIntent.putExtra("year",year);
                    startActivity(GroupImageIntent);}
            }
        });
       chatRelativeLayout.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               if(id.contains("groupId")){

                   Intent profileIntent=new Intent(getApplicationContext(),Group_Activity.class);
                   profileIntent.putExtra("key_noti",id);
                   startActivity(profileIntent);

               }

              else if(!(id.equals("cse")||id.equals("me")||id.equals("ec")||id.equals("it")||id.equals("ce")||id.equals("ee")||id.equals("ip")||id.contains("groupId")||id.contains("branchId"))){
               Intent profileIntent=new Intent(getApplicationContext(),UserProfile_Activity.class);
               profileIntent.putExtra("key_noti",id);
               profileIntent.putExtra("request"," ");
               startActivity(profileIntent);}
               else{

                   Intent profileIntent=new Intent(getApplicationContext(),AllUsersOfABranch_Activity.class);
                   profileIntent.putExtra("branchKey",id);
                   profileIntent.putExtra("yearKey",year);
                   profileIntent.putExtra("collegeKey",college);
                   profileIntent.putExtra("fieldKey",fieldOfWork);
                   startActivity(profileIntent);


               }
           }
       });




        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String text=chatText.getText().toString();
                if(!TextUtils.isEmpty(text)) {
                    chatText.setText("");

                    try {
                        String encryptedString=encrypt(text,userId);

                        DatabaseReference message_push_ref=FirebaseDatabase.getInstance().getReference().child("messages").child(userId).child(id).push();
                        String message_push_key=message_push_ref.getKey();
                        String time=DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date())+" "+DateFormat.getDateInstance().format(new Date());

                        final Map saveUserText = new HashMap();
                        saveUserText.put("message", encryptedString);
                        saveUserText.put("seen", true);
                        saveUserText.put("image","null");
                        saveUserText.put("file","null");
                        saveUserText.put("timestamp",time.substring(0,time.length()-5));
                        saveUserText.put("type","text");
                        saveUserText.put("from",userId);
                        saveUserText.put("name","null");

                        final Map saveText = new HashMap();
                        saveText.put("message", encryptedString);
                        saveText.put("seen", false);
                        saveText.put("image","null");
                        saveText.put("file","null");
                        saveText.put("timestamp",time.substring(0,time.length()-5));
                        saveText.put("type","text");
                        saveText.put("from",userId);
                        saveText.put("name","null");

                        sendMessage(text,message_push_key,saveUserText,saveText);



                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            }
        });


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               final AlertDialog.Builder builder=new AlertDialog.Builder(ChatActivity.this);
                final LayoutInflater inflater=(LayoutInflater)ChatActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View actionbarView=inflater.inflate(R.layout.alert_dilog_layout,null);
                builder.setView(actionbarView);
                builder.show();
                final AlertDialog ad = builder.create();
                documents=actionbarView.findViewById(R.id.document);
                images=actionbarView.findViewById(R.id.images);
                video=actionbarView.findViewById(R.id.video);
                music=actionbarView.findViewById(R.id.music);




                images.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {



                       /* Intent i = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                        final int ACTIVITY_SELECT_IMAGE = 1234;
                        startActivityForResult(i, 1);*/
                        ad.cancel();
                        Intent intent=new Intent();
                        intent.setType("image/*");
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent,"select picture"),GALLARY_PIC);





                    }
                });


                documents.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ad.cancel();
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("*/*");
                        //intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 2);

                    }
                });

                video.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        ad.cancel();
                        Intent i = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Video.Media.INTERNAL_CONTENT_URI);
                        startActivityForResult(i, 3);


                    }
                });

                music.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ad.cancel();
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                       // intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("audio/mpeg");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 4);


                    }
                });




            }
        });






    }




    //sending images


    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if (requestCode == GALLARY_PIC&& resultCode == RESULT_OK) {

            if (data.getClipData()!=null){
                final int n=data.getClipData().getItemCount();
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                builder.setMessage("Add Message...");
                final EditText input = new EditText(ChatActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                builder.setView(input);
                builder.setPositiveButton("send", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                           for (int j=0;j<n;j++) {
                               final Uri imageUri = data.getClipData().getItemAt(0).getUri();
                               final Cursor returnCursor =
                                       getContentResolver().query(imageUri, null, null, null, null);

                               final int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                               int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                               returnCursor.moveToFirst();
                               final String imageName = returnCursor.getString(nameIndex);
                               String imageSize = returnCursor.getString(sizeIndex);
                               final String size = size(Integer.parseInt(imageSize));
                               final String imagePushkey = mrootRef.push().getKey();
                               String m = null;

                               try {
                                   if (input.getText().toString().length() > 20) {
                                       lMessage = input.getText().toString().substring(17) + "..*image";
                                   } else lMessage = input.getText().toString() + "..*image";
                                   m = encrypt(input.getText().toString(), userId);

                                   String time = DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date()) + " " + DateFormat.getDateInstance().format(new Date());

                                   final Map saveUserMessage = new HashMap();
                                   saveUserMessage.put("message", m);
                                   saveUserMessage.put("image", "https://firebasestorage.googleapis.com/v0/b/jecchat-bcd82.appspot.com/o/images.jpeg?alt=media&token=5cdc5b9b-6d05-4221-947e-80a7c5af8b89");
                                   saveUserMessage.put("file", "null");
                                   saveUserMessage.put("seen", true);
                                   saveUserMessage.put("timestamp", "uploading and sending message please wait" + " " + size);
                                   saveUserMessage.put("type", "image");
                                   saveUserMessage.put("from", userId);
                                   saveUserMessage.put("name", imageName);
                                   mrootRef.child("messages").child(userId).child(id).child(imagePushkey).setValue(saveUserMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
                                       @Override
                                       public void onComplete(@NonNull Task<Void> task) {
                                           if (task.isSuccessful()) {

                                               StorageReference thumbStorage = mImageRef.child("imageMessage").child(userId).child(id).child("thumb").child(imagePushkey).child(ServerValue.TIMESTAMP + "jpg");
                                               thumbStorage.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                   @Override
                                                   public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                                       if (task.isSuccessful()) {

                                                           final String imageUrl = task.getResult().getDownloadUrl().toString();

                                                           String uri = downloadUserData(imageUrl, imageName, imagePushkey, "image");
                                                           String message = input.getText().toString();
                                                           if (message.equals(null))
                                                               message = "   ";
                                                           String m = null;
                                                           try {

                                                               m = encrypt(message, userId);
                                                               String time = DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date()) + " " + DateFormat.getDateInstance().format(new Date());

                                                               final Map saveUserMessage = new HashMap();
                                                               saveUserMessage.put("message", m);
                                                               saveUserMessage.put("image", uri);
                                                               saveUserMessage.put("file", "null");
                                                               saveUserMessage.put("seen", true);
                                                               saveUserMessage.put("timestamp", time.substring(0, time.length() - 5) + " " + size);
                                                               saveUserMessage.put("type", "image");
                                                               saveUserMessage.put("from", userId);
                                                               saveUserMessage.put("name", imageName);

                                                               final Map savefriendMessage = new HashMap();
                                                               savefriendMessage.put("message", m);
                                                               savefriendMessage.put("image", imageUrl);
                                                               savefriendMessage.put("file", "null");
                                                               savefriendMessage.put("seen", false);
                                                               savefriendMessage.put("timestamp", time.substring(0, time.length() - 5) + " " + size);
                                                               savefriendMessage.put("type", "image");
                                                               savefriendMessage.put("from", userId);
                                                               savefriendMessage.put("name", imageName);

                                                               sendMessage(lMessage, imagePushkey, saveUserMessage, savefriendMessage);


                                                           } catch (Exception e) {
                                                               e.printStackTrace();

                                                           }


                                                       } else {
                                                           Toast.makeText(ChatActivity.this, "faild to send", Toast.LENGTH_SHORT).show();

                                                       }

                                                   }
                                               });


                                           }
                                       }
                                   });
                               } catch (Exception e) {
                                   e.printStackTrace();
                               }
                           }

                        }
                });

                    builder.show();

            }
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                builder.setMessage("Add Message...");
                final EditText input = new EditText(ChatActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                builder.setView(input);
                final Uri imageUri = data.getData();
                final Cursor returnCursor =
                        getContentResolver().query(imageUri, null, null, null, null);

                final int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                returnCursor.moveToFirst();
                final String imageName = returnCursor.getString(nameIndex);
                String imageSize = returnCursor.getString(sizeIndex);
                final String size = size(Integer.parseInt(imageSize));


                final String imagePushkey = mrootRef.push().getKey();


                builder.setPositiveButton("send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String m = null;

                        try {
                            if (input.getText().toString().length() > 20) {
                                lMessage = input.getText().toString().substring(17) + "..*image";
                            } else lMessage = input.getText().toString() + "..*image";
                            m = encrypt(input.getText().toString(), userId);

                            String time = DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date()) + " " + DateFormat.getDateInstance().format(new Date());

                            final Map saveUserMessage = new HashMap();
                            saveUserMessage.put("message", m);
                            saveUserMessage.put("image", "https://firebasestorage.googleapis.com/v0/b/jecchat-bcd82.appspot.com/o/images.jpeg?alt=media&token=5cdc5b9b-6d05-4221-947e-80a7c5af8b89");
                            saveUserMessage.put("file", "null");
                            saveUserMessage.put("seen", true);
                            saveUserMessage.put("timestamp", "uploading and sending message please wait" + " " + size);
                            saveUserMessage.put("type", "image");
                            saveUserMessage.put("from", userId);
                            saveUserMessage.put("name", imageName);
                            mrootRef.child("messages").child(userId).child(id).child(imagePushkey).setValue(saveUserMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        StorageReference thumbStorage = mImageRef.child("imageMessage").child(userId).child(id).child("thumb").child(imagePushkey).child(ServerValue.TIMESTAMP + "jpg");
                                        thumbStorage.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                                if (task.isSuccessful()) {

                                                    final String imageUrl = task.getResult().getDownloadUrl().toString();

                                                    String uri = downloadUserData(imageUrl, imageName, imagePushkey, "image");
                                                    String message = input.getText().toString();
                                                    if (message.equals(null))
                                                        message = "   ";
                                                    String m = null;
                                                    try {

                                                        m = encrypt(message, userId);
                                                        String time = DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date()) + " " + DateFormat.getDateInstance().format(new Date());

                                                        final Map saveUserMessage = new HashMap();
                                                        saveUserMessage.put("message", m);
                                                        saveUserMessage.put("image", uri);
                                                        saveUserMessage.put("file", "null");
                                                        saveUserMessage.put("seen", true);
                                                        saveUserMessage.put("timestamp", time.substring(0, time.length() - 5) + " " + size);
                                                        saveUserMessage.put("type", "image");
                                                        saveUserMessage.put("from", userId);
                                                        saveUserMessage.put("name", imageName);

                                                        final Map savefriendMessage = new HashMap();
                                                        savefriendMessage.put("message", m);
                                                        savefriendMessage.put("image", imageUrl);
                                                        savefriendMessage.put("file", "null");
                                                        savefriendMessage.put("seen", false);
                                                        savefriendMessage.put("timestamp", time.substring(0, time.length() - 5) + " " + size);
                                                        savefriendMessage.put("type", "image");
                                                        savefriendMessage.put("from", userId);
                                                        savefriendMessage.put("name", imageName);

                                                        sendMessage(lMessage, imagePushkey, saveUserMessage, savefriendMessage);


                                                    } catch (Exception e) {
                                                        e.printStackTrace();

                                                    }


                                                } else {
                                                    Toast.makeText(ChatActivity.this, "faild to send", Toast.LENGTH_SHORT).show();

                                                }

                                            }
                                        });


                                    }
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });

                builder.show();
            }




        }






        if (requestCode == 2 && resultCode == RESULT_OK) {

             final Uri pdfUri = data.getData();

            final Cursor returnCursor =
                    getContentResolver().query(pdfUri, null, null, null, null);

            final int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
            returnCursor.moveToFirst();
            final String fileName=returnCursor.getString(nameIndex);
            final String fileSize=returnCursor.getString(sizeIndex);
            final String size=size(Integer.parseInt(fileSize));

            final String pdfPushkey=mrootRef.push().getKey();

            String time=DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date())+" "+DateFormat.getDateInstance().format(new Date());
            String encrName= null;
            try {if (fileName.length()>20){
                lMessage=fileName.substring(17)+"...*file";}
            else lMessage=fileName+"..*file";
                encrName = encrypt(fileName,userId);
                final Map saveUserMessage=new HashMap();
                saveUserMessage.put("message",encrName);
                saveUserMessage.put("file","null");
                saveUserMessage.put("image","null" );
                saveUserMessage.put("seen", true);
                saveUserMessage.put("timestamp", "sending please wait"+" "+size);
                saveUserMessage.put("type","file");
                saveUserMessage.put("from",userId);
                saveUserMessage.put("name",fileName);
                mrootRef.child("messages").child(userId).child(id).child(pdfPushkey).setValue(saveUserMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                            StorageReference ref = mImageRef.child("pdfMessage").child(userId).child(id).child(pdfPushkey);

                            ref.putFile(pdfUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if (task.isSuccessful()) {

                                        String pdfUrl=task.getResult().getDownloadUrl().toString();

                                        String uri=downloadUserData(pdfUrl,fileName,pdfPushkey,"file");
                                        try {

                                            String time=DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date())+" "+DateFormat.getDateInstance().format(new Date());
                                            String encrName=encrypt(fileName,userId);
                                            final Map saveUserMessage=new HashMap();
                                            saveUserMessage.put("message",encrName);
                                            saveUserMessage.put("file",uri );
                                            saveUserMessage.put("image","null" );
                                            saveUserMessage.put("seen", true);
                                            saveUserMessage.put("timestamp", time.substring(0,time.length()-5)+" "+size);
                                            saveUserMessage.put("type","file");
                                            saveUserMessage.put("from",userId);
                                            saveUserMessage.put("name",fileName);

                                            final Map savefriendMessage=new HashMap();
                                            savefriendMessage.put("message",encrName);
                                            savefriendMessage.put("file",pdfUrl );
                                            savefriendMessage.put("image","null" );
                                            savefriendMessage.put("seen", false);
                                            savefriendMessage.put("timestamp", time.substring(0,time.length()-5)+" "+size);
                                            savefriendMessage.put("type","file");
                                            savefriendMessage.put("from",userId);
                                            savefriendMessage.put("name",fileName);

                                            sendMessage(lMessage,pdfPushkey,saveUserMessage,savefriendMessage);



                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Toast.makeText(ChatActivity.this, "exception", Toast.LENGTH_SHORT).show();

                                            // progressDialog.dismiss();
                                        }






                                    } else {
                                        Toast.makeText(ChatActivity.this, "faild to upload in storage", Toast.LENGTH_SHORT).show();
                                       // progressDialog.dismiss();
                                    }
                                }
                            });



                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }



        }

        if(requestCode==3&&resultCode==RESULT_OK){


           AlertDialog.Builder builder=new AlertDialog.Builder(ChatActivity.this);
            builder.setMessage("Add Message...");
            final EditText input = new EditText(ChatActivity.this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            builder.setView(input);
            final Uri videoUri = data.getData();

            final Cursor returnCursor =
                    getContentResolver().query(videoUri, null, null, null, null);

            final int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
            returnCursor.moveToFirst();
            final String fileName=returnCursor.getString(nameIndex);
            final String fileSize=returnCursor.getString(sizeIndex);
            final String size=size(Integer.parseInt(fileSize));


            final String videoPushkey=mrootRef.push().getKey();


           builder.setPositiveButton("send", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialogInterface, int i) {

                   String time=DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date())+" "+DateFormat.getDateInstance().format(new Date());
                   String encrName= null;
                   try {
                       if (input.getText().toString().length()>20){
                           lMessage=input.getText().toString().substring(17)+"..*video";}
                       else lMessage=input.getText().toString()+"..*video";
                       encrName = encrypt(input.getText().toString(),userId);

                       final Map saveUserMessage=new HashMap();
                       saveUserMessage.put("message",encrName);
                       saveUserMessage.put("file","null" );
                       saveUserMessage.put("image","null" );
                       saveUserMessage.put("seen", true);
                       saveUserMessage.put("timestamp", "sending please wait"+" "+size);
                       saveUserMessage.put("type","video");
                       saveUserMessage.put("from",userId);
                       saveUserMessage.put("name",fileName);

                       mrootRef.child("messages").child(userId).child(id).child(videoPushkey).setValue(saveUserMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                               if (task.isSuccessful()){


                                   StorageReference ref = mImageRef.child("videoMessage").child(userId).child(id).child(videoPushkey);

                                   ref.putFile(videoUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                       @Override
                                       public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                           if (task.isSuccessful()) {

                                               String videoUrl=task.getResult().getDownloadUrl().toString();

                                               try {

                                                   String uri=downloadUserData(videoUrl,fileName,videoPushkey,"file");
                                                   String time=DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date())+" "+DateFormat.getDateInstance().format(new Date());
                                                   String encrName=encrypt(input.getText().toString(),userId);
                                                   final Map saveUserMessage=new HashMap();
                                                   saveUserMessage.put("message",encrName);
                                                   saveUserMessage.put("file",uri );
                                                   saveUserMessage.put("image","null" );
                                                   saveUserMessage.put("seen", true);
                                                   saveUserMessage.put("timestamp", time.substring(0,time.length()-5)+" "+size);
                                                   saveUserMessage.put("type","video");
                                                   saveUserMessage.put("from",userId);
                                                   saveUserMessage.put("name",fileName);

                                                   final Map savefriendMessage=new HashMap();
                                                   savefriendMessage.put("message",encrName);
                                                   savefriendMessage.put("file",videoUrl );
                                                   savefriendMessage.put("image","null" );
                                                   savefriendMessage.put("seen", false);
                                                   savefriendMessage.put("timestamp", time.substring(0,time.length()-5)+" "+size);
                                                   savefriendMessage.put("type","video");
                                                   savefriendMessage.put("from",userId);
                                                   savefriendMessage.put("name",fileName);

                                                   sendMessage(lMessage,videoPushkey,saveUserMessage,savefriendMessage);



                                               } catch (Exception e) {
                                                   e.printStackTrace();
                                                   Toast.makeText(ChatActivity.this, "exception", Toast.LENGTH_SHORT).show();

                                                   // progressDialog.dismiss();
                                               }






                                           } else {
                                               Toast.makeText(ChatActivity.this, "faild to upload in storage", Toast.LENGTH_SHORT).show();
                                               progressDialog.dismiss();
                                           }
                                       }
                                   });





                               }


                           }
                       });
                   } catch (Exception e) {
                       e.printStackTrace();
                   }



               }
           });
            builder.show();
        }


        if(requestCode==4&&resultCode==RESULT_OK){


            AlertDialog.Builder builder=new AlertDialog.Builder(ChatActivity.this);
            builder.setMessage("Add Message...");
            final EditText input = new EditText(ChatActivity.this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            builder.setView(input);
            final Uri videoUri = data.getData();

            final Cursor returnCursor =
                    getContentResolver().query(videoUri, null, null, null, null);

            final int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
            returnCursor.moveToFirst();
            final String fileName=returnCursor.getString(nameIndex);
            final String fileSize=returnCursor.getString(sizeIndex);
            final String size=size(Integer.parseInt(fileSize));


            final String audioPushkey=mrootRef.push().getKey();


            builder.setPositiveButton("send", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    String time=DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date())+" "+DateFormat.getDateInstance().format(new Date());
                    String encrName= null;
                    try {
                        if (input.getText().toString().length()>20){
                            lMessage=input.getText().toString().substring(17)+"..*audio";}
                        else lMessage=input.getText().toString()+"..*audio";
                        encrName = encrypt(input.getText().toString(),userId);

                        final Map saveUserMessage=new HashMap();
                        saveUserMessage.put("message",encrName);
                        saveUserMessage.put("file","null" );
                        saveUserMessage.put("image","null" );
                        saveUserMessage.put("seen", true);
                        saveUserMessage.put("timestamp", "sending please wait"+" "+size);
                        saveUserMessage.put("type","audio");
                        saveUserMessage.put("from",userId);
                        saveUserMessage.put("name",fileName);

                        mrootRef.child("messages").child(userId).child(id).child(audioPushkey).setValue(saveUserMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){


                                    StorageReference ref = mImageRef.child("audioMessage").child(userId).child(id).child(audioPushkey);

                                    ref.putFile(videoUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if (task.isSuccessful()) {

                                                String audioUrl=task.getResult().getDownloadUrl().toString();

                                                try {

                                                    String uri=downloadUserData(audioUrl,fileName,audioPushkey,"file");
                                                    String time=DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date())+" "+DateFormat.getDateInstance().format(new Date());
                                                    String encrName=encrypt(input.getText().toString(),userId);
                                                    final Map saveUserMessage=new HashMap();
                                                    saveUserMessage.put("message",encrName);
                                                    saveUserMessage.put("file",uri );
                                                    saveUserMessage.put("image","null" );
                                                    saveUserMessage.put("seen", true);
                                                    saveUserMessage.put("timestamp", time.substring(0,time.length()-5)+" "+size);
                                                    saveUserMessage.put("type","audio");
                                                    saveUserMessage.put("from",userId);
                                                    saveUserMessage.put("name",fileName);

                                                    final Map savefriendMessage=new HashMap();
                                                    savefriendMessage.put("message",encrName);
                                                    savefriendMessage.put("file",audioUrl );
                                                    savefriendMessage.put("image","null" );
                                                    savefriendMessage.put("seen", false);
                                                    savefriendMessage.put("timestamp", time.substring(0,time.length()-5)+" "+size);
                                                    savefriendMessage.put("type","audio");
                                                    savefriendMessage.put("from",userId);
                                                    savefriendMessage.put("name",fileName);

                                                    sendMessage(lMessage,audioPushkey,saveUserMessage,savefriendMessage);



                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    Toast.makeText(ChatActivity.this, "exception", Toast.LENGTH_SHORT).show();

                                                    // progressDialog.dismiss();
                                                }






                                            } else {
                                                Toast.makeText(ChatActivity.this, "faild to upload in storage", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            }
                                        }
                                    });





                                }


                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }



                }
            });
            builder.show();








        }



    }


    private void sendMessage(final String text, String message_push_key, Map saveUserText, final Map saveText){


        Map chatMapUser=new HashMap();
        chatMapUser.put("seen",true);
        chatMapUser.put("lastMessage",text);
        chatMapUser.put("timeStamp",ServerValue.TIMESTAMP);

        if(id.contains("groupId")){
            Map map = new HashMap();
            map.put("messages/" + userId + "/" + id + "/" + message_push_key, saveUserText);
            map.put("chat/" + userId + "/" + id ,chatMapUser);

            mrootRef.child("group").child(id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    userName=dataSnapshot.child("name").getValue().toString();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            mrootRef.child("group").child(id).child("users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(final DataSnapshot childSnapshot:dataSnapshot.getChildren()){
                        if(!childSnapshot.getKey().equals(userId)) {
                            mrootRef.child("messages").child(childSnapshot.getKey()).child(id).push().setValue(saveText).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Map chatMapFriend=new HashMap();
                                        chatMapFriend.put("seen",false);
                                        chatMapFriend.put("lastMessage",text);
                                        chatMapFriend.put("timeStamp",ServerValue.TIMESTAMP);

                                        mrootRef.child("chat").child(childSnapshot.getKey()).child(id).setValue(chatMapFriend).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){

                                                    HashMap notiHash = new HashMap<>();
                                                    notiHash.put("from", userId);
                                                    notiHash.put("type", userName+": new message received");
                                                    notiHash.put("key", id);
                                                    notiHash.put("clickAction","JecChat.Message.Notification");
                                                    notiHash.put("time", ServerValue.TIMESTAMP);
                                                    mrootRef.child("notifications").child(childSnapshot.getKey()).child(id+" Message").setValue(notiHash);

                                                }
                                            }
                                        });

                                    }
                                }
                            });





                        }

                    }
                    MediaPlayer mp=MediaPlayer.create(getApplicationContext(),R.raw.intuition);
                    mp.start();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            mrootRef.updateChildren(map, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null)
                        Log.i("error sendingText", databaseError.getMessage());
                }
            });

            chatText.setText("");



        }

        else if(!(id.equals("cse")||id.equals("me")||id.equals("ec")||id.equals("it")||id.equals("ce")||id.equals("ee")||id.equals("ip")||id.contains("groupId")||id.contains("branchId"))) {

            Map chatMapFriend=new HashMap();
            chatMapFriend.put("seen",false);
            chatMapFriend.put("lastMessage",text);
            chatMapFriend.put("timeStamp",ServerValue.TIMESTAMP);

            Map map = new HashMap();
            map.put("messages/" + userId + "/" + id + "/" + message_push_key, saveUserText);
            map.put("messages/" + id + "/" + userId + "/" + message_push_key, saveText);
            map.put("chat/" + userId + "/" + id ,chatMapUser);
            map.put("chat/"+id+"/"+userId,chatMapFriend);

            mrootRef.updateChildren(map, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError == null)
                    {
                        HashMap notiHash = new HashMap<>();
                        notiHash.put("from", userId);
                        notiHash.put("type", userName+" has sent you message");
                        notiHash.put("key", userId);
                        notiHash.put("clickAction","JecChat.Message.Notification");
                        notiHash.put("time", ServerValue.TIMESTAMP);
                        mrootRef.child("notifications").child(id).child(userId+" Message").setValue(notiHash);
                        MediaPlayer mp=MediaPlayer.create(getApplicationContext(),R.raw.intuition);
                        mp.start();
                    }



                }
            });

            chatText.setText("");
        }

        else{

            Map map = new HashMap();
            map.put("messages/" + userId + "/" + id + "/" + message_push_key, saveUserText);
            map.put("chat/"+userId+"/"+id+"/"+"seen",false);
            map.put("chat/"+userId+"/"+id+"/"+"lastMessage",text);
            map.put("chat/"+userId+"/"+id+"/"+"timeStamp",ServerValue.TIMESTAMP);
            mrootRef.updateChildren(map, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError == null) {
                        mrootRef.child("college").child(college).child("users").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(final DataSnapshot childSnapshot:dataSnapshot.getChildren()){
                                    if(!childSnapshot.getKey().equals(userId)) {
                                        mrootRef.child("messages").child(childSnapshot.getKey()).child(id).push().setValue(saveText).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Map chatMapFriend=new HashMap();
                                                    chatMapFriend.put("chat/"+childSnapshot.getKey()+"/"+id+"/"+"seen",false);
                                                    chatMapFriend.put("chat/"+childSnapshot.getKey()+"/"+id+"/"+"lastMessage",text);
                                                    chatMapFriend.put("chat/"+childSnapshot.getKey()+"/"+id+"/"+"timeStamp",ServerValue.TIMESTAMP);

                                                    mrootRef.updateChildren(chatMapFriend, new DatabaseReference.CompletionListener() {
                                                        @Override
                                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                            if (databaseError==null){
                                                                HashMap notiHash = new HashMap<>();
                                                                notiHash.put("from", userId);
                                                                notiHash.put("type", id+": new message received");
                                                                notiHash.put("key", id);
                                                                notiHash.put("clickAction","JecChat.Message.Notification");
                                                                notiHash.put("time", ServerValue.TIMESTAMP);
                                                                mrootRef.child("notifications").child(childSnapshot.getKey()).child(id+" Message").setValue(notiHash);

                                                            }
                                                        }
                                                    });


                                                }
                                            }
                                        });
                                    }

                                }
                                MediaPlayer mp=MediaPlayer.create(getApplicationContext(),R.raw.intuition);
                                mp.start();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                }
            });









        }

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



    protected void showChat() {


        mrootRef.child("users").child(userId).child("online").setValue("true");
        mRef.keepSynced(true);
        if(!(id.equals("cse")||id.equals("me")||id.equals("ec")||id.equals("it")||id.equals("ce")||id.equals("ee")||id.equals("ip")||id.contains("groupId")||id.contains("branchId"))) {
               FirebaseRecyclerOptions<Message> options =
                       new FirebaseRecyclerOptions.Builder<Message>()
                               .setQuery(mRef, Message.class)
                               .build();
               firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Message, ChatViewHolder>(options)  {
                   @Override
                   public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                       View view = LayoutInflater.from(parent.getContext())
                               .inflate(R.layout.user_message_layout, parent, false);

                       return new ChatViewHolder(view);
                   }

                   @Override
                   protected void onBindViewHolder(@NonNull final ChatViewHolder viewHolder, final int position, @NonNull final Message model) {
                       final String refKey=getRef(position).getKey();
                       final Picasso picasso;
                       picasso=Picasso.with(viewHolder.itemView.getContext());
                       picasso.setIndicatorsEnabled(false);
                       FirebaseDatabase.getInstance().getReference().child("users").child(model.getFrom()).addValueEventListener(new ValueEventListener() {
                           @Override
                           public void onDataChange(DataSnapshot dataSnapshot) {

                               name=dataSnapshot.child("name").getValue().toString();

                           }

                           @Override
                           public void onCancelled(DatabaseError databaseError) {

                           }
                       });


                       try {
                           viewHolder.messageText1.setText(decrypt(model.getMessage(),model.getFrom()));
                           viewHolder.messageText.setText(decrypt(model.getMessage(),model.getFrom()));
                       } catch (Exception e) {
                           e.printStackTrace();
                       }
                       viewHolder.timeView1.setText(model.getTimestamp());
                       viewHolder.timeView.setText(model.getTimestamp());
                       viewHolder.seen.setVisibility(View.INVISIBLE);


                       mrootRef.child("chat").child(userId).child(id).child("seen").setValue(true);
                       mrootRef.child("users").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                           @Override
                           public void onDataChange(DataSnapshot dataSnapshot) {
                               String online = dataSnapshot.child("online").getValue().toString();


                               if (online.equals("true"))
                                   lastSeen.setText("online");
                               else {

                                   // TimeAgo timeAgo=new TimeAgo();
                                   long seenTime = Long.parseLong(online);
                                   String lastSeenTime = TimeAgo.getTimeAgo(seenTime, getApplicationContext());
                                   lastSeen.setText(lastSeenTime);
                               }

                           }

                           @Override
                           public void onCancelled(DatabaseError databaseError) {

                           }
                       });

                     mrootRef.child("chat").child(id).child(userId).addValueEventListener(new ValueEventListener() {
                         @Override
                         public void onDataChange(DataSnapshot dataSnapshot) {

                             String s=dataSnapshot.child("seen").getValue().toString();
                             String msg= dataSnapshot.child("lastMessage").getValue().toString();
                             String message;
                             try {
                                 message = decrypt(model.getMessage(),userId);
                                 if (message.length()>20){

                                     if (message.equals(msg)||msg.equals(message.substring(17)+"..*image") ||msg.equals(message.substring(17)+"..*video")||msg.equals(message.substring(17)+"..*file")||msg.equals(message.substring(17)+"..*audio")){
                                         viewHolder.seen.setVisibility(View.VISIBLE);
                                         if (s.equals("true")){
                                             viewHolder.seen.setText("seen");
                                         }
                                         else if (s.equals("false"))
                                             viewHolder.seen.setText("sent");

                                         else viewHolder.seen.setText("sending");
                                     }
                                     }
                                 else if (message.equals(msg)||msg.equals(message+"..*image") ||msg.equals(message+"..*video")||msg.equals(message+"..*file")||msg.equals(message+"..*audio")){
                                         viewHolder.seen.setVisibility(View.VISIBLE);
                                         if (s.equals("true")){
                                             viewHolder.seen.setText("seen");
                                         }
                                         else if (s.equals("false"))
                                             viewHolder.seen.setText("sent");

                                         else viewHolder.seen.setText("sending");
                                 }

                                 else viewHolder.seen.setVisibility(View.INVISIBLE);
                             }



                              catch (Exception e) {
                                 e.printStackTrace();
                              }


                         }

                         @Override
                         public void onCancelled(DatabaseError databaseError) {

                         }
                     });




                       picasso.load(model.getImage()).resize(1000, 1000).centerCrop().networkPolicy(NetworkPolicy.OFFLINE).into(viewHolder.imageMessage1, new Callback() {
                               @Override
                               public void onSuccess() {

                               }

                               @Override
                               public void onError() {
                                   picasso.load(model.getImage()).resize(1000, 1000).centerCrop().into(viewHolder.imageMessage1);
                               }
                       });
                       picasso.load(model.getImage()).resize(1000, 1000).centerCrop().networkPolicy(NetworkPolicy.OFFLINE).into(viewHolder.imageMessage, new Callback() {
                               @Override
                               public void onSuccess() {

                               }

                               @Override
                               public void onError() {
                                   picasso.load(model.getImage()).resize(1000, 1000).centerCrop().into(viewHolder.imageMessage);
                               }
                       });

                       if(model.getType().equals("file")||model.getType().equals("video")||model.getType().equals("audio")){
                           final int dr;
                           String message= null;
                           try {
                               message = decrypt(model.getMessage(),model.getFrom()).substring(decrypt(model.getMessage(),model.getFrom()).lastIndexOf("/")+1);
                           } catch (Exception e) {
                               e.printStackTrace();
                           }
                           viewHolder.messageText.setText(message);
                           viewHolder.messageText1.setText(message);
                           if(model.getName().contains("pdf")){
                               dr=R.drawable.pdf;
                           }
                           else if(model.getName().contains("doc")){
                               dr=R.drawable.doc;
                           }
                           else if(model.getName().contains("ppt"))
                           {dr=R.drawable.ppt;}
                           else if(model.getName().contains("mp4")){
                               dr =R.drawable.mp4;
                           }
                           else if(model.getName().contains("zip"))
                           {dr=R.drawable.zip;}
                           else if(model.getName().contains("txt"))
                           {dr=R.drawable.txt;}
                           else if(model.getName().contains("avi"))
                           {dr=R.drawable.avi;}
                           else if(model.getName().contains("html"))
                           {dr=R.drawable.html;}
                           else if(model.getName().contains("raw"))
                           {dr=R.drawable.raw;}
                           else if(model.getName().contains("gif"))
                           {dr=R.drawable.gif;}
                           else dr=R.drawable.folder;
                           picasso.load(dr).resize(70,70).centerCrop().networkPolicy(NetworkPolicy.OFFLINE).into(viewHolder.imageMessage, new Callback() {
                               @Override
                               public void onSuccess() {

                               }

                               @Override
                               public void onError() {
                                   picasso.load(dr).resize(70,70).centerCrop().into(viewHolder.imageMessage);
                               }
                           });

                           picasso.load(dr).resize(70,70).centerCrop().networkPolicy(NetworkPolicy.OFFLINE).into(viewHolder.imageMessage1, new Callback() {
                               @Override
                               public void onSuccess() {

                               }

                               @Override
                               public void onError() {
                                   picasso.load(dr).resize(70,70).centerCrop().into(viewHolder.imageMessage1);
                               }
                           });


                       }

                       viewHolder.video.setVisibility(View.INVISIBLE);
                       viewHolder.video1.setVisibility(View.INVISIBLE);
                       viewHolder.play.setVisibility(View.INVISIBLE);
                       viewHolder.play1.setVisibility(View.INVISIBLE);

                       /*if(model.getType().equals("video")){
                           viewHolder.video.setVisibility(View.VISIBLE);
                           viewHolder.video1.setVisibility(View.VISIBLE);
                           viewHolder.play.setVisibility(View.VISIBLE);
                           viewHolder.play1.setVisibility(View.VISIBLE);
                           viewHolder.imageMessage.setVisibility(View.INVISIBLE);
                           viewHolder.imageMessage1.setVisibility(View.INVISIBLE);
                           viewHolder.video1.setVideoURI(Uri.parse(model.getFile()));

                           RelativeLayout.LayoutParams layoutParams1 = (RelativeLayout.LayoutParams) viewHolder.video1.getLayoutParams();
                           layoutParams1.setMargins(0, 0, 0, 0);



                           picasso.load(R.drawable.user).resize(1000,1000).centerCrop().networkPolicy(NetworkPolicy.OFFLINE).into(viewHolder.imageMessage, new Callback() {
                               @Override
                               public void onSuccess() {

                               }

                               @Override
                               public void onError() {
                                   picasso.load(R.drawable.user).resize(1000,1000).centerCrop().into(viewHolder.imageMessage);
                               }
                           });

                           viewHolder.video.setVideoURI(Uri.parse(model.getFile()));
                           RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewHolder.video.getLayoutParams();
                           layoutParams.setMargins(0, 0, 0, 0);


                           picasso.load(R.drawable.user).resize(1000,1000).centerCrop().networkPolicy(NetworkPolicy.OFFLINE).into(viewHolder.imageMessage1, new Callback() {
                               @Override
                               public void onSuccess() {

                               }

                               @Override
                               public void onError() {
                                   picasso.load(R.drawable.user).resize(1000,1000).centerCrop().into(viewHolder.imageMessage1);
                               }
                           });
                       }

                       viewHolder.play1.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View view) {
                               try {
                                   OpenFile.DownLoadFile(getApplicationContext(),Uri.parse(model.getFile()));
                               } catch (IOException e) {
                                   e.printStackTrace();
                               }
                           }
                       });

                       viewHolder.play.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View view) {
                               if ((!(model.isSeen())&&!model.getFrom().equals(userId))){
                                   try {
                                       download(model.getFile(),model.getName(),refKey,"file");

                                   } catch (Exception e) {
                                       e.printStackTrace();
                                   }
                               }
                               else {

                                   try {
                                       OpenFile.DownLoadFile(getApplicationContext(),Uri.parse(model.getFile()));
                                   } catch (IOException e) {
                                       e.printStackTrace();
                                   }

                               }
                           }
                       });*/


                       viewHolder.imageMessage.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {

                               if(model.getType().equals("image")) {



                                   if ((!(model.isSeen())&&!model.getFrom().equals(userId))){
                                       try {viewHolder.userProgressBar.setVisibility(View.VISIBLE);
                                           download(model.getImage(),model.getName(),refKey,"image",viewHolder.userProgressBar,id,model.getFrom());

                                       } catch (Exception e) {
                                           e.printStackTrace();
                                       }
                                   }
                                   else {

                                       try {
                                           OpenFile.DownLoadFile(getApplicationContext(),Uri.parse(model.getImage()));
                                       } catch (IOException e) {
                                           e.printStackTrace();
                                       }
                                   }

                               }
                               else if(model.getType().equals("file")||model.getType().equals("video")||model.getType().equals("audio")){


                                   if (!model.isSeen()){
                                           try {viewHolder.userProgressBar.setVisibility(View.VISIBLE);
                                               download(model.getFile(),model.getName(),refKey,"file",viewHolder.userProgressBar,id,model.getFrom());

                                           } catch (Exception e) {
                                               e.printStackTrace();
                                           }

                                       }

                                       else {



                                           try {
                                               OpenFile.DownLoadFile(getApplicationContext(),Uri.parse(model.getFile()));
                                           } catch (IOException e) {
                                               e.printStackTrace();
                                           }
                                       }
                               }
                           }
                       });

                       viewHolder.imageMessage1.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {

                               if(model.getType().equals("image")) {

                                   if ((!(model.isSeen())&&!model.getFrom().equals(userId))){
                                       try {viewHolder.userProgressBar.setVisibility(View.VISIBLE);
                                           download(model.getImage(),model.getName(),refKey,"image",viewHolder.userProgressBar,id,model.getFrom());


                                       } catch (Exception e) {
                                           e.printStackTrace();
                                       }
                                   }
                                   else {

                                       try {
                                           OpenFile.DownLoadFile(getApplicationContext(),Uri.parse(model.getImage()));
                                       } catch (IOException e) {
                                           e.printStackTrace();
                                       }

                                   }
                               }
                               else
                               if(model.getType().equals("file")||model.getType().equals("video")||model.getType().equals("audio")){


                                   if ((!(model.isSeen())&&!model.getFrom().equals(userId))){
                                      try {viewHolder.userProgressBar.setVisibility(View.VISIBLE);
                                          download(model.getFile(),model.getName(),refKey,"file",viewHolder.userProgressBar,id,model.getFrom());

                                      } catch (Exception e) {
                                          e.printStackTrace();
                                      }
                                   }
                                   else {

                                      try {
                                          OpenFile.DownLoadFile(getApplicationContext(),Uri.parse(model.getFile()));
                                      } catch (IOException e) {
                                          e.printStackTrace();
                                      }

                                  }

                               }
                           }
                       });


                       if(model.getFrom().equals(userId)){
                           viewHolder.user1Layout.setVisibility(View.VISIBLE);
                           viewHolder.user2Layout.setVisibility(View.INVISIBLE);


                       }

                       else
                       {
                           viewHolder.user1Layout.setVisibility(View.INVISIBLE);
                           viewHolder.user2Layout.setVisibility(View.VISIBLE);

                       }



                       viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                           @Override
                           public boolean onLongClick(View v) {


                               AlertDialog.Builder builder=new AlertDialog.Builder(ChatActivity.this);
                               builder.setMessage("Do you want to delete this message:");
                               builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog, int which) {



                                       mRef.child(refKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task) {
                                               if(task.isSuccessful()) {
                                                   Toast.makeText(ChatActivity.this, "message deleted", Toast.LENGTH_LONG).show();

                                               }
                                           }
                                       });
                                   }
                               });
                               builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog, int which) {

                                   }
                               });
                               builder.show();




                               return true;
                           }
                       });
                   }


               };
               firebaseRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    int friendlyMessageCount = firebaseRecyclerAdapter.getItemCount();
                    int lastVisiblePosition =
                            linearLayout.findLastCompletelyVisibleItemPosition();
                    // If the recycler view is initially being loaded or the
                    // user is at the bottom of the list, scroll to the bottom
                    // of the list to show the newly added message.
                    if (lastVisiblePosition == -1 ||
                            (positionStart >= (friendlyMessageCount - 1) &&
                                    lastVisiblePosition == (positionStart - 1))) {
                        messageList.scrollToPosition(positionStart);
                    }
                }
            });
               firebaseRecyclerAdapter.startListening();
            messageList.setAdapter(firebaseRecyclerAdapter);

        }
       else {
               FirebaseRecyclerOptions<Message> options =
                       new FirebaseRecyclerOptions.Builder<Message>()
                               .setQuery(mRef, Message.class)
                               .build();
              firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Message, ChatViewHolder1>(options) {
                   @Override
                   public ChatViewHolder1 onCreateViewHolder(ViewGroup parent, int viewType) {
                       View view = LayoutInflater.from(parent.getContext())
                               .inflate(R.layout.group_message_layout, parent, false);

                       return new ChatViewHolder1(view);
                   }

                   @Override
                   protected void onBindViewHolder(@NonNull final ChatViewHolder1 viewHolder, final int position, @NonNull final Message model) {
                       final String refKey=getRef(position).getKey();
                       final Picasso picasso;
                       picasso=Picasso.with(viewHolder.itemView.getContext());
                       picasso.setIndicatorsEnabled(false);

                       FirebaseDatabase.getInstance().getReference().child("users").child(model.getFrom()).addValueEventListener(new ValueEventListener() {
                           @Override
                           public void onDataChange(DataSnapshot dataSnapshot) {

                               name=dataSnapshot.child("name").getValue().toString();
                               viewHolder.nameofSender.setText(name);
                           }

                           @Override
                           public void onCancelled(DatabaseError databaseError) {

                           }
                       });

                       try {
                           viewHolder.user_text.setText(decrypt(model.getMessage(),model.getFrom()));
                           viewHolder.group_text.setText(decrypt(model.getMessage(),model.getFrom()));
                       } catch (Exception e) {
                           e.printStackTrace();
                       }
                       viewHolder.user_time.setText(model.getTimestamp());
                       viewHolder.group_time.setText(model.getTimestamp());



                       picasso.load(model.getImage()).resize(1000, 1000).centerCrop().networkPolicy(NetworkPolicy.OFFLINE).into(viewHolder.user_image, new Callback() {
                               @Override
                               public void onSuccess() {

                               }

                               @Override
                               public void onError() {

                                   picasso.load(model.getImage()).resize(1000, 1000).into(viewHolder.user_image);

                               }
                       });
                       picasso.load(model.getImage()).resize(1000, 1000).centerCrop().networkPolicy(NetworkPolicy.OFFLINE).into(viewHolder.group_image, new Callback() {
                               @Override
                               public void onSuccess() {

                               }

                               @Override
                               public void onError() {

                                   picasso.load(model.getImage()).resize(1000, 1000).centerCrop().into(viewHolder.group_image);

                               }
                       });

                       if(model.getType().equals("file")||model.getType().equals("video")||model.getType().equals("audio")){
                           final int dr;

                           if (model.getName().contains(".pdf")) {
                               dr = R.drawable.pdf;
                           } else if (model.getName().contains(".doc")) {
                               dr = R.drawable.doc;
                           } else if (model.getName().contains(".ppt")) {
                               dr = R.drawable.ppt;
                           } else if(model.getName().contains(".mp4")||model.getName().contains(".m4a")){
                               dr =R.drawable.mp4;
                           }
                           else if(model.getName().contains(".zip"))
                           {dr=R.drawable.zip;}
                           else if(model.getName().contains(".txt"))
                           {dr=R.drawable.txt;}
                           else if(model.getName().contains(".avi"))
                           {dr=R.drawable.avi;}
                           else if(model.getName().contains(".html"))
                           {dr=R.drawable.html;}
                           else if(model.getName().contains(".raw"))
                           {dr=R.drawable.raw;}
                           else if(model.getName().contains(".gif"))
                           {dr=R.drawable.gif;}
                           else if(model.getName().contains(".mp3")||model.getName().contains(".m4a")||model.getName().contains(".wav"))
                           {dr=R.drawable.mp3;}
                           else dr = R.drawable.folder;
                           picasso.load(dr).resize(70, 70).centerCrop().networkPolicy(NetworkPolicy.OFFLINE).into(viewHolder.group_image, new Callback() {
                               @Override
                               public void onSuccess() {

                               }

                               @Override
                               public void onError() {
                                   picasso.load(dr).resize(70, 70).centerCrop().into(viewHolder.group_image);
                               }
                           });

                           picasso.load(dr).resize(70, 70).centerCrop().networkPolicy(NetworkPolicy.OFFLINE).into(viewHolder.user_image, new Callback() {
                               @Override
                               public void onSuccess() {

                               }

                               @Override
                               public void onError() {
                                   picasso.load(dr).resize(70, 70).centerCrop().into(viewHolder.user_image);
                               }
                           });
                       }


                       viewHolder.group_image.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {

                               if(model.getType().equals("image")) {

                                   if ((!(model.isSeen())&&!model.getFrom().equals(userId))){
                                       try {viewHolder.group_progressBar.setVisibility(View.VISIBLE);
                                           download(model.getImage(),model.getName(),refKey,"image",viewHolder.group_progressBar,id,model.getFrom());

                                       } catch (Exception e) {
                                           e.printStackTrace();
                                       }

                                   }
                                   else {

                                       try {
                                           OpenFile.DownLoadFile(getApplicationContext(),Uri.parse(model.getImage()));
                                       } catch (IOException e) {
                                           e.printStackTrace();
                                       }

                                   }
                               }
                               else
                               if(model.getType().equals("file")||model.getType().equals("video")||model.getType().equals("audio")){

                                   if ((!(model.isSeen())&&!model.getFrom().equals(userId))){
                                       try {viewHolder.group_progressBar.setVisibility(View.VISIBLE);
                                           download(model.getFile(),model.getName(),refKey,"file",viewHolder.group_progressBar,id,model.getFrom());
                                       } catch (Exception e) {
                                           e.printStackTrace();
                                       }
                                   }
                                   else {

                                       try {
                                           OpenFile.DownLoadFile(getApplicationContext(),Uri.parse(model.getFile()));
                                       } catch (IOException e) {
                                           e.printStackTrace();
                                       }

                                   }

                               }
                           }
                       });

                       viewHolder.user_image.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {

                               if(model.getType().equals("image")) {


                                   if ((!(model.isSeen())&&!model.getFrom().equals(userId))){
                                       try {viewHolder.group_progressBar.setVisibility(View.VISIBLE);
                                           download(model.getImage(),model.getName(),refKey,"image",viewHolder.group_progressBar,id,model.getFrom());

                                       } catch (Exception e) {
                                           e.printStackTrace();
                                       }
                                   }
                                   else {

                                       try {
                                           OpenFile.DownLoadFile(getApplicationContext(),Uri.parse(model.getImage()));
                                       } catch (IOException e) {
                                           e.printStackTrace();
                                       }

                                   }
                               }
                               else
                               if(model.getType().equals("file")||model.getType().equals("video")||model.getType().equals("audio")){

                                   if ((!(model.isSeen())&&!model.getFrom().equals(userId))){
                                       try {viewHolder.group_progressBar.setVisibility(View.VISIBLE);
                                           download(model.getFile(),model.getName(),refKey,"file",viewHolder.group_progressBar,id,model.getFrom());


                                       } catch (Exception e) {
                                           e.printStackTrace();
                                       }
                                   }
                                   else {


                                       try {
                                           OpenFile.DownLoadFile(getApplicationContext(),Uri.parse(model.getFile()));
                                       } catch (IOException e) {
                                           e.printStackTrace();
                                       }

                                   }

                               }
                           }
                       });


                       viewHolder.user_video.setVisibility(View.INVISIBLE);
                       viewHolder.group_video.setVisibility(View.INVISIBLE);
                       viewHolder.userPlay.setVisibility(View.INVISIBLE);
                       viewHolder.groupPlay.setVisibility(View.INVISIBLE);

                       /*if(model.getType().equals("video")){
                           viewHolder.user_image.setVisibility(View.INVISIBLE);
                           viewHolder.group_image.setVisibility(View.INVISIBLE);
                           viewHolder.user_video.setVisibility(View.VISIBLE);
                           viewHolder.group_video.setVisibility(View.VISIBLE);
                           viewHolder.userPlay.setVisibility(View.VISIBLE);
                           viewHolder.groupPlay.setVisibility(View.VISIBLE);


                           viewHolder.user_video.setVideoURI(Uri.parse(model.getFile()));
                           RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewHolder.user_video.getLayoutParams();
                           layoutParams.setMargins(0, 0, 0, 0);



                           viewHolder.group_video.setVideoURI(Uri.parse(model.getFile()));
                           RelativeLayout.LayoutParams layoutParams1 = (RelativeLayout.LayoutParams) viewHolder.group_video.getLayoutParams();
                           layoutParams1.setMargins(0, 0, 0, 0);



                           picasso.load(R.drawable.user).resize(1000,1000).centerCrop().networkPolicy(NetworkPolicy.OFFLINE).into(viewHolder.user_image, new Callback() {
                               @Override
                               public void onSuccess() {

                               }

                               @Override
                               public void onError() {

                                   picasso.load(R.drawable.user).resize(1000,1000).into(viewHolder.user_image);

                               }
                           });

                           picasso.load(R.drawable.user).resize(1000,1000).centerCrop().networkPolicy(NetworkPolicy.OFFLINE).into(viewHolder.group_image, new Callback() {
                               @Override
                               public void onSuccess() {

                               }

                               @Override
                               public void onError() {

                                   picasso.load(R.drawable.user).resize(1000,1000).centerCrop().into(viewHolder.group_image);

                               }
                           });



                       }


                       viewHolder.userPlay.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View view) {
                               try {
                                   OpenFile.DownLoadFile(getApplicationContext(),Uri.parse(model.getFile()));
                               } catch (IOException e) {
                                   e.printStackTrace();
                               }
                           }
                       });

                      /* viewHolder.groupPlay.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View view) {
                               if ((!(model.isSeen())&&!model.getFrom().equals(userId))){
                                   try {
                                       download(model.getFile(),model.getName(),refKey,"file");

                                   } catch (Exception e) {
                                       e.printStackTrace();
                                   }



                               }
                               else {


                                   try {
                                       OpenFile.DownLoadFile(getApplicationContext(),Uri.parse(model.getFile()));
                                   } catch (IOException e) {
                                       e.printStackTrace();
                                   }

                               }
                           }
                       });*/


                       if(model.getFrom().equals(userId)){
                           viewHolder.groupLayout.setVisibility(View.INVISIBLE);
                           viewHolder.userLayout.setVisibility(View.VISIBLE);
                       }

                       else
                       {
                           viewHolder.groupLayout.setVisibility(View.VISIBLE);
                           viewHolder.userLayout.setVisibility(View.INVISIBLE);
                       }

                       viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                           @Override
                           public boolean onLongClick(View v) {


                               AlertDialog.Builder builder=new AlertDialog.Builder(ChatActivity.this);
                               builder.setMessage("Do you want to delete this message");
                               builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog, int which) {



                                       mRef.child(refKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task) {
                                               if(task.isSuccessful()) {
                                                   Toast.makeText(ChatActivity.this, "message deleted", Toast.LENGTH_LONG).show();

                                               }
                                           }
                                       });
                                   }
                               });
                               builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog, int which) {

                                   }
                               });
                               builder.show();
                               return true;
                           }
                       });
                   }




            };
            firebaseRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    int friendlyMessageCount = firebaseRecyclerAdapter.getItemCount();
                    int lastVisiblePosition =
                            linearLayout.findLastCompletelyVisibleItemPosition();
                    // If the recycler view is initially being loaded or the
                    // user is at the bottom of the list, scroll to the bottom
                    // of the list to show the newly added message.
                    if (lastVisiblePosition == -1 ||
                            (positionStart >= (friendlyMessageCount - 1) &&
                                    lastVisiblePosition == (positionStart - 1))) {
                        messageList.scrollToPosition(positionStart);
                    }
                }
            });

            firebaseRecyclerAdapter.startListening();
            messageList.setAdapter(firebaseRecyclerAdapter);


        }



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        firebaseRecyclerAdapter.stopListening();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {

        TextView messageText ;
        TextView timeView ;
        ImageView imageMessage1 ;

        TextView messageText1 ;
        TextView timeView1 ;
        ImageView imageMessage ;
        RelativeLayout user1Layout;
        RelativeLayout user2Layout;
        RelativeLayout userMessageRelativeLayout;
        ProgressBar user1ProgressBar;
        ProgressBar userProgressBar;
        VideoView video1;
        VideoView video;
        ImageButton play,play1;
        TextView seen;

        View itemView;
        public ChatViewHolder(View itemView) {
            super(itemView);
            this.itemView=itemView;
             messageText = (TextView) itemView.findViewById(R.id.user2_text);
             timeView = (TextView) itemView.findViewById(R.id.WelcomeToChat);
            imageMessage1 = (ImageView) itemView.findViewById(R.id.user1_image);
            messageText1 = (TextView) itemView.findViewById(R.id.user_text);
             timeView1 = (TextView) itemView.findViewById(R.id.user_time);
             imageMessage = (ImageView) itemView.findViewById(R.id.user2_image);
             user1Layout=(RelativeLayout)itemView.findViewById(R.id.user1_relativeLayout);
             user2Layout=(RelativeLayout)itemView.findViewById(R.id.user2_relativeLayout);
             userMessageRelativeLayout=(RelativeLayout)itemView.findViewById(R.id.user_message_relativeLayout);
             userProgressBar=itemView.findViewById(R.id.user2progressBar);
             user1ProgressBar=itemView.findViewById(R.id.userProgressBar);
             video=itemView.findViewById(R.id.user2_video);
             video1=itemView.findViewById(R.id.user1_video);
             play=itemView.findViewById(R.id.user2_playButton);
             play1=itemView.findViewById(R.id.user1_playButton);
             seen=itemView.findViewById(R.id.seen);



        }





    }
    public static class ChatViewHolder1 extends RecyclerView.ViewHolder {


        TextView nameofSender;


        TextView group_text ;
        TextView group_time;
        TextView user_text ;
        TextView user_time ;
        ImageView group_image;
        ImageView user_image;
        RelativeLayout groupLayout;
        RelativeLayout userLayout;
        RelativeLayout groupMessageRelativeLayout;
        ProgressBar group_progressBar;
        ProgressBar user_progressBar;
        VideoView user_video;
        VideoView group_video;
        ImageButton userPlay,groupPlay;


        View itemView;
        public ChatViewHolder1(View itemView) {
            super(itemView);
            this.itemView=itemView;
            nameofSender = (TextView) itemView.findViewById(R.id.group_name);


            group_text = (TextView) itemView.findViewById(R.id.group_text);
            group_text.setMovementMethod(LinkMovementMethod.getInstance());
            group_time = (TextView) itemView.findViewById(R.id.group_time);
            user_text = (TextView) itemView.findViewById(R.id.user2_text);
            user_text.setMovementMethod(LinkMovementMethod.getInstance());
            user_time = (TextView) itemView.findViewById(R.id.WelcomeToChat);
            group_image=(ImageView) itemView.findViewById(R.id.group_image);
            user_image=(ImageView)itemView.findViewById(R.id.user_image);
            groupLayout=(RelativeLayout)itemView.findViewById(R.id.group_relativeLayout);
            userLayout=(RelativeLayout)itemView.findViewById(R.id.user_relativeLayout);
            groupMessageRelativeLayout=(RelativeLayout)itemView.findViewById(R.id.group_message_relativeLayout);
            group_progressBar=itemView.findViewById(R.id.group_progressBar);
            user_progressBar=itemView.findViewById(R.id.user2_progressBar);
            group_video=itemView.findViewById(R.id.group_video);
            user_video=itemView.findViewById(R.id.user_video);
            userPlay=itemView.findViewById(R.id.user_playButton);
            groupPlay=itemView.findViewById(R.id.group_playButton);


        }





    }


    private String encrypt(String data,String password) throws Exception{
        SecretKeySpec key=generateKey(password);

        Cipher cipher=Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE,key);
        byte[] encyVal=cipher.doFinal(data.getBytes());
        String encryptedValue= Base64.encodeToString(encyVal,Base64.DEFAULT);
        return encryptedValue;

    }

    private SecretKeySpec generateKey(String password) throws Exception{

        final MessageDigest digest=MessageDigest.getInstance("SHA-256");
        byte[] bytes=password.getBytes();
        digest.update(bytes,0,bytes.length);
        byte[] key=digest.digest();

        SecretKeySpec secretKeySpec= new SecretKeySpec(key,"Aes");

        return secretKeySpec;
    }

    private String decrypt(String data,String password) throws Exception{

        SecretKeySpec key=generateKey(password);
        Cipher cipher=Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE,key);

        byte[] decodedValue=Base64.decode(data,Base64.DEFAULT);
        byte[] decVal=cipher.doFinal(decodedValue);
        String decryptedValue=new String(decVal);
        return decryptedValue;

    }

    private void download(String imageUrl, String name, final String key, final String type, final ProgressBar progressBar, final String ID, final String from){


        dm=(DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request=new DownloadManager.Request(Uri.parse(imageUrl));
        File direct = new File(Environment.getExternalStorageDirectory().getAbsoluteFile()
                + "/jecChat_files");

        if (!direct.exists()) {
            direct.mkdirs();
        }

        request.setDestinationInExternalFilesDir(getApplicationContext(),"/jecChat_files",name);

        queiId=dm.enqueue(request);

        try {


            new Thread(new Runnable() {

                @Override
                public void run() {


                    boolean downloading=true;
                    DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                    while (downloading) {

                        DownloadManager.Query q = new DownloadManager.Query();
                        q.setFilterById(queiId); //filter by id which you have receieved when reqesting download from download manager
                        Cursor cursor = manager.query(q);
                        cursor.moveToFirst();
                        int bytes_downloaded = cursor.getInt(cursor
                                .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                        int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

                        if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                            iMageUri=cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                            downloading = false;
                            mRef.child(key).child("seen").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        mRef.child(key).child(type).setValue(iMageUri).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    try {
                                                        OpenFile.DownLoadFile(getApplicationContext(), Uri.parse(iMageUri));
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    if(!(ID.equals("cse")||ID.equals("me")||ID.equals("ec")||ID.equals("it")||ID.equals("ce")||ID.equals("ee")||ID.equals("ip")||ID.contains("groupId")||ID.contains("branchId"))) {
                                                        if (type.equals("image")) {
                                                            mImageRef.child("imageMessage").child(from).child(userId).child(key).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                }
                                                            });
                                                        }
                                                        else if (type.equals("file")){
                                                            mImageRef.child("pdfMessage").child(from).child(userId).child(key).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                }
                                                            });
                                                            mImageRef.child("videoMessage").child(from).child(userId).child(key).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                }
                                                            });
                                                            mImageRef.child("audioMessage").child(from).child(userId).child(key).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                }
                                                            });
                                                        }


                                                    }

                                            }   }
                                        });

                                    }
                                }
                            });
                        }

                        final int dl_progress = (int) ((bytes_downloaded * 100l) / bytes_total);

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                progressBar.setProgress((int) dl_progress);

                            }
                        });

                        // Log.d(Constants.MAIN_VIEW_ACTIVITY, statusMessage(cursor));
                        cursor.close();
                    }

                }
            }).start();



        } catch (Exception e) {
            e.printStackTrace();
        }



    }

    private String downloadUserData(String imageUrl, String name, final String key, final String type){

        final String[] uriString = new String[1];

        dm=(DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request=new DownloadManager.Request(Uri.parse(imageUrl));
        File direct = new File(Environment.getExternalStorageDirectory().getAbsoluteFile()
                + "/jecChat_files");

        if (!direct.exists()) {
            direct.mkdirs();
        }

        request.setDestinationInExternalFilesDir(getApplicationContext(),"/jecChat_files",name);

        queiId=dm.enqueue(request);

        try {


            new Thread(new Runnable() {

                @Override
                public void run() {


                    boolean downloading=true;
                    DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                    while (downloading) {

                        DownloadManager.Query q = new DownloadManager.Query();
                        q.setFilterById(queiId); //filter by id which you have receieved when reqesting download from download manager
                        Cursor cursor = manager.query(q);
                        cursor.moveToFirst();
                        int bytes_downloaded = cursor.getInt(cursor
                                .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                        int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

                        if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                            uriString[0] =cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                            downloading = false;
                            mRef.child(key).child(type).setValue(uriString[0]);

                        }

                        cursor.close();
                    }

                }
            }).start();



        } catch (Exception e) {
            e.printStackTrace();
        }


        return uriString[0];

    }

    public String size(int sizeIndex){
        String size= "";

        double k = sizeIndex/1024;
        double m = sizeIndex/1048576;
        double g = sizeIndex/1073741824;
        Log.i("size",String.valueOf(sizeIndex));

        DecimalFormat dec = new DecimalFormat("0.0");

        if (g > 1) {
            size = dec.format(g).concat("GB");
        } else if (m > 1) {
            size = dec.format(m).concat("MB");
        } else if (k > 1) {
            size = dec.format(k).concat("KB");
        } else {
            size =String.valueOf(sizeIndex); //dec.format(size).concat("KB");
        }
        return size;
    }

}
