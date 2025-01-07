package com.example.gomind.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.gomind.R;
import com.example.gomind.api.RetrofitClient;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterFragment extends Fragment {


    EditText edtNickname;
    EditText edtEmail;
    EditText edtPassword;

    RequestBody bodyString;

    Button btnRegister;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.registration_fragment, container, false);

        edtNickname = view.findViewById(R.id.edt_nickname);
        edtEmail = view.findViewById(R.id.edt_email);
        edtPassword = view.findViewById(R.id.edt_password);
        btnRegister = view.findViewById(R.id.register_btn);







        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        return view;
    }

    private void register() {
        RequestBody bodyString;

        JSONObject params = new JSONObject();
        try {
            params.put("nickname", Objects.requireNonNull(edtNickname.getText()).toString());
            params.put("email", Objects.requireNonNull(edtEmail.getText()).toString());
            params.put("password", Objects.requireNonNull(edtPassword.getText()).toString());



            bodyString = RequestBody.create(MediaType.parse("application/json"),
                    params.toString());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        Call<ResponseBody> call = RetrofitClient.getInstance(getActivity()).getAuthApi()
                .register(bodyString);

        Log.d("Запрос: ", call.toString());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    Log.d("Ответ: "," " + response.code());
                    if (response.code() == 200 || response.code() == 201) {
                        assert response.body() != null;
                        JSONObject obj = new JSONObject(response.body().string());

                        // если пользователь ввел корректные данные
                        if (obj.getString("status").equals("success")) {

                            // пускаем подтвердить почту
                            Bundle bundle = new Bundle();
                            bundle.putString("param", "register");
                            bundle.putString("login", params.getString("email"));
                            bundle.putString("password", params.getString("password"));

                            Toast.makeText(getActivity(), "Мы отпраили ссылку на Вашу почту", Toast.LENGTH_LONG).show();
                            btnRegister.setEnabled(false);

                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Ответ", e.toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                btnRegister.setEnabled(true);
            }
        });

        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        Fragment fragment = new LoginFragment();

        transaction.replace(R.id.authentication_container, fragment);

// Добавляем транзакцию в backstack, чтобы пользователь мог вернуться к предыдущему фрагменту
        transaction.addToBackStack(null);

// Применяем изменения
        transaction.commit();

    }


}
