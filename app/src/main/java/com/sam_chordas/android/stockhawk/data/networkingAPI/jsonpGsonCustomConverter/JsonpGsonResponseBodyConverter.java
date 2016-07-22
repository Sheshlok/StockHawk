package com.sam_chordas.android.stockhawk.data.networkingAPI.jsonpGsonCustomConverter;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Created by sheshloksamal on 13/07/16.
 *
 */
final class JsonpGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final TypeAdapter<T> adapter;

    JsonpGsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        String jsonP = value.string();
        String json = JsonpParser.jsonpToJson(jsonP);
        return adapter.fromJson(json);
    }
}
