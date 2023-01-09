package com.commonsware.todo_3.repo

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class ToDoRemoteDataSource(private val ok: OkHttpClient) {
    private val moshi = Moshi.Builder().add(MoshiInstantAdapter()).build()
    private val adapter: JsonAdapter<List<ToDoServerItem>> = moshi.adapter(
        Types.newParameterizedType(
            List::class.java,
            ToDoServerItem::class.java
        )
    )

    suspend fun load(url: String) = withContext(Dispatchers.IO) {
        /**
            • Wrap the url in an OkHttp Request object
            (Request.Builder().url(url).build())
            • Tell OkHttp to create a Call object representing our request (newCall())
            • Execute the HTTP request on the current thread (execute()) (p.530)
         * */
        val response = ok.newCall(Request.Builder().url(url).build()).execute()

        if (response.isSuccessful) {
            response.body?.let { adapter.fromJson(it.source()) }
                ?: throw IOException("No response body: $response")
        } else {
            throw IOException("Unexpected HTTP response code: ${response.code}")
        }
    }
}
