/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import gui.portal.siriBean;
import gui.portal.nominas.TrimestralesBean;
import gui.portal.nominas.ValidacionRFCBean;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import util.FacesUtils;

/**
 *
 * @author desarolloyc
 */
@FacesConverter("plazaConverterCorr")
public class PlazaConverterRFCCorr implements Converter
{

	@Override
	public Object getAsObject(FacesContext fc, UIComponent uic, String string)
	{
		ValidacionRFCBean validacionRFCBean = (ValidacionRFCBean) FacesUtils.getManagedBean("validacionRFCBean");

		if (validacionRFCBean.getPlazas() == null)
		{
			return null;
		}

		if (string.equals("..."))
		{
			return null;
		}

		for (Plaza plaza : validacionRFCBean.getPlazas())
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
