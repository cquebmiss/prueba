package gui.portal.minuta;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import modelo.minutas.Participante;

@ManagedBean
@ViewScoped
public class DialogoFirmarBean
{
	private Participante participante;

	public DialogoFirmarBean()
	{
		super();
	}

	@PostConstruct
	public void postConstruct()
	{
	}

	public Participante getParticipante()
	{
		return participante;
	}

	public void setParticipante(Participante participante)
	{
		this.participante = participante;
	}

}
