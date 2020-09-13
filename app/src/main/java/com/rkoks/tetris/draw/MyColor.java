package com.rkoks.tetris.draw;

import android.graphics.Color;

public class MyColor extends Color{
    private static final double FACTOR = 0.7;

    //Получаем значение нашего цвета по РГБ
    public static int toColor(int r,int g, int b) {
        return argb(255, r, g, b);
    }

    // Получаем цвет светлее данного
    public static int brighter(int myColor) {
        int r = red(myColor);
        int g = green(myColor);
        int b = blue(myColor);
        int alpha = alpha(myColor);

        int i = (int)(1.0/(1.0-FACTOR));
        if ( r == 0 && g == 0 && b == 0) {
            return argb(alpha,i,i,i);
        }
        if ( r > 0 && r < i ) r = i;
        if ( g > 0 && g < i ) g = i;
        if ( b > 0 && b < i ) b = i;

        return argb(alpha, Math.min((int)(r/FACTOR), 255),
                Math.min((int)(g/FACTOR), 255),
                Math.min((int)(b/FACTOR), 255));
    }

    //Получаем цвет темнее данного
    public static int darker(int myColor) {
        return argb(alpha(myColor), Math.max((int) (red(myColor) * FACTOR), 0),
                Math.max((int) (green(myColor) * FACTOR), 0),
                Math.max((int) (blue(myColor) * FACTOR), 0));
    }

    //Получаем цвет для "прицела"
    public static int ghostColor(int myColor){
        return argb(50, red(myColor), green(myColor), blue(myColor));
    }
}
