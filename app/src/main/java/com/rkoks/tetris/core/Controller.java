package com.rkoks.tetris.core;

import android.view.GestureDetector;
import android.view.MotionEvent;

public class Controller extends GestureDetector.SimpleOnGestureListener {
    //координаты центра
    private float centerX, centerY;
    //экземпляр тетриса
    private Tetris tetris;

    public Controller(Tetris tetris, int centerX, int centerY) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.tetris = tetris;
    }

    //свапы
    @Override
    public boolean onFling(MotionEvent downEvent, MotionEvent moveEvent, float vX, float vY) {
        boolean result = false;
        float diffX = moveEvent.getX() - downEvent.getX();
        float diffY = moveEvent.getY() - downEvent.getY();
        if (Math.abs(diffX) < Math.abs(diffY)) {
            //при свайпе вверх или вниз вращаем фигуру по или против часовой стрелки
            if (Math.abs(diffY) > 50 && Math.abs(vY) > 50) {
                if (diffY > 0) { //свайп вниз
                    if (downEvent.getX() > centerX){ //в правой части экрана
                        tetris.rotateClockwise();
                    } else { //в левой части экрана
                        tetris.rotateAnticlockwise();
                    }
                } else { //свайп вверх
                    if (downEvent.getX() > centerX){ //в правой части экрана
                        tetris.rotateAnticlockwise();
                    } else { //в левой части экрана
                        tetris.rotateClockwise();
                    }
                }
                result = true;
            }
        } else { //при свайпе вправо или влево перемещаем фигуру в соответствующую сторону
            if (Math.abs(diffX) > 50 && Math.abs(vX) > 50) {
                if (diffX > 0) { //свайп вправо
                    tetris.moveRight();
                } else { //свайп влево
                    tetris.moveLeft();
                }
                result = true;
            }
        }
        return result;
    }

    //при зажатии в нижней части поля сбрасываем фигуру
    @Override
    public boolean onDown(MotionEvent e) {
        if (e.getY() > centerY * 1.5f) {
            tetris.speedDrop();
        }
        return true;
    }

    //по тапу перемещаем фигуру
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        if (e.getY() < centerY * 1.5f) {
            if (e.getX() > centerX) { //вправо
                tetris.moveRight();
            } else { //влево
                tetris.moveLeft();
            }
        }
        return super.onSingleTapConfirmed(e);
    }
}
