package com.codepath.apps.restclienttemplate

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codepath.apps.restclienttemplate.models.Tweet
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TweetsAdapter(val tweets: ArrayList<Tweet>) : RecyclerView.Adapter<TweetsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TweetsAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_tweet, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: TweetsAdapter.ViewHolder, position: Int) {
        val tweet: Tweet = tweets.get(position)

        holder.bind(tweet)
    }

    override fun getItemCount(): Int {
        return tweets.size
    }

    // Clean all elements of the recycler
    fun clear() {
        tweets.clear()
        notifyDataSetChanged()
    }

    // Add a list of items -- change to type used
    fun addAll(tweetList: List<Tweet>) {
        tweets.addAll(tweetList)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivProfileImage = itemView.findViewById<ImageView>(R.id.ivProfileImage)
        val tvScreenName = itemView.findViewById<TextView>(R.id.tvScreenName)
        val tvBody = itemView.findViewById<TextView>(R.id.tvBody)
        val tvCreatedAt = itemView.findViewById<TextView>(R.id.tvCreatedAt)
        val ivMediaImage = itemView.findViewById<ImageView>(R.id.ivMediaImage)

        fun bind(tweet: Tweet) {
            tvScreenName.text = tweet.user?.name
            tvBody.text = tweet.body
            tvCreatedAt.text = getRelativeTimeAgo(tweet.createdAt)

            Glide.with(itemView).load(tweet.user?.publicImageUrl).into(ivProfileImage)

            if (tweet.mediaUrl != "") {
                Glide.with(itemView).load(tweet.mediaUrl).into(ivMediaImage)
                ivMediaImage.setVisibility(View.VISIBLE)
            } else {
                ivMediaImage.setVisibility(View.GONE)
            }
        }

        // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
        fun getRelativeTimeAgo(rawJsonDate: String?): String? {
            val twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy"
            val sf = SimpleDateFormat(twitterFormat, Locale.ENGLISH)
            sf.isLenient = true
            var relativeDate = ""
            try {
                val dateMillis = sf.parse(rawJsonDate).time
                relativeDate = DateUtils.getRelativeTimeSpanString(
                    dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS
                ).toString()
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return relativeDate
        }
    }

}