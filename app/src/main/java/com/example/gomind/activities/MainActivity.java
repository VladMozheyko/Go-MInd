package com.example.gomind.activities;

import static ru.yoomoney.sdk.kassa.payments.Checkout.createTokenizationResult;
import static ru.yoomoney.sdk.kassa.payments.Checkout.createTokenizeIntent;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.gomind.ApiResponse;
import com.example.gomind.R;
import com.example.gomind.SharedPrefManager;
import com.example.gomind.Utils;
import com.example.gomind.api.RetrofitClient;
import com.example.gomind.fragments.LeadersFragment;
import com.example.gomind.fragments.ProfileFragment;
import com.example.gomind.fragments.QuizFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Currency;
import java.util.HashSet;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Response;
import ru.yoomoney.sdk.kassa.payments.TokenizationResult;
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.Amount;
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.PaymentMethodType;
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.PaymentParameters;
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.SavePaymentMethod;


public class MainActivity extends AppCompatActivity {

    Fragment fragment;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startTokenize();

        getProfile();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setItemIconTintList(null);  // Отключаем цвета из темы
        Menu menu = bottomNavigationView.getMenu();
        // Получаем цвета из ресурсов, затем парсим их в Color и назначаем тексту, которой затем
        // назначим каждому элементу меню отдельно
        Resources resources = getResources();

        String[] colors = resources.getStringArray(R.array.colors);

       // String[] titles = resources.getStringArray(R.array.item_values);

        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
          //  SpannableString s = new SpannableString(titles[i]);

         //   s.setSpan(new ForegroundColorSpan(Color.parseColor(colors[i])), 0, s.length(), 0);
          //  item.setTitle(s);
        }

        Objects.requireNonNull(bottomNavigationView.getMenu().getItem(0).getIcon()).setTint(R.drawable.home);

        // Установка слушателя
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Определяем действия в зависимости от выбранного элемента
               int id = item.getItemId();
               if(id == R.id.nav_home){
                   fragment = new LeadersFragment();
                   fragmentManager = getSupportFragmentManager();
                   fragmentManager.beginTransaction()
                           .replace(R.id.main_container, fragment)
                           .commit();
               }
                else if(id == R.id.nav_quiz){
                    fragment = new QuizFragment();
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.main_container, fragment)
                            .commit();
                }
                else if (id == R.id.nav_profile){
                    fragment = new ProfileFragment();
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.main_container, fragment)
                            .commit();
                }
                return true;
            }
        });

        fragment = new LeadersFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.main_container, fragment)
                .commit();
    }

    public void startTokenize() {

        PaymentParameters paymentParameters = new PaymentParameters(
                new Amount(BigDecimal.TEN, Currency.getInstance("RUB")),
                "Груша",
                "Внутренняя валюта",
                "live_NDk2NjAwpCrWyn1RSQ4Q4ZMwglUB0C_DaFQQ3yypP7Y",
                "496600",
                SavePaymentMethod.OFF,
                 new HashSet<>(Arrays.asList(
                        PaymentMethodType.BANK_CARD
                )));



        Intent intent = createTokenizeIntent(this, paymentParameters);
        startActivityForResult(intent, 345);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 345) {
            switch (resultCode) {
                case RESULT_OK:
                    // Successful tokenization
                    if (data != null) {
                        TokenizationResult result = createTokenizationResult(data);
                        Log.d("токен", result.toString());

                    }

                    break;
                case RESULT_CANCELED:
                   Log.d("Ошибка токена", "");
                    break;
            }
        }
    }

    private void getProfile() {
        Call<ApiResponse> call = RetrofitClient.getInstance(this)
                .getUserAPI()
                .getProfile("jwt-cookie=" + SharedPrefManager.getInstance(this).getToken().getAccessToken() +
                        "; refresh-jwt-cookie=" + SharedPrefManager.getInstance(this).getToken().getRefreshToken());

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Выполняем запрос синхронно
                    Response<ApiResponse> response = call.execute();
                    Log.d("code", " " + response.code());

                    if (response.isSuccessful() && response.body() != null) {
                        Utils.user = response.body();


                        // Сохраняем данные пользователя, например, обновляем UI

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
}