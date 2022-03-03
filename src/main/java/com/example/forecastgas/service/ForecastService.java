package com.example.forecastgas.service;


import com.example.forecastgas.chart.Charter;
import com.example.forecastgas.handler.*;
import com.example.forecastgas.model.Forecast;
import com.example.forecastgas.model.ForecastTest;
import com.example.forecastgas.model.GasReserves;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

@Component
@Service
public class ForecastService {

    private final List<Handler> handlers;

    public ForecastService(List<Handler> handlers) {
        this.handlers = handlers;
    }

    public static void main(String[] args) throws IOException {
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
                60,
                323.0,
                347,
                204,
                60,
                10,
                50,
                4,
                10,
                20
        );

        Forecast forecast=new Forecast(
                2021,
                126.4,
                307.2,
                6,
                247.33,
                186.6,
                reserves

        );

        List<Handler> handlers=new ArrayList<>();
        handlers.add(new ProductionHandler());
        handlers.add(new WellHandler());
        handlers.add(new DeltaPressureHandler());
        handlers.add(new SpeedHandler());
        new ForecastService(handlers).calculate(forecast,reserves);
//        inspect(Forecast.class);
//        System.out.println(forecast);
//        for (int i = 0; i <100 ; i++) {
//            System.out.println(forecast);
//            forecast=forecast.next(330);
//            if (forecast==null) break;
//        }
    }


    public static void chart(List<Forecast> forecasts){
        List<Double> yearList=new ArrayList<>();

        List<Double> Q_za_god=new ArrayList<>();
        List<Double> Q_otb=new ArrayList<>();
        List<Double> sredniy_debit_skvajina=new ArrayList<>();
        List<Double> kol_skvajina=new ArrayList<>();
        List<Double> kol_den=new ArrayList<>();
        List<Double> Z_plasta=new ArrayList<>();
        List<Double> P_plasta=new ArrayList<>();
        List<Double> P_zaboy=new ArrayList<>();
        List<Double> V_zaboy=new ArrayList<>();
        List<Double> P_delta=new ArrayList<>();
        List<Double> P_ustivoy=new ArrayList<>();
        List<Double> V_ustivoy=new ArrayList<>();

        forecasts.forEach(f->{
            yearList.add((double) f.god);
            Q_za_god.add(f.Q_za_god);
            Q_otb.add(f.Q_otb);
            sredniy_debit_skvajina.add(f.sredniy_debit_skvajina);
            kol_skvajina.add((double) f.kol_skvajina);
            kol_den.add(f.kol_den);
            Z_plasta.add(f.Z_plasta);
            P_plasta.add(f.P_plasta);
            P_zaboy.add(f.P_zaboy);
            V_zaboy.add(f.V_zaboy);
            P_delta.add(f.P_delta);
            P_ustivoy.add(f.P_ustivoy);
            V_ustivoy.add(f.V_ustivoy);
        });

        Charter.chart("Q_za_god","God","X","Q_za_god",yearList,Q_za_god).draw();
        Charter.chart("kol_skvajina","God","X","kol_skvajina",yearList,kol_skvajina).draw();
        Charter.chart("sredniy_debit_skvajina","God","X","sredniy_debit_skvajina",yearList,sredniy_debit_skvajina).draw();



    }


    public void calculate(Forecast forecast,GasReserves reserves) throws IOException {

        double Q=0;
        List<Forecast> list=new ArrayList<>();
        System.out.println(forecast);
        forecast.setKol_skvajina(9);
        forecast.setKol_den(330);
        while (reserves.V_o()-Q>0){


            Handler handler = getHandlerByCheck(forecast, reserves);
            if (handler==null) break;
            Forecast handle=handler.handle(forecast, reserves);
            if (handle==null) break;
            Q=handle.Q_nach;
            forecast=handle;
            list.add(handle);

        }
        System.out.println("SIZE = "+list.size());
        list.forEach(System.out::println);
        chart(list);
    }


    private Handler getHandlerByCheck(Forecast forecast, GasReserves reserves){
        System.out.println(handlers.size());
        for (Handler handler : handlers) {
            if (handler.check(forecast, reserves)) {
                System.out.println(handler);
                return handler;
            }
        }
        return null;
    }

    static <T> void inspect(Class<T> klazz) {
        Field[] fields = klazz.getDeclaredFields();
        System.out.printf("%d fields:%n", fields.length);
        for (Field field : fields) {
//            System.out.printf("%s %s %s%n",
//                    Modifier.toString(field.getModifiers()),
//                    field.getType().getSimpleName(),
//                    field.getName()
//            );
            System.out.println(field.getName());
        }
    }
}
