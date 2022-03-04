package com.codepath.apps.restclienttemplate.models

import org.json.JSONArray
import org.json.JSONObject

class Tweet {
    var body: String = ""
    var createdAt: String = ""
    var user: User? = null
    var mediaUrl: String = ""
    var liked: Boolean = false
    var uid: Long = 0

    companion object {
        fun fromJson(jsonObject: JSONObject): Tweet {
            val tweet = Tweet()
            tweet.body = jsonObject.getString("text")
            tweet.createdAt = jsonObject.getString("created_at")
            tweet.user = User.fromJson(jsonObject.getJSONObject("user"))
            tweet.liked = jsonObject.getBoolean("favorited")
            tweet.uid = jsonObject.getLong("id")

            val entities = jsonObject.getJSONObject("entities")

            if (entities.has("media")) {
                tweet.mediaUrl =
                    entities.getJSONArray("media").getJSONObject(0).getString("media_url_https")
            } else {
                tweet.mediaUrl = ""
            }

            return tweet
        }

        fun fromJsonArray(jsonArray: JSONArray): List<Tweet> {
            val tweets = ArrayList<Tweet>()
            for (i in 0 until jsonArray.length()) {
                tweets.add(fromJson(jsonArray.getJSONObject(i)))
            }

            return tweets
        }
    }
}