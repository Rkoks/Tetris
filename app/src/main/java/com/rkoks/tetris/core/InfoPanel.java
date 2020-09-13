package com.rkoks.tetris.core;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.rkoks.tetris.draw.DrawGameField;

public class InfoPanel {
    //размер блока для предпросмотра
    private static final int BLOCK_SIZE = Board.blockSize / 2;
    //толщина тени для предпросмотра
    private static final int SHADE_WIDTH = Board.shadeWidth / 2;
    //размерность фигуры для предпросмотра
    private static final int BLOCK_COUNT = 5;

    //размер окна отображения следующей фигуры
    private static final int SQUARE_SIZE = BLOCK_SIZE * BLOCK_COUNT / 2;

    //размер отступа
    private static final int INSET = 40;

    //шрифт
    private static final Paint FONT = new Paint();

    //для заны препросмотра
    private Paint p = new Paint();

    //экземпляр тетриса
    private Tetris tetris;
    //Ширина и высота боковой панели
    private static int panelWidth;
    private static int panelHeight;

    //координаты центра поля
    private static int centerX;
    private static int offset;

    //Координаты начала отрисовки поля
    private static int startX;
    private static int startY;

    public InfoPanel(Tetris tetris) {
        this.tetris = tetris;
        init();
    }
    //инициализация
    private void init() {
        FONT.setColor(Color.GREEN);
        FONT.setTextSize(32);
        FONT.setStyle(Paint.Style.FILL);
        FONT.setTypeface(Typeface.create(Typeface.SANS_SERIF,Typeface.BOLD));

        p.setColor(Color.GREEN);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(2);

        startX = Board.panelWidth;
        startY = 0;
        panelHeight = Board.panelHeight;
        int width = DrawGameField.getSurfaceWidth();
        panelWidth = width - Board.panelWidth;

        centerX = panelWidth / 2;
    }

    //отрисовка доп панели
    public void drawPanel(Canvas canvas) {
        //положение "курсора" отрисовки
        offset = 100;

        //окно предпросмотра
        canvas.drawText("Следующая:",
                startX + centerX - FONT.measureText("Следующая:") / 2,
                startY + offset, FONT);
        offset += INSET;

        canvas.drawRect(startX + centerX - SQUARE_SIZE,
                startY + offset,
                startX + centerX + SQUARE_SIZE,
                startY + offset + SQUARE_SIZE * 2,
                p);

        //отрисовка фигуры в окне предпросмотра
        Figure type = tetris.getNextFigureType();
        if(!tetris.isGameOver() && type != null) {
            //положение фигуры
            int cols = type.getCols();
            int rows = type.getRows();
            int dimension = type.getDimension();

            //координаты левого верхнего угла
            int nextX = (startX + centerX - (cols * BLOCK_SIZE / 2));
            int nextY = (startY + offset + SQUARE_SIZE - (rows * BLOCK_SIZE / 2));

            int top = type.getTopInset(0);
            int left = type.getLeftInset(0);

            //отриысовываем каждый блок
            for (int row = 0; row < dimension; row++) {
                for (int col = 0; col < dimension; col++) {
                    if (type.isBlock(col, row, 0)) {
                        drawBlock(type, nextX + ((col - left) * BLOCK_SIZE), nextY + ((row - top) * BLOCK_SIZE), canvas);
                    }
                }
            }
        }
        //смещаем "курсор"
        offset += (SQUARE_SIZE + INSET) * 2;

        //Статистика
        FONT.setStyle(Paint.Style.FILL);

        canvas.drawText("Статы", startX + centerX - FONT.measureText("Статы") / 2, startY + offset, FONT);
        offset += INSET;

        canvas.drawText("Уровень: " + tetris.getLevel(),
                startX + centerX - FONT.measureText("Уровень: " + tetris.getLevel()) / 2,
                startY + offset, FONT);
        offset += INSET;
        canvas.drawText("Очки: " + tetris.getScore(),
                startX + centerX - FONT.measureText("Очки: " + tetris.getScore()) / 2,
                startY + offset, FONT);




    }


    //рисуем блок зная тип фигуры
    private void drawBlock(Figure type, int x, int y, Canvas canvas) {
        drawBlock(type.getBaseColor(), type.getLightColor(), type.getDarkColor(), x, y, canvas);
    }

    //рисуем блок зная цвета переданные от типа фигуры
    private void drawBlock(int base, int light, int dark, int x, int y, Canvas canvas) {
        Paint p = new Paint();
        //заливаем одноцветный блок
        p.setStyle(Paint.Style.FILL);
        p.setColor(base);
        canvas.drawRect(x, y, x + BLOCK_SIZE, y + BLOCK_SIZE, p);

        //заливаем нижнюю и правую тени
        p.setColor(dark);
        canvas.drawRect(x, y + BLOCK_SIZE - SHADE_WIDTH, x + BLOCK_SIZE, y + BLOCK_SIZE, p);
        canvas.drawRect(x + BLOCK_SIZE - SHADE_WIDTH, y, x + BLOCK_SIZE, y + BLOCK_SIZE, p);

        //полинейно отрисовываем левую и верзнюю тень, чтобы получить диагональное соединение на границе теней
        p.setColor(light);
        for(int i = 0; i < SHADE_WIDTH; i++) {
            canvas.drawLine(x, y + i, x + BLOCK_SIZE - i - 1, y + i, p);
            canvas.drawLine(x + i, y, x + i, y + BLOCK_SIZE - i - 1, p);
        }
    }
}
