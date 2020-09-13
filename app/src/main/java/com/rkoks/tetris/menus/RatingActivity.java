package com.rkoks.tetris.menus;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.rkoks.tetris.R;

import java.util.Objects;

public class RatingActivity extends AppCompatActivity {

    private Button bBackRate;
    private Database mDB;
    private ListView mlist;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rating_menu);

        //разворачиваем приложение на весь экран
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        init();
    }

    public void init(){
        mDB = new Database();
        //Переход в главное меню
        bBackRate = (Button) findViewById(R.id.btnBackRate);
        bBackRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack();
            }
        });

        mlist = (ListView) findViewById(R.id.listView);

        //выведем результаты пользователя
        mDB.getRecord(mlist);
    }
    //Возвращаемся в главное меню
    @Override
    public void onBackPressed() {
        goBack();
    }

    private void goBack() {
        try {
            Intent intent = new Intent(RatingActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Log.e("Rating.btnBack: ", Objects.requireNonNull(e.getMessage()));
        }
    }

}
