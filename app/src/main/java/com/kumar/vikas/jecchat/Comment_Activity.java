package com.kumar.vikas.jecchat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Comment_Activity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private EditText commentText;
    private ImageButton sendComment;
    private DatabaseReference mRootRef;
    private DatabaseReference mRef;
    private String userId;
    private String push_key;
    private Toolbar toolbar;
    private String postfrom;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    private String type,userName;
    private int k=0;
    private String nameOfPoster;
    private String typeNoti;
    private String collegeKey;
    private String count;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_);
        toolbar=(Toolbar)findViewById(R.id.commentToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("comments");
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        commentText=(EditText)findViewById(R.id.commentText);
        commentText.setFocusable(true);
        sendComment=(ImageButton)findViewById(R.id.sendComment);
        mRootRef= FirebaseDatabase.getInstance().getReference();
        push_key=getIntent().getStringExtra("push_key");
        collegeKey=getIntent().getStringExtra("college_key");
        mRef=mRootRef.child("timeLinePost").child("timeLineAllPost").child(collegeKey).child(push_key).child("userComments");

        mRootRef.child("timeLinePost").child("timeLineAllPost").child(collegeKey).child(push_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("from"))
                postfrom=dataSnapshot.child("from").getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRootRef.child("deactivateAccount").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (postfrom!=null)
                if (dataSnapshot.hasChild(postfrom)){
                    sendComment.setEnabled(false);
                    commentText.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        mRootRef.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("online").setValue("true");
        userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        linearLayoutManager=new LinearLayoutManager(this);
        recyclerView=(RecyclerView)findViewById(R.id.commentRecyclerView);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        mRootRef.child("users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userName=dataSnapshot.child("name").getValue().toString();
                type=userName+" has commented on your post";
                typeNoti=userName+" has liked your comment";
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        showComments();

        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cText=commentText.getText().toString();
                if (cText.equals("")||cText==null){
                    Toast.makeText(Comment_Activity.this,"message is empty",Toast.LENGTH_SHORT).show();
                }
                else
                {

                    DatabaseReference cPush_ref=mRef.push();
                    final String cPush_key=cPush_ref.getKey();

                    commentText.setText("");
                    String time=DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date())+" "+DateFormat.getDateInstance().format(new Date());
                    Map<String,String> map=new HashMap<>();
                    map.put("comment_Text",cText);
                    map.put("image","null");
                    map.put("video","null");
                    map.put("file","null");
                    map.put("time", time.substring(0,time.length()-5));
                    map.put("from", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    map.put("likes","0");
                    map.put("type","text");
                    map.put("reply", "0");
                    map.put("cPush_key",cPush_key);
                    map.put("push_key",push_key);
                    mRef.child(cPush_key).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {

                                commentText.setText("");
                                mRootRef.child("timeLinePost").child("timeLineAllPost").child(collegeKey).child(push_key).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final String no=dataSnapshot.child("comments").getValue().toString();
                                        k=Integer.parseInt(no);
                                        mRootRef.child("timeLinePost").child("timeLineAllPost").child(collegeKey).child(push_key).child("comments").setValue(String.valueOf(dataSnapshot.child("userComments").getChildrenCount())).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){

                                                    if(!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(postfrom)){
                                                    DatabaseReference notiref=mRootRef.child("notifications").child(postfrom).push();
                                                    String notirefid=notiref.getKey();

                                                    if (k>1){
                                                       // type=userName+" and "+String.valueOf(k-1)+" others commented on your post";
                                                    }
                                                    HashMap notiHash=new HashMap<>();
                                                    notiHash.put("from",FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                    notiHash.put("type",type);
                                                    notiHash.put("key",push_key);
                                                    notiHash.put("clickAction","JecChat.Notification.Notification");
                                                    notiHash.put("time",ServerValue.TIMESTAMP);
                                                    mRootRef.child("notifications").child(postfrom).child(push_key+" Comments").setValue(notiHash);
                                                    }
                                                    Toast.makeText(getApplicationContext(), "Comment Posted", Toast.LENGTH_SHORT).show();

                                                }
                                            }
                                        });

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }

                        }
                    });




                }


            }
        });


    }



    private void showComments(){


        FirebaseRecyclerOptions<Comments> options =
                new FirebaseRecyclerOptions.Builder<Comments>()
                        .setQuery(mRef, Comments.class)
                        .build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Comments, CommentViewHolder>(options) {
            @Override
            public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.time_line_post_layout, parent, false);

                return new CommentViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final CommentViewHolder viewHolder, int position, @NonNull final Comments model) {


                final String comnt=getRef(position).getKey();



                mRootRef.child("deactivateAccount").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(model.getFrom())){
                            viewHolder.displayPostName.setText("Not available");
                            nameOfPoster="Not available";
                            viewHolder.branchSem.setText("");
                            viewHolder.like.setEnabled(false);

                        }
                        else {

                            mRootRef.child("users").child(model.getFrom()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    nameOfPoster = dataSnapshot.child("name").getValue().toString();
                                    final String image = dataSnapshot.child("thumbImage").getValue().toString();
                                    viewHolder.displayPostName.setText(nameOfPoster);
                                    viewHolder.branchSem.setText("(" + dataSnapshot.child("branch").getValue().toString() + "," + dataSnapshot.child("sem").getValue().toString() + ")");

                                    Picasso.with(viewHolder.postUserImage.getContext()).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.user).into(viewHolder.postUserImage, new Callback() {
                                        @Override
                                        public void onSuccess() {

                                        }

                                        @Override
                                        public void onError() {
                                            Picasso.with(viewHolder.postUserImage.getContext()).load(image).placeholder(R.drawable.user).into(viewHolder.postUserImage);
                                        }
                                    });

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

                mRef.child(comnt).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child("userLikes").hasChild(userId)){
                            viewHolder.like.setAlpha((float)1);
                        }
                        else
                            viewHolder.like.setAlpha((float)0.7);
                        viewHolder.no_like.setText(String.valueOf(dataSnapshot.child("userLikes").getChildrenCount())+" likes");
                        viewHolder.no_comments.setText(String.valueOf(dataSnapshot.child("replies").getChildrenCount())+" replies");

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                viewHolder.postMessage.setText(model.getComment_Text());
                viewHolder.postTime.setText(model.getTime());
                viewHolder.displayPostName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profileIntent = new Intent(viewHolder.displayPostName.getContext(), UserProfile_Activity.class);
                        profileIntent.putExtra("key_noti", model.getFrom());
                        profileIntent.putExtra("request", " ");
                        startActivity(profileIntent);
                    }
                });



              /*  if (!model.getVideo().equals("null")) {
                    viewHolder.imagePost.setEnabled(false);
                    viewHolder.imagePost.setVisibility(View.INVISIBLE);
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewHolder.videoPost.getLayoutParams();
                    layoutParams.setMargins(0, 0, 0, 0);
                    viewHolder.videoPost.setVideoURI(Uri.parse(model.getVideo()));
                    viewHolder.videoPost.seekTo(200);

                    viewHolder.videoPost.start();
                    viewHolder.videoPost.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                        @Override
                        public void onPrepared(MediaPlayer m) {
                            try {
                                if (m.isPlaying()) {
                                    m.stop();
                                    m.release();
                                    m = new MediaPlayer();
                                }
                                m.setVolume(0f, 0f);
                                m.setLooping(false);
                                m.start();
                                viewHolder.playVideo.setImageResource(R.drawable.ic_pause_circle_outline_white_24dp);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    viewHolder.progressBar.setVisibility(View.INVISIBLE);
                    viewHolder.videoPost.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                        @Override
                        public boolean onInfo(MediaPlayer mp, int what, int extra) {
                            if (what == mp.MEDIA_INFO_BUFFERING_START) {
                                viewHolder.progressBar.setVisibility(View.VISIBLE);
                                viewHolder.playVideo.setImageResource(R.drawable.ic_play_circle_outline_white_24dp);
                                viewHolder.playVideo.setVisibility(View.INVISIBLE);
                            } else if (what == mp.MEDIA_INFO_BUFFERING_END) {
                                viewHolder.progressBar.setVisibility(View.INVISIBLE);
                                viewHolder.playVideo.setImageResource(R.drawable.ic_pause_circle_outline_white_24dp);
                            }
                            return false;
                        }
                    });
                    viewHolder.playVideo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Context context=viewHolder.playVideo.getContext();
                            Intent intent=new Intent(context,VideoPost_Activity.class);
                            intent.putExtra("videoUrl",model.getVideo());
                            intent.putExtra("name",nameOfPoster);
                            context.startActivity(intent);

                        }
                    });
                } else {
                    viewHolder.videoPost.setVisibility(View.INVISIBLE);
                    viewHolder.playVideo.setVisibility(View.INVISIBLE);
                    viewHolder.progressBar.setVisibility(View.INVISIBLE);
                    viewHolder.imagePost.setVisibility(View.VISIBLE);
                    viewHolder.imagePost.setEnabled(true);
                }*/
                viewHolder.videoPost.setVisibility(View.INVISIBLE);
                viewHolder.playVideo.setVisibility(View.INVISIBLE);
                viewHolder.progressBar.setVisibility(View.INVISIBLE);
                viewHolder.imagePost.setVisibility(View.VISIBLE);
                viewHolder.imagePost.setEnabled(true);

                mRootRef.child("notiCount").child(model.getFrom()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("count"))
                            count=dataSnapshot.child("count").getValue().toString();
                        else count="0";
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                viewHolder.like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mRef.child(comnt).child("userLikes").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(final DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild(userId))
                                {viewHolder.like.setAlpha((float)0.7);
                                    mRef.child(comnt).child("userLikes").child(userId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){

                                                viewHolder.like.setAlpha((float)0.7);
                                                viewHolder.no_like.setText(String.valueOf(dataSnapshot.getChildrenCount()-1)+" likes");}
                                        }
                                    });


                                }
                                else
                                {viewHolder.like.setAlpha((float)1);
                                    mRef.child(comnt).child("userLikes").child(userId).child("like").setValue("liked").addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){
                                                if(!userId.equals(model.getFrom())) {
                                                    DatabaseReference notiref = mRootRef.child("notifications").child(model.getFrom()).push();
                                                    String notirefid = notiref.getKey();
                                                    if (dataSnapshot.getChildrenCount()>=1){
                                                        typeNoti=userName+" and"+String.valueOf(dataSnapshot.getChildrenCount())+" others has liked your comment";
                                                    }
                                                    HashMap notiHash = new HashMap<>();
                                                    notiHash.put("from", userId);
                                                    notiHash.put("type", typeNoti);
                                                    notiHash.put("key",push_key+" "+comnt);
                                                    notiHash.put("clickAction","JecChat.Notification.Notification");
                                                    notiHash.put("time", ServerValue.TIMESTAMP);
                                                    mRootRef.child("notifications").child(model.getFrom()).child(push_key+" "+comnt+" like").setValue(notiHash);
                                                    mRootRef.child("notiCount").child(model.getFrom()).child("count").setValue(String.valueOf(Integer.parseInt(count)+1));

                                                    viewHolder.like.setAlpha((float)1);
                                                    viewHolder.no_like.setText(String.valueOf(dataSnapshot.getChildrenCount()+1)+" likes");
                                                }
                                            }
                                        }
                                    });

                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });




                    }
                });

                int color = Color.parseColor("#29434e");
                viewHolder.like.setColorFilter(color);
                viewHolder.reply.setColorFilter(color);


                viewHolder.reply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Context context = viewHolder.reply.getContext();
                        Intent intent = new Intent(Comment_Activity.this, Reply_Activity.class);
                        intent.putExtra("push_key", push_key+" "+comnt);
                        intent.putExtra("college_key", collegeKey);
                        startActivity(intent);
                    }
                });
            }


        };

        firebaseRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = firebaseRecyclerAdapter.getItemCount();
                int lastVisiblePosition =
                        linearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    recyclerView.scrollToPosition(positionStart);
                }
            }
        });
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

    public static class CommentViewHolder extends RecyclerView.ViewHolder {

        public TextView displayPostName;
        public TextView postTime;
        public TextView postMessage;
        public CircleImageView postUserImage;
        public View itemView;
        public ImageButton like;
        public ImageButton reply;
        public String st = "not_liked";
        public TextView no_like;
        public TextView no_comments;
        private DatabaseReference mRootRef;
        ImageView imagePost;
        VideoView videoPost;
        ImageButton playVideo;
        ProgressBar progressBar;
        boolean isPlaying=false;
        TextView branchSem;

        public CommentViewHolder(View itemView) {
            super(itemView);
            this.itemView=itemView;
            branchSem=(TextView)itemView.findViewById(R.id.branchSem);
            displayPostName = (TextView) itemView.findViewById(R.id.timelinePostName);
            postMessage = (TextView) itemView.findViewById(R.id.timelinePostMessage);
            postTime = (TextView) itemView.findViewById(R.id.timeLinePostTime);
            postUserImage = (CircleImageView) itemView.findViewById(R.id.timelinePostImage);
            like = (ImageButton) itemView.findViewById(R.id.like);
            reply = (ImageButton) itemView.findViewById(R.id.comment);
            no_comments = (TextView) itemView.findViewById(R.id.no_comments);
            no_like = (TextView) itemView.findViewById(R.id.no_likes);
            videoPost=(VideoView)itemView.findViewById(R.id.videoView);
            playVideo=(ImageButton)itemView.findViewById(R.id.playVideo);
            progressBar=(ProgressBar)itemView.findViewById(R.id.progressBar3);
            imagePost=(ImageView)itemView.findViewById(R.id.imageView2);

            mRootRef = FirebaseDatabase.getInstance().getReference();
        }
    }
}
