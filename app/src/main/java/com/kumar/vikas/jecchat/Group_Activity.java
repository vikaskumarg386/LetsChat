package com.kumar.vikas.jecchat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.LinearLayout;
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

import de.hdodenhof.circleimageview.CircleImageView;


public class Group_Activity extends AppCompatActivity {


    private DatabaseReference mRef;
    private String userId;
    private Toolbar toolbar;
    private String groupId;
    private DatabaseReference mRootRef;
    private RecyclerView recyclerView;
    private EditText searchText;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    private String groupName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_);

        userId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        mRootRef=FirebaseDatabase.getInstance().getReference();

        groupId=getIntent().getStringExtra("key_noti");
        mRef=mRootRef.child("group").child(groupId).child("users");
        toolbar=(Toolbar)findViewById(R.id.groupToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("All members");

        recyclerView=(RecyclerView)findViewById(R.id.groupRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(Group_Activity.this));
        recyclerView.setHasFixedSize(true);
        searchText=(EditText)findViewById(R.id.groupSearchText);

        mRootRef.child("group").child(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                groupName=dataSnapshot.child("name").getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        showgroup(searchText.getText().toString());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.group_menu,menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       super.onOptionsItemSelected(item);

       switch (item.getItemId()){




           case R.id.changeName:{
               AlertDialog.Builder builder=new AlertDialog.Builder(Group_Activity.this);
               final EditText name=new EditText(Group_Activity.this);
               LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(
                       ViewGroup.LayoutParams.MATCH_PARENT,
                       ViewGroup.LayoutParams.MATCH_PARENT
               );
               name.setLayoutParams(lp);
               builder.setView(name);
               name.setText(groupName);

               builder.setMessage("Enter new Name");
               builder.setPositiveButton("save", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                       mRootRef.child("group").child(groupId).child("name").setValue(name.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                               if (task.isSuccessful()){
                                   Toast.makeText(getApplicationContext(),"name changed",Toast.LENGTH_SHORT).show();
                               }
                               else {
                                   Toast.makeText(getApplicationContext(),"failed to save",Toast.LENGTH_SHORT).show();

                               }
                           }
                       });

                   }
               });
               builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {

                   }
               });
               builder.show();
               break;


           }

           case R.id.groupAdd:{

               Intent intent=new Intent(Group_Activity.this,AllUsers_Activity.class);
               intent.putExtra("groupId",groupId);
               startActivity(intent);
               break;

           }

           case R.id.groupSearch:{

               searchText.setVisibility(View.VISIBLE);


               searchText.addTextChangedListener(new TextWatcher() {
                   @Override
                   public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                       showgroup(searchText.getText().toString());
                   }

                   @Override
                   public void onTextChanged(CharSequence s, int start, int before, int count) {
                       showgroup(searchText.getText().toString());
                   }

                   @Override
                   public void afterTextChanged(Editable s) {
                       showgroup(searchText.getText().toString());
                   }
               });


           }
       }
        return true;
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

    private void showgroup(String name){

        Query query=mRef.orderByChild("name").startAt(name);
        FirebaseRecyclerOptions<users> options =
                new FirebaseRecyclerOptions.Builder<users>()
                        .setQuery(query, users.class)
                        .build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<users, GroupViewHolder>(options) {
            @Override
            public GroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_layout, parent, false);

                return new GroupViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final GroupViewHolder viewHolder, int position, @NonNull users model) {

                final String id=getRef(position).getKey();


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

                mRootRef.child("deactivateAccount").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(id)){

                            viewHolder.setImage("abc",getApplicationContext());
                            viewHolder.setStatus("Deactivated Account");
                            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Toast.makeText(Group_Activity.this,"Deactivated Account",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });




                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profileIntent=new Intent(Group_Activity.this,UserProfile_Activity.class);
                        profileIntent.putExtra("from_user_Id",id);
                        startActivity(profileIntent);
                    }
                });


            }



        };


        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        firebaseRecyclerAdapter.stopListening();
    }



    public static class GroupViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public GroupViewHolder(View itemView) {
            super(itemView);
            this.mView=itemView;

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
