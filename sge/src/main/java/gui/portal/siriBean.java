/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.portal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLType;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.print.attribute.standard.Severity;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import modelo.BaseSiri;
import modelo.Campo;
import modelo.CampoPlantilla;
import modelo.CampoVinculado;
import modelo.ConverterVinculoPlantilla;
import modelo.CorrectorCURP;
import modelo.Institucion;
import modelo.Layout;
import modelo.LayoutVersion;
import modelo.Plantilla;
import modelo.PlantillaAnexoGenerado;
import modelo.PlantillaBimestre;
import modelo.PlantillaRegistro;
import modelo.Plaza;
import modelo.RegistroAhorroSolidario;
import modelo.TipoPlantilla;
import modelo.Unidad;
import modelo.VinculoPlantilla;
import resources.DataBase;
import util.FacesUtils;
import util.utilidades;

/**
 *
 * @author desarolloyc
 */
@ManagedBean
@SessionScoped
public class siriBean implements Serializable
{

    private DataTable			  dataTableSAR100;
    private List<BaseSiri>		  basesSiri;
    private BaseSiri			  baseSeleccionada;
    private int				  modoInterfaz;

    // Atributos para la sección de SIRI
    private int				  tipoCatalogoEditando;
    private List<CampoPlantilla>	  camposGenerales;
    private List<CampoPlantilla>	  camposFiltrados;
    private CampoPlantilla		  campoSeleccionado;
    private UploadedFile		  archivoSIRI;
    private String			  comentariosArchivoSIRI;

    // 0 nuevo, 1 edición, -1 nada
    private int				  estadoNuevoEdicionCampo;
    private CampoPlantilla		  nuevoEdicionCampo;

    // Atributos para la parte de los anexos
    private List<Layout>		  anexos;
    private Layout			  anexoSeleccionado;
    private Layout			  anexoSeleccionadoAux;
    private LayoutVersion		  versionSeleccionada;
    private LayoutVersion		  nuevoEdicionLayoutVersion;
    private Plantilla			  detalleSeleccionado;

    private Plantilla			  nuevoEdicionPlantillaAnexo;
    private int				  estadoNuevoEdicionAnexo;
    // 0 sAR 1 SIRI
    private int				  tipoPlantillaEditando;
    private DualListModel<CampoPlantilla> camposDualPlantillaAnexo;

    private String			  indicePrincipal;
    private String			  indiceSecundario;

    private ConverterVinculoPlantilla	  converterVinculoPlantilla;
    private List<VinculoPlantilla>	  vinculosCreados;
    private VinculoPlantilla		  vinculoSeleccionado;
    private int				  idVinculoSeleccionado;
    private int				  tipoPlantillaVinculoEditando;

    private VinculoPlantilla		  vinculoNuevoEdicion;

    private Layout			  sarVersionesVincular;
    private Layout			  siriVersionesVincular;
    private LayoutVersion		  sarVinculado;
    private LayoutVersion		  siriVinculado;

    private List<CampoPlantilla>	  camposAElegirAVincular;
    private List<CampoPlantilla>	  camposAElegirFiltrados;
    private CampoPlantilla		  campoVinculando;
    private CampoPlantilla		  campoAVincularSeleccionado;
    private int				  tipoDoialgoVinculando;

    private Date			  fechaBaja;

    // Atributos para la parte de los datos SAR100
    private List<String>		  años;
    private String			  añoSeleccionado;
    private String			  bimestreSelec;
    private List<Plaza>			  plazas;
    private Plaza			  plazaSeleccionada;
    private Unidad			  unidadSeleccionada;
    private UploadedFile		  archivoSAR100;

    private PlantillaBimestre		  plantillaBimestre;
    private List<PlantillaRegistro>	  registrosFiltrados;
    private PlantillaRegistro		  registroSeleccionado;
    // columnas de la tabla de datos de SAR100
    private List<ColumnModel>		  columnas		   = new ArrayList<>();

    // GENERACION DE ANEXOS
    private String			  curpEditando;
    private String			  curpCorregido;
    private CampoPlantilla		  indiceBusquedaAnexo;
    private CampoPlantilla		  indiceBusquedaSAR100;
    private CampoPlantilla		  indicebusquedaSIRI;
    private String			  tipoPagoRealizar;
    private String			  tipoAnexoARealizar;
    private String			  origenSueldo;

    private StreamedContent		  txt;

    BigDecimal				  factorVivienda	   = new BigDecimal(".0500");
    BigDecimal				  factorRetiro		   = new BigDecimal(".0200");
    BigDecimal				  factorCesantiaPatron	   = new BigDecimal(".03175");
    BigDecimal				  factorCesantiaTrabajador = new BigDecimal(".06125");
    BigDecimal				  factorAhorroPatron	   = new BigDecimal("3.25");
    BigDecimal				  bigCero		   = new BigDecimal("0.00");

    // SECCIÓN DE DATOS INSTITUCIONALES
    private DualListModel<CampoPlantilla> camposDualDatosInstitucionales;
    private Institucion			  institucion;

    private CampoPlantilla		  campoInstitucionalSeleccionado;

    // SECCIÓN DE AHORRO SOLIDARIO
    private List<String>		  curpsDisponiblesAhorro;
    private String			  curpSeleccionado;
    private RegistroAhorroSolidario	  registroAhorroSeleccionado;
    private RegistroAhorroSolidario	  nuevoRegistroAhorroSolidario;
    private RegistroAhorroSolidario	  edicionRegistroAhorroSolidario;

    public siriBean()
    {
	this.basesSiri = null;
	setInterfaz(1);

    }

    public void setInterfaz(int modo)
    {
	this.setModoInterfaz(modo);

	switch (modo)
	{
	    case 0:

		// Se establecen los layout solo de anexos A, B, C, etc...
		// excluyendo los layouts de siri y de sar100
		this.setAnexos(utilidades.getLayoutsBDExcluyendoSARSIRI());
		this.setAnexoSeleccionado(null);
		this.setVersionSeleccionada(null);
		this.nuevoEdicionLayoutVersion = new LayoutVersion(-1, "", null);

		setAñosDisponibles();

		setBimestreSelec("1");
		this.plazas = utilidades.getPlazas();
		setPlazaSeleccionada(null);
		setUnidadSeleccionada(null);

		break;

	    case 1:

		this.setBasesSiri(utilidades.getBasesSiri());
		this.setBaseSeleccionada(null);

		break;

	    case 2:

		setAñosDisponibles();

		setBimestreSelec("1");
		this.plazas = utilidades.getPlazas();
		setPlazaSeleccionada(null);
		setUnidadSeleccionada(null);

	    case 3:

		setTipoCatalogoEditando(0);
		setEstadoNuevoEdicionAnexo(-1);
		setEstadoNuevoEdicionCampo(-1);
		this.setCamposGenerales(utilidades.getCamposBD());
		this.setNuevoEdicionCampo(new CampoPlantilla(-1, "", "", 0, 0));
		this.setEstadoNuevoEdicionCampo(-1);
		this.nuevoEdicionLayoutVersion = new LayoutVersion(-1, "", null);

		break;

	    // Datos Institucionales
	    case 4:

		this.setCamposGenerales(utilidades.getCamposBD());
		this.institucion = new Institucion();
		this.institucion.getCamposInstitucionales();

		this.camposDualDatosInstitucionales = new DualListModel<>(this.camposGenerales,
			this.institucion.getCampos());

		break;

	    // Ahorro Solidario
	    case 5:

		this.institucion = new Institucion();
		this.institucion.updatePlantillaAhorroSolidario();

		this.curpsDisponiblesAhorro = utilidades.getCURPSDisponiblesParaAhorroSolidario();

		this.nuevoRegistroAhorroSolidario = new RegistroAhorroSolidario("", 1);
		this.edicionRegistroAhorroSolidario = new RegistroAhorroSolidario("", 1);

		this.curpSeleccionado = null;
		this.registroAhorroSeleccionado = null;

		break;
	}
    }

    private void setAñosDisponibles()
    {
	this.años = new ArrayList<>();

	Calendar ahoraCal = Calendar.getInstance();

	int año = +ahoraCal.get(Calendar.YEAR);

	setAñoSeleccionado("" + año);
	años.add("" + año);
	año--;
	años.add("" + año);
	año--;
	años.add("" + año);

    }

    public void preparaInterfazCatalogo(AjaxBehaviorEvent evt)
    {
	switch (this.getTipoCatalogoEditando())
	{
	    case 0:

		setTipoCatalogoEditando(0);
		this.setCamposGenerales(utilidades.getCamposBD());
		this.setNuevoEdicionCampo(new CampoPlantilla(-1, "", "", 0, 0));
		this.setEstadoNuevoEdicionCampo(-1);

		break;

	    case 1:

		this.setAnexos(utilidades.getLayoutsBD());
		this.setAnexoSeleccionado(null);
		this.setVersionSeleccionada(null);
		this.nuevoEdicionLayoutVersion = new LayoutVersion(-1, "", null);

		break;

	    case 2:

		break;

	    case 3:

		break;

	}
    }

    // MÉTODOS PARA LA SECCIÓN DEL SAR100
    public void listenerChangeAño(AjaxBehaviorEvent evt)
    {
	updateDatosBimestreSAR100();
    }

    public void listenerChangeBimestre(AjaxBehaviorEvent evt)
    {
	updateDatosBimestreSAR100();
    }

    public void listenerChangePlaza(AjaxBehaviorEvent evt)
    {
	this.plazaSeleccionada.updateUnidades();
	this.unidadSeleccionada = null;
	// updateDatosBimestreSAR100();

    }

    public void listenerChangeUnidad(AjaxBehaviorEvent evt)
    {
	updateDatosBimestreSAR100();
    }

    public void updateDatosBimestreSAR100()
    {
	this.dataTableSAR100.reset();

	if (unidadSeleccionada == null)
	{
	    this.plantillaBimestre = null;
	    return;
	}

	preparaLayoutAnexoSAR100(false);

	if (this.anexoSeleccionado.getVersiones() != null)
	{
	    updateColumnas(this.anexoSeleccionado.getVersiones().get(0).getDetalles().get(0));
	}
	else
	{
	    this.columnas = new ArrayList<>();
	    this.plantillaBimestre = null;
	}

    }

    public void updateColumnas(Plantilla plan)
    {
	this.columnas = new ArrayList<>();

	for (CampoPlantilla campo : plan.getCampos())
	{
	    this.columnas.add(new ColumnModel("" + campo.getDescripcion(), "" + (campo.getOrden() - 1)));
	}

    }

    // Por defecto se selecciona la única versión del SAR100 de acuerdo al
    // bimestre, ya que al ingresar datos del bimestre solo se podrá hacer con
    // la versión que se elija y no con más versiones
    public void preparaLayoutAnexoSAR100(boolean utilizarAuxilar)
    {

	if (utilizarAuxilar)
	{

	    this.anexoSeleccionadoAux = new Layout(6, "SAR100");
	    this.anexoSeleccionadoAux.updateVersionSAR(Integer.parseInt(this.añoSeleccionado),
		    Integer.parseInt(this.bimestreSelec), this.plazaSeleccionada, this.unidadSeleccionada);

	    if (this.anexoSeleccionado.getVersiones() == null)
	    {
		FacesContext.getCurrentInstance().addMessage(null,
			new FacesMessage(FacesMessage.SEVERITY_WARN, "SAR100",
				"No existe información para SAR100 en el bimestre " + this.bimestreSelec + " de "
					+ this.añoSeleccionado));

		return;
	    }

	    this.anexoSeleccionadoAux.getVersiones().get(0).updatePlantillaEncabezado(true);
	    this.anexoSeleccionadoAux.getVersiones().get(0).updatePlantillasDetalle(true);

	    this.plantillaBimestre = new PlantillaBimestre(this.plazaSeleccionada, this.unidadSeleccionada,
		    Integer.parseInt(this.añoSeleccionado), Integer.parseInt(this.bimestreSelec),
		    this.anexoSeleccionadoAux.getVersiones().get(0).getEncabezado(),
		    this.anexoSeleccionadoAux.getVersiones().get(0).getDetalles().get(0));

	    this.plantillaBimestre.updateEncabezado();
	    this.plantillaBimestre.updateRegistrosDetalle();

	}
	else
	{
	    this.anexoSeleccionado = new Layout(6, "SAR100");
	    this.anexoSeleccionado.updateVersionSAR(Integer.parseInt(this.añoSeleccionado),
		    Integer.parseInt(this.bimestreSelec), this.plazaSeleccionada, this.unidadSeleccionada);

	    if (this.anexoSeleccionado.getVersiones() == null)
	    {
		FacesContext.getCurrentInstance().addMessage(null,
			new FacesMessage(FacesMessage.SEVERITY_WARN, "SAR100",
				"No existe información para SAR100 en el bimestre " + this.bimestreSelec + " de "
					+ this.añoSeleccionado));

		return;
	    }

	    this.versionSeleccionada = null;

	    this.anexoSeleccionado.getVersiones().get(0).updatePlantillaEncabezado(true);
	    this.anexoSeleccionado.getVersiones().get(0).updatePlantillasDetalle(true);

	    this.plantillaBimestre = new PlantillaBimestre(this.plazaSeleccionada, this.unidadSeleccionada,
		    Integer.parseInt(this.añoSeleccionado), Integer.parseInt(this.bimestreSelec),
		    this.anexoSeleccionado.getVersiones().get(0).getEncabezado(),
		    this.anexoSeleccionado.getVersiones().get(0).getDetalles().get(0));

	    this.plantillaBimestre.updateEncabezado();
	    this.plantillaBimestre.updateRegistrosDetalle();
	}

    }

    public void actionEliminarCatalogoSAR100()
    {

	try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionSIRI();)
	{
	    // Eliminar tanto encabezado como cada uno de los registros de la
	    // plantilla del sar100
	    PreparedStatement prep = conexion.prepareStatement(
		    " DELETE FROM plantillavalores WHERE Ano=? AND Bimestre=? AND idPlaza=? AND idUnidad=? ");

	    prep.setInt(1, Integer.parseInt(this.añoSeleccionado));
	    prep.setInt(2, Integer.parseInt(this.bimestreSelec));
	    prep.setInt(3, this.plazaSeleccionada.getIdPlaza());
	    prep.setInt(4, this.unidadSeleccionada.getIdUnidad());

	    prep.executeUpdate();

	    RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_INFO,
		    "Catálogo SAR100 Eliminado", "El catálogo del SAR100 se ha eliminado exitosamente."));

	    this.plantillaBimestre = null;

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	    RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_ERROR,
		    "Excepción",
		    "Ha ocurrido una excepción al momento de eliminar el catálogo del SAR100, favor de contactar con el desarrollador del sistema."));
	}

    }

    public void actionGuardarCatalogoSAR100()
    {

	try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionSIRI();)
	{
	    PreparedStatement prep = null;
	    ResultSet rBD = null;

	    // se recorre el archivo subido, y se van subiendo los datos de
	    // acuerdo a la plantilla
	    if (this.archivoSAR100 != null)
	    {

		BufferedReader buffer = null;

		buffer = new BufferedReader(new InputStreamReader(this.archivoSAR100.getInputstream()));

		this.versionSeleccionada.updatePlantillaEncabezado(true);
		// this.anexoSeleccionado.getVersiones().get(0).updatePlantillaEncabezado(true);

		boolean registrado = registrarPlantillaValoresSAR100(this.versionSeleccionada.getEncabezado(), buffer,
			this.plazaSeleccionada, this.unidadSeleccionada, Integer.parseInt(añoSeleccionado),
			Integer.parseInt(bimestreSelec), true);

		if (!registrado)
		{
		    return;
		}

		this.versionSeleccionada.updatePlantillasDetalle(true);
		// this.anexoSeleccionado.getVersiones().get(0).updatePlantillasDetalle(true);

		registrado = registrarPlantillaValoresSAR100(this.versionSeleccionada.getDetalles().get(0), buffer,
			this.plazaSeleccionada, this.unidadSeleccionada, Integer.parseInt(añoSeleccionado),
			Integer.parseInt(bimestreSelec), false);

		if (registrado)
		{
		    FacesContext.getCurrentInstance().addMessage(null,
			    new FacesMessage(FacesMessage.SEVERITY_INFO, "Base de Datos Registrada",
				    "La base de datos bimestral del SAR100 ha sido registrada exitosamente."));

		    // Se visualiza en pantalla la base de datos que se ha
		    // subido del sar100
		    updateDatosBimestreSAR100();
		    // this.plantillaBimestre.updateEncabezado();
		    // this.plantillaBimestre.updateRegistrosDetalle();
		}

	    }

	    this.basesSiri = utilidades.getBasesSiri();

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}

    }

    public boolean registrarPlantillaValoresSAR100(Plantilla plan, BufferedReader buffer, Plaza plaza, Unidad unidad,
	    int año, int bimestre, boolean encabezado)
    {

	try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionSIRI();)
	{
	    PreparedStatement prep = null;
	    ResultSet rBD = null;

	    ordenarPosicionesCamposPlantilla(plan.getCampos());

	    boolean lecturanula = false;
	    int nLinea = 1;

	    // Primero se da de alta el año y bimestre que se intenta ingresar
	    prep = conexion.prepareStatement(" SELECT * FROM anobimestre WHERE ano=? AND bimestre=? ");

	    prep.setInt(1, año);
	    prep.setInt(2, bimestre);

	    rBD = prep.executeQuery();

	    if (!rBD.next())
	    {
		prep = conexion.prepareStatement("INSERT INTO anobimestre (ano,bimestre) VALUES (?, ?)");

		prep.setInt(1, año);
		prep.setInt(2, bimestre);

		prep.executeUpdate();

	    }

	    int limite = -1;

	    if (encabezado)
	    {
		limite = 1;
	    }

	    String insercion = " INSERT INTO plantillavalores (idPlantilla, Orden, Valor, Ano, Bimestre, idPlaza, idUnidad, idRegistro) VALUES (?, ?, ?, ?, ?, ?, ?, ?) ";
	    String subCadenaValor;
	    String linea;
	    int orden;
	    prep = conexion.prepareStatement(insercion);

	    while (!lecturanula)
	    {

		try
		{

		    linea = buffer.readLine();

		    if (linea == null)
		    {

			lecturanula = true;
			System.out.println("terminó lectura");

		    }
		    else
		    {

			if (linea.trim().length() < 1)
			{
			    return true;
			}

			orden = 1;

			for (Campo campo : plan.getCampos())
			{

			    subCadenaValor = linea.substring(campo.getPosInicioCampo() - 1, campo.getPosFinCampo());

			    prep.setInt(1, plan.getIdPlantilla());
			    prep.setInt(2, orden);
			    prep.setString(3, subCadenaValor);
			    prep.setInt(4, año);
			    prep.setInt(5, bimestre);
			    prep.setInt(6, plaza.getIdPlaza());
			    prep.setInt(7, unidad.getIdUnidad());
			    prep.setInt(8, nLinea);

			    prep.addBatch();

			    orden++;

			}

			prep.executeBatch();

			System.out.println(nLinea);

		    }

		    nLinea++;

		    if (limite == -1)
		    {
			continue;
		    }

		    if (nLinea > limite)
		    {
			return true;
		    }

		}
		catch (IOException ex)
		{
		    System.out.println(ex);
		    return false;

		}

	    }

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	    return false;
	}

	return true;

    }

    // MÉTODOS PARA LA SECCIÓN DE SIRI
    public void preparaFormularioNuevaBDSiri()
    {
	this.anexoSeleccionado = new Layout(5, "SIRI");
	this.anexoSeleccionado.updateVersiones();
	this.anexoSeleccionado.getVersiones().get(0).updatePlantillasDetalle(true);
	this.comentariosArchivoSIRI = "";
    }

    public void preparaFormularioNuevoSAR10()
    {
	this.anexoSeleccionado = new Layout(6, "SAR100");
	this.anexoSeleccionado.updateVersiones();
    }

    public void selectTablaBasesSIRI(SelectEvent evt)
    {
	this.baseSeleccionada.setPlantilla(null);
    }

    public void unselectTablaBasesSIRI(UnselectEvent evt)
    {
	this.baseSeleccionada.setPlantilla(null);
    }

    public void actionVisualizarDatosSIRI()
    {
	this.baseSeleccionada.updatePlantilla();
	this.registroSeleccionado = null;
	this.registrosFiltrados = null;
	updateColumnas(this.baseSeleccionada.getPlantilla().getPlantilla());
	RequestContext.getCurrentInstance().scrollTo("datosSIRI");
    }

    public void actionEliminarCatalogoSIRI()
    {
	try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionSIRI();)
	{
	    PreparedStatement prep = null;
	    ResultSet rBD = null;

	    prep = conexion.prepareStatement(" DELETE FROM plantillasirivalores WHERE idBaseSiri=? ");

	    prep.setInt(1, this.baseSeleccionada.getIdBaseSiri());

	    prep.executeUpdate();

	    prep = conexion.prepareStatement(" DELETE FROM basesiri WHERE idBaseSiri=? ");

	    prep.setInt(1, this.baseSeleccionada.getIdBaseSiri());

	    prep.executeUpdate();

	    RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_INFO,
		    "Catálogo del SIRI Eliminado", "El catálogo del SIRI ha sido eliminado exitosamente."));

	    this.basesSiri = utilidades.getBasesSiri();
	    this.baseSeleccionada = null;

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	    RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_ERROR,
		    "Excepción",
		    "Ha ocurrido una excepción al momento de eliminar el catálogo del SIRI, favor de contactar con el desarrollador del sistema."));
	}

    }

    public void actionGuardarCatalogoSiri()
    {

	try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionSIRI();)
	{
	    PreparedStatement prep = null;
	    ResultSet rBD = null;

	    // se recorre el archivo subido, y se van subiendo los datos de
	    // acuerdo a la plantilla
	    if (this.archivoSIRI != null)
	    {

		BufferedReader buffer = null;

		buffer = new BufferedReader(new InputStreamReader(this.archivoSIRI.getInputstream()));

		boolean registrado = registrarPlantillaValores(this.versionSeleccionada.getDetalles().get(0), buffer,
			this.comentariosArchivoSIRI);

		if (registrado)
		{
		    System.out.println("Archivo SIRI registrado exitosamente.");
		    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
			    "Base de Datos Registrada", "La base de datos del SIRI ha sido registrada exitosamente."));
		}

	    }

	    this.basesSiri = utilidades.getBasesSiri();

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}

    }

    public boolean registrarPlantillaValores(Plantilla plan, BufferedReader buffer, String comentarios)
    {

	try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionSIRI();)
	{
	    PreparedStatement prep = null;
	    ResultSet rBD = null;

	    // Primero registramos la BaseSiri
	    boolean registroCorrecto = false;
	    int idBaseSiri = 1;

	    do
	    {
		// Se saca el id máximo de la plantillaanexo
		prep = conexion.prepareStatement(" SELECT MAX(idBaseSiri) FROM basesiri ");

		rBD = prep.executeQuery();

		if (rBD.next())
		{
		    idBaseSiri = (rBD.getInt("MAX(idBaseSiri)") + 1);
		}

		try
		{
		    prep = conexion.prepareStatement(
			    " INSERT INTO basesiri (idBaseSiri, Fecha, Hora, Comentarios) VALUES (?, ?, ?, ?) ");

		    prep.setInt(1, idBaseSiri);

		    java.util.Date fecha = new java.util.Date();
		    prep.setDate(2, new java.sql.Date(fecha.getTime()));
		    prep.setTime(3, new java.sql.Time(fecha.getTime()));

		    prep.setString(4, comentarios);

		    prep.executeUpdate();

		    registroCorrecto = true;

		}
		catch (Exception e)
		{
		    e.printStackTrace();
		    idBaseSiri++;
		}

	    }
	    while (!registroCorrecto);

	    ordenarPosicionesCamposPlantilla(plan.getCampos());

	    boolean lecturanula = false;
	    int nLinea = 1;
	    prep = conexion.prepareStatement(" INSERT INTO plantillasirivalores VALUES (?, ?, ?, ?, ?) ");

	    while (!lecturanula)
	    {

		try
		{

		    String linea = buffer.readLine();

		    if (linea == null)
		    {

			lecturanula = true;
			System.out.println("terminó lectura");

		    }
		    else
		    {

			String subCadenaValor = null;

			int orden = 1;

			for (Campo campo : plan.getCampos())
			{

			    subCadenaValor = linea.substring(campo.getPosInicioCampo() - 1, campo.getPosFinCampo());

			    prep.setInt(1, idBaseSiri);
			    prep.setInt(2, plan.getIdPlantilla());
			    prep.setInt(3, orden);
			    prep.setString(4, subCadenaValor);
			    prep.setInt(5, nLinea);

			    prep.addBatch();

			    orden++;

			}

			System.out.println(nLinea);
		    }

		    nLinea++;

		}
		catch (IOException ex)
		{
		    System.out.println(ex);
		    return false;

		}

	    }

	    prep.executeBatch();

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	    return false;
	}

	return true;

    }

    // MÉTODOS PARA LA SECCION DE CAMPOS
    public void preparaFormularioCampo(int tipo, boolean moverPosicion)
    {
	setEstadoNuevoEdicionCampo(tipo);

	if (tipo == 0)
	{
	    this.setNuevoEdicionCampo(new CampoPlantilla(-1, "", "", 0, 0));
	}
	else if (tipo == 1)
	{
	    this.setNuevoEdicionCampo((CampoPlantilla) this.getCampoSeleccionado().clone());
	}

	if (moverPosicion)
	{
	    RequestContext.getCurrentInstance().scrollTo("formularioNuevoEdicionCampo");

	}
    }

    public void actionRegistrarCampoEnPlantilla()
    {
	actionRegistrarCampo();
	// Se actualiza la lista de campos elegibles para la plantilla

	this.camposDualPlantillaAnexo.setSource(utilidades.getCamposBD());

	RequestContext.getCurrentInstance().execute("PF('dialogoNuevoCampo').hide()");

    }

    public void actionRegistrarCampo()
    {
	try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
	{
	    PreparedStatement prep = null;
	    String titulo = "";
	    String mensaje = "";

	    // Se verifica que no exista un registroSAR100 con el mismo nombre
	    if (this.getNuevoEdicionCampo().getIdCampo() == -1)
	    {
		prep = conexion.prepareStatement(
			" SELECT * FROM campo WHERE Descripcion = ? AND Tipo=? AND Entero=? AND Decimales=?");

	    }
	    else
	    {
		prep = conexion.prepareStatement(
			" SELECT * FROM campo WHERE Descripcion =? AND Tipo=? AND Entero=? AND Decimales=? AND ( Tipo=? AND Entero=? AND Decimales=? AND Descripcion =? ) = FALSE  ");
		prep.setString(5, this.campoSeleccionado.getTipo());
		prep.setInt(6, this.campoSeleccionado.getEntero());
		prep.setInt(7, this.campoSeleccionado.getDecimal());
		prep.setString(8, this.campoSeleccionado.getDescripcion());

	    }

	    prep.setString(1, this.nuevoEdicionCampo.getDescripcion());
	    prep.setString(2, this.nuevoEdicionCampo.getTipo());
	    prep.setInt(3, this.nuevoEdicionCampo.getEntero());
	    prep.setInt(4, this.nuevoEdicionCampo.getDecimal());

	    ResultSet rBD = prep.executeQuery();

	    if (rBD.next())
	    {
		RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_ERROR,
			"Nombre en Uso",
			"El nombre o descripción ingresado ya está siendo utilizado por otro campo. Especifique otro."));
		return;
	    }

	    if (this.getNuevoEdicionCampo().getIdCampo() == -1)
	    {
		int idCampo = 1;
		boolean insercionCorrecta = false;

		do
		{
		    try
		    {
			prep = conexion.prepareStatement(
				"INSERT INTO campo (idCampo, Descripcion, Tipo, Entero, Decimales) VALUES (?, ?, ?, ?, ?) ");
			prep.setInt(1, idCampo);
			prep.setString(2, this.getNuevoEdicionCampo().getDescripcion().trim());
			prep.setString(3, this.getNuevoEdicionCampo().getTipo());
			prep.setInt(4, this.getNuevoEdicionCampo().getEntero());
			prep.setInt(5, this.getNuevoEdicionCampo().getDecimal());

			prep.executeUpdate();

			insercionCorrecta = true;
			titulo = "Campo Creado";
			mensaje = "El campo fue creado exitosamente";
		    }
		    catch (Exception e)
		    {
			idCampo++;
		    }

		}
		while (!insercionCorrecta);

		this.getNuevoEdicionCampo().setIdCampo(idCampo);

	    }
	    else
	    {
		prep = conexion.prepareStatement(
			"UPDATE campo SET Descripcion=?, Tipo=?, Entero=?, Decimales=? WHERE idCampo=? ");

		prep.setString(1, this.getNuevoEdicionCampo().getDescripcion().trim());
		prep.setString(2, this.getNuevoEdicionCampo().getTipo());
		prep.setInt(3, this.getNuevoEdicionCampo().getEntero());
		prep.setInt(4, this.getNuevoEdicionCampo().getDecimal());

		prep.setInt(5, this.getNuevoEdicionCampo().getIdCampo());

		prep.executeUpdate();

		titulo = "Campo Modificado";
		mensaje = "El campo fue modificado exitosamente";

	    }

	    setCamposGenerales(utilidades.getCamposBD());
	    setEstadoNuevoEdicionCampo(-1);
	    FacesContext.getCurrentInstance().addMessage(null,
		    new FacesMessage(FacesMessage.SEVERITY_INFO, titulo, mensaje));

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	    RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_ERROR,
		    "Excepción",
		    "Se ha generado una excepción al momento de guardar o modificar la información del campo. Favor de contactar con el desarrollador del sistema."));
	}
    }

    public void actionEliminarCampo()
    {
	try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
	{
	    PreparedStatement prep = conexion.prepareStatement(" DELETE FROM campo WHERE idCampo=?");

	    prep.setInt(1, this.getCampoSeleccionado().getIdCampo());

	    prep.executeUpdate();

	    setCamposGenerales(utilidades.getCamposBD());
	    setEstadoNuevoEdicionCampo(-1);

	    RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_ERROR,
		    "Campo Eliminado", "El campo se ha eliminado exitosamente del sistema."));
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	    RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_ERROR,
		    "Excepción",
		    "Se ha generado una excepción al momento de eliminar el campo. Favor de contactar con el desarrollador del sistema."));
	}

    }

    // METODOS PARA ANEXOS Y VERSIONES
    public void listenerChangeListaAnexo(AjaxBehaviorEvent evt)
    {
	if (this.getAnexoSeleccionado() != null)
	{
	    this.getAnexoSeleccionado().updateVersiones();
	}

	setEstadoNuevoEdicionAnexo(-1);

	this.setVersionSeleccionada(null);
	this.setDetalleSeleccionado(null);

    }

    public void listenerChangeListaVersionAnexo(AjaxBehaviorEvent evt)
    {

	if (this.versionSeleccionada != null)
	{
	    this.versionSeleccionada.updatePlantillaEncabezado(false);
	    this.versionSeleccionada.updatePlantillasDetalle(false);
	}
	else
	{
	    setEstadoNuevoEdicionAnexo(-1);
	}
    }

    public void listenerChangeListaDetallesVersion(AjaxBehaviorEvent evt)
    {
	setEstadoNuevoEdicionAnexo(-1);
    }

    // Obtiene los vínculos creados para las plantillas seleccionadas (Anexos
    // solamente// tipoPlantilla 1 para encabezado y 2 para detalle
    public void obtieneVinculosCreadosPlantilla(int tipoPlantilla)
    {
	this.tipoPlantillaVinculoEditando = tipoPlantilla;

	if (tipoPlantilla == 1)
	{
	    this.vinculosCreados = utilidades.getVinculosPlantilla(this.versionSeleccionada.getEncabezado());
	}
	else if (tipoPlantilla == 2)
	{
	    this.vinculosCreados = utilidades.getVinculosPlantilla(this.detalleSeleccionado);
	}

	if (this.converterVinculoPlantilla == null)
	{
	    this.converterVinculoPlantilla = new ConverterVinculoPlantilla();
	}

	this.converterVinculoPlantilla.setCatalogo(this.vinculosCreados);

	// cambia el estado para poder visualizar el panel de vinculo solo
	// cuando se oprima en el botón correspondiente
	setEstadoNuevoEdicionAnexo(2);
	this.vinculoSeleccionado = null;

    }

    // modo 0 nuevo, modo 1 edición.
    public void prepararaNuevoEdicionVinculoPlantilla(int modo)
    {

	switch (modo)
	{
	    case 0:
		this.vinculoNuevoEdicion = new VinculoPlantilla(-1, "", null, null);
		break;

	    case 1:
		this.vinculoNuevoEdicion = this.vinculoSeleccionado;
		break;
	}

    }

    // tipoPlantilla 1 para encabezado 2 para detalle
    public void actionCrearNuevoVinculo()
    {
	try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionSIRI();)
	{
	    PreparedStatement prep = null;
	    ResultSet rBD = null;

	    // Se crea el índice cuando no existe dado que es un
	    // nuevo registro
	    boolean registroCorrecto = false;
	    int idVinculoPlantilla = 0;

	    do
	    {
		// Se saca el id máximo de la plantillaanexo
		prep = conexion.prepareStatement(" SELECT MAX(idVinculoPlantilla) FROM siri.vinculoplantilla ");

		rBD = prep.executeQuery();

		if (rBD.next())
		{
		    idVinculoPlantilla = (rBD.getInt("MAX(idVinculoPlantilla)") + 1);
		}

		try
		{
		    prep = conexion.prepareStatement(
			    " INSERT INTO vinculoplantilla ( idVinculoPlantilla, Descripcion, idPlantilla) VALUES ( ?, ?, ? ) ");

		    prep.setInt(1, idVinculoPlantilla);
		    prep.setString(2, "Nuevo Vínculo");

		    if (this.tipoPlantillaVinculoEditando == 1)
		    {
			prep.setInt(3, this.versionSeleccionada.getEncabezado().getIdPlantilla());
		    }
		    else if (this.tipoPlantillaVinculoEditando == 2)
		    {
			prep.setInt(3, this.detalleSeleccionado.getIdPlantilla());

		    }

		    prep.executeUpdate();

		    registroCorrecto = true;

		}
		catch (Exception e)
		{
		    e.printStackTrace();
		    idVinculoPlantilla++;
		}

	    }
	    while (!registroCorrecto);

	    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
		    "Vínculo Creado", "El nuevo vínculo ha sido creado exitosamente."));

	    obtieneVinculosCreadosPlantilla(this.tipoPlantillaVinculoEditando);
	    this.vinculoNuevoEdicion = null;
	    this.vinculoSeleccionado = null;

	    RequestContext.getCurrentInstance().scrollTo("formularioNuevoEdicionAnexo");

	}
	catch (Exception e)
	{
	    RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_ERROR,
		    "Excepción",
		    "Se ha generado una excepción al momento de crear el nuevo vínculo. Favor de contactar con el desarrollador del sistema."));
	}

    }

    public void preparaFormularioAnexo(int tipo, int tipoPlantilla)
    {
	setEstadoNuevoEdicionAnexo(tipo);
	setTipoPlantillaEditando(tipoPlantilla);

	if (tipo == -1)
	{
	    return;
	}

	this.setCamposGenerales(utilidades.getCamposBD());

	if (tipo == 0)
	{
	    TipoPlantilla tipoPlan = null;

	    if (tipoPlantilla == 1)
	    {
		tipoPlan = new TipoPlantilla(1, "Encabezado");
	    }
	    else if (tipoPlantilla == 2)
	    {
		tipoPlan = new TipoPlantilla(2, "Detalle");
	    }

	    this.setNuevoEdicionPlantillaAnexo(new Plantilla(-1, tipoPlan, "", new ArrayList<CampoPlantilla>(), null));

	    this.camposDualPlantillaAnexo = new DualListModel<>(camposGenerales,
		    this.getNuevoEdicionPlantillaAnexo().getCampos());

	}
	else if (tipo == 1)
	{

	    if (tipoPlantilla == 1)
	    {
		this.nuevoEdicionPlantillaAnexo = this.versionSeleccionada.getEncabezado().getClone();

	    }
	    else if (tipoPlantilla == 2)
	    {

		this.nuevoEdicionPlantillaAnexo = this.detalleSeleccionado.getClone();

	    }

	    this.nuevoEdicionPlantillaAnexo.updateCampos();

	    this.camposDualPlantillaAnexo = new DualListModel<>(camposGenerales,
		    this.getNuevoEdicionPlantillaAnexo().getCampos());

	    for (CampoPlantilla campo : this.getNuevoEdicionPlantillaAnexo().getCampos())
	    {

		if (campo.isIndice())
		{
		    this.indicePrincipal = "" + campo.getIdCampo();
		}
	    }
	    /*
	     * //se compara que campos están ya dentro y se descartan de la lista de campos
	     * generales for (CampoPlantilla registroSAR100 :
	     * this.nuevoEdicionPlantillaAnexo.getCampos()) { for (CampoPlantilla cam :
	     * this.getCamposGenerales()) { if (cam.getIdCampo() ==
	     * registroSAR100.getIdCampo()) { this.getCamposGenerales().remove(cam); break;
	     * }
	     * 
	     * } }
	     */
	}
	else if (tipo == 2)
	{

	    // Se saca los vínculos de SAR100 y de SIRI del Vínculo seleccionado
	    // para la plantilla
	    if (this.vinculoSeleccionado == null)
	    {
		return;
	    }

	    this.vinculoSeleccionado.updateVersionesVinculadas();

	    this.vinculoNuevoEdicion = new VinculoPlantilla(this.vinculoSeleccionado.getIdVinculoPlantilla(),
		    this.vinculoSeleccionado.getDescripcion(), null, null);

	    if (this.vinculoSeleccionado.getLayoutVersionSAR100() != null)
	    {
		this.vinculoSeleccionado.getLayoutVersionSAR100().updatePlantillasDetalle(true);
	    }
	    if (this.vinculoSeleccionado.getLayoutVersionSIRI() != null)
	    {
		this.vinculoSeleccionado.getLayoutVersionSIRI().updatePlantillasDetalle(true);
	    }

	    // this.setSarVersionesVincular(utilidades.getLayoutsBD(6));
	    // this.setSiriVersionesVincular(utilidades.getLayoutsBD(5));

	    this.setSarVersionesVincular(utilidades.getLayoutsBD(6));
	    this.setSiriVersionesVincular(utilidades.getLayoutsBD(5));

	    this.getSarVersionesVincular().updateVersiones();
	    this.getSiriVersionesVincular().updateVersiones();

	    if (this.tipoPlantillaVinculoEditando == 1)
	    {
		this.nuevoEdicionPlantillaAnexo = this.versionSeleccionada.getEncabezado().getClone();

	    }
	    else if (this.tipoPlantillaVinculoEditando == 2)
	    {

		this.nuevoEdicionPlantillaAnexo = this.detalleSeleccionado.getClone();

	    }

	    this.nuevoEdicionPlantillaAnexo.updateCampos();
	    this.sarVinculado = this.vinculoSeleccionado.getLayoutVersionSAR100();
	    this.siriVinculado = this.vinculoSeleccionado.getLayoutVersionSIRI();
	    this.indicePrincipal = null;

	    this.nuevoEdicionPlantillaAnexo.updateCamposVinculados(this.vinculoSeleccionado.getIdVinculoPlantilla());

	    if (this.siriVinculado != null && this.siriVinculado.getDetalles() != null)
	    {
		// Ahora recorrer las versiones de sar y de siri para determinar
		// que versión se está utilizando
		for (LayoutVersion version : this.siriVersionesVincular.getVersiones())
		{
		    version.updatePlantillaEncabezado(false);
		    version.updatePlantillasDetalle(false);

		    if (this.tipoPlantillaVinculoEditando == 1)
		    {
			if (version.getEncabezado().getIdPlantilla() == this.siriVinculado.getEncabezado()
				.getIdPlantilla())
			{
			    this.siriVinculado = version;
			    this.siriVinculado.updatePlantillaEncabezado(true);
			}

		    }
		    else if (this.tipoPlantillaVinculoEditando == 2)
		    {

			if (version.getDetalles().get(0).getIdPlantilla() == this.siriVinculado.getDetalles().get(0)
				.getIdPlantilla())
			{
			    this.siriVinculado = version;
			    this.siriVinculado.updatePlantillasDetalle(true);
			}

		    }

		}

	    }

	    if (this.sarVinculado != null && this.sarVinculado.getDetalles() != null)
	    {

		for (LayoutVersion version : this.sarVersionesVincular.getVersiones())
		{
		    version.updatePlantillaEncabezado(false);
		    version.updatePlantillasDetalle(false);

		    if (this.tipoPlantillaVinculoEditando == 1)
		    {
			if (version.getEncabezado().getIdPlantilla() == this.sarVinculado.getEncabezado()
				.getIdPlantilla())
			{
			    this.sarVinculado = version;
			    this.sarVinculado.updatePlantillaEncabezado(true);
			}

		    }
		    else if (this.tipoPlantillaVinculoEditando == 2)
		    {

			if (version.getDetalles().get(0).getIdPlantilla() == this.sarVinculado.getDetalles().get(0)
				.getIdPlantilla())
			{
			    this.sarVinculado = version;
			    this.sarVinculado.updatePlantillasDetalle(true);
			}

		    }

		}

	    }

	}

	if (tipo == 1)
	{
	    reordenaPosicionesPlantilla(getCamposDualPlantillaAnexo());
	}

	RequestContext.getCurrentInstance().scrollTo("formularioNuevoEdicionAnexo");
    }

    // Elimina el vínculo que se encuentre seleccionado
    public void actionEliminarVinculoSeleccionado()
    {
	try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionSIRI();)
	{
	    PreparedStatement prep = null;

	    prep = conexion.prepareStatement(" DELETE FROM anexodatosvinculados WHERE idVinculoPlantilla=? ");
	    prep.setInt(1, this.vinculoSeleccionado.getIdVinculoPlantilla());
	    prep.executeUpdate();

	    prep = conexion.prepareStatement(" DELETE FROM vinculoplantillaconfiguracion WHERE idVinculoPlantilla=? ");
	    prep.setInt(1, this.vinculoSeleccionado.getIdVinculoPlantilla());
	    prep.executeUpdate();

	    prep = conexion.prepareStatement(" DELETE FROM vinculoplantilla WHERE idVinculoPlantilla=? ");
	    prep.setInt(1, this.vinculoSeleccionado.getIdVinculoPlantilla());
	    prep.executeUpdate();

	    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
		    "Vínculo Eliminado", "El vínculo ha sido eliminado exitosamente."));

	    obtieneVinculosCreadosPlantilla(this.tipoPlantillaEditando);
	    this.vinculoNuevoEdicion = null;
	    this.vinculoSeleccionado = null;

	    RequestContext.getCurrentInstance().scrollTo("formularioNuevoEdicionAnexo");

	}
	catch (Exception e)
	{
	    RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_ERROR,
		    "Excepción",
		    "Se ha generado una excepción al momento de eliminar el vínculo. Favor de contactar con el desarrollador del sistema."));
	}

    }

    public void listenerChangeListaVersionSARVincular(AjaxBehaviorEvent evt)
    {
	if (this.sarVinculado != null)
	{
	    if (this.nuevoEdicionPlantillaAnexo.getTipoPlantilla().getIdTipoPlantilla() == 1)
	    {
		this.sarVinculado.updatePlantillaEncabezado(true);
	    }
	    else if (this.nuevoEdicionPlantillaAnexo.getTipoPlantilla().getIdTipoPlantilla() == 2)
	    {
		this.sarVinculado.updatePlantillasDetalle(true);
	    }
	}
    }

    public void listenerChangeListaVersionSIRIVincular(AjaxBehaviorEvent evt)
    {
	if (this.siriVinculado != null)
	{
	    if (this.nuevoEdicionPlantillaAnexo.getTipoPlantilla().getIdTipoPlantilla() == 1)
	    {
		this.siriVinculado.updatePlantillaEncabezado(true);
	    }
	    else if (this.nuevoEdicionPlantillaAnexo.getTipoPlantilla().getIdTipoPlantilla() == 2)
	    {
		this.siriVinculado.updatePlantillasDetalle(true);
	    }
	}

    }

    public void onTransfer(TransferEvent event)
    {
	// Se clonan para evitar que siendo el mismo objeto el convertidor
	// devuelva siempre el mismo con el mismo IdCampo en vez del objeto
	// correcto
	if (event.isAdd())
	{
	    this.camposDualPlantillaAnexo.getSource()
		    .add((CampoPlantilla) ((CampoPlantilla) event.getItems().get(0)).clone());
	}
	else if (event.isRemove())
	{
	    this.camposDualPlantillaAnexo.getSource().remove((CampoPlantilla) event.getItems().get(0));

	}

	reordenaPosicionesPlantilla(getCamposDualPlantillaAnexo());

    }

    public void onReorder()
    {
	reordenaPosicionesPlantilla(getCamposDualPlantillaAnexo());
    }

    public void reordenaPosicionesPlantilla(DualListModel<CampoPlantilla> dualList)
    {

	for (Campo campo : dualList.getSource())
	{
	    campo.setPosInicioCampo(0);
	    campo.setPosFinCampo(0);
	}

	ordenarPosicionesCamposPlantilla(dualList.getTarget());

    }

    public void ordenarPosicionesCamposPlantilla(List<CampoPlantilla> listaCampos)
    {
	int indice = 1;
	int totalposCampo = 0;

	for (Campo campo : listaCampos)
	{
	    campo.setPosInicioCampo(indice);

	    totalposCampo = campo.getEntero() + campo.getDecimal();
	    indice += totalposCampo - 1;

	    campo.setPosFinCampo(indice);

	    indice++;
	}
    }

    public void actionGuardarPlantillaAnexo()
    {
	/*
	 * if (this.indicePrincipal == null) {
	 * RequestContext.getCurrentInstance().showMessageInDialog(new
	 * FacesMessage(FacesMessage.SEVERITY_ERROR, "Índice de Búsqueda",
	 * "Debe definir el índice de búsqueda para comparar los registros.")); return;
	 * }
	 */

	if (this.getCamposDualPlantillaAnexo() == null || this.getCamposDualPlantillaAnexo().getTarget().size() < 1)
	{
	    RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_ERROR,
		    "Sin Campos en Plantilla", "Agrege campos a la plantilla para poder crearse."));
	    return;
	}

	try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
	{
	    PreparedStatement prep = null;
	    ResultSet rBD = null;

	    int idPlantilla = 1;
	    String titulo = "";
	    String mensaje = "";

	    // Se crea el índice de la plantilla cuando no existe dado que es un
	    // nuevo registro
	    switch (this.nuevoEdicionPlantillaAnexo.getIdPlantilla())
	    {

		case -1:

		    boolean registroCorrecto = false;

		    do
		    {
			// Se saca el id máximo de la plantillaanexo
			prep = conexion.prepareStatement(" SELECT MAX(idPlantilla) FROM plantilla ");

			rBD = prep.executeQuery();

			if (rBD.next())
			{
			    idPlantilla = (rBD.getInt("MAX(idPlantilla)") + 1);
			}

			try
			{
			    prep = conexion.prepareStatement(
				    " INSERT INTO plantilla (idPlantilla, idLayout, Version, idTipoPlantilla, Descripcion, Separador) VALUES (?, ?, ?, ?, ?, ?) ");

			    prep.setInt(1, idPlantilla);
			    prep.setInt(2, this.anexoSeleccionado.getIdLayout());
			    prep.setString(3, this.versionSeleccionada.getVersion());
			    prep.setInt(4, this.nuevoEdicionPlantillaAnexo.getTipoPlantilla().getIdTipoPlantilla());
			    prep.setString(5, this.nuevoEdicionPlantillaAnexo.getDescripcion());
			    prep.setString(6, this.nuevoEdicionPlantillaAnexo.getCaracterSeparador());

			    prep.executeUpdate();

			    this.nuevoEdicionPlantillaAnexo.setIdPlantilla(idPlantilla);

			    registroCorrecto = true;

			}
			catch (Exception e)
			{
			    e.printStackTrace();
			    idPlantilla++;
			}

		    }
		    while (!registroCorrecto);

		    titulo = "Plantilla Guardada";
		    mensaje = "La plantilla se ha guardado exitosamente.";

		    break;

		default:

		    prep = conexion.prepareStatement(
			    " UPDATE plantilla SET idLayout=?, Version=?, idTipoPlantilla=?, Descripcion=?, Separador=? WHERE idPlantilla=? ");

		    prep.setInt(1, this.anexoSeleccionado.getIdLayout());
		    prep.setString(2, this.versionSeleccionada.getVersion());
		    prep.setInt(3, this.nuevoEdicionPlantillaAnexo.getTipoPlantilla().getIdTipoPlantilla());
		    prep.setString(4, this.nuevoEdicionPlantillaAnexo.getDescripcion());
		    prep.setString(5, this.nuevoEdicionPlantillaAnexo.getCaracterSeparador());

		    prep.setInt(6, this.nuevoEdicionPlantillaAnexo.getIdPlantilla());

		    prep.executeUpdate();

		    // Se borran todos los campos antes de volver a insertarlos
		    prep = conexion.prepareStatement(" DELETE FROM plantilladatos WHERE idPlantilla=? ");

		    prep.setInt(1, this.nuevoEdicionPlantillaAnexo.getIdPlantilla());

		    prep.executeUpdate();

		    titulo = "Plantilla Modificada";
		    mensaje = "La plantilla se ha modificado exitosamente.";

		    break;

	    }

	    int pos = 1;

	    // Se registran los campos de la plantilla
	    for (CampoPlantilla campo : this.camposDualPlantillaAnexo.getTarget())
	    {

		prep = conexion.prepareStatement(
			" INSERT INTO plantilladatos (idPlantilla, idCampo, Orden, Caracteristicas, indice) VALUES (?, ?, ?, ?, ?) ");

		prep.setInt(1, this.nuevoEdicionPlantillaAnexo.getIdPlantilla());
		prep.setInt(2, campo.getIdCampo());
		prep.setInt(3, pos);
		prep.setString(4, "Sin descripción por proceso de desarrollo");

		if (this.indicePrincipal.length() < 1)
		{
		    prep.setBoolean(5, false);
		}
		else
		{
		    if (campo.getIdCampo() == Integer.parseInt(this.indicePrincipal))
		    {
			prep.setBoolean(5, true);
		    }
		    else
		    {
			prep.setBoolean(5, false);
		    }
		}

		prep.executeUpdate();

		pos++;

	    }

	    RequestContext.getCurrentInstance()
		    .showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_INFO, titulo, mensaje));
	    setAnexoSeleccionado(null);
	    setVersionSeleccionada(null);
	    setEstadoNuevoEdicionAnexo(-1);

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}

    }

    public void preparaDialogNuevoEdicionVersion(int tipoEdicion)
    {
	if (tipoEdicion == 0)
	{
	    this.nuevoEdicionLayoutVersion = new LayoutVersion(-1, "", null);
	}
	else if (tipoEdicion == 1)
	{
	    this.nuevoEdicionLayoutVersion = this.versionSeleccionada.getClone();
	}
    }

    public void actionGuardarNuevaVersion()
    {

	try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
	{

	    PreparedStatement prep = null;
	    ResultSet rBD = null;
	    String titulo = "";
	    String mensaje = "";

	    switch (this.nuevoEdicionLayoutVersion.getIdLayout())
	    {
		case -1:

		    prep = conexion.prepareStatement("SELECT * FROM layoutversion WHERE version=? AND idLayout=?");

		    break;

		default:

		    prep = conexion.prepareStatement(
			    " SELECT  * FROM layoutversion WHERE version=? AND idLayout=?  AND ( version=? AND idLayout=? ) = FALSE");

		    prep.setString(3, this.versionSeleccionada.getVersion());
		    prep.setInt(4, this.anexoSeleccionado.getIdLayout());

		    break;

	    }

	    prep.setString(1, this.nuevoEdicionLayoutVersion.getVersion());
	    prep.setInt(2, this.anexoSeleccionado.getIdLayout());

	    rBD = prep.executeQuery();

	    if (rBD.next())
	    {
		RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_ERROR,
			"Versión en Uso", "La versión ingresada ya se encuentra en uso, especifique otra."));
		return;
	    }

	    // ahora se inserta o se hace el update
	    switch (this.nuevoEdicionLayoutVersion.getIdLayout())
	    {
		case -1:

		    prep = conexion.prepareStatement(
			    " INSERT INTO layoutversion (idLayout, Version, FechaEmision) VALUES (?, ?, ?) ");
		    prep.setInt(1, this.anexoSeleccionado.getIdLayout());
		    prep.setString(2, this.nuevoEdicionLayoutVersion.getVersion());
		    prep.setDate(3, new java.sql.Date(this.nuevoEdicionLayoutVersion.getFechaEmision().getTime()));

		    titulo = "Nueva Versión Guardada";
		    mensaje = "La nueva versión del layout ha sido registrada exitosamente";

		    break;

		default:

		    prep = conexion.prepareStatement(
			    " UPDATE layoutversion SET idLayout=?, Version=?, FechaEmision=? WHERE idLayout=? AND Version=? ");

		    prep.setInt(1, this.nuevoEdicionLayoutVersion.getIdLayout());
		    prep.setString(2, this.nuevoEdicionLayoutVersion.getVersion());
		    prep.setDate(3, new java.sql.Date(this.nuevoEdicionLayoutVersion.getFechaEmision().getTime()));

		    prep.setInt(4, this.anexoSeleccionado.getIdLayout());
		    prep.setString(5, this.versionSeleccionada.getVersion());

		    titulo = "Versión Modificada";
		    mensaje = "Los datos de la versión han sido modificados exitosamente.";

		    break;

	    }

	    prep.executeUpdate();
	    this.anexoSeleccionado.updateVersiones();
	    setVersionSeleccionada(null);
	    setDetalleSeleccionado(null);
	    setEstadoNuevoEdicionAnexo(-1);
	    setEstadoNuevoEdicionCampo(-1);

	    RequestContext.getCurrentInstance()
		    .showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_INFO, titulo, mensaje));

	}
	catch (Exception e)
	{
	    e.printStackTrace();

	}

    }

    public void actionEliminarVersion()
    {
	try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
	{
	    // se verifica que la versión no contenga plantillas
	    PreparedStatement prep = conexion
		    .prepareStatement(" SELECT * FROM plantilla WHERE idLayout=? AND Version=? ");

	    prep.setInt(1, this.anexoSeleccionado.getIdLayout());
	    prep.setString(2, this.versionSeleccionada.getVersion());

	    ResultSet rBD = prep.executeQuery();

	    if (rBD.next())
	    {
		RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_ERROR,
			"Imposible Eliminar",
			"La versión no se puede eliminar ya que contiene plantillas creadas. Elimine primero las plantillas."));
		return;
	    }

	    prep = conexion.prepareStatement(" DELETE FROM layoutversion WHERE idLayout=? AND version=?");

	    prep.setInt(1, this.anexoSeleccionado.getIdLayout());
	    prep.setString(2, this.versionSeleccionada.getVersion());

	    prep.executeUpdate();

	    this.anexoSeleccionado.updateVersiones();
	    setVersionSeleccionada(null);
	    setDetalleSeleccionado(null);
	    setEstadoNuevoEdicionAnexo(-1);
	    setEstadoNuevoEdicionCampo(-1);

	    RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_INFO,
		    "Versión Eliminada", "La versión se ha eliminado exitosamente."));

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }

    public void eliminaVinculoCampo(CampoPlantilla campoVinculando)
    {
	campoVinculando.getCampoVinculado().reinciaCampoValoresDefecto();
    }

    // METODOS PARA VINCULACION
    // tipoDialogo 0 para sar y 1 para SIRI
    public void preparaDialogoVincularCampo(int tipoDialogo, CampoPlantilla campoVinculando)
    {
	setTipoDoialgoVinculando(tipoDialogo);
	this.setCampoVinculando(campoVinculando);

	if (tipoDialogo == 0)
	{

	    if (this.tipoPlantillaEditando == 1)
	    {
		this.camposAElegirAVincular = this.sarVinculado.getEncabezado().getCampos();

	    }
	    else if (this.tipoPlantillaEditando == 2)
	    {
		this.camposAElegirAVincular = this.sarVinculado.getDetalles().get(0).getCampos();

	    }

	}
	else if (tipoDialogo == 1)
	{

	    if (this.tipoPlantillaEditando == 1)
	    {
		this.camposAElegirAVincular = this.siriVinculado.getEncabezado().getCampos();

	    }
	    else if (this.tipoPlantillaEditando == 2)
	    {
		this.camposAElegirAVincular = this.siriVinculado.getDetalles().get(0).getCampos();

	    }

	}

    }

    // tipoDialogo 0 para sar y 1 para SIRI
    public void selectTablaGuardarCampo(SelectEvent evt)
    {
	if (this.tipoDoialgoVinculando == 0)
	{
	    if (this.tipoPlantillaEditando == 1)
	    {
		this.getCampoVinculando().setCampoVinculado(new CampoVinculado(
			this.sarVinculado.getEncabezado().getIdPlantilla(), this.campoAVincularSeleccionado.getOrden(),
			this.sarVinculado.getEncabezado().getDescripcion() + " - " + this.sarVinculado.getVersion(),
			this.campoAVincularSeleccionado));

	    }
	    else if (this.tipoPlantillaEditando == 2)
	    {
		this.getCampoVinculando()
			.setCampoVinculado(new CampoVinculado(this.sarVinculado.getDetalles().get(0).getIdPlantilla(),
				this.campoAVincularSeleccionado.getOrden(),
				this.sarVinculado.getDetalles().get(0).getDescripcion() + " - "
					+ this.sarVinculado.getVersion(),
				this.campoAVincularSeleccionado));

	    }

	}
	else if (this.tipoDoialgoVinculando == 1)
	{
	    if (this.tipoPlantillaEditando == 1)
	    {
		this.getCampoVinculando().setCampoVinculado(new CampoVinculado(
			this.siriVinculado.getEncabezado().getIdPlantilla(), this.campoAVincularSeleccionado.getOrden(),
			this.siriVinculado.getEncabezado().getDescripcion() + " - " + this.siriVinculado.getVersion(),
			this.campoAVincularSeleccionado));

	    }
	    else if (this.tipoPlantillaEditando == 2)
	    {
		this.getCampoVinculando()
			.setCampoVinculado(new CampoVinculado(this.siriVinculado.getDetalles().get(0).getIdPlantilla(),
				this.campoAVincularSeleccionado.getOrden(),
				this.siriVinculado.getDetalles().get(0).getDescripcion() + " - "
					+ this.siriVinculado.getVersion(),
				this.campoAVincularSeleccionado));

	    }

	}

    }

    public void onCellEditValorPorDefecto(CellEditEvent evt)
    {

    }

    public void actionGuardarCamposVinculados()
    {

	// Primero se borra la vinculación de todos los campos para la
	// plantilla, evitando residuos
	PreparedStatement prep = null;
	ResultSet rBD = null;

	try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionSIRI();)
	{

	    if (this.tipoPlantillaVinculoEditando == 2 && (this.sarVinculado == null || this.siriVinculado == null))
	    {
		FacesContext.getCurrentInstance().addMessage(null,
			new FacesMessage(FacesMessage.SEVERITY_ERROR, "Versiones a Vincular",
				"Se debe seleccionar una versión de SIRI y SAR para poder vincular correctamente."));

		return;

	    }

	    if (this.tipoPlantillaVinculoEditando == 2)
	    {
		// Se valida que no haya ya un vínculo creado con ambas
		// versiones de
		// sar y de siri
		prep = conexion.prepareStatement(
			" SELECT * FROM vinculoplantilla WHERE idPlantilla=? AND idPlantillaSAR100=? AND idPlantillaSIRI=? AND idVinculoPlantilla NOT IN(?) ");

		prep.setInt(1, this.nuevoEdicionPlantillaAnexo.getIdPlantilla());
		prep.setInt(2, this.sarVinculado.getDetalles().get(0).getIdPlantilla());
		prep.setInt(3, this.siriVinculado.getDetalles().get(0).getIdPlantilla());
		prep.setInt(4, this.vinculoSeleccionado.getIdVinculoPlantilla());

		rBD = prep.executeQuery();

		if (rBD.next())
		{
		    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
			    "Versiones Ya Vinculadas", "La versión del SAR100 y el SIRI ya han sido vinculadas."));

		    prep.close();
		    return;

		}

		prep.close();

	    }

	    prep = conexion.prepareStatement(" DELETE FROM anexodatosvinculados WHERE idVinculoPlantilla=? ");

	    prep.setInt(1, this.vinculoSeleccionado.getIdVinculoPlantilla());

	    prep.executeUpdate();

	    prep.close();

	    prep = conexion
		    .prepareStatement(" DELETE FROM siri.vinculoplantillaconfiguracion WHERE idVinculoPlantilla=? ");

	    prep.setInt(1, this.vinculoSeleccionado.getIdVinculoPlantilla());

	    prep.executeUpdate();
	    prep.close();

	    // Actualiza el nombre del vínculo
	    prep = conexion.prepareStatement(
		    "UPDATE siri.vinculoplantilla SET Descripcion=?, idPlantillaSAR100=?, idPlantillaSIRI=? WHERE idVinculoPlantilla=?");
	    prep.setString(1, this.vinculoNuevoEdicion.getDescripcion());

	    if (this.tipoPlantillaVinculoEditando == 2)
	    {
		prep.setInt(2, this.sarVinculado.getDetalles().get(0).getIdPlantilla());
		prep.setInt(3, this.siriVinculado.getDetalles().get(0).getIdPlantilla());

	    }
	    else if (this.tipoPlantillaVinculoEditando == 1)
	    {
		prep.setNull(2, Types.INTEGER);
		prep.setNull(3, Types.INTEGER);
	    }

	    prep.setInt(4, this.vinculoNuevoEdicion.getIdVinculoPlantilla());

	    prep.executeUpdate();
	    prep.close();

	    // Se recorre los campos y se va realizando la inserción en la tabla
	    // de vinculación de acuerdo a su valor correspondiente
	    for (CampoPlantilla campo : this.nuevoEdicionPlantillaAnexo.getCampos())
	    {

		if (campo.getCampoVinculado().getValorPorDefecto() == null
			|| campo.getCampoVinculado().getValorPorDefecto().trim().length() < 1)
		{

		    if (campo.getCampoVinculado().getIdPlantilla() == -1
			    || campo.getCampoVinculado().getIdPlantilla() == 0)
		    {
			prep = conexion.prepareStatement(
				" INSERT INTO anexodatosvinculados (idVinculoPlantilla, idPlantilla, Orden, ValorDefecto) VALUES (?, ?, ?, ?) ");

			prep.setString(4, "-1");

		    }
		    else
		    {
			prep = conexion.prepareStatement(
				" INSERT INTO anexodatosvinculados (idVinculoPlantilla, idPlantilla, Orden, idPlantillaVinculo, OrdenVinculo) VALUES (?, ?, ?, ?, ?) ");

			prep.setInt(4, campo.getCampoVinculado().getIdPlantilla());
			prep.setInt(5, campo.getCampoVinculado().getOrden());

		    }

		}
		else
		{
		    prep = conexion.prepareStatement(
			    " INSERT INTO anexodatosvinculados (idVinculoPlantilla, idPlantilla, Orden, ValorDefecto) VALUES (?, ?, ?, ?) ");

		    prep.setString(4, campo.getCampoVinculado().getValorPorDefecto());

		}

		prep.setInt(1, this.vinculoNuevoEdicion.getIdVinculoPlantilla());
		prep.setInt(2, this.nuevoEdicionPlantillaAnexo.getIdPlantilla());
		prep.setInt(3, campo.getOrden());

		prep.executeUpdate();

		// Se actualiza si el campo debe de incluirse en retiro o en
		// vivienda

		prep = conexion.prepareStatement(
			"INSERT INTO siri.vinculoplantillaconfiguracion( idVinculoPlantilla, idPlantilla, Orden, Vivienda, Retiro) VALUES ( ?, ?, ?, ?, ?)");

		prep.setInt(1, this.vinculoNuevoEdicion.getIdVinculoPlantilla());
		prep.setInt(2, this.nuevoEdicionPlantillaAnexo.getIdPlantilla());
		prep.setInt(3, campo.getOrden());
		prep.setBoolean(4, campo.isVivienda());
		prep.setBoolean(5, campo.isRetiro());

		prep.executeUpdate();
		/*
		 * prep = conexion.prepareStatement(
		 * "UPDATE webrh.plantilladatos SET Vivienda=?, Retiro=? WHERE idPlantilla=? AND Orden=? "
		 * ); prep.setBoolean(1, campo.isVivienda()); prep.setBoolean(2,
		 * campo.isRetiro());
		 * 
		 * if (this.tipoPlantillaEditando == 1) { prep.setInt(3,
		 * this.versionSeleccionada.getEncabezado().getIdPlantilla()); } else if
		 * (this.tipoPlantillaEditando == 2) { prep.setInt(3,
		 * this.detalleSeleccionado.getIdPlantilla()); }
		 * 
		 * prep.setInt(4, campo.getOrden());
		 * 
		 * prep.executeUpdate();
		 */

	    }

	    setEstadoNuevoEdicionAnexo(-1);

	    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
		    "Campos Vinculados", "Los campos de la plantilla se han vinculado exitosamente."));

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}

    }

    private PlantillaAnexoGenerado anexoGenerado;

    // MÉTODOS DE USO PARA LA GENERACIÓN DE ANEXOS
    // Versión Seleccionada, representa la plantilla de anexo que se utilizará
    // para la generación del
    // plantillaBimestre, representa el archivo de SAR100 que se utilizará
    public void actionAnalizarAnexo()
    {

	// Se prepara el AnexoSAR100 con su versión correcta
	preparaLayoutAnexoSAR100(true);

	// Ahora se obtienen los registros del catálogo del SIRI
	if (this.baseSeleccionada.getPlantilla() == null)
	{
	    this.baseSeleccionada.updatePlantilla();

	}

	// Se determina cuál es el vínculo que se debe de utilizar para generar
	// el anexo

	// PLANTILLA DEL ANEXO A GENERARSE CON LOS CAMPOS VINCULADOS
	this.detalleSeleccionado.updateCampos();

	if (!this.detalleSeleccionado.updateCamposVinculados(this.baseSeleccionada.getPlantilla().getIdPlantilla(),
		this.plantillaBimestre.getPlantillaDetalles().getIdPlantilla()))
	{
	    FacesContext.getCurrentInstance().addMessage(null,
		    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Sin Vínculo",
			    "No existe ningún vínculo para poder generar anexos entre la versión de SIRI y SAR100"));
	    return;
	}

	// Se obtienen los índices de búsqueda de las 3 plantillas, anexo,
	// sar100 y siri
	setIndiceBusquedaAnexo(this.detalleSeleccionado.getCampoIndice());
	setIndiceBusquedaSAR100(this.plantillaBimestre.getPlantillaDetalles().getCampoIndice());
	setIndicebusquedaSIRI(this.baseSeleccionada.getPlantilla().getPlantilla().getCampoIndice());

	// Una vez que se han obtenido todos los datos se procesa la información
	// y se realizan las comparaciones
	// Se crea el objeto que contiene el resultado del anexo que se está
	// generando
	setAnexoGenerado(new PlantillaAnexoGenerado(this.anexoSeleccionado.getIdLayout(),
		this.anexoSeleccionado.getDescripcion(), this.detalleSeleccionado.getClone(),
		Integer.parseInt(this.añoSeleccionado), Integer.parseInt(this.bimestreSelec),
		this.plazaSeleccionada.getDescripcionPlaza(), this.unidadSeleccionada.getDescripcion()));

	// Se actualiza la estructura de campos de la tabla que mostrará los
	// resultados
	updateColumnas(getAnexoGenerado().getPlantillaEstructura());

	int idRegistro = 0;
	CorrectorCURP correctorCURP = new CorrectorCURP();

	String valorIndiceSAR;
	PlantillaRegistro registroSIRI = null;

	// se obtienen los campos institucionales que traen los valores por
	// defecto
	// Estos campos solamente se utilizan cuando el campo no tiene valor por
	// defecto y no está vinculado a ninguna plantilla
	this.institucion = new Institucion();
	this.institucion.getCamposInstitucionales();
	this.institucion.updatePlantillaAhorroSolidario();

	BigDecimal retiro = new BigDecimal("0.00");
	BigDecimal vivienda = new BigDecimal("0.00");
	BigDecimal cesantiaPatron = new BigDecimal("0.00");
	BigDecimal cesantiaTrabajador = new BigDecimal("0.00");
	BigDecimal ahorroTrabajador = new BigDecimal("0.00");
	BigDecimal ahorroPatron = new BigDecimal("0.00");
	BigDecimal totalSueldosCV = new BigDecimal("0.00");
	BigDecimal totalSueldosVivienda = new BigDecimal("0.00");
	int nCasosAhorroSolidario = 0;

	// Variable que se reutiliza para verificar si los registros tienen
	// sueldo, y en caso de no tenerlo rechazarlo
	String importe = "";

	try
	{

	    if (this.anexoSeleccionado.getDescripcion().indexOf("Anexo A") > -1)
	    {
		// Baja de trabajadores
		if (this.detalleSeleccionado.getDescripcion().indexOf("Baja") > -1)
		{

		    if (tipoAnexoARealizar.equals("bimestre"))
		    {
			// Se recorre cada registro del SAR100
			for (PlantillaRegistro registroSAR100 : this.plantillaBimestre.getRegistros())
			{
			    valorIndiceSAR = registroSAR100.getValorEnCampo(getIndiceBusquedaSAR100().getOrden());
			    curpCorregido = correctorCURP.getCURPCorreguido(valorIndiceSAR);

			    if (curpCorregido != null)
			    {
				valorIndiceSAR = curpCorregido;
			    }

			    // Se obtiene el registro coincidente dentro del
			    // SIRI
			    registroSIRI = this.baseSeleccionada.getPlantilla()
				    .findRegistro(getIndicebusquedaSIRI().getOrden(), valorIndiceSAR);

			    // si se están obteniendo los registros de los
			    // trabajadores nuevos y ya se encuentra en el SIRI
			    // se salta al siguiente registro
			    if (registroSIRI == null)
			    {
				continue;
			    }

			    if (!registroSAR100.tieneSueldo())
			    {

				/*
				 * for (CampoPlantilla campo : registroSAR100.getPlantilla().getCampos()) {
				 * System.out.print(campo.getValor()); }
				 * 
				 * System.out.println("");
				 */

				continue;

			    }

			    importe = "";

			    String valorIndiceSIRI = registroSIRI.getValorEnCampo(getIndicebusquedaSIRI().getOrden());

			    // Se crea un registro para añadir al anexo
			    PlantillaRegistro registroAnexoAñadiendo = new PlantillaRegistro(idRegistro,
				    getAnexoGenerado().getPlantillaEstructura().getClone());

			    for (CampoPlantilla campoAnexo : registroAnexoAñadiendo.getPlantilla().getCampos())
			    {
				CampoVinculado campoVinculado = campoAnexo.getCampoVinculado();

				if (registroSIRI.getPlantilla().getIdPlantilla() == campoVinculado.getIdPlantilla())
				{
				    campoAnexo.setValor(registroSIRI.getValorEnCampo(campoVinculado.getOrden()));
				}
				else
				{
				    // verifica que el valor final se añada
				    // mediante
				    // un comando abreviado, en caso contrario
				    // inserta el valor por defecto tal cual
				    if (!verificaComandosAbreviados(campoAnexo, valorIndiceSIRI, null))
				    {
					if (campoVinculado.getValorPorDefecto() != null
						&& campoVinculado.getValorPorDefecto().length() > 0)
					{
					    campoAnexo.setValor(campoVinculado.getValorPorDefecto());
					}
					else
					{
					    // cuando el campo no tiene valor
					    // por
					    // defecto, es entonces que se
					    // verifica
					    // si el campo tiene valor como
					    // campo
					    // institucional
					    institucion.getValorCoincidente(campoAnexo);
					}

				    }

				}

				// Se ajusta correctamente la longitud del
				// campo, ya
				// que el anexo final puede tener diferencias de
				// longitud contra el origen de los datos
				ajustaLongitudCampo(campoAnexo);

				if (campoAnexo.getDescripcion().contains("Sueldo"))
				{
				    Double importeD = Double.parseDouble(campoAnexo.getValor());

				    if (importeD > 0.00)
				    {
					importe = campoAnexo.getValor();
				    }

				}

			    }

			    getAnexoGenerado().getRegistrosDetalle().add(registroAnexoAñadiendo);
			    idRegistro++;

			}

		    }
		    else if (tipoAnexoARealizar.equals("general"))
		    {
			for (PlantillaRegistro rSIRI : this.baseSeleccionada.getPlantilla().getRegistros())
			{
			    // Se crea un registro para añadir al anexo
			    PlantillaRegistro registroAnexoAñadiendo = new PlantillaRegistro(idRegistro,
				    getAnexoGenerado().getPlantillaEstructura().getClone());

			    String valorIndiceSIRI = rSIRI.getValorEnCampo(getIndicebusquedaSIRI().getOrden());

			    importe = "";

			    // Se recorren todos los campos de la plantilla del
			    // registro que se va a añadir para obtener los
			    // valores
			    // de cada campo
			    // de acuerdo a la plantilla siri o sar a la que
			    // estén
			    // vinculados
			    for (CampoPlantilla campoAnexo : registroAnexoAñadiendo.getPlantilla().getCampos())
			    {
				CampoVinculado campoVinculado = campoAnexo.getCampoVinculado();

				if (rSIRI.getPlantilla().getIdPlantilla() == campoVinculado.getIdPlantilla())
				{
				    campoAnexo.setValor(rSIRI.getValorEnCampo(campoVinculado.getOrden()));
				}
				else
				{
				    // verifica que el valor final se añada
				    // mediante
				    // un comando abreviado, en caso contrario
				    // inserta el valor por defecto tal cual
				    if (!verificaComandosAbreviados(campoAnexo, valorIndiceSIRI, null))
				    {
					if (campoVinculado.getValorPorDefecto() != null
						&& campoVinculado.getValorPorDefecto().length() > 0)
					{
					    campoAnexo.setValor(campoVinculado.getValorPorDefecto());
					}
					else
					{
					    // cuando el campo no tiene valor
					    // por
					    // defecto, es entonces que se
					    // verifica
					    // si el campo tiene valor como
					    // campo
					    // institucional
					    institucion.getValorCoincidente(campoAnexo);
					}

				    }

				}

				// Se ajusta correctamente la longitud del
				// campo, ya
				// que el anexo final puede tener diferencias de
				// longitud contra el origen de los datos
				ajustaLongitudCampo(campoAnexo);

				if (campoAnexo.getDescripcion().contains("Sueldo"))
				{
				    Double importeD = Double.parseDouble(campoAnexo.getValor());

				    if (importeD > 0.00)
				    {
					importe = campoAnexo.getValor();
				    }

				}

			    }

			    getAnexoGenerado().getRegistrosDetalle().add(registroAnexoAñadiendo);
			    idRegistro++;

			}

		    }

		}
		else if (this.detalleSeleccionado.getDescripcion().contains("Alta"))
		{
		    PlantillaRegistro registroAnexoAñadiendo = null;
		    CampoVinculado campoVinculado = null;

		    if (tipoAnexoARealizar.equals("nuevos") || tipoAnexoARealizar.equals("bimestre")
			    || tipoAnexoARealizar.equals("catalogo"))
		    {

			// Se recorre cada registro del SAR100
			for (PlantillaRegistro registroSAR100 : this.plantillaBimestre.getRegistros())
			{

			    valorIndiceSAR = registroSAR100.getValorEnCampo(getIndiceBusquedaSAR100().getOrden());
			    curpCorregido = correctorCURP.getCURPCorreguido(valorIndiceSAR);

			    if (curpCorregido != null)
			    {
				valorIndiceSAR = curpCorregido;
			    }

			    // Se obtiene el registro coincidente dentro del
			    // SIRI
			    registroSIRI = this.baseSeleccionada.getPlantilla()
				    .findRegistro(getIndicebusquedaSIRI().getOrden(), valorIndiceSAR);

			    // si se están obteniendo los registros de los
			    // trabajadores nuevos y ya se encuentra en el SIRI
			    // se salta al siguiente registro
			    if (tipoAnexoARealizar.equals("nuevos") && registroSIRI != null)
			    {
				continue;
			    }
			    else if (tipoAnexoARealizar.equals("catalogo") && registroSIRI == null)
			    {
				// si se están generando únicamente los
				// registros que hay en catálogo ignora cuando
				// no hay registro coincidente en SIRI
				continue;
			    }

			    // Se crea un registro para añadir al anexo
			    registroAnexoAñadiendo = new PlantillaRegistro(idRegistro,
				    getAnexoGenerado().getPlantillaEstructura().getClone());

			    if (!registroSAR100.tieneSueldo())
			    {
				/*
				 * for (CampoPlantilla campo : registroSAR100.getPlantilla().getCampos()) {
				 * System.out.print(campo.getValor()); }
				 * 
				 * System.out.println("");
				 */
				continue;
			    }

			    importe = "";
			    boolean valorDefinido = false;

			    // Se recorren todos los campos de la plantilla
			    // del
			    // registro que se va a añadir para obtener los
			    // valores de cada campo
			    // de acuerdo a la plantilla siri o sar a la que
			    // estén vinculados
			    for (CampoPlantilla campoAnexo : registroAnexoAñadiendo.getPlantilla().getCampos())
			    {
				campoVinculado = campoAnexo.getCampoVinculado();

				// solo en caso de pedir el tipo de
				// movimiento se verifica con los comandos
				// abreviados
				if (campoAnexo.getDescripcion().contains("CURP"))
				{
				    // añadido para que quede el CURP
				    // correcto cuando hay una corrección ya
				    // establecida en el sistema
				    if (curpCorregido != null)
				    {
					campoAnexo.setValor(curpCorregido);
				    }
				    else
				    {
					campoAnexo.setValor(registroSAR100.getValorEnCampo(campoVinculado.getOrden()));
				    }
				}
				else if (campoAnexo.getDescripcion().contains("Sueldo"))
				{
				    // En caso de ser trabajadores nuevos
				    // siempre se va a poner el sueldo desde el
				    // sar100
				    if (tipoAnexoARealizar.equals("nuevos"))
				    {
					campoAnexo.setValor(registroSAR100
						.getValorPorDescripcion(campoVinculado.getCampo().getDescripcion()));
				    }
				    else
				    {
					switch (this.origenSueldo)
					{
					    case "siri":

						if (registroSIRI == null)
						{
						    campoAnexo.setValor(registroSAR100.getValorPorDescripcion(
							    campoVinculado.getCampo().getDescripcion()));
						}
						else
						{
						    campoAnexo.setValor(registroSIRI.getValorPorDescripcion(
							    campoVinculado.getCampo().getDescripcion()));
						}

						break;

					    case "sar100":

						campoAnexo.setValor(registroSAR100.getValorPorDescripcion(
							campoVinculado.getCampo().getDescripcion()));
						break;

					}

				    }
				}
				else
				{
				    valorDefinido = false;

				    if (registroSIRI != null)
				    {
					if (campoAnexo.getDescripcion().toLowerCase().contains("paterno")
						|| campoAnexo.getDescripcion().toLowerCase().contains("materno")
						|| campoAnexo.getDescripcion().toLowerCase().contains("nombre")
						|| campoAnexo.getDescripcion().toLowerCase().contains("fecha de nac")
						|| campoAnexo.getDescripcion().toLowerCase().contains("entidad de nac")
						|| campoAnexo.getDescripcion().toLowerCase().contains("sexo")
						|| campoAnexo.getDescripcion().toLowerCase().contains("domicil")
						|| campoAnexo.getDescripcion().toLowerCase().contains("colonia")
						|| campoAnexo.getDescripcion().toLowerCase().contains("poblaci")
						|| campoAnexo.getDescripcion().toLowerCase()
							.contains("fecha de ingreso")
						|| campoAnexo.getDescripcion().toLowerCase()
							.contains("fecha desde la que cotiza")
						|| campoAnexo.getDescripcion().toLowerCase().contains("digo postal")
						|| campoAnexo.getDescripcion().toLowerCase()
							.contains("entidad federativa"))
					{
					    campoAnexo.setValor(registroSIRI
						    .getValorPorDescripcionContains(campoAnexo.getDescripcion()));
					    valorDefinido = true;
					}

				    }

				    if (!valorDefinido)
				    {
					// En última instancia si el campo
					// vinculado corresponde a la misma
					// plantilla del SAR100 se pone el valor
					// del registro de SAR100 de acuerdo a
					// su posición en la plantilla
					if (registroSAR100.getPlantilla().getIdPlantilla() == campoVinculado
						.getIdPlantilla())
					{
					    campoAnexo.setValor(
						    registroSAR100.getValorEnCampo(campoVinculado.getOrden()));
					}
					else
					{
					    // En caso contrario se identifica
					    // si es el vínculo es hacia la
					    // plantilla de SIRI
					    if (registroSIRI != null)
					    {

						if (registroSIRI.getPlantilla().getIdPlantilla() == campoVinculado
							.getIdPlantilla())
						{
						    campoAnexo.setValor(
							    registroSIRI.getValorEnCampo(campoVinculado.getOrden()));
						}
					    }
					}
				    }

				}

				if (campoAnexo.getValor() == null || campoAnexo.getValor().length() < 1)
				{
				    // verifica que el valor final se añada
				    // mediante un comando abreviado, en
				    // caso
				    // contrario inserta el valor por
				    // defecto
				    // tal cual
				    if (!verificaComandosAbreviados(campoAnexo, valorIndiceSAR,
					    registroAnexoAñadiendo.getValorPorDescripcionContains("RCV")))
				    {
					if (campoVinculado.getValorPorDefecto() != null
						&& campoVinculado.getValorPorDefecto().length() > 0)
					{

					    campoAnexo.setValor(campoVinculado.getValorPorDefecto());
					}
					else
					{
					    // cuando el campo no tiene
					    // valor
					    // por defecto, es entonces que
					    // se
					    // verifica si el campo tiene
					    // valor
					    // como campo institucional
					    institucion.getValorCoincidente(campoAnexo);
					}

				    }

				}

				// Se ajusta correctamente la longitud del
				// campo, ya que el anexo final puede tener
				// diferencias de longitud contra el origen
				// de
				// los datos
				ajustaLongitudCampo(campoAnexo);

			    }

			    getAnexoGenerado().getRegistrosDetalle().add(registroAnexoAñadiendo);
			    idRegistro++;

			}

		    }
		    else
		    {

			if (tipoAnexoARealizar.equals("general"))
			{

			}
			else if (tipoAnexoARealizar.equals("seleccion"))
			{

			}

			for (PlantillaRegistro rSIRI : this.baseSeleccionada.getPlantilla().getRegistros())
			{
			    // Se crea un registro para añadir al anexo
			    registroAnexoAñadiendo = new PlantillaRegistro(idRegistro,
				    getAnexoGenerado().getPlantillaEstructura().getClone());

			    importe = "";

			    // Se recorren todos los campos de la plantilla del
			    // registro que se va a añadir para obtener los
			    // valores
			    // de cada campo
			    // de acuerdo a la plantilla siri o sar a la que
			    // estén
			    // vinculados
			    for (CampoPlantilla campoAnexo : registroAnexoAñadiendo.getPlantilla().getCampos())
			    {
				if (campoAnexo.getDescripcion().toLowerCase().contains("tipo de mov"))
				{
				    campoAnexo.setValor("A");
				}
				else
				{
				    campoAnexo.setValor(
					    rSIRI.getValorPorDescripcionContains(campoAnexo.getDescripcion()));
				}

				ajustaLongitudCampo(campoAnexo);

			    }

			    getAnexoGenerado().getRegistrosDetalle().add(registroAnexoAñadiendo);
			    idRegistro++;

			}

		    }

		}

	    }
	    else if (this.anexoSeleccionado.getDescripcion().contains("Anexo B"))
	    {

		// Se recorre cada registro del SAR100
		for (PlantillaRegistro registroSAR100 : this.plantillaBimestre.getRegistros())
		{

		    valorIndiceSAR = registroSAR100.getValorEnCampo(getIndiceBusquedaSAR100().getOrden());
		    curpCorregido = correctorCURP.getCURPCorreguido(valorIndiceSAR);

		    if (curpCorregido != null)
		    {
			valorIndiceSAR = curpCorregido;
		    }

		    // Se obtiene el registro coincidente dentro del SIRI
		    registroSIRI = this.baseSeleccionada.getPlantilla().findRegistro(getIndicebusquedaSIRI().getOrden(),
			    valorIndiceSAR);

		    if (registroSIRI != null)
		    {

			// Se crea un registro para añadir al anexo
			PlantillaRegistro registroAnexoAñadiendo = new PlantillaRegistro(idRegistro,
				getAnexoGenerado().getPlantillaEstructura().getClone());

			if (!registroSAR100.tieneSueldo())
			{
			    continue;
			}

			// El campo se utiliza para interceptar cualquier
			// importe en la generación del anexo b de retiro
			// dado que todos los importes son siempre iguales da
			// igual tomar cualquiera
			// Si esto cambia se deberá de cambiar la lógica
			importe = "";

			// Se recorren todos los campos de la plantilla del
			// registro que se va a añadir para obtener los valores
			// de cada campo
			// de acuerdo a la plantilla siri o sar a la que estén
			// vinculados
			boolean calculoSueldo = false;

			for (CampoPlantilla campoAnexo : registroAnexoAñadiendo.getPlantilla().getCampos())
			{
			    CampoVinculado campoVinculado = campoAnexo.getCampoVinculado();

			    switch (this.tipoPagoRealizar)
			    {
				case "retiro":

				    if (!campoAnexo.isRetiro())
				    {
					campoAnexo.setValor(getRellenoCampoVacio(campoAnexo));

					continue;
				    }

				    break;

				case "vivienda":

				    if (!campoAnexo.isVivienda())
				    {
					campoAnexo.setValor(getRellenoCampoVacio(campoAnexo));

					continue;
				    }

				    break;

			    }

			    // Es un campo de siri
			    if (this.baseSeleccionada.getPlantilla().getIdPlantilla() == campoVinculado
				    .getIdPlantilla())
			    {
				campoAnexo.setValor("Valor encontrado del campo");
				campoAnexo.setValor(registroSIRI.getValorEnCampo(campoVinculado.getOrden()));

			    }
			    else if (registroSAR100.getPlantilla().getIdPlantilla() == campoVinculado.getIdPlantilla())
			    {
				campoAnexo.setValor(registroSAR100.getValorEnCampo(campoVinculado.getOrden()));
			    }
			    else
			    {
				// verifica que el valor final se añada mediante
				// un comando abreviado, en caso contrario
				// inserta el valor por defecto tal cual
				if (!verificaComandosAbreviados(campoAnexo, valorIndiceSAR, importe))
				{
				    if (campoVinculado.getValorPorDefecto() != null
					    && campoVinculado.getValorPorDefecto().length() > 0)
				    {
					campoAnexo.setValor(campoVinculado.getValorPorDefecto());
				    }
				    else
				    {// cuando el campo no tiene valor por
				     // defecto, es entonces que se verifica
				     // si el campo tiene valor como campo
				     // institucional
					institucion.getValorCoincidente(campoAnexo);
				    }

				}

			    }

			    // Se ajusta correctamente la longitud del campo, ya
			    // que el anexo final puede tener diferencias de
			    // longitud contra el origen de los datos
			    ajustaLongitudCampo(campoAnexo);

			    if (campoAnexo.getDescripcion().contains("Sueldo") && !calculoSueldo)
			    {
				importe = campoAnexo.getValor();

				BigDecimal suma = new BigDecimal(campoAnexo.getValor());
				suma = suma.divide(new BigDecimal("100"));

				System.out.print(registroSAR100.getValorPorDescripcionContains("CURP")
					+ " El sueldo es " + utilidades.formato.format(suma) + "	");

				if (campoAnexo.getDescripcion().contains("CV"))
				{
				    totalSueldosCV = totalSueldosCV.add(suma);

				    cesantiaPatron = cesantiaPatron.add(
					    suma.multiply(factorCesantiaPatron).setScale(2, BigDecimal.ROUND_HALF_UP));
				    cesantiaTrabajador = cesantiaTrabajador.add(suma.multiply(factorCesantiaTrabajador)
					    .setScale(2, BigDecimal.ROUND_HALF_UP));

				    retiro = retiro
					    .add(suma.multiply(factorRetiro).setScale(2, BigDecimal.ROUND_HALF_UP));

				    System.out.print("Retiro: "
					    + suma.multiply(factorRetiro).setScale(2, BigDecimal.ROUND_HALF_UP)
					    + "	");
				    System.out.print("Cesantía Patrón: "
					    + suma.multiply(factorCesantiaPatron).setScale(2, BigDecimal.ROUND_HALF_UP)
					    + "	");
				    System.out.print("Cesantía Trabajador: " + suma.multiply(factorCesantiaTrabajador)
					    .setScale(2, BigDecimal.ROUND_HALF_UP) + "	");

				}
				else if (campoAnexo.getDescripcion().contains("ivienda"))
				{
				    totalSueldosVivienda = totalSueldosVivienda.add(suma);
				    vivienda = vivienda
					    .add(suma.multiply(factorVivienda).setScale(2, BigDecimal.ROUND_HALF_UP));
				    System.out.print("Vivienda: "
					    + suma.multiply(factorVivienda).setScale(2, BigDecimal.ROUND_HALF_UP)
					    + "	");
				}

				calculoSueldo = true;

			    }
			    else if (campoAnexo.getDescripcion().contains("mporte de"))
			    {

				if (campoAnexo.getDescripcion().contains("olidario"))
				{
				    // si el valor contiene as1 o as2 es porque
				    // no se encontró ahorro solidario y hay que
				    // desecharlo
				    if (!campoAnexo.getValor().contains("as"))
				    {
					BigDecimal suma = new BigDecimal(campoAnexo.getValor());

					if (suma.doubleValue() > 0.00)
					{
					    nCasosAhorroSolidario++;
					}

					suma = suma.divide(new BigDecimal("100"));
					ahorroTrabajador = ahorroTrabajador.add(suma);
					ahorroPatron = ahorroPatron.add(suma.multiply(factorAhorroPatron).setScale(2,
						BigDecimal.ROUND_HALF_UP));

					System.out.print("Importe Ahorro Solidario: " + suma + "		");
					System.out.print("Factor Ahorro Solidario: " + suma.multiply(factorAhorroPatron)
						.setScale(2, BigDecimal.ROUND_HALF_UP) + "		");

				    }
				}
			    }

			}

			getAnexoGenerado().getRegistrosDetalle().add(registroAnexoAñadiendo);
			idRegistro++;
			System.out.println("");

		    }

		}

	    }

	    System.out.println("Total de sueldos de CV " + utilidades.formato.format(totalSueldosCV));
	    System.out.println("Total de sueldos de Vivienda " + utilidades.formato.format(totalSueldosVivienda));

	    DecimalFormat formato = new DecimalFormat("$ #,###.00");

	    getAnexoGenerado().setRetiro(formato.format(retiro.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()));
	    getAnexoGenerado()
		    .setVivienda(formato.format(vivienda.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()));

	    getAnexoGenerado().setCasosAhorro(nCasosAhorroSolidario);

	    if (retiro.compareTo(new BigDecimal("0.002")) > 0)
	    {
		getAnexoGenerado().setCesantiaPatron(
			formato.format(cesantiaPatron.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()));
		getAnexoGenerado().setCesantiaTrabajador(
			formato.format(cesantiaTrabajador.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()));
		getAnexoGenerado().setAhorroSolidarioTrabajador(
			formato.format(ahorroTrabajador.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()));
		getAnexoGenerado().setAhorroSolidarioPatron(
			formato.format(ahorroPatron.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()));

	    }
	    else
	    {
		getAnexoGenerado().setCesantiaPatron(formato.format(bigCero));
		getAnexoGenerado().setCesantiaTrabajador(formato.format(bigCero));
		getAnexoGenerado().setAhorroSolidarioTrabajador(formato.format(bigCero));
		getAnexoGenerado().setAhorroSolidarioPatron(formato.format(bigCero));
	    }

	    // Se clona el encabezado a la plantilla de anexo Generado
	    this.versionSeleccionada.getEncabezado().updateCamposVinculados(
		    this.baseSeleccionada.getPlantilla().getIdPlantilla(),
		    this.plantillaBimestre.getPlantillaDetalles().getIdPlantilla());

	    PlantillaRegistro registroEncabezado = new PlantillaRegistro(idRegistro,
		    this.versionSeleccionada.getEncabezado().getClone());

	    // En cualquier caso se crea el encabezado de acuerdo, este proceso
	    // es al final para colocar correctamente los registros
	    for (CampoPlantilla campoAnexo : registroEncabezado.getPlantilla().getCampos())
	    {
		CampoVinculado campoVinculado = campoAnexo.getCampoVinculado();

		// verifica que el valor final se añada mediante un comando
		// abreviado, en caso contrario inserta el valor por defecto tal
		// cual
		if (!verificaComandosAbreviados(campoAnexo, null, null))
		{
		    if (campoVinculado.getValorPorDefecto() != null && campoVinculado.getValorPorDefecto().length() > 0)
		    {
			campoAnexo.setValor(campoVinculado.getValorPorDefecto());
		    }
		    else
		    {// cuando el campo no tiene valor por defecto, es entonces
		     // que se verifica si el campo tiene valor como campo
		     // institucional
			institucion.getValorCoincidente(campoAnexo);
		    }

		}

		// Se ajusta correctamente la longitud del campo, ya que el
		// anexo final puede tener diferencias de longitud contra el
		// origen de los datos
		ajustaLongitudCampo(campoAnexo);

	    }

	    getAnexoGenerado().getRegistroEncabezado().add(registroEncabezado);

	}
	catch (

	Exception e)

	{
	    e.printStackTrace();
	}

	/*
	 * RequestContext.getCurrentInstance().showMessageInDialog(new
	 * FacesMessage(FacesMessage.SEVERITY_INFO, "Registros Coincidentes",
	 * "Se encontraron " + idRegistro + " registros coincidentes."));
	 */
    }

    BigDecimal importeFinal;

    // devuelve falso en caso de no haber encontrado el valor mediante comando
    // abreviado
    // El valor indice es el CURP
    public boolean verificaComandosAbreviados(CampoPlantilla campo, String valorIndice, String importeParaRetiro)
    {

	if (campo.getCampoVinculado().getValorPorDefecto() == null)
	{
	    return false;
	}
	// Se anexa periodo de pago
	if (campo.getCampoVinculado().getValorPorDefecto().equals("pp"))
	{
	    campo.setValor(this.añoSeleccionado + "0" + this.bimestreSelec);

	    return true;

	}
	else if (campo.getCampoVinculado().getValorPorDefecto().equals("fa"))
	{
	    java.util.Calendar fecha = java.util.Calendar.getInstance();

	    String año = "" + fecha.get(Calendar.YEAR);
	    String mes = "" + (fecha.get(Calendar.MONTH) + 1);

	    if (mes.length() == 1)
	    {
		mes = "0" + mes;
	    }

	    String dia = "" + fecha.get(Calendar.DAY_OF_MONTH);

	    if (dia.length() == 1)
	    {
		dia = "0" + dia;
	    }

	    campo.setValor("" + año + mes + dia);

	    return true;
	}
	// Fecha de baja
	else if (campo.getCampoVinculado().getValorPorDefecto().equals("fb"))
	{
	    if (this.fechaBaja == null)
	    {
		return false;
	    }

	    SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyMMdd");
	    campo.setValor(simpleFormat.format(this.fechaBaja));

	    return true;

	}
	else if (campo.getCampoVinculado().getValorPorDefecto().equals("tr"))
	{
	    campo.setValor("" + this.anexoGenerado.getRegistrosDetalle().size());
	    return true;
	}
	else if (campo.getCampoVinculado().getValorPorDefecto().equals("ta"))
	{
	    if (this.detalleSeleccionado.getDescripcion().indexOf("Alta") > -1)
	    {
		campo.setValor("" + this.anexoGenerado.getRegistrosDetalle().size());
		return true;
	    }
	    if (this.detalleSeleccionado.getDescripcion().indexOf("Baja") > -1)
	    {
		campo.setValor("00");
		return true;
	    }
	    else
	    {
		return false;
	    }
	}
	else if (campo.getCampoVinculado().getValorPorDefecto().equals("tb"))
	{
	    if (this.detalleSeleccionado.getDescripcion().indexOf("Baja") > -1)
	    {
		campo.setValor("" + this.anexoGenerado.getRegistrosDetalle().size());
		return true;
	    }
	    else if (this.detalleSeleccionado.getDescripcion().indexOf("Alta") > -1)
	    {
		campo.setValor("00");
		return true;
	    }
	    else
	    {
		return false;
	    }

	}
	else if (campo.getCampoVinculado().getValorPorDefecto().equals("tm"))
	{
	    campo.setValor("00");
	    return true;

	}
	else if (campo.getCampoVinculado().getValorPorDefecto().equals("te"))
	{
	    if (this.detalleSeleccionado.getDescripcion().indexOf("Extempor") > -1)
	    {
		campo.setValor("" + this.anexoGenerado.getRegistrosDetalle().size());
		return true;
	    }
	    else
	    {
		return false;
	    }
	}
	else if (campo.getCampoVinculado().getValorPorDefecto().equals("tl"))
	{
	    campo.setValor("00");
	    return true;
	}
	else if (campo.getCampoVinculado().getValorPorDefecto().equals("as1"))
	{// Si cuenta con ahorro solidario aplica el indicador
	    for (RegistroAhorroSolidario reg : this.institucion.getPlantillaAhorroSolidario())
	    {
		if (reg.getCURP().equalsIgnoreCase(valorIndice))
		{

		    if (importeParaRetiro == null)
		    {
			return false;
		    }

		    if (importeParaRetiro.length() < 1)
		    {
			return false;
		    }

		    campo.setValor("1");
		    return true;
		}
	    }

	    campo.setValor("0");

	    return true;

	}
	else if (campo.getCampoVinculado().getValorPorDefecto().equals("as2"))
	{// si cuenta con ahorro solidario aplica el porcentaje
	    for (RegistroAhorroSolidario reg : this.institucion.getPlantillaAhorroSolidario())
	    {
		if (reg.getCURP().equalsIgnoreCase(valorIndice))
		{
		    // Se saca el valor correspondiente al ahorro solidario de
		    // acuerdo al porcentaje y la cantidad

		    if (importeParaRetiro.length() < 1)
		    {
			return false;
		    }

		    importeFinal = new BigDecimal(importeParaRetiro);

		    importeFinal = importeFinal.divide(new BigDecimal(100));

		    importeFinal = importeFinal.multiply(new BigDecimal(reg.getPorcentaje()));

		    importeFinal = importeFinal.divide(new BigDecimal(100));

		    importeFinal = importeFinal.setScale(2, RoundingMode.CEILING);

		    campo.setValor(importeFinal.toPlainString().replace(".", ""));

		    return true;
		}

	    }

	    campo.setValor("0");

	    return true;
	}

	return false;
    }

    String texto = null;

    public void ajustaLongitudCampo(CampoPlantilla campo)
    {
	try
	{
	    int longitud;
	    int diferencia = 0;

	    if (campo.getValor() == null)
	    {
		longitud = (campo.getEntero() + campo.getDecimal());
		diferencia = -longitud;
	    }
	    else
	    {

		if (campo.getValor().contains("MZA! 52 LOTE 134    TEL! 7 55"))
		{
		    campo.setValor(campo.getValor());
		}
		campo.setValor(campo.getValor().replaceAll("\\s+", " ").trim());

		// longitud final que debe de tener el campo
		longitud = campo.getEntero() + campo.getDecimal();

		// diferencia de la longitud del valor insertado contra la
		// longitud
		// que
		// debe de tener finalmente el campo
		diferencia = (campo.getValor().length() - longitud);

	    }

	    if (diferencia != 0)
	    {
		// en caso de ser negativa la diferencia nos indica que al valor
		// le
		// hace falta que se rellene con más caracteres el valor del
		// campo
		// final
		if (diferencia < 0)
		{
		    String caracteresRelleno = "";

		    switch (campo.getTipo())
		    {

			case "N":

			    for (int x = 0; x < (diferencia * -1); x++)
			    {
				caracteresRelleno += "0";

			    }

			    campo.setValor(caracteresRelleno + campo.getValor());

			    break;

			case "AN":

			    for (int x = 0; x < (diferencia * -1); x++)
			    {
				caracteresRelleno += " ";

			    }

			    campo.setValor(campo.getValor() + caracteresRelleno);

			    break;

		    }

		}
		// en éste caso la diferencia es positiva, ya que el valor del
		// campo
		// excede la longitud que el campo debe de manejar finalmente
		else if (diferencia > 0)
		{
		    campo.setValor(campo.getValor().substring(0, longitud));

		}

	    }
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}

    }

    public String getRellenoCampoVacio(CampoPlantilla campo)
    {
	String valorGenerado = "";

	int longitud = campo.getEntero() + campo.getDecimal();

	for (int x = 0; x < longitud; x++)
	{
	    switch (campo.getTipo())
	    {
		case "N":
		    valorGenerado += "0";
		    break;

		case "AN":
		    valorGenerado += " ";
		    break;
	    }

	}

	return valorGenerado;

    }

    public void listenerChangeListaVersionAnexoGeneracionAnexo(AjaxBehaviorEvent evt)
    {
	this.setDetalleSeleccionado(null);

	if (this.versionSeleccionada != null)
	{
	    this.versionSeleccionada.updatePlantillaEncabezado(true);
	    this.versionSeleccionada.updatePlantillasDetalle(true);
	}
	else
	{
	    setEstadoNuevoEdicionAnexo(-1);
	}
    }

    public void listenerChangeUnidadGeneracionAnexo(AjaxBehaviorEvent evt)
    {

    }

    public void selectTablaResultadosPreviosAnexo(SelectEvent evt)
    {
	this.curpEditando = this.registroSeleccionado.getValorEnCampo(this.indiceBusquedaAnexo.getOrden());
    }

    public void actionCorregirCURP()
    {
	try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionSIRI();)
	{
	    boolean curpRepetidoEnSIRI = false;

	    // Se recorre en todos los registros de SIRI para comprobar que el
	    // CURP no se este ingresando el de otra persona ya registrada en la
	    // base de datos SIRI
	    for (PlantillaRegistro registro : this.baseSeleccionada.getPlantilla().getRegistros())
	    {
		if (registro.getValorEnCampo(getIndicebusquedaSIRI().getOrden()).equals(this.curpCorregido))
		{
		    PreparedStatement prep = conexion.prepareStatement(" SELECT * FROM curpcorreccion WHERE CURP=? ");
		    prep.setString(1, this.curpEditando);

		    ResultSet rBD = prep.executeQuery();

		    if (rBD.next())
		    {
			RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(
				FacesMessage.SEVERITY_INFO, "Curp Previamente Corregido",
				"El curp ya había sido corregido previamente, ninguna modificación realizada."));
			return;
		    }

		    curpRepetidoEnSIRI = true;
		    prep = conexion.prepareStatement(
			    " INSERT INTO `siri`.`curpcorreccion` (`CURP`, `CURPCorreccion`) VALUES (?, ?) ");

		    prep.setString(1, this.curpEditando);
		    prep.setString(2, this.curpCorregido);

		    prep.executeUpdate();

		    this.curpEditando = "";
		    this.curpCorregido = "";

		    RequestContext.getCurrentInstance().update("formGenerarAnexos");
		    RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_INFO,
			    "Curp Corregido", "Vuelva a ejecutar el análisis para verificar la información."));
		    actionAnalizarAnexo();

		    return;

		}

	    }

	    if (curpRepetidoEnSIRI)
	    {
		RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_ERROR,
			"Curp NO en Plantilla SIRI",
			"El curp no se puede corregir ya que no pertenece a ningún trabajador de la plantilla."));
		return;
	    }

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }

    // MÉTODOS PARA LA SECCIÓN DE DATOS INSTITUCIONALES
    /*
     * public void onSelectCamposInstitucionales(SelectEvent evt) {
     * this.campoInstitucionalSeleccionado = (CampoPlantilla) evt.getObject();
     * 
     * }
     * 
     * public void onUnselectCamposInstitucionales(UnselectEvent evt) {
     * 
     * }
     */
    public void actionGuardarDatosInstitucionales()
    {

	try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionSIRI();)
	{
	    PreparedStatement prep = null;
	    ResultSet rBD = null;

	    // Se borran todos los valores antes de guardarlos todos, en
	    // cualquier caso, ya sea la primera vez o la edición de los datos
	    prep = conexion.prepareStatement(" DELETE FROM datosinstitucionales ");

	    prep.executeUpdate();

	    // Se recorren todos los campos que se han utilizado
	    for (CampoPlantilla campo : this.camposDualDatosInstitucionales.getTarget())
	    {
		prep = conexion.prepareStatement(" INSERT INTO datosinstitucionales (idCampo, Valor) VALUES (?, ?) ");

		prep.setInt(1, campo.getIdCampo());
		prep.setString(2, campo.getValor());

		prep.executeUpdate();

	    }

	    RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_INFO,
		    "Configuración Guardada", "Los datos institucionales se han guardado exitosamente."));

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}

    }

    public void actionDescargarTxt()
    {

	try
	{

	    File f = new File("#{resource['images:temp.txt']}");
	    // FileWriter w = new FileWriter(f);
	    // BufferedWriter bw = new BufferedWriter(w);
	    PrintWriter wr = new PrintWriter(new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8),
		    true);

	    // Se recorre el anexo generado para ir imprimiendo el archivo.txt
	    for (PlantillaRegistro registro : this.anexoGenerado.getRegistroEncabezado())
	    {
		for (CampoPlantilla campo : registro.getPlantilla().getCampos())
		{
		    wr.print(campo.getValor());
		}

		wr.println();
	    }

	    for (PlantillaRegistro registro : this.anexoGenerado.getRegistrosDetalle())
	    {
		for (CampoPlantilla campo : registro.getPlantilla().getCampos())
		{
		    wr.print(campo.getValor());
		}

		wr.println();
	    }

	    /*
	     * FileOutputStream pdfTemporal = new FileOutputStream( new
	     * File(FacesContext.getCurrentInstance().getExternalContext().
	     * getRealPath(String.format("/resources/%s", "ejemplo" + ".pdf"))));
	     * 
	     * 
	     * Document document = new Document(); Rectangle rect = new
	     * Rectangle(PageSize.LETTER);
	     * 
	     * document.setPageSize(rect); document.setMargins(36, 36, 36, 65); // step 2
	     * PdfWriter writer = PdfWriter.getInstance(document, pdfTemporal); //
	     * TableHeader event = new TableHeader(); // writer.setPageEvent(event); // step
	     * 3 document.open();
	     * 
	     * 
	     * 
	     * Font fuenteReporte = new Font(); fuenteReporte.setFamily("Courier");
	     * fuenteReporte.setSize(7.3f); fuenteReporte.setStyle(Font.NORMAL);
	     * 
	     * Font fuenteNegrita = new Font(); fuenteNegrita.setFamily("Courier");
	     * fuenteNegrita.setSize(7.3f); fuenteNegrita.setStyle(Font.BOLD);
	     * 
	     * Font fuentePiePagina = new Font(); fuentePiePagina.setFamily("Courier");
	     * fuentePiePagina.setSize(7); fuentePiePagina.setStyle(Font.BOLD);
	     * 
	     * Font fuenteNumeroPagina = new Font();
	     * fuenteNumeroPagina.setFamily("Courier"); fuenteNumeroPagina.setSize(9);
	     * fuenteNumeroPagina.setStyle(Font.BOLD);
	     * 
	     * 
	     * BufferedReader b = new BufferedReader(new
	     * FileReader("/Volumes/SINAME/prueba.spo")); String linea = null; Paragraph
	     * parrafo = new Paragraph();
	     * 
	     * writer.setSpaceCharRatio(PdfWriter.NO_SPACE_CHAR_RATIO);
	     * 
	     * 
	     * while( ( linea = b.readLine() ) != null ) {
	     * 
	     * System.out.println("Ancho: "+linea.length()+" caractéres");
	     * 
	     * Chunk trozo = new Chunk(linea, fuenteNegrita); if(
	     * linea.contains("S E C R E T A R I A    D E    S A L U D") ) {
	     * document.newPage(); }
	     * 
	     * document.add(new Paragraph(trozo) ); }
	     * 
	     * document.close(); // close the writer writer.close();
	     * 
	     * b.close();
	     * 
	     */

	    wr.close();
	    // bw.close();
	    // w.close();

	    String tipoAnexo = this.anexoSeleccionado.getDescripcion() + " - ";

	    switch (this.anexoSeleccionado.getIdLayout())
	    {
		case 1:

		    if (this.detalleSeleccionado.getDescripcion().contains("Alta"))
		    {
			tipoAnexo += "Altas - ";
		    }
		    else if (this.detalleSeleccionado.getDescripcion().contains("Baja"))
		    {
			tipoAnexo += "Baja - ";
		    }

		    break;

		case 2:

		    if (this.detalleSeleccionado.getDescripcion().contains("Pagos Extemp"))
		    {
			tipoAnexo += " Extemporánea " + this.tipoPagoRealizar + " - ";
		    }

		    break;

	    }

	    txt = new DefaultStreamedContent(new FileInputStream(f), "text/plain",
		    tipoAnexo + this.plazaSeleccionada.getDescripcionPlaza() + " - Bim " + this.bimestreSelec + "/"
			    + this.añoSeleccionado + " - " + this.unidadSeleccionada.getDescripcion() + ".txt");

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}

    }

    // AHORRO SOLIDARIO
    public void preparaDialogoNuevoRegistroAhorroSolidario()
    {
	this.nuevoRegistroAhorroSolidario = new RegistroAhorroSolidario(this.curpSeleccionado, 1);

    }

    public void preparaDialogoModificarRegistroAhorroSolidario()
    {
	this.edicionRegistroAhorroSolidario = new RegistroAhorroSolidario(this.registroAhorroSeleccionado.getCURP(),
		this.registroAhorroSeleccionado.getPorcentaje());
    }

    public void actionGuardarRegistroAhorroSolidario()
    {
	try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionSIRI();)
	{

	    PreparedStatement prep = conexion.prepareStatement(" SELECT * FROM ahorrosolidario WHERE CURP=? ");

	    prep.setString(1, this.nuevoRegistroAhorroSolidario.getCURP());

	    ResultSet rBD = prep.executeQuery();

	    if (rBD.next())
	    {
		RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_INFO,
			"Ahorro Solidario Ya Asignado", "El CURP " + this.nuevoRegistroAhorroSolidario.getCURP()
				+ " ya cuenta con ahorro solidario registrado del " + rBD.getInt("Porcentaje")));
		return;
	    }

	    prep = conexion.prepareStatement("INSERT INTO ahorrosolidario (CURP,Porcentaje) VALUES (?, ?) ");

	    prep.setString(1, this.nuevoRegistroAhorroSolidario.getCURP());
	    prep.setInt(2, this.nuevoRegistroAhorroSolidario.getPorcentaje());

	    prep.executeUpdate();

	    RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_INFO,
		    "Ahorro Solidario Asignado", "El ahorro solidario ha sido vinculado al CURP exitosamente."));

	    setInterfaz(5);

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}

    }

    public void actionGuardarModificarRegistroAhorroSolidario()
    {
	try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionSIRI();)
	{
	    PreparedStatement prep = conexion.prepareStatement(" UPDATE ahorrosolidario SET Porcentaje=? WHERE CURP=?");

	    prep.setInt(1, this.edicionRegistroAhorroSolidario.getPorcentaje());
	    prep.setString(2, this.edicionRegistroAhorroSolidario.getCURP());

	    prep.executeUpdate();

	    RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_INFO,
		    "Ahorro Solidario Modificado", "El ahorro solidario ha sido modificado exitosamente."));

	    setInterfaz(5);

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}

    }

    public void actionEliminarAhorroSolidario()
    {
	try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionSIRI();)
	{
	    PreparedStatement prep = conexion.prepareStatement(" DELETE FROM ahorrosolidario WHERE CURP=? ");

	    prep.setString(1, this.registroAhorroSeleccionado.getCURP());

	    prep.executeUpdate();

	    RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_INFO,
		    "Ahorro Solidario Eliminado", "El ahorro solidario se ha eliminado exitosamente."));

	    setInterfaz(5);

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}

    }

    /**
     * @return the basesSiri
     */
    public List<BaseSiri> getBasesSiri()
    {
	return basesSiri;
    }

    /**
     * @param basesSiri
     *            the basesSiri to set
     */
    public void setBasesSiri(List<BaseSiri> basesSiri)
    {
	this.basesSiri = basesSiri;
    }

    /**
     * @return the baseSeleccionada
     */
    public BaseSiri getBaseSeleccionada()
    {
	return baseSeleccionada;
    }

    /**
     * @param baseSeleccionada
     *            the baseSeleccionada to set
     */
    public void setBaseSeleccionada(BaseSiri baseSeleccionada)
    {
	this.baseSeleccionada = baseSeleccionada;
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

    /**
     * @return the tipoCatalogoEditando
     */
    public int getTipoCatalogoEditando()
    {
	return tipoCatalogoEditando;
    }

    /**
     * @param tipoCatalogoEditando
     *            the tipoCatalogoEditando to set
     */
    public void setTipoCatalogoEditando(int tipoCatalogoEditando)
    {
	this.tipoCatalogoEditando = tipoCatalogoEditando;
    }

    /**
     * @return the camposGenerales
     */
    public List<CampoPlantilla> getCamposGenerales()
    {
	return camposGenerales;
    }

    /**
     * @param camposGenerales
     *            the camposGenerales to set
     */
    public void setCamposGenerales(List<CampoPlantilla> camposGenerales)
    {
	this.camposGenerales = camposGenerales;
    }

    /**
     * @return the camposFiltrados
     */
    public List<CampoPlantilla> getCamposFiltrados()
    {
	return camposFiltrados;
    }

    /**
     * @param camposFiltrados
     *            the camposFiltrados to set
     */
    public void setCamposFiltrados(List<CampoPlantilla> camposFiltrados)
    {
	this.camposFiltrados = camposFiltrados;
    }

    /**
     * @return the campoSeleccionado
     */
    public CampoPlantilla getCampoSeleccionado()
    {
	return campoSeleccionado;
    }

    /**
     * @param campoSeleccionado
     *            the campoSeleccionado to set
     */
    public void setCampoSeleccionado(CampoPlantilla campoSeleccionado)
    {
	this.campoSeleccionado = campoSeleccionado;
    }

    /**
     * @return the estadoNuevoEdicionCampo
     */
    public int getEstadoNuevoEdicionCampo()
    {
	return estadoNuevoEdicionCampo;
    }

    /**
     * @param estadoNuevoEdicionCampo
     *            the estadoNuevoEdicionCampo to set
     */
    public void setEstadoNuevoEdicionCampo(int estadoNuevoEdicionCampo)
    {
	this.estadoNuevoEdicionCampo = estadoNuevoEdicionCampo;
    }

    /**
     * @return the nuevoEdicionCampo
     */
    public CampoPlantilla getNuevoEdicionCampo()
    {
	return nuevoEdicionCampo;
    }

    /**
     * @param nuevoEdicionCampo
     *            the nuevoEdicionCampo to set
     */
    public void setNuevoEdicionCampo(CampoPlantilla nuevoEdicionCampo)
    {
	this.nuevoEdicionCampo = nuevoEdicionCampo;
    }

    /**
     * @return the anexos
     */
    public List<Layout> getAnexos()
    {
	return anexos;
    }

    /**
     * @param anexos
     *            the anexos to set
     */
    public void setAnexos(List<Layout> anexos)
    {
	this.anexos = anexos;
    }

    /**
     * @return the anexoSeleccionado
     */
    public Layout getAnexoSeleccionado()
    {
	return anexoSeleccionado;
    }

    /**
     * @param anexoSeleccionado
     *            the anexoSeleccionado to set
     */
    public void setAnexoSeleccionado(Layout anexoSeleccionado)
    {
	this.anexoSeleccionado = anexoSeleccionado;
    }

    /**
     * @return the versionSeleccionada
     */
    public LayoutVersion getVersionSeleccionada()
    {
	return versionSeleccionada;
    }

    /**
     * @param versionSeleccionada
     *            the versionSeleccionada to set
     */
    public void setVersionSeleccionada(LayoutVersion versionSeleccionada)
    {
	this.versionSeleccionada = versionSeleccionada;
    }

    /**
     * @return the nuevoEdicionPlantillaAnexo
     */
    public Plantilla getNuevoEdicionPlantillaAnexo()
    {
	return nuevoEdicionPlantillaAnexo;
    }

    /**
     * @param nuevoEdicionPlantillaAnexo
     *            the nuevoEdicionPlantillaAnexo to set
     */
    public void setNuevoEdicionPlantillaAnexo(Plantilla nuevoEdicionPlantillaAnexo)
    {
	this.nuevoEdicionPlantillaAnexo = nuevoEdicionPlantillaAnexo;
    }

    /**
     * @return the estadoNuevoEdicionAnexo
     */
    public int getEstadoNuevoEdicionAnexo()
    {
	return estadoNuevoEdicionAnexo;
    }

    /**
     * @param estadoNuevoEdicionAnexo
     *            the estadoNuevoEdicionAnexo to set
     */
    public void setEstadoNuevoEdicionAnexo(int estadoNuevoEdicionAnexo)
    {
	this.estadoNuevoEdicionAnexo = estadoNuevoEdicionAnexo;
    }

    /**
     * @return the camposDualPlantillaAnexo
     */
    public DualListModel<CampoPlantilla> getCamposDualPlantillaAnexo()
    {
	return camposDualPlantillaAnexo;
    }

    /**
     * @param camposDualPlantillaAnexo
     *            the camposDualPlantillaAnexo to set
     */
    public void setCamposDualPlantillaAnexo(DualListModel<CampoPlantilla> camposDualPlantillaAnexo)
    {
	this.camposDualPlantillaAnexo = camposDualPlantillaAnexo;
    }

    /**
     * @return the detalleSeleccionado
     */
    public Plantilla getDetalleSeleccionado()
    {
	return detalleSeleccionado;
    }

    /**
     * @param detalleSeleccionado
     *            the detalleSeleccionado to set
     */
    public void setDetalleSeleccionado(Plantilla detalleSeleccionado)
    {
	this.detalleSeleccionado = detalleSeleccionado;
    }

    /**
     * @return the nuevoEdicionLayoutVersion
     */
    public LayoutVersion getNuevoEdicionLayoutVersion()
    {
	return nuevoEdicionLayoutVersion;
    }

    /**
     * @param nuevoEdicionLayoutVersion
     *            the nuevoEdicionLayoutVersion to set
     */
    public void setNuevoEdicionLayoutVersion(LayoutVersion nuevoEdicionLayoutVersion)
    {
	this.nuevoEdicionLayoutVersion = nuevoEdicionLayoutVersion;
    }

    /**
     * @return the archivoSIRI
     */
    public UploadedFile getArchivoSIRI()
    {
	return archivoSIRI;
    }

    /**
     * @param archivoSIRI
     *            the archivoSIRI to set
     */
    public void setArchivoSIRI(UploadedFile archivoSIRI)
    {
	this.archivoSIRI = archivoSIRI;
    }

    /**
     * @return the comentariosArchivoSIRI
     */
    public String getComentariosArchivoSIRI()
    {
	return comentariosArchivoSIRI;
    }

    /**
     * @param comentariosArchivoSIRI
     *            the comentariosArchivoSIRI to set
     */
    public void setComentariosArchivoSIRI(String comentariosArchivoSIRI)
    {
	this.comentariosArchivoSIRI = comentariosArchivoSIRI;
    }

    /**
     * @return the años
     */
    public List<String> getAños()
    {
	return años;
    }

    /**
     * @param años
     *            the años to set
     */
    public void setAños(List<String> años)
    {
	this.años = años;
    }

    /**
     * @return the añoSeleccionado
     */
    public String getAñoSeleccionado()
    {
	return añoSeleccionado;
    }

    /**
     * @param añoSeleccionado
     *            the añoSeleccionado to set
     */
    public void setAñoSeleccionado(String añoSeleccionado)
    {
	this.añoSeleccionado = añoSeleccionado;
    }

    /**
     * @return the plazas
     */
    public List<Plaza> getPlazas()
    {
	return plazas;
    }

    /**
     * @param plazas
     *            the plazas to set
     */
    public void setPlazas(List<Plaza> plazas)
    {
	this.plazas = plazas;
    }

    /**
     * @return the plazaSeleccionada
     */
    public Plaza getPlazaSeleccionada()
    {
	return plazaSeleccionada;
    }

    /**
     * @param plazaSeleccionada
     *            the plazaSeleccionada to set
     */
    public void setPlazaSeleccionada(Plaza plazaSeleccionada)
    {
	this.plazaSeleccionada = plazaSeleccionada;
    }

    /**
     * @return the unidadSeleccionada
     */
    public Unidad getUnidadSeleccionada()
    {
	return unidadSeleccionada;
    }

    /**
     * @param unidadSeleccionada
     *            the unidadSeleccionada to set
     */
    public void setUnidadSeleccionada(Unidad unidadSeleccionada)
    {
	this.unidadSeleccionada = unidadSeleccionada;
    }

    /**
     * @return the archivoSAR100
     */
    public UploadedFile getArchivoSAR100()
    {
	return archivoSAR100;
    }

    /**
     * @param archivoSAR100
     *            the archivoSAR100 to set
     */
    public void setArchivoSAR100(UploadedFile archivoSAR100)
    {
	this.archivoSAR100 = archivoSAR100;
    }

    /**
     * @return the bimestreSelec
     */
    public String getBimestreSelec()
    {
	return bimestreSelec;
    }

    /**
     * @param bimestreSelec
     *            the bimestreSelec to set
     */
    public void setBimestreSelec(String bimestreSelec)
    {
	this.bimestreSelec = bimestreSelec;
    }

    /**
     * @return the plantillaBimestre
     */
    public PlantillaBimestre getPlantillaBimestre()
    {
	return plantillaBimestre;
    }

    /**
     * @param plantillaBimestre
     *            the plantillaBimestre to set
     */
    public void setPlantillaBimestre(PlantillaBimestre plantillaBimestre)
    {
	this.plantillaBimestre = plantillaBimestre;
    }

    /**
     * @return the columnas
     */
    public List<ColumnModel> getColumnas()
    {
	return columnas;
    }

    /**
     * @param columnas
     *            the columnas to set
     */
    public void setColumnas(List<ColumnModel> columnas)
    {
	this.columnas = columnas;
    }

    /**
     * @return the registrosFiltrados
     */
    public List<PlantillaRegistro> getRegistrosFiltrados()
    {
	return registrosFiltrados;
    }

    /**
     * @param registrosFiltrados
     *            the registrosFiltrados to set
     */
    public void setRegistrosFiltrados(List<PlantillaRegistro> registrosFiltrados)
    {
	this.registrosFiltrados = registrosFiltrados;
    }

    /**
     * @return the registroSeleccionado
     */
    public PlantillaRegistro getRegistroSeleccionado()
    {
	return registroSeleccionado;
    }

    /**
     * @param registroSeleccionado
     *            the registroSeleccionado to set
     */
    public void setRegistroSeleccionado(PlantillaRegistro registroSeleccionado)
    {
	this.registroSeleccionado = registroSeleccionado;
    }

    /**
     * @return the indicePrincipal
     */
    public String getIndicePrincipal()
    {
	return indicePrincipal;
    }

    /**
     * @param indicePrincipal
     *            the indicePrincipal to set
     */
    public void setIndicePrincipal(String indicePrincipal)
    {
	this.indicePrincipal = indicePrincipal;
    }

    /**
     * @return the indiceSecundario
     */
    public String getIndiceSecundario()
    {
	return indiceSecundario;
    }

    /**
     * @param indiceSecundario
     *            the indiceSecundario to set
     */
    public void setIndiceSecundario(String indiceSecundario)
    {
	this.indiceSecundario = indiceSecundario;
    }

    /**
     * @return the sarVinculado
     */
    public LayoutVersion getSarVinculado()
    {
	return sarVinculado;
    }

    /**
     * @param sarVinculado
     *            the sarVinculado to set
     */
    public void setSarVinculado(LayoutVersion sarVinculado)
    {
	this.sarVinculado = sarVinculado;
    }

    /**
     * @return the siriVinculado
     */
    public LayoutVersion getSiriVinculado()
    {
	return siriVinculado;
    }

    /**
     * @param siriVinculado
     *            the siriVinculado to set
     */
    public void setSiriVinculado(LayoutVersion siriVinculado)
    {
	this.siriVinculado = siriVinculado;
    }

    /**
     * @return the sarVersionesVincular
     */
    public Layout getSarVersionesVincular()
    {
	return sarVersionesVincular;
    }

    /**
     * @param sarVersionesVincular
     *            the sarVersionesVincular to set
     */
    public void setSarVersionesVincular(Layout sarVersionesVincular)
    {
	this.sarVersionesVincular = sarVersionesVincular;
    }

    /**
     * @return the siriVersionesVincular
     */
    public Layout getSiriVersionesVincular()
    {
	return siriVersionesVincular;
    }

    /**
     * @param siriVersionesVincular
     *            the siriVersionesVincular to set
     */
    public void setSiriVersionesVincular(Layout siriVersionesVincular)
    {
	this.siriVersionesVincular = siriVersionesVincular;
    }

    /**
     * @return the camposAElegirAVincular
     */
    public List<CampoPlantilla> getCamposAElegirAVincular()
    {
	return camposAElegirAVincular;
    }

    /**
     * @param camposAElegirAVincular
     *            the camposAElegirAVincular to set
     */
    public void setCamposAElegirAVincular(List<CampoPlantilla> camposAElegirAVincular)
    {
	this.camposAElegirAVincular = camposAElegirAVincular;
    }

    /**
     * @return the tipoPlantillaEditando
     */
    public int getTipoPlantillaEditando()
    {
	return tipoPlantillaEditando;
    }

    /**
     * @param tipoPlantillaEditando
     *            the tipoPlantillaEditando to set
     */
    public void setTipoPlantillaEditando(int tipoPlantillaEditando)
    {
	this.tipoPlantillaEditando = tipoPlantillaEditando;
    }

    /**
     * @return the camposAElegirFiltrados
     */
    public List<CampoPlantilla> getCamposAElegirFiltrados()
    {
	return camposAElegirFiltrados;
    }

    /**
     * @param camposAElegirFiltrados
     *            the camposAElegirFiltrados to set
     */
    public void setCamposAElegirFiltrados(List<CampoPlantilla> camposAElegirFiltrados)
    {
	this.camposAElegirFiltrados = camposAElegirFiltrados;
    }

    /**
     * @return the campoAVincularSeleccionado
     */
    public CampoPlantilla getCampoAVincularSeleccionado()
    {
	return campoAVincularSeleccionado;
    }

    /**
     * @param campoAVincularSeleccionado
     *            the campoAVincularSeleccionado to set
     */
    public void setCampoAVincularSeleccionado(CampoPlantilla campoAVincularSeleccionado)
    {
	this.campoAVincularSeleccionado = campoAVincularSeleccionado;
    }

    /**
     * @return the tipoDoialgoVinculando
     */
    public int getTipoDoialgoVinculando()
    {
	return tipoDoialgoVinculando;
    }

    /**
     * @param tipoDoialgoVinculando
     *            the tipoDoialgoVinculando to set
     */
    public void setTipoDoialgoVinculando(int tipoDoialgoVinculando)
    {
	this.tipoDoialgoVinculando = tipoDoialgoVinculando;
    }

    /**
     * @return the campoVinculando
     */
    public CampoPlantilla getCampoVinculando()
    {
	return campoVinculando;
    }

    /**
     * @param campoVinculando
     *            the campoVinculando to set
     */
    public void setCampoVinculando(CampoPlantilla campoVinculando)
    {
	this.campoVinculando = campoVinculando;
    }

    /**
     * @return the anexoGenerado
     */
    public PlantillaAnexoGenerado getAnexoGenerado()
    {
	return anexoGenerado;
    }

    /**
     * @param anexoGenerado
     *            the anexoGenerado to set
     */
    public void setAnexoGenerado(PlantillaAnexoGenerado anexoGenerado)
    {
	this.anexoGenerado = anexoGenerado;
    }

    /**
     * @return the curpEditando
     */
    public String getCurpEditando()
    {
	return curpEditando;
    }

    /**
     * @param curpEditando
     *            the curpEditando to set
     */
    public void setCurpEditando(String curpEditando)
    {
	this.curpEditando = curpEditando;
    }

    /**
     * @return the curpCorregido
     */
    public String getCurpCorregido()
    {
	return curpCorregido;
    }

    /**
     * @param curpCorregido
     *            the curpCorregido to set
     */
    public void setCurpCorregido(String curpCorregido)
    {
	this.curpCorregido = curpCorregido;
    }

    /**
     * @return the indiceBusquedaSAR100
     */
    public CampoPlantilla getIndiceBusquedaSAR100()
    {
	return indiceBusquedaSAR100;
    }

    /**
     * @param indiceBusquedaSAR100
     *            the indiceBusquedaSAR100 to set
     */
    public void setIndiceBusquedaSAR100(CampoPlantilla indiceBusquedaSAR100)
    {
	this.indiceBusquedaSAR100 = indiceBusquedaSAR100;
    }

    /**
     * @return the indicebusquedaSIRI
     */
    public CampoPlantilla getIndicebusquedaSIRI()
    {
	return indicebusquedaSIRI;
    }

    /**
     * @param indicebusquedaSIRI
     *            the indicebusquedaSIRI to set
     */
    public void setIndicebusquedaSIRI(CampoPlantilla indicebusquedaSIRI)
    {
	this.indicebusquedaSIRI = indicebusquedaSIRI;
    }

    /**
     * @return the indiceBusquedaAnexo
     */
    public CampoPlantilla getIndiceBusquedaAnexo()
    {
	return indiceBusquedaAnexo;
    }

    /**
     * @param indiceBusquedaAnexo
     *            the indiceBusquedaAnexo to set
     */
    public void setIndiceBusquedaAnexo(CampoPlantilla indiceBusquedaAnexo)
    {
	this.indiceBusquedaAnexo = indiceBusquedaAnexo;
    }

    /**
     * @return the anexoSeleccionadoAux
     */
    public Layout getAnexoSeleccionadoAux()
    {
	return anexoSeleccionadoAux;
    }

    /**
     * @param anexoSeleccionadoAux
     *            the anexoSeleccionadoAux to set
     */
    public void setAnexoSeleccionadoAux(Layout anexoSeleccionadoAux)
    {
	this.anexoSeleccionadoAux = anexoSeleccionadoAux;
    }

    /**
     * @return the camposDualDatosInstitucionales
     */
    public DualListModel<CampoPlantilla> getCamposDualDatosInstitucionales()
    {
	return camposDualDatosInstitucionales;
    }

    /**
     * @param camposDualDatosInstitucionales
     *            the camposDualDatosInstitucionales to set
     */
    public void setCamposDualDatosInstitucionales(DualListModel<CampoPlantilla> camposDualDatosInstitucionales)
    {
	this.camposDualDatosInstitucionales = camposDualDatosInstitucionales;
    }

    /**
     * @return the institucion
     */
    public Institucion getInstitucion()
    {
	return institucion;
    }

    /**
     * @param institucion
     *            the institucion to set
     */
    public void setInstitucion(Institucion institucion)
    {
	this.institucion = institucion;
    }

    /**
     * @return the campoInstitucionalSeleccionado
     */
    public CampoPlantilla getCampoInstitucionalSeleccionado()
    {
	return campoInstitucionalSeleccionado;
    }

    /**
     * @param campoInstitucionalSeleccionado
     *            the campoInstitucionalSeleccionado to set
     */
    public void setCampoInstitucionalSeleccionado(CampoPlantilla campoInstitucionalSeleccionado)
    {
	this.campoInstitucionalSeleccionado = campoInstitucionalSeleccionado;
    }

    /**
     * @return the tipoPagoRealizar
     */
    public String getTipoPagoRealizar()
    {
	return tipoPagoRealizar;
    }

    /**
     * @param tipoPagoRealizar
     *            the tipoPagoRealizar to set
     */
    public void setTipoPagoRealizar(String tipoPagoRealizar)
    {
	this.tipoPagoRealizar = tipoPagoRealizar;
    }

    /**
     * @return the txt
     */
    public StreamedContent getTxt()
    {
	return txt;
    }

    /**
     * @param txt
     *            the txt to set
     */
    public void setTxt(StreamedContent txt)
    {
	this.txt = txt;
    }

    /**
     * @return the curpsDisponiblesAhorro
     */
    public List<String> getCurpsDisponiblesAhorro()
    {
	return curpsDisponiblesAhorro;
    }

    /**
     * @param curpsDisponiblesAhorro
     *            the curpsDisponiblesAhorro to set
     */
    public void setCurpsDisponiblesAhorro(List<String> curpsDisponiblesAhorro)
    {
	this.curpsDisponiblesAhorro = curpsDisponiblesAhorro;
    }

    /**
     * @return the curpSeleccionado
     */
    public String getCurpSeleccionado()
    {
	return curpSeleccionado;
    }

    /**
     * @param curpSeleccionado
     *            the curpSeleccionado to set
     */
    public void setCurpSeleccionado(String curpSeleccionado)
    {
	this.curpSeleccionado = curpSeleccionado;
    }

    /**
     * @return the registroAhorroSeleccionado
     */
    public RegistroAhorroSolidario getRegistroAhorroSeleccionado()
    {
	return registroAhorroSeleccionado;
    }

    /**
     * @param registroAhorroSeleccionado
     *            the registroAhorroSeleccionado to set
     */
    public void setRegistroAhorroSeleccionado(RegistroAhorroSolidario registroAhorroSeleccionado)
    {
	this.registroAhorroSeleccionado = registroAhorroSeleccionado;
    }

    /**
     * @return the nuevoRegistroAhorroSolidario
     */
    public RegistroAhorroSolidario getNuevoRegistroAhorroSolidario()
    {
	return nuevoRegistroAhorroSolidario;
    }

    /**
     * @param nuevoRegistroAhorroSolidario
     *            the nuevoRegistroAhorroSolidario to set
     */
    public void setNuevoRegistroAhorroSolidario(RegistroAhorroSolidario nuevoRegistroAhorroSolidario)
    {
	this.nuevoRegistroAhorroSolidario = nuevoRegistroAhorroSolidario;
    }

    /**
     * @return the edicionRegistroAhorroSolidario
     */
    public RegistroAhorroSolidario getEdicionRegistroAhorroSolidario()
    {
	return edicionRegistroAhorroSolidario;
    }

    /**
     * @param edicionRegistroAhorroSolidario
     *            the edicionRegistroAhorroSolidario to set
     */
    public void setEdicionRegistroAhorroSolidario(RegistroAhorroSolidario edicionRegistroAhorroSolidario)
    {
	this.edicionRegistroAhorroSolidario = edicionRegistroAhorroSolidario;
    }

    static public class ColumnModel implements Serializable
    {

	private String header;
	private String property;

	public ColumnModel(String header, String property)
	{
	    this.header = header;
	    this.property = property;
	}

	public String getHeader()
	{
	    return header;
	}

	public String getProperty()
	{
	    return property;
	}

    }

    public String getTipoAnexoARealizar()
    {
	return tipoAnexoARealizar;
    }

    public void setTipoAnexoARealizar(String tipoAnexoARealizar)
    {
	this.tipoAnexoARealizar = tipoAnexoARealizar;
    }

    public String getOrigenSueldo()
    {
	return origenSueldo;
    }

    public void setOrigenSueldo(String origenSueldo)
    {
	this.origenSueldo = origenSueldo;
    }

    public Date getFechaBaja()
    {
	return fechaBaja;
    }

    public void setFechaBaja(Date fechaBaja)
    {
	this.fechaBaja = fechaBaja;
    }

    public DataTable getDataTableSAR100()
    {
	return dataTableSAR100;
    }

    public void setDataTableSAR100(DataTable dataTableSAR100)
    {
	this.dataTableSAR100 = dataTableSAR100;
    }

    public List<VinculoPlantilla> getVinculosCreados()
    {
	return vinculosCreados;
    }

    public void setVinculosCreados(List<VinculoPlantilla> vinculosCreados)
    {
	this.vinculosCreados = vinculosCreados;
    }

    public VinculoPlantilla getVinculoSeleccionado()
    {
	return vinculoSeleccionado;
    }

    public void setVinculoSeleccionado(VinculoPlantilla vinculoSeleccionado)
    {
	this.vinculoSeleccionado = vinculoSeleccionado;
    }

    public ConverterVinculoPlantilla getConverterVinculoPlantilla()
    {
	return converterVinculoPlantilla;
    }

    public void setConverterVinculoPlantilla(ConverterVinculoPlantilla converterVinculoPlantilla)
    {
	this.converterVinculoPlantilla = converterVinculoPlantilla;
    }

    public int getIdVinculoSeleccionado()
    {
	return idVinculoSeleccionado;
    }

    public void setIdVinculoSeleccionado(int idVinculoSeleccionado)
    {
	this.idVinculoSeleccionado = idVinculoSeleccionado;
    }

    public VinculoPlantilla getVinculoNuevoEdicion()
    {
	return vinculoNuevoEdicion;
    }

    public void setVinculoNuevoEdicion(VinculoPlantilla vinculoNuevoEdicion)
    {
	this.vinculoNuevoEdicion = vinculoNuevoEdicion;
    }

    public int getTipoPlantillaVinculoEditando()
    {
	return tipoPlantillaVinculoEditando;
    }

    public void setTipoPlantillaVinculoEditando(int tipoPlantillaVinculoEditando)
    {
	this.tipoPlantillaVinculoEditando = tipoPlantillaVinculoEditando;
    }

}
