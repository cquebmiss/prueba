package gui.portal.nominas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import modelo.Layout;
import modelo.Plaza;
import modelo.TipoNomina;
import modelo.TipoProducto;
import resources.DataBase;
import util.FacesUtils;
import util.utilidades;

@ManagedBean
@SessionScoped
public class GestionProductosBean implements Serializable
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -6919899594892514960L;
	private List<Plaza>			plazas;
	private int					plazaSelec;
	private List<Plaza>			plazasMasiva;
	private int					plazaMasivaSelec;
	private int					añoMasiva;
	private int					quincenaMasiva;
	private List<TipoNomina>	tiposNomina;
	int							tipoNominaSelec;
	private List<TipoProducto>	tiposProducto;
	int							tipoProductoSelec;
	UploadedFile				archivoProducto;
	UploadedFile				archivoTRA;
	private int					añoProducto;
	private int					quincenaProducto;
	private String				nombreDescripcionProducto;

	BufferedReader				buffer				= null;
	BufferedReader				bufferTRA			= null;
	PreparedStatement			prep				= null;
	ResultSet					rBD					= null;
	int							idProducto;
	Layout						layoutDAT			= null;
	Layout						layoutTRA			= null;
	int							nLinea;
	int							nLineaTotal;
	int							progress;

	long						totalLineas			= 0;
	long						totalLineasTRA;

	public GestionProductosBean()
	{
		java.util.Calendar fechaActual = java.util.Calendar.getInstance();
		this.añoProducto = fechaActual.get(Calendar.YEAR);
		this.añoMasiva = fechaActual.get(Calendar.YEAR);

		this.quincenaProducto = (fechaActual.get(Calendar.MONTH) + 1) * 2;

		if (fechaActual.get(Calendar.DAY_OF_MONTH) < 16)
		{
			this.quincenaProducto -= 1;
		}

		this.quincenaMasiva = this.quincenaProducto;

		this.tiposNomina = utilidades.getTiposNomina();
		this.tiposProducto = utilidades.getTiposProducto();
		this.plazas = utilidades.getPlazas();
		this.plazasMasiva = utilidades.getPlazas();
		this.totalLineas = 1;
		this.nLineaTotal = 0;
		// TODO Auto-generated constructor stub
	}

	public void actionInicializarSecuencia()
	{
		try
		{
			boolean validado = true;

			if (!this.archivoProducto.getFileName().substring(0, this.archivoProducto.getFileName().length() - 4)
					.equals(this.archivoTRA.getFileName().substring(0, this.archivoTRA.getFileName().length() - 4)))
			{
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Archivos No Coincidentes",
								"Los nombres de los archivos no coinciden, favor de verificarlos."));
				validado = false;
			}
			else if (!this.archivoProducto.getFileName().toLowerCase().contains(".dat"))
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Archivo .DAT", "El archivo .DAT no posee dicha extensión."));
				validado = false;
			}
			else if (!this.archivoTRA.getFileName().toLowerCase().contains(".tra"))
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Archivo .TRA", "El archivo .TRA no posee dicha extensión."));
				validado = false;
			}

			if (!validado)
			{
				this.archivoProducto = null;
				this.archivoTRA = null;
				return;
			}

			buffer = new BufferedReader(new InputStreamReader(this.archivoProducto.getInputstream()));
			this.totalLineas = buffer.lines().count();
			buffer = new BufferedReader(new InputStreamReader(this.archivoProducto.getInputstream()));

			bufferTRA = new BufferedReader(new InputStreamReader(this.archivoTRA.getInputstream()));
			this.totalLineas += this.totalLineasTRA = bufferTRA.lines().count();
			bufferTRA = new BufferedReader(new InputStreamReader(this.archivoTRA.getInputstream()));

		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void prueba()
	{
		if (1 == 1)
		{
			return;
		}
	}

	public void handleFileUpload(FileUploadEvent event)
	{
		UploadedFile file = event.getFile();

		BufferedReader buffer = null;
		BufferedReader bufferTRA = null;
		PreparedStatement prep = null;
		ResultSet rBD = null;
		int idProductoDAT = 0;
		Layout layoutDAT = null;
		Layout layoutTRA = null;
		int nLinea;

		long totalLineasDATTRA;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionNominas();)
		{

			if (file.getFileName().contains(".dat") || file.getFileName().contains(".DAT"))
			{
				buffer = new BufferedReader(new InputStreamReader(file.getInputstream()));
				totalLineasDATTRA = (int) buffer.lines().count();
				buffer = new BufferedReader(new InputStreamReader(file.getInputstream()));

				// se busca al último número de producto
				prep = conexion.prepareStatement("SELECT * FROM producto ORDER BY idProducto DESC LIMIT 1");
				rBD = prep.executeQuery();
				idProductoDAT = 0;

				if (rBD.next())
				{
					idProductoDAT = rBD.getInt("idProducto") + 1;
				}

				boolean indiceCorrecto = false;

				String analisisProducto = "";

				do
				{

					try
					{

						// Se crea la referencia del producto
						prep = conexion.prepareStatement(
								" INSERT INTO producto (idProducto, Descripcion, Ano, Qna, idPlaza, idTipoNomina, idTipoProducto, NombreProducto) VALUES (?, ?, ?, ?, ?, ?, ?, ?) ");

						prep.setInt(1, idProductoDAT);
						prep.setString(2, file.getFileName().substring(0, file.getFileName().length() - 4));
						prep.setInt(3, this.añoMasiva);
						prep.setInt(4, this.quincenaMasiva);
						prep.setInt(5, getPlazaMasivaSelec());

						prep.setInt(6, 0);

						if (file.getFileName().toLowerCase().contains("prdo")
								|| file.getFileName().toLowerCase().contains("prdp"))
						{

							if (file.getFileName().toLowerCase().contains("prdp"))
							{
								prep.setInt(6, 1);

								analisisProducto = "Producto de Pensión Alimenticia "
										+ file.getFileName().toLowerCase();
							}
							else
							{
								analisisProducto = "Producto Normal (O/A " + file.getFileName().toLowerCase();
							}

							prep.setInt(7, 0);
						}
						else if (file.getFileName().toLowerCase().contains("prde"))
						{
							prep.setInt(7, 2);
							analisisProducto = "Producto Extraordinario " + file.getFileName().toLowerCase();
						}
						else if (file.getFileName().toLowerCase().contains("prdr"))
						{
							prep.setInt(7, 1);
							analisisProducto = "Producto Retroactivo " + file.getFileName().toLowerCase();
						}
						else if (file.getFileName().toLowerCase().contains("prdc"))
						{
							prep.setInt(7, 3);
							analisisProducto = "Producto Recálculo " + file.getFileName().toLowerCase();
						}
						else if (file.getFileName().toLowerCase().contains("prda"))
						{
							prep.setInt(7, 5);
							analisisProducto = "Producto Aguinaldo " + file.getFileName().toLowerCase();
						}
						else if (file.getFileName().toLowerCase().contains("chc"))
						{
							prep.setInt(7, 4);

							analisisProducto = "Producto Cheque Cancelado " + file.getFileName().toLowerCase();

							/*
							 * if
							 * (file.getFileName().toLowerCase().contains("p"))
							 * { prep.setInt(6, 1); }
							 */
						}
						else
						{
							prep.setInt(7, 0);
						}

						prep.setString(8, file.getFileName());

						prep.executeUpdate();

						indiceCorrecto = true;

					} catch (Exception e)
					{
						indiceCorrecto = false;
						idProductoDAT++;
					}

				}
				while (!indiceCorrecto);

				System.out.println("Indice " + idProductoDAT + " para " + file.getFileName());

				layoutDAT = new Layout(7, "Layout DAT");
				layoutDAT.updateVersiones();
				layoutDAT.getVersiones().get(0).updatePlantillaEncabezado(true);
				layoutDAT.getVersiones().get(0).updatePlantillasDetalle(true);

				String insercion = " INSERT INTO datvalores VALUES (?, ?, ?, ?, ?)";
				String inserAdicional = ", (?, ?, ?, ?, ?) ";

				for (int x = 1; x < layoutDAT.getVersiones().get(0).getDetalles().get(0).getCampos().size(); x++)
				{
					insercion += inserAdicional;
				}

				String linea;
				int orden;
				boolean lecturanula = false;
				nLinea = 1;
				prep = conexion.prepareStatement(insercion);
				String[] camposLinea = null;

				boolean pensionDetectada = false;
				int posNivelPuesto = layoutDAT.getVersiones().get(0).getDetalles().get(0)
						.getPosicionValorPorDescripcionContains("nivel de puesto");

				int nCampoInsert = 0;
				int nConteoBatch = 0;

				while (!lecturanula)
				{

					try
					{

						linea = buffer.readLine();

						if (linea == null)
						{

							lecturanula = true;
							System.out.println("Producto " + file.getFileName() + " con indice " + idProductoDAT
									+ " terminó lectura con la línea " + nLinea);
							// System.out.println(analisisProducto);

						}
						else
						{

							if (linea.trim().length() < 1)
							{
								return;
							}

							orden = 1;
							nCampoInsert = 0;

							camposLinea = linea.split("[|]");

							for (String valorCampo : camposLinea)
							{

								if (!pensionDetectada)
								{

									if (posNivelPuesto == orden)
									{
										// Siempre se pondrá el indicador para
										// que solamente se revise el primer
										// registro de producto
										pensionDetectada = true;
										int nivelPuesto = Integer.parseInt(valorCampo.trim());

										if (nivelPuesto == 0 || (nivelPuesto > 10 && nivelPuesto < 40))
										{
											analisisProducto += "		Registro de Nomina Ordinaria en " + valorCampo
													+ " en "
													+ file.getFileName().substring(0, file.getFileName().length() - 4);
											// Normal");
										}
										else
										{
											analisisProducto += "		Registro de Pensión " + valorCampo + " en "
													+ file.getFileName().substring(0, file.getFileName().length() - 4);

											PreparedStatement prepPension = conexion.prepareStatement(
													"UPDATE producto SET idTipoNomina=? WHERE idProducto=?");

											prepPension.setInt(1, 1);
											prepPension.setInt(2, idProductoDAT);

											prepPension.executeUpdate();

											prepPension.close();

										}
									}
								}

								prep.setInt(++nCampoInsert,
										layoutDAT.getVersiones().get(0).getDetalles().get(0).getIdPlantilla());
								prep.setInt(++nCampoInsert, orden);
								prep.setString(++nCampoInsert, valorCampo);
								prep.setInt(++nCampoInsert, nLinea);
								prep.setInt(++nCampoInsert, idProductoDAT);


								orden++;

							}

							prep.addBatch();
							
						}

						nLinea++;
						
						nConteoBatch++;

						if (nConteoBatch == 1000 || linea == null)
						{
							try
							{
								prep.executeBatch();
								nConteoBatch = 0;

							} catch (Exception e)
							{
								System.out.println("Línea " + nLinea + " contenido: " + linea);
							}

						}

					} catch (IOException ex)
					{
						System.out.println(ex);

					}

				}

			}
			else if (file.getFileName().contains(".tra") || file.getFileName().contains(".TRA"))
			{

				prep = conexion.prepareStatement(
						"SELECT * FROM producto WHERE Descripcion=? AND idPlaza=? AND Ano=? AND Qna=? ");

				prep.setString(1, file.getFileName().substring(0, file.getFileName().length() - 4));
				prep.setInt(2, getPlazaMasivaSelec());
				prep.setInt(3, this.añoMasiva);
				prep.setInt(4, this.quincenaMasiva);

				rBD = prep.executeQuery();

				if (rBD.next())
				{
					idProductoDAT = rBD.getInt("idProducto");
				}

				bufferTRA = new BufferedReader(new InputStreamReader(file.getInputstream()));
				totalLineasDATTRA = totalLineasTRA = bufferTRA.lines().count();
				bufferTRA = new BufferedReader(new InputStreamReader(file.getInputstream()));

				layoutTRA = new Layout(8, "Layout TRA");
				layoutTRA.updateVersiones();
				layoutTRA.getVersiones().get(0).updatePlantillaEncabezado(true);
				layoutTRA.getVersiones().get(0).updatePlantillasDetalle(true);

				String insercion = " INSERT INTO travalores VALUES (?, ?, ?, ?, ?) ";
				String inserAdicional = ", (?, ?, ?, ?, ?)";

				for (int x = 1; x < layoutTRA.getVersiones().get(0).getDetalles().get(0).getCampos().size(); x++)
				{
					insercion += inserAdicional;

				}

				String linea = "";
				boolean lecturanula = false;
				nLinea = 1;
				int nCampoInsert = 0;
				int nConteoBatch = 0;

				String[] camposLinea = null;
				int orden;

				System.out.println("Subiendo TRA");
				prep = conexion.prepareStatement(insercion);

				while (!lecturanula)
				{

					try
					{

						linea = bufferTRA.readLine();

						if (linea == null)
						{

							lecturanula = true;
							System.out.println("TRA Producto " + file.getFileName() + " con indice " + idProductoDAT
									+ " terminó lectura con la línea " + nLinea);

						}
						else
						{

							if (linea.trim().length() < 1)
							{
								return;
							}

							orden = 1;
							nCampoInsert = 0;

							camposLinea = linea.split("[|]");

							for (String valorCampo : camposLinea)
							{

								prep.setInt(++nCampoInsert,
										layoutTRA.getVersiones().get(0).getDetalles().get(0).getIdPlantilla());
								prep.setInt(++nCampoInsert, orden);
								prep.setString(++nCampoInsert, valorCampo);
								prep.setInt(++nCampoInsert, nLinea);
								prep.setInt(++nCampoInsert, idProductoDAT);

								orden++;

							}

							prep.addBatch();

						}

						nLinea++;

						nConteoBatch++;

						if (nConteoBatch == 1000 || linea == null)
						{
							try
							{
								prep.executeBatch();
								nConteoBatch = 0;

							} catch (Exception e)
							{
								System.out.println("Línea " + nLinea + " contenido: " + linea);
							}

						}

					} catch (IOException ex)
					{
						System.out.println(ex);
						return;

					}

				}

			}

			updateProductos();

			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Producto Cargado", "La información del producto se ha cargado exitosamente."));

		} catch (Exception e)
		{
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción", "Se ha generado una excepción al momento de subir el producto." + e));
		}

	}

	public void updateProductos()
	{

		InfoProductosBean infoProductosBean = (InfoProductosBean) FacesUtils.getManagedBean("infoProductosBean");
		infoProductosBean.updateProductos();

	}

	public void actionGuardarProducto()
	{

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionNominas();)
		{

			// Se busca que en el sistema no exista la misma descripción del
			// archivo de producto
			prep = conexion.prepareStatement("SELECT * FROM producto WHERE NombreProducto=? ");

			prep.setString(1, this.nombreDescripcionProducto.trim());

			rBD = prep.executeQuery();

			if (rBD.next())
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Nombre del Producto",
						"Ya existe dicho nombre de producto, verifique la información para evitar duplicidad de productos."));
				return;
			}

			// se busca al último número de producto
			prep = conexion.prepareStatement("SELECT * FROM producto ORDER BY idProducto DESC LIMIT 1");
			rBD = prep.executeQuery();
			idProducto = 0;

			if (rBD.next())
			{
				idProducto = rBD.getInt("idProducto") + 1;
			}

			// Se crea la referencia del producto
			prep = conexion.prepareStatement(
					" INSERT INTO producto (idProducto, Descripcion, Ano, Qna, idPlaza, idTipoNomina, idTipoProducto, NombreProducto) VALUES (?, ?, ?, ?, ?, ?, ?, ?) ");

			prep.setInt(1, idProducto);
			prep.setString(2, this.nombreDescripcionProducto);
			prep.setInt(3, this.añoProducto);
			prep.setInt(4, this.quincenaProducto);
			prep.setInt(5, this.plazaSelec);
			prep.setInt(6, this.tipoNominaSelec);
			prep.setInt(7, this.tipoProductoSelec);
			prep.setString(8, this.archivoProducto.getFileName());

			prep.executeUpdate();

			layoutDAT = new Layout(7, "Layout DAT");
			layoutDAT.updateVersiones();
			layoutDAT.getVersiones().get(0).updatePlantillaEncabezado(true);
			layoutDAT.getVersiones().get(0).updatePlantillasDetalle(true);

			layoutTRA = new Layout(8, "Layout TRA");
			layoutTRA.updateVersiones();
			layoutTRA.getVersiones().get(0).updatePlantillaEncabezado(true);
			layoutTRA.getVersiones().get(0).updatePlantillasDetalle(true);

			String insercion = " INSERT INTO datvalores VALUES (?, ?, ?, ?, ?); ";
			String linea;
			int orden;
			boolean lecturanula = false;
			nLinea = 1;
			nLineaTotal = 1;
			prep = conexion.prepareStatement(insercion);
			String[] camposLinea = null;

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

						camposLinea = linea.split("[|]");

						System.out.println(nLinea);

						for (String valorCampo : camposLinea)
						{

							prep.setInt(1, layoutDAT.getVersiones().get(0).getDetalles().get(0).getIdPlantilla());
							prep.setInt(2, orden);
							prep.setString(3, valorCampo);
							prep.setInt(4, nLinea);
							prep.setInt(5, idProducto);

							prep.addBatch();

							orden++;

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

			setProgress(0);

			insercion = " INSERT INTO travalores VALUES (?, ?, ?, ?, ?); ";
			linea = "";
			lecturanula = false;
			nLinea = 1;
			System.out.println("Subiendo TRA");
			prep = conexion.prepareStatement(insercion);

			while (!lecturanula)
			{

				try
				{

					linea = bufferTRA.readLine();

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

						camposLinea = linea.split("[|]");

						System.out.println("TRA: " + nLinea);

						for (String valorCampo : camposLinea)
						{

							prep.setInt(1, layoutTRA.getVersiones().get(0).getDetalles().get(0).getIdPlantilla());
							prep.setInt(2, orden);
							prep.setString(3, valorCampo);
							prep.setInt(4, nLinea);
							prep.setInt(5, idProducto);

							prep.addBatch();

							orden++;

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

			this.buffer = null;
			this.archivoProducto = null;
			this.archivoTRA = null;
			this.bufferTRA = null;
			this.nombreDescripcionProducto = "";

			this.nLineaTotal = 0;
			this.totalLineas = 1;

			InfoProductosBean infoProductosBean = (InfoProductosBean) FacesUtils.getManagedBean("infoProductosBean");
			infoProductosBean.updateProductos();

			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Producto Cargado", "La información del producto se ha cargado exitosamente."));

		} catch (Exception e)
		{
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción", "Se ha generado una excepción al momento de subir el producto." + e));
		}

	}

	public List<TipoNomina> getTiposNomina()
	{
		return tiposNomina;
	}

	public void setTiposNomina(List<TipoNomina> tiposNomina)
	{
		this.tiposNomina = tiposNomina;
	}

	public int getTipoNominaSelec()
	{
		return tipoNominaSelec;
	}

	public void setTipoNominaSelec(int tipoNominaSelec)
	{
		this.tipoNominaSelec = tipoNominaSelec;
	}

	public List<TipoProducto> getTiposProducto()
	{
		return tiposProducto;
	}

	public void setTiposProducto(List<TipoProducto> tiposProducto)
	{
		this.tiposProducto = tiposProducto;
	}

	public int getTipoProductoSelec()
	{
		return tipoProductoSelec;
	}

	public void setTipoProductoSelec(int tipoProductoSelec)
	{
		this.tipoProductoSelec = tipoProductoSelec;
	}

	public UploadedFile getArchivoProducto()
	{
		return archivoProducto;
	}

	public void setArchivoProducto(UploadedFile archivoProducto)
	{
		this.archivoProducto = archivoProducto;
	}

	public List<Plaza> getPlazas()
	{
		return plazas;
	}

	public void setPlazas(List<Plaza> plazas)
	{
		this.plazas = plazas;
	}

	public int getPlazaSelec()
	{
		return plazaSelec;
	}

	public void setPlazaSelec(int plazaSelec)
	{
		this.plazaSelec = plazaSelec;
	}

	public String getNombreDescripcionProducto()
	{
		return nombreDescripcionProducto;
	}

	public void setNombreDescripcionProducto(String nombreDescripcionProducto)
	{
		this.nombreDescripcionProducto = nombreDescripcionProducto;
	}

	public int getAñoProducto()
	{
		return añoProducto;
	}

	public void setAñoProducto(int añoProducto)
	{
		this.añoProducto = añoProducto;
	}

	public int getQuincenaProducto()
	{
		return quincenaProducto;
	}

	public void setQuincenaProducto(int quincenaProducto)
	{
		this.quincenaProducto = quincenaProducto;
	}

	public UploadedFile getArchivoTRA()
	{
		return archivoTRA;
	}

	public void setArchivoTRA(UploadedFile archivoTRA)
	{
		this.archivoTRA = archivoTRA;
	}

	public int getnLinea()
	{
		return nLinea;
	}

	public void setnLinea(int nLinea)
	{
		this.nLinea = nLinea;
	}

	public int getProgress()
	{
		if (totalLineas == 0)
		{
			return 0;
		}

		setProgress((int) (nLineaTotal * 100 / totalLineas));
		return progress;
	}

	public void setProgress(int progress)
	{
		this.progress = progress;
	}

	public List<Plaza> getPlazasMasiva()
	{
		return plazasMasiva;
	}

	public void setPlazasMasiva(List<Plaza> plazasMasiva)
	{
		this.plazasMasiva = plazasMasiva;
	}

	public int getQuincenaMasiva()
	{
		return quincenaMasiva;
	}

	public void setQuincenaMasiva(int quincenaMasiva)
	{
		this.quincenaMasiva = quincenaMasiva;
	}

	public int getAñoMasiva()
	{
		return añoMasiva;
	}

	public void setAñoMasiva(int añoMasiva)
	{
		this.añoMasiva = añoMasiva;
	}

	public int getPlazaMasivaSelec()
	{
		return plazaMasivaSelec;
	}

	public void setPlazaMasivaSelec(int plazaMasivaSelec)
	{
		this.plazaMasivaSelec = plazaMasivaSelec;
	}

}
