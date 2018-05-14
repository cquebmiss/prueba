package modelo.climalaboral;

public class Opcion
{
	private int idOpcion;
	private String descripcion;
	//El orden en que se mostrará en la barra de respuestas
	private int orden;
	private Pregunta pregunta;

	public Opcion()
	{
		// TODO Auto-generated constructor stub
		this.idOpcion = -1;
		this.descripcion = "";
		this.orden = -1;
	}

	public Opcion(int idOpcion, String descripcion, int orden, Pregunta pregunta)
	{
		super();
		this.idOpcion = idOpcion;
		this.descripcion = descripcion;
		this.orden = orden;
		this.pregunta = pregunta;
	}

	public int getIdOpcion()
	{
		return idOpcion;
	}

	public void setIdOpcion(int idOpcion)
	{
		this.idOpcion = idOpcion;
	}

	public String getDescripcion()
	{
		return descripcion;
	}

	public void setDescripcion(String descripcion)
	{
		this.descripcion = descripcion;
	}

	public int getOrden()
	{
		return orden;
	}

	public void setOrden(int orden)
	{
		this.orden = orden;
	}

	public Pregunta getPregunta()
	{
		return pregunta;
	}

	public void setPregunta(Pregunta pregunta)
	{
		this.pregunta = pregunta;
	}

}
