package com.example.maciagamecenter.mazmorra;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.maciagamecenter.R;
import com.example.maciagamecenter.database.DatabaseHelper;

public class GameOverFragment extends Fragment {
    private int score;
    private int level;
    private GameOverListener listener;

    public interface GameOverListener {
        void onContinueClicked();
    }

    public void setGameOverListener(GameOverListener listener) {
        this.listener = listener;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_over, container, false);

        TextView scoreText = view.findViewById(R.id.score_text);
        TextView levelText = view.findViewById(R.id.level_text);
        Button continueButton = view.findViewById(R.id.continue_button);

        scoreText.setText("Final Score: " + score);
        levelText.setText("Level Reached: " + level);

        continueButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        return view;
    }
}