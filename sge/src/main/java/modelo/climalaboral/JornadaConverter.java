package modelo.climalaboral;

import java.util.Optional;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import gui.portal.climalaboral.PersistEncuestaBean;
import util.FacesUtils;

@FacesConverter("jornadaConverter")
public class JornadaConverter implements Converter
{
	@Override
	public Object getAsObject(FacesContext fc, UIComponent uic, String id)
	{
		PersistEncuestaBean bean = (PersistEncuestaBean) FacesUtils.getManagedBean("persistEncuestaBean");

		if (bean.getCatJornadas() == null || bean.getCatJornadas().isEmpty())
		{
			return null;
		}

		Optional<Jornada> res = bean.getCatJornadas().stream().filter(v -> v.getIdJornada() == Integer.parseInt(id))
				.findFirst();

		return res.get();
	}

	@Override
	public String getAsString(FacesContext fc, UIComponent uic, Object o)
	{
		if (o != null)
		{
			return "" + ((Jornada) o).getIdJornada();
		}

		return null;

	}

}
