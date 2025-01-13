package com.example.gomind.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gomind.ApiResponse;
import com.example.gomind.R;
import com.example.gomind.SharedPrefManager;
import com.example.gomind.UserData;
import com.example.gomind.api.RetrofitClient;
import com.example.gomind.api.UserAPI;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuypearsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.buypears_fragment, container, false);

        // Загружаем баланс пользователя
        fetchUserBalance(view);

        return view;
    }

    private void fetchUserBalance(View view) {
        UserAPI userAPI = RetrofitClient.getInstance(getActivity()).getUserAPI();

        Call<ApiResponse> call = userAPI.getProfile("jwt-cookie=" + SharedPrefManager.getInstance(getActivity()).getToken().getAccessToken() +
                "; refresh-jwt-cookie=" + SharedPrefManager.getInstance(getActivity()).getToken().getRefreshToken());

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();

                    UserData userData = apiResponse.getData();
                    if (userData != null) {
                        int balance = userData.getPears();

                        // Обновляем TextView
                        TextView txtPears = view.findViewById(R.id.txt_pears);
                        txtPears.setText("Баланс: " + balance);
                    } else {
                        Toast.makeText(getContext(), "Ошибка: данные пользователя отсутствуют", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Ошибка получения профиля", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getContext(), "Ошибка подключения к серверу", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
