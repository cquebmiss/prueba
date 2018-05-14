package modelo;

import java.io.Serializable;

public class InstrumentoPago implements Serializable
{
	private Plaza			plaza;
	private String			idInstrumentoPago;
	private String			descripcion;
	private String			abreviacion;
	private String			caracterTerminador;
	private String			indicativoArchivo;
	private String			caracterSeparador;

	private Layout			layout;

	// Se vincula con el layout específico
	private LayoutVersion	layoutVersion;
	
	

	public InstrumentoPago()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public InstrumentoPago(Plaza plaza, String idInstrumentoPago, String descripcion, String abreviacion,
			String caracterTerminador, String indicativoArchivo, String caracterSeparador)
	{
		super();
		this.plaza = plaza;
		this.idInstrumentoPago = idInstrumentoPago;
		this.descripcion = descripcion;
		this.abreviacion = abreviacion;
		this.caracterTerminador = caracterTerminador;
		this.indicativoArchivo = indicativoArchivo;
		this.caracterSeparador = caracterSeparador;
	}

	public Plaza getPlaza()
	{
		return plaza;
	}

	public void setPlaza(Plaza plaza)
	{
		this.plaza = plaza;
	}

	public String getIdInstrumentoPago()
	{
		return idInstrumentoPago;
	}

	public void setIdInstrumentoPago(String idInstrumentoPago)
	{
		this.idInstrumentoPago = idInstrumentoPago;
	}

	public String getDescripcion()
	{
		return descripcion;
	}

	public void setDescripcion(String descripcion)
	{
		this.descripcion = descripcion;
	}

	public String getAbreviacion()
	{
		return abreviacion;
	}

	public void setAbreviacion(String abreviacion)
	{
		this.abreviacion = abreviacion;
	}

	public String getCaracterTerminador()
	{
		return caracterTerminador;
	}

	public void setCaracterTerminador(String caracterTerminador)
	{
		this.caracterTerminador = caracterTerminador;
	}

	public String getIndicativoArchivo()
	{
		return indicativoArchivo;
	}

	public void setIndicativoArchivo(String indicativoArchivo)
	{
		this.indicativoArchivo = indicativoArchivo;
	}

	public String getCaracterSeparador()
	{
		return caracterSeparador;
	}

	public void setCaracterSeparador(String caracterSeparador)
	{
		this.caracterSeparador = caracterSeparador;
	}

	public Layout getLayout()
	{
		return layout;
	}

	public void setLayout(Layout layout)
	{
		this.layout = layout;
	}

	public LayoutVersion getLayoutVersion()
	{
		return layoutVersion;
	}

	public void setLayoutVersion(LayoutVersion layoutVersion)
	{
		this.layoutVersion = layoutVersion;
	}

}
