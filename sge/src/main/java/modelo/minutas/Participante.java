package modelo.minutas;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import modelo.Persona;
import resources.DataBase;
import util.FacesUtils;

public class Participante
{
	private int idParticipante;
	private Persona persona;
	private Minuta minuta;
	//indica únicamente el orden de aparición dentro de la minuta
	private int orden;
	private String firma;
	private String email;

	public Participante()
	{
		super();
	}

	public Participante(String nombre, String apellidoPaterno, String apellidoMaterno, String cargo, String sexo,
			String titulo, int idParticipante, Minuta minuta, int orden)
	{
		super();
		this.persona = new Persona(-1, nombre, apellidoPaterno, apellidoMaterno, cargo, sexo, titulo);
		this.idParticipante = idParticipante;
		this.minuta = minuta;
		this.orden = orden;
	}

	public Participante(int idPersona, String nombre, String apellidoPaterno, String apellidoMaterno, String cargo,
			String sexo, String titulo, int idParticipante, Minuta minuta, int orden, String email)
	{
		super();
		this.persona = new Persona(idPersona, nombre, apellidoPaterno, apellidoMaterno, cargo, sexo, titulo);
		this.idParticipante = idParticipante;
		this.minuta = minuta;
		this.orden = orden;
		this.email = email;
	}

	public void updateIdParticipante()
	{
		PreparedStatement prep = null;
		ResultSet rBD = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionMinutas();)
		{
			prep = conexion
					.prepareStatement("SELECT idParticipante FROM participante WHERE idMinuta=? AND Orden=? LIMIT 1");
			prep.setInt(1, this.minuta.getIdMinuta());
			prep.setInt(2, this.orden);

			rBD = prep.executeQuery();

			if (rBD.next())
			{
				setIdParticipante(rBD.getInt("idParticipante"));
			}

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al añadir al participante, favor de contactar con el desarrollador del sistema."));

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

	public void updateEmailPersona()
	{	
		this.persona.updateEmail();

	}

	public void updateEmailParticipante()
	{
		PreparedStatement prep = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionMinutas();)
		{
			prep = conexion.prepareStatement("UPDATE participante SET email=? WHERE idParticipante=? ");
			prep.setString(1, this.email);
			prep.setInt(2, this.idParticipante);

			prep.executeUpdate();

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al actualizar el email del participante, favor de contactar con el desarrollador del sistema."));

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

	public void addFirmaBD()
	{
		PreparedStatement prep = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionMinutas();)
		{
			prep = conexion.prepareStatement("UPDATE participante SET Firma=? where idMinuta=? AND idParticipante=?");
			prep.setString(1, this.firma);
			prep.setInt(2, this.minuta.getIdMinuta());
			prep.setInt(3, this.idParticipante);

			prep.executeUpdate();

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al registrar la firma en la bd, favor de contactar con el desarrollador del sistema."));

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

	public int getIdParticipante()
	{
		return idParticipante;
	}

	public void setIdParticipante(int idParticipante)
	{
		this.idParticipante = idParticipante;
	}

	public Persona getPersona()
	{
		return persona;
	}

	public void setPersona(Persona persona)
	{
		this.persona = persona;
	}

	public Minuta getMinuta()
	{
		return minuta;
	}

	public void setMinuta(Minuta minuta)
	{
		this.minuta = minuta;
	}

	public int getOrden()
	{
		return orden;
	}

	public void setOrden(int orden)
	{
		this.orden = orden;
	}

	public String getFirma()
	{
		return firma;
	}

	public void setFirma(String firma)
	{
		this.firma = firma;
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
