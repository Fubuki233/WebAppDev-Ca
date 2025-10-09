package sg.com.aori.repository;

import sg.com.aori.model.ReturnRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReturnRepository extends JpaRepository<ReturnRequest, String> {

}