package com.example.weblab4.repository;

import com.example.weblab4.model.Dot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DotRepo extends JpaRepository<Dot, Long> {
    public List<Dot> findAllByREquals(double r);
}
