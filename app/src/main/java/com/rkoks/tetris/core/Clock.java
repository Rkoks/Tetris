package com.rkoks.tetris.core;

public class Clock {
    //продолжительность 1 цикла
    private float millisPerCycle;
    //Время последнего обновления
    private long lastUpdate;
    //количество прошкдших, но необработанных циклов
    private int elapsedCycles;
    //времени до следующего завершения цикла
    private float excessCycles;

    private boolean isPaused;

    public Clock(float cyclesPerSecond) {
        setCyclesPerSecond(cyclesPerSecond);
        reset();
    }

    //определяем кол-во мс на цикл
    public void setCyclesPerSecond(float cyclesPerSecond) {
        this.millisPerCycle = (1.0f / cyclesPerSecond) * 1000;
    }

    //сбрасываем таймер
    public void reset() {
        this.elapsedCycles = 0;
        this.excessCycles = 0.0f;
        this.lastUpdate = getCurrentTime();
        this.isPaused = false;
    }

    //обновляем таймер
    public void update() {
        //вычисляем разницу во времени
        long currUpdate = getCurrentTime();
        float delta = (float)(currUpdate - lastUpdate) + excessCycles;

        //Вычисляем количество прошедших и незаконченых циклов
        if(!isPaused) {
            this.elapsedCycles += (int)Math.floor(delta / millisPerCycle);
            this.excessCycles = delta % millisPerCycle;
        }

        this.lastUpdate = currUpdate;
    }

    public void setPaused(boolean paused) {
        this.isPaused = paused;
    }

    //проверяем наличие пройденных циклов
    public boolean hasElapsedCycle() {
        if(elapsedCycles > 0) {
            this.elapsedCycles--;
            return true;
        }
        return false;
    }

    //вычисляем текущее время с самой высокой точностью
    private static final long getCurrentTime() {
        return (System.nanoTime() / 1000000L);
    }
}
