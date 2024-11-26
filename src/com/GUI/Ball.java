package com.GUI;

import javax.swing.ImageIcon;

public class Ball extends Sprite {

    private int xdir;
    private int ydir;

    public Ball() {
        initBall();
    }

    private void initBall() {
        xdir = 1; // Начальное направление по оси X
        ydir = -1; // Начальное направление по оси Y

        loadImage(); // Загружаем изображение мяча
        getImageDimensions(); // Получаем размеры изображения мяча
        resetState(); // Устанавливаем начальное положение мяча
    }

    private void loadImage() {
        var ii = new ImageIcon("src/resources/ball.png"); // Путь к изображению мяча
        image = ii.getImage();
    }

    void move() {
        x += xdir;
        y += ydir;

        // Проверка столкновения с левой и правой границей
        if (x <= 0) {
            setXDir(1); // Отскок от левой границы
        }

        if (x >= Commons.WIDTH - imageWidth) {
            setXDir(-1); // Отскок от правой границы
        }

        // Проверка столкновения с верхней границей
        if (y <= 0) {
            setYDir(1); // Отскок от верхней границы
        }

        // Проверка столкновения с нижней границей
        if (y >= Commons.HEIGHT - imageHeight) {
            setYDir(-1); // Отскок от нижней границы
        }
    }

    private void resetState() {
        x = Commons.INIT_BALL_X; // Начальная позиция мяча по оси X
        y = Commons.INIT_BALL_Y; // Начальная позиция мяча по оси Y
    }

    // Устанавливаем новое направление по оси X
    void setXDir(int x) {
        xdir = x;
    }

    // Устанавливаем новое направление по оси Y
    void setYDir(int y) {
        ydir = y;
    }

    // Получаем направление по оси X
    int getXDir() {
        return xdir;
    }

    // Получаем направление по оси Y
    int getYDir() {
        return ydir;
    }
}
