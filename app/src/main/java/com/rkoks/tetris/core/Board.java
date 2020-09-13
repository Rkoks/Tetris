package com.rkoks.tetris.core;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.rkoks.tetris.draw.DrawGameField;
import com.rkoks.tetris.draw.MyColor;

public class Board {
    //цветовые границы наших фигур
    public static final int COLOR_MIN = 35;
    public static final int COLOR_MAX = 255 - COLOR_MIN;

    //толщина границы поля
    private static final int BORDER_WIDTH = 3;
    private static final Paint BORDER_PAINT = new Paint();

    //краска для отрисовки сетки
    private static final Paint BACKGROUND_PAINT = new Paint();

    //количество столбцов на поле
    public static final int COL_COUNT = 10;

    //видимые, невидимые строки и общее их количество
    private static final int VISIBLE_ROW_COUNT = 20;
    private static final int HIDDEN_ROW_COUNT = 2;
    public static final int ROW_COUNT = VISIBLE_ROW_COUNT + HIDDEN_ROW_COUNT;

    //Для хранения шрифтов
    private static final Paint LARGE_FONT = new Paint();
    private static final Paint SMALL_FONT = new Paint();

    //ссылки на тетрис и фигуры
    private Tetris tetris;
    private Figure[][] blocks;

    //Размер блока
    public static int blockSize;
    //толщина тени блока
    public static int shadeWidth;

    //координаты центра поля
    private static int centerX;
    private static int centerY;

    //Координаты начала отрисовки поля
    private static int startX;
    private static int startY;

    //Ширина и высота поля
    public static int panelWidth;
    public static int panelHeight;

    //создаем экземпляр поля передаем ссылки на логику и фигуры, инициализируем
    public Board(Tetris tetris) {
        this.tetris = tetris;
        this.blocks = new Figure[ROW_COUNT][COL_COUNT];
        init();
    }


    private void init(){
        LARGE_FONT.setColor(Color.WHITE);
        LARGE_FONT.setTextSize(48);
        LARGE_FONT.setStyle(Paint.Style.FILL);
        LARGE_FONT.setTypeface(Typeface.create(Typeface.SANS_SERIF,Typeface.BOLD));

        SMALL_FONT.setColor(Color.WHITE);
        SMALL_FONT.setTextSize(32);
        SMALL_FONT.setStyle(Paint.Style.FILL);
        SMALL_FONT.setTypeface(Typeface.create(Typeface.SANS_SERIF,Typeface.BOLD));


        BACKGROUND_PAINT.setColor(Color.argb(255,169,124,189));
        BACKGROUND_PAINT.setStyle(Paint.Style.FILL);

        BORDER_PAINT.setColor(Color.LTGRAY);
        BORDER_PAINT.setStyle(Paint.Style.STROKE);
        BORDER_PAINT.setStrokeWidth(BORDER_WIDTH);

        int height = DrawGameField.getSurfaceHeight();
        int width = DrawGameField.getSurfaceWidth();
        int blSize1 = height / VISIBLE_ROW_COUNT;
        int blSize2 = (width - BORDER_WIDTH * 2) / (COL_COUNT + 3);
        blockSize = (blSize1 < blSize2) ? (blSize1) : (blSize2);
        shadeWidth = blockSize / 6;
        centerX = COL_COUNT * blockSize / 2;
        centerY = VISIBLE_ROW_COUNT * blockSize / 2;
        panelWidth = COL_COUNT * blockSize + BORDER_WIDTH * 2;
        panelHeight = VISIBLE_ROW_COUNT * blockSize + BORDER_WIDTH * 2;
        startX = 0;
        startY = 0;
    }

    //Чистим игровое поле
    public void clear() {
        for(int i = 0; i < ROW_COUNT; i++) {
            for(int j = 0; j < COL_COUNT; j++) {
                blocks[i][j] = null;
            }
        }
    }

    //возможно ли разместить фигуру
    public boolean checkCollisions(Figure type, int x, int y, int rotation) {

        //в горизонтальных границах поля.
        if(x < -type.getLeftInset(rotation) || x + type.getDimension() - type.getRightInset(rotation) >= COL_COUNT) {
            return false;
        }

        //в вертикальных границах поля
        if(y < -type.getTopInset(rotation) || y + type.getDimension() - type.getBottomInset(rotation) >= ROW_COUNT) {
            return false;
        }

        //проверка с каждым существующим блоком на поле
        for(int col = 0; col < type.getDimension(); col++) {
            for(int row = 0; row < type.getDimension(); row++) {
                if(type.isBlock(col, row, rotation) && isOccupied(x + col, y + row)) {
                    return false;
                }
            }
        }
        return true;
    }

    //добавляем фигуру на поле
    public void addFigure(Figure type, int x, int y, int rotation) {
        for(int col = 0; col < type.getDimension(); col++) {
            for(int row = 0; row < type.getDimension(); row++) {
                if(type.isBlock(col, row, rotation)) {
                    setBlock(col + x, row + y, type);
                }
            }
        }
    }

    //находим количество заполненных линий на поле
    public int checkLines() {
        int completedLines = 0;

        for(int row = 0; row < ROW_COUNT; row++) {
            if(checkLine(row)) {
                completedLines++;
            }
        }
        return completedLines;
    }

    //проверяем линию
    private boolean checkLine(int line) {
        //проверяем наличие пустого блока
        for(int col = 0; col < COL_COUNT; col++) {
            if(!isOccupied(col, line)) {
                return false;
            }
        }

        //если полная, то смещаем все верхние строки вниз
        for(int row = line - 1; row >= 0; row--) {
            for(int col = 0; col < COL_COUNT; col++) {
                setBlock(col, row + 1, getBlock(col, row));
            }
        }
        return true;
    }


    //занята ли клетка поля в данных координатах
    private boolean isOccupied(int x, int y) {
        return blocks[y][x] != null;
    }

    //устанавливаем блок в клетку поля в данных координатах
    private void setBlock(int  x, int y, Figure type) {
        blocks[y][x] = type;
    }

    //получаем блок по координатам
    private Figure getBlock(int x, int y) {
        return blocks[y][x];
    }

    //отрисовка стакана
    public void drawBoard(Canvas canvas){
        //рисуем в зависимости от состояния игры
        if (tetris.isPaused()) { //пауза
            String msg = "ПАУЗА";
            canvas.drawText(msg, startX + centerX - LARGE_FONT.measureText(msg) / 2, startY + centerY, LARGE_FONT);
        } else if (tetris.isNewGame() || tetris.isGameOver()) {//конец и начало - похожи
            String msg = tetris.isNewGame() ? "TETRIS" : "ИГРА ОКОНЧЕНА";
            canvas.drawText(msg, startX + centerX - LARGE_FONT.measureText(msg) / 2, startY + centerY, LARGE_FONT);

            msg = "Нажмите \"Заново\", чтобы сыграть" + (tetris.isNewGame() ? "" : " ещё раз");
            canvas.drawText(msg, startX + centerX - SMALL_FONT.measureText(msg) / 2, startY + centerY+100, SMALL_FONT);

        } else {//игра идёт
            canvas.drawColor(Color.BLACK);
            //рисуем блоки на поле
            for (int x = 0; x < COL_COUNT; x++) {
                for (int y = HIDDEN_ROW_COUNT; y < ROW_COUNT; y++) {
                    Figure tile = getBlock(x, y);
                    if (tile != null) {
                        drawBlock(tile, startX + x * blockSize, startY + (y - HIDDEN_ROW_COUNT) * blockSize, canvas);
                    }
                }
            }

            //определяем текущую фигуру
            Figure type = tetris.getFigureType();
            int figureCol = tetris.getFigureCol();
            int figureRow = tetris.getFigureRow();
            int rotation = tetris.getFigureRotation();

            //рисуем текущую фигуру
            for (int col = 0; col < type.getDimension(); col++) {
                for (int row = 0; row < type.getDimension(); row++) {
                    if (figureRow + row >= 2 && type.isBlock(col, row, rotation)) {
                        drawBlock(type, startX + (figureCol + col) * blockSize, startY + (figureRow + row - HIDDEN_ROW_COUNT) * blockSize, canvas);
                    }
                }
            }

            //рисуем "прицел" фигуры снизу
            int base = type.getBaseColor();
            base = MyColor.ghostColor(base);
            //определяем высоту
            for (int lowest = figureRow; lowest < ROW_COUNT; lowest++) {
                //если нет соприкоснования, то проверяем следующую
                if (checkCollisions(type, figureCol, lowest, rotation)) {
                    continue;
                }

                //отрисуем на единицу выше
                lowest--;

                //отрисовываем
                for (int col = 0; col < type.getDimension(); col++) {
                    for (int row = 0; row < type.getDimension(); row++) {
                        if (lowest + row >= 2 && type.isBlock(col, row, rotation)) {
                            drawBlock(base, MyColor.brighter(base), MyColor.darker(base),
                                    startX + (figureCol + col) * blockSize,
                                    startY + (lowest + row - HIDDEN_ROW_COUNT) * blockSize, canvas);
                        }
                    }
                }

                break;
            }

            //рисуем сетку
            for (int x = 0; x < COL_COUNT; x++) {
                for (int y = 0; y < VISIBLE_ROW_COUNT; y++) {
                    canvas.drawLine(startX, startY + y * blockSize, startX + COL_COUNT * blockSize, startY + y * blockSize, BORDER_PAINT);
                    canvas.drawLine(startX + x * blockSize, startY, startX + x * blockSize, startY + VISIBLE_ROW_COUNT * blockSize, BORDER_PAINT);
                }
            }
        }

        //внешний контур
        canvas.drawRect(startX, startY, startX + COL_COUNT * blockSize, startY + VISIBLE_ROW_COUNT * blockSize, BORDER_PAINT);
        canvas.drawRect(startX, startY + panelHeight,
                DrawGameField.getSurfaceWidth(),
                DrawGameField.getSurfaceHeight(), BACKGROUND_PAINT);


    }


    //рисуем блок сначала по типу, затем передаём цвета
    private void drawBlock(Figure type, int x, int y, Canvas canvas) {
        drawBlock(type.getBaseColor(), type.getLightColor(), type.getDarkColor(), x, y, canvas);
    }

    private void drawBlock(int base, int light, int dark, int x, int y, Canvas canvas) {
        Paint p = new Paint();
        //заливаем одноцветный блок
        p.setStyle(Paint.Style.FILL);
        p.setColor(base);
        canvas.drawRect(x, y, x + blockSize, y + blockSize, p);

        //заливаем нижнюю и правую тени
        p.setColor(dark);
        canvas.drawRect(x, y + blockSize - shadeWidth, x + blockSize, y + blockSize, p);
        canvas.drawRect(x + blockSize - shadeWidth, y, x + blockSize, y + blockSize, p);

        //полинейно отрисовываем левую и верзнюю тень, чтобы получить диагональное соединение на границе теней
        p.setColor(light);
        for(int i = 0; i < shadeWidth; i++) {
            canvas.drawLine(x, y + i, x + blockSize - i - 1, y + i, p);
            canvas.drawLine(x + i, y, x + i, y + blockSize - i - 1, p);
        }
    }

}
