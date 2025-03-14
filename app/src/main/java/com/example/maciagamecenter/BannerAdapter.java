package com.example.maciagamecenter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;
import com.example.maciagamecenter.mazmorra.MazmorraActivity;
import com.example.maciagamecenter.Game2048Activity; // Añadir este import

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {
    private List<Banner> banners;

    public BannerAdapter(List<Banner> banners) {
        this.banners = banners;
    }

    @Override
    public BannerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_banner, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        Banner banner = banners.get(position);
        String title = banner.getTitle();
        int imageId = banner.getImageResId();
        
        holder.imageView.setImageResource(imageId);
        holder.title.setText(title);
        
        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            // Simplemente abrimos la actividad según la posición
            if (position == 0) {  // Primer banner
                Intent intent = new Intent(context, Game2048Activity.class);
                context.startActivity(intent);
            } else if (position == 1) {  // Segundo banner
                Intent intent = new Intent(context, MazmorraActivity.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return banners.size();
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title;

        BannerViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.bannerImage);
            title = itemView.findViewById(R.id.bannerTitle);
        }
    }
}