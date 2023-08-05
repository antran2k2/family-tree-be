package com.ql.giapha.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ql.giapha.model.Sibling;
import com.ql.giapha.model.Person;

@Repository
public interface SiblingRepo extends JpaRepository<Sibling, Long> {
    void deleteAllBySibling(Person person);
}
