package modelo;

public class ConceptoAsociadoPlaza
{
	private int idRegistro;
	private String partidaAntecedente;
	private String concepto;
	private boolean asociado;
	
	public ConceptoAsociadoPlaza()
	{
		this.idRegistro = -1;
		this.partidaAntecedente = "";
		this.concepto = "";
		this.asociado = false;
		
	}
	
	public ConceptoAsociadoPlaza(int idRegistro, String partidaAntecedente, String concepto, boolean asociado)
	{
		super();
		this.idRegistro = idRegistro;
		this.partidaAntecedente = partidaAntecedente;
		this.concepto = concepto;
		this.asociado = asociado;
	}
	public int getIdRegistro()
	{
		return idRegistro;
	}
	public void setIdRegistro(int idRegistro)
	{
		this.idRegistro = idRegistro;
	}
	public String getPartidaAntecedente()
	{
		return partidaAntecedente;
	}
	public void setPartidaAntecedente(String partidaAntecedente)
	{
		this.partidaAntecedente = partidaAntecedente;
	}
	public String getConcepto()
	{
		return concepto;
	}
	public void setConcepto(String concepto)
	{
		this.concepto = concepto;
	}
	public boolean isAsociado()
	{
		return asociado;
	}
	public void setAsociado(boolean asociado)
	{
		this.asociado = asociado;
	}

}
