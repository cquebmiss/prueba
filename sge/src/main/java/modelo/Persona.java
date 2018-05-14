package modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import resources.DataBase;
import util.FacesUtils;

public class Persona
{
	private int idPersona;
	private String nombre;
	private String apellidoPaterno;
	private String apellidoMaterno;
	private String cargo;
	private String sexo;
	private String titulo;
	private String email;

	//En caso de que el objeto persona tenga registro vinculado como trabajador dentro de los registros de la nómina se enlaza con la clave foránea
	private Plaza plaza;
	private String numEmpleado;

	public Persona()
	{
		super();
	}

	public Persona(int idPersona, String nombre, String apellidoPaterno, String apellidoMaterno, String cargo,
			String sexo, String titulo)
	{
		super();
		this.idPersona = idPersona;
		this.nombre = nombre;
		this.apellidoPaterno = apellidoPaterno;
		this.apellidoMaterno = apellidoMaterno;
		this.cargo = cargo;
		this.sexo = sexo;
		this.titulo = titulo;
	}

	public void updateEmail()
	{
		PreparedStatement prep = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{
			prep = conexion.prepareStatement("UPDATE persona SET email=? WHERE idPersona=? ");
			prep.setString(1, this.email);
			prep.setInt(2, this.idPersona);

			prep.executeUpdate();

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al actualizar el email de la persona, favor de contactar con el desarrollador del sistema."));

			e.printStackTrace();
		}
		finally
		{
			if (prep != null)
			{
				try
				{
					prep.close();
				}
				catch (SQLException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	public String getNombreCompletoYCargo()
	{
		return this.nombre + " " + this.apellidoPaterno + " " + this.apellidoMaterno + " - " + this.cargo;
	}

	public String getNombreCompleto()
	{
		return this.nombre + " " + this.apellidoPaterno + " " + this.apellidoMaterno;
	}

	public int getIdPersona()
	{
		return idPersona;
	}

	public void setIdPersona(int idPersona)
	{
		this.idPersona = idPersona;
	}

	public String getNombre()
	{
		return nombre;
	}

	public void setNombre(String nombre)
	{
		this.nombre = nombre;
	}

	public String getApellidoPaterno()
	{
		return apellidoPaterno;
	}

	public void setApellidoPaterno(String apellidoPaterno)
	{
		this.apellidoPaterno = apellidoPaterno;
	}

	public String getApellidoMaterno()
	{
		return apellidoMaterno;
	}

	public void setApellidoMaterno(String apellidoMaterno)
	{
		this.apellidoMaterno = apellidoMaterno;
	}

	public String getCargo()
	{
		return cargo;
	}

	public void setCargo(String cargo)
	{
		this.cargo = cargo;
	}

	public Plaza getPlaza()
	{
		return plaza;
	}

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

	public String getSexo()
	{
		return sexo;
	}

	public void setSexo(String sexo)
	{
		this.sexo = sexo;
	}

	public String getTitulo()
	{
		return titulo;
	}

	public void setTitulo(String titulo)
	{
		this.titulo = titulo;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

}
