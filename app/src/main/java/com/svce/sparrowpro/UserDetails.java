package com.svce.sparrowpro;

import java.io.Serializable;

public class UserDetails implements Serializable{
    String username,password,actcreated,city,district,dob,gender,hierarchy,mobile,name,profileurl,state,tnc,userkey;
    UserDetails() {}
    UserDetails(String username,String password,String actcreated,String city,String district,String dob,String gender,String hierarchy,String mobile,String name,String profileurl,String state,String tnc,String userkey)
    {
        this.username=username;
        this.password=password;
        this.actcreated=actcreated;
        this.city=city;
        this.district=district;
        this.dob=dob;
        this.gender=gender;
        this.hierarchy=hierarchy;
        this.mobile=mobile;
        this.name=name;
        this.profileurl=profileurl;
        this.state=state;
        this.tnc=tnc;
        this.userkey=userkey;
    }
    public void setUsername(String username)
    {
        this.username=username;
    }
    public void setPassword(String password)
    {
        this.password=password;
    }
    public void setActcreated(String actstaus) {this.actcreated=actcreated; }
    public void setCity(String city)
    {
        this.city=city;
    }
    public void setDistrict(String district)
    {
        this.district=district;
    }
    public void setDob(String dob)
    {
        this.dob=dob;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public void setHierarchy(String hierarchy)
    {
        this.hierarchy=hierarchy;
    }
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setProfileurl(String profileurl) {
        this.profileurl = profileurl;
    }
    public void setState(String state) {
        this.state = state;
    }
    public void setTnc(String tnc) { this.tnc = tnc; }
    public void setUserkey(String userkey) { this.userkey = userkey; }
}
