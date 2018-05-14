package modelo;

import java.io.Serializable;

public class TipoProducto implements Serializable, Cloneable
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	private int					idTipoProducto;
	private String				descripcion;

	public TipoProducto()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public TipoProducto(int idTipoProducto, String descripcion)
	{
		super();
		this.idTipoProducto = idTipoProducto;
		this.descripcion = descripcion;
	}

	@Override
	public Object clone() throws CloneNotSupportedException
	{
		return super.clone();

	}

	public int getIdTipoProducto()
	{
		return idTipoProducto;
	}

	public void setIdTipoProducto(int idTipoProducto)
	{
		this.idTipoProducto = idTipoProducto;
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
