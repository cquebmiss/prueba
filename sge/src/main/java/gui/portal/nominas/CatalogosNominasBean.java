package gui.portal.nominas;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.RowEditEvent;

import application.CatalogosBean;
import modelo.ConceptoAsociadoPlaza;
import modelo.FuenteFinanciamiento;
import modelo.IPConverter;
import modelo.InstrumentoPago;
import modelo.Layout;
import resources.DataBase;
import util.FacesUtils;
import util.utilidades;

@ManagedBean
@ViewScoped
public class CatalogosNominasBean implements Serializable
{
	private CatalogosBean				catBean;

	List<ConceptoAsociadoPlaza>			catConceptosAsociados;
	List<ConceptoAsociadoPlaza>			catConceptosAsociadosFilter;
	ConceptoAsociadoPlaza				conceptoAsociadoPlazaSelec;

	private List<FuenteFinanciamiento>	catFuentesFinanciamiento;
	private List<FuenteFinanciamiento>	catFuentesFinanciamientoFilter;
	private FuenteFinanciamiento		fuenteFinanciamientoSelec;

	private List<InstrumentoPago>		catInstrumentosPago;
	private List<InstrumentoPago>		catInstrumentoPagoFilter;
	private InstrumentoPago				instrumentoPagoSelec;

	// atributos para el diálogo de asignación de layout a los instrumentos de
	// pago
	private Layout						layoutBancoSeleccionado;
	private String						versionLayoutSeleccionado;

	public CatalogosNominasBean()
	{
		inicializaCatalogos();

	}

	@PostConstruct
	public void onPostConstruct()
	{
		this.catBean = (CatalogosBean) FacesUtils.getManagedBean("catalogosBean");
		this.catInstrumentosPago = catBean.getCatInstrumentosPago();

	}

	// Se añade un nuevo registro a la tabla de conceptos asociados a la plaza
	public void addConceptoAsociadoPlaza()
	{
		this.catConceptosAsociados.add(new ConceptoAsociadoPlaza(-1, "", "", false));
	}

	public void addRegistroFuenteFinanciamiento()
	{
		this.catFuentesFinanciamiento.add(new FuenteFinanciamiento(-1, "", ""));
	}

	public void onRowEditTablaConceptos(RowEditEvent evt)
	{
		PreparedStatement prep = null;
		ConceptoAsociadoPlaza concepto = (ConceptoAsociadoPlaza) evt.getObject();

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{

			switch (concepto.getIdRegistro())
			{
				case -1:

					prep = conexion.prepareStatement(
							" INSERT INTO conceptoasociadoplaza (PartidaEspecifica, Concepto, Asociado) VALUES (?, ?, ?) ");

					break;

				default:

					prep = conexion.prepareStatement(
							" UPDATE conceptoasociadoplaza SET PartidaEspecifica=?, Concepto=?, Asociado=? WHERE idRegistro=?; ");
					prep.setInt(4, concepto.getIdRegistro());

					break;
			}

			prep.setString(1, concepto.getPartidaAntecedente());
			prep.setString(2, concepto.getConcepto());
			prep.setBoolean(3, concepto.isAsociado());

			prep.executeUpdate();

			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Concepto Editado", "El concepto se ha editado exitosamente."));

			inicializaCatalogos();

		} catch (Exception e)
		{

			e.printStackTrace();

			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Se ha generado una excepción al momento de editar el concepto. Favor de contactar con el desarrollador del sistema."));
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

	public void onRowEditTablaFuenteFinanciamiento(RowEditEvent evt)
	{
		PreparedStatement prep = null;
		FuenteFinanciamiento fuenteFinanciamiento = (FuenteFinanciamiento) evt.getObject();

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{

			switch (fuenteFinanciamiento.getIdRegistro())
			{
				case -1:

					prep = conexion.prepareStatement(
							" INSERT INTO fuentefinanciamiento (idFuenteFinanciamiento, Descripcion) VALUES (?, ?) ");

					break;

				default:

					prep = conexion.prepareStatement(
							" UPDATE fuentefinanciamiento SET idFuenteFinanciamiento=?, Descripcion=? WHERE idRegistro=?; ");
					prep.setInt(3, fuenteFinanciamiento.getIdRegistro());

					break;
			}

			prep.setString(1, fuenteFinanciamiento.getIdFuenteFinanciamiento());
			prep.setString(2, fuenteFinanciamiento.getDescripcion());

			prep.executeUpdate();

			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Fuente de Financiamiento", "La fuente de financiamiento se ha editado exitosamente."));

			inicializaCatalogos();

		} catch (Exception e)
		{

			e.printStackTrace();

			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Se ha generado una excepción al momento de editar la fuente de financiamiento. Favor de contactar con el desarrollador del sistema."));
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

	public void onRowCancelTablaConceptos(RowEditEvent evt)
	{

		int a = 1;
		a = 5;

	}

	// Se elimina el costo asociado a la plaza seleccionado
	public void actionEliminarConceptoAsociadoPlaza()
	{
		PreparedStatement prep = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{
			prep = conexion.prepareStatement("DELETE FROM conceptoasociadoplaza WHERE idRegistro=?");

			prep.setInt(1, conceptoAsociadoPlazaSelec.getIdRegistro());

			prep.executeUpdate();

			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Concepto Eliminado", "El concepto se ha eliminado exitosamente."));

			inicializaCatalogos();

		} catch (Exception e)
		{

			e.printStackTrace();

			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Se ha generado una excepción al momento de eliminar el concepto. Favor de contactar con el desarrollador del sistema."));

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

	public void actionEliminarFuenteFinanciamiento()
	{
		PreparedStatement prep = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{
			prep = conexion.prepareStatement("DELETE FROM fuentefinanciamiento WHERE idRegistro=?");

			prep.setInt(1, this.fuenteFinanciamientoSelec.getIdRegistro());

			prep.executeUpdate();

			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Fuente de Financiamiento Eliminada", "La fuente de financiamiento se ha eliminado exitosamente."));

			inicializaCatalogos();

		} catch (Exception e)
		{

			e.printStackTrace();

			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Se ha generado una excepción al momento de eliminar la fuente de financiamiento. Favor de contactar con el desarrollador del sistema."));

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

	public void actionPrepararDialogoAsignarLayouts()
	{
		this.layoutBancoSeleccionado = null;
		this.versionLayoutSeleccionado = null;

	}

	public void actionVincularLayoutIP()
	{

		PreparedStatement prep = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionNominas();)
		{

			prep = conexion
					.prepareStatement(" DELETE FROM  instrumentopagolayout WHERE idInstrumentoPago=? AND idPlaza=? ");

			prep.setString(1, this.instrumentoPagoSelec.getIdInstrumentoPago());
			prep.setInt(2, this.instrumentoPagoSelec.getPlaza().getIdPlaza());

			prep.executeUpdate();

			prep.close();

			prep = conexion.prepareStatement(
					" INSERT INTO instrumentopagolayout (idInstrumentoPago, idPlaza, idLayout, Version, Tipo, Observaciones) VALUES (?, ?, ?, ?, ?, ?) ");

			prep.setString(1, this.instrumentoPagoSelec.getIdInstrumentoPago());
			prep.setInt(2, this.instrumentoPagoSelec.getPlaza().getIdPlaza());
			prep.setInt(3, this.layoutBancoSeleccionado.getIdLayout());
			prep.setString(4, this.versionLayoutSeleccionado);
			prep.setInt(5, 0);
			prep.setString(6, "Sin observaciones por desarrollo");

			prep.executeUpdate();

			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Layout Vinculado",
							"El layout seleccionado ha sido vinculado exitosamente con el instrumento de pago."));

			this.catBean.updateCatInstrumentosPago();
			this.catInstrumentosPago = this.catBean.getCatInstrumentosPago();

		} catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Excepción",
							"Ha ocurrido una excepción al momento vincular el layout con el instrumento de pago."));

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

	public void inicializaCatalogos()
	{
		this.catConceptosAsociados = utilidades.getCatalogoConceptosAsociadosPlaza(2);
		this.catFuentesFinanciamiento = utilidades.getCatalogoFuentesFinanciamiento();
	}

	public List<ConceptoAsociadoPlaza> getCatConceptosAsociados()
	{
		return catConceptosAsociados;
	}

	public void setCatConceptosAsociados(List<ConceptoAsociadoPlaza> catConceptosAsociados)
	{
		this.catConceptosAsociados = catConceptosAsociados;
	}

	public ConceptoAsociadoPlaza getConceptoAsociadoPlazaSelec()
	{
		return conceptoAsociadoPlazaSelec;
	}

	public void setConceptoAsociadoPlazaSelec(ConceptoAsociadoPlaza conceptoAsociadoPlazaSelec)
	{
		this.conceptoAsociadoPlazaSelec = conceptoAsociadoPlazaSelec;
	}

	public List<ConceptoAsociadoPlaza> getCatConceptosAsociadosFilter()
	{
		return catConceptosAsociadosFilter;
	}

	public void setCatConceptosAsociadosFilter(List<ConceptoAsociadoPlaza> catConceptosAsociadosFilter)
	{
		this.catConceptosAsociadosFilter = catConceptosAsociadosFilter;
	}

	public List<FuenteFinanciamiento> getCatFuentesFinanciamiento()
	{
		return catFuentesFinanciamiento;
	}

	public void setCatFuentesFinanciamiento(List<FuenteFinanciamiento> catFuentesFinanciamiento)
	{
		this.catFuentesFinanciamiento = catFuentesFinanciamiento;
	}

	public List<FuenteFinanciamiento> getCatFuentesFinanciamientoFilter()
	{
		return catFuentesFinanciamientoFilter;
	}

	public void setCatFuentesFinanciamientoFilter(List<FuenteFinanciamiento> catFuentesFinanciamientoFilter)
	{
		this.catFuentesFinanciamientoFilter = catFuentesFinanciamientoFilter;
	}

	public FuenteFinanciamiento getFuenteFinanciamientoSelec()
	{
		return fuenteFinanciamientoSelec;
	}

	public void setFuenteFinanciamientoSelec(FuenteFinanciamiento fuenteFinanciamientoSelec)
	{
		this.fuenteFinanciamientoSelec = fuenteFinanciamientoSelec;
	}

	public List<InstrumentoPago> getCatInstrumentoPagoFilter()
	{
		return catInstrumentoPagoFilter;
	}

	public void setCatInstrumentoPagoFilter(List<InstrumentoPago> catInstrumentoPagoFilter)
	{
		this.catInstrumentoPagoFilter = catInstrumentoPagoFilter;
	}

	public InstrumentoPago getInstrumentoPagoSelec()
	{
		return instrumentoPagoSelec;
	}

	public void setInstrumentoPagoSelec(InstrumentoPago instrumentoPagoSelec)
	{
		this.instrumentoPagoSelec = instrumentoPagoSelec;
	}

	public Layout getLayoutBancoSeleccionado()
	{
		return layoutBancoSeleccionado;
	}

	public void setLayoutBancoSeleccionado(Layout layoutBancoSeleccionado)
	{
		this.layoutBancoSeleccionado = layoutBancoSeleccionado;
	}

	public List<InstrumentoPago> getCatInstrumentosPago()
	{
		return catInstrumentosPago;
	}

	public void setCatInstrumentosPago(List<InstrumentoPago> catInstrumentosPago)
	{
		this.catInstrumentosPago = catInstrumentosPago;
	}

	public CatalogosBean getCatBean()
	{
		return catBean;
	}

	public void setCatBean(CatalogosBean catBean)
	{
		this.catBean = catBean;
	}

	public String getVersionLayoutSeleccionado()
	{
		return versionLayoutSeleccionado;
	}

	public void setVersionLayoutSeleccionado(String versionLayoutSeleccionado)
	{
		this.versionLayoutSeleccionado = versionLayoutSeleccionado;
	}

}
