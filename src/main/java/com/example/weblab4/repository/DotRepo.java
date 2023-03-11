package com.example.weblab4.repository;

import com.example.weblab4.model.Dot;
import com.example.weblab4.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@Repository
public interface DotRepo extends JpaRepository<Dot, Long> {
    List<Dot> findByUser(User user);
    List<Dot> findByUserAndR(User user, @Min(0) @Max(4) double r);
}
