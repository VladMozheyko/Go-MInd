package com.example.gomind.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.gomind.Question;
import com.example.gomind.R;
import com.example.gomind.SharedPrefManager;
import com.example.gomind.adapters.AnswersAdapter;
import com.example.gomind.api.QuestionAPI;
import com.example.gomind.api.RetrofitClient;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizFragment extends Fragment implements View.OnClickListener {

    private RecyclerView recyclerView;
    private AnswersAdapter answersAdapter;
    private List<String> answersList = new ArrayList<>();
    private Question question;
    private int selectedAnswerId = -1; // ID выбранного ответа
    private TextView txtQuestion;
    private Button answerBtn;
    private ImageView imgAds;
    private TextView remainingTimeTextView;
    private TextView txtPoints;
    private ScheduledExecutorService scheduler;
    private final QuestionAPI questionAPI = RetrofitClient.getInstance(getActivity()).getQuestionAPI();
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());
    private int id = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.quiz_fragment, container, false);

        txtQuestion = view.findViewById(R.id.txt_question);
        imgAds = view.findViewById(R.id.img_add);
        remainingTimeTextView = view.findViewById(R.id.txt_timeAuction);
        txtPoints = view.findViewById(R.id.txt_points);
        answerBtn = view.findViewById(R.id.answer_btn);
        recyclerView = view.findViewById(R.id.question_list);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity())); // 2 столбца для вариантов

        answerBtn.setOnClickListener(v -> submitAnswer()); // Обработчик нажатия кнопки отправки

        getPoints();
        getAdvertisements();
        getQuestion(); // Получаем первый вопрос

        return view;
    }

    private void getPoints() {
        Call<String> call = questionAPI.getPoints("jwt-cookie=" + SharedPrefManager.getInstance(getActivity()).getToken().getAccessToken() +
                "; refresh-jwt-cookie=" + SharedPrefManager.getInstance(getActivity()).getToken().getRefreshToken());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String points = response.body();
                Log.d("Баллы: ", points);
                mainThreadHandler.post(() -> txtPoints.setText(points));
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("Ошибка времени", " ");
            }
        });
    }

    private void getAdvertisements() {
        Call<Integer> call = questionAPI.getAdvertisementsByCost("jwt-cookie=" + SharedPrefManager.getInstance(getActivity()).getToken().getAccessToken() +
                "; refresh-jwt-cookie=" + SharedPrefManager.getInstance(getActivity()).getToken().getRefreshToken());
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.body() != null) {
                    id = response.body();
                    Log.d("Ид ", " " + id);
                    String imageUrl = "http://10.0.2.2:8081/user/file-system-image-by-id/" + id;

                    // Загрузка изображения с закругленными углами
                    Glide.with(requireActivity())
                            .load(imageUrl)
                            .apply(RequestOptions.bitmapTransform(new RoundedCorners(26))) // Установите радиус закругления
                            .into(imgAds);
                } else {
                    String imageUrl = "http://10.0.2.2:8081/user/file-system-image-by-id/" + 4;

                    // Загрузка изображения с закругленными углами
                    Glide.with(requireActivity())
                            .load(imageUrl)
                            .apply(RequestOptions.bitmapTransform(new RoundedCorners(26))) // Установите радиус закругления
                            .into(imgAds);
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                t.printStackTrace();
                Log.d("Ошибка ", " " + id);
            }
        });
    }

    private void getQuestion() {
        Call<Question> call = questionAPI.getRandomQuestion();
        call.enqueue(new Callback<Question>() {
            @Override
            public void onResponse(Call<Question> call, Response<Question> response) {
                if (response.isSuccessful() && response.body() != null) {
                    question = response.body();
                    txtQuestion.setText(question.getText());

                    answersList.clear();
                    answersList.add(question.getOptionA());
                    answersList.add(question.getOptionB());
                    answersList.add(question.getOptionC());
                    answersList.add(question.getOptionD());

                    AnswersAdapter.AnswerClickListener clickListener = new AnswersAdapter.AnswerClickListener() {
                        @Override
                        public void onClickListener(int answerId) {
                            selectedAnswerId = answerId;
                        }
                    };
                    answersAdapter = new AnswersAdapter(answersList, clickListener);
                    recyclerView.setAdapter(answersAdapter);
                } else {
                    Log.e("Quiz", "Ошибка получения данных: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Question> call, Throwable t) {
                Log.e("Quiz", "Ошибка: " + t.getMessage());
            }
        });
    }

    private void submitAnswer() {
        if (selectedAnswerId == -1) {
            return; // Если ответ не выбран, ничего не делаем
        }

        // Отправляем выбранный ответ
        Call<Void> call = questionAPI.submitAnswer("jwt-cookie=" + SharedPrefManager.getInstance(getActivity()).getToken().getAccessToken() +
                "; refresh-jwt-cookie=" + SharedPrefManager.getInstance(getActivity()).getToken().getRefreshToken(), question.getId(), selectedAnswerId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("Quiz", "Ответ успешно отправлен!");
                    getPoints(); // Обновляем баллы после отправки ответа
                    getQuestion(); // Получаем новый вопрос
                } else {
                    Log.e("Quiz", "Ошибка при отправке ответа");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("Quiz", "Ошибка: " + t.getMessage());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        startPeriodicRequests();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopPeriodicRequests();
    }

    private void startPeriodicRequests() {
        scheduler = Executors.newScheduledThreadPool(1);
        Runnable task = () -> {
            Call<String> call = questionAPI.getRemainingTime();
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    String remainingTime = response.body();
                    mainThreadHandler.post(() -> remainingTimeTextView.setText(remainingTime));
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.d("Ошибка времени", " ");
                    mainThreadHandler.post(() -> remainingTimeTextView.setText("Ошибка: " + t.getMessage()));
                }
            });
        };
        scheduler.scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS);
    }

    private void stopPeriodicRequests() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }

    @Override
    public void onClick(View v) {

    }
}
