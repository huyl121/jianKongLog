package org.example;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.math.BigDecimal;

public class JsonWrapperArray {

    private JSONArray array = null;

    public JsonWrapperArray(JSONArray array) {
        this.array = array;
    }

    public JsonWrapper getJsonObjectAt(int index) {
        if (array != null && array.size() > index) {
            JSONObject object = (JSONObject) array.get(index);
            if (object == null) {
                return null;
            }
            return new JsonWrapper(object);
        } else {
            return null;
        }
    }

    public void add(JSON val) {
        this.array.add(val);
    }

    public JsonWrapperArray getArrayAt(int index) {
        if (array != null && array.size() > index) {
            JSONArray newArray = (JSONArray) array.get(index);
            if (newArray == null) {
                return null;
            }
            return new JsonWrapperArray(newArray);
        } else {
            return null;
        }
    }

    private Object getObjectAt(int index) {
        if (array != null && array.size() > index) {
            return array.get(index);
        } else {
            return null;
        }
    }

    public long getLongAt(int index) {
        try {
            return (Long) getObjectAt(index);
        } catch (Exception e) {
            return 0;
        }

    }

    public void forEach(Handler<JsonWrapper> objectHandler) {
        array.forEach((object) -> {
            if (!(object instanceof JSONObject)) {

            }
            objectHandler.handle(new JsonWrapper((JSONObject) object));
        });
    }

    public Integer getIntegerAt(int index) {
        try {
            return (Integer) getObjectAt(index);
        } catch (Exception e) {
            return null;
        }

    }

    public BigDecimal getBigDecimalAt(int index) {

        try {
            return new BigDecimal(new BigDecimal(getStringAt(index)).stripTrailingZeros().toPlainString());
        } catch (RuntimeException e) {
            return null;
        }

    }

    public String getStringAt(int index) {

        try {
            return (String) getObjectAt(index);
        } catch (RuntimeException e) {
            return null;
        }

    }

}
