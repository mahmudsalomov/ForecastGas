package com.example.forecastgas.views;

import com.example.forecastgas.model.Forecast;
import com.example.forecastgas.model.Person;
import com.example.forecastgas.repository.PersonRepository;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;

@Route("/test")
public class ForecastView extends VerticalLayout {

    private PersonRepository personRepository;


    private TextField firstname=new TextField("Firstname");
    private TextField lastName=new TextField("Lastname");
    private EmailField email=new EmailField("email");


    private Grid<Forecast> grid=new Grid<>(Forecast.class);
//    private Binder<Forecast> binder=new Binder<>(Forecast.class);
    private List<Forecast> people=new ArrayList<>();

    public ForecastView(PersonRepository personRepository){
        this.personRepository=personRepository;
        grid.setColumns("firstname","lastname","email");
        add(getForm(),grid);
        refreshGrid();
    }

    public Component getForm(){
        var layout=new HorizontalLayout();
        var addButton=new Button("Add");
//        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
//        layout.setAlignItems(Alignment.BASELINE);
//
//        firstname.setWidthFull();
//
//        layout.add(firstname,lastName,email,addButton);
//
////        binder.bindInstanceFields(new Forecast());
//
//        addButton.addClickListener(click->{
//           try {
//               var person=new Forecast();
//               binder.writeBean(person);
//               people.add(person);
////               personRepository.save(person);
//               binder.readBean(new Forecast());
//               refreshGrid();
//           }catch (Exception e){
//               e.printStackTrace();
//           }
//        });

        return layout;
    }

    private void refreshGrid() {
//        List<Person> personList=personRepository.findAll();
//        System.out.println(personList);
        System.out.println(people);
        grid.setItems(people);
    }
}
