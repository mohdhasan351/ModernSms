package com.example.modernsms.adapters;

import android.content.Context;
import android.provider.Telephony;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.modernsms.R;
import com.example.modernsms.models.SmsModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ViewSmsAdapter extends RecyclerView.Adapter {
    private List<SmsModel> list;
    private Context ctx;
    Calendar c;
    SimpleDateFormat sdf;
    Date monday;
    Date nextMonday;
    String currentdate;
    Set<String> uniqueday;

    public ViewSmsAdapter() {
        currentdate = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
        uniqueday =new HashSet<>();
        initialize();
    }

    public void setCtx(Context ctx) {
        this.ctx = ctx;
    }

    public void setList(List<SmsModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.smsboxright, parent, false);
            return new SenderViewSenderSmsHolder(view);
        }
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.smsboxleft, parent, false);
        return new ReceiverViewSenderSmsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getClass() == ReceiverViewSenderSmsHolder.class) {
            ((ReceiverViewSenderSmsHolder) holder).textView.setText(list.get(position).getBody());
            ((ReceiverViewSenderSmsHolder)holder).time.setText(list.get(position).getTime());

            if(currentdate.equals(list.get(position).getDate())){
                ((ReceiverViewSenderSmsHolder)holder).date.setVisibility(View.GONE);
            }else if(inBetweenThisWeek(list.get(position).getDate())) {
                if(!uniqueday.contains(list.get(position).getDay())) {
                    ((ReceiverViewSenderSmsHolder)holder).date.setVisibility(View.VISIBLE);
                    ((ReceiverViewSenderSmsHolder) holder).date.setText(list.get(position).getDay());
                    uniqueday.add(list.get(position).getDay());
                }else{
                    ((ReceiverViewSenderSmsHolder)holder).date.setVisibility(View.GONE);
                }
            }
            else
                ((ReceiverViewSenderSmsHolder)holder).date.setText(list.get(position).getDate());


        }else{
            ((SenderViewSenderSmsHolder)holder).textView.setText(list.get(position).getBody());
            ((SenderViewSenderSmsHolder)holder).time.setText(list.get(position).getTime());

            if(currentdate.equals(list.get(position).getDate())){
                ((SenderViewSenderSmsHolder)holder).date.setVisibility(View.GONE);
            }else if(inBetweenThisWeek(list.get(position).getDate())) {
                if(!uniqueday.contains(list.get(position).getDay())) {
                    ((SenderViewSenderSmsHolder) holder).date.setVisibility(View.VISIBLE);
                    ((SenderViewSenderSmsHolder) holder).date.setText(list.get(position).getDay());
                    uniqueday.add(list.get(position).getDay());
                }else{
                    ((SenderViewSenderSmsHolder) holder).date.setVisibility(View.GONE);

                }
            }
            else
                ((SenderViewSenderSmsHolder)holder).date.setText(list.get(position).getDate());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).getType() == Telephony.Sms.MESSAGE_TYPE_SENT)
            return 1;
        return 2;
    }


    class SenderViewSenderSmsHolder extends RecyclerView.ViewHolder {

        TextView textView,date,time;
        public SenderViewSenderSmsHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            date = itemView.findViewById(R.id.datelong);
            time = itemView.findViewById(R.id.timeday);
        }
    }

    class ReceiverViewSenderSmsHolder extends RecyclerView.ViewHolder {
        TextView textView,date,time;

        public ReceiverViewSenderSmsHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            date = itemView.findViewById(R.id.datelong);
            time = itemView.findViewById(R.id.timeday);
            //textView.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }
    //helper fun for checking date lies in this week then return true
    public  boolean inBetweenThisWeek(String yourDate) {

        Date date2 = null;
        try {
            date2 = sdf.parse(yourDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        boolean isThisWeek = (date2.compareTo(monday)>=0) && date2.before(nextMonday);
        return isThisWeek;
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
}
