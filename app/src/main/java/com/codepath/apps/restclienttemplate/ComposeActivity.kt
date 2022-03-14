package com.codepath.apps.restclienttemplate

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

private const val TAG = "ComposeActivity"

class ComposeActivity : AppCompatActivity() {
    private lateinit var etCompose: EditText
    private lateinit var btnTweet: Button
    private lateinit var tvCount: TextView
    lateinit var client: TwitterClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)

        etCompose = findViewById(R.id.composeTweet)
        btnTweet = findViewById(R.id.btnTweet)
        tvCount = findViewById(R.id.tvCharCount)

        client = TwitterApplication.getRestClient(this)

        val limit = 280
        etCompose.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // Fires right as the text is being changed (even supplies the range of text)

                if (s.length > limit) {
                    tvCount.setTextColor(Color.RED)
                    btnTweet.isEnabled = false
                } else {
                    tvCount.setTextColor(Color.GRAY)
                    btnTweet.isEnabled = true
                }
                tvCount.setText((limit - s.length).toString())
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Fires right before text is changing
            }

            override fun afterTextChanged(s: Editable) {
                // Fires right after the text has changed
            }
        })

        // Handling the user's click on the tweet button
        btnTweet.setOnClickListener{
            // Get the content of edit text
            val tweetContent = etCompose.text.toString()

            // 1. Make sure tweet is not empty
            if (tweetContent.isEmpty()) {
                Toast.makeText(this, "Empty tweet not allowed!", Toast.LENGTH_SHORT).show()
            }
            // 2. Make sure tweet is less than character count
            else if (tweetContent.length > limit) {
                Toast.makeText(this, "Tweet exceeds limit of 280 characters", Toast.LENGTH_SHORT).show()
            } else {
                client.postTweet(object: JsonHttpResponseHandler() {
                    override fun onFailure(statusCode: Int, headers: Headers?, response: String?, throwable: Throwable?
                    ) {
                        Log.e(TAG, "Unable to post tweet $statusCode", throwable)

                    }

                    override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {
                        Log.i(TAG, "Tweet posted")

                        val tweet = Tweet.fromJson(json.jsonObject)
                        val intent = Intent()
                        intent.putExtra("tweet", tweet)
                        setResult(RESULT_OK, intent)
                        finish()
                    }

                }, tweetContent)
            }
        }
    }
}