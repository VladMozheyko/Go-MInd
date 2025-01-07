package com.example.gomind.api;

import com.example.gomind.ApiResponse;
import com.example.gomind.User;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface UserAPI {

    @GET("user/profile")
    Call<ApiResponse> getProfile(@Header("Cookie") String cookie);


}

