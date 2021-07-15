package me.zhengjie.modules.flow.domain;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * 流程变量
 * @author Spur
 * @date 2019-11-27
 */
public class Variable {
    private Map<String, Object> map = Maps.newHashMap();

    private String keys;
    private String values;
    private String types;
    public Variable() {

    }

    public Variable(Map<String, Object> map) {
        this.map = map;
    }
    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }

    public String getKeys() {
        return keys;
    }

    public void setKeys(String keys) {
        this.keys = keys;
    }

    public String getValues() {
        return values;
    }

    public void setValues(String values) {
        this.values = values;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }
}
