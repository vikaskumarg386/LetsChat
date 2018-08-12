package com.kumar.vikas.jecchat;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class AddNewCollege_Activity extends AppCompatActivity {


    private Button save;
    private EditText name;
    private EditText address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_college_);

        save=findViewById(R.id.saveCollege);
        name=findViewById(R.id.saveCollegeNameAdd);
        name.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        address=findViewById(R.id.addressOfCollege);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map map=new HashMap();
                map.put("name",name.getText().toString());
                map.put("address",address.getText().toString());
                FirebaseDatabase.getInstance().getReference().child("college").child(name.getText().toString()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(AddNewCollege_Activity.this, "Added", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
