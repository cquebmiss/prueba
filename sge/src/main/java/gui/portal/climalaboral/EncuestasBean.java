package gui.portal.climalaboral;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import modelo.climalaboral.Area;
import modelo.climalaboral.Encuesta;
import modelo.climalaboral.Jornada;
import modelo.climalaboral.Pregunta;
import modelo.climalaboral.Profesion;
import util.FacesUtils;
import util.climalaboral.UtilidadesClimaLaboral;

@ManagedBean
@SessionScoped
public class EncuestasBean implements Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private Encuesta			encuesta;
	// Estatus del proceso de la encuesta
	// -1 inicio o bienvenida de la encuesta
	// 0 encuesta en curso
	// 1 encuesta finalizada
	private int					estadoModulo;

	private List<Pregunta>		preguntasAlAzar;
	private int					indicadorPregunta;
	private Pregunta			preguntaEnPantalla;

	//Catálogos para seleccionar en la encuesta

	public EncuestasBean()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	@PostConstruct
	public void postConstruct()
	{
		actionReiniciarEncuesta();

	}

	public void actionReiniciarEncuesta()
	{
		PersistEncuestaBean bean = (PersistEncuestaBean) FacesUtils.getManagedBean("persistEncuestaBean");
		bean.iniciarCatalogos();
		this.estadoModulo = -1;
	}

	public void actionPasoPrevioInicioEncuesta()
	{
		this.estadoModulo = 0;
	}

	public void actionIniciarEncuesta()
	{
		this.estadoModulo = 1;
		this.indicadorPregunta = -1;

		this.encuesta = UtilidadesClimaLaboral.getEncuestaFromBD();
		this.encuesta.getSeccionesFromBD();

		this.preguntasAlAzar = new ArrayList<>();

		this.encuesta.getSecciones().stream().forEach(seccion ->
		{
			seccion.getPreguntasFromBD();

			seccion.getPreguntas().stream().forEach(pregunta ->
			{
				pregunta.getOpcionesFromBD();
			});

			this.preguntasAlAzar.addAll(seccion.getPreguntas());

		});

		// Se revuelven al azar las preguntas
		Collections.shuffle(this.preguntasAlAzar);

		actionSiguientePregunta();

	}

	public void actionSiguientePregunta()
	{

		if (this.indicadorPregunta == (this.preguntasAlAzar.size() - 1))
		{
			System.out.println("Encuesta finalizada");
			PersistEncuestaBean bean = (PersistEncuestaBean) FacesUtils.getManagedBean("persistEncuestaBean");
			bean.actionRegistrarEncuesta(this.encuesta);
			this.estadoModulo = 2;
		}
		else
		{
			this.preguntaEnPantalla = this.preguntasAlAzar.get(++this.indicadorPregunta);

		}

	}

	public Encuesta getEncuesta()
	{
		return encuesta;
	}

	public void setEncuesta(Encuesta encuesta)
	{
		this.encuesta = encuesta;
	}

	public int getEstadoModulo()
	{
		return estadoModulo;
	}

	public void setEstadoModulo(int estadoModulo)
	{
		this.estadoModulo = estadoModulo;
	}

	public List<Pregunta> getPreguntasAlAzar()
	{
		return preguntasAlAzar;
	}

	public void setPreguntasAlAzar(List<Pregunta> preguntasAlAzar)
	{
		this.preguntasAlAzar = preguntasAlAzar;
	}

	public int getIndicadorPregunta()
	{
		return indicadorPregunta;
	}

	public void setIndicadorPregunta(int indicadorPregunta)
	{
		this.indicadorPregunta = indicadorPregunta;
	}

	public Pregunta getPreguntaEnPantalla()
	{
		return preguntaEnPantalla;
	}

	public void setPreguntaEnPantalla(Pregunta preguntaEnPantalla)
	{
		this.preguntaEnPantalla = preguntaEnPantalla;
	}

}
