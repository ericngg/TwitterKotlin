package com.codepath.apps.restclienttemplate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import org.json.JSONException
import kotlin.properties.Delegates

class TimelineActivity : AppCompatActivity() {

    lateinit var client: TwitterClient

    lateinit var rvTweets: RecyclerView
    lateinit var adapter: TweetsAdapter
    lateinit var srlContainer: SwipeRefreshLayout

    lateinit var scrollListener: EndlessRecyclerViewScrollListener

    var lowestMaxId: Long = 0
    val tweets = ArrayList<Tweet>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline)

        client = TwitterApplication.getRestClient(this)

        rvTweets = findViewById(R.id.rvTweets)
        srlContainer = findViewById(R.id.srlContainer)
        adapter = TweetsAdapter(tweets)
        var linearLayoutManager = LinearLayoutManager(this)
        rvTweets.layoutManager = linearLayoutManager
        rvTweets.adapter = adapter

        // Endless Scroll Listener
        scrollListener = object : EndlessRecyclerViewScrollListener(linearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadMoreData()
            }
        }

        rvTweets.addOnScrollListener(scrollListener)

        // Swipe Refresh Layout
        srlContainer.setOnRefreshListener {
            // Your code to refresh the list here.
            // Make sure you call swipeContainer.setRefreshing(false)
            // once the network request has completed successfully.
            fetchTimelineAsync()
        }

        // Configure the refreshing colors for srlContainer
        srlContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light)



        populateHomeTimeline()
    }

    private fun loadMoreData() {
        client.populateHomeTimeline2(lowestMaxId, object: JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JsonHttpResponseHandler.JSON) {
                Log.i(TAG, "Success! Another 25")
                val jsonArray = json.jsonArray

                try {
                    val retrievedTweets = Tweet.fromJsonArray(jsonArray)
                    tweets.addAll(retrievedTweets)
                    adapter.notifyDataSetChanged()
                    lowestMaxId = tweets[tweets.size - 1].uid

                } catch (e: JSONException) {
                    Log.e(TAG, "JSONException $e")
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers,
                response: String,
                throwable: Throwable
            ) {
                Log.d(TAG, "Fetch timeline error", throwable)
            }
        })
    }

    private fun fetchTimelineAsync() {
        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP
        // getHomeTimeline is an example endpoint.
        client.populateHomeTimeline(object: JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                try {
                    // Remember to CLEAR OUT old items before appending in the new ones
                    adapter.clear()
                    // ...the data has come back, add new items to your adapter...
                    val jsonArray = json.jsonArray

                    val retrievedTweets = Tweet.fromJsonArray(jsonArray)
                    tweets.addAll(retrievedTweets)
                    adapter.notifyDataSetChanged()
                    lowestMaxId = tweets[tweets.size - 1].uid

                    // Now we call setRefreshing(false) to signal refresh has finished
                    srlContainer.setRefreshing(false)
                } catch (e: JSONException) {
                    Log.e(TAG, "JSON Exception $e")
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers,
                response: String,
                throwable: Throwable
            ) {
                Log.d(TAG, "Fetch timeline error", throwable)
            }
        })
    }

    private fun populateHomeTimeline() {
        client.populateHomeTimeline(object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                Log.i(TAG, "onSuccess!")

                try {
                    val jsonArray = json.jsonArray

                    val retrievedTweets = Tweet.fromJsonArray(jsonArray)
                    tweets.addAll(retrievedTweets)
                    adapter.notifyDataSetChanged()
                    lowestMaxId = tweets[tweets.size - 1].uid

                } catch (e: JSONException) {
                    Log.e(TAG, "JSON Exception $e")
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String?,
                throwable: Throwable?
            ) {
                Log.e(TAG, "onFailure", throwable)
            }

        })
    }

    companion object {
        const val TAG = "TimelineActivity"
    }
}