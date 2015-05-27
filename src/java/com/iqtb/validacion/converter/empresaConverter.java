/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iqtb.validacion.converter;

import com.iqtb.validacion.pojo.Empresas;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import org.primefaces.component.picklist.PickList;
import org.primefaces.model.DualListModel;

/**
 *
 * @author danielromero
 */
@FacesConverter("empresasPickListConverter")
public class empresaConverter implements Converter{

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        return getObjectFromUIPickListComponent(component,value);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        String string;
//        LOG.trace("Object value: {}", object);
        System.out.println("Object value: {}"+ value);
        if(value == null){
            string="";
        }else{
            try{
                string = String.valueOf(((Empresas)value).getIdEmpresa());
            }catch(ClassCastException cce){
                throw new ConverterException();
            }
        }
        return string;
    }
    
    @SuppressWarnings("unchecked")
    private Empresas getObjectFromUIPickListComponent(UIComponent component, String value) {
        final DualListModel<Empresas> dualList;
        try{
            dualList = (DualListModel<Empresas>) ((PickList)component).getValue();
            Empresas empresa = getObjectFromList(dualList.getSource(),Integer.valueOf(value));
            if(empresa==null){
                empresa = getObjectFromList(dualList.getTarget(),Integer.valueOf(value));
            }
             
            return empresa;
        }catch(ClassCastException cce){
            throw new ConverterException();
        }catch(NumberFormatException nfe){
            throw new ConverterException();
        }
    }
 
    private Empresas getObjectFromList(final List<?> list, final Integer identifier) {
        for(final Object object:list){
            final Empresas empresa = (Empresas) object;
            if(empresa.getIdEmpresa().equals(identifier)){
                return empresa;
            }
        }
        return null;
    }
    
}
