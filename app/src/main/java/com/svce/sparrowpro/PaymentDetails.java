package com.svce.sparrowpro;

import java.io.Serializable;

public class PaymentDetails implements Serializable {
    String paymentid,boxid,userkey,userid,transdate,processstatus;
    PaymentDetails( String paymentid,String boxid,String userkey,String userid,String transdate,String processstatus)
    {
        this.paymentid=paymentid;
        this.boxid=boxid;
        this.userkey=userkey;
        this.userid=userid;
        this.transdate=transdate;
        this.processstatus=processstatus;
    }
    public void setProcessstatus(String processstatus)
    {
        this.processstatus=processstatus;
    }
}
