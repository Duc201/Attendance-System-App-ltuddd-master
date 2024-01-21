package com.example.attendancesystem.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.attendancesystem.R;
import com.example.attendancesystem.model.Student;
import com.example.attendancesystem.storage.SaveUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttendanceDetails extends AppCompatActivity2 {

    private TextView presentTV, absentTV, name, id, daypresent, daysbsent;
    private Button button;
    private DatabaseReference presentRef, absentRef;
    private List<String> presentList = new ArrayList<>();
    private List<String> absentList = new ArrayList<>();

    private Map<String, String> dayList = new HashMap<>();

    private ImageView imageStudent1;
    private LinearLayout viewAllqrcode;

    TableLayout tableLayout;

    private Student student;



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
       viewAllqrcode.setVisibility(View.GONE);


        Intent intent=getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null) {
            student = (Student) bundle.getSerializable("DetailStu");
        }



        presentRef = FirebaseDatabase.getInstance().getReference().child("Department").child(new SaveUser().getStudent(getApplicationContext()).getDepartment())
                .child("Attendance").child(new SaveUser().getTeacher(this).getShift()).child(new SaveUser().teacher_CourseLoadData(this));

        presentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                presentList.clear();
                absentList.clear();
                dayList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                        for (DataSnapshot dataSnapshot2 : dataSnapshot1.child("Present").getChildren()) {

                            String key = dataSnapshot2.getKey();

                            if (key.equals(student.getId())) {
                                presentList.add(key);
                                dayList.put(dataSnapshot1.getKey(), "Học");
                            }

                        }
                        for (DataSnapshot dataSnapshot2 : dataSnapshot1.child("Absent").getChildren()) {

                            String key = dataSnapshot2.getValue().toString();
                            if (key.equals(student.getId())) {
                                absentList.add(key);
                                dayList.put(dataSnapshot1.getKey(), "Nghỉ");
                            }
                        }

                    }
                    presentTV.setText(Integer.toString(presentList.size()));
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
                             name.setText(student.getName());
                            id.setText(student.getId());
        Picasso.get().load(student.getPathImage())
                .error(R.drawable.ic_account)
                .placeholder(R.drawable.ic_account)
                .into(imageStudent1);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}