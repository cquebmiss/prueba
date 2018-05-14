/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import application.CatalogosBean;
import util.FacesUtils;

/**
 *
 * @author desarolloyc
 */
@FacesConverter("layoutIPConverter")
public class LayoutConverter implements Converter
{

	@Override
	public Object getAsObject(FacesContext fc, UIComponent uic, String string)
	{
		CatalogosBean catalogosBean = (CatalogosBean) FacesUtils.getManagedBean("catalogosBean");

		for (Layout lay : catalogosBean.getCatLayoutIP())
		{
			if (lay.hashCode() == Integer.parseInt(string))
			{
				return lay;
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
