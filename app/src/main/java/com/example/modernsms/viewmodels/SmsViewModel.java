package com.example.modernsms.viewmodels;

import android.content.Context;

import androidx.activity.result.ActivityResult;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.modernsms.models.ContactsModel;
import com.example.modernsms.models.SmsModel;
import com.example.modernsms.repositories.RepositorySms;

import java.util.List;

public class SmsViewModel extends ViewModel {
    private RepositorySms repositorySms;
    public void init(Context context){
        this.repositorySms = RepositorySms.getInstance(context);
    }

    public LiveData<List<SmsModel>> getAllSms() {
        return repositorySms.getAllSms();
    }
    public LiveData<List<SmsModel>> getSmsByAddress(String address){
        return repositorySms.getSmsByAddress(address);
    }
    public LiveData<List<ContactsModel>> getContacts(ActivityResult result){
        return repositorySms.getContacts(result);
    }
}
