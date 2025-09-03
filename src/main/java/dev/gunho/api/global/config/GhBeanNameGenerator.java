package dev.gunho.api.global.config;

import dev.gunho.api.global.util.Util;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.util.StringUtils;

/**
 * 패키지 경로 기반 빈 이름 생성
 */
public class GhBeanNameGenerator implements BeanNameGenerator {

    @Override
    public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
        String className = definition.getBeanClassName();
        if (Util.CommonUtil.isNotEmpty(className)) {
            String[] packageParts = className.split("\\.");
            StringBuilder beanName = new StringBuilder();

            boolean foundApi = false;
            for (String part : packageParts) {
                if (foundApi) {
                    if (!part.matches("v\\d+") &&
                            !part.equals("handler") && !part.equals("service") &&
                            !part.equals("repository") && !part.equals("model") &&
                            !part.equals("dto") && !part.equals("entity") &&
                            !part.equals("config") && !part.equals("util")) {
                        beanName.append(part).append("_");
                    }
                }

                if (part.equals("api")) foundApi = true;
            }

            // 클래스명의 첫 글자를 소문자로 변환하여 추가
            String simpleClassName = className.substring(className.lastIndexOf('.') + 1);
            beanName.append(StringUtils.uncapitalize(simpleClassName));

            return beanName.toString();

        }


        return null;
    }
}
