package modelo.actividades;

public class Actividad
{
	private int idActividad;
	private String descripcion;
	private StatusActividad statusActividad;

	public Actividad(int idActividad, String descripcion, StatusActividad statusActividad)
	{
		super();
		this.idActividad = idActividad;
		this.descripcion = descripcion;
		this.statusActividad = statusActividad;
	}

	public int getIdActividad()
	{
		return idActividad;
	}

	public void setIdActividad(int idActividad)
	{
		this.idActividad = idActividad;
	}

	public String getDescripcion()
	{
		return descripcion;
	}

	public void setDescripcion(String descripcion)
	{
		this.descripcion = descripcion;
	}

	public StatusActividad getStatusActividad()
	{
		return statusActividad;
	}

	public void setStatusActividad(StatusActividad statusActividad)
	{
		this.statusActividad = statusActividad;
	}

}
