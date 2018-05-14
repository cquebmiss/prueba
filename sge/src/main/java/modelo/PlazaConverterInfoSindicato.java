/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import gui.portal.nominas.InfoSindicatoBean;
import util.FacesUtils;

/**
 *
 * @author desarolloyc
 */
@FacesConverter("plazaConverterInfoSindicato")
public class PlazaConverterInfoSindicato implements Converter
{

	@Override
	public Object getAsObject(FacesContext fc, UIComponent uic, String string)
	{
		InfoSindicatoBean infoSindiBean = (InfoSindicatoBean) FacesUtils.getManagedBean("infoSindicatoBean");

		if (infoSindiBean.getPlazas() == null)
		{
			return null;
		}

		if (string.equals("..."))
		{
			return null;
		}

		for (Plaza plaza : infoSindiBean.getPlazas())
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
