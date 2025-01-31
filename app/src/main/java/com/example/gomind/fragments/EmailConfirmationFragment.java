package com.example.gomind.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.gomind.R;
import com.example.gomind.sound.SoundManager;
import com.google.android.material.button.MaterialButton;

public class EmailConfirmationFragment extends Fragment {

    private OnEmailChangeListener emailChangeListener;
    private SoundManager soundManager;

    public interface OnEmailChangeListener {
        void onConfirmEmailChange();
        void onCancelEmailChange();
    }

    public void setEmailChangeListener(OnEmailChangeListener listener) {
        this.emailChangeListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.messagebox4, container, false);

        // Инициализация SoundManager
        soundManager = SoundManager.getInstance(getContext());
        MaterialButton btnYes = view.findViewById(R.id.byu_fruits); // Да
        MaterialButton btnNo = view.findViewById(R.id.add); // Нет
        ImageView crossIcon = view.findViewById(R.id.cross_icon); // Крестик для закрытия

        btnYes.setOnClickListener(v -> {
            soundManager.playSound();
            if (emailChangeListener != null) {
                emailChangeListener.onConfirmEmailChange();
            }
            closeFragment();
        });

        btnNo.setOnClickListener(v -> {
            soundManager.playSound();
            if (emailChangeListener != null) {
                emailChangeListener.onCancelEmailChange();
            }
            closeFragment();
        });

        crossIcon.setOnClickListener(v -> {
            soundManager.playSound(); // Воспроизводим звук при нажатии
            closeFragment();
        });

        return view;
    }

    private void closeFragment() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.popBackStack(); // Закрытие фрагмента
    }
}
