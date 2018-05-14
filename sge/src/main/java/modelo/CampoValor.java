/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.io.Serializable;

/**
 *
 * @author desarolloyc
 */
public class CampoValor extends Campo implements Serializable
{

	private String	valor;
	// Atributo que nos permite modificar el valor y conservar el original en la
	// edición de los valores de los campos de los archivos de pago
	private String	valorModificado;

	public CampoValor(int idCampo, String Descripcion, String tipo, int entero, int decimal)
	{
		super(idCampo, Descripcion, tipo, entero, decimal);
		this.valor = null;
	}

	public CampoValor(int idCampo, String Descripcion, String tipo, int entero, int decimal, String valor)
	{
		super(idCampo, Descripcion, tipo, entero, decimal);
		this.valor = valor;
	}

	/**
	 * @return the valor
	 */
	public String getValor()
	{
		return valor;
	}

	/**
	 * @param valor
	 *            the valor to set
	 */
	public void setValor(String valor)
	{
		this.valor = valor;
	}

	public String getValorModificado()
	{
		return valorModificado;
	}

	public void setValorModificado(String valorModificado)
	{
		this.valorModificado = valorModificado;
	}

}
