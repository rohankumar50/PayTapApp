package com.vastgk.paytap;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TransactionsModel {
    private String id,time,amount,type,vendorid,vendorname;

    public TransactionsModel(String id, String time, String amount, String type, String vendorid, String vendorname) {
        this.id = id;
        this.time = time;
        this.amount = amount;
        this.type = type;
        this.vendorid = vendorid;
        this.vendorname = vendorname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
try {

   DateFormat original=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
   DateFormat newformat=new SimpleDateFormat("dd MMM yyyy hh:mm:ss a");
Date date=original.parse(this.time);
    time=newformat.format(date);


}catch (Exception e) {
    return time;
}
return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVendorid() {
        return vendorid;
    }

    public void setVendorid(String vendorid) {
        this.vendorid = vendorid;
    }

    public String getVendorname() {
        return vendorname;
    }

    public void setVendorname(String vendorname) {
        this.vendorname = vendorname;
    }
}
