package sg.com.aori.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sg.com.aori.model.Role;

public interface RoleRepository extends JpaRepository<Role, String> {

}