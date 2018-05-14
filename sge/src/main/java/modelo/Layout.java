/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import resources.DataBase;
import util.FacesUtils;
import util.utilidades;

/**
 *
 * @author desarolloyc
 */
public class Layout implements Serializable
{
	private int					idLayout;
	private String				descripcion;
	private List<LayoutVersion>	versiones;

	public Layout(int idLayout, String descripcion)
	{
		this.idLayout = idLayout;
		this.descripcion = descripcion;
		this.versiones = null;
	}

	public Layout getClone()
	{
		return new Layout(this.idLayout, this.descripcion);
	}

	public void updateVersiones()
	{
		this.versiones = utilidades.getVersionesLayoutsBD(this.idLayout);
	}

	// Actualiza las versiones en búsqueda de la versión específica de una
	// plantilla indicada
	// Esto es práctico para encontrar la versión de una plantilla de SIRI o SAR
	// en específico
	// Esto nos devolverá solo un resultado en el List<LayoutVersion> y de esta
	// manera seguimos utilizando el mismo esquema de seleccionar la posición 0
	public void updateVersiones(int idPlantilla)
	{
		this.versiones = utilidades.getVersionLayoutsBD(idPlantilla);

	}

	//Obtiene el id de la plantilla de detalle para obtener la versión correcta empleando el método updateVersiones
	public void updateVersionSAR(int año, int bimestre, Plaza plaza, Unidad unidad)
	{

		PreparedStatement prep = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{

			prep = conexion.prepareStatement(
					" SELECT pv.idPlantilla, pl.idLayout, pl.idTipoPlantilla, pl.Version, pl.Descripcion FROM siri.plantillavalores pv, webrh.plantilla pl "
					+ "WHERE pv.Ano=? AND pv.Bimestre=? AND pv.idPlaza=? AND pv.idUnidad=? AND idTipoPlantilla=2 AND pv.idPlantilla = pl.idPlantilla group by pv.idPlantilla ");
			prep.setInt(1, año);
			prep.setInt(2, bimestre);
			prep.setInt(3, plaza.getIdPlaza());
			prep.setInt(4, unidad.getIdUnidad());
			

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{
				updateVersiones(rBD.getInt("idPlantilla"));
			}

		} catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción", "Ha ocurrido una excepción al momento de obtener la versión del SAR100."));

			e.printStackTrace();
		}
		finally
		{
			if (prep != null)
			{
				try
				{
					prep.close();
				} catch (SQLException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	public LayoutVersion getVersion(String version)
	{
		for (LayoutVersion versionLayout : this.versiones)
		{
			if (versionLayout.getVersion().equalsIgnoreCase(version))
			{
				return versionLayout;

			}

		}

		return null;
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

	/**
	 * @return the versiones
	 */
	public List<LayoutVersion> getVersiones()
	{
		return versiones;
	}

	/**
	 * @param versiones
	 *            the versiones to set
	 */
	public void setVersiones(List<LayoutVersion> versiones)
	{
		this.versiones = versiones;
	}

}
