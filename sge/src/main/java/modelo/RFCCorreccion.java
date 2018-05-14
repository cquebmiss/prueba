package modelo;

import java.io.Serializable;

public class RFCCorreccion implements Serializable
{
	private int		idPlaza;
	private String	numEmpleado;
	private String 	RFC;
	private boolean	valido;
	private String	rfcCorreccion;
	
	//atributo de apoyo para la edición en la tabla de correcciones de RFC
	private String rfcCorreccionEdit;

	
	public RFCCorreccion(int idPlaza, String numEmpleado, String rFC, boolean valido, String rfcCorreccion)
	{
		super();
		this.idPlaza = idPlaza;
		this.numEmpleado = numEmpleado;
		RFC = rFC;
		this.valido = valido;
		this.rfcCorreccion = rfcCorreccion;
		this.rfcCorreccionEdit = "";
	}

	public int getIdPlaza()
	{
		return idPlaza;
	}

	public void setIdPlaza(int idPlaza)
	{
		this.idPlaza = idPlaza;
	}

	public String getNumEmpleado()
	{
		return numEmpleado;
	}

	public void setNumEmpleado(String numEmpleado)
	{
		this.numEmpleado = numEmpleado;
	}

	public boolean isValido()
	{
		return valido;
	}

	public void setValido(boolean valido)
	{
		this.valido = valido;
	}

	public String getRfcCorreccion()
	{
		return rfcCorreccion;
	}

	public void setRfcCorreccion(String rfcCorreccion)
	{
		this.rfcCorreccion = rfcCorreccion;
	}

	public String getRFC()
	{
		return this.RFC;
	}

	public void setRFC(String rFC)
	{
		this.RFC = rFC;
	}

	public String getRfcCorreccionEdit()
	{
		return rfcCorreccionEdit;
	}

	public void setRfcCorreccionEdit(String rfcCorreccionEdit)
	{
		this.rfcCorreccionEdit = rfcCorreccionEdit;
	}

}
