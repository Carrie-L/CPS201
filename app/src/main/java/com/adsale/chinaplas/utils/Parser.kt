package com.adsale.chinaplas.utils

import com.adsale.chinaplas.data.entity.Person
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonReader

/**
 * Created by Carrie on 2019/10/17.
 */
//fun  parseJsonFilesDirFile(clazz: Class<T>, fileName: String){
//
//}

//fun <T> parseJsonFilesDirFile(clz: Class<T>, fileName: String): T {
//    LogUtil.i("Parser", "parseJsonFilesDirFile: $fileName")
//    return parseJson(T, FileUtil.readFilesDirFile(fileName))
//}
//
//fun <T> parseJson(clz: Class<T>, json: String): T {
//    return Gson().fromJson(json, T)
//}

class Parser {
    fun parse(reader: JsonReader): List<Person> {
        val result = mutableListOf<Person>()

        reader.beginArray()
        while (reader.hasNext()) {
            var id: Long = -1L
            var name: String = ""
            var age: Int = -1

            reader.beginObject()
            while (reader.hasNext()) {
                when (reader.nextName()) {
                    "id" -> id = reader.nextLong()
                    "name" -> name = reader.nextString()
                    "age" -> age = reader.nextInt()
                    else -> reader.skipValue()
                }
            }
            reader.endObject()

            if (id == -1L || name == "") {
                throw JsonDataException("Missing required field")
            }
            val person = Person(id, name, age)
            result.add(person)
        }
        reader.endArray()

        return result
    }
}