package com.example.one;

import java.util.ArrayList;

/**
 * Created by 44967 on 2016/11/13.
 */
public class TypeList {
    public Body showapi_res_body;
}
class Body{
    public String ret_code;
    public ArrayList<TList> typeList;
}
class TList{
    public String id;
    public String name;
}