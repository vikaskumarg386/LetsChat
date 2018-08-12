package com.kumar.vikas.jecchat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectCollege_Activity extends AppCompatActivity {

    private AutoCompleteTextView searchCollege;
    private EditText address;
    private TextView textView,textView1,textView2;
    private Button saveCollege;
    private DatabaseReference mRootRef,mRef;
    private String id;
    //private RecyclerView recyclerView;
    private String college,fieldOfWork,year,display_name;
    private ArrayAdapter adapter;
    private final List list=new ArrayList();
    private String s;
    private Spinner semSpinner,branchSpinner,yearSpinner;
    private Spinner selectField;
    private String user_branch;
    private String sem;
    private String graduationYear;
    private String type;
    private String item;
    private String tag="student";
    private  String[] branchArray={" "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "};
    private  ArrayAdapter branchAdapter;
    private View brView,smView,gyView;
    private String websiteUrl,collegeAddress;
    private EditText collegeWebsiteUrl;
    private ArrayList collegeNameList=new ArrayList();

    private String branchId;
    private String preCollege;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_college_);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Select college");


        searchCollege=findViewById(R.id.searchCollege);
        saveCollege=findViewById(R.id.saveCollege);
        address=findViewById(R.id.address);
        textView=findViewById(R.id.textView14);
        textView1=findViewById(R.id.textView16);
        textView2=findViewById(R.id.textView17);
        collegeWebsiteUrl=findViewById(R.id.websiteUrl);
        brView=findViewById(R.id.brView);
        smView=findViewById(R.id.smView);
        gyView=findViewById(R.id.gyView);

        preCollege=getIntent().getStringExtra("college");
        branchId="branchId"+getIntent().getStringExtra("branch");

        semSpinner=findViewById(R.id.spinner3);
        branchSpinner=findViewById(R.id.spinner2);
        yearSpinner=findViewById(R.id.spinner);
        selectField=findViewById(R.id.selectField);
        brView.setVisibility(View.INVISIBLE);
        smView.setVisibility(View.INVISIBLE);;
        gyView.setVisibility(View.INVISIBLE);
        branchSpinner.setVisibility(View.INVISIBLE);
        yearSpinner.setVisibility(View.INVISIBLE);
        semSpinner.setVisibility(View.INVISIBLE);
        String[] field={"*Select field of work","Arts & Humanities","Commerce","Engineering and Technology","Management","Medical Science","polytechnic","Science","School"};
        final String[] arts={"*courses","Law Courses","Animation & Multimedia","Fashion Technology","Visual Arts","Literary Arts","Performing Arts","Aviation & Hospitality Management","Hotel Management & Catering","FilmMass Communication","Languages","Bachelor of Arts"};
        final String[] commerce={"*courses","BCom","BBA","CA","BMS","BBS","BAF","CS","BMS"};
        final String[] engineering={"*branch","Computer Science and Engineering","Civil Engineering","Mechanical Engineering","Electrical Engineering","Electronics & Communication Engineering","Biotechnology","Electrical & Electronics Engineering","Industrial Production Engineering","Mining Engineering","Aeronautical Engineering","Agricultural Engineering","Petroleum Engineering","Food Technology","Textile Engineering","Chemical Engineering"};
        final String[] management={"*courses","BBA Bachelor of Business Administration","MBA Master of Business Administration","Marketing Management","Finance Management","Human Resource Management","International Business Management","Operation Management","Event Management","Retail Management"};
        final String[] medical={"*courses","MBBS","BDS","BAMS","BHMS","BUMS","Nursing","Physiotherapy","Occupational Therapy","Medical Lab Technician","Pharmacy"};
        final String[] polytechnic={"*branch","Diploma in Mechanical Engineering",
                "Diploma in Electrical Engineering",
                "Diploma in Civil Engineering",
                "Diploma in Chemical Engineering",
                "Diploma in Computer Science Engineering",
                "Diploma in IT Engineering",
                "Diploma in IC Engineering",
                "Diploma in EC Engineering",
                "Diploma in Electronics Engineering",
                "Diploma in Electronics and Telecommunication Engineering",
                "Diploma in Petroleum Engineering",
                "Diploma in Aeronautical Engineering",
                "Diploma in Aerospace Engineering",
                "Diploma in Automobile Engineering",
                "Diploma in Mining Engineering",
                "Diploma in Biotechnology Engineering",
                "Diploma in Genetic Engineering",
                "Diploma in Plastics Engineering",
                "Diploma in Food Processing and Technology",
                "Diploma in Agricultural Engineering",
                "Diploma in Dairy Technology and Engineering",
                "Agricultural Information Technology",
                "Diploma in Power Engineering",
                "Diploma in Production Engineering",
                "Diploma in Infrastructure Engineering",
                "Diploma in Motorsport Engineering",
                "Diploma in Metallurgy Engineering",
                "Diploma in Textile Engineering",
                "Diploma in Environmental Engineering"};
        final String[] science={"*courses","B.Sc","B.Sc (Hons)","Naturopathy & Yogic Science","Dairy Technology","Biotechnology","BCA","(BVSc AH)","Environmental Science","BSC IT"};
        final String[] school={"*class","1st","2nd","3rd","4th","5th","6th","7th","8th","9th","10th","11th","12th"};
        final String[] semArray ={"*sem","1st","2nd","3rd","4th","5th","6th","7th","8th"};
        // String[] branchArray ;//={"branch","cse","it","me","ce","ec","ee","ip"};
        final int currentYear= Calendar.getInstance().get(Calendar.YEAR);
        final String[] yearArray=new String[currentYear-2000+5];
        yearArray[0]="*year of graduation";
        for (int i=1;i<currentYear-2000+5;i++){
            yearArray[i]=String.valueOf(2000+i);
        }
        ArrayAdapter fieldAdapter=new ArrayAdapter(SelectCollege_Activity.this,android.R.layout.simple_list_item_1,field);
        selectField.setAdapter(fieldAdapter);



        searchCollege.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        id= FirebaseAuth.getInstance().getCurrentUser().getUid();
        mRootRef= FirebaseDatabase.getInstance().getReference();
        final String collegePushkey=mRootRef.child("users").child(id).child("college").push().getKey();

        mRootRef.child("users").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                display_name=dataSnapshot.child("name").getValue().toString();
                type=dataSnapshot.child("type").getValue().toString();
                String semester =dataSnapshot.child("sem").getValue().toString();

                if (type.equals("teacher")){
                    semSpinner.setEnabled(false);
                    yearSpinner.setEnabled(false);
                    branchSpinner.setEnabled(true);
                    tag="teacher";
                    textView.setVisibility(View.VISIBLE);
                    textView1.setVisibility(View.INVISIBLE);
                    textView2.setVisibility(View.INVISIBLE);
                    brView.setVisibility(View.INVISIBLE);
                    smView.setVisibility(View.INVISIBLE);;
                    gyView.setVisibility(View.INVISIBLE);
                    branchSpinner.setVisibility(View.VISIBLE);
                    semSpinner.setVisibility(View.INVISIBLE);
                    yearSpinner.setVisibility(View.INVISIBLE);

                }
                else if (type.equals("other")){
                    semSpinner.setEnabled(false);
                    yearSpinner.setEnabled(false);
                    branchSpinner.setEnabled(false);
                    textView.setVisibility(View.INVISIBLE);
                    textView1.setVisibility(View.INVISIBLE);
                    textView2.setVisibility(View.INVISIBLE);
                    brView.setVisibility(View.INVISIBLE);
                    smView.setVisibility(View.INVISIBLE);;
                    gyView.setVisibility(View.INVISIBLE);
                    branchSpinner.setVisibility(View.INVISIBLE);
                    semSpinner.setVisibility(View.INVISIBLE);
                    yearSpinner.setVisibility(View.INVISIBLE);
                    tag="other";
                }
                else {
                    semSpinner.setEnabled(true);
                    yearSpinner.setEnabled(true);
                    branchSpinner.setEnabled(true);
                    textView.setVisibility(View.VISIBLE);
                    textView1.setVisibility(View.VISIBLE);
                    textView2.setVisibility(View.VISIBLE);
                    brView.setVisibility(View.INVISIBLE);
                    smView.setVisibility(View.VISIBLE);
                    gyView.setVisibility(View.VISIBLE);
                    branchSpinner.setVisibility(View.VISIBLE);
                    semSpinner.setVisibility(View.VISIBLE);
                    yearSpinner.setVisibility(View.VISIBLE);
                    tag="student";
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        selectField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                item =String.valueOf(adapterView.getSelectedItem());
                ((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(R.color.dark_gray));
                ((TextView) adapterView.getChildAt(0)).setTextSize(17);

                if (item.equals("*Select field of work")){
                    branchAdapter= new ArrayAdapter(getApplicationContext(),android.R.layout.simple_list_item_1,arts);
                    branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    branchSpinner.setAdapter(branchAdapter);
                    if (tag.equals("student")){
                        textView.setVisibility(View.INVISIBLE);
                        textView1.setVisibility(View.INVISIBLE);
                        textView2.setVisibility(View.INVISIBLE);
                        brView.setVisibility(View.INVISIBLE);
                        smView.setVisibility(View.INVISIBLE);;
                        gyView.setVisibility(View.INVISIBLE);
                        branchSpinner.setVisibility(View.INVISIBLE);
                        semSpinner.setVisibility(View.INVISIBLE);
                        yearSpinner.setVisibility(View.INVISIBLE);}

                    branchArray=arts;
                }
                else if (item.equals("Arts & Humanities")){
                    branchAdapter= new ArrayAdapter(getApplicationContext(),android.R.layout.simple_list_item_1,arts);
                    branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    branchSpinner.setAdapter(branchAdapter);
                    if (tag.equals("student")){
                        textView.setVisibility(View.VISIBLE);
                        textView1.setVisibility(View.VISIBLE);
                        textView2.setVisibility(View.VISIBLE);
                        textView.setText("course");
                        brView.setVisibility(View.VISIBLE);
                        smView.setVisibility(View.VISIBLE);;
                        gyView.setVisibility(View.VISIBLE);
                        branchSpinner.setVisibility(View.VISIBLE);
                        semSpinner.setVisibility(View.VISIBLE);
                        yearSpinner.setVisibility(View.VISIBLE);}


                    branchArray=arts;

                }
                else if (item.equals("Commerce")){
                    branchAdapter= new ArrayAdapter(getApplicationContext(),android.R.layout.simple_list_item_1,commerce);
                    branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    branchSpinner.setAdapter(branchAdapter);
                    if (tag.equals("student")){
                        textView.setVisibility(View.VISIBLE);
                        textView1.setVisibility(View.VISIBLE);
                        textView2.setVisibility(View.VISIBLE);
                        textView.setText("course");
                        brView.setVisibility(View.VISIBLE);
                        smView.setVisibility(View.VISIBLE);
                        gyView.setVisibility(View.VISIBLE);
                        branchSpinner.setVisibility(View.VISIBLE);
                        semSpinner.setVisibility(View.VISIBLE);
                        yearSpinner.setVisibility(View.VISIBLE);}
                    branchArray=commerce;

                }
                else if (item.equals("Engineering and Technology")){
                    branchAdapter= new ArrayAdapter(getApplicationContext(),android.R.layout.simple_list_item_1,engineering);
                    branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    branchSpinner.setAdapter(branchAdapter);
                    if (tag.equals("student")){
                        textView.setVisibility(View.VISIBLE);
                        textView1.setVisibility(View.VISIBLE);
                        textView2.setVisibility(View.VISIBLE);
                        brView.setVisibility(View.VISIBLE);
                        smView.setVisibility(View.VISIBLE);;
                        gyView.setVisibility(View.VISIBLE);
                        branchSpinner.setVisibility(View.VISIBLE);
                        semSpinner.setVisibility(View.VISIBLE);
                        yearSpinner.setVisibility(View.VISIBLE);}
                    branchArray=engineering;

                }
                else if (item.equals("Management")){
                    branchAdapter= new ArrayAdapter(getApplicationContext(),android.R.layout.simple_list_item_1,management);
                    branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    branchSpinner.setAdapter(branchAdapter);
                    if (tag.equals("student")){
                        textView.setVisibility(View.VISIBLE);
                        textView1.setVisibility(View.VISIBLE);
                        textView2.setVisibility(View.VISIBLE);
                        textView.setText("course");
                        brView.setVisibility(View.VISIBLE);
                        smView.setVisibility(View.VISIBLE);;
                        gyView.setVisibility(View.VISIBLE);
                        branchSpinner.setVisibility(View.VISIBLE);
                        semSpinner.setVisibility(View.VISIBLE);
                        yearSpinner.setVisibility(View.VISIBLE);}
                    branchArray=management;

                }
                else if (item.equals("Medical Science")){
                    branchAdapter= new ArrayAdapter(getApplicationContext(),android.R.layout.simple_list_item_1,medical);
                    branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    branchSpinner.setAdapter(branchAdapter);
                    if (tag.equals("student")){
                        textView.setVisibility(View.VISIBLE);
                        textView1.setVisibility(View.VISIBLE);
                        textView2.setVisibility(View.VISIBLE);
                        textView.setText("course");
                        brView.setVisibility(View.VISIBLE);
                        smView.setVisibility(View.VISIBLE);;
                        gyView.setVisibility(View.VISIBLE);
                        branchSpinner.setVisibility(View.VISIBLE);
                        semSpinner.setVisibility(View.VISIBLE);
                        yearSpinner.setVisibility(View.VISIBLE);}
                    branchArray=medical;

                }
                else if (item.equals("polytechnic")){
                    branchAdapter= new ArrayAdapter(getApplicationContext(),android.R.layout.simple_list_item_1,polytechnic);
                    branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    branchSpinner.setAdapter(branchAdapter);
                    if (tag.equals("student")){
                        textView.setVisibility(View.VISIBLE);
                        textView1.setVisibility(View.VISIBLE);
                        textView2.setVisibility(View.VISIBLE);
                        brView.setVisibility(View.VISIBLE);
                        smView.setVisibility(View.VISIBLE);;
                        gyView.setVisibility(View.VISIBLE);
                        branchSpinner.setVisibility(View.VISIBLE);
                        semSpinner.setVisibility(View.VISIBLE);
                        yearSpinner.setVisibility(View.VISIBLE);}
                    branchArray=polytechnic;

                }
                else if (item.equals("Science")){
                    branchAdapter= new ArrayAdapter(getApplicationContext(),android.R.layout.simple_list_item_1,science);
                    branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    branchSpinner.setAdapter(branchAdapter);
                    if (tag.equals("student")){
                        textView.setVisibility(View.VISIBLE);
                        textView1.setVisibility(View.VISIBLE);
                        textView2.setVisibility(View.VISIBLE);
                        textView.setText("course");
                        brView.setVisibility(View.VISIBLE);
                        smView.setVisibility(View.VISIBLE);;
                        gyView.setVisibility(View.VISIBLE);
                        branchSpinner.setVisibility(View.VISIBLE);
                        semSpinner.setVisibility(View.VISIBLE);
                        yearSpinner.setVisibility(View.VISIBLE);}
                    branchArray=science;

                }
                else if(item.equals("School")){
                    branchAdapter= new ArrayAdapter(getApplicationContext(),android.R.layout.simple_list_item_1,school);
                    branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    branchSpinner.setAdapter(branchAdapter);
                    if (tag.equals("student")){
                        textView.setVisibility(View.VISIBLE);
                        textView1.setVisibility(View.INVISIBLE);
                        textView2.setVisibility(View.INVISIBLE);
                        textView.setText("class");
                        brView.setVisibility(View.VISIBLE);
                        smView.setVisibility(View.INVISIBLE);;
                        gyView.setVisibility(View.INVISIBLE);
                        branchSpinner.setVisibility(View.VISIBLE);
                        semSpinner.setVisibility(View.INVISIBLE);
                        yearSpinner.setVisibility(View.INVISIBLE);}
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
                ((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorPrimary));
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
                ((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorPrimary));
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
                ((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorPrimary));
                ((TextView) adapterView.getChildAt(0)).setTextSize(17);
                graduationYear=yearArray[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });





        mRef=mRootRef.child("college");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot d:dataSnapshot.getChildren()) {
                    if (d.hasChild("address")) {
                        collegeNameList.add(d.getKey());
                       // list.add(d.getKey() + " address: " + d.child("address").getValue().toString());
                        list.add(new college(d.child("name").getValue().toString(),d.child("address").getValue().toString()));
                        CollegeAdapter adapter=new CollegeAdapter(SelectCollege_Activity.this,list);
                        searchCollege.setAdapter(adapter);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //adapter=new ArrayAdapter(SelectCollege_Activity.this,android.R.layout.simple_list_item_1,list);

        searchCollege.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FirebaseDatabase.getInstance().getReference().child("college").child(searchCollege.getText().toString()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        address.setText(dataSnapshot.child("address").getValue().toString());
                        if(dataSnapshot.hasChild("websiteUrl"))
                        collegeWebsiteUrl.setText(dataSnapshot.child("websiteUrl").getValue().toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });





        saveCollege.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

             if (!(TextUtils.isEmpty(searchCollege.getText().toString())||TextUtils.isEmpty(address.getText().toString())||TextUtils.isEmpty(graduationYear)||TextUtils.isEmpty(collegeWebsiteUrl.getText()) || TextUtils.isEmpty(user_branch) || TextUtils.isEmpty(sem) || sem .equals("*sem")|| graduationYear.equals("*year of graduation") || user_branch.equals("*courses")||user_branch.equals("*branch") || sem.equals("sem")||item.equals("*Select field of work"))) {

                 college = searchCollege.getText().toString();
                 fieldOfWork=item;
                 year=graduationYear;
                 user_branch="branchId"+user_branch;
                 websiteUrl=collegeWebsiteUrl.getText().toString();
                 collegeAddress=address.getText().toString();
                 final Map map1 = new HashMap();
                 final Map<String, String> map2 = new HashMap<>();
                 final DatabaseReference pushRef = FirebaseDatabase.getInstance().getReference().push();
                 final String pushKey = pushRef.getKey();

                 FirebaseDatabase.getInstance().getReference().child("timeLinePost").child("timeLineAllPost").addValueEventListener(new ValueEventListener() {
                     @Override
                     public void onDataChange(DataSnapshot dataSnapshot) {
                         if(!dataSnapshot.hasChild(college)){
                             String time=DateFormat.getDateInstance().format(new Date()).substring(0,DateFormat.getDateInstance().format(new Date()).lastIndexOf(" "))+" at "+DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date());


                             map2.put("message", "Welcome to the Let'sChat App. This app is for your institute where you can post anything about your institute which will visible to all students who are registered in the app with this institute. And many features are included like you can see all students of your institute, you can interact with them. This app automatically creates a group for your batch mate which include all students of your batch and you can also create a group of your friends. The main idea if this app to provide a platform for your institute where students can easily interact with the other students and teachers and if there is any event happening in your institute you can post it here so that other students also get aware of this. Try to tell about this app to your friends of your institute and friends who are from other institute. We are trying hard to add more features in app to have a better experience and in future you will see it. You can also suggest us in review section in playstore you will be appreciable. Thank you");
                             map2.put("image","null");
                             map2.put("video","null");
                             map2.put("file","null");
                             map2.put("time", time);
                             map2.put("from", "Fa8rYa7svLOZJXUWULdH8lD1w572");
                             map2.put("type", "text");
                             map2.put("likes", "0");
                             map2.put("comments", "0");
                             map2.put("push_key", pushKey);
                             map2.put("college",college);

                             map1.put("timeLinePost/"+"timeLineUserPost/"+"Fa8rYa7svLOZJXUWULdH8lD1w572"+"/"+pushKey,map2);
                             map1.put("timeLinePost/"+"timeLineUserAllPost/"+"Fa8rYa7svLOZJXUWULdH8lD1w572"+"/"+pushKey,map2);
                             map1.put("timeLinePost/"+"timeLineAllPost/"+college+"/"+pushKey,map2);

                         }

                         if(!TextUtils.isEmpty(preCollege)&&!preCollege.equals(college)){
                             map1.put("college/" + preCollege + "/" + "users/" + id ,null);

                         }
                         if(!TextUtils.isEmpty(preCollege)&&!preCollege.equals(college)){
                             FirebaseDatabase.getInstance().getReference().child("college").child(college).child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                             FirebaseDatabase.getInstance().getReference().child("chat").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(branchId).removeValue();
                         }
                         else if(!TextUtils.isEmpty(branchId)&&preCollege.equals(college)&&!branchId.equals(user_branch)){
                             FirebaseDatabase.getInstance().getReference().child("chat").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(branchId).removeValue();
                         }

                         final ProgressDialog progressDialog=new ProgressDialog(SelectCollege_Activity.this);
                         progressDialog.setMessage("wait");
                         if(!((Activity) SelectCollege_Activity.this).isFinishing())
                         {
                             progressDialog.show();
                         }

                         Map map = new HashMap();
                         map.put("seen", false);
                         map.put("lastMessage", " ");
                         map.put("timeStamp", ServerValue.TIMESTAMP);
                         map.put("college", college);
                         map.put("fieldOfWork", fieldOfWork);
                         map.put("year", year);

                         Map mapCollege = new HashMap();
                         mapCollege.put("name", college);
                         mapCollege.put("date", DateFormat.getDateTimeInstance().format(new Date()));

                         Log.i("key", "college" + college + "  field" + fieldOfWork + "  year" + year + "  branch" + user_branch);


                         map1.put("users/" + id + "/" + "college", college);
                         map1.put("users/" + id + "/" + "branch", user_branch.substring(8));
                         map1.put("users/" + id + "/" + "sem", sem);
                         map1.put("users/" + id + "/" + "fieldOfWork",fieldOfWork);
                         map1.put("users/" + id + "/" + "graduationYear", graduationYear);
                         map1.put("college/" + college + "/" + "name", college);
                         map1.put("college/" + college + "/" + "websiteUrl", websiteUrl);
                         map1.put("college/" + college + "/" + "address", collegeAddress);
                         map1.put("college/" + college + "/" + "fieldOfWork/" + fieldOfWork + "/" + "name", fieldOfWork);
                         map1.put("college/" + college + "/" + "fieldOfWork/" + fieldOfWork + "/" + "year/" + year + "/" + "name", year);
                         map1.put("college/" + college + "/" + "fieldOfWork/" + fieldOfWork + "/" + "year/" + year + "/" + "branch/" + user_branch + "/" + "name", user_branch.substring(8));
                         map1.put("college/" + college + "/" + "fieldOfWork/" + fieldOfWork + "/" + "year/" + year + "/" + "branch/" + user_branch + "/" + "status", "default");
                         map1.put("college/" + college + "/" + "fieldOfWork/" + fieldOfWork + "/" + "year/" + year + "/" + "branch/" + user_branch + "/" + "users/" + id + "/" + "date", DateFormat.getDateTimeInstance().format(new Date()));
                         map1.put("college/" + college + "/" + "fieldOfWork/" + fieldOfWork + "/" + "year/" + year + "/" + "branch/" + user_branch + "/" + "users/" + id + "/" + "name", display_name);
                         map1.put("college/" + college + "/" + "fieldOfWork/" + fieldOfWork + "/" + "users/" + id + "/" + "name", display_name);
                         map1.put("college/" + college + "/" + "users/" + id + "/" + "name", display_name);
                         map1.put("chat/" + id + "/" + user_branch, map);




                         FirebaseDatabase.getInstance().getReference().updateChildren(map1, new DatabaseReference.CompletionListener() {
                             @Override
                             public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                 progressDialog.dismiss();
                                 if (databaseError == null) {

                                     Intent intent = new Intent(SelectCollege_Activity.this, MainPage.class);
                                     intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                     startActivity(intent);
                                     finish();

                                 } else {
                                     Toast.makeText(SelectCollege_Activity.this, "failed to register", Toast.LENGTH_SHORT).show();
                                     Log.i("database error",databaseError.toString());

                                 }
                             }
                         });
                     }

                     @Override
                     public void onCancelled(DatabaseError databaseError) {

                     }
                 });



             }
             else {
                 Toast.makeText(SelectCollege_Activity.this,"fill all information",Toast.LENGTH_SHORT).show();
             }
            }
        });


    }

}
