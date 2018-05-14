package modelo;

import java.io.Serializable;
import java.util.List;

import util.utilidades;

public class ArchivoPuesto implements Serializable
{
	private int						idArchivoPuesto;
	private String					descripcion;
	private Plaza					plaza;
	private int						año;
	private int						quincena;
	private String					observaciones;

	private Plantilla				plantilla;

	private List<PlantillaRegistro>	registros;

	// Auxiliares para visualización en tablas
	private List<PlantillaRegistro>	registrosFilter;
	private PlantillaRegistro		registroSelec;

	public ArchivoPuesto()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public ArchivoPuesto(int idArchivoPuesto, String descripcion, Plaza plaza, int año, int quincena,
			String observaciones, Plantilla plantilla)
	{
		super();
		this.idArchivoPuesto = idArchivoPuesto;
		this.descripcion = descripcion;
		this.plaza = plaza;
		this.año = año;
		this.quincena = quincena;
		this.observaciones = observaciones;
		this.plantilla = plantilla;
	}

	public void updatePlantillaRegistros()
	{
		this.plantilla = utilidades.getPlantillaLayout(this.plantilla.getIdPlantilla());
		this.plantilla.updateCampos();
	}

	public void updateRegistros()
	{
		this.registros = utilidades.getRegistrosArchivoPuesto(this);

	}

	public PlantillaRegistro getRegistro(String clave)
	{
		for (PlantillaRegistro reg : this.registros)
		{
			if (reg.getValorPorDescripcionContains("Clave").trim().equalsIgnoreCase(clave))
			{
				return reg;
			}

		}

		return null;

	}

	public int getIdArchivoPuesto()
	{
		return idArchivoPuesto;
	}

	public void setIdArchivoPuesto(int idArchivoPuesto)
	{
		this.idArchivoPuesto = idArchivoPuesto;
	}

	public String getDescripcion()
	{
		return descripcion;
	}

	public void setDescripcion(String descripcion)
	{
		this.descripcion = descripcion;
	}

	public int getAño()
	{
		return año;
	}

	public void setAño(int año)
	{
		this.año = año;
	}

	public int getQuincena()
	{
		return quincena;
	}

	public void setQuincena(int quincena)
	{
		this.quincena = quincena;
	}

	public String getObservaciones()
	{
		return observaciones;
	}

	public void setObservaciones(String observaciones)
	{
		this.observaciones = observaciones;
	}

	public Plantilla getPlantilla()
	{
		return plantilla;
	}

	public void setPlantilla(Plantilla plantilla)
	{
		this.plantilla = plantilla;
	}

	public List<PlantillaRegistro> getRegistros()
	{
		return registros;
	}

	public void setRegistros(List<PlantillaRegistro> registros)
	{
		this.registros = registros;
	}

	public List<PlantillaRegistro> getRegistrosFilter()
	{
		return registrosFilter;
	}

	public void setRegistrosFilter(List<PlantillaRegistro> registrosFilter)
	{
		this.registrosFilter = registrosFilter;
	}

	public PlantillaRegistro getRegistroSelec()
	{
		return registroSelec;
	}

	public void setRegistroSelec(PlantillaRegistro registroSelec)
	{
		this.registroSelec = registroSelec;
	}

	public Plaza getPlaza()
	{
		return plaza;
	}

	public void setPlaza(Plaza plaza)
	{
		this.plaza = plaza;
	}

}
