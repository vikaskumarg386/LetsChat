package com.kumar.vikas.jecchat;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;

public class Image_view_Activity extends AppCompatActivity implements View.OnTouchListener {



    private ImageView imageView;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;
    private String imageUrl;
    private long queiId;
    private DownloadManager dm;
    private DatabaseReference mRootRef;

    private static final String TAG = "Touch";
    @SuppressWarnings("unused")
    private static final float MIN_ZOOM = 1f,MAX_ZOOM = 1f;

    // These matrices will be used to scale points of the image
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    // The 3 states (events) which the user is trying to perform
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    // these PointF objects are used to record the point(s) the user is touching
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        imageView=(ImageView) findViewById(R.id.postImage);

        String name=getIntent().getStringExtra("name");
        toolbar=(Toolbar)findViewById(R.id.imageViewToolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(name);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        progressDialog=new ProgressDialog(Image_view_Activity.this);
        progressDialog.setTitle("Image is loading");
        progressDialog.setMessage("Please wait");
        progressDialog.show();

        mRootRef= FirebaseDatabase.getInstance().getReference();

        imageUrl=getIntent().getStringExtra("imageUrl");

        imageView.setOnTouchListener(this);


        if(!(imageUrl==null||imageUrl.equals("null"))) {
            Picasso.with(Image_view_Activity.this).load(imageUrl).networkPolicy(NetworkPolicy.OFFLINE).into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    progressDialog.dismiss();
                    //Toast.makeText(Image_view_Activity.this,imageUrl,Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError() {
                    Picasso.with(Image_view_Activity.this).load(imageUrl).into(imageView);
                    progressDialog.dismiss();
                }
            });
        }



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
                return true;
            }

            case R.id.download:
            {
                download();



                final ProgressDialog progressBarDialog= new ProgressDialog(Image_view_Activity.this);
                progressBarDialog.setTitle("Download App Data, Please Wait");

                progressBarDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressBarDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog,
                                        int whichButton){
                        // Toast.makeText(getBaseContext(),
                        //       "OK clicked!", Toast.LENGTH_SHORT).show();


                    }
                });
                progressBarDialog.setProgress(0);

                new Thread(new Runnable() {

                    @Override
                    public void run() {

                        boolean downloading = true;

                        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                        while (downloading) {

                            DownloadManager.Query q = new DownloadManager.Query();
                            q.setFilterById(queiId); //filter by id which you have receieved when reqesting download from download manager
                            Cursor cursor = manager.query(q);
                            cursor.moveToFirst();
                            int bytes_downloaded = cursor.getInt(cursor
                                    .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                            int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

                            if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                               // iMageUri=cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                                downloading = false;
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


            }
        }
        return true;
    }

    private void download(){

        dm=(DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request=new DownloadManager.Request(Uri.parse(imageUrl));

        File direct = new File(Environment.getExternalStorageDirectory().getAbsoluteFile()
                + "/jecChat_files");

        if (!direct.exists()) {
            direct.mkdirs();
        }

        request.setDestinationInExternalFilesDir(Image_view_Activity.this,"/jecChat_files","postImage.jpg");

        queiId=dm.enqueue(request);
    }




    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        ImageView view = (ImageView) v;
        view.setScaleType(ImageView.ScaleType.MATRIX);
        float scale;

        dumpEvent(event);
        // Handle touch events here...

        switch (event.getAction() & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:   // first finger down only
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                Log.d(TAG, "mode=DRAG"); // write to LogCat
                mode = DRAG;
                break;

            case MotionEvent.ACTION_UP: // first finger lifted

            case MotionEvent.ACTION_POINTER_UP: // second finger lifted

                mode = NONE;
                Log.d(TAG, "mode=NONE");
                break;

            case MotionEvent.ACTION_POINTER_DOWN: // first and second finger down

                oldDist = spacing(event);
                Log.d(TAG, "oldDist=" + oldDist);
                if (oldDist > 5f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                    Log.d(TAG, "mode=ZOOM");
                }
                break;

            case MotionEvent.ACTION_MOVE:

                if (mode == DRAG)
                {
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x, event.getY() - start.y); // create the transformation in the matrix  of points
                }
                else if (mode == ZOOM)
                {
                    // pinch zooming
                    float newDist = spacing(event);
                    Log.d(TAG, "newDist=" + newDist);
                    if (newDist > 5f)
                    {
                        matrix.set(savedMatrix);
                        scale = newDist / oldDist; // setting the scaling of the
                        // matrix...if scale > 1 means
                        // zoom in...if scale < 1 means
                        // zoom out
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;
        }

        view.setImageMatrix(matrix); // display the transformation on screen

        return true; // indicate event was handled
    }

    /*
     * --------------------------------------------------------------------------
     * Method: spacing Parameters: MotionEvent Returns: float Description:
     * checks the spacing between the two fingers on touch
     * ----------------------------------------------------
     */

    private float spacing(MotionEvent event)
    {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /*
     * --------------------------------------------------------------------------
     * Method: midPoint Parameters: PointF object, MotionEvent Returns: void
     * Description: calculates the midpoint between the two fingers
     * ------------------------------------------------------------
     */

    private void midPoint(PointF point, MotionEvent event)
    {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    /** Show an event in the LogCat view, for debugging */
    private void dumpEvent(MotionEvent event)
    {
        String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE","POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
        StringBuilder sb = new StringBuilder();
        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        sb.append("event ACTION_").append(names[actionCode]);

        if (actionCode == MotionEvent.ACTION_POINTER_DOWN || actionCode == MotionEvent.ACTION_POINTER_UP)
        {
            sb.append("(pid ").append(action >> MotionEvent.ACTION_POINTER_INDEX_SHIFT);
            sb.append(")");
        }

        sb.append("[");
        for (int i = 0; i < event.getPointerCount(); i++)
        {
            sb.append("#").append(i);
            sb.append("(pid ").append(event.getPointerId(i));
            sb.append(")=").append((int) event.getX(i));
            sb.append(",").append((int) event.getY(i));
            if (i + 1 < event.getPointerCount())
                sb.append(";");
        }

        sb.append("]");
        Log.d("Touch Events ---------", sb.toString());
    }
}
