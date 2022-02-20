package com.example.forecastgas.model;

public class Main {
    public static void main(String[] args) {
        GasReserves reserves=new GasReserves(
                2468,
                307.201,
                2160.799,
                186.6,
                371,
                48.7,
                204,
                87.94,
                2.74554,
                1.142582,
                0.249793,
                86.0,
                323.0,
                347
        );
        Forecast forecast=new Forecast(
                2021,
                126.4,
                307.2,
                6,
                186.6,
                reserves,
                247.33
                );

        System.out.println(forecast);
    }
}
