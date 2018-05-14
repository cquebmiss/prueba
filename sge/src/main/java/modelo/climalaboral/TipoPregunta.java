package modelo.climalaboral;

public class TipoPregunta
{
	private int idTipoPregunta;
	private String descripcion;

	public TipoPregunta()
	{
		// TODO Auto-generated constructor stub
	}

	public TipoPregunta(int idTipoPregunta, String descripcion)
	{
		super();
		this.idTipoPregunta = idTipoPregunta;
		this.descripcion = descripcion;
	}

	public int getIdTipoPregunta()
	{
		return idTipoPregunta;
	}

	public void setIdTipoPregunta(int idTipoPregunta)
	{
		this.idTipoPregunta = idTipoPregunta;
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
