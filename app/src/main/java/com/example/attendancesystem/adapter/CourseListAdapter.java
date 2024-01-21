package com.example.attendancesystem.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.attendancesystem.R;
import com.example.attendancesystem.model.Course;
import com.example.attendancesystem.model.Student;
import com.example.attendancesystem.model.Teacher;

import java.util.ArrayList;
import java.util.List;

public class CourseListAdapter extends RecyclerView.Adapter<CourseListAdapter.CourseListViewHolder> implements Filterable {

    private List<Course> courseList;
    private Context context;
   IClickListenerCourse mIClickListener;
    private List<Course> courseListold;

    public interface IClickListenerCourse{
        void onClickItem(Course course);

    }
    public CourseListAdapter(Context context, List<Course> courseList,
                            IClickListenerCourse iClickListener) {

        this.context = context;
        this.courseList = courseList;
        this.mIClickListener= iClickListener;
        this.courseListold =courseList;
    }

    public CourseListAdapter(){

    }

    @NonNull
    @Override
    public CourseListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.single_student_layout,viewGroup,false);
        return new CourseListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CourseListViewHolder courseListViewHolder, int i) {
        final Course course = courseList.get(i);
        courseListViewHolder.Course_name.setText(courseList.get(courseListViewHolder.getAdapterPosition()).getCourse_name());
        courseListViewHolder.course_code.setText(courseList.get(courseListViewHolder.getAdapterPosition()).getCourse_code());
        courseListViewHolder.teacherName.setText(courseList.get(courseListViewHolder.getAdapterPosition()).getTeacher());
        courseListViewHolder.layoutForceGround.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIClickListener.onClickItem(course);
            }
        });

    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

   public class CourseListViewHolder extends  RecyclerView.ViewHolder {
        TextView Course_name,teacherName;
        TextView course_code;
        public ConstraintLayout layoutForceGround;

        public CourseListViewHolder(@NonNull View itemView) {
            super(itemView);
            Course_name=itemView.findViewById(R.id.studentNameTV);
            course_code=itemView.findViewById(R.id.studentCourseTv);
            teacherName=itemView.findViewById(R.id.studentIDv);
            layoutForceGround = itemView.findViewById(R.id.forceground_itemstudent);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            // Thực hiện truy vấn tìm kiếm
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String strSearch = constraint.toString();
                // Nếu chưa nhập gì
                if(strSearch.isEmpty()){
                    courseList = courseListold;
                }
                // Nếu nhập rồi
                else {
                    List<Course> list = new ArrayList<>();
                    for(Course course : courseListold){
                        if(course.getCourse_name().toLowerCase().contains(strSearch.toLowerCase())){
                            list.add(course);
                        }
                    }
                    courseList = list;
                }
                // Tạo một filterResults là lưu kết quả
                FilterResults filterResults = new FilterResults();
                filterResults.values = courseList;

                return filterResults;
            }
            // Cập nhật giao diện người dùng với kết quả đã lọc
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                courseList = (List<Course>) results.values;
                // Thông báo adapter đã thay đổi
                notifyDataSetChanged();;
            }
        };
    }

    public void updateCollection(List<Course> courseList){
        this.courseList =courseList;
        notifyDataSetChanged();
    }
    public void removeItem(int index){
        courseList.remove(index);
        notifyItemRemoved(index);
    }
    public void undoItem(Course course, int index){
        courseList.add(index,course);
        notifyItemInserted(index);
    }
}
