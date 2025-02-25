package com.example.maciagamecenter.mazmorra;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.maciagamecenter.R;

public class PowerUpFragment extends Fragment {
    private PowerUpListener listener;

    public interface PowerUpListener {
        void onAttackSelected();
        void onHealSelected();
    }

    public void setPowerUpListener(PowerUpListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_power_up, container, false);

        Button attackButton = view.findViewById(R.id.attack_button);
        Button healButton = view.findViewById(R.id.heal_button);
        TextView titleText = view.findViewById(R.id.power_up_title);

        attackButton.setBackgroundResource(R.drawable.attack);
        healButton.setBackgroundResource(R.drawable.potion);

        attackButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAttackSelected();
            }
        });

        healButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onHealSelected();
            }
        });

        return view;
    }
}