package com.ql.giapha.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ql.giapha.model.Child;
import com.ql.giapha.model.Person;

@Repository
public interface ChildRepo extends JpaRepository<Child, Long> {
    void deleteAllByChild(Person person);
}
