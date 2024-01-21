package com.example.attendancesystem.view.activity;

import android.annotation.SuppressLint;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


import com.example.attendancesystem.R;
import com.example.attendancesystem.storage.SaveUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttendanceDetails2 extends AppCompatActivity {
    private Button showAttendanceBtn;
    private String courses;
    private String course_code;
    private TextView presentTV, absentTV, name, id, daypresent, daysbsent;
    private Button button;



    private List<String> courseList=new ArrayList<>();
    private List<String> course_codeList=new ArrayList<>();
    private List<String> PresentList=new ArrayList<>();

    private List<String> absentList=new ArrayList<>();
    private String selected_course;
    private Map<String, String> dayList = new HashMap<>();
    private DatabaseReference presentRef;
    TableLayout tableLayout;
    private ImageView imageStudent1;
    private LinearLayout viewAllqrcode;

    private CardView cardViewAttendance, cardViewCamera;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewatedance);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        presentTV = findViewById(R.id.presentStudentTV1);
        absentTV = findViewById(R.id.absentStudentTV1);
        name = findViewById(R.id.vName);
        id = findViewById(R.id.vID);
        button=findViewById(R.id.Okbtn);
        tableLayout = findViewById(R.id.tableLayout);
        viewAllqrcode = findViewById(R.id.viewall_qrcode);
        imageStudent1 = findViewById(R.id.image_student);
        imageStudent1.setVisibility(View.GONE);
        cardViewAttendance = findViewById(R.id.qrcode);
        cardViewCamera = findViewById(R.id.btn_camera);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        selected_course =  bundle.getString("haha");



        final SaveUser saveUser=new SaveUser();
        courses=saveUser.getStudent(getApplicationContext()).getCourse();
        course_code=saveUser.getStudent(getApplicationContext()).getCourse_code();
        name.setText(saveUser.getStudent(getApplicationContext()).getName());
        id.setText(saveUser.getStudent(getApplicationContext()).getId());

        presentRef= FirebaseDatabase.getInstance().getReference().child("Department").child(new SaveUser().getStudent(getApplicationContext()).getDepartment())
                .child("Attendance").child(new SaveUser().getStudent(getApplicationContext()).getShift()).child(selected_course);

        presentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("MissingInflatedId")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                PresentList.clear();
                absentList.clear();
                if(dataSnapshot.exists()){
                    for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                        for(DataSnapshot dataSnapshot2:dataSnapshot1.child("Present").getChildren()){

                            String key=dataSnapshot2.getKey();

                            if(key.equals(new SaveUser().getStudent(getApplicationContext()).getId())){
                                PresentList.add(key);
                                dayList.put(dataSnapshot1.getKey(), "Học");
                            }

                        }

                        for(DataSnapshot dataSnapshot2:dataSnapshot1.child("Absent").getChildren()){

                            String key=dataSnapshot2.getValue().toString();
                            if(key.equals(new SaveUser().getStudent(getApplicationContext()).getId())){
                                absentList.add(key);
                                dayList.put(dataSnapshot1.getKey(), "Nghỉ");

                            }
                        }

                    }

                    presentTV.setText(Integer.toString(PresentList.size()));
                    absentTV.setText(Integer.toString(absentList.size()));

                    TableRow headerRow = new TableRow(getApplicationContext());

                    TextView headerKeyTextView = new TextView(getApplicationContext());
                    headerKeyTextView.setText("Ngày điểm danh");
                    headerKeyTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)); // Set layout_weight to 1
                    headerKeyTextView.setGravity(Gravity.CENTER);


                    TextView headerValueTextView = new TextView(getApplicationContext());
                    headerValueTextView.setText("Trạng thái");
                    headerValueTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)); // Set layout_weight to 1
                    headerValueTextView.setGravity(Gravity.CENTER); // Center the content

                    headerRow.addView(headerKeyTextView);
                    headerRow.addView(headerValueTextView);
                    headerRow.setPadding(0,0,0,40);
                    tableLayout.addView(headerRow);

                    for (Map.Entry<String, String> entry : dayList.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();

                        // Create a TableRow
                        TableRow row = new TableRow(getApplicationContext());

                        // Create TextViews for Key and Value
                        TextView keyTextView = new TextView(getApplicationContext());
                        keyTextView.setText(key);
                        keyTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)); // Set layout_weight to 1
                        keyTextView.setGravity(Gravity.CENTER);


                        TextView valueTextView = new TextView(getApplicationContext());
                        valueTextView.setText(value);
                        valueTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)); // Set layout_weight to 1
                        valueTextView.setGravity(Gravity.CENTER);

                        if(value.equals("Nghỉ")){
                            keyTextView.setTextColor(getResources().getColor(R.color.colorRed));
                            valueTextView.setTextColor(getResources().getColor(R.color.colorRed));
                        }

                        // Add TextViews to the TableRow
                        row.addView(keyTextView);
                        row.addView(valueTextView);

                        // Add TableRow to the TableLayout
                        tableLayout.addView(row);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        cardViewAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AttendanceDetails2.this, GenerateQRCodeActivity.class);
                intent.putExtra("course", name.getText());
                Log.e("Bello","course: " +  name.getText());
                intent.putExtra("mssv", saveUser.getStudent(getApplicationContext()).getId());
                intent.putExtra("name", saveUser.getStudent(getApplicationContext()).getName());
                startActivity(intent);
            }
        });
        cardViewCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AttendanceDetails2.this, CameraAttendanceActivity.class);
                startActivity(intent);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
