/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import util.utilidades;

/**
 *
 * @author desarolloyc
 */
public class Plaza implements Serializable, Cloneable
{

	private int				idPlaza;
	private String			descripcionPlaza;
	private List<Unidad>	unidades;

	public Plaza()
	{
		this.idPlaza = 0;
		this.descripcionPlaza = "BASE";
	}

	public Plaza(int idPlaza, String descripcionPlaza)
	{
		this.idPlaza = idPlaza;
		this.descripcionPlaza = descripcionPlaza;
	}

	public Plaza getClone()
	{
		Plaza plaza = new Plaza();
		plaza.setIdPlaza(this.idPlaza);
		plaza.setDescripcionPlaza(this.descripcionPlaza);

		if (this.unidades != null)
		{
			plaza.setUnidades(this.unidades.subList(0, this.unidades.size()));

		}

		return plaza;
	}

	@Override
	public Object clone()
	{
		Plaza clon = null;

		try
		{
			clon = (Plaza) super.clone();

			if (this.unidades != null)
			{
				List<Unidad> unidadesClon = new ArrayList<>();

				this.unidades.forEach(item ->
				{
					unidadesClon.add((Unidad) item.clone());

				});

				clon.setUnidades(unidadesClon);

			}

		} catch (CloneNotSupportedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return clon;
	}

	public void updateUnidades()
	{
		this.unidades = utilidades.getUnidadesPlaza(this.idPlaza);
	}

	/**
	 * @return the idPlaza
	 */
	public int getIdPlaza()
	{
		return idPlaza;
	}

	/**
	 * @param idPlaza
	 *            the idPlaza to set
	 */
	public void setIdPlaza(int idPlaza)
	{
		this.idPlaza = idPlaza;
	}

	/**
	 * @return the descripcionPlaza
	 */
	public String getDescripcionPlaza()
	{
		return descripcionPlaza;
	}

	/**
	 * @param descripcionPlaza
	 *            the descripcionPlaza to set
	 */
	public void setDescripcionPlaza(String descripcionPlaza)
	{
		this.descripcionPlaza = descripcionPlaza;
	}

	/**
	 * @return the unidades
	 */
	public List<Unidad> getUnidades()
	{
		return unidades;
	}

	/**
	 * @param unidades
	 *            the unidades to set
	 */
	public void setUnidades(List<Unidad> unidades)
	{
		this.unidades = unidades;
	}

}
