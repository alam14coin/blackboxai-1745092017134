package com.example.quizapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.database.Cursor

class QuizDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "quiz.db"
        private const val DATABASE_VERSION = 2

        private const val TABLE_QUESTIONS = "questions"
        private const val COLUMN_ID = "id"
        private const val COLUMN_QUIZ_NUMBER = "quiz_number"
        private const val COLUMN_QUESTION_TEXT = "question_text"
        private const val COLUMN_ANSWER1 = "answer1"
        private const val COLUMN_ANSWER2 = "answer2"
        private const val COLUMN_ANSWER3 = "answer3"
        private const val COLUMN_ANSWER4 = "answer4"
        private const val COLUMN_CORRECT_ANSWER_INDEX = "correct_answer_index"

        private const val TABLE_RESULTS = "results"
        private const val COLUMN_RESULT_ID = "result_id"
        private const val COLUMN_RESULT_QUIZ_NUMBER = "quiz_number"
        private const val COLUMN_SCORE = "score"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = ("CREATE TABLE $TABLE_QUESTIONS ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_QUIZ_NUMBER TEXT, "
                + "$COLUMN_QUESTION_TEXT TEXT, "
                + "$COLUMN_ANSWER1 TEXT, "
                + "$COLUMN_ANSWER2 TEXT, "
                + "$COLUMN_ANSWER3 TEXT, "
                + "$COLUMN_ANSWER4 TEXT, "
                + "$COLUMN_CORRECT_ANSWER_INDEX INTEGER)")
        db.execSQL(createTable)

        val createResultsTable = ("CREATE TABLE $TABLE_RESULTS ("
                + "$COLUMN_RESULT_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_RESULT_QUIZ_NUMBER TEXT UNIQUE, "
                + "$COLUMN_SCORE INTEGER)")
        db.execSQL(createResultsTable)

        // Insert sample questions for quiz 1
        insertQuestion(db, "1", "What is the capital of France?", listOf("Paris", "London", "Berlin", "Madrid"), 0)
        insertQuestion(db, "1", "What is 2 + 2?", listOf("3", "4", "5", "6"), 1)
        insertQuestion(db, "1", "Which planet is known as the Red Planet?", listOf("Earth", "Mars", "Jupiter", "Saturn"), 1)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            val createResultsTable = ("CREATE TABLE $TABLE_RESULTS ("
                    + "$COLUMN_RESULT_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "$COLUMN_RESULT_QUIZ_NUMBER TEXT UNIQUE, "
                    + "$COLUMN_SCORE INTEGER)")
            db.execSQL(createResultsTable)
        }
        onCreate(db)
    }

    private fun insertQuestion(db: SQLiteDatabase, quizNumber: String, questionText: String, answers: List<String>, correctAnswerIndex: Int) {
        val values = ContentValues()
        values.put(COLUMN_QUIZ_NUMBER, quizNumber)
        values.put(COLUMN_QUESTION_TEXT, questionText)
        values.put(COLUMN_ANSWER1, answers[0])
        values.put(COLUMN_ANSWER2, answers[1])
        values.put(COLUMN_ANSWER3, answers[2])
        values.put(COLUMN_ANSWER4, answers[3])
        values.put(COLUMN_CORRECT_ANSWER_INDEX, correctAnswerIndex)
        db.insert(TABLE_QUESTIONS, null, values)
    }

    fun getQuestionsForQuiz(quizNumber: String): List<Question> {
        val questions = mutableListOf<Question>()
        val db = readableDatabase
        val cursor: Cursor = db.query(
            TABLE_QUESTIONS,
            null,
            "$COLUMN_QUIZ_NUMBER = ?",
            arrayOf(quizNumber),
            null,
            null,
            null
        )
        if (cursor.moveToFirst()) {
            do {
                val questionText = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_QUESTION_TEXT))
                val answers = listOf(
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ANSWER1)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ANSWER2)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ANSWER3)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ANSWER4))
                )
                val correctAnswerIndex = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CORRECT_ANSWER_INDEX))
                questions.add(Question(questionText, answers, correctAnswerIndex))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return questions
    }

    fun saveQuizResult(quizNumber: String, score: Int) {
        val db = writableDatabase
        val values = ContentValues()
        values.put(COLUMN_RESULT_QUIZ_NUMBER, quizNumber)
        values.put(COLUMN_SCORE, score)
        val rowsUpdated = db.update(TABLE_RESULTS, values, "$COLUMN_RESULT_QUIZ_NUMBER = ?", arrayOf(quizNumber))
        if (rowsUpdated == 0) {
            db.insert(TABLE_RESULTS, null, values)
        }
    }

    fun getQuizResult(quizNumber: String): Int? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_RESULTS,
            arrayOf(COLUMN_SCORE),
            "$COLUMN_RESULT_QUIZ_NUMBER = ?",
            arrayOf(quizNumber),
            null,
            null,
            null
        )
        var score: Int? = null
        if (cursor.moveToFirst()) {
            score = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCORE))
        }
        cursor.close()
        return score
    }
}
