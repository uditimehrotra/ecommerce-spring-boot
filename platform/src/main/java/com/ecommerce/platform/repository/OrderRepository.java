package com.ecommerce.platform.repository;
import com.ecommerce.platform.model.OrderItem;
import com.ecommerce.platform.model.Order;
import com.ecommerce.platform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // The "JOIN FETCH" tells JPA to grab the items in a single SQL query
    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.user = :user")
    List<Order> findByUserWithItems(@Param("user") User user);

    List<Order> findByUser(User user); // To show a user their "Order History"
}