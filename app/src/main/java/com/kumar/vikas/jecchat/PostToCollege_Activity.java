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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class PostToCollege_Activity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private DatabaseReference mRootRef;
    private DatabaseReference mRef;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_to_college_);

        toolbar=findViewById(R.id.postCollegeToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("select institute");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        recyclerView=findViewById(R.id.postCollegeRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(PostToCollege_Activity.this));

        mRootRef= FirebaseDatabase.getInstance().getReference();
        userId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        mRef=mRootRef.child("users").child(userId).child("college");
        Toast.makeText(this, mRef.toString(), Toast.LENGTH_SHORT).show();
        showListOfCollege();
    }

    private void showListOfCollege() {

        FirebaseRecyclerOptions<users> options=new FirebaseRecyclerOptions.Builder<users>().setQuery(mRef,users.class).build();

        FirebaseRecyclerAdapter firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<users,collegViewHolder>(options) {

            @NonNull
            @Override
            public collegViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view=getLayoutInflater().inflate(R.layout.setting_layout,parent,false);
                return new collegViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull collegViewHolder holder, final int position, @NonNull final users model) {

                final String name=getRef(position).getKey();
                holder.name.setText(getRef(position).getKey());
                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(PostToCollege_Activity.this,Post_Activity.class);
                        intent.putExtra("college",name);
                        intent.putExtra("message","");
                        intent.putExtra("image","null");
                        intent.putExtra("video","null");
                        intent.putExtra("ref","null");
                        startActivity(intent);
                    }
                });

            }
        };
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);



    }

    public static class collegViewHolder extends RecyclerView.ViewHolder {
        View view;
        TextView name;
        public collegViewHolder(View itemView) {
            super(itemView);

            this.view=itemView;
            name=itemView.findViewById(R.id.settingText);


        }
    }

}
