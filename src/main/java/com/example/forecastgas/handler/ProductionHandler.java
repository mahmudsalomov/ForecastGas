package com.example.forecastgas.handler;

import com.example.forecastgas.model.Forecast;
import com.example.forecastgas.model.ForecastTest;
import com.example.forecastgas.model.GasReserves;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

public class ProductionHandler implements Handler{

    @Override
    public Forecast handle(Forecast forecast, GasReserves reserves) throws IOException {



        double Z_plasta=forecast.Z_plasta;

        double P_plasta=((1-forecast.Q_otb/reserves.V_t())*forecast.P_plasta)*Z_plasta/forecast.Z_plasta;
//        System.out.println("Z_n = "+Z_n);
//        System.out.println("P_p = "+P_p);

        ForecastTest.IT it = helper_ZnPp(Z_plasta, P_plasta, forecast,reserves);
        Z_plasta=it.value1;
        P_plasta=it.value2;




        double sredniy_debit_skvajina=(reserves.Q_const()/(forecast.kol_den*forecast.kol_skvajina))*1000;

        double P_zaboy=Math.sqrt(P_plasta*P_plasta-(reserves.A()*sredniy_debit_skvajina+reserves.B()*sredniy_debit_skvajina*sredniy_debit_skvajina));

        double P_ustivoy=Math.sqrt((P_zaboy*P_zaboy-sredniy_debit_skvajina*sredniy_debit_skvajina*reserves.teta())/reserves.eS());;

//        if (P_u<=P_u_limit) return iteration_production_decrease_P_u_constant(inYearLimit, reserves, numberOfWells, y,  day,  limit,P_u_limit);

        double Z_ustivoy=(Math.pow(0.4*Math.log10(reserves.T_sr()/reserves.T_kr())+0.73, P_ustivoy/reserves.P_kr())+0.1*P_ustivoy/reserves.P_kr());

        double V_zaboy=(0.52*sredniy_debit_skvajina*reserves.T_u()*Z_ustivoy)/(8.57*8.57*P_zaboy);
        double V_ustivoy=(0.52*sredniy_debit_skvajina*reserves.T_u()*Z_ustivoy)/(8.57*8.57*P_ustivoy);


        double P_delta=P_plasta-P_zaboy;

        double Q_za_god=reserves.Q_const();
        double Q_otb=forecast.Q_otb+Q_za_god;
        double Q_nach=forecast.Q_nach+Q_za_god;
        double ot_zap=(Q_nach/reserves.V_o())*100;



        return Forecast
                .builder()
                .god(forecast.god+1)
                .Q_za_god(Q_za_god)
                .Q_otb(Q_otb)
                .Q_nach(Q_nach)
                .ot_zap(ot_zap)
                .sredniy_debit_skvajina(sredniy_debit_skvajina)
                .kol_skvajina(forecast.kol_skvajina)
                .kol_den(forecast.kol_den)
                .Z_plasta(Z_plasta)
                .P_plasta(P_plasta)
                .P_zaboy(P_zaboy)
                .V_zaboy(V_zaboy)
                .P_delta(P_delta)
                .P_ustivoy(P_ustivoy)
                .Z_ustivoy(Z_ustivoy)
                .V_ustivoy(V_ustivoy)
                .reserves(reserves)
                .build();
    }

    @Override
    public boolean check(Forecast forecast, GasReserves reserves) {

        double Z_plasta=forecast.Z_plasta;

        double P_plasta=((1-forecast.Q_otb/reserves.V_t())*forecast.P_plasta)*Z_plasta/forecast.Z_plasta;
        ForecastTest.IT it = helper_ZnPp(forecast.Z_plasta, P_plasta, forecast,reserves);
        Z_plasta=it.value1;
        P_plasta=it.value2;
        if (P_plasta<=0) return false;
        double sredniy_debit_skvajina=(reserves.Q_const()/(forecast.kol_den*forecast.kol_skvajina))*1000;

        double P_zaboy=Math.sqrt(P_plasta*P_plasta-(reserves.A()*sredniy_debit_skvajina+reserves.B()*sredniy_debit_skvajina*sredniy_debit_skvajina));

        return !new Double(P_zaboy).isNaN();
    }



    public ForecastTest.IT helper_ZnPp(double Z_n, double P_p, Forecast forecast, GasReserves reserves){
        int i;
        for (i = 0; i <1000 ; i++) {
            double temp_Z_n=Z_n;
            double temp_P_p=P_p;
            Z_n=(Math.pow(0.4*Math.log10(reserves.T_pl()/reserves.T_kr())+0.73, P_p/reserves.P_kr())+0.1*P_p/reserves.P_kr());
            P_p=((1-forecast.Q_otb/reserves.V_t())*forecast.P_plasta)*Z_n/forecast.Z_plasta;
            if (Math.abs(Z_n-temp_Z_n)<=0.001||Math.abs(P_p-temp_P_p)<=0.001) {
                break;
            }
        }
        return new ForecastTest.IT(Z_n, P_p, i);
    }
}
