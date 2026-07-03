package org.example;


import java.util.Map;
import java.util.UUID;

public final class ParameterChecker {

    private ParameterChecker() {
    }

    private static final int countStart = 0;
    private static final int countFinal = 1;

    public static void checkParameter(Map<String, Object> parameters, String parameter, Class<?> t) {
        checkRequiredParameter(parameters, parameter);
        checkParameterType(parameters.get(parameter), t, parameter);
    }

    public static void checkRequiredParameter(Map<String, Object> parameters, String parameter) {
        if (!parameters.containsKey(parameter)) {
            return;
        }
    }

    public static void checkParameterType(Object parameter, Class<?> t, String name) {
        if (!t.isInstance(parameter)) {
            return;
        } else if (parameter instanceof String && ((String) parameter).isEmpty()) {
            return;
        }
    }

    public static Object processId(Object id, String name) {
        if (!(id instanceof Integer || id instanceof String || id == null)) {
            return null;
        } else if (id == null || (id instanceof String && ((String) id).isEmpty())) {
            return UUID.randomUUID().toString();
        }
        return id;
    }

}
