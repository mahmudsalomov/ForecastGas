package com.example.forecastgas.model;

import lombok.*;
import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;


import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class ForecastTest {


//    public Forecast(int year, double inYear, int numberOfWells, double P_p) {
//        this.year = year;
//        this.inYear = inYear;
//        this.numberOfWells = numberOfWells;
//        this.P_p = P_p;
//
//    }
//
//
//    public Forecast(int year, double inYear, int numberOfWells, double P_p,GasReserves reserves) {
//        this.year = year;
//        this.inYear = inYear;
//        this.numberOfWells = numberOfWells;
//        this.P_p = P_p;
//        this.reserves=reserves;
//
//
//    }





    public ForecastTest(int year, double inYear, double fromStart, int numberOfWells, double P_p, GasReserves reserves, double workDayCount) {



        this.year = year;
        this.inYear = inYear;
        this.fromStart = fromStart;
        this.numberOfWells = numberOfWells;
        this.P_p = P_p;
        this.workDayCount=workDayCount;
//        this.reserves = reserves;

        this.fromStartReserve=(fromStart/reserves.V_o())*100;
        this.averageWellFlowRate=((inYear/workDayCount)/numberOfWells)*1000;
        this.P_z=Math.sqrt(P_p*P_p-(reserves.A()*averageWellFlowRate+reserves.B()*averageWellFlowRate*averageWellFlowRate));

        this.averageWellFlowRate2=(
                Math.sqrt(reserves.A()*reserves.A()+4*reserves.B()*(P_p*P_p-P_z*P_z))
                        -reserves.A())
                /(2*reserves.B());



        this.delta_P=P_p-P_z;
        this.P_u=Math.sqrt((P_z*P_z-averageWellFlowRate*averageWellFlowRate*reserves.teta())/reserves.eS());


        this.Z_n=(Math.pow(0.4*Math.log10(reserves.T_pl()/reserves.T_kr())+0.73, P_p/reserves.P_kr())+0.1*P_p/reserves.P_kr());


        this.Z_w=(Math.pow(0.4*Math.log10(reserves.T_sr()/reserves.T_kr())+0.73, P_u/reserves.P_kr())+0.1*P_u/reserves.P_kr());



        this.speed=(0.52*averageWellFlowRate*reserves.T_u()*Z_w)/(8.57*8.57*P_u);


//        V_t=reserves.V_o()-fromStart;

//        double temp=Q_otb;
        Q_otb=204;
        for (int i = 0; i <100 ; i++) {

            if (!iteration_Z_n_To_P_p(Q_otb,reserves,9,i+1,330,204,50)) {
//                System.out.println(" YEAR = "+i);
                break;
            }


        }

        Double[][] data=new Double[year_list.size()][7];



        List<List<Double>> list=new ArrayList<>();

        for (int i = 0; i <7 ; i++) {
            switch (i) {
                case 0 -> list.add(year_list);
                case 1 -> list.add(P_p_list);
                case 2 -> list.add(Z_n_list);
                case 3 -> list.add(well_list);
                case 4 -> list.add(speed_list);
                case 5 -> list.add(P_u_list);
                case 6 -> list.add(percent_list);
            }
        }
        for (int i = 0; i <year_list.size() ; i++) {
            data[i][0]=list.get(0).get(i);
            data[i][1]=list.get(1).get(i);
            data[i][2]=list.get(2).get(i);
            data[i][3]=list.get(3).get(i);
            data[i][4]=list.get(4).get(i);
            data[i][5]=list.get(5).get(i);
            data[i][6]=list.get(6).get(i);
        }



        String[] columnNames = { "Год","Давление, кгс/см2 пластовое", "Z", "Количество действующих скважин","скорость","Давление, кгс/см2 устьевое","% от зап." };

        var f = new JFrame();

        // Frame Title
        f.setTitle("Jadval");
        JTable table = new JTable(data,columnNames);
        table.setFont(new Font("Serif", Font.BOLD, 20));
        table.getTableHeader().setFont(new Font("SanSerif", Font.BOLD, 12));

        table.setDefaultEditor(Object.class, null);
        table.setRowHeight(50);
        JScrollPane sp = new JScrollPane(table);
        f.add(sp);
        f.setExtendedState(JFrame.MAXIMIZED_BOTH);
        f.setVisible(true);

        XYChart chart_P_p = QuickChart.getChart("Plast Pressure forecast", "Yil", "Bosim", "y(x)", year_list, P_p_list);
        XYChart chart_P_z = QuickChart.getChart("Zaboy Pressure forecast", "Yil", "Bosim", "y(x)", year_list, P_z_list);
        XYChart chart_Z_n = QuickChart.getChart("Z_n forecast", "Yil", "Koeffitsiyent", "y(x)", year_list, Z_n_list);
        XYChart chart_P_u = QuickChart.getChart("P_u forecast", "Yil", "Bosim", "y(x)", year_list, P_u_list);


        XYChart chart_year_Q = QuickChart.getChart("Отбор газа за год, ", "Yil", "Отбор", "y(x)", year_list, year_product_list);
        XYChart chart_well = QuickChart.getChart("Well forecast", "Yil", "Number well", "y(x)", year_list, well_list);
        XYChart chart_speed = QuickChart.getChart("speed forecast", "Yil", "Speed", "y(x)", year_list, speed_list);

        XYChart chart_delta_P = QuickChart.getChart("ΔP forecast", "Yil", "ΔP", "y(x)", year_list, delta_P_list);
        XYChart chart_avg = QuickChart.getChart("Средний дебит скважин", "Yil", "Средний дебит", "y(x)", year_list, avg_list);


        charter(chart_P_p,"P_p plast");
        charter(chart_P_z,"P_z zaboy");
        charter(chart_Z_n,"Koeffitsiyent");
        charter(chart_well,"Number");
        charter(chart_speed,"Speed");
        charter(chart_P_u,"P_u Bosim");
        charter(chart_delta_P,"ΔP");
        charter(chart_avg,"Средний дебит");
        charter(chart_year_Q,"Отбор газа за год");

        List<XYChart> charts = new ArrayList<XYChart>();
        List<XYChart> charts2 = new ArrayList<XYChart>();

        charts.add(chart_P_p);
        charts.add(chart_P_z);
        charts.add(chart_Z_n);
        charts.add(chart_P_u);

        charts2.add(chart_year_Q);
        charts2.add(chart_well);
        charts2.add(chart_avg);
        charts2.add(chart_speed);

        new SwingWrapper<XYChart>(charts).displayChartMatrix();
        new SwingWrapper<XYChart>(charts2).displayChartMatrix();






    }


    public void charter(XYChart chart,String name){
        chart.getStyler().setZoomEnabled(true);
        chart.getStyler().setCursorEnabled(true);
        chart.getStyler().setCustomCursorXDataFormattingFunction(x -> "Yil = " + x);
        chart.getStyler().setCustomCursorYDataFormattingFunction(y -> name+" =  " + y);
    }

    public ForecastTest(int year){

    }


    // Год
    public int year;


    /** Отбор газа **/

    //Запасы газа
    private double V_t;

    // за год
    private double inYear;

    private double Q_otb;
    // с начала разработки
    private double fromStart;
    // % от зап.
    private double fromStartReserve;

    /** Средний дебит скважин **/
    // тыс. м3/сутки
    private double averageWellFlowRate;
    private double averageWellFlowRate2;

    /** Количество действующих скважин **/
    private int numberOfWells;

    // Коэффициент сверсжимаемости газа
    private double Z_n;


    /** Давление, кгс/см2 **/
    // пластовое
    private double P_p;

    // забойное
    private double P_z;

    // ΔP
    private double delta_P;

    // устьевое
    private double P_u;

    // Z
    private double Z_w;

    // скорость
    private double speed;

    private GasReserves reserves;

    private double workDayCount;





    List<Double> P_p_list =new ArrayList<>();
    List<Double> P_z_list =new ArrayList<>();
    List<Double> Z_n_list =new ArrayList<>();
    List<Double> year_list =new ArrayList<>();
    List<Double> well_list =new ArrayList<>();
    List<Double> speed_list =new ArrayList<>();
    List<Double> P_u_list =new ArrayList<>();
    List<Double> percent_list =new ArrayList<>();
    List<Double> delta_P_list =new ArrayList<>();
    List<Double> avg_list =new ArrayList<>();
    List<Double> year_product_list =new ArrayList<>();
//    List<Integer> test2 =new ArrayList<>();



    public boolean iteration_Z_n_To_P_p(double inYearLimit,GasReserves reserves,
                                        int numberOfWells,int y, int day, double limit,
                                        double P_u_limit){
        double Z_n=this.Z_n;

        double P_p=((1-Q_otb/reserves.V_t())*this.P_p)*Z_n/this.Z_n;
//        System.out.println("Z_n = "+Z_n);
//        System.out.println("P_p = "+P_p);

        IT it = helper_ZnPp(Z_n, P_p, Q_otb,reserves);
        Z_n=it.value1;
        P_p=it.value2;
        if (P_p<=0) return false;




        double averageWellFlowRate=(limit/(day*numberOfWells))*1000;

        double P_z=Math.sqrt(P_p*P_p-(reserves.A()*averageWellFlowRate+reserves.B()*averageWellFlowRate*averageWellFlowRate));

        double P_u=Math.sqrt((P_z*P_z-averageWellFlowRate*averageWellFlowRate*reserves.teta())/reserves.eS());;

//        if (P_u<=P_u_limit) return iteration_production_decrease_P_u_constant(inYearLimit, reserves, numberOfWells, y,  day,  limit,P_u_limit);

        double Z_w=(Math.pow(0.4*Math.log10(reserves.T_sr()/reserves.T_kr())+0.73, P_u/reserves.P_kr())+0.1*P_u/reserves.P_kr());

        double speed=(0.52*averageWellFlowRate*reserves.T_u()*Z_w)/(8.57*8.57*P_u);


        if (
                new Double(P_z).isNaN()||
                new Double(P_u).isNaN()||
                new Double(Z_w).isNaN()||
                new Double(speed).isNaN()
        ) return iteration_well_count_growth(inYearLimit,reserves,numberOfWells,y,day,limit,P_u_limit);



        P_p_list.add(P_p);
        P_z_list.add(P_z);
        Z_n_list.add(Z_n);
        year_list.add((double) (y+year));
        well_list.add((double) numberOfWells);
        speed_list.add(speed);
        P_u_list.add(P_u);
        delta_P_list.add(P_p-P_z);
        avg_list.add(averageWellFlowRate);
        year_product_list.add((averageWellFlowRate*day*numberOfWells)/1000);


        System.out.println("YEAR = "+(year+y));
        System.out.println("YEAR production = "+limit);
        System.out.println("Iteration count = "+it.count);
        System.out.println("Percent of  reserve = "+(reserves.Q_n()+Q_otb)/reserves.V_o()*100);
        percent_list.add((reserves.Q_n()+Q_otb)/reserves.V_o()*100);
        System.out.println("Q_otb = "+Q_otb);
        System.out.println("Z_n = "+Z_n);
        System.out.println("P_p = "+P_p);
        System.out.println("Average flow rate = "+averageWellFlowRate);
        System.out.println("Number of wells = "+numberOfWells);
        System.out.println("P_z = "+P_z);
        System.out.println("ΔP = "+(P_p-P_z));
        System.out.println("P_u = "+P_u);
        System.out.println("Z_w = "+Z_w);
        System.out.println("Speed = "+speed);
        System.out.println("****************************");
        Q_otb=Q_otb+limit;



        return true;
    }



    public boolean iteration_well_count_growth(double inYearLimit,GasReserves reserves,
                                               int numberOfWells,int y, int day, double limit,
                                               double P_u_limit){



//        if (numberOfWells+1>reserves.well_const())
//            return iteration_production_decrease_P_u_constant(
//                    inYearLimit, reserves,
//                    numberOfWells, y,  day,  limit,
//                    P_u_limit);

        numberOfWells++;




        double Z_n=this.Z_n;
//        double Q_otb=this.Q_otb+inYearLimit;
        double P_p=((1-Q_otb/reserves.V_t())*this.P_p)*Z_n/this.Z_n;
//        System.out.println("Z_n = "+Z_n);
//        System.out.println("P_p = "+P_p);


        IT it = helper_ZnPp(Z_n, P_p, Q_otb,reserves);
        Z_n=it.value1;
        P_p=it.value2;
        if (P_p<=0) return false;




        double averageWellFlowRate=(limit/(day*numberOfWells))*1000;

        double P_z=Math.sqrt(P_p*P_p-(reserves.A()*averageWellFlowRate+reserves.B()*averageWellFlowRate*averageWellFlowRate));

        double P_u=Math.sqrt((P_z*P_z-averageWellFlowRate*averageWellFlowRate*reserves.teta())/reserves.eS());;

//        if (P_u<=P_u_limit) return iteration_production_decrease_P_u_constant(inYearLimit, reserves, numberOfWells, y,  day,  limit,P_u_limit);

        double Z_w=(Math.pow(0.4*Math.log10(reserves.T_sr()/reserves.T_kr())+0.73, P_u/reserves.P_kr())+0.1*P_u/reserves.P_kr());

        double speed=(0.52*averageWellFlowRate*reserves.T_u()*Z_w)/(8.57*8.57*P_u);


        if (
                new Double(P_z).isNaN()||
                        new Double(P_u).isNaN()||
                        new Double(Z_w).isNaN()||
                        new Double(speed).isNaN()
        ) return iteration_well_count_growth(inYearLimit,reserves,numberOfWells,y,day,limit,P_u_limit);



        P_p_list.add(P_p);
        P_z_list.add(P_z);
        Z_n_list.add(Z_n);
        year_list.add((double) (y+year));
        well_list.add((double) numberOfWells);
        speed_list.add(speed);
        P_u_list.add(P_u);
        delta_P_list.add(P_p-P_z);
        avg_list.add(averageWellFlowRate);
        year_product_list.add((averageWellFlowRate*day*numberOfWells)/1000);


        System.out.println("YEAR = "+(year+y));
        System.out.println("YEAR production = "+limit);
        System.out.println("Iteration count = "+it.count);
        System.out.println("Percent of  reserve = "+(reserves.Q_n()+Q_otb)/reserves.V_o()*100);
        percent_list.add((reserves.Q_n()+Q_otb)/reserves.V_o()*100);
        System.out.println("Q_otb = "+Q_otb);
        System.out.println("Z_n = "+Z_n);
        System.out.println("P_p = "+P_p);
        System.out.println("Average flow rate = "+averageWellFlowRate);
        System.out.println("Number of wells = "+numberOfWells);
        System.out.println("P_z = "+P_z);
        System.out.println("ΔP = "+(P_p-P_z));
        System.out.println("P_u = "+P_u);
        System.out.println("Z_w = "+Z_w);
        System.out.println("Speed = "+speed);
        System.out.println("****************************");
        Q_otb=Q_otb+limit;
        this.Z_n=Z_n;
        this.P_p=P_p;

        return true;
    }



    public boolean iteration_production_decrease_P_u_constant(double inYearLimit,GasReserves reserves,
                                                              int numberOfWells,int y, int day, double limit,
                                                              double P_u_limit){






        double Z_n=this.Z_n;
        double P_p=((1-Q_otb/reserves.V_t())*this.P_p)*Z_n/this.Z_n;


        IT it = helper_ZnPp(Z_n, P_p, Q_otb,reserves);
        Z_n=it.value1;
        P_p=it.value2;
        if (P_p<=0) return false;

        double averageWellFlowRate=(limit/(day*numberOfWells))*1000;

//        double P_z=Math.sqrt(P_p*P_p-(reserves.A()*averageWellFlowRate+reserves.B()*averageWellFlowRate*averageWellFlowRate));

        IT it2 = helper_AvgPz(averageWellFlowRate,P_p-reserves.delta_P_const(),P_p,reserves);
        averageWellFlowRate=it2.value1;
        P_z=it2.value2;


        double P_u=Math.sqrt((P_z*P_z-averageWellFlowRate*averageWellFlowRate*reserves.teta())/reserves.eS());;

        double Z_w=(Math.pow(0.4*Math.log10(reserves.T_sr()/reserves.T_kr())+0.73, P_u/reserves.P_kr())+0.1*P_u/reserves.P_kr());

        double speed=(0.52*averageWellFlowRate*reserves.T_u()*Z_w)/(8.57*8.57*P_u);


        if (speed>reserves.speed_z_const()) return iteration_production_decrease_speed_constant(inYearLimit,reserves,numberOfWells,y,day,limit,P_u_limit);



        if (
                new Double(P_z).isNaN()||
                        new Double(P_u).isNaN()||
                        new Double(Z_w).isNaN()||
                        new Double(speed).isNaN()
        ) return false;



        P_p_list.add(P_p);
        P_z_list.add(P_z);
        Z_n_list.add(Z_n);
        year_list.add((double) (y+year));
        well_list.add((double) numberOfWells);
        speed_list.add(speed);
        P_u_list.add(P_u);
        delta_P_list.add(P_p-P_z);
        avg_list.add(averageWellFlowRate);
        year_product_list.add((averageWellFlowRate*day*numberOfWells)/1000);

        System.out.println("YEAR = "+(year+y));
        System.out.println("YEAR production = "+(averageWellFlowRate*day*numberOfWells)/1000);
        System.out.println("Iteration count = "+it.count);
        System.out.println("Iteration count P_z avg = "+it2.count);
        System.out.println("Percent of  reserve = "+(reserves.Q_n()+Q_otb)/reserves.V_o()*100);
        percent_list.add((reserves.Q_n()+Q_otb)/reserves.V_o()*100);
        System.out.println("Q_otb = "+Q_otb);
        System.out.println("Z_n = "+Z_n);
        System.out.println("P_p = "+P_p);
        System.out.println("Average flow rate = "+averageWellFlowRate);
        System.out.println("Number of wells = "+numberOfWells);
        System.out.println("P_z = "+P_z);
        System.out.println("ΔP = "+(P_p-P_z));
        System.out.println("P_u = "+P_u);
        System.out.println("Z_w = "+Z_w);
        System.out.println("Speed = "+speed);
        System.out.println("****************************");
        Q_otb=Q_otb+limit;
        this.Z_n=Z_n;
        this.P_p=P_p;
        return true;
    }



    public boolean iteration_production_decrease_speed_constant(double inYearLimit,GasReserves reserves,
                                                              int numberOfWells,int y, int day, double limit,
                                                              double P_u_limit){




        double Z_n=this.Z_n;
//        double Q_otb=this.Q_otb+inYearLimit;
        double P_p=((1-Q_otb/reserves.V_t())*this.P_p)*Z_n/this.Z_n;
//        System.out.println("Z_n = "+Z_n);
//        System.out.println("P_p = "+P_p);

        IT it = helper_ZnPp(Z_n, P_p, Q_otb,reserves);
        Z_n=it.value1;
        P_p=it.value2;
        if (P_p<=0) return false;

        double averageWellFlowRate=(limit/(day*numberOfWells))*1000;

//        double P_z=Math.sqrt(P_p*P_p-(reserves.A()*averageWellFlowRate+reserves.B()*averageWellFlowRate*averageWellFlowRate));

        IT it2 = helper_AvgPz(averageWellFlowRate,P_p-reserves.delta_P_const(),P_p,reserves);
        averageWellFlowRate=it2.value1;
        double P_z=it2.value2;


        double P_u=Math.sqrt((P_z*P_z-averageWellFlowRate*averageWellFlowRate*reserves.teta())/reserves.eS());;

        double Z_w=(Math.pow(0.4*Math.log10(reserves.T_sr()/reserves.T_kr())+0.73, P_u/reserves.P_kr())+0.1*P_u/reserves.P_kr());

        double speed=(0.52*averageWellFlowRate*reserves.T_u()*Z_w)/(8.57*8.57*P_u);




        if (
                new Double(P_z).isNaN()||
                        new Double(P_u).isNaN()||
                        new Double(Z_w).isNaN()||
                        new Double(speed).isNaN()
//        ) return iteration_well_count_growth(inYearLimit,reserves,numberOfWells,y,day,limit,P_u_limit);
        ) return false;



        P_p_list.add(P_p);
        P_z_list.add(P_z);
        Z_n_list.add(Z_n);
        year_list.add((double) (y+year));
        well_list.add((double) numberOfWells);
        speed_list.add(speed);
        P_u_list.add(P_u);
        delta_P_list.add(P_p-P_z);
        avg_list.add(averageWellFlowRate);
        year_product_list.add((averageWellFlowRate*day*numberOfWells)/1000);

        System.out.println("YEAR = "+(year+y));
        System.out.println("YEAR production = "+(averageWellFlowRate*day*numberOfWells)/1000);
        System.out.println("Iteration count = "+it.count);
        System.out.println("Iteration count P_z avg = "+it2.count);
        System.out.println("Percent of  reserve = "+(reserves.Q_n()+Q_otb)/reserves.V_o()*100);
        percent_list.add((reserves.Q_n()+Q_otb)/reserves.V_o()*100);
        System.out.println("Q_otb = "+Q_otb);
        System.out.println("Z_n = "+Z_n);
        System.out.println("P_p = "+P_p);
        System.out.println("Average flow rate = "+averageWellFlowRate);
        System.out.println("Number of wells = "+numberOfWells);
        System.out.println("P_z = "+P_z);
        System.out.println("ΔP = "+(P_p-P_z));
        System.out.println("P_u = "+P_u);
        System.out.println("Z_w = "+Z_w);
        System.out.println("Speed = "+speed);
        System.out.println("****************************");
        Q_otb=Q_otb+limit;
        this.Z_n=Z_n;
        this.P_p=P_p;
        return true;
    }




    public IT helper_ZnPp(double Z_n,double P_p,double Q_otb,GasReserves reserves){
        int i;
        for (i = 0; i <1000 ; i++) {
            double temp_Z_n=Z_n;
            double temp_P_p=P_p;
            Z_n=(Math.pow(0.4*Math.log10(reserves.T_pl()/reserves.T_kr())+0.73, P_p/reserves.P_kr())+0.1*P_p/reserves.P_kr());
            P_p=((1-Q_otb/reserves.V_t())*this.P_p)*Z_n/this.Z_n;
            if (Math.abs(Z_n-temp_Z_n)<=0.001||Math.abs(P_p-temp_P_p)<=0.001) {
                break;
            }
        }
        return new IT(Z_n, P_p, i);
    }


    public IT helper_AvgPz(double avg,double P_z,double P_p,GasReserves reserves){
        if(P_z<0) new IT(0, 0, 0);
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
        return new IT(avg, P_z, i);
    }




    public IT helper_ZwPu(double Z_w,double P_u,double Q_otb,GasReserves reserves){
        int i;
        for (i = 0; i <1000 ; i++) {
            double temp_Z_n=Z_n;
            double temp_P_p=P_p;
            Z_n=(Math.pow(0.4*Math.log10(reserves.T_pl()/reserves.T_kr())+0.73, P_p/reserves.P_kr())+0.1*P_p/reserves.P_kr());
            P_p=((1-Q_otb/reserves.V_t())*this.P_p)*Z_n/this.Z_n;
            if (Math.abs(Z_n-temp_Z_n)<=0.001||Math.abs(P_p-temp_P_p)<=0.001) {
                break;
            }
        }
        return new IT(Z_n, P_p, i);
    }


    //1. Yillik otbor berilgan va o'zgarmaydi

//    public Forecast firstCondition(){
//
//    }



    //2. Yillik otbor o'zgarmaydi


    //1. Yillik otbor o'zgarmaydi


    //1. Yillik otbor o'zgarmaydi

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class IT{
        public double value1;
        public double value2;
        public int count;
    }


}
