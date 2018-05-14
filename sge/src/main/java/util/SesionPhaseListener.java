/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.IOException;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import modelo.Sesion;

/**
 *
 * @author desarolloyc
 */
public class SesionPhaseListener implements PhaseListener {

	@Override
	public void afterPhase(PhaseEvent event) {

		// System.out.println("Fase: "+event.getPhaseId());
		// se obtiene el contexto
		FacesContext fc = event.getFacesContext();
		// se obtiene el URL de la página
		String paginaActual = fc.getViewRoot().getViewId();
		System.out.println("Página: " + paginaActual);

	/*	if (paginaActual.lastIndexOf("404.xhtml") == -1 || paginaActual.lastIndexOf("index.xhtml") == -1) {

			boolean esLogin = (paginaActual.lastIndexOf("login.xhtml") > -1);

			Sesion controlSesion = (Sesion) FacesUtils.getManagedBean("Sesion");

			// si se está cargando el login y se encu entra en sesión redireccionar
			// a la sesión indicada
			if (esLogin) {
				String redireccion = "";

				if (controlSesion.getSesionActiva() != null) {

					switch (controlSesion.getSesionActiva()) {
					case "Admin":
					case "SuperAdmin":
					case "Consultas":
						redireccion = "/sge/portal/dashboard.jsf";
						break;

					}

				}

				if (redireccion.length() > 0) {
					try {
						FacesContext.getCurrentInstance().getExternalContext().redirect(redireccion);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} else {
				String cuenta = controlSesion.getSesionActiva();
				String urlRedireccion = "/sge/index.xhtml";
				boolean redireccionar = false;

				if (cuenta == null || cuenta.equals("")) {
					redireccionar = true;
				} else {
					if (paginaActual.indexOf("/administracion/") > -1) {
						if (cuenta.equals("Usuario")) {
							redireccionar = true;
						}
					}
				}

				if (redireccionar) {
					try {

						System.out.println("Redireccionando a: " + urlRedireccion);
						FacesContext.getCurrentInstance().getExternalContext().redirect(urlRedireccion);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		}*/
	}

	@Override
	public void beforePhase(PhaseEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public PhaseId getPhaseId() {
		// System.out.println("La fase esta en "+PhaseId.RESTORE_VIEW);
		// TODO Auto-generated method stub
		return PhaseId.RESTORE_VIEW;
	}
}
