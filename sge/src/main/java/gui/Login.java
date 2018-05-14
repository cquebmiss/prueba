/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.awt.Button;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import modelo.Sesion;
import resources.DataBase;
import util.FacesUtils;
import util.utilidades;

/**
 *
 * @author desarolloyc
 */

@ManagedBean
@SessionScoped

public class Login implements Serializable
{
	private String	usuario;
	private String	contrasena;
	private String	mensajeError;

	public Login()
	{

	}

	/**
	 * @return the usuario
	 */
	public String getUsuario()
	{
		return usuario;
	}

	/**
	 * @param usuario
	 *            the usuario to set
	 */
	public void setUsuario(String usuario)
	{
		this.usuario = usuario;
	}

	/**
	 * @return the contrasena
	 */
	public String getContrasena()
	{
		return contrasena;
	}

	/**
	 * @param contrasena
	 *            the contrasena to set
	 */
	public void setContrasena(String contrasena)
	{
		this.contrasena = contrasena;
	}

	/**
	 * @return the mensajeError
	 */
	public String getMensajeError()
	{
		return mensajeError;
	}

	/**
	 * @param mensajeError
	 *            the mensajeError to set
	 */
	public void setMensajeError(String mensajeError)
	{
		this.mensajeError = mensajeError;
	}

	public String isInSession()
	{
		Sesion controlSesion = (Sesion) FacesUtils.getManagedBean("Sesion");
		return controlSesion.getSesionActiva();
	}

	private void setDatosUsuarioSesion(Sesion controlSesion, String tipoSesion, String idUsuario, String nombreUsuario,
			String nombreReal, String email)
	{
		controlSesion.setSesionActiva(tipoSesion);
		controlSesion.setIdUsuario(idUsuario);
		controlSesion.setNombreUsuario(nombreUsuario);
		controlSesion.setNombreReal(nombreReal);
		controlSesion.setEmail(email);

		if (idUsuario != null)
		{
			controlSesion.getPermisosUsuarioSistemas();
		}

	}

	public void actionBotonAcceder(ActionEvent e)
	{

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{
			Sesion controlSesion = (Sesion) FacesUtils.getManagedBean("Sesion");

			setDatosUsuarioSesion(controlSesion, null, null, null, null, null);

			if (getUsuario().length() < 1 || getContrasena().length() < 1)
			{
				setMensajeError("Nombre de usuario y/o contraseña incorrecta.");
				return;
			}

			String consulta = "SELECT * FROM usuario u WHERE  u.Nombre=? AND u.Contrasena=?";

			PreparedStatement prep = conexion.prepareStatement(consulta);

			prep.setString(1, getUsuario());
			prep.setString(2, utilidades.MD5(getContrasena()));

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{
				String fInicio = rBD.getString("VigenciaInicial");
				String fFinal = rBD.getString("VigenciaFinal");

				java.util.Calendar fInicioC = null;

				if (fInicio != null)
				{
					fInicioC = java.util.Calendar.getInstance();
					fInicioC.clear(Calendar.HOUR);
					fInicioC.clear(Calendar.MINUTE);
					fInicioC.clear(Calendar.SECOND);
					fInicioC.clear(Calendar.MILLISECOND);
					fInicioC.set(Integer.parseInt(fInicio.substring(0, 4)), Integer.parseInt(fInicio.substring(5, 7)),
							Integer.parseInt(fInicio.substring(8, 10)));
					fInicioC.add(Calendar.MONTH, -1);
				}

				java.util.Calendar fFinalC = null;

				if (fFinal != null)
				{
					fFinalC = java.util.Calendar.getInstance();
					fFinalC.clear(Calendar.HOUR);
					fFinalC.clear(Calendar.MINUTE);
					fFinalC.clear(Calendar.SECOND);
					fFinalC.clear(Calendar.MILLISECOND);
					fFinalC.set(Integer.parseInt(fFinal.substring(0, 4)), Integer.parseInt(fFinal.substring(5, 7)),
							Integer.parseInt(fFinal.substring(8, 10)));
					fFinalC.add(Calendar.MONTH, -1);
				}

				if (fInicio != null | fFinal != null)
				{

					java.util.Calendar sqlDateActual = java.util.Calendar.getInstance();
					sqlDateActual.clear(Calendar.HOUR);
					sqlDateActual.clear(Calendar.MINUTE);
					sqlDateActual.clear(Calendar.SECOND);
					sqlDateActual.clear(Calendar.MILLISECOND);

					if (fFinalC != null & fInicioC == null)
					{
						if (sqlDateActual.after(fFinalC))
						{
							setMensajeError("Vigencia de la cuenta expirada.");
							return;
						}
					}

					if (fInicioC != null & fFinalC == null)
					{
						if (sqlDateActual.before(fInicioC))
						{
							setMensajeError("Cuenta fuera de vigencia.");
							return;
						}
					}

					if (fInicioC != null & fFinalC != null)
					{
						if (!(sqlDateActual.compareTo(fInicioC) >= 0 & sqlDateActual.compareTo(fFinalC) <= 0))
						{
							setMensajeError("Cuenta fuera de vigencia.");
							return;
						}
					}

				}

				String tipoSesion = "";

				// En este sistema va a cambiar el esquema de permisos, por lo
				// que
				// no es necesario enviar a determinada página de acuerdo al
				// nivel
				// de permiso, sino se acumularán los permisos a los que tenga
				// derecho el usuario
				switch (rBD.getString("NivelPermiso"))
				{
					case "0":
						tipoSesion = "Admin";
						break;
					case "1":
						tipoSesion = "SuperAdmin";
						break;
					case "2":
						tipoSesion = "Usuario";
						break;

				}

				setMensajeError("Bienvenido, redireccionando...");
				setDatosUsuarioSesion(controlSesion, tipoSesion, rBD.getString("idUsuario"), rBD.getString("Nombre"),
						rBD.getString("NombreReal"), rBD.getString("CuentaCorreo"));
			}

			setMensajeError("Nombre de usuario y/o contraseña incorrectos.");

		} catch (Exception ex)
		{
			ex.printStackTrace();
		}

	}

}
