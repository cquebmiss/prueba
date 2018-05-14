package gui.portal.nominas;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import application.CatalogosBean;
import modelo.PlantillaRegistro;
import modelo.Plaza;
import modelo.Producto;
import modelo.Status;
import modelo.Trabajador;
import resources.DataBase;
import util.FacesUtils;
import util.utilidades;

@ManagedBean
@SessionScoped
public class MantEmpleadosBean implements Serializable
{

	/**
	 */
	private static final long	serialVersionUID	= 1L;
	private CatalogosBean		catalogosBean;

	private List<Plaza>			plazas;
	private int					idPlazaSeleccionada;

	private int					año;
	private int					qna;
	private int					añoFinal;
	private int					qnaFinal;

	private List<Trabajador>	trabajadoresConsultaPlaza;
	private List<Trabajador>	trabajadoresConsultaPlazaFiltrados;

	private List<Trabajador>	trabajadoresNuevos;
	private List<Trabajador>	trabajadoresNuevosFiltrados;

	public MantEmpleadosBean()
	{
		super();
		// TODO Auto-generated constructor stub
		this.catalogosBean = (CatalogosBean) FacesUtils.getManagedBean("catalogosBean");
	}

	@PostConstruct
	public void postConstruct()
	{

		LocalDateTime localDate = LocalDateTime.now();
		this.año = localDate.getYear();
		this.añoFinal = localDate.getYear();

		this.qna = localDate.getMonthValue() * 2;

		if (this.qna == 24)
		{
			this.qna = 23;
		}

		this.qnaFinal = this.qna;

		this.plazas = this.catalogosBean.getCatPlazas();

	}

	public void actionConsultaTrabajadoresPlaza()
	{

		this.trabajadoresConsultaPlaza = new ArrayList<>();
		this.trabajadoresConsultaPlazaFiltrados = null;

		PreparedStatement prep = null;
		ResultSet rBD = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{

			prep = conexion.prepareStatement(
					" SELECT t.*, pl.Descripcion AS descripcionPlaza, st.Descripcion AS descripcionStatus FROM trabajador t, plaza pl, status st  WHERE t.idPlaza=? AND t.idPlaza = pl.idPlaza AND t.idStatus = st.idStatus");

			prep.setInt(1, this.idPlazaSeleccionada);

			rBD = prep.executeQuery();

			if (rBD.next())
			{
				do
				{
					Trabajador regTrabajador = new Trabajador(rBD.getString("NumEmpleado"), rBD.getString("RFC"),
							rBD.getString("Nombre"), rBD.getString("CURP"),
							new Plaza(rBD.getInt("idPlaza"), rBD.getString("descripcionPlaza")),
							new Status(rBD.getInt("idStatus"), rBD.getString("descripcionStatus")));

					this.trabajadoresConsultaPlaza.add(regTrabajador);

				}
				while (rBD.next());

			}

		} catch (Exception e)
		{
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción", "Ha ocurrido una excepción al registrar los nuevos empleados"));
		}
		finally
		{
			if (prep != null)
			{
				try
				{
					prep.close();

				} catch (SQLException e)
				{ // TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	public void actionObtenerNuevosEmpleados()
	{

		this.trabajadoresNuevos = new ArrayList<>();
		List<Plaza> plazas = utilidades.getPlazas();

		List<Producto> prds;

		String numeroEmpleado = null;
		List<String> empleadosPlaza;

		for (Plaza plaza : plazas)
		{
			prds = utilidades.getProductosPeriodo(this.año, this.qna, this.añoFinal, this.qnaFinal, true, true,
					plaza.getIdPlaza());

			empleadosPlaza = new ArrayList<>();

			for (Producto prd : prds)
			{

				// Solamente se considera los empleados que estén dentro de los
				// productos de ordinaria de las quincenas, ya que son los
				// empleados que entran definitivamente en las nóminas
				if (!(prd.getTipoNomina().getIdTipoNomina() == 0 && prd.getTipoProducto().getIdTipoProducto() == 0))
				{
					continue;
				}

				prd.updateRegistrosDATSinEmpEnRegGeneral();

				for (PlantillaRegistro regDAT : prd.getRegistrosDAT())
				{

					numeroEmpleado = regDAT.getValorPorDescripcionContains("mero de emp");

					Trabajador nuevoTrabajador = new Trabajador(regDAT.getValorPorDescripcionContains("mero de emp"),
							regDAT.getValorPorDescripcionContains("rfc"),
							regDAT.getValorPorDescripcionContains("nombre del emp"),
							regDAT.getValorPorDescripcionContains("curp"), prd.getPlaza(), null);

					// Se evita que muestre resultados duplicados
					if (nuevoTrabajador.isRegistrado() || empleadosPlaza.contains(numeroEmpleado))
					{
						continue;
					}

					empleadosPlaza.add(numeroEmpleado);

					this.trabajadoresNuevos.add(nuevoTrabajador);

				}

				prd.setRegistrosTRA(new ArrayList<>());
				prd.setRegistrosDAT(new ArrayList<>());
				prd.setConceptos(new ArrayList<>());
				prd.setUnidadResponsable(new ArrayList<>());

			}

		}

	}

	public void actualizarEmpleados()
	{

		PreparedStatement prep = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{

			for (Trabajador empleadoNuevo : this.trabajadoresNuevos)
			{

				prep = conexion.prepareStatement(
						" INSERT INTO trabajador (idPlaza, NumEmpleado, RFC, Nombre, CURP, idStatus) VALUES (?, ?, ?, ?, ?, ?) ");

				prep.setInt(1, empleadoNuevo.getPlaza().getIdPlaza());
				prep.setString(2, empleadoNuevo.getNumEmpleado());
				prep.setString(3, empleadoNuevo.getRFC());
				prep.setString(4, empleadoNuevo.getNombre());
				prep.setString(5, empleadoNuevo.getCURP());
				prep.setInt(6, 0);

				prep.executeUpdate();

			}

			this.trabajadoresNuevos = null;
			this.trabajadoresNuevosFiltrados = null;

		} catch (Exception e)
		{
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción", "Ha ocurrido una excepción al registrar los nuevos empleados"));
		}
		finally
		{
			if (prep != null)
			{
				try
				{
					prep.close();

				} catch (SQLException e)
				{ // TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	public List<Plaza> getPlazas()
	{
		return plazas;
	}

	public void setPlazas(List<Plaza> plazas)
	{
		this.plazas = plazas;
	}

	public static long getSerialversionuid()
	{
		return serialVersionUID;
	}

	public CatalogosBean getCatalogosBean()
	{
		return catalogosBean;
	}

	public void setCatalogosBean(CatalogosBean catalogosBean)
	{
		this.catalogosBean = catalogosBean;
	}

	public int getIdPlazaSeleccionada()
	{
		return idPlazaSeleccionada;
	}

	public void setIdPlazaSeleccionada(int idPlazaSeleccionada)
	{
		this.idPlazaSeleccionada = idPlazaSeleccionada;
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

	public List<Trabajador> getTrabajadoresNuevos()
	{
		return trabajadoresNuevos;
	}

	public void setTrabajadoresNuevos(List<Trabajador> trabajadoresNuevos)
	{
		this.trabajadoresNuevos = trabajadoresNuevos;
	}

	public List<Trabajador> getTrabajadoresConsultaPlaza()
	{
		return trabajadoresConsultaPlaza;
	}

	public void setTrabajadoresConsultaPlaza(List<Trabajador> trabajadoresConsultaPlaza)
	{
		this.trabajadoresConsultaPlaza = trabajadoresConsultaPlaza;
	}

	public List<Trabajador> getTrabajadoresConsultaPlazaFiltrados()
	{
		return trabajadoresConsultaPlazaFiltrados;
	}

	public void setTrabajadoresConsultaPlazaFiltrados(List<Trabajador> trabajadoresConsultaPlazaFiltrados)
	{
		this.trabajadoresConsultaPlazaFiltrados = trabajadoresConsultaPlazaFiltrados;
	}

	public List<Trabajador> getTrabajadoresNuevosFiltrados()
	{
		return trabajadoresNuevosFiltrados;
	}

	public void setTrabajadoresNuevosFiltrados(List<Trabajador> trabajadoresNuevosFiltrados)
	{
		this.trabajadoresNuevosFiltrados = trabajadoresNuevosFiltrados;
	}

}
