package com.example.gomind.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.gomind.ApiResponse;
import com.example.gomind.R;
import com.example.gomind.SharedPrefManager;
import com.example.gomind.api.QuestionAPI;
import com.example.gomind.api.RetrofitClient;
import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment implements View.OnClickListener{


    private EditText edtNick;
    private EditText edtEmail;
    private EditText edtPears;
    private TextView txtPoints;
    private ScheduledExecutorService scheduler;
    private final QuestionAPI questionAPI = RetrofitClient.getInstance(getActivity()).getQuestionAPI();
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    MaterialButton addImageBtn;
    MaterialButton auctionBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);

        edtNick = view.findViewById(R.id.edt_bid);
        edtEmail = view.findViewById(R.id.edt_email);
        edtPears = view.findViewById(R.id.edt_pears);
        txtPoints = view.findViewById(R.id.txt_points);

        addImageBtn = view.findViewById(R.id.add_image_btn);
        auctionBtn = view.findViewById(R.id.auction_btn);

        addImageBtn.setOnClickListener(this);
        auctionBtn.setOnClickListener(this);

        getProfile();

        getPoints();

        return view;
    }

    private void getPoints() {

        Call<String> call = questionAPI.getPoints("jwt-cookie=" + SharedPrefManager.getInstance(getActivity()).getToken().getAccessToken() +
                "; refresh-jwt-cookie=" + SharedPrefManager.getInstance(getActivity()).getToken().getRefreshToken());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                String points = response.body();
                mainThreadHandler.post(() -> txtPoints.setText(points));

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("Ошибка времени", " ");
                // mainThreadHandler.post(() -> remainingTimeTextView.setText("Ошибка: " + t.getMessage()));

            }
        });
    }

    private void getProfile() {
        Call<ApiResponse> call = RetrofitClient.getInstance(getActivity())
                .getUserAPI()
                .getProfile("jwt-cookie=" + SharedPrefManager.getInstance(getActivity()).getToken().getAccessToken() +
                        "; refresh-jwt-cookie=" + SharedPrefManager.getInstance(getActivity()).getToken().getRefreshToken());

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Выполняем запрос синхронно
                    Response<ApiResponse> response = call.execute();
                    Log.d("code", " " + response.code());

                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse user = response.body();
                        Log.e("Пользователь: ", user.toString());

                        // Сохраняем данные пользователя, например, обновляем UI
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                edtNick.setText(user.getData().getNickname()); // Предполагаем, что у объекта User есть метод getNickname
                                edtEmail.setText(user.getData().getEmail());
                                edtPears.setText(""+user.getData().getPears());
                          //      txtPoints.setText("" + user.getCount());
                                // Добавьте другие данные для отображения
                            }
                        });
                    } else {
                        Log.e("Ошибка API", "Код ответа: " + response.code() + ", сообщение: " + response.message());
                    }
                } catch (IOException e) {
                    Log.e("Ошибка сети", e.getMessage(), e);
                }
            }
        });

// Запускаем поток
        thread.start();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if(id == R.id.add_image_btn){
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            Fragment fragment = new UploadFragment();

            transaction.replace(R.id.main_container, fragment);

// Добавляем транзакцию в backstack, чтобы пользователь мог вернуться к предыдущему фрагменту
            transaction.addToBackStack(null);

// Применяем изменения
            transaction.commit();

        }
        else if(id == R.id.auction_btn){

        }
    }
}
