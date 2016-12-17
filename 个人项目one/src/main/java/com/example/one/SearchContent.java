package com.example.one;

import java.util.ArrayList;

/**
 * Created by 44967 on 2016/11/25.
 */
public class SearchContent {
    public String showapi_res_code;
    public String showapi_res_error;
    public ResBody showapi_res_body;
}
class ResBody{
    public Page  pagebean;
}
class Page{
    public  String  allPages;
    public ArrayList<Contentlist> contentlist;
}
class Contentlist{
    public String id;
    public String pubNum;
    public String type2_name;
    public String userLogo;
    public String code2img;
    public String weiNum;

}