package modelo;

import java.io.Serializable;

public class TipoNomina implements Serializable, Cloneable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private int					idTipoNomina;
	private String				descripcion;

	public TipoNomina()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public TipoNomina(int idTipoNomina, String descripcion)
	{
		super();
		this.idTipoNomina = idTipoNomina;
		this.descripcion = descripcion;
	}

	@Override
	public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}

	public int getIdTipoNomina()
	{
		return idTipoNomina;
	}

	public void setIdTipoNomina(int idTipoNomina)
	{
		this.idTipoNomina = idTipoNomina;
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
