package com.example.studentlocator.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.studentlocator.Interface.ItemClickListener;
import com.example.studentlocator.R;

public class StudentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ImageView  profile_image;
    public TextView student_name, student_department, student_location;
    ItemClickListener itemClickListener;

    public StudentViewHolder(@NonNull View itemView) {
        super(itemView);

        this.profile_image = (ImageView) itemView.findViewById(R.id.profile_image);
        this.student_name = (TextView) itemView.findViewById(R.id.student_name);
        this.student_department = (TextView) itemView.findViewById(R.id.student_department);
        this.student_location = (TextView) itemView.findViewById(R.id.student_location);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void onClick(View view) {
        this.itemClickListener.onClick(view, getAdapterPosition(), false);
    }

}
