package com.example.gomind.fragments;

import static com.example.gomind.Utils.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.example.gomind.sound.SoundManager;
import com.google.android.material.button.MaterialButton;
import com.example.gomind.activities.AuthenticationActivity;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment implements View.OnClickListener {


    private EditText edtNick;
    private EditText edtEmail;
    private EditText edtPears;
    private TextView txtPoints;
    private ScheduledExecutorService scheduler;
    private final QuestionAPI questionAPI = RetrofitClient.getInstance(getActivity()).getQuestionAPI();
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    MaterialButton buypearsBtn;
    MaterialButton balanceBtn;
    MaterialButton addImageBtn;
    MaterialButton auctionBtn;
    MaterialButton exitBtn;
    private SoundManager soundManager;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);

        // Инициализируем элементы
        ImageView iconKrug = view.findViewById(R.id.icon_krug);
        ImageView iconCross = view.findViewById(R.id.icon_cross);
        ImageView iconDone = view.findViewById(R.id.icon_done);
        edtEmail = view.findViewById(R.id.edt_email);

        // Начальное состояние
        resetIcons(iconKrug, iconCross, iconDone);

        // Слушатель на иконку "krug"
        iconKrug.setOnClickListener(v -> {
            iconKrug.setVisibility(View.GONE);
            iconCross.setVisibility(View.VISIBLE);
            iconDone.setVisibility(View.VISIBLE);
            edtEmail.setFocusableInTouchMode(true);
            edtEmail.setFocusable(true);
            edtEmail.requestFocus();
        });

        // Слушатель на иконку "cross"
        iconCross.setOnClickListener(v -> {
            resetIcons(iconKrug, iconCross, iconDone);
        });

        // Слушатель на иконку "done"
        iconDone.setOnClickListener(v -> {
            String emailText = edtEmail.getText().toString().trim();
            if (!emailText.isEmpty()) {
                EmailConfirmationFragment dialogFragment = new EmailConfirmationFragment();
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

                // Возвращаем иконки в исходное состояние после закрытия диалога
                fragmentManager.setFragmentResultListener("email_confirmed", this, (requestKey, result) -> {
                    resetIcons(iconKrug, iconCross, iconDone);
                });

                fragmentManager.beginTransaction()
                        .add(R.id.main_container, dialogFragment)
                        .addToBackStack(null)
                        .commit();
            } else {
                edtEmail.setError("Введите email!");
            }
        });

        // Инициализация SoundManager
        soundManager = SoundManager.getInstance(getContext());
        edtNick = view.findViewById(R.id.edt_bid);
        edtPears = view.findViewById(R.id.edt_pears);
        txtPoints = view.findViewById(R.id.txt_points);

        addImageBtn = view.findViewById(R.id.add_image_btn);
        balanceBtn = view.findViewById(R.id.money);
        buypearsBtn = view.findViewById(R.id.byu_fruits);
        exitBtn = view.findViewById(R.id.exit_btn);

        addImageBtn.setOnClickListener(this);
        balanceBtn.setOnClickListener(this);
        buypearsBtn.setOnClickListener(this);
        exitBtn.setOnClickListener(this);

        getProfile();
        getPoints();

        return view;
    }

    // Метод для сброса иконок в начальное состояние
    private void resetIcons(ImageView iconKrug, ImageView iconCross, ImageView iconDone) {
        iconKrug.setVisibility(View.VISIBLE);
        iconCross.setVisibility(View.GONE);
        iconDone.setVisibility(View.GONE);
        edtEmail.clearFocus();
        edtEmail.setFocusable(false);
        edtEmail.setFocusableInTouchMode(false);
    }



    //message
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        edtEmail.setOnFocusChangeListener((v, hasFocus) -> {
//            if (!hasFocus) {
//                // Показываем иконку, если поле потеряло фокус
//                showEmailConfirmationDialog();
//            }
//        });
    }

    private void showEmailConfirmationDialog() {
        EmailConfirmationFragment dialogFragment = new EmailConfirmationFragment();
        dialogFragment.setEmailChangeListener(new EmailConfirmationFragment.OnEmailChangeListener() {
            @Override
            public void onConfirmEmailChange() {
                // Сохраняем изменения E-mail
                saveEmailToApi();
            }

            @Override
            public void onCancelEmailChange() {
                // Восстанавливаем старый E-mail
                edtEmail.setText(user.getData().getEmail());
            }
        });

        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.main_container, dialogFragment)
                .addToBackStack(null)
                .commit();
    }

    private void saveEmailToApi() {
        String newEmail = edtEmail.getText().toString();
        Call<ApiResponse> call = RetrofitClient.getInstance(getActivity())
                .getUserAPI()
                .updateEmail("jwt-cookie=" + SharedPrefManager.getInstance(getActivity()).getToken().getAccessToken(), newEmail);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Обновление прошло успешно

                } else {
                    // Обработка ошибки
                    Log.e("Update Email", "Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("Update Email", "Failure: " + t.getMessage());
            }
        });
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
                                edtPears.setText("" + user.getData().getPears());
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
        soundManager.playSound();
        int id = v.getId();

        if (id == R.id.add_image_btn) {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            Fragment fragment = new UploadFragment();

            transaction.replace(R.id.main_container, fragment);
            transaction.addToBackStack(null);

            transaction.commit();


        } else if (id == R.id.money) {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            Fragment balanceFragment = new BalanceFragment();
            transaction.replace(R.id.main_container, balanceFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (id == R.id.byu_fruits) {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            Fragment buypearsFragment = new BuyPearsFragment();
            transaction.replace(R.id.main_container, buypearsFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (id == R.id.exit_btn) {
            logout();
        }


    }

    private void logout() {
        // Получаем SharedPreferences.Editor через SharedPrefManager
        SharedPreferences.Editor editor = SharedPrefManager.getInstance(getActivity()).getSharedPreferences().edit();

        // Удаляем данные, которые не касаются флага первого запуска
        editor.remove("token");  // Пример удаления только токена
        // Тут можешь очистить другие данные, которые нужно удалить
        editor.apply();

        // Завершаем активность
        getActivity().finishAffinity();
    }

    @Override
    public void onStop() {
        super.onStop();
        // Останавливаем планировщик, когда фрагмент больше не видим
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }
}
