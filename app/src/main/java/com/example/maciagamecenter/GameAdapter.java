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
import com.example.maciagamecenter.mazmorra.MazmorraActivity;
import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameViewHolder> {
    private List<Game> games;
    private Context context;

    public GameAdapter(Context context, List<Game> games) {
        this.context = context;
        this.games = games;
    }

    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_game, parent, false);
        return new GameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
        Game game = games.get(position);
        holder.gameTitle.setText(game.getTitle());
        holder.gameImage.setImageResource(game.getImageResource());

        holder.itemView.setOnClickListener(v -> {
            if (game.getTitle().equals("Dungeon")) {
                Intent intent = new Intent(context, MazmorraActivity.class);
                context.startActivity(intent);
            } else {
                // Mantener la lógica existente para el 2048
                Intent intent = new Intent(context, Game2048Activity.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    static class GameViewHolder extends RecyclerView.ViewHolder {
        ImageView gameImage;
        TextView gameTitle;

        GameViewHolder(View itemView) {
            super(itemView);
            gameImage = itemView.findViewById(R.id.gameImage);
            gameTitle = itemView.findViewById(R.id.gameTitle);
        }
    }
}