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
public class Unidad implements Serializable, Cloneable
{
	private int						idUnidad;
	private String					descripcion;
	private FuenteFinanciamiento	fuenteFinanciamiento;

	public Unidad(int idUnidad, String descripcion)
	{
		this.idUnidad = idUnidad;
		this.descripcion = descripcion;
	}

	@Override
	public Object clone()
	{
		Unidad clon = null;

		try
		{
			clon = (Unidad) super.clone();

			if (this.fuenteFinanciamiento != null)
			{
				clon.setFuenteFinanciamiento((FuenteFinanciamiento) this.fuenteFinanciamiento.clone());
			}

		} catch (CloneNotSupportedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return clon;

	}

	/**
	 * @return the idUnidad
	 */
	public int getIdUnidad()
	{
		return idUnidad;
	}

	/**
	 * @param idUnidad
	 *            the idUnidad to set
	 */
	public void setIdUnidad(int idUnidad)
	{
		this.idUnidad = idUnidad;
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

	public FuenteFinanciamiento getFuenteFinanciamiento()
	{
		return fuenteFinanciamiento;
	}

	public void setFuenteFinanciamiento(FuenteFinanciamiento fuenteFinanciamiento)
	{
		this.fuenteFinanciamiento = fuenteFinanciamiento;
	}

}
