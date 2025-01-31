package com.example.gomind.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.example.gomind.ApiResponse;
import com.example.gomind.R;
import com.example.gomind.SharedPrefManager;
import com.example.gomind.UserData;
import com.example.gomind.api.QuestionAPI;
import com.example.gomind.api.RetrofitClient;
import com.example.gomind.api.UserAPI;
import com.example.gomind.Auction;
import com.example.gomind.User;
import com.example.gomind.sound.SoundManager;
import com.google.android.material.button.MaterialButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    // Предпочтительные размеры
    private static final int PREFERRED_WIDTH = 320;
    private static final int PREFERRED_HEIGHT = 180;
    private static final float ALLOWED_DEVIATION = 0.1f; // 10% отклонение
    private ImageView uploadImg;
    private Bitmap bitmap;

    private final QuestionAPI questionAPI = RetrofitClient.getInstance(getActivity()).getQuestionAPI();

    private MaterialButton bidBtn;
    private EditText edtBid;
    private SoundManager soundManager;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.upload_fragment, container, false);
        uploadImg = view.findViewById(R.id.upload_ads);
        edtBid = view.findViewById(R.id.edt_bid);
        bidBtn = view.findViewById(R.id.make_bid_btn);
        SoundManager soundManager = SoundManager.getInstance(getContext());
        // Получаем баланс пользователя
        fetchUserBalance();

        // Вызов метода для получения максимальной ставки
        fetchMaxBid();

        bidBtn.setOnClickListener(v -> {
            soundManager.playSound(); // Воспроизведение звука
            sendAdsRequest();
        });
        uploadImg.setOnClickListener(v -> openGallery());

        return view;
    }

    private void fetchUserBalance() {
        // Получаем экземпляр API UserAPI
        UserAPI userAPI = RetrofitClient.getInstance(getActivity()).getUserAPI();

        // Выполняем запрос для получения профиля пользователя
        Call<ApiResponse> call = userAPI.getProfile("jwt-cookie=" + SharedPrefManager.getInstance(getActivity()).getToken().getAccessToken() +
                "; refresh-jwt-cookie=" + SharedPrefManager.getInstance(getActivity()).getToken().getRefreshToken());

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();

                    // Извлекаем UserData из ApiResponse
                    UserData userData = apiResponse.getData();
                    if (userData != null) {
                        int balance = userData.getPears(); // Получаем баланс из UserData

                        // Обновляем TextView с id txt_pears
                        TextView txtPears = requireView().findViewById(R.id.txt_pears);
                        txtPears.setText("Баланс: " + balance);
                    } else {
                        Toast.makeText(getContext(), "Ошибка: данные пользователя отсутствуют", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Ошибка получения профиля", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getContext(), "Ошибка подключения к серверу", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void fetchMaxBid() {
        String cookie = "jwt-cookie=" + SharedPrefManager.getInstance(getActivity()).getToken().getAccessToken() +
                "; refresh-jwt-cookie=" + SharedPrefManager.getInstance(getActivity()).getToken().getRefreshToken();

        Call<List<Auction>> call = questionAPI.getAuctions();
        call.enqueue(new Callback<List<Auction>>() {
            @Override
            public void onResponse(Call<List<Auction>> call, Response<List<Auction>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Auction> auctions = response.body();
                    if (!auctions.isEmpty()) {
                        int maxBid = auctions.get(0).getCost(); // Берем первый элемент
                        TextView txtMaxBid = requireView().findViewById(R.id.txt_max_bid);
                        txtMaxBid.setText("Минимальная стоимость: " + maxBid);
                    } else {
                        Toast.makeText(getContext(), "Нет доступных ставок", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Ошибка загрузки ставок", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Auction>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getContext(), "Ошибка подключения к серверу", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST); // Для старых API
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
                if (isValidImage(bitmap)) {
                    // Отображаем изображение или выполняем другие действия
                    uploadImg.setImageBitmap(bitmap); // Например, в ImageView
                } else {
                    // Показываем сообщение о неверных размерах
                    Toast.makeText(getContext(), "Изображение должно быть 16:9 и размером около 320x180 (±10%)", Toast.LENGTH_LONG).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendAdsRequest() {
        File cacheDir = getActivity().getApplicationContext().getCacheDir();

        File imageFile;
        try {
            imageFile = bitmapToFile(bitmap, "uploaded_image.jpg", cacheDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        RequestBody title = RequestBody.create(MediaType.parse("application/json"), "Котенок");
        RequestBody description = RequestBody.create(MediaType.parse("application/json"), "Котейка");
        RequestBody cost = RequestBody.create(MediaType.parse("application/json"), edtBid.getText().toString());

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageFile);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", imageFile.getName(), requestFile);

        Call<String> call = questionAPI.addAdvertisementWithFile(
                "jwt-cookie=" + SharedPrefManager.getInstance(getActivity()).getToken().getAccessToken() +
                        "; refresh-jwt-cookie=" + SharedPrefManager.getInstance(getActivity()).getToken().getRefreshToken(),
                title, description, cost, filePart);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    System.out.println("Ответ сервера: " + response.body());
                } else {
                    System.out.println("Ошибка: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public static File bitmapToFile(Bitmap bitmap, String fileName, File cacheDir) throws IOException {
        File file = new File(cacheDir, fileName);
        FileOutputStream fos = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        fos.flush();
        fos.close();
        return file;
    }

    private boolean isValidImage(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float aspectRatio = (float) width / height;
        float targetRatio = 16f / 9f;

        if (Math.abs(aspectRatio - targetRatio) > 0.01) {
            return false; // Соотношение не соответствует 16:9
        }

        boolean isWidthValid = Math.abs(width - 320) <= 32; // ±10% от 320
        boolean isHeightValid = Math.abs(height - 180) <= 18; // ±10% от 180

        return isWidthValid && isHeightValid;
    }
}
