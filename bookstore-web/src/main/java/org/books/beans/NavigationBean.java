package org.books.beans;

import java.io.Serializable;
import java.util.LinkedList;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

/**
 *
 * @author AWy
 */
@Named("navigationBean")
@SessionScoped
public class NavigationBean implements Serializable {

    @Inject
    LoginBean login;

    private final LinkedList<Pages> history = new LinkedList();
    private Pages nextView;

    public String goToCatalogSearch() {
	saveCurrentPage();
	return goToPage(Pages.PAGE_CATALOG_SEARCH);
    }

    public String goToBookDetails() {
	saveCurrentPage();
	return goToPage(Pages.PAGE_BOOK_DETAILS);
    }

    public String goToShoppingCart() {
	saveCurrentPage();
	return goToPage(Pages.PAGE_SHOPPING_CART);
    }

    public String goToOrderSummary() {
	saveCurrentPage();
	return goToPage(Pages.PAGE_ORDER_SUMMARY);
    }

    public String goToRegistration() {
	saveCurrentPage();
	return goToPage(Pages.PAGE_REGISTRATION);
    }

    public String goToAccount() {
	saveCurrentPage();
	return goToPage(Pages.PAGE_ACCOUNT);
    }

    public String goToCustomerDetails() {
	saveCurrentPage();
	return goToPage(Pages.PAGE_CUSTOMER_DETAILS);
    }

    public String goToOrderConfirmation() {
	saveCurrentPage();
	return goToPage(Pages.PAGE_ORDER_CONFIRMATION);
    }
    
    public String goToOrderDetail() {
	saveCurrentPage();
	return goToPage(Pages.PAGE_ORDER_DETAIL);
    }
    
    public String goToChangePassword() {
	saveCurrentPage();
	return goToPage(Pages.PAGE_CHANGE_PASSWORD);
    }

    private String goToLogin() {
	return Pages.PAGE_LOGIN.getPageName();
    }

    public String goToNextPage() {
	return nextView == null ? Pages.PAGE_CATALOG_SEARCH.getPageName() : nextView.getPageName();
    }

    public String goBack() {
	return history.isEmpty() ? Pages.PAGE_CATALOG_SEARCH.getPageName() : history.pop().getPageName();
    }

    private String goToPage(Pages page) {
	if (page.isLoginNeeded() && !login.isLoggedIn()) {
	    this.nextView = page;
	    return goToLogin();
	}
	return page.getPageName();
    }

    private void saveCurrentPage() {
	Pages currentView = getCurrentView();
	Pages previousView = history.isEmpty() ? null : history.peek();
	if (previousView == null || !previousView.equals(currentView)) {
	    history.push(currentView);
	}
    }

    private static Pages getCurrentView() {
	return Pages.getPage(FacesContext.getCurrentInstance().getViewRoot().getViewId());
    }

    public String getHeaderStyle(String page) {
	Pages currentView = getCurrentView();
	if (currentView == Pages.valueOf(page)) {
	    return "active important";
	}
	return "";
    }

    public enum Pages {

	PAGE_CATALOG_SEARCH("/catalogSearch.xhtml", false),
	PAGE_BOOK_DETAILS("/bookDetails.xhtml", false),
	PAGE_SHOPPING_CART("/shoppingCart.xhtml", false),
	PAGE_ORDER_SUMMARY("/orderSummary.xhtml", true),
	PAGE_LOGIN("/login.xhtml", false),
	PAGE_REGISTRATION("/registration.xhtml", false),
	PAGE_ACCOUNT("/account.xhtml", true),
	PAGE_CUSTOMER_DETAILS("/customerDetails.xhtml", true),
	PAGE_ORDER_CONFIRMATION("/orderConfirmation.xhtml", true),
	PAGE_ORDER_DETAIL("/orderDetails.xhtml", true),
	PAGE_ERROR("/errorPage.xhtml", false),
	PAGE_CHANGE_PASSWORD("/changePassword.xhtml", true);

	private final String pageName;
	private final boolean loginNeeded;

	private Pages(String pageName, boolean loginNeeded) {
	    this.pageName = pageName;
	    this.loginNeeded = loginNeeded;
	}

	public static Pages getPage(String pageName) {
	    for (Pages page : Pages.values()) {
		if (page.getPageName().equals(pageName)) {
		    return page;
		}
	    }
	    throw new IllegalArgumentException("PageName unknown: " + pageName);
	}

	public String getPageName() {
	    return pageName;
	}

	public boolean isLoginNeeded() {
	    return loginNeeded;
	}
    }
}
