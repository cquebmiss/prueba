package modelo.climalaboral;

public class Folio
{
	private int		numero;
	private int		idFolio;
	private boolean	encuestado;

	public Folio()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public int getIdFolio()
	{
		return idFolio;
	}

	public void setIdFolio(int idFolio)
	{
		this.idFolio = idFolio;
	}

	public boolean isEncuestado()
	{
		return encuestado;
	}

	public void setEncuestado(boolean encuestado)
	{
		this.encuestado = encuestado;
	}

	public int getNumero()
	{
		return numero;
	}

	public void setNumero(int numero)
	{
		this.numero = numero;
	}

}
