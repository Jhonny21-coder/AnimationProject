package com.example.application.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;

@Route("calculator")
public class CalculatorView extends VerticalLayout {

    public CalculatorView() {
    	setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

	addClassName("register-form");

	H1 header = new H1("CALCULATOR");

	Button equalButton = new Button("=");
	Span resultText = new Span();
	Span result = new Span();

	NumberField firstNumber = new NumberField();
	NumberField secondNumber = new NumberField();

	ComboBox<String> operators = new ComboBox<>();
	operators.setPlaceholder("Select operator");
	operators.setItems("+", "-", "×", "÷");

	equalButton.addClickListener(event -> {
	    double firstValue =  firstNumber.getValue();
            double secondValue = secondNumber.getValue();
	    String operatorValue = operators.getValue();

	    result.setText("RESULT:");

	    switch(operatorValue){
	    	case "+":
	    	   resultText.setText("I miss youuuu");
	    	   break;
	    	case "-":
                   resultText.setText("I miss youuuu");
                   break;
            	case "×":
                   resultText.setText("I miss youuuu");
                   break;
            	case "÷":
                   resultText.setText("I miss youuuu");
                   break;
	    }
	});

        add(header, firstNumber, operators, secondNumber, equalButton, result, resultText);
     }
}
