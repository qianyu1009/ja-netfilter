package io.zhile.research.ja.netfilter.models;

import io.zhile.research.ja.netfilter.enums.RuleType;

public class FilterRule {
    private RuleType type;

    private String content;

    public FilterRule(RuleType type, String content) {
        this.type = type;
        this.content = content;
    }

    public RuleType getType() {
        return type;
    }

    public void setType(RuleType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
