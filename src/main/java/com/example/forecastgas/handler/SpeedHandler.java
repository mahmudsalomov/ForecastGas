package com.example.forecastgas.handler;

import com.example.forecastgas.model.Forecast;
import com.example.forecastgas.model.ForecastTest;
import com.example.forecastgas.model.GasReserves;

import java.io.IOException;

public class SpeedHandler implements Handler{


    @Override
    public Forecast handle(Forecast forecast, GasReserves reserves) throws IOException {
        return null;
    }




    @Override
    public boolean check(Forecast forecast, GasReserves reserves) {
        return false;
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
