package com.example.logintesting;

public class Upload {
    private String mName;
    private String mImageUrl;

    public Upload()
    {
        //empty constrycter needed
    }

    public Upload(String name,String ImageUrl)
    {
        if(name.trim().equals(""))
        {
            name="No Name";
        }

        mName=name;
        mImageUrl=ImageUrl;
    }



    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }
}
