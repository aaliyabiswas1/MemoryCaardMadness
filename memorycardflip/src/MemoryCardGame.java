import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class MemoryCardGame extends JFrame {    
    private static final int GRID_SIZE = 4;
    private static final int NUM_CARDS = GRID_SIZE * GRID_SIZE;
    private static final int CARD_SIZE = 100;  // Restored to original size
    private static final int FLIP_DELAY = 1000;
    
    private JPanel cardPanel;
    private JButton[] cards;
    private String[] cardValues;
    private boolean[] flipped;
    private int firstCardIndex;
    private int secondCardIndex;    
    private int moves;
    private int points;
    private boolean isProcessing;
    private javax.swing.Timer gameTimer;
    private int secondsElapsed;
    private JLabel timerLabel;
    private JLabel movesLabel;
    private JLabel pointsLabel;
    
    private static final String[] SYMBOLS = {
        "A", "B", "C", "D", "E", "F", "G", "H"
    };

    public MemoryCardGame() {
        setTitle("Memory Card Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Initialize game components
        initializeGame();
        
        // Create menu panel
        JPanel menuPanel = new JPanel();
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton restartButton = new JButton("Restart Game");
        restartButton.setFont(new Font("Arial", Font.PLAIN, 14));
        
        timerLabel = new JLabel("Time: 0s");
        timerLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        timerLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        
        movesLabel = new JLabel("Moves: 0");
        movesLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        pointsLabel = new JLabel("Points: 0");
        pointsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        pointsLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        
        menuPanel.add(restartButton);
        menuPanel.add(timerLabel);
        menuPanel.add(movesLabel);
        menuPanel.add(pointsLabel);
        
        // Layout setup
        setLayout(new BorderLayout());
        add(menuPanel, BorderLayout.NORTH);
        add(cardPanel, BorderLayout.CENTER);
        
        // Add restart button action
        restartButton.addActionListener(event -> restartGame());
        
        // Set up the game timer
        gameTimer = new javax.swing.Timer(1000, event -> {
            secondsElapsed++;
            timerLabel.setText("Time: " + secondsElapsed + "s");
        });
        
        // Window setup
        pack();
        setLocationRelativeTo(null);
    }
    
    private void initializeGame() {
        cardPanel = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE, 10, 10));
        cardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        cards = new JButton[NUM_CARDS];
        cardValues = new String[NUM_CARDS];
        flipped = new boolean[NUM_CARDS];
        
        firstCardIndex = -1;
        secondCardIndex = -1;
        moves = 0;
        points = 0;
        secondsElapsed = 0;
        isProcessing = false;
        
        // Initialize cards
        for (int i = 0; i < NUM_CARDS; i++) {
            final int index = i;
            cards[i] = createCard(index);
            cardPanel.add(cards[i]);
            cardValues[i] = SYMBOLS[i / 2];
        }
        
        // Shuffle cards
        shuffleCards();
    }
    
    private JButton createCard(int index) {
        JButton card = new JButton("?");
        card.setPreferredSize(new Dimension(CARD_SIZE, CARD_SIZE));
        card.setFont(new Font("Arial", Font.BOLD, 24));
        card.setBackground(new Color(220, 220, 220));
        card.setFocusPainted(false);
        
        // Add click handler
        card.addActionListener(event -> handleCardClick(index));
        
        return card;
    }
    
    private void handleCardClick(int index) {
        // Ignore clicks if processing or card is already flipped
        if (isProcessing || flipped[index]) {
            return;
        }
        
        // Start timer on first move
        if (moves == 0) {
            gameTimer.start();
        }
        
        // Flip card
        flipCard(index);
        
        // Process move
        if (firstCardIndex == -1) {
            firstCardIndex = index;
        } else {
            secondCardIndex = index;
            moves++;
            movesLabel.setText("Moves: " + moves);
            
            // Check for match
            if (cardValues[firstCardIndex].equals(cardValues[secondCardIndex])) {
                // Cards match
                points++;
                pointsLabel.setText("Points: " + points);
                firstCardIndex = -1;
                secondCardIndex = -1;
                
                // Check for win
                if (checkWin()) {
                    gameTimer.stop();
                    showWinDialog();
                }
            } else {
                // Cards don't match, flip them back after delay
                isProcessing = true;
                javax.swing.Timer flipBackTimer = new javax.swing.Timer(FLIP_DELAY, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        flipCard(firstCardIndex);
                        flipCard(secondCardIndex);
                        firstCardIndex = -1;
                        secondCardIndex = -1;
                        isProcessing = false;
                    }
                });
                flipBackTimer.setRepeats(false);
                flipBackTimer.start();
            }
        }
    }
    
    private void flipCard(int index) {
        JButton card = cards[index];
        if (flipped[index]) {
            // Flip back to hidden state
            card.setText("?");
            card.setBackground(new Color(220, 220, 220));
        } else {
            // Flip to show symbol
            card.setText(cardValues[index]);
            card.setBackground(Color.WHITE);
        }
        flipped[index] = !flipped[index];
    }
    
    private void shuffleCards() {
        Random random = new Random();
        for (int i = cardValues.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            String temp = cardValues[i];
            cardValues[i] = cardValues[j];
            cardValues[j] = temp;
        }
    }
    
    private boolean checkWin() {
        for (boolean isFlipped : flipped) {
            if (!isFlipped) return false;
        }
        return true;
    }
    
    private void showWinDialog() {
        String message = String.format(
            "Congratulations! You've won!\n\n" +
            "Points: %d\n" +
            "Moves: %d\n" +
            "Time: %d seconds\n\n" +
            "Would you like to play again?", 
            points, moves, secondsElapsed);
        
        int option = JOptionPane.showConfirmDialog(
            this, message, 
            "Victory!",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.PLAIN_MESSAGE);
            
        if (option == JOptionPane.YES_OPTION) {
            restartGame();
        }
    }
    
    private void restartGame() {
        // Reset game state
        gameTimer.stop();
        for (int i = 0; i < NUM_CARDS; i++) {
            if (flipped[i]) {
                flipCard(i);
            }
        }
        shuffleCards();
        moves = 0;
        points = 0;
        secondsElapsed = 0;
        firstCardIndex = -1;
        secondCardIndex = -1;
        isProcessing = false;
        timerLabel.setText("Time: 0s");
        movesLabel.setText("Moves: 0");
        pointsLabel.setText("Points: 0");
    }

    public static void main(String[] args) {
        // Set look and feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Start the game
        SwingUtilities.invokeLater(() -> {
            MemoryCardGame game = new MemoryCardGame();
            game.setVisible(true);
        });
    }
}
