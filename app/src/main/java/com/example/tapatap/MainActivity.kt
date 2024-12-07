package com.example.tapatap

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var tapButton: Button
    private lateinit var countdownText: TextView
    private lateinit var tapthejoker: TextView
    private lateinit var timerText: TextView
    private lateinit var tapCountText: TextView
    private lateinit var highScoreText: TextView

    private var tapCount = 0
    private var highScore = 0
    private var countdownValue = 3
    private var gameTime = 15
    private var isGameRunning = false
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Views
        tapButton = findViewById(R.id.tapButton)
        countdownText = findViewById(R.id.countdownText)
        timerText = findViewById(R.id.timerText)
        tapCountText = findViewById(R.id.tapCount)
        highScoreText = findViewById(R.id.highScoreText)
        tapthejoker= findViewById(R.id.tapthejoker)

        // Handle tap anywhere
        findViewById<View>(R.id.gameLayout).setOnClickListener {
            if (isGameRunning) {
                tapCount += 1 // Add 1 point for tapping anywhere
                updateScore()
            }
        }

        countdownText.visibility = View.INVISIBLE

        tapButton.setOnClickListener {
            if (!isGameRunning) {
                startGame()
            }
        }
    }

    private fun startGame() {
        isGameRunning = true
        tapButton.text = "Tap"
        countdownValue = 3
        tapCount = 0
        gameTime = 15
        updateScore()

        // Hide buttons and texts to give full screen to the user
        hideUI()

        countdownText.visibility = View.VISIBLE
        startCountdown()
    }

    private fun hideUI() {
        // Hide buttons and texts to give full screen to the user
        countdownText.visibility = View.INVISIBLE
        tapButton.visibility = View.INVISIBLE
        tapCountText.visibility = View.INVISIBLE
        highScoreText.visibility = View.INVISIBLE
        timerText.visibility = View.INVISIBLE
        tapthejoker.visibility = View.INVISIBLE
    }

    private fun showUI() {
        // Show buttons and texts after the game is over

        tapButton.visibility = View.VISIBLE
        tapCountText.visibility = View.VISIBLE
        highScoreText.visibility = View.VISIBLE
        timerText.visibility = View.VISIBLE
        tapthejoker.visibility = View.VISIBLE
    }

    private fun startCountdown() {
        countdownText.text = "Starting in $countdownValue..."
        handler.postDelayed(object : Runnable {
            override fun run() {
                countdownValue--
                if (countdownValue >= 0) {
                    countdownText.text = "Starting in $countdownValue..."
                    handler.postDelayed(this, 1000)
                } else {
                    countdownText.text = "Go!"
                    countdownText.visibility = View.INVISIBLE
                    startGameTimer()
                    showRandomBalls()
                }
            }
        }, 1000)
    }

    private fun startGameTimer() {
        val gameTimer = object : Runnable {
            override fun run() {
                if (gameTime > 0) {
                    gameTime--
                    timerText.text = "Time: ${gameTime}s"
                    handler.postDelayed(this, 1000)
                } else {
                    endGame()
                }
            }
        }
        handler.post(gameTimer)
    }

    private fun showRandomBalls() {
        if (gameTime > 0) {
            val ball = ImageView(this)

            // Randomly choose an image for the ball
            val ballImages = listOf(R.drawable.jk, R.drawable.rajumistri, R.drawable.srt)
            val ballScores = listOf(2, 3, 5) // Scores corresponding to each ball image
            val ballSizes = listOf(150, 250, 700) // Different sizes for balls


            val randomIndex = ballImages.indices.random()
            ball.setImageResource(ballImages[randomIndex])

            val layout = findViewById<FrameLayout>(R.id.gameLayout)

            layout.post {
                val randomSize = ballSizes[randomIndex]
                val randomX = (Math.random() * (layout.width - randomSize)).toInt()
                val randomY = (Math.random() * (layout.height - randomSize)).toInt()

                val ballParams = FrameLayout.LayoutParams(randomSize, randomSize)
                ballParams.leftMargin = randomX
                ballParams.topMargin = randomY
                ball.layoutParams = ballParams

                layout.addView(ball)

                ball.setOnClickListener {
                    // Add the ball score
                    val scoreToAdd = ballScores[randomIndex]
                    tapCount += scoreToAdd
                    updateScore()
                    layout.removeView(ball)

                    // Show score pop-up at ball position
                    showScorePopup("+$scoreToAdd", randomX, randomY)
                }

                // Remove the ball after a delay if not clicked
                handler.postDelayed({
                    layout.removeView(ball)
                    if (gameTime > 0) {
                        showRandomBalls()
                    }
                }, (1000..2000).random().toLong()) // Random delay between 1-2 seconds
            }

            // Handle tap on empty screen
            layout.setOnClickListener { event ->
                val x = event.x.toInt()
                val y = event.y.toInt()

                // Add 1 to score for empty screen tap
                tapCount += 1
                updateScore()

                // Show +1 pop-up
                showScorePopup("+1", x, y)
            }
        }
    }

    private fun showScorePopup(text: String, x: Int, y: Int) {
        val layout = findViewById<FrameLayout>(R.id.gameLayout)
        val scoreText = TextView(this)
        scoreText.text = text
        scoreText.setTextColor(Color.WHITE)
        scoreText.textSize = 18f
        scoreText.setTypeface(null, Typeface.BOLD)

        val textParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        textParams.leftMargin = x
        textParams.topMargin = y
        scoreText.layoutParams = textParams

        layout.addView(scoreText)

        // Remove score text after 1 second
        handler.postDelayed({
            layout.removeView(scoreText)
        }, 1000)
    }





    private fun updateScore() {
        tapCountText.text = "Score: $tapCount"
        if (tapCount > highScore) {
            highScore = tapCount
            highScoreText.text = "High Score: $highScore"
        }
    }

    private fun endGame() {
        isGameRunning = false
        tapButton.text = "Start"
        countdownText.text = "Game Over"

        // Show the score and the buttons again after the game ends
        showUI()
    }
}
