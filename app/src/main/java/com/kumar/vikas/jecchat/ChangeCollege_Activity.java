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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Calendar;
import java.util.HashMap;

public class ChangeCollege_Activity extends AppCompatActivity {


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
    private RadioButton radioButton1;
    private RadioButton radioButton2;
    private RadioGroup radioGroup;
    private Spinner semSpinner,branchSpinner,yearSpinner;
    private Spinner selectField;
    private String user_branch;
    private String sem;
    private String graduationYear;
    private String type;
    private String item;
    private String college;
    private String branchId;


    private  String[] branchArray={" "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "};
    private ArrayAdapter branchAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_college_);


        mAuth=FirebaseAuth.getInstance();
        button=(Button)findViewById(R.id.cButton2);

        Toolbar toolbar=(Toolbar)findViewById(R.id.cToolbar);


        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Change institute");


        t1=(TextInputLayout)findViewById(R.id.cName);
        t2=(TextInputLayout)findViewById(R.id.cEnroll);
        t4=(TextInputLayout)findViewById(R.id.cPass);
        t5=(TextInputLayout)findViewById(R.id.cUserEmail);

        college=getIntent().getStringExtra("college");
        branchId="branchId"+getIntent().getStringExtra("branch");
        Toast.makeText(ChangeCollege_Activity.this,"branchId  "+branchId,Toast.LENGTH_SHORT).show();

        semSpinner=findViewById(R.id.cSpinner3);
        branchSpinner=findViewById(R.id.cSpinner2);
        yearSpinner=findViewById(R.id.cSpinner);
        selectField=findViewById(R.id.cSelectField);
        String[] field={"*Select field of work","Arts & Humanities","Commerce","Engineering and Technology","Management","Medical Science","Science","School"};
        final String[] arts={"*courses","Law Courses","Animation & Multimedia","Fashion Technology","Visual Arts","Literary Arts","Performing Arts","Aviation & Hospitality Management","Hotel Management & Catering","FilmMass Communication","Languages","Bachelor of Arts"};
        final String[] commerce={"*courses","B.Com","BBA","CA","B.M.S","B.B.S","B.A.F","CS","BMS"};
        final String[] engineering={"*branch","Computer Science and Engineering","Civil Engineering","Mechanical Engineering","Electrical Engineering","Electronics & Communication Engineering","Biotechnology","Electrical & Electronics Engineering","Industrial Production Engineering","Aeronautical Engineering","Agricultural Engineering","Petroleum Engineering","Food Technology","Textile Engineering","Chemical Engineering"};
        final String[] management={"*courses","BBA – Bachelor of Business Administration","MBA – Master of Business Administration","Marketing Management","Finance Management","Human Resource Management","International Business Management","Operation Management","Event Management","Retail Management"};
        final String[] medical={"*courses","MBBS","BDS","BAMS","BHMS","BUMS","Nursing","Physiotherapy","Occupational Therapy","Medical Lab Technician","Pharmacy"};
        final String[] science={"*courses","B.Sc","B.Sc (Hons)","Naturopathy & Yogic Science","Dairy Technology","Biotechnology","BCA","(B.VSc AH)","Environmental Science","BSC.IT"};
        final String[] school={"*class","1st","2nd","3rd","4th","5th","6th","7th","8th","9th","10th","11th","12th"};
        final String[] semArray ={"*sem","1st","2nd","3rd","4th","5th","6th","7th","8th"};
        // String[] branchArray ;//={"branch","cse","it","me","ce","ec","ee","ip"};
        final int currentYear= Calendar.getInstance().get(Calendar.YEAR);
        final String[] yearArray=new String[currentYear-2000+5];
        yearArray[0]="year";
        for (int i=1;i<currentYear-2000+5;i++){
            yearArray[i]=String.valueOf(2000+i);
        }
        ArrayAdapter fieldAdapter=new ArrayAdapter(ChangeCollege_Activity.this,android.R.layout.simple_list_item_1,field);
        selectField.setAdapter(fieldAdapter);


        branchSpinner.setVisibility(View.INVISIBLE);
        yearSpinner.setVisibility(View.INVISIBLE);
        semSpinner.setVisibility(View.INVISIBLE);

        selectField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                item =String.valueOf(adapterView.getSelectedItem());
                ((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorWhite));
                ((TextView) adapterView.getChildAt(0)).setTextSize(17);

                if (item.equals("Select field of work")){
                    branchAdapter= new ArrayAdapter(getApplicationContext(),android.R.layout.simple_list_item_1,arts);
                    branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    branchSpinner.setAdapter(branchAdapter);
                    branchSpinner.setVisibility(View.INVISIBLE);

                    branchArray=arts;
                }
                else if (item.equals("Arts & Humanities")){
                    branchAdapter= new ArrayAdapter(getApplicationContext(),android.R.layout.simple_list_item_1,arts);
                    branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    branchSpinner.setAdapter(branchAdapter);
                    branchSpinner.setVisibility(View.VISIBLE);
                    semSpinner.setVisibility(View.VISIBLE);
                    yearSpinner.setVisibility(View.VISIBLE);

                    branchArray=arts;

                }
                else if (item.equals("Commerce")){
                    branchAdapter= new ArrayAdapter(getApplicationContext(),android.R.layout.simple_list_item_1,commerce);
                    branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    branchSpinner.setAdapter(branchAdapter);
                    branchSpinner.setVisibility(View.VISIBLE);
                    semSpinner.setVisibility(View.VISIBLE);
                    yearSpinner.setVisibility(View.VISIBLE);
                    branchArray=commerce;

                }
                else if (item.equals("Engineering and Technology")){
                    branchAdapter= new ArrayAdapter(getApplicationContext(),android.R.layout.simple_list_item_1,engineering);
                    branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    branchSpinner.setAdapter(branchAdapter);
                    branchSpinner.setVisibility(View.VISIBLE);
                    semSpinner.setVisibility(View.VISIBLE);
                    yearSpinner.setVisibility(View.VISIBLE);
                    branchArray=engineering;

                }
                else if (item.equals("Management")){
                    branchAdapter= new ArrayAdapter(getApplicationContext(),android.R.layout.simple_list_item_1,management);
                    branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    branchSpinner.setAdapter(branchAdapter);
                    branchSpinner.setVisibility(View.VISIBLE);
                    semSpinner.setVisibility(View.VISIBLE);
                    yearSpinner.setVisibility(View.VISIBLE);
                    branchArray=management;

                }
                else if (item.equals("Medical Science")){
                    branchAdapter= new ArrayAdapter(getApplicationContext(),android.R.layout.simple_list_item_1,medical);
                    branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    branchSpinner.setAdapter(branchAdapter);
                    branchSpinner.setVisibility(View.VISIBLE);
                    semSpinner.setVisibility(View.VISIBLE);
                    yearSpinner.setVisibility(View.VISIBLE);
                    branchArray=medical;

                }
                else if (item.equals("Science")){
                    branchAdapter= new ArrayAdapter(getApplicationContext(),android.R.layout.simple_list_item_1,science);
                    branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    branchSpinner.setAdapter(branchAdapter);
                    branchSpinner.setVisibility(View.VISIBLE);
                    semSpinner.setVisibility(View.VISIBLE);
                    yearSpinner.setVisibility(View.VISIBLE);
                    branchArray=science;

                }
                else if(item.equals("School")){
                    branchAdapter= new ArrayAdapter(getApplicationContext(),android.R.layout.simple_list_item_1,school);
                    branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    branchSpinner.setAdapter(branchAdapter);
                    branchSpinner.setVisibility(View.VISIBLE);
                    semSpinner.setVisibility(View.INVISIBLE);
                    yearSpinner.setVisibility(View.INVISIBLE);
                    graduationYear="school";
                    sem="school";
                    branchArray=school;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter semAdapter=new ArrayAdapter(getApplicationContext(),android.R.layout.simple_list_item_1,semArray);
        semAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter yearAdapter=new ArrayAdapter(getApplicationContext(),android.R.layout.simple_list_item_1,yearArray);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        semSpinner.setAdapter(semAdapter);

        yearSpinner.setAdapter(yearAdapter);

        semSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //item = (String) adapterView.getItemAtPosition(i);
                ((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorWhite));
                ((TextView) adapterView.getChildAt(0)).setTextSize(17);

                sem=semArray[i]+"sem";
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        branchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorWhite));
                ((TextView) adapterView.getChildAt(0)).setTextSize(17);
                user_branch=branchArray[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorWhite));
                ((TextView) adapterView.getChildAt(0)).setTextSize(17);
                graduationYear=yearArray[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        radioGroup=findViewById(R.id.cSelect);


        radioButton1=findViewById(R.id.cStudent);
        radioButton2=findViewById(R.id.cTeacher);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i==R.id.cTeacher){
                    semSpinner.setEnabled(false);
                    yearSpinner.setEnabled(false);
                    branchSpinner.setEnabled(true);

                    branchSpinner.setVisibility(View.VISIBLE);
                    semSpinner.setVisibility(View.INVISIBLE);
                    yearSpinner.setVisibility(View.INVISIBLE);

                }
                else if (i==R.id.cOther){
                    semSpinner.setEnabled(false);
                    yearSpinner.setEnabled(false);
                    branchSpinner.setEnabled(false);
                    branchSpinner.setVisibility(View.INVISIBLE);
                    semSpinner.setVisibility(View.INVISIBLE);
                    yearSpinner.setVisibility(View.INVISIBLE);

                }
                else {
                    semSpinner.setEnabled(true);
                    yearSpinner.setEnabled(true);
                    branchSpinner.setEnabled(true);
                    branchSpinner.setVisibility(View.VISIBLE);
                    semSpinner.setVisibility(View.VISIBLE);
                    yearSpinner.setVisibility(View.VISIBLE);

                }
            }
        });









        button.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          radioButton=findViewById(radioGroup.getCheckedRadioButtonId());
                                          type=radioButton.getTag().toString();
                                          if (type.equals("teacher")){
                                              semSpinner.setEnabled(false);
                                              yearSpinner.setEnabled(false);
                                              sem="Faculty";
                                              graduationYear="faculty";
                                          }
                                          else if (type.equals("other")){
                                              sem ="Other";
                                              graduationYear="other";
                                              user_branch="other";
                                          }

                                          final String enroll = t2.getEditText().getText().toString();
                                          String pass = t4.getEditText().getText().toString();
                                          final String display_name = t1.getEditText().getText().toString();
                                          final String user_email = t5.getEditText().getText().toString();



                                          if (!(TextUtils.isEmpty(enroll) || TextUtils.isEmpty(pass) || TextUtils.isEmpty(display_name) || TextUtils.isEmpty(user_email)|| TextUtils.isEmpty(graduationYear) || TextUtils.isEmpty(user_branch) || TextUtils.isEmpty(sem)|| sem .equals("*sem")|| graduationYear.equals("*year of graduation") || user_branch.equals("*courses")||user_branch.equals("*branch") || sem.equals("sem")||item.equals("*Select field of work"))) {

                                              progressDialog = new ProgressDialog(ChangeCollege_Activity.this);
                                              progressDialog.setTitle("Changing...");
                                              progressDialog.setMessage(" please wait");
                                              progressDialog.setCanceledOnTouchOutside(false);
                                              progressDialog.show();

                                              AuthCredential credential = EmailAuthProvider.getCredential(user_email, pass);

                                              FirebaseAuth.getInstance().getCurrentUser().reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                  @Override
                                                  public void onComplete(@NonNull Task<Void> task) {
                                                      if (task.isSuccessful()) {

                                                          final String id = mAuth.getCurrentUser().getUid();
                                                          String tokenId = FirebaseInstanceId.getInstance().getToken();
                                                          root.child("college").child(college).child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                                                          root.child("chat").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(branchId).removeValue();
                                                          HashMap hm = new HashMap<>();

                                                          hm.put("name", display_name);
                                                          hm.put("enrollment", enroll);
                                                          hm.put("branch", user_branch);
                                                          hm.put("tokenId", tokenId);
                                                          hm.put("email", user_email);
                                                          hm.put("sem", sem);
                                                          hm.put("fieldOfWork", item);
                                                          hm.put("graduationYear", graduationYear);
                                                          root.child("users").child(id).updateChildren(hm, new DatabaseReference.CompletionListener() {
                                                              @Override
                                                              public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                                  if (databaseError==null){
                                                                      Intent intent = new Intent(ChangeCollege_Activity.this, SelectCollege_Activity.class);
                                                                      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                      startActivity(intent);
                                                                      progressDialog.dismiss();
                                                                  }
                                                              }
                                                          });


                                                      } else {
                                                          Toast.makeText(ChangeCollege_Activity.this, "Wrong email and Password", Toast.LENGTH_LONG).show();
                                                          progressDialog.dismiss();
                                                      }


                                                  }
                                              });
                                          }
                                          else {
                                              Toast.makeText(ChangeCollege_Activity.this, "please fill correct information", Toast.LENGTH_SHORT).show();}



                                      }
                                  }
        );

    }
}
