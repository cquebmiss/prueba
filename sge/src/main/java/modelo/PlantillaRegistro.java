/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import util.utilidades;

/**
 *
 * @author desarolloyc
 */
public class PlantillaRegistro implements Serializable, Cloneable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int idRegistro;
	private Plantilla plantilla;
	// Se utiliza para vincular registros de una plantilla diferente con el
	// registro de esta plantilla
	private List<PlantillaRegistro> registrosVinculados;

	// Se utiliza para el reporte de acumulados únicamente
	private HashMap<String, Concepto> conceptosAcum;
	// Se utiliza para el reporte de varios
	private List<Concepto> listConceptosAcum;

	// Sirve para almacenar los conceptos con sus valores totales
	private List<Concepto> conceptos;
	private List<Concepto> conceptosDeduc;

	// Atributo para acumular el total de conceptosAsociadosALaPlaza
	private BigDecimal totalAcumulado;

	// indica si el registro esta de alta o de baja, esto para utilizar en los
	// archivos de banco para indicar si el registro deberá ser eliminado al
	// generar el archivo final: -1 de baja, 0 normal o activo
	private int status;

	// para servir de referencia en los registros vinculados
	private String motivoVinculo;
	private String observaciones;

	public PlantillaRegistro(int idRegistro, Plantilla plantilla)
	{
		this.idRegistro = idRegistro;
		this.plantilla = plantilla;
		this.registrosVinculados = new ArrayList<>();
		conceptosAcum = new HashMap();
		this.totalAcumulado = new BigDecimal("0.00");

	}

	public void intercambioStatus()
	{
		if (this.status == 0)
		{
			this.status = -1;
		}
		else if (this.status == -1)
		{
			this.status = 0;
		}

	}

	@Override
	public Object clone()
	{
		PlantillaRegistro clon = null;

		try
		{
			clon = (PlantillaRegistro) super.clone();

			if (this.conceptosAcum != null)
			{
				HashMap<String, Concepto> clonConceptosAcum = new HashMap<String, Concepto>();

				this.conceptosAcum.forEach((k, v) ->
				{
					Concepto clonConcepto = (Concepto) v.clone();
					clonConceptosAcum.put(k, clonConcepto);
				});

				clon.setConceptosAcum(clonConceptosAcum);

			}

			if (this.plantilla != null)
			{
				clon.setPlantilla((Plantilla) this.plantilla.clone());
			}

			if (this.registrosVinculados != null)
			{
				List<PlantillaRegistro> registrosVinculadosClon = new ArrayList<>();

				this.registrosVinculados.forEach(item ->
				{
					registrosVinculadosClon.add((PlantillaRegistro) item.clone());

				});

				clon.setRegistrosVinculados(registrosVinculadosClon);

			}

			if (this.listConceptosAcum != null)
			{
				List<Concepto> listConceptosAcumClon = new ArrayList<>();

				this.listConceptosAcum.forEach(item ->
				{
					listConceptosAcumClon.add((Concepto) item.clone());

				});

				clon.setListConceptosAcum(listConceptosAcumClon);

			}

			if (this.conceptos != null)
			{
				List<Concepto> conceptosClon = new ArrayList<>();

				this.conceptos.forEach(item ->
				{
					conceptosClon.add((Concepto) item.clone());

				});

				clon.setConceptos(conceptosClon);

			}

			if (this.conceptosDeduc != null)
			{
				List<Concepto> conceptosDeducClon = new ArrayList<>();

				this.conceptosDeduc.forEach(item ->
				{
					conceptosDeducClon.add((Concepto) item.clone());

				});

				clon.setConceptosDeduc(conceptosDeducClon);

			}

			if (this.totalAcumulado != null)
			{
				clon.setTotalAcumulado(new BigDecimal(this.totalAcumulado.toPlainString()));

			}

		}
		catch (CloneNotSupportedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return clon;

	}

	public void incilizaObjsConceptos()
	{
		this.conceptos = new ArrayList<>();
		this.conceptosDeduc = new ArrayList<>();
	}

	public void addImporteAcumulado(BigDecimal importe)
	{
		this.totalAcumulado = totalAcumulado.add(importe);
	}

	public void limpiaMotivosVinculo()
	{
		this.motivoVinculo = "";
		this.observaciones = "";
	}

	public void addConcepto(Concepto conceptoAñadiendo)
	{

		if (conceptos == null)
		{
			conceptos = new ArrayList<>();

		}

		if (conceptosDeduc == null)
		{
			conceptosDeduc = new ArrayList<>();
		}

		switch (conceptoAñadiendo.getTipoConcepto())
		{
			case 1:
			case 3:
			case 5:
				conceptos.add(conceptoAñadiendo.getClone());

			break;

			case 2:
			case 4:
			case 6:

				conceptosDeduc.add(conceptoAñadiendo.getClone());
			break;
		}

	}

	// Se recorren todos los concepto para ajustar los que empiecen con 3 se
	// reemplacen con 2 para ponerlo como deducción
	public void ajustaConcepto3Acum()
	{

		Set setConceptos = this.conceptosAcum.entrySet();
		Iterator iteratorConceptos = setConceptos.iterator();

		while (iteratorConceptos.hasNext())
		{
			Map.Entry<?, Concepto> me = (Map.Entry) iteratorConceptos.next();

			Concepto con = me.getValue();

			if (con.getTipoConcepto() == 3)
			{
				con.setTipoConcepto(2);
			}

		}

	}

	public boolean tieneSueldo()
	{
		Double importeD = null;

		for (CampoPlantilla campo : this.plantilla.getCampos())
		{
			if (campo.getDescripcion().contains("Sueldo") || campo.getDescripcion().contains("sueldo"))
			{
				importeD = Double.parseDouble(campo.getValor());

				if (importeD > 0.00)
				{
					return true;
				}
			}
		}

		return false;
	}

	public String getValorPorDescripcion(String descripcion)
	{
		for (CampoPlantilla campo : this.plantilla.getCampos())
		{
			if (campo.getDescripcion().equalsIgnoreCase(descripcion))
			{
				return campo.getValor();
			}
		}

		return null;
	}

	public String getValorPorDescripcionContainsConValorModificado(String descripcion)
	{
		String desc = descripcion.toLowerCase();

		for (CampoPlantilla campo : this.plantilla.getCampos())
		{
			if (campo.getDescripcion().toLowerCase().contains(desc))
			{
				if (campo.getValorModificado() != null)
				{
					return campo.getValorModificado();
				}
				else
				{
					return campo.getValor();
				}
			}
		}

		return null;
	}

	public String getValorPorDescripcionContains(String descripcion)
	{

		final String desc = descripcion.toLowerCase();
		/*
		 * final long start = System.nanoTime();
		 * this.plantilla.getCampos().stream().filter(campo ->
		 * campo.getDescripcion().toLowerCase().contains(desc))
		 * .findAny().ifPresent(campo -> { System.out.println("Encontrado en: " +
		 * (System.nanoTime() - start) + "( declarativo)"); });
		 * 
		 * final long start1 = System.nanoTime();
		 */
		for (CampoPlantilla campo : this.plantilla.getCampos())
		{
			if (campo.getDescripcion().toLowerCase().contains(desc))
			{
				// System.out.println("Encontrado en: " + (System.nanoTime() - start1) + "
				// (imperativo)");
				return campo.getValor();
			}
		}

		return null;
	}

	public String getValorPorDescripcionContainsFormatoMoneda(String descripcion)
	{
		String cantidad = getValorPorDescripcionContains(descripcion);

		return utilidades.formatoMoneda(cantidad);
	}

	// Devuelve el índice de un campo específico buscado por su descripción
	public int getPosicionValorPorDescripcionContains(String descripcion)
	{
		String desc = descripcion.toLowerCase();

		for (int x = 0; x < this.plantilla.getCampos().size(); x++)
		{
			if (this.plantilla.getCampos().get(x).getDescripcion().toLowerCase().contains(desc))
			{
				return x + 1;
			}
		}

		return -1;
	}

	public String getValorEnCampo(int orden)
	{

		for (CampoPlantilla campo : this.plantilla.getCampos())
		{
			if (campo.getOrden() == orden)
			{
				return campo.getValor();
			}
		}

		return null;
	}

	public CampoPlantilla getCampo(int orden)
	{
		for (CampoPlantilla campo : this.plantilla.getCampos())
		{
			if (campo.getOrden() == orden)
			{
				return campo;
			}
		}

		return null;
	}

	// Se obtienen los totales de percepciones, deducciones y neto
	public List<BigDecimal> getPercepDeducNeto()
	{
		List<BigDecimal> totales = new ArrayList<>();

		BigDecimal percep = new BigDecimal("0.00");
		BigDecimal deduc = new BigDecimal("0.00");
		BigDecimal neto = new BigDecimal("0.00");

		for (Object obj : this.conceptosAcum.values())
		{
			Concepto con = (Concepto) obj;

			switch (con.getTipoConcepto())
			{
				case 1:
				case 5:
					percep = percep.add(con.getValor());

				break;

				case 2:
				case 3:
				case 4:
				case 6:
					deduc = deduc.add(con.getValor());

				break;

				default:
				break;
			}

		}

		neto = percep.subtract(deduc);

		totales.add(percep);
		totales.add(deduc);
		totales.add(neto);

		return totales;
	}

	public BigDecimal getValorConcepto(String tipoConcepto, String clave, String partidaAntecedente)
	{

		BigDecimal total = new BigDecimal("0.00");

		int indiceTipoConcepto = -1;
		int indiceConcepto = -1;
		int indicePartida = -1;
		int indicePartidaAntecedente = -1;
		int indiceImporte = -1;

		for (PlantillaRegistro regTRA : this.registrosVinculados)
		{
			if (indiceTipoConcepto == -1)
			{
				indiceTipoConcepto = regTRA.plantilla.getPosicionValorPorDescripcionContains("ipo de concep");
			}

			if (indiceConcepto == -1)
			{
				indiceConcepto = regTRA.plantilla.getPosicionValorPorDescripcion("Concepto");
			}

			if (indicePartida == -1)
			{
				indicePartida = regTRA.plantilla.getPosicionValorPorDescripcion("partida");
			}

			if (indicePartidaAntecedente == -1)
			{
				indicePartidaAntecedente = regTRA.plantilla.getPosicionValorPorDescripcionContains("partida de ante");
			}

			if (indiceImporte == -1)
			{
				indiceImporte = regTRA.plantilla.getPosicionValorPorDescripcionContains("Importe");
			}

			if (regTRA.getValorEnCampo(indiceTipoConcepto).trim().equalsIgnoreCase(tipoConcepto))
			{

				if (regTRA.getValorEnCampo(indiceConcepto).trim().equalsIgnoreCase(clave))
				{

					if (regTRA.getValorEnCampo(indicePartidaAntecedente).trim().equalsIgnoreCase(partidaAntecedente))
					{

						total = total.add(new BigDecimal(regTRA.getValorEnCampo(indiceImporte).trim()));

					}

				}

			}

		}

		return total;

	}

	// Busca los conceptos tomando en cuenta que ésta objeto de plantilla es un
	// DAT, así que busca en sus conceptos asociados
	public BigDecimal getValorConceptoAcum(String clave, String partida, String partidaAntecedente)
	{
		BigDecimal valor = new BigDecimal("0.00");

		if (this.conceptos != null)
		{

			for (Concepto con : this.conceptos)
			{
				if (con.getClave().trim().equalsIgnoreCase(clave.trim()))
				{

					if (partida == null && partidaAntecedente == null)
					{
						valor = valor.add(con.getValor());
						continue;
					}

					if (partida == null)
					{
						if (con.getPartidaAntecedente().trim().equalsIgnoreCase(partidaAntecedente.trim()))
						{
							valor = valor.add(con.getValor());
							continue;
						}

					}
					else
					{
						if (con.getPartida().trim().equalsIgnoreCase(partida.trim()))
						{
							if (partidaAntecedente == null)
							{
								valor = valor.add(con.getValor());
								continue;
							}
							else
							{
								if (con.getPartidaAntecedente().trim().equalsIgnoreCase(partidaAntecedente.trim()))
								{
									valor = valor.add(con.getValor());
									continue;
								}

							}

						}

					}

				}

			}

		}

		if (this.conceptosDeduc != null)
		{
			for (Concepto con : this.conceptosDeduc)
			{
				if (con.getClave().trim().equalsIgnoreCase(clave.trim()))
				{

					if (partida == null && partidaAntecedente == null)
					{
						valor = valor.add(con.getValor());
						continue;
					}

					if (partida == null)
					{
						if (con.getPartidaAntecedente().trim().equalsIgnoreCase(partidaAntecedente.trim()))
						{
							valor = valor.add(con.getValor());
							continue;
						}

					}
					else
					{
						if (con.getPartida().trim().equalsIgnoreCase(partida.trim()))
						{
							if (partidaAntecedente == null)
							{
								valor = valor.add(con.getValor());
								continue;
							}
							else
							{
								if (con.getPartidaAntecedente().trim().equalsIgnoreCase(partidaAntecedente.trim()))
								{
									valor = valor.add(con.getValor());
									continue;
								}

							}

						}

					}

				}

			}

		}

		return valor;

	}

	// Realiza la acumulación de los valores del concepto indicado, y puede
	// acumular excluyendo alguna partida en específico
	public BigDecimal getValorEnListaConceptoAcum(String clave, String partida, String partidaAntecedente,
			String partidaAntecedenteExcluida)
	{

		BigDecimal valor = new BigDecimal("0.00");

		if (this.conceptosAcum != null)
		{

			Set<?> setElementos = conceptosAcum.entrySet();
			Iterator<?> iterator = setElementos.iterator();

			while (iterator.hasNext())
			{
				Map.Entry<String, Concepto> me = (Map.Entry) iterator.next();

				Concepto con = (Concepto) me.getValue();

				// Verifica si hay que excluir alguna partida en específico
				if (partidaAntecedenteExcluida != null)
				{
					if (con.getPartidaAntecedente().trim().equalsIgnoreCase(partidaAntecedenteExcluida.trim()))
					{
						continue;

					}
				}

				if (clave == null || con.getClave().trim().equalsIgnoreCase(clave.trim()))
				{

					if (partida == null && partidaAntecedente == null)
					{
						valor = valor.add(con.getValor());
						continue;
					}

					if (partida == null)
					{
						if (con.getPartidaAntecedente().trim().equalsIgnoreCase(partidaAntecedente.trim()))
						{
							valor = valor.add(con.getValor());
							continue;
						}

					}
					else
					{
						if (con.getPartida().trim().equalsIgnoreCase(partida.trim()))
						{
							if (partidaAntecedente == null)
							{
								valor = valor.add(con.getValor());
								continue;
							}
							else
							{
								if (con.getPartidaAntecedente().trim().equalsIgnoreCase(partidaAntecedente.trim()))
								{
									valor = valor.add(con.getValor());
									continue;
								}

							}

						}

					}

				}

			}

		}

		return valor;

	}

	// La lista se compone de 0 - String clave de concepto, 1 - Descripción del
	// concepto, 2 - Importe Bruto, 3 - ISR, 4 - Importe Neto
	public List<List<String>> getConceptosISRCalculados()
	{
		List<List<String>> conceptosAIncluir = new ArrayList<>();

		// Obtiene los conceptos
		final Map<String, List<Concepto>> conceptosFiltrados = this.conceptosAcum.values().stream().filter(c ->
		{

			if (c.getTipoConcepto() != 1)
			{
				return false;
			}

			if ((c.getClave().equals("69") && c.getPartidaAntecedente().equals("TR"))

					|| (c.getClave().equals("59")
							&& (c.getPartidaAntecedente().equals("DT") || c.getPartidaAntecedente().equals("TT")))
					|| (c.getClave().equals("73") && (c.getPartidaAntecedente().equals("DM")
							|| c.getPartidaAntecedente().equals("MM") || c.getPartidaAntecedente().equals("RR")
							|| c.getPartidaAntecedente().equals("DR") || c.getPartidaAntecedente().equals("00")))

					|| (c.getClave().equals("69") && (c.getPartidaAntecedente().equals("AN")))
					|| (c.getClave().equals("75") && (c.getPartidaAntecedente().equals("AP")))
					|| (c.getClave().equals("68")
							&& (c.getPartidaAntecedente().equals("EA") || c.getPartidaAntecedente().equals("AA")))
					|| (c.getClave().equals("37") && (c.getPartidaAntecedente().equals("00")
							|| c.getPartidaAntecedente().equals("TP") || c.getPartidaAntecedente().equals("TT")))
					|| (c.getClave().equals("45") && (c.getPartidaAntecedente().equals("00")))
					|| (c.getClave().equals("57") && (c.getPartidaAntecedente().equals("LM"))))
			{

				return true;

			}

			return false;

		}).collect(Collectors.groupingBy(c ->
		{
			String pa = "";

			if (c.getClave().equals("73") &&

					(c.getPartidaAntecedente().equals("RR") || c.getPartidaAntecedente().equals("00")))
			{
				pa = "DR";
			}
			else if (c.getClave().equals("59") && c.getPartidaAntecedente().equals("TT"))
			{
				pa = "DT";
			}
			else if (c.getClave().equals("73") && c.getPartidaAntecedente().equals("MM"))
			{
				pa = "DM";
			}
			else if (c.getClave().equals("68") && c.getPartidaAntecedente().equals("AA"))
			{
				pa = "EA";
			}
			else if (c.getClave().equals("37") && (c.getPartidaAntecedente().equals("00")
					|| c.getPartidaAntecedente().equals("TP") || c.getPartidaAntecedente().equals("TT")))
			{
				pa = "TP";
			}
			else
			{
				pa = c.getPartidaAntecedente();
			}

			return "" + c.getTipoConcepto() + "-" + c.getClave() + "-" + pa;
		}));

		// Se verifican las compensaciones y los conceptos que se gravan al ISR
		conceptosFiltrados.entrySet().forEach(e ->

		{

			Optional<BigDecimal> sumarizacionConceptos = e.getValue().stream().map(c -> c.getValor())
					.reduce(BigDecimal::add);

			String clave = e.getKey();

			String[] claveDesglose = e.getKey().split("-");
			final String adicional;

			// e.getValue().getTipoConcepto() == 2 && e.getValue().getClave().equals("01")

			Optional<BigDecimal> totalISR = this.conceptosAcum.values().stream().filter(c ->
			{
				if (!(c.getTipoConcepto() == 2 && c.getClave().equals("01")))
				{
					return false;
				}
				if (c.getPartidaAntecedente().equals("TR"))
				{
					return clave.contains("1-69");
				}
				else if (c.getPartidaAntecedente().equals("DT"))
				{
					return clave.contains("1-59");
				}
				else if (c.getPartidaAntecedente().equals("DM"))
				{
					return clave.contains("1-73");
				}
				else if (c.getPartidaAntecedente().equals("DR"))
				{
					return clave.contains("1-73");

				}
				else if (c.getPartidaAntecedente().equals("AN"))
				{
					return clave.contains("1-69");

				}
				else if (c.getPartidaAntecedente().equals("AP"))
				{
					return clave.contains("1-75");

				}
				else if (c.getPartidaAntecedente().equals("EA"))
				{
					return clave.contains("1-68");

				}
				else if (c.getPartidaAntecedente().equals("ES")) // Premios y Recompensas ES con 137 00
				{
					return clave.contains("1-37-ES");

				}
				else if (c.getPartidaAntecedente().equals("TP"))// Tesis
				{
					return clave.contains("1-37-TP");

				}
				else if (c.getPartidaAntecedente().equals("45"))// Lentes
				{
					System.out.println("Impuesto de lentes "+getValorPorDescripcionContains("RFC"));
					return clave.contains("1-45");

				}
				else if (c.getPartidaAntecedente().equals("57"))// Licencia de manejo
				{
					return clave.contains("1-57");

				}
				// Estímulos a médicos, enfermeras, etc
				else if (c.getPartidaAntecedente().equals("MD") || c.getPartidaAntecedente().equals("EN")
						|| c.getPartidaAntecedente().equals("TS") || c.getPartidaAntecedente().equals("QM")
						|| c.getPartidaAntecedente().equals("ON"))
				{
					return clave.contains("1-56");

				}

				return false;

			}).map(c -> c.getValor()).reduce(BigDecimal::add);

			if (totalISR.isPresent())
			{
				// La lista se compone de 0 - String clave de concepto, 1 - Descripción del
				// concepto, 2 - Importe Bruto, 3 - ISR, 4 - Importe Neto
				List<String> elemento = Arrays.asList(clave, "", sumarizacionConceptos.get().toString(),
						totalISR.get().toString(), sumarizacionConceptos.get().subtract(totalISR.get()).toString());
				conceptosAIncluir.add(elemento);

			}
			else
			{
				List<String> elemento = Arrays.asList(clave, "", sumarizacionConceptos.get().toString(), "0.00",
						sumarizacionConceptos.get().toString());
				conceptosAIncluir.add(elemento);

			}

		});

		return conceptosAIncluir;

	}

	/**
	 * @return the idRegistro
	 */
	public int getIdRegistro()
	{
		return idRegistro;
	}

	/**
	 * @param idRegistro
	 *            the idRegistro to set
	 */
	public void setIdRegistro(int idRegistro)
	{
		this.idRegistro = idRegistro;
	}

	/**
	 * @return the plantilla
	 */
	public Plantilla getPlantilla()
	{
		return plantilla;
	}

	/**
	 * @param plantilla
	 *            the plantilla to set
	 */
	public void setPlantilla(Plantilla plantilla)
	{
		this.plantilla = plantilla;
	}

	public List<PlantillaRegistro> getRegistrosVinculados()
	{
		return registrosVinculados;
	}

	public void setRegistrosVinculados(List<PlantillaRegistro> registrosVinculados)
	{
		this.registrosVinculados = registrosVinculados;
	}

	public HashMap getConceptosAcum()
	{
		return conceptosAcum;
	}

	public void setConceptosAcum(HashMap conceptosAcum)
	{
		this.conceptosAcum = conceptosAcum;
	}

	public static long getSerialversionuid()
	{
		return serialVersionUID;
	}

	public List<Concepto> getConceptos()
	{
		return conceptos;
	}

	public void setConceptos(List<Concepto> conceptos)
	{
		this.conceptos = conceptos;
	}

	public List<Concepto> getConceptosDeduc()
	{
		return conceptosDeduc;
	}

	public void setConceptosDeduc(List<Concepto> conceptosDeduc)
	{
		this.conceptosDeduc = conceptosDeduc;
	}

	public BigDecimal getTotalAcumulado()
	{
		return totalAcumulado;
	}

	public void setTotalAcumulado(BigDecimal totalAcumulado)
	{
		this.totalAcumulado = totalAcumulado;
	}

	public List<Concepto> getListConceptosAcum()
	{
		return listConceptosAcum;
	}

	public void setListConceptosAcum(List<Concepto> listConceptosAcum)
	{
		this.listConceptosAcum = listConceptosAcum;
	}

	public int getStatus()
	{
		return status;
	}

	public void setStatus(int status)
	{
		this.status = status;
	}

	public String getMotivoVinculo()
	{
		return motivoVinculo;
	}

	public void setMotivoVinculo(String motivoVinculo)
	{
		this.motivoVinculo = motivoVinculo;
	}

	public String getObservaciones()
	{
		return observaciones;
	}

	public void setObservaciones(String observaciones)
	{
		this.observaciones = observaciones;
	}

}
