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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class AllBranches_Activity extends AppCompatActivity {


    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private DatabaseReference mRef;
    private DatabaseReference mRootRef;
    private String yearKey,fieldKey,collegeKey;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_branches_);

        toolbar=(Toolbar)findViewById(R.id.all_branches_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("All Branches");
        mRootRef= FirebaseDatabase.getInstance().getReference();
        fieldKey=getIntent().getStringExtra("fieldKey");
        yearKey=getIntent().getStringExtra("yearKey");
        collegeKey=getIntent().getStringExtra("collegeKey");
        mRef=mRootRef.child("college").child(collegeKey).child("fieldOfWork").child(fieldKey).child("year").child(yearKey).child("branch");
        recyclerView=(RecyclerView)findViewById(R.id.all_branches_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(AllBranches_Activity.this));

    }

    @Override
    protected void onPause() {
        super.onPause();
        mRootRef.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("online").setValue(ServerValue.TIMESTAMP);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mRootRef.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("online").setValue(true);

        FirebaseRecyclerOptions<users> options =
                new FirebaseRecyclerOptions.Builder<users>()
                        .setQuery(mRef, users.class)
                        .build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<users, AllBranchesViewHolder>(options) {
            @Override
            public AllBranchesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_layout, parent, false);

                return new AllBranchesViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final AllBranchesViewHolder viewHolder, int position, @NonNull users model) {

                final String key=getRef(position).getKey();
                viewHolder.setName(model.getName());
                viewHolder.setImage(model.getThumbImage(),getApplicationContext());
                mRef.child(key).child("users").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String totalStudent=String.valueOf(dataSnapshot.getChildrenCount());
                        viewHolder.setStatus(totalStudent);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent=new Intent(AllBranches_Activity.this,AllUsersOfABranch_Activity.class);
                        intent.putExtra("branchKey",key);
                        intent.putExtra("yearKey", yearKey);
                        intent.putExtra("fieldKey", fieldKey);
                        intent.putExtra("collegeKey", collegeKey);
                        startActivityForResult(intent, 1);

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



    public static class AllBranchesViewHolder extends RecyclerView.ViewHolder {

        View view;
        public AllBranchesViewHolder(View itemView) {
            super(itemView);
            this.view=itemView;
        }

        public void setName(String name) {

            TextView branchName=(TextView)view.findViewById(R.id.notification);
            branchName.setText(name);

        }

        public void setImage(final String thumbImage, final Context applicationContext) {
            final CircleImageView image=(CircleImageView)view.findViewById(R.id.notiImage);
            Picasso.with(applicationContext).load(thumbImage).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.user).into(image, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {

                    Picasso.with(applicationContext).load(thumbImage).placeholder(R.drawable.user).into(image);
                }
            });

        }

        public void setStatus(String totalStudent) {

            TextView status=(TextView)view.findViewById(R.id.displayUserStatus);
            status.setText(totalStudent+" students");

        }
    }



}
