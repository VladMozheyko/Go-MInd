package com.example.gomind.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.CheckBox;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gomind.R;
import com.example.gomind.SharedPrefManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class GreetingsActivity extends AppCompatActivity {
    private PdfRenderer pdfRenderer;
    private ParcelFileDescriptor fileDescriptor;
    private LinearLayout pdfContainer;
    private HorizontalScrollView horizontalScrollView;
    private ScrollView pdfScrollView;
    private CheckBox confirmCheckBox;
    private ScaleGestureDetector scaleGestureDetector;

    private float scaleFactor = 1.7f; // Исходный масштаб
    private final float MIN_SCALE = 1.7f; // Минимальный масштаб
    private final float MAX_SCALE = 4.0f; // Максимальный масштаб

    private boolean isScaling = false; // Флаг для отслеживания состояния масштабирования
    private boolean isInitialScale = true; // Флаг для определения начального масштаба

    private List<Rect> hitboxes = new ArrayList<>(); // Список хитбоксов для каждой страницы

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greetings);
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            // Если пользователь авторизован, переходим в MainActivity
            Intent intent = new Intent(this, AuthenticationActivity.class);
            startActivity(intent);
            finish(); // Закрываем текущую активность
            return; // Завершаем метод
        }

        pdfContainer = findViewById(R.id.pdfContainer);
        pdfScrollView = findViewById(R.id.verticalScrollView);
        horizontalScrollView = findViewById(R.id.horizontalScrollView);
        confirmCheckBox = findViewById(R.id.confirmCheckBox);

        try {
            openRenderer();
            displayVisiblePages();

            findViewById(R.id.confirmButton).setOnClickListener(v -> {
                if (confirmCheckBox.isChecked()) {
                    SharedPrefManager.getInstance(this).saveAgreementAccepted();
                    navigateToLoginFragment();
                } else {
                    Toast.makeText(this, "Необходимо поставить галочку для подтверждения", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка открытия PDF", Toast.LENGTH_SHORT).show();
        }

        // Инициализация ScaleGestureDetector для обработки масштабирования
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        // Установка обработчика касаний
        pdfScrollView.setOnTouchListener(this::onTouch);
        horizontalScrollView.setOnTouchListener(this::onTouch);
    }

    public boolean onTouch(View v, MotionEvent event) {
        // Обработка жестов масштабирования
        scaleGestureDetector.onTouchEvent(event);

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                isScaling = false;
                checkHitbox(event.getX(), event.getY());
                break;

            case MotionEvent.ACTION_MOVE:
                if (!isScaling) {
                    checkHitbox(event.getX(), event.getY());
                }
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                isScaling = true;
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                isScaling = false;
                break;

            case MotionEvent.ACTION_CANCEL:
                isScaling = false;
                break;
        }

        return isScaling;
    }

    private void openRenderer() throws Exception {
        File file = new File(getCacheDir(), "agreement.pdf");
        if (!file.exists()) {
            try (InputStream asset = getAssets().open("agreement.pdf");
                 FileOutputStream output = new FileOutputStream(file)) {
                byte[] buffer = new byte[1024];
                int size;
                while ((size = asset.read(buffer)) != -1) {
                    output.write(buffer, 0, size);
                }
            }
        }
        fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
        pdfRenderer = new PdfRenderer(fileDescriptor);
    }

    private void displayVisiblePages() {
        if (pdfRenderer == null) return;

        pdfContainer.removeAllViews();
        hitboxes.clear(); // Очищаем хитбоксы перед новым рендерингом

        for (int i = 0; i < pdfRenderer.getPageCount(); i++) {
            PdfRenderer.Page page = pdfRenderer.openPage(i);

            ImageView imageView = new ImageView(this);

            int originalWidth = page.getWidth();
            int originalHeight = page.getHeight();
            int scaledWidth = (int) (originalWidth * scaleFactor);
            int scaledHeight = (int) (originalHeight * scaleFactor);

            Bitmap bitmap = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888);
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            imageView.setImageBitmap(bitmap);

            // Настройка параметров для ImageView
            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            imageView.setLayoutParams(imageParams);

            // Создаем контейнер для страницы
            LinearLayout pageContainer = new LinearLayout(this);
            pageContainer.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            pageContainer.setPadding(40, 16, 60, 16); // Отступы
            pageContainer.setBackgroundColor(getResources().getColor(android.R.color.black));
            pageContainer.addView(imageView);

            pdfContainer.addView(pageContainer);
            page.close();

            // Вычисляем хитбокс
            int[] location = new int[2];
            imageView.getLocationOnScreen(location); // Получаем координаты в экранных единицах
            Rect hitbox = new Rect(
                    location[0],
                    location[1],
                    location[0] + scaledWidth,
                    location[1] + scaledHeight
            );
            hitboxes.add(hitbox); // Сохраняем хитбокс
        }

        // Устанавливаем начальную позицию прокрутки только один раз
        if (isInitialScale) {
            pdfScrollView.scrollTo(0, 0);
            horizontalScrollView.scrollTo(0, 0);
            isInitialScale = false;
        }
    }

    private void checkHitbox(float x, float y) {
        for (int i = 0; i < hitboxes.size(); i++) {
            Rect hitbox = hitboxes.get(i);
            if (hitbox.contains((int) x, (int) y)) {
                //Toast.makeText(this, "Попадание на странице: " + (i + 1), Toast.LENGTH_SHORT).show();
                return; // Выход после первого попадания
            }
        }
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scale = detector.getScaleFactor();
            scaleFactor *= scale;

            // Ограничиваем масштабирование
            scaleFactor = Math.max(MIN_SCALE, Math.min(scaleFactor, MAX_SCALE));

            // Обновляем только видимые страницы
            displayVisiblePages();

            return true;
        }
    }

    private void navigateToLoginFragment() {
        SharedPrefManager.getInstance(this).saveAgreementAccepted(); // Сохраняем, что пользователь принял соглашение
        Intent intent = new Intent(this, SplashScreen.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (pdfRenderer != null) {
                pdfRenderer.close();
            }
            if (fileDescriptor != null) {
                fileDescriptor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
