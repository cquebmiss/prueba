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

@FacesConverter("plazaConverterPlantillas")
public class PlazaConverterPlantillas implements Converter
{

	@Override
	public Object getAsObject(FacesContext fc, UIComponent uic, String string)
	{
		PlantillasBean plantillasBean = (PlantillasBean) FacesUtils.getManagedBean("plantillasBean");

		if (plantillasBean.getPlazas() == null)
		{
			return null;
		}

		if (string.equals("..."))
		{
			return null;
		}

		for (Plaza plaza : plantillasBean.getPlazas())
		{
			if (plaza.hashCode() == Integer.parseInt(string))
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
