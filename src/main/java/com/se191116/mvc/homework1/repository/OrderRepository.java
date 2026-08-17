package com.se191116.mvc.homework1.repository;

import com.se191116.mvc.homework1.entity.CustomerOrder;
import com.se191116.mvc.homework1.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<CustomerOrder, Long> {

    @EntityGraph(attributePaths = {"user", "items", "items.product"})
    List<CustomerOrder> findByUserOrderByCreatedDateDesc(User user);

    @Override
    @EntityGraph(attributePaths = {"user", "items", "items.product"})
    List<CustomerOrder> findAll();
}
