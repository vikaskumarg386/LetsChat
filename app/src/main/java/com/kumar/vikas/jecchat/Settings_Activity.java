package com.kumar.vikas.jecchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Settings_Activity extends AppCompatActivity {


    private CircleImageView mImage;
    private ImageView mImageCover;
    private TextView mEnrollment;
    private TextView mBranch;
    private TextView mSem;
    private TextView mEmail;
    private TextView mName;
    private TextView mYear;
    private TextView mStatus;
    private Button mSave;
    private int GALLARY_PIC=1;
    private StorageReference mImageRef;

    private FirebaseUser firebaseUser;
    private DatabaseReference user;
    private ProgressDialog progressDialog;
    private DatabaseReference mRootFef;
    private String branch;
    private String img;
    private String imageCover;
    private String graduationYear;
    private String college;
    private ImageButton changeStatus;
    private String sts;
    private String nam;
    private String sem;
    private String userId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_);

        mImageRef= FirebaseStorage.getInstance().getReference();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

        mName= findViewById(R.id.displayName);
        mStatus= findViewById(R.id.s_tatus);
        mEnrollment=findViewById(R.id.userEnroll);
        mBranch=findViewById(R.id.userBranch);
        mSem=findViewById(R.id.userSemester);
        mEmail=findViewById(R.id.profileEmailid);
        mImage=(CircleImageView)findViewById(R.id.circleImageView);
        mImageCover=(ImageView)findViewById(R.id.display_cover_image);
        mYear=findViewById(R.id.graduationYear);
        changeStatus=findViewById(R.id.changeStatus);
        mRootFef=FirebaseDatabase.getInstance().getReference();


        userId= FirebaseAuth.getInstance().getCurrentUser().getUid();

        user=FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        user.child("online").setValue("true");
               user.keepSynced(true);
        user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                nam=dataSnapshot.child("name").getValue().toString();
                sts=dataSnapshot.child("status").getValue().toString();
                img=dataSnapshot.child("thumbImage").getValue().toString();
                String enrl=dataSnapshot.child("enrollment").getValue().toString();
                branch=dataSnapshot.child("branch").getValue().toString();
                sem=dataSnapshot.child("sem").getValue().toString();
                String emailId=dataSnapshot.child("email").getValue().toString();
                imageCover=dataSnapshot.child("thumbImageCover").getValue().toString();
                graduationYear=dataSnapshot.child("graduationYear").getValue().toString();
                college=dataSnapshot.child("college").getValue().toString();

                mEnrollment.setText(enrl);
                mBranch.setText(branch);
                mSem.setText(sem);
                mEmail.setText(emailId);
                mName.setText(nam);
                mStatus.setText(sts);
                mYear.setText(graduationYear);
                if (sem.equals("Faculty"))
                mSem.setEnabled(false);

                if(!img.equals("default"))
                Picasso.with(Settings_Activity.this).load(img).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.user).into(mImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(Settings_Activity.this).load(img).placeholder(R.drawable.user).into(mImage);

                    }
                });

                if(!imageCover.equals("default"))
                    Picasso.with(Settings_Activity.this).load(imageCover).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.user).into(mImageCover, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(Settings_Activity.this).load(imageCover).placeholder(R.drawable.user).into(mImageCover);

                        }
                    });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mRootFef=FirebaseDatabase.getInstance().getReference();
        mSave=(Button)findViewById(R.id.saveSetting);
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent intent=new Intent(Settings_Activity.this,SelectCollege_Activity.class);
              intent.putExtra("college",college);
              intent.putExtra("branch",branch);
              startActivity(intent);

            }
        });

        changeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Settings_Activity.this,ChangeStatus.class);
                intent.putExtra("status",sts);
                intent.putExtra("name",nam);
                intent.putExtra("sem",sem);
                startActivity(intent);
            }
        });


        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent=new Intent(Settings_Activity.this,ChangeProfileImage_Activity.class);
                intent.putExtra("image",img);
                startActivity(intent);




            }
        });

        mImageCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(Settings_Activity.this,ChangeImageCover_Activity.class);
                intent.putExtra("image",imageCover);
                startActivity(intent);

            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();

        user.child("online").setValue("true");
    }

    @Override
    protected void onPause() {
        super.onPause();
        user.child("online").setValue(ServerValue.TIMESTAMP);
    }




}
