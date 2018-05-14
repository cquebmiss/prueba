package modelo;

import java.io.Serializable;

public class Movimiento implements Serializable
{
	public int		idMovimiento;
	public String	descripcion;
	private int		año;
	private int		quincena;
	private Plaza	plaza;
	private String	nombreMovimiento;

	Plantilla		plantilla;

	public Movimiento()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public Movimiento(int idMovimiento, String descripcion, int año, int quincena, Plaza plaza, String nombreMovimiento,
			Plantilla plantilla)
	{
		super();
		this.idMovimiento = idMovimiento;
		this.descripcion = descripcion;
		this.año = año;
		this.quincena = quincena;
		this.plaza = plaza;
		this.nombreMovimiento = nombreMovimiento;
		this.plantilla = plantilla;
	}

	public int getIdMovimiento()
	{
		return idMovimiento;
	}

	public void setIdMovimiento(int idMovimiento)
	{
		this.idMovimiento = idMovimiento;
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

	public Plaza getPlaza()
	{
		return plaza;
	}

	public void setPlaza(Plaza plaza)
	{
		this.plaza = plaza;
	}

	public String getNombreMovimiento()
	{
		return nombreMovimiento;
	}

	public void setNombreMovimiento(String nombreMovimiento)
	{
		this.nombreMovimiento = nombreMovimiento;
	}

	public Plantilla getPlantilla()
	{
		return plantilla;
	}

	public void setPlantilla(Plantilla plantilla)
	{
		this.plantilla = plantilla;
	}

}
