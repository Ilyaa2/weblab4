package com.example.weblab4.domain;


import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;


@Entity
@Table
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Dot(){}

    @Override
    public String toString() {
        return "Dot{" +
                "x=" + x +
                ", y=" + y +
                ", r=" + r +
                ", verdict='" + verdict + '\'' +
                '}';
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getR() {
        return r;
    }

    public void setR(double r) {
        this.r = r;
    }

    public String getVerdict() {
        return verdict;
    }

    public void setVerdict(String verdict) {
        this.verdict = verdict;
    }
}
