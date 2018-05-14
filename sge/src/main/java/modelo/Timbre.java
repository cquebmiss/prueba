package modelo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import util.utilidades;

public class Timbre
{
	private String version;
	private String folio;
	private String UUID;
	private String receptor;
	private String rfc;
	private ProductoTimbrado idProductoTimbrado;
	private int idRegistroProductoNomina;
	private Date fechaComprobante;
	private Date fechaTimbrado;
	private String noCertificado;
	private String noCertificadoSAT;
	private Date fechaPago;
	private BigDecimal totalPercepciones;
	private BigDecimal totalDeducciones;
	private BigDecimal subTotal;
	private BigDecimal descuento;
	private BigDecimal total;
	private BigDecimal totalGravados;
	// Fe de erratas, debe decir Exento
	private BigDecimal totalExcento;
	private BigDecimal totalGravadosDeduc;
	private BigDecimal totalExcentoDeduc;
	private BigDecimal totalSueldos;
	private BigDecimal totalImpuestosRetenidos;
	private BigDecimal totalOtrasDeducciones;

	private String totalString;
	private String totalPercepcionesString;
	private String totalDeduccionesString;
	private String totalGravadosString;
	private String totalExcentoString;
	private String totalSueldosString;
	private String totalImpuestosRetenidosString;
	private String totalOtrasDeduccionesString;

	private Status status;
	private Date fechaCancelacion;
	private String motivoCancelacion;
	private String remesa;

	private TreeNode cfdiXmlroot;
	private List<ConceptoTimbre> conceptos;

	public Timbre()
	{
		super();
		this.totalPercepciones = new BigDecimal(0.00);
		this.totalDeducciones = new BigDecimal(0.00);
		this.total = new BigDecimal(0.00);
		this.subTotal = new BigDecimal(0.00);
		this.descuento = new BigDecimal(0.00);
		this.totalGravados = new BigDecimal(0.00);
		this.totalExcento = new BigDecimal(0.00);
		this.totalGravadosDeduc = new BigDecimal(0.00);
		this.totalExcentoDeduc = new BigDecimal(0.00);
		this.totalSueldos = new BigDecimal(0.00);
		this.totalImpuestosRetenidos = new BigDecimal(0.00);
		this.totalOtrasDeducciones = new BigDecimal(0.00);

		this.totalPercepcionesString = "$ 0.00";
		this.totalDeduccionesString = "$ 0.00";
		this.totalString = "$ 0.00";
		this.totalGravadosString = "$ 0.00";
		this.totalExcentoString = "$ 0.00";
		this.totalSueldosString = "$ 0.00";
		this.totalImpuestosRetenidosString = "$ 0.00";
		this.totalOtrasDeduccionesString = "$ 0.00";

		// TODO Auto-generated constructor stub
	}

	public Timbre(String folio, String receptor, String rfc, Date fechaComprobante, Date fechaTimbrado,
			BigDecimal totalPercepciones, BigDecimal totalDeducciones, BigDecimal total)
	{
		super();
		this.folio = folio;
		this.receptor = receptor;
		this.rfc = rfc;
		this.fechaComprobante = fechaComprobante;
		this.fechaTimbrado = fechaTimbrado;
		this.totalPercepciones = totalPercepciones;
		this.totalDeducciones = totalDeducciones;
		this.total = total;

		this.totalPercepcionesString = utilidades.formatoMoneda(this.totalPercepciones.toString());
		this.totalDeduccionesString = utilidades.formatoMoneda(this.totalDeducciones.toString());
		this.totalString = utilidades.formatoMoneda(this.total.toString());
	}

	public Timbre(String folio, String uUID, String receptor, String rfc, ProductoTimbrado idProductoTimbrado,
			int idRegistroProductoNomina, Date fechaComprobante, Date fechaTimbrado, String noCertificado,
			String noCertificadoSAT, Date fechaPago, BigDecimal totalPercepciones, BigDecimal totalDeducciones,
			BigDecimal total, BigDecimal totalGravados, BigDecimal totalExcento, BigDecimal totalSueldos,
			BigDecimal totalImpuestosRetenidos, BigDecimal totalOtrasDeducciones)
	{
		super();
		this.folio = folio;
		this.UUID = uUID;
		this.receptor = receptor;
		this.rfc = rfc;
		this.idProductoTimbrado = idProductoTimbrado;
		this.idRegistroProductoNomina = idRegistroProductoNomina;
		this.fechaComprobante = fechaComprobante;
		this.fechaTimbrado = fechaTimbrado;
		this.noCertificado = noCertificado;
		this.noCertificadoSAT = noCertificadoSAT;
		this.fechaPago = fechaPago;
		this.totalPercepciones = totalPercepciones;
		this.totalDeducciones = totalDeducciones;
		this.total = total;
		this.totalGravados = totalGravados;
		this.totalExcento = totalExcento;
		this.totalSueldos = totalSueldos;
		this.totalImpuestosRetenidos = totalImpuestosRetenidos;
		this.totalOtrasDeducciones = totalOtrasDeducciones;

		this.totalString = utilidades.formatoMoneda(this.total.toString());
		this.totalPercepcionesString = utilidades.formatoMoneda(this.totalPercepcionesString.toString());
		this.totalDeduccionesString = utilidades.formatoMoneda(this.totalDeducciones.toString());
		this.totalGravadosString = utilidades.formatoMoneda(this.totalGravados.toString());
		this.totalExcentoString = utilidades.formatoMoneda(this.totalExcento.toString());
		this.totalSueldosString = utilidades.formatoMoneda(this.totalSueldos.toString());
		this.totalImpuestosRetenidosString = utilidades.formatoMoneda(this.totalImpuestosRetenidos.toString());
		this.totalOtrasDeduccionesString = utilidades.formatoMoneda(this.totalOtrasDeducciones.toString());
	}

	public boolean isCancelado()
	{
		if (getStatus().getIdStatus() == -1)
		{
			return true;
		}
		return false;
	}

	public void updateCfdiXml()
	{
		// Se localiza el timbre físicamente dentro de la carpeta que lo
		// contiene y luego se va leyendo para formar los nodos

		File timbre = new File(utilidades.getRutaTimbrado() + this.idProductoTimbrado.getIdProductoTimbrado() + "/BPM-"
				+ "0" + this.folio + ".xml");

		this.cfdiXmlroot = new DefaultTreeNode("0" + this.folio + ".xml", null);
		this.cfdiXmlroot.setExpanded(true);

		// System.out.println(" Timbre: " + rutaTimbre.getName());
		// Se realiza la lectura del documento XML
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		InputStream inputStream = null;
		Reader reader = null;
		DocumentBuilder builder = null;
		Document doc = null;

		try
		{

			inputStream = new FileInputStream(timbre);
			reader = new InputStreamReader(inputStream, "Cp1252");

			InputSource is = new InputSource(reader);

			// Se crea el documento
			builder = factory.newDocumentBuilder();
			doc = builder.parse(is);

		}
		catch (ParserConfigurationException e)
		{
			// TODO Auto-generated catch block

			e.printStackTrace();
		}
		catch (SAXException e)
		{
			try
			{
				inputStream = new FileInputStream(timbre);
				doc = builder.parse(inputStream);
			}
			catch (Exception e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		leerNodeList(doc.getChildNodes(), this.cfdiXmlroot);

	}

	public void leerNodeList(NodeList listaNodos, TreeNode nodoPadre)
	{

		Node nodo = null;

		for (int x = 0; x < listaNodos.getLength(); x++)
		{

			nodo = listaNodos.item(x);

			// System.out.println("Nodo Padre: " + nodo.getNodeName());

			NamedNodeMap nnm = nodo.getAttributes();

			if (nnm == null)
			{
				continue;
			}

			String textoAtributos = "";

			for (int y = 0; y < nnm.getLength(); y++)
			{
				Node nodoAtributo = nnm.item(y);

				nodoAtributo.getNodeValue();

				textoAtributos += "<b>" + nodoAtributo.getNodeName() + "</b> = " + nodoAtributo.getNodeValue()
						+ "</br>";

			}

			TreeNode nodoTree = new DefaultTreeNode(
					new ColumnModel("<b>" + nodo.getNodeName() + "</b>", textoAtributos), nodoPadre);

			nodoTree.setExpanded(true);

			if (nodo.getChildNodes() != null)
			{
				leerNodeList(nodo.getChildNodes(), nodoTree);
			}

		}

	}

	public void updateConceptos()
	{

	}

	public void inicializaListaConceptos()
	{
		this.conceptos = new ArrayList<>();
	}

	public void addConcepto(ConceptoTimbre concepto)
	{
		this.conceptos.add(concepto);
	}

	public String getFolio()
	{
		return folio;
	}

	public void setFolio(String folio)
	{
		this.folio = folio;
	}

	public String getUUID()
	{
		return UUID;
	}

	public void setUUID(String uUID)
	{
		UUID = uUID;
	}

	public ProductoTimbrado getIdProductoTimbrado()
	{
		return idProductoTimbrado;
	}

	public void setIdProductoTimbrado(ProductoTimbrado idProductoTimbrado)
	{
		this.idProductoTimbrado = idProductoTimbrado;
	}

	public int getIdRegistroProductoNomina()
	{
		return idRegistroProductoNomina;
	}

	public void setIdRegistroProductoNomina(int idRegistroProductoNomina)
	{
		this.idRegistroProductoNomina = idRegistroProductoNomina;
	}

	public Date getFechaComprobante()
	{
		return fechaComprobante;
	}

	public void setFechaComprobante(Date fechaComprobante)
	{
		this.fechaComprobante = fechaComprobante;
	}

	public Date getFechaTimbrado()
	{
		return fechaTimbrado;
	}

	public void setFechaTimbrado(Date fechaTimbrado)
	{
		this.fechaTimbrado = fechaTimbrado;
	}

	public String getNoCertificado()
	{
		return noCertificado;
	}

	public void setNoCertificado(String noCertificado)
	{
		this.noCertificado = noCertificado;
	}

	public String getNoCertificadoSAT()
	{
		return noCertificadoSAT;
	}

	public void setNoCertificadoSAT(String noCertificadoSAT)
	{
		this.noCertificadoSAT = noCertificadoSAT;
	}

	public Date getFechaPago()
	{
		return fechaPago;
	}

	public void setFechaPago(Date fechaPago)
	{
		this.fechaPago = fechaPago;
	}

	public BigDecimal getTotalPercepciones()
	{
		return totalPercepciones;
	}

	public void setTotalPercepciones(BigDecimal totalPercepciones)
	{
		this.totalPercepciones = totalPercepciones;
		this.totalPercepcionesString = utilidades.formatoMoneda(this.totalPercepciones.toString());
	}

	public BigDecimal getTotalDeducciones()
	{
		return totalDeducciones;
	}

	public void setTotalDeducciones(BigDecimal totalDeducciones)
	{
		this.totalDeducciones = totalDeducciones;
		this.totalDeduccionesString = utilidades.formatoMoneda(this.totalDeducciones.toString());
	}

	public BigDecimal getTotal()
	{
		return total;
	}

	public void setTotal(BigDecimal total)
	{
		this.total = total;
		this.totalString = utilidades.formatoMoneda(this.total.toString());
	}

	public BigDecimal getTotalGravados()
	{
		return totalGravados;
	}

	public void setTotalGravados(BigDecimal totalGravados)
	{
		this.totalGravados = totalGravados;
		this.totalGravadosString = utilidades.formatoMoneda(this.totalGravados.toString());
	}

	public BigDecimal getTotalExcento()
	{
		return totalExcento;
	}

	public void setTotalExcento(BigDecimal totalExcento)
	{
		this.totalExcento = totalExcento;
		this.totalExcentoString = utilidades.formatoMoneda(this.totalExcento.toString());
	}

	public BigDecimal getTotalSueldos()
	{
		return totalSueldos;
	}

	public void setTotalSueldos(BigDecimal totalSueldos)
	{
		this.totalSueldos = totalSueldos;
		this.totalSueldosString = utilidades.formatoMoneda(this.totalSueldos.toString());
	}

	public BigDecimal getTotalImpuestosRetenidos()
	{
		return totalImpuestosRetenidos;
	}

	public void setTotalImpuestosRetenidos(BigDecimal totalImpuestosRetenidos)
	{
		this.totalImpuestosRetenidos = totalImpuestosRetenidos;
		this.totalImpuestosRetenidosString = utilidades.formatoMoneda(this.totalImpuestosRetenidos.toString());
	}

	public BigDecimal getTotalOtrasDeducciones()
	{
		return totalOtrasDeducciones;
	}

	public void setTotalOtrasDeducciones(BigDecimal totalOtrasDeducciones)
	{
		this.totalOtrasDeducciones = totalOtrasDeducciones;
		this.totalOtrasDeduccionesString = utilidades.formatoMoneda(this.totalOtrasDeducciones.toString());
	}

	public String getReceptor()
	{
		return receptor;
	}

	public void setReceptor(String receptor)
	{
		this.receptor = receptor;
	}

	public String getRfc()
	{
		return rfc;
	}

	public void setRfc(String rfc)
	{
		this.rfc = rfc;
	}

	public String getTotalString()
	{
		return totalString;
	}

	public void setTotalString(String totalString)
	{
		this.totalString = totalString;
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

	public String getTotalGravadosString()
	{
		return totalGravadosString;
	}

	public void setTotalGravadosString(String totalGravadosString)
	{
		this.totalGravadosString = totalGravadosString;
	}

	public String getTotalExcentoString()
	{
		return totalExcentoString;
	}

	public void setTotalExcentoString(String totalExcentoString)
	{
		this.totalExcentoString = totalExcentoString;
	}

	public String getTotalSueldosString()
	{
		return totalSueldosString;
	}

	public void setTotalSueldosString(String totalSueldosString)
	{
		this.totalSueldosString = totalSueldosString;
	}

	public String getTotalImpuestosRetenidosString()
	{
		return totalImpuestosRetenidosString;
	}

	public void setTotalImpuestosRetenidosString(String totalImpuestosRetenidosString)
	{
		this.totalImpuestosRetenidosString = totalImpuestosRetenidosString;
	}

	public String getTotalOtrasDeduccionesString()
	{
		return totalOtrasDeduccionesString;
	}

	public void setTotalOtrasDeduccionesString(String totalOtrasDeduccionesString)
	{
		this.totalOtrasDeduccionesString = totalOtrasDeduccionesString;
	}

	public TreeNode getCfdiXmlroot()
	{
		return cfdiXmlroot;
	}

	public void setCfdiXmlroot(TreeNode cfdiXmlroot)
	{
		this.cfdiXmlroot = cfdiXmlroot;
	}

	public Status getStatus()
	{
		return status;
	}

	public void setStatus(Status status)
	{
		this.status = status;
	}

	public Date getFechaCancelacion()
	{
		return fechaCancelacion;
	}

	public void setFechaCancelacion(Date fechaCancelacion)
	{
		this.fechaCancelacion = fechaCancelacion;
	}

	public String getMotivoCancelacion()
	{
		return motivoCancelacion;
	}

	public void setMotivoCancelacion(String motivoCancelacion)
	{
		this.motivoCancelacion = motivoCancelacion;
	}

	public String getRemesa()
	{
		return remesa;
	}

	public void setRemesa(String remesa)
	{
		this.remesa = remesa;
	}

	public List<ConceptoTimbre> getConceptos()
	{
		return conceptos;
	}

	public void setConceptos(List<ConceptoTimbre> conceptos)
	{
		this.conceptos = conceptos;
	}

	public String getVersion()
	{
		return version;
	}

	public void setVersion(String version)
	{
		this.version = version;
	}

	public BigDecimal getTotalGravadosDeduc()
	{
		return totalGravadosDeduc;
	}

	public void setTotalGravadosDeduc(BigDecimal totalGravadosDeduc)
	{
		this.totalGravadosDeduc = totalGravadosDeduc;
	}

	public BigDecimal getTotalExcentoDeduc()
	{
		return totalExcentoDeduc;
	}

	public void setTotalExcentoDeduc(BigDecimal totalExcentoDeduc)
	{
		this.totalExcentoDeduc = totalExcentoDeduc;
	}

	public BigDecimal getSubTotal()
	{
		return subTotal;
	}

	public void setSubTotal(BigDecimal subTotal)
	{
		this.subTotal = subTotal;
	}

	public BigDecimal getDescuento()
	{
		return descuento;
	}

	public void setDescuento(BigDecimal descuento)
	{
		this.descuento = descuento;
	}

}
