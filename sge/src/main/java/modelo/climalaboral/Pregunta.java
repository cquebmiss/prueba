package modelo.climalaboral;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import resources.DataBase;
import util.FacesUtils;

public class Pregunta
{
	private int							idPregunta;
	private String						descripcion;
	//Orden dentro de su sección
	private int							orden;
	private Seccion						seccion;
	private TipoPregunta				tipoPregunta;
	private Optional<List<Opcion>>		opciones;

	//Atributo para el registro específico que se está contestando o de un solo registro del formulario
	private Optional<Respuesta>			respuesta;

	//Atributo para el concentrado de respuestas de cada pregunta, cada respuesta indica su registro específico
	private Optional<List<Respuesta>>	respuestasResultados;

	public Pregunta()
	{
		// TODO Auto-generated constructor stub
	}

	public Pregunta(int idPregunta, String descripcion, int orden, Seccion seccion, TipoPregunta tipoPregunta)
	{
		super();
		this.idPregunta = idPregunta;
		this.descripcion = descripcion;
		this.orden = orden;
		this.seccion = seccion;
		this.tipoPregunta = tipoPregunta;

		this.respuesta = Optional.of(new Respuesta(-1, this, new Opcion(-1, "", 1, this)));
	}

	public Pregunta(int idPregunta, String descripcion, int orden, Seccion seccion, TipoPregunta tipoPregunta,
			Respuesta respuesta)
	{
		super();
		this.idPregunta = idPregunta;
		this.descripcion = descripcion;
		this.orden = orden;
		this.seccion = seccion;
		this.tipoPregunta = tipoPregunta;
		this.respuesta = Optional.of(respuesta);
	}

	public Pregunta(int idPregunta, String descripcion, int orden, Seccion seccion, TipoPregunta tipoPregunta,
			List<Opcion> opciones, Respuesta respuesta)
	{
		super();
		this.idPregunta = idPregunta;
		this.descripcion = descripcion;
		this.orden = orden;
		this.seccion = seccion;
		this.tipoPregunta = tipoPregunta;
		this.opciones = Optional.of(opciones);
		this.respuesta = Optional.of(respuesta);
	}

	public void initRespuestasResultados()
	{
		if (this.respuestasResultados == null)
		{
			List<Respuesta> respuestas = new ArrayList<>();
			this.respuestasResultados = Optional.of(respuestas);

		}
	}

	public void getOpcionesFromBD()
	{

		//Solo obtiene las opciones de la bd si es de tipo opción múltiple
		if (this.tipoPregunta.getIdTipoPregunta() != 3)
		{
			return;
		}

		PreparedStatement prep = null;
		ResultSet rBD = null;

		List<Opcion> opciones = new ArrayList<>();
		this.opciones = Optional.of(opciones);

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionClimaLaboral();)
		{
			prep = conexion.prepareStatement(" SELECT * FROM opcion WHERE idPregunta=? ");
			prep.setInt(1, this.idPregunta);

			rBD = prep.executeQuery();

			if (rBD.next())
			{
				do
				{
					opciones.add(new Opcion(rBD.getInt("idOpcion"), rBD.getString("Descripcion"), rBD.getInt("Orden"),
							this));

				} while (rBD.next());

			}

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Excepción",
							"Ha ocurrido una excepción al obtener las opciones de la pregunta " + this.idPregunta
									+ ", favor de contactar con el desarrollador del sistema."));

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

	public int getIdPregunta()
	{
		return idPregunta;
	}

	public void setIdPregunta(int idPregunta)
	{
		this.idPregunta = idPregunta;
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

	public Seccion getSeccion()
	{
		return seccion;
	}

	public void setSeccion(Seccion seccion)
	{
		this.seccion = seccion;
	}

	public TipoPregunta getTipoPregunta()
	{
		return tipoPregunta;
	}

	public void setTipoPregunta(TipoPregunta tipoPregunta)
	{
		this.tipoPregunta = tipoPregunta;
	}

	public List<Opcion> getOpciones()
	{
		return opciones.get();
	}

	public void setOpciones(List<Opcion> opciones)
	{
		this.opciones = Optional.of(opciones);
	}

	public Respuesta getRespuesta()
	{
		return respuesta.get();
	}

	public void setRespuesta(Respuesta respuesta)
	{
		this.respuesta = Optional.of(respuesta);
	}

	public List<Respuesta> getRespuestasResultados()
	{
		return respuestasResultados.get();
	}

	public void setRespuestasResultados(List<Respuesta> respuestasResultados)
	{
		this.respuestasResultados = Optional.ofNullable(respuestasResultados);
	}

}
