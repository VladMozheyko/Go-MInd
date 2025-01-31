package com.example.gomind.activities;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gomind.R;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ImageView mainImageView = findViewById(R.id.imageView);
        ImageView ricochetImageView = findViewById(R.id.ricochetImageView);
        ImageView imgLeft = findViewById(R.id.img_left);
        ImageView imgRight = findViewById(R.id.img_right);

        // Устанавливаем начальное состояние для основной картинки
        mainImageView.setScaleX(10f);
        mainImageView.setScaleY(10f);
        mainImageView.setTranslationY(-1000f);

        // Устанавливаем начальное состояние для рикошетной картинки (невидима)
        ricochetImageView.setScaleX(10f);
        ricochetImageView.setScaleY(10f);
        ricochetImageView.setTranslationY(-1000f);

        // Анимация падения основной картинки
        PropertyValuesHolder scaleXMain = PropertyValuesHolder.ofFloat("scaleX", 1f);
        PropertyValuesHolder scaleYMain = PropertyValuesHolder.ofFloat("scaleY", 1f);
        PropertyValuesHolder translationYMain = PropertyValuesHolder.ofFloat("translationY", 0f);
//
        ObjectAnimator mainAnimator = ObjectAnimator.ofPropertyValuesHolder(mainImageView, scaleXMain, scaleYMain, translationYMain);
        mainAnimator.setDuration(900); // Длительность анимации в миллисекундах

        // Анимация для рикошетной картинки (сначала она будет копировать основную картинку)
        ObjectAnimator ricochetAnimator = ObjectAnimator.ofPropertyValuesHolder(ricochetImageView, scaleXMain, scaleYMain, translationYMain);
        ricochetAnimator.setDuration(900); // Синхронно с основной анимацией

        // Создаем AnimatorSet для синхронного выполнения анимаций
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(mainAnimator, ricochetAnimator);

        // Добавляем слушатель завершения анимации основной картинки
        mainAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {

                // Плавное появление imgLeft и imgRight
                fadeInView(imgLeft);
                fadeInView(imgRight);

                // Запуск анимации рикошета
                animateRicochet(ricochetImageView);

                // Запускаем AuthActivity с задержкой, чтобы дождаться завершения рикошета
                ricochetImageView.postDelayed(() -> {
                    //TODO проверить SharedPrefManager заходили ли мы в приложение, если да, то запускаем AuthenticationActivity, если нет, то запускаем GreetingsActivity(после принятия условий записываем в )
                    //TODO SharedPrefManager записываем, что приложение уже запускалось и переходим в майн активит
                    //if()
                    Intent intent = new Intent(SplashScreen.this, AuthenticationActivity.class);
                    startActivity(intent);
                    finish(); // Завершение текущей активности, чтобы пользователь не вернулся к SplashScreen
                }, 1500); // Задержка в миллисекундах (1 секунда)
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

        // Запускаем анимацию синхронно
        animatorSet.start();
    }

    private void animateRicochet(ImageView ricochetImageView) {
        // Увеличиваем картинку до 7x, затем уменьшаем до нормального размера
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 0.6f, 1.2f, 1f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 0.6f, 1.2f, 1f);

        ObjectAnimator ricochetAnimator = ObjectAnimator.ofPropertyValuesHolder(ricochetImageView, scaleX, scaleY);
        ricochetAnimator.setDuration(900); // Длительность рикошета в миллисекундах
        ricochetAnimator.start();
    }

    private void fadeInView(View view) {
        view.setVisibility(View.VISIBLE); // Сначала делаем элемент видимым
        ObjectAnimator fadeInAnimator = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f); // Анимация прозрачности
        fadeInAnimator.setDuration(1500); // Длительность появления
        fadeInAnimator.start();
    }

}