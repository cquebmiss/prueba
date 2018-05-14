package modelo.climalaboral;

public class Jornada
{
	private int		idJornada;
	private String	descripcion;

	public Jornada()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public Jornada(int idJornada, String descripcion)
	{
		super();
		this.idJornada = idJornada;
		this.descripcion = descripcion;
	}

	public int getIdJornada()
	{
		return idJornada;
	}

	public void setIdJornada(int idJornada)
	{
		this.idJornada = idJornada;
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
