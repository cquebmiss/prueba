package modelo.climalaboral;

public class Profesion
{
	private int		idProfesion;
	private String	descripcion;

	public Profesion()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public Profesion(int idProfesion, String descripcion)
	{
		super();
		this.idProfesion = idProfesion;
		this.descripcion = descripcion;
	}

	public int getIdProfesion()
	{
		return idProfesion;
	}

	public void setIdProfesion(int idProfesion)
	{
		this.idProfesion = idProfesion;
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
