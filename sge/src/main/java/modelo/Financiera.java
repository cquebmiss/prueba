package modelo;

import java.io.Serializable;

public class Financiera implements Serializable
{
	private int		idFinanciera;
	private String	descripcion;
	private String	abreviacionArchivo;
	private String	partidaAntecedente;

	public Financiera()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public Financiera(int idFinanciera, String descripcion, String abreviacionArchivo, String partidaAntecedente)
	{
		super();
		this.idFinanciera = idFinanciera;
		this.descripcion = descripcion;
		this.abreviacionArchivo = abreviacionArchivo;
		this.partidaAntecedente = partidaAntecedente;
	}

	public int getIdFinanciera()
	{
		return idFinanciera;
	}

	public void setIdFinanciera(int idFinanciera)
	{
		this.idFinanciera = idFinanciera;
	}

	public String getDescripcion()
	{
		return descripcion;
	}

	public void setDescripcion(String descripcion)
	{
		this.descripcion = descripcion;
	}

	public String getAbreviacionArchivo()
	{
		return abreviacionArchivo;
	}

	public void setAbreviacionArchivo(String abreviacionArchivo)
	{
		this.abreviacionArchivo = abreviacionArchivo;
	}

	public String getPartidaAntecedente()
	{
		return partidaAntecedente;
	}

	public void setPartidaAntecedente(String partidaAntecedente)
	{
		this.partidaAntecedente = partidaAntecedente;
	}

}
