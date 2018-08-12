package com.kumar.vikas.jecchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CreateUserPage extends AppCompatActivity {

   private FirebaseAuth mAuth;
   private Button button;

   private TextInputLayout t1;
   private TextInputLayout t2;

   private TextInputLayout t4;
   private TextInputLayout t5;

   private ProgressDialog progressDialog;
   private FirebaseDatabase db=FirebaseDatabase.getInstance();
   private DatabaseReference root=db.getReference();
   private RadioButton radioButton;
   private RadioGroup radioGroup;
   private String type;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        mAuth=FirebaseAuth.getInstance();
        button=(Button)findViewById(R.id.button2);

        Toolbar toolbar=(Toolbar)findViewById(R.id.createToolbar);


        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create Account");


        t1=(TextInputLayout)findViewById(R.id.name);
        t2=(TextInputLayout)findViewById(R.id.enroll);
        t4=(TextInputLayout)findViewById(R.id.pass);
        t5=(TextInputLayout)findViewById(R.id.userEmail);

        radioGroup=findViewById(R.id.select);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButton=findViewById(radioGroup.getCheckedRadioButtonId());
                type=radioButton.getTag().toString();


                final String enroll = t2.getEditText().getText().toString();
                String pass = t4.getEditText().getText().toString();
                final String display_name = t1.getEditText().getText().toString();
                final String user_email = t5.getEditText().getText().toString();



                    if (!(TextUtils.isEmpty(pass) || TextUtils.isEmpty(display_name) || TextUtils.isEmpty(user_email))) {

                        progressDialog = new ProgressDialog(CreateUserPage.this);
                        progressDialog.setTitle("Create Account");
                        progressDialog.setMessage("Account is creating please wait");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();


                        mAuth.createUserWithEmailAndPassword(user_email, pass)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        Log.i("TAG", "createUserWithEmail:onComplete:" + task.isSuccessful());


                                        if (!task.isSuccessful()) {
                                            Toast.makeText(CreateUserPage.this, "Password is week or you have already account",
                                                    Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        } else {
                                            final String id = mAuth.getCurrentUser().getUid();
                                            String tokenId = FirebaseInstanceId.getInstance().getToken();

                                            HashMap<String, String> hm = new HashMap<>();

                                            hm.put("name", display_name);
                                            hm.put("enrollment", enroll);
                                            hm.put("branch", "branch");
                                            hm.put("image", "default");
                                            hm.put("thumbImage", "default");
                                            hm.put("imageCover", "default");
                                            hm.put("thumbImageCover", "default");
                                            hm.put("status", "Hii there i am using Let'sChat");
                                            hm.put("tokenId", tokenId);
                                            hm.put("email", user_email);
                                            hm.put("sem", "sem");
                                            hm.put("fieldOfWork","fieldOfWork");
                                            hm.put("graduationYear", "graduationYear");
                                            hm.put("type",type);
                                            root.child("users").child(id).setValue(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful()){
                                                        Intent intent = new Intent(CreateUserPage.this, SelectCollege_Activity.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        startActivity(intent);
                                                        progressDialog.dismiss();
                                                    }

                                                }
                                            });

                                        }


                                    }
                                });
                    } else {
                        Toast.makeText(CreateUserPage.this, "please fill correct information", Toast.LENGTH_SHORT).show();
                    }



            }
            }
        );
    }
}
