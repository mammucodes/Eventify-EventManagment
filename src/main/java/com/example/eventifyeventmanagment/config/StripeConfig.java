package com.example.eventifyeventmanagment.config;
import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

//import javax.annotation.PostConstruct;

@Component
public class StripeConfig {
    @PostConstruct
    public void setup() {
        Stripe.apiKey = "sk_test_51Qo6igP2ZwHOC8kMAfX57vWekAaQeAYEth9nSXtj5AbPK2fnMj5iwFUCB4d7I4hA9pA8mBlI7azgwPVLeFMGTnB200VQCOw0HM"; // Replace with your secret key
    }
}
