package com.example.modernsms.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.modernsms.R;
import com.example.modernsms.interfaces.OnRecyclerItemClick;
import com.example.modernsms.models.SmsModel;

import java.util.List;
import java.util.Random;

public class ListSmsAdapter extends RecyclerView.Adapter<ListSmsAdapter.ViewHolder> {

    private List<SmsModel> list;
    private OnRecyclerItemClick onRecyclerItemClick;

    public ListSmsAdapter() {
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
        holder.datetime.setText(list.get(position).getDisplayDate());


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

}
