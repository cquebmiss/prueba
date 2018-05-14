package modelo.climalaboral;

public class Area
{
	private int		idArea;
	private String	descripcion;

	public Area()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public Area(int idArea, String descripcion)
	{
		super();
		this.idArea = idArea;
		this.descripcion = descripcion;
	}

	public int getIdArea()
	{
		return idArea;
	}

	public void setIdArea(int idArea)
	{
		this.idArea = idArea;
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
