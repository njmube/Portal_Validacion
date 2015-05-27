/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iqtb.validacion.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;



/**
 *
 * @author danielromero
 */

@FacesValidator(value = "validarVacio")
public class ValidarVacio implements Validator{

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String label;
        String valor = (String)value;
        HtmlInputText htmlInputText = (HtmlInputText) component;
        
        if (htmlInputText.getLabel() == null || htmlInputText.getLabel().trim().equals("")) {
            label = htmlInputText.getId();
        } else {
            label = htmlInputText.getLabel();
        }
        
        if (valor == null || valor.trim().isEmpty()) {
//            System.out.println("ENTRO IF");
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", label + ": es un campo requerido."));
        }else{
//            System.out.println("ENTRO ELSE");
        }
        
    }
    
}
