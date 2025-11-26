package org.ctpn.chungtayphongngua.repository;

import org.ctpn.chungtayphongngua.entity.UserRole;
import org.ctpn.chungtayphongngua.entity.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {
    void deleteByUser(org.ctpn.chungtayphongngua.entity.User user);
}
