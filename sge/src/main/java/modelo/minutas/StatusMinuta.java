package modelo.minutas;

public class StatusMinuta
{

	private int idStatusMinuta;
	private String descripcion;

	public StatusMinuta(int idStatusMinuta, String descripcion)
	{
		super();
		this.idStatusMinuta = idStatusMinuta;
		this.descripcion = descripcion;
	}

	public int getIdStatusMinuta()
	{
		return idStatusMinuta;
	}

	public void setIdStatusMinuta(int idStatusMinuta)
	{
		this.idStatusMinuta = idStatusMinuta;
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
