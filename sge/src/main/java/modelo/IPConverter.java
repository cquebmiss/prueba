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

import gui.portal.siriBean;
import util.FacesUtils;

/**
 *
 * @author desarolloyc
 */
@FacesConverter("ipConverter")
public class IPConverter implements Converter
{
	private List<InstrumentoPago> catInstrumentoPago;

	@Override
	public Object getAsObject(FacesContext fc, UIComponent uic, String string)
	{

		for (InstrumentoPago ip : this.catInstrumentoPago)
		{
			if (ip.hashCode() == Integer.parseInt(string))
			{
				return ip;
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

	public List<InstrumentoPago> getCatInstrumentoPago()
	{
		return catInstrumentoPago;
	}

	public void setCatInstrumentoPago(List<InstrumentoPago> catInstrumentoPago)
	{
		this.catInstrumentoPago = catInstrumentoPago;
	}

}
