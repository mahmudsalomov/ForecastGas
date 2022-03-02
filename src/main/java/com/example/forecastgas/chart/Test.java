package com.example.forecastgas.chart;

public class Test {
    public static void main(String[] args) {
        double[] x={1,2,3,4,1,3};
        double[] y={1,2,3,4,1,3};

        Charter chart1 = Charter.chart("Test", "X", "Y", "y(x)", x, y);
        Charter chart2 = Charter.chart("Test", "X", "Y", "y(x)", x, y);
        Charter.mergeAndDraw(chart1,chart2,chart1,chart2);
    }
}
