package com.example.gomind;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class SharedPrefManager {
    private static final String SHARED_PREF_NAME = "my_shared_preff";
    private static SharedPrefManager mInstance;
    private final String VISITED_KEY = "IS_VISITED";
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    private static final String AGREEMENT_ACCEPTED_KEY = "agreement_accepted";


    // Сохранение флага принятия соглашения
    public void saveAgreementAccepted() {
        editor.putBoolean(AGREEMENT_ACCEPTED_KEY, true);
        editor.apply();
    }

    // Проверка, было ли принято соглашение
    public boolean isAgreementAccepted() {
        return sp.getBoolean(AGREEMENT_ACCEPTED_KEY, false);
    }

    private SharedPrefManager(Context mCtx) {
        this.sp =  mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    public static synchronized SharedPrefManager getInstance(Context mCtx){
        if(mInstance == null){
            mInstance = new SharedPrefManager(mCtx);
        }

        return mInstance;
    }

    public void saveVisit(){
        editor.putBoolean(VISITED_KEY, true);
        editor.apply();
    }


    public void saveToken(Token token){
        editor.putString("token", token.getAccessToken());
        editor.putString("refreshToken", token.getRefreshToken());

        editor.apply();
    }

    public boolean isVisited(){
        return sp.getBoolean(VISITED_KEY, false);
    }


    public Token getToken(){
        return new Token(
                sp.getString("token", null),
                sp.getString("refreshToken", null)
        );
    }

    public void saveUser(User user) {
        editor.putString("email", user.getEmail());
        editor.putString("nickname", user.getNickname());
        editor.putString("pears", String.valueOf(user.getPears()));
        editor.putString("count", String.valueOf(user.getCount()));
        saveToken(user.getToken());
        editor.apply();
    }


    public boolean isLoggedIn(){
        return !sp.getString("token", "unauthorized").equals("unauthorized");
    }

    public User getUser(){
        User user = new User(
                sp.getString("email", null),
                sp.getString("nickname", null),
                Integer.parseInt(sp.getString("pears", "0")),
                Integer.parseInt(sp.getString("count", "0")),
                getToken());
        return user;
    }

    public void saveImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        editor.putString("image", encodedImage);
        editor.apply();
    }

    public Bitmap loadImage() {

        String encodedImage = sp.getString("image", null);

        if (encodedImage != null) {
            byte[] imageBytes = Base64.decode(encodedImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        }

        return null;
    }

    public void clear(){
        editor.clear();
        editor.apply();
    }
}