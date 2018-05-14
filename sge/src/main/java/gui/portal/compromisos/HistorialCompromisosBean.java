package gui.portal.compromisos;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import modelo.minutas.Compromiso;
import util.minutas.UtilidadesMinutas;

@ManagedBean
@SessionScoped
public class HistorialCompromisosBean
{
	private List<Compromiso> compromisos;
	private List<Compromiso> compromisosFilter;
	private Compromiso compromisoSelec;

	public HistorialCompromisosBean()
	{
		// TODO Auto-generated constructor stub
	}

	@PostConstruct
	public void postConstruct()
	{
		this.compromisos = UtilidadesMinutas.getHistorialCompromisosBD(false);
	}

	public List<Compromiso> getCompromisos()
	{
		return compromisos;
	}

	public void setCompromisos(List<Compromiso> compromisos)
	{
		this.compromisos = compromisos;
	}

	public List<Compromiso> getCompromisosFilter()
	{
		return compromisosFilter;
	}

	public void setCompromisosFilter(List<Compromiso> compromisosFilter)
	{
		this.compromisosFilter = compromisosFilter;
	}

	public Compromiso getCompromisoSelec()
	{
		return compromisoSelec;
	}

	public void setCompromisoSelec(Compromiso compromisoSelec)
	{
		this.compromisoSelec = compromisoSelec;
	}

}
