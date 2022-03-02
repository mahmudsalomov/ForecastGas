package com.example.forecastgas.handler;

import com.example.forecastgas.model.Forecast;
import com.example.forecastgas.model.GasReserves;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;


@Component
public interface Handler {

    Forecast handle(Forecast forecast, GasReserves reserves) throws IOException;


    boolean check(Forecast forecast, GasReserves reserves);

}
