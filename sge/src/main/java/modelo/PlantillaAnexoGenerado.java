/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author desarolloyc
 */
public class PlantillaAnexoGenerado implements Serializable
{
	private int						idLayout;
	private String					descripcionLayout;
	private Plantilla				plantillaEstructura;
	private int						año;
	private int						bimestre;
	private String					plaza;
	private String					unidad;

	private List<PlantillaRegistro>	registroEncabezado;
	private List<PlantillaRegistro>	registrosDetalle;

	private String					vivienda;
	private String					retiro;
	private String					cesantiaPatron;
	private String					cesantiaTrabajador;
	private String					ahorroSolidarioTrabajador;
	private String					ahorroSolidarioPatron;

	private int						casosAhorro;

	public PlantillaAnexoGenerado(int idLayout, String descripcionLayout, Plantilla plantillaEstructura, int año,
			int bimestre, String plaza, String unidad)
	{
		this.idLayout = idLayout;
		this.descripcionLayout = descripcionLayout;
		this.plantillaEstructura = plantillaEstructura;
		this.año = año;
		this.bimestre = bimestre;
		this.plaza = plaza;
		this.unidad = unidad;

		this.registroEncabezado = new ArrayList<>();
		this.registrosDetalle = new ArrayList<>();
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
	 * @return the descripcionLayout
	 */
	public String getDescripcionLayout()
	{
		return descripcionLayout;
	}

	/**
	 * @param descripcionLayout
	 *            the descripcionLayout to set
	 */
	public void setDescripcionLayout(String descripcionLayout)
	{
		this.descripcionLayout = descripcionLayout;
	}

	/**
	 * @return the plantillaEstructura
	 */
	public Plantilla getPlantillaEstructura()
	{
		return plantillaEstructura;
	}

	/**
	 * @param plantillaEstructura
	 *            the plantillaEstructura to set
	 */
	public void setPlantillaEstructura(Plantilla plantillaEstructura)
	{
		this.plantillaEstructura = plantillaEstructura;
	}

	/**
	 * @return the año
	 */
	public int getAño()
	{
		return año;
	}

	/**
	 * @param año
	 *            the año to set
	 */
	public void setAño(int año)
	{
		this.año = año;
	}

	/**
	 * @return the bimestre
	 */
	public int getBimestre()
	{
		return bimestre;
	}

	/**
	 * @param bimestre
	 *            the bimestre to set
	 */
	public void setBimestre(int bimestre)
	{
		this.bimestre = bimestre;
	}

	/**
	 * @return the plaza
	 */
	public String getPlaza()
	{
		return plaza;
	}

	/**
	 * @param plaza
	 *            the plaza to set
	 */
	public void setPlaza(String plaza)
	{
		this.plaza = plaza;
	}

	/**
	 * @return the unidad
	 */
	public String getUnidad()
	{
		return unidad;
	}

	/**
	 * @param unidad
	 *            the unidad to set
	 */
	public void setUnidad(String unidad)
	{
		this.unidad = unidad;
	}

	/**
	 * @return the registroEncabezado
	 */
	public List<PlantillaRegistro> getRegistroEncabezado()
	{
		return registroEncabezado;
	}

	/**
	 * @param registroEncabezado
	 *            the registroEncabezado to set
	 */
	public void setRegistroEncabezado(List<PlantillaRegistro> registroEncabezado)
	{
		this.registroEncabezado = registroEncabezado;
	}

	/**
	 * @return the registrosDetalle
	 */
	public List<PlantillaRegistro> getRegistrosDetalle()
	{
		return registrosDetalle;
	}

	/**
	 * @param registrosDetalle
	 *            the registrosDetalle to set
	 */
	public void setRegistrosDetalle(List<PlantillaRegistro> registrosDetalle)
	{
		this.registrosDetalle = registrosDetalle;
	}

	public String getVivienda()
	{
		return vivienda;
	}

	public void setVivienda(String vivienda)
	{
		this.vivienda = vivienda;
	}

	public String getRetiro()
	{
		return retiro;
	}

	public void setRetiro(String retiro)
	{
		this.retiro = retiro;
	}

	public String getCesantiaPatron()
	{
		return cesantiaPatron;
	}

	public void setCesantiaPatron(String cesantiaPatron)
	{
		this.cesantiaPatron = cesantiaPatron;
	}

	public String getCesantiaTrabajador()
	{
		return cesantiaTrabajador;
	}

	public void setCesantiaTrabajador(String cesantiaTrabajador)
	{
		this.cesantiaTrabajador = cesantiaTrabajador;
	}

	public String getAhorroSolidarioTrabajador()
	{
		return ahorroSolidarioTrabajador;
	}

	public void setAhorroSolidarioTrabajador(String ahorroSolidarioTrabajador)
	{
		this.ahorroSolidarioTrabajador = ahorroSolidarioTrabajador;
	}

	public String getAhorroSolidarioPatron()
	{
		return ahorroSolidarioPatron;
	}

	public void setAhorroSolidarioPatron(String ahorroSolidarioPatron)
	{
		this.ahorroSolidarioPatron = ahorroSolidarioPatron;
	}

	public int getCasosAhorro()
	{
		return casosAhorro;
	}

	public void setCasosAhorro(int casosAhorro)
	{
		this.casosAhorro = casosAhorro;
	}

}
