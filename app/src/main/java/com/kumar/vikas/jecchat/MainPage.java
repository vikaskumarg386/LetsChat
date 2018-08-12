package com.kumar.vikas.jecchat;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MainPage extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private ViewPager mViewPager;
    private SectionPagerAdapter mSectionPagerAdapter;
    private TabLayout mTabLayout;
    private DatabaseReference userDataRef;
    private String userId;
    private DatabaseReference mRootRef;
    private ImageView openNoti;

    private Toolbar toolbar;
    private String name;
    private TextView notificationCount;
    private int mCartItemCount = 10;
    private String college,fieldOfWork,branch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth=FirebaseAuth.getInstance();


        if(mAuth.getCurrentUser()==null){
            login_page();
        }
        else {
        userDataRef= FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        toolbar=(Toolbar)findViewById(R.id.main_tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Let'sChat");

        mViewPager =(ViewPager)findViewById(R.id.viewPager);

        mSectionPagerAdapter =new SectionPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionPagerAdapter);

        mTabLayout=(TabLayout)findViewById(R.id.tabLayout);
        mTabLayout.setupWithViewPager(mViewPager);

        mTabLayout.getTabAt(0).setIcon(R.drawable.ic_home_white_24dp);
        mTabLayout.getTabAt(1).setIcon(R.drawable.ic_message_white_24dp);
        mTabLayout.getTabAt(2).setIcon(R.drawable.friends);
        mRootRef=FirebaseDatabase.getInstance().getReference();
        userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        mRootRef.child("users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("college")){
                college=dataSnapshot.child("college").getValue().toString();
                fieldOfWork=dataSnapshot.child("fieldOfWork").getValue().toString();
                branch=dataSnapshot.child("branch").getValue().toString();}
                else {
                    Intent intent=new Intent(MainPage.this,SelectCollege_Activity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });}





    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user=mAuth.getCurrentUser();
        if(user==null)
        {
            login_page();
        }
        else {
            userDataRef.child("online").setValue("true");
            userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
            mRootRef=FirebaseDatabase.getInstance().getReference();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mAuth.getCurrentUser()!=null)
            userDataRef.child("online").setValue(ServerValue.TIMESTAMP);
    }

    void login_page()
    {
        Intent intent=new Intent(MainPage.this,WelcomePage.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu_bar,menu);

        final MenuItem menuItem = menu.findItem(R.id.action_notifications);

        View actionView = menuItem.getActionView();
        if (actionView!=null){
        notificationCount = actionView.findViewById(R.id.noti_count);
            notificationCount.setVisibility(View.INVISIBLE);
        openNoti=actionView.findViewById(R.id.openNoti);
        openNoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainPage.this,Notification_Activity.class);
                startActivity(intent);
            }
        });
        mRootRef.child("notiCount").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("count")){
                    if (!dataSnapshot.child("count").getValue().toString().equals("0"))
                        notificationCount.setVisibility(View.VISIBLE);
                    notificationCount.setText(dataSnapshot.child("count").getValue().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });}
        //setupBadge();

       /* actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });*/


        return true;
    }

   /* private void setupBadge() {

        if (notificationCount != null) {
            if (mCartItemCount == 0) {
                if (notificationCount.getVisibility() != View.GONE) {
                    notificationCount.setVisibility(View.GONE);
                }
            } else {
                notificationCount.setText(String.valueOf(Math.min(mCartItemCount, 99)));
                if (notificationCount.getVisibility() != View.VISIBLE) {
                    notificationCount.setVisibility(View.VISIBLE);
                }
            }
        }
    }*/


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);



        switch(item.getItemId()){


            case R.id.action_notifications:{

               Intent intent=new Intent(this,Notification_Activity.class);
               startActivity(intent);
               break;


            }

            case R.id.createGroup:{


                mRootRef.child("users").child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        name = dataSnapshot.child("name").getValue().toString();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                final AlertDialog.Builder builder=new AlertDialog.Builder(MainPage.this);
                final EditText editText=new EditText(MainPage.this);
                LinearLayout.LayoutParams layoutParam=new LinearLayout.LayoutParams(

                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                );
                editText.setLayoutParams(layoutParam);
                builder.setView(editText);
                builder.setMessage("Enter group name");
                builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String groupId="groupId"+editText.getText().toString();

                        HashMap groupMap=new HashMap();
                        groupMap.put("groupId",editText.getText().toString());
                        groupMap.put("thumbImage","default");
                        groupMap.put("status","default");
                        groupMap.put("createdBy",userId);
                        groupMap.put("name",editText.getText().toString());

                        mRootRef.child("group").child(groupId).setValue(groupMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                Map addMap = new HashMap();
                                addMap.put("timeStamp", ServerValue.TIMESTAMP);
                                addMap.put("name", name);

                                Map chatMap = new HashMap();
                                chatMap.put("seen", false);
                                chatMap.put("lastMessage", "  ");
                                chatMap.put("timeStamp", ServerValue.TIMESTAMP);

                                Map map = new HashMap();
                                map.put("group/" + groupId + "/" + "users/" + userId, addMap);
                                map.put("chat/" + userId + "/" + groupId, chatMap);

                                mRootRef.updateChildren(map, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        if (databaseError != null) {
                                            Toast.makeText(MainPage.this, "error", Toast.LENGTH_SHORT).show();


                                        }
                                    }
                                });

                            }
                        });






                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
                break;

            }
            case R.id.logout:{

                AlertDialog.Builder builder=new AlertDialog.Builder(MainPage.this);
                builder.setMessage("Do you want to logout");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        userDataRef.child("online").setValue(ServerValue.TIMESTAMP);
                        userDataRef.child("tokenId").setValue(" ");
                        FirebaseAuth.getInstance().signOut();
                        login_page();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();

                break;}

            case R.id.setting:
                Intent intent=new Intent(MainPage.this,Setting1_Activity.class);
                startActivity(intent);break;
            case R.id.all_users:
                Intent intent2=new Intent(MainPage.this,AllUsersOfColleg_Activity.class);
                intent2.putExtra("key",college);

                startActivity(intent2);
            default:return true;
        }

       /* if(item.getItemId()==R.id.logout)
        {
            FirebaseAuth.getInstance().signOut();
            login_page();

        }
        if(item.getItemId()==R.id.setting);
        {
            Intent intent=new Intent(MainPage.this,Settings_Activity.class);
            startActivity(intent);
        }*/
        return true;
    }
}
