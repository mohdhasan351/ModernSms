package com.example.modernsms.repositories;

import android.content.Context;

import androidx.activity.result.ActivityResult;
import androidx.lifecycle.MutableLiveData;

import com.example.modernsms.datasource.SmsData;
import com.example.modernsms.models.ContactsModel;
import com.example.modernsms.models.SmsModel;

import java.util.List;

public class RepositorySms {
    private static RepositorySms instance;
    private final SmsData smsData;
    public RepositorySms(Context context){
        this.smsData = SmsData.getInstance(context);
    }
    public static RepositorySms getInstance(Context context){
        if(instance==null) {
            instance =  new RepositorySms(context);
        }
        return instance;
    }
    public MutableLiveData<List<SmsModel>> getAllSms(){
        return smsData.getAllSms();
    }
    public MutableLiveData<List<SmsModel>> getSmsByAddress(String address){
        return smsData.getSmsByAddress(address);
    }
    public MutableLiveData<List<ContactsModel>> getContacts(ActivityResult result){
        return smsData.getContacts(result);
    }
}
