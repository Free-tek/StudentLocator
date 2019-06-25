package com.example.studentlocator.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Category {


    private String mTitle;
    private Category category;

    private String mCatgPhoto;
    private UUID mCatgId;

    public Category(){

    }

    public Category(String title){
        this.mTitle=title;
    }

    private List<Category>mCategory;


    public List<Category> getCategory(){
      List<Category> mCategory=new ArrayList<>();
        mCategory.add(new Category("BIOCHEMISTRY"));
        mCategory.add(new Category("CHEMICAL ENGR"));
        mCategory.add(new Category("PHARMACY"));
        mCategory.add(new Category("MED LAB"));
        mCategory.add(new Category("MICROBIOLOGY"));
        mCategory.add(new Category("GEOGRAPHY"));
        mCategory.add(new Category("COMPUTER ENGR"));
        mCategory.add(new Category("MASS COM"));
        mCategory.add(new Category("ELECTRICAL ENGR"));
        mCategory.add(new Category("PHYSICS"));
        mCategory.add(new Category("MATHEMATICS"));
        mCategory.add(new Category("BIOLOGY"));
        mCategory.add(new Category("BIOSCIENCES"));
        mCategory.add(new Category("COMPUTER SCIENCE"));
        mCategory.add(new Category("ARTS AND CULTURE"));
        mCategory.add(new Category("ECONOMY"));
        mCategory.add(new Category("NUTRITION"));
        mCategory.add(new Category("AGRIC SCIENCE"));
        mCategory.add(new Category("MEDICINE"));
        mCategory.add(new Category("HISTORY"));
        mCategory.add(new Category("POLITICAL SCIENCE"));

        return mCategory;

    }

  public Category get(int pos){
        if(category==null)
           category=new Category();
        return category;
  }



    public void Size(){
        mCategory=new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Category cat = new Category();
           // cat.setCatgTitle("Category " + i);
            mCategory.add(cat);
        }
    }

    public String getCatgPhoto() {
        return mCatgPhoto;
    }

    public void setCatgPhoto(String catgPhoto) {
        mCatgPhoto = catgPhoto;
    }


    public UUID getCatgId() {
        return mCatgId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }
}
