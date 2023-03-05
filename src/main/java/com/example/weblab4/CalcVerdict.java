package com.example.weblab4;

import com.example.weblab4.domain.Dot;

public class CalcVerdict {
    public static String calculate(Dot dot) {
        if (circle(dot) || triangle(dot) || rectangle(dot)){
            return "In";
        } else {
            return "Out";
        }
    }

    private static boolean circle(Dot dot) {
        return (dot.getX()>=0 && dot.getY()>=0
                && Math.pow(dot.getX(), 2) + Math.pow(dot.getY(), 2) <= Math.pow(dot.getR(), 2));
    }

    private static boolean triangle(Dot dot) {
        return (dot.getX() <=0 && dot.getY() <=0 &&
                (-dot.getX() - dot.getR()/2) <= dot.getY());
    }

    private static boolean rectangle(Dot dot) {
        return (dot.getX() >= 0 && dot.getY() <=0 &&
                dot.getY() >= -dot.getR() && dot.getX() <=dot.getR()/2);
    }
}
