package gui.portal.nominas;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import modelo.Plaza;
import modelo.ReporteAnual;
import modelo.Unidad;
import util.utilidades;

@ManagedBean
@SessionScoped
public class FlujoEfectivoBean implements Serializable
{
	private List<ReporteAnual>	catReporteAnual;
	private List<ReporteAnual>	catReporteAnualFilter;
	private ReporteAnual		reporteAnualSelec;

	private ReporteAnual		nuevoEdicionReporteAnual;

	// 0 para visualizar solamente la tabla de reportes creados
	// 1 para visualizar la creación de un reporte
	// 2 para visualizar la edición de un reporte
	// 3 para visualizar y poder rellenar los rubros del reporte creado
	private int					statusInterfaz;
	private List<Unidad>		unidades;
	private List<Integer>		etapas;

	private List<Plaza>			catPlazas;

	public FlujoEfectivoBean()
	{
		// TODO Auto-generated constructor stub
		super();
		this.etapas = new ArrayList<>();
		this.etapas.add(1);
		this.etapas.add(2);

		this.catPlazas = utilidades.getPlazas();
		this.unidades = utilidades.getUnidades();

		setInterfaz(0);
	}

	public void setInterfaz(int tipoInterfaz)
	{
		this.statusInterfaz = tipoInterfaz;

		switch (tipoInterfaz)
		{

			case 0:

				this.catReporteAnual = utilidades.getReportesAnuales();

				break;

			case 1:
				this.nuevoEdicionReporteAnual = new ReporteAnual();

				break;

			case 2:
				this.nuevoEdicionReporteAnual = this.reporteAnualSelec.getClone();
				this.nuevoEdicionReporteAnual.updateEtapasReporte();
				this.nuevoEdicionReporteAnual.updateUnidadesReporte();
				this.nuevoEdicionReporteAnual.updateColumnas();
				this.nuevoEdicionReporteAnual.updateRubros();

				break;

			case 3:

				break;

		}

	}

	public void actionGuardarReporte()
	{

		this.nuevoEdicionReporteAnual.actionGuardarReporte();
		setInterfaz(0);

	}

	public List<ReporteAnual> getCatReporteAnual()
	{
		return catReporteAnual;
	}

	public void setCatReporteAnual(List<ReporteAnual> catReporteAnual)
	{
		this.catReporteAnual = catReporteAnual;
	}

	public List<ReporteAnual> getCatReporteAnualFilter()
	{
		return catReporteAnualFilter;
	}

	public void setCatReporteAnualFilter(List<ReporteAnual> catReporteAnualFilter)
	{
		this.catReporteAnualFilter = catReporteAnualFilter;
	}

	public ReporteAnual getReporteAnualSelec()
	{
		return reporteAnualSelec;
	}

	public void setReporteAnualSelec(ReporteAnual reporteAnualSelec)
	{
		this.reporteAnualSelec = reporteAnualSelec;
	}

	public int getStatusInterfaz()
	{
		return statusInterfaz;
	}

	public void setStatusInterfaz(int statusInterfaz)
	{
		this.statusInterfaz = statusInterfaz;
	}

	public ReporteAnual getNuevoEdicionReporteAnual()
	{
		return nuevoEdicionReporteAnual;
	}

	public void setNuevoEdicionReporteAnual(ReporteAnual nuevoEdicionReporteAnual)
	{
		this.nuevoEdicionReporteAnual = nuevoEdicionReporteAnual;
	}

	public List<Unidad> getUnidades()
	{
		return unidades;
	}

	public void setUnidades(List<Unidad> unidades)
	{
		this.unidades = unidades;
	}

	public List<Integer> getEtapas()
	{
		return etapas;
	}

	public void setEtapas(List<Integer> etapas)
	{
		this.etapas = etapas;
	}

	public List<Plaza> getCatPlazas()
	{
		return catPlazas;
	}

	public void setCatPlazas(List<Plaza> catPlazas)
	{
		this.catPlazas = catPlazas;
	}

}
