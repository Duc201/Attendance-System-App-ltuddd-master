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

import com.example.attendancesystem.ItemTouchHelperListenerTeacher;
import com.example.attendancesystem.R;
import com.example.attendancesystem.RecyclerViewItemTouchHelper;
import com.example.attendancesystem.RecyclerViewItemTouchHelperTeacher;
import com.example.attendancesystem.adapter.StudentListAdapter;
import com.example.attendancesystem.adapter.TeacherListAdapter;
import com.example.attendancesystem.model.Student;
import com.example.attendancesystem.model.Teacher;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import xyz.hasnat.sweettoast.SweetToast;

public class TeacherListActivity extends AppCompatActivity implements ItemTouchHelperListenerTeacher {
    private Toolbar teacherListToolbar;
    private FloatingActionButton addTeacherButton;
    private RecyclerView teacherListRv;
    private String intentedDept,intentedShift;
    private DatabaseReference teacherListRef;
    private List<Teacher>  teacherlist=new ArrayList<>();

    private TeacherListAdapter teacherListAdapter;
    private RelativeLayout rootView;
    private SearchView searchView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_list);

        teacherListToolbar=findViewById(R.id.teacherListToolbar);
        setSupportActionBar(teacherListToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        teacherListRv=findViewById(R.id.teacherListRV);
        rootView=findViewById(R.id.root_view_teacher);

        final Intent intent=getIntent();
        intentedDept=intent.getStringExtra("TDEPT");
        intentedShift=intent.getStringExtra("TSHIFT");

        teacherListRef= FirebaseDatabase.getInstance().getReference().child("Department").child(intentedDept).child("Teacher").child(intentedShift);

        teacherListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                teacherlist.clear();
                if (dataSnapshot.exists()){
                    for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                        if (dataSnapshot1.hasChildren()){
                            Teacher teacher=dataSnapshot1.getValue(Teacher.class);
                            teacherlist.add(teacher);
                        }
                    }

                    teacherListAdapter=new TeacherListAdapter(TeacherListActivity.this, teacherlist, new TeacherListAdapter.IClickListenerTeacher() {
                        @Override
                        public void onClickItem(Teacher teacher) {
                            openEditTeacher(teacher);
                        }
                    });
                    teacherListRv.setLayoutManager(new LinearLayoutManager(TeacherListActivity.this));
                    teacherListAdapter.notifyDataSetChanged();
                    teacherListRv.setAdapter(teacherListAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





        addTeacherButton=findViewById(R.id.addTeacherBtn);

        addTeacherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent(TeacherListActivity.this,AddTeacherActivity.class);
                intent1.putExtra("TDEPT",intentedDept);
                intent1.putExtra("TSHIFT",intentedShift);

                startActivity(intent1);
        }
        });
        ItemTouchHelper.SimpleCallback simpleCallback = new RecyclerViewItemTouchHelperTeacher(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(teacherListRv);
    }

    private void openEditTeacher(Teacher teacher) {
        Intent intent = new Intent(TeacherListActivity.this,EditTeacherActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("teacher",teacher);
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
        if(viewHolder instanceof TeacherListAdapter.TeacherListViewHolder){
            final String nameImageDelete =teacherlist.get(viewHolder.getAdapterPosition()).getName();

            final Teacher teacherDelete = teacherlist.get(viewHolder.getAdapterPosition());
            final int indexDelte = viewHolder.getAdapterPosition();


            teacherListAdapter.removeItem(indexDelte);
            Snackbar snackbar = Snackbar.make(rootView,nameImageDelete+"remover!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    teacherListAdapter.undoItem(teacherDelete,indexDelte);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);

            snackbar.addCallback(new Snackbar.Callback(){
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    super.onDismissed(transientBottomBar, event);
                    if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                        deleteStudentFireBase(teacherDelete);
                    }
                }


            });
            snackbar.show();

        }
    }

    private void deleteStudentFireBase(final Teacher teacherDelete) {
        teacherListRef= FirebaseDatabase.getInstance().getReference().child("Department").child(intentedDept).child("Teacher").child(intentedShift);


        teacherListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        if (dataSnapshot1.hasChildren()) {
                            Teacher teacherFB = dataSnapshot1.getValue(Teacher.class);
                            if(teacherFB.getId()== teacherDelete.getId()){
                                teacherListRef.child(dataSnapshot1.getKey()).removeValue(new DatabaseReference.CompletionListener() {
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
                teacherListAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Áp dụng bộ lọc dữ liệu vào RecyclerView
                teacherListAdapter.getFilter().filter(newText);
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
