package com.kumar.vikas.jecchat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAllPost_Activity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private DatabaseReference mrootRef;
    private DatabaseReference mRef;
    private String userId;
    private LinearLayoutManager linearLayoutManager;
    private String nameOfPoster;
    private String message;
    private String image;
    private String video;
    private String college;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_all_post_);

        toolbar=(Toolbar)findViewById(R.id.userAllToolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All Posts");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        userId=FirebaseAuth.getInstance().getCurrentUser().getUid();

        mrootRef= FirebaseDatabase.getInstance().getReference();

        mrootRef.child("users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                college=dataSnapshot.child("college").getValue().toString();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRef=mrootRef.child("timeLinePost").child("timeLineUserPost").child(userId);
        mRef.keepSynced(true);

        recyclerView = (RecyclerView)findViewById(R.id.userAllRecyclerView);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        showPost();
    }



    private void showPost() {

        FirebaseRecyclerOptions<post> options =
                new FirebaseRecyclerOptions.Builder<post>()
                        .setQuery(mRef, post.class)
                        .build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<post, userAllPostViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final userAllPostViewHolder viewHolder, int position, @NonNull final post model) {
                final String post=getRef(position).getKey();

                // currentVisiblePosition=viewHolder.getLayoutPosition();
                final String refKey=getRef(position).getKey();
                final String from=model.getFrom();

                mRef.child(refKey).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild("message")){
                            message=dataSnapshot.child("message").getValue().toString();
                            image=dataSnapshot.child("image").getValue().toString();
                            video=dataSnapshot.child("video").getValue().toString();}



                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                 int color= Color.parseColor("#29434e");
                 viewHolder.like.setColorFilter(color);
                 viewHolder.comment.setColorFilter(color);

                mrootRef.child("users").child(from).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        viewHolder.setName(dataSnapshot.child("name").getValue().toString());
                        viewHolder.setImage(dataSnapshot.child("thumbImage").getValue().toString(),getApplicationContext());
                        viewHolder.branchSem.setText("("+dataSnapshot.child("branch").getValue().toString()+","+dataSnapshot.child("sem").getValue().toString()+")");
                        nameOfPoster=dataSnapshot.child("name").getValue().toString();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                viewHolder.view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {

                        AlertDialog.Builder builder=new AlertDialog.Builder(UserAllPost_Activity.this);
                        builder.setMessage("Do  you want to");
                        builder.setPositiveButton("Edit this post", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent =new Intent(UserAllPost_Activity.this,Post_Activity.class);
                                intent.putExtra("message",model.getMessage());
                                intent.putExtra("image",model.getImage());
                                intent.putExtra("video",model.getVideo());
                                intent.putExtra("ref",refKey);
                                intent.putExtra("college",model.getCollege());
                                startActivity(intent);
                            }
                        });
                        builder.setNeutralButton("Delete this post", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                AlertDialog.Builder builder1=new AlertDialog.Builder(UserAllPost_Activity.this);
                                builder1.setMessage("Are you sure...");
                                builder1.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {



                                        mRef.child(refKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()) {
                                                    mrootRef.child("timeLinePost").child("timeLineAllPost").child(model.getCollege()).child(refKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(UserAllPost_Activity.this, "post deleted", Toast.LENGTH_LONG).show();
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                });
                                builder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                builder1.show();
                            }
                        });
                        builder.show();
                        return false;
                    }
                });




                mrootRef.child("timeLinePost").child("timeLineAllPost").child(college).child(post).child("userLiked").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(userId))
                        {
                            viewHolder.like.setAlpha((float)1);

                            viewHolder.no_like.setText(String.valueOf(dataSnapshot.getChildrenCount())+" likes");
                        }
                        else
                        {

                            viewHolder.like.setAlpha((float)0.7);
                            viewHolder.no_like.setText(String.valueOf(dataSnapshot.getChildrenCount())+" likes");
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                mrootRef.child("timeLinePost").child("timeLineAllPost").child(college).child(post).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        viewHolder.setLikes(String.valueOf(dataSnapshot.child("userLiked").getChildrenCount()));
                        viewHolder.setComments(String.valueOf(dataSnapshot.child("userComments").getChildrenCount()));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                viewHolder.setTime(model.getTime());
                viewHolder.setMessage(model.getMessage());

                if (!model.getVideo().equals("null")) {
                    viewHolder.imagePost.setEnabled(false);
                    viewHolder.imagePost.setVisibility(View.INVISIBLE);
                    viewHolder.videoPost.setVisibility(View.VISIBLE);
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
                }
                final Picasso picasso=Picasso.with(viewHolder.imagePost.getContext());
                picasso.setIndicatorsEnabled(false);

                picasso.load(model.getImage()).resize(1000,1000).centerCrop().networkPolicy(NetworkPolicy.OFFLINE).into(viewHolder.imagePost, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(viewHolder.imagePost.getContext()).load(model.getImage()).resize(1000,1000).centerCrop().into(viewHolder.imagePost);
                    }
                });

                if(model.getType().equals("file")){

                    final int dr;
                    if(model.getFileName().contains("pdf")){
                        dr=R.drawable.a_pdf;}
                    else if(model.getFileName().contains("doc")){
                        dr=R.drawable.a_doc;}
                    else if(model.getFileName().contains("ppt"))
                    {dr=R.drawable.a_ppt;}
                    else if(model.getFileName().contains("zip"))
                    {dr=R.drawable.a_zip;}
                    else if(model.getFileName().contains("txt"))
                    {dr=R.drawable.a_txt;}
                    else if(model.getFileName().contains("html"))
                    {dr=R.drawable.a_html;}
                    else if(model.getFileName().contains("raw"))
                    {dr=R.drawable.a_raw;}
                    else if(model.getFileName().contains("gif"))
                    {dr=R.drawable.a_gif;}
                    else dr=R.drawable.a_folder;

                    picasso.load(dr).resize(100,100).centerCrop().networkPolicy(NetworkPolicy.OFFLINE).into(viewHolder.imagePost, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            picasso.load(dr).resize(100,100).centerCrop().into(viewHolder.imagePost);
                        }
                    });
                }




                viewHolder.no_like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(UserAllPost_Activity.this,LikedUsers_Activity.class);
                        intent.putExtra("push_key",post);
                        startActivity(intent);
                    }
                });
                viewHolder.imagePost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent=new Intent(UserAllPost_Activity.this,Image_view_Activity.class);
                        intent.putExtra("imageUrl",model.getImage());
                        intent.putExtra("name",nameOfPoster);
                        startActivity(intent);

                    }
                });

                viewHolder.pname.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profileIntent=new Intent(getApplicationContext(),UserProfile_Activity.class);
                        profileIntent.putExtra("Id",from);
                        startActivity(profileIntent);
                    }
                });
                viewHolder.like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {



                        mrootRef.child("timeLinePost").child("timeLineAllPost").child(college).child(post).child("userLiked").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(final DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild(userId))
                                {viewHolder.like.setAlpha((float)0.7);
                                    mrootRef.child("timeLinePost").child("timeLineAllPost").child(college).child(post).child("userLiked").child(userId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
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
                                    mrootRef.child("timeLinePost").child("timeLineAllPost").child(college).child(post).child("userLiked").child(userId).child("like").setValue("liked").addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){
                                                if(!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(from)) {
                                                    DatabaseReference notiref = mrootRef.child("notifications").child(from).push();
                                                    String notirefid = notiref.getKey();

                                                    HashMap<String, String> notiHash = new HashMap<>();
                                                    notiHash.put("from", userId);
                                                    notiHash.put("type", "liked your post");
                                                    notiHash.put("key",post);
                                                    notiHash.put("time", DateFormat.getDateTimeInstance().format(new Date()));
                                                    mrootRef.child("notifications").child(from).child(notirefid).setValue(notiHash);

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
                viewHolder.comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Context context=viewHolder.comment.getContext();
                        Intent intent=new Intent(context,Comment_Activity.class);
                        intent.putExtra("push_key", post);
                        intent.putExtra("collegeKey", college);
                        context.startActivity(intent);




                    }
                });
            }

            @Override
            public userAllPostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.time_line_post_layout, parent, false);

                return new userAllPostViewHolder(view);
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

    public static class userAllPostViewHolder extends RecyclerView.ViewHolder {
        ImageButton like,comment;
        ImageButton playVideo;
        TextView cmnt;
        TextView no_like;
        ImageView imagePost;
        VideoView videoPost;
        TextView branchSem;
        View view;
        DatabaseReference mrootRef;
        ImageButton menuButton;
        TextView pname;
        ProgressBar progressBar;
        public userAllPostViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            like = (ImageButton) view.findViewById(R.id.like);
            comment = (ImageButton) view.findViewById(R.id.comment);
            cmnt = (TextView) view.findViewById(R.id.no_comments);
            no_like = (TextView) view.findViewById(R.id.no_likes);
            imagePost=(ImageView)itemView.findViewById(R.id.imageView2);
            branchSem=(TextView)itemView.findViewById(R.id.branchSem);
            pname=(TextView)view.findViewById(R.id.timelinePostName);
            mrootRef = FirebaseDatabase.getInstance().getReference();
            menuButton=(ImageButton)view.findViewById(R.id.menuButton);
            videoPost=(VideoView)view.findViewById(R.id.videoView);
            playVideo=(ImageButton)itemView.findViewById(R.id.playVideo);
            progressBar=(ProgressBar)itemView.findViewById(R.id.progressBar3);


        }




        public void setImage(final String thumbImage, final Context context) {
            final CircleImageView image=(CircleImageView)view.findViewById(R.id.timelinePostImage);
            Picasso.with(context).load(thumbImage).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.user).into(image, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(context).load(thumbImage).placeholder(R.drawable.user).into(image);
                }
            });

        }

        public void setName(String name) {

            pname.setText(name);

        }

        public void setComments(String comments) {

            cmnt.setText(comments+" comments");
        }

        public void setLikes(String likes) {

            no_like.setText(likes+" likes");
        }

        public void setMessage(String message) {
            TextView msg=(TextView)view.findViewById(R.id.timelinePostMessage);
            msg.setText(message);
        }

        public void setTime(String time) {
            TextView tm=(TextView)view.findViewById(R.id.timeLinePostTime);
            tm.setText(time);

        }

        public void setPostImage(final String postImage, final Context context) {
            final CircleImageView image=(CircleImageView)view.findViewById(R.id.timelinePostImage);
            Picasso.with(context).load(postImage).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.user).into(image, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(context).load(postImage).placeholder(R.drawable.user).into(image);
                }
            });

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        mrootRef.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("online").setValue("true");

    }

    @Override
    protected void onPause() {
        super.onPause();
        mrootRef.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("online").setValue(ServerValue.TIMESTAMP);

    }
}
