package com.kumar.vikas.jecchat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;


public class ChangePassword_Activity extends AppCompatActivity {


    private TextInputLayout oldPassword;
    private TextInputLayout newPassword;
    private Button button;

    private Toolbar toolbar;
    private FirebaseUser user;
    private DatabaseReference mRootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password_);

        oldPassword=(TextInputLayout)findViewById(R.id.oldPassword);
        newPassword=(TextInputLayout)findViewById(R.id.newPassword);
        button=(Button)findViewById(R.id.change);
        toolbar=(Toolbar)findViewById(R.id.changePasswordToolBar);
        final String email=getIntent().getStringExtra("email");
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Change Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        mRootRef= FirebaseDatabase.getInstance().getReference();

        user= FirebaseAuth.getInstance().getCurrentUser();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String oldPass=oldPassword.getEditText().getText().toString();
                final String newPass=newPassword.getEditText().getText().toString();

                AuthCredential credential= EmailAuthProvider.getCredential(email,oldPass);

                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                            user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){

                                        Toast.makeText(ChangePassword_Activity.this,"User Password Updated",Toast.LENGTH_LONG).show();

                                        Intent intent=new Intent(ChangePassword_Activity.this,Setting1_Activity.class);
                                        startActivity(intent);

                                    }

                                    else {
                                        Toast.makeText(ChangePassword_Activity.this,"User Password Not Updated",Toast.LENGTH_LONG).show();

                                    }

                                }
                            });




                        }

                        else {

                            Toast.makeText(ChangePassword_Activity.this,"You have entered wrong password",Toast.LENGTH_LONG).show();


                        }
                    }
                });



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
