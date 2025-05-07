package com.Banking.udfcbankapplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class BeanChecker {

    @Autowired
    private ApplicationContext context;

    public void checkBeans() {
        if (context.containsBean("passwordEncoder")) {
            System.out.println("✅ passwordEncoder is in the container.");
        } else {
            System.out.println("❌ passwordEncoder is NOT in the container.");
        }
    }
}
