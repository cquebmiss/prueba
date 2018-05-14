package modelo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Columna implements Cloneable
{
	private int				idColumna;
	private ReporteAnual	reporteAnual;
	private int				orden;
	private boolean			antesDelDetalle;
	private String			descripcion;
	private String			formula;
	// 0 para String, 1 para numérico
	private int				tipoDato;

	private boolean			productos;
	private boolean			cancelados;
	private boolean			pension;
	private List<Concepto>	conceptosIncluidos;
	private BigDecimal		total;
	private String			conceptosSelecString;

	@Override
	public Object clone()
	{
		Columna clon = null;

		try
		{
			clon = (Columna) super.clone();

			if (this.conceptosIncluidos != null)
			{
				List<Concepto> conceptosClone = new ArrayList<>();

				for (Concepto con : this.conceptosIncluidos)
				{
					conceptosClone.add((Concepto) con.clone());

				}

				clon.setConceptosIncluidos(conceptosClone);

			}

			if (this.total != null)
			{
				this.total = new BigDecimal(this.total.toString());
			}

		} catch (CloneNotSupportedException e)
		{
			e.printStackTrace();
		}

		return clon;
	}

	public Columna()
	{
		super();
		// TODO Auto-generated constructor stub
		this.total = new BigDecimal("0.00");
	}

	public Columna(int idColumna, ReporteAnual reporteAnual, int orden, boolean antesDelDetalle, String descripcion,
			String formula, int tipoDato)
	{
		super();
		this.idColumna = idColumna;
		this.reporteAnual = reporteAnual;
		this.orden = orden;
		this.antesDelDetalle = antesDelDetalle;
		this.descripcion = descripcion;
		this.formula = formula;
		this.tipoDato = tipoDato;
		this.total = new BigDecimal("0.00");
	}

	public int getIdColumna()
	{
		return idColumna;
	}

	public void setIdColumna(int idColumna)
	{
		this.idColumna = idColumna;
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

	public boolean isAntesDelDetalle()
	{
		return antesDelDetalle;
	}

	public void setAntesDelDetalle(boolean antesDelDetalle)
	{
		this.antesDelDetalle = antesDelDetalle;
	}

	public String getDescripcion()
	{
		return descripcion;
	}

	public void setDescripcion(String descripcion)
	{
		this.descripcion = descripcion;
	}

	public String getFormula()
	{
		return formula;
	}

	public void setFormula(String formula)
	{
		this.formula = formula;
	}

	public int getTipoDato()
	{
		return tipoDato;
	}

	public void setTipoDato(int tipoDato)
	{
		this.tipoDato = tipoDato;
	}

	public List<Concepto> getConceptosIncluidos()
	{
		return conceptosIncluidos;
	}

	public void setConceptosIncluidos(List<Concepto> conceptosIncluidos)
	{
		this.conceptosIncluidos = conceptosIncluidos;
	}

	public BigDecimal getTotal()
	{
		return total;
	}

	public void setTotal(BigDecimal total)
	{
		this.total = total;
	}

	public boolean isProductos()
	{
		return productos;
	}

	public void setProductos(boolean productos)
	{
		this.productos = productos;
	}

	public boolean isCancelados()
	{
		return cancelados;
	}

	public void setCancelados(boolean cancelados)
	{
		this.cancelados = cancelados;
	}

	public boolean isPension()
	{
		return pension;
	}

	public void setPension(boolean pension)
	{
		this.pension = pension;
	}

	public String getConceptosSelecString()
	{
		return conceptosSelecString;
	}

	public void setConceptosSelecString(String conceptosSelecString)
	{
		this.conceptosSelecString = conceptosSelecString;
	}

}
