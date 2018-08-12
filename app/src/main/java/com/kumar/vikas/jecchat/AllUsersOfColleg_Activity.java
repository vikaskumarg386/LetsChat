package com.kumar.vikas.jecchat;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUsersOfColleg_Activity extends AppCompatActivity {


    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private DatabaseReference mRootRef,mRef;
    private String userId;
    private EditText searchText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users_of_colleg_);

        toolbar = findViewById(R.id.allUsersOfCollegeToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All users");
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView=findViewById(R.id.allUsersOfCollegeRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(AllUsersOfColleg_Activity.this));
        searchText=findViewById(R.id.searchUsers);

        userId= FirebaseAuth.getInstance().getCurrentUser().getUid();

        mRootRef= FirebaseDatabase.getInstance().getReference();
        String college=getIntent().getStringExtra("key");

        mRef=mRootRef.child("college").child(college).child("users");

        showUsers(searchText.getText().toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search_menu,menu);


        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
            { onBackPressed();
                return true;}

            case R.id.app_bar_search:{
                searchText.setVisibility(View.VISIBLE);
                searchText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        showUsers(searchText.getText().toString());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        showUsers(searchText.getText().toString());

                    }
                });
                showUsers(searchText.getText().toString());

            }
        }
        return super.onOptionsItemSelected(item);
    }


    private void showUsers(String s) {
        Query query=mRef.orderByChild("name").startAt(s);
        FirebaseRecyclerOptions<users> options=new FirebaseRecyclerOptions.Builder<users>().setQuery(query,users.class).build();

        FirebaseRecyclerAdapter firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<users,AllUsersOfCollegeViewHolder>(options) {

            @NonNull
            @Override
            public AllUsersOfCollegeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_layout, parent, false);
                return new AllUsersOfCollegeViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final AllUsersOfCollegeViewHolder holder, final int position, @NonNull final users model) {

                final String id=getRef(position).getKey();
                mRootRef.child("users").child(id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        holder.setName(dataSnapshot.child("name").getValue().toString());
                        holder.setStatus(dataSnapshot.child("status").getValue().toString());
                        holder.setImage(dataSnapshot.child("thumbImage").getValue().toString(),getApplicationContext());

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                mRootRef.child("deactivateAccount").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(id)){
                            holder.setName("Not Available");
                            holder.setImage("abc",getApplicationContext());
                            holder.setStatus("Deactivated Account");
                            holder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Toast.makeText(AllUsersOfColleg_Activity.this,"Deactivated Account",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });






                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profileIntent=new Intent(AllUsersOfColleg_Activity.this,UserProfile_Activity.class);
                        profileIntent.putExtra("key_noti",id);
                        profileIntent.putExtra("request"," ");
                        startActivity(profileIntent);
                    }
                });

            }
        };
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class AllUsersOfCollegeViewHolder extends RecyclerView.ViewHolder{

        View mView;
        ImageView done;

        public AllUsersOfCollegeViewHolder(View itemView) {
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
