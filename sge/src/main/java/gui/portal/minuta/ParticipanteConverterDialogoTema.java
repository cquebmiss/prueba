package gui.portal.minuta;

import java.util.Optional;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import modelo.minutas.Participante;
import util.FacesUtils;

@FacesConverter("participanteConverterDialogoTema")
public class ParticipanteConverterDialogoTema implements Converter
{
	// El método getAsObject devuelve el objeto de acuerdo al índice definido anteriormene en el método getAsString
	@Override
	public Object getAsObject(FacesContext fc, UIComponent uic, String string)
	{
		MinutaBean bean = (MinutaBean) FacesUtils.getManagedBean("minutaBean");

		if (bean.getMinuta().getParticipantes() == null || bean.getMinuta().getParticipantes().isEmpty())
		{
			return null;
		}

		Optional<Participante> res = bean.getMinuta().getParticipantes().stream()
				.filter(v -> v.getIdParticipante() == Integer.parseInt(string)).findFirst();

		return res.get();
	}

	// EL método getAsString lo que realiza definir el id de cada elemento en una varible String
	@Override
	public String getAsString(FacesContext fc, UIComponent uic, Object o)
	{
		if (o != null)
		{
			return "" + ((Participante) o).getIdParticipante();
		}

		return null;

	}

}
