package modelo.minutas;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import modelo.Persona;
import resources.DataBase;
import util.FacesUtils;
import java.util.Optional;

public class Minuta
{

	private int idMinuta;
	private String descripcion;
	private String lugar;
	private Date FechaHora;
	private String fechaHoraString;
	private String introduccion;
	private String desarrollo;
	private String conclusion;
	private StatusMinuta status;

	private List<Participante> participantes;
	private List<TemaMinuta> temas;
	private List<Compromiso> compromisos;

	public Minuta()
	{
		// TODO Auto-generated constructor stub
	}

	public Minuta(int idMinuta, String descripcion, Date fechaHora, StatusMinuta status)
	{
		super();
		this.idMinuta = idMinuta;
		this.descripcion = descripcion;
		FechaHora = fechaHora;
		this.status = status;

		this.introduccion = "";
		this.desarrollo = "";
		this.conclusion = "";
		this.participantes = new ArrayList<>();
		this.temas = new ArrayList<>();
		this.compromisos = new ArrayList<>();
	}

	public void crearMinutaBD()
	{
		PreparedStatement prep = null;
		ResultSet rBD = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionMinutas();)
		{
			//Obtener el idActual a insertar
			int idInsercion = 0;

			prep = conexion.prepareStatement("SELECT idMinuta FROM minuta ORDER BY idMinuta DESC limit 1");
			rBD = prep.executeQuery();

			if (rBD.next())
			{
				idInsercion = (rBD.getInt("idMinuta") + 1);
			}

			prep.close();
			rBD.close();

			boolean correcto = false;

			do
			{

				try
				{
					prep = conexion.prepareStatement(
							" INSERT INTO minuta (idMinuta, Fecha, Hora, Descripcion, Lugar, Introduccion, Desarrollo, Finalizacion, idStatus) "
									+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?); ");

					prep.setInt(1, idInsercion);
					prep.setDate(2, new java.sql.Date(this.FechaHora.getTime()));
					prep.setTime(3, new java.sql.Time(this.FechaHora.getTime()));
					prep.setString(4, this.descripcion);
					prep.setString(5, this.lugar);
					prep.setNull(6, java.sql.Types.LONGVARCHAR);
					prep.setNull(7, java.sql.Types.LONGVARCHAR);
					prep.setNull(8, java.sql.Types.LONGVARCHAR);
					//Status 0 = Creada
					prep.setInt(9, 0);

					prep.executeUpdate();

					setIdMinuta(idInsercion);

				}
				catch (Exception e)
				{
					idInsercion++;
					e.printStackTrace();
				}

				correcto = true;

			} while (!correcto);

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al crear la minuta, favor de contactar con el desarrollador del sistema."));

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

	public void updateAllDataBD()
	{
		getDatosBasicosFromBD();
		getParticipantesFromBD();
		getTemasFromBD();
		getCompromisosFromBD();
	}

	public void updateDatosBasicosMinuta()
	{
		PreparedStatement prep = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionMinutas();)
		{

			prep = conexion
					.prepareStatement("UPDATE minuta set Descripcion=?, Lugar=?, Fecha=?, Hora=? WHERE idMinuta=?");
			prep.setString(1, this.getDescripcion());
			prep.setString(2, this.getLugar());
			prep.setDate(3, new java.sql.Date(this.FechaHora.getTime()));
			prep.setTime(4, new java.sql.Time(this.getFechaHora().getTime()));

			prep.setInt(5, this.idMinuta);

			prep.executeUpdate();

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al actualizar los datos básicos de la minuta, favor de contactar con el desarrollador del sistema."));

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

	public void getDatosBasicosFromBD()
	{
		PreparedStatement prep = null;
		ResultSet rBD = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionMinutas();)
		{

			prep = conexion.prepareStatement("SELECT * FROM minuta WHERE idMinuta=?");
			prep.setInt(1, this.idMinuta);

			rBD = prep.executeQuery();

			if (rBD.next())
			{
				this.setLugar(rBD.getString("Lugar"));
				this.setIntroduccion(rBD.getString("Introduccion"));
				this.setConclusion(rBD.getString("Finalizacion"));

				DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date fechaHora = formatter.parse(rBD.getString("Fecha") + " " + rBD.getString("Hora"));

				this.setFechaHora(fechaHora);

				this.updateFechaHoraString();

			}

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al obtener los datos básicos de la minuta, favor de contactar con el desarrollador del sistema."));

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

	//MÉTODOS PARA PARTICIPANTES

	public void getParticipantesFromBD()
	{
		this.participantes = new ArrayList<>();

		PreparedStatement prep = null;
		ResultSet rBD = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionMinutas();)
		{

			prep = conexion.prepareStatement(
					"SELECT par.*, per.* FROM minutas.participante par, webrh.persona per WHERE par.idPersona = per.idPersona AND par.idMinuta=?");
			prep.setInt(1, this.idMinuta);
			rBD = prep.executeQuery();

			if (rBD.next())
			{
				do
				{
					Participante participante = new Participante();
					participante.setIdParticipante(rBD.getInt("idParticipante"));
					participante.setMinuta(this);
					participante.setOrden(rBD.getInt("Orden"));
					participante.setFirma(rBD.getString("Firma"));
					participante.setEmail(rBD.getString("Email"));

					Persona persona = new Persona();
					persona.setIdPersona(rBD.getInt("idPersona"));
					persona.setNombre(rBD.getString("Nombre"));
					persona.setApellidoPaterno(rBD.getString("ApPaterno"));
					persona.setApellidoMaterno(rBD.getString("ApMaterno"));
					persona.setCargo(rBD.getString("Cargo"));
					persona.setSexo(rBD.getString("Sexo"));
					persona.setTitulo(rBD.getString("Titulo"));
					persona.setEmail(rBD.getString("Email"));

					participante.setPersona(persona);

					this.participantes.add(participante);

				} while (rBD.next());
			}

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al obtener los participantes de la bd, favor de contactar con el desarrollador del sistema."));

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

	public void addParticipante(Participante participante)
	{
		PreparedStatement prep = null;
		ResultSet rBD = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionMinutas();)
		{
			//Se obtiene el orden en que irá el participante en la minuta
			int ordenEnMinuta = 0;

			prep = conexion.prepareStatement("SELECT count(*) as totalPart FROM participante WHERE idMinuta=? LIMIT 1");
			prep.setInt(1, this.idMinuta);
			rBD = prep.executeQuery();

			if (rBD.next())
			{
				ordenEnMinuta = rBD.getInt("totalPart");
			}

			prep.close();
			rBD.close();

			boolean correcto = false;

			do
			{

				try
				{
					prep = conexion.prepareStatement(
							" INSERT INTO participante (idMinuta, Orden, idPersona) VALUES (?, ?, ?) ");

					prep.setInt(1, this.idMinuta);
					prep.setInt(2, ordenEnMinuta);
					prep.setInt(3, participante.getPersona().getIdPersona());

					prep.executeUpdate();

					correcto = true;

				}
				catch (Exception e)
				{
					ordenEnMinuta++;
				}

			} while (!correcto);

			participante.setOrden(ordenEnMinuta);
			participante.updateIdParticipante();

			this.participantes.add(participante);

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

	public void removeParticipante(Participante participante)
	{
		PreparedStatement prep = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionMinutas();)
		{
			prep = conexion.prepareStatement("DELETE FROM participante WHERE idMinuta=? AND idParticipante=?");
			prep.setInt(1, this.idMinuta);
			prep.setInt(2, participante.getIdParticipante());
			prep.executeUpdate();

			this.participantes.remove(participante);
			reordenarIndicesParticipantes();

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al remover al participante de la minuta, favor de contactar con el desarrollador del sistema."));

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

	//reordena el índice de orden dentro de la minuta
	public void reordenarIndicesParticipantes()
	{
		for (int x = 0; x < this.participantes.size(); x++)
		{

			PreparedStatement prep = null;

			try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionMinutas();)
			{
				prep = conexion
						.prepareStatement("UPDATE participante SET Orden=? WHERE idMinuta=? AND idParticipante=?");
				prep.setInt(1, x);
				prep.setInt(2, this.idMinuta);
				prep.setInt(3, this.participantes.get(x).getIdParticipante());
				prep.executeUpdate();

				this.participantes.get(x).setOrden(x);

			}
			catch (Exception e)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Excepción",
						"Ha ocurrido una excepción al reordenar los participantes de la minuta, favor de contactar con el desarrollador del sistema."));

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
	}

	//FIN DE MÉTODOS PARA PARTICIPANTES

	//MÉTODOS PARA AÑADIR TEMAS

	public boolean addNuevoTemaBD(TemaMinuta temaMinuta)
	{
		//		temaMinuta.setOrden(this.temas.size());

		PreparedStatement prep = null;
		ResultSet rBD = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionMinutas();)
		{
			int ordenEnMinuta = 0;

			prep = conexion.prepareStatement("SELECT count(*) as total FROM temaminuta WHERE idMinuta=? LIMIT 1");
			prep.setInt(1, this.idMinuta);
			rBD = prep.executeQuery();

			if (rBD.next())
			{
				ordenEnMinuta = rBD.getInt("total");
			}

			prep.close();
			rBD.close();

			boolean correcto = false;

			do
			{

				try
				{
					prep = conexion.prepareStatement(
							" INSERT INTO temaminuta (idMinuta, Orden, Descripcion, Desarrollo, idParticipante) VALUES (?, ?, ?, ?, ?); ");

					prep.setInt(1, this.idMinuta);
					prep.setInt(2, ordenEnMinuta);
					prep.setString(3, temaMinuta.getDescripcion());
					prep.setString(4, temaMinuta.getDesarrollo());
					prep.setNull(5, temaMinuta.getResponsable().getIdParticipante());

					prep.executeUpdate();

					temaMinuta.setOrden(ordenEnMinuta);
					this.temas.add(temaMinuta);

					correcto = true;

				}
				catch (Exception e)
				{
					ordenEnMinuta++;
				}

			} while (!correcto);

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al añadir un nuevo tema a la minuta, favor de contactar con el desarrollador del sistema."));

			e.printStackTrace();

			return false;
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

		return true;

	}

	public boolean updateTemaMinutaBD(TemaMinuta temaMinuta)
	{

		PreparedStatement prep = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionMinutas();)
		{

			prep = conexion.prepareStatement(
					" UPDATE temaminuta SET Descripcion=?, Desarrollo=?, idParticipante=? WHERE idMinuta=? AND Orden=?");

			prep.setString(1, temaMinuta.getDescripcion());
			prep.setString(2, temaMinuta.getDesarrollo());
			prep.setInt(3, temaMinuta.getResponsable().getIdParticipante());
			prep.setInt(4, this.idMinuta);
			prep.setInt(5, temaMinuta.getOrden());

			prep.executeUpdate();

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al actualizar el tema de la minuta, favor de contactar con el desarrollador del sistema."));

			e.printStackTrace();
			return false;
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

		return true;

	}

	public boolean updateDesarrolloTemaMinutaBD(TemaMinuta temaMinuta)
	{

		PreparedStatement prep = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionMinutas();)
		{

			prep = conexion.prepareStatement(" UPDATE temaminuta SET Desarrollo=? WHERE idMinuta=? AND Orden=?");

			prep.setString(1, temaMinuta.getDesarrollo());
			prep.setInt(2, this.idMinuta);
			prep.setInt(3, temaMinuta.getOrden());

			prep.executeUpdate();

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al actualizar el desarrollo del tema de la minuta, favor de contactar con el desarrollador del sistema."));

			e.printStackTrace();
			return false;
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

		return true;

	}

	public void removeTema(TemaMinuta temaMinuta)
	{
		PreparedStatement prep = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionMinutas();)
		{
			prep = conexion.prepareStatement("DELETE FROM temaminuta WHERE idMinuta=? AND Orden=?");
			prep.setInt(1, temaMinuta.getMinuta().getIdMinuta());
			prep.setInt(2, temaMinuta.getOrden());
			prep.executeUpdate();

			this.temas.remove(temaMinuta);
			reordenarIndicesTemaMinuta();

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al remover el tema de la minuta, favor de contactar con el desarrollador del sistema."));

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

	//reordena el índice de orden dentro de la minuta
	public void reordenarIndicesTemaMinuta()
	{
		for (int x = 0; x < this.temas.size(); x++)
		{

			PreparedStatement prep = null;

			try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionMinutas();)
			{
				prep = conexion.prepareStatement("UPDATE temaminuta SET Orden=? WHERE idMinuta=? AND Orden=?");
				prep.setInt(1, x);
				prep.setInt(2, temas.get(x).getMinuta().getIdMinuta());
				prep.setInt(3, temas.get(x).getOrden());
				prep.executeUpdate();

				this.temas.get(x).setOrden(x);

			}
			catch (Exception e)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Excepción",
						"Ha ocurrido una excepción al reordenar los temas de la minuta, favor de contactar con el desarrollador del sistema."));

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
	}

	public void getTemasFromBD()
	{
		this.temas = new ArrayList<>();

		PreparedStatement prep = null;
		ResultSet rBD = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionMinutas();)
		{

			prep = conexion.prepareStatement("SELECT * FROM temaminuta WHERE idMinuta=?");
			prep.setInt(1, this.idMinuta);
			rBD = prep.executeQuery();

			if (rBD.next())
			{

				do
				{
					TemaMinuta temaMinuta = new TemaMinuta();
					temaMinuta.setMinuta(this);
					temaMinuta.setOrden(rBD.getInt("Orden"));
					temaMinuta.setDescripcion(rBD.getString("Descripcion"));
					temaMinuta.setDesarrollo(rBD.getString("Desarrollo"));
					final int idResponsable = rBD.getInt("idParticipante");

					Optional<Participante> responsable = this.participantes.stream()
							.filter(part -> part.getIdParticipante() == idResponsable).findFirst();

					if (responsable.isPresent())
					{
						temaMinuta.setResponsable(responsable.get());
					}

					this.temas.add(temaMinuta);

				} while (rBD.next());
			}

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al obtener los temas de la bd, favor de contactar con el desarrollador del sistema."));

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

	//FIN DE MÉTODOS PARA TEMAS

	//MÉTODOS PARA COMPROMISOS

	public boolean addCompromisoBD(Compromiso compromiso)
	{
		compromiso.setOrden(this.temas.size());

		PreparedStatement prep = null;
		ResultSet rBD = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionGestiones();)
		{
			try
			{

				conexion.setAutoCommit(false);
				conexion.rollback();

				prep = conexion.prepareStatement("INSERT INTO actividad (Descripcion, idStatus) VALUES (?, ?); ",
						PreparedStatement.RETURN_GENERATED_KEYS);
				prep.setString(1, compromiso.getDescripcion());
				prep.setInt(2, -1);
				prep.executeUpdate();

				rBD = prep.getGeneratedKeys();

				if (rBD.next())
				{
					compromiso.getActividad().setIdActividad(rBD.getInt(1));
					compromiso.getActividad().setDescripcion(compromiso.getDescripcion());
				}

				prep.close();
				rBD.close();

				prep = conexion.prepareStatement(
						" INSERT INTO minutas.compromiso (idMinuta, idActividad, Descripcion, Orden, Responsable, FechaFinalizacionEstimada, Resolucion)"
								+ " VALUES (?, ?, ?, ?, ?, ?, ?);",
						PreparedStatement.RETURN_GENERATED_KEYS);

				prep.setInt(1, compromiso.getMinuta().getIdMinuta());
				prep.setInt(2, compromiso.getActividad().getIdActividad());
				prep.setString(3, compromiso.getDescripcion());
				prep.setInt(4, compromiso.getOrden());
				prep.setInt(5, compromiso.getResponsable().getPersona().getIdPersona());

				if (compromiso.getFechaFinalizacionEstimada() != null)
				{
					prep.setDate(6, new java.sql.Date(compromiso.getFechaFinalizacionEstimada().getTime()));

				}
				else
				{
					prep.setNull(6, java.sql.Types.DATE);

				}

				prep.setNull(7, java.sql.Types.VARCHAR);

				prep.executeUpdate();

				rBD = prep.getGeneratedKeys();

				if (rBD.next())
				{
					compromiso.setIdCompromiso(rBD.getInt(1));
				}

				conexion.commit();

				this.compromisos.add(compromiso);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				conexion.rollback();
			}

		}
		catch (

		Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al añadir un nuevo compromiso a la minuta, favor de contactar con el desarrollador del sistema."));

			e.printStackTrace();

			return false;
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

		return true;

	}

	public boolean updateCompromisoBD(Compromiso compromiso)
	{

		PreparedStatement prep = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionMinutas();)
		{

			prep = conexion.prepareStatement(" UPDATE gestiones.actividad SET Descripcion=? WHERE idActividad=?");
			prep.setString(1, compromiso.getActividad().getDescripcion());
			prep.setInt(2, compromiso.getActividad().getIdActividad());
			prep.executeUpdate();

			prep.close();

			prep = conexion.prepareStatement(
					" UPDATE compromiso SET Descripcion=?, Responsable=?, FechaFinalizacionEstimada=? WHERE idCompromiso=?");

			prep.setString(1, compromiso.getDescripcion());
			prep.setInt(2, compromiso.getResponsable().getPersona().getIdPersona());

			if (compromiso.getFechaFinalizacionEstimada() == null)
			{
				prep.setNull(3, java.sql.Types.DATE);
			}
			else
			{
				prep.setDate(3, new java.sql.Date(compromiso.getFechaFinalizacionEstimada().getTime()));
			}

			prep.setInt(4, compromiso.getIdCompromiso());

			prep.executeUpdate();

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al añadir actualizar el compromiso de la minuta, favor de contactar con el desarrollador del sistema."));

			e.printStackTrace();
			return false;
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

		return true;

	}

	public void removeCompromiso(Compromiso compromiso)
	{
		PreparedStatement prep = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionMinutas();)
		{

			prep = conexion.prepareStatement("DELETE FROM compromiso WHERE idCompromiso=? ");
			prep.setInt(1, compromiso.getIdCompromiso());
			prep.executeUpdate();
			prep.close();

			prep = conexion.prepareStatement("DELETE FROM gestiones.actividad WHERE idActividad=? ");
			prep.setInt(1, compromiso.getActividad().getIdActividad());
			prep.executeUpdate();

			this.compromisos.remove(compromiso);
			reordenarIndicesCompromisos();

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al remover el compromiso de la minuta, favor de contactar con el desarrollador del sistema."));

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

	//reordena el índice de orden dentro de la minuta
	public void reordenarIndicesCompromisos()
	{
		PreparedStatement prep = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionMinutas();)
		{

			for (int x = 0; x < this.compromisos.size(); x++)
			{

				prep = conexion.prepareStatement("UPDATE compromiso SET Orden=? WHERE idCompromiso=?");
				prep.setInt(1, x);
				prep.setInt(2, this.compromisos.get(x).getIdCompromiso());
				prep.executeUpdate();

				this.compromisos.get(x).setOrden(x);

				prep.close();

			}
		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al reordenar los compromisos de la minuta, favor de contactar con el desarrollador del sistema."));

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

	//Obtiene los compromisos sin la actividad correspondiente
	public void getCompromisosFromBD()
	{
		this.compromisos = new ArrayList<>();

		PreparedStatement prep = null;
		ResultSet rBD = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionMinutas();)
		{

			prep = conexion.prepareStatement("SELECT * FROM compromiso WHERE idMinuta=?");
			prep.setInt(1, this.idMinuta);
			rBD = prep.executeQuery();

			if (rBD.next())
			{

				do
				{
					Compromiso compromiso = new Compromiso();
					compromiso.setIdCompromiso(rBD.getInt("idCompromiso"));
					compromiso.setMinuta(this);
					compromiso.setDescripcion(rBD.getString("Descripcion"));
					compromiso.setOrden(rBD.getInt("Orden"));
					compromiso.setFechaFinalizacionEstimada(rBD.getDate("FechaFinalizacionEstimada"));
					compromiso.setResolucion(rBD.getString("Resolucion"));

					final int idResponsable = rBD.getInt("Responsable");

					Optional<Participante> responsable = this.participantes.stream()
							.filter(part -> part.getIdParticipante() == idResponsable).findFirst();

					if (responsable.isPresent())
					{
						compromiso.setResponsable(responsable.get());
					}

					this.compromisos.add(compromiso);

				} while (rBD.next());
			}

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al obtener los compromisos de la bd, favor de contactar con el desarrollador del sistema."));

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

	//FIN DE MÉTODOS PARA COMPROMISOS

	//MÉTODOS PARA INTRODUCCION

	public void updateIntroduccionMinuta()
	{
		PreparedStatement prep = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionMinutas();)
		{
			prep = conexion.prepareStatement("UPDATE minuta SET Introduccion=? WHERE idMinuta=?");
			prep.setString(1, this.introduccion);
			prep.setInt(2, this.idMinuta);

			prep.executeUpdate();
		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al guardar la introducción de la minuta, favor de contactar con el desarrollador del sistema."));

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

	//FIN DE MÉTODOS PARA INTRODUCCION

	//MÉTODOS PARA CONCLUSIÓN

	public void updateConclusionMinuta()
	{
		PreparedStatement prep = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionMinutas();)
		{
			prep = conexion.prepareStatement("UPDATE minuta SET Finalizacion=? WHERE idMinuta=?");
			prep.setString(1, this.conclusion);
			prep.setInt(2, this.idMinuta);
			prep.executeUpdate();

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al guardar la conclusión de la minuta, favor de contactar con el desarrollador del sistema."));

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

	//FIN DE MÉTODOS PARA CONCLUSIÓN

	public int getIdMinuta()
	{
		return idMinuta;
	}

	public void setIdMinuta(int idMinuta)
	{
		this.idMinuta = idMinuta;
	}

	public String getDescripcion()
	{
		return descripcion;
	}

	public void setDescripcion(String descripcion)
	{
		this.descripcion = descripcion;
	}

	public String getIntroduccion()
	{
		return introduccion;
	}

	public void setIntroduccion(String introduccion)
	{
		this.introduccion = introduccion;
	}

	public String getDesarrollo()
	{
		return desarrollo;
	}

	public void setDesarrollo(String desarrollo)
	{
		this.desarrollo = desarrollo;
	}

	public String getConclusion()
	{
		return conclusion;
	}

	public void setConclusion(String conclusion)
	{
		this.conclusion = conclusion;
	}

	public List<Participante> getParticipantes()
	{
		return participantes;
	}

	public void setParticipantes(List<Participante> participantes)
	{
		this.participantes = participantes;
	}

	public List<TemaMinuta> getTemas()
	{
		return temas;
	}

	public void setTemas(List<TemaMinuta> temas)
	{
		this.temas = temas;
	}

	public Date getFechaHora()
	{
		return FechaHora;
	}

	public void setFechaHora(Date fechaHora)
	{
		FechaHora = fechaHora;
	}

	public StatusMinuta getStatus()
	{
		return status;
	}

	public void setStatus(StatusMinuta status)
	{
		this.status = status;
	}

	public String getLugar()
	{
		return lugar;
	}

	public void setLugar(String lugar)
	{
		this.lugar = lugar;
	}

	public List<Compromiso> getCompromisos()
	{
		return compromisos;
	}

	public void setCompromisos(List<Compromiso> compromisos)
	{
		this.compromisos = compromisos;
	}

	public String getFechaHoraString()
	{
		return fechaHoraString;
	}

	public void setFechaHoraString(String fechaHoraString)
	{
		this.fechaHoraString = fechaHoraString;
	}

	//convierte el objeto java.util.Date fecha en la fecha y la hora en formato String
	public void updateFechaHoraString()
	{
		setFechaHoraString(new SimpleDateFormat("yyyy-MM-dd - HH:mm:dd").format(this.FechaHora));

	}

}
