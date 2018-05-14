package modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ReporteVarios implements Serializable
{

	private List<UnidadRegistros>	unidades;
	private int						numeroRegistrosGeneral;
	private BigDecimal				total;
	private String					totalString;

	public ReporteVarios()
	{
		super();
		this.unidades = new ArrayList<>();
	}

	public ReporteVarios(List<UnidadRegistros> unidades)
	{
		super();
		this.unidades = unidades;
	}

	public List<UnidadRegistros> getUnidades()
	{
		return unidades;
	}

	public void setUnidades(List<UnidadRegistros> unidades)
	{
		this.unidades = unidades;
	}

	public BigDecimal getTotal()
	{
		return total;
	}

	public void setTotal(BigDecimal total)
	{
		this.total = total;
	}

	public String getTotalString()
	{
		return totalString;
	}

	public void setTotalString(String totalString)
	{
		this.totalString = totalString;
	}

	public int getNumeroRegistrosGeneral()
	{
		return numeroRegistrosGeneral;
	}

	public void setNumeroRegistrosGeneral(int numeroRegistrosGeneral)
	{
		this.numeroRegistrosGeneral = numeroRegistrosGeneral;
	}

}
