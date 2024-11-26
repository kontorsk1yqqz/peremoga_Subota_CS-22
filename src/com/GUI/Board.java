package com.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Board extends JPanel {

    private Timer timer;
    private String message = "Game Over";
    private Ball ball;
    private Paddle paddle;
    private Brick[] bricks;
    private boolean inGame = true;
    private boolean isPaused = false;
    private int score = 0; // Добавляем переменную для счета
    private int destroyedBricks = 0;

    public Board() {
        initBoard();
    }

    private void initBoard() {
        addKeyListener(new TAdapter());
        setFocusable(true);
        setPreferredSize(new Dimension(Commons.WIDTH, Commons.HEIGHT));
        gameInit();
    }

    private void gameInit() {
        bricks = new Brick[Commons.N_OF_BRICKS];

        ball = new Ball();
        paddle = new Paddle();

        int k = 0;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 6; j++) {
                bricks[k] = new Brick(j * 40 + 30, i * 10 + 50);
                k++;
            }
        }

        timer = new Timer(Commons.PERIOD, new GameCycle());
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        var g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        if (inGame) {
            if (isPaused) {
                drawPausedScreen(g2d);
            } else {
                drawObjects(g2d);
            }
        } else {
            gameFinished(g2d);
        }

        Toolkit.getDefaultToolkit().sync();
    }

    private void drawObjects(Graphics2D g2d) {
        g2d.drawImage(ball.getImage(), ball.getX(), ball.getY(),
                ball.getImageWidth(), ball.getImageHeight(), this);
        g2d.drawImage(paddle.getImage(), paddle.getX(), paddle.getY(),
                paddle.getImageWidth(), paddle.getImageHeight(), this);

        for (Brick brick : bricks) {
            if (!brick.isDestroyed()) {
                g2d.drawImage(brick.getImage(), brick.getX(), brick.getY(),
                        brick.getImageWidth(), brick.getImageHeight(), this);
            }
        }

        // Отображаем счет
        var font = new Font("Verdana", Font.PLAIN, 18);
        g2d.setColor(Color.BLACK);
        g2d.setFont(font);
        g2d.drawString("Score: " + score, 10, 20); // Отображаем счет
    }

    private void drawPausedScreen(Graphics2D g2d) {
        var font = new Font("Verdana", Font.BOLD, 18);
        FontMetrics fontMetrics = this.getFontMetrics(font);

        g2d.setColor(Color.BLACK);
        g2d.setFont(font);
        g2d.drawString("Game Paused",
                (Commons.WIDTH - fontMetrics.stringWidth("Game Paused")) / 2,
                Commons.HEIGHT / 2);
    }

    private void gameFinished(Graphics2D g2d) {
        var font = new Font("Verdana", Font.BOLD, 18);
        FontMetrics fontMetrics = this.getFontMetrics(font);

        g2d.setColor(Color.BLACK);
        g2d.setFont(font);
        g2d.drawString(message,
                (Commons.WIDTH - fontMetrics.stringWidth(message)) / 2,
                Commons.HEIGHT / 2);

        font = new Font("Verdana", Font.PLAIN, 14);
        g2d.setFont(font);
        g2d.drawString("          Press R to Restart or Q to Quit",
                (Commons.WIDTH - fontMetrics.stringWidth("Press R to Restart or Q to Quit")) / 2,
                Commons.HEIGHT / 2 + 30);
    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {
            paddle.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            if (key == KeyEvent.VK_P && inGame) {
                isPaused = !isPaused; // Toggle pause
                repaint();
            }

            if (key == KeyEvent.VK_R) {
                if (!inGame) {
                    restartGame();
                }
            }

            if (key == KeyEvent.VK_Q) {
                if (!inGame) {
                    System.exit(0);
                }
            }

            if (!isPaused) {
                paddle.keyPressed(e);
            }
        }
    }

    private class GameCycle implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!isPaused) {
                doGameCycle();
            }
        }
    }

    private void doGameCycle() {
        ball.move();
        paddle.move();
        checkCollision();
        repaint();
    }

    private void stopGame() {
        inGame = false;
        timer.stop();
    }

    private void restartGame() {
        inGame = true;
        isPaused = false;
        score = 0; // Сбрасываем счет при рестарте
        message = "Game Over";
        gameInit();
    }

    private void checkCollision() {
        if (this.ball.getRect().getMaxY() > 390.0) {
            this.stopGame();
        }

        int i = 0;
        int ballLPos;

        // Checking for victory (all bricks destroyed)
        for (ballLPos = 0; i < 30; ++i) {
            if (this.bricks[i].isDestroyed()) {
                ++ballLPos;
            }

            if (ballLPos == 30) {
                this.message = "Victory";
                this.stopGame();
            }
        }

        // Checking for paddle collision
        if (this.ball.getRect().intersects(this.paddle.getRect())) {
            i = (int)this.paddle.getRect().getMinX();
            ballLPos = (int)this.ball.getRect().getMinX();
            int ballHeight = i + 8;
            int second = i + 16;
            int third = i + 24;
            int fourth = i + 32;

            if (ballLPos < ballHeight) {
                this.ball.setXDir(-1);
                this.ball.setYDir(-1);
            }

            if (ballLPos >= ballHeight && ballLPos < second) {
                this.ball.setXDir(-1);
                this.ball.setYDir(-1 * this.ball.getYDir());
            }

            if (ballLPos >= second && ballLPos < third) {
                this.ball.setXDir(0);
                this.ball.setYDir(-1);
            }

            if (ballLPos >= third && ballLPos < fourth) {
                this.ball.setXDir(1);
                this.ball.setYDir(-1 * this.ball.getYDir());
            }

            if (ballLPos > fourth) {
                this.ball.setXDir(1);
                this.ball.setYDir(-1);
            }

            // Increment score when the ball hits the paddle
            score++; // Increase score on every paddle hit
        }

        // Checking for brick collision
        for (i = 0; i < 30; ++i) {
            if (this.ball.getRect().intersects(this.bricks[i].getRect())) {
                ballLPos = (int)this.ball.getRect().getMinX();
                int ballHeight = (int)this.ball.getRect().getHeight();
                int ballWidth = (int)this.ball.getRect().getWidth();
                int ballYPos = (int)this.ball.getRect().getMinY();

                // Checking all directions for collision
                Point pointRight = new Point(ballLPos + ballWidth + 1, ballYPos);
                Point pointLeft = new Point(ballLPos - 1, ballYPos);
                Point pointTop = new Point(ballLPos, ballYPos - 1);
                Point pointBottom = new Point(ballLPos, ballYPos + ballHeight + 1);

                // If the brick is not destroyed yet
                if (!this.bricks[i].isDestroyed()) {
                    // Checking for collision with right/left side
                    if (this.bricks[i].getRect().contains(pointRight)) {
                        this.ball.setXDir(-1);  // Bounce left
                    } else if (this.bricks[i].getRect().contains(pointLeft)) {
                        this.ball.setXDir(1);   // Bounce right
                    }

                    // Checking for collision with top/bottom
                    if (this.bricks[i].getRect().contains(pointTop)) {
                        this.ball.setYDir(1);   // Bounce down
                    } else if (this.bricks[i].getRect().contains(pointBottom)) {
                        this.ball.setYDir(-1);  // Bounce up
                    }

                    // Destroy the brick
                    this.bricks[i].setDestroyed(true);
                }
            }
        }
    }
}

