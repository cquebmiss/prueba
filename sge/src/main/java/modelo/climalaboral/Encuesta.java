package modelo.climalaboral;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import resources.DataBase;
import util.FacesUtils;

public class Encuesta
{

	private int				idEncuesta;
	private String			descripcion;
	private List<Seccion>	secciones;
	private List<Registro>	registrosRespuestas;

	public Encuesta()
	{
		// TODO Auto-generated constructor stub
	}

	public Encuesta(int idEncuesta, String descripcion)
	{
		super();
		this.idEncuesta = idEncuesta;
		this.descripcion = descripcion;
	}

	public void initEncuesta()
	{
		getSeccionesFromBD();

		getSecciones().stream().forEach(seccion ->
		{
			seccion.getPreguntasFromBD();

			seccion.getPreguntas().stream().forEach(pregunta ->
			{
				pregunta.getOpcionesFromBD();
			});

		});
	}

	//Método que debe ejecutarse después de obtener las secciones de la base de datos
	public void getResultadosEncuesta()
	{
		if (this.getSecciones() == null && this.getSecciones().isEmpty())
		{
			System.out.println("Encuestas sin resultados");
			return;
		}

		//Se obtienen los registros que contestaron la encuesta
		getRegistrosFromBD();

		PreparedStatement prep = null;
		ResultSet rBD = null;

		//Ahora obtener las respuestas y enlazarlas con las preguntas
		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionClimaLaboral();)
		{
			prep = conexion.prepareStatement(
					" SELECT r.idRegistro, p.idSeccion, s.Orden AS ordenSec, r.idPregunta, p.idTipoPregunta, p.Orden AS ordenPreg, r.idOpcion AS idOpcionElegida, r.RespuestaAbierta, r.idRespuesta  from respuesta r, pregunta p, seccion s \n"
							+ "where r.idPregunta = p.idPregunta AND p.idSeccion = s.idSeccion AND s.idEncuesta=? \n"
							+ "order by r.idRegistro ASC, ordenSec ASC, ordenPreg ASC");

			prep.setInt(1, this.idEncuesta);

			rBD = prep.executeQuery();

			if (rBD.next())
			{
				do
				{
					int idRegistro = rBD.getInt("idRegistro");
					//Primero se obtiene el registro correspondiente
					Optional<Registro> regOptional = this.registrosRespuestas.stream()
							.filter(reg -> reg.getIdRegistro() == idRegistro).findFirst();

					int idPregunta = rBD.getInt("idPregunta");
					int idRespuesta = rBD.getInt("idRespuesta");

					boolean respuestaAñadida = false;

					switch (rBD.getInt("idTipoPregunta"))
					{
						case 2:
							String respuestaAbierta = rBD.getString("RespuestaAbierta");

							for (Seccion sec : this.secciones)
							{
								if (respuestaAñadida)
								{
									break;
								}

								for (Pregunta pregSec : sec.getPreguntas())
								{
									if (pregSec.getIdPregunta() == idPregunta)
									{
										pregSec.initRespuestasResultados();

										Respuesta res = new Respuesta(idRespuesta, pregSec, respuestaAbierta);
										res.setRegistro(regOptional.get());

										pregSec.getRespuestasResultados().add(res);
										respuestaAñadida = true;
										break;

									}

								}
							}

						break;

						case 3:
							int opcionElegida = rBD.getInt("idOpcionElegida");

							for (Seccion sec : this.secciones)
							{

								if (respuestaAñadida)
								{
									break;
								}

								for (Pregunta pregSec : sec.getPreguntas())
								{

									if (respuestaAñadida)
									{
										break;
									}

									if (pregSec.getIdPregunta() == idPregunta)
									{
										pregSec.initRespuestasResultados();

										//Se localiza la opción elegida
										for (Opcion opcion : pregSec.getOpciones())
										{
											if (opcion.getIdOpcion() == opcionElegida)
											{
												Respuesta res = new Respuesta(idRespuesta, pregSec, opcion);
												res.setRegistro(regOptional.get());

												pregSec.getRespuestasResultados().add(res);
												respuestaAñadida = true;
												break;

											}
										}

									}

								}
							}

						break;

					}

				} while (rBD.next());

			}

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al obtener las respiestas de la encuesta, favor de contactar con el desarrollador del sistema."));

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

	public void getSeccionesFromBD()
	{
		PreparedStatement prep = null;
		ResultSet rBD = null;

		this.secciones = new ArrayList<>();

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionClimaLaboral();)
		{
			prep = conexion.prepareStatement(" SELECT * FROM seccion WHERE idEncuesta=0");

			rBD = prep.executeQuery();

			if (rBD.next())
			{
				do
				{
					secciones.add(new Seccion(rBD.getInt("idSeccion"), rBD.getString("Descripcion"),
							rBD.getInt("Orden"), this));

				} while (rBD.next());

			}

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al obtener las secciones de la encuesta, favor de contactar con el desarrollador del sistema."));

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

	//Graba todas las respuestas de la encuesta
	public void persistRegistroEncuesta(Area area, Profesion profesion, Jornada jornada, int folio)
	{
		PreparedStatement prep = null;
		ResultSet rBD = null;
		Registro registro = new Registro();
		registro.setArea(area);
		registro.setProfesion(profesion);
		registro.setJornada(jornada);
		registro.setFolio(folio);

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionClimaLaboral();)
		{
			try
			{

				conexion.setAutoCommit(false);
				conexion.rollback();

				prep = conexion.prepareStatement("UPDATE folio SET Encuestado=true WHERE Folio=?");
				prep.setInt(1, folio);
				prep.executeUpdate();
				
				prep.close();

				java.util.Date utilDate = new java.util.Date();

				prep = conexion.prepareStatement(
						"INSERT INTO registro (idEncuesta,Fecha,Hora,idArea, idProfesion, idJornada, Folio) VALUES (?, ?, ?, ?, ?, ?, ?) ",
						PreparedStatement.RETURN_GENERATED_KEYS);

				prep.setInt(1, this.getIdEncuesta());
				prep.setDate(2, new java.sql.Date(utilDate.getTime()));
				prep.setTime(3, new java.sql.Time(utilDate.getTime()));
				prep.setInt(4, registro.getArea().getIdArea());
				prep.setInt(5, registro.getProfesion().getIdProfesion());
				prep.setInt(6, registro.getJornada().getIdJornada());
				prep.setInt(7, registro.getFolio());

				prep.executeUpdate();

				rBD = prep.getGeneratedKeys();

				if (rBD.next())
				{
					registro.setIdRegistro(rBD.getInt(1));
				}

				prep.close();
				rBD.close();

				prep = conexion.prepareStatement(" INSERT respuesta (idPregunta,idOpcion,RespuestaAbierta,idRegistro)\n"
						+ "VALUES ( ?, ?, ?, ?) ;", PreparedStatement.RETURN_GENERATED_KEYS);

				for (Seccion sec : this.getSecciones())
				{
					for (Pregunta preg : sec.getPreguntas())
					{
						prep.setInt(1, preg.getIdPregunta());

						switch (preg.getTipoPregunta().getIdTipoPregunta())
						{
							case 3:

								prep.setInt(2, preg.getRespuesta().getOpcion().getIdOpcion());
								prep.setNull(3, Types.VARCHAR);
							break;

							case 2:

								prep.setNull(2, Types.INTEGER);
								prep.setString(3, preg.getRespuesta().getRespuestaAbierta());

							break;

						}

						prep.setInt(4, registro.getIdRegistro());

						prep.addBatch();

					}

				}

				prep.executeBatch();

				conexion.commit();

			}
			catch (Exception e)
			{
				e.printStackTrace();
				conexion.rollback();
			}

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al guardar la encuesta, favor de contactar con el desarrollador del sistema."));

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

	public void getRegistrosFromBD()
	{
		PreparedStatement prep = null;
		ResultSet rBD = null;
		this.registrosRespuestas = new ArrayList<>();

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionClimaLaboral();)
		{

			prep = conexion.prepareStatement("SELECT * FROM registro WHERE idEncuesta=?");
			prep.setInt(1, this.idEncuesta);

			rBD = prep.executeQuery();

			if (rBD.next())
			{
				do
				{
					this.registrosRespuestas.add(new Registro(rBD.getInt("idRegistro"), this, rBD.getDate("Fecha"),
							rBD.getTime("Hora"), rBD.getInt("Folio"), rBD.getString("Observaciones")));

				} while (rBD.next());
			}

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al obtener los registros de la encuesta, favor de contactar con el desarrollador del sistema."));

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

	public int getIdEncuesta()
	{
		return idEncuesta;
	}

	public void setIdEncuesta(int idEncuesta)
	{
		this.idEncuesta = idEncuesta;
	}

	public String getDescripcion()
	{
		return descripcion;
	}

	public void setDescripcion(String descripcion)
	{
		this.descripcion = descripcion;
	}

	public List<Seccion> getSecciones()
	{
		return secciones;
	}

	public void setSecciones(List<Seccion> secciones)
	{
		this.secciones = secciones;
	}

	public List<Registro> getRegistrosRespuestas()
	{
		return registrosRespuestas;
	}

	public void setRegistrosRespuestas(List<Registro> registrosRespuestas)
	{
		this.registrosRespuestas = registrosRespuestas;
	}

}
