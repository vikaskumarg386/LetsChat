package com.kumar.vikas.jecchat;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUsers_Activity extends AppCompatActivity {

    private Toolbar mtoolbar;
    private RecyclerView mUserList;
    private DatabaseReference userDatabse;
    private DatabaseReference mRootRef;
    private String key1,key2;
    private FirebaseRecyclerAdapter<users,AllUsersViewHolder> firebaseRecyclerAdapter;

    private EditText searchText;
    private String groupId;
    private String name,groupName,userId,userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users_);


        mtoolbar =(android.support.v7.widget.Toolbar)findViewById(R.id.allUsresToolBar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("select user");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

       // searchText=(EditText)findViewById(R.id.searchText);
        mUserList=(RecyclerView)findViewById(R.id.allUsersRecyclerView);
        mUserList.setHasFixedSize(true);
        mUserList.setLayoutManager(new LinearLayoutManager(this));
        groupId=getIntent().getStringExtra("groupId");
        mRootRef= FirebaseDatabase.getInstance().getReference();
        userDatabse= mRootRef.child("users");
        userDatabse.keepSynced(true);
        mUserList.setAdapter(firebaseRecyclerAdapter);

        userId= FirebaseAuth.getInstance().getCurrentUser().getUid();

        mRootRef.child("users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userName=dataSnapshot.child("name").getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRootRef.child("group").child(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                groupName=dataSnapshot.child("name").getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        searchText=findViewById(R.id.allUsersSearchText);

        showUsers(searchText.getText().toString());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);

         getMenuInflater().inflate(R.menu.search_menu,menu);


         return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         super.onOptionsItemSelected(item);
         searchText.setVisibility(View.VISIBLE);
         searchText.addTextChangedListener(new TextWatcher() {
             @Override
             public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                 showUsers(searchText.getText().toString());
             }

             @Override
             public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                 showUsers(searchText.getText().toString());
             }

             @Override
             public void afterTextChanged(Editable editable) {
                 showUsers(searchText.getText().toString());
             }
         });

        showUsers(searchText.getText().toString());
         return true;
    }

    protected void showUsers(String sname ) {
        Query query=userDatabse.orderByChild("name").startAt(sname);

        FirebaseRecyclerOptions<users> options =
                new FirebaseRecyclerOptions.Builder<users>()
                        .setQuery(query, users.class)
                        .build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<users, AllUsersViewHolder>(options){
            @Override
            public AllUsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_layout, parent, false);

                return new AllUsersViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final AllUsersViewHolder viewHolder, final int position, @NonNull users model) {


                final String id=getRef(position).getKey();


                mRootRef.child("deactivateAccount").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(id)){
                            viewHolder.setName("Not available");
                            viewHolder.setStatus("Deactivated Account");
                            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Toast.makeText(AllUsers_Activity.this,"Deactivated Account",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else{
                            mRootRef.child("users").child(id).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    viewHolder.setName(dataSnapshot.child("name").getValue().toString());
                                    viewHolder.setStatus(dataSnapshot.child("status").getValue().toString());
                                    viewHolder.setImage(dataSnapshot.child("thumbImage").getValue().toString(),getApplicationContext());

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                mRootRef.child("users").child(id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        name = dataSnapshot.child("name").getValue().toString();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                mRootRef.child("group").child(groupId).child("users").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(id)){
                            viewHolder.done.setVisibility(View.VISIBLE);
                        }
                        else {
                            viewHolder.done.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(viewHolder.done.getVisibility()==View.INVISIBLE) {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(AllUsers_Activity.this);
                            builder.setMessage("Add to the group");
                            builder.setPositiveButton("add", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(final DialogInterface dialog, int which) {

                                    Map addMap = new HashMap();
                                    addMap.put("timeStamp", ServerValue.TIMESTAMP);
                                    addMap.put("name", name);

                                    Map chatMap = new HashMap();
                                    chatMap.put("seen", false);
                                    chatMap.put("lastMessage", "  ");
                                    chatMap.put("timeStamp", ServerValue.TIMESTAMP);

                                    HashMap notiHash = new HashMap<>();
                                    notiHash.put("from", userId);
                                    notiHash.put("type", groupName+": "+userName+" has added you to the group.");
                                    notiHash.put("key", groupId);
                                    notiHash.put("clickAction","JecChat.Notification.Notification");
                                    notiHash.put("time", ServerValue.TIMESTAMP);

                                    String key=groupId+" message";
                                    Map map = new HashMap();
                                    map.put("group/" + groupId + "/" + "users/" + id, addMap);
                                    map.put("chat/" + id + "/" + groupId, chatMap);
                                    map.put("notifications/" + id + "/" + key, notiHash);



                                    mRootRef.updateChildren(map, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                            if (databaseError == null) {
                                                //Toast.makeText(AllUsers_Activity.this, "Added to group", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            }
                                        }
                                    });







                                }
                            });
                            builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();

                                }
                            });
                            builder.show();

                        }
                        else if (viewHolder.done.getVisibility()==View.VISIBLE){

                            final AlertDialog.Builder builder = new AlertDialog.Builder(AllUsers_Activity.this);
                            builder.setMessage("remove from group");
                            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                   mRootRef.child("group").child(groupId).child("users").child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                       @Override
                                       public void onComplete(@NonNull Task<Void> task) {
                                           viewHolder.done.setVisibility(View.INVISIBLE);
                                       }
                                   });

                                }
                            });
                            builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            builder.show();
                        }
                    }
                });
            }



        };


        firebaseRecyclerAdapter.startListening();
        mUserList.setAdapter(firebaseRecyclerAdapter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        firebaseRecyclerAdapter.stopListening();
    }

    public static class AllUsersViewHolder extends RecyclerView.ViewHolder{

        View mView;
        ImageView done;

        public AllUsersViewHolder(View itemView) {
            super(itemView);
            this.mView=itemView;
            done=(ImageView)mView.findViewById(R.id.doneView);
        }

        public void setName(String name){
            TextView usersNameView=(TextView)mView.findViewById(R.id.notification);
            usersNameView.setText(name);

        }
        public void setStatus(String mstatus){
            TextView usersStatusView=(TextView)mView.findViewById(R.id.displayUserStatus);
            usersStatusView.setText(mstatus);

        }
        public void setImage(final String imageUrl, final Context ctx){
            final CircleImageView image=(CircleImageView)mView.findViewById(R.id.notiImage);
            Picasso.with(ctx).load(imageUrl).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.user).into(image, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {

                    Picasso.with(ctx).load(imageUrl).placeholder(R.drawable.user).into(image);
                }
            });
        }
    }
}
