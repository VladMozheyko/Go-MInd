package com.example.gomind.api;

import com.example.gomind.Leader;
import com.example.gomind.Question;
import com.example.gomind.QuizResponse;
import com.example.gomind.RemainingTimeResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface QuestionAPI {
    @GET("quiz/random-questions")
    Call<Question> getRandomQuestion();


    @GET("quiz/remaining-time")
    Call<String> getRemainingTime();

    @GET("/quiz/current-user/points")
    Call<String> getPoints(@Header("Cookie") String cookie);

    @POST("quiz/submit-answer")
    Call<Void> submitAnswer(
            @Header("Cookie") String cookie,
            @Query("questionId") long questionId,
            @Query("userAnswer") int userAnswer
    );

    @GET("quiz/users-with-points")
    Call<List<Leader>> getUsersWithPoints();

    @GET("/quiz/advertisement-max-cost-file")
    Call<Integer> getAdvertisementsByCost(  @Header("Cookie") String cookie);

    @Multipart
    @POST("quiz/add-advertisements")
    Call<String> addAdvertisementWithFile(
            @Header("Cookie") String cookie,
            @Part("title") RequestBody title,
            @Part("description") RequestBody description,
            @Part("cost") RequestBody cost,
            @Part MultipartBody.Part file
    );

    @GET("quiz/advertisement-max-cost-file")
    Call<Integer> getAdvertisementMaxCostFile();
}
