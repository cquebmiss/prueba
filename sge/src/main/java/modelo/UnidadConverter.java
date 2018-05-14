/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import gui.portal.siriBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import util.FacesUtils;

/**
 *
 * @author desarolloyc
 */
@FacesConverter("unidadConverter")
public class UnidadConverter implements Converter
{
    
    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string)
    {
        siriBean siriBean = (siriBean) FacesUtils.getManagedBean("siriBean");

        if (siriBean.getPlazaSeleccionada() == null || siriBean.getPlazaSeleccionada().getUnidades() == null)
        {
            return null;
        }

        
        if ( string.equals("...") )
        {
            return null;
        }
        
        for (Unidad unidad : siriBean.getPlazaSeleccionada().getUnidades())
        {
            if (unidad.hashCode() == Integer.parseInt(string)  )
            {
                return unidad;
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
