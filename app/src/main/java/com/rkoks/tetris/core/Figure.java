package com.rkoks.tetris.core;

import com.rkoks.tetris.draw.MyColor;

public enum Figure {
    //Задаём типы фигур
    TypeI(MyColor.toColor(Board.COLOR_MIN, Board.COLOR_MAX, Board.COLOR_MAX), 4, 4, 1, new boolean[][] {
            {
                    false,	false,	false,	false,
                    true,	true,	true,	true,
                    false,	false,	false,	false,
                    false,	false,	false,	false,
            },
            {
                    false,	false,	true,	false,
                    false,	false,	true,	false,
                    false,	false,	true,	false,
                    false,	false,	true,	false,
            },
            {
                    false,	false,	false,	false,
                    false,	false,	false,	false,
                    true,	true,	true,	true,
                    false,	false,	false,	false,
            },
            {
                    false,	true,	false,	false,
                    false,	true,	false,	false,
                    false,	true,	false,	false,
                    false,	true,	false,	false,
            }
    }),

    TypeJ(MyColor.toColor(Board.COLOR_MIN, Board.COLOR_MIN, Board.COLOR_MAX), 3, 3, 2, new boolean[][] {
            {
                    true,	false,	false,
                    true,	true,	true,
                    false,	false,	false,
            },
            {
                    false,	true,	true,
                    false,	true,	false,
                    false,	true,	false,
            },
            {
                    false,	false,	false,
                    true,	true,	true,
                    false,	false,	true,
            },
            {
                    false,	true,	false,
                    false,	true,	false,
                    true,	true,	false,
            }
    }),

    TypeL(MyColor.toColor(Board.COLOR_MAX, 127, Board.COLOR_MIN), 3, 3, 2, new boolean[][] {
            {
                    false,	false,	true,
                    true,	true,	true,
                    false,	false,	false,
            },
            {
                    false,	true,	false,
                    false,	true,	false,
                    false,	true,	true,
            },
            {
                    false,	false,	false,
                    true,	true,	true,
                    true,	false,	false,
            },
            {
                    true,	true,	false,
                    false,	true,	false,
                    false,	true,	false,
            }
    }),

    TypeO(MyColor.toColor(Board.COLOR_MAX, Board.COLOR_MAX, Board.COLOR_MIN), 2, 2, 2, new boolean[][] {
            {
                    true,	true,
                    true,	true,
            },
            {
                    true,	true,
                    true,	true,
            },
            {
                    true,	true,
                    true,	true,
            },
            {
                    true,	true,
                    true,	true,
            }
    }),

    TypeS(MyColor.toColor(Board.COLOR_MIN, Board.COLOR_MAX, Board.COLOR_MIN), 3, 3, 2, new boolean[][] {
            {
                    false,	true,	true,
                    true,	true,	false,
                    false,	false,	false,
            },
            {
                    false,	true,	false,
                    false,	true,	true,
                    false,	false,	true,
            },
            {
                    false,	false,	false,
                    false,	true,	true,
                    true,	true,	false,
            },
            {
                    true,	false,	false,
                    true,	true,	false,
                    false,	true,	false,
            }
    }),

    TypeT(MyColor.toColor(128, Board.COLOR_MIN, 128), 3, 3, 2, new boolean[][] {
            {
                    false,	true,	false,
                    true,	true,	true,
                    false,	false,	false,
            },
            {
                    false,	true,	false,
                    false,	true,	true,
                    false,	true,	false,
            },
            {
                    false,	false,	false,
                    true,	true,	true,
                    false,	true,	false,
            },
            {
                    false,	true,	false,
                    true,	true,	false,
                    false,	true,	false,
            }
    }),

    TypeZ(MyColor.toColor(Board.COLOR_MAX, Board.COLOR_MIN, Board.COLOR_MIN), 3, 3, 2, new boolean[][] {
            {
                    true,	true,	false,
                    false,	true,	true,
                    false,	false,	false,
            },
            {
                    false,	false,	true,
                    false,	true,	true,
                    false,	true,	false,
            },
            {
                    false,	false,	false,
                    true,	true,	false,
                    false,	true,	true,
            },
            {
                    false,	true,	false,
                    true,	true,	false,
                    true,	false,	false,
            }
    });

    //Основной цвет фигуры
    private int baseColor;
    //Цвет для верхней и левой границы фигуры
    private int lightColor;
    //Цвет для нижней и правой границы фигуры
    private int darkColor;

    //Столбец появления фигуры
    private int spawnCol;
    //Ряд появления фигуры
    private int spawnRow;

    //Размерность фигуры
    private int dimension;
    //Рядов в фигуре
    private int rows;
    //Столбцов в фигуре
    private int cols;

    //Массив блоков для фигуры
    private boolean[][] blocks;

    Figure(int color, int dimension, int cols, int rows, boolean[][] blocks) {
        this.baseColor = color;
        this.lightColor = MyColor.brighter(color);
        this.darkColor = MyColor.darker(color);
        this.dimension = dimension;
        this.blocks = blocks;
        this.cols = cols;
        this.rows = rows;

        this.spawnCol = 5 - (dimension >> 1);
        this.spawnRow = getTopInset(0);
    }

    //геттеры
    public int getBaseColor() {
        return baseColor;
    }
    public int getLightColor() {
        return lightColor;
    }
    public int getDarkColor() {
        return darkColor;
    }
    public int getDimension() {
        return dimension;
    }
    public int getSpawnColumn() {
        return spawnCol;
    }
    public int getSpawnRow() {
        return spawnRow;
    }
    public int getRows() {
        return rows;
    }
    public int getCols() {
        return cols;
    }

    //проверка на блок в масиве фигуры
    public boolean isBlock(int x, int y, int rotation) {
        return blocks[rotation][y * dimension + x];
    }

    //внутренее положение первого блока слева
    public int getLeftInset(int rotation) {
        for(int x = 0; x < dimension; x++) {
            for(int y = 0; y < dimension; y++) {
                if(isBlock(x, y, rotation)) {
                    return x;
                }
            }
        }
        return -1;
    }

    //внутренее положение первого блока справа
    public int getRightInset(int rotation) {
        for(int x = dimension - 1; x >= 0; x--) {
            for(int y = 0; y < dimension; y++) {
                if(isBlock(x, y, rotation)) {
                    return dimension - x;
                }
            }
        }
        return -1;
    }

    //внутренее положение первого блока сверху
    public int getTopInset(int rotation) {
        for(int y = 0; y < dimension; y++) {
            for(int x = 0; x < dimension; x++) {
                if(isBlock(x, y, rotation)) {
                    return y;
                }
            }
        }
        return -1;
    }

    //внутренее положение первого блока сниху
    public int getBottomInset(int rotation) {
        for(int y = dimension - 1; y >= 0; y--) {
            for(int x = 0; x < dimension; x++) {
                if(isBlock(x, y, rotation)) {
                    return dimension - y;
                }
            }
        }
        return -1;
    }
}
