package com.ql.giapha.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ql.giapha.model.Spouse;
import com.ql.giapha.model.Person;

@Repository
public interface SpouseRepo extends JpaRepository<Spouse, Long> {
    void deleteAllBySpouse(Person person);
}
