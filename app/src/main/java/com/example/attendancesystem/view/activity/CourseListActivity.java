package com.example.attendancesystem.view.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SearchView;

import com.example.attendancesystem.ItemTouchHelperListener;
import com.example.attendancesystem.R;
import com.example.attendancesystem.RecyclerViewItemTouchHelper;
import com.example.attendancesystem.RecyclerViewItemTouchHelperCourse;
import com.example.attendancesystem.adapter.CourseListAdapter;
import com.example.attendancesystem.adapter.StudentListAdapter;
import com.example.attendancesystem.model.Course;
import com.example.attendancesystem.model.Student;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import xyz.hasnat.sweettoast.SweetToast;

public class CourseListActivity extends AppCompatActivity implements ItemTouchHelperListener {
   private FloatingActionButton addCourseButton;
   private RecyclerView courseRv;
   private String intented_dept,intented_Shift;
   private List<Course>  courseList=new ArrayList<>();
   private DatabaseReference courseRef;
   private CourseListAdapter courseListAdapter;
   private RelativeLayout rootView;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        final Intent intent=getIntent();
        intented_dept=intent.getStringExtra("CDEPT");
        intented_Shift=intent.getStringExtra("CSHIFT");
        addCourseButton=findViewById(R.id.addCBtn);
        courseRv=findViewById(R.id.CourseListRV);
        rootView=findViewById(R.id.root_view_course);

        courseRef= FirebaseDatabase.getInstance().getReference().child("Department").child(intented_dept).child("Course").child(intented_Shift);
        courseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                courseList.clear();
                if(dataSnapshot.exists()){
                    for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                        if(dataSnapshot1.hasChildren()){
                            Course course=dataSnapshot1.getValue(Course.class);
                            courseList.add(course);
                        }
                    }
                     courseListAdapter=new CourseListAdapter(CourseListActivity.this, courseList, new CourseListAdapter.IClickListenerCourse() {
                        @Override
                        public void onClickItem(Course course) {
                                openEditCourseActivity(course);
                        }
                    });
                    courseRv.setLayoutManager(new LinearLayoutManager(CourseListActivity.this));
                    courseListAdapter.notifyDataSetChanged();
                    courseRv.setAdapter(courseListAdapter);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        addCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent(CourseListActivity.this,AddCourseActivity.class);
                intent1.putExtra("CDEPT",intented_dept);
                intent1.putExtra("CSHIFT",intented_Shift);

                startActivity(intent1);
            }
        });




        ItemTouchHelper.SimpleCallback simpleCallback = new RecyclerViewItemTouchHelperCourse(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(courseRv);


    }

    private void openEditCourseActivity(Course course) {
        Intent intent = new Intent(this,EditCourseActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("Course",course);
        bundle.putString("CDEPT",intented_dept);
        bundle.putString("CSHIFT",intented_Shift);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder) {
        if(viewHolder instanceof CourseListAdapter.CourseListViewHolder){
            final String nameImageDelete = courseList.get(viewHolder.getAdapterPosition()).getCourse_name();

            final Course courseDelete = courseList.get(viewHolder.getAdapterPosition());
            final int indexDelte = viewHolder.getAdapterPosition();


            courseListAdapter.removeItem(indexDelte);
            Snackbar snackbar = Snackbar.make(rootView,nameImageDelete+"remover!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    courseListAdapter.undoItem(courseDelete,indexDelte);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);

            snackbar.addCallback(new Snackbar.Callback(){
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    super.onDismissed(transientBottomBar, event);
                    if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                        deleteCourseFireBase(courseDelete);
                    }
                }


            });
            snackbar.show();

        }
    }

    private void deleteCourseFireBase(final Course courseDelete) {


        courseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        if (dataSnapshot1.hasChildren()) {
                            Course courseFB = dataSnapshot1.getValue(Course.class);
                            if(courseFB.getCourse_code().equals(courseDelete.getCourse_code())){
                                courseRef.child(dataSnapshot1.getKey()).removeValue(new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                        SweetToast.success(getApplicationContext(), " Xóa Thành Công");
                                    }
                                });
                            }
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        MenuItem searchViewItem = menu.findItem(R.id.app_bar_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                courseListAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Áp dụng bộ lọc dữ liệu vào RecyclerView
                courseListAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public void onBackPressed() {
        if(!searchView.isIconified()){
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();

    }
}
