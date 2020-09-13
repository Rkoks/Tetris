package com.rkoks.tetris.draw;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

import com.rkoks.tetris.core.Tetris;

//поток отрисовки
public class DrawThread extends Thread {
    private SurfaceHolder surfaceHolder;
    private volatile boolean running = true;
    private Tetris tetris;

    public DrawThread(SurfaceHolder surfaceHolder, Tetris tetris) {
        this.surfaceHolder = surfaceHolder;
        this.tetris = tetris;
    }

    public void requestStop(){running = false;}

    @Override
    public void run() {
        while (running) {
            Canvas canvas = surfaceHolder.lockCanvas();
            if (canvas != null) {
                try {
                    tetris.render(canvas);
                }finally {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

}
