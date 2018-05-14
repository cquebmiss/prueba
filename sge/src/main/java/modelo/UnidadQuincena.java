package modelo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class UnidadQuincena extends Unidad
{
	private BigDecimal				percepciones;
	private BigDecimal				deducciones;
	private BigDecimal				liquido;

	private Map<String, BigDecimal>	sumarizacionConceptos;
	// Columnas para reporte personalizado
	private List<Columna>			columnas;

	private BigDecimal				percepcionesCancelados;
	private BigDecimal				deduccionesCancelados;
	private BigDecimal				liquidoCancelados;

	private BigDecimal				percepcionesPension;
	private BigDecimal				deduccionesPension;
	private BigDecimal				liquidoPension;

	private BigDecimal				percepcionesCanceladosPension;
	private BigDecimal				deduccionesCanceladosPension;
	private BigDecimal				liquidoCanceladosPension;

	public UnidadQuincena(int idUnidad, String descripcion)
	{
		super(idUnidad, descripcion);

		// TODO Auto-generated constructor stub
		this.percepciones = new BigDecimal("0.00");
		this.deducciones = new BigDecimal("0.00");
		this.liquido = new BigDecimal("0.00");

		this.percepcionesCancelados = new BigDecimal("0.00");
		this.deduccionesCancelados = new BigDecimal("0.00");
		this.liquidoCancelados = new BigDecimal("0.00");

		this.percepcionesPension = new BigDecimal("0.00");
		this.deduccionesPension = new BigDecimal("0.00");
		this.liquidoPension = new BigDecimal("0.00");

		this.percepcionesCanceladosPension = new BigDecimal("0.00");
		this.deduccionesCanceladosPension = new BigDecimal("0.00");
		this.liquidoCanceladosPension = new BigDecimal("0.00");
	}

	public void addPercepcionesDeducciones(BigDecimal percep, BigDecimal deduc)
	{
		this.percepciones = this.percepciones.add(percep);
		this.deducciones = this.deducciones.add(deduc);
		this.liquido = this.percepciones.subtract(this.deducciones);

	}

	public void addPercepcionesDeduccionesCancelados(BigDecimal percep, BigDecimal deduc)
	{
		this.percepcionesCancelados = this.percepcionesCancelados.add(percep);
		this.deduccionesCancelados = this.deduccionesCancelados.add(deduc);
		this.liquidoCancelados = this.percepcionesCancelados.subtract(this.deduccionesCancelados);

	}

	public void addPercepcionesDeduccionesPension(BigDecimal percep, BigDecimal deduc)
	{
		this.percepcionesPension = this.percepcionesPension.add(percep);
		this.deduccionesPension = this.deduccionesPension.add(deduc);
		this.liquidoPension = this.percepcionesPension.subtract(this.deduccionesPension);

	}

	public void addPercepcionesDeduccionesCanceladosPension(BigDecimal percep, BigDecimal deduc)
	{
		this.percepcionesCanceladosPension = this.percepcionesCanceladosPension.add(percep);
		this.deduccionesCanceladosPension = this.deduccionesCanceladosPension.add(deduc);
		this.liquidoCanceladosPension = this.percepcionesCanceladosPension.subtract(this.deduccionesCanceladosPension);
		
	}

	public BigDecimal getPercepciones()
	{
		return percepciones;
	}

	public void setPercepciones(BigDecimal percepciones)
	{
		this.percepciones = percepciones;
	}

	public BigDecimal getDeducciones()
	{
		return deducciones;
	}

	public void setDeducciones(BigDecimal deducciones)
	{
		this.deducciones = deducciones;
	}

	public BigDecimal getLiquido()
	{
		return liquido;
	}

	public void setLiquido(BigDecimal liquido)
	{
		this.liquido = liquido;
	}

	public Map<String, BigDecimal> getSumarizacionConceptos()
	{
		return sumarizacionConceptos;
	}

	public void setSumarizacionConceptos(Map<String, BigDecimal> sumarizacionConceptos)
	{
		this.sumarizacionConceptos = sumarizacionConceptos;
	}

	public BigDecimal getPercepcionesCancelados()
	{
		return percepcionesCancelados;
	}

	public void setPercepcionesCancelados(BigDecimal percepcionesCancelados)
	{
		this.percepcionesCancelados = percepcionesCancelados;
	}

	public BigDecimal getDeduccionesCancelados()
	{
		return deduccionesCancelados;
	}

	public void setDeduccionesCancelados(BigDecimal deduccionesCancelados)
	{
		this.deduccionesCancelados = deduccionesCancelados;
	}

	public BigDecimal getLiquidoCancelados()
	{
		return liquidoCancelados;
	}

	public void setLiquidoCancelados(BigDecimal liquidoCancelados)
	{
		this.liquidoCancelados = liquidoCancelados;
	}

	public List<Columna> getColumnas()
	{
		return columnas;
	}

	public void setColumnas(List<Columna> columnas)
	{
		this.columnas = columnas;
	}

	public BigDecimal getPercepcionesPension()
	{
		return percepcionesPension;
	}

	public void setPercepcionesPension(BigDecimal percepcionesPension)
	{
		this.percepcionesPension = percepcionesPension;
	}

	public BigDecimal getDeduccionesPension()
	{
		return deduccionesPension;
	}

	public void setDeduccionesPension(BigDecimal deduccionesPension)
	{
		this.deduccionesPension = deduccionesPension;
	}

	public BigDecimal getLiquidoPension()
	{
		return liquidoPension;
	}

	public void setLiquidoPension(BigDecimal liquidoPension)
	{
		this.liquidoPension = liquidoPension;
	}

	public BigDecimal getPercepcionesCanceladosPension()
	{
		return percepcionesCanceladosPension;
	}

	public void setPercepcionesCanceladosPension(BigDecimal percepcionesCanceladosPension)
	{
		this.percepcionesCanceladosPension = percepcionesCanceladosPension;
	}

	public BigDecimal getDeduccionesCanceladosPension()
	{
		return deduccionesCanceladosPension;
	}

	public void setDeduccionesCanceladosPension(BigDecimal deduccionesCanceladosPension)
	{
		this.deduccionesCanceladosPension = deduccionesCanceladosPension;
	}

	public BigDecimal getLiquidoCanceladosPension()
	{
		return liquidoCanceladosPension;
	}

	public void setLiquidoCanceladosPension(BigDecimal liquidoCanceladosPension)
	{
		this.liquidoCanceladosPension = liquidoCanceladosPension;
	}

}
