package util.climalaboral;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import modelo.climalaboral.Area;
import modelo.climalaboral.Clasificacion;
import modelo.climalaboral.Encuesta;
import modelo.climalaboral.Jornada;
import modelo.climalaboral.Profesion;
import resources.DataBase;
import util.FacesUtils;

public class UtilidadesClimaLaboral
{
	//Obtiene la encuesta registrada dentro de la base de datos
	public static Encuesta getEncuestaFromBD()
	{
		PreparedStatement prep = null;
		ResultSet rBD = null;

		Encuesta encuesta = new Encuesta();

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionClimaLaboral();)
		{
			prep = conexion.prepareStatement(" SELECT * FROM encuesta WHERE idEncuesta=0");

			rBD = prep.executeQuery();

			if (rBD.next())
			{
				encuesta.setIdEncuesta(rBD.getInt("idEncuesta"));
				encuesta.setDescripcion(rBD.getString("Descripcion"));

			}

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al obtener la encuesta, favor de contactar con el desarrollador del sistema."));

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

		return encuesta;

	}

	//Obtiene el catálogo de profesiones
	public static List<Profesion> getCatProfesiones()
	{
		PreparedStatement prep = null;
		ResultSet rBD = null;

		List<Profesion> catProfesion = new ArrayList<>();

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionClimaLaboral();)
		{
			prep = conexion.prepareStatement(" SELECT * FROM profesion order by descripcion DESC");

			rBD = prep.executeQuery();

			if (rBD.next())
			{
				do
				{

					catProfesion.add(new Profesion(rBD.getInt("idProfesion"), rBD.getString("Descripcion")));

				} while (rBD.next());

			}

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al obtener el catálogo de profesiones, favor de contactar con el desarrollador del sistema."));

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

		return catProfesion;

	}

	//Obtiene el catálogo de Áreas
	public static List<Area> getCatAreas()
	{
		PreparedStatement prep = null;
		ResultSet rBD = null;

		List<Area> catAreas = new ArrayList<>();

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionClimaLaboral();)
		{
			prep = conexion.prepareStatement(" SELECT * FROM area order by descripcion DESC");

			rBD = prep.executeQuery();

			if (rBD.next())
			{
				do
				{

					catAreas.add(new Area(rBD.getInt("idArea"), rBD.getString("Descripcion")));

				} while (rBD.next());

			}

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al obtener el catálogo de áreas, favor de contactar con el desarrollador del sistema."));

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

		return catAreas;

	}

	//Obtiene el catálogo de Jornada
	public static List<Jornada> getCatJornadas()
	{
		PreparedStatement prep = null;
		ResultSet rBD = null;

		List<Jornada> catJornadas = new ArrayList<>();

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionClimaLaboral();)
		{
			prep = conexion.prepareStatement(" SELECT * FROM jornada order by idJornada ASC");

			rBD = prep.executeQuery();

			if (rBD.next())
			{
				do
				{

					catJornadas.add(new Jornada(rBD.getInt("idJornada"), rBD.getString("Descripcion")));

				} while (rBD.next());

			}

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al obtener el catálogo de jornadas, favor de contactar con el desarrollador del sistema."));

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

		return catJornadas;

	}

	//Obtiene el catálogo de clasificaciones de personal y jornadas para el sorteo de la encuesta
	public static List<Clasificacion> getCatClasificacion()
	{
		PreparedStatement prep = null;
		ResultSet rBD = null;

		List<Clasificacion> catClasificacion = new ArrayList<>();

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionClimaLaboral();)
		{
			prep = conexion.prepareStatement(" SELECT * FROM clasificacion order by idClasificacion ASC");

			rBD = prep.executeQuery();

			if (rBD.next())
			{
				do
				{

					catClasificacion.add(new Clasificacion(rBD.getInt("idClasificacion"), rBD.getString("Descripcion"),
							rBD.getInt("Total")));

				} while (rBD.next());

			}

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al obtener el catálogo de jornadas, favor de contactar con el desarrollador del sistema."));

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

		return catClasificacion;

	}

}
