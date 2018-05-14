package util.minutas;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import modelo.actividades.Actividad;
import modelo.actividades.StatusActividad;
import modelo.minutas.Compromiso;
import modelo.minutas.Minuta;
import modelo.minutas.Participante;
import modelo.minutas.StatusMinuta;
import resources.DataBase;
import util.FacesUtils;
import util.UtilidadesCalendario;

public class UtilidadesMinutas
{

	public static List<StatusMinuta> getCatStatusMinutas()
	{
		PreparedStatement prep = null;
		ResultSet rBD = null;

		List<StatusMinuta> catStatusMinutas = new ArrayList<>();

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionMinutas();)
		{
			prep = conexion.prepareStatement(" SELECT * FROM status");

			rBD = prep.executeQuery();

			if (rBD.next())
			{
				do
				{
					catStatusMinutas.add(new StatusMinuta(rBD.getInt("idStatus"), rBD.getString("Descripcion")));

				} while (rBD.next());

			}

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al obtener el catálogo de status de las minutas, favor de contactar con el desarrollador del sistema."));

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

		return catStatusMinutas;

	}

	public static List<Minuta> getHistorialMinutas()
	{
		PreparedStatement prep = null;
		ResultSet rBD = null;

		List<Minuta> catHistorialMinutas = new ArrayList<>();

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionMinutas();)
		{
			prep = conexion.prepareStatement(
					" SELECT m.*, st.Descripcion AS descStatus FROm minutas.minuta m, minutas.status st WHERE m.idStatus = st.idStatus ORDER BY m.idMinuta DESC ");

			rBD = prep.executeQuery();

			if (rBD.next())
			{
				do
				{
					DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date fechaHora = formatter.parse(rBD.getString("Fecha") + " " + rBD.getString("Hora"));

					Minuta m = new Minuta(rBD.getInt("idMinuta"), rBD.getString("Descripcion"), fechaHora,
							new StatusMinuta(rBD.getInt("idStatus"), rBD.getString("descStatus")));

					m.updateFechaHoraString();

					catHistorialMinutas.add(m);

				} while (rBD.next());

			}

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al obtener el catálogo de minutas, favor de contactar con el desarrollador del sistema."));

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

		return catHistorialMinutas;

	}

	//Obtiene el historial de compromisos registrados
	public static List<Compromiso> getHistorialCompromisosBD(boolean soloNoFinalizados)
	{
		List<Compromiso> historialCompromisos = new ArrayList<>();

		PreparedStatement prep = null;
		ResultSet rBD = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionMinutas();)
		{
			String complementoNoFinalizados = "";

			if (soloNoFinalizados)
			{
				complementoNoFinalizados = " AND st.idStatus NOT IN (1) ";
			}

			prep = conexion.prepareStatement(
					"SELECT com.idCompromiso, com.descripcion as descCompromiso, com.Orden, com.Responsable as idPersonaResponsable, com.FechaFinalizacionEstimada, com.idMinuta, act.descripcion AS descActividad, "
							+ "act.idStatus AS statusActividad, st.Descripcion AS descStatus,min.Descripcion AS descMinuta, min.Fecha, min.Hora, min.Lugar,\n"
							+ "per.Nombre, per.ApPaterno, per.ApMaterno, per.Cargo, com.idActividad\n"
							+ "FROM minutas.compromiso com, gestiones.actividad act, gestiones.status st, minutas.minuta min, webrh.persona per\n"
							+ "WHERE com.idActividad = act.idActividad AND act.idStatus = st.idStatus AND com.idMinuta = min.idMinuta AND per.idPersona = com.Responsable "
							+ complementoNoFinalizados + " \n" + "ORDER BY com.idMinuta DESC, com.idCompromiso ASC  ");

			rBD = prep.executeQuery();

			if (rBD.next())
			{

				do
				{

					Minuta minutaOrigen = new Minuta(rBD.getInt("idMinuta"), rBD.getString("descMinuta"),
							UtilidadesCalendario.getUtilDate(rBD.getString("Fecha"), rBD.getString("Hora")),
							new StatusMinuta(-1, ""));

					Compromiso compromiso = new Compromiso();
					compromiso.setIdCompromiso(rBD.getInt("idCompromiso"));
					compromiso.setMinuta(minutaOrigen);
					compromiso.setDescripcion(rBD.getString("descCompromiso"));
					compromiso.setOrden(rBD.getInt("Orden"));
					compromiso.setFechaFinalizacionEstimada(rBD.getDate("FechaFinalizacionEstimada"));
					compromiso.setResponsable(new Participante(rBD.getInt("idPersonaResponsable"),
							rBD.getString("Nombre"), rBD.getString("ApPaterno"), rBD.getString("ApMaterno"),
							rBD.getString("Cargo"), "", "", rBD.getInt("idPersonaResponsable"), minutaOrigen, -1, ""));
					compromiso.updateFechaFinalizacionEstimadaString();

					compromiso.setActividad(new Actividad(rBD.getInt("idActividad"), rBD.getString("descActividad"),
							new StatusActividad(rBD.getInt("statusActividad"), rBD.getString("descStatus"))));

					historialCompromisos.add(compromiso);

				} while (rBD.next());
			}

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al obtener el historial de compromisos de la bd, favor de contactar con el desarrollador del sistema."));

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

		return historialCompromisos;
	}

}
