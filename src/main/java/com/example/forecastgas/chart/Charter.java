package com.example.forecastgas.chart;

import lombok.Builder;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Builder
public class Charter {


    public String title;

    public String x_title;
    public String y_title;
    public String seriesTitle;
    public List<Double> x_list;
    public List<Double> y_list;
    public boolean isFill;




    public static Charter chart(String title, String x_title, String y_title, String seriesTitle, double[] x_list, double[] y_list){
        List<Double> temp_x=new ArrayList<>();
        List<Double> temp_y=new ArrayList<>();
        for (double v : x_list) {
            temp_x.add(v);
        }
        for (double v : y_list) {
            temp_y.add(v);
        }
        return chart(title, x_title, y_title, seriesTitle, temp_x,temp_y);
    }


    public static Charter chart(String title, String x_title, String y_title, String seriesTitle, List<Double> x_list, List<Double> y_list){
        return Charter
                .builder()
                .title(title)
                .x_title(x_title)
                .y_title(y_title)
                .seriesTitle(seriesTitle)
                .x_list(x_list)
                .y_list(y_list)
                .isFill(true)
                .build();
    }

    public void draw(){
        if (isFill){
            new SwingWrapper<>(get()).displayChart();
        }
    }


    public XYChart get(String name){
        if (isFill){
            return charter(QuickChart.getChart(title,x_title,y_title,seriesTitle,x_list,y_list),name);
        }return null;
    }

    public XYChart get(){
        if (isFill){
            return charter(QuickChart.getChart(title,x_title,y_title,seriesTitle,x_list,y_list),title);
        }return null;
    }

    public static void mergeAndDraw(Charter... charters){
        List<XYChart> chartList=new ArrayList<>();
        for (Charter charter : charters) {
            chartList.add(charter.get());
        }
        new SwingWrapper<XYChart>(chartList).displayChartMatrix();
    }


    public static XYChart charter(XYChart chart, String name){
        chart.getStyler().setZoomEnabled(true);
        chart.getStyler().setCursorEnabled(true);
        chart.getStyler().setCustomCursorXDataFormattingFunction(x -> "Yil = " + x);
        chart.getStyler().setCustomCursorYDataFormattingFunction(y -> name+" =  " + y);
        return chart;
    }



}
