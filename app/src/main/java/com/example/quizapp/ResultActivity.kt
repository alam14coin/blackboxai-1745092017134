package com.example.quizapp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val score = intent.getIntExtra("score", 0)
        val total = intent.getIntExtra("total", 0)
        val quizNumber = intent.getStringExtra("quizNumber") ?: "1"

        val resultTextView = findViewById<TextView>(R.id.resultTextView)
        resultTextView.text = "You scored $score out of $total"

        // Save result to database
        val dbHelper = QuizDbHelper(this)
        dbHelper.saveQuizResult(quizNumber, score)
    }
}
