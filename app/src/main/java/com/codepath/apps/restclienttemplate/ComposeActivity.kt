package com.codepath.apps.restclienttemplate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

private const val TAG = "ComposeActivity"

class ComposeActivity : AppCompatActivity() {
    private lateinit var etCompose: EditText
    private lateinit var btnTweet: Button
    lateinit var client: TwitterClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)

        etCompose = findViewById(R.id.composeTweet)
        btnTweet = findViewById(R.id.btnTweet)

        client = TwitterApplication.getRestClient(this)

        // Handling the user's click on the tweet button
        btnTweet.setOnClickListener{
            // Get the content of edit text
            val tweetContent = etCompose.text.toString()

            // 1. Make sure tweet is not empty
            if (tweetContent.isEmpty()) {
                Toast.makeText(this, "Empty tweet not allowed!", Toast.LENGTH_SHORT).show()
            }
            // 2. Make sure tweet is less than character count
            else if (tweetContent.length > 140) {
                Toast.makeText(this, "Tweet exceeds limit of 140 characters", Toast.LENGTH_SHORT).show()
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