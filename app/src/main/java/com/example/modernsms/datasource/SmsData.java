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

import androidx.activity.result.ActivityResult;
import androidx.lifecycle.MutableLiveData;

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
    SimpleDateFormat sdfd,sdft,sdfday;
    Calendar c;
    SimpleDateFormat sdf;
    Date monday;
    Date nextMonday;
    String currentdate;

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

        sdfd=new SimpleDateFormat("dd-MM-yyy");
        sdfday=new SimpleDateFormat("EEEE");
        sdft = new SimpleDateFormat("hh:mm: aa");

        currentdate = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
        initialize();
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
            String date = sdfd.format(new Date(c.getLong(2)));
            String time = sdft.format(new Date(c.getLong(2)));
            String day = sdfday.format(new Date(c.getLong(2)));
            //Log.d("sms",c.getString(0)+" date "+date+" time "+time+" day "+day);

            //(String address, String body, String date, String time, String day, String displayDate, int type)

            if(date.equals(currentdate))
                list.add(new SmsModel(c.getString(0).trim(),c.getString(1).trim(),date,time,day,time,c.getInt(3)));
            else if(inBetweenThisWeek(date))
                list.add(new SmsModel(c.getString(0).trim(),c.getString(1).trim(),date,time,day,day,c.getInt(3)));
            else
                list.add(new SmsModel(c.getString(0).trim(),c.getString(1).trim(),date,time,day,date,c.getInt(3)));
            //list.add(new SmsModel(c.getString(0).trim(),c.getString(1).trim(),date,time,day,c.getInt(3)));
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
            String date = sdfd.format(new Date(c.getLong(2)));
            String time = sdft.format(new Date(c.getLong(2)));
            String day = sdfday.format(new Date(c.getLong(2)));

            //(String address, String body, String date, String time, String day, String displayDate, int type)

            if(date.equals(currentdate))
                list.add(new SmsModel(c.getString(0).trim(),c.getString(1).trim(),date,time,day,time,c.getInt(3)));
            else if(inBetweenThisWeek(date))
                list.add(new SmsModel(c.getString(0).trim(),c.getString(1).trim(),date,time,day,day,c.getInt(3)));
            else
                list.add(new SmsModel(c.getString(0).trim(),c.getString(1).trim(),date,time,day,date,c.getInt(3)));
            //Log.d("type", "" + c.getInt(3));
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
        c.close();
        mutableContactsData.setValue(list);
        return mutableContactsData;
    }

    //helper fun for checking date lies in this week then return true
    public  boolean inBetweenThisWeek(String yourDate) {
        Date date2 = null;
        try {
            date2 = sdf.parse(yourDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  ((date2.compareTo(monday)>=0) && date2.before(nextMonday));
    }
    void initialize(){
        c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        monday = c.getTime();
        sdf = new SimpleDateFormat("dd-MM-yyyy");
        nextMonday= new Date(monday.getTime()+7*24*60*60*1000);
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
