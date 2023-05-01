import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class BrickBreaker extends JFrame {
    private GamePanel gamePanel;

    public BrickBreaker() {
        setTitle("Brick Breaker Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setResizable(false);
        setLocationRelativeTo(null);

        gamePanel = new GamePanel();
        add(gamePanel);
    }

    public static void main(String[] args) {
        BrickBreaker game = new BrickBreaker();
        game.setVisible(true);
    }

    class GamePanel extends JPanel implements ActionListener, KeyListener {
        private int ballX = 350;
        private int ballY = 300;
        private int ballSize = 20;
        private int ballSpeedX = 4;
        private int ballSpeedY = 4;
        private final int numRows = 4;
        private final int numCols = 10;
        private final int brickWidth = 70;
        private final int brickHeight = 20;
        private final int paddleWidth = 100;
        private final int paddleHeight = 15;
        private int paddleX = 350;
        private boolean moveLeft = false;
        private boolean moveRight = false;
        private Brick[][] bricks;

        public GamePanel() {
            Timer timer = new Timer(10, this);
            timer.start();
            bricks = new Brick[numRows][numCols];
            initBricks();
            addKeyListener(this);
            setFocusable(true);
        }

        private void initBricks() {
            for (int row = 0; row < numRows; row++) {
                for (int col = 0; col < numCols; col++) {
                    bricks[row][col] = new Brick(10 + col * (brickWidth + 5), 50 + row * (brickHeight + 5), brickWidth, brickHeight);
                }
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            g.setColor(Color.BLUE);
            g.fillOval(ballX, ballY, ballSize, ballSize);

            for (int row = 0; row < numRows; row++) {
                for (int col = 0; col < numCols; col++) {
                    bricks[row][col].draw(g);
                }
            }

            g.setColor(Color.BLACK);
            g.fillRect(paddleX, getHeight() - paddleHeight - 10, paddleWidth, paddleHeight);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            ballX += ballSpeedX;
            ballY += ballSpeedY;

            if (ballX <= 0 || ballX >= getWidth() - ballSize) {
                ballSpeedX = -ballSpeedX;
            }

            if (ballY <= 0) {
                ballSpeedY = -ballSpeedY;
            } else if (ballY >= getHeight() - ballSize) {
                gameOver();
                return;
            }

            if (moveLeft) {
                paddleX -= 5;
                if (paddleX < 0) {
                    paddleX = 0;
                }
            }

            if (moveRight) {
                paddleX += 5;
                if (paddleX > getWidth() - paddleWidth) {
                    paddleX = getWidth() - paddleWidth;
                }
            }

            ballCollisions();

            if (allBricksRemoved()) {
                gameWon();
            }

            repaint();
        }

        private void ballCollisions() {
            Rectangle ballBounds = new Rectangle(ballX, ballY, ballSize, ballSize);
            Rectangle paddleBounds = new Rectangle(paddleX, getHeight() - paddleHeight - 10, paddleWidth, paddleHeight);

            if (ballBounds.intersects(paddleBounds)) {
                ballSpeedY = -ballSpeedY;
            }

            for (int row = 0; row < numRows; row++) {
                for (int col = 0; col < numCols; col++) {
                    Brick brick = bricks[row][col];
                    if (brick.isVisible && ballBounds.intersects(brick.getBounds())) {
                        brick.isVisible = false;
                        ballSpeedY = -ballSpeedY;
                    }
                }
            }
        }

        private boolean allBricksRemoved() {
            for (int row = 0; row < numRows; row++) {
                for (int col = 0; col < numCols; col++) {
                    if (bricks[row][col].isVisible) {
                        return false;
                    }
                }
            }
            return true;
        }

        private void gameWon() {
            int result = JOptionPane.showOptionDialog(this,
                    "축하합니다! 클리어했습니다! 다시 한번 플레이하시겠습니까?",
                    "Game Won",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null, null, null);

            if (result == JOptionPane.YES_OPTION) {
                resetGame();
            } else {
                System.exit(0);
            }
        }

        private void gameOver() {
            int result = JOptionPane.showOptionDialog(this,
                    "게임 종료! 다시 시작하시겠습니까?",
                    "Game Over",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null, null, null);

            if (result == JOptionPane.YES_OPTION) {
                resetGame();
            } else {
                System.exit(0);
            }
        }

        private void resetGame() {
            ballX = 350;
            ballY = 300;
            ballSpeedX = 4;
            ballSpeedY = 4;
            paddleX = 350;
            initBricks();
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            if (key == KeyEvent.VK_LEFT) {
                moveLeft = true;
            }

            if (key == KeyEvent.VK_RIGHT) {
                moveRight = true;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            int key = e.getKeyCode();

            if (key == KeyEvent.VK_LEFT) {
                moveLeft = false;
            }

            if (key == KeyEvent.VK_RIGHT) {
                moveRight = false;
            }
        }
    }

    class Brick {
        int x, y, width, height;
        boolean isVisible;

        public Brick(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            isVisible = true;
        }

        public void draw(Graphics g) {
            if (isVisible) {
                g.setColor(Color.RED);
                g.fillRect(x, y, width, height);
            }
        }

        public Rectangle getBounds() {
            return new Rectangle(x, y, width, height);
        }
    }
}

