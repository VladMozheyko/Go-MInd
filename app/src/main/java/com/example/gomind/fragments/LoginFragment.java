package com.example.gomind.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gomind.R;
import com.example.gomind.SharedPrefManager;
import com.example.gomind.Token;
import com.example.gomind.activities.MainActivity;
import com.example.gomind.api.RetrofitClient;
import com.example.gomind.validations.EmailValidator;
import com.example.gomind.validations.PasswordValidator;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import okhttp3.Cookie;
import okhttp3.HttpUrl;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
//пп
public class LoginFragment extends Fragment {

    EditText edtPassword;
    EditText edtEmail;
    MaterialButton enterBtn;
    ImageView passwordToggleEye;  // Закрытый глаз
    ImageView passwordToggleEyeOpen;  // Открытый глаз
    TextView emailErrorText, passwordErrorText;  // Текстовые ошибки

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment, container, false);

        edtPassword = view.findViewById(R.id.edt_password);
        edtEmail = view.findViewById(R.id.edt_email);
        enterBtn = view.findViewById(R.id.enter_btn);
        passwordToggleEye = view.findViewById(R.id.password_toggle_eye);
        passwordToggleEyeOpen = view.findViewById(R.id.password_toggle_eye_open);
        emailErrorText = view.findViewById(R.id.email_error);  // Инициализация ошибки email
        passwordErrorText = view.findViewById(R.id.password_error);  // Инициализация ошибки пароля

        enterBtn.setOnClickListener(v -> login());

        // Устанавливаем скрытый тип ввода пароля по умолчанию
        edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        // Обработчик клика по иконке закрытого глаза
        passwordToggleEye.setOnClickListener(v -> togglePasswordVisibility(true));

        // Обработчик клика по иконке открытого глаза
        passwordToggleEyeOpen.setOnClickListener(v -> togglePasswordVisibility(false));

        return view;
    }

    private void togglePasswordVisibility(boolean isPasswordHidden) {
        if (isPasswordHidden) {
            // Показываем открытый глаз и скрываем закрытый
            passwordToggleEye.setVisibility(View.GONE);
            passwordToggleEyeOpen.setVisibility(View.VISIBLE);

            // Делаем пароль видимым
            edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            // Показываем закрытый глаз и скрываем открытый
            passwordToggleEye.setVisibility(View.VISIBLE);
            passwordToggleEyeOpen.setVisibility(View.GONE);

            // Скрываем пароль
            edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }

        // Чтобы курсор остался в конце текста после изменения типа ввода
        edtPassword.setSelection(edtPassword.length());
    }

    private void login() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        // Скрыть ошибки при новом вводе
        emailErrorText.setVisibility(View.GONE);
        passwordErrorText.setVisibility(View.GONE);

        // Валидация email
        if (email.isEmpty()) {
            emailErrorText.setText("Email обязателен для заполнения");
            emailErrorText.setVisibility(View.VISIBLE);
            edtEmail.requestFocus();
            return;
        } else if (!EmailValidator.isValidEmail(email)) {
            emailErrorText.setText("Введите корректный email");
            emailErrorText.setVisibility(View.VISIBLE);
            edtEmail.requestFocus();
            return;
        }

        // Валидация пароля
        if (password.isEmpty()) {
            passwordErrorText.setText("Пароль обязателен для заполнения");
            passwordErrorText.setVisibility(View.VISIBLE);
            edtPassword.requestFocus();
            return;
        } else if (!PasswordValidator.isValidPassword(password)) {
            passwordErrorText.setText("Пароль должен содержать от 6 до 20 символов");
            passwordErrorText.setVisibility(View.VISIBLE);
            edtPassword.requestFocus();
            return;
        }

        try {
            JSONObject paramObject = new JSONObject();
            paramObject.put("password", password);
            paramObject.put("email", email);

            Call<ResponseBody> call = RetrofitClient.getInstance(getActivity()).getAuthApi()
                    .login(paramObject.toString());

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    try {
                        if (response.code() == 200) {
                            // Если логин успешен, сохраняем токены
                            Map<HttpUrl, List<Cookie>> cookieStore = RetrofitClient.cookieManager.getCookieStore();
                            HttpUrl url = HttpUrl.parse("http://31.129.102.70:8081/authentication/login");

                            if (cookieStore.containsKey(url)) {
                                List<Cookie> cookies = cookieStore.get(url);
                                String jwtCookie = null, refreshJwtCookie = null;

                                for (Cookie cookie : cookies) {
                                    if ("jwt-cookie".equals(cookie.name())) {
                                        jwtCookie = cookie.value();
                                    } else if ("refresh-jwt-cookie".equals(cookie.name())) {
                                        refreshJwtCookie = cookie.value();
                                    }
                                }

                                // Сохраняем токены
                                SharedPrefManager.getInstance(getActivity()).saveToken(new Token(jwtCookie, refreshJwtCookie));
                            }

                            // После успешного входа переходим в MainActivity
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getActivity(), "Неверный email или пароль", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    // Обработка ошибки
                    Toast.makeText(getActivity(), "Ошибка подключения", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
