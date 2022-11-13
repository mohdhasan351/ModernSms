package com.example.modernsms.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.modernsms.R;
import com.example.modernsms.interfaces.OnRecyclerItemClick;
import com.example.modernsms.models.SmsModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class ListSmsAdapter extends RecyclerView.Adapter<ListSmsAdapter.ViewHolder> {
    private Context context;
    private List<SmsModel> list;
    private OnRecyclerItemClick onRecyclerItemClick;
    Calendar c;
    SimpleDateFormat sdf;
    Date monday;
    Date nextMonday;
    String currentdate;

    public ListSmsAdapter() {
        currentdate = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
        initialize();
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setOnRecyclerItemClick(OnRecyclerItemClick onRecyclerItemClick) {
        this.onRecyclerItemClick = onRecyclerItemClick;
    }

    public void setList(List<SmsModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reclayout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int rand = new Random().nextInt(8-1)+1;
        switch (rand){
            case 1:
                holder.img.setImageResource(R.drawable.chick);
                break;
            case 2:
                holder.img.setImageResource(R.drawable.leonardo);
                break;
            case 3:
                holder.img.setImageResource(R.drawable.mushrooms);
                break;
            case 4:
                holder.img.setImageResource(R.drawable.turtle);
                break;
            case 5:
                holder.img.setImageResource(R.drawable.dog);
                break;
            case 6:
                holder.img.setImageResource(R.drawable.cat);
                break;
            case 7:
                holder.img.setImageResource(R.drawable.deer);
                break;
        }
        holder.address.setText(list.get(position).getAddress());
        holder.body.setText(list.get(position).getBody());
        Log.d("check",currentdate+" "+list.get(position).getDate());
        if(currentdate.equals(list.get(position).getDate()))
        holder.datetime.setText(list.get(position).getTime());
        else if (inBetweenThisWeek(list.get(position).getDate())==true)
            holder.datetime.setText(list.get(position).getDay());
        else
            holder.datetime.setText(list.get(position).getDate());


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView body,address,datetime;
        ImageView img;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            address = itemView.findViewById(R.id.address);
            body = itemView.findViewById(R.id.msgshort);
            img = itemView.findViewById(R.id.imageView);
            datetime = itemView.findViewById(R.id.datetime);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onRecyclerItemClick.onItemClick(list.get(getAbsoluteAdapterPosition()).getAddress());
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
