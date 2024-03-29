package com.example.attendancesystem;

import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.example.attendancesystem.adapter.CourseListAdapter;
import com.example.attendancesystem.adapter.StudentListAdapter;

public class RecyclerViewItemTouchHelperCourse extends ItemTouchHelper.SimpleCallback {
    private ItemTouchHelperListener mlistener;
    public RecyclerViewItemTouchHelperCourse(int dragDirs, int swipeDirs, ItemTouchHelperListener listener) {

        super(dragDirs, swipeDirs);
        this.mlistener = listener;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        if(mlistener != null){
            mlistener.onSwiped(viewHolder);
        }
    }


    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        if(viewHolder != null){
            View foreGroundView = ((CourseListAdapter.CourseListViewHolder) viewHolder).layoutForceGround;
            getDefaultUIUtil().onSelected(foreGroundView);

        }
    }

    @Override
    public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View foreGroundView = ((CourseListAdapter.CourseListViewHolder) viewHolder).layoutForceGround;
        getDefaultUIUtil().onDrawOver(c, recyclerView, foreGroundView, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View foreGroundView = ((CourseListAdapter.CourseListViewHolder) viewHolder).layoutForceGround;
        getDefaultUIUtil().onDraw(c, recyclerView, foreGroundView, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        View foreGroundView = ((CourseListAdapter.CourseListViewHolder) viewHolder).layoutForceGround;
        getDefaultUIUtil().clearView(foreGroundView);
    }
}
