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
public class CampoPlantilla extends CampoValor implements Serializable, Cloneable
{
	private int				orden;
	private String			caracteristica;
	private boolean			indice;
	private boolean			vivienda;
	private boolean			retiro;

	// Utilizado para la exportación de la plantilla del archivo de bancos,
	// indica si debe o no incluirse en la exportación luego de ser dispersado
	// el archivo
	private boolean			activo;

	private CampoVinculado	campoVinculado;

	public CampoPlantilla(int idCampo, String Descripcion, String tipo, int entero, int decimal)
	{
		super(idCampo, Descripcion, tipo, entero, decimal);
		super.setValor(null);
		this.orden = -1;
		this.caracteristica = null;

		this.campoVinculado = new CampoVinculado(-1, -1, null, null);
	}

	public CampoPlantilla(int idCampo, String Descripcion, String tipo, int entero, int decimal, String valor)
	{
		super(idCampo, Descripcion, tipo, entero, decimal, valor);
		this.orden = -1;
		this.caracteristica = null;
		this.campoVinculado = new CampoVinculado(-1, -1, null, null);
	}

	public CampoPlantilla(int idCampo, String Descripcion, String tipo, int entero, int decimal, String valor,
			int orden, String caracteristica)
	{
		super(idCampo, Descripcion, tipo, entero, decimal, valor);
		this.orden = orden;
		this.caracteristica = caracteristica;
		this.campoVinculado = new CampoVinculado(-1, -1, null, null);
	}

	public CampoPlantilla(int idCampo, String Descripcion, String tipo, int entero, int decimal, String valor,
			int orden, String caracteristica, boolean indice)
	{
		super(idCampo, Descripcion, tipo, entero, decimal, valor);
		this.orden = orden;
		this.caracteristica = caracteristica;
		this.campoVinculado = new CampoVinculado(-1, -1, null, null);
		this.indice = indice;
	}

	@Override
	public Object clone()
	{
		CampoPlantilla clon = null;

		try
		{
			clon = (CampoPlantilla) super.clone();

			if (this.campoVinculado != null)
			{
				clon.setCampoVinculado((CampoVinculado) this.campoVinculado.clone());

			}

		} catch (CloneNotSupportedException e)
		{
			e.printStackTrace();
		}

		return clon;

	}
	/*
	 * public CampoPlantilla getClone() { return new
	 * CampoPlantilla(super.getIdCampo(), super.getDescripcion(),
	 * super.getTipo(), super.getEntero(), super.getDecimal(), super.getValor(),
	 * this.orden, this.caracteristica); }
	 */

	/**
	 * @return the orden
	 */
	public int getOrden()
	{
		return orden;
	}

	/**
	 * @param orden
	 *            the orden to set
	 */
	public void setOrden(int orden)
	{
		this.orden = orden;
	}

	/**
	 * @return the caracteristica
	 */
	public String getCaracteristica()
	{
		return caracteristica;
	}

	/**
	 * @param caracteristica
	 *            the caracteristica to set
	 */
	public void setCaracteristica(String caracteristica)
	{
		this.caracteristica = caracteristica;
	}

	/**
	 * @return the campoVinculado
	 */
	public CampoVinculado getCampoVinculado()
	{
		return campoVinculado;
	}

	/**
	 * @param campoVinculado
	 *            the campoVinculado to set
	 */
	public void setCampoVinculado(CampoVinculado campoVinculado)
	{
		this.campoVinculado = campoVinculado;
	}

	/**
	 * @return the indice
	 */
	public boolean isIndice()
	{
		return indice;
	}

	/**
	 * @param indice
	 *            the indice to set
	 */
	public void setIndice(boolean indice)
	{
		this.indice = indice;
	}

	/**
	 * @return the vivienda
	 */
	public boolean isVivienda()
	{
		return vivienda;
	}

	/**
	 * @param vivienda
	 *            the vivienda to set
	 */
	public void setVivienda(boolean vivienda)
	{
		this.vivienda = vivienda;
	}

	/**
	 * @return the retiro
	 */
	public boolean isRetiro()
	{
		return retiro;
	}

	/**
	 * @param retiro
	 *            the retiro to set
	 */
	public void setRetiro(boolean retiro)
	{
		this.retiro = retiro;
	}

	public boolean getActivo()
	{
		return activo;
	}

	public void setActivo(boolean activo)
	{
		this.activo = activo;
	}

}
