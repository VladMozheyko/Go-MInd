package com.example.gomind.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gomind.R;
import com.google.android.material.button.MaterialButton;

public class ChooseModeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_mode_fragment, container, false);

        MaterialButton shortAnswersButton = view.findViewById(R.id.short_btn);
        MaterialButton longAnswersButton = view.findViewById(R.id.long_btn);

        // Установка обработчиков кнопок
        shortAnswersButton.setOnClickListener(v -> {
            updateButtonStyle(shortAnswersButton, longAnswersButton);
            openQuizFragment();
        });

        longAnswersButton.setOnClickListener(v -> {
            updateButtonStyle(longAnswersButton, shortAnswersButton);
            openQuizFragment();
        });

        return view;
    }
    /**
     * Обновляет стиль кнопок: активной кнопке задается стиль auth_button,
     * а неактивной - reg_button.
     */
    private void updateButtonStyle(MaterialButton activeButton, MaterialButton inactiveButton) {
        activeButton.setBackgroundResource(R.drawable.auth_button);
        activeButton.setTextColor(getResources().getColor(R.color.white));

        inactiveButton.setBackgroundResource(R.drawable.reg_button);
        inactiveButton.setTextColor(getResources().getColor(R.color.black));
    }
    /**
     * Открывает QuizFragment.
     */
    private void openQuizFragment() {
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, new QuizFragment())
                .addToBackStack(null)
                .commit();
    }
}
