package com.rkoks.tetris.core;

import android.graphics.Canvas;

import com.rkoks.tetris.menus.Database;
import com.rkoks.tetris.menus.GameActivity;

import java.util.Random;

public class Tetris {
    //длительность кадра
    private static final long FRAME_TIME = 1000L / 50L;
    //разновидности фигур
    private static final int TYPE_COUNT = Figure.values().length;
    //игровой стакан
    private Board board;
    //вспомогательная панель
    private InfoPanel side;

    public boolean isPaused;
    private boolean isNewGame;
    private boolean isGameOver;
    private int level;
    private int score;
    //для случайного выбора фигур
    private Random random;
    //таймер логики
    private Clock logicTimer;
    //текущая и следующая фигура
    private Figure currentType;
    private Figure nextType;
    //текущее положение
    private int currentCol;
    private int currentRow;
    private int currentRotation;
    //задержка перед сбросом
    private int dropCooldown;
    //скорость игры
    private float gameSpeed;

    public Tetris() {
        //инициализируем стакан и доп панель
        this.board = new Board(this);
        this.side = new InfoPanel(this);

        //передаем экземпляр в игровое активити
        GameActivity.setTetris(this);
    }

    //ускоряем падение фигуры
    public void speedDrop(){
        if(!isPaused && !isNewGame && !isGameOver && dropCooldown == 0) {
            logicTimer.setCyclesPerSecond(10.0f);
        }
    }

    //возвращаемся к текущей скорости падения фигуры
    public void normalDrop(){
        logicTimer.setCyclesPerSecond(gameSpeed);
    }

    //поворачиваем против часовой стрелки
    public void rotateAnticlockwise(){
        if(!isPaused && !isNewGame && !isGameOver) {
            rotateFigure((currentRotation == 0) ? 3 : currentRotation - 1);
        }
    }

    //поворачиваем по часовой стрелке
    public void rotateClockwise(){
        if(!isPaused && !isNewGame && !isGameOver) {
            rotateFigure((currentRotation == 3) ? 0 : currentRotation + 1);
        }
    }

    //перемещение фигуры влево
    public void moveLeft(){
        if(!isPaused && !isNewGame && !isGameOver)
            if (board.checkCollisions(currentType, currentCol - 1, currentRow, currentRotation)){
                currentCol--;
            }
    }

    //перемещение фигуры вправо
    public void moveRight(){
        if (!isPaused && !isNewGame && !isGameOver) {

            if (board.checkCollisions(currentType, currentCol + 1, currentRow, currentRotation)) {
                currentCol++;
            }
        }
    }

    //Поставить игру на паузу или продолжить
    public void pauseGame(){
        if(!isGameOver && !isNewGame) {
            isPaused = !isPaused;
            logicTimer.setPaused(isPaused);
        }
    }

    //Начинаем игру
    public void startGame() {

        this.random = new Random();
        this.isNewGame = true;
        this.gameSpeed = 1.0f;

        //создаём таймер и ставим его на паузу, чтобы игрок мог подготовиться
        this.logicTimer = new Clock(gameSpeed);
        logicTimer.setPaused(true);
    }

    //Обсчитываем игру
    public void calculate(){
        if(!isPaused && !isNewGame && !isGameOver) {

            //время начала кадра
            long start = System.nanoTime();

            //обновляем таймер логики
            logicTimer.update();
            //если цикл завершён, то обновляе  игру
            if (logicTimer.hasElapsedCycle()) {
                updateGame();
            }

            //отсчёт задержки перед возможностью сброса
            if (dropCooldown > 0) {
                dropCooldown--;
            }

            //синхранизируем кадры
            long delta = (System.nanoTime() - start) / 1000000L;
            if (delta < FRAME_TIME) {
                try {
                    Thread.sleep(FRAME_TIME - delta);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //обновляем игру
    private void updateGame() {
        //можно ли опустить фигуру
        if(board.checkCollisions(currentType, currentCol, currentRow + 1, currentRotation)) {
            //увеличиваем текущую строку
            currentRow++;
        } else {
            //кладём фигуру в стакан
            board.addFigure(currentType, currentCol, currentRow, currentRotation);

            //удаляются ли после этого строки
            int cleared = board.checkLines();
            //очки зависят от количества удаленных строк
            switch (cleared) {
                case 1:
                    score += 100;
                    break;
                case 2:
                    score += 300;
                    break;
                case 3:
                    score += 700;
                    break;
                case 4:
                    score += 1500;
                    break;
            }

            //прибавляем скорость и обновляем таймер логики
            gameSpeed += 0.035f;
            logicTimer.setCyclesPerSecond(gameSpeed);
            logicTimer.reset();

            //устанвливаем задержку на сброс
            dropCooldown = 25;
            //определим уровень сложности для отображения игроку
            level = (int)(gameSpeed * 1.70f);

            //создаем новую фигуру
            spawnFigure();
        }
    }

    //перезапуск игры
    public void resetGame() {
        this.level = 1;
        this.score = 0;
        this.gameSpeed = 1.0f;
        this.nextType = Figure.values()[random.nextInt(TYPE_COUNT)];
        this.isNewGame = false;
        this.isGameOver = false;
        board.clear();
        spawnFigure();
        logicTimer.setCyclesPerSecond(gameSpeed);
        logicTimer.reset();

    }

    //Рисуем игру
    public void render(Canvas canvas){
        board.drawBoard(canvas);
        side.drawPanel(canvas);
    }

    //создание новой фигуры
    private void spawnFigure() {
        //запускаем следующую фигуру на поле
        this.currentType = nextType;
        this.currentCol = currentType.getSpawnColumn();
        this.currentRow = currentType.getSpawnRow();
        this.currentRotation = 0;
        //создаём новую "следующую" фигуру
        this.nextType = Figure.values()[random.nextInt(TYPE_COUNT)];

        //проверяем на проигрыш
        if(!board.checkCollisions(currentType, currentCol, currentRow, currentRotation)) {
            this.isGameOver = true;
            //записываем результат
            new Database().writeRecord(getScore());
            logicTimer.setPaused(true);
        }
    }

    //проверка на возможность вращения
    private void rotateFigure(int newRotation) {
        //временые хранилища в случае смещения фигуры
        int newColumn = currentCol;
        int newRow = currentRow;

        //пустое место со всех сторон
        int left = currentType.getLeftInset(newRotation);
        int right = currentType.getRightInset(newRotation);
        int top = currentType.getTopInset(newRotation);
        int bottom = currentType.getBottomInset(newRotation);

        //если выходит за поле вправо или влево, то поправляем
        if(currentCol < -left) {
            newColumn -= currentCol - left;
        } else if(currentCol + currentType.getDimension() - right >= Board.COL_COUNT) {
            newColumn -= (currentCol + currentType.getDimension() - right) - Board.COL_COUNT + 1;
        }

        //если выходит за поле вверх или вниз, то поправляем
        if(currentRow < -top) {
            newRow -= currentRow - top;
        } else if(currentRow + currentType.getDimension() - bottom >= Board.ROW_COUNT) {
            newRow -= (currentRow + currentType.getDimension() - bottom) - Board.ROW_COUNT + 1;
        }

        //в случае возможности нового положения, обновляем
        if(board.checkCollisions(currentType, newColumn, newRow, newRotation)) {
            currentRotation = newRotation;
            currentRow = newRow;
            currentCol = newColumn;
        }
    }

    //геттеры
    public boolean isPaused() {return isPaused;}
    public boolean isGameOver() {return isGameOver;}
    public boolean isNewGame() {return isNewGame;}
    public int getScore() {return score;}
    public int getLevel() {return level;}
    public Figure getFigureType() {return currentType;}
    public Figure getNextFigureType() {return nextType;}
    public int getFigureCol() {return currentCol;}
    public int getFigureRow() {return currentRow;}
    public int getFigureRotation() {return currentRotation;}
}
