package com.rkoks.tetris.draw;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.rkoks.tetris.core.Controller;
import com.rkoks.tetris.core.LogicThread;

public class DrawGameField extends SurfaceView implements SurfaceHolder.Callback {
    //поток отрисовки
    private DrawThread drawThread;
    //поток логики
    private LogicThread logicThread;
    //обработчик жестов
    private GestureDetector gd;
    //высота и ширина поля
    private static int height, width;

    public DrawGameField(Context context) {
        super(context);
        getHolder().addCallback(this);
    }

    public static int getSurfaceHeight(){return height;}
    public static int getSurfaceWidth(){return width;}

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        height = this.getHeight();
        width = this.getWidth();

        //запускаем поток логики
        logicThread = new LogicThread();
        logicThread.start();

        //запускаем новый поток для отрисовки
        drawThread = new DrawThread(getHolder(), logicThread.getTetris());
        drawThread.start();

        //устанавливаем определение жестов
        gd = new GestureDetector(this.getContext(), new Controller(logicThread.getTetris(), width / 2, height / 2));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //востанавливаем скорость после удержания пальца в нижней части экрана
        if (event.getAction() == MotionEvent.ACTION_UP && event.getY() > width / 4 * 3) {
            logicThread.getTetris().normalDrop();
        }
        if (gd.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        //останавливаем поток для отрисовки
        drawThread.requestStop();
        boolean retry = true;
        while (retry) {
            try {
                drawThread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //останавливаем поток логики
        logicThread.requestStop();
        retry = true;
        while (retry) {
            try {
                logicThread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
