package com.ql.giapha.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ql.giapha.model.Role;

@Repository
public interface RoleRepo extends JpaRepository<Role, Long> {

}
