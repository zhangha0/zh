package com.example.one;

import android.graphics.pdf.PdfDocument;

import java.util.ArrayList;

/**
 * Created by 44967 on 2016/11/13.
 */
public class ArticleList {
    public Body2 showapi_res_body;
}
class Body2{
    public Pages pagebean;
}
class Pages{
    public int allPages;
    public ArrayList<Content> contentlist;
}
class Content{
    public String contentImg;
    public String date;
    public String id;
    public String title;
    public String typeName;
    public String url;
    public String userLogo_code;
    public String userName;
}