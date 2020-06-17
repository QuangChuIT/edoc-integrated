package com.bkav.edoc.service.util;

import com.bkav.edoc.service.database.entity.EdocTrace;

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

    public static List<EdocTrace> convertToListTrace(List list) {
        if(list == null) return null;
        List<EdocTrace> result = new ArrayList<>();
        for (Object item : list) {
            if(item instanceof EdocTrace) {
                result.add((EdocTrace)item);
            }
            else {

            }
        }
        return result;
    }
}
