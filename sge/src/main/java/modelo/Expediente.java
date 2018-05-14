/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import util.utilidades;

/**
 *
 * @author desarolloyc
 */
public class Expediente implements Serializable
{

	private int		idPlaza;
	private String	numeroEmpleado;
	private boolean	RFC;
	private boolean	CURP;
	private boolean	ActNacimiento;
	private boolean	CartillaMilitar;
	private boolean	CredElector;
	private boolean	SolicEmpleo;
	private boolean	MaxEstudios;
	private boolean	CedulaFONAC;
	private boolean	SeguroInstitucional;

	private String	sizeDocsPersonal;
	private String	sizeDocsDiversos;
	private String	sizeDocsHistorico;

	public Expediente(boolean RFC, boolean CURP, boolean ActNacimiento, boolean CartillaMilitar, boolean CredElector,
			boolean SolicEmpleo, boolean MaxEstudios, boolean CedulaFONAC, boolean SeguroInstitucional)
	{
		this.RFC = RFC;
		this.CURP = CURP;
		this.ActNacimiento = ActNacimiento;
		this.CartillaMilitar = CartillaMilitar;
		this.CredElector = CredElector;
		this.SolicEmpleo = SolicEmpleo;
		this.MaxEstudios = MaxEstudios;
		this.CedulaFONAC = CedulaFONAC;
		this.SeguroInstitucional = SeguroInstitucional;
	}

	public void updateInfoArchivosExpediente()
	{
		String rutaDocsPersonal = utilidades.getRutaExpendientesServer() + this.idPlaza + "/" + this.numeroEmpleado
				+ "/Documentos Personales/DocPersonal_" + this.numeroEmpleado + ".pdf";
		String rutaDocsDiversos = utilidades.getRutaExpendientesServer() + this.idPlaza + "/" + this.numeroEmpleado
				+ "/Documentos Diversos/DocDiversos_" + this.numeroEmpleado + ".pdf";
		String rutaDocsHistorico = utilidades.getRutaExpendientesServer() + this.idPlaza + "/" + this.numeroEmpleado
				+ "/Historico_" + this.numeroEmpleado + ".pdf";
		;

		try
		{
			List<File> docs = Arrays.asList(new File(rutaDocsPersonal), new File(rutaDocsDiversos),
					new File(rutaDocsHistorico));

			final DecimalFormat df = new DecimalFormat("#.00");
			
			List<String> strings = docs.parallelStream().map(pdf ->
			{

				{
					if ( ! pdf.exists())
					{
						return " --- ";
					}

					
					long size = pdf.length();

					if (size > 1024000000)
					{

						return "" + df.format(size / 1024000000) + " Gb";
					}
					else if (size > 1024000)
					{
						return "" + df.format(size / 1024000) + " Mb";
					}
					else if (size > 1024)
					{
						return "" + (df.format(size / 1024) + " Kb");
					}
					else
					{
						return "" + (df.format(size) + " bytes");

					}

				}

			}).collect(toList());

			this.sizeDocsPersonal = strings.get(0);
			this.sizeDocsDiversos = strings.get(1);
			this.sizeDocsHistorico = strings.get(2);

		} catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al momento obtener el detalle de los pdf's del expediente, favor de contactar con el desarrollador del sistema."));

			e.printStackTrace();

		}

	}

	private List<String> bitacora;

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
	 * @return the RFC
	 */
	public boolean isRFC()
	{
		return RFC;
	}

	/**
	 * @param RFC
	 *            the RFC to set
	 */
	public void setRFC(boolean RFC)
	{
		this.RFC = RFC;
	}

	/**
	 * @return the CURP
	 */
	public boolean isCURP()
	{
		return CURP;
	}

	/**
	 * @param CURP
	 *            the CURP to set
	 */
	public void setCURP(boolean CURP)
	{
		this.CURP = CURP;
	}

	/**
	 * @return the ActNacimiento
	 */
	public boolean isActNacimiento()
	{
		return ActNacimiento;
	}

	/**
	 * @param ActNacimiento
	 *            the ActNacimiento to set
	 */
	public void setActNacimiento(boolean ActNacimiento)
	{
		this.ActNacimiento = ActNacimiento;
	}

	/**
	 * @return the CartillaMilitar
	 */
	public boolean isCartillaMilitar()
	{
		return CartillaMilitar;
	}

	/**
	 * @param CartillaMilitar
	 *            the CartillaMilitar to set
	 */
	public void setCartillaMilitar(boolean CartillaMilitar)
	{
		this.CartillaMilitar = CartillaMilitar;
	}

	/**
	 * @return the CredElector
	 */
	public boolean isCredElector()
	{
		return CredElector;
	}

	/**
	 * @param CredElector
	 *            the CredElector to set
	 */
	public void setCredElector(boolean CredElector)
	{
		this.CredElector = CredElector;
	}

	/**
	 * @return the SolicEmpleo
	 */
	public boolean isSolicEmpleo()
	{
		return SolicEmpleo;
	}

	/**
	 * @param SolicEmpleo
	 *            the SolicEmpleo to set
	 */
	public void setSolicEmpleo(boolean SolicEmpleo)
	{
		this.SolicEmpleo = SolicEmpleo;
	}

	/**
	 * @return the MaxEstudios
	 */
	public boolean isMaxEstudios()
	{
		return MaxEstudios;
	}

	/**
	 * @param MaxEstudios
	 *            the MaxEstudios to set
	 */
	public void setMaxEstudios(boolean MaxEstudios)
	{
		this.MaxEstudios = MaxEstudios;
	}

	/**
	 * @return the CedulaFONAC
	 */
	public boolean isCedulaFONAC()
	{
		return CedulaFONAC;
	}

	/**
	 * @param CedulaFONAC
	 *            the CedulaFONAC to set
	 */
	public void setCedulaFONAC(boolean CedulaFONAC)
	{
		this.CedulaFONAC = CedulaFONAC;
	}

	/**
	 * @return the SeguroInstitucional
	 */
	public boolean isSeguroInstitucional()
	{
		return SeguroInstitucional;
	}

	/**
	 * @param SeguroInstitucional
	 *            the SeguroInstitucional to set
	 */
	public void setSeguroInstitucional(boolean SeguroInstitucional)
	{
		this.SeguroInstitucional = SeguroInstitucional;
	}

	public int getIdPlaza()
	{
		return idPlaza;
	}

	public void setIdPlaza(int idPlaza)
	{
		this.idPlaza = idPlaza;
	}

	public String getNumeroEmpleado()
	{
		return numeroEmpleado;
	}

	public void setNumeroEmpleado(String numeroEmpleado)
	{
		this.numeroEmpleado = numeroEmpleado;
	}

	public String getSizeDocsPersonal()
	{
		return sizeDocsPersonal;
	}

	public void setSizeDocsPersonal(String sizeDocsPersonal)
	{
		this.sizeDocsPersonal = sizeDocsPersonal;
	}

	public String getSizeDocsDiversos()
	{
		return sizeDocsDiversos;
	}

	public void setSizeDocsDiversos(String sizeDocsDiversos)
	{
		this.sizeDocsDiversos = sizeDocsDiversos;
	}

	public String getSizeDocsHistorico()
	{
		return sizeDocsHistorico;
	}

	public void setSizeDocsHistorico(String sizeDocsHistorico)
	{
		this.sizeDocsHistorico = sizeDocsHistorico;
	}

}
