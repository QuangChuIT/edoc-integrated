package com.bkav.edoc.service.util;

import java.util.ArrayList;
import java.util.List;

public class CommonUtil {
    public static List<Long> convertToListLong(List list) {
        if(list == null) return null;
        List<Long> result = new ArrayList<>();
        for (Object item : list) {
            Double value = Double.parseDouble(item.toString());
            result.add(value.longValue());
        }
        return result;
    }
}
