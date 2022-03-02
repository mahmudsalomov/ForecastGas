package com.example.forecastgas.handler;

import com.example.forecastgas.model.Forecast;
import com.example.forecastgas.model.ForecastTest;
import com.example.forecastgas.model.GasReserves;

import java.io.IOException;

public class MouthPressureHandler implements Handler{
    @Override
    public Forecast handle(Forecast forecast, GasReserves reserves) throws IOException {
        double Z_plasta=forecast.Z_plasta;

        double P_plasta=((1-forecast.Q_otb/reserves.V_t())*forecast.P_plasta)*Z_plasta/forecast.Z_plasta;
//        System.out.println("Z_n = "+Z_n);
//        System.out.println("P_p = "+P_p);

        ForecastTest.IT it = helper_ZnPp(Z_plasta, P_plasta, forecast,reserves);
        Z_plasta=it.value1;
        P_plasta=it.value2;


        double Z_ustivoy=(Math.pow(0.4*Math.log10(reserves.T_sr()/reserves.T_kr())+0.73, reserves.P_u_const()/reserves.P_kr())+0.1*reserves.P_u_const()/reserves.P_kr());

        double sredniy_debit_skvajina=(reserves.P_u_const()*reserves.speed_u_const()*8.57*8.57)/(0.52*reserves.T_u()*Z_ustivoy);



//        double Q_otb=(1-(P_plasta*forecast.Z_plasta)/(forecast.P_plasta*Z_plasta))*forecast.Q_nach;
        double Q_za_god=(sredniy_debit_skvajina/1000)*(forecast.kol_den*forecast.kol_skvajina);
        double Q_otb= forecast.Q_otb+Q_za_god;
//        double sredniy_debit_skvajina=(Q_otb/(forecast.kol_den*forecast.kol_skvajina))*1000;

//        ForecastTest.IT it2 = helper_AvgPz(sredniy_debit_skvajina,P_plasta-reserves.delta_P_const(),P_plasta,reserves);
//        sredniy_debit_skvajina=it2.value1;
//        double P_zaboy=Math.sqrt(P_plasta*P_plasta-(reserves.A()*sredniy_debit_skvajina+reserves.B()*sredniy_debit_skvajina*sredniy_debit_skvajina));
        double P_zaboy=P_plasta-forecast.P_delta*0.9;
//        sredniy_debit_skvajina=(Math.sqrt(reserves.A()*reserves.A()+4*reserves.B()*(P_plasta*P_plasta-P_zaboy*P_zaboy))
//                -reserves.A())
//                /(2*reserves.B());



//        double P_ustivoy=Math.sqrt((P_zaboy*P_zaboy-sredniy_debit_skvajina*sredniy_debit_skvajina*reserves.teta())/reserves.eS());;

//        if (P_u<=P_u_limit) return iteration_production_decrease_P_u_constant(inYearLimit, reserves, numberOfWells, y,  day,  limit,P_u_limit);


        double V_zaboy=(0.52*sredniy_debit_skvajina*reserves.T_u()*Z_ustivoy)/(8.57*8.57*P_zaboy);
        double V_ustivoy=(0.52*sredniy_debit_skvajina*reserves.T_u()*Z_ustivoy)/(8.57*8.57*reserves.P_u_const());


        double P_delta=P_plasta-P_zaboy;

//        double Q_za_god=sredniy_debit_skvajina*forecast.kol_skvajina*forecast.kol_den/1000;
//        double Q_otb=forecast.Q_otb+Q_za_god;
        double Q_nach=forecast.Q_nach+Q_za_god;
        double ot_zap=(Q_nach/reserves.V_o())*100;



        return Forecast
                .builder()
                .god(forecast.god+1)
                .Q_za_god((sredniy_debit_skvajina*forecast.kol_den*forecast.kol_skvajina)/1000)
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
                .P_ustivoy(reserves.P_u_const())
                .Z_ustivoy(Z_ustivoy)
                .V_ustivoy(V_ustivoy)
                .reserves(reserves)
                .build();
    }

    @Override
    public boolean check(Forecast forecast, GasReserves reserves) {


        return true;
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
