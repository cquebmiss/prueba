package gui.portal.minuta;

import java.util.Optional;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import modelo.minutas.Compromiso;
import modelo.minutas.Participante;
import util.FacesUtils;

@FacesConverter("compromisoConverterDlgCompNoFinalizado")
public class CompromisoConverterDlgCompNoFinalizado implements Converter
{
	@Override
	public Object getAsObject(FacesContext fc, UIComponent uic, String id)
	{
		DialogoTemaBean bean = (DialogoTemaBean) FacesUtils.getManagedBean("dialogoTemaBean");

		if (bean.getCompromisosNoFinalizados() == null || bean.getCompromisosNoFinalizados().isEmpty())
		{
			return null;
		}

		Optional<Compromiso> res = bean.getCompromisosNoFinalizados().stream()
				.filter(v -> v.getIdCompromiso() == Integer.parseInt(id)).findFirst();

		return res.get();
	}

	@Override
	public String getAsString(FacesContext fc, UIComponent uic, Object o)
	{
		if (o != null)
		{
			return "" + ((Compromiso) o).getIdCompromiso();
		}

		return null;

	}

}
