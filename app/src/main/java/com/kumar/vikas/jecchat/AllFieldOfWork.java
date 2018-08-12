package com.kumar.vikas.jecchat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AllFieldOfWork extends AppCompatActivity {


    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private DatabaseReference mRootRef,mRef;
    private String userId;
    private String collegeKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_field_of_work);

        toolbar = findViewById(R.id.all_filed_of_work_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(" ");
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView=findViewById(R.id.all_field_of_work_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(AllFieldOfWork.this));
        recyclerView.setHasFixedSize(true);

        userId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        mRootRef= FirebaseDatabase.getInstance().getReference();
        collegeKey=getIntent().getStringExtra("key");
        mRef=mRootRef.child("college").child(collegeKey).child("fieldOfWork");

        showFieldOfWork();
    }

    private void showFieldOfWork() {

        FirebaseRecyclerOptions<users> options=new FirebaseRecyclerOptions.Builder<users>().setQuery(mRef,users.class).build();

        FirebaseRecyclerAdapter firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<users,AllFieldOfWorkViewHolder>(options) {

            @NonNull
            @Override
            public AllFieldOfWorkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view=getLayoutInflater().inflate(R.layout.setting_layout,parent,false);
                return new AllFieldOfWorkViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull AllFieldOfWorkViewHolder holder, final int position, @NonNull final users model) {


                holder.name.setText(getRef(position).getKey());
                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(AllFieldOfWork.this, AllYears_Activity.class);
                        intent.putExtra("fieldKey", getRef(position).getKey());
                        intent.putExtra("collegeKey", collegeKey);
                        startActivity(intent);
                    }
                });

            }
        };
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class AllFieldOfWorkViewHolder extends RecyclerView.ViewHolder{

        View view;
        TextView name;
        public AllFieldOfWorkViewHolder(View itemView) {
            super(itemView);
            this.view=itemView;
            name=itemView.findViewById(R.id.settingText);
        }
    }


}
