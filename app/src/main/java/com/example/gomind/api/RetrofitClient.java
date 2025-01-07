package com.example.gomind.api;

import android.content.Context;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
public class RetrofitClient {

    private static final String BASE_URL =  "http://31.129.102.70:8081/";
    private static RetrofitClient mInstance;
    private Retrofit retrofit;

    public static CookieManager cookieManager;

    private RetrofitClient(Context context){
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        cookieManager = new CookieManager();

        //   cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .cookieJar(cookieManager)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized RetrofitClient getInstance(Context context){
        if(mInstance == null){
            mInstance = new RetrofitClient(context);
        }

        return mInstance;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    public AuthAPI getAuthApi(){
        return retrofit.create(AuthAPI.class);
    }

    public UserAPI getUserAPI(){
        return retrofit.create(UserAPI.class);
    }

    public QuestionAPI getQuestionAPI(){
        return retrofit.create(QuestionAPI.class);
    }



}
