package com.example.forecastgas.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Builder
public class Forecast {

    public int god;
    public double Q_za_god;
    public double Q_otb;
    public double Q_nach;
    public double ot_zap;
    public double sredniy_debit_skvajina;
    public int kol_skvajina;
    public double kol_den;
    public double Z_plasta;
    public double P_plasta;
    public double P_zaboy;
    public double V_zaboy;
    public double P_delta;
    public double P_ustivoy;
    public double Z_ustivoy;
    public double V_ustivoy;
    public GasReserves reserves;


    public Forecast(int god, double Q_za_god, double Q_nach, int kol_skvajina,double kol_den, double P_plasta, GasReserves reserves) {
        this.reserves=reserves;
        this.god = god;
        this.Q_za_god = Q_za_god;
        this.Q_nach = Q_nach;
        this.kol_skvajina = kol_skvajina;
        this.P_plasta = P_plasta;
        this.kol_den=kol_den;
//        this.reserves = reserves;

        this.ot_zap=(Q_nach/reserves.V_o())*100;
        this.sredniy_debit_skvajina=((Q_za_god/kol_den)/kol_skvajina)*1000;
        this.P_zaboy=Math.sqrt(P_plasta*P_plasta-(reserves.A()*sredniy_debit_skvajina+reserves.B()*sredniy_debit_skvajina*sredniy_debit_skvajina));

        this.sredniy_debit_skvajina=(
                Math.sqrt(reserves.A()*reserves.A()+4*reserves.B()*(P_plasta*P_plasta-P_zaboy*P_zaboy))
                        -reserves.A())
                /(2*reserves.B());



        this.P_delta=P_plasta-P_zaboy;
        this.P_ustivoy=Math.sqrt((P_zaboy*P_zaboy-sredniy_debit_skvajina*sredniy_debit_skvajina*reserves.teta())/reserves.eS());


        this.Z_plasta=(Math.pow(0.4*Math.log10(reserves.T_pl()/reserves.T_kr())+0.73, P_plasta/reserves.P_kr())+0.1*P_plasta/reserves.P_kr());


        this.Z_ustivoy=(Math.pow(0.4*Math.log10(reserves.T_sr()/reserves.T_kr())+0.73, P_ustivoy/reserves.P_kr())+0.1*P_ustivoy/reserves.P_kr());



        this.V_ustivoy=(0.52*sredniy_debit_skvajina*reserves.T_u()*Z_ustivoy)/(8.57*8.57*P_ustivoy);
        this.V_zaboy=(0.52*sredniy_debit_skvajina*reserves.T_u()*Z_ustivoy)/(8.57*8.57*P_zaboy);

    }






    public Forecast next(double kol_den){
//        System.out.println(reserves);
//        System.out.println(reserves.P_kr());

        double Z_plasta=this.Z_plasta;

        double P_plasta=((1-Q_otb/reserves.V_t())*this.P_plasta)*Z_plasta/this.Z_plasta;


        ForecastTest.IT it = helper_ZpPp(Z_plasta, P_plasta, Q_otb,reserves);
        Z_plasta=it.value1;
        P_plasta=it.value2;
        if (P_plasta<=0) return null;




        double sredniy_debit_skvajina=(reserves.Q_const()/(kol_den*kol_skvajina))*1000;

        double P_zaboy=Math.sqrt(P_plasta*P_plasta-(reserves.A()*sredniy_debit_skvajina+reserves.B()*sredniy_debit_skvajina*sredniy_debit_skvajina));

        double P_ustivoy=Math.sqrt((P_zaboy*P_zaboy-sredniy_debit_skvajina*sredniy_debit_skvajina*reserves.teta())/reserves.eS());;

//        if (P_u<=P_u_limit) return iteration_production_decrease_P_u_constant(inYearLimit, reserves, numberOfWells, y,  day,  limit,P_u_limit);

        double Z_ustivoy=(Math.pow(0.4*Math.log10(reserves.T_sr()/reserves.T_kr())+0.73, P_ustivoy/reserves.P_kr())+0.1*P_ustivoy/reserves.P_kr());

//        double speed=(0.52*sredniy_debit_skvajina*reserves.T_u()*Z_w)/(8.57*8.57*P_u);
        double V_ustivoy=(0.52*sredniy_debit_skvajina*reserves.T_u()*Z_ustivoy)/(8.57*8.57*P_ustivoy);
        double V_zaboy=(0.52*sredniy_debit_skvajina*reserves.T_u()*Z_ustivoy)/(8.57*8.57*P_zaboy);
        double P_delta=P_plasta-P_zaboy;

        if (
                new Double(P_zaboy).isNaN()||
                        new Double(P_ustivoy).isNaN()||
                        new Double(Z_ustivoy).isNaN()||
                        new Double(V_ustivoy).isNaN()||
                        new Double(V_zaboy).isNaN()
        ) return next(kol_den,kol_skvajina+1);

//        double Q_za_god=sredniy_debit_skvajina*kol_den*sredniy_debit_skvajina;
        double Q_za_god=reserves.Q_const();

        double Q_nach=(this.Q_nach+Q_za_god);
        double ot_zap=(Q_nach/reserves.V_o())*100;

        return Forecast
                .builder()
                .god(god+1)
                .Q_za_god(Q_za_god)
                .Q_otb(Q_otb+Q_za_god)
                .Q_nach(Q_nach)
                .ot_zap(ot_zap)
                .sredniy_debit_skvajina(sredniy_debit_skvajina)
                .kol_skvajina(kol_skvajina)
                .kol_den(kol_den)
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


    public Forecast next(double kol_den,int kol_skvajina){
//        System.out.println(reserves);
//        System.out.println(reserves.P_kr());


        double Z_plasta=this.Z_plasta;

        double P_plasta=((1-Q_otb/reserves.V_t())*this.P_plasta)*Z_plasta/this.Z_plasta;


        ForecastTest.IT it = helper_ZpPp(Z_plasta, P_plasta, Q_otb,reserves);
        Z_plasta=it.value1;
        P_plasta=it.value2;
        if (P_plasta<=0) return null;



        double sredniy_debit_skvajina=(reserves.Q_const()/(kol_den*kol_skvajina))*1000;

        double P_zaboy=0;
        if (kol_skvajina>reserves.well_const()) {

            ForecastTest.IT it2 = helper_AvgPz(sredniy_debit_skvajina,P_plasta-reserves.delta_P_const(),P_plasta,reserves);
            sredniy_debit_skvajina=it2.value1;
            P_zaboy=it2.value2;


        }
        else {
            P_zaboy=Math.sqrt(P_plasta*P_plasta-(reserves.A()*sredniy_debit_skvajina+reserves.B()*sredniy_debit_skvajina*sredniy_debit_skvajina));
        }



        double P_ustivoy=Math.sqrt((P_zaboy*P_zaboy-sredniy_debit_skvajina*sredniy_debit_skvajina*reserves.teta())/reserves.eS());;

//        if (P_u<=P_u_limit) return iteration_production_decrease_P_u_constant(inYearLimit, reserves, numberOfWells, y,  day,  limit,P_u_limit);

        double Z_ustivoy=(Math.pow(0.4*Math.log10(reserves.T_sr()/reserves.T_kr())+0.73, P_ustivoy/reserves.P_kr())+0.1*P_ustivoy/reserves.P_kr());

//        double speed=(0.52*sredniy_debit_skvajina*reserves.T_u()*Z_w)/(8.57*8.57*P_u);
        double V_ustivoy=(0.52*sredniy_debit_skvajina*reserves.T_u()*Z_ustivoy)/(8.57*8.57*P_ustivoy);
        double V_zaboy=(0.52*sredniy_debit_skvajina*reserves.T_u()*Z_ustivoy)/(8.57*8.57*P_zaboy);
        double P_delta=P_plasta-P_zaboy;

        if (
                new Double(P_zaboy).isNaN()||
                        new Double(P_ustivoy).isNaN()||
                        new Double(Z_ustivoy).isNaN()||
                        new Double(V_ustivoy).isNaN()||
                        new Double(V_zaboy).isNaN()
        ) return next(kol_den,kol_skvajina+1);

//        double Q_za_god=sredniy_debit_skvajina*kol_den*kol_skvajina/1000;
        double Q_za_god=reserves.Q_const();

        double Q_nach=(this.Q_nach+Q_za_god);
        double ot_zap=(Q_nach/reserves.V_o())*100;

        return Forecast
                .builder()
                .god(god+1)
                .Q_za_god(Q_za_god)
                .Q_otb(Q_otb+Q_za_god)
                .Q_nach(Q_nach)
                .ot_zap(ot_zap)
                .sredniy_debit_skvajina(sredniy_debit_skvajina)
                .kol_skvajina(kol_skvajina)
                .kol_den(kol_den)
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




    public ForecastTest.IT helper_ZpPp(double Z_plasta, double P_plasta, double Q_otb, GasReserves reserves){
        int i;
        for (i = 0; i <1000 ; i++) {
            double temp_Z_plasta=Z_plasta;
            double temp_P_plasta=P_plasta;
            Z_plasta=(Math.pow(0.4*Math.log10(reserves.T_pl()/reserves.T_kr())+0.73, P_plasta/reserves.P_kr())+0.1*P_plasta/reserves.P_kr());
            P_plasta=((1-Q_otb/reserves.V_t())*this.P_plasta)*Z_plasta/this.Z_plasta;
            if (Math.abs(Z_plasta-temp_Z_plasta)<=0.001||Math.abs(P_plasta-temp_P_plasta)<=0.001) {
                break;
            }
        }
//        System.out.println("ITERATION count = "+i);
        return new ForecastTest.IT(Z_plasta, P_plasta, i);
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



    public void next(){
        System.out.println();
    }

}
