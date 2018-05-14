package modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import resources.DataBase;
import util.FacesUtils;
import util.utilidades;

public class ArchivoBanco implements Serializable
{

	/**
	 * 
	 */
	private static final long		serialVersionUID	= 1L;

	private int						idArchivoPago;
	private String					descripcion;
	private String					nombreArchivo;
	private Plaza					plaza;
	private String					idInstrumentoPago;
	private int						año;
	private int						qna;
	private String					observaciones;
	private Plantilla				plantilla;

	private List<PlantillaRegistro>	registros;

	private BigDecimal				netoTotalOriginal;
	private String					netoTotalOriginalString;
	private BigDecimal				netoTotal;
	private String					netoTotalString;
	private BigDecimal				netoTotalBaja;
	private String					netoTotalBajaString;
	private BigDecimal				netoTotalNuevos;
	private String					netoTotalNuevosString;

	private List<PlantillaRegistro>	registrosBaja;

	private List<PlantillaRegistro>	registrosModificados;

	private List<PlantillaRegistro>	registrosNuevos;

	private List<PlantillaRegistro>	registrosVinculados;

	private String					erroresGenerales;

	private String					erroresNetoOriginal;
	private String					erroresNeto;
	private String					erroresNetoBaja;
	private String					erroresNetoNuevos;

	public ArchivoBanco()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public ArchivoBanco(int idArchivoPago, String descripcion, Plaza plaza, String idInstrumentoPago, int año, int qna,
			String observaciones, Plantilla plantilla)
	{
		super();
		this.idArchivoPago = idArchivoPago;
		this.descripcion = descripcion;
		this.plaza = plaza;
		this.idInstrumentoPago = idInstrumentoPago;
		this.año = año;
		this.qna = qna;
		this.observaciones = observaciones;
		this.plantilla = plantilla;
	}

	public void updatePlantillaRegistros()
	{
		this.plantilla = utilidades.getPlantillaLayout(this.plantilla.getIdPlantilla());
		this.plantilla.updateCampos();
	}

	// Devuelve un registro en específico, extrayéndolo directamente de la base
	// de datos
	public List<PlantillaRegistro> getRegistroFromBD(int idRegistro)
	{
		return utilidades.getRegistrosArchivoBanco(this, new Integer(idRegistro));

	}

	public void updateRegistros()
	{
		this.registros = utilidades.getRegistrosArchivoBanco(this, null);
		detectaRegistrosModificados();
		detectaRegistrosBaja();
		detectaRegistrosNuevos();
		detectaRegistrosVinculados();

	}

	public PlantillaRegistro añadirRegistro(PlantillaRegistro registroADuplicar)
	{
		if (this.registros == null)
		{
			this.registros = new ArrayList<>();
		}

		if (this.registrosNuevos == null)
		{
			this.registrosNuevos = new ArrayList<>();
		}

		PlantillaRegistro registroNuevo = utilidades.añadirRegistroArchivoBanco(this, registroADuplicar);

		// para evitar que los registros que se han duplicado y estaban de baja
		// aparezcan igual de baja
		registroNuevo.setStatus(0);

		this.registros.add(registroNuevo);
		this.registrosNuevos.add(registroNuevo);

		calculaTotalNeto();

		return registroNuevo;

	}

	public void detectaRegistrosNuevos()
	{
		PreparedStatement prep = null;
		ResultSet rBD = null;

		this.registrosNuevos = new ArrayList<>();

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionNominas();)
		{

			prep = conexion
					.prepareStatement(" SELECT * FROM registropagonuevo WHERE idArchivoPago=? ORDER BY idRegistro ASC");

			prep.setInt(1, this.idArchivoPago);

			rBD = prep.executeQuery();

			if (rBD.next())
			{
				do
				{
					this.registrosNuevos.add(this.registros.get(rBD.getInt("idRegistro") - 1));

				}
				while (rBD.next());
			}

		} catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al momento de detectar los registros nuevos, favor de contactar con el desarrollador del sistema."));
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

	public void detectaRegistrosModificados()
	{
		PreparedStatement prep = null;
		ResultSet rBD = null;

		this.registrosModificados = new ArrayList<>();

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionNominas())
		{

			prep = conexion.prepareStatement(
					" SELECT idRegistro, idArchivoPago FROM archivopagovalores WHERE idArchivoPago=? AND ValorModificado IS NOT NULL GROUP BY idRegistro  ORDER BY idRegistro ASC");

			prep.setInt(1, this.idArchivoPago);

			rBD = prep.executeQuery();

			if (rBD.next())
			{
				do
				{
					this.registrosModificados.add(this.registros.get(rBD.getInt("idRegistro") - 1));

				}
				while (rBD.next());
			}

		} catch (Exception e)
		{
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al detectar los registros que han sido modificados, favor de contactar con el desarrollador del sistema."));
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

	public void detectaRegistrosBaja()
	{

		PreparedStatement prep = null;
		ResultSet rBD = null;

		this.registrosBaja = new ArrayList<>();

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionNominas();)
		{

			prep = conexion.prepareStatement(
					" SELECT * FROM registropagoinactivo WHERE idArchivoPago=? ORDER BY idRegistro ASC");

			prep.setInt(1, this.idArchivoPago);

			rBD = prep.executeQuery();

			if (rBD.next())
			{
				do
				{
					this.registrosBaja.add(this.registros.get(rBD.getInt("idRegistro") - 1));

				}
				while (rBD.next());
			}

		} catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al momento de detectar los registros que han sido dados de baja, favor de contactar con el desarrollador del sistema."));
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

	public void detectaRegistrosVinculados()
	{

		PreparedStatement prep = null;
		ResultSet rBD = null;

		this.registrosVinculados = new ArrayList<>();

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionNominas();)
		{

			prep = conexion.prepareStatement(
					" SELECT rpv.idArchivoPago AS idArchivoPagoReg, rpv.idRegistro, rpv.idArchivoPagoVinculo, rpv.idRegistroVinculo, rpv.Motivo, rpv.Observaciones, ap.* FROM nominas.registropagovinculado rpv, nominas.archivopago ap WHERE rpv.idArchivoPagoVinculo = ap.idArchivoPago AND rpv.idArchivoPago=? ORDER BY rpv.idRegistro ASC ");

			prep.setInt(1, this.idArchivoPago);

			rBD = prep.executeQuery();

			ArchivoBanco archivoVinculado = null;

			if (rBD.next())
			{
				do
				{
					PlantillaRegistro regVinculado = this.registros.get(rBD.getInt("idRegistro") - 1);
					this.registrosVinculados.add(regVinculado);

					// Se obtiene el registro del archivo de banco que se
					// encuentra vinculado
					archivoVinculado = utilidades.getArchivoBanco(rBD.getInt("idPlaza"), rBD.getInt("Ano"),
							rBD.getInt("Qna"), rBD.getString("idInstrumentoPago"), rBD.getInt("idArchivoPago"));

					archivoVinculado.updatePlantillaRegistros();

					regVinculado.setRegistrosVinculados(
							archivoVinculado.getRegistroFromBD(rBD.getInt("idRegistroVinculo")));

				}
				while (rBD.next());
			}

		} catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al momento de detectar los registros vinculados, favor de contactar con el desarrollador del sistema."));
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

	public void cambiaStatusRegistro(PlantillaRegistro registro)
	{
		registro.intercambioStatus();

	}

	public void calculaTotalNeto()
	{

		this.erroresGenerales = "";
		this.erroresNetoOriginal = "";
		this.erroresNeto = "";
		this.erroresNetoBaja = "";
		this.erroresNetoNuevos = "";

		BigDecimal big100 = new BigDecimal("100.00");

		this.netoTotalOriginal = new BigDecimal("0.00");
		this.netoTotalOriginalString = "0.00";
		this.netoTotal = new BigDecimal("0.00");
		this.netoTotalString = "0.00";
		this.netoTotalBaja = new BigDecimal("0.00");
		this.netoTotalBajaString = "0.00";

		if (this.registros != null)
		{
			String netoRegistro = null;

			for (PlantillaRegistro registro : this.registros)
			{

				if (this.registrosNuevos != null && this.registrosNuevos.contains(registro))
				{
					continue;
				}

				netoRegistro = registro.getValorPorDescripcionContains("neto");

				BigDecimal netoRegBig = null;

				try
				{

					if (netoRegistro == null || netoRegistro.trim().isEmpty())
					{
						this.erroresNetoOriginal += "Registro Original: " + registro.getIdRegistro()
								+ ", neto vacío. </br>";

					}
					else
					{
						netoRegBig = new BigDecimal(netoRegistro.trim());
					}

				} catch (NumberFormatException e)
				{
					this.erroresNetoOriginal += "Registro Original: " + registro.getIdRegistro() + ", neto: "
							+ netoRegistro + "</br>";
					continue;
				}

				if (netoRegistro != null && netoRegBig != null)
				{
					if (netoRegistro.contains("."))
					{
						this.netoTotalOriginal = this.netoTotalOriginal.add(netoRegBig);
					}
					else
					{
						this.netoTotalOriginal = this.netoTotalOriginal.add(netoRegBig.divide(big100));
					}

				}

			}

		}

		if (!this.erroresNetoOriginal.isEmpty())
		{
			this.netoTotalOriginalString = "Errores en netos";
		}
		else
		{
			this.netoTotalOriginalString = utilidades.formatoMoneda(this.netoTotalOriginal.toPlainString());

		}

		if (this.registros != null)
		{
			String netoRegistro = null;

			for (PlantillaRegistro registro : this.registros)
			{
				if (registro.getStatus() == -1)
				{
					continue;
				}

				netoRegistro = registro.getValorPorDescripcionContainsConValorModificado("neto");

				BigDecimal netoRegBig = null;

				try
				{
					if (netoRegistro == null || netoRegistro.trim().isEmpty())
					{
						this.erroresNeto += "Registro Final: " + registro.getIdRegistro() + ", neto vacío. </br>";

					}
					else
					{
						netoRegBig = new BigDecimal(netoRegistro.trim());
					}

				} catch (NumberFormatException e)
				{
					this.erroresNeto += "Registro Final: " + registro.getIdRegistro() + ", neto: " + netoRegistro
							+ "<br/>";
					continue;
				}

				if (netoRegistro != null && netoRegBig != null)
				{
					if (netoRegistro.contains("."))
					{
						this.netoTotal = this.netoTotal.add(netoRegBig);
					}
					else
					{
						this.netoTotal = this.netoTotal.add(netoRegBig.divide(big100));
					}

				}

			}

		}

		if (!this.erroresNeto.isEmpty())
		{
			this.netoTotalString = "Errores en netos";
		}
		else
		{
			this.netoTotalString = utilidades.formatoMoneda(this.netoTotal.toPlainString());
		}

		if (this.registrosBaja != null)
		{
			String netoRegistro = null;

			for (PlantillaRegistro registro : this.registrosBaja)
			{

				netoRegistro = registro.getValorPorDescripcionContains("neto");

				BigDecimal netoRegBig = null;

				try
				{

					if (netoRegistro == null || netoRegistro.trim().isEmpty())
					{
						this.erroresNetoBaja += "Registro Final: " + registro.getIdRegistro() + ", neto vacío. </br>";
					}
					else
					{
						netoRegBig = new BigDecimal(netoRegistro.trim());
					}

				} catch (NumberFormatException e)
				{
					this.erroresNetoBaja += "Registro Baja: " + registro.getIdRegistro() + ", neto: " + netoRegistro
							+ "<br/>";
					continue;
				}

				if (netoRegistro != null && netoRegBig != null)
				{

					if (netoRegistro.contains("."))
					{
						this.netoTotalBaja = this.netoTotalBaja.add(netoRegBig);
					}
					else
					{
						this.netoTotalBaja = this.netoTotalBaja.add(netoRegBig.divide(big100));
					}

				}

			}

		}

		if (!this.erroresNetoBaja.isEmpty())
		{
			this.netoTotalBajaString = "Errores en netos";
		}
		else
		{
			this.netoTotalBajaString = utilidades.formatoMoneda(this.netoTotalBaja.toPlainString());
		}

		this.netoTotalNuevos = new BigDecimal("0.00");

		if (this.registrosNuevos != null)
		{
			String netoRegistro = null;

			for (PlantillaRegistro registro : this.registrosNuevos)
			{

				netoRegistro = registro.getValorPorDescripcionContainsConValorModificado("neto");

				BigDecimal netoRegBig = null;

				try
				{
					if (netoRegistro == null || netoRegistro.trim().isEmpty())
					{
						this.erroresNetoNuevos += "Registro Nuevo: " + registro.getIdRegistro() + ", neto vacío. </br>";
					}
					else
					{
						netoRegBig = new BigDecimal(netoRegistro.trim());
					}

				} catch (NumberFormatException e)
				{
					this.erroresNetoNuevos += "Registro Nuevo: " + registro.getIdRegistro() + ", neto: " + netoRegistro
							+ "<br/>";
					continue;
				}

				if (netoRegistro != null && netoRegBig != null)
				{

					if (netoRegistro.contains("."))
					{
						this.netoTotalNuevos = this.netoTotalNuevos.add(netoRegBig);
					}
					else
					{
						this.netoTotalNuevos = this.netoTotalNuevos.add(netoRegBig.divide(big100));
					}

				}
			}

		}

		if (!this.erroresNetoNuevos.isEmpty())
		{
			this.netoTotalNuevosString = "Errores en netos";
		}
		else
		{
			this.netoTotalNuevosString = utilidades.formatoMoneda(this.netoTotalNuevos.toPlainString());
		}

		if (!this.erroresNetoOriginal.isEmpty() || !this.erroresNeto.isEmpty() || !this.erroresNetoBaja.isEmpty()
				|| !this.erroresNetoNuevos.isEmpty())
		{

			if (!this.erroresNetoOriginal.isEmpty())
			{
				this.erroresGenerales = this.erroresNetoOriginal;
			}

			if (!this.erroresNeto.isEmpty())
			{

				if (!this.erroresGenerales.isEmpty())
				{
					this.erroresGenerales += "<br/>";
				}

				this.erroresGenerales += this.erroresNeto;
			}

			if (!this.erroresNetoBaja.isEmpty())
			{

				if (!this.erroresGenerales.isEmpty())
				{
					this.erroresGenerales += "<br/>";
				}

				this.erroresGenerales += this.erroresNetoBaja;
			}

			if (!this.erroresNetoNuevos.isEmpty())
			{

				if (!this.erroresGenerales.isEmpty())
				{
					this.erroresGenerales += "<br/>";
				}

				this.erroresGenerales += this.erroresNetoNuevos;
			}

		}

	}

	public PlantillaRegistro getRegistro(String clave)
	{
		for (PlantillaRegistro reg : this.registros)
		{
			if (reg.getValorPorDescripcionContains("Clave").trim().equalsIgnoreCase(clave))
			{
				return reg;
			}

		}

		return null;

	}

	// Se ajusta el valor dado a las longitudes del campo, solo como texto
	public String ajustaLongitudCampo(CampoPlantilla campo, String valor)
	{
		try
		{

			// longitud final que debe de tener el campo
			int longitud = campo.getEntero() + campo.getDecimal();

			// diferencia de la longitud del valor insertado contra la longitud
			// que
			// debe de tener finalmente el campo
			int diferencia = (valor.length() - longitud);

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

					for (int x = 0; x < (diferencia * -1); x++)
					{
						caracteresRelleno += " ";

					}

					// Se identifica si es un número se alinea a la derecha, en
					// caso contrario a la izquierda
					try
					{
						Double.parseDouble(valor);

						return caracteresRelleno + valor;

					} catch (NumberFormatException e)
					{
						return valor + caracteresRelleno;
					}

				}
				// en éste caso la diferencia es positiva, ya que el valor del
				// campo
				// excede la longitud que el campo debe de manejar finalmente
				else if (diferencia > 0)
				{
					return valor.substring(0, longitud);

				}

			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return valor;

	}

	public int getIdArchivoPago()
	{
		return idArchivoPago;
	}

	public void setIdArchivoPago(int idArchivoPago)
	{
		this.idArchivoPago = idArchivoPago;
	}

	public String getDescripcion()
	{
		return descripcion;
	}

	public void setDescripcion(String descripcion)
	{
		this.descripcion = descripcion;
	}

	public Plaza getPlaza()
	{
		return plaza;
	}

	public void setPlaza(Plaza plaza)
	{
		this.plaza = plaza;
	}

	public String getIdInstrumentoPago()
	{
		return idInstrumentoPago;
	}

	public void setIdInstrumentoPago(String idInstrumentoPago)
	{
		this.idInstrumentoPago = idInstrumentoPago;
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

	public String getObservaciones()
	{
		return observaciones;
	}

	public void setObservaciones(String observaciones)
	{
		this.observaciones = observaciones;
	}

	public Plantilla getPlantilla()
	{
		return plantilla;
	}

	public void setPlantilla(Plantilla plantilla)
	{
		this.plantilla = plantilla;
	}

	public List<PlantillaRegistro> getRegistros()
	{
		return registros;
	}

	public void setRegistros(List<PlantillaRegistro> registros)
	{
		this.registros = registros;
	}

	public static long getSerialversionuid()
	{
		return serialVersionUID;
	}

	public BigDecimal getNetoTotal()
	{
		return netoTotal;
	}

	public void setNetoTotal(BigDecimal netoTotal)
	{
		this.netoTotal = netoTotal;
	}

	public String getNetoTotalString()
	{
		return netoTotalString;
	}

	public void setNetoTotalString(String netoTotalString)
	{
		this.netoTotalString = netoTotalString;
	}

	public List<PlantillaRegistro> getRegistrosBaja()
	{
		return registrosBaja;
	}

	public void setRegistrosBaja(List<PlantillaRegistro> registrosBaja)
	{
		this.registrosBaja = registrosBaja;
	}

	public List<PlantillaRegistro> getRegistrosModificados()
	{
		return registrosModificados;
	}

	public void setRegistrosModificados(List<PlantillaRegistro> registrosModificados)
	{
		this.registrosModificados = registrosModificados;
	}

	public BigDecimal getNetoTotalOriginall()
	{
		return netoTotalOriginal;
	}

	public void setNetoTotalOriginal(BigDecimal netoTotalOriginal)
	{
		this.netoTotalOriginal = netoTotalOriginal;
	}

	public String getNetoTotalOriginalString()
	{
		return netoTotalOriginalString;
	}

	public void setNetoTotalOriginalString(String netoTotalOriginalString)
	{
		this.netoTotalOriginalString = netoTotalOriginalString;
	}

	public BigDecimal getNetoTotalBaja()
	{
		return netoTotalBaja;
	}

	public void setNetoTotalBaja(BigDecimal netoTotalBaja)
	{
		this.netoTotalBaja = netoTotalBaja;
	}

	public String getNetoTotalBajaString()
	{
		return netoTotalBajaString;
	}

	public void setNetoTotalBajaString(String netoTotalBajaString)
	{
		this.netoTotalBajaString = netoTotalBajaString;
	}

	public BigDecimal getNetoTotalNuevos()
	{
		return netoTotalNuevos;
	}

	public void setNetoTotalNuevos(BigDecimal netoTotalNuevos)
	{
		this.netoTotalNuevos = netoTotalNuevos;
	}

	public String getNetoTotalNuevosString()
	{
		return netoTotalNuevosString;
	}

	public void setNetoTotalNuevosString(String netoTotalNuevosString)
	{
		this.netoTotalNuevosString = netoTotalNuevosString;
	}

	public String getNombreArchivo()
	{
		return nombreArchivo;
	}

	public void setNombreArchivo(String nombreArchivo)
	{
		this.nombreArchivo = nombreArchivo;
	}

	public BigDecimal getNetoTotalOriginal()
	{
		return netoTotalOriginal;
	}

	public String getErroresNeto()
	{
		return erroresNeto;
	}

	public void setErroresNeto(String erroresNeto)
	{
		this.erroresNeto = erroresNeto;
	}

	public String getErroresNetoOriginal()
	{
		return erroresNetoOriginal;
	}

	public void setErroresNetoOriginal(String erroresNetoOriginal)
	{
		this.erroresNetoOriginal = erroresNetoOriginal;
	}

	public String getErroresNetoBaja()
	{
		return erroresNetoBaja;
	}

	public void setErroresNetoBaja(String erroresNetoBaja)
	{
		this.erroresNetoBaja = erroresNetoBaja;
	}

	public String getErroresNetoNuevos()
	{
		return erroresNetoNuevos;
	}

	public void setErroresNetoNuevos(String erroresNetoNuevos)
	{
		this.erroresNetoNuevos = erroresNetoNuevos;
	}

	public String getErroresGenerales()
	{
		return erroresGenerales;
	}

	public void setErroresGenerales(String erroresGenerales)
	{
		this.erroresGenerales = erroresGenerales;
	}

	public List<PlantillaRegistro> getRegistrosNuevos()
	{
		return registrosNuevos;
	}

	public void setRegistrosNuevos(List<PlantillaRegistro> registrosNuevos)
	{
		this.registrosNuevos = registrosNuevos;
	}

	public List<PlantillaRegistro> getRegistrosVinculados()
	{
		return registrosVinculados;
	}

	public void setRegistrosVinculados(List<PlantillaRegistro> registrosVinculados)
	{
		this.registrosVinculados = registrosVinculados;
	}

}
