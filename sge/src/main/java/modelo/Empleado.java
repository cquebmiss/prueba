package modelo;

public class Empleado
{

	// Estructura provisional para crear al empleado en lo que se continua con
	// el desarrollo
	private String	numEmpleado;

	private String	nombre;
	private Plaza	plaza;
	private String	observaciones;
	
	private Unificacion unificacion;

	public Empleado()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public Empleado(String numEmpleado, String nombre, Plaza plaza, String observaciones)
	{
		super();
		this.numEmpleado = numEmpleado;
		this.nombre = nombre;
		this.plaza = plaza;
		this.observaciones = observaciones;
	}

	public String getNumEmpleado()
	{
		return numEmpleado;
	}

	public void setNumEmpleado(String numEmpleado)
	{
		this.numEmpleado = numEmpleado;
	}

	public Plaza getPlaza()
	{
		return plaza;
	}

	public void setPlaza(Plaza plaza)
	{
		this.plaza = plaza;
	}

	public String getObservaciones()
	{
		return observaciones;
	}

	public void setObservaciones(String observaciones)
	{
		this.observaciones = observaciones;
	}

	public String getNombre()
	{
		return nombre;
	}

	public void setNombre(String nombre)
	{
		this.nombre = nombre;
	}

	public Unificacion getUnificacion()
	{
		return unificacion;
	}

	public void setUnificacion(Unificacion unificacion)
	{
		this.unificacion = unificacion;
	}

}
