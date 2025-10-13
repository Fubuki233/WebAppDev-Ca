package sg.com.aori.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sg.com.aori.model.Returns;

/**
 * @author Jiayi
 * @date 2025-10-09
 * @version 1.0
 */

@Repository
public interface ReturnRepository extends JpaRepository<Returns, String> {

    List<Returns> findByOrderItemIdIn(List<String> orderItemIds);
}