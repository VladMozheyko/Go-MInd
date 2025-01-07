package com.example.gomind.fragments;

import android.graphics.BlurMaskFilter;
import android.graphics.RenderEffect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.gomind.Question;
import com.example.gomind.QuizResponse;
import com.example.gomind.R;
import com.example.gomind.RemainingTimeResponse;
import com.example.gomind.SharedPrefManager;
import com.example.gomind.api.QuestionAPI;
import com.example.gomind.api.RetrofitClient;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizFragment extends Fragment implements View.OnClickListener {

    private TextView txtQuestion;
    private Button btn1;
    private Button btn2;
    private Button btn3;
    private Button btn4;
    private Button answerBtn;

    private ImageView imgAds;
    int answer = 0;
    boolean isAnswered;
    ArrayList<Button> buttons = new ArrayList<>();

    private TextView remainingTimeTextView;

    private TextView txtPoints;
    private ScheduledExecutorService scheduler;
    private final QuestionAPI questionAPI = RetrofitClient.getInstance(getActivity()).getQuestionAPI();
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());
    Question question;
    int id = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.quiz_fragment, container, false);

        remainingTimeTextView = view.findViewById(R.id.txt_time);
        txtPoints = view.findViewById(R.id.txt_points);
        imgAds = view.findViewById(R.id.img_add);

        txtQuestion = view.findViewById(R.id.txt_question);
        btn1 = view.findViewById(R.id.button1);
        btn2 = view.findViewById(R.id.button2);
        btn3 = view.findViewById(R.id.button3);
        btn4 = view.findViewById(R.id.button4);
        buttons.add(btn1);
        buttons.add(btn2);
        buttons.add(btn3);
        buttons.add(btn4);
        answerBtn = view.findViewById(R.id.answer_btn);
        btn1.setOnClickListener(this::onClick);
        btn2.setOnClickListener(this::onClick);
        btn3.setOnClickListener(this::onClick);
        btn4.setOnClickListener(this::onClick);
        answerBtn.setOnClickListener(this::onClick);

        Call<Integer> call = questionAPI.getAdvertisementsByCost("jwt-cookie=" + SharedPrefManager.getInstance(getActivity()).getToken().getAccessToken() +
                "; refresh-jwt-cookie=" + SharedPrefManager.getInstance(getActivity()).getToken().getRefreshToken());
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {

                if(response.body() != null) {
                    id = response.body();
                    Log.d("Ид ", " " + id);
                    Glide.with(requireActivity()).load("http://31.129.102.70:8081/user/file-system-image-by-id/" + id).into(imgAds);
                }
                else {
                    Glide.with(requireActivity()).load("http://31.129.102.70:8081/user/file-system-image-by-id/" + 4).into(imgAds);
                }

            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                t.printStackTrace();
                Log.d("Ошибка ", " " + id);
            }
        });

        getQuestion();
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
                Log.d("Баллы: ", points);
                mainThreadHandler.post(() -> txtPoints.setText(points));

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("Ошибка времени", " ");
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.button1){
            answer = 1;
            setAnsweredBackground(v);

        }
        else if(id == R.id.button2){
            answer = 2;
            setAnsweredBackground(v);

        }
        else if(id == R.id.button3){
            answer = 3;
            setAnsweredBackground(v);

        }
        else if(id == R.id.button4){
            answer = 4;
            setAnsweredBackground(v);

        }
        else if(id == R.id.answer_btn){
            resetBackground();
            sendAnswer();
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            getQuestion();
            getPoints();
        }

    }

    private void sendAnswer() {

        Call<Void> call = questionAPI.submitAnswer("jwt-cookie=" + SharedPrefManager.getInstance(getActivity()).getToken().getAccessToken() +
                "; refresh-jwt-cookie=" + SharedPrefManager.getInstance(getActivity()).getToken().getRefreshToken(), question.getId(), answer);

        // Выполнение запроса в фоновом потоке (например, в AsyncTask или отдельном потоке)
        new Thread(() -> {
            try {
                Response<Void> response = call.execute();
                if (response.isSuccessful()) {
                    System.out.println("Ответ успешно отправлен!");
                } else {
                    System.out.println("Ошибка: " + response.code());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
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

        // Запускаем задачу каждую секунду
        scheduler.scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS);
    }

    private void stopPeriodicRequests() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }

    public void getQuestion() {
        QuestionAPI quizApi = RetrofitClient.getInstance(getActivity()).getQuestionAPI();

        // Запрос к API
        Call<Question> call = quizApi.getRandomQuestion();
        call.enqueue(new Callback<Question>() {
            @Override
            public void onResponse(Call<Question> call, Response<Question> response) {

                if (response.isSuccessful() && response.body() != null) {
                    question = response.body();


                    // Используем данные вопроса
                    txtQuestion.setText(question.getText());
                    btn1.setText(question.getOptionA());
                    btn2.setText(question.getOptionB());
                    btn3.setText(question.getOptionC());
                    btn4.setText(question.getOptionD());

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



    private void resetBackground() {
        Drawable oldDraw = getResources().getDrawable(R.drawable.border_inside);
        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).setBackground(oldDraw);
        }
    }

    private void setAnsweredBackground(View view) {
        Drawable drawable = getResources().getDrawable(R.drawable.auth_button);
        Drawable oldDraw = getResources().getDrawable(R.drawable.border_inside);
        view.setBackground(drawable);
        for (int i = 0; i < buttons.size(); i++) {
            if(!buttons.get(i).equals(view)){
                buttons.get(i).setBackground(oldDraw);
            }
        }
    }
//    public void getAds(){
////    Call<ResponseBody> call = quizService.getAdvertisementMaxCostFile();
////        call.enqueue(new Callback<ResponseBody>() {
////        @Override
////        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
////            if (response.isSuccessful() && response.body() != null) {
////                // Сохраняем файл
////                boolean isSaved = saveToFile(response.body(), "advertisement_max_cost_file.txt");
////                if (isSaved) {
////                    System.out.println("Файл успешно сохранён!");
////                } else {
////                    System.out.println("Ошибка при сохранении файла.");
////                }
////            } else {
////                System.out.println("Ошибка: " + response.code());
////            }
////        }
////
////        @Override
////        public void onFailure(Call<ResponseBody> call, Throwable t) {
////            t.printStackTrace();
////        }
//  //  });

}
