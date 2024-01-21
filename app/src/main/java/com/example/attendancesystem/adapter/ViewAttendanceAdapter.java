package com.example.attendancesystem.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
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

import xyz.hasnat.sweettoast.SweetToast;

public class ViewAttendanceAdapter extends RecyclerView.Adapter<ViewAttendanceAdapter.ViewAttendanceViewHolder>  implements Filterable {

    private List<Student> studentList=new ArrayList<>();
    private static List<Student> studentListold;

    IClickListenerAttend mIClickListener;

    private Context context;
    private DatabaseReference presentRef,absentRef;



    public interface IClickListenerAttend{
        void onClickItem(Student student);
    }

    public ViewAttendanceAdapter(Context context, List<Student> studentList, IClickListenerAttend iClickListenerAttend) {

        this.context = context;
        this.studentList = studentList;
        this.mIClickListener = iClickListenerAttend;
        this.studentListold = studentList;

    }

    public ViewAttendanceAdapter(){

    }

    @NonNull
    @Override
    public ViewAttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.single_student_layout,viewGroup,false);
        return new ViewAttendanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewAttendanceViewHolder viewAttendanceViewHolder, final int i) {
        final Student student = studentList.get(i);
        viewAttendanceViewHolder.Student_name.setText(studentList.get(viewAttendanceViewHolder.getAdapterPosition()).getName());
//        viewAttendanceViewHolder.course_code.setText(studentList.get(viewAttendanceViewHolder.getAdapterPosition()).getCourse_code());
        viewAttendanceViewHolder.id.setText(studentList.get(viewAttendanceViewHolder.getAdapterPosition()).getId());
        Picasso.get().load(studentList.get(viewAttendanceViewHolder.getAdapterPosition()).getPathImage())
                .error(R.drawable.ic_account)
                .placeholder(R.drawable.ic_account)
                .into(viewAttendanceViewHolder.Student_image);


        viewAttendanceViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
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

    class ViewAttendanceViewHolder extends  RecyclerView.ViewHolder {
        TextView Student_name;
        TextView course_code;
        TextView id;

        ImageView Student_image;
        public ViewAttendanceViewHolder(@NonNull View itemView) {
            super(itemView);
            Student_name=itemView.findViewById(R.id.studentNameTV);
            course_code=itemView.findViewById(R.id.studentCourseTv);
            id=itemView.findViewById(R.id.studentIDv);
            Student_image=itemView.findViewById(R.id.image_account);
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
}
