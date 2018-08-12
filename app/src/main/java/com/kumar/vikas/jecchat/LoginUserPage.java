package com.kumar.vikas.jecchat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginUserPage extends AppCompatActivity {

   private FirebaseAuth mAuth;
   private TextInputLayout userEmail;
   private TextInputLayout pass;
   private Button login;private Toolbar toolbar;
   private DatabaseReference dbref;
   private ProgressDialog progressDialoge;
   private TextView forgetPassword;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth=FirebaseAuth.getInstance();

        toolbar=(Toolbar) findViewById(R.id.loginToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login");



        dbref= FirebaseDatabase.getInstance().getReference();



        userEmail=(TextInputLayout)findViewById(R.id.enrollment);
        pass=(TextInputLayout)findViewById(R.id.password);
        login=(Button)findViewById(R.id.login);
        forgetPassword=(TextView)findViewById(R.id.forgetPassword);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialoge =new ProgressDialog(LoginUserPage.this);
                progressDialoge.setTitle("Logging In");
                progressDialoge.setMessage("Please Wait");
                progressDialoge.setCanceledOnTouchOutside(false);

                progressDialoge.show();

                String en=userEmail.getEditText().getText().toString();
                String pa=pass.getEditText().getText().toString();

                if(!TextUtils.isEmpty(en)&&!TextUtils.isEmpty(pa)){

                    mAuth.signInWithEmailAndPassword(en,pa).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                final String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
                               String deviceToken= FirebaseInstanceId.getInstance().getToken();
                               dbref.child("users").child(uid).child("tokenId").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                                   @Override
                                   public void onSuccess(Void aVoid) {

                                       Intent intent=new Intent(LoginUserPage.this,MainPage.class);
                                       intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                       startActivity(intent);
                                       progressDialoge.dismiss();
                                   }
                               });


                            }

                            else {
                                Toast.makeText(LoginUserPage.this, "Enrollment and password are wrong", Toast.LENGTH_SHORT).show();
                                progressDialoge.dismiss();
                            }
                        }
                    });
                }


            }
        });


        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String en=userEmail.getEditText().getText().toString();
                if(!TextUtils.isEmpty(en)){

                    mAuth.sendPasswordResetEmail(en).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                final AlertDialog.Builder builder=new AlertDialog.Builder(LoginUserPage.this);
                                builder.setMessage("An email is sent to you to reset password ");
                                builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                builder.show();

                            }
                            else {
                                Toast.makeText(LoginUserPage.this,"Wrong Email",Toast.LENGTH_SHORT).show();
                            }


                        }
                    });


                }
                else {
                    Toast.makeText(LoginUserPage.this,"Please Enter Your Email",Toast.LENGTH_SHORT).show();
                }






            }
        });

    }
}
