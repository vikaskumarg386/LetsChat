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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class AllUsersOfABranch_Activity extends AppCompatActivity {

    private Toolbar mtoolbar;
    private RecyclerView mUserList;
    private DatabaseReference userDatabse;
    private DatabaseReference mRootRef;
    private String yearKey,fieldKey,collegeKey,brnachKey;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    private EditText searchText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users_of_a_branch);

        mtoolbar =(android.support.v7.widget.Toolbar)findViewById(R.id.user_toolBar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        searchText=(EditText)findViewById(R.id.searchText);
        mUserList=(RecyclerView)findViewById(R.id.users_RecyclerView);
        mUserList.setHasFixedSize(true);
        mUserList.setLayoutManager(new LinearLayoutManager(this));
        brnachKey=getIntent().getStringExtra("branchKey");
        yearKey=getIntent().getStringExtra("yearKey");
        fieldKey=getIntent().getStringExtra("fieldKey");
        collegeKey=getIntent().getStringExtra("collegeKey");
        mRootRef=FirebaseDatabase.getInstance().getReference();
        userDatabse= mRootRef.child("college").child(collegeKey).child("fieldOfWork").child(fieldKey).child("year").child(yearKey).child("branch").child(brnachKey).child("users");
       // userDatabse=mRootRef.child("college").child(collegeKey).child("users");
        userDatabse.keepSynced(true);
        mUserList.setAdapter(firebaseRecyclerAdapter);

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



    private void showUsers(String name){

        mRootRef.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("online").setValue("true");
        Query query=userDatabse.orderByChild("name").startAt(name);
        FirebaseRecyclerOptions<users> options =
                new FirebaseRecyclerOptions.Builder<users>()
                        .setQuery(query, users.class)
                        .build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<users, usersViewHolder>(options) {
                    @Override
                    public usersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.users_layout, parent, false);
                        return new usersViewHolder(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull final usersViewHolder viewHolder, int position, @NonNull users model) {
                        final String id=getRef(position).getKey();


                        mRootRef.child("deactivateAccount").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(id)){
                                    viewHolder.setName("Not Available");
                                    viewHolder.setImage("abc",getApplicationContext());
                                    viewHolder.setStatus("Deactivated Account");
                                    viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Toast.makeText(AllUsersOfABranch_Activity.this,"Deactivated Account",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                else {
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









                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent profileIntent=new Intent(AllUsersOfABranch_Activity.this,UserProfile_Activity.class);
                                profileIntent.putExtra("key_noti",id);
                                profileIntent.putExtra("request"," ");
                                profileIntent.putExtra("id",id);
                                startActivity(profileIntent);
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

    public static class usersViewHolder extends RecyclerView.ViewHolder{

         View mView;

        public usersViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
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
