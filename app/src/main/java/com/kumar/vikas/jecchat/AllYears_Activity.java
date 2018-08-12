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
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;


public class AllYears_Activity extends AppCompatActivity {


    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private DatabaseReference mRootRef;
    private DatabaseReference mref;
    private LinearLayoutManager linearLayoutManager;
    private String collegeKey,fieldKey;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    private EditText search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_years_);

        toolbar=(Toolbar)findViewById(R.id.all_years_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Select Year");

        collegeKey=getIntent().getStringExtra("collegeKey");
        fieldKey=getIntent().getStringExtra("fieldKey");

        linearLayoutManager=new LinearLayoutManager(AllYears_Activity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        search=findViewById(R.id.allYearsSearchText);

        recyclerView=(RecyclerView)findViewById(R.id.all_year_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        mRootRef= FirebaseDatabase.getInstance().getReference();
        mref=mRootRef.child("college").child(collegeKey).child("fieldOfWork").child(fieldKey).child("year");

        showBranch();

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



    private void showBranch(){
        Query refQuery=mref.orderByChild("name");

        FirebaseRecyclerOptions<users> options =
                new FirebaseRecyclerOptions.Builder<users>()
                        .setQuery(refQuery, users.class)
                        .build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<users, AllYearViewHolder>(options) {
            @Override
            public AllYearViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.setting_layout, parent, false);

                return new AllYearViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull AllYearViewHolder viewHolder, int position, @NonNull users model) {
                final String key = getRef(position).getKey();
                viewHolder.setName(key);

                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(AllYears_Activity.this, AllBranches_Activity.class);
                        intent.putExtra("yearKey", key);
                        intent.putExtra("fieldKey", fieldKey);
                        intent.putExtra("collegeKey", collegeKey);
                        startActivity(intent);

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


    public static class AllYearViewHolder extends RecyclerView.ViewHolder {

        View view;
        public AllYearViewHolder(View itemView) {
            super(itemView);
            this.view=itemView;
        }

        public void setName(String key) {

            TextView name=(TextView)view.findViewById(R.id.settingText);
            name.setText(key);
        }
    }

}
