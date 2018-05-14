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
public class TipoPlantilla implements Serializable, Cloneable
{
	private int		idTipoPlantilla;
	private String	descripcion;

	public TipoPlantilla(int idTipoPlantilla, String descripcion)
	{
		this.idTipoPlantilla = idTipoPlantilla;
		this.descripcion = descripcion;
	}

	@Override
	public Object clone()
	{
		TipoPlantilla tipoPlantillaClon = null;

		try
		{
			tipoPlantillaClon = (TipoPlantilla) super.clone();
		} catch (CloneNotSupportedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return tipoPlantillaClon;

	}

	/**
	 * @return the idTipoPlantilla
	 */
	public int getIdTipoPlantilla()
	{
		return idTipoPlantilla;
	}

	/**
	 * @param idTipoPlantilla
	 *            the idTipoPlantilla to set
	 */
	public void setIdTipoPlantilla(int idTipoPlantilla)
	{
		this.idTipoPlantilla = idTipoPlantilla;
	}

	/**
	 * @return the descripcion
	 */
	public String getDescripcion()
	{
		return descripcion;
	}

	/**
	 * @param descripcion
	 *            the descripcion to set
	 */
	public void setDescripcion(String descripcion)
	{
		this.descripcion = descripcion;
	}

}
