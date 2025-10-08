package sg.com.aori.repository;

import sg.com.aori.model.Permission;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for Permission entity.
 *
 * @author xiaobo
 * @date 2025-10-07
 * @version 1.0
 */
public interface PermissionRepository extends JpaRepository<Permission, String> {

}
