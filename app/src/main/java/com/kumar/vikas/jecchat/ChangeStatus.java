package com.kumar.vikas.jecchat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ChangeStatus extends AppCompatActivity {

    private EditText status;
    private EditText nameText;
    private EditText semesterText;
    private Button saveStatus;
    private Toolbar toolbar;
    private String userId;
    private DatabaseReference mRootRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_status);

        toolbar=(Toolbar)findViewById(R.id.statusToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        getSupportActionBar().setTitle("Change status");
        status=findViewById(R.id.statusText);
        saveStatus=findViewById(R.id.saveStatus);
        nameText=findViewById(R.id.nameText);
        semesterText=findViewById(R.id.semesterText);
        final String sts=getIntent().getStringExtra("status");
        String name=getIntent().getStringExtra("name");
        String sem=getIntent().getStringExtra("sem");
        mRootRef=FirebaseDatabase.getInstance().getReference();
        userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        status.setText(sts);
        nameText.setText(name);
        semesterText.setText(sem);
        saveStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(nameText.getText().toString())) {
                    Map map = new HashMap();
                    map.put("users/" + userId + "/" + "name", nameText.getText().toString());
                    map.put("users/" + userId + "/" + "status", status.getText().toString());
                    map.put("users/" + userId + "/" + "sem", semesterText.getText().toString());

                    mRootRef.updateChildren(map, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                Intent intent = new Intent(ChangeStatus.this, Settings_Activity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
               /* FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("status").setValue(status.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Intent intent=new Intent(ChangeStatus.this,Settings_Activity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });*/
                }
                else {
                    Toast.makeText(ChangeStatus.this, "Name is empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
