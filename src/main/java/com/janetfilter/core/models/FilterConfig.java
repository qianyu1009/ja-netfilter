package com.janetfilter.core.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FilterConfig {
    private static FilterConfig current;

    private final Map<String, List<FilterRule>> data;

    public FilterConfig(Map<String, List<FilterRule>> data) {
        this.data = data;
    }

    public static FilterConfig getCurrent() {
        return current;
    }

    public static void setCurrent(FilterConfig current) {
        FilterConfig.current = current;
    }

    public static List<FilterRule> getBySection(String section) {
        do {
            if (null == current) {
                break;
            }

            if (null == current.data) {
                break;
            }

            List<FilterRule> list = current.data.get(section);
            if (null == list) {
                break;
            }

            return list;
        } while (false);

        return new ArrayList<>();
    }
}