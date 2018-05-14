package modelo.climalaboral;

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

public class Seccion
{

	private int				idSeccion;
	private String			descripcion;
	private int				orden;
	private Encuesta		encuesta;
	private List<Pregunta>	preguntas;

	public Seccion()
	{
		// TODO Auto-generated constructor stub
	}

	public Seccion(int idSeccion, String descripcion, int orden, Encuesta encuesta)
	{
		super();
		this.idSeccion = idSeccion;
		this.descripcion = descripcion;
		this.orden = orden;
		this.encuesta = encuesta;
	}

	public Seccion(int idSeccion, String descripcion, int orden, Encuesta encuesta, List<Pregunta> preguntas)
	{
		super();
		this.idSeccion = idSeccion;
		this.descripcion = descripcion;
		this.orden = orden;
		this.encuesta = encuesta;
		this.preguntas = preguntas;
	}

	public void getPreguntasFromBD()
	{
		PreparedStatement prep = null;
		ResultSet rBD = null;

		this.preguntas = new ArrayList<>();

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionClimaLaboral();)
		{
			prep = conexion.prepareStatement(
					" select p.*, tp.Descripcion AS descTipoPregunta from pregunta p, tipopregunta tp "
							+ "WHERE p.idSeccion=? AND p.idTipoPregunta = tp.idTipoPregunta  ");
			prep.setInt(1, this.idSeccion);

			rBD = prep.executeQuery();

			if (rBD.next())
			{
				do
				{
					this.preguntas.add(new Pregunta(rBD.getInt("idPregunta"), rBD.getString("Descripcion"),
							rBD.getInt("Orden"), this,
							new TipoPregunta(rBD.getInt("idTipoPregunta"), rBD.getString("descTipoPregunta"))));

				} while (rBD.next());

			}

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al obtener las preguntas de la sección, favor de contactar con el desarrollador del sistema."));

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

	public void getRespuestasFromBD()
	{
		getPreguntas().stream().forEach(preg ->
		{
			
			
		});

	}

	public int getIdSeccion()
	{
		return idSeccion;
	}

	public void setIdSeccion(int idSeccion)
	{
		this.idSeccion = idSeccion;
	}

	public String getDescripcion()
	{
		return descripcion;
	}

	public void setDescripcion(String descripcion)
	{
		this.descripcion = descripcion;
	}

	public int getOrden()
	{
		return orden;
	}

	public void setOrden(int orden)
	{
		this.orden = orden;
	}

	public Encuesta getEncuesta()
	{
		return encuesta;
	}

	public void setEncuesta(Encuesta encuesta)
	{
		this.encuesta = encuesta;
	}

	public List<Pregunta> getPreguntas()
	{
		return preguntas;
	}

	public void setPreguntas(List<Pregunta> preguntas)
	{
		this.preguntas = preguntas;
	}

}
