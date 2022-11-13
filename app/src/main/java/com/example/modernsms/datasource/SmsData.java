package com.example.modernsms.datasource;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.lifecycle.MutableLiveData;

import com.example.modernsms.fragments.SentSmsFragment;
import com.example.modernsms.fragments.SmsListFragment;
import com.example.modernsms.models.ContactsModel;
import com.example.modernsms.models.SmsModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SmsData {
    private Context ctx;
    private ContentResolver contentResolver;
    private MutableLiveData<List<SmsModel>> mutableLiveData;
    private MutableLiveData<List<SmsModel>> mutableLiveDataByAddress;
    private MutableLiveData<List<ContactsModel>> mutableContactsData;
    boolean isContactAvail;
    boolean isAddressAvail;
    String address;
    private static SmsData instance;

    public static SmsData getInstance(Context context) {
        if (instance == null)
            instance = new SmsData(context);
        return instance;
    }



    public SmsData(Context context) {
        this.isContactAvail = false;
        this.isAddressAvail = false;
        this.ctx = context;
        this.mutableLiveData = new MutableLiveData<>();
        this.mutableLiveDataByAddress = new MutableLiveData<>();
        this.mutableContactsData = new MutableLiveData<>();
        contentResolver = ctx.getContentResolver();
        contentResolver.registerContentObserver(Telephony.Sms.CONTENT_URI,true,new MyObserver(new Handler()));
    }

    public MutableLiveData<List<SmsModel>> getAllSms() {
        isContactAvail = false;
        isAddressAvail = false;
        ArrayList<SmsModel> list = new ArrayList<>();

        Cursor c = contentResolver.query(Telephony.Sms.CONTENT_URI,
                new String[]{Telephony.Sms.ADDRESS, Telephony.Sms.BODY, Telephony.Sms.DATE,Telephony.Sms.TYPE}
                , null, null,
                Telephony.Sms.DEFAULT_SORT_ORDER);
        for (c.moveToFirst(); c.getPosition() < c.getCount(); c.moveToNext()) {
            String date = new SimpleDateFormat("dd-MM-yyy").format(new Date(c.getLong(2)));
            String time = new SimpleDateFormat("hh:mm aa").format(new Date(c.getLong(2)));
            String day = new SimpleDateFormat("EEEE").format(new Date(c.getLong(2)));
            //Log.d("sms",c.getString(0)+" date "+date+" time "+time+" day "+day);

            list.add(new SmsModel(c.getString(0).trim(),c.getString(1).trim(),date,time,day,c.getInt(3)));
        }
        mutableLiveData.setValue(list);
        c.close();
        return mutableLiveData;
    }

    public MutableLiveData<List<SmsModel>> getSmsByAddress(String address) {
        this.address = address;
        ArrayList<SmsModel> list = new ArrayList<>();
        isAddressAvail = true;
        Cursor c = contentResolver.query(Telephony.Sms.CONTENT_URI,
                new String[]{Telephony.Sms.ADDRESS, Telephony.Sms.BODY, Telephony.Sms.DATE, Telephony.Sms.TYPE}, "Address='" + address + "'", null,
                "date");
        for (c.moveToFirst(); c.getPosition() < c.getCount(); c.moveToNext()) {
            String date = new SimpleDateFormat("dd-MM-yyy").format(new Date(c.getLong(2)));
            String time = new SimpleDateFormat("hh:mm: aa").format(new Date(c.getLong(2)));
            String day = new SimpleDateFormat("EEEE").format(new Date(c.getLong(2)));
            list.add(new SmsModel(c.getString(0).trim(),c.getString(1).trim(),date,time,day,c.getInt(3)));
            Log.d("type", "" + c.getInt(3));
        }
        c.close();
        mutableLiveDataByAddress.setValue(list);
        return mutableLiveDataByAddress;
    }

    public MutableLiveData<List<ContactsModel>> getContacts(ActivityResult result) {
        List<ContactsModel> list = new ArrayList<>();
        isContactAvail = true;
        Uri uri = result.getData().getData();
        Cursor c = ctx.getContentResolver().query(uri, null, null, null, null);
        if (c.moveToFirst()) {
            String name = c.getString(c.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String number = c.getString(c.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
            number=number.replaceAll("[^+\\d]","");
            list.add(new ContactsModel(name, number));
        }

        mutableContactsData.setValue(list);
        return mutableContactsData;
    }
    private class MyObserver extends ContentObserver{
        public MyObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            if(isContactAvail)
                getSmsByAddress(mutableContactsData.getValue().get(0).getNumber());
            else if(isAddressAvail)
                getSmsByAddress(address);
            else getAllSms();
        }

    }


}
