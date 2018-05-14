package modelo;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import gui.portal.nominas.GestionArchivosBancoBean;
import util.FacesUtils;

@FacesConverter("campoPlantillaConverter1")
public class CampoPlantillaConverter1 implements Converter
{

	@Override
	public Object getAsObject(FacesContext fc, UIComponent uic, String string)
	{
		GestionArchivosBancoBean gestionBean = (GestionArchivosBancoBean) FacesUtils
				.getManagedBean("gestionArchivosBancoBean");

		for (CampoPlantilla cam : gestionBean.getArchivoBanco().getPlantilla().getCampos())
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

}
