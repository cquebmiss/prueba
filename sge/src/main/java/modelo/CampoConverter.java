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
@FacesConverter("campoConverter")
public class CampoConverter implements Converter
{

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string)
    {
        siriBean siriBean = (siriBean) FacesUtils.getManagedBean("siriBean");

        if (siriBean.getCamposDualPlantillaAnexo() == null)
        {
            return null;
        }

        for (CampoPlantilla cam : siriBean.getCamposDualPlantillaAnexo().getSource())
        {
            if (cam.hashCode() == Integer.parseInt(string) )
            {
                return cam;
            }
        }
        
        for (CampoPlantilla cam : siriBean.getCamposDualPlantillaAnexo().getTarget())
        {
            if (cam.hashCode() == Integer.parseInt(string)  )
            {
                return cam;
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
