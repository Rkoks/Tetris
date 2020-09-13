package com.rkoks.tetris.menus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.rkoks.tetris.R;

import java.util.Objects;

import static com.rkoks.tetris.menus.Auth.doAuth;
import static com.rkoks.tetris.menus.Auth.regUser;

public class MainActivity extends AppCompatActivity {

    private Button bStart, bRating;
    private long backPressedTime;
    private Toast backToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //разворачиваем приложение на весь экран
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Зарегестрируем игрока в Firebase
        doAuth();
        regUser(this);

        //Переход в окно с игровым полем по нажатии кнопки Новая игра
        bStart = (Button) findViewById(R.id.btnStart);
        bStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(MainActivity.this, GameActivity.class);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    Log.e("MainActivity.btnStart: ", Objects.requireNonNull(e.getMessage()));
                }
            }
        });

        //Переход в окно с рейтингом
        bRating = (Button) findViewById(R.id.btnRatings);
        bRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(MainActivity.this, RatingActivity.class);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    Log.e("MainActivity.bRating: ", Objects.requireNonNull(e.getMessage()));
                }
            }
        });

    }

    //Двойное нажатие на системную кнопку "Назад" для выхода
    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            return;
        } else {
            backToast = Toast.makeText(getBaseContext(), "Нажмите ещё раз, чтобы выйти", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }
}