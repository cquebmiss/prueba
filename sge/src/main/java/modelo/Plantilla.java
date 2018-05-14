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
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import resources.DataBase;
import util.FacesUtils;
import util.utilidades;

/**
 *
 * @author desarolloyc
 */
public class Plantilla implements Serializable, Cloneable
{

	private int						idPlantilla;
	private TipoPlantilla			tipoPlantilla;
	private String					descripcion;
	private List<CampoPlantilla>	campos;

	private Plantilla				sarVinculado;
	private Plantilla				siriVinculado;

	private String					caracterSeparador;

	public Plantilla(int idPlantilla)
	{
		super();
		this.idPlantilla = idPlantilla;
	}

	public Plantilla(int idPlantilla, TipoPlantilla tipoPlantilla, String descripcion, List<CampoPlantilla> campos,
			String caracterSeparador)
	{
		this.idPlantilla = idPlantilla;
		this.tipoPlantilla = tipoPlantilla;
		this.descripcion = descripcion;
		this.campos = campos;
		this.caracterSeparador = caracterSeparador;
	}

	@Override
	public Object clone()
	{
		Plantilla clon = null;

		try
		{
			clon = (Plantilla) super.clone();

			if (this.tipoPlantilla != null)
			{
				clon.setTipoPlantilla((TipoPlantilla) this.tipoPlantilla.clone());
			}

			if (this.campos != null)
			{
				List<CampoPlantilla> camposClon = new ArrayList<>();

				this.campos.forEach(campo ->
				{
					camposClon.add((CampoPlantilla) campo.clone());

				});

				clon.setCampos(camposClon);

			}

			if (this.sarVinculado != null)
			{
				clon.setSarVinculado((Plantilla) this.sarVinculado.clone());

			}

			if (this.siriVinculado != null)
			{
				clon.setSiriVinculado((Plantilla) this.siriVinculado.clone());
			}

		} catch (CloneNotSupportedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return clon;

	}

	public CampoPlantilla getCampoIndice()
	{
		for (CampoPlantilla campo : this.campos)
		{
			if (campo.isIndice())
			{
				return campo;
			}
		}

		return null;
	}

	// Devuelve el índice de un campo específico buscado por su descripción con
	// modo filto contains
	public int getPosicionValorPorDescripcionContains(String descripcion)
	{
		String desc = descripcion.toLowerCase();

		for (int x = 0; x < this.campos.size(); x++)
		{
			if (this.campos.get(x).getDescripcion().toLowerCase().contains(desc))
			{
				return x + 1;
			}
		}

		return -1;
	}

	// Devuelve el índice de un campo específico buscado por su descripción con
	// modo filto contains
	public String getValorPorDescripcionContains(String descripcion)
	{
		String desc = descripcion.toLowerCase();

		for (int x = 0; x < this.campos.size(); x++)
		{
			if (this.campos.get(x).getDescripcion().toLowerCase().contains(desc))
			{
				return this.campos.get(x).getValor();
			}
		}

		return "";
	}

	// Devuelve el índice de un campo específico buscado por su descripción
	public int getPosicionValorPorDescripcion(String descripcion)
	{
		String desc = descripcion.toLowerCase();

		for (int x = 0; x < this.campos.size(); x++)
		{
			if (this.campos.get(x).getDescripcion().toLowerCase().equals(desc))
			{
				return x + 1;
			}
		}

		return -1;
	}

	public Plantilla getClone()
	{
		List<CampoPlantilla> camposN = null;

		if (this.campos != null)
		{
			camposN = new ArrayList<>();

			for (CampoPlantilla campo : this.campos)
			{
				/*
				 * CampoPlantilla campoCopia = new
				 * CampoPlantilla(campo.getIdCampo(), campo.getDescripcion(),
				 * campo.getTipo(), campo.getEntero(), campo.getDecimal(), "",
				 * campo.getOrden(), campo.getCaracteristica(),
				 * campo.isIndice());
				 * 
				 * campoCopia.setRetiro(campo.isRetiro());
				 * campoCopia.setVivienda(campo.isVivienda());
				 * 
				 * if (campo.getCampoVinculado() != null) {
				 * campoCopia.setCampoVinculado(campo.getCampoVinculado().
				 * getClone()); }
				 * 
				 * camposN.add(campoCopia);
				 */
				camposN.add((CampoPlantilla) campo.clone());

			}

		}

		return new Plantilla(this.idPlantilla, this.tipoPlantilla, this.descripcion, camposN, this.caracterSeparador);

	}

	public void updateCampos()
	{
		setCampos(utilidades.getCamposPlantilla(idPlantilla));

		if (this.campos == null)
		{
			this.campos = new ArrayList<>();
		}

	}

	public void updateCamposVinculados(int idVinculoPlantilla)
	{
		utilidades.updateCamposVinculados(this, idVinculoPlantilla);
		verificaInclusionRetiroVivienda(idVinculoPlantilla);
	}

	// Verifica los campos que son incluidos en retiro y vivienda
	public void verificaInclusionRetiroVivienda(int idVinculoPlantilla)
	{

		CampoPlantilla campo;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionSIRI();)
		{
			PreparedStatement prep = conexion.prepareStatement(
					" SELECT Orden,Vivienda,Retiro FROM vinculoplantillaconfiguracion WHERE idPlantilla=? AND idVinculoPlantilla=? ORDER BY Orden ASC");

			prep.setInt(1, this.idPlantilla);
			prep.setInt(2, idVinculoPlantilla);

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{
				do
				{
					campo = getCampoOrden(rBD.getInt("Orden"));
					campo.setRetiro(rBD.getBoolean("Retiro"));
					campo.setVivienda(rBD.getBoolean("Vivienda"));
				}
				while (rBD.next());
			}

		} catch (Exception e)
		{
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al momento verificar , favor de contactar con el desarrollador del sistema."));
		}

	}

	// Cuando es un Anexo seleccionado, se determina e base al idPlantilla de
	// SAR y de SIRI cuál es el vínculo correcto que se debe de utilizar
	public boolean updateCamposVinculados(int idPlantillaSIRI, int idPlantillaSAR)
	{

		// CampoPlantilla campo;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionSIRI();)
		{
			/*
			 * PreparedStatement prep =
			 * conexion.prepareStatement(" SELECT t1.idVinculoPlantilla FROM " +
			 * "(SELECT idVinculoPlantilla, idPlantilla, idPlantillaVinculo FROM siri.anexodatosvinculados"
			 * + " WHERE idPlantilla = ? AND idPlantillaVinculo IN(?,?)  " +
			 * "GROUP BY idVinculoPlantilla,idPlantillaVinculo) AS t1, webrh.plantilla plt WHERE t1.idPlantillaVinculo = plt.idPlantilla"
			 * +
			 * " GROUP BY t1.idVinculoPlantilla HAVING COUNT(t1.idPlantillaVinculo)=2"
			 * );
			 * 
			 * prep.setInt(1, this.idPlantilla); prep.setInt(2,
			 * idPlantillaSIRI); prep.setInt(3, idPlantillaSAR);
			 * 
			 * ResultSet rBD = prep.executeQuery();
			 * 
			 * if (rBD.next()) {
			 * updateCamposVinculados(rBD.getInt("idVinculoPlantilla"));
			 * 
			 * return true; }
			 */

			String conector = "";

			switch (this.tipoPlantilla.getIdTipoPlantilla())
			{

				case 1:
					conector = "IS";
					break;

				case 2:
					conector = "=";
					break;
			}

			PreparedStatement prep = conexion
					.prepareStatement("SELECT * FROM siri.vinculoplantilla WHERE idPlantilla=? AND idPlantillaSAR100 "
							+ conector + " ? AND idPlantillaSIRI " + conector + " ?");

			prep.setInt(1, this.idPlantilla);

			switch (this.tipoPlantilla.getIdTipoPlantilla())
			{

				case 1:
					prep.setNull(2, Types.INTEGER);
					prep.setNull(3, Types.INTEGER);
					break;

				case 2:
					prep.setInt(2, idPlantillaSAR);
					prep.setInt(3, idPlantillaSIRI);
					break;
			}

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{
				updateCamposVinculados(rBD.getInt("idVinculoPlantilla"));
				return true;
			}

		} catch (Exception e)
		{
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al momento de determinar cuál es el vínculo correcto a utilizarse , favor de contactar con el desarrollador del sistema."));
		}

		return false;

	}

	public CampoPlantilla getCampoOrden(int orden)
	{
		if (this.campos != null)
		{
			for (CampoPlantilla campo : this.campos)
			{
				if (campo.getOrden() == orden)
				{
					return campo;
				}
			}

		}

		return null;
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
	 * @return the tipoPlantilla
	 */
	public TipoPlantilla getTipoPlantilla()
	{
		return tipoPlantilla;
	}

	/**
	 * @param tipoPlantilla
	 *            the tipoPlantilla to set
	 */
	public void setTipoPlantilla(TipoPlantilla tipoPlantilla)
	{
		this.tipoPlantilla = tipoPlantilla;
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
	 * @return the campos
	 */
	public List<CampoPlantilla> getCampos()
	{
		return campos;
	}

	/**
	 * @param campos
	 *            the campos to set
	 */
	public void setCampos(List<CampoPlantilla> campos)
	{
		this.campos = campos;
	}

	/**
	 * @return the sarVinculado
	 */
	public Plantilla getSarVinculado()
	{
		return sarVinculado;
	}

	/**
	 * @param sarVinculado
	 *            the sarVinculado to set
	 */
	public void setSarVinculado(Plantilla sarVinculado)
	{
		this.sarVinculado = sarVinculado;
	}

	/**
	 * @return the siriVinculado
	 */
	public Plantilla getSiriVinculado()
	{
		return siriVinculado;
	}

	/**
	 * @param siriVinculado
	 *            the siriVinculado to set
	 */
	public void setSiriVinculado(Plantilla siriVinculado)
	{
		this.siriVinculado = siriVinculado;
	}

	public String getCaracterSeparador()
	{
		return caracterSeparador;
	}

	public void setCaracterSeparador(String caracterSeparador)
	{
		this.caracterSeparador = caracterSeparador;
	}

}
