package com.janetfilter.core.commons;

import com.janetfilter.core.models.FilterRule;
import com.janetfilter.core.utils.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigParser {
    public static Map<String, List<FilterRule>> parse(File file) throws Exception {
        Map<String, List<FilterRule>> map = new HashMap<>();

        if (null == file || !file.exists() || !file.isFile() || !file.canRead()) {
            return map;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            int lineNumber = 0;
            String line, lastSection = null;

            while (null != (line = reader.readLine())) {
                lineNumber++;
                line = line.trim();
                if (StringUtils.isEmpty(line)) {
                    continue;
                }

                int len = line.length();
                switch (line.charAt(0)) {
                    case '[':
                        if (']' != line.charAt(len - 1)) {
                            throw new Exception("Invalid section! Line: " + lineNumber);
                        }

                        String section = line.substring(1, len - 1);
                        if (StringUtils.isEmpty(section)) {
                            throw new Exception("Empty section name! Line: " + lineNumber);
                        }

                        map.computeIfAbsent(lastSection = section, k -> new ArrayList<>());
                        break;
                    case '#':
                    case ';':
                        break;  // comment
                    case '/':
                        if (len > 1 && '/' == line.charAt(1)) {
                            break;  // comment
                        }
                        throw new Exception("Invalid character! Line: " + lineNumber);
                    default:
                        if (null == lastSection) {
                            break;  // ignore rules without section
                        }

                        String[] parts = line.split(",", 2);
                        if (2 != parts.length) {
                            throw new Exception("Invalid rule! Line: " + lineNumber);
                        }

                        String type = parts[0].trim();
                        String content = parts[1].trim();
                        if (StringUtils.isEmpty(type) || StringUtils.isEmpty(content)) {
                            throw new Exception("Invalid rule! Line: " + lineNumber);
                        }

                        if (!Character.isAlphabetic(type.charAt(0))) {
                            throw new Exception("Invalid rule! Line: " + lineNumber);
                        }

                        FilterRule rule = FilterRule.of(type, content);
                        if (null == rule) {
                            throw new Exception("Invalid rule type! Line: " + lineNumber);
                        }

                        map.get(lastSection).add(rule);
                        DebugInfo.debug("Add section: " + lastSection + ", rule: " + rule);
                        break;
                }
            }
        }

        DebugInfo.debug("Config file loaded: " + file);
        return map;
    }
}
