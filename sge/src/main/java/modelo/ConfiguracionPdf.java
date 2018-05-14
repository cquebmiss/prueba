package modelo;

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

public class ConfiguracionPdf implements Cloneable
{

	private int idConfiguracionPdf;
	private String descripcion;
	private String TamañoHoja;
	private String Orientacion;
	private String FontFamily;
	private String TamañoFuente;
	private String EstiloFuente;
	private String EspacioEntreCaracteres;
	private String EspacioAntesParrafo;
	private String EspacioDespuesParrafo;
	private String MargenDerecho;
	private String MargenIzquierdo;
	private String MargenSuperior;
	private String MargenInferior;

	@Override
	public Object clone()
	{
		ConfiguracionPdf clon = null;

		try
		{
			clon = (ConfiguracionPdf) super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return clon;

	}

	public ConfiguracionPdf()
	{
		super();
		this.idConfiguracionPdf = -1;
		// TODO Auto-generated constructor stub
	}

	public ConfiguracionPdf(int idConfiguracionPdf, String descripcion, String tamañoHoja, String orientacion,
			String fontFamily, String tamañoFuente, String estiloFuente, String espacioEntreCaracteres,
			String espacioAntesParrafo, String espacioDespuesParrafo, String margenDerecho, String margenIzquierdo,
			String margenSuperior, String margenInferior)
	{
		super();
		this.idConfiguracionPdf = idConfiguracionPdf;
		this.descripcion = descripcion;
		TamañoHoja = tamañoHoja;
		Orientacion = orientacion;
		FontFamily = fontFamily;
		TamañoFuente = tamañoFuente;
		EstiloFuente = estiloFuente;
		EspacioEntreCaracteres = espacioEntreCaracteres;
		EspacioAntesParrafo = espacioAntesParrafo;
		EspacioDespuesParrafo = espacioDespuesParrafo;
		MargenDerecho = margenDerecho;
		MargenIzquierdo = margenIzquierdo;
		MargenSuperior = margenSuperior;
		MargenInferior = margenInferior;
	}

	// Se actualiza la información de las propiedades del objeto
	public void update()
	{

		if (this.idConfiguracionPdf == -1)
		{
			setDescripcion("");
			setTamañoFuente("7.5f");
			setMargenIzquierdo("95");
			setMargenDerecho("5");
			setMargenSuperior("5");
			setMargenInferior("5");
			setEspacioDespuesParrafo("5");
			setEspacioAntesParrafo("0");
			setTamañoHoja("legal");
			setOrientacion("vertical");
			setEspacioEntreCaracteres("1.6f");

			return;
		}

		PreparedStatement prep = null;
		ResultSet rBD = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionSpoolerPDF();)
		{
			prep = conexion.prepareStatement(" SELECT * FROM configuracionpdf WHERE idConfiguracionPdf=? ");

			prep.setInt(1, this.idConfiguracionPdf);

			rBD = prep.executeQuery();

			if (rBD.next())
			{
				setDescripcion(rBD.getString("Descripcion"));
				setEspacioAntesParrafo(rBD.getString("espacioAntesParrafo"));
				setEspacioDespuesParrafo(rBD.getString("espacioDespuesParrafo"));
				setEspacioEntreCaracteres(rBD.getString("espacioEntreCaracteres"));
				setEstiloFuente(rBD.getString("estiloFuente"));
				setFontFamily(rBD.getString("fontFamily"));
				setMargenDerecho(rBD.getString("margenDerecho"));
				setMargenInferior(rBD.getString("margenInferior"));
				setMargenIzquierdo(rBD.getString("margenIzquierdo"));
				setMargenSuperior(rBD.getString("margenSuperior"));
				setOrientacion(rBD.getString("orientacion"));
				setTamañoFuente(rBD.getString("tamañoFuente"));
				setTamañoHoja(rBD.getString("tamañoHoja"));

			}

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al obtener el detalle de la configuración Pdf, favor de contactar con el desarrollador del sistema."));

			e.printStackTrace();
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

	// Actualiza la información de la configuración en la base de datos
	public void updateBD()
	{
		PreparedStatement prep = null;
		ResultSet rBD = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionSpoolerPDF();)
		{
			prep = conexion.prepareStatement(" UPDATE configuracionpdf SET  " + "espacioAntesParrafo=?, "
					+ "espacioDespuesParrafo=?, " + "espacioEntreCaracteres=?, " + "estiloFuente=?, " + "fontFamily=?, "
					+ "margenDerecho=?, " + "margenInferior=?, " + "margenIzquierdo=?, " + "margenSuperior=?, "
					+ "orientacion=?, " + "tamañoFuente=?, " + "tamañoHoja=?, descripcion=? " + "WHERE idConfiguracionPdf=? ");

			prep.setString(1, getEspacioAntesParrafo());
			prep.setString(2, getEspacioDespuesParrafo());
			prep.setString(3, getEspacioEntreCaracteres());
			prep.setString(4, getEstiloFuente());
			prep.setString(5, getFontFamily());
			prep.setString(6, getMargenDerecho());
			prep.setString(7, getMargenInferior());
			prep.setString(8, getMargenIzquierdo());
			prep.setString(9, getMargenSuperior());
			prep.setString(10, getOrientacion());
			prep.setString(11, getTamañoFuente());
			prep.setString(12, getTamañoHoja());
			prep.setString(13, getDescripcion());

			prep.setInt(14, this.idConfiguracionPdf);

			prep.executeUpdate();

			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Configuración Actualizada", "La configuración del reporte se ha actualizado exitosamente."));

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al actualizar el detalle de la configuración Pdf, favor de contactar con el desarrollador del sistema."));

			e.printStackTrace();
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

	public int getIdConfiguracionPdf()
	{
		return idConfiguracionPdf;
	}

	public void setIdConfiguracionPdf(int idConfiguracionPdf)
	{
		this.idConfiguracionPdf = idConfiguracionPdf;
	}

	public String getDescripcion()
	{
		return descripcion;
	}

	public void setDescripcion(String descripcion)
	{
		this.descripcion = descripcion;
	}

	public String getTamañoHoja()
	{
		return TamañoHoja;
	}

	public void setTamañoHoja(String tamañoHoja)
	{
		TamañoHoja = tamañoHoja;
	}

	public String getOrientacion()
	{
		return Orientacion;
	}

	public void setOrientacion(String orientacion)
	{
		Orientacion = orientacion;
	}

	public String getFontFamily()
	{
		return FontFamily;
	}

	public void setFontFamily(String fontFamily)
	{
		FontFamily = fontFamily;
	}

	public String getTamañoFuente()
	{
		return TamañoFuente;
	}

	public void setTamañoFuente(String tamañoFuente)
	{
		TamañoFuente = tamañoFuente;
	}

	public String getEstiloFuente()
	{
		return EstiloFuente;
	}

	public void setEstiloFuente(String estiloFuente)
	{
		EstiloFuente = estiloFuente;
	}

	public String getEspacioEntreCaracteres()
	{
		return EspacioEntreCaracteres;
	}

	public void setEspacioEntreCaracteres(String espacioEntreCaracteres)
	{
		EspacioEntreCaracteres = espacioEntreCaracteres;
	}

	public String getEspacioAntesParrafo()
	{
		return EspacioAntesParrafo;
	}

	public void setEspacioAntesParrafo(String espacioAntesParrafo)
	{
		EspacioAntesParrafo = espacioAntesParrafo;
	}

	public String getEspacioDespuesParrafo()
	{
		return EspacioDespuesParrafo;
	}

	public void setEspacioDespuesParrafo(String espacioDespuesParrafo)
	{
		EspacioDespuesParrafo = espacioDespuesParrafo;
	}

	public String getMargenDerecho()
	{
		return MargenDerecho;
	}

	public void setMargenDerecho(String margenDerecho)
	{
		MargenDerecho = margenDerecho;
	}

	public String getMargenIzquierdo()
	{
		return MargenIzquierdo;
	}

	public void setMargenIzquierdo(String margenIzquierdo)
	{
		MargenIzquierdo = margenIzquierdo;
	}

	public String getMargenSuperior()
	{
		return MargenSuperior;
	}

	public void setMargenSuperior(String margenSuperior)
	{
		MargenSuperior = margenSuperior;
	}

	public String getMargenInferior()
	{
		return MargenInferior;
	}

	public void setMargenInferior(String margenInferior)
	{
		MargenInferior = margenInferior;
	}

}
