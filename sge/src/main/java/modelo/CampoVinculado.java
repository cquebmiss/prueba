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
public class CampoVinculado implements Serializable, Cloneable
{
	private int				idPlantilla;
	private int				orden;
	private String			descripcionYVersionPlantilla;
	private CampoPlantilla	campo;
	private String			valorPorDefecto;

	public CampoVinculado(int idPlantilla, int orden, String descripcionYVersionPlantilla, CampoPlantilla campo)
	{
		this.idPlantilla = idPlantilla;
		this.orden = orden;
		this.descripcionYVersionPlantilla = descripcionYVersionPlantilla;
		this.campo = campo;
	}

	@Override
	public Object clone()
	{
		CampoVinculado campo = null;

		try
		{
			campo = (CampoVinculado) super.clone();
			
			if( this.campo != null )
			{
				campo.setCampo((CampoPlantilla) this.campo.clone());
			}
		} catch (CloneNotSupportedException e)
		{
			e.printStackTrace();
		}

		return campo;

	}
/*
	public CampoVinculado getClone()
	{
		CampoVinculado copia = new CampoVinculado(this.idPlantilla, this.orden, this.descripcionYVersionPlantilla,
				null);

		if (this.campo != null)
		{
			copia.setCampo(this.campo.getClone());
		}

		copia.setValorPorDefecto(this.valorPorDefecto);

		return copia;
	}
*/
	public void reinciaCampoValoresDefecto()
	{
		this.idPlantilla = -1;
		this.orden = -1;
		this.descripcionYVersionPlantilla = null;
		this.campo = null;
		this.valorPorDefecto = null;
	}

	/**
	 * @return the idPlantilla
	 */
	public int getIdPlantilla()
	{
		return idPlantilla;
	}

	/**
	 * @param idPlantilla
	 *            the idPlantilla to set
	 */
	public void setIdPlantilla(int idPlantilla)
	{
		this.idPlantilla = idPlantilla;
	}

	/**
	 * @return the descripcionYVersionPlantilla
	 */
	public String getDescripcionYVersionPlantilla()
	{
		return descripcionYVersionPlantilla;
	}

	/**
	 * @param descripcionYVersionPlantilla
	 *            the descripcionYVersionPlantilla to set
	 */
	public void setDescripcionYVersionPlantilla(String descripcionYVersionPlantilla)
	{
		this.descripcionYVersionPlantilla = descripcionYVersionPlantilla;
	}

	/**
	 * @return the campo
	 */
	public CampoPlantilla getCampo()
	{
		return campo;
	}

	/**
	 * @param campo
	 *            the campo to set
	 */
	public void setCampo(CampoPlantilla campo)
	{
		this.campo = campo;
	}

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
	 * @return the valorPorDefecto
	 */
	public String getValorPorDefecto()
	{
		return valorPorDefecto;
	}

	/**
	 * @param valorPorDefecto
	 *            the valorPorDefecto to set
	 */
	public void setValorPorDefecto(String valorPorDefecto)
	{
		this.valorPorDefecto = valorPorDefecto;
	}

}
