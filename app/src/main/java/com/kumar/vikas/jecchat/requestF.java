package com.kumar.vikas.jecchat;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class requestF extends Fragment {

    private DatabaseReference mRequestRef;
    private DatabaseReference mRootRef;
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    public requestF() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mMainView;
        mMainView=inflater.inflate(R.layout.fragment_request, container, false);

        if(FirebaseAuth.getInstance().getCurrentUser()==null)
        {
            Intent intent=new Intent(getContext(),WelcomePage.class);
            startActivity(intent);
        }
        else{
        mRootRef=FirebaseDatabase.getInstance().getReference();
        mRequestRef= mRootRef.child("friendRequest").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        recyclerView=(RecyclerView)mMainView.findViewById(R.id.request_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        showRequests();
        }

        return mMainView;
    }



    public void showRequests() {


        if(FirebaseAuth.getInstance().getCurrentUser()==null)
        {
            Intent intent=new Intent(getContext(),WelcomePage.class);
            startActivity(intent);
        }
        else {

            FirebaseRecyclerOptions<request> options =
                    new FirebaseRecyclerOptions.Builder<request>()
                            .setQuery(mRequestRef, request.class)
                            .build();
            firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<request, requestViewHolder>(options)

             {
                 @Override
                 public requestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                     View view = LayoutInflater.from(parent.getContext())
                             .inflate(R.layout.users_layout, parent, false);

                     return new requestViewHolder(view);
                 }

                 @Override
                 protected void onBindViewHolder(@NonNull final requestViewHolder viewHolder, int position, @NonNull request model) {

                     final String requestUserId = getRef(position).getKey();
                     String reqType = model.getRequest_type();
                     if (reqType.equals("received")) {
                         mRootRef.child("users").child(requestUserId).addValueEventListener(new ValueEventListener() {
                             @Override
                             public void onDataChange(DataSnapshot dataSnapshot) {

                                 String name = dataSnapshot.child("name").getValue().toString();
                                 String image = dataSnapshot.child("thumbImage").getValue().toString();
                                 viewHolder.setName(name);
                                 viewHolder.setImage(image, getContext());
                                 viewHolder.setStatus("Request Received");
                                 viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View v) {
                                         Intent profileIntet = new Intent(getContext(), UserProfile_Activity.class);
                                         profileIntet.putExtra("from_user_Id", requestUserId);
                                         startActivity(profileIntet);
                                     }
                                 });


                             }

                             @Override
                             public void onCancelled(DatabaseError databaseError) {

                             }
                         });

                     }
                     if(reqType.equals("sent")){
                         mRootRef.child("users").child(requestUserId).addValueEventListener(new ValueEventListener() {
                             @Override
                             public void onDataChange(DataSnapshot dataSnapshot) {

                                 String name = dataSnapshot.child("name").getValue().toString();
                                 String image = dataSnapshot.child("thumbImage").getValue().toString();
                                 viewHolder.setName(name);
                                 viewHolder.setImage(image, getContext());
                                 viewHolder.setStatus("Request Sent");
                                 viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View v) {
                                         Intent profileIntet = new Intent(getContext(), UserProfile_Activity.class);
                                         profileIntet.putExtra("from_user_Id", requestUserId);
                                         startActivity(profileIntet);
                                     }
                                 });


                             }

                             @Override
                             public void onCancelled(DatabaseError databaseError) {

                             }
                         });






                     }
                 }

            };

            firebaseRecyclerAdapter.startListening();
            recyclerView.setAdapter(firebaseRecyclerAdapter);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
       // firebaseRecyclerAdapter.stopListening();
    }

    public static class requestViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public requestViewHolder(View itemView) {
            super(itemView);

            this.mView=itemView;
        }

        public void setName(String name){
            TextView sname=(TextView)mView.findViewById(R.id.notification);
            sname.setText(name);
        }

        public void setImage(String image, Context context){
            CircleImageView img=(CircleImageView)mView.findViewById(R.id.notiImage);
            Picasso.with(context).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.user).into(img);
        }

        public void setStatus(String sts){
            TextView status=(TextView)mView.findViewById(R.id.displayUserStatus);
            status.setText(sts);
        }
    }
}
