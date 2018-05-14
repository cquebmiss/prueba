package gui.portal.nominas;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import modelo.Empleado;
import modelo.PlantillaRegistro;
import modelo.Plaza;
import modelo.Producto;
import modelo.Unificacion;
import resources.DataBase;
import util.FacesUtils;
import util.utilidades;

@ManagedBean
@SessionScoped
public class UnificacionBean implements Serializable
{
	private List<Unificacion>	unificaciones;
	private Unificacion			unificacionSelec;

	private int					statusInterfaz;

	private List<Producto>		productos;
	private List<Producto>		productosFiltro;
	private Producto			productoSelec;

	private Unificacion			nuevoEdicionUnificacion;

	// Sección de sugerencias de unificación
	private List<Unificacion>	sugerenciasUnificar;
	private List<Unificacion>	sugerenciasUnificarFilter;

	private Unificacion			sugerenciaUnificarSelec;

	private int					año;
	private int					qna;
	private int					añoFinal;
	private int					qnaFinal;

	public UnificacionBean()
	{
		super();
		setModoInterfaz(-1);
		LocalDateTime localDate = LocalDateTime.now();
		this.año = localDate.getYear();
		this.añoFinal = localDate.getYear();

		this.qna = localDate.getMonthValue() * 2;

		if (this.qna == 24)
		{
			this.qna = 23;
		}

		this.qnaFinal = this.qna;
		// TODO Auto-generated constructor stub
	}

	public void setModoInterfaz(int modo)
	{
		setStatusInterfaz(modo);

		switch (modo)
		{
			case -1:
				this.unificaciones = utilidades.getUnificaciones(true);
				this.unificacionSelec = null;

				break;

			case 0:

				updateProductos();
				this.nuevoEdicionUnificacion = new Unificacion();

				break;

			case 1:

				updateProductos();

				break;
		}

	}

	public void updateProductos()
	{
		this.productos = utilidades.getProductos();
		this.productosFiltro = null;
		this.productoSelec = null;
	}

	public void updateInfoRegistrosProductosSelec()
	{

		this.productoSelec.updateRegistrosTRAConConceptos(true, true, true, true, false, true);

	}

	public void añadirEmpleadoAUnificacion(PlantillaRegistro regDAT)
	{
		String numEmpleado = regDAT.getValorPorDescripcionContains("mero de Empleado");
		String nombreEmpleado = regDAT.getValorPorDescripcionContains("nombre");
		Plaza plaza = this.productoSelec.getPlaza();

		// Se verifica que en la unificación que se llevará a cabo no esté aún
		// presente el registro del empleado de la misma plaza
		for (Empleado emp : this.nuevoEdicionUnificacion.getEmpleados())
		{
			if (emp.getPlaza().getIdPlaza() == plaza.getIdPlaza()
					&& numEmpleado.trim().equalsIgnoreCase(emp.getNumEmpleado().trim()))
			{
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_WARN, "Empleado Ya Añadido",
								"El empleado ya ha sido cargado en la lista para crear la unificación."));
				return;
			}

		}

		Empleado nuevoEmpleado = new Empleado(numEmpleado, nombreEmpleado, plaza, "");
		this.nuevoEdicionUnificacion.addEmpleado(nuevoEmpleado);

	}

	public void actionGuardarUnificacion()
	{
		// Primero se registran en la tabla de empleados los registros que se
		// van a unificar

		PreparedStatement prep = null;
		ResultSet rBD = null;
		String nombreUnificacion = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionNominas();)
		{

			for (Empleado emp : this.nuevoEdicionUnificacion.getEmpleados())
			{
				nombreUnificacion = emp.getNombre();

				try
				{
					prep = conexion.prepareStatement(
							" INSERT INTO empleado (NumEmpleado, idPlaza, Nombre, Observaciones) VALUES (?, ?, ?, ?) ");

					prep.setString(1, emp.getNumEmpleado());
					prep.setInt(2, emp.getPlaza().getIdPlaza());
					prep.setString(3, emp.getNombre());
					prep.setString(4, "");

					prep.executeUpdate();

					prep.close();

				} catch (Exception e)
				{
					// Captura cuando los empleados ya estén dados de alta lo
					// cual no afecta el proceso
					e.printStackTrace();
				}

			}

			int idUnificacion = 0;
			boolean insertCorrecto = false;

			prep = conexion.prepareStatement("SELECT MAX(idUnificacion) AS id FROM unificacion");

			rBD = prep.executeQuery();

			if (rBD.next())
			{
				idUnificacion = rBD.getInt("id") + 1;
			}

			do
			{

				try
				{
					prep = conexion.prepareStatement(
							" INSERT INTO unificacion (idUnificacion, Nombre, Observaciones) VALUES (?, ?, ?) ");

					prep.setInt(1, idUnificacion);
					prep.setString(2, nombreUnificacion);
					prep.setString(3, "");

					prep.executeUpdate();
					prep.close();

					this.nuevoEdicionUnificacion.setIdUnificacion(idUnificacion);

					insertCorrecto = true;

				} catch (Exception e)
				{
					idUnificacion++;
				}

			}
			while (!insertCorrecto);

			// Finalmente se inserta uno a uno los registros de la unificación

			for (Empleado emp : this.nuevoEdicionUnificacion.getEmpleados())
			{

				try
				{
					prep = conexion.prepareStatement(
							" INSERT INTO empleadounificado (idUnificacion, NumEmpleado, idPlaza) VALUES (?, ?, ?) ");

					prep.setInt(1, this.nuevoEdicionUnificacion.getIdUnificacion());
					prep.setString(2, emp.getNumEmpleado());
					prep.setInt(3, emp.getPlaza().getIdPlaza());

					prep.executeUpdate();

					prep.close();

				} catch (Exception e)
				{
					// Captura cuando los empleados ya estén dados de alta lo
					// cual no afecta el proceso
					e.printStackTrace();
				}

			}

			setModoInterfaz(-1);

			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Unificación Registrada", "La unificación ha sido registrada exitosamente."));

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{

			if (prep != null)
			{
				try
				{
					prep.close();
				} catch (SQLException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}

	public void actionSugerirUnificaciones()
	{
		this.sugerenciasUnificar = new ArrayList<>();
		this.sugerenciasUnificarFilter = null;
		this.sugerenciaUnificarSelec = null;
		List<Plaza> plazas = utilidades.getPlazas();

		// RFC
		Map<String, Map<Plaza, PlantillaRegistro>> rfcCoincidentes = new HashMap<>();
		Map<String, Map<Plaza, PlantillaRegistro>> nombresCoincidentes = new HashMap<>();

		List<Producto> prds;
		String RFC;
		String nombres;
		Map<Plaza, PlantillaRegistro> rfcUnificar;
		Map<Plaza, PlantillaRegistro> nombresUnificar;

		for (Plaza plaza : plazas)
		{
			prds = utilidades.getProductosPeriodo(this.año, this.qna, this.añoFinal, this.qnaFinal, true, true,
					plaza.getIdPlaza());

			for (Producto prd : prds)
			{

				if (prd.getTipoNomina().getDescripcion().contains("Pensi"))
				{
					continue;
				}

				prd.updateRegistrosDAT();

				for (PlantillaRegistro regDAT : prd.getRegistrosDAT())
				{
					RFC = regDAT.getValorPorDescripcionContains("RFC");
					nombres = regDAT.getValorPorDescripcionContains("nombre").trim();

					if (RFC != null && RFC.length() > 9)
					{
						RFC = RFC.substring(0, 9);
					}

					rfcUnificar = rfcCoincidentes.get(RFC);
					nombresUnificar = nombresCoincidentes.get(nombres);

					if (rfcUnificar != null)
					{

						rfcUnificar.put(plaza, regDAT);

					}
					else
					{
						rfcUnificar = new HashMap<>();

						rfcUnificar.put(plaza, regDAT);

						rfcCoincidentes.put(RFC, rfcUnificar);

					}

					if (nombresUnificar != null)
					{

						nombresUnificar.put(plaza, regDAT);

					}
					else
					{
						nombresUnificar = new HashMap<>();

						nombresUnificar.put(plaza, regDAT);

						nombresCoincidentes.put(nombres, nombresUnificar);

					}

				}
				
				prd.setRegistrosTRA(new ArrayList<>());
				prd.setRegistrosDAT(new ArrayList<>());
				prd.setConceptos(new ArrayList<>());
				prd.setUnidadResponsable(new ArrayList<>());

			}

		}

		Set set = rfcCoincidentes.entrySet();
		Iterator iterator = set.iterator();

		int idConsecutivo = -1;
		PreparedStatement prep = null;
		ResultSet rBD = null;

		int idUnificacionGrupo = 0;
		boolean grupoValido = false;
		boolean grupoConAlgunoNoUnificado = false;

		while (iterator.hasNext())
		{
			Map.Entry me = (Map.Entry) iterator.next();
			Map<Plaza, PlantillaRegistro> rfcUnificarR = (Map<Plaza, PlantillaRegistro>) me.getValue();

			if (rfcUnificarR.size() > 1)
			{
				System.out.println("Sugerencias para " + me.getKey());

				Set setRegs = rfcUnificarR.entrySet();
				Iterator iteratorRegs = setRegs.iterator();

				Unificacion unifemp = new Unificacion(idConsecutivo, "", "", new ArrayList<>());

				idConsecutivo--;

				while (iteratorRegs.hasNext())
				{
					Map.Entry meRegs = (Map.Entry) iteratorRegs.next();
					PlantillaRegistro reg = (PlantillaRegistro) meRegs.getValue();
					Plaza pl = (Plaza) meRegs.getKey();

					System.out.println("			" + "(" + pl.getDescripcionPlaza() + ")"
							+ reg.getValorPorDescripcionContains("RFC") + " - "
							+ reg.getValorPorDescripcionContains("nombre") + "-"
							+ reg.getValorPorDescripcionContains("mero de cheque"));

					unifemp.setNombre(reg.getValorPorDescripcionContains("nombre"));

					Empleado nEmp = new Empleado(reg.getValorPorDescripcionContains("mero de empleado"),
							reg.getValorPorDescripcionContains("nombre"), pl, "");

					Unificacion unifEmpIndividual = unifemp.getClone();

					nEmp.setUnificacion(unifEmpIndividual);

					// Se verifica el id de unificación en caso de que el
					// empleado ya esté unificado dentro de alguno registrado en
					// el sistema
					try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database"))
							.getConnectionNominas();)
					{
						prep = conexion.prepareStatement(
								" SELECT * FROM empleadounificado WHERE NumEmpleado=? AND idPlaza=? ");
						prep.setString(1, reg.getValorPorDescripcionContains("mero de empleado"));
						prep.setInt(2, pl.getIdPlaza());

						rBD = prep.executeQuery();

						if (rBD.next())
						{
							unifEmpIndividual.setIdUnificacion(rBD.getInt("idUnificacion"));
						}

					} catch (Exception e)
					{
						e.printStackTrace();
					}
					finally
					{

						if (prep != null)
						{
							try
							{
								prep.close();
							} catch (SQLException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

					}

					unifemp.getEmpleados().add(nEmp);

				}

				// Se verifica si todos los índices de unificación son los
				// mismos quiere decir que no hay nada que unificar
				idUnificacionGrupo = unifemp.getEmpleados().get(0).getUnificacion().getIdUnificacion();
				grupoValido = false;

				if (idUnificacionGrupo < 0)
				{
					grupoConAlgunoNoUnificado = true;

				}
				else

				{
					grupoConAlgunoNoUnificado = false;

				}

				for (int x = 1; x < unifemp.getEmpleados().size(); x++)
				{
					if (unifemp.getEmpleados().get(x).getUnificacion().getIdUnificacion() != idUnificacionGrupo)
					{
						grupoValido = true;
					}

					if (unifemp.getEmpleados().get(x).getUnificacion().getIdUnificacion() < 0)
					{
						grupoConAlgunoNoUnificado = true;
					}
				}

				if (!grupoValido && !grupoConAlgunoNoUnificado)
				{
					continue;
				}

				this.sugerenciasUnificar.add(unifemp);

			}

		}

		System.out.println("\n\nCoincidencias por nombres \n\n");

		set = nombresCoincidentes.entrySet();
		iterator = set.iterator();

		while (iterator.hasNext())
		{
			Map.Entry me = (Map.Entry) iterator.next();
			Map<Plaza, PlantillaRegistro> nombresUnificarR = (Map<Plaza, PlantillaRegistro>) me.getValue();

			if (nombresUnificarR.size() > 1)
			{
				System.out.println("Sugerencias para " + me.getKey());

				Set setRegs = nombresUnificarR.entrySet();
				Iterator iteratorRegs = setRegs.iterator();

				while (iteratorRegs.hasNext())
				{
					Map.Entry meRegs = (Map.Entry) iteratorRegs.next();
					PlantillaRegistro reg = (PlantillaRegistro) meRegs.getValue();
					Plaza pl = (Plaza) meRegs.getKey();

					System.out.println("			" + "(" + pl.getDescripcionPlaza() + ")"
							+ reg.getValorPorDescripcionContains("RFC") + " - "
							+ reg.getValorPorDescripcionContains("nombre") + "-"
							+ reg.getValorPorDescripcionContains("mero de cheque"));

				}

			}

		}

	}

	public void actionGuardarUnificacionSugerida(List<Empleado> empUnificar)
	{
		PreparedStatement prep = null;
		ResultSet rBD = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionNominas();)
		{

			int idUnifGeneral = empUnificar.get(0).getUnificacion().getIdUnificacion();
			String nombreUnificacion = empUnificar.get(0).getNombre();

			// Primero se verifica que la unificación sea posible
			for (Empleado emp : empUnificar)
			{
				// Si tienen id de unificación menor que 0 es porque es una
				// unificación nueva
				if (emp.getUnificacion().getIdUnificacion() < 0)
				{
					continue;
				}

				if (idUnifGeneral != emp.getUnificacion().getIdUnificacion() && idUnifGeneral > -1)
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
							"Imposible Unificar",
							"Los empleados no cuentan con el mismo ID de unificación, retire los que no pertenezcan al grupo."));
					return;
				}

				idUnifGeneral = emp.getUnificacion().getIdUnificacion();

			}

			// Si el idUnifGeneral es menor que 0, quiere decir que la
			// unificación no ha sido aún creada
			int idUnificacion = -1;
			boolean insertCorrecto = false;

			if (idUnifGeneral < 0)
			{
				prep = conexion.prepareStatement("SELECT MAX(idUnificacion) AS id FROM unificacion");

				rBD = prep.executeQuery();

				if (rBD.next())
				{
					idUnificacion = rBD.getInt("id") + 1;
				}

				do
				{

					try
					{
						prep = conexion.prepareStatement(
								" INSERT INTO unificacion (idUnificacion, Nombre, Observaciones) VALUES (?, ?, ?) ");

						prep.setInt(1, idUnificacion);
						prep.setString(2, nombreUnificacion);
						prep.setString(3, "");

						prep.executeUpdate();
						prep.close();

						insertCorrecto = true;

					} catch (Exception e)
					{
						idUnificacion++;
					}

				}
				while (!insertCorrecto);
			}
			else
			{
				idUnificacion = idUnifGeneral;
			}

			// En esta instancia es porque no todos los empleados han sido
			// unificados
			for (Empleado emp : empUnificar)
			{
				if (emp.getUnificacion().getIdUnificacion() > -1)
				{
					continue;
				}

				// Primero verifica si el emp ya está registrado en empleados,
				// en caso contrario lo insarta

				prep = conexion.prepareStatement(" SELECT * FROM empleado WHERE idPlaza=? AND NumEmpleado=? ");
				prep.setInt(1, emp.getPlaza().getIdPlaza());
				prep.setString(2, emp.getNumEmpleado().trim());

				rBD = prep.executeQuery();

				if (!rBD.next())
				{

					prep.close();

					prep = conexion.prepareStatement(
							" INSERT INTO empleado (NumEmpleado, idPlaza, Nombre, Observaciones) VALUES (?, ?, ?, ?) ");

					prep.setString(1, emp.getNumEmpleado());
					prep.setInt(2, emp.getPlaza().getIdPlaza());
					prep.setString(3, emp.getNombre());
					prep.setString(4, "");

					prep.executeUpdate();

					prep.close();

				}

				// Se inserta dentro de la unificación
				prep = conexion.prepareStatement(
						" INSERT INTO empleadounificado (idUnificacion, NumEmpleado, idPlaza) VALUES (?, ?, ?) ");

				prep.setInt(1, idUnificacion);
				prep.setString(2, emp.getNumEmpleado());
				prep.setInt(3, emp.getPlaza().getIdPlaza());

				prep.executeUpdate();

				prep.close();

				emp.getUnificacion().setIdUnificacion(idUnificacion);

			}

			this.unificaciones = utilidades.getUnificaciones(true);
			this.unificacionSelec = null;

			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Empleados Unificados", "La unificación se ha registrado exitosamente."));

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (prep != null)
			{
				try
				{
					prep.close();
				} catch (SQLException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	public List<Unificacion> getUnificaciones()
	{
		return unificaciones;
	}

	public void setUnificaciones(List<Unificacion> unificaciones)
	{
		this.unificaciones = unificaciones;
	}

	public Unificacion getUnificacionSelec()
	{
		return unificacionSelec;
	}

	public void setUnificacionSelec(Unificacion unificacionSelec)
	{
		this.unificacionSelec = unificacionSelec;
	}

	public List<Producto> getProductos()
	{
		return productos;
	}

	public void setProductos(List<Producto> productos)
	{
		this.productos = productos;
	}

	public List<Producto> getProductosFiltro()
	{
		return productosFiltro;
	}

	public void setProductosFiltro(List<Producto> productosFiltro)
	{
		this.productosFiltro = productosFiltro;
	}

	public Producto getProductoSelec()
	{
		return productoSelec;
	}

	public void setProductoSelec(Producto productoSelec)
	{
		this.productoSelec = productoSelec;
	}

	public int getStatusInterfaz()
	{
		return statusInterfaz;
	}

	public void setStatusInterfaz(int statusInterfaz)
	{
		this.statusInterfaz = statusInterfaz;
	}

	public Unificacion getNuevoEdicionUnificacion()
	{
		return nuevoEdicionUnificacion;
	}

	public void setNuevoEdicionUnificacion(Unificacion nuevoEdicionUnificacion)
	{
		this.nuevoEdicionUnificacion = nuevoEdicionUnificacion;
	}

	public List<Unificacion> getSugerenciasUnificar()
	{
		return sugerenciasUnificar;
	}

	public void setSugerenciasUnificar(List<Unificacion> sugerenciasUnificar)
	{
		this.sugerenciasUnificar = sugerenciasUnificar;
	}

	public int getAño()
	{
		return año;
	}

	public void setAño(int año)
	{
		this.año = año;
	}

	public int getQna()
	{
		return qna;
	}

	public void setQna(int qna)
	{
		this.qna = qna;
	}

	public int getAñoFinal()
	{
		return añoFinal;
	}

	public void setAñoFinal(int añoFinal)
	{
		this.añoFinal = añoFinal;
	}

	public int getQnaFinal()
	{
		return qnaFinal;
	}

	public void setQnaFinal(int qnaFinal)
	{
		this.qnaFinal = qnaFinal;
	}

	public List<Unificacion> getSugerenciasUnificarFilter()
	{
		return sugerenciasUnificarFilter;
	}

	public void setSugerenciasUnificarFilter(List<Unificacion> sugerenciasUnificarFilter)
	{
		this.sugerenciasUnificarFilter = sugerenciasUnificarFilter;
	}

	public Unificacion getSugerenciaUnificarSelec()
	{
		return sugerenciaUnificarSelec;
	}

	public void setSugerenciaUnificarSelec(Unificacion sugerenciaUnificarSelec)
	{
		this.sugerenciaUnificarSelec = sugerenciaUnificarSelec;
	}

}
