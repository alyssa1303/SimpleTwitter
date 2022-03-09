package com.codepath.apps.restclienttemplate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import org.json.JSONException

private const val TAG = "TimelineActivity"
class TimelineActivity : AppCompatActivity() {

    lateinit var client: TwitterClient
    lateinit var rvTweets: RecyclerView
    lateinit var adapter: TweetsAdapter
    lateinit var swipeContainer: SwipeRefreshLayout
    val tweets = ArrayList<Tweet>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline)

        client = TwitterApplication.getRestClient(this)

        // Lookup the swipe container view
        swipeContainer = findViewById(R.id.swipeContainer)

        swipeContainer.setOnRefreshListener {
            Log.i(TAG, "Refreshing timeline")
            getHomeTimeline()
        }

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )

        rvTweets = findViewById(R.id.rvTweets)
        adapter = TweetsAdapter(tweets)

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvTweets.layoutManager = layoutManager
        rvTweets.adapter = adapter
        rvTweets.addItemDecoration(
            DividerItemDecoration(
                baseContext,
                layoutManager.orientation
            )
        )

        getHomeTimeline()
    }

    fun getHomeTimeline() {
        client.populateHomeTimeline(object: JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {
                Log.i(TAG, "onSuccess")
                try {
                    // Clear out currently fetched tweets
                    adapter.clear()

                    val jsonArray = json.jsonArray
                    tweets.addAll(Tweet.fromJsonArray(jsonArray))
                    adapter.notifyDataSetChanged()

                    // Now we call setRefreshing(false) to signal refresh has finished
                    swipeContainer.setRefreshing(false)
                } catch (e: JSONException) {
                    Log.e(TAG, "JSON Exception $e")
                }
            }

            override fun onFailure(statusCode: Int, headers: Headers?, response: String?, throwable: Throwable?
            ) {
                Log.e(TAG, "onFailure $statusCode")
            }
        })
    }
}