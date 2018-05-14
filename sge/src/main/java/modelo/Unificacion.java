package modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Unificacion implements Cloneable
{
	private int						idUnificacion;
	private String					nombre;
	private String					observaciones;

	private List<Empleado>			empleados;
	private Map<String, Empleado>	empleadosMap;

	public Unificacion()
	{
		super();
		this.idUnificacion = -1;
		this.nombre = "";
		this.observaciones = "";
		this.empleados = new ArrayList<>();
	}

	public Unificacion(int idUnificacion, String nombre, String observaciones, List<Empleado> empleados)
	{
		super();
		this.idUnificacion = idUnificacion;
		this.nombre = nombre;
		this.observaciones = observaciones;
		this.empleados = empleados;
	}

	public Unificacion getClone()
	{
		Unificacion clon = new Unificacion();
		clon.setIdUnificacion(this.idUnificacion);
		clon.setNombre(this.nombre);
		clon.setObservaciones(this.observaciones);
		clon.setEmpleados(this.empleados.subList(0, this.empleados.size()));
		clon.setEmpleadosMap(this.empleadosMap);

		return clon;

	}

	public void addEmpleado(Empleado emp)
	{
		this.empleados.add(emp);
	}

	public void removeEmpleado(Empleado emp)
	{
		this.empleados.remove(emp);
	}

	public int getIdUnificacion()
	{
		return idUnificacion;
	}

	public void setIdUnificacion(int idUnificacion)
	{
		this.idUnificacion = idUnificacion;
	}

	public String getNombre()
	{
		return nombre;
	}

	public void setNombre(String nombre)
	{
		this.nombre = nombre;
	}

	public String getObservaciones()
	{
		return observaciones;
	}

	public void setObservaciones(String observaciones)
	{
		this.observaciones = observaciones;
	}

	public List<Empleado> getEmpleados()
	{
		return empleados;
	}

	public void setEmpleados(List<Empleado> empleados)
	{
		this.empleados = empleados;
	}

	public Map<String, Empleado> getEmpleadosMap()
	{
		return empleadosMap;
	}

	public void setEmpleadosMap(Map<String, Empleado> empleadosMap)
	{
		this.empleadosMap = empleadosMap;
	}

}
