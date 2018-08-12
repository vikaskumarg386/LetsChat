package com.kumar.vikas.jecchat;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.squareup.picasso.Picasso;

import java.io.File;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class ChatImageMessage extends AppCompatActivity {


    private ImageView image;
    private ImageButton sendBUtton;
    private EditText textMessage;
    private DatabaseReference mrootRef;
    private DatabaseReference mRef;
    private String userId;
    private StorageReference mImageRef;
    private String pushKey;
    private ProgressDialog progressDialog;
    private long queiId;
    private DownloadManager dm;
    private String iMageUri;
    private boolean downloading = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_image_message);

        image=(ImageView)findViewById(R.id.imageView3);
        textMessage=(EditText) findViewById(R.id.imageText);
        sendBUtton=(ImageButton)findViewById(R.id.imageSendButton);
        mrootRef= FirebaseDatabase.getInstance().getReference().getRef();
        userId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        mImageRef= FirebaseStorage.getInstance().getReference();


        final String imageSize=getIntent().getStringExtra("imageSize");
        final String imageUri=getIntent().getStringExtra("imageUri");
        final String imageName=getIntent().getStringExtra("imageName");
        final String year=getIntent().getStringExtra("year");
        final String id=getIntent().getStringExtra("id");
        mRef=mrootRef.child("messages").child(userId).child(id);
        Picasso.with(ChatImageMessage.this).load(imageUri).into(image);

        sendBUtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog = new ProgressDialog(ChatImageMessage.this);
                progressDialog.setTitle("Uploading Image...");
                progressDialog.setMessage("Please wait until image upload");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();



                DatabaseReference pushRef = mrootRef.push();
                pushKey = pushRef.getKey();
                StorageReference thumbStorage = mImageRef.child("imageMessage").child(userId).child(id).child("thumb").child(pushKey).child(ServerValue.TIMESTAMP + "jpg");
                thumbStorage.putFile(Uri.parse(imageUri)).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()) {
                            final String imageUrl = task.getResult().getDownloadUrl().toString();
                            try {
                                download(imageUrl,imageName);
                                DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                                        while (downloading) {

                                            DownloadManager.Query q = new DownloadManager.Query();
                                            q.setFilterById(queiId); //filter by id which you have receieved when reqesting download from download manager
                                            Cursor cursor = manager.query(q);
                                            cursor.moveToFirst();
                                            if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                                                iMageUri=cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                                                downloading = false;
                                                String message=textMessage.getText().toString();
                                                if(message.equals(null))
                                                    message="   ";
                                                DatabaseReference keyRef=mrootRef.child("messages").child(userId).child(id).push();
                                                String pushKey=keyRef.getKey();
                                                String m= null;
                                                try {

                                                    m = encrypt(message,userId);
                                                    String time=DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date())+" "+DateFormat.getDateInstance().format(new Date());

                                                    final Map saveUserMessage=new HashMap();
                                                    saveUserMessage.put("message",m);
                                                    saveUserMessage.put("image",iMageUri);
                                                    saveUserMessage.put("file","null");
                                                    saveUserMessage.put("seen", true);
                                                    saveUserMessage.put("timestamp", time.substring(0,time.length()-5));
                                                    saveUserMessage.put("type","image");
                                                    saveUserMessage.put("from",userId);
                                                    saveUserMessage.put("name",imageName);

                                                    final Map savefriendMessage=new HashMap();
                                                    savefriendMessage.put("message",m);
                                                    savefriendMessage.put("image",imageUrl);
                                                    savefriendMessage.put("file","null");
                                                    savefriendMessage.put("seen", false);
                                                    savefriendMessage.put("timestamp", time.substring(0,time.length()-5));
                                                    savefriendMessage.put("type","image");
                                                    savefriendMessage.put("from",userId);
                                                    savefriendMessage.put("name",imageName);

                                                    if(!(id.equals("cse")||id.equals("me")||id.equals("ec")||id.equals("it")||id.equals("ce")||id.equals("ee")||id.equals("ip")||id.contains("branchId")))
                                                    {
                                                        Map imageMap = new HashMap();
                                                        imageMap.put("messages/" + userId + "/" + id + "/" + pushKey, saveUserMessage);
                                                        imageMap.put("messages/" + id + "/" + userId + "/" + pushKey, savefriendMessage);
                                                        mrootRef.updateChildren(imageMap, new DatabaseReference.CompletionListener() {
                                                            @Override
                                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                                if (databaseError != null)
                                                                    Log.i("error sendingImage", databaseError.getMessage());
                                                            }
                                                        });
                                                        mrootRef.child("chat").child(userId).child(id).child("seen").setValue(true);
                                                        mrootRef.child("chat").child(userId).child(id).child("lastMessage").setValue("*image");
                                                        mrootRef.child("chat").child(userId).child(id).child("timeStamp").setValue(ServerValue.TIMESTAMP);
                                                        mrootRef.child("chat").child(id).child(userId).child("seen").setValue(false);
                                                        mrootRef.child("chat").child(id).child(userId).child("lastMessage").setValue("*image");
                                                        mrootRef.child("chat").child(id).child(userId).child("timeStamp").setValue(ServerValue.TIMESTAMP);

                                                    }

                                                    else
                                                    {
                                                        Map imageMap = new HashMap();
                                                        imageMap.put("messages/" + userId + "/" + id + "/" + pushKey, saveUserMessage);


                                                        mrootRef.child("year").child(year).child("branch").child(id).child("users").addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                for(DataSnapshot childSnapshot:dataSnapshot.getChildren()){
                                                                    if(!childSnapshot.getKey().equals(userId)) {
                                                                        mrootRef.child("messages").child(childSnapshot.getKey()).child(id).push().setValue(savefriendMessage);
                                                                        mrootRef.child("chat").child(childSnapshot.getKey()).child(id).child("seen").setValue(false);
                                                                        mrootRef.child("chat").child(childSnapshot.getKey()).child(id).child("lastMessage").setValue("*image");
                                                                        mrootRef.child("chat").child(childSnapshot.getKey()).child(id).child("timeStamp").setValue(ServerValue.TIMESTAMP);
                                                                    }

                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {

                                                            }
                                                        });

                                                        mrootRef.updateChildren(imageMap, new DatabaseReference.CompletionListener() {
                                                            @Override
                                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                                if (databaseError != null)
                                                                    Log.i("error sendingImage", databaseError.getMessage());
                                                                else progressDialog.dismiss();
                                                            }
                                                        });
                                                        mrootRef.child("chat").child(userId).child(id).child("seen").setValue(true);
                                                        mrootRef.child("chat").child(userId).child(id).child("lastMessage").setValue("*image");
                                                        mrootRef.child("chat").child(userId).child(id).child("timeStamp").setValue(ServerValue.TIMESTAMP);




                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    progressDialog.dismiss();
                                                }



                                                textMessage.setText("");
                                                Intent intent=new Intent(ChatImageMessage.this,ChatActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                intent.putExtra("ID",id);
                                                startActivity(intent);
                                            }


                                        }




                            } catch (Exception e) {
                                e.printStackTrace();
                            }



                        } else {
                            Toast.makeText(ChatImageMessage.this, "faild to upload in storage", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }

                    }
                });
            }
        });





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

    private void download(String imageUrl,String name){


        dm=(DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request=new DownloadManager.Request(Uri.parse(imageUrl));
        File direct = new File(Environment.getExternalStorageDirectory().getAbsoluteFile()
                + "/jecChat_files");

        if (!direct.exists()) {
            direct.mkdirs();
        }

        request.setDestinationInExternalFilesDir(getApplicationContext(),"/jecChat_files",name);

        queiId=dm.enqueue(request);
    }
}
