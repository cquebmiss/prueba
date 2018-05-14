package modelo.minutas;

import java.text.SimpleDateFormat;
import java.util.Date;

import modelo.actividades.Actividad;

public class Compromiso
{
	private int idCompromiso;
	private Minuta minuta;
	private Actividad actividad;
	private String descripcion;

	//indica únicamente el orden del compromiso dentro de la minuta
	private int orden;
	private Participante responsable;
	private Date fechaFinalizacionEstimada;
	private String fechaFinalizacionEstimadaString;
	private String resolucion;

	public Compromiso()
	{
		super();
	}

	public Compromiso(int idCompromiso, Minuta minuta, Actividad actividad, String descripcion, int orden,
			Participante responsable, Date fechaFinalizacionEstimada, String resolucion)
	{
		super();
		this.idCompromiso = idCompromiso;
		this.minuta = minuta;
		this.actividad = actividad;
		this.descripcion = descripcion;
		this.orden = orden;
		this.responsable = responsable;
		this.fechaFinalizacionEstimada = fechaFinalizacionEstimada;
		this.resolucion = resolucion;

		updateFechaFinalizacionEstimadaString();
	}

	public void updateFechaFinalizacionEstimadaString()
	{
		if (this.fechaFinalizacionEstimada != null)
		{
			setFechaFinalizacionEstimadaString(
					new SimpleDateFormat("yyyy-MM-dd").format(this.fechaFinalizacionEstimada));
		}
		else
		{
			setFechaFinalizacionEstimadaString("No definida");
		}
	}

	public int getIdCompromiso()
	{
		return idCompromiso;
	}

	public void setIdCompromiso(int idCompromiso)
	{
		this.idCompromiso = idCompromiso;
	}

	public Minuta getMinuta()
	{
		return minuta;
	}

	public void setMinuta(Minuta minuta)
	{
		this.minuta = minuta;
	}

	public Actividad getActividad()
	{
		return actividad;
	}

	public void setActividad(Actividad actividad)
	{
		this.actividad = actividad;
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

	public Participante getResponsable()
	{
		return responsable;
	}

	public void setResponsable(Participante responsable)
	{
		this.responsable = responsable;
	}

	public Date getFechaFinalizacionEstimada()
	{
		return fechaFinalizacionEstimada;
	}

	public void setFechaFinalizacionEstimada(Date fechaFinalizacionEstimada)
	{
		this.fechaFinalizacionEstimada = fechaFinalizacionEstimada;
	}

	public String getResolucion()
	{
		return resolucion;
	}

	public void setResolucion(String resolucion)
	{
		this.resolucion = resolucion;
	}

	public String getFechaFinalizacionEstimadaString()
	{
		return fechaFinalizacionEstimadaString;
	}

	public void setFechaFinalizacionEstimadaString(String fechaFinalizacionEstimadaString)
	{
		this.fechaFinalizacionEstimadaString = fechaFinalizacionEstimadaString;
	}

}
