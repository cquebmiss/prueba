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

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import resources.DataBase;
import util.FacesUtils;

/**
 *
 * @author desarolloyc
 */
public class Trabajador implements Serializable
{
	private String		numEmpleado;
	private String		RFC;
	private String		Nombre;
	private String		CURP;
	private Expediente	Expediente;
	private Plaza		plaza;
	private Status		status;

	public Trabajador(String numEmpleado, String RFC, String Nombre, String CURP, Plaza plaza, Status status)
	{
		this.numEmpleado = numEmpleado;
		this.RFC = RFC;
		this.Nombre = Nombre;
		this.CURP = CURP;
		this.Expediente = new Expediente(false, false, false, false, false, false, false, false, false);
		this.status = status;

		if (plaza == null)
		{
			this.plaza = new Plaza();
		}
		else
		{
			this.plaza = plaza;
		}
	}

	public boolean isRegistrado()
	{
		boolean resultado = false;

		PreparedStatement prep = null;
		ResultSet rBD = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{
			prep = conexion.prepareStatement("SELECT * FROM trabajador WHERE idPlaza=? AND NumEmpleado=?");

			prep.setInt(1, this.plaza.getIdPlaza());
			prep.setString(2, this.numEmpleado);

			rBD = prep.executeQuery();

			if (rBD.next())
			{
				resultado = true;
			}

		} catch (Exception e)
		{
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Excepción",
							"Ha ocurrido una excepción al verificar si el empleado ya está agregado en el registro principal. NumEmpleado: "
									+ this.numEmpleado));
		}
		finally
		{
			if (prep != null)
			{
				try
				{
					prep.close();

				} catch (SQLException e)
				{ // TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return resultado;

	}

	/**
	 * @return the RFC
	 */
	public String getRFC()
	{
		return RFC;
	}

	/**
	 * @param RFC
	 *            the RFC to set
	 */
	public void setRFC(String RFC)
	{
		this.RFC = RFC;
	}

	/**
	 * @return the Nombre
	 */
	public String getNombre()
	{
		return Nombre;
	}

	/**
	 * @param Nombre
	 *            the Nombre to set
	 */
	public void setNombre(String Nombre)
	{
		this.Nombre = Nombre;
	}

	/**
	 * @return the CURP
	 */
	public String getCURP()
	{
		return CURP;
	}

	/**
	 * @param CURP
	 *            the CURP to set
	 */
	public void setCURP(String CURP)
	{
		this.CURP = CURP;
	}

	/**
	 * @return the Expediente
	 */
	public Expediente getExpediente()
	{
		return Expediente;
	}

	/**
	 * @param Expediente
	 *            the Expediente to set
	 */
	public void setExpediente(Expediente Expediente)
	{
		this.Expediente = Expediente;
	}

	/**
	 * @return the plaza
	 */
	public Plaza getPlaza()
	{
		return plaza;
	}

	/**
	 * @param plaza
	 *            the plaza to set
	 */
	public void setPlaza(Plaza plaza)
	{
		this.plaza = plaza;
	}

	public String getNumEmpleado()
	{
		return numEmpleado;
	}

	public void setNumEmpleado(String numEmpleado)
	{
		this.numEmpleado = numEmpleado;
	}

	public Status getStatus()
	{
		return status;
	}

	public void setStatus(Status status)
	{
		this.status = status;
	}

}
