package com.ql.giapha.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ql.giapha.model.AppUser;
import com.ql.giapha.model.Family;

@Repository
public interface FamilyRepo extends JpaRepository<Family, Long> {
    List<Family> findAllByOwner(AppUser user);
}
