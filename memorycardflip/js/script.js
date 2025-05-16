// Game state
const state = {
    cards: [],
    flippedCards: [],
    matchedPairs: 0,
    moves: 0,
    timer: 0,
    timerInterval: null,
    isProcessing: false
};

// Card emojis
const cardEmojis = ['🍕', '🐶', '🚗', '🌈', '🎵', '⚽', '🐱', '🎮'];

// Initialize game
function startGame() {
    const introScreen = document.getElementById('introScreen');
    const gameArea = document.getElementById('gameArea');
    
    handleTransition(introScreen, gameArea);
    resetGame();
}

// Reset game state
function resetGame() {
    // Reset state
    state.cards = [...cardEmojis, ...cardEmojis];
    state.flippedCards = [];
    state.matchedPairs = 0;
    state.moves = 0;
    state.timer = 0;
    state.isProcessing = false;

    // Clear timer if exists
    if (state.timerInterval) clearInterval(state.timerInterval);

    // Shuffle cards
    shuffleArray(state.cards);

    // Reset UI
    updateStats('moves', 0);
    updateStats('timer', 0);

    // Create board
    createBoard();

    // Start timer
    startTimer();
}

// Create game board
function createBoard() {
    const board = document.getElementById('gameBoard');
    board.innerHTML = '';

    // Reset animations
    resetCardAnimations();

    state.cards.forEach((emoji, index) => {
        const card = document.createElement('div');
        card.className = 'card';
        card.dataset.index = index;
        card.dataset.emoji = emoji;
        card.innerHTML = `
            <div class="card-inner">
                <div class="card-front">❓</div>
                <div class="card-back">${emoji}</div>
            </div>`;
        card.style.opacity = '0';
        card.addEventListener('click', () => handleCardClick(index));
        board.appendChild(card);
    });

    // Start entrance animation
    handleCardAnimations();
}

// Handle card click
function handleCardClick(index) {
    if (state.isProcessing || 
        state.flippedCards.includes(index) || 
        document.querySelector(`[data-index="${index}"]`).classList.contains('matched')) {
        return;
    }

    const card = document.querySelector(`[data-index="${index}"]`);    flipCard(card, index);

    if (state.flippedCards.length === 2) {
        state.isProcessing = true;
        updateMoves();
        checkForMatch();
    }
}

// Flip card
function flipCard(card, index) {
    card.classList.add('flipped');
    // Using card-inner structure with large emoji
    card.innerHTML = `
        <div class="card-inner">
            <div class="card-front">❓</div>
            <div class="card-back">${state.cards[index]}</div>
        </div>`;
    state.flippedCards.push(index);
    playSound('flip');
}

// Check for match
function checkForMatch() {
    const [index1, index2] = state.flippedCards;
    const card1 = document.querySelector(`[data-index="${index1}"]`);
    const card2 = document.querySelector(`[data-index="${index2}"]`);

    if (state.cards[index1] === state.cards[index2]) {
        // Match found
        card1.classList.add('matched');
        card2.classList.add('matched');
        state.matchedPairs++;
        playSound('match');

        if (state.matchedPairs === cardEmojis.length) {
            handleWin();
        }
    } else {
        // No match
        setTimeout(() => {
            card1.classList.remove('flipped');
            card2.classList.remove('flipped');
            card1.innerHTML = `
                <div class="card-inner">
                    <div class="card-front">❓</div>
                    <div class="card-back">${state.cards[index1]}</div>
                </div>`;
            card2.innerHTML = `
                <div class="card-inner">
                    <div class="card-front">❓</div>
                    <div class="card-back">${state.cards[index2]}</div>
                </div>`;
            playSound('nomatch');
        }, 1000);
    }

    // Reset for next turn
    setTimeout(() => {
        state.flippedCards = [];
        state.isProcessing = false;
    }, 1000);
}

// Start timer
function startTimer() {
    state.timerInterval = setInterval(() => {
        updateTimer();
    }, 1000);
}

// Handle win
function handleWin() {
    clearInterval(state.timerInterval);
    playSound('win');
    
    const modal = document.getElementById('winModal');
    document.getElementById('finalTime').textContent = state.timer;
    document.getElementById('finalMoves').textContent = state.moves;
    
    // Show modal with animation
    modal.style.display = 'flex';
    setTimeout(() => {
        modal.classList.add('visible');
    }, 50);
    
    // Handle button clicks
    document.getElementById('playAgainButton').onclick = () => {
        modal.classList.remove('visible');
        setTimeout(() => {
            modal.style.display = 'none';
            resetGame();
        }, 300);
    };
    
    document.getElementById('homeButton').onclick = () => {
        modal.classList.remove('visible');
        setTimeout(() => {
            modal.style.display = 'none';
            location.reload();
        }, 300);
    };
}

// Shuffle array with Fisher-Yates algorithm
function shuffleArray(array) {
    for (let i = array.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [array[i], array[j]] = [array[j], array[i]];
    }
    return array;
}

// Handle card animations
function handleCardAnimations() {
    const cards = document.querySelectorAll('.card');
    cards.forEach((card, index) => {
        setTimeout(() => {
            card.style.transform = 'rotateY(360deg)';
            card.style.opacity = '1';
        }, index * 100);
    });
}

// Reset card animations
function resetCardAnimations() {
    const cards = document.querySelectorAll('.card');
    cards.forEach(card => {
        card.style.transform = 'rotateY(0deg)';
        card.style.opacity = '0';
    });
}

// Play sound effect
function playSound(type) {
    const sound = new Audio(`sounds/${type}.wav`);
    sound.volume = 0.3;
    sound.play().catch(err => console.log('Sound playback failed:', err));
}

// Update stats with animation
function updateStats(type, value) {
    const element = document.getElementById(type);
    element.textContent = value;
    element.classList.add('updated');
    setTimeout(() => element.classList.remove('updated'), 300);
}

// Improve smooth transitions
function handleTransition(from, to) {
    from.classList.add('fade-out');
    setTimeout(() => {
        from.style.display = 'none';
        to.style.display = 'flex';
        setTimeout(() => {
            to.classList.add('visible');
        }, 50);
    }, 500);
}

// Update the move counter
function updateMoves() {
    state.moves++;
    updateStats('moves', state.moves);
}

// Update the timer display
function updateTimer() {
    state.timer++;
    updateStats('timer', state.timer);
}

// Initialize when document loads
document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('introScreen').style.display = 'block';
    document.getElementById('gameArea').style.display = 'none';
    
    // Event Listeners
    document.getElementById('startButton').addEventListener('click', startGame);
});