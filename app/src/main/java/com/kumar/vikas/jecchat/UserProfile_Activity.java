package com.kumar.vikas.jecchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile_Activity extends AppCompatActivity {

    private CircleImageView profileImage;
    private ImageView displayImageCover;
    private TextView profileName;
    private TextView profileStatus;
    private TextView profileEnroll;
    private TextView profileBranch;
    private TextView profileSem;
    private TextView profileEmail;
    private Button frndReq;
    private Button declReq;
    private String count;

    private DatabaseReference profileRef;
    private DatabaseReference requestRef;
    private DatabaseReference friendsRef;
    private DatabaseReference notificationRef;
    private DatabaseReference mrootRef;
    private DatabaseReference mRef;

    private String userId;
    private int requestStatus=0;//send=0,cancel=1,accept=2,unfriend=3,checking own profile=4;

    private String request;
    private String image;
    private String imageCover;
    private String name;
    private String userName;
    private String id;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_);

        profileImage=(CircleImageView) findViewById(R.id.circleImageView);
        displayImageCover=(ImageView)findViewById(R.id.display_cover_image);
        profileName=(TextView)findViewById(R.id.displayName);
        profileStatus=(TextView)findViewById(R.id.s_tatus);
        profileEnroll=(TextView)findViewById(R.id.profileEnroll);
        profileBranch=(TextView)findViewById(R.id.profileBranch);
        profileSem=(TextView)findViewById(R.id.userSemester);
        profileEmail=(TextView)findViewById(R.id.profileEmailid);
        frndReq=(Button)findViewById(R.id.send_request);
        declReq=(Button)findViewById(R.id.decline_request);
        declReq.setVisibility(View.INVISIBLE);
        declReq.setEnabled(false);
        request=getIntent().getStringExtra("request");

        id=getIntent().getStringExtra("key_noti");
        profileRef= FirebaseDatabase.getInstance().getReference().child("users").child(id);
        requestRef=FirebaseDatabase.getInstance().getReference().child("friendRequest");
        friendsRef=FirebaseDatabase.getInstance().getReference().child("friends");
        notificationRef=FirebaseDatabase.getInstance().getReference().child("notifications");
        mrootRef=FirebaseDatabase.getInstance().getReference();







        userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (request.equals(" ")){
            frndReq.setEnabled(false);
            declReq.setEnabled(false);
        }
        else if (request.equals("sent you friend request")){
            declReq.setEnabled(true);
            declReq.setVisibility(View.VISIBLE);
            frndReq.setText("Accept friend request");
            requestStatus = 2;
        }
        else if (request.equals("accepted your friend request")){
            declReq.setEnabled(false);
            declReq.setVisibility(View.INVISIBLE);
            frndReq.setText("Unfriend this person");
            requestStatus = 3;
        }

       




        mrootRef.child("users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userName=dataSnapshot.child("name").getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //setting request status
       requestRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(id)) {
                    String requestType = dataSnapshot.child(id).child("request_type").getValue().toString();
                    if (requestType.equals("received")) {
                        frndReq.setText("Accept friend request");
                        requestStatus = 2;


                        Log.i("butoon", frndReq.getText().toString());
                        declReq.setVisibility(View.VISIBLE);
                        declReq.setEnabled(true);


                    } else if (requestType.equals("sent")) {
                        frndReq.setText("cancel the request");
                        requestStatus = 1;

                        Log.i("butoon", frndReq.getText().toString());
                        declReq.setVisibility(View.INVISIBLE);
                        declReq.setEnabled(false);

                    }

                }
                else {
                    friendsRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(id)) {
                                frndReq.setText("unfriend this person");
                                requestStatus = 3;
                                Log.i("butoon", frndReq.getText().toString());
                                declReq.setVisibility(View.INVISIBLE);
                                declReq.setEnabled(false);

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }

                frndReq.setEnabled(true);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


            }
        });

        if(id.equals(userId)){
            requestStatus=4;
            frndReq.setEnabled(false);
            frndReq.setText("This is your account");



        }




        profileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name=dataSnapshot.child("name").getValue().toString();
                final String status=dataSnapshot.child("status").getValue().toString();
                image=dataSnapshot.child("thumbImage").getValue().toString();
                final String enroll=dataSnapshot.child("enrollment").getValue().toString();
                final String branch=dataSnapshot.child("branch").getValue().toString();
                final String sem=dataSnapshot.child("sem").getValue().toString();
                final String emailId=dataSnapshot.child("email").getValue().toString();
                imageCover=dataSnapshot.child("thumbImageCover").getValue().toString();


                mrootRef.child("deactivateAccount").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(id)){
                            requestStatus=4;
                            frndReq.setEnabled(false);
                            frndReq.setText("Account Deactivated");
                            profileName.setText("Not Available");
                            profileSem.setText("not available");
                            profileBranch.setText("not available");
                            profileEmail.setText("not available");
                            profileEnroll.setText("not available");

                            Picasso.with(UserProfile_Activity.this).load("abc").networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.user).into(profileImage, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {
                                    Picasso.with(UserProfile_Activity.this).load("abc").placeholder(R.drawable.user).into(profileImage);
                                }
                            });
                            Picasso.with(UserProfile_Activity.this).load("abc").networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.user).into(displayImageCover, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {
                                    Picasso.with(UserProfile_Activity.this).load("abc").placeholder(R.drawable.user).into(displayImageCover);
                                }
                            });
                        }

                        else {
                            profileEnroll.setText(enroll);
                            profileBranch.setText(branch);
                            profileEmail.setText(emailId);
                            profileSem.setText(sem);
                            profileName.setText(name);
                            profileStatus.setText(status);
                            Picasso.with(UserProfile_Activity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.user).into(profileImage, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {
                                    Picasso.with(UserProfile_Activity.this).load(image).placeholder(R.drawable.user).into(profileImage);
                                }
                            });
                            Picasso.with(UserProfile_Activity.this).load(imageCover).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.user).into(displayImageCover, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {
                                    Picasso.with(UserProfile_Activity.this).load(imageCover).placeholder(R.drawable.user).into(displayImageCover);
                                }
                            });

                            //imageView
                            profileImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Intent intent=new Intent(UserProfile_Activity.this,Image_view_Activity.class);
                                    intent.putExtra("imageUrl",image);
                                    intent.putExtra("name",name);
                                    startActivity(intent);
                                }
                            });

                            displayImageCover.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent=new Intent(UserProfile_Activity.this,Image_view_Activity.class);
                                    intent.putExtra("imageUrl",imageCover);
                                    intent.putExtra("name",name);
                                    startActivity(intent);
                                }
                            });
                        }
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









        //decline request
        declReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestRef.child(userId).child(id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        requestRef.child(id).child(userId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                frndReq.setEnabled(true);
                                requestStatus=0;
                                frndReq.setText("send friend request");
                                declReq.setVisibility(View.INVISIBLE);
                                declReq.setEnabled(false);
                            }
                        });
                    }
                });


                declReq.setEnabled(false);
                requestRef.child(userId).child(id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        requestRef.child(id).child(userId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mrootRef.child("notifications").child(userId).child(id+" request").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {


                                            requestStatus = 0;
                                            frndReq.setText("send friend request");
                                            declReq.setVisibility(View.INVISIBLE);
                                            declReq.setEnabled(false);
                                            frndReq.setEnabled(true);
                                        }

                                    }
                                });

                            }
                        });
                    }
                });
            }
        });

        mrootRef.child("notiCount").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("count"))
                count=dataSnapshot.child("count").getValue().toString();
                else count="0";
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

         //sending friend request or canceling friend request or accepting request or unfriend
        frndReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               switch (requestStatus) {

                   case 0: {

                       String notirefid = userId+" request";
                       frndReq.setEnabled(false);


                       HashMap notiHash = new HashMap<>();
                       notiHash.put("from", userId);
                       notiHash.put("type", userName+" has sent you friend request");
                       notiHash.put("key", userId);
                       notiHash.put("clickAction","JecChat.Notification.Notification");
                       notiHash.put("time", ServerValue.TIMESTAMP);


                       Map requestMap = new HashMap();
                       requestMap.put("friendRequest/" + userId + "/" + id + "/" + "request_type", "sent");
                       requestMap.put("friendRequest/" + id + "/" + userId + "/" + "request_type", "received");
                       requestMap.put("friendRequest/" + id + "/" + userId + "/" + "noti_id", notirefid);
                       requestMap.put("notifications/" + id + "/" + notirefid, notiHash);
                       requestMap.put("notiCount/" + id + "/" + "count", String.valueOf(Integer.parseInt(count)+1));

                       mrootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                           @Override
                           public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                               if (databaseError == null) {
                                   frndReq.setEnabled(true);
                                   frndReq.setText("cancel the request");
                                   requestStatus = 1;
                                   declReq.setVisibility(View.INVISIBLE);
                                   declReq.setEnabled(false);
                               } else {
                                   Toast.makeText(UserProfile_Activity.this, "request not sent", Toast.LENGTH_SHORT).show();
                               }
                           }
                       });


                   } break;

                   case 1: {

                       frndReq.setEnabled(false);
                           requestRef.child(userId).child(id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                               @Override
                               public void onSuccess(Void aVoid) {
                                   requestRef.child(id).child(userId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                       @Override
                                       public void onSuccess(Void aVoid) {
                                           mrootRef.child("notifications").child(id).child(userId+" request").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                               @Override
                                               public void onComplete(@NonNull Task<Void> task) {

                                                   if (task.isSuccessful()) {

                                                       mrootRef.child("notifications").child(id).child("count").setValue(String.valueOf(Integer.parseInt(count)-1));
                                                       requestStatus = 0;
                                                       frndReq.setText("send friend request");
                                                       declReq.setVisibility(View.INVISIBLE);
                                                       declReq.setEnabled(false);
                                                       frndReq.setEnabled(true);
                                                   }

                                               }
                                           });

                                       }
                                   });
                               }
                           });

                       break;

                   }

                   case 2: {

                       final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                       String notirefid = userId+" request";
                       String notirefid1 = id+" request";
                       frndReq.setEnabled(false);

                       HashMap notiHash = new HashMap<>();
                       notiHash.put("from", userId);
                       notiHash.put("type", userName+" has accepted your friend request");
                       notiHash.put("key", userId);
                       notiHash.put("clickAction","JecChat.Notification.Notification");
                       notiHash.put("time", ServerValue.TIMESTAMP);

                       HashMap notiHash1 = new HashMap<>();
                       notiHash1.put("from", id);
                       notiHash1.put("type", "you have accepted friend request of "+name);
                       notiHash1.put("key", id);
                       notiHash1.put("clickAction","JecChat.Notification.Notification");
                       notiHash1.put("time", ServerValue.TIMESTAMP);

                       Map friendsMap = new HashMap();
                       friendsMap.put("friends/" + userId + "/" + id + "/date", currentDate);
                       friendsMap.put("friends/" + id + "/" + userId + "/date", currentDate);
                       friendsMap.put("notifications/" + id + "/" + notirefid, notiHash);
                       friendsMap.put("notiCount/" + id + "/" + "count", String.valueOf(Integer.parseInt(count)+1));
                       friendsMap.put("notifications/" + userId + "/" + notirefid1, notiHash1);

                       friendsMap.put("friendRequest/" + userId + "/" + id, null);
                       friendsMap.put("friendRequest/" + id + "/" + userId, null);
                       mrootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                           @Override
                           public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                               if (databaseError == null) {
                                   frndReq.setEnabled(true);
                                   requestStatus = 3;
                                   frndReq.setText("Unfriend this person");
                                   frndReq.setEnabled(true);

                                   declReq.setVisibility(View.INVISIBLE);
                                   declReq.setEnabled(false);
                               } else
                                   Toast.makeText(UserProfile_Activity.this, "task is not successfull", Toast.LENGTH_SHORT).show();

                           }
                       });

                      break;
                   }

                   case 3: {

                       frndReq.setEnabled(false);
                       Map unfriendMap = new HashMap();
                       unfriendMap.put("friends/" + userId + "/" + id, null);
                       unfriendMap.put("friends/" + id + "/" + userId, null);


                       mrootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                           @Override
                           public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                               if (databaseError == null) {
                                   frndReq.setEnabled(true);
                                   requestStatus = 0;
                                   frndReq.setText("send friend request");
                                   declReq.setVisibility(View.INVISIBLE);
                                   declReq.setEnabled(false);
                               } else
                                   Toast.makeText(UserProfile_Activity.this, "task is not successful", Toast.LENGTH_SHORT).show();
                           }
                       });

                       break;
                   }

                   default:

               }

            }
        });




        if(id.equals(userId)){
            requestStatus=4;
            frndReq.setEnabled(false);
            frndReq.setText("This is your account");



        }


    }



    @Override
    protected void onStart() {
        super.onStart();
        mrootRef.child("users").child(userId).child("online").setValue("true");




    }

    @Override
    protected void onPause() {
        super.onPause();
        mrootRef.child("users").child(userId).child("online").setValue(ServerValue.TIMESTAMP);
    }
}
