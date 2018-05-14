package gui.portal.nominas;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.RowEditEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

import modelo.PlantillaRegistro;
import modelo.Plaza;
import modelo.Producto;
import modelo.RFCCorreccion;
import resources.DataBase;
import util.FacesUtils;
import util.utilidades;

@ManagedBean
@SessionScoped
public class ValidacionRFCBean implements Serializable
{

    private List<Integer>	años;
    private int			añoSeleccionado;

    private List<Plaza>		plazas;
    private Plaza		plazaSeleccionada;

    private int			plazaSeleccionadaCorreccion;

    private StreamedContent	txt;

    private List<RFCCorreccion>	rfcCorrecciones;

    private UploadedFile	archivoRFCValidados;

    public ValidacionRFCBean()
    {
	setAñosDisponibles();
	this.plazas = utilidades.getPlazas();
	this.plazaSeleccionadaCorreccion = -1;

    }

    private void setAñosDisponibles()
    {
	this.años = new ArrayList<>();

	Calendar ahoraCal = Calendar.getInstance();

	int año = +ahoraCal.get(Calendar.YEAR);
	setAñoSeleccionado(año);

	while (año > 2010)
	{
	    años.add(año);
	    año--;

	}

    }

    public void actionGenerarArchivoValidacionRFC()
    {
	PreparedStatement prep = null;

	try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionSIRI();)
	{
	    int qnaInicial = 1;
	    int qnaFinal = 23;

	    List<Producto> prds = utilidades.getProductos(qnaInicial, qnaFinal, añoSeleccionado,
		    this.plazaSeleccionada.getIdPlaza());

	    Map<String, PlantillaRegistro> empleados = new HashMap<String, PlantillaRegistro>();

	    // Se rechazan los productos que sean de pensión o de cancelación,
	    // solo se considerarán los productos de ordinaria y de
	    // extraordinaria
	    Producto prd;
	    int posNumeroEmp = -1;
	    String numEmpleado = null;
	    Object registroMapa = null;

	    for (int x = 0; x < prds.size(); x++)
	    {
		prd = prds.get(x);

		prd.updateRegistrosDAT();

		// Se obtiene el índice del campo del número de empleado
		posNumeroEmp = prd.getPlantillaDAT().getPosicionValorPorDescripcionContains("mero de emple");

		for (PlantillaRegistro regDAT : prd.getRegistrosDAT())
		{
		    // Se almacenan en un mapa todos los empleados que
		    // contengan valor
		    numEmpleado = regDAT.getValorEnCampo(posNumeroEmp);

		    if (numEmpleado.trim().length() < 10)
		    {
			System.out.println("Número de Empleado Incorrecto: " + numEmpleado + " para "
				+ regDAT.getValorPorDescripcionContains("RFC"));
			continue;
		    }

		    registroMapa = empleados.get(numEmpleado);

		    if (registroMapa != null)
		    {
			((PlantillaRegistro) registroMapa).addImporteAcumulado(regDAT.getTotalAcumulado());

		    }
		    else
		    {
			empleados.put(numEmpleado, regDAT);

		    }

		}

		prd.setRegistrosTRA(new ArrayList<>());
		prd.setRegistrosDAT(new ArrayList<>());
		prd.setConceptos(new ArrayList<>());
		prd.setUnidadResponsable(new ArrayList<>());

	    }

	    try
	    {	

		File f = new File("#{resource['images:temp.txt']}");
		// FileWriter w = new FileWriter(f);
		// BufferedWriter bw = new BufferedWriter(w);
		PrintWriter wr = new PrintWriter(
			new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8), true);

		empleados.entrySet().forEach((e) ->
		{
		    PlantillaRegistro regDAT = (PlantillaRegistro) e.getValue();
		    wr.println(regDAT.getValorPorDescripcionContains("mero de emple") + "|"
			    + regDAT.getValorPorDescripcionContains("RFC") + "|");

		});

		wr.close();

		txt = new DefaultStreamedContent(new FileInputStream(f), "text/plain", "Validación SAT RFC Plaza "
			+ " - " + this.plazaSeleccionada.getDescripcionPlaza() + " - " + this.añoSeleccionado + ".txt");

	    }
	    catch (Exception e)
	    {
		e.printStackTrace();
	    }

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
		    "Excepción",
		    "Ha ocurrido una excepción al momento de generar el archivo de validación de RFC para hacienda, favor de contactar con el desarrollador del sistema."));
	}
	finally
	{
	    if (prep != null)
	    {
		try
		{
		    prep.close();
		}
		catch (SQLException e)
		{
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
	    }
	}

    }

    public void actionRegistrarRFCValidadosArchivo()
    {

	PreparedStatement prep = null;
	ResultSet rBD = null;

	try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
	{

	    BufferedReader buffer = null;

	    buffer = new BufferedReader(new InputStreamReader(this.archivoRFCValidados.getInputstream()));

	    String linea;
	    String sentenciaConCorreccion = "INSERT INTO rfccorrecciones (idPlaza, NumEmpleado, RFC, Valido, RFCCorreccion) VALUES (?, ?, ?, ?, ?) ";

	    boolean excepcion = false;

	    while ((linea = buffer.readLine()) != null)
	    {
		String campos[] = linea.split("\\|");

		try
		{

		    if (campos.length < 4)
		    {
			prep = conexion.prepareStatement(sentenciaConCorreccion);
			prep.setString(5, "");
		    }
		    else
		    {
			prep = conexion.prepareStatement(sentenciaConCorreccion);

			prep.setString(5, campos[3]);
		    }

		    prep.setInt(1, this.plazaSeleccionadaCorreccion);
		    prep.setString(2, campos[0]);
		    prep.setString(3, campos[1]);

		    switch (campos[2])
		    {
			case "V":
			case "v":
			    prep.setBoolean(4, true);
			    break;

			case "I":
			case "i":
			    prep.setBoolean(4, false);
			    break;
		    }

		    prep.executeUpdate();

		}
		catch (MySQLIntegrityConstraintViolationException ex)
		{

		    if (ex.getErrorCode() == 1062)
		    {

			switch (campos[2])
			{
			    case "V":
			    case "v":

				prep.close();

				prep = conexion.prepareStatement(
					"UPDATE RFCCorrecciones SET RFC=?, Valido=?, RFCCorreccion=? WHERE idPlaza=? AND NumEmpleado=?");
				prep.setString(1, campos[1]);
				prep.setBoolean(2, true);
				prep.setString(3, "");

				prep.setInt(4, this.plazaSeleccionadaCorreccion);
				prep.setString(5, campos[0]);

				prep.executeUpdate();

				break;

			    case "I":
			    case "i":

				// Si el registro ya se encuentra insertado
				// Verificar si el status de valido o no valido
				// cambió
				prep.close();

				prep = conexion.prepareStatement(
					"SELECT * FROM RFCCorrecciones WHERE idPlaza=? AND NumEmpleado=?");
				prep.setInt(1, this.plazaSeleccionadaCorreccion);
				prep.setString(2, campos[0]);

				rBD = prep.executeQuery();

				if (rBD.next())
				{

				    if (rBD.getString("RFC").trim().equalsIgnoreCase(campos[1].trim()))
				    {
					if (rBD.getBoolean("Valido"))
					{
					    prep.close();

					    prep = conexion.prepareStatement(
						    "UPDATE RFCCorrecciones SET Valido=?, RFCCorreccion=? WHERE idPlaza=? AND NumEmpleado=?");
					    prep.setBoolean(1, false);

					    if (campos.length == 4)
					    {
						prep.setString(2, campos[3]);
					    }
					    else
					    {
						prep.setString(2, rBD.getString("RFCCorreccion"));

					    }

					    prep.setInt(3, this.plazaSeleccionadaCorreccion);
					    prep.setString(4, campos[1]);

					    prep.executeUpdate();

					}
					else
					{
					    // si existe el campo de corrección,
					    // sino se queda igual o si el
					    // RFCCorrección es exactamente
					    // igual
					    String rfcCo = rBD.getString("RFCCorreccion");

					    if (campos.length == 4 && !rBD.getString("RFCCorreccion").trim()
						    .equalsIgnoreCase(campos[3].trim()))
					    {
						prep.close();

						prep = conexion.prepareStatement(
							"UPDATE RFCCorrecciones SET RFCCorreccion=? WHERE idPlaza=? AND NumEmpleado=?");
						prep.setString(1, campos[3]);

						prep.setInt(2, this.plazaSeleccionadaCorreccion);
						prep.setString(3, campos[0]);

						prep.executeUpdate();

					    }

					}

				    }
				    else
				    {
					prep.close();

					prep = conexion.prepareStatement(
						"UPDATE RFCCorrecciones SET RFC=?, Valido=?, RFCCorreccion=? WHERE idPlaza=? AND NumEmpleado=?");

					prep.setString(1, campos[1]);
					prep.setBoolean(2, false);

					if (campos.length == 4)
					{
					    prep.setString(3, campos[3]);
					}
					else
					{
					    prep.setString(3, "");

					}

					prep.setInt(4, this.plazaSeleccionadaCorreccion);
					prep.setString(5, campos[0]);

					prep.executeUpdate();

				    }

				}

				break;

			}

		    }
		    else
		    {
			excepcion = true;
			break;
		    }

		}

	    }

	    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
		    "Catálogo Actualizado", "El catálogo de correcciones de RFC se ha actualizado exitosamente."));

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
		    "Excepción",
		    "Ha ocurrido una excepción al momento de registrar los RFC Validados por el SAT, favor de contactar con el desarrollador del sistema."));
	}
	finally
	{
	    if (prep != null)
	    {
		try
		{
		    prep.close();
		}
		catch (SQLException e)
		{
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
	    }
	}

    }

    // Sección para corregir subir los archivo validados y corregir los RFC
    public void getCorreccionesRFC()
    {
	if (this.plazaSeleccionadaCorreccion > -1)
	{
	    this.rfcCorrecciones = utilidades.getRFCCorrecciones(this.plazaSeleccionadaCorreccion);
	}
	else
	{
	    this.rfcCorrecciones = null;
	}
    }

    public void updateCorreccionRFC(RowEditEvent evt)
    {
	PreparedStatement prep = null;
	ResultSet rBD = null;

	try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
	{

	    RFCCorreccion rfcNuevo = (RFCCorreccion) evt.getObject();

	    if (rfcNuevo.getRfcCorreccion().trim().equalsIgnoreCase(rfcNuevo.getRfcCorreccionEdit().trim()))
	    {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
			"Mismo RFC de Corrección",
			"El RFC de corrrección es el mismo que la última corrección, ningún cambio fue aplicado."));

		return;

	    }

	    if (rfcNuevo.getRFC().trim().equalsIgnoreCase(rfcNuevo.getRfcCorreccionEdit().trim()))
	    {
		FacesContext.getCurrentInstance().addMessage(null,
			new FacesMessage(FacesMessage.SEVERITY_WARN, "Mismo RFC que el original",
				"El RFC de corrrección es el mismo que el original, ningún cambio fue aplicado."));

		return;

	    }

	    prep = conexion
		    .prepareStatement("UPDATE RFCCorrecciones SET RFCCorreccion=? WHERE idPlaza=? AND NumEmpleado=?");

	    prep.setString(1, rfcNuevo.getRfcCorreccionEdit());
	    prep.setInt(2, this.plazaSeleccionadaCorreccion);
	    prep.setString(3, rfcNuevo.getNumEmpleado());

	    prep.executeUpdate();

	    rfcNuevo.setRfcCorreccion(rfcNuevo.getRfcCorreccionEdit());

	    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
		    "RFC Actualizado", "La corrección del RFC ha sido realizada exitosamente."));

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
		    "Excepción",
		    "Ha ocurrido una excepción al momento de registrar la corrección del RFC, favor de contactar con el desarrollador del sistema."));
	}
	finally
	{
	    if (prep != null)
	    {
		try
		{
		    prep.close();
		}
		catch (SQLException e)
		{
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
	    }
	}

    }

    public List<Integer> getAños()
    {
	return años;
    }

    public void setAños(List<Integer> años)
    {
	this.años = años;
    }

    public int getAñoSeleccionado()
    {
	return añoSeleccionado;
    }

    public void setAñoSeleccionado(int añoSeleccionado)
    {
	this.añoSeleccionado = añoSeleccionado;
    }

    public List<Plaza> getPlazas()
    {
	return plazas;
    }

    public void setPlazas(List<Plaza> plazas)
    {
	this.plazas = plazas;
    }

    public Plaza getPlazaSeleccionada()
    {
	return plazaSeleccionada;
    }

    public void setPlazaSeleccionada(Plaza plazaSeleccionada)
    {
	this.plazaSeleccionada = plazaSeleccionada;
    }

    public StreamedContent getTxt()
    {
	return txt;
    }

    public void setTxt(StreamedContent txt)
    {
	this.txt = txt;
    }

    public List<RFCCorreccion> getRfcCorrecciones()
    {
	return rfcCorrecciones;
    }

    public void setRfcCorrecciones(List<RFCCorreccion> rfcCorrecciones)
    {
	this.rfcCorrecciones = rfcCorrecciones;
    }

    public int getPlazaSeleccionadaCorreccion()
    {
	return plazaSeleccionadaCorreccion;
    }

    public void setPlazaSeleccionadaCorreccion(int plazaSeleccionadaCorreccion)
    {
	this.plazaSeleccionadaCorreccion = plazaSeleccionadaCorreccion;
    }

    public UploadedFile getArchivoRFCValidados()
    {
	return archivoRFCValidados;
    }

    public void setArchivoRFCValidados(UploadedFile archivoRFCValidados)
    {
	this.archivoRFCValidados = archivoRFCValidados;
    }

}
