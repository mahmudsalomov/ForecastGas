package com.example.forecastgas.handler;

import com.example.forecastgas.model.Forecast;
import com.example.forecastgas.model.ForecastTest;
import com.example.forecastgas.model.GasReserves;

import java.io.IOException;

public class DeltaPressureHandler implements Handler{
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

//        ForecastTest.IT it2 = helper_AvgPz(sredniy_debit_skvajina,P_plasta-reserves.delta_P_const(),P_plasta,reserves);
//        sredniy_debit_skvajina=it2.value1;
        double P_zaboy=P_plasta-reserves.delta_P_const();
        sredniy_debit_skvajina=(Math.sqrt(reserves.A()*reserves.A()+4*reserves.B()*(P_plasta*P_plasta-P_zaboy*P_zaboy))
                -reserves.A())
                /(2*reserves.B());



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

        double P_zaboy=P_plasta-reserves.delta_P_const();
        sredniy_debit_skvajina=(Math.sqrt(reserves.A()*reserves.A()+4*reserves.B()*(P_plasta*P_plasta-P_zaboy*P_zaboy))
                -reserves.A())
                /(2*reserves.B());


        double P_ustivoy=Math.sqrt((P_zaboy*P_zaboy-sredniy_debit_skvajina*sredniy_debit_skvajina*reserves.teta())/reserves.eS());;

//        if (P_u<=P_u_limit) return iteration_production_decrease_P_u_constant(inYearLimit, reserves, numberOfWells, y,  day,  limit,P_u_limit);

        double Z_ustivoy=(Math.pow(0.4*Math.log10(reserves.T_sr()/reserves.T_kr())+0.73, P_ustivoy/reserves.P_kr())+0.1*P_ustivoy/reserves.P_kr());

        double V_zaboy=(0.52*sredniy_debit_skvajina*reserves.T_u()*Z_ustivoy)/(8.57*8.57*P_zaboy);
        double V_ustivoy=(0.52*sredniy_debit_skvajina*reserves.T_u()*Z_ustivoy)/(8.57*8.57*P_ustivoy);

        //                        new Double(Z_w).isNaN()||
        //                        new Double(speed).isNaN()
        return
                !(new Double(P_zaboy).isNaN() ||
                new Double(P_ustivoy).isNaN() ||
                new Double(V_zaboy).isNaN() ||
                new Double(sredniy_debit_skvajina).isNaN());
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


    public ForecastTest.IT helper_AvgPz(double avg, double P_z, double P_p, GasReserves reserves){
        if(P_z<0) new ForecastTest.IT(0, 0, 0);
        int i;
        for (i = 0; i <1000 ; i++) {
            double temp_avg=avg;
            double temp_P_z=P_z;

            avg=(Math.sqrt(reserves.A()*reserves.A()+4*reserves.B()*(P_p*P_p-P_z*P_z))
                    -reserves.A())
                    /(2*reserves.B());
            P_z=Math.sqrt(P_p*P_p-(reserves.A()*avg+reserves.B()*avg*avg));


            if (Math.abs(temp_avg-avg)<=0.001||Math.abs(P_z-temp_P_z)<=0.001) {
                break;
            }
        }
        return new ForecastTest.IT(avg, P_z, i);
    }
}
