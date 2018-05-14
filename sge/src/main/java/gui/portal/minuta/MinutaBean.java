package gui.portal.minuta;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;

import modelo.minutas.Compromiso;
import modelo.minutas.Minuta;
import modelo.minutas.Participante;
import modelo.minutas.StatusMinuta;
import modelo.minutas.TemaMinuta;
import reports.CrearPDF;
import resources.DataBase;
import util.FacesUtils;
import util.minutas.UtilidadesMinutas;

@ManagedBean
@SessionScoped
public class MinutaBean
{
	// Catálogos para trabajo
	private List<StatusMinuta>	catStatusMinuta;

	private Minuta				minuta;

	// atributos auxiliares para la tabla de participantes
	private List<Participante>	tablaParticipantesSelection;
	private List<Participante>	tablaParticipantesFilter;

	// apoyo para el autocomplete
	private List<Participante>	resultadosAutoCompleteParticipante;
	private Participante		participanteSelec;

	private String				urlPDFMinuta;
	private String				urlHTMLMinuta;
	private Session				emailSession;

	private int					editarMinuta;

	public MinutaBean()
	{

	}

	@PostConstruct
	public void postConstruct()
	{
		iniciaNuevaMinuta();
		final String username = "cquebmiss@gmail.com";// change accordingly
		final String password = "FPD2600lenovo";// change accordingly

		// Assuming you are sending email through relay.jangosmtp.net
		String host = "smtp.gmail.com";

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", "587");

		// Get the Session object.
		this.emailSession = Session.getInstance(props, new javax.mail.Authenticator()
		{
			protected PasswordAuthentication getPasswordAuthentication()
			{
				return new PasswordAuthentication(username, password);
			}
		});

	}

	public void actionCrearActualizarMinuta()
	{
		if (this.minuta.getIdMinuta() < 0)
		{
			this.minuta.crearMinutaBD();
		}
		else
		{
			this.minuta.updateDatosBasicosMinuta();
		}
	}

	public void activarModoEdicion()
	{
		this.editarMinuta = 0;

		this.minuta.updateAllDataBD();

		try
		{
			FacesContext.getCurrentInstance().getExternalContext().redirect("minutas.jsf");
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void desActivarModoEdicion()
	{
		this.editarMinuta = -1;
	}

	public void iniciaNuevaMinuta()
	{
		this.catStatusMinuta = UtilidadesMinutas.getCatStatusMinutas();
		// Se crea la nueva minuta con la fecha postcontruct del bean y el catálogo de
		// status de la minuta
		this.minuta = new Minuta(-1, "Nueva Minuta", new java.util.Date(), this.catStatusMinuta.get(0));
		this.editarMinuta = -1;

	}

	public void actionNuevaMinuta()
	{
		iniciaNuevaMinuta();

		try
		{
			FacesContext.getCurrentInstance().getExternalContext().redirect("/sge/portal/minutas.jsf");
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// MÉTODOS FUNCIONALES PARA LA SECCIÓN DE PARTICIPANTES DE LA MINUT

	public void actionAñadirParticipanteDesdeDialogo()
	{
		System.out.print("Funciona el método");
		DialogoParticipanteBean dialogoBean = (DialogoParticipanteBean) FacesUtils
				.getManagedBean("dialogoParticipanteBean");

		Participante nuevoParticipante = dialogoBean.getParticipante();

		boolean participanteRepetido = false;

		if (nuevoParticipante == null)
		{
			dialogoBean.crearPersona();

			// Se obtiene el participante desde la persona creada y se le asigna un orden
			nuevoParticipante = dialogoBean.getParticipante();
			nuevoParticipante.setMinuta(this.minuta);
			nuevoParticipante.setOrden(this.minuta.getParticipantes().size());

			addParticipanteAMinuta(nuevoParticipante);
		}
		else
		{
			FacesContext.getCurrentInstance()
					.addMessage("msjTablaParticipantes", new FacesMessage(FacesMessage.SEVERITY_WARN,
							"Participante Ya Registrado",
							"El participante ya ha sido registrado previamente, utilice el campo de búsqueda."));
			participanteRepetido = true;
		}

		PrimeFaces.current().ajax().addCallbackParam("participanteRepetido", participanteRepetido);

	}

	public void onItemSelectParticipante(SelectEvent event)
	{
		addParticipanteAMinuta((Participante) event.getObject());
		this.participanteSelec = null;
	}

	public void addParticipanteAMinuta(Participante nuevoParticipante)
	{
		if (!existeParticipanteEnMinuta(nuevoParticipante))
		{
			this.minuta.addParticipante(nuevoParticipante);
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage("msjTablaParticipantes",
					new FacesMessage(FacesMessage.SEVERITY_WARN, "Participante",
							"El participante ya se encuentra dentro de la minuta."));
		}
	}

	public boolean existeParticipanteEnMinuta(Participante participanteNuevo)
	{
		Optional<Participante> obj = this.minuta.getParticipantes().stream().filter(v ->
		{
			if (v.getPersona().getNombre().equalsIgnoreCase(participanteNuevo.getPersona().getNombre())
					&& v.getPersona().getApellidoPaterno()
							.equalsIgnoreCase(participanteNuevo.getPersona().getApellidoPaterno())
					&& v.getPersona().getApellidoMaterno()
							.equalsIgnoreCase(participanteNuevo.getPersona().getApellidoMaterno())
					&& v.getPersona().getCargo().equalsIgnoreCase(participanteNuevo.getPersona().getCargo()))
			{

				return true;
			}

			return false;

		}).findFirst();

		return obj.isPresent();
	}

	public List<Participante> getParticipantesRegistrados(String query)
	{
		this.resultadosAutoCompleteParticipante = new ArrayList<>();

		PreparedStatement prep = null;
		ResultSet rBD = null;

		int ordenParticipantes = 0;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{
			prep = conexion
					.prepareStatement(" SELECT * FROM persona WHERE CONCAT(Nombre,ApPaterno,ApMaterno,Cargo) LIKE ?");
			prep.setString(1, "%" + query + "%");

			rBD = prep.executeQuery();

			if (rBD.next())
			{
				do
				{
					this.resultadosAutoCompleteParticipante.add(new Participante(rBD.getInt("idPersona"),
							rBD.getString("Nombre"), rBD.getString("ApPaterno"), rBD.getString("ApMaterno"),
							rBD.getString("Cargo"), rBD.getString("Sexo"), rBD.getString("Titulo"), ordenParticipantes,
							this.minuta, ordenParticipantes, rBD.getString("Email")));
					ordenParticipantes++;

				} while (rBD.next());

			}

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al obtener las sugerencias de personas registradas como participantes, favor de contactar con el desarrollador del sistema."));

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

		return this.resultadosAutoCompleteParticipante;

	}

	public void removeParticipanteDeMinuta(Participante participante)
	{
		this.minuta.removeParticipante(participante);

	}

	public List<StatusMinuta> getCatStatusMinuta()
	{
		return catStatusMinuta;
	}
	// *******************************************************

	// MÉTODOS FUNCIONALES PARA LA SECCIÓN DE TEMAS
	public void removeTemaDeMinuta(TemaMinuta temaMinuta)
	{
		this.minuta.removeTema(temaMinuta);
	}

	// *******************************************************

	// MÉTODOS FUNCIONALES PARA LA SECCIÓN DE COMPROMISOS
	public void removeCompromisoDeMinuta(Compromiso compromiso)
	{
		this.minuta.removeCompromiso(compromiso);
	}

	// *******************************************************

	public void generarPDF()
	{

		try
		{
			CrearPDF nuevo = new CrearPDF(this.minuta);
			nuevo.createPdf();

			ExternalContext ext = FacesContext.getCurrentInstance().getExternalContext();

			this.urlPDFMinuta = ext
					.getRealPath(String.format("/resources/minutas/%s", this.minuta.getIdMinuta() + ".pdf"));
			this.urlHTMLMinuta = ext
					.getRealPath(String.format("/resources/minutas/%s", this.minuta.getIdMinuta() + ".html"));
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void actionSendEmail()
	{

		for (Participante part : this.minuta.getParticipantes())
		{
			try
			{

				// Create a default MimeMessage object.
				Message message = new MimeMessage(this.emailSession);

				// Set From: header field of the header.
				message.setFrom(new InternetAddress("cquebmiss@gmail.com"));

				// Set To: header field of the header.
				message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(part.getEmail()));

				LocalDateTime ldt = LocalDateTime.ofInstant(this.minuta.getFechaHora().toInstant(),
						ZoneId.systemDefault());

				// Set Subject: header field
				message.setSubject("Envío de minuta derivada de la reunión: " + this.minuta.getDescripcion() + " "
						+ ldt.getDayOfMonth() + " / " + ldt.getMonthValue() + " / " + ldt.getYear());

				// Create the message part
				BodyPart messageBodyPart = new MimeBodyPart();

				// Now set the actual message
				messageBodyPart.setText("Lugar de la minuta: " + this.minuta.getLugar());

				// Create a multipar message
				Multipart multipart = new MimeMultipart();

				// Set text message part
				multipart.addBodyPart(messageBodyPart);

				// Part two is attachment
				messageBodyPart = new MimeBodyPart();
				DataSource source = new FileDataSource(this.urlPDFMinuta);
				messageBodyPart.setDataHandler(new DataHandler(source));
				messageBodyPart.setFileName("Minuta No. " + this.minuta.getIdMinuta() + ".pdf");
				multipart.addBodyPart(messageBodyPart);

				// Send the complete message parts
				message.setContent(multipart);

				// Send message
				Transport.send(message);

			}
			catch (MessagingException e)
			{
				FacesContext.getCurrentInstance().addMessage("msjEnvioEmailMinuta",
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Minuta No Enviada",
								"No ha podido ser enviada la minuta a los recipientes: "
										+ part.getPersona().getNombreCompleto()));
				throw new RuntimeException(e);
			}

		}

		FacesContext.getCurrentInstance().addMessage("msjEnvioEmailMinuta", new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Minuta Enviada", "La minuta se ha enviado exitosamente a todos los participantes"));

	}

	public void setCatStatusMinuta(List<StatusMinuta> catStatusMinuta)
	{
		this.catStatusMinuta = catStatusMinuta;
	}

	public Minuta getMinuta()
	{
		return minuta;
	}

	public void setMinuta(Minuta minuta)
	{
		this.minuta = minuta;
	}

	public List<Participante> getTablaParticipantesSelection()
	{
		return tablaParticipantesSelection;
	}

	public void setTablaParticipantesSelection(List<Participante> tablaParticipantesSelection)
	{
		this.tablaParticipantesSelection = tablaParticipantesSelection;
	}

	public List<Participante> getTablaParticipantesFilter()
	{
		return tablaParticipantesFilter;
	}

	public void setTablaParticipantesFilter(List<Participante> tablaParticipantesFilter)
	{
		this.tablaParticipantesFilter = tablaParticipantesFilter;
	}

	public List<Participante> getResultadosAutoCompleteParticipante()
	{
		return resultadosAutoCompleteParticipante;
	}

	public void setResultadosAutoCompleteParticipante(List<Participante> resultadosAutoCompleteParticipante)
	{
		this.resultadosAutoCompleteParticipante = resultadosAutoCompleteParticipante;
	}

	public Participante getParticipanteSelec()
	{
		return participanteSelec;
	}

	public void setParticipanteSelec(Participante participanteSelec)
	{
		this.participanteSelec = participanteSelec;
	}

	public String getUrlPDFMinuta()
	{
		return urlPDFMinuta;
	}

	public void setUrlPDFMinuta(String urlPDFMinuta)
	{
		this.urlPDFMinuta = urlPDFMinuta;
	}

	public String getUrlHTMLMinuta()
	{
		return urlHTMLMinuta;
	}

	public void setUrlHTMLMinuta(String urlHTMLMinuta)
	{
		this.urlHTMLMinuta = urlHTMLMinuta;
	}

	public Session getEmailSession()
	{
		return emailSession;
	}

	public void setEmailSession(Session emailSession)
	{
		this.emailSession = emailSession;
	}

	public int getEditarMinuta()
	{
		return editarMinuta;
	}

	public void setEditarMinuta(int editarMinuta)
	{
		this.editarMinuta = editarMinuta;
	}

}
