package com.bluestacks.bugzy.data.local.db;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import com.bluestacks.bugzy.models.resp.CaseEvent;

import android.arch.persistence.room.TypeConverter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BugzyTypeConverters {
    private static Gson sGson;

    static {
        sGson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                .create();
    }

    @TypeConverter
    public static List<String> fromString(String value) {
        Type listType = new TypeToken<ArrayList<String>>() {}.getType();
        return sGson.fromJson(value, listType);
    }

    @TypeConverter
    public static String fromList(List<String> list) {
        String json = sGson.toJson(list);
        return json;
    }

    @TypeConverter
    public static String fromCaseEventList(List<CaseEvent> list) {
        String json = sGson.toJson(list);
        return json;
    }

    @TypeConverter
    public static List<CaseEvent> caseEventListFromString(String value) {
        Type listType = new TypeToken<List<CaseEvent>>() {}.getType();
        return sGson.fromJson(value, listType);
    }

    @TypeConverter
    public static List<Integer> integerListFromString(String value) {
        Type listType = new TypeToken<ArrayList<Integer>>() {}.getType();
        return sGson.fromJson(value, listType);
    }

    @TypeConverter
    public static String stringFromIntegerList(List<Integer> list) {
        String json = sGson.toJson(list);
        return json;
    }
}
