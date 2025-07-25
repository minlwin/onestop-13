package com.jdc.scope.servlet;

import java.io.IOException;

import com.jdc.scope.servlet.model.Account;
import com.jdc.scope.servlet.model.AccountManager;
import com.jdc.scope.servlet.model.ShoppingCart;
import com.jdc.scope.servlet.model.exception.BusinessException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/signup")
public class SignUpServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private AccountManager accountManager;
	
	@Override
	public void init() throws ServletException {
		accountManager = (AccountManager) getServletContext().getAttribute("AccountManager");
	}
		
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		getServletContext().getRequestDispatcher("/views/sign-up.jsp")
			.forward(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		try {
			var name = req.getParameter("name");
			var email = req.getParameter("email");
			var password = req.getParameter("password");
			var phone = req.getParameter("phone");
			
			var account = Account.builder()
					.name(name)
					.email(email)
					.password(password)
					.phone(phone).build();
			
			accountManager.add(account);
			
			var session = req.getSession(true);
			session.setAttribute("loginUser", account);

			ShoppingCart cart = (ShoppingCart) session.getAttribute("myCart");
			if(null == cart || cart.getItems().isEmpty()) {
				resp.sendRedirect(getServletContext().getContextPath().concat("/products"));
				return;
			}
			
			getServletContext().getRequestDispatcher("/sales").forward(req, resp);
		} catch (BusinessException e) {
			req.setAttribute("message", e.getMessage());
			getServletContext().getRequestDispatcher("/views/sign-up.jsp").forward(req, resp);
		}
	}
}
