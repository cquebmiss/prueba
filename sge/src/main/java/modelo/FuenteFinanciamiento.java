package modelo;

public class FuenteFinanciamiento implements Cloneable
{

	private int		idRegistro;
	private String	idFuenteFinanciamiento;
	private String	descripcion;

	public FuenteFinanciamiento(int idRegistro, String idFuenteFinanciamiento, String descripcion)
	{
		super();
		this.idRegistro = idRegistro;
		this.idFuenteFinanciamiento = idFuenteFinanciamiento;
		this.descripcion = descripcion;
	}

	@Override
	public Object clone() throws CloneNotSupportedException
	{
		return super.clone();

	}

	public String getIdFuenteFinanciamiento()
	{
		return idFuenteFinanciamiento;
	}

	public void setIdFuenteFinanciamiento(String idFuenteFinanciamiento)
	{
		this.idFuenteFinanciamiento = idFuenteFinanciamiento;
	}

	public String getDescripcion()
	{
		return descripcion;
	}

	public void setDescripcion(String descripcion)
	{
		this.descripcion = descripcion;
	}

	public int getIdRegistro()
	{
		return idRegistro;
	}

	public void setIdRegistro(int idRegistro)
	{
		this.idRegistro = idRegistro;
	}

}
