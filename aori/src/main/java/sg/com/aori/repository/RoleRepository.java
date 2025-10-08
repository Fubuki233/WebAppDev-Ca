package sg.com.aori.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sg.com.aori.model.Role;

/**
 * Repository interface for Role entity.
 *
 * @author xiaobo
 * @date 2025-10-07
 * @version 1.0
 */

public interface RoleRepository extends JpaRepository<Role, String> {

}