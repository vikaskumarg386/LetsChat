package com.kumar.vikas.jecchat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class LikedUsers_Activity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseReference mRootRef;
    private DatabaseReference mRef;
    private String userId;
    private String push_key;
    private Toolbar toolbar;
    private String postfrom;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liked_users_);

        toolbar=(Toolbar)findViewById(R.id.liked_usersToolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Liked Users");
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        push_key=getIntent().getStringExtra("push_key");
        String college=getIntent().getStringExtra("college_key");


        mRootRef= FirebaseDatabase.getInstance().getReference();
        mRef=mRootRef.child("timeLinePost").child("timeLineAllPost").child(college).child(push_key).child("userLiked");
        userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        linearLayoutManager=new LinearLayoutManager(this);
        recyclerView=(RecyclerView)findViewById(R.id.liked_usersRecyclerView);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);


        mRootRef.child("timeLinePost").child("timeLineAllPost").child(college).child(push_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                postfrom=dataSnapshot.child("from").getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        showLikedUsers();
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



    protected void showLikedUsers() {


        mRootRef.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("online").setValue("true");

        FirebaseRecyclerOptions<users> options =
                new FirebaseRecyclerOptions.Builder<users>()
                        .setQuery(mRef, users.class)
                        .build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<users, LikedUsersViewHolder>(options) {
            @Override
            public LikedUsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_layout, parent, false);

                return new LikedUsersViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final LikedUsersViewHolder viewHolder, int position, @NonNull users model) {

                final String likedUser = getRef(position).getKey();

                mRootRef.child("deactivateAccount").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(likedUser)){
                            viewHolder.name.setText("Not Available");
                            viewHolder.status.setText("Deactivated Account");
                            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Toast.makeText(LikedUsers_Activity.this,"Deactivated Account",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        else {

                            mRootRef.child("users").child(likedUser).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(final DataSnapshot dataSnapshot) {
                                    viewHolder.name.setText(dataSnapshot.child("name").getValue().toString());
                                    viewHolder.status.setText(dataSnapshot.child("status").getValue().toString());
                                    Picasso.with(viewHolder.image.getContext()).load(dataSnapshot.child("thumbImage").getValue().toString()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.user).into(viewHolder.image, new Callback() {
                                        @Override
                                        public void onSuccess() {

                                        }

                                        @Override
                                        public void onError() {
                                            Picasso.with(viewHolder.image.getContext()).load(dataSnapshot.child("thumbImage").getValue().toString()).placeholder(R.drawable.user).into(viewHolder.image);
                                        }
                                    });


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





                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profileIntent=new Intent(viewHolder.name.getContext(),UserProfile_Activity.class);
                        profileIntent.putExtra("key_noti",likedUser);
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

    public static class LikedUsersViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView status;

        public CircleImageView image;
        public View itemView;



        public LikedUsersViewHolder(View itemView) {
            super(itemView);
            this.itemView=itemView;

            name = (TextView) itemView.findViewById(R.id.notification);
            status= (TextView) itemView.findViewById(R.id.displayUserStatus);
            image = (CircleImageView) itemView.findViewById(R.id.notiImage);



        }
    }
}
