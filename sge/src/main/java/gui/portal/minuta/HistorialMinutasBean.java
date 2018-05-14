package gui.portal.minuta;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import modelo.minutas.Minuta;
import util.minutas.UtilidadesMinutas;

@ManagedBean
@ViewScoped
public class HistorialMinutasBean
{

	private List<Minuta> minutas;
	private List<Minuta> minutasFilter;
	private Minuta minutaSelec;

	public HistorialMinutasBean()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	@PostConstruct
	public void postConstruct()
	{
		this.minutas = UtilidadesMinutas.getHistorialMinutas();

	}

	public List<Minuta> getMinutas()
	{
		return minutas;
	}

	public void setMinutas(List<Minuta> minutas)
	{
		this.minutas = minutas;
	}

	public List<Minuta> getMinutasFilter()
	{
		return minutasFilter;
	}

	public void setMinutasFilter(List<Minuta> minutasFilter)
	{
		this.minutasFilter = minutasFilter;
	}

	public Minuta getMinutaSelec()
	{
		return minutaSelec;
	}

	public void setMinutaSelec(Minuta minutaSelec)
	{
		this.minutaSelec = minutaSelec;
	}

}
