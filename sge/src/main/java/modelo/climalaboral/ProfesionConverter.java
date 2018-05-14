package modelo.climalaboral;

import java.util.Optional;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import gui.portal.climalaboral.PersistEncuestaBean;
import util.FacesUtils;

@FacesConverter("profesionConverter")
public class ProfesionConverter implements Converter
{
	@Override
	public Object getAsObject(FacesContext fc, UIComponent uic, String id)
	{
		PersistEncuestaBean bean = (PersistEncuestaBean) FacesUtils.getManagedBean("persistEncuestaBean");

		if (bean.getCatProfesiones() == null || bean.getCatProfesiones().isEmpty())
		{
			return null;
		}

		Optional<Profesion> res = bean.getCatProfesiones().stream()
				.filter(v -> v.getIdProfesion() == Integer.parseInt(id)).findFirst();

		return res.get();
	}

	@Override
	public String getAsString(FacesContext fc, UIComponent uic, Object o)
	{
		if (o != null)
		{
			return "" + ((Profesion) o).getIdProfesion();
		}

		return null;

	}

}
