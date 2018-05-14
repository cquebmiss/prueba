package modelo.climalaboral;

import java.util.Optional;

public class Respuesta
{
	private Registro registro;
	private int idRespuesta;
	private Pregunta pregunta;
	private Optional<Opcion> opcion;
	private Optional<String> respuestaAbierta;

	public Respuesta()
	{
		// TODO Auto-generated constructor stub
	}

	public Respuesta(int idRespuesta, Pregunta pregunta, Opcion opcion)
	{
		super();
		this.idRespuesta = idRespuesta;
		this.pregunta = pregunta;
		this.opcion = Optional.ofNullable(opcion);
		this.respuestaAbierta = Optional.of("");
	}

	public Respuesta(int idRespuesta, Pregunta pregunta, String respuestaAbierta)
	{
		super();
		this.idRespuesta = idRespuesta;
		this.pregunta = pregunta;
		this.respuestaAbierta = Optional.of(respuestaAbierta);
	}

	public int getIdRespuesta()
	{
		return idRespuesta;
	}

	public void setIdRespuesta(int idRespuesta)
	{
		this.idRespuesta = idRespuesta;
	}

	public Pregunta getPregunta()
	{
		return pregunta;
	}

	public void setPregunta(Pregunta pregunta)
	{
		this.pregunta = pregunta;
	}

	public Opcion getOpcion()
	{
		return opcion.get();
	}

	public void setOpcion(Opcion opcion)
	{
		this.opcion = Optional.of(opcion);
	}

	public String getRespuestaAbierta()
	{
		return respuestaAbierta.get();
	}

	public void setRespuestaAbierta(String respuestaAbierta)
	{
		this.respuestaAbierta = Optional.of(respuestaAbierta);
	}

	public Registro getRegistro()
	{
		return registro;
	}

	public void setRegistro(Registro registro)
	{
		this.registro = registro;
	}

}
