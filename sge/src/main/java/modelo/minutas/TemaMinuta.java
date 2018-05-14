package modelo.minutas;

public class TemaMinuta
{

	private Minuta minuta;
	private int orden;
	private String descripcion;
	private String desarrollo;
	private Participante responsable;
	
	public TemaMinuta()
	{
		super();
		// TODO Auto-generated constructor stub
	}	

	public TemaMinuta(Minuta minuta, int orden, String descripcion, String desarrollo, Participante responsable)
	{
		super();
		this.minuta = minuta;
		this.orden = orden;
		this.descripcion = descripcion;
		this.desarrollo = desarrollo;
		this.responsable = responsable;
	}

	public Minuta getMinuta()
	{
		return minuta;
	}

	public void setMinuta(Minuta minuta)
	{
		this.minuta = minuta;
	}

	public int getOrden()
	{
		return orden;
	}

	public void setOrden(int orden)
	{
		this.orden = orden;
	}

	public String getDescripcion()
	{
		return descripcion;
	}

	public void setDescripcion(String descripcion)
	{
		this.descripcion = descripcion;
	}

	public String getDesarrollo()
	{
		return desarrollo;
	}

	public void setDesarrollo(String desarrollo)
	{
		this.desarrollo = desarrollo;
	}

	public Participante getResponsable()
	{
		return responsable;
	}

	public void setResponsable(Participante responsable)
	{
		this.responsable = responsable;
	}

}
