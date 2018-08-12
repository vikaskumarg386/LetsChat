package com.kumar.vikas.jecchat;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Setting1_Activity extends AppCompatActivity {

    private CircleImageView imageView;
    private TextView name;
    private TextView status;
    private TextView allPosts;
    private TextView deleteAccount;
    private TextView changePassword;
    private Toolbar toolbar;
    private RelativeLayout relativeLayout;
    private String email;
    private String pass;
    private TextView openWebView;
    private int s;

    private String userId;
    private DatabaseReference mRootRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting1_);

        toolbar=(Toolbar)findViewById(R.id.setting1Toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Setting");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        userId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        mRootRef= FirebaseDatabase.getInstance().getReference();

        imageView=(CircleImageView)findViewById(R.id.setting1Image);
        name=(TextView)findViewById(R.id.setting1Name);
        status=(TextView)findViewById(R.id.setting1Status);
        allPosts=(TextView)findViewById(R.id.setting1Posts);
        openWebView=(TextView)findViewById(R.id.settingWebView);
        deleteAccount=(TextView)findViewById(R.id.setting1DeleteAccount);
        changePassword=(TextView)findViewById(R.id.setting1ChangePassword);
        relativeLayout =(RelativeLayout)findViewById(R.id.goToSetting);



        mRootRef.child("deactivateAccount").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(userId)){
                    deleteAccount.setText("Activate Your Account");
                    s=1;

                }
                else {
                    deleteAccount.setText("Deactivate Your Account");
                    s=0;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRootRef.child("users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                name.setText(dataSnapshot.child("name").getValue().toString());
                status.setText(dataSnapshot.child("status").getValue().toString());
                email=dataSnapshot.child("email").getValue().toString();
                Picasso.with(Setting1_Activity.this).load(dataSnapshot.child("thumbImage").getValue().toString()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.user).into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(Setting1_Activity.this).load(dataSnapshot.child("thumbImage").getValue().toString()).placeholder(R.drawable.user).into(imageView);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Setting1_Activity.this,Settings_Activity.class);

                ActivityOptionsCompat optionsCompat=ActivityOptionsCompat.makeSceneTransitionAnimation(Setting1_Activity.this,findViewById(R.id.setting1Image),"myImage");

                startActivity(intent,optionsCompat.toBundle());
            }
        });

        allPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Setting1_Activity.this,UserAllPost_Activity.class);
                startActivity(intent);
            }
        });

        openWebView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Setting1_Activity.this,WebView_Activity.class);
                intent.putExtra("url","https://www.jecjabalpur.ac.in");
                startActivity(intent);
            }
        });


        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                final AlertDialog.Builder builder=new AlertDialog.Builder(Setting1_Activity.this);
                builder.setMessage("Enter Password");
                final EditText input = new EditText(Setting1_Activity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                builder.setView(input);

                builder.setPositiveButton(deleteAccount.getText(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pass=input.getText().toString();
                        if(!(pass==null||pass.equals(""))) {
                             final ProgressDialog progressDialog = new ProgressDialog(Setting1_Activity.this);
                             progressDialog.setMessage("please wait...");
                             progressDialog.setCanceledOnTouchOutside(false);
                             progressDialog.show();

                            final FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                            AuthCredential credential = EmailAuthProvider.getCredential(email, pass);

                            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {


                                       switch (s) {
                                           case 1: {
                                               mRootRef.child("deactivateAccount").child(user.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                   @Override
                                                   public void onComplete(@NonNull Task<Void> task) {
                                                       Toast.makeText(Setting1_Activity.this, "Account Activated", Toast.LENGTH_SHORT).show();
                                                       deleteAccount.setText("Deactivate Your Account");
                                                       progressDialog.dismiss();
                                                       s =0;

                                                   }
                                               });
                                               break;
                                           }

                                           case 0: {
                                               mRootRef.child("deactivateAccount").child(user.getUid()).child("timeStamp").setValue(ServerValue.TIMESTAMP).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                   @Override
                                                   public void onComplete(@NonNull Task<Void> task) {
                                                       Toast.makeText(Setting1_Activity.this, "Account Deactivated", Toast.LENGTH_SHORT).show();
                                                       deleteAccount.setText("Activate Your Account");
                                                       progressDialog.dismiss();
                                                       s=1;
                                                   }
                                               });
                                               break;
                                           }
                                       }
                                    } else {
                                        Toast.makeText(Setting1_Activity.this, "Wrong Password", Toast.LENGTH_LONG).show();
                                        progressDialog.dismiss();
                                    }



                                }
                            });
                        }


                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

               builder.show();


            }
        });


        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Setting1_Activity.this,ChangePassword_Activity.class);
                intent.putExtra("email",email);
                startActivity(intent);


            }
        });



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


}
