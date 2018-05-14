/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.portal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.primefaces.component.fileupload.FileUpload;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import modelo.Expediente;
import modelo.Sesion;
import resources.DataBase;
import util.FacesUtils;
import util.utilidades;

/**
 *
 * @author desarolloyc
 */
@ManagedBean
@ViewScoped

public class ingresoDocsBean implements Serializable
{

	private String				tipoDocumento;
	private List<UploadedFile>	archivosUpload;
	private boolean				checkRFC;
	private boolean				checkCURP;
	private boolean				checkActNacimiento;
	private boolean				checkCartillaMilitar;
	private boolean				checkCredElector;
	private boolean				checkSolicEmpleo;
	private boolean				checkMaxEstudios;
	private boolean				checkCedulaFONAC;
	private boolean				checkSeguroInstitucional;
	private String				comentariosDocumento;

	// indicadores para el control del bloqueo de interfaz e interacción
	private boolean				interfazDisabled;
	private boolean				fileUploadDisabled;
	private boolean				listaTipoDisables;
	private boolean				panelDocsPersonalesActivo;
	private boolean				botonCargarInactivo;
	private boolean				uploadedFileRendered;
	private String				textoBotonCargarArchivo;
	private boolean				copiandoArchivoStatus;
	private FileUpload			fileUpload;

	public ingresoDocsBean()
	{
		setBotonCargarInactivo(true);
		setUploadedFileRendered(false);
		setTextoBotonCargarArchivo("Cargar Archivo");
		setListaTipoDisables(false);
		setFileUploadDisabled(true);
	}

	public void uploadFile(FileUploadEvent event)
	{
		if (event.getFile() != null)
		{
			Sesion sesion = (Sesion) FacesUtils.getManagedBean("Sesion");

			setCopiandoArchivoStatus(true);

			UploadedFile archivoSubido = event.getFile();
			System.out.println("Se ha subido el archivo de nombre de " + event.getFile().getFileName());

			idxArchivoDigital idxArchivoBean = (idxArchivoDigital) FacesUtils.getManagedBean("idxArchivoDigital");
			String tituloMovimiento;

			String rutaArchivo = utilidades.getRutaExpendientesServer()
					+ idxArchivoBean.getTrabajadorSelection().getPlaza().getIdPlaza() + "/"
					+ idxArchivoBean.getTrabajadorSelection().getNumEmpleado() + "/";

			try
			{

				if (tipoDocumento.equals("docPer"))
				{
					rutaArchivo += "Documentos Personales/";
					tituloMovimiento = "Documentos Personales";
				}
				else
				{
					rutaArchivo += "Documentos Diversos/";
					tituloMovimiento = "Documentos Diversos";
				}

				// extraigo el archivo base que contenga el directorio (al cual
				// se concatenará el nuevo)
				File archivoDirectorioComprobacion = new File(rutaArchivo);

				if (!archivoDirectorioComprobacion.exists())
				{
					archivoDirectorioComprobacion.mkdirs();

				}

				if (tipoDocumento.equals("docPer"))
				{
					rutaArchivo += "DocPersonal_";
				}
				else
				{
					rutaArchivo += "DocDiversos_";
				}

				rutaArchivo += idxArchivoBean.getTrabajadorSelection().getNumEmpleado() + ".pdf";

				String rutaArchivoTmpServidor = rutaArchivo.substring(0, rutaArchivo.length() - 4) + "-tempconcat.pdf";
				String rutaArchivoTmpUnion = rutaArchivo.substring(0, rutaArchivo.length() - 4)
						+ "-tempconcatUnion.pdf";

				copiarArchivo(rutaArchivoTmpServidor, archivoSubido.getInputstream());

				File archivoTmpServidor = new File(rutaArchivoTmpServidor);

				archivoDirectorioComprobacion = new File(rutaArchivo);

				if (archivoDirectorioComprobacion.exists())
				{
					// cargo los dos archivos y los concateno
					try
					{

						// creo el objeto Reader del archivo que se encuentra
						// encriptado en la carpeta (al que se añadirán las
						// nuevas páginas)
				/*		PdfReader reader1 = new PdfReader(rutaArchivo, "4832indesalud".getBytes());

						// creo el Reader 2 con el nuevo documento a
						// concatenarse

						PdfReader reader2 = null;

						try
						{
							reader2 = new PdfReader(new RandomAccessFileOrArray(rutaArchivoTmpServidor), null);

						} catch (Exception e)
						{
							e.printStackTrace();
							
							reader2 = new PdfReader(new RandomAccessFileOrArray(rutaArchivoTmpServidor),
									"4832indesalud".getBytes());
						}

						Document document = new Document();

						// se realiza la concatenación de los archivos
						PdfCopy copy = new PdfCopy(document, new FileOutputStream(rutaArchivoTmpUnion));

						copy.setEncryption("4832indesalud".getBytes(), "4832indesalud".getBytes(),
								PdfWriter.ALLOW_COPY | PdfWriter.ALLOW_PRINTING, 0);

						document.open();

						int n = reader1.getNumberOfPages();

						for (int page = 0; page < n;)
						{
							copy.addPage(copy.getImportedPage(reader1, ++page));
						}

						copy.freeReader(reader1);

						n = reader2.getNumberOfPages();

						for (int page = 0; page < n;)
						{
							copy.addPage(copy.getImportedPage(reader2, ++page));
						}

						copy.freeReader(reader2);*/

						// cualquiera de los dos métodos es funcional, ya sea
						// mediante la importación de cada página como la
						// adición del documento completo
						// en caso de buscar un resultado específico sería bueno
						// tomar tiempo para verificar las diferencias absolutas
						// a nivel de lo que incluye la copia, vínculos,
						// enlaces, formularios, etc...
						/*
						 * copy.addDocument(reader1); copy.addDocument(reader2);
						 */
					/*	copy.close();
						reader1.close();
						reader2.close();

						document.close();

						// borro el archivo anterior
						archivoDirectorioComprobacion.delete();

						// ahora renombrar el archivo, con el nuevo nombre
						// generado que incluye la fecha y hora
						File original = new File(rutaArchivoTmpUnion);
						File renombre = new File(rutaArchivo);

						original.renameTo(renombre);

						archivoTmpServidor.delete();*/

					} catch (Exception e)
					{
						e.printStackTrace();
						
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL,
								"Registro de Documentación",
								"Se ha generado una excepción al concatenar los documentos, favor de contactar con el desarrollador del sistema."));
						setCopiandoArchivoStatus(false);
						return;
					}

				}
				else
				{

					// se carga el archivo a ingresar en el Reader y encripta
					// directamente en la nueva ruta (genera una copia
					// encriptada)
/*
					PdfReader reader = null;

					try
					{
						reader = new PdfReader(new RandomAccessFileOrArray(rutaArchivoTmpServidor), null);
					} catch (BadPasswordException e)
					{
						e.printStackTrace();
						
						reader = new PdfReader(new RandomAccessFileOrArray(rutaArchivoTmpServidor),
								"4832indesalud".getBytes());
					}

					PdfEncryptor.encrypt(reader, new FileOutputStream(rutaArchivo), "4832indesalud".getBytes(),
							"4832indesalud".getBytes(), PdfWriter.ALLOW_PRINTING | PdfWriter.ALLOW_COPY, false);

					archivoTmpServidor.delete(); */

				}

				// si el proceso ha finalizado exitosamente se hace el grabado
				// de los atributos de documentos personales en su caso
				if (tipoDocumento.equals("docPer"))
				{

					PreparedStatement prep = null;
					ResultSet rBD = null;

					try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database"))
							.getConnectionArchivo();)
					{

						prep = conexion
								.prepareStatement("SELECT * FROM docpersonales WHERE NumEmpleado=? AND idPlaza=?");

						prep.setString(1, idxArchivoBean.getTrabajadorSelection().getNumEmpleado());
						prep.setInt(2, idxArchivoBean.getTrabajadorSelection().getPlaza().getIdPlaza());

						rBD = prep.executeQuery();

						if (rBD.next())
						{
							prep.close();

							prep = conexion.prepareStatement(
									"UPDATE docpersonales SET docRFC=?, docCURP=?, docActa=?, docCartilla=?, docElector=?, docSolicitudEmpleo=?, docMaximosEstudios=?, docInscripcionSeguro=?, docCedulaFONAC=?  WHERE NumEmpleado=? AND idPlaza=?         ");
							prep.setBoolean(1, checkRFC);
							prep.setBoolean(2, checkCURP);
							prep.setBoolean(3, checkActNacimiento);
							prep.setBoolean(4, checkCartillaMilitar);
							prep.setBoolean(5, checkCredElector);
							prep.setBoolean(6, checkSolicEmpleo);
							prep.setBoolean(7, checkMaxEstudios);
							prep.setBoolean(8, checkSeguroInstitucional);
							prep.setBoolean(9, checkCedulaFONAC);

							prep.setString(10, idxArchivoBean.getTrabajadorSelection().getNumEmpleado());
							prep.setInt(11, idxArchivoBean.getTrabajadorSelection().getPlaza().getIdPlaza());

						}
						else
						{

							prep.close();

							prep = conexion.prepareStatement(
									"INSERT INTO docpersonales (NumEmpleado,docRFC,docCURP,docActa,docCartilla,docElector,docSolicitudEmpleo,docMaximosEstudios,docInscripcionSeguro,docCedulaFONAC,idPlaza) VALUES (?,?,?,?,?,?,?,?,?,?,?)  ");
							prep.setBoolean(2, checkRFC);
							prep.setBoolean(3, checkCURP);
							prep.setBoolean(4, checkActNacimiento);
							prep.setBoolean(5, checkCartillaMilitar);
							prep.setBoolean(6, checkCredElector);
							prep.setBoolean(7, checkSolicEmpleo);
							prep.setBoolean(8, checkMaxEstudios);
							prep.setBoolean(9, checkSeguroInstitucional);
							prep.setBoolean(10, checkCedulaFONAC);
							prep.setInt(11, idxArchivoBean.getTrabajadorSelection().getPlaza().getIdPlaza());

							prep.setString(1, idxArchivoBean.getTrabajadorSelection().getNumEmpleado());

						}

						prep.executeUpdate();

					} catch (Exception e)
					{
						e.printStackTrace();
						
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL,
								"Registro de Documentación",
								"Se ha generado una excepción al actualizar los indicadores de documentos personales, favor de contactar con el desarrollador del sistema."));
						setCopiandoArchivoStatus(false);

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
								// e.printStackTrace();
							}
						}

					}

				}

				utilidades.registrarEnBitacora(Integer.parseInt(sesion.getIdUsuario()),
						idxArchivoBean.getTrabajadorSelection().getNumEmpleado(),
						idxArchivoBean.getTrabajadorSelection().getPlaza().getIdPlaza(),
						"Se añadieron " + tituloMovimiento, comentariosDocumento);
				idxArchivoBean.getDatosExpedienteTrabajador(idxArchivoBean.getTrabajadorSelection().getNumEmpleado(),
						idxArchivoBean.getTrabajadorSelection().getPlaza().getIdPlaza());

				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Proceso Exitoso", "El documento se ha añadido exitosamente."));

				limpiarInterfaz();

				// terminado el proceso se genera el archivo histórico del
				// trabajador que es la unión de los dos archivos diversos y
				// personales
				generarArchivoHistorico(idxArchivoBean.getTrabajadorSelection().getNumEmpleado(),
						idxArchivoBean.getTrabajadorSelection().getPlaza().getIdPlaza());

			} catch (Exception e)
			{
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_FATAL, "Registro de Documentación",
								"Se ha generado una excepción, favor de contactar con el desarrollador del sistema."));
				e.printStackTrace();
				setCopiandoArchivoStatus(false);
			}

		}

		setCopiandoArchivoStatus(false);

	}

	private void copiarArchivo(String ruta, InputStream in)
	{
		try
		{
			OutputStream out = new FileOutputStream(new File(ruta));

			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = in.read(bytes)) != -1)
			{
				out.write(bytes, 0, read);
			}

			in.close();
			out.flush();
			out.close();

		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void generarArchivoHistorico(String NumEmpleado, int idPlaza)
	{
		String rutaPersonal = utilidades.getRutaExpendientesServer() + idPlaza + "/" + NumEmpleado
				+ "/Documentos Personales/DocPersonal_" + NumEmpleado + ".pdf";
		String rutaDiverso = utilidades.getRutaExpendientesServer() + idPlaza + "/" + NumEmpleado
				+ "/Documentos Diversos/DocDiversos_" + NumEmpleado + ".pdf";
		String rutaHistorico = utilidades.getRutaExpendientesServer() + idPlaza + "/" + NumEmpleado + "/Historico_"
				+ NumEmpleado + ".pdf";
		boolean existePersonal = false;
		boolean existeDiverso = false;

		File archivoHistorico = new File(rutaHistorico);
		archivoHistorico.delete();

		File filePersonal = new File(rutaPersonal);
		File fileDiverso = new File(rutaDiverso);

		if (filePersonal.exists())
		{
			existePersonal = true;
		}

		if (fileDiverso.exists())
		{
			existeDiverso = true;
		}

		if (!existePersonal && !existeDiverso)
		{
			return;
		}

		try
		{
/*
			PdfReader reader1 = null;

			if (existePersonal)
			{
				reader1 = new PdfReader(rutaPersonal, "4832indesalud".getBytes());
			}

			PdfReader reader2 = null;

			if (existeDiverso)
			{
				reader2 = new PdfReader(rutaDiverso, "4832indesalud".getBytes());
			}

			Document document = new Document();

			// se realiza la concatenación de los archivos
			PdfCopy copy = new PdfCopy(document, new FileOutputStream(rutaHistorico));

			copy.setEncryption("4832indesalud".getBytes(), "4832indesalud".getBytes(),
					PdfWriter.ALLOW_COPY | PdfWriter.ALLOW_PRINTING, 0);

			document.open();

			int n = 0;

			if (existePersonal)
			{
				n = reader1.getNumberOfPages();

				for (int page = 0; page < n;)
				{
					copy.addPage(copy.getImportedPage(reader1, ++page));
				}

				copy.freeReader(reader1);
			}

			if (existeDiverso)
			{
				n = reader2.getNumberOfPages();

				for (int page = 0; page < n;)
				{
					copy.addPage(copy.getImportedPage(reader2, ++page));
				}

				copy.freeReader(reader2);
			}

			copy.close();

			if (existePersonal)
			{
				reader1.close();
			}

			if (existeDiverso)
			{
				reader2.close();
			}

			document.close();
*/
		} catch (Exception e)
		{
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_FATAL, "Generación de Archivo Histórico",
							"Se ha generado una excepción, favor de contactar con el desarrollador del sistema."));

		}
	}

	public void changeListenerListaTipoDocumento(ValueChangeEvent evt)
	{
		if (evt.getNewValue() == null)
		{
			setBotonCargarInactivo(true);
			setPanelDocsPersonalesActivo(false);
			return;
		}

		setInterfazDisabled(false);

		switch (evt.getNewValue().toString())
		{
			case "docPer":
				setPanelDocsPersonalesActivo(true);
				actualizarPanelChecks();
				setBotonCargarInactivo(false);
				break;
			case "docDiv":
				setPanelDocsPersonalesActivo(false);
				setBotonCargarInactivo(false);
				break;
		}

	}

	public void cerrarDialogo(CloseEvent evt)
	{
		fileUpload.setSubmittedValue(null);
		fileUpload.clearInitialState();

		if (copiandoArchivoStatus)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
					"Concatenando Documento",
					"El documento se está concatenando en el servidor, se recomienda no trabajar con el expediente hasta que el proceso termine."));
		}

		limpiarInterfaz();

	}

	public void limpiarInterfaz()
	{
		setTipoDocumento(null);
		setArchivosUpload(null);
		setPanelDocsPersonalesActivo(false);
		setComentariosDocumento(null);
		setBotonCargarInactivo(true);
		setUploadedFileRendered(false);
		setInterfazDisabled(true);
		setListaTipoDisables(false);
		setTextoBotonCargarArchivo("Cargar Archivo");
		setFileUploadDisabled(true);
	}

	public void actualizarPanelChecks()
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

		idxArchivoDigital idxArchivoBean = (idxArchivoDigital) FacesUtils.getManagedBean("idxArchivoDigital");

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionArchivo();)
		{

			PreparedStatement prep = conexion
					.prepareStatement(" SELECT * FROM docpersonales WHERE NumEmpleado=? AND idPlaza=?");

			prep.setString(1, idxArchivoBean.getTrabajadorSelection().getNumEmpleado());
			prep.setInt(2, idxArchivoBean.getTrabajadorSelection().getPlaza().getIdPlaza());

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{
				Expediente nExpediente = new Expediente(rBD.getBoolean("docRFC"), rBD.getBoolean("docCURP"),
						rBD.getBoolean("docActa"), rBD.getBoolean("docCartilla"), rBD.getBoolean("docElector"),
						rBD.getBoolean("docSolicitudEmpleo"), rBD.getBoolean("docMaximosEstudios"),
						rBD.getBoolean("docCedulaFONAC"), rBD.getBoolean("docInscripcionSeguro"));
				idxArchivoBean.getTrabajadorSelection().setExpediente(nExpediente);

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
		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	public void activarUploadedFile(ActionEvent evt)
	{

		switch (textoBotonCargarArchivo)
		{
			case "Cancelar":

				fileUpload.setSubmittedValue(null);
				fileUpload.clearInitialState();

				if (copiandoArchivoStatus)
				{
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_FATAL, "Adjuntando Documento",
									"El documento se está concatenando en el servidor, por favor espere."));
					return;
				}

				setInterfazDisabled(false);
				setListaTipoDisables(false);
				setFileUploadDisabled(true);
				setTextoBotonCargarArchivo("Cargar Archivo");
				break;

			default:
				setInterfazDisabled(true);
				setListaTipoDisables(true);
				setFileUploadDisabled(false);
				setTextoBotonCargarArchivo("Cancelar");
				break;
		}

	}

	/**
	 * @return the archivosUpload
	 */
	public List<UploadedFile> getArchivosUpload()
	{
		return archivosUpload;
	}

	/**
	 * @param archivosUpload
	 *            the archivosUpload to set
	 */
	public void setArchivosUpload(List<UploadedFile> archivosUpload)
	{
		this.archivosUpload = archivosUpload;
	}

	/**
	 * @return the tipoDocumento
	 */
	public String getTipoDocumento()
	{
		return tipoDocumento;
	}

	/**
	 * @param tipoDocumento
	 *            the tipoDocumento to set
	 */
	public void setTipoDocumento(String tipoDocumento)
	{
		this.tipoDocumento = tipoDocumento;
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
	 * @return the panelDocsPersonalesActivo
	 */
	public boolean isPanelDocsPersonalesActivo()
	{
		return panelDocsPersonalesActivo;
	}

	/**
	 * @param panelDocsPersonalesActivo
	 *            the panelDocsPersonalesActivo to set
	 */
	public void setPanelDocsPersonalesActivo(boolean panelDocsPersonalesActivo)
	{
		this.panelDocsPersonalesActivo = panelDocsPersonalesActivo;
	}

	/**
	 * @return the comentariosDocumento
	 */
	public String getComentariosDocumento()
	{
		return comentariosDocumento;
	}

	/**
	 * @param comentariosDocumento
	 *            the comentariosDocumento to set
	 */
	public void setComentariosDocumento(String comentariosDocumento)
	{
		this.comentariosDocumento = comentariosDocumento;
	}

	/**
	 * @return the botonCargarInactivo
	 */
	public boolean isBotonCargarInactivo()
	{
		return botonCargarInactivo;
	}

	/**
	 * @param botonCargarInactivo
	 *            the botonCargarInactivo to set
	 */
	public void setBotonCargarInactivo(boolean botonCargarInactivo)
	{
		this.botonCargarInactivo = botonCargarInactivo;
	}

	/**
	 * @return the uploadedFileRendered
	 */
	public boolean isUploadedFileRendered()
	{
		return uploadedFileRendered;
	}

	/**
	 * @param uploadedFileRendered
	 *            the uploadedFileRendered to set
	 */
	public void setUploadedFileRendered(boolean uploadedFileRendered)
	{
		this.uploadedFileRendered = uploadedFileRendered;
	}

	/**
	 * @return the interfazDisabled
	 */
	public boolean isInterfazDisabled()
	{
		return interfazDisabled;
	}

	/**
	 * @param interfazDisabled
	 *            the interfazDisabled to set
	 */
	public void setInterfazDisabled(boolean interfazDisabled)
	{
		this.interfazDisabled = interfazDisabled;
	}

	/**
	 * @return the textoBotonCargarArchivo
	 */
	public String getTextoBotonCargarArchivo()
	{
		return textoBotonCargarArchivo;
	}

	/**
	 * @param textoBotonCargarArchivo
	 *            the textoBotonCargarArchivo to set
	 */
	public void setTextoBotonCargarArchivo(String textoBotonCargarArchivo)
	{
		this.textoBotonCargarArchivo = textoBotonCargarArchivo;
	}

	/**
	 * @return the copiandoArchivoStatus
	 */
	public boolean isCopiandoArchivoStatus()
	{
		return copiandoArchivoStatus;
	}

	/**
	 * @param copiandoArchivoStatus
	 *            the copiandoArchivoStatus to set
	 */
	public void setCopiandoArchivoStatus(boolean copiandoArchivoStatus)
	{
		this.copiandoArchivoStatus = copiandoArchivoStatus;
	}

	/**
	 * @return the fileUpload
	 */
	public FileUpload getFileUpload()
	{
		return fileUpload;
	}

	/**
	 * @param fileUpload
	 *            the fileUpload to set
	 */
	public void setFileUpload(FileUpload fileUpload)
	{
		this.fileUpload = fileUpload;
	}

	/**
	 * @return the listaTipoDisables
	 */
	public boolean isListaTipoDisables()
	{
		return listaTipoDisables;
	}

	/**
	 * @param listaTipoDisables
	 *            the listaTipoDisables to set
	 */
	public void setListaTipoDisables(boolean listaTipoDisables)
	{
		this.listaTipoDisables = listaTipoDisables;
	}

	/**
	 * @return the fileUploadDisabled
	 */
	public boolean isFileUploadDisabled()
	{
		return fileUploadDisabled;
	}

	/**
	 * @param fileUploadDisabled
	 *            the fileUploadDisabled to set
	 */
	public void setFileUploadDisabled(boolean fileUploadDisabled)
	{
		this.fileUploadDisabled = fileUploadDisabled;
	}

}
