package com.example.studentlocator.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.studentlocator.Interface.ItemClickListener;
import com.example.studentlocator.R;

public class DepartmentsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView department;
    private ItemClickListener itemClickListener;

    public DepartmentsViewHolder(@NonNull View itemView) {
        super(itemView);

        this.department = (TextView) itemView.findViewById(R.id.department);
        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void onClick(View view) {
        this.itemClickListener.onClick(view, getAdapterPosition(), false);
    }

}
