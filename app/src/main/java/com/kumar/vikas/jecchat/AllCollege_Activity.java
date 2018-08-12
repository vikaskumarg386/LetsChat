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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllCollege_Activity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private DatabaseReference mRootRef,mRef;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_college_);

        toolbar = findViewById(R.id.all_college_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Select Institute");
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView=findViewById(R.id.all_college_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(AllCollege_Activity.this));


        userId= FirebaseAuth.getInstance().getCurrentUser().getUid();

        mRootRef= FirebaseDatabase.getInstance().getReference();
        mRef=mRootRef.child("users").child(userId).child("college");

        showCollegeList();

    }

    private void showCollegeList() {

        FirebaseRecyclerOptions<users> options=new FirebaseRecyclerOptions.Builder<users>().setQuery(mRef,users.class).build();

        FirebaseRecyclerAdapter firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<users,AllCollegeViewHolder>(options) {

            @NonNull
            @Override
            public AllCollegeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_layout, parent, false);
                return new AllCollegeViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull AllCollegeViewHolder holder, final int position, @NonNull final users model) {
                holder.setName(getRef(position).getKey());

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(AllCollege_Activity.this, AllUsersOfABranch_Activity.class);
                        intent.putExtra("collegeKey", getRef(position).getKey());
                        startActivity(intent);
                    }
                });

            }
        };
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class AllCollegeViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public AllCollegeViewHolder(View itemView) {
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
