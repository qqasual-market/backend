package org.example.market;

import org.springframework.boot.SpringApplication;

public class TestMarketApplication {

    public static void main(String[] args) {
        SpringApplication.from(MarketApplication::main).with(TestMarketApplication.class);
    }
}
