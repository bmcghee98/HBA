package com.coinnpursecoding.hba;

public class Birthday {
    private String personName;
    private long longDate;
    private String dbDate;

    Birthday(String name, long date, String formDate){
        personName = name;
        longDate = date;
        dbDate = formDate;
    }

    Birthday(long date){
        longDate = date;
        personName = "";
    }

    public String getName(){return personName; }

    public long getLongDate(){ return longDate; }

    public String getDbDate(){ return dbDate; }


}
