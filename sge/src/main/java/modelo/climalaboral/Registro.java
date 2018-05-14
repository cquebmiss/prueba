package modelo.climalaboral;

import java.sql.Time;
import java.util.Date;

public class Registro
{
	private int			idRegistro;
	private Encuesta	encuesta;
	private Date		fecha;
	private Time		hora;
	private int			folio;
	private String		observaciones;
	private Area		area;
	private Profesion	profesion;
	private Jornada		jornada;

	public Registro()
	{
		// TODO Auto-generated constructor stub
	}

	public Registro(int idRegistro, Encuesta encuesta, Date fecha, Time hora, int folio, String observaciones)
	{
		super();
		this.idRegistro = idRegistro;
		this.encuesta = encuesta;
		this.fecha = fecha;
		this.hora = hora;
		this.folio = folio;
		this.observaciones = observaciones;
	}

	public int getIdRegistro()
	{
		return idRegistro;
	}

	public void setIdRegistro(int idRegistro)
	{
		this.idRegistro = idRegistro;
	}

	public Encuesta getEncuesta()
	{
		return encuesta;
	}

	public void setEncuesta(Encuesta encuesta)
	{
		this.encuesta = encuesta;
	}

	public Date getFecha()
	{
		return fecha;
	}

	public void setFecha(Date fecha)
	{
		this.fecha = fecha;
	}

	public Time getHora()
	{
		return hora;
	}

	public void setHora(Time hora)
	{
		this.hora = hora;
	}

	public int getFolio()
	{
		return folio;
	}

	public void setFolio(int folio)
	{
		this.folio = folio;
	}

	public String getObservaciones()
	{
		return observaciones;
	}

	public void setObservaciones(String observaciones)
	{
		this.observaciones = observaciones;
	}

	public Area getArea()
	{
		return area;
	}

	public void setArea(Area area)
	{
		this.area = area;
	}

	public Profesion getProfesion()
	{
		return profesion;
	}

	public void setProfesion(Profesion profesion)
	{
		this.profesion = profesion;
	}

	public Jornada getJornada()
	{
		return jornada;
	}

	public void setJornada(Jornada jornada)
	{
		this.jornada = jornada;
	}

}
