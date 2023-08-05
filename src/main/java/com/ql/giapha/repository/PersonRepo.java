package com.ql.giapha.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ql.giapha.model.Family;
import com.ql.giapha.model.Person;

@Repository
public interface PersonRepo extends JpaRepository<Person, Long> {
    List<Person> findAllByFamily(Family family);
}
