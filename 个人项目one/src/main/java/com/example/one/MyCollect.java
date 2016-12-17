package com.example.one;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * Created by 44967 on 2016/11/15.
 */
@Table(name = "myCollect", onCreated = "")
public class MyCollect implements Serializable{
    @Column(name = "id", isId = true, autoGen = true)
    public int id;
    @Column(name = "title")
    public String title;
    @Column(name = "image")
    public String image;
    @Column(name = "url")
    public String url;
    @Column(name = "imgId")
    public String imgId;
    public MyCollect(String image, String title, String url,String imgId) {
        this.image = image;
        this.title = title;
        this.url = url;
        this.imgId=imgId;
    }

    public MyCollect() {
    }
}
