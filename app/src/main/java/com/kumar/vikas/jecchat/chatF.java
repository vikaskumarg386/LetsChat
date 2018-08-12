package com.kumar.vikas.jecchat;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class chatF extends Fragment {


    private RecyclerView recyclerView;
    private View mview;
    private DatabaseReference mRef;
    private DatabaseReference mRootref;
    private LinearLayoutManager linearLayoutManager;
    private String year,college,fieldOfWork;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;


    public chatF() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mview = inflater.inflate(R.layout.fragment_chat, container, false);

        if(FirebaseAuth.getInstance().getCurrentUser()==null)
        {
            Intent intent=new Intent(getContext(),WelcomePage.class);
            startActivity(intent);
        }
        else{
        recyclerView = (RecyclerView) mview.findViewById(R.id.recyclerview_chat_fragment);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        mRef = FirebaseDatabase.getInstance().getReference().child("chat").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mRootref = FirebaseDatabase.getInstance().getReference();

        mRootref.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("college")){
                year=dataSnapshot.child("graduationYear").getValue().toString();
                college=dataSnapshot.child("college").getValue().toString();
                fieldOfWork=dataSnapshot.child("fieldOfWork").getValue().toString();}
                else {
                    Intent intent=new Intent(getContext(),SelectCollege_Activity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        showChat();
        }


        return mview;
    }


    public void showChat() {

        if(FirebaseAuth.getInstance().getCurrentUser()==null)
        {
            Intent intent=new Intent(getContext(),WelcomePage.class);
            startActivity(intent);
        }
        else {

            Query chatQuery = mRef.orderByChild("timeStamp");
            chatQuery.keepSynced(true);

            FirebaseRecyclerOptions<chat> options =
                    new FirebaseRecyclerOptions.Builder<chat>()
                            .setQuery(chatQuery, chat.class)
                            .build();
            firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<chat, chatListViewHolder>(options) {
                @Override
                public chatListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.users_layout, parent, false);

                    return new chatListViewHolder(view);
                }

                @Override
                protected void onBindViewHolder(@NonNull final chatListViewHolder viewHolder, final int position, @NonNull final chat model) {

                    final String chatFriendId = getRef(position).getKey();


                    Log.i("group key",chatFriendId);

                    if (chatFriendId.contains("groupId")){

                        mRootref.child("group").child(chatFriendId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final String name=dataSnapshot.child("name").getValue().toString();
                                final String image=dataSnapshot.child("thumbImage").getValue().toString();
                                viewHolder.setName(name);
                                viewHolder.setImage(image,getContext());
                                viewHolder.setStatus(model.getLastMessage(), model.getSeen());

                                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                        chatIntent.putExtra("key_noti", chatFriendId);
                                        mRef.child(chatFriendId).child("seen").setValue(true);
                                        startActivity(chatIntent);
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }
                    else if(!(chatFriendId.equals("me")||chatFriendId.equals("ce")||chatFriendId.equals("ee")||chatFriendId.equals("ec")||chatFriendId.equals("cse")||chatFriendId.equals("it")||chatFriendId.equals("ip")||chatFriendId.contains("groupId")||chatFriendId.contains("branchId"))) {

                        mRootref.child("users").child(chatFriendId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if(dataSnapshot.hasChild("name")) {
                                    final String name = dataSnapshot.child("name").getValue().toString();
                                    viewHolder.setName(name);
                                    final String image = dataSnapshot.child("thumbImage").getValue().toString();
                                    viewHolder.setImage(image, getContext());
                                    mRootref.child("deactivateAccount").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.hasChild(chatFriendId)) {
                                                viewHolder.setImage("abc", getContext());
                                                // viewHolder.setStatus("Deactivated Account");

                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                    viewHolder.view.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                            chatIntent.putExtra("key_noti", chatFriendId);
                                            mRef.child(chatFriendId).child("seen").setValue(true);
                                            startActivity(chatIntent);
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }


                        });
                        viewHolder.setStatus(model.getLastMessage(),model.getSeen());

                    }

                    else if ((chatFriendId.equals("ip")||chatFriendId.equals("me")||chatFriendId.equals("ce")||chatFriendId.equals("it")||chatFriendId.equals("ee")||chatFriendId.equals("ec")||chatFriendId.equals("cse")||chatFriendId.equals("ip")||chatFriendId.contains("branchId"))){

                        mRootref.child("college").child(model.getCollege()).child("fieldOfWork").child(model.getFieldOfWork()).child("year").child(model.getYear()).child("branch").child(chatFriendId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild("name")){
                                final String name=dataSnapshot.child("name").getValue().toString();
                                    viewHolder.setName(name.toUpperCase());}
                                if (dataSnapshot.hasChild("thumbImage")){
                                final String image=dataSnapshot.child("thumbImage").getValue().toString();
                                    viewHolder.setImage(image,getContext());}


                                viewHolder.setStatus(model.getLastMessage(), model.getSeen());

                                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                        chatIntent.putExtra("key_noti", chatFriendId);
                                        mRef.child(chatFriendId).child("seen").setValue(true);
                                        startActivity(chatIntent);
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    mRootref.child("chat").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(chatFriendId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild("seen")){
                            if (dataSnapshot.child("seen").getValue().toString().equals("false")){
                                viewHolder.newMessage.setVisibility(View.VISIBLE);
                            }
                            else viewHolder.newMessage.setVisibility(View.INVISIBLE);}
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    viewHolder.view.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {


                            AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                            builder.setMessage("Do you want to  this chat");
                            builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    mRef.child(chatFriendId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                mRootref.child("messages").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(chatFriendId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {

                                                            Toast.makeText(getContext(), "removed", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }

                                        }
                                    });

                                }
                            });
                            builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });

                            builder.show();

                            return false;
                        }
                    });
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

    public static class chatListViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView newMessage;

        public chatListViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            newMessage=itemView.findViewById(R.id.newMessage);
        }

        public void setName(String name) {
            TextView chatUserName = (TextView) view.findViewById(R.id.notification);
            chatUserName.setText(name);
        }

        public void setImage(final String image, final Context context) {
            final CircleImageView chatFriendImage = (CircleImageView) view.findViewById(R.id.notiImage);
            Picasso.with(context).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.user).into(chatFriendImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(context).load(image).placeholder(R.drawable.user).into(chatFriendImage);
                }
            });
        }

        public void setStatus(String lastMessage, boolean seen){
            TextView status=(TextView)view.findViewById(R.id.displayUserStatus);
            if(seen==true) {
                if (lastMessage.length() >= 31) {
                    status.setText(lastMessage.substring(0, 30) + "...");
                } else
                    status.setText(lastMessage);
            }
            else
            {
                if (lastMessage.length() >= 31) {
                    status.setText(lastMessage.substring(0, 30) + "...");
                } else
                    status.setText(lastMessage);

                status.setTextColor(Color.BLACK);
            }

        }
    }
}

