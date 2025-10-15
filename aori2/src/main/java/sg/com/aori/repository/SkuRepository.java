package sg.com.aori.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import sg.com.aori.model.Sku;

@Repository
public interface SkuRepository extends JpaRepository<Sku, String> {

}
