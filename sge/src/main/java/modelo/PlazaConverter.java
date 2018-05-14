/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import gui.portal.siriBean;
import gui.portal.nominas.PlantillasBean;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import util.FacesUtils;

/**
 *
 * @author desarolloyc
 */
@FacesConverter("plazaConverter")
public class PlazaConverter implements Converter
{
    
    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string)
    {
        siriBean siriBean = (siriBean) FacesUtils.getManagedBean("siriBean");

        if (siriBean.getPlazas()== null)
        {
            return null;
        }

        if ( string.equals("...") )
        {
            return null;
        }
        
        for (Plaza plaza : siriBean.getPlazas())
        {
            if (plaza.hashCode() == Integer.parseInt(string)  )
            {
                return plaza;
            }
        }

        return null;
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o)
    {
        if (o != null)
        {
            return "" + o.hashCode();
        }

        return null;

    }
    
}
