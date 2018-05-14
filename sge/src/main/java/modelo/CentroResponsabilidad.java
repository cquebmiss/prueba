package modelo;

public class CentroResponsabilidad
{
	private String idCentroResponsabilidad;
	private String descripcion;
	private String clues;
	private String descripcionClues;
	private String tipoUnidad;
	private String tipologia;
	//Es el formato del clues que se adapta a los requerimientos de México
	private String cluesParaMexico;
	
	public CentroResponsabilidad()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public CentroResponsabilidad(String idCentroResponsabilidad, String descripcion, String clues,
			String descripcionClues, String tipoUnidad, String tipologia, String cluesParaMexico)
	{
		super();
		this.idCentroResponsabilidad = idCentroResponsabilidad;
		this.descripcion = descripcion;
		this.clues = clues;
		this.descripcionClues = descripcionClues;
		this.tipoUnidad = tipoUnidad;
		this.tipologia = tipologia;
		this.cluesParaMexico = cluesParaMexico;
	}

	public String getIdCentroResponsabilidad()
	{
		return idCentroResponsabilidad;
	}

	public void setIdCentroResponsabilidad(String idCentroResponsabilidad)
	{
		this.idCentroResponsabilidad = idCentroResponsabilidad;
	}

	public String getDescripcion()
	{
		return descripcion;
	}

	public void setDescripcion(String descripcion)
	{
		this.descripcion = descripcion;
	}

	public String getClues()
	{
		return clues;
	}

	public void setClues(String clues)
	{
		this.clues = clues;
	}

	public String getDescripcionClues()
	{
		return descripcionClues;
	}

	public void setDescripcionClues(String descripcionClues)
	{
		this.descripcionClues = descripcionClues;
	}

	public String getTipoUnidad()
	{
		return tipoUnidad;
	}

	public void setTipoUnidad(String tipoUnidad)
	{
		this.tipoUnidad = tipoUnidad;
	}

	public String getTipologia()
	{
		return tipologia;
	}

	public void setTipologia(String tipologia)
	{
		this.tipologia = tipologia;
	}

	public String getCluesParaMexico()
	{
		return cluesParaMexico;
	}

	public void setCluesParaMexico(String cluesParaMexico)
	{
		this.cluesParaMexico = cluesParaMexico;
	}
	
}
