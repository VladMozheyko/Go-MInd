package com.example.gomind.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gomind.R;
import com.example.gomind.SharedPrefManager;
import com.example.gomind.Token;
import com.example.gomind.activities.MainActivity;
import com.example.gomind.api.RetrofitClient;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.Cookie;
import okhttp3.HttpUrl;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {


    EditText edtPassword;
    EditText edtEmail;
    MaterialButton enterBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment, container, false);

        edtPassword = view.findViewById(R.id.edt_password);
        edtEmail = view.findViewById(R.id.edt_email);
        enterBtn = view.findViewById(R.id.enter_btn);

        enterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });



        return view;
    }

    private void login() {
        try {
            JSONObject paramObject = new JSONObject();
            paramObject.put("password", Objects.requireNonNull(edtPassword.getText()).toString().trim());
            paramObject.put("email", Objects.requireNonNull(edtEmail.getText()).toString().trim());

            Call<ResponseBody> call = RetrofitClient.getInstance(getActivity()).getAuthApi()
                    .login(paramObject.toString());

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                    try {
                        Log.d("Ответ: ", " " +  response.code());
                        if (response.code() == 200) {
                            // если пользователь ввел корректные данные


                            // пускаем в mainActivity
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            Log.d("Куки: ", RetrofitClient.cookieManager.getCookieStore().toString());
                            // Получаем Map<HttpUrl, List<Cookie>> из cookieManager
                            Map<HttpUrl, List<Cookie>> cookieStore = RetrofitClient.cookieManager.getCookieStore();

                            // Указываем URL для которого ищем cookies
                            HttpUrl url = HttpUrl.parse("http://31.129.102.70:8081/authentication/login");

                            if (cookieStore.containsKey(url)) {
                                List<Cookie> cookies = cookieStore.get(url);

                                String jwtCookie = null;
                                String refreshJwtCookie = null;

                                // Перебираем cookies
                                for (Cookie cookie : cookies) {
                                    if ("jwt-cookie".equals(cookie.name())) {
                                        jwtCookie = cookie.value();
                                    } else if ("refresh-jwt-cookie".equals(cookie.name())) {
                                        refreshJwtCookie = cookie.value();
                                    }
                                }

                                // Логируем результаты
                                SharedPrefManager.getInstance(getActivity()).saveToken(
                                        new Token(
                                                jwtCookie,
                                                refreshJwtCookie
                                        ));
                            } else {
                                Log.e("Cookies", "No cookies found for URL: " + url.toString());
                            }
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {}
            });

        } catch (
                JSONException e) {
            e.printStackTrace();
        }
    }
}