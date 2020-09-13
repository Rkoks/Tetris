package com.rkoks.tetris.core;

//запуск расчётов игры в отдельном потоке
public class LogicThread extends Thread {
    private volatile boolean running = true;
    private Tetris tetris;

    public LogicThread() {
        //создадим игру
        this.tetris = new Tetris();
        tetris.startGame();
    }
    //получим экземпляр
    public Tetris getTetris(){
        return this.tetris;
    }

    //запрос на остановку потока
    public void requestStop(){running = false;}

    @Override
    public void run() {
        while (running) {
            tetris.calculate();//расчёты
        }
    }
}
