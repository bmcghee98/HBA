package com.coinnpursecoding.hba;

public class Birthday {
    private String personName;
    private long longDate;

    Birthday(String name, long date){
        personName = name;
        longDate = date;
    }

    Birthday(long date){
        longDate = date;
        personName = "";
    }

    public String getName(){
        return personName;
    }

    public long getLongDate(){
        return longDate;
    }


}
