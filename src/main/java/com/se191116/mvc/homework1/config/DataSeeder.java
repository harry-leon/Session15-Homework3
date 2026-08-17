package com.se191116.mvc.homework1.config;

import com.se191116.mvc.homework1.entity.Product;
import com.se191116.mvc.homework1.entity.Role;
import com.se191116.mvc.homework1.entity.User;
import com.se191116.mvc.homework1.repository.ProductRepository;
import com.se191116.mvc.homework1.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(UserRepository userRepository, ProductRepository productRepository) {
        return args -> {
            if (userRepository.count() == 0) {
                userRepository.save(new User("customer1", Role.CUSTOMER));
                userRepository.save(new User("staff1", Role.STAFF));
                userRepository.save(new User("admin1", Role.ADMIN));
            }

            if (productRepository.count() == 0) {
                productRepository.save(new Product("Classic Milk Tea", new BigDecimal("35000")));
                productRepository.save(new Product("Taro Milk Tea", new BigDecimal("40000")));
                productRepository.save(new Product("Peach Tea", new BigDecimal("30000")));
            }
        };
    }
}
