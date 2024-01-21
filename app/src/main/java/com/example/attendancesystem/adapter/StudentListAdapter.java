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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.attendancesystem.R;
import com.example.attendancesystem.model.Student;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.StudentListViewHolder>
implements Filterable {

    private static List<Student> studentList;
    private static List<Student> studentListold;
    private Context context;

    IClickListener mIClickListener;


    public interface IClickListener{
        void onClickItem(Student student);

    }

    public StudentListAdapter(Context context,List<Student> studentList,
                              IClickListener iClickListener) {

        this.context = context;
        this.studentList = studentList;
        this.studentListold = studentList;
        this.mIClickListener= iClickListener;

    }

    public  StudentListAdapter(){

    }

    @NonNull
    @Override
    public StudentListAdapter.StudentListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.single_student_layout,viewGroup,false);
        return new StudentListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentListAdapter.StudentListViewHolder studentListViewHolder, int i) {
        final Student student = studentList.get(i);
        studentListViewHolder.Student_name.setText(studentList.get(studentListViewHolder.getAdapterPosition()).getName());
        studentListViewHolder.course_code.setText(studentList.get(studentListViewHolder.getAdapterPosition()).getCourse_code());
        studentListViewHolder.id.setText(studentList.get(studentListViewHolder.getAdapterPosition()).getId());
        Picasso.get().load(studentList.get(studentListViewHolder.getAdapterPosition()).getPathImage())
                .error(R.drawable.ic_account)
                .placeholder(R.drawable.ic_account)
                .into(studentListViewHolder.Student_image);
        studentListViewHolder.layoutForceGround.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIClickListener.onClickItem(student);
            }
        });
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public class StudentListViewHolder extends  RecyclerView.ViewHolder {
        TextView Student_name;
        TextView course_code;
        TextView id;
        ImageView Student_image;
       public ConstraintLayout layoutForceGround;
        public StudentListViewHolder(@NonNull View itemView) {
            super(itemView);
            Student_name=itemView.findViewById(R.id.studentNameTV);
            course_code=itemView.findViewById(R.id.studentCourseTv);
            id=itemView.findViewById(R.id.studentIDv);
            Student_image=itemView.findViewById(R.id.image_account);
            layoutForceGround = itemView.findViewById(R.id.forceground_itemstudent);
        }
    }

    public void updateCollection(List<Student> studentList){
        this.studentList=studentList;
        notifyDataSetChanged();
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
                    studentList = studentListold;
                }
                // Nếu nhập rồi
                else {
                    List<Student> list = new ArrayList<>();
                    for(Student student : studentListold){
                        if(student.getName().toLowerCase().contains(strSearch.toLowerCase())){
                            list.add(student);
                        }
                    }
                    studentList = list;
                }
                // Tạo một filterResults là lưu kết quả
                FilterResults filterResults = new FilterResults();
                filterResults.values = studentList;

                return filterResults;
            }

            // Cập nhật giao diện người dùng với kết quả đã lọc
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                studentList = (List<Student>) results.values;
                // Thông báo adapter đã thay đổi
                notifyDataSetChanged();;
            }
        };
    }
    public void removeItem(int index){
        studentList.remove(index);
        notifyItemRemoved(index);
    }
    public void undoItem(Student student, int index){
        studentList.add(index,student);
        notifyItemInserted(index);
    }
}
