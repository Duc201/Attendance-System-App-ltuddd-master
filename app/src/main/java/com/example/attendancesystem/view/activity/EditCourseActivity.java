package com.example.attendancesystem.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.attendancesystem.R;
import com.example.attendancesystem.model.Course;
import com.example.attendancesystem.model.Teacher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import xyz.hasnat.sweettoast.SweetToast;

public class EditCourseActivity extends AppCompatActivity {
    private Spinner courseBatchSp;
    private Spinner courseTeacherSp,courseTitleSP;
    private EditText courseCodeET;
    private DatabaseReference teacherListRef,batchListRef,courseRef,courseTitleRef,courseCodeRef;
    private List<String> teacherList,batchList,teacherIDList,CourseTitleList,courseCodeList;
    private String intentedDep,intentedShift;
    private Button addCourseBtn;
    private String selected_batch,selected_teacher,selected_teacherID,selected_course_title;

    private Course courseStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Edit Course");
        courseBatchSp=findViewById(R.id.courseBatchSp);
        courseTeacherSp=findViewById(R.id.courseTeacherSp);
        courseTitleSP=findViewById(R.id.courseTitleSp);
        courseCodeET=findViewById(R.id.courseCode);
        addCourseBtn=findViewById(R.id.addCourseBtn);

        Intent intent=getIntent();
        Bundle bundle = intent.getExtras();
        intentedDep=bundle.getString("CDEPT");
        Log.e("Bello","intentedDep: " + intentedDep);
        intentedShift=bundle.getString("CSHIFT");
        Log.e("Bello","intentedShift: " + intentedShift);
        addCourseBtn.setText("Update Course");
        courseStart= (Course) bundle.getSerializable("Course");
        selected_course_title = courseStart.getCourse_name();
        selected_teacher = courseStart.getTeacher();
        selected_batch = courseStart.getSelected_batch();
        //SweetToast.success(getApplicationContext(),intentedShift);
        teacherList=new ArrayList<>();
        batchList=new ArrayList<>();
        teacherIDList=new ArrayList<>();
        CourseTitleList=new ArrayList<>();
        courseCodeList=new ArrayList<>();
        CourseTitleList.add(0,"Select course");


        // lấy ra danh sách lớp học cho vào spin
        courseRef= FirebaseDatabase.getInstance().getReference().child("Department").child(intentedDep).child("Course").child(intentedShift);
        courseTitleRef=FirebaseDatabase.getInstance().getReference().child("Department").child(intentedDep).child("Courselist");
        courseTitleRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CourseTitleList.clear();
                courseCodeList.add(0,"Course Code");
                CourseTitleList.add(0,"Course Name");
                if (dataSnapshot.exists()){
                    Log.e("Bello","here1");
                    for (DataSnapshot ds1:dataSnapshot.getChildren()){
                        String key=ds1.getKey();
                        String key1=ds1.getValue().toString();
                        CourseTitleList.add(key); // tên môn học
                        courseCodeList.add(key1); // mã số lớp môn học
                    }

                    ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(EditCourseActivity.this,android.R.layout.simple_list_item_1,CourseTitleList);

                    courseTitleSP.setAdapter(arrayAdapter);
                    int position = getPositionByValue(CourseTitleList, selected_course_title);
                    if (position != -1) {
                        courseTitleSP.setSelection(position);
                        courseCodeET.setText(courseCodeList.get(position));
                    }
                    courseTitleSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            selected_course_title=parent.getItemAtPosition(position).toString();
                            courseCodeET.setText(courseCodeList.get(position));
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Lấy ra danh sách giáo viên
        teacherListRef= FirebaseDatabase.getInstance().getReference().child("Department").child(intentedDep).child("Teacher").child(intentedShift);
        teacherListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                teacherList.clear();
                teacherIDList.clear();
                teacherList.add(0,"Select teacher");
                teacherIDList.add(0,"id");

                if(dataSnapshot.exists()){

                    for(DataSnapshot dataSnapshot2:dataSnapshot.getChildren()){
                        if(dataSnapshot2.hasChildren()){
                            Teacher teacher=dataSnapshot2.getValue(Teacher.class);
                            String name=teacher.getName();
                            String id=teacher.getId();
                            teacherList.add(name);
                            teacherIDList.add(id);
                        }
                    }


                    ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(EditCourseActivity.this,android.R.layout.simple_list_item_1,teacherList);
                    courseTeacherSp.setAdapter(arrayAdapter);
                    int position = getPositionByValue(teacherList, selected_teacher);
                    if (position != -1) {
                        courseTeacherSp.setSelection(position);
                        selected_teacherID=teacherIDList.get(position);
                    }
                    courseTeacherSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            selected_teacher=parent.getItemAtPosition(position).toString();
                            selected_teacherID=teacherIDList.get(position);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        // lấy danh sách khoa
        batchListRef=FirebaseDatabase.getInstance().getReference().child("Department").child(intentedDep).child("Student");
        batchListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                batchList.clear();
                batchList.add("Chọn khoa");

                if(dataSnapshot.exists()){
                    for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                        if(dataSnapshot1.hasChildren()){
                            String key=dataSnapshot1.getKey();
                            batchList.add(key);
                        }
                    }
                    ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(EditCourseActivity.this,android.R.layout.simple_list_item_1,batchList);
                    courseBatchSp.setAdapter(arrayAdapter);
                    int position = getPositionByValue(batchList, selected_batch);
                    if (position != -1) {
                        courseBatchSp.setSelection(position);
                    }
                    courseBatchSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            selected_batch=parent.getItemAtPosition(position).toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        addCourseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCourse();
            }
        });


    }

    private int getPositionByValue(List<String> courseTitleList, String selectedCourseTitle) {
        for (int i = 0; i < courseTitleList.size(); i++) {
            if (courseTitleList.get(i).equals(selectedCourseTitle)) {
                return i;
            }
        }
        return -1;
    }


    private void addCourse(){

        String course1=courseCodeET.getText().toString();

        if(selected_course_title.equals("Select course")){
            SweetToast.error(getApplicationContext(),"Select course");
        }else if(course1.isEmpty()){
            courseCodeET.setError("Give course code");
        }else if(selected_batch.equals("Chọn khoa")){
            SweetToast.warning(getApplicationContext(),"Chọn khoa");
        }else if(selected_teacher.equals("Select teacher")) {
            SweetToast.warning(getApplicationContext(), "Select teacher");
        }else if(selected_teacherID.equals("id")){

        }
        else {
            final Course course=new Course("",selected_course_title,course1,selected_teacher,selected_teacherID,selected_batch);
            courseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            if (dataSnapshot1.hasChildren()) {
                                Course courseFB = dataSnapshot1.getValue(Course.class);
                                if(courseFB.getCourse_code().toString().trim().equals( course.getCourse_code().toString().trim())){
                                    courseRef.child(dataSnapshot1.getKey())
                                            .setValue(course)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    SweetToast.success(getApplicationContext(),"Update Thành Công");
                                                    finish();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    SweetToast.error(getApplicationContext(),"Lỗi");
                                                }
                                            });
                                }
                            }

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    SweetToast.error(getApplicationContext(),"Lỗi Update");
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
