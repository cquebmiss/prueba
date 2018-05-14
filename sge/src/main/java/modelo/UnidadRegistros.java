package modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class UnidadRegistros extends Unidad implements Serializable, Cloneable
{

	private static final long		serialVersionUID	= 1L;
	// Se encuentran los registros DAT con cada concepto que se pida en el
	// reporte Repv
	private Plaza					plaza;
	private List<PlantillaRegistro>	registrosDAT;
	private BigDecimal				total;
	private String					totalString;

	public UnidadRegistros(Plaza plaza, int idUnidad, String descripcion)
	{
		super(idUnidad, descripcion);
		this.plaza = plaza;
		this.registrosDAT = new ArrayList<>();
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object clone()
	{
		UnidadRegistros clon = null;

		clon = (UnidadRegistros) super.clone();

		if (this.plaza != null)
		{
			clon.setPlaza((Plaza) this.plaza.clone());
		}

		if (this.registrosDAT != null)
		{
			List<PlantillaRegistro> registrosDATClon = new ArrayList<>();

			this.registrosDAT.forEach(item ->
			{
				registrosDATClon.add((PlantillaRegistro) item.clone());
			});

			clon.setRegistrosDAT(registrosDATClon);
		}

		if (this.total != null)
		{
			clon.setTotal(new BigDecimal(this.total.toString()));
		}

		return clon;

	}

	public List<PlantillaRegistro> getRegistrosDAT()
	{
		return registrosDAT;
	}

	public void setRegistrosDAT(List<PlantillaRegistro> registrosDAT)
	{
		this.registrosDAT = registrosDAT;
	}

	public Plaza getPlaza()
	{
		return plaza;
	}

	public void setPlaza(Plaza plaza)
	{
		this.plaza = plaza;
	}

	public static long getSerialversionuid()
	{
		return serialVersionUID;
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

}
