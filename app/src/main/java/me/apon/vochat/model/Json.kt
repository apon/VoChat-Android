package me.apon.vochat.model

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonReader
import com.squareup.moshi.Moshi

/**
 * Created by yaopeng(aponone@gmail.com) on 2019/3/9.
 */
class Json {

    companion object {
        val M: Json by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            Json()
        }
    }

    object NullStringAdapter {
        @FromJson
        fun fromJson(reader: JsonReader): String {
            if (reader.peek() != JsonReader.Token.NULL) {
                return reader.nextString()
            }
            reader.nextNull<Unit>()
            return ""
        }
    }

    object NullLongAdapter {
        @FromJson
        fun fromJson(reader: JsonReader): Long {
            if (reader.peek() != JsonReader.Token.NULL) {
                return reader.nextLong()
            }
            reader.nextNull<Unit>()
            return 0
        }
    }

    object NullIntAdapter {
        @FromJson
        fun fromJson(reader: JsonReader): Int {
            if (reader.peek() != JsonReader.Token.NULL) {
                return reader.nextInt()
            }
            reader.nextNull<Unit>()
            return 0
        }
    }

    object NullDoubleAdapter {
        @FromJson
        fun fromJson(reader: JsonReader): Double {
            if (reader.peek() != JsonReader.Token.NULL) {
                return reader.nextDouble()
            }
            reader.nextNull<Unit>()
            return 0.0
        }
    }

    object NullBooleanAdapter {
        @FromJson
        fun fromJson(reader: JsonReader): Boolean {
            if (reader.peek() != JsonReader.Token.NULL) {
                return reader.nextBoolean()
            }
            reader.nextNull<Unit>()
            return false
        }
    }

    var moshi: Moshi = Moshi.Builder()
        .add(NullStringAdapter)
        .add(NullLongAdapter)
        .add(NullIntAdapter)
        .add(NullDoubleAdapter)
        .add(NullBooleanAdapter)
        .build()

    inline fun <reified T> foJson(json: String): T? {
        val adapter = moshi.adapter<T>(T::class.java)
        return adapter.fromJson(json)
    }

    inline fun <reified T> toJson(t: T): String? {
        val adapter = moshi.adapter<T>(T::class.java)
        return adapter.toJson(t)
    }
}