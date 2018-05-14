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

public class ProductoTimbrado implements Serializable, Cloneable
{
	private String idProductoTimbrado;
	private Producto productoNomina;

	private String totalPercepcionesString;
	private String totalDeduccionesString;
	private String totalString;
	private BigDecimal totalPercepciones;
	private BigDecimal totalDeducciones;
	private BigDecimal total;
	private int totalRegistros;
	private int totalRegistrosCancelados;

	private List<Timbre> timbres;
	private Status status;

	private List<ConceptoTimbre> conceptosPercep;
	private List<ConceptoTimbre> conceptosDeduc;

	public ProductoTimbrado(String idProductoTimbrado)
	{
		super();
		this.idProductoTimbrado = idProductoTimbrado;
		this.totalPercepcionesString = "$ 0.00";
		this.totalDeduccionesString = "$ 0.00";
		this.totalString = "$ 0.00";
		this.totalPercepciones = new BigDecimal("0.00");
		this.totalDeducciones = new BigDecimal("0.00");
		this.total = new BigDecimal("0.00");
		this.totalRegistros = 0;
		this.totalRegistrosCancelados = 0;
		this.timbres = new ArrayList<>();
		this.status = new Status(0, "TIMBRADO");

	}

	public ProductoTimbrado(String idProductoTimbrado, Producto productoNomina, BigDecimal totalPercepciones,
			BigDecimal totalDeducciones, BigDecimal total, int totalRegistros, int totalRegistrosCancelados,
			List<Timbre> timbres, Status status)
	{
		super();
		this.idProductoTimbrado = idProductoTimbrado;
		this.productoNomina = productoNomina;
		this.totalPercepciones = totalPercepciones;
		this.totalDeducciones = totalDeducciones;
		this.total = total;
		this.totalRegistros = totalRegistros;
		this.totalRegistrosCancelados = totalRegistrosCancelados;
		this.timbres = timbres;
		this.status = status;

		this.totalPercepcionesString = utilidades.formatoMoneda(totalPercepciones.toString());
		this.totalDeduccionesString = utilidades.formatoMoneda(this.totalDeducciones.toString());
		this.totalString = utilidades.formatoMoneda(this.total.toString());

	}

	@Override
	public Object clone()
	{
		ProductoTimbrado clon = null;
		try
		{
			clon = (ProductoTimbrado) super.clone();

			clon.setProductoNomina(this.productoNomina);
			clon.setTotalPercepciones(new BigDecimal(this.totalPercepciones.toString()));
			clon.setTotalDeducciones(new BigDecimal(this.totalDeducciones.toString()));
			clon.setTotal(new BigDecimal(this.total.toString()));
			clon.setStatus((Status) this.status.clone());

		}
		catch (CloneNotSupportedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return clon;

	}

	public boolean isCancelado()
	{
		if (getStatus().getIdStatus() == -1)
		{
			return true;
		}

		return false;
	}

	public boolean isTimbreCancelado(Timbre timbre)
	{

		return false;
	}

	public void updateTimbres(boolean incluirCancelados)
	{
		this.timbres = new ArrayList<>();

		PreparedStatement prep = null;
		ResultSet rBD = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionTimbrado();)
		{
			String query = " SELECT tim.*, st.descripcion AS descripcionStatus FROM timbrado.timbre tim, status st WHERE tim.idProductoTimbrado=? AND tim.idStatus = st.idStatus AND tim.idStatus IN (0 ";

			if (incluirCancelados)
			{
				query += ", -1";

			}

			query += ") ";

			prep = conexion.prepareStatement(query);

			prep.setString(1, this.idProductoTimbrado);

			rBD = prep.executeQuery();

			if (rBD.next())
			{
				do
				{
					Timbre objTimbre = new Timbre();
					objTimbre.setFolio(rBD.getString("Folio"));
					objTimbre.setUUID(rBD.getString("UUID"));
					objTimbre.setFechaComprobante(rBD.getDate("FechaComprobante"));
					objTimbre.setFechaTimbrado(rBD.getDate("FechaTimbrado"));
					objTimbre.setNoCertificado(rBD.getString("NoCertificado"));
					objTimbre.setRfc(rBD.getString("rfc"));
					objTimbre.setReceptor(rBD.getString("Receptor"));
					objTimbre.setTotal(rBD.getBigDecimal("Total"));
					objTimbre.setTotalPercepciones(rBD.getBigDecimal("TotalPercepciones"));
					objTimbre.setTotalDeducciones(rBD.getBigDecimal("TotalDeducciones"));
					objTimbre.setIdProductoTimbrado(this);

					objTimbre.setStatus(new Status(rBD.getInt("idStatus"), rBD.getString("DescripcionStatus")));

					// Solo si el timbre está cancelado se obtiene los datos de
					// la cancelación
					if (objTimbre.getStatus().getIdStatus() == -1)
					{
						objTimbre.setFechaCancelacion(rBD.getDate("FechaCancelacion"));
						objTimbre.setMotivoCancelacion(rBD.getString("Motivo_Can"));
						objTimbre.setRemesa(rBD.getString("Remesa"));
					}

					this.timbres.add(objTimbre);

				} while (rBD.next());

			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción", "Al actualizar los timbres del producto. Favor de contactar con el desarrollador."));
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

	// realiza una actualización de los conceptos de los timbres, de forma
	// masiva con una sola consulta, par actualizar un solo timbre, se refiere a
	// el método implementado en la clase Timbre
	public void updateConceptosTimbres(boolean clasificarConceptosPercepDeduc, boolean acumularConceptos,
			boolean clasificarCancelados)
	{

		if (clasificarConceptosPercepDeduc)
		{
			this.conceptosPercep = new ArrayList<>();
			this.conceptosDeduc = new ArrayList<>();
		}

		PreparedStatement prep = null;
		ResultSet rBD = null;

		String lastFolio = null;
		Timbre lastTimbre = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionTimbrado();)
		{
			prep = conexion.prepareStatement(
					" SELECT * FROM timbrado.conceptotimbre WHERE idProductoTimbrado=? ORDER BY Folio ");

			prep.setString(1, this.idProductoTimbrado);

			rBD = prep.executeQuery();

			if (rBD.next())
			{
				do
				{
					String folio = rBD.getString("Folio");

					if (lastFolio == null || !lastFolio.equalsIgnoreCase(folio))
					{
						lastTimbre = getTimbre(folio);
					}

					lastFolio = folio;

					if (!clasificarCancelados)
					{
						if (lastTimbre.isCancelado())
						{
							continue;
						}

					}

					String clave = rBD.getString("Clave");

					ConceptoTimbre conTimbre = new ConceptoTimbre(Integer.parseInt(clave.substring(0, 1)),
							clave.substring(1, 3), "", null, clave.substring(3, clave.length()));
					conTimbre.setTotalCasos(1);
					conTimbre.setImporteExento(rBD.getBigDecimal("ImporteExento"));
					conTimbre.setImporteGravado(rBD.getBigDecimal("ImporteGravado"));
					conTimbre.setImporteDeduc(rBD.getBigDecimal("ImporteDeduc"));
					conTimbre.setTipoPercepDeduc(rBD.getString("TipoPercepDeduc"));

					lastTimbre.inicializaListaConceptos();

					lastTimbre.addConcepto(conTimbre);

					if (clasificarConceptosPercepDeduc)
					{
						
						if( clave.equals("20117"))
						{
							System.out.println(clave);
						}

						if (conTimbre.getTipoConcepto() == 1
								|| (conTimbre.getTipoConcepto() == 2 && (conTimbre.getImporteExento().signum() > 0
										|| conTimbre.getImporteGravado().signum() > 0)))
						{
							if (!acumularConceptos)
							{
								this.conceptosPercep.add(conTimbre);
							}
							else
							{
								boolean encontrado = false;

								for (ConceptoTimbre concon : this.conceptosPercep)
								{
									if (concon.getTipoConcepto() == conTimbre.getTipoConcepto()
											&& concon.getClave().equalsIgnoreCase(conTimbre.getClave())
											&& concon.getPartidaAntecedente().equals(conTimbre.getPartidaAntecedente()))
									{

										concon.addImporteExento(conTimbre.getImporteExento());
										concon.addImporteGravado(conTimbre.getImporteGravado());
										concon.addImporteDeduc(conTimbre.getImporteDeduc());
										concon.addNCaso();

										encontrado = true;

									}

								}

								if (!encontrado)
								{
									this.conceptosPercep.add(conTimbre);
								}

							}

						}
						else
						{

							if (!acumularConceptos)
							{
								this.conceptosDeduc.add(conTimbre);
							}
							else
							{
								boolean encontrado = false;

								for (ConceptoTimbre concon : this.conceptosDeduc)
								{
									if (concon.getTipoConcepto() == conTimbre.getTipoConcepto()
											&& concon.getClave().equalsIgnoreCase(conTimbre.getClave())
											&& concon.getPartidaAntecedente().equals(conTimbre.getPartidaAntecedente()))
									{

										concon.addImporteExento(conTimbre.getImporteExento());
										concon.addImporteGravado(conTimbre.getImporteGravado());
										concon.addImporteDeduc(conTimbre.getImporteDeduc());
										concon.addNCaso();

										encontrado = true;

									}

								}

								if (!encontrado)
								{
									this.conceptosDeduc.add(conTimbre);
								}

							}

						}

					}

				} while (rBD.next());

			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Excepción",
							"Al actualizar los conceptos de los timbres. Favor de contactar con el desarrollador."));
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

	public Timbre getTimbre(String folio)
	{
		if (this.timbres == null)
		{
			return null;
		}

		for (Timbre tim : this.timbres)
		{
			if (tim.getFolio().equalsIgnoreCase(folio))
			{
				return tim;
			}

		}

		return null;

	}

	public Producto getProductoNomina()
	{
		return productoNomina;
	}

	public void setProductoNomina(Producto productoNomina)
	{
		this.productoNomina = productoNomina;
	}

	public String getIdProductoTimbrado()
	{
		return idProductoTimbrado;
	}

	public void setIdProductoTimbrado(String idProductoTimbrado)
	{
		this.idProductoTimbrado = idProductoTimbrado;
	}

	public BigDecimal getTotalPercepciones()
	{
		return totalPercepciones;
	}

	public void setTotalPercepciones(BigDecimal totalPercepciones)
	{
		this.totalPercepciones = totalPercepciones;
	}

	public BigDecimal getTotalDeducciones()
	{
		return totalDeducciones;
	}

	public void setTotalDeducciones(BigDecimal totalDeducciones)
	{
		this.totalDeducciones = totalDeducciones;
	}

	public BigDecimal getTotal()
	{
		return total;
	}

	public void setTotal(BigDecimal total)
	{
		this.total = total;
	}

	public int getTotalRegistros()
	{
		return totalRegistros;
	}

	public void setTotalRegistros(int totalRegistros)
	{
		this.totalRegistros = totalRegistros;
	}

	public List<Timbre> getTimbres()
	{
		return timbres;
	}

	public void setTimbres(List<Timbre> timbres)
	{
		this.timbres = timbres;
	}

	public String getTotalPercepcionesString()
	{
		return totalPercepcionesString;
	}

	public void setTotalPercepcionesString(String totalPercepcionesString)
	{
		this.totalPercepcionesString = totalPercepcionesString;
	}

	public String getTotalDeduccionesString()
	{
		return totalDeduccionesString;
	}

	public void setTotalDeduccionesString(String totalDeduccionesString)
	{
		this.totalDeduccionesString = totalDeduccionesString;
	}

	public String getTotalString()
	{
		return totalString;
	}

	public void setTotalString(String totalString)
	{
		this.totalString = totalString;
	}

	public int getTotalRegistrosCancelados()
	{
		return totalRegistrosCancelados;
	}

	public void setTotalRegistrosCancelados(int totalRegistrosCancelados)
	{
		this.totalRegistrosCancelados = totalRegistrosCancelados;
	}

	public Status getStatus()
	{
		return status;
	}

	public void setStatus(Status status)
	{
		this.status = status;
	}

	public List<ConceptoTimbre> getConceptosPercep()
	{
		return conceptosPercep;
	}

	public void setConceptosPercep(List<ConceptoTimbre> conceptosPercep)
	{
		this.conceptosPercep = conceptosPercep;
	}

	public List<ConceptoTimbre> getConceptosDeduc()
	{
		return conceptosDeduc;
	}

	public void setConceptosDeduc(List<ConceptoTimbre> conceptosDeduc)
	{
		this.conceptosDeduc = conceptosDeduc;
	}

}