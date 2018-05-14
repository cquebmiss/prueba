package gui.portal.nominas;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.primefaces.event.CellEditEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import application.CatalogosBean;
import modelo.ArchivoBanco;
import modelo.CampoPlantilla;
import modelo.CampoPlantillaConverter;
import modelo.ColumnModel;
import modelo.InstrumentoPago;
import modelo.LayoutVersion;
import modelo.PlantillaRegistro;
import modelo.Plaza;
import resources.DataBase;
import util.FacesUtils;
import util.utilidades;

@ViewScoped
@ManagedBean
public class GestionArchivosBancoBean implements Serializable
{

	/**
	 * 
	 */
	private static final long		serialVersionUID	= 1L;

	private Plaza					plazaSelec;
	private int						añoSelec;
	private int						qnaSelec;

	private List<Plaza>				catPlazas;
	private CatalogosBean			catBean;

	private List<InstrumentoPago>	catInstrumentosPago;
	private InstrumentoPago			instrumentoPagoSelec;

	private List<ArchivoBanco>		archivosPago;
	private int						idArchivoPagoSelec;

	// Corresponde a la versión del detalle
	private LayoutVersion			layoutVersion;

	private String					descripcionArchivo;
	private String					comentariosArchivo;

	private UploadedFile			archivo;

	private ArchivoBanco			archivoBanco;

	private Plaza					plazaSelecVinculando;
	private int						añoSelecVinculando;
	private int						qnaSelecVinculando;

	private List<InstrumentoPago>	catInstrumentosPagoVinculando;
	private int						idArchivoPagoSelecVinculando;
	private LayoutVersion			layoutVersionVinculando;

	// Registro que se estará editando para la dispersión
	private PlantillaRegistro		registroEditando;

	// Para vinculación
	private List<ArchivoBanco>		catArchivosPago;
	private List<ArchivoBanco>		catArchivosPagoFilter;
	private ArchivoBanco			archivoBancoVinculandoSelec;
	private PlantillaRegistro		registroVinculando;

	// Auxiliares para visualización en tablas
	private List<PlantillaRegistro>	registrosFilter;
	private PlantillaRegistro		registroSelec;
	private List<PlantillaRegistro>	registrosFilterBaja;
	private PlantillaRegistro		registroSelecBaja;
	private List<PlantillaRegistro>	registrosFilterModificados;
	private PlantillaRegistro		registroSelecModificados;
	private List<PlantillaRegistro>	registrosFilterNuevos;
	private PlantillaRegistro		registroSelecNuevos;

	private List<PlantillaRegistro>	registrosFilterVinculados;
	private PlantillaRegistro		registroSelecVinculados;

	// Exportación del archivo de texto
	private CampoPlantillaConverter	campoPlantillaConverter;
	private List<CampoPlantilla>	camposLayoutSeleccionadoExportar;
	private List<CampoPlantilla>	camposLayoutSeleccionadoExportarModificados;
	private List<CampoPlantilla>	camposLayoutSeleccionadoExportarBajas;
	private List<CampoPlantilla>	camposLayoutSeleccionadoExportarNuevos;
	private List<CampoPlantilla>	camposLayoutSeleccionadoExportarVinculados;
	private StreamedContent			archivoLayoutExportacion;

	@PostConstruct
	public void onPostConstruct()
	{
		this.catBean = (CatalogosBean) FacesUtils.getManagedBean("catalogosBean");
		this.catPlazas = this.catBean.getCatPlazas();

		LocalDateTime ldt = LocalDateTime.now();

		this.añoSelec = ldt.getYear();
		this.qnaSelec = ldt.getMonthValue() * 2;

		if (ldt.getDayOfMonth() < 16)
		{
			this.qnaSelec--;
		}

		this.catInstrumentosPago = this.catBean.getCatInstrumentosPago();

		if (!this.catPlazas.isEmpty())
		{
			this.plazaSelec = this.catPlazas.get(0);
			actionSelecionNomina();
		}

	}

	public void actionSelecionNomina()
	{
		this.catInstrumentosPago = new ArrayList<>();

		this.catBean.getCatInstrumentosPago().forEach(ip ->
		{
			if (ip.getPlaza().getIdPlaza() == this.plazaSelec.getIdPlaza())
			{
				this.catInstrumentosPago.add(ip);
			}

		});

		if (!this.catInstrumentosPago.isEmpty())
		{
			this.instrumentoPagoSelec = this.catInstrumentosPago.get(0);
		}

		actionSeleccionarInstrumentoPago();

	}

	public void actionSeleccionarInstrumentoPago()
	{
		this.archivosPago = new ArrayList<>();

		this.archivosPago = utilidades.getArchivosBancoPlazaIP(this.plazaSelec.getIdPlaza(), this.añoSelec,
				this.qnaSelec, this.instrumentoPagoSelec.getIdInstrumentoPago());

	}

	public void actionPrepararDialogoIngresarArchivo()
	{

		this.layoutVersion = utilidades.getVersionLayoutIP(this.plazaSelec.getIdPlaza(),
				this.instrumentoPagoSelec.getIdInstrumentoPago());
		this.descripcionArchivo = "";
		this.comentariosArchivo = "";

	}

	public void actionPrepararDialogoModificarRegistro(PlantillaRegistro registro)
	{
		this.registroEditando = registro;
	}

	public void actionGuardarArchivo()
	{

		PreparedStatement prep = null;

		BufferedReader buffer = null;
		this.archivoBanco = null;

		try
		{
			buffer = new BufferedReader(new InputStreamReader(this.archivo.getInputstream()));

		} catch (IOException e1)
		{
			// TODO Auto-generated catch block
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción", "Ha ocurrido una excepción al cargar el archivo, favor de verificar el archivo."));

			e1.printStackTrace();
		}

		this.layoutVersion.updatePlantillasDetalle(true);

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionNominas();)
		{
			// Se valida que ya exista el archivo de puestos en el sistema
			prep = conexion.prepareStatement(" SELECT MAX(idArchivoPago) as idMaximo FROM archivopago ");

			ResultSet rBD = prep.executeQuery();

			int idArchivoBanco = 0;

			LocalDateTime ldt = LocalDateTime.now();
			LocalDate ld = ldt.toLocalDate();
			LocalTime lt = ldt.toLocalTime();

			if (rBD.next())
			{
				idArchivoBanco = rBD.getInt("idMaximo") + 1;
			}

			prep.close();

			// Se inserta primero el registro y luego se obtiene el índice
			// que se obtuvo ya que solo puede haber un archivo de puestos
			// por cada nómina en una quincena y año específico

			boolean correcto = false;

			int nIntentos = 0;

			do

			{
				try
				{

					prep = conexion.prepareStatement(
							" INSERT INTO archivopago (idArchivoPago, Descripcion, NombreArchivo, idPlaza, Ano, Qna, Observaciones, idPlantilla, FechaCarga, HoraCarga, idInstrumentoPago) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ");

					prep.setInt(1, idArchivoBanco);
					prep.setString(2, this.descripcionArchivo);
					prep.setString(3, this.archivo.getFileName());
					prep.setInt(4, this.plazaSelec.getIdPlaza());
					prep.setInt(5, this.añoSelec);
					prep.setInt(6, this.qnaSelec);
					prep.setString(7, this.comentariosArchivo);
					prep.setInt(8, this.layoutVersion.getDetalles().get(0).getIdPlantilla());

					prep.setDate(9, java.sql.Date.valueOf(ld));
					prep.setTime(10, java.sql.Time.valueOf(lt));
					prep.setString(11, this.instrumentoPagoSelec.getIdInstrumentoPago());

					prep.executeUpdate();

					prep.close();

					correcto = true;

				} catch (Exception e)
				{
					if (nIntentos > 3)
					{
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
								"Excepción",
								"Se ha generado una excepción al generar el índice del archivo, favor de contactar con el desarrollador del sistema."));
						return;
					}

					idArchivoBanco++;
					nIntentos++;
				}

			}
			while (!correcto);

			// Ahora se inserta todo el contenido de el archivo dentro de la
			// tabla de datos
			String linea;
			int orden;
			boolean lecturanula = false;
			int nLinea = 1;
			int nLineaTotal = 1;
			String[] camposLinea = null;
			prep = conexion.prepareStatement(
					"INSERT INTO archivopagovalores (idArchivoPago, idPlantilla, Orden, Valor, idRegistro) VALUES (?, ?, ?, ?, ?); ");

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
							return;
						}

						orden = 1;

						if (this.layoutVersion.getDetalles().get(0).getCaracterSeparador().length() < 1)
						{
							int indicePosicioneCampo = 0;

							for (CampoPlantilla campo : this.layoutVersion.getDetalles().get(0).getCampos())
							{
								if (indicePosicioneCampo >= linea.length())
								{
									break;
								}

								prep.setInt(1, idArchivoBanco);
								prep.setInt(2, this.layoutVersion.getDetalles().get(0).getIdPlantilla());
								prep.setInt(3, orden);
								prep.setString(4, linea.substring(indicePosicioneCampo,
										(indicePosicioneCampo + (campo.getEntero() + campo.getDecimal()))));
								prep.setInt(5, nLinea);

								prep.addBatch();

								orden++;

								indicePosicioneCampo += (campo.getEntero() + campo.getDecimal());

							}

						}
						else
						{

							camposLinea = linea
									.split("[" + this.layoutVersion.getDetalles().get(0).getCaracterSeparador() + "]");

							System.out.println(nLinea);

							for (String valorCampo : camposLinea)
							{

								prep.setInt(1, idArchivoBanco);
								prep.setInt(2, this.layoutVersion.getDetalles().get(0).getIdPlantilla());
								prep.setInt(3, orden);
								prep.setString(4, valorCampo);
								prep.setInt(5, nLinea);

								prep.addBatch();

								orden++;

							}
						}

						prep.executeBatch();

					}

					nLinea++;
					nLineaTotal++;

				} catch (IOException ex)
				{
					System.out.println(ex);
					return;

				}

			}

			actionSeleccionarInstrumentoPago();

			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Archivo Cargado", "La información del archivo se ha cargado exitosamente."));

		} catch (Exception e)
		{
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Se ha generado una excepción al subir el archivo, favor de contactar con el desarrollador del sistema."));
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

	public void actionObtenerRegistrosCatalogoBanco()
	{
		this.archivo = null;
		this.archivoBanco = null;
		PreparedStatement prep = null;
		ResultSet rBD = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionNominas();)
		{
			prep = conexion.prepareStatement(
					" SELECT * FROM archivopago WHERE idPlaza=? AND Ano=? AND Qna=? AND idInstrumentoPago=?");

			prep.setInt(1, this.plazaSelec.getIdPlaza());
			prep.setInt(2, this.añoSelec);
			prep.setInt(3, this.qnaSelec);
			prep.setString(4, this.instrumentoPagoSelec.getIdInstrumentoPago());

			rBD = prep.executeQuery();

			if (rBD.next())
			{

				this.archivoBanco = utilidades.getArchivoBanco(this.plazaSelec.getIdPlaza(), this.añoSelec,
						this.qnaSelec, this.instrumentoPagoSelec.getIdInstrumentoPago(), this.idArchivoPagoSelec);
				this.archivoBanco.updatePlantillaRegistros();
				this.archivoBanco.updateRegistros();
				this.archivoBanco.calculaTotalNeto();

			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Archivo No Cargado",
						"Aún no se ha cargado el archivo banco para la quincena e instrumento de pago indicado."));
				return;

			}

			if (this.campoPlantillaConverter == null)
			{
				this.campoPlantillaConverter = new CampoPlantillaConverter();
			}

			if (this.archivoBanco != null)
			{
				this.campoPlantillaConverter.setCampos(this.archivoBanco.getPlantilla().getCampos());
			}

		} catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al obtener el índice del archivo de banco, favor de contactar con el desarrollador del sistema."));

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

	public void actionEliminarArchivoBanco()
	{
		PreparedStatement prep = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionNominas();)
		{
			prep = conexion.prepareStatement("DELETE FROM registropagonuevo WHERE idArchivoPago=?");

			prep.setInt(1, this.archivoBanco.getIdArchivoPago());

			prep.executeUpdate();

			prep.close();

			prep = conexion.prepareStatement("DELETE FROM registropagoinactivo WHERE idArchivoPago=?");

			prep.setInt(1, this.archivoBanco.getIdArchivoPago());

			prep.executeUpdate();

			prep.close();

			prep = conexion.prepareStatement("DELETE FROM archivopagovalores WHERE idArchivoPago=? ");

			prep.setInt(1, this.archivoBanco.getIdArchivoPago());

			prep.executeUpdate();

			prep.close();

			prep = conexion.prepareStatement("DELETE FROM registropagovinculado WHERE idArchivoPago=? ");
			
			prep.setInt(1, this.archivoBanco.getIdArchivoPago());
			
			prep.executeUpdate();
			
			prep.close();

			prep = conexion.prepareStatement("DELETE FROM archivopago WHERE idArchivoPago=? ");

			prep.setInt(1, this.archivoBanco.getIdArchivoPago());

			prep.executeUpdate();

			this.archivoBanco = null;

			this.registroEditando = null;
			this.registrosFilter = null;
			this.registroSelec = null;
			this.registrosFilterBaja = null;
			this.registroSelecBaja = null;
			this.registrosFilterModificados = null;
			this.registroSelecModificados = null;
			this.registrosFilterNuevos = null;
			this.registroSelecNuevos = null;
			this.campoPlantillaConverter = null;
			this.camposLayoutSeleccionadoExportar = null;
			this.camposLayoutSeleccionadoExportarModificados = null;
			this.camposLayoutSeleccionadoExportarBajas = null;
			this.camposLayoutSeleccionadoExportarNuevos = null;
			this.archivoLayoutExportacion = null;

			actionSeleccionarInstrumentoPago();
			// actionObtenerRegistrosCatalogoBanco();

		} catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al eliminar el archivo de banco, favor de contactar con el desarrollador del sistema."));

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

	public void actionEliminarRegistro(PlantillaRegistro registro)
	{
		PreparedStatement prep = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionNominas();)
		{
			prep = conexion.prepareStatement("DELETE FROM registropagonuevo WHERE idArchivoPago=? AND idRegistro=?");

			prep.setInt(1, this.archivoBanco.getIdArchivoPago());
			prep.setInt(2, registro.getIdRegistro());

			prep.executeUpdate();

			prep.close();

			prep = conexion.prepareStatement("DELETE FROM registropagoinactivo WHERE idArchivoPago=? AND idRegistro=?");

			prep.setInt(1, this.archivoBanco.getIdArchivoPago());
			prep.setInt(2, registro.getIdRegistro());

			prep.executeUpdate();

			prep.close();

			prep = conexion.prepareStatement("DELETE FROM archivopagovalores WHERE idArchivoPago=? AND idRegistro=? ");

			prep.setInt(1, this.archivoBanco.getIdArchivoPago());
			prep.setInt(2, registro.getIdRegistro());

			prep.executeUpdate();

			prep.close();

			// Se reordenan los nuevos registros, en caso de ser eliminado algún
			// registro nuevo
			if (this.archivoBanco.getRegistrosNuevos().contains(registro))
			{
				prep = conexion.prepareStatement(
						"UPDATE archivopagovalores SET idRegistro = (idRegistro-1) WHERE idArchivoPago=? AND idRegistro>?");
				prep.setInt(1, this.archivoBanco.getIdArchivoPago());
				prep.setInt(2, registro.getIdRegistro());

				prep.executeUpdate();

				prep = conexion.prepareStatement(
						"UPDATE registropagonuevo SET idRegistro = (idRegistro-1) WHERE idArchivoPago=? AND idRegistro>?");
				prep.setInt(1, this.archivoBanco.getIdArchivoPago());
				prep.setInt(2, registro.getIdRegistro());

				prep.executeUpdate();

				prep = conexion.prepareStatement(
						"UPDATE registropagoinactivo SET idRegistro = (idRegistro-1) WHERE idArchivoPago=? AND idRegistro>?");
				prep.setInt(1, this.archivoBanco.getIdArchivoPago());
				prep.setInt(2, registro.getIdRegistro());

				prep.executeUpdate();

			}

			this.archivoBanco.updateRegistros();
			this.archivoBanco.calculaTotalNeto();

		} catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al eliminar el registro del archivo de banco, favor de contactar con el desarrollador del sistema."));

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

	public void actionCambiaStatusRegistro(PlantillaRegistro registro)
	{

		this.archivoBanco.cambiaStatusRegistro(registro);

		PreparedStatement prep = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionNominas();)
		{

			// Se consulta is ya existe el registro
			prep = conexion.prepareStatement(
					"DELETE FROM registropagoinactivo WHERE idArchivoPago=? AND idPlantilla=? AND idRegistro=?");

			prep.setInt(1, this.archivoBanco.getIdArchivoPago());
			prep.setInt(2, registro.getPlantilla().getIdPlantilla());
			prep.setInt(3, registro.getIdRegistro());

			prep.executeUpdate();

			prep.close();

			if (registro.getStatus() == -1)
			{
				prep = conexion.prepareStatement(
						" INSERT INTO registropagoinactivo (idArchivoPago, idPlantilla, idRegistro) VALUES (?, ?, ?); ");

				prep.setInt(1, this.archivoBanco.getIdArchivoPago());
				prep.setInt(2, registro.getPlantilla().getIdPlantilla());
				prep.setInt(3, registro.getIdRegistro());

				prep.executeUpdate();

			}

			String tituloMensaje = "El registro se ha dado de baja exitosamente.";

			if (registro.getStatus() == 0)
			{
				tituloMensaje = "El registro se ha dado de alta exitosamente.";
			}

			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
					tituloMensaje, "El status del registro ha sido modificado exitosamente"));

			this.archivoBanco.detectaRegistrosBaja();
			this.archivoBanco.calculaTotalNeto();

		} catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al cambiar el status del registro, favor de contactar con el desarrollador del sistema."));

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

	public void onRowEditDatosRegistro(CampoPlantilla campo)
	{

		PreparedStatement prep = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionNominas();)
		{

			if (campo.getValorModificado() != null && !campo.getValorModificado().trim().isEmpty())
			{

				if ((campo.getEntero() + campo.getDecimal()) >= campo.getValorModificado().length())
				{
					// Se identifica si lo que se intenta guaradar es un número
					// o una cadena, en caso de ser un número el contenido se
					// alineará con espacios sobrantes hacia la izquierda
					campo.setValorModificado(this.archivoBanco.ajustaLongitudCampo(campo, campo.getValorModificado()));

					// Se valida que el valor del campo modificado esté
					// correcto
					prep = conexion.prepareStatement(
							" UPDATE archivopagovalores SET ValorModificado=? WHERE idArchivoPago=? AND idPlantilla=? AND Orden=? AND idRegistro=? ");

					prep.setString(1, campo.getValorModificado());
					prep.setInt(2, this.archivoBanco.getIdArchivoPago());
					prep.setInt(3, this.archivoBanco.getPlantilla().getIdPlantilla());
					prep.setInt(4, campo.getOrden());
					prep.setInt(5, this.registroEditando.getIdRegistro());

					prep.executeUpdate();

					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Valor Modificado", "El valor del campo ha sido modificado exitosamente"));

				}
				else
				{
					campo.setValorModificado("");

					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, "Valor Demasiado Largo",
									"El valor del campo debe ser máximo de " + (campo.getEntero() + campo.getDecimal())
											+ " caractéres"));
				}

			}
			else
			{
				prep = conexion.prepareStatement(
						" UPDATE archivopagovalores SET ValorModificado=? WHERE idArchivoPago=? AND idPlantilla=? AND Orden=? AND idRegistro=? ");

				prep.setNull(1, java.sql.Types.VARCHAR);
				prep.setInt(2, this.archivoBanco.getIdArchivoPago());
				prep.setInt(3, this.archivoBanco.getPlantilla().getIdPlantilla());
				prep.setInt(4, campo.getOrden());
				prep.setInt(5, this.registroEditando.getIdRegistro());

				prep.executeUpdate();

				campo.setValorModificado("");

				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Valor Modificado", "Valor eliminado, se usará el valor original."));
			}

			this.archivoBanco.detectaRegistrosModificados();
			this.archivoBanco.calculaTotalNeto();

		} catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al guardar la edición del dato, favor de contactar con el desarrollador del sistema."));

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

	public void onRowCancelDatosRegistro(CellEditEvent evt)
	{

	}

	public void onRowEditDatosRegistroNuevo(CampoPlantilla campo)
	{

		PreparedStatement prep = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionNominas();)
		{

			if (campo.getValor() != null && !campo.getValor().trim().isEmpty())
			{

				if ((campo.getEntero() + campo.getDecimal()) >= campo.getValor().length())
				{
					// Se valida que el valor del campo modificado esté
					// correcto
					prep = conexion.prepareStatement(
							" UPDATE archivopagovalores SET Valor=? WHERE idArchivoPago=? AND idPlantilla=? AND Orden=? AND idRegistro=? ");

					prep.setString(1, campo.getValor());
					prep.setInt(2, this.archivoBanco.getIdArchivoPago());
					prep.setInt(3, this.archivoBanco.getPlantilla().getIdPlantilla());
					prep.setInt(4, campo.getOrden());
					prep.setInt(5, this.registroEditando.getIdRegistro());

					prep.executeUpdate();

					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Valor Insertado", "El valor del campo ha sido modificado exitosamente"));

				}
				else
				{
					campo.setValorModificado("");

					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, "Valor Demasiado Largo",
									"El valor del campo debe ser máximo de " + (campo.getEntero() + campo.getDecimal())
											+ " caractéres"));
				}

			}
			else
			{
				prep = conexion.prepareStatement(
						" UPDATE archivopagovalores SET ValorModificado=? WHERE idArchivoPago=? AND idPlantilla=? AND Orden=? AND idRegistro=? ");

				prep.setNull(1, java.sql.Types.VARCHAR);
				prep.setInt(2, this.archivoBanco.getIdArchivoPago());
				prep.setInt(3, this.archivoBanco.getPlantilla().getIdPlantilla());
				prep.setInt(4, campo.getOrden());
				prep.setInt(5, this.registroEditando.getIdRegistro());

				prep.executeUpdate();

				campo.setValorModificado("");

				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Valor Modificado", "Valor eliminado, se usará el valor original."));
			}

			this.archivoBanco.calculaTotalNeto();

		} catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al guardar la edición del dato, favor de contactar con el desarrollador del sistema."));

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

	// Añade un registro nuevo en base a otro que se estará duplicando
	public void actionDuplicarRegistro(PlantillaRegistro regADuplicar)
	{
		this.archivoBanco.añadirRegistro(regADuplicar);

	}

	public void actionAñadirNuevoRegistro()
	{
		this.registroEditando = this.archivoBanco.añadirRegistro(null);
	}

	// tipoExportacion 0 para finales, 1 para bajas, 2 para modificados y 3 para
	// nuevos
	public void actionExportar(List<PlantillaRegistro> registrosExportando, int tipoExportacion)
	{

		try
		{
			ExternalContext ext = FacesContext.getCurrentInstance().getExternalContext();

			// Se obtiene el id de la sesión del usuario
			HttpSession session = (HttpSession) ext.getSession(false);
			String sessionId = session.getId();

			File folderDescarga = new File(ext.getRealPath("/resources/bancos/" + sessionId + "/"));

			// Se verifica que la carpeta exista, en caso contrario la crea
			if (!folderDescarga.exists())
			{
				folderDescarga.mkdirs();
			}

			String nombreArchivo = "ArchivoBanco_" + this.plazaSelec.getDescripcionPlaza() + "_" + this.añoSelec + "_"
					+ this.qnaSelec + "_IP_" + this.idArchivoPagoSelec + "_" + this.archivoBanco.getDescripcion();
			String extension = ".txt";

			// Crea el archivo temporal, primero establece la carpeta mediante
			// un objeto Path y mediante otro objeto Path crea el archivo
			// temporal
			Path folder = Paths
					.get(ext.getRealPath(String.format(String.format("/resources/bancos/" + sessionId + "/"))));

			Path file = Files.createTempFile(folder, nombreArchivo, extension);

			File archivoDescarga = file.toFile();

			// Obtenemos un objeto File a partir del archivo temporal creado
			// mediante el objeto Path

			// Se crea el objeto PrintWriter para escribir dentro del archivo
			// txt que estamos descargando con la información del layout del
			// archivo de banco con las modificaciones que se acaban de hacer
			PrintWriter wr = new PrintWriter(
					new OutputStreamWriter(new FileOutputStream(archivoDescarga), StandardCharsets.UTF_8), true);

			String lineaGenerada;
			String caracterSeparador = this.archivoBanco.getPlantilla().getCaracterSeparador();

			boolean tieneSeparador = false;

			String valorCampo;

			if (caracterSeparador != null && caracterSeparador.length() > 0)
			{
				tieneSeparador = true;
			}

			for (PlantillaRegistro registro : registrosExportando)
			{
				lineaGenerada = "";

				if (tipoExportacion == 0 && this.archivoBanco.getRegistrosBaja().contains(registro))
				{
					System.out.print("Registro excluido: " + registro.getIdRegistro());
					continue;

				}

				for (CampoPlantilla campo : registro.getPlantilla().getCampos())
				{
					if (isCampoExportacion(campo, tipoExportacion))
					{

						if (campo.getValorModificado() != null && campo.getValorModificado().length() > 0)
						{
							valorCampo = this.archivoBanco.ajustaLongitudCampo(campo,
									campo.getValorModificado() == null ? "" : campo.getValorModificado());

						}
						else
						{
							valorCampo = this.archivoBanco.ajustaLongitudCampo(campo,
									campo.getValor() == null ? "" : campo.getValor());

						}

						if (campo.getDescripcion().toLowerCase().contains("nombre"))
						{
							valorCampo = utilidades.reemplazarCaracteresExtraños(valorCampo);
						}

						lineaGenerada += valorCampo;

						if (tieneSeparador)
						{
							lineaGenerada += caracterSeparador;

						}

					}

				}

				if (lineaGenerada.length() > 0)
				{

					if (tieneSeparador)
					{
						wr.println(lineaGenerada.substring(0, lineaGenerada.length() - 1));
					}
					else
					{
						wr.println(lineaGenerada);
					}

				}

			}

			wr.close();
			// bw.close();
			// w.close();

			String complemento = "";

			switch (tipoExportacion)
			{
				case 0:
					complemento = "Final_";
					break;

				case 1:

					complemento = "Excluidos_";
					break;

				case 2:
					complemento = "Modificados_";
					break;

				case 3:
					complemento = "Nuevos_";
					break;

				case 4:
					complemento = "Vinculados_";
					break;

			}

			this.archivoLayoutExportacion = new DefaultStreamedContent(new FileInputStream(archivoDescarga),
					"text/plain", complemento + nombreArchivo + extension);

		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	// Verifica si el campo es un campo que está incluido dentro del layout que
	// se ha marcado para la exportación
	public boolean isCampoExportacion(CampoPlantilla campoExportacion, int tipoExportacion)
	{
		List<CampoPlantilla> camposExportando = null;

		switch (tipoExportacion)
		{
			case 0:
				camposExportando = this.camposLayoutSeleccionadoExportar;
				break;

			case 1:
				camposExportando = this.camposLayoutSeleccionadoExportarBajas;
				break;

			case 2:
				camposExportando = this.camposLayoutSeleccionadoExportarModificados;
				break;

			case 3:
				camposExportando = this.camposLayoutSeleccionadoExportarNuevos;
				break;

			case 4:
				camposExportando = this.camposLayoutSeleccionadoExportarVinculados;
				break;

		}

		for (CampoPlantilla campo : camposExportando)
		{
			if (campo.getOrden() == campoExportacion.getOrden())
			{
				return true;

			}
		}

		return false;

	}

	public void limpiarArchivoVinculando()
	{

		this.archivoBancoVinculandoSelec = null;
	}

	public void actionPreparaDialogoVincular()
	{
		this.catArchivosPago = utilidades.getCatalogoArchivosBanco();
		limpiarArchivoVinculando();

	}

	public void onRowSelectArchivoBancoVincular()
	{
		this.archivoBancoVinculandoSelec.updatePlantillaRegistros();
		this.archivoBancoVinculandoSelec.updateRegistros();
	}

	// Se guarda el vinculo del registro selecionado
	public void actionGuardarVinculoRegistro()
	{
		PreparedStatement prep = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionNominas();)
		{
			// El vínculo no podrá hacerse hacia el propio archivo de bancos
			if (this.archivoBancoVinculandoSelec.getIdArchivoPago() == this.archivoBanco.getIdArchivoPago())
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Imposible Vincular", "No puede vincularse un registro hacia su propio archivo."));
				return;
			}

			// Se elimina el vínculo anterior y se crea el nuevo
			prep = conexion
					.prepareStatement("DELETE FROM registropagovinculado WHERE idArchivoPago=? AND idRegistro=? ");

			prep.setInt(1, this.idArchivoPagoSelecVinculando);
			prep.setInt(2, this.registroSelecVinculados.getIdRegistro());

			prep.executeUpdate();

			prep.close();

			prep = conexion.prepareStatement(
					"INSERT INTO registropagovinculado (idArchivoPago, idRegistro, idArchivoPagoVinculo, idRegistroVinculo, Motivo, Observaciones) VALUES (?, ?, ?, ?, ?, ?) ");

			prep.setInt(1, this.archivoBanco.getIdArchivoPago());
			prep.setInt(2, this.registroVinculando.getIdRegistro());
			prep.setInt(3, this.archivoBancoVinculandoSelec.getIdArchivoPago());
			prep.setInt(4, this.registroSelecVinculados.getIdRegistro());

			prep.setString(5, this.registroSelecVinculados.getMotivoVinculo());
			prep.setString(6, this.registroSelecVinculados.getObservaciones());

			prep.executeUpdate();

			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Registro Vinculado", "El registro se ha vinculado exitosamente."));

			this.archivoBanco.detectaRegistrosVinculados();

		} catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al vincular el registro, favor de contactar con el desarrollador del sistema."));

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

	// Remueve el vinculo que contiene el registro seleccionado, en el argumento
	// se pasa el registro que contiene el vinculo,
	public void actionRemoverVinculoRegistro(PlantillaRegistro registro)
	{

		PreparedStatement prep = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionNominas();)
		{
			// Se elimina el vínculo anterior y se crea el nuevo
			prep = conexion
					.prepareStatement("DELETE FROM registropagovinculado WHERE idArchivoPago=? AND idRegistro=? ");

			prep.setInt(1, this.archivoBanco.getIdArchivoPago());
			prep.setInt(2, registro.getIdRegistro());

			prep.executeUpdate();

			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Vínculo Removido", "El registro se ha desvinculado exitosamente."));

			this.archivoBanco.detectaRegistrosVinculados();

		} catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al desvincular el registro, favor de contactar con el desarrollador del sistema."));

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

	public int getAñoSelec()
	{
		return añoSelec;
	}

	public void setAñoSelec(int añoSelec)
	{
		this.añoSelec = añoSelec;
	}

	public int getQnaSelec()
	{
		return qnaSelec;
	}

	public void setQnaSelec(int qnaSelec)
	{
		this.qnaSelec = qnaSelec;
	}

	public List<Plaza> getCatPlazas()
	{
		return catPlazas;
	}

	public void setCatPlazas(List<Plaza> catPlazas)
	{
		this.catPlazas = catPlazas;
	}

	public static long getSerialversionuid()
	{
		return serialVersionUID;
	}

	public CatalogosBean getCatBean()
	{
		return catBean;
	}

	public void setCatBean(CatalogosBean catBean)
	{
		this.catBean = catBean;
	}

	public List<InstrumentoPago> getCatInstrumentosPago()
	{
		return catInstrumentosPago;
	}

	public void setCatInstrumentosPago(List<InstrumentoPago> catInstrumentosPago)
	{
		this.catInstrumentosPago = catInstrumentosPago;
	}

	public Plaza getPlazaSelec()
	{
		return plazaSelec;
	}

	public void setPlazaSelec(Plaza plazaSelec)
	{
		this.plazaSelec = plazaSelec;
	}

	public LayoutVersion getLayoutVersion()
	{
		return layoutVersion;
	}

	public void setLayoutVersion(LayoutVersion layoutVersion)
	{
		this.layoutVersion = layoutVersion;
	}

	public InstrumentoPago getInstrumentoPagoSelec()
	{
		return instrumentoPagoSelec;
	}

	public void setInstrumentoPagoSelec(InstrumentoPago instrumentoPagoSelec)
	{
		this.instrumentoPagoSelec = instrumentoPagoSelec;
	}

	public String getComentariosArchivo()
	{
		return comentariosArchivo;
	}

	public void setComentariosArchivo(String comentariosArchivo)
	{
		this.comentariosArchivo = comentariosArchivo;
	}

	public UploadedFile getArchivo()
	{
		return archivo;
	}

	public void setArchivo(UploadedFile archivo)
	{
		this.archivo = archivo;
	}

	public ArchivoBanco getArchivoBanco()
	{
		return archivoBanco;
	}

	public void setArchivoBanco(ArchivoBanco archivoBanco)
	{
		this.archivoBanco = archivoBanco;
	}

	public PlantillaRegistro getRegistroEditando()
	{
		return registroEditando;
	}

	public void setRegistroEditando(PlantillaRegistro registroEditando)
	{
		this.registroEditando = registroEditando;
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

	public List<PlantillaRegistro> getRegistrosFilterBaja()
	{
		return registrosFilterBaja;
	}

	public void setRegistrosFilterBaja(List<PlantillaRegistro> registrosFilterBaja)
	{
		this.registrosFilterBaja = registrosFilterBaja;
	}

	public PlantillaRegistro getRegistroSelecBaja()
	{
		return registroSelecBaja;
	}

	public void setRegistroSelecBaja(PlantillaRegistro registroSelecBaja)
	{
		this.registroSelecBaja = registroSelecBaja;
	}

	public PlantillaRegistro getRegistroSelecModificados()
	{
		return registroSelecModificados;
	}

	public void setRegistroSelecModificados(PlantillaRegistro registroSelecModificados)
	{
		this.registroSelecModificados = registroSelecModificados;
	}

	public String getDescripcionArchivo()
	{
		return descripcionArchivo;
	}

	public void setDescripcionArchivo(String descripcionArchivo)
	{
		this.descripcionArchivo = descripcionArchivo;
	}

	public List<ArchivoBanco> getArchivosPago()
	{
		return archivosPago;
	}

	public void setArchivosPago(List<ArchivoBanco> archivosPago)
	{
		this.archivosPago = archivosPago;
	}

	public int getIdArchivoPagoSelec()
	{
		return idArchivoPagoSelec;
	}

	public void setIdArchivoPagoSelec(int idArchivoPagoSelec)
	{
		this.idArchivoPagoSelec = idArchivoPagoSelec;
	}

	public List<PlantillaRegistro> getRegistrosFilterModificados()
	{
		return registrosFilterModificados;
	}

	public void setRegistrosFilterModificados(List<PlantillaRegistro> registrosFilterModificados)
	{
		this.registrosFilterModificados = registrosFilterModificados;
	}

	public List<PlantillaRegistro> getRegistrosFilterNuevos()
	{
		return registrosFilterNuevos;
	}

	public void setRegistrosFilterNuevos(List<PlantillaRegistro> registrosFilterNuevos)
	{
		this.registrosFilterNuevos = registrosFilterNuevos;
	}

	public PlantillaRegistro getRegistroSelecNuevos()
	{
		return registroSelecNuevos;
	}

	public void setRegistroSelecNuevos(PlantillaRegistro registroSelecNuevos)
	{
		this.registroSelecNuevos = registroSelecNuevos;
	}

	public List<CampoPlantilla> getCamposLayoutSeleccionadoExportar()
	{
		return camposLayoutSeleccionadoExportar;
	}

	public void setCamposLayoutSeleccionadoExportar(List<CampoPlantilla> camposLayoutSeleccionadoExportar)
	{
		this.camposLayoutSeleccionadoExportar = camposLayoutSeleccionadoExportar;
	}

	public CampoPlantillaConverter getCampoPlantillaConverter()
	{
		return campoPlantillaConverter;
	}

	public void setCampoPlantillaConverter(CampoPlantillaConverter campoPlantillaConverter)
	{
		this.campoPlantillaConverter = campoPlantillaConverter;
	}

	public StreamedContent getArchivoLayoutExportacion()
	{
		return archivoLayoutExportacion;
	}

	public void setArchivoLayoutExportacion(StreamedContent archivoLayoutExportacion)
	{
		this.archivoLayoutExportacion = archivoLayoutExportacion;
	}

	public List<CampoPlantilla> getCamposLayoutSeleccionadoExportarModificados()
	{
		return camposLayoutSeleccionadoExportarModificados;
	}

	public void setCamposLayoutSeleccionadoExportarModificados(
			List<CampoPlantilla> camposLayoutSeleccionadoExportarModificados)
	{
		this.camposLayoutSeleccionadoExportarModificados = camposLayoutSeleccionadoExportarModificados;
	}

	public List<CampoPlantilla> getCamposLayoutSeleccionadoExportarBajas()
	{
		return camposLayoutSeleccionadoExportarBajas;
	}

	public void setCamposLayoutSeleccionadoExportarBajas(List<CampoPlantilla> camposLayoutSeleccionadoExportarBajas)
	{
		this.camposLayoutSeleccionadoExportarBajas = camposLayoutSeleccionadoExportarBajas;
	}

	public List<CampoPlantilla> getCamposLayoutSeleccionadoExportarNuevos()
	{
		return camposLayoutSeleccionadoExportarNuevos;
	}

	public void setCamposLayoutSeleccionadoExportarNuevos(List<CampoPlantilla> camposLayoutSeleccionadoExportarNuevos)
	{
		this.camposLayoutSeleccionadoExportarNuevos = camposLayoutSeleccionadoExportarNuevos;
	}

	public PlantillaRegistro getRegistroSelecVinculados()
	{
		return registroSelecVinculados;
	}

	public void setRegistroSelecVinculados(PlantillaRegistro registroSelecVinculados)
	{
		this.registroSelecVinculados = registroSelecVinculados;
	}

	public List<PlantillaRegistro> getRegistrosFilterVinculados()
	{
		return registrosFilterVinculados;
	}

	public void setRegistrosFilterVinculados(List<PlantillaRegistro> registrosFilterVinculados)
	{
		this.registrosFilterVinculados = registrosFilterVinculados;
	}

	public List<CampoPlantilla> getCamposLayoutSeleccionadoExportarVinculados()
	{
		return camposLayoutSeleccionadoExportarVinculados;
	}

	public void setCamposLayoutSeleccionadoExportarVinculados(
			List<CampoPlantilla> camposLayoutSeleccionadoExportarVinculados)
	{
		this.camposLayoutSeleccionadoExportarVinculados = camposLayoutSeleccionadoExportarVinculados;
	}

	public Plaza getPlazaSelecVinculando()
	{
		return plazaSelecVinculando;
	}

	public void setPlazaSelecVinculando(Plaza plazaSelecVinculando)
	{
		this.plazaSelecVinculando = plazaSelecVinculando;
	}

	public int getAñoSelecVinculando()
	{
		return añoSelecVinculando;
	}

	public void setAñoSelecVinculando(int añoSelecVinculando)
	{
		this.añoSelecVinculando = añoSelecVinculando;
	}

	public int getQnaSelecVinculando()
	{
		return qnaSelecVinculando;
	}

	public void setQnaSelecVinculando(int qnaSelecVinculando)
	{
		this.qnaSelecVinculando = qnaSelecVinculando;
	}

	public List<InstrumentoPago> getCatInstrumentosPagoVinculando()
	{
		return catInstrumentosPagoVinculando;
	}

	public void setCatInstrumentosPagoVinculando(List<InstrumentoPago> catInstrumentosPagoVinculando)
	{
		this.catInstrumentosPagoVinculando = catInstrumentosPagoVinculando;
	}

	public int getIdArchivoPagoSelecVinculando()
	{
		return idArchivoPagoSelecVinculando;
	}

	public void setIdArchivoPagoSelecVinculando(int idArchivoPagoSelecVinculando)
	{
		this.idArchivoPagoSelecVinculando = idArchivoPagoSelecVinculando;
	}

	public LayoutVersion getLayoutVersionVinculando()
	{
		return layoutVersionVinculando;
	}

	public void setLayoutVersionVinculando(LayoutVersion layoutVersionVinculando)
	{
		this.layoutVersionVinculando = layoutVersionVinculando;
	}

	public List<ArchivoBanco> getCatArchivosPago()
	{
		return catArchivosPago;
	}

	public void setCatArchivosPago(List<ArchivoBanco> catArchivosPago)
	{
		this.catArchivosPago = catArchivosPago;
	}

	public List<ArchivoBanco> getCatArchivosPagoFilter()
	{
		return catArchivosPagoFilter;
	}

	public void setCatArchivosPagoFilter(List<ArchivoBanco> catArchivosPagoFilter)
	{
		this.catArchivosPagoFilter = catArchivosPagoFilter;
	}

	public ArchivoBanco getArchivoBancoVinculandoSelec()
	{
		return archivoBancoVinculandoSelec;
	}

	public void setArchivoBancoVinculandoSelec(ArchivoBanco archivoBancoVinculandoSelec)
	{
		this.archivoBancoVinculandoSelec = archivoBancoVinculandoSelec;
	}

	public PlantillaRegistro getRegistroVinculando()
	{
		return registroVinculando;
	}

	public void setRegistroVinculando(PlantillaRegistro registroVinculando)
	{
		this.registroVinculando = registroVinculando;
	}

}
