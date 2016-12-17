package com.example.one;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * Created by 44967 on 2016/12/3.
 */
@Table(name = "myLogin", onCreated = "")
public class MyLogin implements Serializable{
    @Column(name = "id", isId = true, autoGen = true)
    public int id;
    @Column(name = "myName")
    public String myName;
    @Column(name = "tou")
    public String tou;
    @Column(name = "logNum")
    public String logNum;
    @Column(name = "passWord")
    public String passWord;
    public MyLogin(){}
    public MyLogin(String myName,String tou,String logNum,String passWord){
        this.myName=myName;
        this.tou=tou;
        this.logNum=logNum;
        this.passWord=passWord;
    }
}
