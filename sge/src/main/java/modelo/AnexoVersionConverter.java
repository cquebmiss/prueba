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
@FacesConverter("anexoVersionConverter")
public class AnexoVersionConverter implements Converter
{
     @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string)
    {
        siriBean siriBean = (siriBean) FacesUtils.getManagedBean("siriBean");
        
        if ( siriBean.getAnexoSeleccionado() == null )
        {
            return null;
        }
        
        if ( siriBean.getAnexoSeleccionado().getVersiones()== null )
        {
            return null;
        }
        
        if ( ! string.equals("...") )
        {
            for( LayoutVersion anx : siriBean.getAnexoSeleccionado().getVersiones())
            {
                if ( anx.getVersion().equals(string) )
                {
                    return anx;
                }
            }
            
        }
        
        
        return null;
    }

    //EL método getAsString lo que realiza es convertir el objeto en una cadena para representar el valor ID
    //Es decir, si tengo mi arreglo de Objetos Artículos, yo necesito indicar con éste método que cadena va a representar el valor del Elemento
    //En éste caso sería el Id del Artículo
    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o)
    {   
        if (o != null )
        {
            return "" + ( (LayoutVersion) o ).getVersion();
        }

        return null;
        
    }
}