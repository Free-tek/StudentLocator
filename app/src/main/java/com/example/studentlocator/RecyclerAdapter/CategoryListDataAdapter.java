package com.example.studentlocator.RecyclerAdapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studentlocator.Interface.ItemClickListener;
import com.example.studentlocator.Model.Category;
import com.example.studentlocator.R;

import java.util.List;
import java.util.Random;

import static android.graphics.Color.BLACK;

public class CategoryListDataAdapter extends RecyclerView.Adapter<CategoryListDataAdapter.CategoryHolder> {

   //
    List<Category> mCategories;

    private Context mContext;

   public CategoryListDataAdapter(List<Category> category, Context mContext) {
        //List<Category> mCategories = Category.getCategory();
        mCategories = category;
        this.mContext=mContext;

    }


    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.client_grid_view, null);
        CategoryHolder catHolder = new CategoryHolder(v);
        return catHolder;
    }

    @Override
    public void onBindViewHolder(CategoryHolder holder, int position) {

        Category mCategory = mCategories.get(position);
        Random r=new Random();
        int color=Color.argb(255,r.nextInt(256),r.nextInt(256),r.nextInt(256));

        final Category category = mCategory.get(position);
        holder.bind(category);
        holder.mCatView.setText(mCategory.getTitle());
        holder.mCatView.setTextColor(BLACK);

        holder.mCatView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "clicked!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {

        if (mCategories!=null){
            return mCategories.size();
            }else{
            return 0;
        }
    }


    public class CategoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {

        private TextView mCatView;
        public Category mCategory;
        private RecyclerView mRecyclerview;
        private ItemClickListener itemClickListener;


        public CategoryHolder(View itemView) {
            super(itemView);
            this.mCatView = (TextView) itemView.findViewById(R.id.cat_label);
            this.mRecyclerview = (RecyclerView) itemView.findViewById(R.id.horizontal_recyclerview);
            itemView.setOnClickListener(this);


        }

        public void bind(Category cat) {
            mCategory = cat;

        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        public void onClick(View view) {
            this.itemClickListener.onClick(view, getAdapterPosition(), false);
        }
    }
}







