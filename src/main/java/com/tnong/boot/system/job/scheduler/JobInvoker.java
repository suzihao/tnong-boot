package com.tnong.boot.system.job.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 任务调用器
 * 支持格式：beanName.methodName 或 beanName.methodName(params)
 */
@Slf4j
@Component
public class JobInvoker {

    private final ApplicationContext applicationContext;

    public JobInvoker(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void invoke(String invokeTarget) throws Exception {
        String beanName;
        String methodName;
        Object[] params = new Object[0];

        if (invokeTarget.contains("(")) {
            int idx1 = invokeTarget.indexOf(".");
            int idx2 = invokeTarget.indexOf("(");
            beanName = invokeTarget.substring(0, idx1);
            methodName = invokeTarget.substring(idx1 + 1, idx2);
            String paramStr = invokeTarget.substring(idx2 + 1, invokeTarget.length() - 1);
            if (!paramStr.isEmpty()) {
                params = parseParams(paramStr);
            }
        } else {
            int idx = invokeTarget.indexOf(".");
            beanName = invokeTarget.substring(0, idx);
            methodName = invokeTarget.substring(idx + 1);
        }

        Object bean = applicationContext.getBean(beanName);
        Method method = findMethod(bean.getClass(), methodName, params);
        method.invoke(bean, params);
    }

    private Object[] parseParams(String paramStr) {
        String[] parts = paramStr.split(",");
        Object[] params = new Object[parts.length];
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i].trim();
            if (part.startsWith("\"") && part.endsWith("\"")) {
                params[i] = part.substring(1, part.length() - 1);
            } else if (part.equals("true") || part.equals("false")) {
                params[i] = Boolean.parseBoolean(part);
            } else {
                try {
                    params[i] = Long.parseLong(part);
                } catch (NumberFormatException e) {
                    params[i] = part;
                }
            }
        }
        return params;
    }

    private Method findMethod(Class<?> clazz, String methodName, Object[] params) throws NoSuchMethodException {
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName) && method.getParameterCount() == params.length) {
                return method;
            }
        }
        throw new NoSuchMethodException("Method not found: " + methodName);
    }
}
