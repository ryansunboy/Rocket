package org.snail.rocket.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.NameFilter;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.ValueFilter;
import org.apache.commons.lang.math.NumberUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * JSON转换工具类
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2017-09-12 15:17
 */
public class JSONUtils {

    private static final NameFilter nameFilter = new NameFilter() {
        public String process(Object source, String name, Object value) {
            return CamelCaseUtils.toUnderlineName(name);
        }
    };

    private static final NameFilter name2CamelCaseFilter = new NameFilter() {
        public String process(Object source, String name, Object value) {
            return CamelCaseUtils.toCamelCase(name);
        }
    };

    private static final ValueFilter numberToStringValueFilter = new ValueFilter() {
        @Override
        public Object process(Object object, String name, Object value) {
            if (value instanceof Number) {
                value = String.valueOf(value);
            }
            return value;
        }
    };
    public static final String toJSONString(Object object) {
        SerializeFilter[] serializeFilters = new SerializeFilter[]{nameFilter, numberToStringValueFilter};
        return JSON.toJSONString(object, serializeFilters);
    }


    public static final String toJSONString(Object object, Collection<String> booleanFieldNames) {
        if (booleanFieldNames == null || booleanFieldNames.isEmpty()) {
            return JSONUtils.toJSONString(object);
        }
        SerializeFilter[] serializeFilters = new SerializeFilter[]{nameFilter, new BooleanValueFilter(booleanFieldNames), numberToStringValueFilter};
        return JSON.toJSONString(object, serializeFilters);
    }

    public static final <T> List<T> parseList(String text, Class<T> clazz) {
        List<T> result = new ArrayList<>();
        JSONArray jsonArray = JSON.parseArray(text);
        for (Object object : jsonArray) {
            result.add(parse(String.valueOf(object), clazz));
        }
        return result;
    }

    public static final <T> T parse(String text, Class<T> clazz) {
        SerializeFilter[] serializeFilters = new SerializeFilter[]{name2CamelCaseFilter};
        return JSON.parseObject(JSON.toJSONString(JSON.parse(text), serializeFilters), clazz);
    }

    public static final <T> T parse(String text, TypeReference<T> type) {
        SerializeFilter[] serializeFilters = new SerializeFilter[]{name2CamelCaseFilter};
        return JSON.parseObject(JSON.toJSONString(JSON.parse(text), serializeFilters), type);
    }

    public static boolean isDataEmpty(JSONObject object) {
        if (object == null) {
            return true;
        }
        Object dataObj = object.get("data");
        if (dataObj == null) {
            return true;
        }
        if (dataObj instanceof JSONObject) {
            return ((JSONObject) dataObj).size() == 0;
        } else if (dataObj instanceof JSONArray) {
            return ((JSONArray) dataObj).size() == 0;
        } else {
            // invalidate data
            return true;
        }
    }

    private static class BooleanValueFilter implements ValueFilter {
        Collection<String> booleanFieldNames;

        public BooleanValueFilter(Collection<String> booleanFieldNames) {
            this.booleanFieldNames = booleanFieldNames;
        }

        @Override
        public Object process(Object object, String name, Object value) {
            if (value == null || (!booleanFieldNames.contains(name) && !booleanFieldNames.contains(CamelCaseUtils.toCamelCase(name)))) {
                return value;
            }
            if (value instanceof String) {
                if (!NumberUtils.isNumber((String) value)) {
                    return value.equals("yes");
                }
                value = Integer.valueOf((String) value);
            }
            if (value instanceof Number) {
                return ((Number) value).intValue() > 0;
            }
            return value;
        }
    }
}
