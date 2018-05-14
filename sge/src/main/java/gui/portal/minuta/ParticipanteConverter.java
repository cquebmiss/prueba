package gui.portal.minuta;

import java.util.Optional;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import modelo.minutas.Participante;
import util.FacesUtils;

@FacesConverter("participanteConverter")
public class ParticipanteConverter implements Converter
{
	// El método getAsObject, lo que hace es que el parámetro que envía al
	// método es el índice que definimos en el método getAsString
	// Por lo que en base a éste índice se va a devolver el objeto
	// correspondiente al valor seleccionado
	// Es decir en base al índice yo voy a buscar cual es el objeto que debo
	// devolver
	@Override
	public Object getAsObject(FacesContext fc, UIComponent uic, String string)
	{
		MinutaBean bean = (MinutaBean) FacesUtils.getManagedBean("minutaBean");

		if (bean.getResultadosAutoCompleteParticipante() == null)
		{
			return null;
		}

		Optional<Participante> res = bean.getResultadosAutoCompleteParticipante().stream()
				.filter(v -> v.getIdParticipante() == Integer.parseInt(string)).findFirst();

		return res.get();
	}

	// EL método getAsString lo que realiza es convertir el objeto en una cadena
	// para representar el valor ID
	// Es decir, si tengo mi arreglo de Objetos Artículos, yo necesito indicar
	// con éste método que cadena va a representar el valor del Elemento
	// En éste caso sería el Id del Artículo
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
