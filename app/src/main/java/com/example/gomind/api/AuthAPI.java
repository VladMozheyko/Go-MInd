package com.example.gomind.api;

import java.io.File;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface AuthAPI {
    @POST("authentication/register")
    Call<ResponseBody> register(@Body RequestBody requestBody);

    @Headers("Content-Type: application/json")
    @POST("authentication/login")
    Call<ResponseBody> login(@Body String body);


}
