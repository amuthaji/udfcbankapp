package com.Banking.udfcbankapplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.context.ApplicationContext;

@Component
public class AllBeansPrinter implements CommandLineRunner {

    private final ApplicationContext context;

    public AllBeansPrinter(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void run(String... args) {
        String[] allBeans = context.getBeanDefinitionNames();
        for (String bean : allBeans) {
            System.out.println(bean);
        }
    }
}
