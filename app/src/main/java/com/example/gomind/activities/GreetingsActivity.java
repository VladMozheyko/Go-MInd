package com.example.gomind.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class GreetingsActivity extends AppCompatActivity {
    private PdfRenderer pdfRenderer;
    private ParcelFileDescriptor fileDescriptor;
    private LinearLayout pdfContainer;
    private HorizontalScrollView horizontalScrollView;
    private ScrollView pdfScrollView;
    private CheckBox confirmCheckBox;
    private ScaleGestureDetector scaleGestureDetector;

    private float scaleFactor = 1.7f; // Исходное значение
    private final float MIN_SCALE = 1.7f; // Минимальный масштаб — не меньше 1.7
    private final float MAX_SCALE = 4.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greetings);

        pdfContainer = findViewById(R.id.pdfContainer);
        pdfScrollView = findViewById(R.id.verticalScrollView);
        horizontalScrollView = findViewById(R.id.horizontalScrollView);
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

        // Обработка событий прокрутки и масштабирования
        View.OnTouchListener touchListener = (v, event) -> {
            scaleGestureDetector.onTouchEvent(event);
            return false;
        };

        pdfScrollView.setOnTouchListener(touchListener);
        horizontalScrollView.setOnTouchListener(touchListener);
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

        pdfContainer.removeAllViews();

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

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            imageView.setLayoutParams(params);

            pdfContainer.addView(imageView);

            page.close();
        }

        pdfContainer.invalidate();
    }

    private void navigateToLoginFragment() {
        Intent intent = new Intent(this, SplashScreen.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (scaleGestureDetector.onTouchEvent(event)) {
            return true;
        }
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
            scaleFactor = Math.max(MIN_SCALE, Math.min(scaleFactor, MAX_SCALE));

            // Если пользователь уменьшает масштаб ниже исходного значения, вернуть к 1.7
            if (scaleFactor < 1.7f) {
                scaleFactor = 1.7f;
            }

            Log.d("ScaleGesture", "ScaleFactor: " + scaleFactor);

            displayAllPages();
            return true;
        }
    }
}
