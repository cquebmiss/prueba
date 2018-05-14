package modelo;

import java.math.BigDecimal;

public class Status implements Cloneable
{
	private int		idStatus;
	private String	descripcion;

	public Status()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object clone()
	{
		Status clon = null;
		try
		{
			clon = (Status) super.clone();
		} catch (CloneNotSupportedException e)
		{
			e.printStackTrace();
		}

		return clon;

	}

	public Status(int idStatus, String descripcion)
	{
		super();
		this.idStatus = idStatus;
		this.descripcion = descripcion;
	}

	public int getIdStatus()
	{
		return idStatus;
	}

	public void setIdStatus(int idStatus)
	{
		this.idStatus = idStatus;
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
