package com.example.weblab4.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;


@Entity
@Table
@Data
@EqualsAndHashCode(of= {"id"})
public class Dot {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Min(-2)
    @Max(2)
    private double x;

    @Min(-5)
    @Max(5)
    private double y;

    @Min(0)
    @Max(4)
    private double r;

    private String verdict;

    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Override
    public String toString() {
        return "Dot{" +
                "x=" + x +
                ", y=" + y +
                ", r=" + r +
                ", verdict='" + verdict + '\'' +
                '}';
    }

}
