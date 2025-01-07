package com.example.gomind.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gomind.Auction;
import com.example.gomind.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class AuctionAdapter extends RecyclerView.Adapter<AuctionAdapter.ViewHolder> {
    private List<Auction> auctions;
    private Context context;

    // Конструктор для адаптера
    public AuctionAdapter(Context context, List<Auction> auctions) {
        this.context = context;
        this.auctions = auctions;
    }

    // Привязка разметки auction_item
    @NonNull
    @Override
    public AuctionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.auction_item, parent, false);
        return new ViewHolder(view);
    }

    // Связывание данных с элементами разметки
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Auction auction = auctions.get(position);

        // Установка данных в UI
        holder.nameBtn.setText(auction.getNickname());
        holder.btn2.setText(String.valueOf(auction.getPosition()));
        holder.txtPeaches.setText(String.valueOf(auction.getCost()));

        // Загрузка изображения с помощью Glide
        String imageUrl = "http://31.129.102.70:8081/user/file-system-image-by-id/" + auction.getFileDataId();
        Glide.with(context)
                .load(imageUrl)
                .into(holder.imgAds);

        // Применение стилей в зависимости от позиции
        if (position == 0) {
            // Для первого элемента
            holder.imgClock1.setColorFilter(context.getResources().getColor(android.R.color.white));
            holder.txtTime.setTextColor(context.getResources().getColor(android.R.color.white));
            holder.line.setBackgroundColor(context.getResources().getColor(android.R.color.white));
            holder.line3.setBackgroundColor(context.getResources().getColor(android.R.color.white));
            holder.img5.setColorFilter(context.getResources().getColor(android.R.color.white));
            holder.txtPeaches.setTextColor(context.getResources().getColor(android.R.color.white));
            holder.nameBtn.setTextColor(context.getResources().getColor(R.color.soft_orange)); // #DC6A33
            holder.btn2.setBackgroundResource(R.drawable.click2);
        } else {
            // Для остальных элементов
            holder.imgClock1.setColorFilter(context.getResources().getColor(android.R.color.black));
            holder.txtTime.setTextColor(context.getResources().getColor(android.R.color.black));
            holder.line.setBackgroundColor(context.getResources().getColor(android.R.color.black));
            holder.line3.setBackgroundColor(context.getResources().getColor(android.R.color.black));
            holder.img5.setColorFilter(context.getResources().getColor(android.R.color.black));
            holder.txtPeaches.setTextColor(context.getResources().getColor(android.R.color.black));
            holder.nameBtn.setTextColor(context.getResources().getColor(android.R.color.black));
            holder.btn2.setBackgroundResource(R.drawable.noclick2);
        }
    }

    // Возвращаем количество элементов
    @Override
    public int getItemCount() {
        return auctions.size();
    }

    // ViewHolder связывает элементы разметки
    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialButton nameBtn, btn2;
        TextView txtPeaches, txtTime;
        ImageView imgAds, imgClock1, img5;
        View line, line3;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Привязка элементов разметки
            nameBtn = itemView.findViewById(R.id.name_btn);
            btn2 = itemView.findViewById(R.id.btn2);
            txtPeaches = itemView.findViewById(R.id.txt_peaches);
            imgAds = itemView.findViewById(R.id.img_add);
            imgClock1 = itemView.findViewById(R.id.img_clock1);
            txtTime = itemView.findViewById(R.id.txt_time);
            line = itemView.findViewById(R.id.line);
            line3 = itemView.findViewById(R.id.line3);
            img5 = itemView.findViewById(R.id.img5);
        }
    }
}
