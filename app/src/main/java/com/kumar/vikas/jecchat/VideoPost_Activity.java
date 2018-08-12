package com.kumar.vikas.jecchat;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.io.File;
import java.io.IOException;

public class VideoPost_Activity extends AppCompatActivity {

    private VideoView v;
    private MediaController mediaController;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;
    private ProgressBar circularProgressBar;
    private DatabaseReference mRootRef;
    private long queiId;
    private DownloadManager dm;
    private String url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_post_);

        v=(VideoView) findViewById(R.id.videoView2);
        circularProgressBar=findViewById(R.id.circularProgressBar);

        String name=getIntent().getStringExtra("name");
        toolbar=findViewById(R.id.videoViewToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);


        mRootRef= FirebaseDatabase.getInstance().getReference();
        url=getIntent().getStringExtra("videoUrl");
        playvideo(url);


        v.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {

                if(i==mediaPlayer.MEDIA_INFO_BUFFERING_START){
                    circularProgressBar.setVisibility(View.VISIBLE);
                }
                else if(i==mediaPlayer.MEDIA_INFO_BUFFERING_END){

                    circularProgressBar.setVisibility(View.INVISIBLE);
                }
                return false;
            }
        });

    }

    public void playvideo(String videopath) {
        Log.e("entered", "playvide");
        Log.e("path is", "" + videopath);
        try {
            progressDialog = ProgressDialog.show(VideoPost_Activity.this, "",
                    "Buffering video...", false);
            progressDialog.setCancelable(true);
            getWindow().setFormat(PixelFormat.TRANSLUCENT);

            mediaController = new MediaController(VideoPost_Activity.this);

            Uri video = Uri.parse(videopath);
            v.setMediaController(mediaController);
            v.setVideoURI(video);

            v.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                public void onPrepared(MediaPlayer mp) {
                    progressDialog.dismiss();
                    v.start();
                }
            });

        } catch (Exception e) {
            progressDialog.dismiss();
            System.out.println("Video Play Error :" + e.getMessage());
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);
         getMenuInflater().inflate(R.menu.download,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         super.onOptionsItemSelected(item);

         switch (item.getItemId()){


             case android.R.id.home:
             { onBackPressed();
                 return true;}
             case R.id.download:{
                 AlertDialog.Builder builder=new AlertDialog.Builder(VideoPost_Activity.this);
                 builder.setMessage("Do you want to download this video");
                 builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialogInterface, int i) {
                         try {
                             download(url,"download.mp4");
                             final ProgressDialog progressBarDialog= new ProgressDialog(VideoPost_Activity.this);
                             progressBarDialog.setTitle("File is downloading, Please Wait");

                             progressBarDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

                             progressBarDialog.setProgress(0);

                             new Thread(new Runnable() {

                                 @Override
                                 public void run() {


                                    boolean downloading=true;
                                     DownloadManager manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                                     while (downloading) {

                                         DownloadManager.Query q = new DownloadManager.Query();
                                         q.setFilterById(queiId); //filter by id which you have receieved when reqesting download from download manager
                                         Cursor cursor = manager.query(q);
                                         cursor.moveToFirst();
                                         int bytes_downloaded = cursor.getInt(cursor
                                                 .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                                         int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

                                         if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                                             String uri=cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                                             downloading = false;
                                             try {
                                                 OpenFile.DownLoadFile(VideoPost_Activity.this, Uri.parse(uri));
                                             } catch (IOException e) {
                                                 e.printStackTrace();

                                             }
                                             progressBarDialog.dismiss();
                                         }

                                         final int dl_progress = (int) ((bytes_downloaded * 100l) / bytes_total);

                                         runOnUiThread(new Runnable() {

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
                 });
                 builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialogInterface, int i) {

                     }
                 });
                 builder.show();

             }
         }
        return true;
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

    private void download(String imageUrl,String name){


        dm=(DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request=new DownloadManager.Request(Uri.parse(imageUrl));
        File direct = new File(Environment.getExternalStorageDirectory().getAbsoluteFile()
                + "/jecChat_files");

        if (!direct.exists()) {
            direct.mkdirs();
        }

        request.setDestinationInExternalFilesDir(VideoPost_Activity.this,"/jecChat_files",name);

        queiId=dm.enqueue(request);
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        v.pause();
    }
}
