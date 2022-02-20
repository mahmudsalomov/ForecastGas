package com.example.forecastgas.model;

import lombok.ToString;


public record GasReserves(
        double V_o,
        double Q_n,
        double V_t,
        double P_n,
        double T_pl,
        double P_kr,
        double T_kr,
        double A,
        double B,
        double eS,
        double teta,
        double delta_P,
        double T_u,
        double T_sr
) {
}
