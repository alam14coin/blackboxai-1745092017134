package com.example.quizapp

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class QuizActivity : AppCompatActivity() {

    private lateinit var questionTextView: TextView
    private lateinit var answerButtons: List<Button>
    private lateinit var timerTextView: TextView

    private var currentQuestionIndex = 0
    private var score = 0
    private lateinit var quizNumber: String

    private lateinit var quizQuestions: List<Question>

    private lateinit var countDownTimer: CountDownTimer
    private val questionTimeMillis: Long = 15000 // 15 seconds per question

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        questionTextView = findViewById(R.id.questionTextView)
        timerTextView = findViewById(R.id.timerTextView)
        answerButtons = listOf(
            findViewById(R.id.answerButton1),
            findViewById(R.id.answerButton2),
            findViewById(R.id.answerButton3),
            findViewById(R.id.answerButton4)
        )

        quizNumber = intent.getIntExtra("quizNumber", 1).toString()

        // Load questions from database for the selected quiz
        val dbHelper = QuizDbHelper(this)
        quizQuestions = dbHelper.getQuestionsForQuiz(quizNumber)

        loadQuestion()

        for ((index, button) in answerButtons.withIndex()) {
            button.setOnClickListener {
                checkAnswer(index)
            }
        }
    }

    private fun loadQuestion() {
        if (currentQuestionIndex >= quizQuestions.size) {
            showResult()
            return
        }

        val question = quizQuestions[currentQuestionIndex]
        questionTextView.text = question.questionText
        for (i in answerButtons.indices) {
            answerButtons[i].text = question.answers[i]
        }

        startTimer()
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(questionTimeMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timerTextView.text = "Time: ${millisUntilFinished / 1000}s"
            }

            override fun onFinish() {
                currentQuestionIndex++
                loadQuestion()
            }
        }
        countDownTimer.start()
    }

    private fun checkAnswer(selectedIndex: Int) {
        countDownTimer.cancel()
        val correctIndex = quizQuestions[currentQuestionIndex].correctAnswerIndex
        if (selectedIndex == correctIndex) {
            score++
        }
        currentQuestionIndex++
        loadQuestion()
    }

    private fun showResult() {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra("score", score)
        intent.putExtra("total", quizQuestions.size)
        startActivity(intent)
        finish()
    }
}

data class Question(
    val questionText: String,
    val answers: List<String>,
    val correctAnswerIndex: Int
)
