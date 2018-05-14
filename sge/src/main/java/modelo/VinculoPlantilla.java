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

public class VinculoPlantilla implements Serializable
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -6859632024291145176L;

	private int					idVinculoPlantilla;
	private String				descripcion;
	private LayoutVersion		layoutVersionSAR100;
	private LayoutVersion		layoutVersionSIRI;

	public VinculoPlantilla()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public VinculoPlantilla(int idVinculoPlantilla, String descripcion, LayoutVersion layoutVersionSAR100,
			LayoutVersion layoutVersionSIRI)
	{
		super();
		this.idVinculoPlantilla = idVinculoPlantilla;
		this.descripcion = descripcion;
		this.layoutVersionSAR100 = layoutVersionSAR100;
		this.layoutVersionSIRI = layoutVersionSIRI;
	}

	// Detecta qué versiones son las vinculadas para la plantilla de SAR100 y la
	// de SIRI
	public void updateVersionesVinculadas()
	{
		this.layoutVersionSAR100 = null;
		this.layoutVersionSIRI = null;

		PreparedStatement prep = null;
		ResultSet rBD = null;

//		List<VinculoPlantilla> catVinculosPlantilla = new ArrayList<>();

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionSIRI();)
		{

			prep = conexion.prepareStatement(
					" SELECT vp.*, pl.idLayout AS idLayVinculo, pl.Version AS verVinculo, plSIRI.idLayout AS idLaySIRI, plSIRI.Version AS verSIRI, plSAR.idLayout AS idLaySAR, plSAR.Version AS verSAR FROM siri.vinculoplantilla vp LEFT JOIN webrh.plantilla pl ON vp.idPlantilla = pl.idPLantilla LEFT JOIN webrh.plantilla plSIRI ON vp.idPlantillaSIRI = plSIRI.idPlantilla LEFT JOIN webrh.plantilla plSAR ON vp.idPlantillaSAR100 = plSAR.idPlantilla"
							+ " WHERE vp.idVinculoPlantilla=?");

			prep.setInt(1, this.idVinculoPlantilla);

			rBD = prep.executeQuery();

			if (rBD.next())
			{
				this.layoutVersionSAR100 = new LayoutVersion(rBD.getInt("idLaySAR"), rBD.getString("verSAR"), null);
				this.layoutVersionSIRI = new LayoutVersion(rBD.getInt("idLaySIRI"), rBD.getString("verSIRI"), null);

			}

			/*
			 * prep = conexion.prepareStatement(
			 * " SELECT pl.*, lyt.Descripcion AS descripcionLayout FROM webrh.plantilla pl, webrh.layout lyt "
			 * + "WHERE pl.idPlantilla IN " +
			 * "( SELECT DISTINCT(adv.idPlantillaVinculo) FROM siri.anexodatosvinculados adv WHERE adv.idVinculoPlantilla=? ) "
			 * + "AND lyt.idLayout = pl.idLayout ");
			 * 
			 * prep.setInt(1, this.idVinculoPlantilla);
			 * 
			 * rBD = prep.executeQuery();
			 * 
			 * if (rBD.next()) { do {
			 * 
			 * // Se identifica si el layout de la plantilla vinculada //
			 * pertence al SIRI o al SAR100 y se saca la versión //
			 * correspondiente switch
			 * (rBD.getString("descripcionLayout").trim()) {
			 * 
			 * case "SIRI": this.layoutVersionSIRI = new
			 * LayoutVersion(rBD.getInt("idLayout"), rBD.getString("Version"),
			 * null);
			 * 
			 * break;
			 * 
			 * case "SAR100": this.layoutVersionSAR100 = new
			 * LayoutVersion(rBD.getInt("idLayout"), rBD.getString("Version"),
			 * null);
			 * 
			 * break;
			 * 
			 * }
			 * 
			 * } while (rBD.next());
			 * 
			 * }
			 */

		} catch (

		Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al obtener los vínculos creados para la plantilla, favor de contactar con el desarrollador del sistema."));

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

	public int getIdVinculoPlantilla()
	{
		return idVinculoPlantilla;
	}

	public void setIdVinculoPlantilla(int idVinculoPlantilla)
	{
		this.idVinculoPlantilla = idVinculoPlantilla;
	}

	public String getDescripcion()
	{
		return descripcion;
	}

	public void setDescripcion(String descripcion)
	{
		this.descripcion = descripcion;
	}

	public static long getSerialversionuid()
	{
		return serialVersionUID;
	}

	public LayoutVersion getLayoutVersionSAR100()
	{
		return layoutVersionSAR100;
	}

	public void setLayoutVersionSAR100(LayoutVersion layoutVersionSAR100)
	{
		this.layoutVersionSAR100 = layoutVersionSAR100;
	}

	public LayoutVersion getLayoutVersionSIRI()
	{
		return layoutVersionSIRI;
	}

	public void setLayoutVersionSIRI(LayoutVersion layoutVersionSIRI)
	{
		this.layoutVersionSIRI = layoutVersionSIRI;
	}

}
