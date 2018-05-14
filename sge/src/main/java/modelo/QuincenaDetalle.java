package modelo;

import java.io.Serializable;

public class QuincenaDetalle extends AñoDetalle implements Serializable
{
	private int quincena;

	public QuincenaDetalle(int año, int quincena)
	{
		super(año);
		// TODO Auto-generated constructor stub
		this.quincena = quincena;
		
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
