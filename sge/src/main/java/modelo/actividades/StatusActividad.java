package modelo.actividades;

public class StatusActividad
{
	private int idStatusActividad;
	private String descripcion;

	public StatusActividad(int idStatusActividad, String descripcion)
	{
		super();
		this.idStatusActividad = idStatusActividad;
		this.descripcion = descripcion;
	}

	public int getIdStatusActividad()
	{
		return idStatusActividad;
	}

	public void setIdStatusActividad(int idStatusActividad)
	{
		this.idStatusActividad = idStatusActividad;
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
