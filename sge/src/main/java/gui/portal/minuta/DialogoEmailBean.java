package gui.portal.minuta;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import modelo.minutas.Participante;

@ManagedBean
@ViewScoped
public class DialogoEmailBean
{
	private Participante participante;

	public DialogoEmailBean()
	{
		super();
	}

	@PostConstruct
	public void postConstruct()
	{

	}

	public void updateEmailParticipante()
	{
		//Actualiza tanto el email para el objeto persona como para el participante en sí mismo
		this.participante.getPersona().setEmail(this.participante.getEmail());
		this.participante.updateEmailPersona();
		this.participante.updateEmailParticipante();
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
