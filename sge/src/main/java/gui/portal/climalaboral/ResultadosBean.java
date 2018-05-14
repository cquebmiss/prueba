package gui.portal.climalaboral;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.ItemSelectEvent;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;

import modelo.climalaboral.Encuesta;
import modelo.climalaboral.Pregunta;
import modelo.climalaboral.Respuesta;
import modelo.climalaboral.Seccion;
import util.climalaboral.UtilidadesClimaLaboral;

@SessionScoped
@ManagedBean
public class ResultadosBean
{
	private BarChartModel	modeloGrafica;
	private BarChartModel	modeloSecSeleccionada;

	private Encuesta		encuesta;
	private Seccion			secSeleccionada;

	//Lista que contiene las secciones que se representan en la gráfica, ya que pueden haber secciones que no entran en la gráfica por solo contener preguntas abiertas
	private List<Seccion>	secEnGrafica;

	//modoPanel 0 vista general, 1 detalle de sección
	private int				modoPanel;

	public ResultadosBean()
	{

	}

	@PostConstruct
	public void postConstruct()
	{
		this.modoPanel = 0;
		getResultadosFromBD();
		createBarModel();

	}

	public void getResultadosFromBD()
	{
		this.encuesta = UtilidadesClimaLaboral.getEncuestaFromBD();
		this.encuesta.initEncuesta();
		this.encuesta.getResultadosEncuesta();
	}

	private void createBarModel()
	{

		modeloGrafica = initBarModel();
		modeloGrafica.setAnimate(true);

		modeloGrafica.setTitle("Resultados Generales por Categoría");
		modeloGrafica.setLegendPosition("ne");

		Axis xAxis = modeloGrafica.getAxis(AxisType.X);
		xAxis.setLabel("Categoría");

		Axis yAxis = modeloGrafica.getAxis(AxisType.Y);
		yAxis.setLabel("Grado de Satisfacción");
		yAxis.setMin(0);
		yAxis.setMax(100);

	}

	private BarChartModel initBarModel()
	{
		BarChartModel model = new BarChartModel();

		this.secEnGrafica = new ArrayList<>();

		for (Seccion sec : this.encuesta.getSecciones())
		{
			ChartSeries secChart = new ChartSeries();
			secChart.setLabel(sec.getDescripcion());

			int totalPreguntasOpcionMultiple = 0;

			//totalRespuestas solo por cualquier detalle para un cálculo posterior lo dejaré
			int totalRespuestas = 0;
			int totalSiempre = 0;
			int porcentajeSatisfaccion = 0;

			for (Pregunta preg : sec.getPreguntas())
			{
				totalRespuestas += preg.getRespuestasResultados().size();

				if (preg.getTipoPregunta().getIdTipoPregunta() == 3)
				{
					totalPreguntasOpcionMultiple++;

					for (Respuesta resp : preg.getRespuestasResultados())
					{

						if (resp.getOpcion().getDescripcion().toLowerCase().contains("siempre"))
						{
							totalSiempre++;
						}

					}

				}

			}

			if (totalPreguntasOpcionMultiple > 0)
			{

				if (totalSiempre > 0)
				{
					porcentajeSatisfaccion = (totalSiempre * 100) / totalRespuestas;
				}

				secChart.set(" ", porcentajeSatisfaccion);
				secEnGrafica.add(sec);

				model.addSeries(secChart);

			}

		}

		return model;

	}

	private void createBarModelSeccion()
	{

		this.modeloSecSeleccionada = initBarModelSeccion();
		this.modeloSecSeleccionada.setAnimate(true);

		this.modeloSecSeleccionada.setTitle("");
		this.modeloSecSeleccionada.setLegendPosition("ne");

		Axis xAxis = this.modeloSecSeleccionada.getAxis(AxisType.X);
		xAxis.setLabel(" ");

		Axis yAxis = this.modeloSecSeleccionada.getAxis(AxisType.Y);
		yAxis.setLabel("Porcentaje");
		yAxis.setMin(0);
		yAxis.setMax(100);

	}

	private BarChartModel initBarModelSeccion()
	{
		BarChartModel model = new BarChartModel();

		int cont = 0;

		ChartSeries secChart = new ChartSeries();
		secChart.setLabel("Grado de Satisfacción");

		for (Pregunta preg : this.secSeleccionada.getPreguntas())
		{
			int totalRespuestas = 0;
			int totalSiempre = 0;
			int porcentajeSatisfaccion = 0;
			int totalPreguntasOpcionMultiple = 0;

			cont++;

			totalRespuestas += preg.getRespuestasResultados().size();

			if (preg.getTipoPregunta().getIdTipoPregunta() == 3)
			{
				totalPreguntasOpcionMultiple++;

				for (Respuesta resp : preg.getRespuestasResultados())
				{

					if (resp.getOpcion().getDescripcion().toLowerCase().contains("siempre"))
					{
						totalSiempre++;
					}

				}

			}

			if (totalPreguntasOpcionMultiple > 0)
			{

				if (totalSiempre > 0)
				{
					porcentajeSatisfaccion = (totalSiempre * 100) / totalRespuestas;
				}

				secChart.set(" " + cont, porcentajeSatisfaccion);

			}

		}

		model.addSeries(secChart);

		return model;

	}

	public void itemSelect(ItemSelectEvent event)
	{
		this.secSeleccionada = this.secEnGrafica.get(event.getSeriesIndex());
		this.modoPanel = 1;

		createBarModelSeccion();
	}

	public void actionRegresarAGraficaGeneral()
	{
		this.modoPanel = 0;
		this.modeloSecSeleccionada = null;
	}

	public BarChartModel getModeloGrafica()
	{
		return modeloGrafica;
	}

	public void setModeloGrafica(BarChartModel modeloGrafica)
	{
		this.modeloGrafica = modeloGrafica;
	}

	public Encuesta getEncuesta()
	{
		return encuesta;
	}

	public void setEncuesta(Encuesta encuesta)
	{
		this.encuesta = encuesta;
	}

	public Seccion getSecSeleccionada()
	{
		return secSeleccionada;
	}

	public void setSecSeleccionada(Seccion secSeleccionada)
	{
		this.secSeleccionada = secSeleccionada;
	}

	public BarChartModel getModeloSecSeleccionada()
	{
		return modeloSecSeleccionada;
	}

	public void setModeloSecSeleccionada(BarChartModel modeloSecSeleccionada)
	{
		this.modeloSecSeleccionada = modeloSecSeleccionada;
	}

	public List<Seccion> getSecEnGrafica()
	{
		return secEnGrafica;
	}

	public void setSecEnGrafica(List<Seccion> secEnGrafica)
	{
		this.secEnGrafica = secEnGrafica;
	}

	public int getModoPanel()
	{
		return modoPanel;
	}

	public void setModoPanel(int modoPanel)
	{
		this.modoPanel = modoPanel;
	}

}
