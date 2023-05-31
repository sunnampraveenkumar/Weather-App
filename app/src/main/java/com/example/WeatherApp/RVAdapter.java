package com.example.WeatherApp;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.icu.text.Transliterator;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.constraintlayout.helper.widget.Layer;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ViewHloder> {
    Context context;
    ArrayList<RVModal> RVModalArrayList;

    public RVAdapter(Context context, ArrayList<RVModal> RVModalArrayList) {
        this.context = context;
        this.RVModalArrayList = RVModalArrayList;
    }

    @NonNull
    @Override
    public RVAdapter.ViewHloder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.forecast_rv_item,parent,false);
        return  new ViewHloder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHloder holder, int position) {
        RVModal modal = RVModalArrayList.get(position);
        holder.tempTv.setText(modal.getTemp()+"°C");
        holder.wsTv.setText(modal.getWindspeed()+"Km/h");
        Picasso.get().load("http".concat(modal.getIcon())).into(holder.condIv);
        SimpleDateFormat input = null,output = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            input = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            output = new SimpleDateFormat("hh:mm aa");
        }
        try{
            Date t = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                t = input.parse(modal.getTime());
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.timeTv.setText(output.format(t));
            }
        }
        catch (ParseException e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return RVModalArrayList.size();
    }

    public class  ViewHloder extends RecyclerView.ViewHolder {
        private TextView wsTv,tempTv,timeTv;
        private ImageView condIv;
        public ViewHloder(@NonNull View itemView) {
            super(itemView);
            wsTv = itemView.findViewById(R.id.Tvwindspeedid);
            tempTv = itemView.findViewById(R.id.Tvtempid);
            timeTv = itemView.findViewById(R.id.Tvtimeid);
            condIv = itemView.findViewById(R.id.IVconditionid);

        }
    }
}
