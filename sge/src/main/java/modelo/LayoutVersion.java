/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import util.utilidades;

/**
 *
 * @author desarolloyc
 */
public class LayoutVersion implements Serializable
{
	private int				idLayout;
	private String			version;
	private Date			fechaEmision;
	private Plantilla		encabezado;
	private List<Plantilla>	detalles;

	public LayoutVersion(int idLayout, String version)
	{
		this.idLayout = idLayout;
		this.version = version;
	}

	public LayoutVersion(int idLayout, String version, Date fechaEmision)
	{
		this.idLayout = idLayout;
		this.version = version;
		this.fechaEmision = fechaEmision;
	}

	public LayoutVersion getClone()
	{
		return new LayoutVersion(this.idLayout, this.version, this.fechaEmision);

	}

	public void updatePlantillaEncabezado(boolean incluirCampos)
	{
		List<Plantilla> plantilla = utilidades.getPlantillasLayout(this.idLayout, this.version, 1);

		if (plantilla == null)
		{
			setEncabezado(null);
		}
		else
		{
			setEncabezado(plantilla.get(0));

			if (incluirCampos)
			{

				this.encabezado.setCampos(utilidades.getCamposPlantilla(plantilla.get(0).getIdPlantilla()));
			}

		}

	}

	public void updatePlantillasDetalle(boolean incluirCampos)
	{
		setDetalles(utilidades.getPlantillasLayout(this.idLayout, this.version, 2));

		if (incluirCampos)
		{
			if (this.detalles == null)
			{
				return;
			}

			for (Plantilla plan : this.detalles)
			{
				plan.setCampos(utilidades.getCamposPlantilla(plan.getIdPlantilla()));
			}

		}

	}

	/**
	 * @return the version
	 */
	public String getVersion()
	{
		return version;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(String version)
	{
		this.version = version;
	}

	/**
	 * @return the fechaEmision
	 */
	public Date getFechaEmision()
	{
		return fechaEmision;
	}

	/**
	 * @param fechaEmision
	 *            the fechaEmision to set
	 */
	public void setFechaEmision(Date fechaEmision)
	{
		this.fechaEmision = fechaEmision;
	}

	/**
	 * @return the encabezado
	 */
	public Plantilla getEncabezado()
	{
		return encabezado;
	}

	/**
	 * @param encabezado
	 *            the encabezado to set
	 */
	public void setEncabezado(Plantilla encabezado)
	{
		this.encabezado = encabezado;
	}

	/**
	 * @return the detalles
	 */
	public List<Plantilla> getDetalles()
	{
		return detalles;
	}

	/**
	 * @param detalles
	 *            the detalles to set
	 */
	public void setDetalles(List<Plantilla> detalles)
	{
		this.detalles = detalles;
	}

	/**
	 * @return the idLayout
	 */
	public int getIdLayout()
	{
		return idLayout;
	}

	/**
	 * @param idLayout
	 *            the idLayout to set
	 */
	public void setIdLayout(int idLayout)
	{
		this.idLayout = idLayout;
	}

}
