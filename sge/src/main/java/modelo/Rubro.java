package modelo;

import java.math.BigDecimal;
import java.util.List;

public class Rubro
{
	private int					idRubro;
	private ReporteAnual		reporteAnual;
	private int					orden;
	private String				descripcion;
	private String				formula;

	private List<Columna>		columnas;
	private List<BigDecimal>	quincenas;

	public Rubro()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public Rubro(int idRubro, ReporteAnual reporteAnual, int orden, String descripcion, String formula,
			List<Columna> columnas, List<BigDecimal> quincenas)
	{
		super();
		this.idRubro = idRubro;
		this.reporteAnual = reporteAnual;
		this.orden = orden;
		this.descripcion = descripcion;
		this.formula = formula;
		this.columnas = columnas;
		this.quincenas = quincenas;
	}

	public int getIdRubro()
	{
		return idRubro;
	}

	public void setIdRubro(int idRubro)
	{
		this.idRubro = idRubro;
	}

	public ReporteAnual getReporteAnual()
	{
		return reporteAnual;
	}

	public void setReporteAnual(ReporteAnual reporteAnual)
	{
		this.reporteAnual = reporteAnual;
	}

	public int getOrden()
	{
		return orden;
	}

	public void setOrden(int orden)
	{
		this.orden = orden;
	}

	public String getFormula()
	{
		return formula;
	}

	public void setFormula(String formula)
	{
		this.formula = formula;
	}

	public List<Columna> getColumnas()
	{
		return columnas;
	}

	public void setColumnas(List<Columna> columnas)
	{
		this.columnas = columnas;
	}

	public List<BigDecimal> getQuincenas()
	{
		return quincenas;
	}

	public void setQuincenas(List<BigDecimal> quincenas)
	{
		this.quincenas = quincenas;
	}

	public String getDescripcion()
	{
		return descripcion;
	}

	public void setDescripcion(String descripcion)
	{
		this.descripcion = descripcion;
	}

}
