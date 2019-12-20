package com.svce.sparrowpro;


import java.io.Serializable;

public class SparrowReportDetails implements Serializable{
    String userkey="",userid="",reportid="",sparrowimageurl="",reporteddate="",status="",locationkey="";
    SparrowReportDetails(String userkey,String userid,String reportid,String reporteddate,String sparrowimageurl,String status,String locationkey)
    {
        this.userkey=userkey;
        this.userid=userid;
        this.reportid=reportid;
        this.reporteddate=reporteddate;
        this.sparrowimageurl=sparrowimageurl;
        this.status=status;
        this.locationkey=locationkey;
    }
    public void setStatus(String status)
    {
        this.status=status;
    }
}
