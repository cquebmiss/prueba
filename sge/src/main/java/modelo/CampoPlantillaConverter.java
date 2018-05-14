package modelo;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import gui.portal.siriBean;
import util.FacesUtils;

public class CampoPlantillaConverter implements Converter
{
	List<CampoPlantilla> campos;

	public CampoPlantillaConverter()
	{
		super();

	}

	public CampoPlantillaConverter(List<CampoPlantilla> campos)
	{
		this.campos = campos;

	}

	@Override
	public Object getAsObject(FacesContext fc, UIComponent uic, String string)
	{

		for (CampoPlantilla cam : this.campos)
		{
			if (("" + cam.hashCode()).equalsIgnoreCase(string))
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

	public List<CampoPlantilla> getCampos()
	{
		return campos;
	}

	public void setCampos(List<CampoPlantilla> campos)
	{
		this.campos = campos;
	}

}
