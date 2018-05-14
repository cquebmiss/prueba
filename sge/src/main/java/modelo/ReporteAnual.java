package modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import resources.DataBase;
import util.FacesUtils;
import util.utilidades;

public class ReporteAnual
{

	private int				idReporteAnual;
	private int				año;
	private String			descripcion;
	private Plaza			plaza;
	private List<Unidad>	unidades;

	// Atributo para la visualización en la tabla de flujo de efectivo, para la
	// visualización correcta de las unidades únicamente
	private String			unidadesIncluidas;
	private List<Integer>	etapasIncluidas;

	// Atributo para la visualización en la tabla de flujo de efectivo
	private String			etapasIncluidasString;
	private String			comentarios;
	private boolean			incluirProducto;
	private boolean			incluirCancelado;
	private int				tipoGeneracion;

	private List<Columna>	columnas;
	private List<Rubro>		rubros;

	public ReporteAnual()
	{
		super();
		this.idReporteAnual = -1;
		this.año = 0;
		this.descripcion = "";
		this.plaza = new Plaza(-1, "");
		this.unidades = new ArrayList<>();
		this.unidadesIncluidas = "";
		this.etapasIncluidas = new ArrayList<>();
		this.etapasIncluidasString = "";
		this.comentarios = "";
		this.incluirProducto = false;
		this.incluirCancelado = false;
		this.columnas = new ArrayList<>();
		this.rubros = new ArrayList<>();
		this.tipoGeneracion = 0;

		// TODO Auto-generated constructor stub
	}

	public ReporteAnual(int idReporteAnual, int año, String descripcion, Plaza plaza, List<Unidad> unidades,
			String unidadesIncluidas, List<Integer> etapasIncluidas, String etapasIncluidasString, String comentarios,
			boolean incluirProducto, boolean incluirCancelado, List<Columna> columnas, List<Rubro> rubros,
			int tipoGeneracion)
	{
		super();
		this.idReporteAnual = idReporteAnual;
		this.año = año;
		this.descripcion = descripcion;
		this.plaza = plaza;
		this.unidades = unidades;
		this.unidadesIncluidas = unidadesIncluidas;
		this.etapasIncluidas = etapasIncluidas;
		this.etapasIncluidasString = etapasIncluidasString;
		this.comentarios = comentarios;
		this.incluirProducto = incluirProducto;
		this.incluirCancelado = incluirCancelado;
		this.columnas = columnas;
		this.rubros = rubros;
		this.tipoGeneracion = tipoGeneracion;

		updateUnidadesString();
		updateEtapasString();

	}

	public ReporteAnual getClone()
	{
		ReporteAnual clon = new ReporteAnual();

		clon.setIdReporteAnual(this.idReporteAnual);
		clon.setAño(this.año);
		clon.setDescripcion(this.descripcion);
		clon.setPlaza(this.plaza.getClone());
		clon.setUnidades(this.unidades.subList(0, this.unidades.size()));
		clon.setUnidadesIncluidas(this.unidadesIncluidas);
		clon.setEtapasIncluidas(this.etapasIncluidas.subList(0, this.etapasIncluidas.size()));
		clon.setEtapasIncluidasString(this.etapasIncluidasString);
		clon.setComentarios(this.comentarios);
		clon.setIncluirProducto(this.incluirProducto);
		clon.setIncluirCancelado(this.incluirCancelado);
		clon.setColumnas(this.columnas.subList(0, this.columnas.size()));
		clon.setRubros(this.rubros.subList(0, this.rubros.size()));
		clon.setTipoGeneracion(this.tipoGeneracion);

		return clon;

	}

	public void updateUnidadesReporte()
	{
		this.unidades = utilidades.getUnidadesReporteAnual(this.idReporteAnual);
	}

	public void updateEtapasReporte()
	{
		this.etapasIncluidas = utilidades.getEtapasReporteAnual(this.idReporteAnual);
	}

	public void updateColumnas()
	{
		this.columnas = utilidades.getColumnasReporteAnual(this);

	}

	public void updateRubros()
	{
		this.rubros = utilidades.getRubrosReporteAnual(this);

	}

	public void updateUnidadesString()
	{
		String unidades = "";

		for (Unidad unidad : this.unidades)
		{
			unidades += unidad.getDescripcion() + ", ";
		}

		if (unidades.isEmpty())
		{
			this.unidadesIncluidas = unidades;
		}
		else
		{
			this.unidadesIncluidas = unidades.substring(0, unidades.length() - 2);
		}

	}

	public void updateEtapasString()
	{
		String etapas = "";

		for (Integer etapa : this.etapasIncluidas)
		{
			etapas += etapa + ", ";
		}

		if (etapas.isEmpty())
		{
			this.etapasIncluidasString = etapas;
		}
		else
		{
			this.etapasIncluidasString = etapas.substring(0, etapas.length() - 2);
		}

	}

	public void addColumnaVacia()
	{
		this.columnas.add(new Columna(-1, this, this.columnas.size() + 1, false, "" + this.columnas.size() + 1, "", 0));
	}

	public void addRubroVacio()
	{
		this.rubros.add(new Rubro(-1, this, this.rubros.size() + 1, "" + this.rubros.size() + 1, "", null, null));
	}

	public void removerColumna(Columna col)
	{
		this.columnas.remove(col);

		// Se reenumera el orden de las columnas
		for (int x = 0; x < this.columnas.size(); x++)
		{
			this.columnas.get(x).setOrden((x + 1));

		}

	}

	public void removerRubro(Rubro rub)
	{
		this.rubros.remove(rub);

		// Se reenumera el orden de los rubros
		for (int x = 0; x < this.rubros.size(); x++)
		{
			this.rubros.get(x).setOrden((x + 1));
		}

	}

	// 0 bajar, 1 subir
	public void moverColumna(Columna col, int opcion)
	{

		for (int x = 0; x < this.columnas.size(); x++)
		{

			if (this.columnas.get(x).equals(col))
			{

				switch (opcion)
				{
					case 0:

						if (x > 0)
						{
							// Se intercambian las posiciones de la columna
							Columna colAbajo = this.columnas.get((x - 1));
							Columna columna = this.columnas.get(x);

							this.columnas.set((x - 1), columna);
							this.columnas.set(x, colAbajo);

							colAbajo.setOrden(colAbajo.getOrden() + 1);
							columna.setOrden(columna.getOrden() - 1);

						}

						break;

					case 1:

						if (x < this.columnas.size() - 1)
						{
							Columna colArriba = this.columnas.get((x + 1));
							Columna columna = this.columnas.get(x);

							this.columnas.set((x + 1), columna);
							this.columnas.set(x, colArriba);

							colArriba.setOrden(colArriba.getOrden() - 1);
							columna.setOrden(columna.getOrden() + 1);

						}

						break;

				}

				break;

			}

		}

	}

	public void moverRubro(Rubro rub, int opcion)
	{

		for (int x = 0; x < this.rubros.size(); x++)
		{

			if (this.rubros.get(x).equals(rub))
			{

				switch (opcion)
				{
					case 0:

						if (x > 0)
						{
							// Se intercambian las posiciones de la columna
							Rubro rubAbajo = this.rubros.get((x - 1));
							Rubro rubro = this.rubros.get(x);

							this.rubros.set((x - 1), rubro);
							this.rubros.set(x, rubAbajo);

							rubAbajo.setOrden(rubAbajo.getOrden() + 1);
							rubro.setOrden(rubro.getOrden() - 1);

						}

						break;

					case 1:

						if (x < this.rubros.size() - 1)
						{
							Rubro rubArriba = this.rubros.get((x + 1));
							Rubro rubro = this.rubros.get(x);

							this.rubros.set((x + 1), rubro);
							this.rubros.set(x, rubArriba);

							rubArriba.setOrden(rubArriba.getOrden() - 1);
							rubro.setOrden(rubro.getOrden() + 1);

						}

						break;

				}

				break;

			}

		}

	}

	public void actionGuardarReporte()
	{

		PreparedStatement prep = null;
		ResultSet rBD = null;

		int id = 0;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionNominas();)
		{

			switch (this.idReporteAnual)
			{

				case -1:

					prep = conexion.prepareStatement("SELECT MAX(idReporteAnual) AS id FROM nominas.reporteanual");

					rBD = prep.executeQuery();

					if (rBD.next())
					{
						id = rBD.getInt("id");
					}

					boolean insertCorrecto = false;

					do
					{

						try
						{

							prep.close();

							prep = conexion.prepareStatement(
									" INSERT INTO reporteanual (idReporteAnual, Descripcion, idPlaza, "
											+ "Comentarios, "
											+ "IncluirProducto, IncluirCancelado, TipoGeneracion, Año) VALUES (?, ?, ?, ?, ?, ?, ?, ?) ");

							prep.setInt(1, id);
							prep.setString(2, this.descripcion);
							prep.setInt(3, this.plaza.getIdPlaza());
							prep.setString(4, this.comentarios);
							prep.setBoolean(5, this.incluirProducto);
							prep.setBoolean(6, this.incluirCancelado);
							prep.setInt(7, this.tipoGeneracion);
							prep.setInt(8, this.año);

							prep.executeUpdate();

							setIdReporteAnual(id);

							insertCorrecto = true;

						} catch (Exception e)
						{
							id++;
						}

					}
					while (!insertCorrecto);

					break;

				default:

					prep = conexion.prepareStatement(
							" UPDATE reporteanual SET Descripcion=?, idPlaza=?, " + "Comentarios=?, IncluirProducto=?,"
									+ " IncluirCancelado=?, TipoGeneracion=?, " + "Año=? WHERE idReporteAnual=? ");

					prep.setString(1, this.descripcion);
					prep.setInt(2, this.plaza.getIdPlaza());
					prep.setString(3, this.comentarios);
					prep.setBoolean(4, this.incluirProducto);
					prep.setBoolean(5, this.incluirCancelado);
					prep.setInt(6, this.tipoGeneracion);
					prep.setInt(7, this.año);

					prep.setInt(8, this.idReporteAnual);

					prep.executeUpdate();

					break;
			}

			prep.close();

			// Se añaden las unidades que conforman al reporte
			prep = conexion.prepareStatement("DELETE FROM unidadreporteanual WHERE idReporteAnual=?");
			prep.setInt(1, this.idReporteAnual);

			prep.executeUpdate();

			// Se van insertando las unidades del reporte
			for (Unidad unidad : this.unidades)
			{
				prep.close();
				prep = conexion
						.prepareStatement(" INSERT INTO unidadreporteanual (idReporteAnual, idUnidad) VALUES (?, ?) ");

				prep.setInt(1, this.idReporteAnual);
				prep.setInt(2, unidad.getIdUnidad());
				prep.executeUpdate();

			}

			// Se añaden las etapas que conforman al reporte
			prep.close();
			prep = conexion.prepareStatement("DELETE FROM etapareporteanual WHERE idReporteAnual=?");
			prep.setInt(1, this.idReporteAnual);

			prep.executeUpdate();

			// Se van insertando las etapas del reporte
			for (Integer et : this.etapasIncluidas)
			{
				prep.close();
				prep = conexion
						.prepareStatement(" INSERT INTO etapareporteanual (idReporteAnual, idEtapa) VALUES (?, ?) ");
				prep.setInt(1, this.idReporteAnual);
				prep.setInt(2, et);
				prep.executeUpdate();

			}

			// Guardar COLUMNAS y RUBROS
			prep.close();
			prep = conexion.prepareStatement("DELETE FROM columnareporteanual WHERE idReporteAnual=?");
			prep.setInt(1, this.idReporteAnual);

			prep.executeUpdate();

			for (int x = 0; x < this.columnas.size(); x++)
			{
				prep.close();
				prep = conexion.prepareStatement(" INSERT INTO columnareporteanual (idReporteAnual, "
						+ "Orden, AntesDelDetalle, " + "Descripcion, Formula, TipoDato) VALUES (?, ?, ?, ?, ?, ? ) ");

				prep.setInt(1, this.idReporteAnual);
				prep.setInt(2, this.columnas.get(x).getOrden());
				prep.setBoolean(3, this.columnas.get(x).isAntesDelDetalle());
				prep.setString(4, this.columnas.get(x).getDescripcion());
				prep.setString(5, this.columnas.get(x).getFormula());
				// Por defecto el tipo de dato cadena, pero más adelante podría
				// cambiar el tipo de dato
				prep.setInt(6, 0);

				prep.executeUpdate();

			}

			prep.close();
			prep = conexion.prepareStatement("DELETE FROM rubro WHERE idReporteAnual=?");
			prep.setInt(1, this.idReporteAnual);

			prep.executeUpdate();

			for (int x = 0; x < this.rubros.size(); x++)
			{
				prep.close();
				prep = conexion.prepareStatement(
						" INSERT INTO rubro (idReporteAnual, Orden, Descripcion, Formula) VALUES ( ?, ?, ?, ?); ");

				prep.setInt(1, this.idReporteAnual);
				prep.setInt(2, this.rubros.get(x).getOrden());
				prep.setString(3, this.rubros.get(x).getDescripcion());
				prep.setString(4, this.rubros.get(x).getFormula());

				prep.executeUpdate();

			}

			updateColumnas();
			updateRubros();

			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Reporte Guardado", "El reporte ha sido guardado exitosamente."));

		} catch (Exception e)
		{
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al momento de guardar el reporte, favor de contactar con el desarrollador del sistema."));
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

	public int getIdReporteAnual()
	{
		return idReporteAnual;
	}

	public void setIdReporteAnual(int idReporteAnual)
	{
		this.idReporteAnual = idReporteAnual;
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

	public List<Unidad> getUnidades()
	{
		return unidades;
	}

	public void setUnidades(List<Unidad> unidades)
	{
		this.unidades = unidades;
	}

	public String getUnidadesIncluidas()
	{
		return unidadesIncluidas;
	}

	public void setUnidadesIncluidas(String unidadesIncluidas)
	{
		this.unidadesIncluidas = unidadesIncluidas;
	}

	public List<Integer> getEtapasIncluidas()
	{
		return etapasIncluidas;
	}

	public void setEtapasIncluidas(List<Integer> etapasIncluidas)
	{
		this.etapasIncluidas = etapasIncluidas;
	}

	public String getComentarios()
	{
		return comentarios;
	}

	public void setComentarios(String comentarios)
	{
		this.comentarios = comentarios;
	}

	public boolean isIncluirProducto()
	{
		return incluirProducto;
	}

	public void setIncluirProducto(boolean incluirProducto)
	{
		this.incluirProducto = incluirProducto;
	}

	public boolean isIncluirCancelado()
	{
		return incluirCancelado;
	}

	public void setIncluirCancelado(boolean incluirCancelado)
	{
		this.incluirCancelado = incluirCancelado;
	}

	public String getEtapasIncluidasString()
	{
		return etapasIncluidasString;
	}

	public void setEtapasIncluidasString(String etapasIncluidasString)
	{
		this.etapasIncluidasString = etapasIncluidasString;
	}

	public int getAño()
	{
		return año;
	}

	public void setAño(int año)
	{
		this.año = año;
	}

	public List<Columna> getColumnas()
	{
		return columnas;
	}

	public void setColumnas(List<Columna> columnas)
	{
		this.columnas = columnas;
	}

	public List<Rubro> getRubros()
	{
		return rubros;
	}

	public void setRubros(List<Rubro> rubros)
	{
		this.rubros = rubros;
	}

	public int getTipoGeneracion()
	{
		return tipoGeneracion;
	}

	public void setTipoGeneracion(int tipoGeneracion)
	{
		this.tipoGeneracion = tipoGeneracion;
	}

}
