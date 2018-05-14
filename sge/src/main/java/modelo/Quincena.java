package modelo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import util.utilidades;

public class Quincena
{
	private int						año;
	private int						nQuincena;
	private Plaza					plaza;
	private List<UnidadQuincena>	unidades;

	private List<Producto>			productos;
	private List<Producto>			prodCancelados;

	private BigDecimal				percepciones;
	private BigDecimal				deducciones;
	private BigDecimal				liquido;

	private Map<String, BigDecimal>	sumarizacionConceptos;

	private BigDecimal				percepcionesCancelados;
	private BigDecimal				deduccionesCancelados;
	private BigDecimal				liquidoCancelados;

	public Quincena()
	{
		super();
		// TODO Auto-generated constructor stub
		this.percepciones = new BigDecimal("0.00");
		this.deducciones = new BigDecimal("0.00");
		this.liquido = new BigDecimal("0.00");

		this.percepcionesCancelados = new BigDecimal("0.00");
		this.deduccionesCancelados = new BigDecimal("0.00");
		this.liquidoCancelados = new BigDecimal("0.00");
	}

	public Quincena(int año, int nQuincena, Plaza plaza, List<UnidadQuincena> unidades)
	{
		super();
		this.año = año;
		this.nQuincena = nQuincena;
		this.plaza = plaza;
		this.unidades = unidades;

		this.productos = new ArrayList<>();
		this.prodCancelados = new ArrayList<>();
		this.percepciones = new BigDecimal("0.00");
		this.deducciones = new BigDecimal("0.00");
		this.liquido = new BigDecimal("0.00");

		this.percepcionesCancelados = new BigDecimal("0.00");
		this.deduccionesCancelados = new BigDecimal("0.00");
		this.liquidoCancelados = new BigDecimal("0.00");
	}

	public Quincena(int año, int nQuincena, Plaza plaza)
	{
		super();
		this.año = año;
		this.nQuincena = nQuincena;
		this.plaza = plaza;
		this.productos = new ArrayList<>();
		this.prodCancelados = new ArrayList<>();
		this.unidades = new ArrayList<>();

		this.percepciones = new BigDecimal("0.00");
		this.deducciones = new BigDecimal("0.00");
		this.liquido = new BigDecimal("0.00");

		this.percepcionesCancelados = new BigDecimal("0.00");
		this.deduccionesCancelados = new BigDecimal("0.00");
		this.liquidoCancelados = new BigDecimal("0.00");
	}

	public void filtraDATUnidadesProductos()
	{

		PlantillaRegistro regDAT;
		String unidad;
		boolean trabajadorAIncluirse;

		int indicePercepciones;
		int indiceDeducciones;

		for (Producto prod : this.productos)
		{

			indicePercepciones = prod.getPlantillaDAT().getPosicionValorPorDescripcionContains("Percep");
			indiceDeducciones = prod.getPlantillaDAT().getPosicionValorPorDescripcionContains("Deduc");

			for (int x = 0; x < prod.getRegistrosDAT().size(); x++)
			{
				regDAT = prod.getRegistrosDAT().get(x);

				trabajadorAIncluirse = false;

				unidad = utilidades.determinaUnidad(this.plaza, regDAT);

				// Se revisa en cada unidad si corresponde
				for (UnidadQuincena unid : this.unidades)
				{

					if (unid.getDescripcion().trim().equals(unidad.trim()))
					{

						if (prod.getTipoNomina().getIdTipoNomina() == 1)
						{
							unid.addPercepcionesDeduccionesPension(
									new BigDecimal(regDAT.getValorEnCampo(indicePercepciones)),
									new BigDecimal(regDAT.getValorEnCampo(indiceDeducciones)));
						}
						else
						{
							unid.addPercepcionesDeducciones(new BigDecimal(regDAT.getValorEnCampo(indicePercepciones)),
									new BigDecimal(regDAT.getValorEnCampo(indiceDeducciones)));
						}

						trabajadorAIncluirse = true;
						break;
					}

				}

				if (!trabajadorAIncluirse)
				{
					prod.getRegistrosDAT().remove(x);
					x--;
				}

			}

		}

		this.liquido = this.percepciones.subtract(this.deducciones);

		for (UnidadQuincena unid : this.unidades)
		{

			System.out.println(unid.getDescripcion() + " - Total de Percep: " + unid.getPercepciones());
			System.out.println(unid.getDescripcion() + " - Total de Deduc: " + unid.getDeducciones());
			System.out.println(unid.getDescripcion() + " - Total de Líquido: " + unid.getLiquido());

			System.out.println(unid.getDescripcion() + " - Total Pensión de Percep: " + unid.getPercepcionesPension());
			System.out.println(unid.getDescripcion() + " - Total Pensión de Deduc: " + unid.getDeduccionesPension());
			System.out.println(unid.getDescripcion() + " - Total Pensión de Líquido: " + unid.getLiquidoPension());

		}

	}

	public void filtraDATUnidadesCancelados()
	{

		PlantillaRegistro regDAT;
		String unidad;
		boolean trabajadorAIncluirse;

		int indicePercepciones;
		int indiceDeducciones;

		for (Producto prod : this.prodCancelados)
		{
			indicePercepciones = prod.getPlantillaDAT().getPosicionValorPorDescripcionContains("Percep");
			indiceDeducciones = prod.getPlantillaDAT().getPosicionValorPorDescripcionContains("Deduc");

			for (int x = 0; x < prod.getRegistrosDAT().size(); x++)
			{
				regDAT = prod.getRegistrosDAT().get(x);

				trabajadorAIncluirse = false;

				unidad = utilidades.determinaUnidad(this.plaza, regDAT);

				// Se revisa en cada unidad si corresponde
				for (UnidadQuincena unid : this.unidades)
				{

					if (unid.getDescripcion().trim().equals(unidad.trim()))
					{

						if (prod.getTipoNomina().getIdTipoNomina() == 1)
						{
							unid.addPercepcionesDeduccionesCanceladosPension(
									new BigDecimal(regDAT.getValorEnCampo(indicePercepciones)),
									new BigDecimal(regDAT.getValorEnCampo(indiceDeducciones)));
						}
						else
						{
							unid.addPercepcionesDeduccionesCancelados(
									new BigDecimal(regDAT.getValorEnCampo(indicePercepciones)),
									new BigDecimal(regDAT.getValorEnCampo(indiceDeducciones)));
						}

						trabajadorAIncluirse = true;
						break;
					}

				}

				if (!trabajadorAIncluirse)
				{
					prod.getRegistrosDAT().remove(x);
					x--;
				}

			}

		}

		this.liquidoCancelados = this.percepcionesCancelados.subtract(this.deduccionesCancelados);

		for (UnidadQuincena unid : this.unidades)
		{

			System.out.println(unid.getDescripcion() + " - CAN Total de Percep: " + unid.getPercepcionesCancelados());
			System.out.println(unid.getDescripcion() + " - CAN Total de Deduc: " + unid.getDeduccionesCancelados());
			System.out.println(unid.getDescripcion() + " - CAN Total de Líquido: " + unid.getLiquidoCancelados());

			System.out.println(unid.getDescripcion() + " - CAN Total Pensión de Percep: "
					+ unid.getPercepcionesCanceladosPension());
			System.out.println(
					unid.getDescripcion() + " - CAN Total Pensión de Deduc: " + unid.getDeduccionesCanceladosPension());
			System.out.println(
					unid.getDescripcion() + " - CAN Total Pensión de Líquido: " + unid.getLiquidoCanceladosPension());

		}

	}

	public void addColumnasReporte(List<Columna> colsReporte)
	{
		for (UnidadQuincena unid : this.unidades)
		{
			unid.setColumnas(new ArrayList<>());

			for (Columna col : colsReporte)
			{
				unid.getColumnas().add((Columna) col.clone());

			}

		}

	}

	public void procesaColumnas()
	{

		PlantillaRegistro regDAT;
		String unidad;

		for (UnidadQuincena unid : this.unidades)
		{

			for (Columna col : unid.getColumnas())
			{

				if (col.isProductos())
				{
					for (Producto prod : this.productos)
					{

						if (prod.getRegistrosTRA() == null)
						{
							prod.enlazaTRAconDAT();

						}

						for (int x = 0; x < prod.getRegistrosDAT().size(); x++)
						{
							regDAT = prod.getRegistrosDAT().get(x);

							unidad = utilidades.determinaUnidad(this.plaza, regDAT);

							// Se revisa en cada unidad si corresponde
							if (unid.getDescripcion().trim().equals(unidad.trim()))
							{

								if (col.getConceptosIncluidos() != null)
								{
									BigDecimal sumaColumna = new BigDecimal("0.00");

									for (Concepto con : col.getConceptosIncluidos())
									{

										sumaColumna = sumaColumna
												.add(regDAT.getValorConcepto("" + con.getTipoConcepto(), con.getClave(),
														con.getPartidaAntecedente()));

									}

									/*
									 * if (sumaColumna.compareTo(new
									 * BigDecimal("0.00")) != 0) {
									 * System.out.println("Total de la columna "
									 * + col.getDescripcion() + " en qna: " +
									 * this.nQuincena + " producto: " +
									 * prod.getDescripcion() + " trabajador: " +
									 * regDAT.getValorPorDescripcionContains(
									 * "RFC") + ": " + sumaColumna);
									 * 
									 * }
									 */

									col.setTotal(col.getTotal().add(sumaColumna));

								}
							}

						}

						// System.out.println("Total de la columna " +
						// col.getDescripcion() + " en qna: " + this.nQuincena
						// + ": " + col.getTotal() + " producto: " +
						// prod.getDescripcion());

					}

				}

				if (col.isCancelados())
				{

					for (Producto prod : this.prodCancelados)
					{

						if (prod.getRegistrosTRA() == null)
						{
							prod.enlazaTRAconDAT();

						}

						for (int x = 0; x < prod.getRegistrosDAT().size(); x++)
						{
							regDAT = prod.getRegistrosDAT().get(x);

							unidad = utilidades.determinaUnidad(this.plaza, regDAT);

							// Se revisa en cada unidad si corresponde
							if (unid.getDescripcion().trim().equals(unidad.trim()))
							{

								if (col.getConceptosIncluidos() != null)
								{

									BigDecimal sumaColumna = new BigDecimal("0.00");

									for (Concepto con : col.getConceptosIncluidos())
									{

										sumaColumna = sumaColumna
												.add(regDAT.getValorConcepto("" + con.getTipoConcepto(), con.getClave(),
														con.getPartidaAntecedente()));

									}

									if (sumaColumna.compareTo(new BigDecimal("0.00")) != 0)
									{
										System.out.println("Total de la columna " + col.getDescripcion() + " en qna: "
												+ this.nQuincena + " producto: " + prod.getDescripcion()
												+ " trabajador: " + regDAT.getValorPorDescripcionContains("RFC") + ": "
												+ sumaColumna);

									}

									col.setTotal(col.getTotal().add(sumaColumna));

								}

							}

						}

					}

				}

			}
		}

		for (Producto prod : this.productos)
		{
			prod.setRegistrosDAT(null);
			prod.setRegistrosTRA(null);
		}

		for (Producto prod : this.prodCancelados)
		{
			prod.setRegistrosDAT(null);
			prod.setRegistrosTRA(null);
		}

	}

	public void addUnidades(List<Unidad> unidades)
	{

		for (Unidad uni : unidades)
		{
			UnidadQuincena uniQna = new UnidadQuincena(uni.getIdUnidad(), uni.getDescripcion());
			this.unidades.add(uniQna);
		}

	}

	public void updateProductos()
	{
		this.productos = utilidades.getProductosPeriodo(this.año, this.nQuincena, this.año, this.nQuincena, true, false,
				plaza.getIdPlaza());
	}

	public void updateCancelados()
	{
		this.prodCancelados = utilidades.getProductosPeriodo(this.año, this.nQuincena, this.año, this.nQuincena, false,
				true, plaza.getIdPlaza());
	}

	public void updateDATsProductos()
	{
		for (Producto prod : this.productos)
		{
			System.out.println("DAT prod: " + prod.getDescripcion());
			prod.updateRegistrosDAT();
		}
	}

	public void updateDATsCancelados()
	{
		for (Producto can : this.prodCancelados)
		{
			System.out.println("DAT prodCan: " + can.getDescripcion());
			can.updateRegistrosDAT();

		}

	}

	public int getAño()
	{
		return año;
	}

	public void setAño(int año)
	{
		this.año = año;
	}

	public int getnQuincena()
	{
		return nQuincena;
	}

	public void setnQuincena(int nQuincena)
	{
		this.nQuincena = nQuincena;
	}

	public Plaza getPlaza()
	{
		return plaza;
	}

	public void setPlaza(Plaza plaza)
	{
		this.plaza = plaza;
	}

	public List<UnidadQuincena> getUnidades()
	{
		return unidades;
	}

	public void setUnidades(List<UnidadQuincena> unidades)
	{
		this.unidades = unidades;
	}

	public List<Producto> getProductos()
	{
		return productos;
	}

	public void setProductos(List<Producto> productos)
	{
		this.productos = productos;
	}

	public List<Producto> getProdCancelados()
	{
		return prodCancelados;
	}

	public void setProdCancelados(List<Producto> prodCancelados)
	{
		this.prodCancelados = prodCancelados;
	}

	public BigDecimal getPercepciones()
	{
		return percepciones;
	}

	public void setPercepciones(BigDecimal percepciones)
	{
		this.percepciones = percepciones;
	}

	public BigDecimal getDeducciones()
	{
		return deducciones;
	}

	public void setDeducciones(BigDecimal deducciones)
	{
		this.deducciones = deducciones;
	}

	public BigDecimal getLiquido()
	{
		return liquido;
	}

	public void setLiquido(BigDecimal liquido)
	{
		this.liquido = liquido;
	}

	public Map<String, BigDecimal> getSumarizacionConceptos()
	{
		return sumarizacionConceptos;
	}

	public void setSumarizacionConceptos(Map<String, BigDecimal> sumarizacionConceptos)
	{
		this.sumarizacionConceptos = sumarizacionConceptos;
	}

	public BigDecimal getPercepcionesCancelados()
	{
		return percepcionesCancelados;
	}

	public void setPercepcionesCancelados(BigDecimal percepcionesCancelados)
	{
		this.percepcionesCancelados = percepcionesCancelados;
	}

	public BigDecimal getDeduccionesCancelados()
	{
		return deduccionesCancelados;
	}

	public void setDeduccionesCancelados(BigDecimal deduccionesCancelados)
	{
		this.deduccionesCancelados = deduccionesCancelados;
	}

	public BigDecimal getLiquidoCancelados()
	{
		return liquidoCancelados;
	}

	public void setLiquidoCancelados(BigDecimal liquidoCancelados)
	{
		this.liquidoCancelados = liquidoCancelados;
	}

}
