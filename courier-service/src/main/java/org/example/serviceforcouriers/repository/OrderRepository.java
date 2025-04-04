package org.example.serviceforcouriers.repository;

import org.example.serviceforcouriers.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Order findOrderByProduct(String product);

    @Query("select o.product from Order o")
    List<Order> findAllProduct();
}