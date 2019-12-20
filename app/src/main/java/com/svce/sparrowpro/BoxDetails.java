package com.svce.sparrowpro;

import java.io.Serializable;


public class BoxDetails implements Serializable {
    String boxkey,address,adopt;
    BoxDetails(String boxkey,String address,String adopt)
    {
        this.boxkey=boxkey;
        this.adopt=adopt;
        this.address=address;
    }

    public void setAdopt(String adopt) {
        this.adopt = adopt;
    }
}
