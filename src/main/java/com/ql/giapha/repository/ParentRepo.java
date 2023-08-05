package com.ql.giapha.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ql.giapha.model.Parent;
import com.ql.giapha.model.Person;

@Repository
public interface ParentRepo extends JpaRepository<Parent, Long> {
    void deleteAllByParent(Person person);
}
