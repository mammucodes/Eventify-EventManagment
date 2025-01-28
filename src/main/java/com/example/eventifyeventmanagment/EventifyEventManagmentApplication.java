package com.example.eventifyeventmanagment;

import com.example.eventifyeventmanagment.loaders.EventStatusStaticLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EventifyEventManagmentApplication {

    public static void main(String[] args) {



        SpringApplication.run(EventifyEventManagmentApplication.class, args);
    }

}
