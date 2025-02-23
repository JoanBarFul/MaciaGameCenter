package com.example.maciagamecenter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ScoresAdapter extends RecyclerView.Adapter<ScoresAdapter.ViewHolder> {
    private List<ScoreItem> scores;

    public ScoresAdapter(List<ScoreItem> scores) {
        this.scores = scores;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_score, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ScoreItem score = scores.get(position);
        holder.playerNameText.setText(score.getPlayerName());
        holder.scoreText.setText(String.valueOf(score.getScore()));
    }

    @Override
    public int getItemCount() {
        return scores.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView playerNameText;
        TextView scoreText;

        ViewHolder(View view) {
            super(view);
            playerNameText = view.findViewById(R.id.playerNameText);
            scoreText = view.findViewById(R.id.scoreText);
        }
    }
}