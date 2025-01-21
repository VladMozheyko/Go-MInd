package com.example.gomind.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gomind.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class GreetingsActivity extends AppCompatActivity {
    private PdfRenderer pdfRenderer;
    private ParcelFileDescriptor fileDescriptor;
    private LinearLayout pdfContainer;
    private CheckBox confirmCheckBox;
    private ScaleGestureDetector scaleGestureDetector;

    private float scaleFactor = 1.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greetings);

        pdfContainer = findViewById(R.id.pdfContainer);
        confirmCheckBox = findViewById(R.id.confirmCheckBox);

        try {
            openRenderer();
            displayAllPages();

            findViewById(R.id.confirmButton).setOnClickListener(v -> {
                if (confirmCheckBox.isChecked()) {
                    navigateToLoginFragment();
                } else {
                    Toast.makeText(this, "Необходимо поставить галочку для подтверждения", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка открытия PDF", Toast.LENGTH_SHORT).show();
        }

        // Инициализация ScaleGestureDetector для масштабирования
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
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

    private void displayAllPages() {
        if (pdfRenderer == null) return;

        pdfContainer.removeAllViews(); // Очистка контейнера перед добавлением страниц

        for (int i = 0; i < pdfRenderer.getPageCount(); i++) {
            PdfRenderer.Page page = pdfRenderer.openPage(i);
            ImageView imageView = new ImageView(this);

            // Установка размеров в зависимости от масштаба
            int width = (int) (getResources().getDisplayMetrics().widthPixels * scaleFactor);
            int height = (int) (page.getHeight() * width / page.getWidth()); // Сохраняем пропорции

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            imageView.setImageBitmap(bitmap);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER); // Используем FIT_CENTER для лучшего отображения

            pdfContainer.addView(imageView);
            page.close();
        }
    }

    private void navigateToLoginFragment() {
        Intent intent = new Intent(this, SplashScreen.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
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

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(0.5f, Math.min(scaleFactor, 3.0f));

            // Перерисовка страниц при изменении масштаба
            displayAllPages();
            return true;
        }
    }
}

