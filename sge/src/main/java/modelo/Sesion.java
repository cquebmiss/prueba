/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.io.Serializable;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import util.utilidades;

@ManagedBean(name = "Sesion")
@SessionScoped
public class Sesion implements Serializable
{

	private String sesionActiva = "";
	private String nombreUsuario = "";
	private String nombreReal = "";
	private String idUsuario = "";
	private String email = "";
	private List<PermisoSistema> permisosSistemas;

	public Sesion()
	{
		setSesionActiva("");
	}

	public String getSesionActiva()
	{
		return sesionActiva;
	}

	public void setSesionActiva(String sesionActiva)
	{
		this.sesionActiva = sesionActiva;
	}

	public String getIdUsuario()
	{
		return idUsuario;
	}

	public void setIdUsuario(String idUsuario)
	{
		this.idUsuario = idUsuario;
	}

	public String getNombreUsuario()
	{
		return nombreUsuario;
	}

	public void setNombreUsuario(String nombreUsuario)
	{
		this.nombreUsuario = nombreUsuario;
	}

	public String getNombreReal()
	{
		return nombreReal;
	}

	public void setNombreReal(String nombreReal)
	{
		this.nombreReal = nombreReal;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	/**
	 * @return the permisosSistemas
	 */
	public List<PermisoSistema> getPermisosSistemas()
	{
		return permisosSistemas;
	}

	/**
	 * @param permisosSistemas the permisosSistemas to set
	 */
	public void setPermisosSistemas(List<PermisoSistema> permisosSistemas)
	{
		this.permisosSistemas = permisosSistemas;
	}

	public void getPermisosUsuarioSistemas()
	{
		setPermisosSistemas(utilidades.getPermisosSistemasUsuario(Integer.parseInt(getIdUsuario())));
	}

	//sistema es el índice del sistema que se está buscando que se tenga permitido
	public int tienePermisoSistema(int sistema)
	{
		int nivelPermiso = -1;

		if (getPermisosSistemas() != null)
		{

			for (PermisoSistema permisoSistema : getPermisosSistemas())
			{
				if (permisoSistema.getIdSistema() == sistema)
				{
					return permisoSistema.getTipoPermiso();
				}
			}

		}

		return nivelPermiso;
	}

	public void actionCerrarSesion()
	{
		try
		{
			setSesionActiva("");
			setNombreUsuario("");
			setNombreReal("");
			setIdUsuario("");
			setEmail("");
			setPermisosSistemas(null);

			FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
			FacesContext.getCurrentInstance().getExternalContext().redirect("/sge/login.jsf");

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

}
