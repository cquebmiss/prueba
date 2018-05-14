/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.portal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.primefaces.context.RequestContext;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import modelo.Expediente;
import modelo.Plaza;
import modelo.Sesion;
import modelo.Status;
import modelo.Trabajador;
import resources.DataBase;
import util.FacesUtils;
import util.utilidades;

/**
 *
 * @author desarolloyc
 */
@ManagedBean
@SessionScoped

public class idxArchivoDigital implements Serializable
{

	private boolean				herramientasInactivas;
	private boolean				botonEliminarDialogoInactivo;

	private String				campoBusqueda;
	private List<Trabajador>	trabajadores;
	private String				tipoBusquedaTrabajador;

	private Trabajador			trabajadorSelection;

	private boolean				checkRFC;
	private boolean				checkCURP;
	private boolean				checkActNacimiento;
	private boolean				checkCartillaMilitar;
	private boolean				checkCredElector;
	private boolean				checkSolicEmpleo;
	private boolean				checkMaxEstudios;
	private boolean				checkCedulaFONAC;
	private boolean				checkSeguroInstitucional;

	private List<String>		bitacora;

	private StreamedContent		imagenT;
	private String				rfcImagenTrabajador;
	private StreamedContent		expDescarga;
	private String				docVisualizando;
	private String				docEliminar;

	// Modo de la interfaz -1 modo de consulta normal, 0 de alta de trabajador y
	// 1 de edición de trabajador
	private int					modoInterfaz;

	private String				RFC;
	private String				CURP;
	private String				Nombre;

	public idxArchivoDigital() throws IOException
	{

		setTipoBusquedaTrabajador("nombre");
		bitacora = new ArrayList<>();

		setImagenT(null);
		setHerramientasInactivas(true);
		setBotonEliminarDialogoInactivo(true);
		setModoInterfaz(-1);

	}

	/**
	 * @return the herramientasInactivas
	 */
	public boolean isHerramientasInactivas()
	{
		return herramientasInactivas;
	}

	/**
	 * @param herramientasInactivas
	 *            the herramientasInactivas to set
	 */
	public void setHerramientasInactivas(boolean herramientasInactivas)
	{
		this.herramientasInactivas = herramientasInactivas;
	}

	/**
	 * @return the campoBusqueda
	 */
	public String getCampoBusqueda()
	{
		return campoBusqueda;
	}

	/**
	 * @param campoBusqueda
	 *            the campoBusqueda to set
	 */
	public void setCampoBusqueda(String campoBusqueda)
	{
		this.campoBusqueda = campoBusqueda;
	}

	/**
	 * @return the trabajadores
	 */
	public List<Trabajador> getTrabajadores()
	{
		return trabajadores;
	}

	/**
	 * @param trabajadores
	 *            the trabajadores to set
	 */
	public void setTrabajadores(List<Trabajador> trabajadores)
	{
		this.trabajadores = trabajadores;
	}

	/**
	 * @return the tipoBusquedaTrabajador
	 */
	public String getTipoBusquedaTrabajador()
	{
		return tipoBusquedaTrabajador;
	}

	/**
	 * @param tipoBusquedaTrabajador
	 *            the tipoBusquedaTrabajador to set
	 */
	public void setTipoBusquedaTrabajador(String tipoBusquedaTrabajador)
	{
		this.tipoBusquedaTrabajador = tipoBusquedaTrabajador;
	}

	/**
	 * @return the trabajadorSelection
	 */
	public Trabajador getTrabajadorSelection()
	{
		return trabajadorSelection;
	}

	/**
	 * @param trabajadorSelection
	 *            the trabajadorSelection to set
	 */
	public void setTrabajadorSelection(Trabajador trabajadorSelection)
	{
		this.trabajadorSelection = trabajadorSelection;
	}

	/**
	 * @return the checkRFC
	 */
	public boolean isCheckRFC()
	{
		return checkRFC;
	}

	/**
	 * @param checkRFC
	 *            the checkRFC to set
	 */
	public void setCheckRFC(boolean checkRFC)
	{
		this.checkRFC = checkRFC;
	}

	/**
	 * @return the checkCURP
	 */
	public boolean isCheckCURP()
	{
		return checkCURP;
	}

	/**
	 * @param checkCURP
	 *            the checkCURP to set
	 */
	public void setCheckCURP(boolean checkCURP)
	{
		this.checkCURP = checkCURP;
	}

	/**
	 * @return the checkActNacimiento
	 */
	public boolean isCheckActNacimiento()
	{
		return checkActNacimiento;
	}

	/**
	 * @param checkActNacimiento
	 *            the checkActNacimiento to set
	 */
	public void setCheckActNacimiento(boolean checkActNacimiento)
	{
		this.checkActNacimiento = checkActNacimiento;
	}

	/**
	 * @return the checkCartillaMilitar
	 */
	public boolean isCheckCartillaMilitar()
	{
		return checkCartillaMilitar;
	}

	/**
	 * @param checkCartillaMilitar
	 *            the checkCartillaMilitar to set
	 */
	public void setCheckCartillaMilitar(boolean checkCartillaMilitar)
	{
		this.checkCartillaMilitar = checkCartillaMilitar;
	}

	/**
	 * @return the checkCredElector
	 */
	public boolean isCheckCredElector()
	{
		return checkCredElector;
	}

	/**
	 * @param checkCredElector
	 *            the checkCredElector to set
	 */
	public void setCheckCredElector(boolean checkCredElector)
	{
		this.checkCredElector = checkCredElector;
	}

	/**
	 * @return the checkSolicEmpleo
	 */
	public boolean isCheckSolicEmpleo()
	{
		return checkSolicEmpleo;
	}

	/**
	 * @param checkSolicEmpleo
	 *            the checkSolicEmpleo to set
	 */
	public void setCheckSolicEmpleo(boolean checkSolicEmpleo)
	{
		this.checkSolicEmpleo = checkSolicEmpleo;
	}

	/**
	 * @return the checkMaxEstudios
	 */
	public boolean isCheckMaxEstudios()
	{
		return checkMaxEstudios;
	}

	/**
	 * @param checkMaxEstudios
	 *            the checkMaxEstudios to set
	 */
	public void setCheckMaxEstudios(boolean checkMaxEstudios)
	{
		this.checkMaxEstudios = checkMaxEstudios;
	}

	/**
	 * @return the checkCedulaFONAC
	 */
	public boolean isCheckCedulaFONAC()
	{
		return checkCedulaFONAC;
	}

	/**
	 * @param checkCedulaFONAC
	 *            the checkCedulaFONAC to set
	 */
	public void setCheckCedulaFONAC(boolean checkCedulaFONAC)
	{
		this.checkCedulaFONAC = checkCedulaFONAC;
	}

	/**
	 * @return the checkSeguroInstitucional
	 */
	public boolean isCheckSeguroInstitucional()
	{
		return checkSeguroInstitucional;
	}

	/**
	 * @param checkSeguroInstitucional
	 *            the checkSeguroInstitucional to set
	 */
	public void setCheckSeguroInstitucional(boolean checkSeguroInstitucional)
	{
		this.checkSeguroInstitucional = checkSeguroInstitucional;
	}

	/**
	 * @return the bitacora
	 */
	public List<String> getBitacora()
	{
		return bitacora;
	}

	/**
	 * @param bitacora
	 *            the bitacora to set
	 */
	public void setBitacora(List<String> bitacora)
	{
		this.bitacora = bitacora;
	}

	/**
	 * @return the rfcImagenTrabajador
	 */
	public String getRfcImagenTrabajador()
	{
		return rfcImagenTrabajador;
	}

	/**
	 * @param rfcImagenTrabajador
	 *            the rfcImagenTrabajador to set
	 */
	public void setRfcImagenTrabajador(String rfcImagenTrabajador)
	{
		this.rfcImagenTrabajador = rfcImagenTrabajador;
	}

	/**
	 * @return the imagenT
	 */
	public StreamedContent getImagenT()
	{
		return imagenT;
	}

	/**
	 * @param imagenT
	 *            the imagenT to set
	 */
	public void setImagenT(StreamedContent imagenT)
	{
		this.imagenT = imagenT;
	}

	/**
	 * @return the expDescarga
	 */
	public StreamedContent getExpDescarga()
	{
		return expDescarga;
	}

	/**
	 * @param expDescarga
	 *            the expDescarga to set
	 */
	public void setExpDescarga(StreamedContent expDescarga)
	{
		this.expDescarga = expDescarga;
	}

	/**
	 * @return the docVisualizando
	 */
	public String getDocVisualizando()
	{
		return docVisualizando;
	}

	/**
	 * @param docVisualizando
	 *            the docVisualizando to set
	 */
	public void setDocVisualizando(String docVisualizando)
	{
		this.docVisualizando = docVisualizando;
	}

	/**
	 * @return the docEliminar
	 */
	public String getDocEliminar()
	{
		return docEliminar;
	}

	/**
	 * @param docEliminar
	 *            the docEliminar to set
	 */
	public void setDocEliminar(String docEliminar)
	{
		this.docEliminar = docEliminar;
	}

	/**
	 * @return the botonEliminarDialogoInactivo
	 */
	public boolean isBotonEliminarDialogoInactivo()
	{
		return botonEliminarDialogoInactivo;
	}

	/**
	 * @param botonEliminarDialogoInactivo
	 *            the botonEliminarDialogoInactivo to set
	 */
	public void setBotonEliminarDialogoInactivo(boolean botonEliminarDialogoInactivo)
	{
		this.botonEliminarDialogoInactivo = botonEliminarDialogoInactivo;
	}

	/**
	 * @return the modoInterfaz
	 */
	public int getModoInterfaz()
	{
		return modoInterfaz;
	}

	/**
	 * @param modoInterfaz
	 *            the modoInterfaz to set
	 */
	public void setModoInterfaz(int modoInterfaz)
	{
		this.modoInterfaz = modoInterfaz;
	}

	public void actionBotonBuscar()
	{
		// RequestContext.getCurrentInstance().showMessageInDialog(new
		// FacesMessage(FacesMessage.SEVERITY_INFO, "What we do in life",
		// "Echoes in eternity.") );

		buscarTrabajador();

	}

	private void buscarTrabajador()
	{

		PreparedStatement prep = null;
		ResultSet rBD = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{

			limpiaInterfaz();

			trabajadores = new ArrayList<>();

			if (getCampoBusqueda() == null || getCampoBusqueda().length() < 1)
			{
				return;
			}

			prep = conexion.prepareStatement(
					"SELECT t.*, p.Descripcion AS descripcionPlaza, st.Descripcion AS descripcionStatus FROM trabajador t, webrh.plaza p, webrh.status st WHERE t.idPlaza = p.idPlaza AND t.idStatus = st.idStatus AND ( t.RFC LIKE ? OR t.Nombre LIKE ? )");

			prep.setString(1, "%" + getCampoBusqueda() + "%");
			prep.setString(2, "%" + getCampoBusqueda() + "%");

			rBD = prep.executeQuery();

			if (rBD.next())
			{
				do
				{
					trabajadores.add(new Trabajador(rBD.getString("NumEmpleado"), rBD.getString("RFC"),
							rBD.getString("Nombre"), rBD.getString("CURP"),
							new Plaza(rBD.getInt("idPlaza"), rBD.getString("descripcionPlaza")),
							new Status(rBD.getInt("idStatus"), rBD.getString("descripcionStatus"))));

				}
				while (rBD.next());
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
					// e.printStackTrace();
				}
			}
		}
	}

	public void selectTablaResultados(SelectEvent evt)
	{
		limpiaInterfaz();

		setHerramientasInactivas(false);

		getDatosExpedienteTrabajador(getTrabajadorSelection().getNumEmpleado(),
				getTrabajadorSelection().getPlaza().getIdPlaza());

		setRfcImagenTrabajador(getTrabajadorSelection().getRFC());

	}

	public void getDatosExpedienteTrabajador(String NumEmpleado, int idPlaza)
	{
		PreparedStatement prep = null;
		ResultSet rBD = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionArchivo();)
		{

			prep = conexion.prepareStatement(" SELECT * FROM docpersonales WHERE NumEmpleado=? AND idPlaza=? ");

			prep.setString(1, NumEmpleado);
			prep.setInt(2, idPlaza);

			rBD = prep.executeQuery();

			if (rBD.next())
			{
				Expediente nExpediente = new Expediente(rBD.getBoolean("docRFC"), rBD.getBoolean("docCURP"),
						rBD.getBoolean("docActa"), rBD.getBoolean("docCartilla"), rBD.getBoolean("docElector"),
						rBD.getBoolean("docSolicitudEmpleo"), rBD.getBoolean("docMaximosEstudios"),
						rBD.getBoolean("docCedulaFONAC"), rBD.getBoolean("docInscripcionSeguro"));
				nExpediente.setIdPlaza(idPlaza);
				nExpediente.setNumeroEmpleado(NumEmpleado);

				nExpediente.updateInfoArchivosExpediente();

				getTrabajadorSelection().setExpediente(nExpediente);

				setCheckRFC(nExpediente.isRFC());
				setCheckCURP(nExpediente.isCURP());
				setCheckActNacimiento(nExpediente.isActNacimiento());
				setCheckCartillaMilitar(nExpediente.isCartillaMilitar());
				setCheckCredElector(nExpediente.isCredElector());
				setCheckSolicEmpleo(nExpediente.isSolicEmpleo());
				setCheckMaxEstudios(nExpediente.isMaxEstudios());
				setCheckCedulaFONAC(nExpediente.isCedulaFONAC());
				setCheckSeguroInstitucional(nExpediente.isSeguroInstitucional());

			}

			prep.close();

			prep = conexion.prepareStatement(
					"SELECT * FROM bitacoraexpediente WHERE NumEmpleado=? AND idPlaza=? ORDER BY Fecha DESC, Hora DESC");

			prep.setString(1, NumEmpleado);
			prep.setInt(2, idPlaza);

			rBD = prep.executeQuery();

			if (rBD.next())
			{
				List<String> bitExpe = new ArrayList<>();

				do
				{
					bitExpe.add(rBD.getDate("Fecha") + " / " + rBD.getTime("Hora") + " - " + rBD.getString("Movimiento")
							+ " : " + rBD.getString("Comentarios"));

				}
				while (rBD.next());

				getTrabajadorSelection().getExpediente().setBitacora(bitExpe);
				setBitacora(bitExpe);
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
					// e.printStackTrace();
				}
			}
		}

	}

	public void actionGetImagenTrabajador()
	{

		setImagenT(utilidades.getImagenStreamed(
				utilidades.getRutaExpendientesServer() + getRfcImagenTrabajador() + "/"
						+ getTrabajadorSelection().getPlaza().getIdPlaza() + "/Imagen/",
				getRfcImagenTrabajador() + "-" + getTrabajadorSelection().getPlaza().getIdPlaza()));

	}

	public void unselectTablaResultados(UnselectEvent evt)
	{
		limpiaInterfaz();
	}

	public void limpiaInterfaz()
	{
		setCheckRFC(false);
		setCheckCURP(false);
		setCheckActNacimiento(false);
		setCheckCartillaMilitar(false);
		setCheckCredElector(false);
		setCheckSolicEmpleo(false);
		setCheckMaxEstudios(false);
		setCheckCedulaFONAC(false);
		setCheckSeguroInstitucional(false);

		setBitacora(new ArrayList<String>());

		setImagenT(null);

		setHerramientasInactivas(true);

		setExpDescarga(null);

		setBotonEliminarDialogoInactivo(true);
		setDocEliminar(null);

	}

	public void actionDescargarExpPersonal(ActionEvent evt)
	{

		try
		{
			FileInputStream fis = new FileInputStream(utilidades.getRutaExpendientesServer()
					+ getTrabajadorSelection().getPlaza().getIdPlaza() + "/" + getTrabajadorSelection().getNumEmpleado()
					+ "/Documentos Personales/DocPersonal_" + getTrabajadorSelection().getNumEmpleado() + ".pdf");

			DefaultStreamedContent nuevo = new DefaultStreamedContent(fis, "application/pdf");
			setExpDescarga(nuevo);

			setDocVisualizando("DOCUMENTOS PERSONALES");

			RequestContext.getCurrentInstance().scrollTo("areaPdfs");

		} catch (FileNotFoundException ex)
		{
			ex.printStackTrace();
			Logger.getLogger(idxArchivoDigital.class.getName()).log(Level.SEVERE, null, ex);
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage("mensajes", new FacesMessage(FacesMessage.SEVERITY_WARN, "Documentos Personales",
					"El trabajador no cuenta con documentos personales."));

		}

	}

	public void actionDescargarExpDiversos(ActionEvent evt)
	{
		try
		{
			FileInputStream fis = new FileInputStream(utilidades.getRutaExpendientesServer() + getRfcImagenTrabajador()
					+ "/" + getTrabajadorSelection().getPlaza().getIdPlaza() + "/Documentos Diversos/DocDiversos_"
					+ getRfcImagenTrabajador() + ".pdf");

			DefaultStreamedContent nuevo = new DefaultStreamedContent(fis, "application/pdf");
			setExpDescarga(nuevo);

			setDocVisualizando("DOCUMENTOS DIVERSOS");

			RequestContext.getCurrentInstance().scrollTo("areaPdfs");

		} catch (FileNotFoundException ex)
		{
			ex.printStackTrace();
			Logger.getLogger(idxArchivoDigital.class.getName()).log(Level.SEVERE, null, ex);
			FacesContext.getCurrentInstance().addMessage("mensajes", new FacesMessage(FacesMessage.SEVERITY_WARN,
					"Documentos Diversos", "El trabajador no cuenta con documentos diversos."));
		}

	}

	public void actionDescargarHistorico(ActionEvent evt)
	{
		try
		{
			FileInputStream fis = new FileInputStream(utilidades.getRutaExpendientesServer() + getRfcImagenTrabajador()
					+ "/" + getTrabajadorSelection().getPlaza().getIdPlaza() + "/Historico_" + getRfcImagenTrabajador()
					+ ".pdf");

			DefaultStreamedContent nuevo = new DefaultStreamedContent(fis, "application/pdf");
			setExpDescarga(nuevo);

			setDocVisualizando("DOCUMENTO HISTÓRICO");

			RequestContext.getCurrentInstance().scrollTo("areaPdfs");

		} catch (FileNotFoundException ex)
		{
			ex.printStackTrace();
			Logger.getLogger(idxArchivoDigital.class.getName()).log(Level.SEVERE, null, ex);
			FacesContext.getCurrentInstance().addMessage("mensajes",
					new FacesMessage(FacesMessage.SEVERITY_WARN, "Documento Histórico",
							"Documento Histórico No Generado, favor de contactar con el desarrollador del sistema."));
		}
	}

	public void changeListenerListaTipoEliminarExpediente(ValueChangeEvent evt)
	{
		if (evt.getNewValue() == null)
		{
			setBotonEliminarDialogoInactivo(true);
			return;
		}

		setBotonEliminarDialogoInactivo(false);

	}

	public void actionEliminarExpediente(ActionEvent evt)
	{
		String ruta = getTrabajadorSelection().getPlaza().getIdPlaza() + "/" + getTrabajadorSelection().getNumEmpleado()
				+ "/";

		List<String> directoriosABorrar = new ArrayList<>();
		String tipoEliminacion = "";

		switch (docEliminar)
		{
			case "docPer":
				directoriosABorrar.add("Documentos Personales");
				tipoEliminacion = "Documentos Personales";
				break;
			case "docDiv":
				directoriosABorrar.add("Documentos Diversos");
				tipoEliminacion = "Documentos Diversos";
				break;
			case "todoExpediente":
				directoriosABorrar.add("Documentos Personales");
				directoriosABorrar.add("Documentos Diversos");
				tipoEliminacion = "Todo el Expediente";
				break;
		}

		Sesion sesion = (Sesion) FacesUtils.getManagedBean("Sesion");

		for (String carpeta : directoriosABorrar)
		{

			File directorio = new File(utilidades.getRutaExpendientesServer() + ruta + carpeta);

			// primero se debe de borrar todos los archivos contenidos para que
			// se pueda borra el diectorio indicado

			if (!directorio.exists())
			{
				continue;

			}

			utilidades.eliminarRuta(new File(utilidades.getRutaExpendientesServer() + ruta + carpeta));

			if (!tipoEliminacion.equals("Documentos Diversos"))
			{
				PreparedStatement prep = null;

				try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionArchivo();)
				{
					prep = conexion.prepareStatement("DELETE FROM docpersonales WHERE idPlaza=? AND NumEmpleado=?");

					prep.setInt(1, this.getTrabajadorSelection().getPlaza().getIdPlaza());
					prep.setString(2, this.getTrabajadorSelection().getNumEmpleado());

					prep.executeUpdate();

				} catch (Exception e)
				{
					e.printStackTrace();
					FacesContext.getCurrentInstance().addMessage("mensajes", new FacesMessage(
							FacesMessage.SEVERITY_WARN, "Excepción al Eliminar Expediente",
							"No se pudieron eliminar los indicadores del registro de doc. personales. Favor de contactar con el desarrollador del sistema."));
					return;

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
			else
			{
				FacesContext.getCurrentInstance().addMessage("mensajes", new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Expediente No Eliminado",
						"El expediente no se ha podido eliminar, favor de contactar con el desarrollador del sistema."));
				return;
			}

		}

		if (tipoEliminacion.equals("Todo el Expediente"))
		{
			utilidades.eliminarRuta(new File(utilidades.getRutaExpendientesServer() + ruta));
		}
		else
		{
			ingresoDocsBean ingresoDBean = (ingresoDocsBean) FacesUtils.getManagedBean("ingresoDocsBean");
			ingresoDBean.generarArchivoHistorico(getTrabajadorSelection().getNumEmpleado(),
					getTrabajadorSelection().getPlaza().getIdPlaza());

		}

		utilidades.registrarEnBitacora(Integer.parseInt(sesion.getIdUsuario()),
				getTrabajadorSelection().getNumEmpleado(), getTrabajadorSelection().getPlaza().getIdPlaza(),
				"Eliminación de Expediente", "Expediente eliminado: " + tipoEliminacion);
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Expediente Eliminado", "El expediente se ha eliminado exitosamente."));

		limpiaInterfaz();

		getDatosExpedienteTrabajador(getTrabajadorSelection().getNumEmpleado(),
				getTrabajadorSelection().getPlaza().getIdPlaza());
		RequestContext.getCurrentInstance().execute("PF('dlgEliminarExpediente').hide()");

	}

	public void cerrarDialogoEliminarExpediente(CloseEvent evt)
	{
		setDocEliminar(null);
		setBotonEliminarDialogoInactivo(true);

	}

	public void cambiarModoInterfaz(int modo)
	{
		setModoInterfaz(modo);
		TrabajadorNuevoEdicionBean bean = (TrabajadorNuevoEdicionBean) FacesUtils
				.getManagedBean("trabajadorNuevoEdicionBean");

		if (getModoInterfaz() == 0)
		{
			bean.inicializaBeanNuevoTrabajador();
		}
		else if (getModoInterfaz() == 1)
		{
			bean.inicializaBeanEdicionTrabajador(getTrabajadorSelection());
			bean.setImagenT(utilidades.getImagenStreamed(
					utilidades.getRutaExpendientesServer() + getTrabajadorSelection().getRFC() + "/"
							+ getTrabajadorSelection().getPlaza().getIdPlaza() + "/Imagen/"
							+ getTrabajadorSelection().getRFC() + "-" + getTrabajadorSelection().getPlaza().getIdPlaza()
							+ ".jpg",
					getTrabajadorSelection().getRFC() + " - " + getTrabajadorSelection().getNombre() + " - "
							+ getTrabajadorSelection().getPlaza().getDescripcionPlaza()));
		}

	}

	public void actionEliminarTrabajador(ActionEvent evt)
	{
		PreparedStatement prep = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionArchivo();)
		{
			prep = conexion.prepareStatement("DELETE FROM bitacoraexpediente WHERE RFC=? AND idPlaza=?");

			prep.setString(1, getTrabajadorSelection().getRFC());
			prep.setInt(2, getTrabajadorSelection().getPlaza().getIdPlaza());

			prep.executeUpdate();

			prep.close();

			prep = conexion.prepareStatement("DELETE FROM docpersonales WHERE RFC=? AND idPlaza=?");

			prep.setString(1, getTrabajadorSelection().getRFC());
			prep.setInt(2, getTrabajadorSelection().getPlaza().getIdPlaza());

			prep.executeUpdate();

			prep.close();

			prep = conexion.prepareStatement("DELETE FROM trabajador WHERE RFC=? AND idPlaza=?");

			prep.setString(1, getTrabajadorSelection().getRFC());
			prep.setInt(2, getTrabajadorSelection().getPlaza().getIdPlaza());

			prep.executeUpdate();

			prep.close();

			// Finalmente se eliminan los archivos físicos del servidor
			String rutaArchivo = utilidades.getRutaExpendientesServer() + getRfcImagenTrabajador() + "/"
					+ getTrabajadorSelection().getPlaza().getIdPlaza() + "/";

			File expediente = new File(rutaArchivo);

			utilidades.eliminarRuta(expediente);

			limpiaInterfaz();
			actionBotonBuscar();

			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Trabajador Eliminado",
							"El trabajador y su expediente han sido eliminados definitivamente del sistema."));

		} catch (Exception e)
		{
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Excepción",
					"Ha ocurrido una excepción al eliminar al trabajador del sistema, contacte con el desarrollador del sistema."));
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
					// e.printStackTrace();
				}
			}
		}

	}

	public void actionNuevoEdicionTrabajador(ActionEvent evt)
	{

		PreparedStatement prep = null;
		ResultSet rBD = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionArchivo();)
		{
			TrabajadorNuevoEdicionBean beanNuevoTrabajador = (TrabajadorNuevoEdicionBean) FacesUtils
					.getManagedBean("trabajadorNuevoEdicionBean");

			// Primero se valida que el RFC con el tipo de plaza

			switch (modoInterfaz)
			{
				case 0:
					prep = conexion.prepareStatement(" SELECT * FROM trabajador WHERE RFC=? AND idPlaza=?");
					break;

				case 1:
					prep = conexion.prepareStatement(
							" SELECT * FROM trabajador WHERE ( RFC=? AND idPlaza=? ) AND ! ( RFC=? AND idPlaza=? )");

					prep.setString(3, getTrabajadorSelection().getRFC());
					prep.setInt(4, getTrabajadorSelection().getPlaza().getIdPlaza());
					break;
			}

			prep.setString(1, beanNuevoTrabajador.getTrabajador().getRFC());
			prep.setInt(2, beanNuevoTrabajador.getTrabajador().getPlaza().getIdPlaza());

			rBD = prep.executeQuery();

			if (rBD.next())
			{
				prep.close();

				FacesContext context = FacesContext.getCurrentInstance();
				context.addMessage("mensajesAltaEdicion",
						new FacesMessage(FacesMessage.SEVERITY_WARN, "Trabajador Ya Registrado",
								"El trabajador ya está dado de alta con el tipo de plaza seleccionada."));
				return;
			}

			String tituloMensaje = null;
			String detalleMensaje = null;
			// Si pasó la validación del tipo de plaza se registra en la base de
			// datos
			switch (modoInterfaz)
			{
				case 0:
					tituloMensaje = "Alta de Trabajador";
					detalleMensaje = "El trabajador se ha dado de alta exitosamente.";

					prep.close();
					prep = conexion.prepareStatement(
							" INSERT INTO trabajador (RFC, Nombre, CURP, idPlaza) VALUES (?, ?, ?, ?) ");
					break;

				case 1:

					tituloMensaje = "Datos del Trabajador";
					detalleMensaje = "Los datos del trabajador se han actualizado exitosamente.";

					prep.close();
					prep = conexion.prepareStatement(
							" UPDATE trabajador SET RFC=?,Nombre=?,CURP=?,idPlaza=? WHERE RFC=? and idPlaza=? ");

					prep.setString(5, getTrabajadorSelection().getRFC());
					prep.setInt(6, getTrabajadorSelection().getPlaza().getIdPlaza());

					break;
			}

			prep.setString(1, beanNuevoTrabajador.getTrabajador().getRFC());
			prep.setString(2, beanNuevoTrabajador.getTrabajador().getNombre());
			prep.setString(3, beanNuevoTrabajador.getTrabajador().getCURP());
			prep.setInt(4, beanNuevoTrabajador.getTrabajador().getPlaza().getIdPlaza());

			prep.executeUpdate();

			String ruta = utilidades.getRutaExpendientesServer() + beanNuevoTrabajador.getTrabajador().getRFC() + "/";
			String nombreImagen = beanNuevoTrabajador.getTrabajador().getRFC() + "-"
					+ beanNuevoTrabajador.getTrabajador().getPlaza().getIdPlaza() + ".jpg";

			File destino = null;

			switch (modoInterfaz)
			{
				case 0:

					destino = new File(ruta + beanNuevoTrabajador.getTrabajador().getPlaza().getIdPlaza() + "/Imagen/");

					if (!destino.exists())
					{
						destino.mkdirs();
					}

					// Ahora solamente se copia el archivo de imágen que se está
					// subiendo en caso de haberlo
					if (beanNuevoTrabajador.getImagenTrab() != null)
					{
						utilidades.creaUploadedADisco(
								ruta + beanNuevoTrabajador.getTrabajador().getPlaza().getIdPlaza() + "/Imagen/",
								beanNuevoTrabajador.getTrabajador().getRFC() + "-"
										+ beanNuevoTrabajador.getTrabajador().getPlaza().getIdPlaza(),
								beanNuevoTrabajador.getImagenTrab());
					}

					break;

				case 1:

					String rutaAnterior = utilidades.getRutaExpendientesServer() + getTrabajadorSelection().getRFC()
							+ "/";
					File directorioAnterior = null;

					// Primero se renombra el directorio con el RFC principal
					if (!getTrabajadorSelection().getRFC().equals(beanNuevoTrabajador.getTrabajador().getRFC()))
					{
						destino = new File(ruta);
						directorioAnterior = new File(rutaAnterior);
						directorioAnterior.renameTo(destino);
					}

					// Ahora se renombra la plaza
					if (getTrabajadorSelection().getPlaza().getIdPlaza() != beanNuevoTrabajador.getTrabajador()
							.getPlaza().getIdPlaza())
					{
						destino = new File(ruta + beanNuevoTrabajador.getTrabajador().getPlaza().getIdPlaza() + "/");
						directorioAnterior = new File(ruta + getTrabajadorSelection().getPlaza().getIdPlaza() + "/");
						directorioAnterior.renameTo(destino);
					}

					boolean imagenCargada = false;

					if (beanNuevoTrabajador.getImagenTrab() != null)
					{
						try
						{
							if (beanNuevoTrabajador.getImagenTrab().getFileName().length() > 0)
							{
								imagenCargada = true;
							}
						} catch (Exception e)
						{
							e.printStackTrace();

							imagenCargada = false;
						}
					}

					directorioAnterior = new File(ruta + beanNuevoTrabajador.getTrabajador().getPlaza().getIdPlaza()
							+ "/Imagen/" + getTrabajadorSelection().getRFC() + "-"
							+ getTrabajadorSelection().getPlaza().getIdPlaza() + ".jpg");

					if (imagenCargada)
					{
						if (directorioAnterior.exists())
						{
							directorioAnterior.delete();
						}

						utilidades.creaUploadedADisco(
								ruta + beanNuevoTrabajador.getTrabajador().getPlaza().getIdPlaza() + "/Imagen/",
								beanNuevoTrabajador.getTrabajador().getRFC() + "-"
										+ beanNuevoTrabajador.getTrabajador().getPlaza().getIdPlaza(),
								beanNuevoTrabajador.getImagenTrab());

					}
					else
					{

						if (directorioAnterior.exists())
						{
							destino = new File(ruta + beanNuevoTrabajador.getTrabajador().getPlaza().getIdPlaza()
									+ "/Imagen/" + beanNuevoTrabajador.getTrabajador().getRFC() + "-"
									+ beanNuevoTrabajador.getTrabajador().getPlaza().getIdPlaza() + ".jpg");

							directorioAnterior.renameTo(destino);
						}

					}

					// Ahora se actualiza el histórico
					directorioAnterior = new File(ruta + beanNuevoTrabajador.getTrabajador().getPlaza().getIdPlaza()
							+ "/Historico_" + getTrabajadorSelection().getRFC() + ".pdf");

					if (directorioAnterior.exists())
					{
						destino = new File(ruta + beanNuevoTrabajador.getTrabajador().getPlaza().getIdPlaza()
								+ "/Historico_" + beanNuevoTrabajador.getTrabajador().getRFC() + ".pdf");

						directorioAnterior.renameTo(destino);
					}

					// Se actualiza documentos diversos en caso de haber
					directorioAnterior = new File(ruta + beanNuevoTrabajador.getTrabajador().getPlaza().getIdPlaza()
							+ "/Documentos Diversos/DocDiversos_" + getTrabajadorSelection().getRFC() + ".pdf");

					if (directorioAnterior.exists())
					{
						destino = new File(ruta + beanNuevoTrabajador.getTrabajador().getPlaza().getIdPlaza()
								+ "/Documentos Diversos/DocDiversos_" + beanNuevoTrabajador.getTrabajador().getRFC()
								+ ".pdf");

						directorioAnterior.renameTo(destino);
					}

					// se actualiza documentos personales en caso de haberlos
					directorioAnterior = new File(ruta + beanNuevoTrabajador.getTrabajador().getPlaza().getIdPlaza()
							+ "/Documentos Personales/DocPersonal_" + getTrabajadorSelection().getRFC() + ".pdf");

					if (directorioAnterior.exists())
					{
						destino = new File(ruta + beanNuevoTrabajador.getTrabajador().getPlaza().getIdPlaza()
								+ "/Documentos Personales/DocPersonal_" + beanNuevoTrabajador.getTrabajador().getRFC()
								+ ".pdf");

						directorioAnterior.renameTo(destino);
					}

					break;
			}

			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, tituloMensaje, detalleMensaje));

			setModoInterfaz(-1);

			setCampoBusqueda(campoBusqueda);
			buscarTrabajador();

		} catch (Exception e)
		{
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Excepción en Formulario",
					"Se ha generado una excepción al utilizar el formulario, favor de contactar con el desarrollador del sistema."));
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
					// e.printStackTrace();
				}
			}
		}

	}

	/**
	 * @return the RFC
	 */
	public String getRFC()
	{
		return RFC;
	}

	/**
	 * @param RFC
	 *            the RFC to set
	 */
	public void setRFC(String RFC)
	{
		this.RFC = RFC;
	}

	/**
	 * @return the CURP
	 */
	public String getCURP()
	{
		return CURP;
	}

	/**
	 * @param CURP
	 *            the CURP to set
	 */
	public void setCURP(String CURP)
	{
		this.CURP = CURP;
	}

	/**
	 * @return the Nombre
	 */
	public String getNombre()
	{
		return Nombre;
	}

	/**
	 * @param Nombre
	 *            the Nombre to set
	 */
	public void setNombre(String Nombre)
	{
		this.Nombre = Nombre;
	}

}
