package com.kumar.vikas.jecchat;

import android.content.Context;
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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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


public class Notification_Activity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private DatabaseReference mRef;
    private DatabaseReference mRootRef;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    private String college;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_);

        toolbar=(Toolbar)findViewById(R.id.notificationToolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Notifications");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        recyclerView=(RecyclerView)findViewById(R.id.noti_recyclerView);
        linearLayoutManager=new LinearLayoutManager(Notification_Activity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        mRootRef=FirebaseDatabase.getInstance().getReference();
        mRef=mRootRef.child("notifications").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mRootRef.child("notiCount").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("count").setValue("0");
        showNotification();

    }


    private void showNotification(){

        Query query=mRef.orderByChild("time");

        FirebaseRecyclerOptions<Noti> options =
                new FirebaseRecyclerOptions.Builder<Noti>()
                        .setQuery(query, Noti.class)
                        .build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Noti, NotiViewHolder>(options) {
            @Override
            public NotiViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.noti_layout, parent, false);

                return new NotiViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final NotiViewHolder viewHolder, int position, @NonNull final Noti model) {




                final String from=model.getFrom();



                mRootRef.child("users").child(from).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String name=dataSnapshot.child("name").getValue().toString();
                        college=dataSnapshot.child("college").getValue().toString();
                        String thumbImage=dataSnapshot.child("thumbImage").getValue().toString();
                        viewHolder.setName(model.getType());
                        viewHolder.setImage(thumbImage,getApplicationContext());
                        viewHolder.setType(model.getTime());

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                mRootRef.child("deactivateAccount").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(from)){

                            viewHolder.setImage("abc",getApplicationContext());

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(model.getType().contains("sent you friend request")){
                            Intent intent=new Intent(Notification_Activity.this,UserProfile_Activity.class);
                            intent.putExtra("key_noti",model.getKey());
                            intent.putExtra("request","sent you friend request");
                            startActivity(intent);
                        }

                        if (model.getType().contains("accepted your friend request")){
                            Intent intent=new Intent(Notification_Activity.this,UserProfile_Activity.class);
                            intent.putExtra("key_noti",model.getKey());
                            intent.putExtra("request","accepted your friend request");
                            startActivity(intent);
                        }

                        if(model.getType().contains("commented on your post")){

                            Intent intent=new Intent(Notification_Activity.this,Comment_Activity.class);
                            intent.putExtra("push_key",model.getKey());
                            intent.putExtra("college_key",college);
                            startActivity(intent);
                        }

                        if (model.getType().contains("liked your post")){
                            Intent intent=new Intent(Notification_Activity.this,LikedUsers_Activity.class);
                            intent.putExtra("push_key",model.getKey());
                            intent.putExtra("college_key",college);
                            startActivity(intent);
                        }

                        if (model.getType().contains("message")){
                            Intent intent=new Intent(Notification_Activity.this,ChatActivity.class);
                            intent.putExtra("key_noti",model.getKey());
                            startActivity(intent);

                        }

                        if (model.getType().contains("replied on your comment")||model.getType().contains("replied on your post")){
                            Intent intent=new Intent(Notification_Activity.this,Reply_Activity.class);
                            intent.putExtra("push_key",model.getKey());
                            intent.putExtra("college_key",college);
                            startActivity(intent);

                        }

                        if (model.getType().contains("added you to the group")){
                            Intent intent=new Intent(Notification_Activity.this,ChatActivity.class);
                            intent.putExtra("key_noti",model.getKey());
                            startActivity(intent);

                        }
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

    public static class NotiViewHolder extends RecyclerView.ViewHolder{
       public View mView;

        public NotiViewHolder(View itemView) {
            super(itemView);
            this.mView=itemView;
        }

        public void setType(long type) {
            TextView mStatus=(TextView)mView.findViewById(R.id.notiTime);
            mStatus.setTextSize(12);
            String lastSeenTime = TimeAgo.getTimeAgo(type, itemView.getContext());
            mStatus.setText(lastSeenTime);

        }

        public void setName(String name) {
            TextView mName=(TextView)mView.findViewById(R.id.notification);
            mName.setTextSize(14);
            mName.setText(name);
        }

        public void setImage(final String thumbImage, final Context applicationContext) {
            final CircleImageView imageView=(CircleImageView)mView.findViewById(R.id.notiImage);
            Picasso.with(applicationContext).load(thumbImage).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.user).into(imageView, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(applicationContext).load(thumbImage).placeholder(R.drawable.user).into(imageView);
                }
            });

        }
    }
}
