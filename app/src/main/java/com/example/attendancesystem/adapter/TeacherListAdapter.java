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
import android.widget.TextView;

import com.example.attendancesystem.R;
import com.example.attendancesystem.model.Student;
import com.example.attendancesystem.model.Teacher;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class TeacherListAdapter extends RecyclerView.Adapter<TeacherListAdapter.TeacherListViewHolder> implements Filterable {

    private List<Teacher> teacherlist;
    private Context context;
    private List<Teacher> teacherlistold;
    IClickListenerTeacher mIClickListener;



    public interface IClickListenerTeacher{
        void onClickItem(Teacher teacher);

    }
    public TeacherListAdapter(Context context, List<Teacher> teacherlist,IClickListenerTeacher iClickListener) {

        this.context = context;
        this.teacherlist = teacherlist;
        this.mIClickListener= iClickListener;
        this.teacherlistold = teacherlist;
    }

    public TeacherListAdapter(){

    }

    @NonNull
    @Override
    public TeacherListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.single_student_layout,viewGroup,false);
        return new TeacherListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherListViewHolder teacherListViewHolder, int i) {
        final Teacher teacher = teacherlist.get(i);

        teacherListViewHolder.Student_name.setText(teacherlist.get(teacherListViewHolder.getAdapterPosition()).getName());
        teacherListViewHolder.course_code.setText(teacherlist.get(teacherListViewHolder.getAdapterPosition()).getDesignation());
        teacherListViewHolder.teacherEmail.setText(teacherlist.get(teacherListViewHolder.getAdapterPosition()).getEmail());

        Picasso.get().load(teacherlist.get(teacherListViewHolder.getAdapterPosition()).getPathImage())
                .error(R.drawable.ic_account)
                .placeholder(R.drawable.ic_account)
                .into(teacherListViewHolder.Teacher_image);
        teacherListViewHolder.layoutForceGround.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIClickListener.onClickItem(teacher);
            }
        });
    }

    @Override
    public int getItemCount() {
        return teacherlist.size();
    }

   public class TeacherListViewHolder extends  RecyclerView.ViewHolder {
        TextView Student_name,teacherEmail;
        TextView course_code;
        public ConstraintLayout layoutForceGround;
        ImageView Teacher_image;
        public TeacherListViewHolder(@NonNull View itemView) {
            super(itemView);
            Student_name=itemView.findViewById(R.id.studentNameTV);
            course_code=itemView.findViewById(R.id.studentCourseTv);
            teacherEmail=itemView.findViewById(R.id.studentIDv);
            layoutForceGround = itemView.findViewById(R.id.forceground_itemstudent);
            Teacher_image= itemView.findViewById(R.id.image_account);
        }
    }

    public void updateCollection(List<Teacher> studentList){
        this.teacherlist =studentList;
        notifyDataSetChanged();
    }

    public void removeItem(int index){
        teacherlist.remove(index);
        notifyItemRemoved(index);
    }
    public void undoItem(Teacher teacher, int index){
        teacherlist.add(index,teacher);
        notifyItemInserted(index);
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
                    teacherlist = teacherlistold;
                }
                // Nếu nhập rồi
                else {
                    List<Teacher> list = new ArrayList<>();
                    for(Teacher teacher : teacherlistold){
                        if(teacher.getName().toLowerCase().contains(strSearch.toLowerCase())){
                            list.add(teacher);
                        }
                    }
                    teacherlist = list;
                }
                // Tạo một filterResults là lưu kết quả
                FilterResults filterResults = new FilterResults();
                filterResults.values = teacherlist;

                return filterResults;
            }

            // Cập nhật giao diện người dùng với kết quả đã lọc
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                teacherlist = (List<Teacher>) results.values;
                // Thông báo adapter đã thay đổi
                notifyDataSetChanged();;
            }
        };
    }

}
