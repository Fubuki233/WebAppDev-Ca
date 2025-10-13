package sg.com.aori.repository;

/**
 * ViewHistoryRepository for managing product view history in the database.
 * 
 * @Author Yunhe
 * @date 2025-10-12
 * @version 1.0
 */
import sg.com.aori.model.ViewHistory;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ViewHistoryRepository extends JpaRepository<ViewHistory, String> {

    List<ViewHistory> findByUserId(String userId);

    // Changed to List to handle potential duplicates
    List<ViewHistory> findByUserIdAndProductId(String userId, String productId);

    List<ViewHistory> findByUserIdOrderByTimestampDesc(String userId);
}
