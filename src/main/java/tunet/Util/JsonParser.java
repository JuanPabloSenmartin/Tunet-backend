package tunet.Util;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class JsonParser {

    private static final Gson parser = new Gson();

    public static <T> T fromJson(String json, Class<T> classOfT) throws JsonSyntaxException {
        return parser.fromJson(json, classOfT);
    }

    public static String toJson(Object src) {
        return parser.toJson(src);
    }
}