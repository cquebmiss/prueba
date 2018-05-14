package modelo;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

public class ConverterVinculoPlantilla implements Converter
{
	private List<VinculoPlantilla> catalogo;

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2)
	{

		if (this.catalogo == null || this.catalogo.isEmpty())
		{
			return null;

		}

		for (VinculoPlantilla obj : this.catalogo)
		{
			if (("" + obj.hashCode()).equalsIgnoreCase(arg2))
			{
				return obj;

			}
		}

		return null;
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2)
	{
		// TODO Auto-generated method stub
		if (arg2 != null)
		{
			return "" + arg2.hashCode();
		}

		return null;
	}

	public List<VinculoPlantilla> getCatalogo()
	{
		return catalogo;
	}

	public void setCatalogo(List<VinculoPlantilla> catalogo)
	{
		this.catalogo = catalogo;
	}

}
