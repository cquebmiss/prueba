package security;

import java.io.IOException;

import javax.faces.application.ResourceHandler;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import modelo.Sesion;

/**
 * Servlet Filter implementation class FiltroResources
 */
@WebFilter(description = "Filtra el acceso a la carpeta de expedientes", urlPatterns = { "/portal/*" })

public class FiltroResources implements Filter
{

	/**
	 * Default constructor.
	 */
	public FiltroResources()
	{
		// TODO Auto-generated constructor stub
		System.out.println("Constructor del Filtro de Resources");
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy()
	{
		System.out.println("Destroy del Filtro de Resources");
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException
	{
		// TODO Auto-generated method stub
		// place your code here

		// pass the request along the filter chain
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		HttpSession session = httpRequest.getSession();
		System.out.println("Do Filter del Filtro de Resources en página: " + httpRequest.getRequestURL());

		String loginURL = httpRequest.getContextPath() + "/";

		boolean loggedIn = false;

		if (session != null)
		{
			Sesion sesionBean = (Sesion) session.getAttribute("Sesion");

			if (sesionBean != null && sesionBean.getSesionActiva() != null && !sesionBean.getSesionActiva().isEmpty())
			{
				loggedIn = true;
			}

		}
		boolean loginRequest = httpRequest.getRequestURI().equals(loginURL);
		boolean resourceRequest = httpRequest.getRequestURI()
				.startsWith(httpRequest.getContextPath() + ResourceHandler.RESOURCE_IDENTIFIER + "/");
		boolean ajaxRequest = "partial/ajax".equals(httpRequest.getHeader("Faces-Request"));

		if (loggedIn || loginRequest || resourceRequest)
		{
			if (!resourceRequest)
			{ // Prevent browser from caching restricted resources. See also https://stackoverflow.com/q/4194207/157882
				httpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
				httpResponse.setHeader("Pragma", "no-cache"); // HTTP 1.0.
				httpResponse.setDateHeader("Expires", 0); // Proxies.
			}

			chain.doFilter(request, response); // So, just continue request.
		}
		else if (ajaxRequest)
		{
			httpResponse.setContentType("text/xml");
			httpResponse.setCharacterEncoding("UTF-8");
			//		httpResponse.getWriter().printf(AJAX_REDIRECT_XML, loginURL); // So, return special XML response instructing JSF ajax to send a redirect.
		}
		else
		{
			httpResponse.sendRedirect(loginURL); // So, just perform standard synchronous redirect.
		}

	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException
	{
		// TODO Auto-generated method stub
	}

}
