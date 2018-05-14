package gui.portal.minuta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import modelo.minutas.Participante;
import resources.DataBase;
import util.FacesUtils;

@ManagedBean
@RequestScoped
public class DialogoParticipanteBean
{
	private String nombres;
	private String apellidoPaterno;
	private String apellidoMaterno;
	private String cargo;
	private String sexo;
	private String titulo;

	private boolean valido;

	public DialogoParticipanteBean()
	{
	}

	@PostConstruct
	public void postConstruct()
	{

	}

	public void crearPersona()
	{
		PreparedStatement prep = null;
		ResultSet rBD = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{
			prep = conexion.prepareStatement(
					" INSERT INTO persona (Nombre, ApPaterno, ApMaterno, Cargo, Sexo, Titulo) VALUES (?, ?, ?, ?, ?, ?) ");

			prep.setString(1, this.nombres.trim());
			prep.setString(2, this.apellidoPaterno.trim());
			prep.setString(3, this.apellidoMaterno.trim());
			prep.setString(4, this.cargo.trim());
			prep.setString(5, this.sexo);
			prep.setString(6, this.titulo);

			prep.executeUpdate();

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al crear el registro de la persona, favor de contactar con el desarrollador del sistema."));

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

	public Participante getParticipante()
	{
		Participante participante = null;

		PreparedStatement prep = null;
		ResultSet rBD = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{
			prep = conexion.prepareStatement(
					" SELECT * FROM persona WHERE Nombre = ? AND ApPaterno = ? AND ApMaterno = ? AND Cargo = ? ");

			prep.setString(1, this.nombres.trim());
			prep.setString(2, this.apellidoPaterno.trim());
			prep.setString(3, this.apellidoMaterno.trim());
			prep.setString(4, this.cargo.trim());

			rBD = prep.executeQuery();

			if (rBD.next())
			{
				return new Participante(rBD.getInt("idPersona"), rBD.getString("Nombre"), rBD.getString("ApPaterno"),
						rBD.getString("ApMaterno"), rBD.getString("Sexo"), rBD.getString("Titulo"),
						rBD.getString("Cargo"), -1, null, -1, rBD.getString("Email"));

			}

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al obtener el registro de la persona que será participante, favor de contactar con el desarrollador del sistema."));

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

		return participante;
	}

	public boolean actionExisteParticipante()
	{
		System.out.println("Validando participante");

		PreparedStatement prep = null;
		ResultSet rBD = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{
			prep = conexion.prepareStatement(
					" SELECT * FROM persona WHERE Nombre = ? AND ApPaterno = ? AND ApMaterno = ? AND Cargo = ? ");

			prep.setString(1, this.nombres.trim());
			prep.setString(2, this.apellidoPaterno.trim());
			prep.setString(3, this.apellidoMaterno.trim());
			prep.setString(4, this.cargo.trim());

			rBD = prep.executeQuery();

			if (rBD.next())
			{
				return true;

			}

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al validar el registro de la persona participante, favor de contactar con el desarrollador del sistema."));

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

		return false;

	}

	public String getNombres()
	{
		return nombres;
	}

	public void setNombres(String nombres)
	{
		this.nombres = nombres;
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

	public boolean isValido()
	{
		return valido;
	}

	public void setValido(boolean valido)
	{
		this.valido = valido;
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

}
