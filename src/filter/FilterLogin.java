package filter;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebFilter(filterName="FilterLogin",urlPatterns="/jsp/admin/*",
			initParams={@WebInitParam(name="allowPath",value="login.jsp;LoginServlet;images;css")})
public class FilterLogin implements Filter {
	private String allowPath;

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest  httpRequest=(HttpServletRequest) request;
		HttpServletResponse httpResponse=(HttpServletResponse) response;
		String urlPath=httpRequest.getServletPath();
		if(httpRequest.getSession().getAttribute("adminUser")!=null){
			chain.doFilter(request, response);
			return;
		}
		
		String[] urls=this.allowPath.split(";");
		for(String url:urls){
			if(urlPath.indexOf(url)>0){
				chain.doFilter(request, response);
				return;
			}
		}
		String noPath=httpRequest.getScheme()+"://"+httpRequest.getServerName()+":"+httpRequest.getServerPort()+httpRequest.getContextPath()+"/jsp/admin/login.jsp";
		
		PrintWriter pw=httpResponse.getWriter();
		pw.println("<script>top.location.href='"+noPath+"'</script>");
	}

	public void init(FilterConfig fConfig) throws ServletException {
		this.allowPath=fConfig.getInitParameter("allowPath");
	}

}
