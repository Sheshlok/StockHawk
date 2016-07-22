package com.sam_chordas.android.stockhawk.data.networkingAPI.jsonpGsonCustomConverter;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by sheshloksamal on 13/07/16.
 * http://stackoverflow.com/questions/34421851/retrofit-how-to-parse-this-response/34421880.
 * Converter Factory for parsing YQL responses that begin with following characters:
 * "finance_charts_json_callback(". It is a custom JSONP -> GSON converter for 2nd API interface
 * of stock histories. We are doing this since JsonReader.setLenient(true) does not work as the
 * 1st line of API response is a string.
 */

/**
 * A {@linkplain Converter.Factory converter} which uses Gson for JSONP.
 * <p>
 * Because Gson is so flexible in the types it supports, this converter assumes that it can handle
 * all types. If you are mixing JSON serialization with something else (such as protocol buffers),
 * you must {@linkplain retrofit2.Retrofit.Builder#addConverterFactory(Converter.Factory)}
 * add this instance last to allow the other converters a chance to see their types.
 */

public final class JsonpGsonConverterFactory extends Converter.Factory {

    /**
     * Create an instance using a default {@link Gson} instance for conversion. Encoding to JSON and
     * decoding from JSON (when no charset is specified by a header) will use UTF-8.
     */
    public static JsonpGsonConverterFactory create() {
        return create(new Gson());
    }

    /**
     * Create an instance using {@code gson} for conversion. Encoding to JSON and
     * decoding from JSON (when no charset is specified by a header) will use UTF-8.
     */
    public static JsonpGsonConverterFactory create(Gson gson) {
        return new JsonpGsonConverterFactory(gson);
    }

    private final Gson gson;

    private JsonpGsonConverterFactory(Gson gson) {
        if (gson == null) throw new NullPointerException("gson == null");
        this.gson = gson;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                            Retrofit retrofit) {
//        if ((type instanceof Class<?>)) return null;
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new JsonpGsonResponseBodyConverter<>(gson, adapter);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations,
                                                          Annotation[] methodAnnotations, Retrofit retrofit) {
        if ((type instanceof Class<?>)) return null;
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new JsonpGsonRequestBodyConverter<>(gson, adapter);
    }

}
