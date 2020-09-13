package com.rkoks.tetris.menus;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.rkoks.tetris.R;
import com.rkoks.tetris.core.Tetris;
import com.rkoks.tetris.draw.DrawGameField;

import java.util.Objects;

public class GameActivity extends AppCompatActivity {
    private Button bBack, bPauseResume, bRestart;
    private SurfaceView sfView;
    private static Tetris tetris;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_field);

        //разворачиваем приложение на весь экран
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Переход в главное меню
        bBack = (Button) findViewById(R.id.btnBackRate);
        bBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack();
            }
        });

        //Пауза/Продолжить игру
        bPauseResume = (Button) findViewById(R.id.btnPauseResume);
        bPauseResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tetris.pauseGame();
                if (tetris.isPaused){
                    bPauseResume.setText(getString(R.string.resume));
                } else {
                    bPauseResume.setText(getString(R.string.pause));
                }
            }
        });

        //Начать заново
        bRestart = (Button) findViewById(R.id.btnRestart);
        bRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tetris.resetGame();
            }
        });
        //добавляем область отрисовки
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linear);
        sfView = new DrawGameField(this);
        sfView.setLongClickable(true);
        linearLayout.addView(sfView);
    }

    //Меняем функцию системной кнопки "Назад" для перехода в главное меню
    @Override
    public void onBackPressed() {
        goBack();
    }

    public static void setTetris(Tetris t){tetris = t;}

    private void goBack() {
        try {
            Intent intent = new Intent(GameActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Log.e("GameActivity.btnBack: ", Objects.requireNonNull(e.getMessage()));
        }
    }
}