package com.example.gomind.activities;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.gomind.R;
import com.example.gomind.fragments.QuizFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    Fragment fragment;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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

        fragment = new QuizFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.main_container, fragment)
                .commit();


    }
}