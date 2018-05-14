package modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import util.utilidades;

public class Producto implements Serializable, Cloneable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int idProducto;
	private String descripcion;
	private int año;
	private int quincena;
	private Plaza plaza;
	private TipoNomina tipoNomina;
	private TipoProducto tipoProducto;
	private String nombreProducto;

	Plantilla plantillaDAT;
	Plantilla plantillaTRA;

	private List<PlantillaRegistro> registrosDAT;
	private List<PlantillaRegistro> registrosTRA;

	// Auxiliares para visualización en tablas
	private List<PlantillaRegistro> registrosDATFilter;
	private List<PlantillaRegistro> registrosTRAFilter;

	private PlantillaRegistro registroDATSelec;
	private PlantillaRegistro registroTRASelec;

	BigDecimal bigPercepTotal;
	BigDecimal bigDeducTotal;
	BigDecimal bigRegPercep;
	BigDecimal bigRegDeduc;
	private String totalPercepiones;
	private String totalDeducciones;
	private String totalLiquido;
	private int totalRegistros;

	private List<UnidadProducto> unidadResponsable;
	private List<UnidadRegistros> unidadesRegsClasificados;

	private List<Concepto> conceptos;
	private List<Concepto> conceptosDeduc;

	// Se utiliza para llevar el control para la validación de los conceptos que
	// están o no asociados a plaza durante el análisis de la información
	Map<String, Concepto> conceptosAsociadosAPlaza;
	Map<String, Concepto> conceptosNoAsociadosAPlaza;

	public Producto()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public Producto(int idProducto, String descripcion, int año, int quincena, Plaza plaza, TipoNomina tipoNomina,
			TipoProducto tipoProducto, String nombreProducto)
	{
		super();
		this.idProducto = idProducto;
		this.descripcion = descripcion;
		this.año = año;
		this.quincena = quincena;
		this.plaza = plaza;
		this.tipoNomina = tipoNomina;
		this.tipoProducto = tipoProducto;
		this.nombreProducto = nombreProducto;
		this.conceptos = new ArrayList<>();
		this.conceptosDeduc = new ArrayList<>();

	}

	public Producto getClone()
	{
		Producto clon = new Producto(this.idProducto, this.descripcion, this.año, this.quincena, this.plaza,
				this.tipoNomina, this.tipoProducto, this.nombreProducto);
		clon.setPlantillaDAT(this.plantillaDAT);
		clon.setPlantillaTRA(this.plantillaTRA);

		return clon;
	}

	@Override
	public Object clone()
	{
		Producto clon = null;

		try
		{
			clon = (Producto) super.clone();

			if (this.plaza != null)
			{
				clon.setPlaza((Plaza) this.plaza.clone());
			}

			if (this.tipoNomina != null)
			{
				clon.setTipoNomina((TipoNomina) this.tipoNomina.clone());

			}

			if (this.tipoProducto != null)
			{
				clon.setTipoProducto((TipoProducto) this.tipoProducto.clone());

			}

			if (this.plantillaDAT != null)
			{
				clon.setPlantillaDAT((Plantilla) this.plantillaDAT.clone());

			}

			if (this.plantillaTRA != null)
			{
				clon.setPlantillaTRA((Plantilla) this.plantillaTRA.clone());
			}

			if (this.registrosDAT != null)
			{
				List<PlantillaRegistro> registrosDATClon = new ArrayList<>();

				this.registrosDAT.forEach(item ->
				{
					registrosDATClon.add((PlantillaRegistro) item.clone());

				});

				clon.setRegistrosDAT(registrosDATClon);

			}

			if (this.registrosTRA != null)
			{
				List<PlantillaRegistro> registrosTRAClon = new ArrayList<>();

				this.registrosTRA.forEach(item ->
				{
					registrosTRAClon.add((PlantillaRegistro) item.clone());
				});

				clon.setRegistrosTRA(registrosTRAClon);

			}

			if (this.registrosDATFilter != null)
			{
				List<PlantillaRegistro> registrosDATFilterClon = new ArrayList<>();

				this.registrosDATFilter.forEach(item ->
				{
					registrosDATFilterClon.add((PlantillaRegistro) item.clone());

				});

				clon.setRegistrosDATFilter(registrosDATFilterClon);

			}

			if (this.registrosTRAFilter != null)
			{
				List<PlantillaRegistro> registrosTRAFilterClon = new ArrayList<>();

				this.registrosTRAFilter.forEach(item ->
				{
					registrosTRAFilterClon.add((PlantillaRegistro) item.clone());
				});

				clon.setRegistrosTRAFilter(registrosTRAFilterClon);

			}

			if (this.registroDATSelec != null)
			{
				clon.setRegistroDATSelec((PlantillaRegistro) this.registroDATSelec.clone());
			}

			if (this.registroTRASelec != null)
			{
				clon.setRegistroTRASelec((PlantillaRegistro) this.registroTRASelec.clone());
			}

			if (this.bigPercepTotal != null)
			{
				clon.setBigPercepTotal(new BigDecimal(this.bigPercepTotal.toString()));
			}

			if (this.bigDeducTotal != null)
			{
				clon.setBigDeducTotal(new BigDecimal(this.bigDeducTotal.toString()));
			}

			if (this.bigRegPercep != null)
			{
				clon.setBigRegPercep(new BigDecimal(this.bigRegPercep.toString()));
			}

			if (this.bigRegDeduc != null)
			{
				clon.setBigRegDeduc(new BigDecimal(this.bigRegDeduc.toString()));
			}

			if (this.unidadResponsable != null)
			{
				List<UnidadProducto> unidadResponsableClon = new ArrayList<>();

				this.unidadResponsable.forEach(item ->
				{
					unidadResponsableClon.add((UnidadProducto) item.clone());

				});

				clon.setUnidadResponsable(unidadResponsableClon);
			}

			if (this.unidadesRegsClasificados != null)
			{
				List<UnidadRegistros> unidadesRegsClasificadosClon = new ArrayList<>();

				this.unidadesRegsClasificados.forEach(item ->
				{
					unidadesRegsClasificadosClon.add((UnidadRegistros) item.clone());

				});

				clon.setUnidadesRegsClasificados(unidadesRegsClasificadosClon);
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

			if (this.conceptosAsociadosAPlaza != null)
			{
				Map<String, Concepto> conceptosAsociadosAPlazaClon = new HashMap<>();

				this.conceptosAsociadosAPlaza.forEach((k, v) ->
				{
					conceptosAsociadosAPlazaClon.put(k, (Concepto) v.clone());

				});

				clon.setConceptosAsociadosAPlaza(conceptosAsociadosAPlazaClon);

			}

			if (this.conceptosNoAsociadosAPlaza != null)
			{
				Map<String, Concepto> conceptosNoAsociadosAPlazaClon = new HashMap<>();

				this.conceptosNoAsociadosAPlaza.forEach((k, v) ->
				{

					conceptosNoAsociadosAPlazaClon.put(k, (Concepto) v.clone());

				});

				clon.setConceptosNoAsociadosAPlaza(conceptosNoAsociadosAPlazaClon);

			}

		}
		catch (CloneNotSupportedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return clon;

	}

	// Actualiza los datos TRA de un registro DAT en específico
	public void updateTalonRegistroSeleccionado()
	{

		int indiceNumCheque = this.plantillaTRA.getPosicionValorPorDescripcionContains("cheque");

		List<PlantillaRegistro> registrosTRA = utilidades.getRegistrosDATTRA(this, 1, indiceNumCheque,
				this.registroDATSelec.getValorPorDescripcionContains("cheque"));

		PlantillaRegistro regTRA;

		int indiceTipoConcepto = this.plantillaTRA.getPosicionValorPorDescripcionContains("ipo de concep");
		int indiceConcepto = this.plantillaTRA.getPosicionValorPorDescripcion("Concepto");
		int indicePartida = this.plantillaDAT.getPosicionValorPorDescripcion("partida");
		int indicePartidaAntecedente = this.plantillaTRA.getPosicionValorPorDescripcionContains("partida de ante");
		int indiceImporte = this.plantillaTRA.getPosicionValorPorDescripcionContains("Importe");

		this.registroDATSelec.incilizaObjsConceptos();

		for (int x = 0; x < registrosTRA.size(); x++)
		{
			regTRA = registrosTRA.get(x);

			Concepto conceptoAñadiendo = new Concepto(Integer.parseInt(regTRA.getValorEnCampo(indiceTipoConcepto)),
					regTRA.getValorEnCampo(indiceConcepto), "", this.registroDATSelec.getValorEnCampo(indicePartida),
					regTRA.getValorEnCampo(indicePartidaAntecedente),
					new BigDecimal(regTRA.getValorEnCampo(indiceImporte).trim()));

			this.registroDATSelec.addConcepto(conceptoAñadiendo);

			registrosTRA.remove(x);
			x--;

		}

		int a = 12;
		a = 45;

	}

	// Reinicia en vacío los atributos de la clase de producto
	public void reiniciaProducto()
	{
		this.registrosDAT = null;
		this.registrosTRA = null;

		// Auxiliares para visualización en tablas
		this.registrosDATFilter = null;
		this.registrosTRAFilter = null;

		this.registroDATSelec = null;
		this.registroTRASelec = null;

		this.bigPercepTotal = null;
		this.bigDeducTotal = null;
		this.bigRegPercep = null;
		this.bigRegDeduc = null;
		this.totalPercepiones = null;
		this.totalDeducciones = null;
		this.totalLiquido = null;
		this.totalRegistros = 0;

		this.unidadResponsable = null;
		this.unidadesRegsClasificados = null;

		this.conceptos = new ArrayList<>();
		this.conceptosDeduc = new ArrayList<>();

		// Se utiliza para llevar el control para la validación de los conceptos
		// que
		// están o no asociados a plaza durante el análisis de la información
		this.conceptosAsociadosAPlaza = null;
		this.conceptosNoAsociadosAPlaza = null;
	}

	public void updatePlantillaDAT_TRA()
	{
		if (this.plantillaDAT == null || this.plantillaTRA == null)
		{
			utilidades.updateProductoPlantillaDATTRA(this);
		}
	}

	public void updateRegistrosDAT_TRA()
	{
		updateRegistrosDAT(true, true, true, true, false);
		updateRegistrosTRA();
	}

	// Solo actualiza los registros DAT del producto
	public void updateRegistrosDAT()
	{
		if (this.plantillaDAT == null)
		{
			updatePlantillaDAT_TRA();
		}

		this.registrosDAT = utilidades.getRegistrosDATTRA(this, 0);
	}

	// Solo actualiza los registros DAT del producto que no estén en el registro
	// general de empleados
	public void updateRegistrosDATSinEmpEnRegGeneral()
	{
		if (this.plantillaDAT == null)
		{
			updatePlantillaDAT_TRA();
		}

		this.registrosDAT = utilidades.getRegistrosDATTRA(this, 3);
	}

	public void getRegistrosDATFiltrados(boolean incluir416, boolean incluirU00, boolean incluir610)
	{
		updatePlantillaDAT_TRA();
		updateRegistrosDAT();

		String unidad;
		String funcionCompleta;

		int indiceUnidadResponsable = plantillaDAT.getPosicionValorPorDescripcionContains("Unidad R");
		int indiceUnidad = plantillaDAT.getPosicionValorPorDescripcionContains("Unidad Respo");
		int indiceProyecto = plantillaDAT.getPosicionValorPorDescripcionContains("Proyecto Proce");
		int indiceGrupoFuncional = plantillaDAT.getPosicionValorPorDescripcionContains("Grupo Funcion");
		int indiceFuncion = plantillaDAT.getPosicionValorPorDescripcion("Función");
		int indiceSubfuncion = plantillaDAT.getPosicionValorPorDescripcion("Subfunción");

		for (int x = 0; x < this.registrosDAT.size(); x++)
		{
			PlantillaRegistro regDAT = this.registrosDAT.get(x);

			unidad = utilidades.determinaUnidad(plaza, regDAT);

			if (unidad.toLowerCase().contains("416"))
			{
				if (!incluir416)
				{
					this.registrosDAT.remove(regDAT);
					x--;
					continue;
				}
			}
			if (unidad.toLowerCase().contains("U00"))
			{
				if (!incluirU00)
				{
					this.registrosDAT.remove(regDAT);
					x--;
					continue;
				}
			}
			else if (unidad.toLowerCase().contains("610"))
			{
				if (!incluir610)
				{
					this.registrosDAT.remove(regDAT);
					x--;
					continue;
				}
			}

		}

	}

	// Obtiene los DAT dentro del producto sin clasificarlos por unidad
	public void updateConceptosAsociadosPlaza(boolean incluir416, boolean incluirU00, boolean incluir610,
			boolean conceptosAsociadosAPlaza)
	{

		this.conceptosAsociadosAPlaza = new HashMap<>();
		this.conceptosNoAsociadosAPlaza = new HashMap<>();

		// Actualiza la estructura de las plantillas DAT y TRA
		updatePlantillaDAT_TRA();

		// Se obtienen todos los registros DAT y TRA del producto
		this.registrosDAT = utilidades.getRegistrosDATTRA(this, 0);
		this.registrosTRA = utilidades.getRegistrosDATTRA(this, 1);

		List<Concepto> catConceptosPercep = utilidades.getConceptos(1);
		List<Concepto> catConceptosDeduc = utilidades.getConceptos(2);

		// List<ConceptoAsociadoPlaza> catConceptosAsociados =
		// utilidades.getCatalogoConceptosAsociadosPlaza(2);

		int indiceUnidadResponsable = plantillaDAT.getPosicionValorPorDescripcionContains("Unidad R");

		this.registrosDAT = utilidades.getRegistrosDATTRA(this, 0);

		int indiceUnidad = plantillaDAT.getPosicionValorPorDescripcionContains("Unidad Respo");
		int indiceProyecto = plantillaDAT.getPosicionValorPorDescripcionContains("Proyecto Proce");
		int indiceGrupoFuncional = plantillaDAT.getPosicionValorPorDescripcionContains("Grupo Funcion");
		int indiceFuncion = plantillaDAT.getPosicionValorPorDescripcion("Función");
		int indiceSubfuncion = plantillaDAT.getPosicionValorPorDescripcion("Subfunción");
		int indiceNumeroCheque = this.plantillaTRA.getPosicionValorPorDescripcionContains("mero de Cheque");
		int indiceNumeroChequeDAT = this.plantillaDAT.getPosicionValorPorDescripcionContains("mero de Cheque");
		int indiceTipoConcepto = this.plantillaTRA.getPosicionValorPorDescripcionContains("ipo de concep");
		int indiceConcepto = this.plantillaTRA.getPosicionValorPorDescripcion("Concepto");
		int indicePartida = this.plantillaDAT.getPosicionValorPorDescripcion("partida");
		int indicePartidaAntecedente = this.plantillaTRA.getPosicionValorPorDescripcionContains("partida de ante");
		int indiceImporte = this.plantillaTRA.getPosicionValorPorDescripcionContains("Importe");

		PlantillaRegistro regTRA;
		String numeroChequeTRA = null;

		String funcionCompleta;

		String unidad;
		boolean chequeListo = false;

		String claveConcepto;
		Concepto conceptoAñadiendo;

		// Se recorre los DAT filtrando las unidades en caso de que aplique
		// Seguidamente se van acomodando los registros TRA y revisando los
		// conceptos que apliquen
		for (int x = 0; x < this.registrosDAT.size(); x++)
		{
			PlantillaRegistro regDAT = this.registrosDAT.get(x);

			unidad = utilidades.determinaUnidad(plaza, regDAT);

			if (unidad.toLowerCase().contains("416"))
			{
				if (!incluir416)
				{
					this.registrosDAT.remove(regDAT);
					x--;
					continue;
				}
			}
			if (unidad.toLowerCase().contains("U00"))
			{
				if (!incluirU00)
				{
					this.registrosDAT.remove(regDAT);
					x--;
					continue;
				}
			}
			else if (unidad.toLowerCase().contains("610"))
			{
				if (!incluir610)
				{
					this.registrosDAT.remove(regDAT);
					x--;
					continue;
				}
			}

			// Ahora filtrar los registros TRA para ir sacando los conceptos
			// asociados o no a la plaza
			chequeListo = false;

			for (int rt = 0; rt < this.registrosTRA.size(); rt++)
			{
				regTRA = this.registrosTRA.get(rt);

				numeroChequeTRA = regTRA.getValorEnCampo(indiceNumeroCheque);

				regDAT.getRegistrosVinculados().add(regTRA);

				if (!regDAT.getValorEnCampo(indiceNumeroChequeDAT).equals(numeroChequeTRA))
				{
					if (chequeListo)
					{
						break;
					}

					continue;
				}

				chequeListo = true;

				conceptoAñadiendo = new Concepto(Integer.parseInt(regTRA.getValorEnCampo(indiceTipoConcepto)),
						regTRA.getValorEnCampo(indiceConcepto), "", regDAT.getValorEnCampo(indicePartida),
						regTRA.getValorEnCampo(indicePartidaAntecedente),
						new BigDecimal(regTRA.getValorEnCampo(indiceImporte).trim()));

				claveConcepto = "" + conceptoAñadiendo.getTipoConcepto() + "-" + conceptoAñadiendo.getClave() + "-"
						+ conceptoAñadiendo.getPartidaAntecedente();

				boolean conceptoEncontrado = false;

				// Se busca dentro de los conceptos y se añade a la sumatoria
				// del importe total
				for (Concepto con : catConceptosPercep)
				{
					if (con.getClave().equals(conceptoAñadiendo.getClave())
							&& con.getPartidaAntecedente().equals(conceptoAñadiendo.getPartidaAntecedente()))
					{

						if (con.isAsociadoAPlaza() == conceptosAsociadosAPlaza)
						{

							if (!this.conceptosNoAsociadosAPlaza.containsKey(claveConcepto))
							{
								this.conceptosNoAsociadosAPlaza.put(claveConcepto, conceptoAñadiendo);

							}

							regDAT.addImporteAcumulado(conceptoAñadiendo.getValor());

							conceptoEncontrado = true;

							break;

						}

					}

				}

				if (!conceptoEncontrado)
				{

					for (Concepto con : catConceptosDeduc)
					{
						if (con.getClave().equals(conceptoAñadiendo.getClave())
								&& con.getPartidaAntecedente().equals(conceptoAñadiendo.getPartidaAntecedente()))
						{

							if (con.isAsociadoAPlaza() == conceptosAsociadosAPlaza)
							{

								if (!this.conceptosNoAsociadosAPlaza.containsKey(claveConcepto))
								{
									this.conceptosNoAsociadosAPlaza.put(claveConcepto, conceptoAñadiendo);

								}

								regDAT.addImporteAcumulado(conceptoAñadiendo.getValor());

								conceptoEncontrado = true;

								break;

							}

						}

					}

				}

				if (!conceptoEncontrado)
				{
					if (!this.conceptosAsociadosAPlaza.containsKey(claveConcepto))
					{
						this.conceptosAsociadosAPlaza.put(claveConcepto, conceptoAñadiendo);

					}

				}

				this.registrosTRA.remove(rt);
				rt--;

			}

		}

		this.plantillaDAT = null;
		this.plantillaTRA = null;

		System.out.println("Producto leído y clasificados los conceptos " + this.nombreProducto);

	}

	// Clasifica los trabajadores por año, unidad y saca los totales de ambas
	// cosas
	public void updateRegistrosDATSinClasificacion(boolean incluir416, boolean incluirU00, boolean incluir610)
	{
		updatePlantillaDAT_TRA();

		int indiceUnidadResponsable = plantillaDAT.getPosicionValorPorDescripcionContains("Unidad R");

		this.registrosDAT = utilidades.getRegistrosDATTRA(this, 0);
		this.unidadResponsable = new ArrayList<>();

		bigPercepTotal = new BigDecimal(0);
		bigDeducTotal = new BigDecimal(0);
		bigRegPercep = null;
		bigRegDeduc = null;

		int indicePercepciones = plantillaDAT.getPosicionValorPorDescripcionContains("Percep");
		int indiceDeducciones = plantillaDAT.getPosicionValorPorDescripcionContains("Deduc");
		int indicePeriodoInicial = plantillaDAT.getPosicionValorPorDescripcionContains("do de Pago Inicia");

		int indiceUnidad = plantillaDAT.getPosicionValorPorDescripcionContains("Unidad Respo");
		int indiceProyecto = plantillaDAT.getPosicionValorPorDescripcionContains("Proyecto Proce");
		int indiceGrupoFuncional = plantillaDAT.getPosicionValorPorDescripcionContains("Grupo Funcion");
		int indiceFuncion = plantillaDAT.getPosicionValorPorDescripcion("Función");
		int indiceSubfuncion = plantillaDAT.getPosicionValorPorDescripcion("Subfunción");

		String funcionCompleta;

		String unidad = null;
		boolean asignado = false;
		UnidadProducto upRegistro = null;
		String rPercepciones = null;
		String rDeducciones = null;
		int año;

		for (int x = 0; x < this.registrosDAT.size(); x++)
		{
			PlantillaRegistro reg = this.registrosDAT.get(x);

			unidad = utilidades.determinaUnidad(plaza, reg);

			if (unidad.toLowerCase().contains("416"))
			{
				if (!incluir416)
				{
					this.registrosDAT.remove(reg);
					x--;
					continue;
				}
			}
			if (unidad.toLowerCase().contains("U00"))
			{
				if (!incluirU00)
				{
					this.registrosDAT.remove(reg);
					x--;
					continue;
				}
			}
			else if (unidad.toLowerCase().contains("610"))
			{
				if (!incluir610)
				{
					this.registrosDAT.remove(reg);
					x--;
					continue;
				}
			}

			rPercepciones = reg.getValorEnCampo(indicePercepciones);
			rDeducciones = reg.getValorEnCampo(indiceDeducciones);

			// Optimizar con solo índice en vez de buscar cada vez
			bigRegPercep = new BigDecimal(rPercepciones);
			bigRegDeduc = new BigDecimal(rDeducciones);

			bigPercepTotal = bigPercepTotal.add(bigRegPercep);
			bigDeducTotal = bigDeducTotal.add(bigRegDeduc);

		}

		this.totalPercepiones = utilidades.formato.format(bigPercepTotal);
		this.totalDeducciones = utilidades.formato.format(bigDeducTotal);
		this.totalLiquido = utilidades.formato.format(bigPercepTotal.subtract(bigDeducTotal));

		this.totalRegistros = this.registrosDAT.size();

	}

	public void updateRegistrosDAT(boolean incluir416, boolean incluirU00, boolean incluirU01, boolean incluir610,
			boolean conservarRegistrosDAT)
	{
		updatePlantillaDAT_TRA();

		int indiceUnidadResponsable = plantillaDAT.getPosicionValorPorDescripcionContains("Unidad R");

		this.registrosDAT = utilidades.getRegistrosDATTRA(this, 0);
		this.unidadResponsable = new ArrayList<>();

		bigPercepTotal = new BigDecimal(0);
		bigDeducTotal = new BigDecimal(0);
		bigRegPercep = null;
		bigRegDeduc = null;

		int indicePercepciones = plantillaDAT.getPosicionValorPorDescripcionContains("Percep");
		int indiceDeducciones = plantillaDAT.getPosicionValorPorDescripcionContains("Deduc");
		int indicePeriodoInicial = plantillaDAT.getPosicionValorPorDescripcionContains("do de Pago Inicia");

		int indiceUnidad = plantillaDAT.getPosicionValorPorDescripcionContains("Unidad Respo");
		int indiceProyecto = plantillaDAT.getPosicionValorPorDescripcionContains("Proyecto Proce");
		int indiceGrupoFuncional = plantillaDAT.getPosicionValorPorDescripcionContains("Grupo Funcion");
		int indiceFuncion = plantillaDAT.getPosicionValorPorDescripcion("Función");
		int indiceSubfuncion = plantillaDAT.getPosicionValorPorDescripcion("Subfunción");

		String funcionCompleta;

		String unidad = null;
		boolean asignado = false;
		UnidadProducto upRegistro = null;
		String rPercepciones = null;
		String rDeducciones = null;
		int año;

		for (int x = 0; x < this.registrosDAT.size(); x++)
		{
			PlantillaRegistro reg = this.registrosDAT.get(x);

			unidad = utilidades.determinaUnidad(plaza, reg);

			if (unidad.toLowerCase().contains("416"))
			{
				if (!incluir416)
				{
					this.registrosDAT.remove(reg);
					x--;
					continue;
				}
			}
			if (unidad.toLowerCase().contains("u00"))
			{
				if (!incluirU00)
				{
					this.registrosDAT.remove(reg);
					x--;
					continue;
				}
			}
			if (unidad.toLowerCase().contains("u01"))
			{
				if (!incluirU01)
				{
					this.registrosDAT.remove(reg);
					x--;
					continue;
				}
			}
			if (unidad.toLowerCase().contains("610"))
			{
				if (!incluir610)
				{
					this.registrosDAT.remove(reg);
					x--;
					continue;
				}
			}

			rPercepciones = reg.getValorEnCampo(indicePercepciones);
			rDeducciones = reg.getValorEnCampo(indiceDeducciones);

			// Optimizar con solo índice en vez de buscar cada vez
			bigRegPercep = new BigDecimal(rPercepciones);
			bigRegDeduc = new BigDecimal(rDeducciones);

			bigPercepTotal = bigPercepTotal.add(bigRegPercep);
			bigDeducTotal = bigDeducTotal.add(bigRegDeduc);

			asignado = false;

			if (reg.getValorEnCampo(indicePeriodoInicial).trim().length() < 1)
			{
				año = 0;
			}
			else
			{
				año = Integer.parseInt(reg.getValorEnCampo(indicePeriodoInicial).substring(0, 4));
			}

			for (UnidadProducto up : this.unidadResponsable)
			{
				if (up.getDescripcion().equals(unidad))
				{
					up.getRegistrosDAT().add(reg);

					// ahora busca en que año está el registro
					for (AñoDetalle añoDet : up.getAños())
					{
						if (añoDet.getAño() == año)
						{
							añoDet.getRegistrosDAT().add(reg);
							asignado = true;
							break;
						}
					}

					if (!asignado)
					{
						// Creo el año del registro DAT
						AñoDetalle añoDeta = new AñoDetalle(año);
						añoDeta.getRegistrosDAT().add(reg);

						up.getAños().add(añoDeta);
					}

					asignado = true;
					upRegistro = up;

					break;
				}

			}

			if (!asignado)
			{
				upRegistro = new UnidadProducto(0, unidad);
				upRegistro.getRegistrosDAT().add(reg);
				this.unidadResponsable.add(upRegistro);

				// Creo el año del registro DAT
				upRegistro.getAños().add(new AñoDetalle(año));
				// Añado el registro DAT al año
				upRegistro.getAños().get(0).getRegistrosDAT().add(reg);

			}

			upRegistro.setTotalPercep(upRegistro.getTotalPercep().add(bigRegPercep));
			upRegistro.setTotalDeduc(upRegistro.getTotalDeduc().add(bigRegDeduc));

		}

		for (UnidadProducto ur : this.unidadResponsable)
		{
			ur.setTotalLiq(upRegistro.getTotalPercep().subtract(upRegistro.getTotalDeduc()));
			ur.setTotalPercepciones(utilidades.formato.format(ur.getTotalPercep()));
			ur.setTotalDeducciones(utilidades.formato.format(ur.getTotalDeduc()));
			ur.setTotalLiquido(utilidades.formato.format(ur.getTotalPercep().subtract(ur.getTotalDeduc())));
			ur.setTotalRegistros(ur.getRegistrosDAT().size());
		}

		this.totalPercepiones = utilidades.formato.format(bigPercepTotal);
		this.totalDeducciones = utilidades.formato.format(bigDeducTotal);
		this.totalLiquido = utilidades.formato.format(bigPercepTotal.subtract(bigDeducTotal));

		this.totalRegistros = this.registrosDAT.size();

		if (!conservarRegistrosDAT)
		{
			this.registrosDAT = null;
		}

	}

	public void updateRegistrosDATSinClasificarAño(boolean incluir416, boolean incluirU00, boolean incluirU01,
			boolean incluir610, boolean conservarRegistrosDAT)
	{
		updatePlantillaDAT_TRA();

		int indiceUnidadResponsable = plantillaDAT.getPosicionValorPorDescripcionContains("Unidad R");

		this.registrosDAT = utilidades.getRegistrosDATTRA(this, 0);
		this.unidadResponsable = new ArrayList<>();

		bigPercepTotal = new BigDecimal(0);
		bigDeducTotal = new BigDecimal(0);
		bigRegPercep = null;
		bigRegDeduc = null;

		int indicePercepciones = plantillaDAT.getPosicionValorPorDescripcionContains("Percep");
		int indiceDeducciones = plantillaDAT.getPosicionValorPorDescripcionContains("Deduc");

		String unidad = null;
		boolean asignado = false;
		UnidadRegistros upRegistro = null;
		String rPercepciones = null;
		String rDeducciones = null;

		this.unidadesRegsClasificados = new ArrayList<>();

		for (int x = 0; x < this.registrosDAT.size(); x++)
		{
			PlantillaRegistro reg = this.registrosDAT.get(x);

			unidad = utilidades.determinaUnidad(plaza, reg);

			if (!incluir416)
			{
				this.registrosDAT.remove(reg);
				x--;
				continue;
			}

			if (!incluir610)
			{
				this.registrosDAT.remove(reg);
				x--;
				continue;
			}

			if (!incluirU00)
			{
				this.registrosDAT.remove(reg);
				x--;
				continue;
			}

			if (!incluirU01)
			{
				this.registrosDAT.remove(reg);
				x--;
				continue;
			}

			rPercepciones = reg.getValorEnCampo(indicePercepciones);
			rDeducciones = reg.getValorEnCampo(indiceDeducciones);

			// Optimizar con solo índice en vez de buscar cada vez
			bigRegPercep = new BigDecimal(rPercepciones);
			bigRegDeduc = new BigDecimal(rDeducciones);

			bigPercepTotal = bigPercepTotal.add(bigRegPercep);
			bigDeducTotal = bigDeducTotal.add(bigRegDeduc);

			asignado = false;

			for (UnidadRegistros up : this.unidadesRegsClasificados)
			{
				if (up.getDescripcion().equals(unidad))
				{
					up.getRegistrosDAT().add(reg);

					asignado = true;
					upRegistro = up;

					break;
				}

			}

			if (!asignado)
			{
				upRegistro = new UnidadRegistros(this.plaza, 0, unidad);
				upRegistro.getRegistrosDAT().add(reg);
				this.unidadesRegsClasificados.add(upRegistro);

			}

		}

		this.totalPercepiones = utilidades.formato.format(bigPercepTotal);
		this.totalDeducciones = utilidades.formato.format(bigDeducTotal);
		this.totalLiquido = utilidades.formato.format(bigPercepTotal.subtract(bigDeducTotal));

		this.totalRegistros = this.registrosDAT.size();

		if (!conservarRegistrosDAT)
		{
			this.registrosDAT = null;
		}

	}

	// Obtiene los registros DAT y los clasifica por año y unidad pero no
	// obtiene totales, es solo para obtener la lista clasificada de
	// trabajadores
	public void updateRegistrosDATSinTotales()
	{
		updatePlantillaDAT_TRA();

		int indiceUnidadResponsable = plantillaDAT.getPosicionValorPorDescripcionContains("Unidad R");

		this.registrosDAT = utilidades.getRegistrosDATTRA(this, 0);
		this.unidadResponsable = new ArrayList<>();

		int indicePeriodoInicial = plantillaDAT.getPosicionValorPorDescripcionContains("do de Pago Inicia");

		String unidad = null;
		boolean asignado = false;
		UnidadProducto upRegistro = null;
		String rPercepciones = null;
		String rDeducciones = null;
		int año;

		for (PlantillaRegistro reg : this.registrosDAT)
		{

			unidad = reg.getValorEnCampo(indiceUnidadResponsable);
			asignado = false;
			año = Integer.parseInt(reg.getValorEnCampo(indicePeriodoInicial).substring(0, 4));

			for (UnidadProducto up : this.unidadResponsable)
			{
				if (up.getDescripcion().equals(unidad))
				{
					up.getRegistrosDAT().add(reg);

					// ahora busca en que año está el registro
					for (AñoDetalle añoDet : up.getAños())
					{
						if (añoDet.getAño() == año)
						{
							añoDet.getRegistrosDAT().add(reg);
							asignado = true;
							break;
						}
					}

					if (!asignado)
					{
						// Creo el año del registro DAT
						AñoDetalle añoDeta = new AñoDetalle(año);
						añoDeta.getRegistrosDAT().add(reg);

						up.getAños().add(añoDeta);
					}

					asignado = true;
					upRegistro = up;

					break;
				}

			}

			if (!asignado)
			{
				upRegistro = new UnidadProducto(0, unidad);
				upRegistro.getRegistrosDAT().add(reg);
				this.unidadResponsable.add(upRegistro);

				// Creo el año del registro DAT
				upRegistro.getAños().add(new AñoDetalle(año));
				// Añado el registro DAT al año
				upRegistro.getAños().get(0).getRegistrosDAT().add(reg);

			}

		}

		this.totalRegistros = this.registrosDAT.size();

		this.registrosDAT = null;
	}

	// Realiza la acumulación global de conceptos de cada registro DAT
	public void updateRegistrosTRA()
	{
		updatePlantillaDAT_TRA();

		updateRegistrosDAT(true, true, true, true, false);

		/*
		 * utilidades.getTRAenBaseDAT(this);
		 * 
		 * if (1 == 1) { return; }
		 */

		this.registrosTRA = utilidades.getRegistrosDATTRA(this, 1);
		this.conceptos = new ArrayList<>();
		this.conceptosDeduc = new ArrayList<>();

		String numeroChequeTRA = null;
		boolean añadidoConcepto = false;

		int indiceNumeroCheque = this.plantillaTRA.getPosicionValorPorDescripcionContains("mero de Cheque");
		int indiceNumeroChequeDAT = this.plantillaDAT.getPosicionValorPorDescripcionContains("mero de Cheque");
		int indiceTipoConcepto = this.plantillaTRA.getPosicionValorPorDescripcionContains("ipo de concep");
		int indiceConcepto = this.plantillaTRA.getPosicionValorPorDescripcion("Concepto");
		int indicePartida = this.plantillaDAT.getPosicionValorPorDescripcion("partida");
		int indicePartidaAntecedente = this.plantillaTRA.getPosicionValorPorDescripcionContains("partida de ante");
		int indiceImporte = this.plantillaTRA.getPosicionValorPorDescripcionContains("Importe");

		// Se verifica cada registro del TRA a qué registro del DAT pertenece y
		// lo enlaza con ello

		PlantillaRegistro regTRA;
		// nos indica si cada cheque(registro del DAT) ya cuenta con sus
		// registros correspondientes TRA para romper el ciclo
		// Esto en el entendido de que cada archivo TRA viene ordenado por
		// número de cheque
		boolean chequeListo = false;

		Concepto conceptoAñadiendo;

		for (UnidadProducto unidadP : this.unidadResponsable)
		{

			for (AñoDetalle añoDet : unidadP.getAños())
			{

				for (PlantillaRegistro regDAT : añoDet.getRegistrosDAT())
				{
					chequeListo = false;

					for (int x = 0; x < this.registrosTRA.size(); x++)
					{
						regTRA = this.registrosTRA.get(x);

						numeroChequeTRA = regTRA.getValorEnCampo(indiceNumeroCheque);

						regDAT.getRegistrosVinculados().add(regTRA);

						if (!regDAT.getValorEnCampo(indiceNumeroChequeDAT).equals(numeroChequeTRA))
						{
							if (chequeListo)
							{
								break;
							}

							continue;
						}

						chequeListo = true;

						conceptoAñadiendo = new Concepto(Integer.parseInt(regTRA.getValorEnCampo(indiceTipoConcepto)),
								regTRA.getValorEnCampo(indiceConcepto), "", regDAT.getValorEnCampo(indicePartida),
								regTRA.getValorEnCampo(indicePartidaAntecedente),
								new BigDecimal(regTRA.getValorEnCampo(indiceImporte).trim()));

						// System.out.println(regDAT.getValorPorDescripcionContains("RFC")+"
						// - "+conceptoAñadiendo.getTipoConcepto()+"
						// "+conceptoAñadiendo.getClave()+"
						// "+conceptoAñadiendo.getPartidaAntecedente()+ "
						// "+conceptoAñadiendo.getValorString() );

						// Se cambia el concepto 201 SE al 1SE 00
						if (conceptoAñadiendo.getTipoConcepto() == 2 && conceptoAñadiendo.getClave().equals("01")
								&& conceptoAñadiendo.getPartidaAntecedente().equals("SE"))
						{
							regTRA.getCampo(indiceTipoConcepto).setValor("1");
							conceptoAñadiendo.setTipoConcepto(1);
							regTRA.getCampo(indiceConcepto).setValor("SE");
							conceptoAñadiendo.setClave("SE");
							regTRA.getCampo(indicePartidaAntecedente).setValor("00");
							conceptoAñadiendo.setPartidaAntecedente("00");
							regTRA.getCampo(indiceImporte).setValor(
									conceptoAñadiendo.getValor().multiply(new BigDecimal("-1")).toPlainString());
							conceptoAñadiendo.setValor(conceptoAñadiendo.getValor().multiply(new BigDecimal("-1")));

						}

						if (!regDAT.getConceptosAcum().containsKey("" + conceptoAñadiendo.getTipoConcepto()
								+ conceptoAñadiendo.getClave() + " " + conceptoAñadiendo.getPartidaAntecedente()))
						{
							regDAT.getConceptosAcum().put("" + conceptoAñadiendo.getTipoConcepto()
									+ conceptoAñadiendo.getClave() + " " + conceptoAñadiendo.getPartidaAntecedente(),
									conceptoAñadiendo);
						}
						else
						{
							((Concepto) regDAT.getConceptosAcum()
									.get("" + conceptoAñadiendo.getTipoConcepto() + conceptoAñadiendo.getClave() + " "
											+ conceptoAñadiendo.getPartidaAntecedente()))
													.addValor(conceptoAñadiendo.getValor());
						}

						this.registrosTRA.remove(x);
						x--;

					}

				}
			}

		}

	}

	public void filtraConceptosIndividualesRepv(List<Concepto> conceptos)
	{
		updateRegistrosDATSinClasificarAño(true, true, true, true, false);
		filtraEnListConceptosIndividualesParaUnidadesNoClasificadasPorAño(conceptos);

	}

	// Realiza la acumulación individual de conceptos de cada registro DAT, la
	// acumulación va dentro de hashmap
	public void filtraConceptosIndividualesParaUnidadesNoClasificadasPorAño(List<Concepto> conceptos)
	{
		updatePlantillaDAT_TRA();

		updateRegistrosDAT(true, true, true, true, false);

		/*
		 * utilidades.getTRAenBaseDAT(this);
		 * 
		 * if (1 == 1) { return; }
		 */

		this.registrosTRA = utilidades.getRegistrosDATTRA(this, 1);
		this.conceptos = new ArrayList<>();
		this.conceptosDeduc = new ArrayList<>();

		String numeroChequeTRA = null;
		boolean añadidoConcepto = false;

		int indiceNumeroCheque = this.plantillaTRA.getPosicionValorPorDescripcionContains("mero de Cheque");
		int indiceNumeroChequeDAT = this.plantillaDAT.getPosicionValorPorDescripcionContains("mero de Cheque");
		int indiceTipoConcepto = this.plantillaTRA.getPosicionValorPorDescripcionContains("ipo de concep");
		int indiceConcepto = this.plantillaTRA.getPosicionValorPorDescripcion("Concepto");
		int indicePartida = this.plantillaDAT.getPosicionValorPorDescripcion("partida");
		int indicePartidaAntecedente = this.plantillaTRA.getPosicionValorPorDescripcionContains("partida de ante");
		int indiceImporte = this.plantillaTRA.getPosicionValorPorDescripcionContains("Importe");

		// Se verifica cada registro del TRA a qué registro del DAT pertenece y
		// lo enlaza con ello

		PlantillaRegistro regTRA;
		// nos indica si cada cheque(registro del DAT) ya cuenta con sus
		// registros correspondientes TRA para romper el ciclo
		// Esto en el entendido de que cada archivo TRA viene ordenado por
		// número de cheque
		boolean chequeListo = false;

		Concepto conceptoAñadiendo;

		for (UnidadRegistros unidadP : this.unidadesRegsClasificados)
		{

			for (int reg = 0; reg < unidadP.getRegistrosDAT().size(); reg++)
			{

				PlantillaRegistro regDAT = unidadP.getRegistrosDAT().get(reg);

				chequeListo = false;

				for (int x = 0; x < this.registrosTRA.size(); x++)
				{
					regTRA = this.registrosTRA.get(x);

					numeroChequeTRA = regTRA.getValorEnCampo(indiceNumeroCheque);

					regDAT.getRegistrosVinculados().add(regTRA);

					if (!regDAT.getValorEnCampo(indiceNumeroChequeDAT).equals(numeroChequeTRA))
					{
						if (chequeListo)
						{
							break;
						}

						continue;
					}

					chequeListo = true;

					conceptoAñadiendo = new Concepto(Integer.parseInt(regTRA.getValorEnCampo(indiceTipoConcepto)),
							regTRA.getValorEnCampo(indiceConcepto).trim(), "",
							regDAT.getValorEnCampo(indicePartida).trim(),
							regTRA.getValorEnCampo(indicePartidaAntecedente),
							new BigDecimal(regTRA.getValorEnCampo(indiceImporte).trim()));

					for (Concepto con : conceptos)
					{

						if (conceptoAñadiendo.getTipoConcepto() == con.getTipoConcepto()
								&& conceptoAñadiendo.getClave().toLowerCase().equalsIgnoreCase(con.getClave())
								&& conceptoAñadiendo.getPartidaAntecedente().toLowerCase()
										.equalsIgnoreCase(con.getPartidaAntecedente()))
						{
							regDAT.getConceptosAcum().put("" + conceptoAñadiendo.getTipoConcepto()
									+ conceptoAñadiendo.getClave() + " " + conceptoAñadiendo.getPartidaAntecedente(),
									conceptoAñadiendo);

							break;

						}

					}

					this.registrosTRA.remove(x);
					x--;

				}

				if (regDAT.getConceptosAcum().isEmpty())
				{
					unidadP.getRegistrosDAT().remove(reg);
					reg--;
				}

			}

		}

	}

	// Realiza la acumulación individual de conceptos de cada registro DAT, la
	// acumulación va dentro de lista
	public void filtraEnListConceptosIndividualesParaUnidadesNoClasificadasPorAño(List<Concepto> conceptos)
	{
		updatePlantillaDAT_TRA();

		updateRegistrosDAT(true, true, true, true, false);

		/*
		 * utilidades.getTRAenBaseDAT(this);
		 * 
		 * if (1 == 1) { return; }
		 */

		this.registrosTRA = utilidades.getRegistrosDATTRA(this, 1);
		this.conceptos = new ArrayList<>();
		this.conceptosDeduc = new ArrayList<>();

		String numeroChequeTRA = null;
		boolean añadidoConcepto = false;

		int indiceNumeroCheque = this.plantillaTRA.getPosicionValorPorDescripcionContains("mero de Cheque");
		int indiceNumeroChequeDAT = this.plantillaDAT.getPosicionValorPorDescripcionContains("mero de Cheque");
		int indiceTipoConcepto = this.plantillaTRA.getPosicionValorPorDescripcionContains("ipo de concep");
		int indiceConcepto = this.plantillaTRA.getPosicionValorPorDescripcion("Concepto");
		int indicePartida = this.plantillaDAT.getPosicionValorPorDescripcion("partida");
		int indicePartidaAntecedente = this.plantillaTRA.getPosicionValorPorDescripcionContains("partida de ante");
		int indiceImporte = this.plantillaTRA.getPosicionValorPorDescripcionContains("Importe");

		// Se verifica cada registro del TRA a qué registro del DAT pertenece y
		// lo enlaza con ello

		PlantillaRegistro regTRA;
		// nos indica si cada cheque(registro del DAT) ya cuenta con sus
		// registros correspondientes TRA para romper el ciclo
		// Esto en el entendido de que cada archivo TRA viene ordenado por
		// número de cheque
		boolean chequeListo = false;

		Concepto conceptoAñadiendo;

		for (UnidadRegistros unidadP : this.unidadesRegsClasificados)
		{

			for (int reg = 0; reg < unidadP.getRegistrosDAT().size(); reg++)
			{

				PlantillaRegistro regDAT = unidadP.getRegistrosDAT().get(reg);
				regDAT.setListConceptosAcum(new ArrayList<>());

				chequeListo = false;

				for (int x = 0; x < this.registrosTRA.size(); x++)
				{
					regTRA = this.registrosTRA.get(x);

					numeroChequeTRA = regTRA.getValorEnCampo(indiceNumeroCheque);

					regDAT.getRegistrosVinculados().add(regTRA);

					if (!regDAT.getValorEnCampo(indiceNumeroChequeDAT).equals(numeroChequeTRA))
					{
						if (chequeListo)
						{
							break;
						}

						continue;
					}

					chequeListo = true;

					conceptoAñadiendo = new Concepto(Integer.parseInt(regTRA.getValorEnCampo(indiceTipoConcepto)),
							regTRA.getValorEnCampo(indiceConcepto).trim(), "",
							regDAT.getValorEnCampo(indicePartida).trim(),
							regTRA.getValorEnCampo(indicePartidaAntecedente),
							new BigDecimal(regTRA.getValorEnCampo(indiceImporte).trim()));

					for (Concepto con : conceptos)
					{

						if (conceptoAñadiendo.getTipoConcepto() == con.getTipoConcepto()
								&& conceptoAñadiendo.getClave().toLowerCase().equalsIgnoreCase(con.getClave())
								&& conceptoAñadiendo.getPartidaAntecedente().toLowerCase()
										.equalsIgnoreCase(con.getPartidaAntecedente()))
						{

							regDAT.getListConceptosAcum().add(conceptoAñadiendo);
							break;

						}

					}

					this.registrosTRA.remove(x);
					x--;

				}

				if (regDAT.getListConceptosAcum().isEmpty())
				{
					unidadP.getRegistrosDAT().remove(reg);
					reg--;
				}

			}

		}

	}

	// Desglosa los conceptos y los divide en percepciones y deducciones,
	// separando los conceptos por tipo de concepto, descripción y partida de
	// antecedente.
	// Se puede utilizar el método acumulando los conceptos o añadiendo un
	// registro por cada concepto, los conceptos acumulados son dentro del
	// producto
	public void updateRegistrosTRAConConceptos(boolean incluir416, boolean incluirU00, boolean incluirU01,
			boolean incluir610, boolean acumularConceptos, boolean conservarRegistrosDAT)
	{
		updatePlantillaDAT_TRA();

		updateRegistrosDAT(incluir416, incluirU00, incluirU01, incluir610, conservarRegistrosDAT);

		this.registrosTRA = utilidades.getRegistrosDATTRA(this, 1);
		this.conceptos = new ArrayList<>();
		this.conceptosDeduc = new ArrayList<>();

		String numeroChequeTRA = null;
		boolean añadidoConcepto = false;

		int indiceNumeroCheque = this.plantillaTRA.getPosicionValorPorDescripcionContains("mero de Cheque");
		int indiceNumeroChequeDAT = this.plantillaDAT.getPosicionValorPorDescripcionContains("mero de Cheque");
		int indiceTipoConcepto = this.plantillaTRA.getPosicionValorPorDescripcionContains("ipo de concep");
		int indiceConcepto = this.plantillaTRA.getPosicionValorPorDescripcion("Concepto");
		int indicePartida = this.plantillaDAT.getPosicionValorPorDescripcion("partida");
		int indicePartidaAntecedente = this.plantillaTRA.getPosicionValorPorDescripcionContains("partida de ante");
		int indiceImporte = this.plantillaTRA.getPosicionValorPorDescripcionContains("Importe");

		// Se verifica cada registro del TRA a qué registro del DAT pertenece y
		// lo enlaza con ello

		PlantillaRegistro regTRA;
		// nos indica si cada cheque(registro del DAT) ya cuenta con sus
		// registros correspondientes TRA para romper el ciclo
		// Esto en el entendido de que cada archivo TRA viene ordenado por
		// número de cheque
		boolean chequeListo = false;

		for (UnidadProducto unidadP : this.unidadResponsable)
		{

			for (AñoDetalle añoDet : unidadP.getAños())
			{

				for (PlantillaRegistro regDAT : añoDet.getRegistrosDAT())
				{
					chequeListo = false;

					for (int x = 0; x < this.registrosTRA.size(); x++)
					{
						regTRA = this.registrosTRA.get(x);

						numeroChequeTRA = regTRA.getValorEnCampo(indiceNumeroCheque);

						if (!regDAT.getValorEnCampo(indiceNumeroChequeDAT).equals(numeroChequeTRA))
						{
							if (chequeListo)
							{
								break;
							}

							continue;
						}

						regDAT.getRegistrosVinculados().add(regTRA);

						chequeListo = true;

						Concepto conceptoAñadiendo = new Concepto(
								Integer.parseInt(regTRA.getValorEnCampo(indiceTipoConcepto)),
								regTRA.getValorEnCampo(indiceConcepto), "", regDAT.getValorEnCampo(indicePartida),
								regTRA.getValorEnCampo(indicePartidaAntecedente),
								new BigDecimal(regTRA.getValorEnCampo(indiceImporte).trim()));

						if (regDAT.getConceptosAcum()
								.get("" + conceptoAñadiendo.getTipoConcepto() + conceptoAñadiendo.getClave() + " "
										+ conceptoAñadiendo.getPartidaAntecedente()) == null)
						{
							regDAT.getConceptosAcum().put("" + conceptoAñadiendo.getTipoConcepto()
									+ conceptoAñadiendo.getClave() + " " + conceptoAñadiendo.getPartidaAntecedente(),
									conceptoAñadiendo);
						}
						else
						{
							((Concepto) regDAT.getConceptosAcum()
									.get("" + conceptoAñadiendo.getTipoConcepto() + conceptoAñadiendo.getClave() + " "
											+ conceptoAñadiendo.getPartidaAntecedente()))
													.addValor(conceptoAñadiendo.getValor());
						}

						// siguiene registro de TRA
						unidadP.addConcepto(conceptoAñadiendo);
						añoDet.addConcepto(conceptoAñadiendo);

						añadidoConcepto = false;

						switch (conceptoAñadiendo.getTipoConcepto())
						{
							case 1:
							case 3:
							case 5:

								if (conceptoAñadiendo.getPartidaAntecedente().trim().equalsIgnoreCase("PD")
										&& conceptoAñadiendo.getClave().trim().equalsIgnoreCase("32"))
								{
									int a = 1;
									a = 34;
								}

								// se recorre toda la lista de conceptos para
								// saber
								// si ya se encuentra dentro, si esta se suma,
								// si
								// no, se crea
								if (acumularConceptos)
								{
									for (Concepto con : this.conceptos)
									{
										if (con.getTipoConcepto() == conceptoAñadiendo.getTipoConcepto()
												&& con.getClave().equalsIgnoreCase(conceptoAñadiendo.getClave())
												&& con.getPartidaAntecedente().trim().equalsIgnoreCase(
														conceptoAñadiendo.getPartidaAntecedente().trim()))
										{
											añadidoConcepto = true;
											// se suma la cantidad del valor del
											// concepto

											if (conceptoAñadiendo.getTotalCasos() > 1)
											{
												con.setValor(con.getValor().add(conceptoAñadiendo.getValor()));
												con.setTotalCasos(
														con.getTotalCasos() + conceptoAñadiendo.getTotalCasos());
											}
											else
											{
												con.addValor(conceptoAñadiendo.getValor());
											}
											break;
										}
									}
								}

								if (!añadidoConcepto)
								{
									this.conceptos.add(conceptoAñadiendo.getClone());
								}

							break;

							case 2:
							case 4:
							case 6:

								// se recorre toda la lista de conceptos para
								// saber
								// si ya se encuentra dentro, si esta se suma,
								// si
								// no, se crea
								if (acumularConceptos)
								{
									for (Concepto con : this.conceptosDeduc)
									{
										if (con.getTipoConcepto() == conceptoAñadiendo.getTipoConcepto()
												&& con.getClave().equalsIgnoreCase(conceptoAñadiendo.getClave())
												&& con.getPartidaAntecedente().trim().equalsIgnoreCase(
														conceptoAñadiendo.getPartidaAntecedente().trim()))
										{
											añadidoConcepto = true;
											// se suma la cantidad del valor del
											// concepto

											if (conceptoAñadiendo.getTotalCasos() > 1)
											{
												con.setValor(con.getValor().add(conceptoAñadiendo.getValor()));
												con.setTotalCasos(
														con.getTotalCasos() + conceptoAñadiendo.getTotalCasos());
											}
											else
											{
												con.addValor(conceptoAñadiendo.getValor());

											}

											break;
										}
									}
								}

								if (!añadidoConcepto)
								{
									this.conceptosDeduc.add(conceptoAñadiendo.getClone());
								}

							break;

						}

						this.registrosTRA.remove(x);
						x--;

					}

				}
			}

		}

	}

	// Desglosa los conceptos y los divide en percepciones y deducciones,
	// separando los conceptos por tipo de concepto, descripción y partida de
	// antecedente.
	// Se puede utilizar el método acumulando los conceptos o añadiendo un
	// registro por cada concepto, los conceptos acumulados son dentro del
	// registro DAT
	public void updateRegistrosTRAConConceptosEnDAT(boolean incluir416, boolean incluirU00, boolean incluir610,
			boolean acumularConceptos, boolean conservarRegistrosDAT)
	{
		updatePlantillaDAT_TRA();

		// FALTA REFACTORIZAR PARA U01
		updateRegistrosDAT(incluir416, incluirU00, true, incluir610, conservarRegistrosDAT);

		this.registrosTRA = utilidades.getRegistrosDATTRA(this, 1);
		this.conceptos = new ArrayList<>();
		this.conceptosDeduc = new ArrayList<>();

		String numeroChequeTRA = null;
		boolean añadidoConcepto = false;

		int indiceNumeroCheque = this.plantillaTRA.getPosicionValorPorDescripcionContains("mero de Cheque");
		int indiceNumeroChequeDAT = this.plantillaDAT.getPosicionValorPorDescripcionContains("mero de Cheque");
		int indiceTipoConcepto = this.plantillaTRA.getPosicionValorPorDescripcionContains("ipo de concep");
		int indiceConcepto = this.plantillaTRA.getPosicionValorPorDescripcion("Concepto");
		int indicePartida = this.plantillaDAT.getPosicionValorPorDescripcion("partida");
		int indicePartidaAntecedente = this.plantillaTRA.getPosicionValorPorDescripcionContains("partida de ante");
		int indiceImporte = this.plantillaTRA.getPosicionValorPorDescripcionContains("Importe");

		// Se verifica cada registro del TRA a qué registro del DAT pertenece y
		// lo enlaza con ello

		PlantillaRegistro regTRA;
		// nos indica si cada cheque(registro del DAT) ya cuenta con sus
		// registros correspondientes TRA para romper el ciclo
		// Esto en el entendido de que cada archivo TRA viene ordenado por
		// número de cheque
		boolean chequeListo = false;

		for (UnidadProducto unidadP : this.unidadResponsable)
		{

			for (AñoDetalle añoDet : unidadP.getAños())
			{

				for (PlantillaRegistro regDAT : añoDet.getRegistrosDAT())
				{
					chequeListo = false;

					for (int x = 0; x < this.registrosTRA.size(); x++)
					{
						regTRA = this.registrosTRA.get(x);

						numeroChequeTRA = regTRA.getValorEnCampo(indiceNumeroCheque);

						if (!regDAT.getValorEnCampo(indiceNumeroChequeDAT).equals(numeroChequeTRA))
						{
							if (chequeListo)
							{
								break;
							}

							continue;
						}

						regDAT.getRegistrosVinculados().add(regTRA);

						chequeListo = true;

						Concepto conceptoAñadiendo = new Concepto(
								Integer.parseInt(regTRA.getValorEnCampo(indiceTipoConcepto)),
								regTRA.getValorEnCampo(indiceConcepto), "", regDAT.getValorEnCampo(indicePartida),
								regTRA.getValorEnCampo(indicePartidaAntecedente),
								new BigDecimal(regTRA.getValorEnCampo(indiceImporte).trim()));

						regDAT.addConcepto(conceptoAñadiendo);

						this.registrosTRA.remove(x);
						x--;

					}

				}
			}

		}

	}

	// Realiza la actualización de cada registro DAT con sus registros TRA
	// filtrado por unidades
	public void updateRegistrosDATTRA(boolean incluir416, boolean incluirU00, boolean incluir610)
	{
		updateRegistrosDATSinClasificacion(incluir416, incluirU00, incluir610);

		this.registrosTRA = utilidades.getRegistrosDATTRA(this, 1);

		String numeroChequeTRA = null;

		int indiceNumeroCheque = this.plantillaTRA.getPosicionValorPorDescripcionContains("mero de Cheque");
		int indiceNumeroChequeDAT = this.plantillaDAT.getPosicionValorPorDescripcionContains("mero de Cheque");

		// Se verifica cada registro del TRA a qué registro del DAT pertenece y
		// lo enlaza con ello

		PlantillaRegistro regTRA;
		// nos indica si cada cheque(registro del DAT) ya cuenta con sus
		// registros correspondientes TRA para romper el ciclo
		// Esto en el entendido de que cada archivo TRA viene ordenado por
		// número de cheque
		boolean chequeListo = false;

		for (PlantillaRegistro regDAT : this.registrosDAT)
		{
			chequeListo = false;

			for (int x = 0; x < this.registrosTRA.size(); x++)
			{
				regTRA = this.registrosTRA.get(x);

				numeroChequeTRA = regTRA.getValorEnCampo(indiceNumeroCheque);

				if (!regDAT.getValorEnCampo(indiceNumeroChequeDAT).equals(numeroChequeTRA))
				{
					if (chequeListo)
					{
						break;
					}

					continue;
				}

				regDAT.getRegistrosVinculados().add(regTRA);

				chequeListo = true;

				this.registrosTRA.remove(x);
				x--;

			}

		}

		System.out.println("Número de DAT " + this.registrosDAT.size() + " y TRA " + this.registrosTRA.size());

	}

	// Asigna los tra a sus dat
	public void enlazaTRAconDAT()
	{

		this.registrosTRA = utilidades.getRegistrosDATTRA(this, 1);

		String numeroChequeTRA = null;

		int indiceNumeroCheque = this.plantillaTRA.getPosicionValorPorDescripcionContains("mero de Cheque");
		int indiceNumeroChequeDAT = this.plantillaDAT.getPosicionValorPorDescripcionContains("mero de Cheque");

		// Se verifica cada registro del TRA a qué registro del DAT pertenece y
		// lo enlaza con ello

		PlantillaRegistro regTRA;
		// nos indica si cada cheque(registro del DAT) ya cuenta con sus
		// registros correspondientes TRA para romper el ciclo
		// Esto en el entendido de que cada archivo TRA viene ordenado por
		// número de cheque
		boolean chequeListo = false;

		for (PlantillaRegistro regDAT : this.registrosDAT)
		{
			chequeListo = false;

			for (int x = 0; x < this.registrosTRA.size(); x++)
			{
				regTRA = this.registrosTRA.get(x);

				numeroChequeTRA = regTRA.getValorEnCampo(indiceNumeroCheque);

				if (!regDAT.getValorEnCampo(indiceNumeroChequeDAT).equals(numeroChequeTRA))
				{
					if (chequeListo)
					{
						break;
					}

					continue;
				}

				regDAT.getRegistrosVinculados().add(regTRA);

				chequeListo = true;

				this.registrosTRA.remove(x);
				x--;

			}

		}

	}

	////////////////////////////////////
	// Agrupa de forma básica los registros por año y por unidades, puede calcular o
	// no los importes de percepciones, deducciones y neto
	public void updateRegistrosDAT(List<String> unidadesAIncluir, boolean conservarRegistrosDAT,
			boolean calcularImportesProducto)
	{
		updatePlantillaDAT_TRA();

		this.registrosDAT = utilidades.getRegistrosDATTRA(this, 0);
		this.unidadResponsable = new ArrayList<>();

		bigPercepTotal = new BigDecimal(0);
		bigDeducTotal = new BigDecimal(0);
		bigRegPercep = null;
		bigRegDeduc = null;

		int indicePercepciones = plantillaDAT.getPosicionValorPorDescripcionContains("Percep");
		int indiceDeducciones = plantillaDAT.getPosicionValorPorDescripcionContains("Deduc");
		int indicePeriodoInicial = plantillaDAT.getPosicionValorPorDescripcionContains("do de Pago Inicia");

		String unidad = null;
		boolean asignado = false;
		UnidadProducto upRegistro = null;
		String rPercepciones = null;
		String rDeducciones = null;
		int año;

		String uniValid = unidadesAIncluir.stream().map(String::toUpperCase).collect(Collectors.joining("|"));

		for (int x = 0; x < this.registrosDAT.size(); x++)
		{
			PlantillaRegistro reg = this.registrosDAT.get(x);

			unidad = utilidades.determinaUnidad(plaza, reg);

			if (!uniValid.contains(unidad))
			{
				this.registrosDAT.remove(reg);
				x--;
				continue;
			}

			if (calcularImportesProducto)
			{
				rPercepciones = reg.getValorEnCampo(indicePercepciones);
				rDeducciones = reg.getValorEnCampo(indiceDeducciones);

				// Optimizar con solo índice en vez de buscar cada vez
				bigRegPercep = new BigDecimal(rPercepciones);
				bigRegDeduc = new BigDecimal(rDeducciones);

				bigPercepTotal = bigPercepTotal.add(bigRegPercep);
				bigDeducTotal = bigDeducTotal.add(bigRegDeduc);

			}

			asignado = false;

			if (reg.getValorEnCampo(indicePeriodoInicial).trim().length() < 1)
			{
				año = 0;
			}
			else
			{
				año = Integer.parseInt(reg.getValorEnCampo(indicePeriodoInicial).substring(0, 4));
			}

			for (UnidadProducto up : this.unidadResponsable)
			{
				if (up.getDescripcion().equals(unidad))
				{
					up.getRegistrosDAT().add(reg);

					// ahora busca en que año está el registro
					for (AñoDetalle añoDet : up.getAños())
					{
						if (añoDet.getAño() == año)
						{
							añoDet.getRegistrosDAT().add(reg);
							asignado = true;
							break;
						}
					}

					if (!asignado)
					{
						// Creo el año del registro DAT
						AñoDetalle añoDeta = new AñoDetalle(año);
						añoDeta.getRegistrosDAT().add(reg);

						up.getAños().add(añoDeta);
					}

					asignado = true;
					upRegistro = up;

					break;
				}

			}

			if (!asignado)
			{
				upRegistro = new UnidadProducto(0, unidad);
				upRegistro.getRegistrosDAT().add(reg);
				this.unidadResponsable.add(upRegistro);

				// Creo el año del registro DAT
				upRegistro.getAños().add(new AñoDetalle(año));
				// Añado el registro DAT al año
				upRegistro.getAños().get(0).getRegistrosDAT().add(reg);

			}

			if (calcularImportesProducto)
			{
				upRegistro.setTotalPercep(upRegistro.getTotalPercep().add(bigRegPercep));
				upRegistro.setTotalDeduc(upRegistro.getTotalDeduc().add(bigRegDeduc));
			}

		}

		if (calcularImportesProducto)
		{
			for (UnidadProducto ur : this.unidadResponsable)
			{
				ur.setTotalLiq(upRegistro.getTotalPercep().subtract(upRegistro.getTotalDeduc()));
				ur.setTotalPercepciones(utilidades.formato.format(ur.getTotalPercep()));
				ur.setTotalDeducciones(utilidades.formato.format(ur.getTotalDeduc()));
				ur.setTotalLiquido(utilidades.formato.format(ur.getTotalPercep().subtract(ur.getTotalDeduc())));
				ur.setTotalRegistros(ur.getRegistrosDAT().size());
			}

			this.totalPercepiones = utilidades.formato.format(bigPercepTotal);
			this.totalDeducciones = utilidades.formato.format(bigDeducTotal);
			this.totalLiquido = utilidades.formato.format(bigPercepTotal.subtract(bigDeducTotal));

		}

		this.totalRegistros = this.registrosDAT.size();

		if (!conservarRegistrosDAT)
		{
			this.registrosDAT = null;
		}

	}

	// Actualiza la información de cada pago, acumula los conceptos en el mapa
	// conceptosAcum, acumulando los importes de cada concepto
	public void updateRegistrosTRAConConceptos(List<String> unidadesAIncluir, boolean conservarRegistrosDAT)
	{
		updatePlantillaDAT_TRA();

		updateRegistrosDAT(unidadesAIncluir, conservarRegistrosDAT, false);

		this.registrosTRA = utilidades.getRegistrosDATTRA(this, 1);

		String numeroChequeTRA = null;

		int indiceNumeroCheque = this.plantillaTRA.getPosicionValorPorDescripcionContains("mero de Cheque");
		int indiceNumeroChequeDAT = this.plantillaDAT.getPosicionValorPorDescripcionContains("mero de Cheque");
		int indiceTipoConcepto = this.plantillaTRA.getPosicionValorPorDescripcionContains("ipo de concep");
		int indiceConcepto = this.plantillaTRA.getPosicionValorPorDescripcion("Concepto");
		int indicePartida = this.plantillaDAT.getPosicionValorPorDescripcion("partida");
		int indicePartidaAntecedente = this.plantillaTRA.getPosicionValorPorDescripcionContains("partida de ante");
		int indiceImporte = this.plantillaTRA.getPosicionValorPorDescripcionContains("Importe");

		// Se verifica cada registro del TRA a qué registro del DAT pertenece y
		// lo enlaza con ello

		PlantillaRegistro regTRA;
		// nos indica si cada cheque(registro del DAT) ya cuenta con sus
		// registros correspondientes TRA para romper el ciclo
		// Esto en el entendido de que cada archivo TRA viene ordenado por
		// número de cheque
		boolean chequeListo = false;
		Concepto conceptoAñadiendo = null;

		for (UnidadProducto unidadP : this.unidadResponsable)
		{

			for (AñoDetalle añoDet : unidadP.getAños())
			{

				for (PlantillaRegistro regDAT : añoDet.getRegistrosDAT())
				{
					chequeListo = false;

					for (int x = 0; x < this.registrosTRA.size(); x++)
					{
						regTRA = this.registrosTRA.get(x);

						numeroChequeTRA = regTRA.getValorEnCampo(indiceNumeroCheque);

						if (!regDAT.getValorEnCampo(indiceNumeroChequeDAT).equals(numeroChequeTRA))
						{
							if (chequeListo)
							{
								break;
							}

							continue;
						}

						regDAT.getRegistrosVinculados().add(regTRA);

						chequeListo = true;

						conceptoAñadiendo = new Concepto(Integer.parseInt(regTRA.getValorEnCampo(indiceTipoConcepto)),
								regTRA.getValorEnCampo(indiceConcepto), "", regDAT.getValorEnCampo(indicePartida),
								regTRA.getValorEnCampo(indicePartidaAntecedente),
								new BigDecimal(regTRA.getValorEnCampo(indiceImporte).trim()));

						if (regDAT.getConceptosAcum()
								.get("" + conceptoAñadiendo.getTipoConcepto() + conceptoAñadiendo.getClave() + " "
										+ conceptoAñadiendo.getPartidaAntecedente()) == null)
						{
							regDAT.getConceptosAcum().put("" + conceptoAñadiendo.getTipoConcepto()
									+ conceptoAñadiendo.getClave() + " " + conceptoAñadiendo.getPartidaAntecedente(),
									conceptoAñadiendo);
						}
						else
						{

							System.out.print("Tiene "
									+ ((Concepto) regDAT.getConceptosAcum()
											.get("" + conceptoAñadiendo.getTipoConcepto() + conceptoAñadiendo.getClave()
													+ " " + conceptoAñadiendo.getPartidaAntecedente())).getValorString()
									+ " se le va a añadir " + conceptoAñadiendo.getValorString() + " ");

							((Concepto) regDAT.getConceptosAcum()
									.get("" + conceptoAñadiendo.getTipoConcepto() + conceptoAñadiendo.getClave() + " "
											+ conceptoAñadiendo.getPartidaAntecedente()))
													.addValor(conceptoAñadiendo.getValor());

							System.out
									.println("Quedó con " + ((Concepto) regDAT.getConceptosAcum()
											.get("" + conceptoAñadiendo.getTipoConcepto() + conceptoAñadiendo.getClave()
													+ " " + conceptoAñadiendo.getPartidaAntecedente()))
															.getValorString());

						}

						this.registrosTRA.remove(x);
						x--;

					}

				}

			}

		}

	}

	public int getIdProducto()
	{
		return idProducto;
	}

	public void setIdProducto(int idProducto)
	{
		this.idProducto = idProducto;
	}

	public String getDescripcion()
	{
		return descripcion;
	}

	public void setDescripcion(String descripcion)
	{
		this.descripcion = descripcion;
	}

	public int getAño()
	{
		return año;
	}

	public void setAño(int año)
	{
		this.año = año;
	}

	public int getQuincena()
	{
		return quincena;
	}

	public void setQuincena(int quincena)
	{
		this.quincena = quincena;
	}

	public Plaza getPlaza()
	{
		return plaza;
	}

	public void setPlaza(Plaza plaza)
	{
		this.plaza = plaza;
	}

	public TipoNomina getTipoNomina()
	{
		return tipoNomina;
	}

	public void setTipoNomina(TipoNomina tipoNomina)
	{
		this.tipoNomina = tipoNomina;
	}

	public TipoProducto getTipoProducto()
	{
		return tipoProducto;
	}

	public void setTipoProducto(TipoProducto tipoProducto)
	{
		this.tipoProducto = tipoProducto;
	}

	public String getNombreProducto()
	{
		return nombreProducto;
	}

	public void setNombreProducto(String nombreProducto)
	{
		this.nombreProducto = nombreProducto;
	}

	public List<PlantillaRegistro> getRegistrosDAT()
	{
		return registrosDAT;
	}

	public void setRegistrosDAT(List<PlantillaRegistro> registrosDAT)
	{
		this.registrosDAT = registrosDAT;
	}

	public List<PlantillaRegistro> getRegistrosTRA()
	{
		return registrosTRA;
	}

	public void setRegistrosTRA(List<PlantillaRegistro> registrosTRA)
	{
		this.registrosTRA = registrosTRA;
	}

	public static long getSerialversionuid()
	{
		return serialVersionUID;
	}

	public Plantilla getPlantillaDAT()
	{
		return plantillaDAT;
	}

	public void setPlantillaDAT(Plantilla plantillaDAT)
	{
		this.plantillaDAT = plantillaDAT;
	}

	public Plantilla getPlantillaTRA()
	{
		return plantillaTRA;
	}

	public void setPlantillaTRA(Plantilla plantillaTRA)
	{
		this.plantillaTRA = plantillaTRA;
	}

	public String getTotalPercepiones()
	{
		return totalPercepiones;
	}

	public void setTotalPercepiones(String totalPercepiones)
	{
		this.totalPercepiones = totalPercepiones;
	}

	public String getTotalDeducciones()
	{
		return totalDeducciones;
	}

	public void setTotalDeducciones(String totalDeducciones)
	{
		this.totalDeducciones = totalDeducciones;
	}

	public String getTotalLiquido()
	{
		return totalLiquido;
	}

	public void setTotalLiquido(String totalLiquido)
	{
		this.totalLiquido = totalLiquido;
	}

	public int getTotalRegistros()
	{
		return totalRegistros;
	}

	public void setTotalRegistros(int totalRegistros)
	{
		this.totalRegistros = totalRegistros;
	}

	public List<UnidadProducto> getUnidadResponsable()
	{
		return unidadResponsable;
	}

	public void setUnidadResponsable(List<UnidadProducto> unidadResponsable)
	{
		this.unidadResponsable = unidadResponsable;
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

	public BigDecimal getBigPercepTotal()
	{
		return bigPercepTotal;
	}

	public void setBigPercepTotal(BigDecimal bigPercepTotal)
	{
		this.bigPercepTotal = bigPercepTotal;
	}

	public BigDecimal getBigDeducTotal()
	{
		return bigDeducTotal;
	}

	public void setBigDeducTotal(BigDecimal bigDeducTotal)
	{
		this.bigDeducTotal = bigDeducTotal;
	}

	public BigDecimal getBigRegPercep()
	{
		return bigRegPercep;
	}

	public void setBigRegPercep(BigDecimal bigRegPercep)
	{
		this.bigRegPercep = bigRegPercep;
	}

	public BigDecimal getBigRegDeduc()
	{
		return bigRegDeduc;
	}

	public void setBigRegDeduc(BigDecimal bigRegDeduc)
	{
		this.bigRegDeduc = bigRegDeduc;
	}

	public String getQuincenaString()
	{
		if (this.quincena < 10)
		{
			return "0" + this.quincena;
		}

		return "" + this.quincena;

	}

	public String getAñoQuincenaString()
	{
		return "" + getAño() + "" + getQuincena();
	}

	public List<PlantillaRegistro> getRegistrosDATFilter()
	{
		return registrosDATFilter;
	}

	public void setRegistrosDATFilter(List<PlantillaRegistro> registrosDATFilter)
	{
		this.registrosDATFilter = registrosDATFilter;
	}

	public List<PlantillaRegistro> getRegistrosTRAFilter()
	{
		return registrosTRAFilter;
	}

	public void setRegistrosTRAFilter(List<PlantillaRegistro> registrosTRAFilter)
	{
		this.registrosTRAFilter = registrosTRAFilter;
	}

	public PlantillaRegistro getRegistroDATSelec()
	{
		return registroDATSelec;
	}

	public void setRegistroDATSelec(PlantillaRegistro registroDATSelec)
	{
		this.registroDATSelec = registroDATSelec;
	}

	public PlantillaRegistro getRegistroTRASelec()
	{
		return registroTRASelec;
	}

	public void setRegistroTRASelec(PlantillaRegistro registroTRASelec)
	{
		this.registroTRASelec = registroTRASelec;
	}

	public Map getConceptosNoAsociadosPlaza()
	{
		return conceptosNoAsociadosAPlaza;
	}

	public void setConceptosNoAsociadosPlaza(Map conceptosNoAsociadosPlaza)
	{
		this.conceptosNoAsociadosAPlaza = conceptosNoAsociadosPlaza;
	}

	public Map getConceptosAsociadosAPlaza()
	{
		return conceptosAsociadosAPlaza;
	}

	public void setConceptosAsociadosAPlaza(Map conceptosAsociadosAPlaza)
	{
		this.conceptosAsociadosAPlaza = conceptosAsociadosAPlaza;
	}

	public Map getConceptosNoAsociadosAPlaza()
	{
		return conceptosNoAsociadosAPlaza;
	}

	public void setConceptosNoAsociadosAPlaza(Map conceptosNoAsociadosAPlaza)
	{
		this.conceptosNoAsociadosAPlaza = conceptosNoAsociadosAPlaza;
	}

	public List<UnidadRegistros> getUnidadesRegsClasificados()
	{
		return unidadesRegsClasificados;
	}

	public void setUnidadesRegsClasificados(List<UnidadRegistros> unidadesRegsClasificados)
	{
		this.unidadesRegsClasificados = unidadesRegsClasificados;
	}

	@Override
	public String toString()
	{
		return "" + this.año + "/" + (("" + this.quincena).length() == 1 ? "0" + this.quincena : this.quincena) + " - "
				+ this.plaza.getDescripcionPlaza() + " - " + this.nombreProducto
				+ (this.tipoNomina.getIdTipoNomina() == 1 ? " ( " + this.tipoNomina.getDescripcion() + " )" : "");

	}

}
