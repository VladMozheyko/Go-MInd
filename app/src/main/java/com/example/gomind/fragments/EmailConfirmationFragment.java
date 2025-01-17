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
import com.google.android.material.button.MaterialButton;

public class EmailConfirmationFragment extends Fragment {

    private OnEmailChangeListener emailChangeListener;

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

        MaterialButton btnYes = view.findViewById(R.id.byu_fruits); // Да
        MaterialButton btnNo = view.findViewById(R.id.add); // Нет
        ImageView crossIcon = view.findViewById(R.id.cross_icon); // Крестик для закрытия

        btnYes.setOnClickListener(v -> {
            if (emailChangeListener != null) {
                emailChangeListener.onConfirmEmailChange();
            }
            closeFragment();
        });

        btnNo.setOnClickListener(v -> {
            if (emailChangeListener != null) {
                emailChangeListener.onCancelEmailChange();
            }
            closeFragment();
        });

        crossIcon.setOnClickListener(v -> closeFragment());

        return view;
    }

    private void closeFragment() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.popBackStack(); // Закрытие фрагмента
    }
}
