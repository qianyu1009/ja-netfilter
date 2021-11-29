package io.zhile.research.ja.netfilter.models;

import io.zhile.research.ja.netfilter.enums.RuleType;

public class FilterRule {
    private RuleType type;

    private String rule;

    public FilterRule(RuleType type, String rule) {
        this.type = type;
        this.rule = rule;
    }

    public RuleType getType() {
        return type;
    }

    public void setType(RuleType type) {
        this.type = type;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public boolean test(String content) {
        return type.getRuler().test(this.rule, content);
    }

    @Override
    public String toString() {
        return "{" +
                "type=" + type +
                ", rule='" + rule + '\'' +
                '}';
    }
}
