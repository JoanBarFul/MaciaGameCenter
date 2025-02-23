package com.example.maciagamecenter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

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
    public void onBindViewHolder(BannerViewHolder holder, int position) {
        Banner banner = banners.get(position);
        holder.title.setText(banner.getTitle());
        Glide.with(holder.itemView.getContext())
                .load(banner.getImageResource())
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return banners.size();
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;

        BannerViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.bannerImage);
            title = itemView.findViewById(R.id.bannerTitle);
        }
    }
}