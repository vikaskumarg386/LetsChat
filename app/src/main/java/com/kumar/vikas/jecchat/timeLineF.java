package com.kumar.vikas.jecchat;


import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.DOWNLOAD_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class timeLineF extends Fragment {


    private RecyclerView recyclerView;

    private DatabaseReference mrootRef;
    private LinearLayoutManager linearLayoutManager;
    private DatabaseReference mRef;
    private String userId;
    private long currentVisiblePosition;
    private String nameOfPoster;
    private FloatingActionButton actionButton;
    private FirebaseRecyclerAdapter<post,postViewHolder> firebaseRecyclerAdapter;
    private long queiId;
    private DownloadManager dm;
    private String iMageUri;
    private boolean downloading = true;
    private String typeNoti,userName;
    private String college;
    private int n=0;
    private String count;


    public timeLineF() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mMainView;
        mMainView=inflater.inflate(R.layout.fragment_time_line, container, false);

        if(FirebaseAuth.getInstance().getCurrentUser()==null)
        {
            Intent mainIntent=new Intent(getContext(),WelcomePage.class);
            startActivity(mainIntent);
        }
        else {



            recyclerView = (RecyclerView) mMainView.findViewById(R.id.timelineRecyclerView);
            recyclerView.setHasFixedSize(true);
            recyclerView.setItemViewCacheSize(20);
            recyclerView.setDrawingCacheEnabled(true);
            recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            linearLayoutManager = new LinearLayoutManager(getContext());
            linearLayoutManager.setReverseLayout(true);
            linearLayoutManager.setStackFromEnd(true);
            recyclerView.setLayoutManager(linearLayoutManager);

            actionButton=(FloatingActionButton)mMainView.findViewById(R.id.add_post_floting_button);
            actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(getContext(),Post_Activity.class);
                    intent.putExtra("message","");
                    intent.putExtra("image","null");
                    intent.putExtra("video","null");
                    intent.putExtra("ref","null");
                    startActivity(intent);
                }
            });

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (dy>0){
                        actionButton.hide();
                    }
                    else if (dy<0){
                        actionButton.show();
                    }

                }
            });


            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            mrootRef=FirebaseDatabase.getInstance().getReference();


            mrootRef.child("users").child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    userName=dataSnapshot.child("name").getValue().toString();
                    /*if (dataSnapshot.hasChild("college")){
                    college=dataSnapshot.child("college").getValue().toString();}*/
                    typeNoti=userName+" has liked your post";
                    mrootRef= FirebaseDatabase.getInstance().getReference();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            showPost();



        }

        return mMainView;
    }



    public void showPost() {


       if (FirebaseAuth.getInstance().getCurrentUser() == null) {
           Intent mainIntent = new Intent(getContext(), WelcomePage.class);
           startActivity(mainIntent);
       }
       else{
           mrootRef.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(DataSnapshot dataSnapshot) {
                   if (dataSnapshot.hasChild("college")){
                       college=dataSnapshot.child("college").getValue().toString();

                       mRef=mrootRef.child("timeLinePost").child("timeLineAllPost").child(college);
                       mRef.keepSynced(true);
                       FirebaseRecyclerOptions<post> options =
                               new FirebaseRecyclerOptions.Builder<post>()
                                       .setQuery(mRef, post.class)
                                       .build();
                       FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<post, postViewHolder>(options)  {
                           @Override
                           public postViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                               View view = LayoutInflater.from(parent.getContext())
                                       .inflate(R.layout.time_line_post_layout, parent, false);

                               return new postViewHolder(view);
                           }

                           @Override
                           protected void onBindViewHolder(@NonNull final postViewHolder viewHolder, final int position, @NonNull final post model) {

                               final String post = getRef(position).getKey();

                               final String from = model.getFrom();

                               college=model.getCollege();

                               mrootRef.child("deactivateAccount").addValueEventListener(new ValueEventListener() {
                                   @Override
                                   public void onDataChange(DataSnapshot dataSnapshot) {
                                       if (dataSnapshot.hasChild(model.getFrom())){

                                           viewHolder.setName("Not available");
                                           nameOfPoster="Not available";
                                           viewHolder.branchSem.setText("");
                                           viewHolder.like.setEnabled(false);

                                       }
                                       else {

                                           mrootRef.child("users").child(from).addValueEventListener(new ValueEventListener() {
                                               @Override
                                               public void onDataChange(DataSnapshot dataSnapshot) {
                                                   viewHolder.setName(dataSnapshot.child("name").getValue().toString());
                                                   viewHolder.setImage(dataSnapshot.child("thumbImage").getValue().toString(), getContext());
                                                   viewHolder.branchSem.setText("(" + dataSnapshot.child("branch").getValue().toString().toUpperCase() + "," + dataSnapshot.child("sem").getValue().toString() + ")");
                                                   nameOfPoster = dataSnapshot.child("name").getValue().toString();
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

                               currentVisiblePosition = viewHolder.getLayoutPosition();

                               int color = Color.parseColor("#29434e");
                               viewHolder.comment.setColorFilter(color);


                               viewHolder.menuButton.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View v) {
                                       PopupMenu popupMenu = new PopupMenu(getContext(), viewHolder.menuButton);
                                       popupMenu.getMenuInflater().inflate(R.menu.user_post_menu, popupMenu.getMenu());
                                       popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                           @Override
                                           public boolean onMenuItemClick(MenuItem item) {
                                               return false;
                                           }
                                       });
                                       popupMenu.show();
                                   }
                               });
                               mrootRef.child("timeLinePost").child("timeLineAllPost").child(college).child(post).child("userLiked").addListenerForSingleValueEvent(new ValueEventListener() {
                                   @Override
                                   public void onDataChange(DataSnapshot dataSnapshot) {
                                       if (dataSnapshot.hasChild(userId)) {
                                           int color = Color.parseColor("#29434e"); //The color u want
                                           viewHolder.like.setColorFilter(color);
                                           viewHolder.like.setAlpha((float) 1);
                                           viewHolder.no_like.setText(String.valueOf(dataSnapshot.getChildrenCount()) + " likes");
                                       } else {
                                           int color = Color.parseColor("#29434e"); //The color u want
                                           viewHolder.like.setColorFilter(color);
                                           viewHolder.like.setAlpha((float) 0.7);
                                           viewHolder.no_like.setText(String.valueOf(dataSnapshot.getChildrenCount()) + " likes");
                                       }
                                   }
                                   // @Override
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

                                           mrootRef.child("users").child(model.getFrom()).addValueEventListener(new ValueEventListener() {
                                               @Override
                                               public void onDataChange(DataSnapshot dataSnapshot) {
                                                   nameOfPoster=dataSnapshot.child("name").getValue().toString();
                                               }

                                               @Override
                                               public void onCancelled(DatabaseError databaseError) {

                                               }
                                           });
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
                               // viewHolder.setPostImage(model.getImage(),getContext());
                               final Picasso picasso = Picasso.with(viewHolder.imagePost.getContext());
                               picasso.setIndicatorsEnabled(false);



                               picasso.load(model.getImage()).resize(1000, 1000).centerCrop().networkPolicy(NetworkPolicy.OFFLINE).into(viewHolder.imagePost, new Callback() {
                                   @Override
                                   public void onSuccess() {
                                   }

                                   @Override
                                   public void onError() {
                                       picasso.load(model.getImage()).resize(1000, 1000).centerCrop().into(viewHolder.imagePost);
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
                                       Intent intent = new Intent(viewHolder.no_like.getContext(), LikedUsers_Activity.class);
                                       intent.putExtra("push_key", post);
                                       intent.putExtra("college_key",college);
                                       startActivity(intent);
                                   }
                               });
                               viewHolder.imagePost.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View v) {

                                       if (!model.getImage().equals("null")){
                                           mrootRef.child("users").child(model.getFrom()).addValueEventListener(new ValueEventListener() {
                                               @Override
                                               public void onDataChange(DataSnapshot dataSnapshot) {
                                                   nameOfPoster=dataSnapshot.child("name").getValue().toString();
                                               }

                                               @Override
                                               public void onCancelled(DatabaseError databaseError) {

                                               }
                                           });
                                           Context context = viewHolder.comment.getContext();
                                           Intent intent = new Intent(context, Image_view_Activity.class);
                                           intent.putExtra("imageUrl", model.getImage());
                                           intent.putExtra("name", nameOfPoster);
                                           context.startActivity(intent);
                                       }

                                       else if(!model.getFile().equals("null")){

                                           try {
                                               download(model.getFile(),model.getFileName());
                                               final ProgressDialog progressBarDialog= new ProgressDialog(viewHolder.view.getContext());
                                               progressBarDialog.setTitle("File is downloading, Please Wait");

                                               progressBarDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

                                               progressBarDialog.setProgress(0);

                                               new Thread(new Runnable() {

                                                   @Override
                                                   public void run() {


                                                       downloading=true;
                                                       DownloadManager manager = (DownloadManager) getActivity().getSystemService(DOWNLOAD_SERVICE);
                                                       while (downloading) {

                                                           DownloadManager.Query q = new DownloadManager.Query();
                                                           q.setFilterById(queiId); //filter by id which you have receieved when reqesting download from download manager
                                                           Cursor cursor = manager.query(q);
                                                           cursor.moveToFirst();
                                                           int bytes_downloaded = cursor.getInt(cursor
                                                                   .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                                                           int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

                                                           if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                                                               iMageUri=cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                                                               downloading = false;
                                                               try {
                                                                   OpenFile.DownLoadFile(viewHolder.view.getContext(), Uri.parse(iMageUri));
                                                               } catch (IOException e) {
                                                                   e.printStackTrace();

                                                               }
                                                               progressBarDialog.dismiss();

                                                           }

                                                           final int dl_progress = (int) ((bytes_downloaded * 100l) / bytes_total);

                                                           getActivity().runOnUiThread(new Runnable() {

                                                               @Override
                                                               public void run() {

                                                                   progressBarDialog.setProgress((int) dl_progress);

                                                               }
                                                           });

                                                           // Log.d(Constants.MAIN_VIEW_ACTIVITY, statusMessage(cursor));
                                                           cursor.close();
                                                       }

                                                   }
                                               }).start();


                                               //show the dialog
                                               progressBarDialog.show();
                                           } catch (Exception e) {
                                               e.printStackTrace();
                                           }



                                       }
                                   }
                               });
                               viewHolder.pname.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View v) {
                                       Intent profileIntent = new Intent(viewHolder.pname.getContext(), UserProfile_Activity.class);
                                       profileIntent.putExtra("key_noti", from);
                                       profileIntent.putExtra("request", " ");
                                       startActivity(profileIntent);
                                   }
                               });

                               mrootRef.child("notiCount").child(from).addValueEventListener(new ValueEventListener() {
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
                                   public void onClick(final View v) {
                                       mrootRef.child("timeLinePost").child("timeLineAllPost").child(college).child(post).child("userLiked").addListenerForSingleValueEvent(new ValueEventListener() {
                                           @Override
                                           public void onDataChange(final DataSnapshot dataSnapshot) {
                                               if (dataSnapshot.hasChild(userId)) {
                                                   viewHolder.like.setAlpha((float) 0.7);
                                                   mrootRef.child("timeLinePost").child("timeLineAllPost").child(college).child(post).child("userLiked").child(userId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                       @Override
                                                       public void onComplete(@NonNull Task<Void> task) {
                                                           if (task.isSuccessful()) {
                                                               int color = Color.parseColor("#29434e"); //The color u want
                                                               viewHolder.like.setColorFilter(color);
                                                               viewHolder.like.setAlpha((float) 0.7);
                                                               viewHolder.no_like.setText(String.valueOf(dataSnapshot.getChildrenCount() - 1) + " likes");
                                                           }
                                                       }
                                                   });
                                               } else {
                                                   viewHolder.like.setAlpha((float) 1);
                                                   mrootRef.child("timeLinePost").child("timeLineAllPost").child(college).child(post).child("userLiked").child(userId).child("like").setValue("liked").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                       @Override
                                                       public void onComplete(@NonNull Task<Void> task) {
                                                           if (task.isSuccessful()) {
                                                               if (!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(from)) {
                                                                   DatabaseReference notiref = mrootRef.child("notifications").child(from).push();
                                                                   String notirefid = notiref.getKey();

                                                                   if (dataSnapshot.getChildrenCount()>=1){
                                                                       typeNoti=userName+" and"+String.valueOf(dataSnapshot.getChildrenCount())+" others has liked your post";
                                                                   }
                                                                   HashMap notiHash = new HashMap<>();
                                                                   notiHash.put("from", userId);
                                                                   notiHash.put("type", typeNoti);
                                                                   notiHash.put("key", post);
                                                                   notiHash.put("clickAction","JecChat.Notification.Notification");
                                                                   notiHash.put("time", ServerValue.TIMESTAMP);
                                                                   mrootRef.child("notifications").child(from).child(post+" Like").setValue(notiHash);
                                                                   mrootRef.child("notiCount").child(from).child("count").setValue(String.valueOf(Integer.parseInt(count)+1));
                                                                   int color = Color.parseColor("#29434e"); //The color u want
                                                                   viewHolder.like.setColorFilter(color);
                                                                   viewHolder.like.setAlpha((float) 1);
                                                                   viewHolder.no_like.setText(String.valueOf(dataSnapshot.getChildrenCount() + 1) + " likes");
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
                                       Context context = viewHolder.comment.getContext();
                                       Intent intent = new Intent(context, Comment_Activity.class);
                                       intent.putExtra("push_key", post);
                                       intent.putExtra("college_key", college);
                                       context.startActivity(intent);
                                   }
                               });


                           }

                       };
                       firebaseRecyclerAdapter.startListening();
                       recyclerView.setAdapter(firebaseRecyclerAdapter);
                   }
                  // Toast.makeText(getContext(), "college"+college, Toast.LENGTH_SHORT).show();
               }

               @Override
               public void onCancelled(DatabaseError databaseError) {

               }
           });


       }





   }


    @Override
    public void onDestroy() {
        super.onDestroy();
       // firebaseRecyclerAdapter.stopListening();
    }

    public static class postViewHolder extends RecyclerView.ViewHolder {
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
        boolean isPlaying=false;
        public postViewHolder(View itemView) {
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

    private void download(String imageUrl,String name){


        dm=(DownloadManager)getActivity().getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request=new DownloadManager.Request(Uri.parse(imageUrl));
        File direct = new File(Environment.getExternalStorageDirectory().getAbsoluteFile()
                + "/jecChat_files");

        if (!direct.exists()) {
            direct.mkdirs();
        }

        request.setDestinationInExternalFilesDir(getContext(),"/jecChat_files",name);

        queiId=dm.enqueue(request);
    }




}
